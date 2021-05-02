package ru.android.zheka.coreUI

import android.content.DialogInterface
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import io.reactivex.Observable
import io.reactivex.functions.Consumer
import ru.android.zheka.gmapexample1.R

open class ErrorDialog(protected var config: DialogConfig, private val view: IActivity) {
    fun showError(throwable: Throwable, consumer: Consumer<Boolean>) {
        val isValidRes = view.activity != null && view.activity!!.resources != null
        // translation can be here
        config = DialogConfig.Companion.builder()
                .labelValue(if (isValidRes) view.activity.resources.getString(R.string.errorDialog_windowTitle) else "")
                .contentValue(if (throwable.message != null) throwable.message else throwable.toString())
                .context(view.activity)
                .positiveConsumer(consumer)
                .layoutId(R.layout.dialog_error)
                .titleId(R.id.errorDialog_windowTitle)
                .contentId(R.id.errorDialog_value)
                .poistiveBtnId(R.string.ok_save_point)
                .build()
        show()
    }

    fun show() {
        val view = dialogView
        val dialog = configureDialog(view)
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
    }

    protected open fun configureDialog(view: View): AlertDialog {
        getContent(view)
        return AlertDialog.Builder(config.context!!)
                .setView(view)
                .setPositiveButton(config.poistiveBtnId) { d: DialogInterface, which: Int ->
                    d.cancel()
                    Observable.just(true).subscribe(config.positiveConsumer, Consumer { throwable: Throwable -> this.view.showError(throwable) }).dispose()
                }
                .create()
    }

    private val dialogView: View
        private get() {
            val view = View.inflate(config.context, config.layoutId, null)
            val titleView = getDialogTitle(view)
            val value = config.labelValue
            if (!value!!.isEmpty()) {
                titleView.text = value
            }
            return view
        }

    private fun getDialogTitle(view: View): TextView {
        return view.findViewById(config.titleId)
    }

    protected fun getContent(view: View): TextView {
        val content = view.findViewById<TextView>(config.contentId)
        content.text = config.contentValue
        content.setOnClickListener { v: View? -> content.error = null }
        return content
    }

}