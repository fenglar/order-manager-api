package pl.marcin.ordermanagerapi.config;

import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.GenericApplicationContext;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

@Configuration
public class TestContainerInitializer implements ApplicationContextInitializer<GenericApplicationContext> {

    private static final DockerImageName MY_SQL_IMAGE = DockerImageName.parse("mysql:8.0.32")
        .asCompatibleSubstituteFor("testdb2");
    private static final MySQLContainer MY_SQL_CONTAINER = (MySQLContainer) new MySQLContainer(MY_SQL_IMAGE)
        .withDatabaseName("testdb2")
        .withUsername("sa")
        .withPassword("sa")
        .withReuse(true);
//        .withInitScript("prepare.sql");

    static {
        MY_SQL_CONTAINER.start();
    }

    @Override
    public void initialize(GenericApplicationContext applicationContext) {
        TestPropertyValues.of(
            "spring.datasource.jdbc-url=" + MY_SQL_CONTAINER.getJdbcUrl(),
            "spring.datasource.url=" + MY_SQL_CONTAINER.getJdbcUrl(),
            "spring.datasource.username=" + MY_SQL_CONTAINER.getUsername(),
            "spring.datasource.password=" + MY_SQL_CONTAINER.getPassword()
        ).applyTo(applicationContext.getEnvironment());
    }
}
