package app;

import lombok.SneakyThrows;
import app.model.StudyGroup;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

@SpringBootApplication
@EnableDiscoveryClient
public class BootApplication {
    public static void main(String[] args) {
        SpringApplication.run(BootApplication.class, args);
    }

    @SneakyThrows
    @Bean
    public Unmarshaller unmarshaller(){
        Unmarshaller unmarshaller = JAXBContext.newInstance(StudyGroup.class).createUnmarshaller();
        unmarshaller.setEventHandler(event -> false);

        return unmarshaller;
    }

    @Bean
    public Validator validator(){
        return Validation.buildDefaultValidatorFactory().getValidator();
    }
}
