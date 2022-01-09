package com.example.fluffstroller.utils.components;

import android.content.Intent;
import android.provider.Settings;

import com.example.fluffstroller.R;

public class EnableLocationPopupDialog extends ConfirmationPopupDialog {

    public EnableLocationPopupDialog() {
        super(R.string.gps_enable_message, fragment -> {
            fragment.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }, null);
    }
}
