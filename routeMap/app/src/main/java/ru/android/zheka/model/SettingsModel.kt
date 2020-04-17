package ru.android.zheka.model

import android.content.Context
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import ru.android.zheka.core.AktionMessage
import ru.android.zheka.core.Message
import ru.android.zheka.coreUI.PanelModel
import ru.android.zheka.coreUI.SpinnerHandler
import ru.android.zheka.db.Config
import ru.android.zheka.db.DbFunctions
import ru.android.zheka.db.DbFunctions.getModelByName
import ru.android.zheka.gmapexample1.R
import java.util.*

class SettingsModel(view: Context) : PanelModel(view), ISettingsModel {
    private val updateLen: ObservableBoolean
    private val optimizationNo: ObservableBoolean
    private val optimizationGoogle: ObservableBoolean
    private val optimizationBellmanFord: ObservableBoolean
    private val spinner: ObservableField<SpinnerHandler>
    override val spinnerTimer: ObservableField<SpinnerHandler?>
    override val spinnerTravel: ObservableField<SpinnerHandler?>
    override val speedTrace: ObservableInt
    override val posTimer: ObservableInt
    override val posTravel: ObservableInt
    override val avoid: ObservableBoolean
    override val avoidHighWays: ObservableBoolean
    override val avoidInDoor: ObservableBoolean
    override val offline: ObservableBoolean

    override fun getSpinner(): ObservableField<SpinnerHandler> {
        return spinner
    }

    override val input: String?
        get() = TODO("Not yet implemented")
    override val action: AktionMessage?
        get() = TODO("Not yet implemented")
    override val message: Message?
        get() = TODO("Not yet implemented")


    override fun updateLen(): ObservableBoolean {
        return updateLen
    }

    override fun optimizationNo(): ObservableBoolean {
        return optimizationNo
    }

    override fun optimizationGoogle(): ObservableBoolean {
        return optimizationGoogle
    }

    override fun optimizationBellmanFord(): ObservableBoolean {
        return optimizationBellmanFord
    }

    init {
        val config = getModelByName(DbFunctions.DEFAULT_CONFIG_NAME, Config::class.java) as Config?
        updateLen = ObservableBoolean(config!!.uLocation)
        val optimizationBellman = view.resources.getString(R.string.optimizationdata3)
        val isBellman = optimizationBellman == config.bellmanFord
        optimizationNo = ObservableBoolean(!config.optimization && !isBellman)
        optimizationBellmanFord = ObservableBoolean(isBellman)
        optimizationGoogle = ObservableBoolean(config.optimization && !isBellman)
        spinner = ObservableField(null )
        val spinnerData = Arrays.asList(*view.resources.getStringArray(R.array.speedList))
        var pos = -1
        val spinnerSelected = java.lang.Double.valueOf(config.rateLimit_ms) / 1000.0
        while (++pos < spinnerData.size) if (spinnerSelected == java.lang.Double.valueOf(spinnerData[pos])) break
        speedTrace = ObservableInt(pos)
        val timerData = view.resources.getStringArray(R.array.timerdatalist)
        var selectedData = config.tenMSTime
        val c = Comparator { o1: String, o2: String? -> o1.compareTo(o2!!) }
        posTimer = ObservableInt(Arrays.binarySearch(timerData, selectedData, c))
        spinnerTimer = ObservableField(null as SpinnerHandler?)
        val travelData = view.resources.getStringArray(R.array.travelmodelist)
        selectedData = config.travelMode
        posTravel = ObservableInt(Arrays.binarySearch(travelData, selectedData, c))
        spinnerTravel = ObservableField(null as SpinnerHandler?)
        avoid = ObservableBoolean(config.avoid.contains(DbFunctions.AVOID_TOLLS))
        avoidHighWays = ObservableBoolean(config.avoid.contains(DbFunctions.AVOID_HIGHWAYS))
        avoidInDoor = ObservableBoolean(config.avoid.contains(DbFunctions.AVOID_INDOR))
        offline = ObservableBoolean(config.offline == view.resources.getString(R.string.offlineOn))
    }
}