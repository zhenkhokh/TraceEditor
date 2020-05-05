package ru.android.zheka.di.cpoint

import dagger.Binds
import dagger.Module
import ru.android.zheka.fragment.CPoint
import ru.android.zheka.fragment.ICPoint

@Module
abstract class CPointModule {
    @Binds
    abstract fun bindCpoint(cp: CPoint): ICPoint
}
