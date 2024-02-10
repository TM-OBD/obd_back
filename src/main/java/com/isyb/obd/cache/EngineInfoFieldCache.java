package com.isyb.obd.cache;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Scope(scopeName = "singleton")
public class EngineInfoFieldCache {
    //    public static final ConcurrentHashMap<String, String> _ENGINE_INFO_FIELDS = new ConcurrentHashMap<>();
//    Записываем в словарь только при старте приложения, в дальнейшем только читаем, поэтому нет необходимости в блокировках
    public static final Map<String, String> _ENGINE_INFO_FIELDS_CACHE = new HashMap<>();
}
