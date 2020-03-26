package ru.android.zheka.gmapexample1

import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.os.Bundle
import android.view.View
import android.widget.EditText

abstract class CoordinateDialog : DialogFragment() {
    protected var lonField: EditText? = null
    protected var latField: EditText? = null
    override fun onCreateDialog(savedInstanceState: Bundle): Dialog {
        super.onCreate(savedInstanceState)
        val inflater = activity.layoutInflater
        val builder = AlertDialog.Builder(activity)
        println("start setView")
        val view = inflater.inflate(R.layout.coordinate_dialog, null)
        builder.setView(view)
        latField = view.findViewById<View>(R.id.text_1) as EditText
        lonField = view.findViewById<View>(R.id.text_2) as EditText
        val hint1 = R.string.hint_latitude
        val hint2 = R.string.hint_longitude
        latField!!.setHint(hint1)
        lonField!!.setHint(hint2)
        builder.setPositiveButton(R.string.ok_save_point) { arg0, arg1 ->
            process()
            arg0.cancel()
        }
        builder.setNegativeButton(R.string.cancel_save_point) { arg0, arg1 -> arg0.cancel() }
        println("start returning")
        return builder.create()
    }

    abstract fun process()
}