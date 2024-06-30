package pw.pdm.pdmserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PdmServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(PdmServerApplication.class, args);
    }

}
