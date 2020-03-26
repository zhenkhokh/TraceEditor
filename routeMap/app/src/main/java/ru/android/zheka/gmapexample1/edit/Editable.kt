package ru.android.zheka.gmapexample1.edit

import com.activeandroid.Model
import java.lang.reflect.Field

class Editable(var model: Model) {
    var name: String? = null
//    fun getName(): String? {
//        return name
//    }

//    fun setName(name: String?) {
//        try {
//            field!![model] = name
//        } catch (e: IllegalAccessException) {
//            e.printStackTrace()
//        } catch (e: IllegalArgumentException) {
//            e.printStackTrace()
//        }
//        this.name = name
//    }

    private val field: Field?
        private get() {
            var field: Field? = null
            try {
                field = model.javaClass.getField("name")
            } catch (e: NoSuchFieldException) {
                e.printStackTrace()
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
            return field
        }

    init {
        try {
            name = field!![model] as String
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }
    }
}