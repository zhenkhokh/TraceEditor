package ru.android.zheka.coreUI

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import ru.android.zheka.gmapexample1.R

abstract class AbstractActivity //RoboFragmentActivity
<B : ViewDataBinding> : AbstractListActivity(), IActivity {
    private var error: ErrorControl? = null
    var binding: B? = null
    protected abstract val layoutId: Int
    protected abstract fun initComponent()
    protected abstract fun onInitBinding(binding: B?)
    protected abstract fun onResumeBinding(binding: B?)
    protected abstract fun onDestroyBinding(binding: B?)
    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)
        supportActionBar!!.setIcon(R.mipmap.ic_launcher)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        error = ErrorControl(this)
        binding = DataBindingUtil.inflate<B>(LayoutInflater.from(context)!!, layoutId,null,false)
    }

    override fun onStart() {
        initComponent()
        super.onStart()
        onInitBinding(binding)
    }

    override fun onResume() {
        super.onResume()
        onResumeBinding(binding)
    }

    override fun onDestroy() {
        super.onDestroy()
        onDestroyBinding(binding)
        binding!!.unbind()
    }

    override fun showError(throwable: Throwable) {
        error!!.showError(throwable) { a: Boolean? -> }
    }

    override fun getContext(): Context {
        return this
    }

    override fun switchToFragment(fragmentId: Int, fragment: Fragment) {
        val transaction = manager.beginTransaction()
        transaction.replace(fragmentId, fragment)
        transaction.commit()
    }

    override fun removeFragment(fragment: Fragment) {
        val transaction = manager.beginTransaction()
        transaction.remove(fragment)
        transaction.commit()
    }

    override fun getManager(): FragmentManager {
        return supportFragmentManager
    }
}