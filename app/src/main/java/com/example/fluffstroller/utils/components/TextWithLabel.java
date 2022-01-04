package com.example.fluffstroller.utils.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.fluffstroller.R;

public class TextWithLabel extends LinearLayout {

    public EditText editText;

    public TextWithLabel(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        View inflate = inflate(context, R.layout.text_view_with_label, this);
        TextView label = inflate.findViewById(R.id.labelTextView);
        editText = inflate.findViewById(R.id.editText);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TextWithLabel);

        label.setText(typedArray.getString(R.styleable.TextWithLabel_label));
        editText.setText(typedArray.getString(R.styleable.TextWithLabel_android_text));
        editText.setHint(typedArray.getString(R.styleable.TextWithLabel_android_hint));
        editText.setEms(typedArray.getInteger(R.styleable.TextWithLabel_android_ems, 0));
        editText.setMinHeight((int) typedArray.getDimension(R.styleable.TextWithLabel_android_minHeight, 0.0f));
        int maxLength = typedArray.getInteger(R.styleable.TextWithLabel_android_maxLength, Integer.MAX_VALUE);
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
        editText.setInputType(typedArray.getInteger(R.styleable.TextWithLabel_android_inputType, 0));
        editText.setClickable(typedArray.getBoolean(R.styleable.TextWithLabel_android_clickable, true));
        editText.setFocusable(typedArray.getBoolean(R.styleable.TextWithLabel_android_focusable, true));
        editText.setCursorVisible(typedArray.getBoolean(R.styleable.TextWithLabel_android_cursorVisible, true));

        typedArray.recycle();
    }

    public void addTextChangedListener(TextWatcher watcher) {
        editText.addTextChangedListener(watcher);
    }

    public void setText(String string) {
        editText.setText(string);
    }

    public String getText() {
        return editText.getText().toString();
    }
}
