package ru.android.zheka.core

import ru.android.zheka.coreUI.IPanelModel

interface IInfoModel : IPanelModel {
    val input: String?
    val action: AktionMessage?
    val message: Message?
}