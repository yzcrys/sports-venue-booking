package com.example.cscb07.ui.stateholders;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import androidx.lifecycle.ViewModel;
import com.example.cscb07.R;
import com.example.cscb07.data.repositories.VenueRepository;
import com.example.cscb07.data.util.MessageUtil;
import com.example.cscb07.data.util.ServiceLocator;

import java.util.*;
import java.util.stream.Collectors;

import com.example.cscb07.ui.state.VenueUiState;

public class VenueListViewModel extends ViewModel {
    private final VenueRepository venueRepository = ServiceLocator.getInstance().getVenueRepository();
    private final MutableLiveData<List<VenueUiState>> venues = new MutableLiveData<>();

    public LiveData<List<VenueUiState>> getVenues() {
        return venues;
    }

    public void loadVenues() {
        venueRepository.getVenues(result -> {
            result.onSuccess(newVenues -> {
                venues.setValue(newVenues.stream()
                        .map(it -> new VenueUiState(it.model.name, it.model.description, it.id))
                        .collect(Collectors.toList()));
            });
            result.onFailure(f -> MessageUtil.showError(R.string.error_fail_to_get_events));
        });
    }
}
