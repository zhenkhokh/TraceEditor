package ru.android.zheka.coreUI;

import android.content.Context;
import android.view.View;

import io.reactivex.functions.Consumer;

public class DialogConfig {

    public Context context;
    public int poistiveBtnId;
    public View.OnClickListener positiveClick;
    public int titleId;
    public String labelValue;
    public int layoutId;
    public int contentId;
    public String contentValue;
    public Consumer <Boolean> positiveConsumer = nothingToDo -> {
    };
    public Consumer <Boolean> negativeConsumer = nothingToDo -> {
    };

    public static Builder builder() {
        return new DialogConfig ().new Builder ();
    }

    public class Builder {
        private Builder() {
        }

        public DialogConfig build() {
            return DialogConfig.this;
        }

        public Builder context(Context context) {
            DialogConfig.this.context = context;
            return this;
        }

        public Builder poistiveBtnId(int poistiveBtnId) {
            DialogConfig.this.poistiveBtnId = poistiveBtnId;
            return this;
        }

        public Builder positiveClick(View.OnClickListener positiveClick) {
            DialogConfig.this.poistiveBtnId = poistiveBtnId;
            return this;
        }

        public Builder titleId(int titleId) {
            DialogConfig.this.titleId = titleId;
            return this;
        }

        public Builder labelValue(String labelValue) {
            DialogConfig.this.labelValue = labelValue;
            return this;
        }

        public Builder layoutId(int layoutId) {
            DialogConfig.this.layoutId = layoutId;
            return this;
        }

        public Builder contentId(int contentId) {
            DialogConfig.this.contentId = contentId;
            return this;
        }

        public Builder contentValue(String contentValue) {
            DialogConfig.this.contentValue = contentValue;
            return this;
        }

        public Builder positiveConsumer(Consumer <Boolean> positiveConsumer) {
            DialogConfig.this.positiveConsumer = positiveConsumer;
            return this;
        }

        public Builder negativeConsumer(Consumer <Boolean> negativeConsumer) {
            DialogConfig.this.negativeConsumer = negativeConsumer;
            return this;
        }
    }
}