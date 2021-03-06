package nanofuntas.tb;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Stack;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import nanofuntas.tb.R;

public class TacticBoardActivity extends Activity 
implements View.OnTouchListener, View.OnLongClickListener, View.OnDragListener {
	private boolean DEBUG = true;
	private String TAG = "TacticBoardActivity";	
	enum ImgView {PLAYER_O, PLAYER_X};
	
	private PaintBoardView mPaintBoard;
	private ViewGroup mContainer;
	private ViewGroup mBoard;
	
	private final float TEXT_SIZE_SMALL = 15.0f;
	private final float TEXT_SIZE_MEDIUM = 25.0f;
	private final float TEXT_SIZE_LARGE = 35.0f;
	
	private final int REMOVE_BORDWER= 15;
	
	// following members are used in dialog
	private View mView;
	private EditText mAddingText;
	private EditText mChangingText;
	private RadioGroup mRadioGroup;
	private int mTextColor = 0xFF000000;
	
	private List<ImageView> mImgOList = new ArrayList<ImageView>();
	private List<ImageView> mImgXList = new ArrayList<ImageView>();
	private Stack<TextView> mTextStack = new Stack<TextView>();
	
	private int xDelta;
	private int yDelta;
	
	private boolean mMoving = true;
	
	// this is for global view change listener
	private TextView mTextView;
	private int mLeft;
	private int mTop;
	private int mRight;
	private int mBottom;
	
	private int mScreenWidth;
	private int mScreenHeight;
	
	private String ROOT;
	private final String APP_NAME = "Soccer_Tactic_Board";
	private final String TEMP = "TEMP";
	private final String TEMP_IMAGE_NAME = "TacticBoard.jpeg";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		ROOT = Environment.getExternalStorageDirectory().toString();
		
		SharedPreferences mSettings = getSharedPreferences("TACTIC_BOARD", 0);
		boolean firstLaunch = mSettings.getBoolean("FIRST_LAUNCH", false);
		if (firstLaunch == false) {
			SharedPreferences.Editor editor = mSettings.edit();
			editor.putBoolean("FIRST_LAUNCH", true).commit();
			Intent i = new Intent(TacticBoardActivity.this, TutorialActivity.class);
			startActivity(i);
			finish();
		}
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// remove status bar
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_tactic_board);
		
		mContainer = (ViewGroup) findViewById(R.id.container);
		mBoard = (ViewGroup) findViewById(R.id.board);
		mBoard.setOnDragListener(this);	
		
		mPaintBoard = (PaintBoardView) findViewById(R.id.tb);
		ViewBar vb = new ViewBar(this, mPaintBoard);	
		FrameLayout frameBar = (FrameLayout) findViewById(R.id.frame_bar);
		frameBar.addView(vb);
		
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		mScreenWidth = size.x;
		mScreenHeight = size.y;
	}

	@Override
	public boolean onDrag(View v, DragEvent event) {
		float x = event.getX();
		float y = event.getY();
		
		switch (event.getAction()) {
		case DragEvent.ACTION_DRAG_STARTED:
		case DragEvent.ACTION_DRAG_ENTERED:
		case DragEvent.ACTION_DRAG_EXITED:
			break;
			
		case DragEvent.ACTION_DROP:
			View view = (View) event.getLocalState();
			
			if (view.getId() == R.id.img_o)
				addImgView(ImgView.PLAYER_O, (int)x-view.getWidth()/2, (int)y-view.getHeight()/2, 0, 0);
			else if (view.getId() == R.id.img_x)
				addImgView(ImgView.PLAYER_X, (int)x-view.getWidth()/2, (int)y-view.getHeight()/2, 0, 0);
			else if (view.getId() == R.id.plus_text)
				showPlusTextDialog((int)x, (int)y, 0, 0);
			view.setVisibility(View.VISIBLE);
			break;
			
		case DragEvent.ACTION_DRAG_ENDED:
			break;
		default:
			break;
		}
		return true;
	}
	
	private void addImgView (ImgView imgV, int l, int t, int r, int b) {
		ImageView iv = new ImageView(this);
		
		switch (imgV) {
		case PLAYER_O:
			iv.setImageResource(R.drawable.ic_circle_blue);
			mImgOList.add(iv);
			break;
		
		case PLAYER_X:
			iv.setImageResource(R.drawable.ic_circle_red);
			mImgOList.add(iv);
			break;
		}
		
		iv.setOnTouchListener(this);
		mBoard.addView(iv);
		setViewRelativeParams(iv, l, t, r, b);
	}
	
	public boolean onTouch(View view, MotionEvent event) {
		if (Config.useMoveIcon == true) {
			if (mMoving == false) return false;
		}
		
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
			if ( (x < REMOVE_BORDWER) || (x > mScreenWidth - REMOVE_BORDWER)
			|| (y < REMOVE_BORDWER) || (y > mScreenHeight - REMOVE_BORDWER) ) 
				view.setVisibility(View.INVISIBLE);
			break;
			
		case MotionEvent.ACTION_POINTER_DOWN:		
		case MotionEvent.ACTION_POINTER_UP:
			break;
			
		}
		
		mContainer.invalidate();
		return true;
	}

	@Override
	public boolean onLongClick(View view) {		
		// when mMoving is false, onLongClick will be activated
		mView = view;
		LayoutInflater inflater = this.getLayoutInflater();
	    View editTextDialog = inflater.inflate(R.layout.edit_text_dialog, null);
	    mChangingText = (EditText) editTextDialog.findViewById(R.id.changing_text);
	    mChangingText.setTextColor(mTextColor);
		mChangingText.setText( ((TextView)mView).getText().toString() );

	    AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    builder.setTitle("Edit Text");
	    builder.setView(editTextDialog);
	    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
	    	@Override
	    	public void onClick(DialogInterface dialog, int id) {
	    		mChangingText.clearComposingText();
	    		((TextView)mView).setText(mChangingText.getText().toString());
	    	}
	    });
	    
	    builder.setNegativeButton("DELETE", new DialogInterface.OnClickListener() {
	    	@Override
	    	public void onClick(DialogInterface dialog, int id) {
				mTextStack.remove((TextView)mView);
	    		((RelativeLayout)mView.getParent()).removeView(mView);
	    	}
	    });
	    
	    AlertDialog ad = builder.create();
	    ad.show();
		return false;
	}
	
	public void showPlusTextDialog(int l, int t, int r, int b) {	
		mLeft = l;
		mTop = t;
		mRight = r;
		mBottom = b;
		
		LayoutInflater inflater = this.getLayoutInflater();
	    View textDialog = inflater.inflate(R.layout.text_dialog, null);
	    mAddingText = (EditText) textDialog.findViewById(R.id.adding_text);
	    mAddingText.setTextColor(mTextColor);	    
	    
	    mRadioGroup = (RadioGroup) textDialog.findViewById(R.id.radio_text);	
	    
	    ((RadioButton) textDialog.findViewById(R.id.radio_small)).setTextSize(TEXT_SIZE_SMALL);
	    ((RadioButton) textDialog.findViewById(R.id.radio_medium)).setTextSize(TEXT_SIZE_MEDIUM);
	    ((RadioButton) textDialog.findViewById(R.id.radio_large)).setTextSize(TEXT_SIZE_LARGE);

	    AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    builder.setTitle("Add Text");
	    builder.setView(textDialog);
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
	    		mAddingText.clearComposingText();
	    		addText(mAddingText.getText().toString(), size,
	    				mLeft, mTop, mRight, mBottom);
	    	}
	    });
	    
	    AlertDialog ad = builder.create();
	    ad.show();
	    ad.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
	}
	
	private void addText(String txt, float size, int l, int t, int r, int b) {
		mLeft = l;
		mTop = t;
		mRight = r;
		mBottom = b;
		
		mTextView = new TextView(this);
		mTextView.setOnTouchListener(this);
		mTextView.setOnLongClickListener(this);
	    mTextStack.push(mTextView);
	    mBoard.addView(mTextView);
	    // TODO: setText Invisible first 
	    mTextView.setTextSize(size);
	    mTextView.setTextColor(mTextColor);
	    mTextView.setText(txt);
		
		ViewTreeObserver vto = mTextView.getViewTreeObserver();
		vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
			@Override
			public void onGlobalLayout() {
				setViewRelativeParams(mTextView,
						mLeft-mTextView.getWidth()/2, mTop-mTextView.getHeight()/2, mRight, mBottom);
				mTextView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
			}
		});
	}
	
	public void saveImgToGallery() {
		Bitmap b = takeScreenShot();
		saveBitmap(b);
	}
	
	private void saveTempImg() {
		Bitmap b = takeScreenShot();
		saveTempBitmap(b);
	}
	
	private Bitmap takeScreenShot() {
		View rootView = findViewById(R.id.board);
		rootView.setDrawingCacheEnabled(true);
		Bitmap result = Bitmap.createBitmap(rootView.getDrawingCache());
		rootView.setDrawingCacheEnabled(false);
		return result;
	}

	private void saveBitmap(Bitmap bitmap) {
		File imageDir = new File(ROOT + "/" + APP_NAME);
    	if(!imageDir.exists())
    		imageDir.mkdirs();
    	
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        Date now = new Date();
        String strDate = sdf.format(now);
    	    	
    	String imagePath = ROOT + "/" + APP_NAME + "/" + strDate + ".jpeg";
    	File image = new File(imagePath);
		
    	try {
			FileOutputStream fos = new FileOutputStream(image);
			bitmap.compress(CompressFormat.JPEG, 100, fos);
			fos.flush();
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
		MediaStore.Images.Media.insertImage(
				this.getContentResolver(), bitmap, "STB", "STB Picture");
		Toast.makeText(getApplication(), R.string.save_image, Toast.LENGTH_SHORT).show();
	}
	
	private void saveTempBitmap(Bitmap bitmap) {
		File imageDir = new File(ROOT + "/" + APP_NAME + "/" + TEMP);
    	if(!imageDir.exists())
    		imageDir.mkdirs();
    	
		File imagePath = new File(imageDir + "/" + TEMP_IMAGE_NAME);
		try {
			FileOutputStream fos = new FileOutputStream(imagePath);
			bitmap.compress(CompressFormat.JPEG, 100, fos);
			fos.flush();
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		MediaStore.Images.Media.insertImage(this.getContentResolver(), bitmap, "STB", "STB Picture");
	}
	
	public void reset() {
		while(!mTextStack.isEmpty()) {
			TextView tv = (TextView) mTextStack.pop();
			((RelativeLayout) tv.getParent()).removeView(tv);
		}
		
		mPaintBoard.resetPaintBoard();
		
		for (ImageView iv : mImgOList) 
			((RelativeLayout) iv.getParent()).removeView(iv);	

		for (ImageView iv : mImgXList)
			((RelativeLayout) iv.getParent()).removeView(iv);
		
		mImgOList.clear();	
		mImgXList.clear();
	}
	
	public void share() {
		saveTempImg();
		Intent share = new Intent(Intent.ACTION_SEND);

		//share.setType("text/plain");
		//share.putExtra(Intent.EXTRA_TEXT, "Share via Tactic Board");
		share.setType("image/jpeg");
		
		final String imageDir = ROOT + "/" + APP_NAME + "/" + TEMP;
    	final String fileName = imageDir + "/" + TEMP_IMAGE_NAME;
		
		share.putExtra(Intent.EXTRA_STREAM, Uri.parse(fileName));
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
