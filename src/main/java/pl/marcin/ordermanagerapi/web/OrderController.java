package pl.marcin.ordermanagerapi.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.marcin.ordermanagerapi.dto.CreateOrderDto;
import pl.marcin.ordermanagerapi.dto.OrderSummaryDto;
import pl.marcin.ordermanagerapi.services.OrderService;

@RestController
@RequestMapping("/api/order")
public class OrderController {
    private final OrderService orderService;

    OrderController(OrderService orderService){
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderSummaryDto> createOrder(@RequestBody CreateOrderDto createOrderDto) {
        OrderSummaryDto orderSummary = orderService.createOrder(createOrderDto);
        return ResponseEntity.ok(orderSummary);
    }

    @DeleteMapping("/{orderNumber}")
    public ResponseEntity<String> cancelOrder(@PathVariable String orderNumber) {
        orderService.cancelOrder(orderNumber);
        return ResponseEntity.ok("Order has been cancelled");
    }

    @GetMapping("/{orderNumber}")
    public ResponseEntity<OrderSummaryDto> getOrder(@PathVariable String orderNumber) {
        return ResponseEntity.ok(orderService.getOrder(orderNumber));
    }
}
