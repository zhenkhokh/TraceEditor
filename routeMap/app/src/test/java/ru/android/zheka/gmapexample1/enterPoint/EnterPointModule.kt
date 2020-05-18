package ru.android.zheka.gmapexample1.enterPoint

import dagger.Binds
import dagger.Module
import ru.android.zheka.fragment.EnterPoint
import ru.android.zheka.fragment.IEnterPoint

@Module
abstract class TestEnterPointModule {
    @Binds
    abstract fun bindEnterPoint(cp: EnterPoint): IEnterPoint
}