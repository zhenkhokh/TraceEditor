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

import io.reactivex.functions.Consumer;
import ru.android.zheka.coreUI.ButtonHandler;
import ru.android.zheka.coreUI.IActivity;
import ru.android.zheka.gmapexample1.AddressActivity;
import ru.android.zheka.gmapexample1.EditActivity;
import ru.android.zheka.gmapexample1.GeoPositionActivity;
import ru.android.zheka.gmapexample1.LatLngActivity;
import ru.android.zheka.gmapexample1.PointToTraceActivity;
import ru.android.zheka.gmapexample1.PositionInterceptor;
import ru.android.zheka.gmapexample1.SettingsActivity;
import ru.android.zheka.gmapexample1.edit.EditModel;
import ru.android.zheka.model.IHomeModel;

import static ru.android.zheka.gmapexample1.R.id;
import static ru.android.zheka.gmapexample1.R.layout;
import static ru.android.zheka.gmapexample1.R.raw;
import static ru.android.zheka.gmapexample1.R.string;

public class PanelHomeVM implements IPanelHomeVM {

    IActivity view;
    IHomeModel model;

    public PanelHomeVM(IActivity view, IHomeModel model) {
        this.view = view;
        this.model = model;
    }

    @Override
    public void info() {
        Activity activity = view.getActivity ();
        LayoutInflater inflater = LayoutInflater.from (activity);
        View view = inflater.inflate (layout.scrolable_dialog, null);
        TextView tv = view.findViewById (id.textmsg);//new TextView (this);
        InputStream is = this.view.getActivity ().getResources ().openRawResource (raw.info);
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
                .setTitle (activity.getResources ().getString (string.home_info_btn))
                .setCancelable (true)
                .create ()
                .show ();
    }

    @Override
    public void address() {

        Intent intent = view.getActivity ().getIntent ();
        intent.setAction (Intent.ACTION_VIEW);
        intent.setClass (view.getActivity (), AddressActivity.class);
        view.getActivity ().startActivity (intent);
        view.getActivity ().finish ();
    }

    @Override
    public void geo() {
        PositionInterceptor position = new PositionInterceptor (view.getActivity ());
        Intent intent = position.updatePosition ();
        intent.setClass (view.getContext (), GeoPositionActivity.class);
        intent.setAction (Intent.ACTION_VIEW);
        //explicit activity
        view.getActivity ().startActivity (intent);
        view.getActivity ().finish ();
    }

    @Override
    public void editPoints() {
        editItem ("Point", string.points_column_name, string.points_column_name1);
    }

    @Override
    public void editTraces() {
        editItem ("Trace", string.traces_column_name, string.traces_column_name1);
    }

    public int editItem(String item, int nameId, int name1Id) {
        Intent intent = view.getActivity ().getIntent ();
        EditModel model = new EditModel ();
        model.clsName = item;
        model.clsPkg = "ru.android.zheka.db";
        model.name1Id = name1Id;
        model.nameId = nameId;
        intent.putExtra (EditActivity.EDIT_MODEL, model);
        intent.setAction (Intent.ACTION_VIEW);
        intent.setClass (view.getContext (), EditActivity.class);
        view.getActivity ().startActivity (intent);
        view.getActivity ().finish ();
        return 0;
    }

    @Override
    public void pointNavigate() {
        Intent intent = view.getActivity ().getIntent ();
        intent.setClass (view.getContext (), LatLngActivity.class);
        intent.setAction (Intent.ACTION_VIEW);
        view.getActivity ().startActivity (intent);
        view.getActivity ().finish ();
    }

    @Override
    public void createTrace() {
        Intent intent = view.getActivity ().getIntent ();
        intent.setClass (view.getActivity (), PointToTraceActivity.class);
        intent.setAction (Intent.ACTION_VIEW);
        view.getActivity ().startActivity (intent);
        view.getActivity ().finish ();
    }

    @Override
    public void settingsAction() {
        Intent intent = view.getActivity ().getIntent ();
        intent.setAction (Intent.ACTION_VIEW);
        intent.setClass (view.getContext (), SettingsActivity.class);
        view.getActivity ().startActivity (intent);
        view.getActivity ().finish ();
    }

    private ButtonHandler getButton(Consumer <Boolean> consumer, int nameId) {
        return new ButtonHandler (consumer
                , nameId
                , view);
    }

    @Override
    public void onResume() {
        model.getStartButton ().set (getButton (a -> settingsAction (), string.home_settings_btn));
        model.getStopButton ().set (getButton (a -> address (), string.home_address_btn));
        model.getNextButton ().set (getButton (a -> pointNavigate (), string.home_points_btn));
        model.getStartButton1 ().set (getButton (a -> editTraces (), string.home_editTrace_btn));
        model.getStopButton1 ().set (getButton (a -> editPoints (), string.home_editPoint_btn));
        model.getNextButton1 ().set (getButton (a -> createTrace (), string.home_toTrace_btn));
        model.getStartButton2 ().set (getButton (a -> geo (), string.home_geo_btn));
        model.getStopButton2 ().set (getButton (a -> info (), string.home_info_btn));
        model.inputVisible ().set (View.GONE);
    }

    @Override
    public void onDestroy() {
    }

    @Override
    public IHomeModel model() {
        return model;
    }
}
