package com.isyb.obd.initialization;

import com.isyb.obd.cache.EngineInfoFieldCache;
import com.isyb.obd.models.entities.EngineInfoField;
import com.isyb.obd.models.repos.EngineInfoFieldRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Order(2)
public class CacheInit implements ApplicationRunner {

    @Autowired
    private EngineInfoFieldRepository engineInfoFieldRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        List<EngineInfoField> all = engineInfoFieldRepository.findAll();

        for (EngineInfoField infoField : all) {
            EngineInfoFieldCache._ENGINE_INFO_FIELDS_CACHE.put(infoField.getSymbol(), infoField.getField_name());
        }
    }
}
