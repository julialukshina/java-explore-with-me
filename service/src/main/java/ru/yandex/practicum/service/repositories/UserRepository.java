package ru.yandex.practicum.service.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.yandex.practicum.service.models.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    //    List<User> findAllById(Iterable<Long> ids);
    @Query("select u from User u where u.id in :ids")
    Page<User> findAllByIds(List<Long> ids, Pageable pageable);

    Page<User> findAll(Pageable pageable);

//        @Query(value = " select u from User u " +
//            "where upper(e.annotation) like upper(concat('%', ?1, '%')) " +
//            "or upper(e.description) like upper(concat('%', ?1, '%')) " +
//            "and e.paid = ?2 " +
//            "and e.category.id in :categories " +
//            "and e.eventDate >= :start " +
//            "and e.participantLimit=0 or e.participantLimit > e.confirmedRequests.size")
//        Page<User> searchW(String text, Boolean paid, @Param("categories") List<Integer> categories, @Param("start") LocalDateTime start,
//                          boolean isAvailable, Pageable pageable);
}
