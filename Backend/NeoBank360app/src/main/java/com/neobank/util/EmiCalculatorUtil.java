package com.neobank.util;

public class EmiCalculatorUtil {

    public static double calculateEMI(double p, double rate, int n) {
        double r = rate / 12 / 100;
        double emi = (p * r * Math.pow(1 + r, n)) /
            (Math.pow(1 + r, n) - 1);
        return Math.round(emi * 100.0) / 100.0;
    }
}