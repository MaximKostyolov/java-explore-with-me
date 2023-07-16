package ru.practicum.ewmmainservice.event.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Formula;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.ewmmainservice.category.model.Category;
import ru.practicum.ewmmainservice.event.model.location.Location;
import ru.practicum.ewmmainservice.user.model.User;


import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private Integer id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String annotation;

    @Column(nullable = false)
    private String description;

    @ManyToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "category_id", referencedColumnName = "category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "initiator", referencedColumnName = "user_id")
    private User initiator;

    @ManyToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "location_id", referencedColumnName = "location_id")
    private Location location;

    @Column(nullable = false)
    private boolean paid;

    @Column(name = "request_moderation", columnDefinition = "boolean default true")
    private boolean requestModeration;

    @Column(columnDefinition = "integer default 0")
    private Integer participantLimit;

    @Column(name = "event_date", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    @Column(name = "created_on", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdOn;

    @Column(name = "published_on")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishedOn;

    private String state;

    @Column(name = "available", columnDefinition = "boolean default true")
    private boolean available;

}