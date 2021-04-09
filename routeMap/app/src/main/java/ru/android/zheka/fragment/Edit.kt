package ru.android.zheka.fragment

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.android.zheka.coreUI.AbstractFragment
import ru.android.zheka.coreUI.IPanelModel
import ru.android.zheka.gmapexample1.R
import ru.android.zheka.gmapexample1.databinding.LatLngFragmentBinding
import ru.android.zheka.gmapexample1.databinding.RowBinding
import ru.android.zheka.vm.IEditVM
import javax.inject.Inject

open class Edit : AbstractFragment<LatLngFragmentBinding>(), IEdit {
    @Inject
    lateinit var viewModel: IEditVM

    @Inject
    open lateinit var panelModel: IPanelModel

    override val layoutId
        get() = R.layout.lat_lng_fragment

    override fun initComponent() {
    }

    override fun onInitBinding(binding: LatLngFragmentBinding) {
        viewModel.panelModel = panelModel
        binding.model = viewModel.model()
    }

    override fun initAdapter(binding: LatLngFragmentBinding): LatLngFragmentBinding {
        val adapter = EditAdapter(viewModel, viewModel.context, viewModel.model().chekedVisibility)
        binding.listLatlng.adapter = adapter
        var recyclerView: RecyclerView? = viewModel.view.activity.findViewById(binding!!.listLatlng.id)
        recyclerView!!.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter
        return binding
    }

//    override val chekedVisibility: Int = View.GONE

    override fun onResumeBinding(binding: LatLngFragmentBinding) {
        viewModel.onResume()
    }

    override fun onDestroyBinding(binding: LatLngFragmentBinding) {
        viewModel.onDestroy()
    }
}

class EditAdapter(val viewModel: IEditVM, val context: Context, val checkedVisibility: Int) : RecyclerView.Adapter<LatLngHandler>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LatLngHandler {
        val itemBind = DataBindingUtil.inflate<RowBinding>(LayoutInflater.from(context),
                R.layout.row, parent, false)
        val handler = LatLngHandler(itemBind, checkedVisibility)
        itemBind.root.setOnClickListener(viewModel.onClickListener)
        return handler
    }

    override fun getItemCount(): Int {
        return viewModel.shownItems.size
    }

    override fun onBindViewHolder(holder: LatLngHandler, position: Int) {
        holder.bind(viewModel.shownItems.get(position))
    }
}


