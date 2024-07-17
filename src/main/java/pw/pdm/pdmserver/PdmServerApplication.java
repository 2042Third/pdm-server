package pw.pdm.pdmserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.scheduling.annotation.EnableScheduling;
import pw.pdm.pdmserver.config.GraalVMHints;

@SpringBootApplication
@EnableScheduling
@ImportRuntimeHints(GraalVMHints.class)
public class PdmServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(PdmServerApplication.class, args);
    }
}
