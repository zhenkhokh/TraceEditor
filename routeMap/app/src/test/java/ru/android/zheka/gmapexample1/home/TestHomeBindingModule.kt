package ru.android.zheka.gmapexample1.home

import dagger.Module
import dagger.Provides
import org.mockito.Mockito
import ru.android.zheka.fragment.*
import ru.android.zheka.model.IHomeModel
import ru.android.zheka.vm.IPanelHomeVM
import ru.android.zheka.vm.PanelHomeVM

@Module(includes = [TestHomeModule::class])
class TestHomeBindingModule {
    var mockVM = Mockito.mock(IPanelHomeVM::class.java)

    @Provides
    fun bindHomeVM(model: IHomeModel?, view: IHome?, edit: IEdit, editTraces: IEditTraces, trace: ITrace, enterPoint: IEnterPoint)
            : IPanelHomeVM {
        return PanelHomeVM(view!!, model!!, edit, editTraces, trace, enterPoint)
    }

    @Provides
    fun provideEdit(): IEdit {
        return Edit()
    }

    @Provides
    fun provideEditTraces(): IEditTraces {
        return EditTraces()
    }

    @Provides
    fun provideTrace(): ITrace {
        return Trace()
    }

    @Provides
    fun provideEnterPoint(): IEnterPoint {
        return EnterPoint()
    }
}