package ru.android.zheka.gmapexample1

import org.junit.rules.MethodRule
import org.junit.runners.model.FrameworkMethod
import org.junit.runners.model.Statement
import org.robolectric.RuntimeEnvironment

abstract class RobolectricMockTestRule : MethodRule {
    abstract fun rule():MethodRule

    companion object {
        val application: RobolectricMainApp
            get() = (RuntimeEnvironment.application as RobolectricMainApp)
    }

    override fun apply(base: Statement?, method: FrameworkMethod?, target: Any?): Statement {
        return rule().apply(base,method,target)
    }
}