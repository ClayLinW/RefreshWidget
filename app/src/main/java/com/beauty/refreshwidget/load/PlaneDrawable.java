package com.beauty.refreshwidget.load;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.beauty.refreshwidget.R;

import java.util.ArrayList;
import java.util.List;

public class PlaneDrawable extends RefreshDrawable implements Runnable
{

	private boolean isRunning;
	private static Handler mHandler = new Handler();

	protected int mOffset;
	protected float mPercent;
	protected int drawableMinddleWidth;
	protected int drawableMinddleHeight;
	protected List<Bitmap> bitmaps = new ArrayList<>();
	protected RectF rectF = new RectF();

	protected int exceptCount = 0;

	protected int[] mFrames; // 帧数组
	protected Bitmap mBitmap = null;
	protected BitmapFactory.Options mBitmapOptions;//Bitmap管理类，可有效减少Bitmap的OOM问题

	public PlaneDrawable(Context context)
	{
		super(context);
		getBufferBitMaps(context);
	}

	private void getBufferBitMaps(Context context)
	{
		mFrames = getData(context, R.array.match_loading_anim);
		BitmapDrawable drawable1 = (BitmapDrawable)context.getResources().getDrawable(mFrames[0]);
		drawableMinddleWidth = drawable1.getMinimumWidth() / 2;
		drawableMinddleHeight = drawable1.getMinimumHeight() / 2;
		// 当图片大小类型相同时进行复用，避免频繁GC
		if(Build.VERSION.SDK_INT >= 11 && (mBitmap == null || mBitmapOptions == null))
		{
			Bitmap bmp = drawable1.getBitmap();
			mBitmapOptions = new BitmapFactory.Options();
			//设置Bitmap内存复用
			if(Build.VERSION.SDK_INT >= 19)
			{
				int width = bmp.getWidth();
				int height = bmp.getHeight();
				Bitmap.Config config = bmp.getConfig();
				mBitmap = Bitmap.createBitmap(width, height, config);
			}
			else
			{
				mBitmap = bmp;
			}
			mBitmapOptions.inBitmap = mBitmap;//Bitmap复用内存块，类似对象池，避免不必要的内存分配和回收
			mBitmapOptions.inMutable = true;//解码时返回可变Bitmap
			mBitmapOptions.inSampleSize = 1;//缩放比例
		}
	}

	/**
	 * 从xml中读取帧数组
	 *
	 * @param resId
	 * @return
	 */
	private int[] getData(Context mContext, int resId)
	{
		TypedArray array = mContext.getResources().obtainTypedArray(resId);

		int len = array.length();
		int[] intArray = new int[array.length()];

		for(int i = 0; i < len; i++)
		{
			intArray[i] = array.getResourceId(i, 0);
		}
		array.recycle();
		return intArray;
	}

	@Override
	public void setPercent(float percent)
	{
		mPercent = percent;
		int centerX = getBounds().centerX();
		if(centerX == 0)
		{
			WindowManager wm = (WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE);
			DisplayMetrics dm = new DisplayMetrics();
			wm.getDefaultDisplay().getMetrics(dm);
			centerX = dm.widthPixels / 2;
		}
		rectF.left = centerX - drawableMinddleWidth;
		rectF.right = centerX + drawableMinddleWidth;
		rectF.top = -drawableMinddleHeight * 2;
		rectF.bottom = 0;
	}

	@Override
	public void setColorSchemeColors(int[] colorSchemeColors)
	{
	}

	@Override
	public void offsetTopAndBottom(int offset)
	{
		mOffset += offset;
		if(mOnOffsetTopAndBottomListener != null)
		{
			mOnOffsetTopAndBottomListener.offsetTopAndBottom(mOffset);
		}
		invalidateSelf();
	}

	private OnOffsetTopAndBottomListener mOnOffsetTopAndBottomListener;

	public void setmOnOffsetTopAndBottomListener(OnOffsetTopAndBottomListener mOnOffsetTopAndBottomListener)
	{
		this.mOnOffsetTopAndBottomListener = mOnOffsetTopAndBottomListener;
	}

	public interface OnOffsetTopAndBottomListener
	{
		void offsetTopAndBottom(int offset);
	}

	@Override
	public void start()
	{
		isRunning = true;
		mHandler.postDelayed(this, 50);
	}

	@Override
	public void run()
	{
		if(isRunning)
		{
			mHandler.postDelayed(this, 50);
//            System.out.println("number");
			invalidateSelf();
		}
	}

	@Override
	public void stop()
	{
		isRunning = false;
		exceptCount = 0;
		mHandler.removeCallbacks(this);
	}

	@Override
	public boolean isRunning()
	{
		return isRunning;
	}

	@Override
	public void draw(Canvas canvas)
	{
		//        int num = (int) (System.currentTimeMillis() / 50 % 11);
		//        final int saveCount = canvas.save();
		//        canvas.translate(0, mOffset);
		//        Bitmap bitmap = bitmaps.get((int) (num));
		//        canvas.drawBitmap(bitmap, null, rectF, null);
		//        canvas.restoreToCount(saveCount);
		if(mPercent == 1)
		{
			int centerX = getBounds().centerX();

			if(centerX == 0)
			{
				WindowManager wm = (WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE);
				DisplayMetrics dm = new DisplayMetrics();
				wm.getDefaultDisplay().getMetrics(dm);
				centerX = dm.widthPixels / 2;
			}


			int num = (int)(System.currentTimeMillis() / 50 % 11);
			final int saveCount = canvas.save();
			canvas.translate(0, mOffset);
			//            Bitmap bitmap = bitmaps.get(exceptCount + 42);
			Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), mFrames[exceptCount + 42], mBitmapOptions);
			canvas.drawBitmap(bitmap, null, rectF, null);
			canvas.restoreToCount(saveCount);
			exceptCount++;
			if(exceptCount > 18)
			{
				exceptCount = 0;
			}
		}
		else
		{
			int v = (int)(mPercent * 100);
			if(v < 58)
			{
				v = 58;
			}
			if(v > 99)
			{
				v = 99;
			}
			final int saveCount = canvas.save();
			canvas.translate(0, mOffset);
			//            Bitmap bitmap = bitmaps.get(v - 58);
			Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), mFrames[v - 58], mBitmapOptions);
			canvas.drawBitmap(bitmap, null, rectF, null);
			canvas.restoreToCount(saveCount);
		}
	}
}
