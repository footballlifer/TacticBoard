package com.example.tb;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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
	private ViewGroup mBoard;
	private ImageView mImageViewO;
	private ImageView mImageViewX;
	
	private EditText mEditText;
	private RadioGroup mRadioGroup;
	private int mTextColor = 0xFF000000;
	
	private List<ImageView> mImgOList = new ArrayList<ImageView>();
	private List<ImageView> mImgXList = new ArrayList<ImageView>();
	private Stack<TextView> mTextStack = new Stack<TextView>();
	
	private int xDelta;
	private int yDelta;
	
	private boolean mMoving = true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		
		mContainer = (ViewGroup) findViewById(R.id.container);
		mBoard = (ViewGroup) findViewById(R.id.board);
		
		for (int i = 0; i < MAX_PLAYER; i++) {
			mImageViewO = new ImageView(this);
			mImageViewO.setImageResource(R.drawable.o);
			mImageViewO.setOnTouchListener(this);
			mBoard.addView(mImageViewO);
			mImgOList.add(mImageViewO);

			mImageViewX = new ImageView(this);
			mImageViewX.setImageResource(R.drawable.x);
			mImageViewX.setOnTouchListener(this);
			mBoard.addView(mImageViewX);
			mImgXList.add(mImageViewX);

			setViewRelativeParams(mImageViewX, 70, 0, 0, 0);
		}

		mTacticBoard = (TacticBoard) findViewById(R.id.tb);
		
		FrameLayout frameBar = (FrameLayout) findViewById(R.id.frame_bar);
		ViewBar vb = new ViewBar(this, mTacticBoard);
		frameBar.addView(vb);
	}

	public boolean onTouch(View view, MotionEvent event) {
		if (mMoving == false) return false;
		
		final int x = (int) event.getRawX();
		final int y = (int) event.getRawY();
		
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		
		case MotionEvent.ACTION_DOWN:
			RelativeLayout.LayoutParams params = 
					(RelativeLayout.LayoutParams) view.getLayoutParams();
			xDelta = x - params.leftMargin;
			yDelta = y - params.topMargin;
			break;
		
		case MotionEvent.ACTION_MOVE:
			setViewRelativeParams(view, x-xDelta, y-yDelta, -50, -50);
			break;
			
		case MotionEvent.ACTION_UP:		
		case MotionEvent.ACTION_POINTER_DOWN:		
		case MotionEvent.ACTION_POINTER_UP:
			break;
			
		}
		
		mContainer.invalidate();
		return true;
	}

	public void showPlusTextDialog() {	
		LayoutInflater inflater = this.getLayoutInflater();
	    View view = inflater.inflate(R.layout.text_dialog, null);
	    mEditText = (EditText) view.findViewById(R.id.text);
	    mEditText.setTextColor(mTextColor);
	    mRadioGroup = (RadioGroup) view.findViewById(R.id.radio_text);	
	    
	    ((RadioButton) view.findViewById(R.id.radio_small)).setTextSize(TEXT_SIZE_SMALL);
	    ((RadioButton) view.findViewById(R.id.radio_medium)).setTextSize(TEXT_SIZE_MEDIUM);
	    ((RadioButton) view.findViewById(R.id.radio_large)).setTextSize(TEXT_SIZE_LARGE);

	    
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
	    		mEditText.clearComposingText();
	    		addText(mEditText.getText().toString(), size);
	    	}
	    });
	    
	    AlertDialog ad = builder.create();
	    ad.show();
	}
	
	private void addText(String txt, float size) {
		TextView tv = new TextView(this);
		tv.setOnTouchListener(this);
	    mTextStack.push(tv);
	    mBoard.addView(tv);
	     
	    setViewRelativeParams(tv, 270, 220, 0, 0);
	    tv.setTextSize(size);
	    tv.setTextColor(mTextColor);
		tv.setText(txt);
	}
	
	public void saveImgToGallery() {
		Bitmap b = takeScreenShot();
		saveBitmap(b);
	}
	
	private Bitmap takeScreenShot() {
		View rootView = findViewById(R.id.board);
		rootView.setDrawingCacheEnabled(true);
		Bitmap result = Bitmap.createBitmap(rootView.getDrawingCache());
		rootView.setDrawingCacheEnabled(false);
		return result;
	}

	private void saveBitmap(Bitmap bitmap) {
		File imagePath = new File(Environment.getExternalStorageDirectory()
				+ "/TacticBoard.png");
		try {
			FileOutputStream fos = new FileOutputStream(imagePath);
			bitmap.compress(CompressFormat.PNG, 100, fos);
			fos.flush();
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		MediaStore.Images.Media.insertImage(this.getContentResolver(), bitmap, "kakpple", "kakpple pic");
	}
	
	public void reset() {
		while(!mTextStack.isEmpty()) {
			TextView tv = (TextView) mTextStack.pop();
			((RelativeLayout) tv.getParent()).removeView(tv);
		}
		
		//this.mTextColor = 0xFF000000;
		
		mTacticBoard.resetTacticBoard();
		
		for (ImageView iv : mImgOList)
			setViewRelativeParams(iv, 0, 0, 0, 0);
		
		for (ImageView iv : mImgXList) 
			setViewRelativeParams(iv, 70, 0, 0, 0);
	}
	
	public void share() {
		saveImgToGallery();
		Intent share = new Intent(Intent.ACTION_SEND);

		//share.setType("text/plain");
		//share.putExtra(Intent.EXTRA_TEXT, "Share via Tactic Board");
		share.setType("image/jpeg");
		share.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///sdcard/TacticBoard.png"));
		
		startActivity(Intent.createChooser(share, "Share Image"));
	}
	
	private void setViewRelativeParams(View v, int l, int t, int r, int b) {
		RelativeLayout.LayoutParams p = 
				(RelativeLayout.LayoutParams) v.getLayoutParams();
		p.leftMargin = l;
		p.topMargin = t;
		p.rightMargin = r;
		p.bottomMargin = b;
		
		v.setLayoutParams(p);	
	}
	
	public void setMoving(boolean b) {
		this.mMoving = b;
	}
	
	public void setTextColor(int color) {
		this.mTextColor = color;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
