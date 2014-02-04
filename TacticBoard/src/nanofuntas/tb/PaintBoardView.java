package nanofuntas.tb;

import java.io.OutputStream;
import java.util.Stack;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import nanofuntas.tb.R;

public class PaintBoardView extends View {
	private boolean DEBUG = true;
	private String TAG = "PaintBoardView";
	
	private Context mContext;
	private int mViewWidth;
	private int mViewHeight;
	
	private Stack<Bitmap> mUndoStack = new Stack<Bitmap>();
	private Stack<Path> mPathStack = new Stack<Path>();
	
	public static int maxUndos = 10;

	private final Paint mPaint = new Paint();
	private Path mPath = new Path();
	private Canvas mCanvas;
	private Bitmap mBitmap;
	private float mLastX;
	private float mLastY;
	private float mCurveEndX;
	private float mCurveEndY;

	private final float RADIUS_TRIANGLE = 20;
	private final float RADIUS_CIRCLE = 15;
	private final float TEXT_SIZE = 30;
	private final int LINE_NUMBER_COLOR = 0xFFFFFFFF;
	private float mStartX = -1;
	private float mStartY = -1;
	private int mLineCount = 1;
	
	private final int SOLID_LINE_WIDTH = 4;
	private final int SHORT_DASH_WIDTH = 3;
	private final int SHORT_DASH_DOT = 5;
	private final int SHORT_DASH_SPACE = 10;
	private final int LONG_DASH_WIDTH = 5;
	private final int LONG_DASH_DOT = 40;
	private final int LONG_DASH_SPACE = 30;
	
	private static final int INVALIDATE_EXTRA_BORDER = 10;
	private static final float TOUCH_TOLERANCE = 2;
	private static final boolean RENDERING_ANTIALIAS = true;
	private static final boolean DITHER_FLAG = true;

	private int mColor = 0xFF000000;

	private Bitmap mBackGround;
	private Bitmap mScaledBackGround;
	
	private boolean mDrawing = false;
	private boolean mPencilMode = true;
	
	private boolean mCrookedLine = false;
	private static long sTotalCrookCount = 0;
	private final float CROOKED_RADIUS = 6f;

