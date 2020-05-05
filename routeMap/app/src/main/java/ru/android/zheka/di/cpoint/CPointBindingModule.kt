package ru.android.zheka.di.cpoint

import dagger.Module
import dagger.Provides
import ru.android.zheka.di.FragmentChildScope
import ru.android.zheka.fragment.Edit
import ru.android.zheka.fragment.ICPoint
import ru.android.zheka.model.LatLngModel
import ru.android.zheka.vm.CPointVM
import ru.android.zheka.vm.ICPointVM

@Module(includes = [CPointModule::class])
class CPointBindingModule {
    @Provides
    fun bindCPointVM(model: LatLngModel?, view: ICPoint?): ICPointVM {
        return CPointVM(view!!, model!!)
    }

    @FragmentChildScope
    @Provides
    fun provideEdit(): Edit {
        return Edit()
    }
}