package com.neobank.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EmiCalculatorUtilTest {

    @Test
    void testCalculateEMI() {

        double principal = 100000.0;
        double rate = 10.0;
        int tenure = 12;

        double emi = EmiCalculatorUtil.calculateEMI(
                principal, rate, tenure
        );

        // ✅ Expected value (based on your formula)
        double expected = 8791.59;

        assertEquals(expected, emi, 0.5);
    }
}