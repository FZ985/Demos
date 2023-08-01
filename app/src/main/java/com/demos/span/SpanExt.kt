package com.demos.span

import androidx.annotation.ColorInt
import com.demos.span.core.Span
import com.demos.span.core.Span.Spannable


/**
 *  author : JFZ
 *  date : 2023/8/1 17:50
 *  description : Span 的扩展
 */
fun String.backgroundColor(@ColorInt color: Int): Span.SpanBuilder =
    Span.SpanBuilder(this).backgroundColor(color)

fun String.textColor(@ColorInt color: Int): Span.SpanBuilder =
    Span.SpanBuilder(this).textColor(color)

fun String.textSize(dpSize: Int): Span.SpanBuilder = Span.SpanBuilder(this).textSize(dpSize)

fun String.underLine(): Span.SpanBuilder = Span.SpanBuilder(this).underLine()

fun String.deleteLine(): Span.SpanBuilder = Span.SpanBuilder(this).deleteLine()

fun String.quoteLine(@ColorInt color: Int, stripeWidth: Int, gapWidth: Int): Span.SpanBuilder =
    Span.SpanBuilder(this).quoteLine(color, stripeWidth, gapWidth)

fun String.textStyle(style: Int): Span.SpanBuilder = Span.SpanBuilder(this).textStyle(style)

fun String.addSpan(span: Any): Span.SpanBuilder = Span.SpanBuilder(this).addSpan(span)

fun String.click(click: Span.OnClickSpanListener): Span.SpanBuilder =
    Span.SpanBuilder(this).click(click)

fun Spannable.build(): Span.SpanBuilder = Span.SpanBuilder(this)
