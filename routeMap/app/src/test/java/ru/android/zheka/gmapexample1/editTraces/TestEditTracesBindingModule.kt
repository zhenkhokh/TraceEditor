package ru.android.zheka.gmapexample1.editTraces

import dagger.Module
import dagger.Provides
import ru.android.zheka.fragment.Edit
import ru.android.zheka.fragment.IEditTraces
import ru.android.zheka.gmapexample1.edit.TestEditModule
import ru.android.zheka.model.LatLngModel
import ru.android.zheka.vm.EditTracesVM
import ru.android.zheka.vm.IEditTracesVM

@Module(includes = [TestEditModule::class])
class TestEditTracesBindingModule {
    @Provides
    fun bindEditTracesVM(model: LatLngModel?, view: IEditTraces?): IEditTracesVM {
        return EditTracesVM(view!!, model!!)
    }


    @Provides
    fun provideEdit(): Edit {
        return Edit()
    }
}