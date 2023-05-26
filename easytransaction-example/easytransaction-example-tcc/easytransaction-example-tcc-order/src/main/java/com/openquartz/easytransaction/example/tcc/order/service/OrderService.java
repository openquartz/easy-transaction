package com.openquartz.easytransaction.example.tcc.order.service;


import com.openquartz.easytransaction.example.tcc.order.mapper.entity.Order;
import java.math.BigDecimal;

/**
 * OrderService.
 *
 * @author svnee
 */
public interface OrderService {
    
    /**
     * 创建订单并且进行扣除账户余额支付，并进行库存扣减操作.
     *
     * @param count  购买数量
     * @param amount 支付金额
     * @return string string
     */
    String orderPay(Integer count, BigDecimal amount);
    
    /**
     * Order pay with nested string.
     *
     * @param count  the count
     * @param amount the amount
     * @return the string
     */
    String orderPayWithNested(Integer count, BigDecimal amount);
}
