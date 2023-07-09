package ru.practicum.stat_server.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.dto.EndpointHit;
import ru.practicum.dto.ViewStats;
import ru.practicum.stat_server.hitwiew.HitView;
import ru.practicum.stat_server.mapper.Mapper;
import ru.practicum.stat_server.model.Endpoint;
import ru.practicum.stat_server.repository.StatsRepository;


import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class StatsServiceImpl implements StatsService {

    private final StatsRepository statsRepository;

    @Override
    public EndpointHit createHit(HttpServletRequest request, EndpointHit endpointHit) {
        Endpoint endpoint = Mapper.endpointHitToEndpoint(endpointHit);
        statsRepository.save(endpoint);
        return Mapper.endpointToEndpointHit(endpoint);
    }

    @Override
    public List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        List<HitView> stats;
        if (start.isBefore(end)) {
            if (uris.isEmpty()) {
                if (unique) {
                    stats = statsRepository.getUniqueStats(start, end);
                } else {
                    stats = statsRepository.getStats(start, end);
                }
            } else {
                if (unique) {
                    stats = statsRepository.getUniqueStatsWithUris(start, end, uris);
                } else {
                    stats = statsRepository.getStatsWithUris(start, end, uris);
                }
            }
            return Mapper.hitViewListToViewStatsList(stats);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ошибка валидации запроса.");
        }
    }

}