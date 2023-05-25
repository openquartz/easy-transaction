package com.openquartz.easytransaction.example.tcc.account.service.impl;

import com.openquartz.easytransaction.core.annotation.Tcc;
import com.openquartz.easytransaction.example.tcc.account.service.InLineService;
import org.springframework.stereotype.Component;

/**
 * The type In line service.
 *
 * @author svnee
 */
@Component
public class InLineServiceImpl implements InLineService {

    @Override
    @Tcc(confirmMethod = "confirm", cancelMethod = "cancel")
    public void test() {
        System.out.println("执行inline try......");
    }

    /**
     * Confrim.
     */
    public void confirm() {
        System.out.println("执行inline confirm......");
    }

    /**
     * Cancel.
     */
    public void cancel() {
        System.out.println("执行inline cancel......");
    }
}
