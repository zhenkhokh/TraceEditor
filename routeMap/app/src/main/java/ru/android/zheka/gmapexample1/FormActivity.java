package ru.android.zheka.gmapexample1;

import ru.android.zheka.db.Config;
import ru.android.zheka.db.DbFunctions;
import ru.android.zheka.gmapexample1.R;
import ru.android.zheka.route.Routing;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;


public class FormActivity  extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.form);
	
        Intent intent = new Intent();
        intent.setClass(this, MainActivity.class);
        intent.setAction(Intent.ACTION_VIEW);
        startActivity(intent);
        finish();
    }


    public void sendFeedback(View button) {


        final EditText nameField = (EditText) findViewById(R.id.EditTextName);
        String name = nameField.getText().toString();

        final EditText emailField = (EditText) findViewById(R.id.EditTextEmail);
        String email = emailField.getText().toString();

        final EditText feedbackField = (EditText) findViewById(R.id.EditTextFeedbackBody);
        String feedback = feedbackField.getText().toString();

        final Spinner feedbackSpinner = (Spinner) findViewById(R.id.SpinnerFeedbackType);
        String feedbackType = feedbackSpinner.getSelectedItem().toString();


        final CheckBox responseCheckbox = (CheckBox) findViewById(R.id.CheckBoxResponse);
        boolean bRequiresResponse = responseCheckbox.isChecked();
     
    }

}
