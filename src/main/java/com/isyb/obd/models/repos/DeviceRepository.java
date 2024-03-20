package com.isyb.obd.models.repos;

import com.isyb.obd.models.entities.Device;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface DeviceRepository extends ReactiveCrudRepository<Device, Long> {
    @Query(value = "SELECT * FROM devices WHERE deviceid = :deviceId")
    Mono<Device> findByDeviceId(String deviceId);
}
