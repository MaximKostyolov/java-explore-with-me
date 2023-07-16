package ru.practicum.ewmmainservice.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewmmainservice.request.dto.ConfirmedRequest;
import ru.practicum.ewmmainservice.request.model.Request;

import java.util.List;

public interface RequestJpaRepository extends JpaRepository<Request, Integer> {

    @Query(value = "select * from requests r " +
           "where (r.requester = ?1)", nativeQuery = true)
    List<Request> findAllByUserId(int userId);

    @Query(value = "select * from requests r " +
            "where (r.requester = ?1) " +
            "and (r.event = ?2)", nativeQuery = true)
    Request findByUserIdAndEventId(int userId, int eventId);

    @Query(value = "select * from requests r " +
            "where (r.event = ?1) " +
            "and (r.status = 'CONFIRMED')", nativeQuery = true)
    List<Request> findByEventAndStatusConfirmed(int eventId);

    @Query(value = "select * from requests r " +
            "where (r.event = ?1)", nativeQuery = true)
    List<Request> findByEventId(int eventId);

    @Query(value = "select * from requests r " +
            "where (r.event = ?1) " +
            "and (r.status = 'REJECTED')", nativeQuery = true)
    List<Request> findByEventIdAndStatusRejected(int eventId);

    @Query(value = "select r from Request r " +
            "where (r.id in ?1)")
    List<Request> findByIds(List<Integer> requestIds);

    @Query(value = "select * from requests r " +
            "where (r.event = ?1) " +
            "and (r.status = 'CONFIRMED')", nativeQuery = true)
    List<Request> getConfirmedRequest(int eventId);


    @Query(value = "select r.event as eventId, count(DISTINCT r.requester) as confirmedRequests " +
            "from requests r " +
            "where r.event in ?1 AND r.status = 'CONFIRMED' " +
            "group by r.event " +
            "order by count(DISTINCT r.requester) DESC", nativeQuery = true)
    List<ConfirmedRequest> findByEventsIdAndStatusConfirmed(List<Integer> eventsId);

}