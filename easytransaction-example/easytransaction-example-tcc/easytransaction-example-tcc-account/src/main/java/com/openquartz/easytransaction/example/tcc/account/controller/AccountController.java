package com.openquartz.easytransaction.example.tcc.account.controller;

import com.openquartz.easytransaction.example.tcc.account.controller.entity.AccountDTO;
import com.openquartz.easytransaction.example.tcc.account.controller.entity.AccountNestedDTO;
import com.openquartz.easytransaction.example.tcc.account.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

/**
 * AccountController.
 * @author svnee
 */
@RestController
@RequestMapping("/account")
public class AccountController {

    private final AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @RequestMapping("/payment")
    public Boolean payment(@RequestBody AccountDTO accountDO) {
        return accountService.payment(accountDO);
    }

    @RequestMapping("/confirm")
    public Boolean confirm(@RequestBody AccountDTO accountDTO){
        return accountService.confirm(accountDTO);
    }

    @RequestMapping("/cancel")
    public Boolean cancel(@RequestBody AccountDTO accountDTO){
        return accountService.cancel(accountDTO);
    }
    
    @RequestMapping("/paymentWithNested")
    public Boolean paymentWithNested(@RequestBody AccountNestedDTO nestedDTO) {
        return accountService.paymentWithNested(nestedDTO);
    }
    
    @RequestMapping("/findByUserId")
    public BigDecimal findByUserId(@RequestParam("userId") String userId) {
        return accountService.findByUserId(userId).getBalance();
    }

    /**
     * cancel nest
     */
    @RequestMapping("/confirmNested")
    public Boolean confirmNested(@RequestBody AccountNestedDTO accountNestedDTO){
        return accountService.confirmNested(accountNestedDTO);
    }

    /**
     * cancelNested
     */
    @RequestMapping("/cancelNested")
    public Boolean cancelNested(@RequestBody AccountNestedDTO accountNestedDTO){
        return accountService.cancelNested(accountNestedDTO);
    }
}
