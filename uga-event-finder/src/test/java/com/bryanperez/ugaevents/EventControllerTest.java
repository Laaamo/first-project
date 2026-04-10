package com.bryanperez.ugaevents;

import com.bryanperez.ugaevents.controller.EventController;
import com.bryanperez.ugaevents.dto.EventDTO;
import com.bryanperez.ugaevents.model.Event;
import com.bryanperez.ugaevents.service.EventService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration-style tests for EventController using MockMvc.
 * Tests HTTP status codes, JSON responses, and routing.
 */
@WebMvcTest(EventController.class)
class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EventService eventService;

    private ObjectMapper objectMapper;
    private Event sampleEvent;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        sampleEvent = new Event(
            "Career Fair",
            "Annual career fair with 100+ companies",
            "career",
            "Stegeman Coliseum",
            LocalDate.of(2025, 2, 20),
            LocalTime.of(10, 0)
        );
    }

    @Test
    void testGetAllEvents_returns200() throws Exception {
        when(eventService.filterEvents(null, null, null, null))
            .thenReturn(List.of(sampleEvent));

        mockMvc.perform(get("/api/events"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].title").value("Career Fair"))
            .andExpect(jsonPath("$[0].category").value("career"));
    }

    @Test
    void testGetEventById_found_returns200() throws Exception {
        when(eventService.getEventById(1L)).thenReturn(Optional.of(sampleEvent));

        mockMvc.perform(get("/api/events/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value("Career Fair"));
    }

    @Test
    void testGetEventById_notFound_returns404() throws Exception {
        when(eventService.getEventById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/events/99"))
            .andExpect(status().isNotFound());
    }

    @Test
    void testCreateEvent_validRequest_returns201() throws Exception {
        EventDTO dto = new EventDTO(
            "New Event", "Description", "social",
            "Tate", LocalDate.of(2025, 5, 1), LocalTime.of(14, 0)
        );
        when(eventService.createEvent(any(EventDTO.class))).thenReturn(sampleEvent);

        mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isCreated());
    }

    @Test
    void testCreateEvent_missingTitle_returns400() throws Exception {
        EventDTO dto = new EventDTO(null, "desc", "social", "Tate",
                LocalDate.of(2025, 5, 1), null);

        mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testDeleteEvent_exists_returns204() throws Exception {
        when(eventService.deleteEvent(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/events/1"))
            .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteEvent_notFound_returns404() throws Exception {
        when(eventService.deleteEvent(99L)).thenReturn(false);

        mockMvc.perform(delete("/api/events/99"))
            .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateEvent_found_returns200() throws Exception {
        EventDTO dto = new EventDTO(
            "Updated Event", "New desc", "academic",
            "MLC", LocalDate.of(2025, 6, 1), LocalTime.of(9, 0)
        );
        when(eventService.updateEvent(eq(1L), any(EventDTO.class)))
            .thenReturn(Optional.of(sampleEvent));

        mockMvc.perform(put("/api/events/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isOk());
    }
}
