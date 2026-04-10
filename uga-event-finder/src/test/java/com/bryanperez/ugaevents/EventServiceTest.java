package com.bryanperez.ugaevents;

import com.bryanperez.ugaevents.dto.EventDTO;
import com.bryanperez.ugaevents.model.Event;
import com.bryanperez.ugaevents.repository.EventRepository;
import com.bryanperez.ugaevents.service.EventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for EventService.
 * Uses Mockito to mock the repository layer.
 */
@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventService eventService;

    private Event sampleEvent;
    private EventDTO sampleDTO;

    @BeforeEach
    void setUp() {
        sampleEvent = new Event(
            "Spring Hackathon",
            "Annual UGA coding competition",
            "academic",
            "Tate Student Center",
            LocalDate.of(2025, 3, 15),
            LocalTime.of(9, 0)
        );

        sampleDTO = new EventDTO(
            "Spring Hackathon",
            "Annual UGA coding competition",
            "academic",
            "Tate Student Center",
            LocalDate.of(2025, 3, 15),
            LocalTime.of(9, 0)
        );
    }

    @Test
    void testGetAllEvents_returnsList() {
        when(eventRepository.findAll()).thenReturn(List.of(sampleEvent));

        List<Event> result = eventService.getAllEvents();

        assertEquals(1, result.size());
        assertEquals("Spring Hackathon", result.get(0).getTitle());
        verify(eventRepository, times(1)).findAll();
    }

    @Test
    void testGetEventById_found() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(sampleEvent));

        Optional<Event> result = eventService.getEventById(1L);

        assertTrue(result.isPresent());
        assertEquals("Tate Student Center", result.get().getLocation());
    }

    @Test
    void testGetEventById_notFound() {
        when(eventRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Event> result = eventService.getEventById(99L);

        assertFalse(result.isPresent());
    }

    @Test
    void testCreateEvent_savesAndReturns() {
        when(eventRepository.save(any(Event.class))).thenReturn(sampleEvent);

        Event created = eventService.createEvent(sampleDTO);

        assertNotNull(created);
        assertEquals("Spring Hackathon", created.getTitle());
        assertEquals("academic", created.getCategory());
        verify(eventRepository, times(1)).save(any(Event.class));
    }

    @Test
    void testUpdateEvent_found_updatesFields() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(sampleEvent));
        when(eventRepository.save(any(Event.class))).thenReturn(sampleEvent);

        EventDTO updateDTO = new EventDTO(
            "Updated Hackathon", "Updated description",
            "social", "MLC", LocalDate.of(2025, 4, 1), LocalTime.of(10, 0)
        );

        Optional<Event> result = eventService.updateEvent(1L, updateDTO);

        assertTrue(result.isPresent());
        verify(eventRepository, times(1)).save(any(Event.class));
    }

    @Test
    void testUpdateEvent_notFound_returnsEmpty() {
        when(eventRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Event> result = eventService.updateEvent(99L, sampleDTO);

        assertFalse(result.isPresent());
        verify(eventRepository, never()).save(any());
    }

    @Test
    void testDeleteEvent_exists_returnsTrue() {
        when(eventRepository.existsById(1L)).thenReturn(true);
        doNothing().when(eventRepository).deleteById(1L);

        boolean result = eventService.deleteEvent(1L);

        assertTrue(result);
        verify(eventRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteEvent_notExists_returnsFalse() {
        when(eventRepository.existsById(99L)).thenReturn(false);

        boolean result = eventService.deleteEvent(99L);

        assertFalse(result);
        verify(eventRepository, never()).deleteById(any());
    }

    @Test
    void testFilterEvents_byCategory() {
        when(eventRepository.findByCategoryIgnoreCase("academic"))
            .thenReturn(List.of(sampleEvent));

        List<Event> result = eventService.filterEvents(null, "academic", null, null);

        assertEquals(1, result.size());
        verify(eventRepository).findByCategoryIgnoreCase("academic");
    }

    @Test
    void testFilterEvents_byKeyword() {
        when(eventRepository.searchByKeyword("hackathon"))
            .thenReturn(List.of(sampleEvent));

        List<Event> result = eventService.filterEvents(null, null, null, "hackathon");

        assertEquals(1, result.size());
        verify(eventRepository).searchByKeyword("hackathon");
    }
}
