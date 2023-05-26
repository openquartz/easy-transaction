package com.openquartz.easytransaction.example.tcc.order.service.impl;

import com.openquartz.easytransaction.example.tcc.order.common.IdWorkerUtils;
import com.openquartz.easytransaction.example.tcc.order.mapper.OrderMapper;
import com.openquartz.easytransaction.example.tcc.order.mapper.entity.Order;
import com.openquartz.easytransaction.example.tcc.order.mapper.entity.OrderStatusEnum;
import com.openquartz.easytransaction.example.tcc.order.service.OrderService;
import com.openquartz.easytransaction.example.tcc.order.service.PaymentService;
import java.math.BigDecimal;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * @author svnee
 */
@Service("orderService")
public class OrderServiceImpl implements OrderService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final OrderMapper orderMapper;

    private final PaymentService paymentService;

    @Autowired(required = false)
    public OrderServiceImpl(OrderMapper orderMapper, PaymentService paymentService) {
        this.orderMapper = orderMapper;
        this.paymentService = paymentService;
    }

    @Override
    public String orderPay(Integer count, BigDecimal amount) {
        Order order = saveOrder(count, amount);
        long start = System.currentTimeMillis();
        paymentService.makePayment(order);
        System.out.println("分布式事务耗时：" + (System.currentTimeMillis() - start));
        return "success";
    }
    
    @Override
    public String orderPayWithNested(Integer count, BigDecimal amount) {
        Order order = saveOrder(count, amount);
        return paymentService.makePaymentWithNested(order);
    }

    private Order saveOrder(Integer count, BigDecimal amount) {
        final Order order = buildOrder(count, amount);
        orderMapper.save(order);
        return order;
    }
    
    private Order buildOrder(Integer count, BigDecimal amount) {
        LOGGER.debug("构建订单对象");
        Order order = new Order();
        order.setCreateTime(new Date());
        order.setNumber(String.valueOf(IdWorkerUtils.getInstance().createUUID()));
        //demo中的表里只有商品id为 1的数据
        order.setProductId("1");
        order.setStatus(OrderStatusEnum.NOT_PAY.getCode());
        order.setTotalAmount(amount);
        order.setCount(count);
        //demo中 表里面存的用户id为10000
        order.setUserId("10000");
        return order;
    }
}
