package nanofuntas.tb;

import com.example.tb.R;

import android.content.ClipData;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

public class ViewBar extends LinearLayout 
implements View.OnClickListener, View.OnTouchListener {
	private boolean DEBUG = true;
	private String TAG = "ViewBar";
	
	private Context mContext;
	private PaintBoardView mPaintBoard;
	private ColorPaletteDialog mColorDialog;

	private ImageView mO;
	private ImageView mX;
	private ImageView mUndo;
	private ImageView mMove;
	private ImageView mSolidLine;
	private ImageView mShortDashLine;
	private ImageView mLongDashLine;
	private ImageView mPlusText;
	private ImageView mPencil;
	private ImageView mCrookedLine;
	
	//Submenu list items
	private ImageView mList;
	private View mViewNew;
	private View mViewSave;
	private View mViewShare;
	private View mViewColorSetting;
	private ImageView mImageViewColor;
	private PopupWindow mPopupWindow;
	
	private boolean mMoving = true;
		
	private int mColor;
	private int mWhite;
	private int mLightGrey;
	
	public ViewBar(Context context, PaintBoardView pb) {
		super(context);
		this.mContext = context;
		this.mPaintBoard = pb;
		
		initView(context);
		
		mColor = getResources().getColor(R.color.Black);
		mWhite = getResources().getColor(R.color.White);
		mLightGrey = getResources().getColor(R.color.LightGrey);
		
		// by default, view moving is enabled, drawing is not
		((TacticBoardActivity) mContext).setMoving(mMoving);
		mPaintBoard.setDrawing(false);
		mMove.setBackgroundColor(mLightGrey);
		
		mColorDialog = new ColorPaletteDialog(mContext);
		mColorDialog.setCanceledOnTouchOutside(true);

		ColorPaletteDialog.sListener = new ColorPaletteDialog.OnColorSelectedListener() {
			@Override
			public void onColorSelected(int color) {
				mColor = color;
				((TacticBoardActivity) mContext).setTextColor(mColor);
				mPaintBoard.updatePaintColor(mColor);
			}
		};
		
		if (Config.useMoveIcon == false) {
			((LinearLayout)mMove.getParent()).removeView(mMove);
		}
	}

	private void initView(Context context) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.view_bar, this, true);
		
		mO = (ImageView) findViewById(R.id.img_o);
		mX = (ImageView) findViewById(R.id.img_x);
		mPlusText = (ImageView) findViewById(R.id.plus_text);
		mUndo = (ImageView) findViewById(R.id.undo);
		mMove = (ImageView) findViewById(R.id.move);
		mSolidLine = (ImageView) findViewById(R.id.solid_line);
		mShortDashLine = (ImageView) findViewById(R.id.short_dash_line);
		mLongDashLine = (ImageView) findViewById(R.id.long_dash_line);
		mList = (ImageView) findViewById(R.id.list);
		mPencil = (ImageView) findViewById(R.id.pencil);
		mCrookedLine = (ImageView) findViewById(R.id.crooked_line);
		
		mO.setOnTouchListener(this);
		mX.setOnTouchListener(this);		
		mPlusText.setOnTouchListener(this);	
		mUndo.setOnClickListener(this);
		mMove.setOnClickListener(this);		
		mSolidLine.setOnClickListener(this);
		mShortDashLine.setOnClickListener(this);
		mLongDashLine.setOnClickListener(this);
		mList.setOnClickListener(this);
		mPencil.setOnClickListener(this);
		mCrookedLine.setOnClickListener(this);
	}
	
	private void popupSubmenu() {
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View popupView = inflater.inflate(R.layout.submenu_list, null);
		
		mViewNew = (View) popupView.findViewById(R.id.new_file);
		mViewSave = (View) popupView.findViewById(R.id.save);
		mViewShare = (View) popupView.findViewById(R.id.share);
		mImageViewColor = (ImageView) popupView.findViewById(R.id.color_setting);
		mViewColorSetting = (View) popupView.findViewById(R.id.color);
		
		mViewNew.setOnClickListener(this);
		mViewSave.setOnClickListener(this);
		mViewShare.setOnClickListener(this);
		mViewColorSetting.setOnClickListener(this);

		mImageViewColor.setBackgroundColor(mColor);
		
		mPopupWindow = new PopupWindow(popupView, 
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		mPopupWindow.setOutsideTouchable(true);
		// dismiss popup window while touching outside of popup
		mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
		mPopupWindow.setFocusable(true);
		
		mPopupWindow.showAsDropDown(mList);
	}
	
		
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		
		case R.id.undo:
			mPaintBoard.undo();
			break;
		
		case R.id.move:
			setDrawingDefault();
			mMove.setBackgroundColor(mLightGrey);	
			((TacticBoardActivity) mContext).setMoving(true);
			mPaintBoard.setDrawing(false);
			break;
		
		case R.id.solid_line:
			setDrawingDefault();
			mPaintBoard.setSolidLinePaint();
			mSolidLine.setBackgroundColor(mLightGrey);
			break;
			
		case R.id.short_dash_line:
			setDrawingDefault();
			mPaintBoard.setShortDashPaint();
			mShortDashLine.setBackgroundColor(mLightGrey);
			break;
			
		case R.id.long_dash_line:
			setDrawingDefault();
			mPaintBoard.setLongDashPaint();
			mLongDashLine.setBackgroundColor(mLightGrey);
			break;	
		
		case R.id.pencil:
			setDrawingDefault();
			mPaintBoard.setSolidLinePaint();
			mPencil.setBackgroundColor(mLightGrey);
			mPaintBoard.setPencilMode(true);
			break;	
		
		case R.id.crooked_line:
			setDrawingDefault();
			mPaintBoard.setSolidLinePaint();
			mCrookedLine.setBackgroundColor(mLightGrey);
			mPaintBoard.setCrookedLineMode(true);
			break;	
			
		case R.id.list:
			popupSubmenu();
			break;	
			
		case R.id.new_file:
			((TacticBoardActivity) mContext).reset();
			if (mPopupWindow.isShowing()) mPopupWindow.dismiss();
			break;	
			
		case R.id.save:
			((TacticBoardActivity) mContext).saveImgToGallery();
			if (mPopupWindow.isShowing()) mPopupWindow.dismiss();
			break;	
			
		case R.id.share:
			((TacticBoardActivity) mContext).share();
			if (mPopupWindow.isShowing()) mPopupWindow.dismiss();
			break;		
			
		case R.id.color:
			mColorDialog.show();
			if (mPopupWindow.isShowing()) mPopupWindow.dismiss();
			break;		
		
		default:
			Log.e(TAG, "ERROR: switch default clided");
			break;
		}
	}

	private void setDrawingDefault() {
		mMove.setBackgroundColor(mWhite);
		mSolidLine.setBackgroundColor(mWhite);
		mShortDashLine.setBackgroundColor(mWhite);
		mLongDashLine.setBackgroundColor(mWhite);
		mPencil.setBackgroundColor(mWhite);
		mCrookedLine.setBackgroundColor(mWhite);
		
		((TacticBoardActivity) mContext).setMoving(false);
		mPaintBoard.setDrawing(true);
		mPaintBoard.setPencilMode(false);
		mPaintBoard.setCrookedLineMode(false);
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		ClipData data = ClipData.newPlainText("", "");
		
		DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
		v.startDrag(data, shadowBuilder, v, 0);
		//v.setVisibility(View.INVISIBLE);
		return true;
	}

}