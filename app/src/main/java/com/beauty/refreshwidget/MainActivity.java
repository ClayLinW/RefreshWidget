package com.beauty.refreshwidget;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	public void pullRefreshLoadPrompt(View view)
	{
		Intent intent = new Intent(this, PullRefreshLoadPromptActivity.class);
		startActivity(intent);
	}

	public void pullDownRefresh(View view)
	{
		Intent intent = new Intent(this, PullDownRefreshActivity.class);
		startActivity(intent);
	}

	public void customLoadRefresh(View view)
	{
		Intent intent = new Intent(this, CustomLoadRefreshActivity.class);
		startActivity(intent);
	}

	public void rvFooterLoad(View view)
	{
		Intent intent = new Intent(this, RvFooterLoadActivity.class);
		startActivity(intent);
	}

	public void noLimitRefresh(View view)
	{
		Intent intent = new Intent(this, NoLimitRefreshActivity.class);
		startActivity(intent);
	}
}
