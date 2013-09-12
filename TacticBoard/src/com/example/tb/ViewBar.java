package com.example.tb;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class ViewBar extends LinearLayout implements View.OnClickListener {
	private boolean DEBUG = true;
	private String TAG = "ViewBar";
	
	private Context mContext;
	
	private TacticBoard mTacticBoard;
	private ImageView mUndo;
	private ImageView mSolidLine;
	private ImageView mDashLine;
	private Button mColorBtn;
	
	private ColorPaletteDialog mColorDialog;
	
	private int mColor = 0xff000000;
	private int mSize = 2;
	
	public ViewBar(Context context, TacticBoard tb) {
		super(context);
		this.mContext = context;
		this.mTacticBoard = tb;
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.view_bar, this, true);
		
		mUndo = (ImageView) findViewById(R.id.undo);
		mSolidLine = (ImageView) findViewById(R.id.solid_line);
		mDashLine = (ImageView) findViewById(R.id.dash_line);
		mColorBtn = (Button) findViewById(R.id.color);
		
		mColorDialog = new ColorPaletteDialog(mContext);
		mColorDialog.setCanceledOnTouchOutside(true);
		
		ColorPaletteDialog.sListener = new ColorPaletteDialog.OnColorSelectedListener() {
			@Override
			public void onColorSelected(int color) {
				mColor = color;
				mTacticBoard.updatePaintProperty(mColor, mSize);
			}
		};
		
		mUndo.setOnClickListener(this);
		mSolidLine.setOnClickListener(this);
		mDashLine.setOnClickListener(this);
		mColorBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.undo:
			mTacticBoard.undo();
			break;
		
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
		
		case R.id.color:
			mColorDialog.show();
			break;
		default:
			Log.e(TAG, "ERROR: switch default clided");
			break;
		}
	}
	
}