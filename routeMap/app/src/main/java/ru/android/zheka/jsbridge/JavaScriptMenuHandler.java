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
        this.activity.nextView(val);
    }

	@Override
	public WebView getVebWebView() {
		throw new UnsupportedOperationException();
	}
}
