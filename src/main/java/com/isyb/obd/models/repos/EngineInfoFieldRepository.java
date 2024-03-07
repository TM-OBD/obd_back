package com.isyb.obd.models.repos;

import com.isyb.obd.models.entities.EngineInfoField;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface EngineInfoFieldRepository extends ReactiveCrudRepository<EngineInfoField, Long> {
    @Query(value = "SELECT column_name FROM information_schema.columns WHERE table_name = :tableName")
    Flux<String> findAllColumnsByTableName(String tableName);
}
