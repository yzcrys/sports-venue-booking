package com.example.cscb07.ui.stateholders;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.cscb07.R;
import com.example.cscb07.data.models.EventModel;
import com.example.cscb07.data.repositories.EventRepository;
import com.example.cscb07.data.repositories.UserRepository;
import com.example.cscb07.data.results.EventId;
import com.example.cscb07.data.results.VenueId;
import com.example.cscb07.data.results.WithId;
import com.example.cscb07.data.util.MessageUtil;
import com.example.cscb07.data.util.ServiceLocator;
import com.example.cscb07.ui.state.EventUiState;
import io.vavr.control.Try;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class EventListViewModel extends ViewModel {
    private final EventRepository eventRepository = ServiceLocator.getInstance().getEventRepository();
    private final UserRepository userRepository = ServiceLocator.getInstance().getUserRepository();
    private final MutableLiveData<List<EventUiState>> events = new MutableLiveData<>();
    private final MutableLiveData<List<EventUiState>> pendingEvents = new MutableLiveData<>();

    public void clearEvents() {
        events.setValue(Collections.emptyList());
    }

    public void clearPendingEvents() {
        pendingEvents.setValue(Collections.emptyList());
    }

    public void loadAllUpcomingEvents() {
        clearEvents();
        eventRepository.getAllUpcomingEvents(result -> setEvents(result, events));
    }

    public void loadUpcomingEventsForCurrentUser() {
        clearEvents();
        eventRepository.getUpcomingEventsForCurrentUser(result -> setEvents(result, events));
    }

    public void loadVenueEvents(VenueId id) {
        clearEvents();
        eventRepository.getEventsForVenue(id, result -> setEvents(result, events));
    }

    public void loadAllPendingEvents() {
        clearPendingEvents();
        eventRepository.getAllPendingEvents(result -> setEvents(result, pendingEvents));
    }

    public void loadPendingEventsForVenue(VenueId id) {
        clearPendingEvents();
    }

    public void joinEvent(EventId event, Runnable callback) {
        eventRepository.signUpEvent(event, result -> {
            result.onSuccess((i) -> callback.run());
            result.onFailure(MessageUtil::showMessage);
        });
    }

    public void approveEvent(EventId event, Runnable callback) {
        eventRepository.approveEvent(event, result -> {
            result.onSuccess((i) -> callback.run());
            result.onFailure(MessageUtil::showMessage);
        });
    }

    public void denyEvent(EventId event, Runnable callback) {
        eventRepository.removeEvent(event, result -> {
            result.onSuccess((i) -> callback.run());
            result.onFailure(MessageUtil::showMessage);
        });
    }

    private void setEvents(Try<List<WithId<EventId, EventModel>>> result, MutableLiveData<List<EventUiState>> events) {
        userRepository.getJoinedEvents(joinedResult -> {
            joinedResult.onSuccess(joined -> {
                result.onSuccess(newVenues -> events.setValue(newVenues.stream()
                        .map(it -> new EventUiState(
                                it.model.name,
                                it.model.description,
                                it.model.getStartDate(),
                                it.model.getEndDate(),
                                it.model.numAttendees,
                                it.model.maxCapacity,
                                it.id,
                                new VenueId(it.model.venue),
                                joined.contains(it.id)
                        ))
                        .collect(Collectors.toList()))
                ).onFailure(MessageUtil::showMessage);
            });
        });
        result.onFailure(f -> MessageUtil.showMessage(R.string.error_fail_to_get_events));
    }

    public LiveData<List<EventUiState>> getEvents() {
        return events;
    }

    public LiveData<List<EventUiState>> getPendingEvents() {
        return pendingEvents;
    }

}
