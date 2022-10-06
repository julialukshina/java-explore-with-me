package ru.yandex.practicum.service.services.events;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.service.Statistics;
import ru.yandex.practicum.service.clients.HitClient;
import ru.yandex.practicum.service.dto.events.EventFullDto;
import ru.yandex.practicum.service.dto.events.EventShortDto;
import ru.yandex.practicum.service.dto.statistics.ViewStats;
import ru.yandex.practicum.service.enums.Sort;
import ru.yandex.practicum.service.enums.State;
import ru.yandex.practicum.service.enums.Status;
import ru.yandex.practicum.service.exeptions.MyNotFoundException;
import ru.yandex.practicum.service.exeptions.MyValidationException;
import ru.yandex.practicum.service.exeptions.TimeValidationException;
import ru.yandex.practicum.service.mappers.events.EventFullMapper;
import ru.yandex.practicum.service.mappers.events.EventShortMapper;
import ru.yandex.practicum.service.models.Event;
import ru.yandex.practicum.service.repositories.CategoryRepository;
import ru.yandex.practicum.service.repositories.EventRepository;
import ru.yandex.practicum.service.repositories.EventStorage;
import ru.yandex.practicum.service.repositories.RequestRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class EventPublicServiceImpl implements EventPublicService {
    private final EventRepository repository;
    private final EventStorage storage;

    private final CategoryRepository categoryRepository;
    private final RequestRepository requestRepository;
    private final HitClient hitClient;
    private final Statistics statistics;
    @Lazy
    private final EventFullMapper eventFullMapper;
    @Lazy
    private final EventShortMapper eventShortMapper;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
//    private final String startStat = LocalDateTime.now().minusDays(30).format(formatter);
//    private final String endStat = LocalDateTime.now().plusDays(30).format(formatter);
//    @Autowired
//    private Session session;

    @Autowired
    public EventPublicServiceImpl(EventRepository repository, EventStorage storage, CategoryRepository categoryRepository, RequestRepository requestRepository, HitClient hitClient, Statistics statistics, EventFullMapper eventFullMapper, EventShortMapper eventShortMapper) {
        this.repository = repository;
        this.storage = storage;
        this.categoryRepository = categoryRepository;
        this.requestRepository = requestRepository;
        this.hitClient = hitClient;
        this.statistics = statistics;
        this.eventFullMapper = eventFullMapper;
        this.eventShortMapper = eventShortMapper;
    }

//    @Override
//    public List<EventShortDto> getEvents(String text, List<Integer> categories, Boolean paid, String rangeStart, String rangeEnd, boolean isAvailable, Sort sort, int from, int size) {
//        LocalDateTime start = null;
//        LocalDateTime end = null;
//        List<Event> events = new ArrayList<>();
//        Pageable pageable = new MyPageable(from, size,
//                org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.ASC, "eventDate"));;
//        Pageable pageableWithoutSort = MyPageable.of(from, size);
//                if(rangeStart != null){
//            try{
//                start=LocalDateTime.parse(rangeStart);
//            }catch(TimeValidationException e){
//                e.getMessage();
//            }
//            if(rangeEnd != null){
//                try{
//                    end=LocalDateTime.parse(rangeEnd);
//                }catch(TimeValidationException e){
//                    e.getMessage();
//                }
//        }
//
//        if(text!=null){
//    if(!categories.isEmpty()){
//        if(paid !=null){
//            if(start != null||(start == null && end ==null)){
//                /**
//                 * указаны все параметры
//                 */
//                if(end != null){
//                    switch (sort){
//                        case EVENT_DATE:
//                            return repository.searchAllArgs(text, paid, categories, start, end, isAvailable, pageable).stream()
//                                    .map(EventShortMapper::toEventShortDto)
//                                    .collect(Collectors.toList());
//                        case VIEWS:
//                            // TODO: 25.09.2022 прикрутить статистику для сортировки
//                            events = repository.searchAllArgForStatistic(text, paid, categories, start, end, isAvailable);
//                            return events.stream()
//                                    .map(EventShortMapper::toEventShortDto)
//                                    .collect(Collectors.toList());
//                        default:
//                            return repository.searchAllArgs(text, paid, categories, start, end, isAvailable, pageableWithoutSort).stream()
//                                    .map(EventShortMapper::toEventShortDto)
//                                    .collect(Collectors.toList());
//                    }
//                }else{
//                    /**
//                     * указаны все параметры, кроме end
//                     */
//                    if(start==null){
//                        start= LocalDateTime.now();
//                    }
//                    switch (sort){
//                        case EVENT_DATE:
//                            return repository.searchAllArgsWithoutEnd(text, paid, categories, start, isAvailable, pageable).stream()
//                                    .map(EventShortMapper::toEventShortDto)
//                                    .collect(Collectors.toList());
//                        case VIEWS:
//                            // TODO: 25.09.2022 прикрутить статистику для сортировки
//                            events = repository.searchAllArgWithoutEndForStatistic(text, paid, categories, start, isAvailable);
//                            return events.stream()
//                                    .map(EventShortMapper::toEventShortDto)
//                                    .collect(Collectors.toList());
//                        default:
//                            return repository.searchAllArgsWithoutEnd(text, paid, categories, start, isAvailable, pageableWithoutSort).stream()
//                                    .map(EventShortMapper::toEventShortDto)
//                                    .collect(Collectors.toList());
//                    }
//                }
//                }else{
////                /**
////                 * нет ни end, ни start
////                 */
////                start = LocalDateTime.now();
////                switch (sort){
////                    case EVENT_DATE:
////                        return repository.searchAllArgsWithoutStartAndEnd(text, categories, paid, isAvailable, pageable).stream()
////                                .map(EventShortMapper::toEventShortDto)
////                                .collect(Collectors.toList());
////                    case VIEWS:
////                        // TODO: 25.09.2022 прикрутить статистику для сортировки
////                        events = repository.searchAllArgWithoutStartAndEndForStatistic(text, categories, paid, isAvailable);
////                        return events.stream()
////                                .map(EventShortMapper::toEventShortDto)
////                                .collect(Collectors.toList());
////                    default:
////                        return repository.searchAllArgsWithoutStartAndEnd(text, categories, paid, isAvailable, pageableWithoutSort).stream()
////                                .map(EventShortMapper::toEventShortDto)
////                                .collect(Collectors.toList());
////               }
//                /**
//                 * нет start, но есть end
//                 */
//                switch (sort){
//                    case EVENT_DATE:
//                        return repository.searchAllArgsWithoutStartWithEnd(text, paid, categories, end, isAvailable, pageable).stream()
//                                .map(EventShortMapper::toEventShortDto)
//                                .collect(Collectors.toList());
//                    case VIEWS:
//                        // TODO: 25.09.2022 прикрутить статистику для сортировки
//                        events = repository.searchAllArgWithoutStartWithEndForStatistic(text, paid, categories, end, isAvailable);
//                        return events.stream()
//                                .map(EventShortMapper::toEventShortDto)
//                                .collect(Collectors.toList());
//                    default:
//                        return repository.searchAllArgsWithoutStartWithEnd(text, paid, categories, end, isAvailable, pageableWithoutSort).stream()
//                                .map(EventShortMapper::toEventShortDto)
//                                .collect(Collectors.toList());
//                }
//            }
//            }else{
//            /**
//             * нет paid
//             */
//            if(start != null||(start == null && end ==null)){
//                if(end != null){
//                    switch (sort){
//                        case EVENT_DATE:
//                            return repository.searchAllArgsWithoutPaid(text, categories, start, end, isAvailable, pageable).stream()
//                                    .map(EventShortMapper::toEventShortDto)
//                                    .collect(Collectors.toList());
//                        case VIEWS:
//                            // TODO: 25.09.2022 прикрутить статистику для сортировки
//                            events = repository.searchAllArgForStatisticWithoutPaid(text, categories, start, end, isAvailable);
//                            return events.stream()
//                                    .map(EventShortMapper::toEventShortDto)
//                                    .collect(Collectors.toList());
//                        default:
//                            return repository.searchAllArgsWithoutPaid(text, categories, start, end, isAvailable, pageableWithoutSort).stream()
//                                    .map(EventShortMapper::toEventShortDto)
//                                    .collect(Collectors.toList());
//                    }
//                }else{
//                    /**
//                     * указаны все параметры, кроме end, paid
//                     */
//                    if(start==null){
//                        start= LocalDateTime.now();
//                    }
//                    switch (sort){
//                        case EVENT_DATE:
//                            return repository.searchAllArgsWithoutPaidAndEnd(text, categories, start, isAvailable, pageable).stream()
//                                    .map(EventShortMapper::toEventShortDto)
//                                    .collect(Collectors.toList());
//                        case VIEWS:
//                            // TODO: 25.09.2022 прикрутить статистику для сортировки
//                            events = repository.searchAllArgWithoutPaidAndEndForStatistic(text, categories, start, isAvailable);
//                            return events.stream()
//                                    .map(EventShortMapper::toEventShortDto)
//                                    .collect(Collectors.toList());
//                        default:
//                            return repository.searchAllArgsWithoutPaidAndEnd(text, categories, start, isAvailable, pageableWithoutSort).stream()
//                                    .map(EventShortMapper::toEventShortDto)
//                                    .collect(Collectors.toList());
//                    }
//                }
//            }else{
//                /**
//                 * нет start, но есть end
//                 */
//                switch (sort){
//                    case EVENT_DATE:
//                        return repository.searchAllArgsWithoutPaidAndStartWithEnd(text, categories, end, isAvailable, pageable).stream()
//                                .map(EventShortMapper::toEventShortDto)
//                                .collect(Collectors.toList());
//                    case VIEWS:
//                        // TODO: 25.09.2022 прикрутить статистику для сортировки
//                        events = repository.searchAllArgWithoutPaidAndStartWithEndForStatistic(text, categories, end, isAvailable);
//                        return events.stream()
//                                .map(EventShortMapper::toEventShortDto)
//                                .collect(Collectors.toList());
//                    default:
//                        return repository.searchAllArgsWithoutPaidAndStartWithEnd(text, categories, end, isAvailable, pageableWithoutSort).stream()
//                                .map(EventShortMapper::toEventShortDto)
//                                .collect(Collectors.toList());
//                }
//            }
//        }
//        }else{
//        /**
//         * нет categories
//         */
//        if(paid !=null){
//            if(start != null||(start == null && end ==null)){
//                if(end != null){
//                    switch (sort){
//                        case EVENT_DATE:
//                            return repository.searchAllArgsWithoutCategories(text, paid, start, end, isAvailable, pageable).stream()
//                                    .map(EventShortMapper::toEventShortDto)
//                                    .collect(Collectors.toList());
//                        case VIEWS:
//                            // TODO: 25.09.2022 прикрутить статистику для сортировки
//                            events = repository.searchAllArgWithoutCategoriesForStatistic(text, paid, start, end, isAvailable);
//                            return events.stream()
//                                    .map(EventShortMapper::toEventShortDto)
//                                    .collect(Collectors.toList());
//                        default:
//                            return repository.searchAllArgsWithoutCategories(text, paid, start, end, isAvailable, pageableWithoutSort).stream()
//                                    .map(EventShortMapper::toEventShortDto)
//                                    .collect(Collectors.toList());
//                    }
//                }else{
//                    /**
//                     * указаны все параметры, кроме categories и end
//                     */
//                    if(start==null){
//                        start= LocalDateTime.now();
//                    }
//                    switch (sort){
//                        case EVENT_DATE:
//                            return repository.searchAllArgsWithoutCategoriesAndEnd(text, paid, start, isAvailable, pageable).stream()
//                                    .map(EventShortMapper::toEventShortDto)
//                                    .collect(Collectors.toList());
//                        case VIEWS:
//                            // TODO: 25.09.2022 прикрутить статистику для сортировки
//                            events = repository.searchAllArgWithoutCategoriesAndEndForStatistic(text, paid, start, isAvailable);
//                            return events.stream()
//                                    .map(EventShortMapper::toEventShortDto)
//                                    .collect(Collectors.toList());
//                        default:
//                            return repository.searchAllArgsWithoutCategoriesAndEnd(text, paid, start, isAvailable, pageableWithoutSort).stream()
//                                    .map(EventShortMapper::toEventShortDto)
//                                    .collect(Collectors.toList());
//                    }
//                }
//            }else{
//                /**
//                 * нет start, categories, но есть end
//                 */
//                switch (sort){
//                    case EVENT_DATE:
//                        return repository.searchAllArgsWithoutStartAndCategoriesWithEnd(text, paid, end, isAvailable, pageable).stream()
//                                .map(EventShortMapper::toEventShortDto)
//                                .collect(Collectors.toList());
//                    case VIEWS:
//                        // TODO: 25.09.2022 прикрутить статистику для сортировки
//                        events = repository.searchAllArgWithoutStartAndCategoriesWithEndForStatistic(text, paid, end, isAvailable);
//                        return events.stream()
//                                .map(EventShortMapper::toEventShortDto)
//                                .collect(Collectors.toList());
//                    default:
//                        return repository.searchAllArgsWithoutStartAndCategoriesWithEnd(text, paid, end, isAvailable, pageableWithoutSort).stream()
//                                .map(EventShortMapper::toEventShortDto)
//                                .collect(Collectors.toList());
//                }
//            }
//        }else{
//            /**
//             * нет paid, categories
//             */
//            if(start != null||(start == null && end ==null)){
//                if(end != null){
//                    switch (sort){
//                        case EVENT_DATE:
//                            return repository.searchAllArgsWithoutPaidAndCategories(text, start, end, isAvailable, pageable).stream()
//                                    .map(EventShortMapper::toEventShortDto)
//                                    .collect(Collectors.toList());
//                        case VIEWS:
//                            // TODO: 25.09.2022 прикрутить статистику для сортировки
//                            events = repository.searchAllArgForStatisticWithoutPaidAndCategories(text, start, end, isAvailable);
//                            return events.stream()
//                                    .map(EventShortMapper::toEventShortDto)
//                                    .collect(Collectors.toList());
//                        default:
//                            return repository.searchAllArgsWithoutPaidAndCategories(text, start, end, isAvailable, pageableWithoutSort).stream()
//                                    .map(EventShortMapper::toEventShortDto)
//                                    .collect(Collectors.toList());
//                    }
//                }else{
//                    /**
//                     * указаны все параметры, кроме end, paid
//                     */
//                    if(start==null){
//                        start= LocalDateTime.now();
//                    }
//                    switch (sort){
//                        case EVENT_DATE:
//                            return repository.searchAllArgsWithoutPaidCategoriesAndEnd(text, start, isAvailable, pageable).stream()
//                                    .map(EventShortMapper::toEventShortDto)
//                                    .collect(Collectors.toList());
//                        case VIEWS:
//                            // TODO: 25.09.2022 прикрутить статистику для сортировки
//                            events = repository.searchAllArgWithoutPaidCategoriesAndEndForStatistic(text, start, isAvailable);
//                            return events.stream()
//                                    .map(EventShortMapper::toEventShortDto)
//                                    .collect(Collectors.toList());
//                        default:
//                            return repository.searchAllArgsWithoutPaidCategoriesAndEnd(text, start, isAvailable, pageableWithoutSort).stream()
//                                    .map(EventShortMapper::toEventShortDto)
//                                    .collect(Collectors.toList());
//                    }
//                }
//            }else{
//                /**
//                 * нет start, paid, categories, но есть end
//                 */
//                switch (sort){
//                    case EVENT_DATE:
//                        return repository.searchAllArgsWithoutPaidCategoriesAndStartWithEnd(text, end, isAvailable, pageable).stream()
//                                .map(EventShortMapper::toEventShortDto)
//                                .collect(Collectors.toList());
//                    case VIEWS:
//                        // TODO: 25.09.2022 прикрутить статистику для сортировки
//                        events = repository.searchAllArgWithoutPaidCategoriesAndStartWithEndForStatistic(text, end, isAvailable);
//                        return events.stream()
//                                .map(EventShortMapper::toEventShortDto)
//                                .collect(Collectors.toList());
//                    default:
//                        return repository.searchAllArgsWithoutPaidCategoriesAndStartWithEnd(text, end, isAvailable, pageableWithoutSort).stream()
//                                .map(EventShortMapper::toEventShortDto)
//                                .collect(Collectors.toList());
//                }
//            }
//        }
//
//    }
//    }
//}
//
//        return events.stream()
//                .map(EventShortMapper::toEventShortDto)
//                .collect(Collectors.toList());
//    }

    @Override
    public List<EventShortDto> getEvents(String text, List<Integer> categories, Boolean paid, String rangeStart,
                                         String rangeEnd, boolean isAvailable, Sort sort, int from, int size) {
//        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
//        SessionFactory sessionFactory =
//                entityManagerFactory.unwrap(SessionFactory.class);
//
//        CriteriaBuilder cb = session.getCriteriaBuilder();
//        CriteriaQuery<Event> cr = cb.createQuery(Event.class);
//        Root<Event> root = cr.from(Event.class);
//        cr.select(root);
//
//        Query<Event> query = session.createQuery(cr);
//        List<Event> results = query.getResultList();
List<EventShortDto> eventDtos = new ArrayList<>();
List<String> uris = new ArrayList<>();
List<ViewStats> views = new ArrayList<>();
Map<String, EventShortDto> uriEventDtos = new HashMap<>();
        LocalDateTime start = null;
        LocalDateTime end = null;
                if(rangeStart != null) {
                    try {
                        start = LocalDateTime.parse(rangeStart, formatter);
                    } catch (TimeValidationException e) {
                        e.getMessage();
                    }
                }
            if(rangeEnd != null){
                try{
                    end=LocalDateTime.parse(rangeEnd, formatter);
                }catch(TimeValidationException e){
                    e.getMessage();
                }
        }
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM Events WHERE ");
        if(text!=null){
            sb.append(String.format("upper(annotation) like upper(concat('%s')) or upper(description) like upper(concat( '%s')) ", text, text));
        }
        if(categories.size()>0){
            StringBuilder builder = new StringBuilder();
            categoryValidation(Long.valueOf(categories.get(0)));
            builder.append(categories.get(0));
            if(categories.size()>1){
                for(int i=1; i<categories.size(); i++){
                    categoryValidation(Long.valueOf(categories.get(i)));
                    builder.append(", "+ categories.get(i));
                }
            }
            sb.append("AND category_id in (" + builder + ") ");
        }
        if(paid!=null){
            sb.append(String.format("AND paid='%s' ", paid));
        }
        if(start != null||(start == null && end ==null)){
            if(start==null){
                start=LocalDateTime.now();
            }
            sb.append(String.format("AND event_date>='%s' ", start));
        }
        if(end!=null){
            sb.append(String.format("AND event_date<='%s' ", end));
        }
        // TODO: 04.10.2022 джойнить таблицы

        if(isAvailable){


            sb.append("AND participant_limit=0 OR participant_limit > confirmedRequests.size ");
        }

       if(sort.equals("EVENT_DATE")){
           sb.append("ORDER BY event_date ");
       }
//       if(sort.equals("EVENT_DATE") || sort==null){
//           sb.append(String.format("LIMIT '%d' OFFSET '%d'"), size, from);
//       }
       if(sb.toString().contains("WHERE AND")){
           int i = sb.indexOf("AND");
           sb.delete(i, i+3);
       }
        String sqlQuery = String.valueOf(sb);
        System.out.println("\n");
        System.out.println("\n");
        System.out.println(sqlQuery);
        System.out.println("\n");
        System.out.println("\n");
//
//        eventDtos = storage.getEvents(sqlQuery).stream()
//                .map(EventShortMapper::toEventShortDto)
//                .collect(Collectors.toList());
//        for (EventShortDto dto:
//                eventDtos) {
//            StringBuilder stringBuilder=new StringBuilder();
//            stringBuilder.append("http://localhost:8080/events/"+dto.getId());
//            uris.add(String.valueOf(stringBuilder));
//            uriEventDtos.put(String.valueOf(stringBuilder), dto);
//        }
//        eventDtos.clear();
//        views = (List<ViewStats>) hitClient.getStats(startStat, endStat, uris, false);
//        for (ViewStats view:
//             views) {
//            EventShortDto dto = uriEventDtos.get(view.getUri());
//            dto.setViews(view.getHits());
//            eventDtos.add(dto);
//        }
        System.out.println("\n");
        System.out.println("\n");
        System.out.println(repository.findAll());
        System.out.println("\n");
        System.out.println("\n");
        List<Event> events = storage.getEvents(sqlQuery);
        System.out.println("\n");
        System.out.println("\n");
        System.out.println(events);
        System.out.println("\n");
        System.out.println("\n");
        if(sort.equals("EVENT_DATE") || sort==null){
            if(isAvailable){
                return statistics.getListEventShortDtoWithViews(events.stream()
                        .map(eventFullMapper::toEventFullDto)
                        .filter(eventFullDto -> eventFullDto.getConfirmedRequests()<eventFullDto.getParticipantLimit() ||
                                eventFullDto.getParticipantLimit()==0)
                        .skip(from)
                        .limit(size)
                        .map(eventFullMapper::toEvent)
                        .collect(Collectors.toList()));
            }else{
                return statistics.getListEventShortDtoWithViews(events.stream()
                        .skip(from)
                        .limit(size)
                        .collect(Collectors.toList()));
            }
        }

   if(sort.equals("VIEWS")){
       if(isAvailable){
           return statistics.getListEventShortDtoWithViews(events.stream()
                   .map(eventFullMapper::toEventFullDto)
                   .filter(eventFullDto -> eventFullDto.getConfirmedRequests()<eventFullDto.getParticipantLimit() ||
                           eventFullDto.getParticipantLimit()==0)
                   .sorted(Comparator.comparing(o->o.getViews()))
                   .skip(from)
                   .limit(size)
                   .map(eventFullMapper::toEvent)
                   .collect(Collectors.toList()));
       }else{
           return  statistics.getListEventShortDtoWithViews(events).stream()
                   .sorted(Comparator.comparing(o->o.getViews()))
                   .skip(from)
                   .limit(size)
                   .collect(Collectors.toList());
       }
//       statistics.getListEventShortDtoWithViews(storage.getEvents(sqlQuery)).stream()
//               .sorted(Comparator.comparing(o->o.getViews()))
//               .skip(from)
//               .limit(size)
//               .collect(Collectors.toList());
   }
return null;
  //      return statistics.getListEventShortDtoWithViews(storage.getEvents(sqlQuery));
//        return storage.getEvents(sqlQuery).stream()
//                .map(eventShortMapper::toEventShortDto)
//                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto getEventById(Long id) {
        eventValidation(id);
        System.out.println(repository.findById(id));
        EventFullDto dto = eventFullMapper.toEventFullDto(repository.findById(id).get());
        if (!dto.getState().equals("PUBLISHED")) {
            throw new MyValidationException("Только опубликованные события могут быть просмотрены");
        }
//        String uri = "http://localhost:8080/events/"+dto.getId();
//        List <String> uris = new ArrayList<>();
//        uris.add(uri);
//        List <ViewStats> views = (List<ViewStats>)hitClient.getStats(startStat, endStat, uris, false);
//        dto.setViews(views.get(0).getHits());

        return statistics.getEventFullDtoWithViews(dto);
 //       return dto;
    }


    private void eventValidation(Long id) {
        if (repository.findById(id).isEmpty()) {
            throw new MyNotFoundException(String.format("Событие с id= '%s' не найдено", id));
        }
    }

    private void categoryValidation(Long id) {
        if (categoryRepository.findById(id).isEmpty()) {
            throw new MyNotFoundException(String.format("Категория с id= '%s' не найдена", id));
        }
    }

    @Override
    public Event getById(Long id) {
        eventValidation(id);
        return repository.findById(id).get();
    }
//    private long getConfirmRequest(Long id){
//        long eventConfirmedRequest = 0;
//                    if(requestRepository.countByEventIdAndStatus(id, Status.CONFIRMED) != null){
//                eventConfirmedRequest=requestRepository.countByEventIdAndStatus(id, Status.CONFIRMED);
//            }
//          return eventConfirmedRequest;
//    }
}
