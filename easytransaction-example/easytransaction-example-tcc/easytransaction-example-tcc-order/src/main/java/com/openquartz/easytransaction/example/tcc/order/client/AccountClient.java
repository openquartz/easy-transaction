/*
 * Copyright 2017-2021 Dromara.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
    
    /**
     * Test payment boolean.
     *
     * @param accountDO the account do
     * @return the boolean
     */
    @RequestMapping("/account-service/account/testPayment")
    Boolean testPayment(@RequestBody AccountDTO accountDO);
    
    /**
     * 获取用户账户信息.
     *
     * @param userId 用户id
     * @return AccountDO big decimal
     */
    @RequestMapping("/account-service/account/findByUserId")
    BigDecimal findByUserId(@RequestParam("userId") String userId);
    
    /**
     * Mock with try exception boolean.
     *
     * @param accountDO the account do
     * @return the boolean
     */
    @RequestMapping("/account-service/account/mockWithTryException")
    Boolean mockWithTryException(@RequestBody AccountDTO accountDO);
    
    /**
     * Mock with try timeout boolean.
     *
     * @param accountDO the account do
     * @return the boolean
     */
    @RequestMapping("/account-service/account/mockWithTryTimeout")
    Boolean mockWithTryTimeout(@RequestBody AccountDTO accountDO);
    
    /**
     * Payment with nested boolean.
     *
     * @param nestedDTO the nested dto
     * @return the boolean
     */
    @RequestMapping("/account-service/account/paymentWithNested")
    Boolean paymentWithNested(@RequestBody AccountNestedDTO nestedDTO);
    
    /**
     * Payment with nested exception boolean.
     *
     * @param nestedDTO the nested dto
     * @return the boolean
     */
    @RequestMapping("/account-service/account/paymentWithNestedException")
    Boolean paymentWithNestedException(@RequestBody AccountNestedDTO nestedDTO);
}
