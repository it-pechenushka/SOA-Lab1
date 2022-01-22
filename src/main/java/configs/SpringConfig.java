package configs;

import lombok.SneakyThrows;
import model.Location;
import model.Person;
import model.StudyGroup;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Environment;
import org.hibernate.service.ServiceRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.util.Properties;

@Configuration
@ComponentScan("controller")
@EnableWebMvc
@PropertySource(value= {"classpath:application.properties"})
public class SpringConfig implements WebMvcConfigurer {

    private final org.springframework.core.env.Environment env;

    public SpringConfig(org.springframework.core.env.Environment env) {
        this.env = env;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**").addResourceLocations("/static/");
    }

    @Bean
    public SessionFactory sessionFactory(){
        SessionFactory sessionFactory = null;
        try {
            org.hibernate.cfg.Configuration configuration = new org.hibernate.cfg.Configuration();

            // Hibernate settings equivalent to hibernate.cfg.xml's properties
            Properties settings = new Properties();
            settings.put(Environment.DRIVER, env.getRequiredProperty("app.database.driver"));
            settings.put(Environment.URL, env.getRequiredProperty("app.database.url"));
            settings.put(Environment.USER, env.getRequiredProperty("app.database.user"));
            settings.put(Environment.PASS, env.getRequiredProperty("app.database.password"));
            settings.put(Environment.SHOW_SQL, env.getRequiredProperty("app.database.show_sql"));
            settings.put(Environment.CURRENT_SESSION_CONTEXT_CLASS, env.getRequiredProperty("app.database.cscc"));
            settings.put(Environment.HBM2DDL_AUTO, env.getRequiredProperty("app.database.mode"));
            settings.put(Environment.POOL_SIZE, env.getRequiredProperty("app.database.pool_size"));
            configuration.setProperties(settings);

            //Entities
            configuration.addAnnotatedClass(StudyGroup.class);
            configuration.addAnnotatedClass(Person.class);
            configuration.addAnnotatedClass(Location.class);

            ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                    .applySettings(configuration.getProperties()).build();

            sessionFactory = configuration.buildSessionFactory(serviceRegistry);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return sessionFactory;
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
