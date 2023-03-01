package pl.marcin.ordermanagerapi.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.marcin.ordermanagerapi.dto.CreateOrderDto;
import pl.marcin.ordermanagerapi.dto.CreateOrderItemDto;
import pl.marcin.ordermanagerapi.dto.OrderItemSummaryDto;
import pl.marcin.ordermanagerapi.dto.OrderSummaryDto;
import pl.marcin.ordermanagerapi.dto.StockDto;
import pl.marcin.ordermanagerapi.entity.Order;
import pl.marcin.ordermanagerapi.entity.OrderItem;
import pl.marcin.ordermanagerapi.entity.Status;
import pl.marcin.ordermanagerapi.mapper.OrderMapper;
import pl.marcin.ordermanagerapi.repository.OrderItemRepository;
import pl.marcin.ordermanagerapi.repository.OrderRepository;
import pl.marcin.ordermanagerapi.stock.services.StockInvoker;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    // TODO: Error missing config annotation
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
        Order order = new Order();
        // TODO: why atomicLong? - Change to a DB Sequence
        order.setOrderNumber(orderNumber.incrementAndGet());
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

        orderMapper.orderToOrderSummaryDto(orderRepository.save(order));
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

    /*
     * ------------------------------------------------------------------------- Test  -------------------------------------------------------------
     */
    @Transactional(readOnly = true)
    public List<OrderSummaryDto> getAllOrders_NPlusOne() {
        return orderRepository.findAll()
            .stream()
            .map(orderMapper::orderToOrderSummaryDto)
//            .map(this::enrichWithItemsSummaryDto)
            .collect(toList());
    }

    @Transactional(readOnly = true)
    public List<OrderSummaryDto> getAllOrders_NPlusOneSolutionOne() {
        return orderRepository.findAllWithOrderItemsEager()
            .stream()
            .map(orderMapper::orderToOrderSummaryDto)
            .collect(toList());
    }

    @Transactional(readOnly = true)
    public OrderItemSummaryDto getOneItemWithFindAll() {
        return orderItemRepository.findAll()
            .stream()
            .findFirst()
//            .map(orderMapper::orderItemToOrderItemSummaryDtoWithOrderNumber)
            .map(orderMapper::orderItemToOrderItemSummaryDto)
            .orElse(null);
    }

    @Transactional(readOnly = true)
    public OrderItemSummaryDto getOneItem(Long itemId) {
        return orderItemRepository.findByIdWithFetchOrder(itemId)
//            .map(orderMapper::orderItemToOrderItemSummaryDtoWithOrderNumber)
            .map(orderMapper::orderItemToOrderItemSummaryDto)
            .orElse(null);
    }

    @Transactional(readOnly = true)
    public OrderItem getAnyItem() {
        return orderItemRepository.findAll().stream().findFirst().orElse(null);
    }

    @Transactional(readOnly = true)
    public List<OrderSummaryDto> getAllOrders_NPlusOneSolutionTwo() {
        final Map<Long, List<OrderItemSummaryDto>> orderItems = orderItemRepository.findAll()
            .stream()
            .collect(groupingBy(OrderItem::getOrderId, mapping(orderMapper::orderItemToOrderItemSummaryDto, toList())));

        return orderRepository.findAll()
            .stream()
            .map(order -> parseOrderSummary(order, orderItems))
            .collect(toList());
    }

    private OrderSummaryDto parseOrderSummary(Order order, Map<Long, List<OrderItemSummaryDto>> orderItems) {
        OrderSummaryDto orderSummaryDto = orderMapper.orderToOrderSummaryDtoWithoutOrderItem(order);
        orderSummaryDto.setOrderItems(orderItems.get(orderSummaryDto.getOrderid()));
        return orderSummaryDto;
    }

    private OrderSummaryDto enrichWithItemsSummaryDto(OrderSummaryDto orderSummaryDto) {

        List<OrderItemSummaryDto> orderItems = orderItemRepository.findByOrderId(orderSummaryDto.getOrderid())
            .stream()
            .map(orderMapper::orderItemToOrderItemSummaryDto)
            .toList();

        orderSummaryDto.setOrderItems(orderItems);

        return orderSummaryDto;
    }

    /*
     * ------------------------------------------------------------------------- Test  -------------------------------------------------------------
     */
}
