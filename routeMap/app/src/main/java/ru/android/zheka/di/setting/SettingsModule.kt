package ru.android.zheka.di.setting

import dagger.Binds
import dagger.Module
import ru.android.zheka.coreUI.IActivity
import ru.android.zheka.di.ActivityScope
import ru.android.zheka.fragment.Settings
import ru.android.zheka.model.ISettingsModel
import ru.android.zheka.model.SettingsModel

@Module
abstract class SettingsModule {
    @Binds
    @ActivityScope
    abstract fun bindSettingsModel(model: SettingsModel?): ISettingsModel?

    @Binds
    abstract fun bindActivity(context: Settings?): IActivity?
}