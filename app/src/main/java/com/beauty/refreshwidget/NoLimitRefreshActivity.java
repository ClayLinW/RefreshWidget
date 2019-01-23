package com.beauty.refreshwidget;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.beauty.refreshwidget.load.refresh.PullDownRefreshLayout;

import java.util.ArrayList;

public class NoLimitRefreshActivity extends AppCompatActivity
{

	private PullDownRefreshLayout prlPullSwipeRefreshLayout;
	private RecyclerView rvBottomPrompt;
	private ArrayList<String> mDatas;
	private Handler mHandler = new Handler();
	private PromptAdapter mPromptAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_no_limit_refresh);
		initView();
		initData();
	}

	private void initView()
	{
		prlPullSwipeRefreshLayout = findViewById(R.id.prl_pullSwipeRefreshLayout);
		rvBottomPrompt = findViewById(R.id.rv_bottomPrompt);
	}

	private void initData()
	{
		mDatas = new ArrayList<>();
		for(int i = 0; i < 10; i++)
		{
			mDatas.add("直到世界的尽头--原数据--" + i);
		}

		mPromptAdapter = new PromptAdapter(this, mDatas);
		rvBottomPrompt.setLayoutManager(new LinearLayoutManager(this));
		rvBottomPrompt.setAdapter(mPromptAdapter);
		rvBottomPrompt.setItemAnimator(null);

		prlPullSwipeRefreshLayout.mNoLimitBorderSlide = true;
		prlPullSwipeRefreshLayout.setOnLoadListener(new PullDownRefreshLayout.OnLoadListener()
		{
			@Override
			public void onLoad()
			{
				disposeLoad();
			}
		});
		prlPullSwipeRefreshLayout.setOnRefreshListener(new PullDownRefreshLayout.OnRefreshListener()
		{
			@Override
			public void onRefresh()
			{
				disposeRefresh();
			}
		});

		mPromptAdapter.setOnItemClickListener(new PromptAdapter.OnItemClickListener()
		{
			@Override
			public void itemClick(int position)
			{
				Toast.makeText(NoLimitRefreshActivity.this, mDatas.get(position), Toast.LENGTH_SHORT).show();
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
					mDatas.add("直到世界的尽头--下拉加载--" + i);
				}
				mPromptAdapter.notifyDataSetChanged();
				prlPullSwipeRefreshLayout.setLoading(false);
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
					list.add("直到世界的尽头--上拉刷新--" + i);
				}
				mDatas.addAll(0, list);
				mPromptAdapter.notifyDataSetChanged();
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
