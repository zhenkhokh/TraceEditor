package ru.android.zheka.gmapexample1.home

import org.junit.rules.MethodRule
import ru.android.zheka.fragment.Home
import ru.android.zheka.gmapexample1.DaggerMock
import ru.android.zheka.gmapexample1.RobolectricMockTestRule
import ru.android.zheka.gmapexample1.TestAppComponent
import ru.android.zheka.gmapexample1.TestApplicationModule
import ru.android.zheka.vm.IPanelHomeVM
import ru.android.zheka.vm.PanelHomeVM

class HomeMockRule: RobolectricMockTestRule(){
    var vm: IPanelHomeVM? = null
    var vmObj: PanelHomeVM? = null
    var homeFragment: Home? = null

    override fun rule() =  DaggerMock.rule<TestAppComponent>(TestApplicationModule(application)
    ,TestHomeBindingModule()) {
        customizeBuilder {
            builder:TestAppComponent.Builder->
            builder.application(application)
        }
        set { component:TestAppComponent->
            component.inject(application)
            homeFragment = Home()
            component.homeSubcomponent().create(homeFragment).inject(homeFragment)
            vm = homeFragment!!.viewModel
        }
    } as MethodRule
}

