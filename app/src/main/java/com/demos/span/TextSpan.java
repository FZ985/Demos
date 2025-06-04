package com.demos.span;

import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

import com.demos.span.core.Span;

import java.util.ArrayList;
import java.util.List;

/**
 * author : JFZ
 * date : 2023/12/28 09:45
 * description : 仅更改文字大小、颜色的span 工具
 */
public final class TextSpan {

    private final List<TextBuilder> builders = new ArrayList<>();

    private TextSpan() {
        builders.clear();
    }

    public static TextSpan get() {
        return new TextSpan();
    }

    public TextSpan add(TextBuilder build) {
        builders.add(build);
        return this;
    }

    public static TextBuilder build(String text) {
        return new TextBuilder(text);
    }

    public static TextBuilder build(Span.Spannable spannable) {
        return new TextBuilder(spannable);
    }

    public SpannableStringBuilder build() {
        SpannableStringBuilder string = new SpannableStringBuilder();
        for (TextBuilder build : builders) {
            String text = build.getText();
            List<TextCreator> creators = build.getCreators();
            SpannableString span = new SpannableString(text);
            for (TextCreator create : creators) {
                span.setSpan(create.getSpan(), create.getStart(), create.getEnd(), create.getFlag());
            }
            string.append(span);
        }
        return string;
    }

    public final static class TextBuilder {

        private final String text;
        private final List<TextCreator> creators = new ArrayList<>();

        public TextBuilder(String string) {
            this.text = string;
            creators.clear();
        }

        public TextBuilder(@NonNull Span.Spannable spannable) {
            this.text = spannable.getText();
            creators.clear();
            addSpan(spannable);
        }

        public TextBuilder textColor(@ColorInt int color) {
            if (!TextUtils.isEmpty(text)) {
                creators.add(new TextCreator(new ForegroundColorSpan(color),
                        0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE));
            }
            return this;
        }

        public TextBuilder textSize(int dpSize) {
            if (!TextUtils.isEmpty(text)) {
                creators.add(new TextCreator(new AbsoluteSizeSpan(dpSize, true),
                        0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE));
            }
            return this;
        }

        public TextBuilder textStyle(int style) {
            if (!TextUtils.isEmpty(text)) {
                creators.add(new TextCreator(new StyleSpan(style),
                        0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE));
            }
            return this;
        }

        public TextBuilder addSpan(Object span) {
            if (!TextUtils.isEmpty(text)) {
                creators.add(new TextCreator(span, 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE));
            }
            return this;
        }

        public TextBuilder addSpan(TextCreator creator) {
            if (!TextUtils.isEmpty(text)) {
                creators.add(creator);
            }
            return this;
        }

        String getText() {
            return text;
        }

        List<TextCreator> getCreators() {
            return creators;
        }
    }

    public final static class TextCreator {

        private final Object span;

        private final int start;

        private final int end;

        private final int flag;

        public TextCreator(Object span, int start, int end, int flag) {
            this.span = span;
            this.start = start;
            this.end = end;
            this.flag = flag;
        }

        public Object getSpan() {
            return span;
        }

        public int getStart() {
            return start;
        }

        public int getEnd() {
            return end;
        }

        public int getFlag() {
            return flag;
        }
    }


}
