package com.bryanperez.ugaevents.controller;

import com.bryanperez.ugaevents.dto.EventDTO;
import com.bryanperez.ugaevents.model.Event;
import com.bryanperez.ugaevents.service.EventService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * REST controller for the /api/events endpoints.
 * Handles HTTP routing; delegates business logic to EventService.
 */
@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    /**
     * GET /api/events
     * Supports optional query params: date, category, location, keyword
     * Returns all events if no filters are provided.
     */
    @GetMapping
    public ResponseEntity<List<Event>> getEvents(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String keyword) {

        List<Event> events = eventService.filterEvents(date, category, location, keyword);
        return ResponseEntity.ok(events);
    }

    /**
     * GET /api/events/upcoming
     * Returns all events from today onward, sorted by date.
     */
    @GetMapping("/upcoming")
    public ResponseEntity<List<Event>> getUpcomingEvents() {
        return ResponseEntity.ok(eventService.getUpcomingEvents());
    }

    /**
     * GET /api/events/{id}
     * Returns a single event by ID, or 404 if not found.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Event> getEventById(@PathVariable Long id) {
        return eventService.getEventById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * POST /api/events
     * Creates a new event. Validates request body.
     */
    @PostMapping
    public ResponseEntity<Event> createEvent(@Valid @RequestBody EventDTO dto) {
        Event created = eventService.createEvent(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * PUT /api/events/{id}
     * Updates an existing event. Returns 404 if not found.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Event> updateEvent(@PathVariable Long id,
                                              @Valid @RequestBody EventDTO dto) {
        return eventService.updateEvent(id, dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * DELETE /api/events/{id}
     * Deletes an event. Returns 204 on success, 404 if not found.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        if (eventService.deleteEvent(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
