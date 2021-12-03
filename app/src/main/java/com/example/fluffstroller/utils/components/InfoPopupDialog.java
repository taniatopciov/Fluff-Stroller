package com.example.fluffstroller.utils.components;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import com.example.fluffstroller.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class InfoPopupDialog extends DialogFragment {

    private final String message;
    private final Runnable action;
    private String buttonText;

    public InfoPopupDialog(String message, String buttonText, Runnable action) {
        this.message = message;
        this.buttonText = buttonText;
        this.action = action;
    }

    public InfoPopupDialog(String message, Runnable action) {
        this.message = message;
        this.action = action;
        buttonText = null;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        if (buttonText == null || buttonText.isEmpty()) {
            buttonText = getContext().getResources().getString(R.string.ok);
        }

        AlertDialog alertDialog = new AlertDialog.Builder(requireContext())
                .setMessage(message)
                .setPositiveButton(buttonText, (dialog, which) -> {
                    action.run();
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
