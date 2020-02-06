package ru.android.zheka.core;

public class AktionMessage {
    private Object[] params;

    public Object[] getParams() {
        return params;
    }

    public Enum getMsg() {
        return msg;
    }

    private Enum msg;

    public AktionMessage(Enum msg, Object... params) {
        this.params = params;
        this.msg = msg;
    }
}