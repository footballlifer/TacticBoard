package com.example.tb;


import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class MainActivity extends Activity implements View.OnTouchListener {
	private TacticBoard mTacticBoard;
	
	private ImageButton mButtonUndo;
	private ImageButton mButtonO;
	private ImageButton mButtonX;
	
	private ViewGroup mContainer;
	private ImageView mImageView;
	
	private int xDelta;
	private int yDelta;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mContainer = (ViewGroup) findViewById(R.id.container);
		mImageView = new ImageView(this);
		mImageView.setImageResource(R.drawable.ic_launcher);
		mContainer.addView(mImageView);

		
		RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mImageView
				.getLayoutParams();
		layoutParams.leftMargin = 200;
		layoutParams.topMargin = 200;
		layoutParams.rightMargin = -50;
		layoutParams.bottomMargin = -50;
		mImageView.setLayoutParams(layoutParams);
		
		
		mImageView.setOnTouchListener(this);
		
		mTacticBoard = (TacticBoard) findViewById(R.id.tb);
		ViewBar vb = new ViewBar(this, mTacticBoard);
		
		FrameLayout fl = (FrameLayout) findViewById(R.id.frame);
		fl.addView(vb);
	}

	
	public boolean onTouch(View view, MotionEvent event) {
		final int X = (int) event.getRawX();
		final int Y = (int) event.getRawY();
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			RelativeLayout.LayoutParams Params = (RelativeLayout.LayoutParams) view
					.getLayoutParams();
			xDelta = X - Params.leftMargin;
			yDelta = Y - Params.topMargin;
			break;
		case MotionEvent.ACTION_UP:
			Toast.makeText(this, "Soltamos", Toast.LENGTH_LONG).show();
			mImageView = new ImageView(this);
			mImageView.setImageResource(R.drawable.ic_launcher);
			mContainer.addView(mImageView);
			mImageView.setOnTouchListener(this);

			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			break;
		case MotionEvent.ACTION_POINTER_UP:
			break;
		case MotionEvent.ACTION_MOVE:
			RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view
					.getLayoutParams();
			layoutParams.leftMargin = X - xDelta;
			layoutParams.topMargin = Y - yDelta;
			layoutParams.rightMargin = -50;
			layoutParams.bottomMargin = -50;
			view.setLayoutParams(layoutParams);
			break;
		}
		mContainer.invalidate();
		return true;
	}	
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
