package com.example.tb;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends Activity implements View.OnTouchListener {
	private boolean DEBUG = true;
	private String TAG = "MainActivity";
	
	private final int MAX_PLAYER = 2;
	
	private TacticBoard mTacticBoard;
	private ViewGroup mContainer;
	private ImageView mImageViewO;
	private ImageView mImageViewX;
	private EditText mEditText;
	private TextView mTextView;
	
	private int xDelta;
	private int yDelta;
	
	private boolean mMoving = true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

		mContainer = (ViewGroup) findViewById(R.id.container);
		
		for (int i = 0; i < MAX_PLAYER; i++) {
			mImageViewO = new ImageView(this);
			mImageViewO.setImageResource(R.drawable.o);
			mContainer.addView(mImageViewO);
			mImageViewO.setOnTouchListener(this);

			mImageViewX = new ImageView(this);
			mImageViewX.setImageResource(R.drawable.x);
			mContainer.addView(mImageViewX);
			mImageViewX.setOnTouchListener(this);

			RelativeLayout.LayoutParams layoutParamsX = (RelativeLayout.LayoutParams) mImageViewX
					.getLayoutParams();
			layoutParamsX.leftMargin = 70;
			layoutParamsX.topMargin = 0;
			layoutParamsX.rightMargin = 10;
			layoutParamsX.bottomMargin = 0;
			mImageViewX.setLayoutParams(layoutParamsX);
		}

		mTacticBoard = (TacticBoard) findViewById(R.id.tb);
		
		ViewBar vb = new ViewBar(this, mTacticBoard);
		FrameLayout frameBar = (FrameLayout) findViewById(R.id.frame_bar);
		frameBar.addView(vb);
	}

	public boolean onTouch(View view, MotionEvent event) {
		if (mMoving == false) return false;
		
		final int x = (int) event.getRawX();
		final int y = (int) event.getRawY();
		
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) view
					.getLayoutParams();
			xDelta = x - params.leftMargin;
			yDelta = y - params.topMargin;
			break;
		
		case MotionEvent.ACTION_UP:
			break;
		
		case MotionEvent.ACTION_POINTER_DOWN:
			break;
		
		case MotionEvent.ACTION_POINTER_UP:
			break;
		
		case MotionEvent.ACTION_MOVE:
			RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view
					.getLayoutParams();
			layoutParams.leftMargin = x - xDelta;
			layoutParams.topMargin = y - yDelta;
			layoutParams.rightMargin = -50;
			layoutParams.bottomMargin = -50;
			view.setLayoutParams(layoutParams);
			break;
		
		}
		
		mContainer.invalidate();
		return true;
	}

	public void setMoving(boolean b) {
		this.mMoving = b;
	}
	
	public void plusText() {	
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    LayoutInflater inflater = this.getLayoutInflater();

	    View dialogView = inflater.inflate(R.layout.text_dialog, null);
	    builder.setView(dialogView);
	    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
	    	@Override
	    	public void onClick(DialogInterface dialog, int id) {
	    		mTextView.setText(mEditText.getText());
	    	}
	    });	    
	    
	    mEditText = (EditText) dialogView.findViewById(R.id.text);
		mEditText.clearComposingText();
	    mTextView = new TextView(this);
	    mTextView.setOnTouchListener(this);
	    mContainer.addView(mTextView);
	    
	    RelativeLayout.LayoutParams layoutText = (RelativeLayout.LayoutParams) mTextView
				.getLayoutParams();
	    layoutText.leftMargin = 270;
	    layoutText.topMargin = 220;
	    layoutText.rightMargin = 10;
	    layoutText.bottomMargin = 0;
	    
	    //layoutText.addRule(RelativeLayout.CENTER_IN_PARENT);
	    
	    mTextView.setLayoutParams(layoutText);
	    
	    AlertDialog ad = builder.create();
	    ad.show();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
