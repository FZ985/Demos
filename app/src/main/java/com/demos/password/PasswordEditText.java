package com.demos.password;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;

import com.demos.R;
import com.demos.widgets.CheckableImageButton;

/**
 * author : JFZ
 * date : 2023/9/11 10:14
 * description : 密码组件
 */
public class PasswordEditText extends FrameLayout {
    private AppCompatEditText et;
    private CheckableImageButton btn;

    public PasswordEditText(@NonNull Context context) {
        this(context, null);
    }

    public PasswordEditText(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PasswordEditText(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {
        View view = inflate(getContext(), R.layout.components_password_edittext, this);
        et = view.findViewById(R.id.password_et);
        btn = view.findViewById(R.id.password_btn);
        TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable.PasswordEditText);
        String hintStr = arr.getString(R.styleable.PasswordEditText_android_hint);
        if (!TextUtils.isEmpty(hintStr)) {
            et.setHint(hintStr);
        }
        et.setTextColor(arr.getColor(R.styleable.PasswordEditText_android_textColor, Color.BLACK));
        et.setHintTextColor(arr.getColor(R.styleable.PasswordEditText_android_textColorHint, Color.GRAY));
        int size = arr.getDimensionPixelSize(R.styleable.PasswordEditText_android_textSize, 0);
        if (size != 0) {
            et.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
        }
        int tint = arr.getColor(R.styleable.PasswordEditText_android_tint, Color.GRAY);
        btn.setImageTintList(ColorStateList.valueOf(tint));
        arr.recycle();
        btn.setOnClickListener(v -> {
            if (!btn.isChecked()) {
                et.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                et.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
            String text = getText();
            et.setSelection(text.length());
            btn.toggle();
        });
    }

    public String getText() {
        Editable text = et.getText();
        if (text != null) {
            return text.toString().trim();
        }
        return "";
    }


    public void addTextChangedListener(TextWatcher watcher){
        et.addTextChangedListener(watcher);
    }


}
