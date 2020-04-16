package ru.android.zheka.model;

import android.content.Context;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;
import ru.android.zheka.core.AktionMessage;
import ru.android.zheka.core.Message;
import ru.android.zheka.coreUI.PanelModel;
import ru.android.zheka.coreUI.SpinnerHandler;
import ru.android.zheka.db.Config;
import ru.android.zheka.db.DbFunctions;
import ru.android.zheka.gmapexample1.R;

public class SettingsModel extends PanelModel implements ISettingsModel {
    private ObservableBoolean updateLen;
    private ObservableBoolean optimizationNo;
    private ObservableBoolean optimizationGoogle;
    private ObservableBoolean optimizationBellmanFord;
    private ObservableField<SpinnerHandler> spinner;
    private ObservableField<SpinnerHandler> spinnerTimer;
    private ObservableField<SpinnerHandler> spinnerTravel;
    private ObservableInt speedTrace;

    private ObservableInt posTimer;

    private ObservableInt posTravel;

    public SettingsModel(Context view) {
        super (view);
        Config config = (Config) DbFunctions.getModelByName(DbFunctions.DEFAULT_CONFIG_NAME, Config.class);
        updateLen = new ObservableBoolean (config.uLocation);
        String optimizationBellman = view.getResources().getString (R.string.optimizationdata3);
        Boolean isBellman = optimizationBellman.equals (config.bellmanFord);
        optimizationNo = new ObservableBoolean (!config.optimization && !isBellman);
        optimizationBellmanFord = new ObservableBoolean (isBellman);
        optimizationGoogle = new ObservableBoolean (config.optimization && !isBellman);
        spinner = new ObservableField <> ((SpinnerHandler) null);
        List<String> spinnerData = Arrays.asList (view.getResources ().getStringArray (R.array.speedList));
        int pos = -1;
        Double spinnerSelected = Double.valueOf(config.rateLimit_ms)/1000.0;
        while (++pos<spinnerData.size ())
            if (spinnerSelected.equals (Double.valueOf (spinnerData.get (pos))))
                break;
        speedTrace = new ObservableInt (pos);
        String[] timerData = view.getResources ().getStringArray (R.array.timerdatalist);
        String selectedData = config.tenMSTime;
        Comparator<String> c = (o1,o2) -> o1.compareTo (o2);
        posTimer = new ObservableInt (Arrays.binarySearch(timerData, selectedData, c));
        spinnerTimer = new ObservableField <> ((SpinnerHandler)null);
        String[] travelData = view.getResources ().getStringArray (R.array.travelmodelist);
        selectedData = config.travelMode;
        posTravel = new ObservableInt (Arrays.binarySearch(travelData, selectedData, c));
        spinnerTravel = new ObservableField <> ((SpinnerHandler)null);
    }

    @Override
    public ObservableField <SpinnerHandler> getSpinnerTimer() {
        return spinnerTimer;
    }

    @Override
    public ObservableField <SpinnerHandler> getSpinnerTravel() {
        return spinnerTravel;
    }

    @Override
    public ObservableInt getPosTimer() {
        return posTimer;
    }

    @Override
    public ObservableInt getPosTravel() {
        return posTravel;
    }

    @Override
    public ObservableInt getSpeedTrace() {
        return speedTrace;
    }

    @Override
    public ObservableField <SpinnerHandler> getSpinner() {
        return spinner;
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

    @Override
    public ObservableBoolean optimizationNo() {
        return optimizationNo;
    }

    @Override
    public ObservableBoolean optimizationGoogle() {
        return optimizationGoogle;
    }

    @Override
    public ObservableBoolean optimizationBellmanFord() {
        return optimizationBellmanFord;
    }
}
