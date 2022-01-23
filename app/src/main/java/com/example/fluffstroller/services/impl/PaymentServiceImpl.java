package com.example.fluffstroller.services.impl;

import com.example.fluffstroller.MainActivity;
import com.example.fluffstroller.models.AccountBalance;
import com.example.fluffstroller.models.ActivityResult;
import com.example.fluffstroller.models.ClientSecretHolder;
import com.example.fluffstroller.models.DogWalk;
import com.example.fluffstroller.models.StripeConfig;
import com.example.fluffstroller.models.WalkRequest;
import com.example.fluffstroller.models.WalkRequestStatus;
import com.example.fluffstroller.repository.FirebaseRepository;
import com.example.fluffstroller.services.FeesService;
import com.example.fluffstroller.services.PaymentService;
import com.example.fluffstroller.utils.observer.Subject;
import com.google.gson.Gson;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.PaymentIntentResult;
import com.stripe.android.Stripe;
import com.stripe.android.model.ConfirmPaymentIntentParams;
import com.stripe.android.model.PaymentIntent;
import com.stripe.android.model.PaymentMethodCreateParams;
import com.stripe.android.model.StripeIntent;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.fluffstroller.BuildConfig.CREATE_PAYMENT_INTENT_URL;
import static com.example.fluffstroller.BuildConfig.PAYMENT_PUBLISHABLE_KEY_URL;
import static com.example.fluffstroller.BuildConfig.TRANSFER_MONEY_URL;

public class PaymentServiceImpl implements PaymentService {
    private static final String BALANCES_COLLECTION_PATH = "balances";

    private final MainActivity activity;
    private final OkHttpClient httpClient;
    private final FirebaseRepository firebaseRepository;
    private final FeesService feesService;

    private Stripe stripe = null;

    public PaymentServiceImpl(MainActivity activity, FirebaseRepository firebaseRepository, FeesService feesService) {
        this.activity = activity;
        this.firebaseRepository = firebaseRepository;
        this.feesService = feesService;
        httpClient = new OkHttpClient();
    }

    @Override
    public Subject<Boolean> initService() {
        Subject<Boolean> subject = new Subject<>();

        Request request = new Request.Builder()
                .url(PAYMENT_PUBLISHABLE_KEY_URL)
                .get()
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
                            Gson gson = new Gson();
                            StripeConfig stripeConfig = gson.fromJson(response.body().string(), StripeConfig.class);

                            stripe = new Stripe(activity, stripeConfig.getPublishableKey());
                        } catch (Exception e) {
                            subject.notifyObservers(e);
                        }
                    }
                });

        return subject;
    }

    @Override
    public Subject<PaymentIntentResult> startCheckout(DogWalk dogWalk, PaymentMethodCreateParams paymentMethodCreateParams) {
        String ownerId = dogWalk.getOwnerId();
        String strollerId = "";
        String walkId = dogWalk.getId();
        Double amount = dogWalk.getTotalPrice();

        List<WalkRequest> requests = dogWalk.getRequests();
        for (WalkRequest request : requests) {
            if (request.getStatus().equals(WalkRequestStatus.ACCEPTED)) {
                strollerId = request.getStrollerId();
                break;
            }
        }

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("ownerId", ownerId);
        metadata.put("strollerId", strollerId);
        metadata.put("walkId", walkId);

        Map<String, Object> paymentData = new HashMap<>();
        paymentData.put("currency", "RON");
        paymentData.put("amount", amount * 100.0);
        paymentData.put("metadata", metadata);

        Gson gson = new Gson();
        String json = gson.toJson(paymentData);

        return createPaymentIntent(json, paymentMethodCreateParams);
    }

    @Override
    public Subject<AccountBalance> getBalance(String userId) {
        return firebaseRepository.listenForDocumentChanges(BALANCES_COLLECTION_PATH + "/" + userId, AccountBalance.class);
    }

    @Override
    public Subject<Boolean> transferMoney(String userId, Double amount, PaymentMethodCreateParams paymentMethodCreateParams) {
        Subject<Boolean> subject = new Subject<>();

        Gson gson = new Gson();

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("userId", userId);

        Map<String, Object> paymentData = new HashMap<>();
        paymentData.put("currency", "RON");
        paymentData.put("amount", amount * 100.0);
        paymentData.put("metadata", metadata);
        String json = gson.toJson(paymentData);

        createPaymentIntent(json, paymentMethodCreateParams).subscribe(response -> {
            if (response.hasErrors()) {
                subject.notifyObservers(response.exception);
                return;
            }
            if (response.data == null) {
                subject.notifyObservers(new Exception("Empty Payment Data"));
                return;
            }

            PaymentIntent intent = response.data.getIntent();
            if (intent.getStatus() == StripeIntent.Status.Succeeded) {
                Map<String, Object> data = new HashMap<>();
                Double refundAmount = amount - feesService.getStrollerFees(amount);
                data.put("userId", userId);
                data.put("paymentIntentId", intent.getId());
                data.put("amount", refundAmount);
                String jsonData = gson.toJson(data);


                makePostRequest(TRANSFER_MONEY_URL, jsonData, new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        subject.notifyObservers(e);
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        subject.notifyObservers(true);
                    }
                });
            }
        });

        return subject;
    }

    private Subject<PaymentIntentResult> createPaymentIntent(String jsonData, PaymentMethodCreateParams paymentMethodCreateParams) {
        Subject<PaymentIntentResult> subject = new Subject<>();

        makePostRequest(CREATE_PAYMENT_INTENT_URL, jsonData, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                subject.notifyObservers(e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try {
                    Gson gson = new Gson();
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

    private void makePostRequest(String url, String data, Callback callback) {
        MediaType mediaType = MediaType.get("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(data, mediaType);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        httpClient.newCall(request)
                .enqueue(callback);

    }
}
