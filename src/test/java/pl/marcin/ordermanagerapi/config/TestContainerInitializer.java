package pl.marcin.ordermanagerapi.config;

import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.GenericApplicationContext;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;


@Configuration
public class TestContainerInitializer implements ApplicationContextInitializer<GenericApplicationContext> {

    @Override
    public void initialize(GenericApplicationContext applicationContext) {
        new MySqlDatabaseInitializer().initialize(applicationContext);
    }

    static class MySqlDatabaseInitializer {
        private static final DockerImageName mySqlImage = DockerImageName.parse("mysql:8.0.32")
                .asCompatibleSubstituteFor("testdb2");
        private static final MySQLContainer mySQLContainer = (MySQLContainer) new MySQLContainer(mySqlImage)
                .withDatabaseName("testdb2")
                .withUsername("sa")
                .withPassword("sa")
                .withInitScript("data-h2.sql")
                ;

        static {
            mySQLContainer.start();
        }

        public void initialize(GenericApplicationContext applicationContext) {
            TestPropertyValues.of(
                    "spring.datasource.jdbc-url=" + mySQLContainer.getJdbcUrl(),
                    "spring.datasource.url=" + mySQLContainer.getJdbcUrl(),
                    "spring.datasource.username=" + mySQLContainer.getUsername(),
                    "spring.datasource.password=" + mySQLContainer.getPassword()
            ).applyTo(applicationContext.getEnvironment());
        }
    }
}
