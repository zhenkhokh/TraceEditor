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

    public SpinnerHandler() {
        isEmpty = true;
        spinnerAdapter = new ObservableField <ArrayAdapter> ((ArrayAdapter) null);
    }

    public ObservableField <ArrayAdapter> getSpinnerAdapter() {
        return spinnerAdapter;
    }

    public void onItemSelected(AdapterView <?> parent, View view, int pos, long id) {
        if (!isEmpty && parent != null && parent.getItemAtPosition (pos) != null) {
            selectedItem = parent.getItemAtPosition (pos).toString ();

            Observable.just (selectedItem).subscribe(name -> {
                        if (map == null) {
                            methodHandler.accept (name);
                        } else {
                            methodHandler.accept (map.get (selectedItem));
                        }
                    }
                    ,this.view::showError).dispose ();
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
