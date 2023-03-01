package pl.marcin.ordermanagerapi.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.marcin.ordermanagerapi.dto.OrderItemSummaryDto;
import pl.marcin.ordermanagerapi.dto.OrderSummaryDto;
import pl.marcin.ordermanagerapi.entity.Order;
import pl.marcin.ordermanagerapi.entity.OrderItem;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {


//    @Mapping(target = "orderNumber", source = "order.orderNumber")
//    OrderItemSummaryDto orderItemToOrderItemSummaryDtoWithOrderNumber(OrderItem orderItem);

    @Mapping(target = "orderNumber", ignore = true)
    OrderItemSummaryDto orderItemToOrderItemSummaryDto(OrderItem orderItem);

    OrderItem orderItemSummaryDtoToOrderItem(OrderItemSummaryDto orderItemSummaryDto);
    List<OrderItemSummaryDto> orderItemListToOrderItemSummaryDtoList(List<OrderItem> orderItem);
    List<OrderItem> orderItemSummaryDtoListToOrderItemList(List<OrderItemSummaryDto> orderItemSummaryDto);
    OrderSummaryDto orderToOrderSummaryDto(Order order);

    @Mapping(target = "orderItems", ignore = true)
    OrderSummaryDto orderToOrderSummaryDtoWithoutOrderItem(Order order);

}
