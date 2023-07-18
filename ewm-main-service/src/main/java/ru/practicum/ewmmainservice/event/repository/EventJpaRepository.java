package ru.practicum.ewmmainservice.event.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewmmainservice.event.model.Event;
import ru.practicum.ewmmainservice.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public interface EventJpaRepository extends JpaRepository<Event, Integer> {

    @Query(value = "select e from Event e " +
            "where e.initiator = ?1")
    List<Event> findByInitiator(User user, Pageable page);

    @Query(value = "select e " +
            "from Event e " +
            "where e.annotation like (concat('%', :text, '%')) " +
            "or e.description like (concat('%', :text, '%')) " +
            "and (:categories is null or e.category.id in :categories) " +
            "and (:paid is null or e.paid = :paid) " +
            "and e.eventDate between :startTime and :endTime " +
            "and (:onlyAvailable is null or e.available = :onlyAvailable) " +
            "and e.state = 'PUBLISHED'")
    List<Event> findEvent(String text, List<Integer> categories, boolean paid, LocalDateTime startTime,
                          LocalDateTime endTime, boolean onlyAvailable, Pageable page);

    @Query(value = "select e " +
            "from Event e " +
            "where (:users is null or e.initiator.id in :users) " +
            "and (:states is null or e.state in :states) " +
            "and (:categories is null or e.category.id in :categories) " +
            "and e.eventDate between :startTime and :endTime ")
    List<Event> findEventToAdmin(List<Integer> users, List<Integer> categories, LocalDateTime startTime,
                                 LocalDateTime endTime, List<String> states, Pageable page);

    @Query(value = "select * from events e " +
            "where e.category_id = ?1", nativeQuery = true)
    List<Event> findByCategoryId(int categoryId);

    @Query(value = "select * from events e " +
            "where e.event_id in ?1", nativeQuery = true)
    List<Event> findByIds(List<Integer> eventsId);

    @Query(value = "select * from events e " +
            "where e.initiator in ?1 " +
            "and e.state = 'PUBLISHED'", nativeQuery = true)
    List<Event> findByInitiatorIds(List<Integer> followingIds, Pageable page);
}