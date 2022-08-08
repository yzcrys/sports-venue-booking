package com.example.cscb07.data.repositories.impl;

import android.util.Pair;
import com.example.cscb07.data.models.VenueModel;
import com.example.cscb07.data.repositories.VenueRepository;
import com.example.cscb07.data.results.VenueId;
import com.example.cscb07.data.results.WithId;
import com.example.cscb07.data.util.FirebaseUtil;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import io.vavr.collection.Stream;
import io.vavr.control.Try;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.IntStream;


public class FirebaseVenueRepository implements VenueRepository {
    @Override
    public void addVenue(String name, String description, Consumer<Try<VenueId>> callback) {
        DatabaseReference venuesRef = FirebaseUtil.getVenues();
        venuesRef.get().addOnSuccessListener(snapshot -> {
            VenueModel venue = new VenueModel(name, description);
            String id = venuesRef.push().getKey();
            venuesRef.child(id).setValue(venue);
            callback.accept(Try.success(new VenueId(id)));
        }).addOnFailureListener(e -> callback.accept(Try.failure(e)));
    }

    @Override
    public void getVenues(VenueId startAt, int amount, String searchFilter, Consumer<Try<List<WithId<VenueId, VenueModel>>>> callback) {
        Query query = FirebaseUtil.getVenues()
                .orderByKey()
                .startAfter(startAt.venueId)
                .orderByChild("name")
                .startAt(searchFilter)
                // Some big unicode character in the end to get anything that starts with the filter
                .endAt(searchFilter + "\uf8ff")
                .limitToFirst(amount);
        query.get().addOnSuccessListener(dataSnapshot -> {
            List<WithId<VenueId, VenueModel>> venues = Stream.ofAll(dataSnapshot.getChildren())
                    .map(snapshot -> WithId.of(new VenueId(snapshot.getKey()), snapshot.getValue(VenueModel.class)))
                    .toJavaList();
            callback.accept(Try.success(venues));
        }).addOnFailureListener(e ->
                callback.accept(Try.failure(e)));
    }

}
