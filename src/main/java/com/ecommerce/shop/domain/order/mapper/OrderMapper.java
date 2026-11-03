package com.ecommerce.shop.domain.order.mapper;

import com.ecommerce.shop.domain.order.dto.Order;
import com.ecommerce.shop.domain.order.dto.OrderItem;
import com.ecommerce.shop.domain.order.entity.OrderEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderMapper {
    public Order convertToDomain(OrderEntity orderEntity){
        List<OrderItem> items = orderEntity
                .getItems()
                .stream()
                .map(item -> new OrderItem(
                        item.getId(),
                        item.getProductId(),
                        item.getProductName(),
                        item.getCurrency(),
                        item.getUnitPrice()
                        )
                ).toList();

        return new Order(
                orderEntity.getId(),
                orderEntity.getUser().getId(),
                orderEntity.getTotalPrice(),
                orderEntity.getStatus(),
                orderEntity.getCurrency(),
                items
        );
    }
}
