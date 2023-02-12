package pl.marcin.ordermanagerapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.marcin.ordermanagerapi.entity.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
