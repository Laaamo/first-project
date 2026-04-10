package com.bryanperez.ugaevents.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Data Transfer Object for Event create/update requests.
 * Keeps the API layer decoupled from the JPA entity.
 */
public class EventDTO {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;
    private String category;
    private String location;

    @NotNull(message = "Event date is required")
    private LocalDate eventDate;

    private LocalTime eventTime;

    // --- Constructors ---

    public EventDTO() {}

    public EventDTO(String title, String description, String category,
                    String location, LocalDate eventDate, LocalTime eventTime) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.location = location;
        this.eventDate = eventDate;
        this.eventTime = eventTime;
    }

    // --- Getters & Setters ---

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public LocalDate getEventDate() { return eventDate; }
    public void setEventDate(LocalDate eventDate) { this.eventDate = eventDate; }

    public LocalTime getEventTime() { return eventTime; }
    public void setEventTime(LocalTime eventTime) { this.eventTime = eventTime; }
}
