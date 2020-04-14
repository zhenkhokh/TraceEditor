package ru.android.zheka.vm

import ru.android.zheka.coreUI.IActivity
import ru.android.zheka.db.Config
import ru.android.zheka.db.DbFunctions.DEFAULT_CONFIG_NAME
import ru.android.zheka.db.DbFunctions.add
import ru.android.zheka.db.DbFunctions.getModelByName
import ru.android.zheka.model.ISettingsModel

class SettingsVM(var view: IActivity, var model: ISettingsModel) : ISettingsVM {

    override fun switchUpdateLen(checked: Boolean) {
        var config:Config = getModelByName (DEFAULT_CONFIG_NAME, Config::class.java) as Config
        config.uLocation = checked;
        try {
            add(config);
        } catch (e:IllegalAccessException) {
            e.printStackTrace();
        } catch (e:InstantiationException) {
            e.printStackTrace();
        } catch (e:IllegalArgumentException) {
            e.printStackTrace();
        } catch (e:Exception) {
            e.printStackTrace();
        }
        println("update location is set as " + config.uLocation);
    }

    override fun optimizationMode() {

    }

    override fun speedTraceControl() {

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

}
