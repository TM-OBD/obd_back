package com.isyb.obd.models.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.OperatingSystemMXBean;
import java.util.HashMap;
import java.util.Map;


public class HeartbeatMonitorDto {
    @Getter
    @Setter
    private String serviceName;
    @Getter
    @Setter
    private String strategyProcessing;
    @Getter
    private String state = collectJVMStatusAsJson();

    private static String collectJVMStatusAsJson() {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> jvmStatusMap = new HashMap<>();

        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        OperatingSystemMXBean osMXBean = ManagementFactory.getOperatingSystemMXBean();
        MemoryUsage heapMemoryUsage = memoryMXBean.getHeapMemoryUsage();
        MemoryUsage nonHeapMemoryUsage = memoryMXBean.getNonHeapMemoryUsage();
        double cpuUsage = osMXBean.getSystemLoadAverage();

        jvmStatusMap.put("heapMemoryUsage", heapMemoryUsage);
        jvmStatusMap.put("nonHeapMemoryUsage", nonHeapMemoryUsage);
        jvmStatusMap.put("cpuUsage", cpuUsage);

        try {
            return objectMapper.writeValueAsString(jvmStatusMap);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "{}";
        }
    }

    public String toJson() {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            return objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "{}";
        }
    }
}
