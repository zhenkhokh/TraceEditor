package ru.android.zheka.coreUI;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import androidx.databinding.ObservableField;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import ru.android.zheka.gmapexample1.R;

public class SpinnerHandler {
    private Map <String, String> map = null;
    private List data;
    private ObservableField <ArrayAdapter> spinnerAdapter;
    private Consumer methodHandler;
    private IActivity view;
    private boolean isEmpty;
    private Consumer enterHandler;
    private String selectedItem;

    public void setData(List data) {
        this.data = data;
    }

    public void setData(Map <String, String> map) {
        this.map = map;
        data = Arrays.asList (map.keySet ().toArray ());
    }

    public SpinnerHandler(Consumer <String> methodHandler, Consumer <String> enterHandler, List data
            , IActivity view) {
        setData (data);
        this.methodHandler = methodHandler;
        this.enterHandler = enterHandler;
        selectedItem = "";
        this.view = view;
        isEmpty = false;
        showArea ();
    }

    public SpinnerHandler(Consumer <String> methodHandler, Consumer <String> enterHandler
            , Map map, IActivity view) {
        this (methodHandler, enterHandler, Arrays.asList (map.keySet ().toArray ()), view);
        this.map = map;
    }

    SpinnerHandler() {
        isEmpty = true;
        spinnerAdapter = new ObservableField <ArrayAdapter> ((ArrayAdapter) null);
    }

    public ObservableField <ArrayAdapter> getSpinnerAdapter() {
        return spinnerAdapter;
    }

    public void onItemSelected(AdapterView <?> parent, View view, int pos, long id) {
        if (!isEmpty && parent != null && parent.getItemAtPosition (pos) != null) {
            selectedItem = parent.getItemAtPosition (pos).toString ();
            //TODO
//            Observable.just (selectedItem).subscribe (name -> {
//                methodHandler.accept (name);// error stop yesNo dialog here
//                new YesNoDialog (YesNoDialogConfig.builder ()
//                        .context (this.view.getContext ())
//                        .negativeConsumer (a -> {
//                            if (data.size () == 1)// no other option, just agree
//                            {
//                                showArea ();
//                            }
//                        }).positiveConsumer (a -> onClick ())// to business model
//                        .contentValue (name)
//                        .build ()).show ();
//            }, this.view::showError).dispose ();
        }
    }

    private void onClick() {
        if (map != null) {
            Observable.just (map.get (selectedItem)).subscribe (enterHandler, view::showError).dispose ();
        } else {
            Observable.just (selectedItem).subscribe (enterHandler, view::showError).dispose ();
        }
    }

    public void showArea() {
        if (!isEmpty) {
            ArrayAdapter <String> arrayAdapter = new ArrayAdapter (view.getContext (),
                    R.layout.spinner_base,
                    R.id.spinner_item,
                    data);
            if (spinnerAdapter == null) {
                spinnerAdapter = new ObservableField <ArrayAdapter> ();
            }
            spinnerAdapter.set (arrayAdapter);
        }
    }
}
