package ru.android.zheka.gmapexample1.home

import dagger.Binds
import dagger.Module
import ru.android.zheka.fragment.Home
import ru.android.zheka.fragment.IHome
import ru.android.zheka.model.HomeModel
import ru.android.zheka.model.IHomeModel

@Module
abstract class TestHomeModule {
    @Binds //    @ActivityScope
    abstract fun bindHomeModel(model: HomeModel?): IHomeModel?

    @Binds
    abstract fun bindActivity(context: Home?): IHome? //    @Binds
    //    public abstract IPanelHomeVM bindHomeVM(PanelHomeVM vm);
}