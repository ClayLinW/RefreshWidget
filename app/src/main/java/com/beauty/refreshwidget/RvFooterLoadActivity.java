package com.beauty.refreshwidget;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class RvFooterLoadActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener
{

	private SwipeRefreshLayout srlRefresh;
	private RecyclerView rvLoadMore;
	private List<String> mServiseList = new ArrayList<>();
	private int mLastVisibleItem = 0;
	private final int LOAD_COUNT = 10;
	private LinearLayoutManager mLayoutManager;
	private LoadMoreAdapter mLoadMoreAdapter;
	private Handler mHandler = new Handler();
	private boolean mIsLoadMore;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rv_footer_load);

		initView();
		initData();
	}

	private void initView()
	{
		srlRefresh = findViewById(R.id.srl_refresh);
		rvLoadMore = findViewById(R.id.rv_loadMore);
	}

	private void initData()
	{
		for(int i = 1; i <= 32; i++)
		{
			mServiseList.add("直到世界的尽头--" + i);
		}

		srlRefresh.setColorSchemeResources(R.color.coral, R.color.hotpink, R.color.yellow, R.color.tomato);
		srlRefresh.setOnRefreshListener(this);

		mLoadMoreAdapter = new LoadMoreAdapter(getServiceDatas(0, LOAD_COUNT), this, getServiceDatas(0, LOAD_COUNT).size() == LOAD_COUNT ? true : false);
		mLayoutManager = new LinearLayoutManager(this);
		rvLoadMore.setLayoutManager(mLayoutManager);
		rvLoadMore.setAdapter(mLoadMoreAdapter);
		rvLoadMore.setItemAnimator(new DefaultItemAnimator());

		rvLoadMore.addOnScrollListener(new RecyclerView.OnScrollListener()
		{
			@Override
			public void onScrollStateChanged(final RecyclerView recyclerView, int newState)
			{
				super.onScrollStateChanged(recyclerView, newState);
				if(newState == RecyclerView.SCROLL_STATE_IDLE)
				{
					if(mLastVisibleItem + 1 == mLoadMoreAdapter.getItemCount() && mLoadMoreAdapter.isHasMore() && !mIsLoadMore)
					{
						mHandler.postDelayed(new Runnable()
						{
							@Override
							public void run()
							{
								mIsLoadMore = true;
								updateLoadData(mLoadMoreAdapter.getRealLastPosition(), mLoadMoreAdapter.getRealLastPosition() + LOAD_COUNT);
								mIsLoadMore = false;
							}
						}, 1000);
					}
				}
			}

			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy)
			{
				super.onScrolled(recyclerView, dx, dy);
				mLastVisibleItem = mLayoutManager.findLastVisibleItemPosition();
			}
		});
	}


	private List<String> getServiceDatas(final int firstIndex, final int lastIndex)
	{
		List<String> getServiceList = new ArrayList<>();
		for(int i = firstIndex; i < lastIndex; i++)
		{
			if(i < mServiseList.size())
			{
				getServiceList.add(mServiseList.get(i));
			}
		}
		return getServiceList;
	}

	private void updateLoadData(int fromIndex, int toIndex)
	{
		List<String> newDatas = getServiceDatas(fromIndex, toIndex);
		if(newDatas.size() == (toIndex - fromIndex))
		{
			mLoadMoreAdapter.updateList(newDatas, true);
		}
		else
		{
			mLoadMoreAdapter.updateList(null, false);
		}
	}

	@Override
	public void onRefresh()
	{
		srlRefresh.setRefreshing(true);
		mLoadMoreAdapter.resetDatas();
		updateLoadData(0, LOAD_COUNT);
		mHandler.postDelayed(new Runnable()
		{
			@Override
			public void run()
			{
				srlRefresh.setRefreshing(false);
			}
		}, 1000);
	}


	public class LoadMoreAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
	{
		private List<String> mDatas;
		private Context mContext;
		private final int NORMAL_TYPE = 1000;
		private final int FOOT_TYPE = 1001;
		private boolean mHasMore = true;

		public LoadMoreAdapter(List<String> mDatas, Context mContext, boolean mHasMore)
		{
			this.mDatas = mDatas;
			this.mContext = mContext;
			this.mHasMore = mHasMore;
		}

		@Override
		public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
		{
			if(viewType == NORMAL_TYPE)
			{
				return new NormalHolder(LayoutInflater.from(mContext).inflate(R.layout.item_layout, parent, false));
			}
			else
			{
				return new FootHolder(LayoutInflater.from(mContext).inflate(R.layout.item_footer_load_view, parent, false));
			}
		}

		@Override
		public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position)
		{
			if(holder instanceof NormalHolder)
			{
				((NormalHolder)holder).tvNickname.setText(mDatas.get(position));
			}
			else
			{
				((FootHolder)holder).tvLoadPrompt.setVisibility(View.VISIBLE);
				if(mHasMore == true)
				{
					if(mDatas.size() > 0)
					{
						((FootHolder)holder).tvLoadPrompt.setText("正在加载更多...");
					}
				}
				else
				{
					if(mDatas.size() > 0)
					{
						((FootHolder)holder).tvLoadPrompt.setText("没有更多数据了~~");
						mHandler.postDelayed(new Runnable()
						{
							@Override
							public void run()
							{
								mHasMore = false;
							}
						}, 1000);
					}
				}
			}
		}

		@Override
		public int getItemCount()
		{
			return mDatas.size() + 1;
		}

		public int getRealLastPosition()
		{
			return mDatas.size();
		}


		public void updateList(List<String> newDatas, boolean hasMore)
		{
			if(newDatas != null)
			{
				mDatas.addAll(newDatas);
			}
			this.mHasMore = hasMore;
			notifyDataSetChanged();
		}

		class NormalHolder extends RecyclerView.ViewHolder
		{
			private TextView tvNickname;

			public NormalHolder(View itemView)
			{
				super(itemView);
				tvNickname = itemView.findViewById(R.id.tv_nickname);
			}
		}

		class FootHolder extends RecyclerView.ViewHolder
		{
			private TextView tvLoadPrompt;

			public FootHolder(View itemView)
			{
				super(itemView);
				tvLoadPrompt = itemView.findViewById(R.id.tv_loadPrompt);
			}
		}

		public boolean isHasMore()
		{
			return mHasMore;
		}

		public void resetDatas()
		{
			mDatas = new ArrayList<>();
		}

		@Override
		public int getItemViewType(int position)
		{
			if(position == getItemCount() - 1)
			{
				return FOOT_TYPE;
			}
			else
			{
				return NORMAL_TYPE;
			}
		}
	}
}
