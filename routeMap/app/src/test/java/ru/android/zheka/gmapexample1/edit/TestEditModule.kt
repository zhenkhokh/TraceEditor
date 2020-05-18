package ru.android.zheka.gmapexample1.edit

import dagger.Binds
import dagger.Module
import ru.android.zheka.vm.EditVM
import ru.android.zheka.vm.IEditVM

@Module
abstract class TestEditModule {
//    @Binds
//    abstract fun bindActivity(context: Edit?): IEdit

    @Binds
    abstract fun bindEditVM(vm:EditVM?): IEditVM

//    @Binds
//    abstract fun bindEditTraces(context: EditTraces?): IEditTraces?
}