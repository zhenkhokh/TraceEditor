package ru.android.zheka.di.jpoint

import dagger.Module
import dagger.Provides
import ru.android.zheka.di.FragmentChildScope
import ru.android.zheka.fragment.Edit
import ru.android.zheka.fragment.IJumpPoint
import ru.android.zheka.model.LatLngModel
import ru.android.zheka.vm.jump.IJumpPointVM
import ru.android.zheka.vm.jump.JumpPointVM

@Module(includes = [JumpPointModule::class])
class JumpPointBindingModule {
    @Provides
    fun provideJumpPointVM(model: LatLngModel?, view: IJumpPoint?): IJumpPointVM {
        return JumpPointVM(view!!, model!!)
    }

    @FragmentChildScope
    @Provides
    fun provideEdit(): Edit {
        return Edit()
    }
}