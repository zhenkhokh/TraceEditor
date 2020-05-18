package ru.android.zheka.gmapexample1

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Spinner

class FormActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.form)
        val intent = Intent()
        intent.setClass(this, MainActivity::class.java)
        intent.action = Intent.ACTION_VIEW
        startActivity(intent)
        finish()
    }

    fun sendFeedback(button: View?) {
        val nameField = findViewById<View>(R.id.EditTextName) as EditText
        val name = nameField.text.toString()
        val emailField = findViewById<View>(R.id.EditTextEmail) as EditText
        val email = emailField.text.toString()
        val feedbackField = findViewById<View>(R.id.EditTextFeedbackBody) as EditText
        val feedback = feedbackField.text.toString()
        val feedbackSpinner = findViewById<View>(R.id.SpinnerFeedbackType) as Spinner
        val feedbackType = feedbackSpinner.selectedItem.toString()
        val responseCheckbox = findViewById<View>(R.id.CheckBoxResponse) as CheckBox
        val bRequiresResponse = responseCheckbox.isChecked
    }
}