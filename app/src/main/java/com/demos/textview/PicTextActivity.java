package com.demos.textview;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextPaint;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.demos.Tools;
import com.demos.databinding.ActivityPicTextviewBinding;

/**
 * author : JFZ
 * date : 2023/7/27 15:42
 * description :
 */
public class PicTextActivity extends AppCompatActivity {
    private ActivityPicTextviewBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPicTextviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.text.clean();

        TextPaint paint = new TextPaint(TextPaint.ANTI_ALIAS_FLAG);
        paint.setTextSize(Tools.dip2px(14));
        paint.setColor(Color.BLUE);
        binding.text.addTextChild("哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈", paint);
//        binding.text.addTextChild("嘿嘿嘿嘿嘿嘿嘿嘿嘿嘿嘿");


//        TextView tv0 = (TextView) LayoutInflater.from(this).inflate(R.layout.text, null);
//        tv0.setText("哈哈哈哈哈哈哈哈哈");
//        TextView tv = (TextView) LayoutInflater.from(this).inflate(R.layout.text, null);
//        tv.setText("哦哦哦哦哦哦哦哦哦哦哦哦哦");
//        binding.text.addNewChild(tv0, new PicAndTextView.LayoutParam(PicAndTextView.LayoutParam.WRAP_CONTENT, PicAndTextView.LayoutParam.WRAP_CONTENT, Gravity.CENTER_VERTICAL));
//        binding.text.addNewChild(tv, new PicAndTextView.LayoutParam(PicAndTextView.LayoutParam.WRAP_CONTENT, PicAndTextView.LayoutParam.WRAP_CONTENT, Gravity.CENTER_VERTICAL));
//
    }
}
