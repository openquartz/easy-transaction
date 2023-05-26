package com.openquartz.easytransaction.example.tcc.account.controller.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * The type Account nested dto.
 *
 * @author svnee
 */
@Data
public class AccountNestedDTO implements Serializable {

    private static final long serialVersionUID = 7223470850578998427L;
    
    /**
     * 用户id.
     */
    private String userId;

    /**
     * 扣款金额.
     */
    private BigDecimal amount;

    /**
     * productId.
     */
    private String productId;

    /**
     * count.
     */
    private Integer count;
}
