package com.insomniac.pdfviewer.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

import com.insomniac.pdfviewer.PDFApp;
import com.insomniac.pdfviewer.R;

/**
 * Created by Kundan Singh on 30-04-2019.
 */
public class TextViewPlus extends androidx.appcompat.widget.AppCompatTextView {

    public TextViewPlus(Context context) {
        super(context);
    }

    public TextViewPlus(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setFont(context, attrs);
    }

    public TextViewPlus(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setFont(context, attrs);
    }


    private void setFont(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TextViewPlus);
        String customFont = a.getString(R.styleable.TextViewPlus_applyfont);
        setFont(customFont);
        a.recycle();

    }

    public boolean setFont(String asset) {
        Typeface typeface = null;
        try {
            typeface = PDFApp.pdfApp.getFont(asset);
        } catch (Exception e) {
            return false;
        }
        setTypeface(typeface);
        return true;
    }
}
