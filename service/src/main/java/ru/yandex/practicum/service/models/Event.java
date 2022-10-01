package ru.yandex.practicum.service.models;


import lombok.*;
import ru.yandex.practicum.service.enums.State;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "Events")
@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "annotation", nullable = false)
    private String annotation;
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
    @OneToMany(mappedBy = "event", fetch = FetchType.LAZY)
    private List<Request> confirmedRequests;
    @Column(name = "createdOn", nullable = false)
    private LocalDateTime createdOn;
    @Column(name = "description", nullable = false)
    private String description;
    @Column(name = "eventDate", nullable = false)
    private LocalDateTime eventDate;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User initiator;
    @Column(name = "paid", nullable = false)
    private boolean paid;
    @Column(name = "participantLimit")
    private long participantLimit;
    @Column(name = "publishedOn")
    private LocalDateTime publishedOn;
    @Column(name = "requestModeration")
    private boolean requestModeration;
    @Column(name = "title", nullable = false)
    private String title;
    @Enumerated(EnumType.STRING)
    @Column(name = "state")
    private State state;
}
