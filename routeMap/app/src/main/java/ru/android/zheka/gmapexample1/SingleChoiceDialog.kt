package ru.android.zheka.gmapexample1

import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.os.Bundle
import android.view.View
import android.widget.TextView

abstract class SingleChoiceDialog(msg: String?) : DialogFragment() {
    lateinit var msg: String
    var msgField: TextView? = null
    var negativeId = R.string.cancel_choice
    var positiveId = R.string.ok_choice

    protected constructor(msg: String?, negativeId: Int, positiveId: Int) : this(msg) {
        this.positiveId = positiveId
        this.negativeId = negativeId
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreate(savedInstanceState)
        val inflater = activity.layoutInflater
        val builder = AlertDialog.Builder(activity)
        val view = inflater.inflate(R.layout.alert_dialog, null)
        builder.setView(view)
        msgField = view.findViewById<View>(R.id.text) as TextView
        msgField!!.text = msg
        builder.setPositiveButton(positiveId) { arg0, arg1 ->
            positiveProcess()
            arg0.cancel()
        }
        builder.setNegativeButton(negativeId) { arg0, arg1 ->
            negativeProcess()
            arg0.cancel()
        }
        return builder.create()
    }

    abstract fun positiveProcess()
    abstract fun negativeProcess()

    init {
        this.msg = msg!!
    }
}