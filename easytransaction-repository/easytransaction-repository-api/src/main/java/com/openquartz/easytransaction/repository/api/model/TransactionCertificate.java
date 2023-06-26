package com.openquartz.easytransaction.repository.api.model;

import java.lang.reflect.Method;
import lombok.Data;
import java.util.Date;

/**
 * 事务凭证
 *
 * @author svnee
 */
@Data
public class TransactionCertificate {

    /**
     * 事务ID
     */
    private String transactionId;

    /**
     * 事务组ID
     */
    private String transactionGroupId;

    /**
     * 凭证状态
     */
    private CertificateStatusEnum certificateStatus;

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
     * confirm method
     */
    private Method confirmMethod;

    /**
     * param
     */
    private Object param;

    /**
     * cancel method
     */
    private Method cancelMethod;

    /**
     * 重试数
     */
    private Integer retryCount;

    /**
     * 数据版本
     */
    private Integer version;

    public boolean isFinished() {
        return certificateStatus == CertificateStatusEnum.FINISHED;
    }
}
