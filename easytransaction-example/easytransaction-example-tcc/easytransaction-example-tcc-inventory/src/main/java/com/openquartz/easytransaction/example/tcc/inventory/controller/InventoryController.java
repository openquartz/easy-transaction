package com.openquartz.easytransaction.example.tcc.inventory.controller;

import com.openquartz.easytransaction.example.tcc.inventory.controller.entity.InventoryDTO;
import com.openquartz.easytransaction.example.tcc.inventory.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author svnee
 */
@RestController
@RequestMapping("/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    @Autowired
    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @RequestMapping("/decrease")
    public Boolean decrease(@RequestBody InventoryDTO inventoryDTO) {
        return inventoryService.decrease(inventoryDTO);
    }
    
    @RequestMapping("/testDecrease")
    public Boolean testDecrease(@RequestBody InventoryDTO inventoryDTO) {
        return inventoryService.testDecrease(inventoryDTO);
    }

    @RequestMapping("/findByProductId")
    public Integer findByProductId(@RequestParam("productId") String productId) {
        return inventoryService.findByProductId(productId).getTotalInventory();
    }

    @RequestMapping("/mockWithTryException")
    public Boolean mockWithTryException(@RequestBody InventoryDTO inventoryDTO) {
        return inventoryService.mockWithTryException(inventoryDTO);
    }

    @RequestMapping("/mockWithTryTimeout")
    public Boolean mockWithTryTimeout(@RequestBody InventoryDTO inventoryDTO) {
        return inventoryService.mockWithTryTimeout(inventoryDTO);
    }
}
