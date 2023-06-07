# EasyTransaction
轻量级最大努力重试型TCC 分布式柔性事务解决方案


## 使用指南
### 引入依赖
```xml
      <dependency>
        <groupId>com.openquartz</groupId>
        <artifactId>easytransaction-spring-boot-starter</artifactId>
        <version>${lastVersion}</version>
      </dependency>
```

使用注解在try 方法上,并在相同的类下提供confirm/cancel方法
例如：
```java
  @Override
    @Tcc(confirmMethod = "confirmOrderStatus", cancelMethod = "cancelOrderStatus")
    @Transactional(rollbackFor = Exception.class)
    public void makePayment(Order order) {
        updateOrderStatus(order, OrderStatusEnum.PAYING);
        accountClient.payment(buildAccountDTO(order));
        inventoryClient.decrease(buildInventoryDTO(order));
    }


    @Override
    @Tcc(confirmMethod = "confirmOrderStatusWithNested", cancelMethod = "cancelOrderStatusWithNested")
    @Transactional(rollbackFor = Exception.class)
    public String makePaymentWithNested(Order order) {
        updateOrderStatus(order, OrderStatusEnum.PAYING);
        final BigDecimal balance = accountClient.findByUserId(order.getUserId());
        if (balance.compareTo(order.getTotalAmount()) <= 0) {
            throw new RuntimeException("余额不足！");
        }
        accountClient.paymentWithNested(buildAccountNestedDTO(order));
        return "success";
    }

    @Transactional(rollbackFor = Exception.class)
    public void confirmOrderStatusWithNested(Order order) {
        updateOrderStatus(order, OrderStatusEnum.PAY_SUCCESS);
        accountClient.confirmNested(buildAccountNestedDTO(order));
        inventoryClient.confirm(buildInventoryDTO(order));
        LOGGER.info("=========进行订单confirmNested操作完成================");
    }

    @Transactional(rollbackFor = Exception.class)
    public void cancelOrderStatusWithNested(Order order) {
        updateOrderStatus(order, OrderStatusEnum.PAY_FAIL);
        accountClient.cancelNested(buildAccountNestedDTO(order));
        inventoryClient.cancel(buildInventoryDTO(order));
        LOGGER.info("=========进行订单cancelNested操作完成================");
    }

    @Transactional(rollbackFor = Exception.class)
    public void confirmOrderStatus(Order order) {
        updateOrderStatus(order, OrderStatusEnum.PAY_SUCCESS);
        accountClient.confirm(buildAccountDTO(order));
        inventoryClient.confirm(buildInventoryDTO(order));
        LOGGER.info("=========进行订单confirm操作完成================");
    }

    @Transactional(rollbackFor = Exception.class)
    public void cancelOrderStatus(Order order) {
        updateOrderStatus(order, OrderStatusEnum.PAY_FAIL);
        accountClient.cancel(buildAccountDTO(order));
        inventoryClient.cancel(buildInventoryDTO(order));
        LOGGER.info("=========进行订单cancel操作完成================");
    }
```
同时注解支持设置事务超时时间设置以及重试设置
```java
/**
 * @author svnee
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Tcc {

    /**
     * 提交method
     */
    String confirmMethod() default "";

    /**
     * 取消method
     */
    String cancelMethod() default "";

    /**
     * 超时时间,单位：毫秒 默认不超时
     */
    long timeout() default Long.MAX_VALUE;

    /**
     * Try method 重试次数。默认 不重试
     */
    int retryCount() default 0;

    /**
     * 重试时间间隔 单位：毫秒
     */
    long retryInterval() default 0;
}
```
