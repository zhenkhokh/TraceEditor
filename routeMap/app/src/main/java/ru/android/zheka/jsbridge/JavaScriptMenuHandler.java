package ru.android.zheka.jsbridge;

import android.app.Activity;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

public class JavaScriptMenuHandler implements JsCallable{
    JsCallable activity;

    public JavaScriptMenuHandler(JsCallable activity) {
        this.activity = activity;
    }

    @JavascriptInterface
    public void nextView(String val){
        try {
            this.activity.nextView(val);
        } catch (InstantiationException e) {
            e.printStackTrace ();
        } catch (IllegalAccessException e) {
            e.printStackTrace ();
        }
    }

	@Override
	public WebView getVebWebView() {
		throw new UnsupportedOperationException();
	}
}
