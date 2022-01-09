package com.example.fluffstroller.models;

public class PaymentData {
    private String currency;
    private String ownerId;
    private String strollerId;
    private String walkId;
    private Double amount;

    public PaymentData() {
    }

    public PaymentData(String currency, String ownerId, String strollerId, String walkId, Double amount) {
        this.currency = currency;
        this.ownerId = ownerId;
        this.strollerId = strollerId;
        this.walkId = walkId;
        this.amount = amount;
    }
}