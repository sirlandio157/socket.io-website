package com.malalu.service;

import com.malalu.model.PaymentStatus;

public class PaymentVerificationResult {

    private final PaymentStatus paymentStatus;
    private final int riskScore;
    private final boolean suspicious;
    private final String note;

    public PaymentVerificationResult(PaymentStatus paymentStatus, int riskScore, boolean suspicious, String note) {
        this.paymentStatus = paymentStatus;
        this.riskScore = riskScore;
        this.suspicious = suspicious;
        this.note = note;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public int getRiskScore() {
        return riskScore;
    }

    public boolean isSuspicious() {
        return suspicious;
    }

    public String getNote() {
        return note;
    }
}
