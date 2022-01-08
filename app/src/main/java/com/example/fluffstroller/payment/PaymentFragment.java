package com.example.fluffstroller.payment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.example.fluffstroller.databinding.PaymentFragmentBinding;
import com.example.fluffstroller.di.Injectable;
import com.example.fluffstroller.models.DogWalk;
import com.example.fluffstroller.services.PaymentService;
import com.example.fluffstroller.utils.FragmentWithServices;
import com.example.fluffstroller.utils.HideKeyboard;
import com.example.fluffstroller.utils.components.CustomToast;
import com.stripe.android.model.PaymentIntent;
import com.stripe.android.model.PaymentMethodCreateParams;
import com.stripe.android.model.StripeIntent;
import com.stripe.android.payments.paymentlauncher.PaymentResult;

public class PaymentFragment extends FragmentWithServices {

    private PaymentFragmentBinding binding;
    private PaymentViewModel viewModel;

    @Injectable
    private PaymentService paymentService;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = PaymentFragmentBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(PaymentViewModel.class);

        DogWalk walk = PaymentFragmentArgs.fromBundle(getArguments()).getWalk();

        viewModel.getLoadingCircle().observe(getViewLifecycleOwner(), visible -> {
            binding.progressBarPaymentPage.setVisibility(visible ? View.VISIBLE : View.GONE);
        });

        viewModel.getDisablePayButton().observe(getViewLifecycleOwner(), ignored -> {
            binding.payButtonPaymentPage.setEnabled(false);
        });

        viewModel.setLoadingCircle(false);

        binding.amountTextViewPaymentPage.setText(walk.getTotalPrice() + "");

        binding.payButtonPaymentPage.setOnClickListener(view -> {
            HideKeyboard.hide(requireActivity());
            viewModel.setLoadingCircle(true);
            viewModel.disablePayButton();

            PaymentMethodCreateParams params = binding.cardInputWidgetPaymentPage.getPaymentMethodCreateParams();

            paymentService.startCheckout(walk, params).subscribe(response -> {
                viewModel.setLoadingCircle(false);

                if (response.hasErrors() || response.data == null) {
                    CustomToast.show(requireActivity(), "Payment error", Toast.LENGTH_LONG);
                    return;
                }

                PaymentIntent intent = response.data.getIntent();
                if (intent.getStatus() == StripeIntent.Status.Succeeded) {
                    displayAlert("Payment succeeded", "");
                } else {
                    displayAlert("Payment unsuccessful", "");
                }
            });
        });

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void displayAlert(@NonNull String title,
                              @Nullable String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity())
                .setTitle(title)
                .setMessage(message);

        builder.setPositiveButton("Ok", null);
        builder.create().show();
    }

    private void onPaymentResult(PaymentResult paymentResult) {
        String message = "";
        if (paymentResult instanceof PaymentResult.Completed) {
            message = "Completed!";
        } else if (paymentResult instanceof PaymentResult.Canceled) {
            message = "Cancelled!";
        } else if (paymentResult instanceof PaymentResult.Failed) {
            // This string comes from the PaymentIntent's error message.
            // See here: https://stripe.com/docs/api/payment_intents/object#payment_intent_object-last_payment_error-message
            message = "Failed: "
                    + ((PaymentResult.Failed) paymentResult).getThrowable().getMessage();
        }

        displayAlert("PaymentResult: ", message);
    }
}