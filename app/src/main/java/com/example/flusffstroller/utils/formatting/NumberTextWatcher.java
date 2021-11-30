package com.example.flusffstroller.utils.formatting;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.util.function.Consumer;

public abstract class NumberTextWatcher implements TextWatcher {

    private final EditText editText;
    private final String replacePattern;
    private final Consumer<String> onTextChanged;
    private String current = "";

    public NumberTextWatcher(EditText editText, String replacePattern, Consumer<String> onTextChanged) {
        this.editText = editText;
        this.replacePattern = replacePattern;
        this.onTextChanged = onTextChanged;
    }

    protected abstract String format(String input);

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if (!charSequence.toString().equals(current)) {
            editText.removeTextChangedListener(this);

            String cleanString = charSequence.toString().replaceAll(replacePattern, "");
            cleanString = cleanString.replaceAll(" ", "");

            int selection = cleanString.length();

            if (cleanString.length() > 0) {
                current = format(cleanString);
                int prefixOffset = current.indexOf(cleanString);
                if (prefixOffset >= 0) {
                    selection += prefixOffset;
                }

                if (onTextChanged != null) {
                    onTextChanged.accept(cleanString);
                }
            } else {
                current = cleanString;

                if (onTextChanged != null) {
                    onTextChanged.accept("0");
                }
            }

            editText.setText(current);
            editText.setSelection(selection);
            editText.addTextChangedListener(this);
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {
    }
}
