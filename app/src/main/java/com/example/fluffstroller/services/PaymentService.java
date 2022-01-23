package com.example.fluffstroller.services;

import com.example.fluffstroller.models.AccountBalance;
import com.example.fluffstroller.models.DogWalk;
import com.example.fluffstroller.utils.observer.Subject;
import com.stripe.android.PaymentIntentResult;
import com.stripe.android.model.PaymentMethodCreateParams;

public interface PaymentService {
    Subject<Boolean> initService();

    Subject<PaymentIntentResult> startCheckout(DogWalk dogWalk, PaymentMethodCreateParams paymentMethodCreateParams);

    Subject<AccountBalance> getBalance(String userId);

    Subject<Boolean> transferMoney(String userId, Double amount, PaymentMethodCreateParams paymentMethodCreateParams);
}
