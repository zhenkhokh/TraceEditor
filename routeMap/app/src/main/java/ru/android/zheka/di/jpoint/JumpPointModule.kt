package ru.android.zheka.di.jpoint

import dagger.Binds
import dagger.Module
import ru.android.zheka.fragment.IJumpPoint
import ru.android.zheka.fragment.JumpPoint
import ru.android.zheka.model.AddressModel
import ru.android.zheka.model.IAddressModel

@Module
abstract class JumpPointModule {
    @Binds
    abstract fun bindJumpPoint(cp: JumpPoint): IJumpPoint

//TODO remove EnterPointModule

    @Binds
    abstract fun bindAddressModel(model: AddressModel?): IAddressModel?

//    @Binds
//    abstract fun bindCoordinatePointModel(model: CoordinatePointModel?): ICoordinatePointModel?
}
