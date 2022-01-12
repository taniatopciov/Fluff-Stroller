package com.example.fluffstroller.utils.components;

import android.app.Activity;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.example.fluffstroller.R;

public class CustomToast {

    public static void show(Activity activity, String message, int duration) {
        activity.runOnUiThread(() -> {
            try {
                Toast toast = Toast.makeText(activity, message, duration);
                TextView text = toast.getView().findViewById(android.R.id.message);
                text.setTextColor(ContextCompat.getColor(activity, R.color.accent));
                text.setTextSize(16);
                toast.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
