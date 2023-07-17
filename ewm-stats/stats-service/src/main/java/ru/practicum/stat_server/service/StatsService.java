package ru.practicum.stat_server.service;

import ru.practicum.dto.EndpointHit;
import ru.practicum.dto.ViewStats;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {
    EndpointHit createHit(HttpServletRequest request, EndpointHit endpointHit);

    List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);

}