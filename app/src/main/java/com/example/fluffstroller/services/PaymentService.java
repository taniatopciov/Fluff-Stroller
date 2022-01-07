package com.example.fluffstroller.services;

import com.example.fluffstroller.models.DogWalk;
import com.example.fluffstroller.utils.observer.Subject;
import com.stripe.android.PaymentIntentResult;
import com.stripe.android.model.PaymentMethodCreateParams;

public interface PaymentService {

    Subject<PaymentIntentResult> startCheckout(DogWalk dogWalk, PaymentMethodCreateParams paymentMethodCreateParams);
}
