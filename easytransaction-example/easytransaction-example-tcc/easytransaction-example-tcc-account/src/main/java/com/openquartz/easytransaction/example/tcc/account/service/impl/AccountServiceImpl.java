package com.openquartz.easytransaction.example.tcc.account.service.impl;

import com.openquartz.easytransaction.example.tcc.account.client.InventoryClient;
import com.openquartz.easytransaction.example.tcc.account.client.entity.InventoryDTO;
import com.openquartz.easytransaction.example.tcc.account.controller.entity.AccountDTO;
import com.openquartz.easytransaction.example.tcc.account.controller.entity.AccountNestedDTO;
import com.openquartz.easytransaction.example.tcc.account.mapper.AccountMapper;
import com.openquartz.easytransaction.example.tcc.account.service.AccountService;
import com.openquartz.easytransaction.example.tcc.account.service.entity.AccountDO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * The type Account service.
 *
 * @author svnee
 */
@Service("accountService")
public class AccountServiceImpl implements AccountService {

    /**
     * logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AccountServiceImpl.class);

    private final AccountMapper accountMapper;

    private final InventoryClient inventoryClient;

    /**
     * Instantiates a new Account service.
     *
     * @param accountMapper the account mapper
     */
    @Autowired(required = false)
    public AccountServiceImpl(final AccountMapper accountMapper, final InventoryClient inventoryClient) {
        this.accountMapper = accountMapper;
        this.inventoryClient = inventoryClient;
    }

    @Override
    public boolean payment(final AccountDTO accountDTO) {
        LOGGER.info("============执行try付款接口===============");
        accountMapper.update(accountDTO);
        return Boolean.TRUE;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean paymentWithNested(AccountNestedDTO nestedDTO) {
        accountMapper.update(buildAccountDTO(nestedDTO));
        inventoryClient.decrease(buildInventoryDTO(nestedDTO));
        return Boolean.TRUE;
    }

    @Override
    public AccountDO findByUserId(final String userId) {
        return accountMapper.findByUserId(userId);
    }

    /**
     * Confirm boolean.
     *
     * @param accountDTO the account dto
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean confirm(final AccountDTO accountDTO) {
        LOGGER.info("============执行confirm 付款接口===============");
        return accountMapper.confirm(accountDTO) > 0;
    }

    /**
     * Cancel boolean.
     *
     * @param accountDTO the account dto
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean cancel(final AccountDTO accountDTO) {
        LOGGER.info("============执行cancel 付款接口===============");
        return accountMapper.cancel(accountDTO) > 0;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean confirmNested(final AccountNestedDTO accountNestedDTO) {
        LOGGER.info("============confirmNested确认付款接口===============");
        boolean confirm = accountMapper.confirm(buildAccountDTO(accountNestedDTO)) > 0;
        if (confirm) {
            return inventoryClient.confirm(buildInventoryDTO(accountNestedDTO));
        }
        return false;
    }

    /**
     * Cancel nested boolean.
     *
     * @param accountNestedDTO the account nested dto
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelNested(AccountNestedDTO accountNestedDTO) {
        LOGGER.info("============cancelNested 执行取消付款接口===============");
        boolean cancel = accountMapper.cancel(buildAccountDTO(accountNestedDTO)) > 0;
        if (cancel){
            return inventoryClient.cancel(buildInventoryDTO(accountNestedDTO));
        }
        return false;
    }

    private AccountDTO buildAccountDTO(AccountNestedDTO nestedDTO) {
        AccountDTO dto = new AccountDTO();
        dto.setAmount(nestedDTO.getAmount());
        dto.setUserId(nestedDTO.getUserId());
        return dto;
    }

    private InventoryDTO buildInventoryDTO(AccountNestedDTO nestedDTO) {
        InventoryDTO inventoryDTO = new InventoryDTO();
        inventoryDTO.setCount(nestedDTO.getCount());
        inventoryDTO.setProductId(nestedDTO.getProductId());
        return inventoryDTO;
    }
}
