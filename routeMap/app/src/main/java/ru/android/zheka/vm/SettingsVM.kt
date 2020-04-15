package ru.android.zheka.vm

import ru.android.zheka.coreUI.IActivity
import ru.android.zheka.coreUI.SpinnerHandler
import ru.android.zheka.db.Config
import ru.android.zheka.db.DbFunctions.DEFAULT_CONFIG_NAME
import ru.android.zheka.db.DbFunctions.add
import ru.android.zheka.db.DbFunctions.getModelByName
import ru.android.zheka.gmapexample1.R
import ru.android.zheka.model.ISettingsModel

class SettingsVM(var view: IActivity, var model: ISettingsModel) : ISettingsVM {

    init {
        val data = view.context.getResources().getStringArray(R.array.speedList).asList()
                as MutableList<*>
        model.spinner.set(SpinnerHandler({ a -> speedTraceControl(a) }, { a -> }, data, view))
    }

    override fun switchUpdateLen(checked: Boolean) {
        val config = udateDb { config -> config.uLocation = checked }
        println("update location is set as " + config.uLocation)
    }

    override fun optimizationNo() {
        val config = udateDb { config ->
            config.optimization = false
            config.bellmanFord = ""
        }
        println("update optimizationNo as " + config.optimization)
    }

    override fun optimizationGoogle() {
        var config = udateDb { config ->
            config.optimization = true
            config.bellmanFord = ""
        }
        println("update optimizationGoogle as " + config.optimization)
    }

    override fun optimizationBellmanFord() {
        var config = udateDb { config ->
            config.optimization = false
            config.bellmanFord = view.context.getResources().getString(R.string.optimizationdata3);
        }
        println("update optimizationBellmanFord as " + config.bellmanFord)
    }

    override fun speedTraceControl(input: String) {
        val config = udateDb { config ->
            config.rateLimit_ms = (input.toDouble() * 1000.0).toString()
        }
        println("speedTraceControl as " + config.rateLimit_ms)
    }

    override fun switchIgnorePaidRoads(checked: Boolean) {

    }

    override fun switchOffline(checked: Boolean) {

    }

    override fun traceMode() {
    }

    override fun updateRatePositionMode() {
    }

    override fun onResume() {
    }

    override fun model(): ISettingsModel? {
        return model
    }

    override fun onDestroy() {
    }

    private inline fun udateDb(chekManip: (config: Config) -> Unit): Config {
        var config1 = getModelByName(DEFAULT_CONFIG_NAME, Config::class.java) as Config
        chekManip(config1)
        try {
            add(config1)
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: InstantiationException) {
            e.printStackTrace()
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return config1
    }
}
