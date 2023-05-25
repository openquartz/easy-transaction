package com.openquartz.easytransaction.example.tcc.order.client.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * The type Account dto.
 *
 * @author svnee
 */
@Data
public class AccountDTO implements Serializable {


    /**
     * 用户id
     */
    private String userId;

    /**
     * 扣款金额
     */
    private BigDecimal amount;

}
