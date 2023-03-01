package pl.marcin.ordermanagerapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class OrderManagerApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrderManagerApiApplication.class, args);
	}

}
