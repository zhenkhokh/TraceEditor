package ru.android.zheka.model;

import android.content.Context;

import androidx.databinding.ObservableBoolean;
import ru.android.zheka.core.AktionMessage;
import ru.android.zheka.core.Message;
import ru.android.zheka.coreUI.PanelModel;
import ru.android.zheka.db.Config;
import ru.android.zheka.db.DbFunctions;

public class SettingsModel extends PanelModel implements ISettingsModel {
    private ObservableBoolean updateLen;

    public SettingsModel(Context view) {
        super (view);
        Config config = (Config) DbFunctions.getModelByName(DbFunctions.DEFAULT_CONFIG_NAME, Config.class);
        updateLen = new ObservableBoolean (config.uLocation);
    }

    @Override
    public String getInput() {
        return null;
    }

    @Override
    public AktionMessage getAction() {
        return null;
    }

    @Override
    public Message getMessage() {
        return null;
    }

    @Override
    public ObservableBoolean updateLen() {
        return updateLen ;
    }
}
