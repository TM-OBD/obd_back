package com.isyb.obd.models.repos;

import com.isyb.obd.models.entities.EngineInfoField;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface EngineInfoFieldRepository extends JpaRepository<EngineInfoField, Long> {
    @Query(value = "SELECT column_name FROM information_schema.columns WHERE table_name = ?1", nativeQuery = true)
    Set<String> findAllColumnsByTableName(String tableName);
}
