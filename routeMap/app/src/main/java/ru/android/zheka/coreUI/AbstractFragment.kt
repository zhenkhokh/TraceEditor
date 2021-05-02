package ru.android.zheka.coreUI

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import dagger.android.support.DaggerFragment
import io.reactivex.functions.Consumer

abstract class AbstractFragment<B : ViewDataBinding> : DaggerFragment(), IActivity {
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

    override fun context() = super.getActivity()!! as Context

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
        binding = initAdapter(binding)
        return binding.getRoot()
    }

    open fun initAdapter(binding: B):B {return binding}

    override fun showError(throwable: Throwable) {
        error!!.showError(throwable, Consumer{ a: Boolean? -> })
    }

    override fun switchToFragment(fragmentId: Int, fragment: Fragment) {
        val transaction = manager!!.beginTransaction()
        transaction.replace(fragmentId, fragment)
        transaction.commit()
    }

    override fun removeFragment(fragment: Fragment) {
        val transaction = manager!!.beginTransaction()
        transaction.remove(fragment)
        transaction.commit()
    }

    override val manager
        get() = fragmentManager

    override val activity
        get() = super.getActivity()?:Activity()
}