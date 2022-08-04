package com.example.cscb07.data.repositories;

import com.example.cscb07.data.RepositoryCallback;
import com.example.cscb07.data.Results.VenueResult;
import com.example.cscb07.ui.state.VenueUiState;

import java.util.List;

public interface VenueRepository {
    void addVenue(String name);
    void readVenue(String name, RepositoryCallback<VenueResult> callback);
    void getVenues(int amount, int page, RepositoryCallback<List<VenueUiState>> callback);
}
