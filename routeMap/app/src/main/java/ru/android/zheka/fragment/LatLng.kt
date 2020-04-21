package ru.android.zheka.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.android.zheka.coreUI.AbstractFragment
import ru.android.zheka.gmapexample1.R
import ru.android.zheka.gmapexample1.databinding.LatLngFragmentBinding
import ru.android.zheka.gmapexample1.databinding.RowBinding
import ru.android.zheka.vm.ILatLngVM
import javax.inject.Inject

class LatLng : AbstractFragment<LatLngFragmentBinding>(), ILatLng {
    @Inject
    lateinit var viewModel: ILatLngVM

    override val layoutId
        get() = R.layout.lat_lng_fragment

    override fun initComponent() {
    }

    override fun onInitBinding(binding: LatLngFragmentBinding) {
        binding.vm = viewModel
    }

    override fun initAdapter(binding: LatLngFragmentBinding):LatLngFragmentBinding {
        val adapter = LatLngAdapter(viewModel)
        binding.listLatlng.adapter  = adapter
        var recyclerView: RecyclerView? = viewModel.view.activity.findViewById(binding!!.listLatlng.id)
        recyclerView!!.layoutManager = LinearLayoutManager(context)
        recyclerView!!.adapter = adapter
//        adapter.notifyDataSetChanged()
        return binding
    }

    override fun onResumeBinding(binding: LatLngFragmentBinding) {
        viewModel!!.onResume()
    }

    override fun onDestroyBinding(binding: LatLngFragmentBinding) {
        viewModel!!.onDestroy()
    }
}

class LatLngAdapter(val viewModel: ILatLngVM) : RecyclerView.Adapter<LatLngHandler>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LatLngHandler {
        var context = viewModel.context
        val itemBind = DataBindingUtil.inflate<RowBinding>(LayoutInflater.from(context),
            R.layout.row, parent, false)
        return LatLngHandler(itemBind)
    }

    override fun getItemCount(): Int {
        return viewModel.shownItems.size
    }

    override fun onBindViewHolder(holder: LatLngHandler, position: Int) {
        holder.bind(viewModel.shownItems.get(position))
    }

}

class LatLngHandler(rowBinding: RowBinding) : RecyclerView.ViewHolder(rowBinding.root) {
    var rowBinding: RowBinding

    init {
        this.rowBinding = rowBinding
    }

    fun bind(text:String) {
        rowBinding.text = text
        rowBinding.executePendingBindings()
    }
}
