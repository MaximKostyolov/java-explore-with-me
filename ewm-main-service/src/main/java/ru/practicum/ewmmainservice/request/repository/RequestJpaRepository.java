package ru.practicum.ewmmainservice.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewmmainservice.request.model.ParticipationRequestDto;

import java.util.List;

public interface RequestJpaRepository extends JpaRepository<ParticipationRequestDto, Integer> {

    @Query(value = "select * from requests r " +
           "where (r.requester = ?1)", nativeQuery = true)
    List<ParticipationRequestDto> findAllByUserId(int userId);

    @Query(value = "select * from requests r " +
            "where (r.requester = ?1) " +
            "and (r.event = ?2)", nativeQuery = true)
    ParticipationRequestDto findByUserIdAndEventId(int userId, int eventId);

    @Query(value = "select * from requests r " +
            "where (r.event = ?1) " +
            "and (r.status = 'CONFIRMED')", nativeQuery = true)
    List<ParticipationRequestDto> findByEventAndStatusConfirmed(int eventId);

    @Query(value = "select * from requests r " +
            "where (r.event = ?1)", nativeQuery = true)
    List<ParticipationRequestDto> findByEventId(int eventId);

    @Query(value = "select * from requests r " +
            "where (r.event = ?1) " +
            "and (r.status = 'REJECTED')", nativeQuery = true)
    List<ParticipationRequestDto> findByEventIdAndStatusRejected(int eventId);

    @Query(value = "select r from ParticipationRequestDto r " +
            "where (r.id in ?1)")
    List<ParticipationRequestDto> findByIds(List<Integer> requestIds);

    @Query(value = "select * from requests r " +
            "where (r.event = ?1) " +
            "and (r.status = 'CONFIRMED')", nativeQuery = true)
    List<ParticipationRequestDto> getConfirmedRequest(int eventId);

}