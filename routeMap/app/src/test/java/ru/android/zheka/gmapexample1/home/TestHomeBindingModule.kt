package ru.android.zheka.gmapexample1.home

import android.content.Context
import dagger.Module
import dagger.Provides
import org.mockito.Mockito
import ru.android.zheka.fragment.*
import ru.android.zheka.model.HomeModel
import ru.android.zheka.model.IHomeModel
import ru.android.zheka.vm.IPanelHomeVM
import ru.android.zheka.vm.PanelHomeVM

@Module(includes = [TestHomeModule::class])
class TestHomeBindingModule {
    var mockVM = Mockito.mock(IPanelHomeVM::class.java)

    @Provides
    fun bindHomeVM(model: IHomeModel?, view: IHome?): IPanelHomeVM {
        return PanelHomeVM(view!!,model!!, Edit(), EditTraces(), Trace(), EnterPoint()) //new PanelHomeVM (Mockito.mock(IActivity.class), Mockito.mock(HomeModel.class));//mockVM;// Mockito.mock (IPanelHomeVM.class)
    }

    @Provides
    fun provideHomeModel(view: Context?): HomeModel {
        return HomeModel(view)
    }
}