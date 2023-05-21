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
     * 最大重试次数
     */
    private Integer maxCompensateRetryCount = 5;

    /**
     * datasource
     */
    private EasyTransactionDataSourceProperties dataSource = new EasyTransactionDataSourceProperties();

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
}
