package ru.yandex.practicum.service.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.service.models.Event;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    // TODO: 26.09.2022 isAvailable учитывается неправильно


//    @Query(value = " select e from Event e, Request r " +
//            "where upper(e.annotation) like upper(concat('%', ?1, '%')) " +
//            "or upper(e.description) like upper(concat('%', ?1, '%')) " +
//            "and e.paid = ?2 " +
//            "and e.category.id in :categories " +
//            "and e.eventDate >= :start " +
//            "and e.eventDate <= :end " +
//            "and e.participantLimit=0 or e.participantLimit > e.confirmedRequests.size")
//    Page<Event> searchAllArgs(String text, Boolean paid,  @Param("categories") List<Integer> categories, @Param("start") LocalDateTime start,
//                              @Param("end") LocalDateTime end, boolean isAvailable, Pageable pageable);
//
//    @Query(value = " select e from Event e, Request r " +
//            "where upper(e.annotation) like upper(concat('%', ?1, '%')) " +
//            "or upper(e.description) like upper(concat('%', ?1, '%')) " +
//            "and e.paid = ?2 " +
//            "and e.category.id in :categories " +
//            "and e.eventDate >= :start " +
//            "and e.eventDate <= :end " +
//            "and e.participantLimit=0 or e.participantLimit > e.confirmedRequests.size")
//    List<Event> searchAllArgForStatistic(String text, Boolean paid,  @Param("categories") List<Integer> categories, @Param("start") LocalDateTime start,
//                                         @Param("end") LocalDateTime end, boolean isAvailable);
//
//    @Query(value = " select e from Event e, Request r " +
//            "where upper(e.annotation) like upper(concat('%', ?1, '%')) " +
//            "or upper(e.description) like upper(concat('%', ?1, '%')) " +
//            "and e.paid = ?2 " +
//            "and e.category.id in :categories " +
//            "and e.eventDate >= :start " +
//            "and e.participantLimit=0 or e.participantLimit > e.confirmedRequests.size")
//    Page<Event> searchAllArgsWithoutEnd(String text, Boolean paid, @Param("categories") List<Integer> categories, @Param("start") LocalDateTime start,
//                                        boolean isAvailable, Pageable pageable);
//
//    @Query(value = " select e from Event e, Request r " +
//            "where upper(e.annotation) like upper(concat('%', ?1, '%')) " +
//            "or upper(e.description) like upper(concat('%', ?1, '%')) " +
//            "and e.paid = ?2 " +
//            "and e.category.id in :categories " +
//            "and e.eventDate >= :start " +
//            "and e.participantLimit=0 or e.participantLimit > e.confirmedRequests.size")
//    List<Event> searchAllArgWithoutEndForStatistic(String text, Boolean paid, @Param("categories") List<Integer> categories, @Param("start") LocalDateTime start,
//                                                    boolean isAvailable);
//
//    @Query(value = " select e from Event e, Request r " +
//            "where upper(e.annotation) like upper(concat('%', ?1, '%')) " +
//            "or upper(e.description) like upper(concat('%', ?1, '%')) " +
//            "and e.paid = ?2 " +
//            "and e.category.id in :categories " +
//            "and e.eventDate <= :end " +
//            "and e.participantLimit=0 or e.participantLimit > e.confirmedRequests.size")
//    Page<Event> searchAllArgsWithoutStartWithEnd(String text, Boolean paid, @Param("categories") List<Integer> categories, @Param("end") LocalDateTime end, boolean isAvailable, Pageable pageable);
//
//    @Query(value = " select e from Event e, Request r " +
//            "where upper(e.annotation) like upper(concat('%', ?1, '%')) " +
//            "or upper(e.description) like upper(concat('%', ?1, '%')) " +
//            "and e.paid = ?2 " +
//            "and e.category.id in :categories " +
//            "and e.eventDate <= :end " +
//            "and e.participantLimit=0 or e.participantLimit > e.confirmedRequests.size")
//    List<Event> searchAllArgWithoutStartWithEndForStatistic(String text,Boolean paid, @Param("categories") List<Integer> categories, @Param("end") LocalDateTime end,
//                                                           boolean isAvailable);
//
//    @Query(value = " select e from Event e, Request r " +
//            "where upper(e.annotation) like upper(concat('%', ?1, '%')) " +
//            "or upper(e.description) like upper(concat('%', ?1, '%')) " +
//            "and e.category.id in :categories " +
//            "and e.eventDate >= :start " +
//            "and e.eventDate <= :end " +
//            "and e.participantLimit=0 or e.participantLimit > e.confirmedRequests.size")
//    Page<Event> searchAllArgsWithoutPaid(String text, @Param("categories") List<Integer> categories, @Param("start") LocalDateTime start,
//                                         @Param("end") LocalDateTime end, boolean isAvailable, Pageable pageable);
//
//    @Query(value = " select e from Event e, Request r " +
//            "where upper(e.annotation) like upper(concat('%', ?1, '%')) " +
//            "or upper(e.description) like upper(concat('%', ?1, '%')) " +
//            "and e.category.id in :categories " +
//            "and e.eventDate >= :start " +
//            "and e.eventDate <= :end " +
//            "and e.participantLimit=0 or e.participantLimit > e.confirmedRequests.size")
//    List<Event> searchAllArgForStatisticWithoutPaid(String text, @Param("categories") List<Integer> categories, @Param("start") LocalDateTime start,
//                                                    @Param("end") LocalDateTime end, boolean isAvailable);
//
//    @Query(value = " select e from Event e, Request r " +
//            "where upper(e.annotation) like upper(concat('%', ?1, '%')) " +
//            "or upper(e.description) like upper(concat('%', ?1, '%')) " +
//            "and e.category.id in :categories " +
//            "and e.eventDate >= :start " +
//            "and e.participantLimit=0 or e.participantLimit > e.confirmedRequests.size")
//    Page<Event> searchAllArgsWithoutPaidAndEnd(String text, @Param("categories") List<Integer> categories, @Param("start") LocalDateTime start,
//                                               boolean isAvailable, Pageable pageable);
//
//    @Query(value = " select e from Event e, Request r " +
//            "where upper(e.annotation) like upper(concat('%', ?1, '%')) " +
//            "or upper(e.description) like upper(concat('%', ?1, '%')) " +
//            "and e.category.id in :categories " +
//            "and e.eventDate >= :start " +
//            "and e.participantLimit=0 or e.participantLimit > e.confirmedRequests.size")
//    List<Event> searchAllArgWithoutPaidAndEndForStatistic(String text, @Param("categories") List<Integer> categories, @Param("start") LocalDateTime start,
//                                                           boolean isAvailable);
//
//    @Query(value = " select e from Event e, Request r " +
//            "where upper(e.annotation) like upper(concat('%', ?1, '%')) " +
//            "or upper(e.description) like upper(concat('%', ?1, '%')) " +
//            "and e.category.id in :categories " +
//            "and e.eventDate <= :end " +
//            "and e.participantLimit=0 or e.participantLimit > e.confirmedRequests.size")
//    Page<Event> searchAllArgsWithoutPaidAndStartWithEnd(String text, @Param("categories") List<Integer> categories, @Param("end") LocalDateTime end, boolean isAvailable, Pageable pageable);
//
//    @Query(value = " select e from Event e, Request r " +
//            "where upper(e.annotation) like upper(concat('%', ?1, '%')) " +
//            "or upper(e.description) like upper(concat('%', ?1, '%')) " +
//            "and e.category.id in :categories " +
//            "and e.eventDate <= :end " +
//            "and e.participantLimit=0 or e.participantLimit > e.confirmedRequests.size")
//    List<Event> searchAllArgWithoutPaidAndStartWithEndForStatistic(String text, @Param("categories") List<Integer> categories, @Param("end") LocalDateTime end, boolean isAvailable);
//
//    @Query(value = " select e from Event e, Request r " +
//            "where upper(e.annotation) like upper(concat('%', ?1, '%')) " +
//            "or upper(e.description) like upper(concat('%', ?1, '%')) " +
//            "and e.paid = ?2 " +
//            "and e.eventDate >= :start " +
//            "and e.eventDate <= :end " +
//            "and e.participantLimit=0 or e.participantLimit > e.confirmedRequests.size")
//    Page<Event> searchAllArgsWithoutCategories(String text, Boolean paid, @Param("start") LocalDateTime start,
//                                               @Param("end") LocalDateTime end, boolean isAvailable, Pageable pageable);
//
//    @Query(value = " select e from Event e, Request r " +
//            "where upper(e.annotation) like upper(concat('%', ?1, '%')) " +
//            "or upper(e.description) like upper(concat('%', ?1, '%')) " +
//            "and e.paid = ?2 " +
//            "and e.eventDate >= :start " +
//            "and e.eventDate <= :end " +
//            "and e.participantLimit=0 or e.participantLimit > e.confirmedRequests.size")
//    List<Event> searchAllArgWithoutCategoriesForStatistic(String text, Boolean paid, @Param("start") LocalDateTime start,
//                                                          @Param("end") LocalDateTime end, boolean isAvailable);
//
//    @Query(value = " select e from Event e, Request r " +
//            "where upper(e.annotation) like upper(concat('%', ?1, '%')) " +
//            "or upper(e.description) like upper(concat('%', ?1, '%')) " +
//            "and e.paid = ?2 " +
//            "and e.eventDate >= :start " +
//            "and e.participantLimit=0 or e.participantLimit > e.confirmedRequests.size")
//    Page<Event> searchAllArgsWithoutCategoriesAndEnd(String text, Boolean paid, @Param("start") LocalDateTime start, boolean isAvailable, Pageable pageable);
//
//    @Query(value = " select e from Event e, Request r " +
//            "where upper(e.annotation) like upper(concat('%', ?1, '%')) " +
//            "or upper(e.description) like upper(concat('%', ?1, '%')) " +
//            "and e.paid = ?2 " +
//            "and e.eventDate >= :start " +
//            "and e.participantLimit=0 or e.participantLimit > e.confirmedRequests.size")
//    List<Event> searchAllArgWithoutCategoriesAndEndForStatistic(String text, Boolean paid, @Param("start") LocalDateTime start, boolean isAvailable);
//
//    @Query(value = " select e from Event e, Request r " +
//            "where upper(e.annotation) like upper(concat('%', ?1, '%')) " +
//            "or upper(e.description) like upper(concat('%', ?1, '%')) " +
//            "and e.paid = ?2 " +
//            "and e.eventDate <= :end " +
//            "and e.participantLimit=0 or e.participantLimit > e.confirmedRequests.size")
//    Page<Event> searchAllArgsWithoutStartAndCategoriesWithEnd(String text, Boolean paid, @Param("end")  LocalDateTime end, boolean isAvailable, Pageable pageable);
//
//    @Query(value = " select e from Event e, Request r " +
//            "where upper(e.annotation) like upper(concat('%', ?1, '%')) " +
//            "or upper(e.description) like upper(concat('%', ?1, '%')) " +
//            "and e.paid = ?2 " +
//            "and e.eventDate <= :end " +
//            "and e.participantLimit=0 or e.participantLimit > e.confirmedRequests.size")
//    List<Event> searchAllArgWithoutStartAndCategoriesWithEndForStatistic(String text, Boolean paid, @Param("end")  LocalDateTime end, boolean isAvailable);
//    @Query(value = " select e from Event e, Request r " +
//            "where upper(e.annotation) like upper(concat('%', ?1, '%')) " +
//            "or upper(e.description) like upper(concat('%', ?1, '%')) " +
//            "and e.eventDate >= :start " +
//            "and e.eventDate <= :end " +
//            "and e.participantLimit=0 or e.participantLimit > e.confirmedRequests.size")
//    Page<Event> searchAllArgsWithoutPaidAndCategories(String text, @Param("start") LocalDateTime start, @Param("end")  LocalDateTime end, boolean isAvailable, Pageable pageable);
//
//    @Query(value = " select e from Event e, Request r " +
//            "where upper(e.annotation) like upper(concat('%', ?1, '%')) " +
//            "or upper(e.description) like upper(concat('%', ?1, '%')) " +
//            "and e.eventDate >= :start " +
//            "and e.eventDate <= :end " +
//            "and e.participantLimit=0 or e.participantLimit > e.confirmedRequests.size")
//    List<Event> searchAllArgForStatisticWithoutPaidAndCategories(String text, @Param("start") LocalDateTime start, @Param("end")  LocalDateTime end, boolean isAvailable);
//
//    @Query(value = " select e from Event e, Request r " +
//            "where upper(e.annotation) like upper(concat('%', ?1, '%')) " +
//            "or upper(e.description) like upper(concat('%', ?1, '%')) " +
//            "and e.eventDate >= :start " +
//            "and e.participantLimit=0 or e.participantLimit > e.confirmedRequests.size")
//    Page<Event> searchAllArgsWithoutPaidCategoriesAndEnd(String text, @Param("start") LocalDateTime start, boolean isAvailable, Pageable pageable);
//
//    @Query(value = " select e from Event e, Request r " +
//            "where upper(e.annotation) like upper(concat('%', ?1, '%')) " +
//            "or upper(e.description) like upper(concat('%', ?1, '%')) " +
//            "and e.eventDate >= :start " +
//            "and e.participantLimit=0 or e.participantLimit > e.confirmedRequests.size")
//    List<Event> searchAllArgWithoutPaidCategoriesAndEndForStatistic(String text, @Param("start") LocalDateTime start, boolean isAvailable);
//
//    @Query(value = " select e from Event e, Request r " +
//            "where upper(e.annotation) like upper(concat('%', ?1, '%')) " +
//            "or upper(e.description) like upper(concat('%', ?1, '%')) " +
//            "and e.eventDate <= :end " +
//            "and e.participantLimit=0 or e.participantLimit > e.confirmedRequests.size")
//    Page<Event> searchAllArgsWithoutPaidCategoriesAndStartWithEnd(String text, @Param("end") LocalDateTime end, boolean isAvailable, Pageable pageable);
//
//    @Query(value = " select e from Event e, Request r " +
//            "where upper(e.annotation) like upper(concat('%', ?1, '%')) " +
//            "or upper(e.description) like upper(concat('%', ?1, '%')) " +
//            "and e.eventDate <= :end " +
//            "and e.participantLimit=0 or e.participantLimit > e.confirmedRequests.size")
//    List<Event> searchAllArgWithoutPaidCategoriesAndStartWithEndForStatistic(String text, @Param("end") LocalDateTime end, boolean isAvailable);

    Page<Event> findByInitiator(Long userId, Pageable pageable);

    List<Event> findByCategory(Long catId);
}

