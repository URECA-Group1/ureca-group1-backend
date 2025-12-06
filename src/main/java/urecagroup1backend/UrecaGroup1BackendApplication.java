package urecagroup1backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
@EnableFeignClients(basePackages = "urecagroup1backend")
public class UrecaGroup1BackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(UrecaGroup1BackendApplication.class, args);
    }

}
