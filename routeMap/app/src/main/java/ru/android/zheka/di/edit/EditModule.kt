package ru.android.zheka.di.edit

import dagger.Binds
import dagger.Module
import ru.android.zheka.fragment.Edit
import ru.android.zheka.fragment.EditTraces
import ru.android.zheka.fragment.IEdit
import ru.android.zheka.fragment.IEditTraces
import ru.android.zheka.vm.EditVM
import ru.android.zheka.vm.IEditVM

@Module
abstract class EditModule {
    @Binds
    abstract fun bindActivity(context: Edit?): IEdit

    @Binds
    abstract fun bindEditVM(vm:EditVM?): IEditVM

    @Binds
    abstract fun bindEditTraces(context: EditTraces?): IEditTraces?
}