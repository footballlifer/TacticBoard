package com.example.tb;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class ViewBar extends LinearLayout implements View.OnClickListener {
	private boolean DEBUG = true;
	private String TAG = "ViewBar";
	
	private Context mContext;
	
	private TacticBoard mTacticBoard;
	private ImageView mUndo;
	private ImageView mMove;
	private ImageView mSolidLine;
	private ImageView mShortDashLine;
	private ImageView mLongDashLine;
	private ImageView mColorSetting;
	private ImageView mText;
	
	private boolean mMoving = true;
	
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
		mMove = (ImageView) findViewById(R.id.move);
		mSolidLine = (ImageView) findViewById(R.id.solid_line);
		mShortDashLine = (ImageView) findViewById(R.id.short_dash_line);
		mLongDashLine = (ImageView) findViewById(R.id.long_dash_line);
		mColorSetting = (ImageView) findViewById(R.id.color_setting);
		mText = (ImageView) findViewById(R.id.plus_text);
		
		mUndo.setOnClickListener(this);
		mMove.setOnClickListener(this);		
		mSolidLine.setOnClickListener(this);
		mShortDashLine.setOnClickListener(this);
		mLongDashLine.setOnClickListener(this);
		mColorSetting.setOnClickListener(this);
		mText.setOnClickListener(this);
		
		mColorSetting.setBackgroundColor(Color.BLACK);
		((MainActivity) mContext).setMoving(mMoving);
		mMove.setBackgroundColor(Color.LTGRAY);
		
		mColorDialog = new ColorPaletteDialog(mContext);
		mColorDialog.setCanceledOnTouchOutside(true);

		ColorPaletteDialog.sListener = new ColorPaletteDialog.OnColorSelectedListener() {
			@Override
			public void onColorSelected(int color) {
				mColor = color;
				mTacticBoard.updatePaintProperty(mColor, mSize);
				mColorSetting.setBackgroundColor(mColor);
			}
		};
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.undo:
			mTacticBoard.undo();
			break;
		
		case R.id.solid_line:
			mTacticBoard.setDefaultPaint();
			mSolidLine.setBackgroundColor(Color.LTGRAY);
			mShortDashLine.setBackgroundColor(Color.WHITE);
			mLongDashLine.setBackgroundColor(Color.WHITE);
			break;
		
		case R.id.move:
			if (mMoving) {
				mMove.setBackgroundColor(Color.WHITE);
				mMoving = false;
			} else {
				mMove.setBackgroundColor(Color.LTGRAY);
				mMoving = true;
			}
			
			((MainActivity) mContext).setMoving(mMoving);
			break;
			
		case R.id.short_dash_line:
			mTacticBoard.setShortDashPaint();
			mSolidLine.setBackgroundColor(Color.WHITE);
			mShortDashLine.setBackgroundColor(Color.LTGRAY);
			mLongDashLine.setBackgroundColor(Color.WHITE);
			break;
			
		case R.id.long_dash_line:
			mTacticBoard.setLongDashPaint();
			mSolidLine.setBackgroundColor(Color.WHITE);
			mShortDashLine.setBackgroundColor(Color.WHITE);
			mLongDashLine.setBackgroundColor(Color.LTGRAY);
			break;	
		
		case R.id.color_setting:
			mColorDialog.show();
			break;
		
		case R.id.plus_text:
			((MainActivity) mContext).plusText();
			
			/*
			AlertDialog.Builder builder = new AlertDialog.Builder(
	                mContext);
	        builder.setCancelable(true);
	        builder.setTitle("Title");
	        
	        builder.setInverseBackgroundForced(true);
	        builder.setPositiveButton("Yes",
	                new DialogInterface.OnClickListener() {
	                    @Override
	                    public void onClick(DialogInterface dialog,
	                            int which) {
	                        dialog.dismiss();
	                    }
	                });
	        builder.setNegativeButton("No",
	                new DialogInterface.OnClickListener() {
	                    @Override
	                    public void onClick(DialogInterface dialog,
	                            int which) {
	                        dialog.dismiss();
	                    }
	                });
	        AlertDialog alert = builder.create();
	        alert.show();
			
			*/
			
			
			
			break;	
		
		default:
			Log.e(TAG, "ERROR: switch default clided");
			break;
		}
	}
	
}