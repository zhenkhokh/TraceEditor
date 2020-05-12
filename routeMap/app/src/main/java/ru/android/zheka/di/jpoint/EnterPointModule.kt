package ru.android.zheka.di.jpoint

import dagger.Binds
import dagger.Module
import ru.android.zheka.fragment.EnterPoint
import ru.android.zheka.fragment.IEnterPoint

@Module
abstract class EnterPointModule {
    @Binds
    abstract fun bindEnterPoint(cp: EnterPoint): IEnterPoint
}