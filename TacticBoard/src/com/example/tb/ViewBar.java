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
	private ImageView mSolidLine;
	private ImageView mDashLine;
	
	public ViewBar(Context context, TacticBoard tb) {
		super(context);
		this.mTacticBoard = tb;
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.view_bar, this, true);
		
		mUndo = (ImageView) findViewById(R.id.undo);
		mSolidLine = (ImageView) findViewById(R.id.solid_line);
		mDashLine = (ImageView) findViewById(R.id.dash_line);
		
		mUndo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mTacticBoard.undo();
			}
		});
		
		mSolidLine.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onViewClicked(v);
			}
		});
		
		mDashLine.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onViewClicked(v);
			}
		});
		
	}
	
	public void onViewClicked(View V) {
		switch (V.getId()) {
		case R.id.solid_line:
			mTacticBoard.setDefaultPaint();
			mSolidLine.setBackgroundColor(Color.BLUE);
			mDashLine.setBackgroundColor(Color.WHITE);
			break;
		case R.id.dash_line:
			mTacticBoard.setDashPaint();
			mDashLine.setBackgroundColor(Color.BLUE);
			mSolidLine.setBackgroundColor(Color.WHITE);
			break;
		}
	}	
}