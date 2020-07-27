package com.insomniac.pdfviewer.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;


/**
 * Created by Kundan Singh on 25-03-2019.
 */
public class SquareRelativeLayoutHeight extends RelativeLayout {
    public SquareRelativeLayoutHeight(Context context) {
        super(context);
    }

    public SquareRelativeLayoutHeight(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareRelativeLayoutHeight(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public SquareRelativeLayoutHeight(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(heightMeasureSpec, heightMeasureSpec);
    }
}
