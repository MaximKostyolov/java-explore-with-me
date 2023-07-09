package ru.practicum.ewmmainservice.event.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewmmainservice.event.model.EventFullDto;
import ru.practicum.ewmmainservice.user.model.UserDto;

import java.time.LocalDateTime;
import java.util.List;

public interface EventJpaRepository extends JpaRepository<EventFullDto, Integer> {

    @Query(value = "select e from EventFullDto e " +
            "where e.initiator = ?1")
    List<EventFullDto> findByInitiator(UserDto user, Pageable page);

    @Query(value = "select * from events e " +
            "where (?1 is null or lower(e.annotation) like concat('%', ?1, '%') " +
            "or lower(e.description) like concat('%', ?1, '%')) " +
            "and (?2 is null or (e.category_id in ?2)) " +
            "and (?3 is null or (e.paid = ?3)) " +
            "and (e.event_date > ?4) " +
            "and (e.event_date < ?5) " +
            "and (?6 is null or e.available = ?6) " +
            "and (e.state = 'PUBLISHED')", nativeQuery = true)
    List<EventFullDto> findEvent(String text, List<Integer> categories, boolean paid, LocalDateTime startTime,
                   LocalDateTime endTime, boolean onlyAvailable, Pageable page);

    @Query(value = "select * from events e " +
            "where (?1 is null or (e.initiator in ?1)) " +
            "and (?2 is null or (e.category_id in ?2)) " +
            "and (?3 is null or (e.event_date > ?3)) " +
            "and (?4 is null or (e.event_date < ?4)) " +
            "and (?5 is null or (e.state in ?5)) " +
            "group by e.event_id", nativeQuery = true)
    List<EventFullDto> findEventToAdmin(List<Integer> users, List<Integer> categories, LocalDateTime startTime,
                                        LocalDateTime endTime, List<String> states, Pageable page);

    @Query(value = "select * from events e " +
            "where e.category_id = ?1", nativeQuery = true)
    List<EventFullDto> findByCategoryId(int categoryId);

    @Query(value = "select * from events e " +
            "where e.event_id in ?1 " +
            "group by e.event_id " +
            "order by e.event_id ASC", nativeQuery = true)
    List<EventFullDto> findByIds(List<Integer> eventsId);

}