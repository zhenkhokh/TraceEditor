package ru.android.zheka.gmapexample1;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public abstract class CoordinateDialog  extends DialogFragment{
	EditText lonField;
	EditText latField;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState){		
		super.onCreate(savedInstanceState);
		LayoutInflater inflater = getActivity().getLayoutInflater();
		android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
		System.out.println("start setView");
		View view = inflater.inflate(R.layout.coordinate_dialog, null);
		builder.setView(view);		
		latField = (EditText)(view.findViewById(R.id.text_1));
		lonField = (EditText)(view.findViewById(R.id.text_2));
		int hint1 = R.string.hint_latitude;
		int hint2 = R.string.hint_longitude;
		latField.setHint(hint1);
		lonField.setHint(hint2);
		
		builder.setPositiveButton(R.string.ok_save_point, new OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				process();
				arg0.cancel();
			}

		});
		builder.setNegativeButton(R.string.cancel_save_point, new OnClickListener() {				
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				arg0.cancel();					
			}
		});
		System.out.println("start returning");
		return builder.create();
	}
	abstract public void process();
}