	public PaintBoardView(Context context) {
		super(context);
		init(context);
	}	
	public PaintBoardView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
	public PaintBoardView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}
	
	public void init(Context context) {
		this.mContext = context;
		setDefaultPaint();
		mBackGround = BitmapFactory.decodeResource(getResources(),R.drawable.img_field2);
		mLineCount = 1;
	}
	
	public void setDefaultPaint() {
		Log.i(TAG, "Constructor Initialized");
		mPaint.reset();
		mPaint.setAntiAlias(RENDERING_ANTIALIAS);
		mPaint.setColor(mColor);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setStrokeWidth(SOLID_LINE_WIDTH);
		mPaint.setDither(DITHER_FLAG);
		
		mLastX = -1;
		mLastY = -1;
	}
	
	public void setSolidLinePaint() {
		mPaint.setPathEffect(null);
		mPaint.setStrokeWidth(SOLID_LINE_WIDTH);
	}
	
	public void setShortDashPaint() {
		mPaint.setStrokeWidth(SHORT_DASH_WIDTH);
		mPaint.setPathEffect(
				new DashPathEffect(new float[] {SHORT_DASH_DOT, SHORT_DASH_SPACE}, 0));
	}
	
	public void setLongDashPaint() {
		mPaint.setStrokeWidth(LONG_DASH_WIDTH);
		mPaint.setPathEffect(
				new DashPathEffect(new float[] {LONG_DASH_DOT, LONG_DASH_SPACE}, 0));
	}

	public void saveImageToUndoStack() {
		if (mBitmap == null) return;
		
		if (mUndoStack.size() >= maxUndos) {
			Bitmap i = (Bitmap) mUndoStack.get(0);
			i.recycle();
			mUndoStack.remove(i);
		}

		Bitmap bitmap = Bitmap.createBitmap(mBitmap.getWidth(),
				mBitmap.getHeight(), Bitmap.Config.ARGB_4444);
		Canvas canvas = new Canvas();
		canvas.setBitmap(bitmap);
		canvas.drawBitmap(mBitmap, 0, 0, mPaint);

		mUndoStack.push(bitmap);
	}

	public void undo() {
		Bitmap prev = null;
		try {
			prev = (Bitmap) mUndoStack.pop();
			if (mLineCount > 0) mLineCount --;
		} catch (Exception e) {
			Log.e(TAG, "undo exception");
			e.printStackTrace();
		}

		if (prev != null) {
			mCanvas.drawBitmap(prev, 0, 0, mPaint);
			invalidate();

			prev.recycle();
		}
	}

	public void drawBackGround(Canvas canvas) {
		if (canvas != null) {
			mScaledBackGround = Bitmap.createScaledBitmap(mBackGround, mViewWidth, mViewHeight, true);
			canvas.drawBitmap (mScaledBackGround, 0, 0, null);
		}
	}

	public void updatePaintProperty(int color, int size) {
		mPaint.setColor(color);
		mPaint.setStrokeWidth(size);
	}
	
	public void updatePaintColor(int color) {
		mPaint.setColor(color);
	}

	public void updatePaintSize(int size) {
		mPaint.setStrokeWidth(size);
	}
	
	public void resetPaintBoard() {
		if (mViewWidth > 0 && mViewHeight > 0) {
			newImage(mViewWidth, mViewHeight);
		}
		mUndoStack.clear();
		mLineCount = 1;
	}
	
	public void newImage(int width, int height) {
		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
		Canvas canvas = new Canvas();
		canvas.setBitmap(bitmap);

		mBitmap = bitmap;
		mCanvas = canvas;

		drawBackGround(mCanvas);	
		drawBackGroundLines(mCanvas);
		invalidate();
	}

	private void drawBackGroundLines(Canvas canvas) {
		//TODO remove black background
		//canvas.drawColor(Color.BLACK);
		
		Paint p = new Paint();
		p.setStyle(Paint.Style.STROKE);
		p.setColor(Color.WHITE);
		p.setStrokeWidth(LONG_DASH_WIDTH);
		p.setFlags(Paint.ANTI_ALIAS_FLAG);
		
		float r = 1.2F; // ratio controlling size of goal area, penalty area, center circle	
		float f = 0.92F; // factor controlling size of field to view size, should be <=1
		float l = f * mViewHeight;
		float x = (1.0F-f) * mViewWidth / 2.0F;
		float y = (1.0F-f) * mViewHeight / 2.0F;
		
		float fieldLength = l;
		//float fieldWidth = 68 / 105F * l;
		float fieldWidth = f * mViewWidth;
		float goalLength = f * r * 7.32F / 105.0F * l;
		float centerCircleRadius = f * r * 9.15F / 105.0F * l;
		float paneltyMarkRadius = f * r * 0.2F / 105.0F * l;
		float cornerRadius = f * r * 2.0F / 105.0F * l;
		float goalAreaLength = f * r * 18.32F / 105.0F * l; // 18.32 = 7.32 + 5.5 + 5.5
		float goalAreaWidth = f * r * 5.5F / 105.0F * l; 
		float paneltyAreaWidth = f * r * 16.5F / 105.0F * l;
		float paneltyAreaLength = f * r * 40.32F / 105.0F * l; // 40.32 = 7.32 + 16.5 + 16.5
		float paneltyDist = f * r * 11F / 105.0F * l;
		float lineWitdh = f * r * 0.12F / 105.0F * l;
		
		p.setStrokeWidth(lineWitdh);
		
		// draw penalty area and goal area of filed
		canvas.drawRect(x+(fieldWidth-paneltyAreaLength)/2.0F, y, 
				x+fieldWidth-(fieldWidth-paneltyAreaLength)/2.0F, y+paneltyAreaWidth, p);
		canvas.drawRect(x+(fieldWidth-paneltyAreaLength) / 2.0F, y+fieldLength-paneltyAreaWidth, 
				x+fieldWidth-(fieldWidth-paneltyAreaLength)/2.0F, y+fieldLength, p);
		canvas.drawRect(x+(fieldWidth-goalAreaLength)/2.0F, y, 
				x+fieldWidth-(fieldWidth-goalAreaLength)/2.0F, y+goalAreaWidth, p);
		canvas.drawRect(x+(fieldWidth-goalAreaLength)/2.0F, y+fieldLength-goalAreaWidth, 
				x+fieldWidth-(fieldWidth-goalAreaLength)/2.0F, y+fieldLength, p);
		
		// draw penalty arc
		RectF rfUp= new RectF(x+fieldWidth/2.0F-centerCircleRadius, y+paneltyDist-centerCircleRadius, 
				x+fieldWidth/2.0F+centerCircleRadius, y+paneltyDist+centerCircleRadius);
		canvas.drawArc(rfUp, 37, 106, false, p); // 36 = Math.toDegrees(Math.atan(5.5/9.15))
		RectF rfDown= new RectF(x+fieldWidth/2.0F-centerCircleRadius, y+fieldLength-paneltyDist-centerCircleRadius, 
				x+fieldWidth/2.0F+centerCircleRadius, y+fieldLength-paneltyDist+centerCircleRadius);
		canvas.drawArc(rfDown, -37, -106, false, p);
		
		// draw corners
		RectF cUpLeft= new RectF(x-cornerRadius, y-cornerRadius, 
				x+cornerRadius, y+cornerRadius);
		canvas.drawArc(cUpLeft, 0, 90, false, p); 
		RectF cDownLeft= new RectF(x-cornerRadius, y+fieldLength-cornerRadius, 
				x+cornerRadius, y+fieldLength+cornerRadius);
		canvas.drawArc(cDownLeft, 0, -90, false, p); 
		RectF cUpRight= new RectF(x+fieldWidth-cornerRadius, y-cornerRadius, 
				x+fieldWidth+cornerRadius, y+cornerRadius);
		canvas.drawArc(cUpRight, 90, 90, false, p); 
		RectF cDownRight= new RectF(x+fieldWidth-cornerRadius, y+fieldLength-cornerRadius, 
				x+fieldWidth+cornerRadius, y+fieldLength+cornerRadius);
		canvas.drawArc(cDownRight, -90, -90, false, p); 
		
		// draw field lines
		canvas.drawRect(x, y, x+fieldWidth, y+fieldLength, p);
		// draw center circle
		canvas.drawCircle(x+fieldWidth/2, y+fieldLength/2, centerCircleRadius, p);		
		canvas.drawLine(x, y+fieldLength/2, x+fieldWidth, y+fieldLength/2, p);
		
		// draw markers
		p.setStyle(Paint.Style.FILL_AND_STROKE);
		canvas.drawCircle(x+fieldWidth/2, y+paneltyDist, paneltyMarkRadius, p);
		canvas.drawCircle(x+fieldWidth/2, y+fieldLength-paneltyDist, paneltyMarkRadius, p);
		canvas.drawCircle(x+fieldWidth/2, y+fieldLength/2, paneltyMarkRadius, p);
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		this.mViewWidth = w;
		this.mViewHeight = h;
		if (mViewWidth > 0 && mViewHeight >0) 
			newImage(mViewWidth, mViewHeight);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		if (mBitmap != null)
			canvas.drawBitmap(mBitmap, 0, 0, null);
	}
	
	public void setDrawing(boolean b) {
		mDrawing = b;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (Config.useMoveIcon == true) {
			if (mDrawing == false) return false;
		}
		
		int action = event.getAction();
		Rect rect = null;
		
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			saveImageToUndoStack();
			
			mStartX = event.getX();
			mStartY = event.getY();
			
			rect = touchDown(event);
			if (rect != null) invalidate(rect);
			
			return true;		
			
		case MotionEvent.ACTION_MOVE:
			rect = touchMoveOrUp(event);
			//if (rect != null) invalidate(rect);	
			invalidate();
			
			return true;
		
		case MotionEvent.ACTION_UP:
			rect = touchMoveOrUp(event);
			if (rect != null) invalidate(rect);
			
			mPathStack.push(mPath);
			mPath = new Path();
			//mPath.rewind();
			
			if (mPencilMode == false) {
				rect = drawArrow();
				invalidate(rect);
				
				rect = drawLineNumber(mStartX, mStartY);
				if (rect != null) invalidate(rect);
			}
			return true;
		}

		return false;
	}
	
	public void setPencilMode(boolean b) {
		this.mPencilMode = b;
	}
	
	private Rect drawLineNumber(float startX, float startY) {
		float x = startX;
		float y = startY;
		
		String lineNumber = Integer.toString(mLineCount);
		
		// draw circle at the starting line point
		//mPaint.setStyle(Paint.Style.FILL);
		//mCanvas.drawCircle(x, y, RADIUS_CIRCLE, mPaint);
		//mPaint.setStyle(Paint.Style.STROKE);

	    Rect numberRect = new Rect();
		Paint pNumber = new Paint();
		pNumber.setTextSize(TEXT_SIZE);
		pNumber.setColor(LINE_NUMBER_COLOR);
	    pNumber.getTextBounds(lineNumber, 0, lineNumber.length(), numberRect);
	    
		mCanvas.drawText(lineNumber, x - numberRect.width()/2, y + numberRect.height()/2, pNumber);
		
		mLineCount ++;
		
		Rect invalidRect = new Rect();
		invalidRect.set((int) (x - RADIUS_CIRCLE - INVALIDATE_EXTRA_BORDER), 
						(int) (y - RADIUS_CIRCLE - INVALIDATE_EXTRA_BORDER),
						(int) (x + RADIUS_CIRCLE + INVALIDATE_EXTRA_BORDER), 
						(int) (y + RADIUS_CIRCLE + INVALIDATE_EXTRA_BORDER) );
		
		return invalidRect;
	}
	
	private Rect drawArrow() {
		float preX = mCurveEndX;
		float preY = mCurveEndY;
		float centerX = mLastX;
		float centerY = mLastY;

		float alpha = (float) Math.atan( (preX - centerX)/(preY - centerY) );
		float alphaDegree = (float) Math.toDegrees(alpha);
		
		if (preY - centerY > 0) 
			alphaDegree = - alphaDegree;
		else 
			alphaDegree = 180 - alphaDegree;
		
		float p1x = (float) centerX;
		float p1y = (float) centerY - RADIUS_TRIANGLE;
		
		float p2x = (float) (centerX - RADIUS_TRIANGLE * Math.sqrt(3)/2);
		float p2y = (float) (centerY + RADIUS_TRIANGLE/2);
		
		float p3x = (float) centerX;
		float p3y = (float) (centerY + RADIUS_TRIANGLE/4);
		
		float p4x = (float) (centerX + RADIUS_TRIANGLE * Math.sqrt(3)/2);
		float p4y = (float) (centerY + RADIUS_TRIANGLE/2);
		
		Path triangle = new Path();
		triangle.moveTo(p1x, p1y);
		triangle.lineTo(p2x, p2y);
		triangle.lineTo(p3x, p3y);
		triangle.lineTo(p4x, p4y);
		triangle.lineTo(p1x, p1y);
		
		Matrix m = new Matrix();
		m.setRotate(alphaDegree, centerX, centerY);
		triangle.transform(m);
		
		Rect invalidRect = new Rect();
		invalidRect.set( (int) (centerX - RADIUS_TRIANGLE - INVALIDATE_EXTRA_BORDER),
				(int) (centerY - RADIUS_TRIANGLE - INVALIDATE_EXTRA_BORDER),
				(int) (centerX + RADIUS_TRIANGLE + INVALIDATE_EXTRA_BORDER),
				(int) (centerY + RADIUS_TRIANGLE + INVALIDATE_EXTRA_BORDER) );
		
		mPaint.setStyle(Paint.Style.FILL);
		mCanvas.drawPath(triangle, mPaint);
		mPaint.setStyle(Paint.Style.STROKE);
		
		return invalidRect;
	}
	
	private Rect touchDown(MotionEvent event) {
		sTotalCrookCount = 0;
		
		float x = event.getX();
		float y = event.getY();
		
		mLastX = mCurveEndX = x;
		mLastY = mCurveEndY = y;
		
		mPath.moveTo(x, y);
		mCanvas.drawPath(mPath, mPaint);
		
		final int border = INVALIDATE_EXTRA_BORDER;
		Rect invalidRect = new Rect();
		invalidRect.set((int) x - border, (int) y - border, (int) x + border, (int) y + border);
		
		return invalidRect;
	}
	
	public void setCrookedLineMode(boolean b) {
		this.mCrookedLine = b;
	}
	
	private Rect touchMoveOrUp(MotionEvent event) {
		final float x = event.getX();
		final float y = event.getY();
		
		final float dx = Math.abs(x - mLastX);
		final float dy = Math.abs(y - mLastY);
		
		Rect invalidRect = new Rect();
		if (dx < TOUCH_TOLERANCE && dy < TOUCH_TOLERANCE) return invalidRect; 
		
		final int border = INVALIDATE_EXTRA_BORDER;
		
		if (mCrookedLine == true) {
			RectF rectF = new RectF();
			float alpha = (float) Math.atan( (y - mLastY)/(x - mLastX) );
			float alphaDegree = (float) Math.toDegrees(alpha);
			
			float dist = (float) Math.sqrt(Math.pow(x - mLastX, 2) + Math.pow(y- mLastY, 2));
			int count =  (int) (dist/(2*CROOKED_RADIUS));
			
			int direction = 1;
			if (x - mLastX > 0) 
				direction = 1;
			else 
				direction = -1;
			
			float xx = (float) (mLastX + direction * CROOKED_RADIUS * Math.cos(alpha)); 
			float yy = (float) (mLastY + direction * CROOKED_RADIUS * Math.sin(alpha)); 
			
			for (int i = 0; i < count; i++) {
				rectF.set(xx-CROOKED_RADIUS, yy-CROOKED_RADIUS, xx+CROOKED_RADIUS, yy+CROOKED_RADIUS);
				int s = (int) Math.pow(-1, sTotalCrookCount); 
				mCanvas.drawArc(rectF, (alphaDegree),  direction * s * (-180), false, mPaint);
				xx = (float) (xx + direction * 2 * CROOKED_RADIUS * Math.cos(alpha));
				yy = (float) (yy + direction * 2 * CROOKED_RADIUS * Math.sin(alpha)); 
				sTotalCrookCount++;
			}
			
			mLastX = (float) (mLastX + direction * 2 * CROOKED_RADIUS * count * Math.cos(alpha));
			mLastY = (float) (mLastY + direction * 2 * CROOKED_RADIUS * count * Math.sin(alpha));
		} else {
			invalidRect.set((int) mCurveEndX - border, (int) mCurveEndY - border, 
					(int) mCurveEndX + border, (int) mCurveEndY + border);
			
			float cX = mCurveEndX = (x + mLastX) / 2;
			float cY = mCurveEndY = (y + mLastY) / 2;
			
			mPath.quadTo(mLastX, mLastY, cX, cY);	
			
			invalidRect.union((int) mLastX - border, (int) mLastY - border, 
					(int) mLastX + border, (int) mLastY + border);
			invalidRect.union((int) cX - border, (int) cY - border, 
					(int) cX + border, (int) cY + border);
			
			mLastX = x;
			mLastY = y;
		}
		
		mCanvas.drawPath(mPath, mPaint);	
		return invalidRect;
	}
	
	// unused API
	public void resetAllPath() {
		while(!mPathStack.isEmpty()){
			Path p = (Path) mPathStack.pop();				
			p.reset();
		}
	}
	
	// unused API
	public boolean save(OutputStream outStream) {
		try {
			mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
			invalidate();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	// unused API
	public void clearUndo() {
		while (true) {
			Bitmap prev = (Bitmap) mUndoStack.pop();
			if (prev == null) return;
			prev.recycle();
		}
	}
	
}
