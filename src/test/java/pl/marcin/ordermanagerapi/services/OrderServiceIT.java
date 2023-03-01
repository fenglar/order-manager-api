package pl.marcin.ordermanagerapi.services;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import pl.marcin.ordermanagerapi.OrderManagerApiApplication;
import pl.marcin.ordermanagerapi.config.TestContainerInitializer;
import pl.marcin.ordermanagerapi.dto.OrderItemSummaryDto;
import pl.marcin.ordermanagerapi.dto.OrderSummaryDto;
import pl.marcin.ordermanagerapi.entity.Order;
import pl.marcin.ordermanagerapi.entity.OrderItem;
import pl.marcin.ordermanagerapi.entity.Status;
import pl.marcin.ordermanagerapi.repository.OrderItemRepository;
import pl.marcin.ordermanagerapi.repository.OrderRepository;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
@SpringBootTest
@ContextConfiguration(classes = {OrderManagerApiApplication.class},
    initializers = {TestContainerInitializer.class})
@ActiveProfiles("integration-test")
class OrderServiceIT {

    private static final AtomicBoolean FIRST_RUN = new AtomicBoolean(TRUE);

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private OrderService orderService;

    @Value("${spring.datasource.url}")
    private String jdbcUrl;

    @BeforeEach
    @Transactional
    public void setUpData() {
        log.info("###################################################################################");
        log.info(jdbcUrl);
        log.info("###################################################################################");

        if (FIRST_RUN.compareAndExchange(TRUE, FALSE)) {
            orderRepository.deleteAll();
            orderRepository.saveAll(createOrders(RandomUtils.nextInt(10, 50)));
        }
    }

    @Test
    public void shouldGetAllOrder_NPlusOneProblem() {
        // when
        log.info("==========================================================================================");
        log.info("====================           N + 1 PROBLEM         =====================================");
        log.info("==========================================================================================");
        List<OrderSummaryDto> orderSummaryDtos = orderService.getAllOrders_NPlusOne();

        // then
        assertNotNull(orderSummaryDtos);
    }

    @Test
    public void shouldGetOrderItem_NPlusOneProblem() {
        // when
        log.info("==========================================================================================");
        log.info("====================           N + 1 PROBLEM         =====================================");
        log.info("==========================================================================================");
        OrderItemSummaryDto orderItemSummaryDto = orderService.getOneItemWithFindAll();

        // then
        assertNotNull(orderItemSummaryDto);
    }

    @Test
    public void shouldGetOrderItem_NPlusOneProblemSolvingJoinFetch() {
        // when
        final Long orderItemId = orderItemRepository.findAll().stream().findAny().map(OrderItem::getId).orElse(null);
        log.info("==========================================================================================");
        log.info("====================           N + 1 PROBLEM         =====================================");
        log.info("==========================================================================================");
        OrderItemSummaryDto orderItemSummaryDto = orderService.getOneItem(orderItemId);

        // then
        assertNotNull(orderItemSummaryDto);
    }

    @Test
    public void shouldGetAllOrder_SolutionOneForNPlusOneProblem() {
        // when
        log.info("==========================================================================================");
        log.info("====================           N + 1 PROBLEM - Solution #1    ============================");
        log.info("==========================================================================================");
        List<OrderSummaryDto> orderSummaryDtos = orderService.getAllOrders_NPlusOneSolutionOne();

        // then
        assertNotNull(orderSummaryDtos);
    }

    @Test
    public void shouldGetAllOrder_SolutionTwoForNPlusOneProblem() {
        // when
        log.info("==========================================================================================");
        log.info("====================           N + 1 PROBLEM - Solution #2    ============================");
        log.info("==========================================================================================");
        List<OrderSummaryDto> orderSummaryDtos = orderService.getAllOrders_NPlusOneSolutionTwo();

        // then
        assertNotNull(orderSummaryDtos);
    }

    @Test
    // TODO: create an example
    public void shouldGetAnOrderItem_ProblemWithLazyAndTransaction() {

        OrderItem orderItem = orderService.getAnyItem();

        assertNotNull(orderItem);
        assertNotNull(orderItem.getOrder().getId());
    }

    private List<Order> createOrders(int numberOfOrders) {
        return IntStream.rangeClosed(0, numberOfOrders)
            .boxed()
            .map(i -> createOrder(numberOfOrders))
            .collect(Collectors.toList());
    }

    private Order createOrder(int orderNumberId) {
        Order order = new Order();

        order.setOrderNumber(Long.valueOf(orderNumberId));
        order.setStatus(Status.ORDERED);
        order.setTotalAmount(BigDecimal.valueOf(RandomUtils.nextDouble(10, 2000)));
        order.setOrderItems(createOrderItems(RandomUtils.nextInt(2, 10), order));

        return order;
    }

    private List<OrderItem> createOrderItems(int numberOfItems, Order order) {
        return IntStream.rangeClosed(0, numberOfItems)
            .boxed()
            .map(i -> createOrderItem(order))
            .collect(Collectors.toList());
    }

    private OrderItem createOrderItem(Order order) {
        OrderItem orderItem = new OrderItem();

        Long quantity = RandomUtils.nextLong(1, 10);
        BigDecimal priceItem = BigDecimal.valueOf(RandomUtils.nextDouble(10, 2000));
        BigDecimal totalAmount = priceItem.multiply(BigDecimal.valueOf(quantity));

        orderItem.setOrder(order);
        orderItem.setProductId(RandomUtils.nextLong(1, 100));
        orderItem.setQuantity(quantity);
        orderItem.setPriceItem(priceItem);
        orderItem.setTotalAmount(totalAmount);

        return orderItem;
    }
}