package com.example.tb;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;

public class ColorPaletteDialog extends Activity {
	GridView mGrid;
	Button mCloseBtn;
	ColorDataAdapter mColorAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.color_dialog);
		
		this.setTitle("Set Color");
		mGrid = (GridView) findViewById(R.id.color_grid);
		mCloseBtn = (Button) findViewById(R.id.close_btn);
		
		mGrid.setColumnWidth(14);
		mGrid.setBackgroundColor(Color.GRAY);
		mGrid.setVerticalSpacing(4);
		mGrid.setNumColumns(4);
		
		mColorAdapter = new ColorDataAdapter(this);
		mGrid.setAdapter(mColorAdapter);
		mGrid.setNumColumns(mColorAdapter.getNumColumns());
		
		mCloseBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		
	}
}

class ColorDataAdapter extends BaseAdapter {
	private Context mContext;
	
	public static final int[] COLORS = new int[] {
		0xff000000,0xff00007f,0xff0000ff,0xff007f00,0xff007f7f,0xff00ff00,0xff00ff7f,
		0xff00ffff,0xff7f007f,0xff7f00ff,0xff7f7f00,0xff7f7f7f,0xffff0000,0xffff007f,
		0xffff00ff,0xffff7f00,0xffff7f7f,0xffff7fff,0xffffff00,0xffffff7f,0xffffffff
	};
	
	private int mRowCount;
	private int mColumnCount;
	
	public ColorDataAdapter(Context context) {
		super();
		mContext = context;
		mRowCount = 3; 
		mColumnCount = 7;
	}
	
	public int getNumColumns() {
		return mColumnCount;
	}
	
	@Override
	public int getCount() {
		return mRowCount * mColumnCount;
	}

	@Override
	public Object getItem(int position) {
		return COLORS[position];
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int position, View arg1, ViewGroup arg2) {
		int rowIndex = position / mRowCount;
		int columnIndex = position % mRowCount;
		
		GridView.LayoutParams params = new GridView.LayoutParams(
				GridView.LayoutParams.MATCH_PARENT,
				GridView.LayoutParams.MATCH_PARENT);
		
		Button aItem = new Button(mContext);
		aItem.setText(" ");
		aItem.setLayoutParams(params);
		aItem.setPadding(4, 4, 4, 4);
		aItem.setBackgroundColor(COLORS[position]);
		aItem.setHeight(64);
		aItem.setTag(COLORS[position]);
		
		aItem.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
			}
			
		});
		
		return aItem;
	}
	
}