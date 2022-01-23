package com.example.fluffstroller.pages.payment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.fluffstroller.R;
import com.example.fluffstroller.databinding.BalanceFragmentBinding;
import com.example.fluffstroller.di.Injectable;
import com.example.fluffstroller.services.FeesService;
import com.example.fluffstroller.services.LoggedUserDataService;
import com.example.fluffstroller.services.PaymentService;
import com.example.fluffstroller.utils.FragmentWithServices;
import com.example.fluffstroller.utils.HideKeyboard;
import com.example.fluffstroller.utils.components.ConfirmationPopupDialog;
import com.example.fluffstroller.utils.components.CustomToast;
import com.stripe.android.model.PaymentMethodCreateParams;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

public class BalanceFragment extends FragmentWithServices {

    private static final String BALANCE_FRAGMENT = "BALANCE_FRAGMENT";

    @Injectable
    private LoggedUserDataService loggedUserDataService;

    @Injectable
    private FeesService feesService;

    @Injectable
    private PaymentService paymentService;

    private BalanceFragmentBinding binding;
    private BalanceViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = BalanceFragmentBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(BalanceViewModel.class);

        viewModel.getLoadingCircle().observe(getViewLifecycleOwner(), visible -> {
            binding.progressBarBalancePage.setVisibility(visible ? View.VISIBLE : View.GONE);
        });

        viewModel.getBalance().observe(getViewLifecycleOwner(), balance -> {
            Double fees = feesService.getStrollerFees(balance);
            Double transferableMoney = balance;

            if (fees != null) {
                transferableMoney -= fees;
            }

            viewModel.setTransferableMoney(transferableMoney);

            binding.balanceTextView.setText(balance + "");
            binding.feesBalanceTextView.setText(fees + "");
        });

        viewModel.getTransferableMoney().observe(getViewLifecycleOwner(), transferableMoney -> {
            binding.transferableMoneyBalanceTextView.setText(transferableMoney + "");
        });

        viewModel.getDisablePayButton().observe(getViewLifecycleOwner(), disabled -> {
            binding.transferMoneyButton.setEnabled(!disabled);
        });

        viewModel.setLoadingCircle(false);
        viewModel.setDisablePayButton(true);

        paymentService.initService().subscribe(response -> {
            if (response.hasErrors()) {
                CustomToast.show(requireActivity(), R.string.could_not_connect_to_payment_server, Toast.LENGTH_LONG);
            }
        });

        binding.transferMoneyButton.setOnClickListener(view -> {
            HideKeyboard.hide(requireActivity());
            PaymentMethodCreateParams params = binding.cardInputWidgetBalancePage.getPaymentMethodCreateParams();

            if (params == null || params.getBillingDetails() == null) {
                return;
            }

            viewModel.setDisablePayButton(true);

            new ConfirmationPopupDialog(R.string.transfer_money_message, ignored -> {
                viewModel.setLoadingCircle(true);

                Double amount = viewModel.getBalance().getValue();

                paymentService.transferMoney(loggedUserDataService.getLoggedUserId(), amount, params).subscribe(response -> {
                    viewModel.setLoadingCircle(false);
                    if (response.hasErrors()) {
                        CustomToast.show(requireActivity(), R.string.could_not_transfer_money, Toast.LENGTH_LONG);
                        viewModel.setDisablePayButton(false);
                    } else {
                        CustomToast.show(requireActivity(), R.string.money_transferred, Toast.LENGTH_LONG);
                    }
                });
            }, null).show(getChildFragmentManager(), BALANCE_FRAGMENT);
        });

        registerSubject(paymentService.getBalance(loggedUserDataService.getLoggedUserId())).subscribe(response -> {
            if (response.hasErrors()) {
                CustomToast.show(requireActivity(), R.string.could_not_connect_to_payment_server, Toast.LENGTH_LONG);
                return;
            }

            if (response.data == null) {
                return;
            }

            Double balance = response.data.getBalance();
            viewModel.setBalance(balance);
            viewModel.setDisablePayButton(balance == null || balance <= 0);
        }, false);

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}