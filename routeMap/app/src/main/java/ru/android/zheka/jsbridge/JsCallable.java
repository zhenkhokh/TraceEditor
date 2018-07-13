package ru.android.zheka.jsbridge;

import android.app.Activity;
import android.webkit.WebView;

public interface JsCallable {
 public void nextView(String val) throws InstantiationException, IllegalAccessException;
 public WebView getVebWebView();
}
