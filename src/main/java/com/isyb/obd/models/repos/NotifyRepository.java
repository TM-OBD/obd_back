package com.isyb.obd.models.repos;

import com.isyb.obd.models.entities.ObdInfo;
import com.isyb.obd.models.entities.FunctionResultOfLoginLogoutNotify;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;


@Repository
public interface NotifyRepository extends ReactiveCrudRepository<ObdInfo, Long> {
    @Query(value = "SELECT * FROM on_login_device(:deviceId)")
    Mono<FunctionResultOfLoginLogoutNotify> onLoginDevice(String deviceId);

    @Query(value = "SELECT * FROM on_logout_device(:deviceId)")
    Mono<FunctionResultOfLoginLogoutNotify> onLogoutDevice(String deviceId);

    @Query(value = "SELECT * FROM poll_device_in_progress(:deviceId)")
    Mono<FunctionResultOfLoginLogoutNotify> pollDeviceInProgress(String deviceId);
}
