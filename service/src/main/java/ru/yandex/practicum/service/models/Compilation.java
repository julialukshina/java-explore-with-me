package ru.yandex.practicum.service.models;

import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "Users")
@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class Compilation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    /**
     * возможно, так не сработает, плюс придется доработать переопределенные методы
     */
//    @Column(columnDefinition = "events_id[]")
//    @Type(type = "models/CustomIntegerArrayType.java")
//    private Long[] events;
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "Events_Compilations", joinColumns = @JoinColumn(name = "compilation_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id"))
    private Set<Event> events;

    @Column(name = "pinned", nullable = false)
    private boolean pinned;
    @Column(name = "title", nullable = false)
    private String title;
}
