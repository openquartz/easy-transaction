package com.openquartz.easytransaction.repository.api.model;

/**
 * 凭证状态
 *
 * @author svnee
 */
public enum CertificateStatusEnum {

    /**
     * 初始化
     */
    INIT(0, "初始化"),

    /**
     * Success
     */
    TRY_SUCCESS(10, "Try Success"),

    /**
     * cancel
     */
    CANCEL(20, "Cancel"),

    /**
     * confirm
     */
    CONFIRM(30,"Confirm"),

    /**
     * 完成
     */
    FINISHED(90, "成功"),

    ;
    private final Integer code;
    private final String desc;

    CertificateStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
