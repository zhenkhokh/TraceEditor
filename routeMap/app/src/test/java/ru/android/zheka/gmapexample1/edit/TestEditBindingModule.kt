package ru.android.zheka.gmapexample1.edit

import dagger.Module
import dagger.Provides
import ru.android.zheka.fragment.IEdit
import ru.android.zheka.model.LatLngModel
import ru.android.zheka.vm.EditVM

@Module(includes = [TestEditModule::class])
class TestEditBindingModule {
    @Provides
    fun bindEditVM(model: LatLngModel?, view: IEdit?): EditVM {
        return EditVM(view!!, model!!)
    }

//    @Provides
//    fun provideEditModel(view: Context?): EditModel {
//        return EditModel(view!!)
//    }
}