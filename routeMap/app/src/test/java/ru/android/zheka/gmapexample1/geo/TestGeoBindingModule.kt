package ru.android.zheka.gmapexample1.geo

import android.content.Context
import dagger.Module
import dagger.Provides
import org.mockito.Mockito
import ru.android.zheka.fragment.IGeo
import ru.android.zheka.model.GeoModel
import ru.android.zheka.model.IGeoModel
import ru.android.zheka.vm.GeoVM
import ru.android.zheka.vm.IGeoVM

@Module(includes = [TestGeoModule::class])
class TestGeoBindingModule {
    var mockVM = Mockito.mock(GeoVM::class.java)

    @Provides
    fun bindGeoVM(view: IGeo?, model: IGeoModel?): IGeoVM {
        return GeoVM(view!!,model!!)
    }

    @Provides
    fun provideGeoModel(view: Context?): GeoModel {
        return GeoModel(view)
    }
}