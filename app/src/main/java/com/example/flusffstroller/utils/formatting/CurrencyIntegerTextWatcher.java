package com.example.flusffstroller.utils.formatting;

import android.widget.EditText;

import java.util.function.Consumer;

public class CurrencyIntegerTextWatcher extends NumberTextWatcher {

    private final String currencySymbol;

    public CurrencyIntegerTextWatcher(EditText editText, String currencySymbol, Consumer<String> onTextChanged) {
        super(editText, "[" + currencySymbol + ",.-]", onTextChanged);
        this.currencySymbol = currencySymbol;
    }

    public CurrencyIntegerTextWatcher(EditText editText, String currencySymbol) {
        this(editText, currencySymbol, null);
    }

    @Override
    protected String format(String input) {
        return currencySymbol + input;
    }
}
