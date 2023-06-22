package ru.practicum.stat_server.mapper;


import ru.practicum.dto.EndpointHit;
import ru.practicum.dto.ViewStats;
import ru.practicum.stat_server.hitwiew.HitView;
import ru.practicum.stat_server.model.Endpoint;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Mapper {

    public static Endpoint endpointHitToEndpoint(EndpointHit endpointHit) {
        if (endpointHit == null) {
            return null;
        }

        Endpoint endpoint = Endpoint.builder()
                .id(endpointHit.getId())
                .app(endpointHit.getApp())
                .uri(endpointHit.getUri())
                .ip(endpointHit.getIp())
                .timestamp(LocalDateTime.parse(endpointHit.getTimestamp(),
                           DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build();

        return endpoint;
    }

    public static EndpointHit endpointToEndpointHit(Endpoint endpoint) {
        if (endpoint == null) {
            return null;
        }

        EndpointHit endpointHit = EndpointHit.builder()
                .id(endpoint.getId())
                .app(endpoint.getApp())
                .uri(endpoint.getUri())
                .ip(endpoint.getIp())
                .timestamp(endpoint.getTimestamp().toString())
                .build();

        return endpointHit;
    }

    public static List<ViewStats> hitViewListToViewStatsList(List<HitView> hits) {
        List<ViewStats> hitViewList = new ArrayList<>();
        for (HitView hit : hits) {
            hitViewList.add(hitViewToViewStats(hit));
        }
        return hitViewList;
    }

    public static ViewStats hitViewToViewStats(HitView hit) {
        if (hit == null) {
            return null;
        }

        ViewStats viewStats = new ViewStats();

        viewStats.setApp(hit.getApp());
        viewStats.setUri(hit.getUri());
        viewStats.setHits(hit.getHits());

        return viewStats;
    }

}