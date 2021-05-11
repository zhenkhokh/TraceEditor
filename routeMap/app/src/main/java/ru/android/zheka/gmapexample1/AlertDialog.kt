package ru.android.zheka.gmapexample1

import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.os.Bundle
import android.view.View
import android.widget.TextView

class AlertDialog(var msg: String) : DialogFragment() {
    private var msgField: TextView? = null
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        println("start onCreateDialog")
        super.onCreate(savedInstanceState)
        val inflater = activity.layoutInflater
        println("start getting builder")
        val builder = AlertDialog.Builder(activity)
        println("start setView")
        val view = inflater.inflate(R.layout.alert_dialog, null)
        builder.setView(view)
        println("start getting nameField,builder is $builder")
        //builder.setView(R.layout.save_dialog);
        msgField = view.findViewById<View>(R.id.text) as TextView
        msgField!!.text = msg
        println("start setPositiveButton")
        builder.setPositiveButton(R.string.ok_save_point) { arg0, arg1 -> arg0.cancel() }
        println("start returning")
        return builder.create()
    }

}