package com.beauty.refreshwidget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by zlw on 2019/1/9.
 */
public class PromptAdapter extends RecyclerView.Adapter<PromptAdapter.PromptViewHolder>
{
	private Context mContext;
	private ArrayList<String> mDatas;
	private final LayoutInflater mLayoutInflater;

	public PromptAdapter(Context context, ArrayList<String> mDatas)
	{
		this.mContext = context;
		this.mDatas = mDatas;
		mLayoutInflater = LayoutInflater.from(context);
	}

	@NonNull
	@Override
	public PromptViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
	{
		View view = mLayoutInflater.inflate(R.layout.item_layout, viewGroup, false);
		PromptViewHolder promptViewHolder = new PromptViewHolder(view);
		return promptViewHolder;
	}

	@Override
	public void onBindViewHolder(@NonNull PromptViewHolder promptViewHolder, final int position)
	{
		promptViewHolder.tvNickname.setText(mDatas.get(position));
		promptViewHolder.itemView.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if(mOnItemClickListener != null)
				{
					mOnItemClickListener.itemClick(position);
				}
			}
		});
	}

	@Override
	public int getItemCount()
	{
		return mDatas.size();
	}

	class PromptViewHolder extends RecyclerView.ViewHolder
	{
		TextView tvNickname;

		public PromptViewHolder(@NonNull View itemView)
		{
			super(itemView);
			tvNickname = itemView.findViewById(R.id.tv_nickname);
		}
	}

	private OnItemClickListener mOnItemClickListener;

	public void setOnItemClickListener(OnItemClickListener mOnItemClickListener)
	{
		this.mOnItemClickListener = mOnItemClickListener;
	}

	public interface OnItemClickListener
	{
		void itemClick(int position);
	}
}
