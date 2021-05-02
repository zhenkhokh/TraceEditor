package ru.android.zheka.coreUI

import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.databinding.ObservableField
import io.reactivex.Observable
import io.reactivex.functions.Consumer
import ru.android.zheka.gmapexample1.R
import java.util.*

class SpinnerHandler {
    private var map: Map<String, String>? = null
    private var data: List<String?>? = null
    var spinnerAdapter: ObservableField<ArrayAdapter<*>?>? = null
        private set
    private var methodHandler: Consumer<String>? = null
    private var view: IActivity? = null
    private var isEmpty: Boolean
    private var enterHandler: Consumer<*>? = null//TODO remove
    private var selectedItem: String? = null
    fun setData(data: List<String>?) {
        this.data = data
    }

    fun setData(map: Map<String, String>) {
        this.map = map
        data = Arrays.asList(*map.keys.toTypedArray())
    }

    constructor(methodHandler: Consumer<String>, enterHandler: Consumer<String?>, data: List<String>?
                , view: IActivity?) {
        setData(data)
        this.methodHandler = methodHandler
        this.enterHandler = enterHandler
        selectedItem = ""
        this.view = view
        isEmpty = false
        showArea()
    }

    constructor(methodHandler: Consumer<String>, enterHandler: Consumer<String?>
                , map: Map<String, String>, view: IActivity?) : this(methodHandler, enterHandler, Arrays.asList(*map.keys.toTypedArray()), view) {
        this.map = map
    }

    constructor() {
        isEmpty = true
        spinnerAdapter = ObservableField(null as ArrayAdapter<*>?)
    }

    fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
        if (!isEmpty && parent != null && parent.getItemAtPosition(pos) != null) {
            selectedItem = parent.getItemAtPosition(pos).toString()
            Observable.just(selectedItem).subscribe({ name: String? ->
                if (map == null) {
                    methodHandler!!.accept(name)
                } else {
                    methodHandler!!.accept(map!![selectedItem!!])
                }
            }
            ) { throwable: Throwable -> this.view!!.showError(throwable) }.dispose()
        }
    }

    fun showArea() {
        if (!isEmpty) {
            val arrayAdapter: ArrayAdapter<String?> = ArrayAdapter<String?>(view!!.activity,
                    R.layout.spinner_base,
                    R.id.spinner_item,
                    data!!)
            if (spinnerAdapter == null) {
                spinnerAdapter = ObservableField()
            }
            spinnerAdapter!!.set(arrayAdapter)
        }
    }
}