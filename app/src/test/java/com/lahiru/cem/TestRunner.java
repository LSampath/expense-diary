package com.lahiru.cem;

import com.lahiru.cem.ControllerTest.AccountTest;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

/**
 * Created by Lahiru on 4/3/2018.
 */

public class TestRunner {
    public static void main(String[] args) {
        Result result = JUnitCore.runClasses(AccountTest.class);

        for (Failure failure: result.getFailures()) {
            System.out.println(failure.toString());
        }

        System.out.println(result.wasSuccessful());
    }
}
