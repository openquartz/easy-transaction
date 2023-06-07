CREATE TABLE `et_transaction_certificate_entity`
(
    `id`                 bigint       NOT NULL AUTO_INCREMENT COMMENT 'id',
    `transaction_id`     varchar(50)  NOT NULL DEFAULT '' COMMENT '事务ID',
    `certificate_status` tinyint      NOT NULL DEFAULT '0' COMMENT '凭证状态',
    `created_time`       timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `finished_time`      datetime              DEFAULT NULL COMMENT '完成时间',
    `updated_time`       timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
    `confirm_method`     varchar(256) NOT NULL DEFAULT '' COMMENT '取消 method',
    `cancel_method`      varchar(256) NOT NULL DEFAULT '' COMMENT '取消 method',
    `retry_count`        int          NOT NULL DEFAULT '0' COMMENT '重试数',
    `version`            int          NOT NULL DEFAULT '0' COMMENT '数据版本',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_transaction_id` (`transaction_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='事务凭证';