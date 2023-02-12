package pl.marcin.ordermanagerapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.marcin.ordermanagerapi.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Order findByOrderNumber(String orderNumber);
}
