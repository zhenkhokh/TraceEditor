package ru.android.zheka.di.jpoint

import android.content.Context
import dagger.Module
import dagger.Provides
import ru.android.zheka.di.FragmentScope
import ru.android.zheka.fragment.IEnterPoint
import ru.android.zheka.model.AddressModel
import ru.android.zheka.vm.jump.AddressPointVM
import ru.android.zheka.vm.jump.CoordinatePointVM
import ru.android.zheka.vm.jump.IAddressPointVM
import ru.android.zheka.vm.jump.ICoordinatePointVM

@Module(includes = [EnterPointModule::class])
class EnterPointBindingModule {
    @FragmentScope
    @Provides
    fun provideCoordinatePointVM(model: AddressModel?, view: IEnterPoint?): ICoordinatePointVM {
        return CoordinatePointVM(view!!, model!!)
    }

    @Provides
    fun provideAddressPointVM(model: AddressModel?, view: IEnterPoint?): IAddressPointVM {
        return AddressPointVM(view!!, model!!)
    }

    @Provides
    fun provideAddressModel(context: Context): AddressModel {
        return AddressModel(context)
    }
}