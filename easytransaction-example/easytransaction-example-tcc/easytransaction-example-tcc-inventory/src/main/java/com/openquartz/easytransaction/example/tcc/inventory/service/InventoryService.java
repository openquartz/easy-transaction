package com.openquartz.easytransaction.example.tcc.inventory.service;

import com.openquartz.easytransaction.example.tcc.inventory.controller.entity.InventoryDTO;
import com.openquartz.easytransaction.example.tcc.inventory.service.entity.InventoryDO;

/**
 * The interface Inventory service.
 *
 * @author svnee
 */
public interface InventoryService {
    
    /**
     * 扣减库存操作.
     * 这一个tcc接口
     *
     * @param inventoryDTO 库存DTO对象
     * @return true boolean
     */
    Boolean decrease(InventoryDTO inventoryDTO);

    /**
     * tcc confirm method
     * @param inventoryDTO inventory
     * @return result
     */
    Boolean confirm(InventoryDTO inventoryDTO);

    /**
     * cancel method
     * @param inventoryDTO inventoryDTO
     * @return boolean
     */
    Boolean cancel(InventoryDTO inventoryDTO);

    /**
     * 获取商品库存信息.
     *
     * @param productId 商品id
     * @return InventoryDO inventory do
     */
    InventoryDO findByProductId(String productId);

}
