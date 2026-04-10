package com.bryanperez.ugaevents.repository;

import com.bryanperez.ugaevents.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Spring Data JPA repository for Event entities.
 * Provides CRUD + custom filter queries.
 */
@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    // Filter by exact date
    List<Event> findByEventDate(LocalDate eventDate);

    // Filter by category (case-insensitive)
    List<Event> findByCategoryIgnoreCase(String category);

    // Filter by location (partial match, case-insensitive)
    List<Event> findByLocationContainingIgnoreCase(String location);

    // Events on or after a given date
    List<Event> findByEventDateGreaterThanEqualOrderByEventDateAsc(LocalDate date);

    // Multi-filter: date + category
    List<Event> findByEventDateAndCategoryIgnoreCase(LocalDate eventDate, String category);

    // Search title or description by keyword
    @Query("SELECT e FROM Event e WHERE " +
           "LOWER(e.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(e.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Event> searchByKeyword(@Param("keyword") String keyword);
}
