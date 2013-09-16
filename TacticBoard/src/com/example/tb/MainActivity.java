package com.example.tb;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends Activity implements View.OnTouchListener {
	private boolean DEBUG = true;
	private String TAG = "MainActivity";
	
	private final int MAX_PLAYER = 2;
	private final float TEXT_SIZE_SMALL = 15.0f;
	private final float TEXT_SIZE_MEDIUM = 25.0f;
	private final float TEXT_SIZE_LARGE = 35.0f;
	
	private TacticBoard mTacticBoard;
	private ViewGroup mContainer;
	private ImageView mImageViewO;
	private ImageView mImageViewX;
	private EditText mEditText;
	private TextView mTextView;
	private RadioGroup mRadioGroup;
	
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
	
	public void showPlusTextDialog() {	
		LayoutInflater inflater = this.getLayoutInflater();
	    View view = inflater.inflate(R.layout.text_dialog, null);
	    mEditText = (EditText) view.findViewById(R.id.text);
	    mRadioGroup = (RadioGroup) view.findViewById(R.id.radio_text);	
	    
	    ((RadioButton) view.findViewById(R.id.radio_small)).setTextSize(TEXT_SIZE_SMALL);
	    ((RadioButton) view.findViewById(R.id.radio_medium)).setTextSize(TEXT_SIZE_MEDIUM);
	    ((RadioButton) view.findViewById(R.id.radio_large)).setTextSize(TEXT_SIZE_LARGE);

	    mTextView = new TextView(this);
	    mTextView.setOnTouchListener(this);
	    mContainer.addView(mTextView);
	    
	    RelativeLayout.LayoutParams layoutText = (RelativeLayout.LayoutParams) mTextView
				.getLayoutParams();
	    layoutText.leftMargin = 270;
	    layoutText.topMargin = 220;
	    layoutText.rightMargin = 10;
	    layoutText.bottomMargin = 0;
	    	    
	    mTextView.setLayoutParams(layoutText);
	    
	    AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    builder.setTitle("Add Text");
	    builder.setView(view);
	    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
	    	@Override
	    	public void onClick(DialogInterface dialog, int id) {
	    		int selectedId = mRadioGroup.getCheckedRadioButtonId();
	    		float size = TEXT_SIZE_MEDIUM;
	    		switch(selectedId) {
	    		case R.id.radio_small:
	    			size = TEXT_SIZE_SMALL;
	    			break;
	    			
	    		case R.id.radio_medium:
	    			size = TEXT_SIZE_MEDIUM;
	    			break;
	    		
	    		case R.id.radio_large:
	    			size = TEXT_SIZE_LARGE;
	    			break;
	    			
	    		default:
	    			size = TEXT_SIZE_MEDIUM;
	    			Log.e(TAG, "Error: default text size selected");
	    			break;
	    		}
	    		mTextView.setTextSize(size);
	    		mEditText.clearComposingText();
	    		mTextView.setText(mEditText.getText());
	    	}
	    });
	    
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
