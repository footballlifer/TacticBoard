package com.example.tb;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class ViewBar extends LinearLayout {
	TacticBoard mTacticBoard;
	private ImageView mUndo;
	private ImageView mO;
	private ImageView mX;
	
	public ViewBar(Context context, TacticBoard tb) {
		super(context);
		this.mTacticBoard = tb;
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.view_bar, this, true);
		
		mUndo = (ImageView) findViewById(R.id.undo);
		mO = (ImageView) findViewById(R.id.o);
		mX = (ImageView) findViewById(R.id.x);
		
		mUndo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mTacticBoard.undo();
			}
		});
		
		mO.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onViewClicked(v);
			}
		});
		
		mX.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onViewClicked(v);
			}
		});
		
	}
	
	public void onViewClicked(View V) {
		switch (V.getId()) {
		case R.id.o:
			mO.setBackgroundColor(Color.BLUE);
			mX.setBackgroundColor(Color.WHITE);
			break;
		case R.id.x:
			mX.setBackgroundColor(Color.BLUE);
			mO.setBackgroundColor(Color.WHITE);
			break;	
		}
	}
	
}