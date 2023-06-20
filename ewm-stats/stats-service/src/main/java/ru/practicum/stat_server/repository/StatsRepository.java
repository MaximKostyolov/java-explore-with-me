package ru.practicum.stat_server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.stat_server.hitwiew.HitView;
import ru.practicum.stat_server.model.Endpoint;


import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends JpaRepository<Endpoint, Integer> {

    @Query(value = "select e.APP, e.URI, count(distinct e.IP) as HITS from endpoint_hit e " +
            "where (e.TIMESTAMP > ?1) " +
            "and (e.TIMESTAMP < ?2) " +
            "group by e.URI, e.APP " +
            "order by HITS desc", nativeQuery = true)
    List<HitView> getUniqueStats(LocalDateTime start, LocalDateTime end);

    @Query(value = "select e.APP, e.URI, count(IP) as HITS from endpoint_hit e " +
            "where (e.TIMESTAMP > ?1) " +
            "and (e.TIMESTAMP < ?2) " +
            "group by e.URI, e.APP " +
            "order by HITS desc", nativeQuery = true)
    List<HitView> getStats(LocalDateTime start, LocalDateTime end);

    @Query(value = "select e.APP, e.URI, count(distinct e.IP) as HITS from endpoint_hit e " +
            "where (e.TIMESTAMP > ?1) " +
            "and (e.TIMESTAMP < ?2) " +
            "and (e.URI in(?3)) " +
            "group by e.URI, e.APP " +
            "order by HITS desc", nativeQuery = true)
    List<HitView> getUniqueStatsWithUris(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query(value = "select e.APP, e.URI, count(IP) as HITS from endpoint_hit e " +
            "where (e.TIMESTAMP > ?1) " +
            "and (e.TIMESTAMP < ?2) " +
            "and (e.URI in(?3)) " +
            "group by e.URI, e.APP " +
            "order by HITS desc", nativeQuery = true)
    List<HitView> getStatsWithUris(LocalDateTime start, LocalDateTime end, List<String> uris);

}