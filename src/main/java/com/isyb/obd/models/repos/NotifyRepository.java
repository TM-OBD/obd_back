package com.isyb.obd.models.repos;

import com.isyb.obd.models.entities.EngineInfo;
import com.isyb.obd.models.entities.FunctionResultOfLoginLogoutNotify;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;


@Repository
public interface NotifyRepository extends ReactiveCrudRepository<EngineInfo, Long> {
    @Query(value = "SELECT * FROM onLoginDevice(:deviceId)")
    Mono<FunctionResultOfLoginLogoutNotify> onLoginDevice(String deviceId);

    @Query(value = "SELECT * FROM logindeviceinprogress(:deviceId)")
    Mono<FunctionResultOfLoginLogoutNotify> loginDeviceInProgress(String deviceId);
}
