package ru.android.zheka.coreUI

import android.app.Activity
import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

interface IActivity {
    open fun context():Context
    open val activity: Activity
    open fun showError(throwable: Throwable)
    open fun switchToFragment(fragmentId: Int, fragment: Fragment)
    open fun removeFragment(fragment: Fragment)
    open val manager: FragmentManager?
}