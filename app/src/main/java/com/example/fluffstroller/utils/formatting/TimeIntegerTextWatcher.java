package com.example.fluffstroller.utils.formatting;

import android.widget.EditText;

import java.util.function.Consumer;

public class TimeIntegerTextWatcher extends NumberTextWatcher {

    private final String timeSpan;

    public TimeIntegerTextWatcher(EditText editText, String timeSpan, Consumer<String> onTextChanged) {
        super(editText, "[" + timeSpan + ",.-]", onTextChanged);
        this.timeSpan = timeSpan;
    }

    public TimeIntegerTextWatcher(EditText editText, String timeSpan) {
        this(editText, timeSpan, null);
    }

    @Override
    protected String format(String input) {
        return input + " " + timeSpan;
    }
}
