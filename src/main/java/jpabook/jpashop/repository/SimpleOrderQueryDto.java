package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SimpleOrderQueryDto{
    private Long orderId;
    private String name;
    private LocalDateTime dateTime;
    private OrderStatus orderStatus;
    private Address address;

    public SimpleOrderQueryDto(Long orderId, String name, LocalDateTime
            dateTime, OrderStatus orderStatus, Address address) {
        this.orderId = orderId;
        this.name = name;
        this.dateTime = dateTime;
        this.orderStatus = orderStatus;
        this.address = address;
    }
}