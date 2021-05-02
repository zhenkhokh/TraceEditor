package ru.android.zheka.coreUI

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

interface IBaseFragment :IActivity{
    override val activity: AppCompatActivity
    override fun showError(throwable: Throwable)
    override fun switchToFragment(fragmentId: Int, fragment: Fragment)
    override val manager: FragmentManager?
    override fun removeFragment(fragment: Fragment)
}