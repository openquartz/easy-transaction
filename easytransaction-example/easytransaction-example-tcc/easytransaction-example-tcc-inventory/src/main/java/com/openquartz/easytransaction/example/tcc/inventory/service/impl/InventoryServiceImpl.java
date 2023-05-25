package com.openquartz.easytransaction.example.tcc.inventory.service.impl;

import com.openquartz.easytransaction.core.annotation.Tcc;
import com.openquartz.easytransaction.example.tcc.inventory.controller.entity.InventoryDTO;
import com.openquartz.easytransaction.example.tcc.inventory.mapper.InventoryMapper;
import com.openquartz.easytransaction.example.tcc.inventory.service.InventoryService;
import com.openquartz.easytransaction.example.tcc.inventory.service.entity.InventoryDO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * InventoryServiceImpl.
 *
 * @author svnee
 */
@Service("inventoryService")
public class InventoryServiceImpl implements InventoryService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(InventoryServiceImpl.class);

    private final InventoryMapper inventoryMapper;

    @Autowired(required = false)
    public InventoryServiceImpl(InventoryMapper inventoryMapper) {
        this.inventoryMapper = inventoryMapper;
    }

    /**
     * 扣减库存操作.
     * 这一个tcc接口
     *
     * @param inventoryDTO 库存DTO对象
     * @return true
     */
    @Override
    @Tcc(confirmMethod = "confirmMethod", cancelMethod = "cancelMethod")
    public Boolean decrease(InventoryDTO inventoryDTO) {
        LOGGER.info("==========try扣减库存decrease===========");
        inventoryMapper.decrease(inventoryDTO);
        return true;
    }
    
    @Override
    public Boolean testDecrease(InventoryDTO inventoryDTO) {
        inventoryMapper.testDecrease(inventoryDTO);
        return true;
    }
    
    /**
     * 获取商品库存信息.
     *
     * @param productId 商品id
     * @return InventoryDO
     */
    @Override
    public InventoryDO findByProductId(String productId) {
        return inventoryMapper.findByProductId(productId);
    }

    @Override
    @Tcc(confirmMethod = "confirmMethod", cancelMethod = "cancelMethod")
    @Transactional
    public Boolean mockWithTryException(InventoryDTO inventoryDTO) {
        throw new RuntimeException("库存扣减异常！");
    }

    @Override
    @Tcc(confirmMethod = "confirmMethod", cancelMethod = "cancelMethod")
    @Transactional(rollbackFor = Exception.class)
    public Boolean mockWithTryTimeout(InventoryDTO inventoryDTO) {
        try {
            //模拟延迟 当前线程暂停10秒
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        LOGGER.info("==========springcloud调用扣减库存mockWithTryTimeout===========");
        final int decrease = inventoryMapper.decrease(inventoryDTO);
        if (decrease != 1) {
            throw new RuntimeException("库存不足");
        }
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean confirmMethodTimeout(InventoryDTO inventoryDTO) {
        try {
            //模拟延迟 当前线程暂停11秒
            Thread.sleep(11000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        LOGGER.info("==========Springcloud调用扣减库存确认方法===========");
        inventoryMapper.decrease(inventoryDTO);
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean confirmMethodException(InventoryDTO inventoryDTO) {
        LOGGER.info("==========Springcloud调用扣减库存确认方法===========");
        final int decrease = inventoryMapper.decrease(inventoryDTO);
        if (decrease != 1) {
            throw new RuntimeException("库存不足");
        }
        return true;
        // throw new TccRuntimeException("库存扣减确认异常！");
    }
    
    public Boolean confirmMethod(InventoryDTO inventoryDTO) {
        LOGGER.info("==========confirmMethod库存确认方法===========");
        return inventoryMapper.confirm(inventoryDTO) > 0;
    }

    public Boolean cancelMethod(InventoryDTO inventoryDTO) {
        LOGGER.info("==========cancelMethod库存取消方法===========");
        return inventoryMapper.cancel(inventoryDTO) > 0;
    }
}
