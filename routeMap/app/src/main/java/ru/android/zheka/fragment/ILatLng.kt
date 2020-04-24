package ru.android.zheka.fragment

import ru.android.zheka.coreUI.IActivity
import ru.android.zheka.gmapexample1.databinding.LatLngFragmentBinding

interface ILatLng : IActivity {
    fun initAdapter(bind:LatLngFragmentBinding): LatLngFragmentBinding
}