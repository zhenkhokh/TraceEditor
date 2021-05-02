package ru.android.zheka.coreUI

import io.reactivex.functions.Consumer
import ru.android.zheka.gmapexample1.R

class ErrorControl(private val view: IActivity) {
    fun showError(throwable: Throwable, consumer: Consumer<Boolean>) {
        ErrorDialog(DialogConfig.Companion.builder().contentId(R.layout.alert_dialog)
                .context(view.activity)
                .poistiveBtnId(R.string.ok_save_point)
                .build(), view).showError(throwable, consumer)
    }

}