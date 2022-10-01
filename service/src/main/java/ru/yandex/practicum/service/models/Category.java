package ru.yandex.practicum.service.models;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "Events")
@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "name", nullable = false)
    private String name;
}
