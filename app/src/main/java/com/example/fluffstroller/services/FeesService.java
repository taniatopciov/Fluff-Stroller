package com.example.fluffstroller.services;

public class FeesService {
    public static final double FEE_PERCENT = 10.0;

    public Double getDogWalkFees(Integer price) {
        if (price == null) {
            return 0.0;
        }
        return (price * FEE_PERCENT) / 100.0;
    }

    public Double getDogWalkPriceWithoutFees(Double price) {
        if (price == null) {
            return 0.0;
        }
        return (price * 100.0) / (FEE_PERCENT + 100.0);
    }

    public Double getStrollerFees(Double price) {
        if (price == null) {
            return 0.0;
        }
        return (price * FEE_PERCENT) / 100.0;
    }
}
