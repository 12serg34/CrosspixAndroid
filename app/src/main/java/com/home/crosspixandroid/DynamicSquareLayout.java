package com.home.crosspixandroid;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class DynamicSquareLayout extends RelativeLayout {

    public DynamicSquareLayout(Context context) {
        super(context);
    }

    public DynamicSquareLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DynamicSquareLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public DynamicSquareLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
        int size = MeasureSpec.getSize(widthMeasureSpec);
        setMeasuredDimension(size, size);
    }
}
