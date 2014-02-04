package nanofuntas.tb;

import nanofuntas.tb.R;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;

public class ColorPaletteDialog extends AlertDialog {
	private boolean DEBUG = true;
	private String TAG = "ColorPaletteDialog";
	
	private final int GRID_VERTICAL_SPACING = 10;
	private final int GRID_HORIZONTAL_SPACING = 10;
	
	private Context mContext;
	private GridView mGrid;
	private ColorDataAdapter mColorAdapter;
	public static OnColorSelectedListener sListener;

	public interface OnColorSelectedListener {
		public void onColorSelected(int color);
	}
	
	protected ColorPaletteDialog(Context context) {
		super(context);
		this.mContext = context;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.color_dialog);
		mGrid = (GridView) findViewById(R.id.color_grid);
		
		mGrid.setBackgroundColor(Color.GRAY);
		mGrid.setVerticalSpacing(GRID_VERTICAL_SPACING);
		mGrid.setHorizontalSpacing(GRID_HORIZONTAL_SPACING);
		
		mColorAdapter = new ColorDataAdapter(mContext, this);
		mGrid.setAdapter(mColorAdapter);
		mGrid.setNumColumns(mColorAdapter.getNumColumns());
	}
}

class ColorDataAdapter extends BaseAdapter {
	private boolean DEBUG = true;
	private String TAG = "ColorDataAdapter";
	
	private Context mContext;
	private AlertDialog mDialog;
	
	private final int ROW_COUNT = 3;
	private final int COLUMN_COUNT = 7;
	
	public static final int[] COLORS = new int[] {
		0xff000000,0xff00007f,0xff0000ff,0xff007f00,0xff007f7f,0xff00ff00,0xff00ff7f,
		0xff00ffff,0xff7f007f,0xff7f00ff,0xff7f7f00,0xff7f7f7f,0xffff0000,0xffff007f,
		0xffff00ff,0xffff7f00,0xffff7f7f,0xffff7fff,0xffffff00,0xffffff7f,0xffffffff
	};
	
	public ColorDataAdapter(Context context, AlertDialog dialog) {
		super();
		mContext = context;
		mDialog = dialog;
	}
	
	public int getNumColumns() {
		return COLUMN_COUNT;
	}
	
	@Override
	public int getCount() {
		return ROW_COUNT * COLUMN_COUNT;
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
		int rowIndex = position / ROW_COUNT;
		int columnIndex = position % COLUMN_COUNT;
		
		GridView.LayoutParams params = new GridView.LayoutParams(
				GridView.LayoutParams.MATCH_PARENT,
				GridView.LayoutParams.MATCH_PARENT);
		
		Button aItem = new Button(mContext);
		aItem.setLayoutParams(params);
		aItem.setBackgroundColor(COLORS[position]);
		aItem.setTag(COLORS[position]);
		
		aItem.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (ColorPaletteDialog.sListener != null) {
					ColorPaletteDialog.sListener.onColorSelected(((Integer) v.getTag()).intValue());
				}
				mDialog.dismiss();
			}
		});
		return aItem;
	}
	
}