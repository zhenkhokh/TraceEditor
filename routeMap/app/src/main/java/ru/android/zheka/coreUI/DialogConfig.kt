package ru.android.zheka.coreUI

import android.content.Context
import android.view.View
import io.reactivex.functions.Consumer

class DialogConfig {
    var context: Context? = null
    var poistiveBtnId = 0
    var positiveClick: View.OnClickListener? = null
    var titleId = 0
    var labelValue: String? = null
    var layoutId = 0
    var contentId = 0
    var contentValue: String? = null
    var positiveConsumer = Consumer { nothingToDo: Boolean -> }
    var negativeConsumer = Consumer { nothingToDo: Boolean -> }

    inner class Builder {
        fun build(): DialogConfig {
            return this@DialogConfig
        }

        fun context(context: Context?): Builder {
            this@DialogConfig.context = context
            return this
        }

        fun poistiveBtnId(poistiveBtnId: Int): Builder {
            this@DialogConfig.poistiveBtnId = poistiveBtnId
            return this
        }

        fun positiveClick(positiveClick: View.OnClickListener?): Builder {
            poistiveBtnId = poistiveBtnId
            return this
        }

        fun titleId(titleId: Int): Builder {
            this@DialogConfig.titleId = titleId
            return this
        }

        fun labelValue(labelValue: String?): Builder {
            this@DialogConfig.labelValue = labelValue
            return this
        }

        fun layoutId(layoutId: Int): Builder {
            this@DialogConfig.layoutId = layoutId
            return this
        }

        fun contentId(contentId: Int): Builder {
            this@DialogConfig.contentId = contentId
            return this
        }

        fun contentValue(contentValue: String?): Builder {
            this@DialogConfig.contentValue = contentValue
            return this
        }

        fun positiveConsumer(positiveConsumer: Consumer<Boolean>): Builder {
            this@DialogConfig.positiveConsumer = positiveConsumer
            return this
        }

        fun negativeConsumer(negativeConsumer: Consumer<Boolean>): Builder {
            this@DialogConfig.negativeConsumer = negativeConsumer
            return this
        }
    }

    companion object {
        fun builder(): Builder {
            return DialogConfig().Builder()
        }
    }
}