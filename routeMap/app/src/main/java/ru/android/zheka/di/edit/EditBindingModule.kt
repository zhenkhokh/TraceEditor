package ru.android.zheka.di.edit

import android.content.Context
import dagger.Module
import dagger.Provides
import ru.android.zheka.fragment.IEdit
import ru.android.zheka.model.EditModel
import ru.android.zheka.model.LatLngModel
import ru.android.zheka.vm.EditVM

@Module(includes = [EditModule::class])
class EditBindingModule {
    @Provides
    fun bindEditVM(model: LatLngModel?, view: IEdit?): EditVM {
        return EditVM(view!!, model!!)
    }

    @Provides
    fun provideEditModel(view: Context?): EditModel {
        return EditModel(view!!)
    }
}