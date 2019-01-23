package com.beauty.refreshwidget.utils;

import android.content.Context;

/**
 * Created by zlw on 2019/1/10.
 */
public class Utils
{
	public static int Dp2Px(Context context, float dp)
	{
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int)(dp * scale + 0.5f);
	}

	public static int Px2Dp(Context context, float px)
	{
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int)(px / scale + 0.5f);
	}
}
