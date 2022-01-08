package com.example.fluffstroller.models;

import android.content.Intent;

public final class ActivityResult {

    public final int requestCode;
    public final int resultCode;
    public final Intent data;

    public ActivityResult(int requestCode, int resultCode, Intent data) {
        this.requestCode = requestCode;
        this.resultCode = resultCode;
        this.data = data;
    }
}
