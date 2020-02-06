package ru.android.zheka.coreUI;

import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import io.reactivex.Observable;
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
                .build ()).showError (throwable, consumer);
    }

    public class ErrorDialog {
        private final IActivity view;
        private DialogConfig config;

        public void showError(Throwable throwable, Consumer<Boolean> consumer) {
            // translation can be here
            config = DialogConfig.builder()
                    .labelValue("Ошибка")
                    .contentValue(throwable.getMessage ())
                    .context(view.getContext ())
                    .positiveConsumer (consumer)
                    .build();
            new ErrorDialog(config).show();
        }

        public ErrorDialog(DialogConfig config) {
            this.config = config;
            this.view = ErrorControl.this.view;
        }

        public void show() {
            View view = getDialogView ();
            getContent (view);//?
            AlertDialog dialog = configureDialog (view);
            dialog.setCanceledOnTouchOutside (false);
        }

        private AlertDialog configureDialog(View view) {
            AlertDialog dialog = new AlertDialog.Builder (config.context)
                    .setView (view)
                    .setPositiveButton (config.poistiveBtnId, null)
                    .show ();
            dialog.getButton (AlertDialog.BUTTON_POSITIVE).setOnClickListener (
                    v-> Observable.just(true).subscribe(config.positiveConsumer, this.view::showError).dispose());
            dialog.dismiss();
            return dialog;
        }

        private View getDialogView() {
            View view = View.inflate (config.context, config.layoutId, null);
            TextView titleView = getDialogTitle (view);
            String value = config.labelValue;
            if (!value.isEmpty ()) {
                titleView.setText (value);
            }
            return view;
        }

        private TextView getDialogTitle(View view) {
            return view.findViewById (config.titleId);
        }

        private TextView getContent(View view) {
            TextView content = view.findViewById (config.contentId);
            content.setText (config.contentValue);
            content.setOnClickListener (v -> content.setError (null));
            return content;
        }
    }
}
