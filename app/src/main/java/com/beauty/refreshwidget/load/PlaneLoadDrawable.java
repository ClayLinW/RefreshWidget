package com.beauty.refreshwidget.load;

import android.content.Context;

public class PlaneLoadDrawable extends PlaneDrawable {

    public PlaneLoadDrawable(Context context) {
        super(context);
    }

    @Override
    public void setPercent(float percent) {
        mPercent = percent;
        int centerX = getBounds().centerX();
        int bottom = getBounds().bottom;
        rectF.left = centerX - drawableMinddleWidth ;
        rectF.right = centerX + drawableMinddleWidth ;
        rectF.top = bottom;
        rectF.bottom = bottom + drawableMinddleWidth * 2;
    }
}
