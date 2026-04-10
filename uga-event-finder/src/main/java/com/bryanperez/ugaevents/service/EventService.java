package com.bryanperez.ugaevents.service;

import com.bryanperez.ugaevents.dto.EventDTO;
import com.bryanperez.ugaevents.model.Event;
import com.bryanperez.ugaevents.repository.EventRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service layer for Event business logic.
 * Sits between the REST controller and the JPA repository.
 */
@Service
public class EventService {

    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    // --- Read ---

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public Optional<Event> getEventById(Long id) {
        return eventRepository.findById(id);
    }

    public List<Event> getUpcomingEvents() {
        return eventRepository.findByEventDateGreaterThanEqualOrderByEventDateAsc(LocalDate.now());
    }

    public List<Event> filterEvents(LocalDate date, String category, String location, String keyword) {
        // Priority: keyword search > combined date+category > individual filters
        if (keyword != null && !keyword.isBlank()) {
            return eventRepository.searchByKeyword(keyword.trim());
        }
        if (date != null && category != null) {
            return eventRepository.findByEventDateAndCategoryIgnoreCase(date, category);
        }
        if (date != null) {
            return eventRepository.findByEventDate(date);
        }
        if (category != null) {
            return eventRepository.findByCategoryIgnoreCase(category);
        }
        if (location != null && !location.isBlank()) {
            return eventRepository.findByLocationContainingIgnoreCase(location);
        }
        return getAllEvents();
    }

    // --- Create ---

    public Event createEvent(EventDTO dto) {
        Event event = mapDtoToEntity(dto);
        return eventRepository.save(event);
    }

    // --- Update ---

    public Optional<Event> updateEvent(Long id, EventDTO dto) {
        return eventRepository.findById(id).map(existing -> {
            existing.setTitle(dto.getTitle());
            existing.setDescription(dto.getDescription());
            existing.setCategory(dto.getCategory());
            existing.setLocation(dto.getLocation());
            existing.setEventDate(dto.getEventDate());
            existing.setEventTime(dto.getEventTime());
            return eventRepository.save(existing);
        });
    }

    // --- Delete ---

    public boolean deleteEvent(Long id) {
        if (eventRepository.existsById(id)) {
            eventRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // --- Helpers ---

    private Event mapDtoToEntity(EventDTO dto) {
        return new Event(
            dto.getTitle(),
            dto.getDescription(),
            dto.getCategory(),
            dto.getLocation(),
            dto.getEventDate(),
            dto.getEventTime()
        );
    }
}
