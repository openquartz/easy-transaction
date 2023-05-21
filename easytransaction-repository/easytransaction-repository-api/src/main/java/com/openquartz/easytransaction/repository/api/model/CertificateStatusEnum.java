package com.openquartz.easytransaction.repository.api.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * 凭证状态
 *
 * @author svnee
 */
public enum CertificateStatusEnum {

    /**
     * 初始化
     */
    INIT(0, "Initial"),

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
    CONFIRM(30, "Confirm"),

    /**
     * 完成
     */
    FINISHED(90, "Finish"),

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

    public static CertificateStatusEnum of(Integer code){
        return Stream.of(values())
            .filter(e->e.code.equals(code))
            .findAny()
            .orElse(null);
    }

    private static final List<CertificateStatusEnum> PROCESSING_CERTIFICATE_STATUS_LIST;

    static {
        PROCESSING_CERTIFICATE_STATUS_LIST =  new ArrayList<>();
        PROCESSING_CERTIFICATE_STATUS_LIST.add(CertificateStatusEnum.INIT);
        PROCESSING_CERTIFICATE_STATUS_LIST.add(CertificateStatusEnum.TRY_SUCCESS);
        PROCESSING_CERTIFICATE_STATUS_LIST.add(CertificateStatusEnum.CONFIRM);
        PROCESSING_CERTIFICATE_STATUS_LIST.add(CertificateStatusEnum.CANCEL);
    }

    public static List<CertificateStatusEnum> getProcessingCertificateStatusList() {
        return PROCESSING_CERTIFICATE_STATUS_LIST;
    }
}
