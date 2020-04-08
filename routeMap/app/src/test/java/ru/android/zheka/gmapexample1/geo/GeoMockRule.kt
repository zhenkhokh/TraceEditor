package ru.android.zheka.gmapexample1.geo

import org.junit.rules.MethodRule
import ru.android.zheka.fragment.Geo
import ru.android.zheka.gmapexample1.DaggerMock
import ru.android.zheka.gmapexample1.RobolectricMockTestRule
import ru.android.zheka.gmapexample1.TestAppComponent
import ru.android.zheka.gmapexample1.TestApplicationModule

class GeoMockRule : RobolectricMockTestRule() {
    var geoFragment: Geo? = null

    override fun rule() = DaggerMock.rule<TestAppComponent>(TestApplicationModule(application)
            , TestGeoBindingModule()) {
        customizeBuilder { builder: TestAppComponent.Builder ->
            builder.application(application)
        }
        set { component: TestAppComponent ->
            component.inject(application)
            geoFragment = Geo()
            component.geoSubcomponent().create(geoFragment).inject(geoFragment)
        }
    } as MethodRule
}

