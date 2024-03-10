package com.isyb.obd.models.repos;

import com.isyb.obd.models.entities.ObdInfo;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ObdInfoRepository extends ReactiveCrudRepository<ObdInfo, Long> {

}
