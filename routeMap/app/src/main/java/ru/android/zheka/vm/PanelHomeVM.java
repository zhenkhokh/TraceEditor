package ru.android.zheka.vm;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;


import java.io.InputStream;
import java.util.Scanner;

import javax.inject.Inject;

import io.reactivex.functions.Consumer;
import ru.android.zheka.coreUI.ButtonHandler;
import ru.android.zheka.coreUI.IActivity;
import ru.android.zheka.gmapexample1.AddressActivity;
import ru.android.zheka.gmapexample1.EditActivity;
import ru.android.zheka.gmapexample1.GeoPositionActivity;
import ru.android.zheka.gmapexample1.LatLngActivity;
import ru.android.zheka.gmapexample1.PointToTraceActivity;
import ru.android.zheka.gmapexample1.PositionInterceptor;
import ru.android.zheka.gmapexample1.R;
import ru.android.zheka.gmapexample1.SettingsActivity;
import ru.android.zheka.gmapexample1.edit.EditModel;
import ru.android.zheka.model.HomeModel;

public class PanelHomeVM implements IPanelHomeVM {

    private final IActivity view;
    private final Activity activity;
    private HomeModel model;

    @Inject
    public PanelHomeVM(IActivity view) {
        this.view = view;
        activity = view.getActivity ();
        model = new HomeModel (view);
        model.getStartButton ().set (getButton (a -> settingsAction (),R.string.home_settings_btn));
        model.getStopButton ().set (getButton (a -> address (), R.string.home_address_btn));
        model.getNextButton ().set (getButton (a -> pointNavigate (), R.string.home_points_btn));
        model.getStartButton1 ().set (getButton (a -> editTraces (), R.string.home_editTrace_btn));
        model.getStopButton1 ().set (getButton (a -> editPoints (), R.string.home_editPoint_btn));
        model.getNextButton1 ().set (getButton (a -> createTrace (), R.string.home_toTrace_btn));
        model.getStartButton2 ().set (getButton (a -> geo (), R.string.home_geo_btn));
        model.getStopButton2 ().set (getButton (a -> info (), R.string.home_info_btn));
        model.inputVisible ().set (View.GONE);
    }

    private void info() {
        LayoutInflater inflater = LayoutInflater.from (activity);
        View view = inflater.inflate (R.layout.scrolable_dialog, null);
        TextView tv = view.findViewById (R.id.textmsg);//new TextView (this);
        InputStream is = activity.getResources ().openRawResource (R.raw.info);
        Scanner scanner = new Scanner (is);
        scanner.useDelimiter ("\n");
        StringBuilder sb = new StringBuilder ();
        while (scanner.hasNextLine ())
            sb.append (scanner.nextLine ());
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            tv.setText (Html.fromHtml (sb.toString (), Html.FROM_HTML_MODE_LEGACY));
        } else
            tv.setText (Html.fromHtml (sb.toString ()));
        new AlertDialog.Builder (activity)
                .setView (view)
                .setTitle ("Помощь")
                .setCancelable (true)
                .create ()
                .show ();
    }

    public void address() {

        Intent intent = activity.getIntent ();
        intent.setAction (Intent.ACTION_VIEW);
        intent.setClass (activity, AddressActivity.class);
        activity.startActivity (intent);
        activity.finish ();
    }

    private void geo() {
        PositionInterceptor position = new PositionInterceptor (activity);
        Intent intent = position.updatePosition ();
        intent.setClass (view.getContext (), GeoPositionActivity.class);
        intent.setAction (Intent.ACTION_VIEW);
        //explicit activity
        activity.startActivity (intent);
        activity.finish ();
    }

    private void editPoints() {
        editItem ("Point", R.string.points_column_name, R.string.points_column_name1);
    }

    public void editTraces() {
        editItem ("Trace", R.string.traces_column_name, R.string.traces_column_name1);
    }

    public int editItem(String item, int nameId, int name1Id) {
        Intent intent = activity.getIntent ();
        EditModel model = new EditModel ();
        model.clsName = item;
        model.clsPkg = "ru.android.zheka.db";
        model.name1Id = name1Id;
        model.nameId = nameId;
        intent.putExtra (EditActivity.EDIT_MODEL, model);
        intent.setAction (Intent.ACTION_VIEW);
        intent.setClass (view.getContext (), EditActivity.class);
        activity.startActivity (intent);
        activity.finish ();
        return 0;
    }

    private void pointNavigate() {
        Intent intent = activity.getIntent ();
        intent.setClass (view.getContext (), LatLngActivity.class);
        intent.setAction (Intent.ACTION_VIEW);
        activity.startActivity (intent);
        activity.finish ();
    }

    private void createTrace() {
        Intent intent = activity.getIntent ();
        intent.setClass (activity, PointToTraceActivity.class);
        intent.setAction (Intent.ACTION_VIEW);
        activity.startActivity (intent);
        activity.finish ();
    }

    private void settingsAction() {
        Intent intent = view.getActivity ().getIntent ();
        intent.setAction (Intent.ACTION_VIEW);
        intent.setClass (view.getContext (), SettingsActivity.class);
        view.getActivity ().startActivity (intent);
        view.getActivity ().finish ();
    }

    private ButtonHandler getButton(Consumer <Boolean> consumer, int nameId) {
        ButtonHandler b = new ButtonHandler (consumer
                , nameId
                , view);
        return b;
    }

    @Override
    public void onResume() {
    }

    @Override
    public void onDestroy() {
    }

    @Override
    public HomeModel model() {
        return model;
    }
}
