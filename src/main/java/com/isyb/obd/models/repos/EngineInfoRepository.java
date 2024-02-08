package com.isyb.obd.models.repos;

import com.isyb.obd.models.entities.EngineInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EngineInfoRepository extends JpaRepository<EngineInfo, Long> {
}
