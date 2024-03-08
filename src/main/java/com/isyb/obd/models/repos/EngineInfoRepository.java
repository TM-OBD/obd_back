package com.isyb.obd.models.repos;

import com.isyb.obd.models.entities.EngineInfo;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EngineInfoRepository extends ReactiveCrudRepository<EngineInfo, Long> {

}
