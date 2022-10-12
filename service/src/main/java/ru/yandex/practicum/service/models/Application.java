package ru.yandex.practicum.service.models;

import lombok.*;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.service.enums.AppReason;
import ru.yandex.practicum.service.enums.AppStatus;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "applications")
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Validated
@AllArgsConstructor
@NoArgsConstructor
public class Application {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "text")
    private String text;
    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;
    @Column(name = "created")
    private LocalDateTime created;
    @Enumerated(EnumType.STRING)
    @Column(name = "app_status")
    private AppStatus appStatus;
    @Enumerated(EnumType.STRING)
    @Column(name = "app_reason")
    private AppReason appReason;
}
