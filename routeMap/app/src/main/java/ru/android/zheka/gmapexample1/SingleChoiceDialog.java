package ru.android.zheka.gmapexample1;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

public abstract class SingleChoiceDialog extends DialogFragment{
	public String msg=null;
	TextView msgField;
	int negativeId = R.string.cancel_choice,
			positiveId = R.string.ok_choice;
	public SingleChoiceDialog(String msg) {
		this.msg = msg;		
	}
	public SingleChoiceDialog(String msg,int negativeId, int positiveId){
		this(msg);
		this.positiveId = positiveId;
		this.negativeId = negativeId;
	}
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState){	
		super.onCreate(savedInstanceState);
		LayoutInflater inflater = getActivity().getLayoutInflater();
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		View view = inflater.inflate(R.layout.alert_dialog, null);
		builder.setView(view);
		msgField = (TextView)(view.findViewById(R.id.text));
		msgField.setText(msg);
		builder.setPositiveButton(positiveId, new OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				positiveProcess();
				arg0.cancel();
			}
		});
		builder.setNegativeButton(negativeId, new OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				negativeProcess();
				arg0.cancel();
			}
		});
		return builder.create();
	}
	abstract public void positiveProcess();
	abstract public void negativeProcess();
}
