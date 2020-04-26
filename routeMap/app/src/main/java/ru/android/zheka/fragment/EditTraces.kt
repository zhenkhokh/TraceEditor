package ru.android.zheka.fragment

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.android.zheka.gmapexample1.R
import ru.android.zheka.gmapexample1.databinding.LatLngFragmentBinding
import ru.android.zheka.gmapexample1.databinding.RowBinding
import ru.android.zheka.model.LatLngModel
import ru.android.zheka.vm.IEditTracesVM
import ru.android.zheka.vm.IEditVM
import javax.inject.Inject

class EditTraces : Edit(), IEditTraces {
    @Inject
    lateinit var viewModel_: IEditTracesVM

    @Inject
    lateinit var mm: LatLngModel

    override fun initAdapter(binding: LatLngFragmentBinding): LatLngFragmentBinding {
        viewModel = viewModel_ as IEditVM
        val adapter = EditTraceAdapter(viewModel, viewModel.view.context)
        binding.listLatlng.adapter = adapter
        var recyclerView: RecyclerView? = activity?.findViewById(binding!!.listLatlng.id)
        recyclerView!!.layoutManager = LinearLayoutManager(viewModel.view.context)
        recyclerView!!.adapter = adapter
        return binding
    }
}

class EditTraceAdapter(viewModel: IEditVM, context: Context) : EditAdapter(viewModel, context) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LatLngHandler {
        val itemBind = DataBindingUtil.inflate<RowBinding>(LayoutInflater.from(context),
                R.layout.row, parent, false)
        //TODO
//        val text: TextView = itemBind.root.findViewById(R.id.lat_lng_textView)
//        text.setText("Маршрут")
        return super.onCreateViewHolder(parent, viewType)
    }

    override fun getItemCount(): Int {
        return viewModel.shownItems.size
    }

    override fun onBindViewHolder(holder: LatLngHandler, position: Int) {
        holder.bind(viewModel.shownItems.get(position))
    }
}