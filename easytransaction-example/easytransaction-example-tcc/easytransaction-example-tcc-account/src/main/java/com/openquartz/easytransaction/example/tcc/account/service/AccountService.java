package com.openquartz.easytransaction.example.tcc.account.service;

import com.openquartz.easytransaction.example.tcc.account.controller.entity.AccountDTO;
import com.openquartz.easytransaction.example.tcc.account.controller.entity.AccountNestedDTO;
import com.openquartz.easytransaction.example.tcc.account.service.entity.AccountDO;

/**
 * AccountService.
 *
 * @author svnee
 */
public interface AccountService {
    
    /**
     * 扣款支付.
     *
     * @param accountDTO 参数dto
     * @return true boolean
     */
    boolean payment(AccountDTO accountDTO);

    /**
     * confirm method
     * @param accountDTO account
     * @return true boolean
     */
    boolean confirm(final AccountDTO accountDTO);

    /**
     * Cancel boolean.
     *
     * @param accountDTO the account dto
     */
    boolean cancel(final AccountDTO accountDTO);
    
    /**
     * Payment with nested boolean.
     *
     * @param nestedDTO the nested dto
     * @return the boolean
     */
    boolean paymentWithNested(AccountNestedDTO nestedDTO);

    /**
     * cancel nest
     */
    boolean confirmNested(final AccountNestedDTO accountNestedDTO);

    /**
     * cancelNested
     */
    boolean cancelNested(AccountNestedDTO accountNestedDTO);
    
    /**
     * 获取用户账户信息.
     *
     * @param userId 用户id
     * @return AccountDO account do
     */
    AccountDO findByUserId(String userId);
}
