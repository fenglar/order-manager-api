package pl.marcin.ordermanagerapi;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.marcin.ordermanagerapi.dto.CreateOrderDto;
import pl.marcin.ordermanagerapi.dto.CreateOrderItemDto;
import pl.marcin.ordermanagerapi.dto.OrderSummaryDto;
import pl.marcin.ordermanagerapi.dto.StockDto;
import pl.marcin.ordermanagerapi.entity.Order;
import pl.marcin.ordermanagerapi.entity.OrderItem;
import pl.marcin.ordermanagerapi.entity.Status;
import pl.marcin.ordermanagerapi.mapper.OrderMapper;
import pl.marcin.ordermanagerapi.repository.OrderItemRepository;
import pl.marcin.ordermanagerapi.repository.OrderRepository;
import pl.marcin.ordermanagerapi.services.OrderService;
import pl.marcin.ordermanagerapi.stock.services.StockInvoker;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private StockInvoker stockInvoker;

    @Mock
    private OrderMapper orderMapper;
    @InjectMocks
    OrderService orderService;

    @BeforeEach
    public void setUp() {
    }

    @Test
    public void ShouldCreateOrderWhenStockIsAvailable() {
        // given
        List<CreateOrderItemDto> orderItems = new ArrayList<>();
        CreateOrderItemDto orderItem = CreateOrderItemDto.builder()
            .productId(1L)
            .quantity(10L)
            .priceItem(BigDecimal.valueOf(100))
            .totalAmount(BigDecimal.valueOf(1000))
            .build();
        orderItems.add(orderItem);
        CreateOrderDto createOrderDto = CreateOrderDto.builder()
            .orderItems(orderItems).build();

        StockDto stockDto = StockDto.builder()
            .availableQuantity(20L)
            .reservedQuantity(10L)
            .build();

        when(stockInvoker.getStock(anyLong())).thenReturn(stockDto);

        // when
        OrderSummaryDto orderSummary = orderService.createOrder(createOrderDto);

        // then
        assertThat(orderSummary.getOrderNumber()).isEqualTo(1L);
        // TODO: Fix it
//        assertThat(orderSummary.getTotalAmount()).isEqualTo(BigDecimal.valueOf(1000));
        assertThat(orderSummary.getStatus()).isEqualTo(Status.ORDERED);
    }
}
