package ru.android.zheka.di.setting

import android.content.Context
import dagger.Module
import dagger.Provides
import ru.android.zheka.coreUI.IActivity
import ru.android.zheka.model.SettingsModel
import ru.android.zheka.vm.ISettingsVM
import ru.android.zheka.vm.SettingsVM

@Module(includes = [SettingsModule::class])
class SettingsBindingModule {
    @Provides
    fun bindSettingsVM(model: SettingsModel?, view: IActivity?): ISettingsVM {
        return SettingsVM(view!!, model!!)
    }

    @Provides
    fun provideSettingsModel(view: Context?): SettingsModel {
        return SettingsModel(view!!)
    }
}