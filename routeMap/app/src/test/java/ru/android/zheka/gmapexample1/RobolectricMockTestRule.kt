package ru.android.zheka.gmapexample1

import org.junit.rules.MethodRule
import org.junit.runners.model.FrameworkMethod
import org.junit.runners.model.Statement
import org.robolectric.RuntimeEnvironment
import ru.android.zheka.fragment.Home
import ru.android.zheka.gmapexample1.home.TestHomeBindingModule
import ru.android.zheka.vm.IPanelHomeVM
import ru.android.zheka.vm.PanelHomeVM

class RobolectricMockTestRule : MethodRule {
    open fun rule() = DaggerMock.rule<TestAppComponent>(TestApplicationModule(application)
            ,TestHomeBindingModule()) {
        set { component:TestAppComponent->
//            homeFragment = Home()
//            component.homeSubcomponent()!!.create(homeFragment).inject(homeFragment)
//            vm = homeFragment!!.viewModel
        }
    } as MethodRule

//DaggerMock<TestAppComponent>(TestAppComponent::class.java, TestApplicationModule(application),
        //TestHomeBindingModule()) {
    var vm: IPanelHomeVM? = null
    var vmObj: PanelHomeVM? = null
    var homeFragment: Home? = null

    companion object {
        val application: RobolectricMainApp
            get() = (RuntimeEnvironment.application as RobolectricMainApp)
    }

    override fun apply(base: Statement?, method: FrameworkMethod?, target: Any?): Statement {
        return rule().apply(base,method,target)
    }
}