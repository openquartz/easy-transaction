package com.openquartz.easytransaction.example.tcc.account.client;

import com.openquartz.easytransaction.example.tcc.account.client.entity.InventoryDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * The interface Inventory client.
 *
 * @author svnee
 */
@FeignClient(value = "inventory-service")
public interface InventoryClient {
    
    /**
     * 库存扣减.
     *
     * @param inventoryDTO 实体对象
     * @return true 成功
     */
    @RequestMapping("/inventory-service/inventory/decrease")
    Boolean decrease(@RequestBody InventoryDTO inventoryDTO);

    /**
     * confirm method
     *
     * @param inventoryDTO 实体对象
     * @return true
     */
    @RequestMapping("/inventory-service/inventory/confirm")
    Boolean confirm(@RequestBody InventoryDTO inventoryDTO);

    /**
     * confirm method
     *
     * @param inventoryDTO 实体对象
     * @return true
     */
    @RequestMapping("/inventory-service/inventory/cancel")
    Boolean cancel(@RequestBody InventoryDTO inventoryDTO);
}
