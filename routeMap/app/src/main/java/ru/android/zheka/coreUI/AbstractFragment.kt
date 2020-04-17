package ru.android.zheka.coreUI

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import dagger.android.support.DaggerFragment

abstract class AbstractFragment<B : ViewDataBinding> : DaggerFragment(), IBaseFragment {
    private var error: ErrorControl? = null
    lateinit var binding: B
    protected abstract val layoutId: Int
    protected abstract fun initComponent()
    protected abstract fun onInitBinding(binding: B)
    protected abstract fun onResumeBinding(binding: B)
    protected abstract fun onDestroyBinding(binding: B)
    override fun onStart() {
        super.onStart()
        initComponent()
        onInitBinding(binding)
    }

    override fun onResume() {
        super.onResume()
        onResumeBinding(binding)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        onDestroyBinding(binding)
        binding.unbind()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        error = ErrorControl(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, saveInstance: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        return binding.getRoot()
    }

    override fun showError(throwable: Throwable) {
        error!!.showError(throwable) { a: Boolean? -> }
    }

    override fun getContext(): Context? {
        return activity
    }

    override fun switchToFragment(fragmentId: Int, fragment: Fragment) {
        val transaction = manager.beginTransaction()
        transaction.replace(fragmentId, fragment)
        transaction.commit()
    }

    override fun getManager(): FragmentManager {
        return activity!!.supportFragmentManager
    }
}