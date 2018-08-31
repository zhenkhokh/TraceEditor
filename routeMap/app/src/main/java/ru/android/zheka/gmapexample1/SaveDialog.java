package ru.android.zheka.gmapexample1;

import ru.android.zheka.gmapexample1.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.app.DialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView.FindListener;
import android.widget.EditText;
import android.widget.Toast;

public abstract class SaveDialog extends DialogFragment {
	
	//protected int hintId = R.string.hint_dialog_point;
	EditText nameField;
	private int DEFAULT_RES_ID = 0;
	private String DEFAULT_RES_ID_STR = "";

	public SaveDialog newInstance(int hintId){		
			System.out.println("start newInstance(int hintId)");
			System.out.println("get newInstance()");
			SaveDialog frag = newInstance(); //new SaveDialog();
			Bundle args = new Bundle();
			args.putString("hint",DEFAULT_RES_ID_STR);
	        args.putInt("hint", hintId);
	        frag.setArguments(args);
	        System.out.println("end newInstance()");
	        return frag;
       	}
	public SaveDialog newInstance(String hint){		
		SaveDialog frag = newInstance(); 
		Bundle args = new Bundle();
		args.putInt("hint",DEFAULT_RES_ID);
        args.putString("hint", hint);
        frag.setArguments(args);
        return frag;
   	}
	//get sub-class instance
	abstract protected SaveDialog newInstance();
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState){		
		System.out.println("start onCreateDialog");
		super.onCreate(savedInstanceState);
		LayoutInflater inflater = getActivity().getLayoutInflater();
		System.out.println("start getting builder");
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		System.out.println("start setView");
		View view = inflater.inflate(R.layout.save_dialog, null);
		builder.setView(view);		
		System.out.println("start getting nameField,builder is "+builder);
		//builder.setView(R.layout.save_dialog);
		nameField = (EditText)(view.findViewById(R.id.name_save_text));
		System.out.println("start getting resId");
		int resId = DEFAULT_RES_ID;
		String hint = DEFAULT_RES_ID_STR;
		try {
			resId = getArguments ().getInt ("hint", DEFAULT_RES_ID);
		}catch (ClassCastException e){
			try{
				hint = getArguments().getString("hint",DEFAULT_RES_ID_STR);
			}catch (ClassCastException ee){
				throw new ClassCastException ("cant define hint");
			}
		}

		System.out.println("start setHint,nameField is "+nameField);
		if (resId!=DEFAULT_RES_ID)
			nameField.setHint(resId);
		else
			System.out.println("resId is 0, try set string value ...");
		if (!hint.equals (DEFAULT_RES_ID_STR))
			nameField.setHint(hint);
		else
			System.out.println("hint is empty, see if resId was set up");

		System.out.println("start setNegativeButton");
			builder.setNegativeButton(R.string.cancel_save_point, new OnClickListener() {				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					arg0.cancel();					
				}
			});
			System.out.println("start setPositiveButton");
		builder.setPositiveButton(R.string.ok_save_point, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				positiveProcess();
				arg0.cancel();
			}
		});
		System.out.println("start returning");
		return builder.create();
	}
	abstract protected void positiveProcess();
		/*Intent intent = getActivity().getIntent();
		PositionUtil positionUtil = new PositionUtil();
		positionUtil.positionAndBoundInit(intent);
		String dataPoint = PositionUtil.latLngToString(positionUtil.getCenter()); 
		Toast.makeText(getActivity()
				, "save process as "
		+nameField.getText().toString()
		+", data: "
		+dataPoint
		+" is saved successfully", 15);
		*/
	
}
