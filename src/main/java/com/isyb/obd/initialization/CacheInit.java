package com.isyb.obd.initialization;

import com.isyb.obd.models.entities.ObdInfoField;
import com.isyb.obd.models.repos.ObdInfoFieldRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import static com.isyb.obd.cache.ObdInfoFieldCache._OBD_INFO_FIELDS_CACHE;

@Component
@Order(2)
public class CacheInit implements ApplicationRunner {

    @Autowired
    private ObdInfoFieldRepository obdInfoFieldRepository;

    @Override
    public void run(ApplicationArguments args) {
        Flux<ObdInfoField> all = obdInfoFieldRepository.findAll();
        all.subscribe(field -> _OBD_INFO_FIELDS_CACHE.put(field.getPid(), field.getField_name()));
    }
}
