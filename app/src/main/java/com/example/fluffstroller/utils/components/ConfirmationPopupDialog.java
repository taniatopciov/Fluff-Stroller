package com.example.fluffstroller.utils.components;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.example.fluffstroller.R;

import java.util.function.Consumer;

public class ConfirmationPopupDialog extends DialogFragment {

    private final int resourceId;
    private final Consumer<Fragment> positiveAction;
    private final Consumer<Fragment> negativeAction;

    public ConfirmationPopupDialog(int resourceId, Consumer<Fragment> positiveAction, Consumer<Fragment> negativeAction) {
        this.resourceId = resourceId;
        this.positiveAction = positiveAction;
        this.negativeAction = negativeAction;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog alertDialog = new AlertDialog.Builder(requireContext())
                .setMessage(resourceId)
                .setPositiveButton(R.string.yes, (dialog, which) -> {

                    if (positiveAction != null) {
                        positiveAction.accept(this);
                    }

                    if (dialog != null) {
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(getString(R.string.no), (dialog, i) -> {
                    if (negativeAction != null) {
                        negativeAction.accept(this);
                    }

                    if (dialog != null) {
                        dialog.dismiss();
                    }
                })
                .setCancelable(false)
                .create();
        alertDialog.setCanceledOnTouchOutside(false);
        return alertDialog;
    }
}
