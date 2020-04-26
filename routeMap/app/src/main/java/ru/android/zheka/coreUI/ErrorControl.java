package ru.android.zheka.coreUI;

import io.reactivex.functions.Consumer;
import ru.android.zheka.gmapexample1.R;

public class ErrorControl {
    private IActivity view;

    public ErrorControl(IActivity view) {
        this.view = view;
    }

    public void showError(Throwable throwable,  Consumer<Boolean> consumer) {
        new ErrorDialog (DialogConfig.builder ().contentId (R.layout.alert_dialog)
                .context (view.getContext ())
                .poistiveBtnId (R.string.ok_save_point)
                .build (), view).showError (throwable, consumer);
    }
}
