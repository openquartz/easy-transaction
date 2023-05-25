package com.openquartz.easytransaction.example.tcc.account.client.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * The type Inventory dto.
 *
 * @author svnee
 */
@Data
public class InventoryDTO implements Serializable {

    private static final long serialVersionUID = 8229355519336565493L;

    /**
     * 商品id.
     */
    private String productId;

    /**
     * 数量.
     */
    private Integer count;
}
