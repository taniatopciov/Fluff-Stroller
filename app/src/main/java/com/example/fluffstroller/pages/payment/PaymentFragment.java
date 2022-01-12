package com.example.fluffstroller.pages.payment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.fluffstroller.databinding.PaymentFragmentBinding;
import com.example.fluffstroller.di.Injectable;
import com.example.fluffstroller.models.DogWalk;
import com.example.fluffstroller.models.WalkRequest;
import com.example.fluffstroller.models.WalkStatus;
import com.example.fluffstroller.services.DogWalksService;
import com.example.fluffstroller.services.PaymentService;
import com.example.fluffstroller.utils.FragmentWithServices;
import com.example.fluffstroller.utils.HideKeyboard;
import com.example.fluffstroller.utils.components.CustomToast;
import com.example.fluffstroller.utils.components.InfoPopupDialog;
import com.stripe.android.model.PaymentIntent;
import com.stripe.android.model.PaymentMethodCreateParams;
import com.stripe.android.model.StripeIntent;

public class PaymentFragment extends FragmentWithServices {

    private PaymentFragmentBinding binding;
    private PaymentViewModel viewModel;
    private static final String PAYMENT_FRAGMENT_UNIQUE_STRING = "PAYMENT_FRAGMENT_UNIQUE_STRING";

    @Injectable
    private PaymentService paymentService;

    @Injectable
    private DogWalksService dogWalksService;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = PaymentFragmentBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(PaymentViewModel.class);

        String walkId = PaymentFragmentArgs.fromBundle(getArguments()).getDogWalkId();

        dogWalksService.getDogWalk(walkId).subscribe(response -> {
            if (response.hasErrors() || response.data == null) {
                CustomToast.show(requireActivity(), "Could not get DogWalk", Toast.LENGTH_LONG);
                return;
            }
            viewModel.setDogWalk(response.data);
        });

        viewModel.getLoadingCircle().observe(getViewLifecycleOwner(), visible -> {
            binding.progressBarPaymentPage.setVisibility(visible ? View.VISIBLE : View.GONE);
        });

        viewModel.getDogWalk().observe(getViewLifecycleOwner(), dogWalk -> {
            binding.amountTextViewPaymentPage.setText(dogWalk.getTotalPrice() + "");
        });

        viewModel.getDisablePayButton().observe(getViewLifecycleOwner(), disabled -> {
            binding.payButtonPaymentPage.setEnabled(!disabled);
        });

        viewModel.setLoadingCircle(false);

        binding.payButtonPaymentPage.setOnClickListener(view -> {
            HideKeyboard.hide(requireActivity());
            PaymentMethodCreateParams params = binding.cardInputWidgetPaymentPage.getPaymentMethodCreateParams();

            DogWalk walk = viewModel.getDogWalk().getValue();
            if (params == null || params.getBillingDetails() == null || walk == null) {
                return;
            }

            viewModel.setLoadingCircle(true);
            viewModel.setDisablePayButton(true);
            NavController navController = NavHostFragment.findNavController(this);

            paymentService.startCheckout(walk, params).subscribe(response -> {
                viewModel.setLoadingCircle(false);

                if (response.hasErrors() || response.data == null) {
                    CustomToast.show(requireActivity(), "Payment error", Toast.LENGTH_LONG);
                    viewModel.setDisablePayButton(false);
                    return;
                }

                PaymentIntent intent = response.data.getIntent();
                if (intent.getStatus() == StripeIntent.Status.Succeeded) {
                    dogWalksService.updateDogWalk(walk.getOwnerId(), walk.getId(), WalkStatus.PAID, null).subscribe(response1 -> {
                        if (response1.hasErrors() || response1.data == null) {
                            CustomToast.show(requireActivity(), "Update Dog Walk Error", Toast.LENGTH_LONG);
                            return;
                        }

                        WalkRequest acceptedRequest = walk.getAcceptedRequest();
                        if (acceptedRequest != null) {
                            dogWalksService.updateWalkAfterPayment(walk.getOwnerId(), acceptedRequest.getStrollerId()).subscribe(response2 -> {
                                if (response2.hasErrors()) {
                                    CustomToast.show(requireActivity(), "Update Walk After Payment Error", Toast.LENGTH_LONG);
                                    return;
                                }
                                new InfoPopupDialog("Payment Successful", () -> {
                                    navController.navigate(PaymentFragmentDirections.actionPaymentFragmentToNavDogOwnerHome());
                                })
                                        .show(getChildFragmentManager(), PAYMENT_FRAGMENT_UNIQUE_STRING);
                            });
                        }
                    });
                } else {
                    CustomToast.show(requireActivity(), "Payment Unsuccessful", Toast.LENGTH_LONG);
                    viewModel.setDisablePayButton(false);
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
}