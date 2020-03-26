package ru.android.zheka.gmapexample1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.SupportMapFragment

//@Implements(SupportMapFragment.class)
class ShadowSupportMapFragment constructor() : SupportMapFragment() {
    //@Implementation
    public override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                                     savedInstanceState: Bundle?): View? {
        //fragment = (SupportMapFragment)getFragmentManager().findFragmentById(R.id.map);
        val view: View = inflater.inflate(R.layout.activity_simple_bundle, container)
        return view
    }

    companion object {
        val instance: ShadowSupportMapFragment = ShadowSupportMapFragment()
    }
}