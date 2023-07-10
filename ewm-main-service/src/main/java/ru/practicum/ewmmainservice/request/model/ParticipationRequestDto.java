package ru.practicum.ewmmainservice.request.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.ewmmainservice.event.model.EventFullDto;
import ru.practicum.ewmmainservice.user.model.UserDto;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "requests")
public class ParticipationRequestDto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private Integer id;

    @Column(nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created;

    @ManyToOne
    @JoinColumn(name = "event", referencedColumnName = "event_id")
    private EventFullDto event;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "requester", referencedColumnName = "user_id")
    private UserDto requester;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RequestStatus status;

}