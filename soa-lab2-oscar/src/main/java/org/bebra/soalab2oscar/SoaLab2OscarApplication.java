package org.bebra.soalab2oscar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class SoaLab2OscarApplication {

    public static void main(String[] args) {
        SpringApplication.run(SoaLab2OscarApplication.class, args);
    }

}
