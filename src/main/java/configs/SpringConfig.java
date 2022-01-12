package configs;

import database.DatabaseContext;
import lombok.SneakyThrows;
import model.StudyGroup;
import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

@Configuration
@ComponentScan("controller")
@EnableWebMvc
public class SpringConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**").addResourceLocations("/static/");
    }

    @Bean
    public SessionFactory sessionFactory(){
        return DatabaseContext.getSessionFactory();
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
