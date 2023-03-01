package pl.marcin.ordermanagerapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.marcin.ordermanagerapi.entity.Order;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Order findByOrderNumber(String orderNumber);

    @Query("FROM Order o JOIN FETCH o.orderItems")
    List<Order> findAllWithOrderItemsEager();

}
