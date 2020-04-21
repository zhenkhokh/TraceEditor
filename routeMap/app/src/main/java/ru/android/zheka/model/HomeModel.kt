package ru.android.zheka.model

import android.content.Context
import ru.android.zheka.core.AktionMessage
import ru.android.zheka.core.Message
import ru.android.zheka.coreUI.PanelModel

class HomeModel(view: Context?) : PanelModel(view), IHomeModel {
    override val input: String?
        get() = TODO("Not yet implemented")
    override val action: AktionMessage?
        get() = TODO("Not yet implemented")
    override val message: Message?
        get() = TODO("Not yet implemented")
}