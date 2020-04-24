package ru.android.zheka.di.edit

import dagger.Binds
import dagger.Module
import ru.android.zheka.di.ActivityScope
import ru.android.zheka.fragment.Edit
import ru.android.zheka.fragment.IEdit
import ru.android.zheka.model.EditModel
import ru.android.zheka.model.IEditModel

@Module
abstract class EditModule {
    @Binds
    @ActivityScope
    abstract fun bindEditModel(model: EditModel?): IEditModel?

    @Binds
    abstract fun bindActivity(context: Edit?): IEdit?
}