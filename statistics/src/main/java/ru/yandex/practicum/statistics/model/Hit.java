package ru.yandex.practicum.statistics.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Hits")
@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class Hit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "uri")
    String uri;
    @Column(name = "app")
    String app;
    @Column(name = "ip")
    String ip;
    @Column(name="timestamp")
    LocalDateTime timestamp;
}
