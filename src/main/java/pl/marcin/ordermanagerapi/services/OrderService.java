package pl.marcin.ordermanagerapi.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.marcin.ordermanagerapi.dto.*;
import pl.marcin.ordermanagerapi.entity.Order;
import pl.marcin.ordermanagerapi.entity.OrderItem;
import pl.marcin.ordermanagerapi.entity.Status;
import pl.marcin.ordermanagerapi.mapper.OrderMapper;
import pl.marcin.ordermanagerapi.repository.OrderItemRepository;
import pl.marcin.ordermanagerapi.repository.OrderRepository;
import pl.marcin.ordermanagerapi.stock.services.StockInvoker;


import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final StockInvoker stockInvoker;
    private final OrderMapper orderMapper;

    @Transactional
    public OrderSummaryDto createOrder(CreateOrderDto createOrderDto) {
        for (CreateOrderItemDto createOrderItemDto : createOrderDto.getOrderItems()) {
            StockDto stock = stockInvoker.getStock(createOrderItemDto.getProductId());
            if (stock.getAvailableQuantity() - stock.getReservedQuantity() < createOrderItemDto.getQuantity()) {
                throw new RuntimeException("Order can not be created because of stock availability");
            } else {
                stockInvoker.reserveQuantityOfProduct(createOrderItemDto.getProductId(), createOrderItemDto.getQuantity());
            }
        }
        AtomicLong orderNumber = new AtomicLong();
        orderNumber.incrementAndGet();
        Order order = new Order();
        order.setOrderNumber(orderNumber);
        order.setStatus(Status.IN_PROGRESS);
        orderRepository.save(order);

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();
        for (CreateOrderItemDto createOrderItemDto : createOrderDto.getOrderItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProductId(createOrderItemDto.getProductId());
            orderItem.setQuantity(createOrderItemDto.getQuantity());
            orderItem.setPriceItem(createOrderItemDto.getPriceItem());

            //ProductDto product = stockInvoker.getProduct(createOrderItemDto.getProductId());
            orderItem.setTotalAmount(createOrderItemDto.getTotalAmount());
            totalAmount.add(orderItem.getTotalAmount());
            orderItemRepository.save(orderItem);
            orderItems.add(orderItem);
        }

        order.setTotalAmount(totalAmount);
        orderRepository.save(order);

        OrderSummaryDto orderSummary = new OrderSummaryDto();

        orderSummary.setOrderNumber(order.getOrderNumber());
        orderSummary.setTotalAmount(order.getTotalAmount());
        orderSummary.setStatus(Status.ORDERED);
        for (OrderItem orderItem : orderItems) {
            OrderItemSummaryDto orderItemSummary = new OrderItemSummaryDto();
            orderItemSummary.setProductId(orderItem.getProductId());
            orderItemSummary.setQuantity(orderItem.getQuantity());
            orderItemSummary.setAmount(orderItem.getTotalAmount());
            orderItemSummary.setTotalAmount(orderItem.getTotalAmount());
        }
        return orderSummary;
    }

    public void cancelOrder(String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber);
        order.setStatus(Status.CANCELLED);
        List<OrderItem> orderItems = order.getOrderItems();
        Map<Long, Long> cancelledProducts = new HashMap<>();
        for (OrderItem orderItem : orderItems) {
            Long productId = orderItem.getProductId();
            Long quantity = orderItem.getQuantity();
            cancelledProducts.put(productId, quantity);
        }
        stockInvoker.updateStock(cancelledProducts);
        //how to send it?

        orderRepository.save(order);
    }

    public OrderSummaryDto getOrder(String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber);
        List<OrderItem> orderItems = order.getOrderItems();
        List<OrderItemSummaryDto> orderItemSummaryDtos = orderMapper.orderItemListToOrderItemSummaryDtoList(orderItems);
        OrderSummaryDto orderSummaryDto = new OrderSummaryDto();
        orderSummaryDto.setOrderItems(orderItemSummaryDtos);
        orderSummaryDto.setOrderNumber(order.getOrderNumber());
        orderSummaryDto.setStatus(order.getStatus());
        orderSummaryDto.setOrderid(orderSummaryDto.getOrderid());
        orderSummaryDto.setTotalAmount(order.getTotalAmount());
        return orderSummaryDto;
    }
}
