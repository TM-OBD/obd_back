package com.isyb.obd.initialization;

import com.isyb.obd.models.entities.EngineInfoField;
import com.isyb.obd.models.repos.EngineInfoFieldRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import static com.isyb.obd.cache.EngineInfoFieldCache._ENGINE_INFO_FIELDS_CACHE;

@Component
@Order(2)
public class CacheInit implements ApplicationRunner {

    @Autowired
    private EngineInfoFieldRepository engineInfoFieldRepository;

    @Override
    public void run(ApplicationArguments args) {
        Flux<EngineInfoField> all = engineInfoFieldRepository.findAll();
        all.subscribe(field -> _ENGINE_INFO_FIELDS_CACHE.put(field.getSymbol(), field.getField_name()));
    }
}
