package ru.yandex.practicum.service.models;


import lombok.*;
import ru.yandex.practicum.service.enums.State;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "annotation", nullable = false)
    private String annotation;
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
    @Column(name = "created_on", nullable = false)
    private LocalDateTime createdOn;
    @Column(name = "description", nullable = false)
    private String description;
    @Column(name = "event_date", nullable = false)
    private LocalDateTime eventDate;
    @ManyToOne
    @JoinColumn(name = "initiator_id")
    private User initiator;
    @Column(name = "lat", nullable = false)
    private Float lat;
    @Column(name = "lon", nullable = false)
    private Float lon;
    @Column(name = "paid", nullable = false)
    private boolean paid;
    @Column(name = "participant_limit")
    private long participantLimit;
    @Column(name = "published_on")
    private LocalDateTime publishedOn;
    @Column(name = "request_moderation")
    private boolean requestModeration;
    @Column(name = "title", nullable = false)
    private String title;
    @Enumerated(EnumType.STRING)
    @Column(name = "state")
    private State state;
    @Column(name = "comment_moderation")
    private boolean commentModeration;
}
