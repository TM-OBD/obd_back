package com.isyb.obd;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Arrays;
import java.util.stream.Collectors;


@SpringBootApplication
@EnableScheduling
public class ObdApplication {
    private static final Logger log = LogManager.getLogger(ObdApplication.class);

    public static void main(String[] args) {
        log.info("Prepare starting application");
        SpringApplication.run(ObdApplication.class, args);
        log.info("Starting application. Arguments: {}", Arrays.stream(args).collect(Collectors.joining(", ")));
    }

}
