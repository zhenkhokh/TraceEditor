package ru.android.zheka.gmapexample1.geo

import android.content.Context
import dagger.Module
import dagger.Provides
import org.mockito.Mockito
import ru.android.zheka.fragment.IGeo
import ru.android.zheka.model.GeoModel
import ru.android.zheka.model.IGeoModel
import ru.android.zheka.vm.GeoVM

@Module(includes = [TestGeoModule::class])
class TestGeoBindingModule {
    var mockVM = Mockito.mock(GeoVM::class.java)

    @Provides
    fun bindGeoVM(view: IGeo?, model: IGeoModel?): GeoVM {
        return mockVM//GeoVM(view!!,model!!)
    }

    @Provides
    fun provideGeoModel(view: Context?): GeoModel {
        return GeoModel(view)
    }
}