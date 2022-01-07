package com.example.fluffstroller.services.impl;

import static com.example.fluffstroller.BuildConfig.CREATE_PAYMENT_INTENT_URL;

import androidx.annotation.NonNull;

import com.example.fluffstroller.BuildConfig;
import com.example.fluffstroller.MainActivity;
import com.example.fluffstroller.models.ActivityResult;
import com.example.fluffstroller.models.ClientSecretHolder;
import com.example.fluffstroller.models.DogWalk;
import com.example.fluffstroller.models.PaymentData;
import com.example.fluffstroller.models.WalkRequest;
import com.example.fluffstroller.models.WalkRequestStatus;
import com.example.fluffstroller.services.PaymentService;
import com.example.fluffstroller.utils.observer.Subject;
import com.google.gson.Gson;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.PaymentIntentResult;
import com.stripe.android.Stripe;
import com.stripe.android.model.ConfirmPaymentIntentParams;
import com.stripe.android.model.PaymentMethodCreateParams;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PaymentServiceImpl implements PaymentService {

    private final MainActivity activity;
    private final OkHttpClient httpClient;
    private final Stripe stripe;

    public PaymentServiceImpl(MainActivity activity) {
        this.activity = activity;
        httpClient = new OkHttpClient();
        stripe = new Stripe(activity, BuildConfig.STRIPE_PUBLISHABLE_KEY);
    }

    @Override
    public Subject<PaymentIntentResult> startCheckout(DogWalk dogWalk, PaymentMethodCreateParams paymentMethodCreateParams) {
        Subject<PaymentIntentResult> subject = new Subject<>();

        String ownerId = dogWalk.getOwnerId();
        String strollerId = "";
        String walkId = dogWalk.getId();
        Integer amount = dogWalk.getTotalPrice();

        List<WalkRequest> requests = dogWalk.getRequests();
        for (WalkRequest request : requests) {
            if (request.getStatus().equals(WalkRequestStatus.ACCEPTED)) {
                strollerId = request.getStrollerId();
                break;
            }
        }

        PaymentData paymentData = new PaymentData("RON", ownerId, strollerId, walkId, amount * 100);
        Gson gson = new Gson();

        MediaType mediaType = MediaType.get("application/json; charset=utf-8");
        String json = gson.toJson(paymentData);
        RequestBody body = RequestBody.create(json, mediaType);
        Request request = new Request.Builder()
                .url(CREATE_PAYMENT_INTENT_URL)
                .post(body)
                .build();
        httpClient.newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        subject.notifyObservers(e);
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        try {
                            ClientSecretHolder clientSecretHolder = gson.fromJson(response.body().string(), ClientSecretHolder.class);
                            String clientSecret = clientSecretHolder.getClientSecret();

                            if (paymentMethodCreateParams != null && clientSecret != null && !clientSecret.isEmpty()) {
                                ConfirmPaymentIntentParams confirmParams = ConfirmPaymentIntentParams
                                        .createWithPaymentMethodCreateParams(paymentMethodCreateParams, clientSecret);

                                activity.activityResultSubject.subscribe(res -> {

                                    if (res.hasErrors() || res.data == null) {
                                        subject.notifyObservers(new Exception("Empty Activity Result"));
                                        return;
                                    }

                                    ActivityResult activityResult = res.data;
                                    stripe.onPaymentResult(activityResult.requestCode, activityResult.data, new ApiResultCallback<PaymentIntentResult>() {
                                        @Override
                                        public void onSuccess(@NonNull PaymentIntentResult paymentIntentResult) {
                                            subject.notifyObservers(paymentIntentResult);
                                        }

                                        @Override
                                        public void onError(@NonNull Exception e) {
                                            subject.notifyObservers(e);
                                        }
                                    });
                                });

                                stripe.confirmPayment(activity, confirmParams);
                            } else {
                                subject.notifyObservers(new Exception("Empty client secret"));
                            }
                        } catch (Exception e) {
                            subject.notifyObservers(e);
                        }
                    }
                });

        return subject;
    }
}
