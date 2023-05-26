package com.openquartz.easytransaction.example.tcc.order.client;

import com.openquartz.easytransaction.example.tcc.order.client.entity.AccountDTO;
import com.openquartz.easytransaction.example.tcc.order.client.entity.AccountNestedDTO;
import java.math.BigDecimal;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * The interface Account client.
 *
 * @author svnee
 */
@FeignClient(value = "account-service")
public interface AccountClient {

    /**
     * 用户账户付款.
     *
     * @param accountDO 实体类
     * @return true 成功
     */
    @RequestMapping("/account-service/account/payment")
    Boolean payment(@RequestBody AccountDTO accountDO);

    @RequestMapping("/account-service/account/confirm")
    Boolean confirm(@RequestBody AccountDTO accountDTO);

    @RequestMapping("/account-service/account/cancel")
    Boolean cancel(@RequestBody AccountDTO accountDTO);

    /**
     * 获取用户账户信息.
     *
     * @param userId 用户id
     * @return AccountDO big decimal
     */
    @RequestMapping("/account-service/account/findByUserId")
    BigDecimal findByUserId(@RequestParam("userId") String userId);

    /**
     * Payment with nested boolean.
     *
     * @param nestedDTO the nested dto
     * @return the boolean
     */
    @RequestMapping("/account-service/account/paymentWithNested")
    Boolean paymentWithNested(@RequestBody AccountNestedDTO nestedDTO);

    /**
     * cancel nest
     */
    @RequestMapping("/account-service/account/confirmNested")
    Boolean confirmNested(@RequestBody AccountNestedDTO accountNestedDTO);

    /**
     * cancelNested
     */
    @RequestMapping("/account-service/account/cancelNested")
    Boolean cancelNested(@RequestBody AccountNestedDTO accountNestedDTO);
}
