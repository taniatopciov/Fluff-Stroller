package com.example.fluffstroller.utils.components;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;

import com.example.fluffstroller.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class EnableLocationPopupDialog extends DialogFragment {


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog alertDialog = new AlertDialog.Builder(requireContext())
                .setMessage(R.string.gps_enable_message)
                .setPositiveButton(R.string.yes, (dialog, which) -> {

                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));

                    if (dialog != null) {
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(getString(R.string.no), (dialog, i) -> {
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
