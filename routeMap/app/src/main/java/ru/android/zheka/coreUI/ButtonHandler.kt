package ru.android.zheka.coreUI

import android.view.View
import androidx.databinding.ObservableInt
import io.reactivex.Observable
import io.reactivex.functions.Consumer

class ButtonHandler {
    // always use constructor before. Observable field does not effective here
    // only one listener must be for one button, this works in generaly
    var label: String? = null
    var visible: ObservableInt
        private set
    private var methodHandler: Consumer<Boolean>? = null
    private var view: IActivity? = null

    fun onClick() {
        Observable.just(true).subscribe(methodHandler, Consumer { throwable: Throwable -> view!!.showError(throwable) }).dispose()
    }

    constructor(methodHandler: Consumer<Boolean>, idLabel: Int, view: IActivity) {
        label = view.activity.getString(idLabel)
        visible = ObservableInt(View.VISIBLE)
        this.methodHandler = methodHandler
        this.view = view
    }

    constructor() {
        visible = ObservableInt(View.INVISIBLE)
    }
}