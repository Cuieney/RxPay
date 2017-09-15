package com.cuieney.sdk.rxpay;

/**
 * Created by cuieney on 18/08/2017.
 */

public class PaymentStatus {
    private boolean status;

    public PaymentStatus(boolean status) {
        this.status = status;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
