package com.openquartz.easytransaction.example.tcc.order.service.impl;

import com.openquartz.easytransaction.core.annotation.Tcc;
import com.openquartz.easytransaction.example.tcc.order.client.AccountClient;
import com.openquartz.easytransaction.example.tcc.order.client.InventoryClient;
import com.openquartz.easytransaction.example.tcc.order.client.entity.AccountDTO;
import com.openquartz.easytransaction.example.tcc.order.client.entity.AccountNestedDTO;
import com.openquartz.easytransaction.example.tcc.order.client.entity.InventoryDTO;
import com.openquartz.easytransaction.example.tcc.order.mapper.OrderMapper;
import com.openquartz.easytransaction.example.tcc.order.mapper.entity.Order;
import com.openquartz.easytransaction.example.tcc.order.mapper.entity.OrderStatusEnum;
import com.openquartz.easytransaction.example.tcc.order.service.PaymentService;
import java.math.BigDecimal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * PaymentServiceImpl.
 *
 * @author svnee
 */
@Service
@SuppressWarnings("all")
public class PaymentServiceImpl implements PaymentService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentServiceImpl.class);

    private final OrderMapper orderMapper;

    private final AccountClient accountClient;

    private final InventoryClient inventoryClient;

    @Autowired(required = false)
    public PaymentServiceImpl(OrderMapper orderMapper,
                              AccountClient accountClient,
                              InventoryClient inventoryClient) {
        this.orderMapper = orderMapper;
        this.accountClient = accountClient;
        this.inventoryClient = inventoryClient;
    }

    @Override
    @Tcc(confirmMethod = "confirmOrderStatus", cancelMethod = "cancelOrderStatus")
    @Transactional(rollbackFor = Exception.class)
    public void makePayment(Order order) {
        updateOrderStatus(order, OrderStatusEnum.PAYING);
        accountClient.payment(buildAccountDTO(order));
        inventoryClient.decrease(buildInventoryDTO(order));
    }


    @Override
    @Tcc(confirmMethod = "confirmOrderStatusWithNested", cancelMethod = "cancelOrderStatusWithNested")
    @Transactional(rollbackFor = Exception.class)
    public String makePaymentWithNested(Order order) {
        updateOrderStatus(order, OrderStatusEnum.PAYING);
        final BigDecimal balance = accountClient.findByUserId(order.getUserId());
        if (balance.compareTo(order.getTotalAmount()) <= 0) {
            throw new RuntimeException("余额不足！");
        }
        accountClient.paymentWithNested(buildAccountNestedDTO(order));
        return "success";
    }

    @Transactional(rollbackFor = Exception.class)
    public void confirmOrderStatusWithNested(Order order) {
        updateOrderStatus(order, OrderStatusEnum.PAY_SUCCESS);
        accountClient.confirmNested(buildAccountNestedDTO(order));
        inventoryClient.confirm(buildInventoryDTO(order));
        LOGGER.info("=========进行订单confirmNested操作完成================");
    }

    @Transactional(rollbackFor = Exception.class)
    public void cancelOrderStatusWithNested(Order order) {
        updateOrderStatus(order, OrderStatusEnum.PAY_FAIL);
        accountClient.cancelNested(buildAccountNestedDTO(order));
        inventoryClient.cancel(buildInventoryDTO(order));
        LOGGER.info("=========进行订单cancelNested操作完成================");
    }

    @Transactional(rollbackFor = Exception.class)
    public void confirmOrderStatus(Order order) {
        updateOrderStatus(order, OrderStatusEnum.PAY_SUCCESS);
        accountClient.confirm(buildAccountDTO(order));
        inventoryClient.confirm(buildInventoryDTO(order));
        LOGGER.info("=========进行订单confirm操作完成================");
    }

    @Transactional(rollbackFor = Exception.class)
    public void cancelOrderStatus(Order order) {
        updateOrderStatus(order, OrderStatusEnum.PAY_FAIL);
        accountClient.cancel(buildAccountDTO(order));
        inventoryClient.cancel(buildInventoryDTO(order));
        LOGGER.info("=========进行订单cancel操作完成================");
    }
    
    private void updateOrderStatus(Order order, OrderStatusEnum orderStatus) {
        order.setStatus(orderStatus.getCode());
        orderMapper.update(order);
    }
    
    private AccountDTO buildAccountDTO(Order order) {
        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setAmount(order.getTotalAmount());
        accountDTO.setUserId(order.getUserId());
        return accountDTO;
    }
    
    private InventoryDTO buildInventoryDTO(Order order) {
        InventoryDTO inventoryDTO = new InventoryDTO();
        inventoryDTO.setCount(order.getCount());
        inventoryDTO.setProductId(order.getProductId());
        return inventoryDTO;
    }
    
    private AccountNestedDTO buildAccountNestedDTO(Order order) {
        AccountNestedDTO nestedDTO = new AccountNestedDTO();
        nestedDTO.setAmount(order.getTotalAmount());
        nestedDTO.setUserId(order.getUserId());
        nestedDTO.setProductId(order.getProductId());
        nestedDTO.setCount(order.getCount());
        return nestedDTO;
    }
}
