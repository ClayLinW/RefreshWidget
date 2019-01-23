package com.beauty.refreshwidget.load.refresh;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.beauty.refreshwidget.R;
import com.beauty.refreshwidget.utils.Utils;

public class LoadMoreRecyclerView extends RecyclerView
{

	private static final String TAG = "LoadMoreRecyclerView";
	private final int TYPE_LINEAR = 1;
	private final int TYPE_GRID = 2;
	private final int TYPE_STAGGERED = 3;

	private int layoutType = -1;
	private int orientation = -1;

	private LayoutManager mLayoutManager;
	private int lastVisableItemIndex;
	LoadMoreRecyclerViewAdapter mAdapter;
	private boolean isNormalPro;
	private boolean hasFooterPadding = false;

	private AdapterDataObserver mDataObserver = new AdapterDataObserver()
	{
		@Override
		public void onChanged()
		{
			super.onChanged();
			checkLoadMore();
		}

		@Override
		public void onItemRangeInserted(int positionStart, int itemCount)
		{
			super.onItemRangeInserted(positionStart, itemCount);
			checkLoadMore();
		}

		@Override
		public void onItemRangeRemoved(int positionStart, int itemCount)
		{
			super.onItemRangeRemoved(positionStart, itemCount);
			checkLoadMore();
		}
	};


	public LoadMoreRecyclerView(Context context)
	{
		super(context);
		init(context);
	}

	public LoadMoreRecyclerView(Context context, @Nullable AttributeSet attrs)
	{
		super(context, attrs);
		init(context);
	}

