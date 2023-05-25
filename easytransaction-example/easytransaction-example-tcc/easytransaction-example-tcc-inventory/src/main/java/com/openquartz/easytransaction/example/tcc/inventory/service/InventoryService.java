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
     * Test decrease boolean.
     *
     * @param inventoryDTO the inventory dto
     * @return the boolean
     */
    Boolean testDecrease(InventoryDTO inventoryDTO);
    
    /**
     * 获取商品库存信息.
     *
     * @param productId 商品id
     * @return InventoryDO inventory do
     */
    InventoryDO findByProductId(String productId);
    
    /**
     * mock 库存扣减try阶段异常.
     *
     * @param inventoryDTO dto
     * @return true boolean
     */
    Boolean mockWithTryException(InventoryDTO inventoryDTO);
    
    /**
     * mock 库存扣减try阶段超时.
     *
     * @param inventoryDTO dto
     * @return true boolean
     */
    Boolean mockWithTryTimeout(InventoryDTO inventoryDTO);
}
