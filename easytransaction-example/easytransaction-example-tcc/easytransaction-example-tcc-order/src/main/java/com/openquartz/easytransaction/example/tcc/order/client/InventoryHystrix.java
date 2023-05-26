package com.openquartz.easytransaction.example.tcc.order.client;

import com.openquartz.easytransaction.example.tcc.order.client.entity.InventoryDTO;
import org.springframework.stereotype.Component;

/**
 * @author svnee
 */
@Component
public class InventoryHystrix implements InventoryClient {

    @Override
    public Boolean decrease(InventoryDTO inventoryDTO) {
        System.out.println("inventory hystrix.......");
        return false;
    }

    @Override
    public Boolean confirm(InventoryDTO inventoryDTO) {
        System.out.println("inventory hystrix.......");
        return false;
    }

    @Override
    public Boolean cancel(InventoryDTO inventoryDTO) {
        System.out.println("inventory hystrix.......");
        return false;
    }
    
    @Override
    public Integer findByProductId(String productId) {
        return 0;
    }
}
