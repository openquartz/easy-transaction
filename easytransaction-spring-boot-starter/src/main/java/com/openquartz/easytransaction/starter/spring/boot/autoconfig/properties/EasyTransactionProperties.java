package com.openquartz.easytransaction.starter.spring.boot.autoconfig.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = EasyTransactionProperties.PREFIX)
public class EasyTransactionProperties {

    public static final String PREFIX = "easytransaction";

    /**
     * 切面顺序
     */
    private Integer transactionAdvisorOrder = Integer.MAX_VALUE;

    /**
     * 最大事务调用超时时间
     */
    private Long maxTransactionTimeout = 300000L;

    /**
     * datasource properties
     */
    private EasyTransactionDataSourceProperties dataSource = new EasyTransactionDataSourceProperties();

    /**
     * compensate properties
     */
    private EasyTransactionCompensateProperties compensate = new EasyTransactionCompensateProperties();

    public static class EasyTransactionDataSourceProperties {

        /**
         * type
         */
        private String type;

        /**
         * jdbcUrl
         */
        private String url;

        /**
         * driver-class-name
         */
        private String driverClassName;

        /**
         * username
         */
        private String username;

        /**
         * password
         */
        private String password;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getDriverClassName() {
            return driverClassName;
        }

        public void setDriverClassName(String driverClassName) {
            this.driverClassName = driverClassName;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        @Override
        public String toString() {
            return "EasyTransactionDataSourceProperties{" +
                "type='" + type + '\'' +
                ", url='" + url + '\'' +
                ", driverClassName='" + driverClassName + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
        }
    }

    public static class EasyTransactionCompensateProperties {

        /**
         * 补偿次数
         */
        private Integer retryCount = 5;

        /**
         * back off hours
         */
        private Integer backoffHours = 1;

        /**
         *  compensate offset
         */
        private Integer offset = 500;

        /**
         * compensate init delay
         */
        private Integer initDelay = 30;

        /**
         * compensate recovery delay
         */
        private Integer recoveryDelay = 60;

        public Integer getRetryCount() {
            return retryCount;
        }

        public void setRetryCount(Integer retryCount) {
            this.retryCount = retryCount;
        }

        public Integer getBackoffHours() {
            return backoffHours;
        }

        public void setBackoffHours(Integer backoffHours) {
            this.backoffHours = backoffHours;
        }

        public Integer getOffset() {
            return offset;
        }

        public void setOffset(Integer offset) {
            this.offset = offset;
        }

        public Integer getInitDelay() {
            return initDelay;
        }

        public void setInitDelay(Integer initDelay) {
            this.initDelay = initDelay;
        }

        public Integer getRecoveryDelay() {
            return recoveryDelay;
        }

        public void setRecoveryDelay(Integer recoveryDelay) {
            this.recoveryDelay = recoveryDelay;
        }

        @Override
        public String toString() {
            return "EasyTransactionCompensateProperties{" +
                "retryCount=" + retryCount +
                ", backoffHours=" + backoffHours +
                ", offset=" + offset +
                ", initDelay=" + initDelay +
                ", recoveryDelay=" + recoveryDelay +
                '}';
        }
    }
}
