package com.openquartz.easytransaction.example.tcc.order.service;


import com.openquartz.easytransaction.example.tcc.order.mapper.entity.Order;

/**
 * PaymentService.
 *
 * @author svnee
 */
public interface PaymentService {
    
    /**
     * 订单支付.
     *
     * @param order 订单实体
     */
    void makePayment(Order order);
    
    /**
     * Make payment with nested.
     *
     * @param order the order
     * @return the string
     */
    String makePaymentWithNested(Order order);
}
