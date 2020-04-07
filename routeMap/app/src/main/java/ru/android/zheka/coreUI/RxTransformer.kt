/*
 * Copyright (c) 2019 iFD SLS
 */
package ru.android.zheka.coreUI

import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * {Description}
 *
 * @author kee
 */
class RxTransformer  {
    companion object {
        val mainThreadScheduler: Scheduler
            get() = AndroidSchedulers.mainThread()

        val iOScheduler: Scheduler
            get() = Schedulers.io()

        fun <T> observableIoToMain(): ObservableTransformer<T, T> {
            return ObservableTransformer { objectObservable: Observable<T> ->
                objectObservable.subscribeOn(iOScheduler)
                        .observeOn(mainThreadScheduler)
            }
        }

        fun <T> singleIoToMain(): SingleTransformer<T, T> {
            return SingleTransformer { objectObservable: Single<T> ->
                objectObservable.subscribeOn(iOScheduler)
                        .observeOn(mainThreadScheduler)
            }
        }

        fun <T> maybeIoToMain(): MaybeTransformer<T, T> {
            return MaybeTransformer { objectObservable: Maybe<T> ->
                objectObservable.subscribeOn(iOScheduler)
                        .observeOn(mainThreadScheduler)
            }
        }
    }
}