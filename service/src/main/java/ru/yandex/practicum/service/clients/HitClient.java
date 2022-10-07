package ru.yandex.practicum.service.clients;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.yandex.practicum.service.dto.statistics.EndpointHit;

import java.util.List;
import java.util.Map;


@Service
@Slf4j
public class HitClient extends BaseClient {
    @Autowired
    public HitClient(@Value("http://stats-server:9090") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }


    public ResponseEntity<Object> createHit(EndpointHit endpointHit) {
        return post("/hit", endpointHit);
    }

    public ResponseEntity<Object> getStats(String start, String end, List<String> uris, Boolean unique) {
        StringBuilder sb=new StringBuilder();
        sb.append(uris.get(0));
        if(uris.size()>1){
            for(int i=1; i< uris.size(); i++){
                sb.append(","+ uris.get(i));
            }
        }
        String uri=String.valueOf(sb);
        Map<String, Object> parameters = Map.of(
                "start", start,
                "end", end,
                "uris", uri,
                "unique", unique.toString()
        );
        return get("/stats?start={start}&end={end}&uris={uris}&unique={unique}", parameters);
    }

}