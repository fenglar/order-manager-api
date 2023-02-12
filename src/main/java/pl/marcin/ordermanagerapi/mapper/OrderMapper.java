package pl.marcin.ordermanagerapi.mapper;

import org.mapstruct.Mapper;
import pl.marcin.ordermanagerapi.dto.OrderItemSummaryDto;
import pl.marcin.ordermanagerapi.entity.Order;
import pl.marcin.ordermanagerapi.entity.OrderItem;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    OrderItemSummaryDto orderItemToOrderItemSummaryDto(OrderItem orderItem);
    OrderItem orderItemSummaryDtoToOrderItem(OrderItemSummaryDto orderItemSummaryDto);
    List<OrderItemSummaryDto> orderItemListToOrderItemSummaryDtoList(List<OrderItem> orderItem);
    List<OrderItem> orderItemSummaryDtoListToOrderItemList(List<OrderItemSummaryDto> orderItemSummaryDto);

}
