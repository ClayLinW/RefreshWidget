package com.beauty.refreshwidget;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.widget.Toast;

import com.beauty.refreshwidget.load.refresh.LoadMoreRecyclerView;
import com.beauty.refreshwidget.load.refresh.PullRefreshLoadPromptLayout;

import java.util.ArrayList;

public class CustomLoadRefreshActivity extends AppCompatActivity
{

	private PullRefreshLoadPromptLayout prlPullSwipeRefreshLayout;
	private LoadMoreRecyclerView rvLoadMore;
	private ArrayList<String> mDatas;
	private Handler mHandler = new Handler();
	private PromptAdapter mPromptAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_custom_load_refresh);
		initView();
		initData();
	}

	private void initView()
	{
		prlPullSwipeRefreshLayout = findViewById(R.id.prl_pullSwipeRefreshLayout);
		rvLoadMore = findViewById(R.id.rv_loadMore);
	}

	private void initData()
	{
		mDatas = new ArrayList<>();
		for(int i = 0; i < 10; i++)
		{
			mDatas.add("直到世界的尽头--原数据--" + i);
		}
		mPromptAdapter = new PromptAdapter(this, mDatas);
		rvLoadMore.setLayoutManager(new LinearLayoutManager(this));
		rvLoadMore.setAdapter(mPromptAdapter);
		rvLoadMore.setItemAnimator(null);
		prlPullSwipeRefreshLayout.setOnRefreshListener(new PullRefreshLoadPromptLayout.OnRefreshListener()
		{
			@Override
			public void onRefresh()
			{
				disposeRefresh();
			}
		});

		rvLoadMore.setOnLoadMoreListener(new LoadMoreRecyclerView.OnLoadMoreListener()
		{
			@Override
			public void loadMore()
			{
				disposeLoad();
			}
		});

		mPromptAdapter.setOnItemClickListener(new PromptAdapter.OnItemClickListener()
		{
			@Override
			public void itemClick(int position)
			{
				Toast.makeText(CustomLoadRefreshActivity.this, mDatas.get(position), Toast.LENGTH_SHORT).show();
			}
		});
	}

	private void disposeLoad()
	{
		mHandler.postDelayed(new Runnable()
		{
			@Override
			public void run()
			{
				int size = mDatas.size();
				for(int i = size; i < size + 3; i++)
				{
					mDatas.add("直到世界的尽头--上拉加载--" + i);
				}
				rvLoadMore.getAdapter().notifyDataSetChanged();
				rvLoadMore.loadingMoreFinish();
				if(mDatas.size() > 16)
				{
					rvLoadMore.setHasMore(false);
//					rvLoadMore.setLoadTexVISI(false);
				}
				else
				{
					rvLoadMore.setHasMore(true);
				}
			}
		}, 2000);
	}

	private void disposeRefresh()
	{
		mHandler.postDelayed(new Runnable()
		{
			@Override
			public void run()
			{
				ArrayList<String> list = new ArrayList<>();
				int size = mDatas.size();
				for(int i = size; i < size + 3; i++)
				{
					list.add("直到世界的尽头--下拉刷新--" + i);
				}
				mDatas.addAll(0, list);
				rvLoadMore.getAdapter().notifyDataSetChanged();
//				mPromptAdapter.notifyDataSetChanged();
				prlPullSwipeRefreshLayout.setRefreshing(false);
			}
		}, 2000);
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		prlPullSwipeRefreshLayout.setDestroyHandler();
	}
}
