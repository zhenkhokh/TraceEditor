package ru.android.zheka.di.trace

import dagger.Binds
import dagger.Module
import ru.android.zheka.fragment.ITrace
import ru.android.zheka.fragment.Trace

@Module
abstract class TraceModule {
    @Binds
    abstract fun bindTraceFragment(view: Trace): ITrace
}