	public LoadMoreRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		init(context);
	}

	public void init(Context context)
	{
		addOnScrollListener(new OnScrollListener()
		{
			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy)
			{
				super.onScrolled(recyclerView, dx, dy);
				switch(layoutType)
				{
					case TYPE_LINEAR:
					{
						lastVisableItemIndex = ((LinearLayoutManager)mLayoutManager).findLastVisibleItemPosition();
						break;
					}
					case TYPE_GRID:
					{
						lastVisableItemIndex = ((GridLayoutManager)mLayoutManager).findLastVisibleItemPosition();
						break;
					}
					case TYPE_STAGGERED:
					{
						StaggeredGridLayoutManager mLayoutManager = (StaggeredGridLayoutManager)LoadMoreRecyclerView.this.mLayoutManager;
						int[] positions = new int[mLayoutManager.getSpanCount()];
						lastVisableItemIndex = mLayoutManager.findLastCompletelyVisibleItemPositions(positions)[0];
						break;
					}
					default:
						break;
				}
				if(LoadMoreRecyclerView.this.getAdapter() != null && lastVisableItemIndex >= LoadMoreRecyclerView.this.getAdapter().getItemCount() - 2)
				{
					if(!mIsLoadingMore && mHasMore && mAdapter != null)
					{
						mIsLoadingMore = true;
						mAdapter.showLoadingView();
						if(mOnLoadMoreListener != null)
						{
							mOnLoadMoreListener.loadMore();
						}
					}
				}
			}
		});
	}

	/**
	 * note：无需外部调用
	 * 检查是否要显示loading icon
	 * 用于recyclerview的item不能铺满一屏的时候不能隐藏loading的问题
	 */
	private void checkLoadMore()
	{
		postDelayed(new Runnable()
		{
			@Override
			public void run()
			{
				switch(layoutType)
				{
					case TYPE_LINEAR:
					{
						lastVisableItemIndex = ((LinearLayoutManager)mLayoutManager).findLastCompletelyVisibleItemPosition();
						break;
					}
					case TYPE_GRID:
					{
						lastVisableItemIndex = ((GridLayoutManager)mLayoutManager).findLastVisibleItemPosition();
						break;
					}
					case TYPE_STAGGERED:
					{
						StaggeredGridLayoutManager mLayoutManager = (StaggeredGridLayoutManager)LoadMoreRecyclerView.this.mLayoutManager;
						int[] positions = new int[mLayoutManager.getSpanCount()];
						lastVisableItemIndex = mLayoutManager.findLastCompletelyVisibleItemPositions(positions)[0];
						break;
					}
					default:
						break;
				}

				if(LoadMoreRecyclerView.this.getAdapter() != null && lastVisableItemIndex + 1 == LoadMoreRecyclerView.this.getAdapter().getItemCount())
				{
					if(!mIsLoadingMore && mHasMore && mOnLoadMoreListener != null)
					{
						mIsLoadingMore = true;
						mAdapter.showLoadingView();
						if(mOnLoadMoreListener != null)
						{
							mOnLoadMoreListener.loadMore();
						}
					}
				}
			}
		}, 250);

	}

	public interface OnLoadMoreListener
	{
		void loadMore();
	}

	OnLoadMoreListener mOnLoadMoreListener;

	public void setOnLoadMoreListener(OnLoadMoreListener l)
	{
		mOnLoadMoreListener = l;
	}


	@Override
	public void setAdapter(Adapter adapter)
	{

		mAdapter = new LoadMoreRecyclerViewAdapter(getContext(), adapter);
		mAdapter.setHasFooterPadding(hasFooterPadding);
		mAdapter.setNormalPro(isNormalPro);
		super.setAdapter(mAdapter);
		registerAdapterDataObserver(adapter);
	}


	/**
	 * 因为重新包装了 Adapter，所以要遍历所有的 Adapter 进行监听解注册
	 *
	 * @param adapter {@link LoadMoreRecyclerView#setAdapter(Adapter)} 原始设置进来的 Adapter
	 */
	private void unRegisterAllAdapterDataObserver(Adapter adapter)
	{
		try
		{
			if(mDataObserver != null)
			{
				if(adapter != null)
				{
					adapter.unregisterAdapterDataObserver(mDataObserver);
				}
				Adapter oldAdapter = getAdapter();
				if(oldAdapter != null)
				{
					oldAdapter.unregisterAdapterDataObserver(mDataObserver);
				}
				if(mAdapter != null)
				{
					mAdapter.unregisterAdapterDataObserver(mDataObserver);
				}
			}
		}
		catch(Exception e)
		{
			//e.printStackTrace();
		}
	}

	/**
	 * 因为包装了 Adapter，原始{{@link LoadMoreRecyclerView#setAdapter(Adapter)}
	 * 和 包装后的{@link LoadMoreRecyclerView#mAdapter} 都要设置监听
	 *
	 * @param adapter {@link LoadMoreRecyclerView#setAdapter(Adapter)} 原始设置进来的 Adapter
	 */
	private void registerAdapterDataObserver(Adapter adapter)
	{
		try
		{
			//unRegisterAllAdapterDataObserver(adapter);
			adapter.registerAdapterDataObserver(mDataObserver);
			mAdapter.registerAdapterDataObserver(mDataObserver);
		}
		catch(Exception e)
		{
			//e.printStackTrace();
		}
	}


	@Override
	public void setLayoutManager(LayoutManager layout)
	{
		super.setLayoutManager(layout);
		if(layout == null)
		{
			return;
		}
		mLayoutManager = layout;
		if(layout instanceof LinearLayoutManager)
		{
			layoutType = TYPE_LINEAR;
			LinearLayoutManager layout1 = (LinearLayoutManager)layout;
			orientation = layout1.getOrientation();
		}
		else if(layout instanceof GridLayoutManager)
		{
			layoutType = TYPE_GRID;
			final GridLayoutManager layout1 = (GridLayoutManager)layout;
			final GridLayoutManager.SpanSizeLookup spanSizeLookup = layout1.getSpanSizeLookup();
			layout1.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup()
			{
				@Override
				public int getSpanSize(int position)
				{
					if(mAdapter != null)
					{
						return mAdapter.getItemViewType(position) == TYPE_FOOTER ? layout1.getSpanCount() : spanSizeLookup.getSpanSize(position);
					}
					return 0;
				}
			});
			orientation = layout1.getOrientation();
		}
		else if(layout instanceof StaggeredGridLayoutManager)
		{
			layoutType = TYPE_STAGGERED;
			StaggeredGridLayoutManager layout1 = (StaggeredGridLayoutManager)layout;
			orientation = layout1.getOrientation();
		}
	}

	boolean mIsLoadingMore = false;
	boolean mHasMore = true;


	public void loadingMoreFinish()
	{
		mIsLoadingMore = false;
		if(mAdapter != null)
		{
			mAdapter.hideLoadingView();
		}
	}


	/**
	 * 是否允许加载更多
	 *
	 * @param hasMore
	 */
	public void setHasMore(boolean hasMore)
	{
		mHasMore = hasMore;
		if(mAdapter != null)
		{
			if(mHasMore)
			{
				mAdapter.showLoadingView();
			}
			else
			{
				mAdapter.showNoMore();
			}
		}
	}

	public void setLoadTexVISI(boolean visable)
	{
		if(mAdapter != null)
		{
			mAdapter.setLoadTexVISI(visable);
		}
	}

	/**
	 * 必须在
	 * {@link LoadMoreRecyclerView#setAdapter(Adapter)}
	 * 之后调用，否则无效
	 */
	public void setFooterTextColor(@ColorInt int color)
	{
		if(mAdapter != null)
		{
			mAdapter.setFooterTextColor(color);
		}
	}

	/**
	 * 必须在
	 * {@link LoadMoreRecyclerView#setAdapter(Adapter)}
	 * 之后调用，否则无效
	 */
	public void setFooterTextSize(float size)
	{
		if(mAdapter != null)
		{
			mAdapter.setFooterTextSize(size);
		}
	}

	/**
	 * 必须在
	 * {@link LoadMoreRecyclerView#setAdapter(Adapter)}
	 * 之后调用，否则无效
	 */
	public void setFooterTextLayoutGravity(int gravity)
	{
		if(mAdapter != null)
		{
			mAdapter.setFooterTextLayoutGravity(gravity);
		}
	}

	/**
	 * 必须在
	 * {@link LoadMoreRecyclerView#setAdapter(Adapter)}
	 * 之后调用，否则无效
	 */
	public void setFooterText(String text)
	{
		if(mAdapter != null)
		{
			mAdapter.setFooterText(text);
		}
	}

	public static final int TYPE_FOOTER = R.id.loadmore_recyclerview_footer;

	public class LoadMoreRecyclerViewAdapter extends Adapter<ViewHolder>
	{

		public Adapter mAdapter;
		private MyFooterViewHolder myFooterViewHolder;
		private boolean isNormalPro;
		private boolean hasFooterPadding = false;
		private LayoutInflater inflater;
		private String mFooterText;
		private int mFooterTextColor = Color.parseColor("#ffa1a1a1");
		private float mFooterTextSize = 14;
		private int mFooterTextLayoutGravity = Gravity.CENTER_VERTICAL;

		public LoadMoreRecyclerViewAdapter(Context context, Adapter adapter)
		{
			mAdapter = adapter;
			inflater = LayoutInflater.from(context);
		}

		@Override
		public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
		{
			if(viewType == TYPE_FOOTER)
			{
				int layoutId = orientation == LinearLayout.HORIZONTAL ? R.layout.loadmore_horizontal_footer : R.layout.loadmore_vertical_footer;
				View view = inflater.inflate(layoutId, null);
				LayoutParams p;
				if(orientation == LinearLayout.HORIZONTAL)
				{
					p = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				}
				else
				{
					p = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
					view.setPadding(0, Utils.Dp2Px(getContext(), 21), 0, Utils.Dp2Px(getContext(), 21));
				}
				view.setLayoutParams(p);
				myFooterViewHolder = new MyFooterViewHolder(view);
				myFooterViewHolder.setNormalPro(isNormalPro);
				myFooterViewHolder.setHasFooterPadding(hasFooterPadding);
				myFooterViewHolder.setFooterText(mFooterText);
				myFooterViewHolder.setFooterTextColor(mFooterTextColor);
				myFooterViewHolder.setFooterTextSize(mFooterTextSize);
				myFooterViewHolder.setFooterTextLayoutGravity(mFooterTextLayoutGravity);
				if(layoutType == TYPE_STAGGERED)
				{
					StaggeredGridLayoutManager.LayoutParams layoutParams = new StaggeredGridLayoutManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
					layoutParams.setFullSpan(true);
					myFooterViewHolder.mFooter.setLayoutParams(layoutParams);
				}
				return myFooterViewHolder;
			}
			else if(mAdapter != null)
			{
				return mAdapter.onCreateViewHolder(parent, viewType);
			}
			return null;
		}

		@Override
		public void onBindViewHolder(ViewHolder holder, int position)
		{
			int itemType = getItemViewType(position);
			if(itemType == TYPE_FOOTER)
			{
				if(layoutType == TYPE_STAGGERED)
				{
					StaggeredGridLayoutManager.LayoutParams layoutParams = new StaggeredGridLayoutManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
					layoutParams.setFullSpan(true);
					myFooterViewHolder.mFooter.setLayoutParams(layoutParams);
				}
			}
			if(itemType != TYPE_FOOTER && mAdapter != null)
			{
				mAdapter.onBindViewHolder(holder, position);
			}
		}

		@Override
		public int getItemCount()
		{
			if(mAdapter != null)
			{
				return mAdapter.getItemCount() + 1;
			}
			return 1;
		}

		@Override
		public int getItemViewType(int position)
		{
			if(position + 1 == getItemCount())
			{
				return TYPE_FOOTER;
			}
			else if(mAdapter != null)
			{
				return mAdapter.getItemViewType(position);
			}
			return -1;
		}

		public void hideLoadingView()
		{
			if(myFooterViewHolder != null)
			{
				myFooterViewHolder.hideLoadingView();
			}
		}


		public void showNoMore()
		{
			if(myFooterViewHolder != null)
			{
				myFooterViewHolder.showNoMore();
			}
		}

		public void showLoadingView()
		{
			if(myFooterViewHolder != null)
			{
				myFooterViewHolder.showLoadingView();
			}
		}

		public void setLoadTexVISI(boolean visable)
		{
			if(myFooterViewHolder != null)
			{
				if(visable)
				{
					myFooterViewHolder.setLoadTexVISI();
				}
				else
				{
					myFooterViewHolder.reMoveLoadText();
				}
			}
		}

		public void setNormalPro(boolean isNormalPro)
		{
			this.isNormalPro = isNormalPro;
			if(myFooterViewHolder != null)
			{
				myFooterViewHolder.setNormalPro(isNormalPro);
			}
		}

		public void setHasFooterPadding(boolean hasFooterPadding)
		{
			this.hasFooterPadding = hasFooterPadding;
			if(myFooterViewHolder != null)
			{
				myFooterViewHolder.setHasFooterPadding(hasFooterPadding);
			}
		}

		public void setFooterTextColor(@ColorInt int color)
		{

			mFooterTextColor = color;
		}

		public void setFooterTextSize(float size)
		{
			mFooterTextSize = size;
		}

		public void setFooterTextLayoutGravity(int gravity)
		{
			mFooterTextLayoutGravity = gravity;
		}

		public void setFooterText(String text)
		{
			mFooterText = text;
		}


		public void clean()
		{
			if(myFooterViewHolder != null)
			{
				myFooterViewHolder.clean();
			}
		}

	}

	static class MyFooterViewHolder extends ViewHolder
	{
		RelativeLayout mFooter;
		LinearLayout mFooterContainer;
		ImageView animView;
		TextView loadText;
		ProgressBar progressBar;
		AnimationDrawable animationDrawable;
		private boolean isNormalPro;
		private boolean hasFooterPadding = false;

		public MyFooterViewHolder(View itemView)
		{
			super(itemView);
			mFooter = (RelativeLayout)itemView;
			mFooterContainer = (LinearLayout)itemView.findViewById(R.id.loadmore_container);
			animView = (ImageView)itemView.findViewById(R.id.loadmore_progressBar);
			loadText = (TextView)itemView.findViewById(R.id.loadmore_text);
			progressBar = (ProgressBar)itemView.findViewById(R.id.loadmore_progress);
			animationDrawable = (AnimationDrawable)animView.getDrawable();
//            animationDrawable.start();
			if(isNormalPro)
			{
				loadText.setText(itemView.getContext().getString(R.string.loading_without_point));
				loadText.setVisibility(View.VISIBLE);
				progressBar.setVisibility(View.VISIBLE);
				animView.setVisibility(View.GONE);
			}
			else
			{
				progressBar.setVisibility(View.GONE);
				animView.setVisibility(View.VISIBLE);
				animationDrawable.start();
			}

			if(hasFooterPadding)
			{
				RelativeLayout.LayoutParams rp = new RelativeLayout.LayoutParams(AbsListView.LayoutParams.WRAP_CONTENT, Utils.Dp2Px(itemView.getContext(), 86));
				rp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
				rp.addRule(RelativeLayout.CENTER_HORIZONTAL);
				mFooterContainer.setGravity(Gravity.TOP);
				mFooterContainer.setLayoutParams(rp);

				LinearLayout.LayoutParams lParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				lParams.gravity = Gravity.TOP;
				loadText.setLayoutParams(lParams);
				loadText.setIncludeFontPadding(false);

				lParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				lParams.gravity = Gravity.TOP;
				//		lParams.topMargin = Utils.getRealPixel2(5);
				animView.setLayoutParams(lParams);
			}

		}

		public void setNormalPro(boolean isNormalPro)
		{
			this.isNormalPro = isNormalPro;
		}

		public void setHasFooterPadding(boolean hasFooterPadding)
		{
			this.hasFooterPadding = hasFooterPadding;

			if(hasFooterPadding)
			{
				RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(AbsListView.LayoutParams.WRAP_CONTENT, Utils.Dp2Px(mFooterContainer.getContext(), 86));
				params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
				params.addRule(RelativeLayout.CENTER_HORIZONTAL);
				mFooterContainer.setGravity(Gravity.TOP);
				mFooterContainer.setLayoutParams(params);

				LinearLayout.LayoutParams lParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				lParams.gravity = Gravity.TOP;
				loadText.setLayoutParams(lParams);
				loadText.setIncludeFontPadding(false);

				lParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				lParams.gravity = Gravity.TOP;
				//		lParams.topMargin = Utils.getRealPixel2(5);
				animView.setLayoutParams(lParams);
			}
		}

		private void showLoadingView()
		{
			mFooterContainer.setVisibility(View.VISIBLE);
			if(isNormalPro)
			{
				loadText.setText(mFooter.getContext().getString(R.string.loading_without_point));
				loadText.setVisibility(View.VISIBLE);
				progressBar.setVisibility(View.VISIBLE);
				animView.setVisibility(View.GONE);
			}
			else
			{
				loadText.setVisibility(View.GONE);
				progressBar.setVisibility(View.GONE);
				animView.setVisibility(View.VISIBLE);
				if(animationDrawable != null)
				{
					animationDrawable.start();
				}
			}
		}

		public void reMoveLoadText()
		{
			mFooter.setPadding(0, 0, 0, 0);
			loadText.setVisibility(View.GONE);
			loadText.setText("");
		}

		public void setLoadTexVISI()
		{
			loadText.setVisibility(View.VISIBLE);
			loadText.setText(mFooter.getContext().getString(R.string.can_not_load_more));
		}

		public void setFooterTextColor(@ColorInt int color)
		{
			loadText.setTextColor(color);
		}

		public void setFooterTextSize(float size)
		{
			loadText.setTextSize(size);
		}

		public void setFooterTextLayoutGravity(int gravity)
		{
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			layoutParams.gravity = gravity;
			loadText.setLayoutParams(layoutParams);
		}

		public void setFooterText(String text)
		{
			if(!TextUtils.isEmpty(text))
			{
				loadText.setText(text);
			}
		}

		private void hideLoadingView()
		{
			animView.clearAnimation();
			progressBar.setVisibility(View.GONE);
			animView.setVisibility(View.GONE);
			if(animationDrawable != null)
			{
				animationDrawable.stop();
			}
			mFooterContainer.setVisibility(View.INVISIBLE);
		}


		private void showNoMore()
		{
			mFooterContainer.setVisibility(View.VISIBLE);
			animView.clearAnimation();
			progressBar.setVisibility(View.GONE);
			animView.setVisibility(View.GONE);
			if(animationDrawable != null)
			{
				animationDrawable.stop();
			}
			loadText.setVisibility(View.VISIBLE);
		}

		public void clean()
		{
			if(animationDrawable != null)
			{
				animationDrawable.stop();
				animationDrawable = null;
			}
		}


	}


	public void setIsNormalPro(boolean b)
	{
		this.isNormalPro = b;
	}


	public void setFooterPadding()
	{
		this.hasFooterPadding = true;
	}


	public void clean()
	{
		if(mAdapter != null)
		{
			mAdapter.clean();
			mAdapter = null;
		}
	}

}
