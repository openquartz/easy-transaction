package com.openquartz.easytransaction.repository.jdbc.model;

import java.util.Date;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

/**
 * Entity
 *
 * @author svnee
 */
@Data
@Table(name = "et_transaction_certificate_entity")
public class TransactionCertificateEntity {

    @Id
    private Long id;

    /**
     * 事务ID
     */
    private String transactionId;

    /**
     * 凭证状态
     */
    private Integer certificateStatus;

    /**
     * 创建时间
     */
    private Date createdTime;

    /**
     * 完成时间
     */
    private Date finishedTime;

    /**
     * 创建时间
     */
    private Date updatedTime;

    /**
     * 取消 method
     */
    private String confirmMethod;

    /**
     * param
     */
    private String param;

    /**
     * 取消 method
     */
    private String cancelMethod;

    /**
     * 重试数
     */
    private Integer retryCount;

    /**
     * 数据版本
     */
    private Integer version;

}
