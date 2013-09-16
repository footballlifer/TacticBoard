package com.example.tb;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;


public class TextDialog extends AlertDialog {
	private boolean DEBUG = true;
	private String TAG = "TextDialog";
	
	private Context mContext;
	private Button mTextOK;
	
	private DialogInterface.OnClickListener mListener;
	
	protected TextDialog(Context context) {
		super(context);
		this.mContext = context;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.text_dialog);
		mTextOK = (Button) findViewById(R.id.text_ok);
		
		setButton(DialogInterface.BUTTON_POSITIVE, "okkk", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
			}
		});

	}
	
}

