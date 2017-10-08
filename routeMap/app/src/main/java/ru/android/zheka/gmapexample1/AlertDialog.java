package ru.android.zheka.gmapexample1;

import android.app.Dialog;
import android.os.Bundle;
import android.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import ru.android.zheka.gmapexample1.R;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

public class AlertDialog extends DialogFragment {
	public String msg;
	private TextView msgField;
	
	public AlertDialog(String msg){
		this.msg = msg;
	}
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState){		
		System.out.println("start onCreateDialog");
		super.onCreate(savedInstanceState);
		LayoutInflater inflater = getActivity().getLayoutInflater();
		System.out.println("start getting builder");
		android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
		System.out.println("start setView");
		View view = inflater.inflate(R.layout.alert_dialog, null);
		builder.setView(view);		
		System.out.println("start getting nameField,builder is "+builder);
		//builder.setView(R.layout.save_dialog);
		msgField = (TextView)(view.findViewById(R.id.text));
		msgField.setText(msg);
		System.out.println("start setPositiveButton");
		builder.setPositiveButton(R.string.ok_save_point, new OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				arg0.cancel();
			}
		});
		System.out.println("start returning");
		return builder.create();
	}
}
