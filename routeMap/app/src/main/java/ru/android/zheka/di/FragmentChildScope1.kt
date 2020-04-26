package ru.android.zheka.di

import java.lang.annotation.RetentionPolicy
import javax.inject.Scope
import java.lang.annotation.Retention

@Scope
@Retention(RetentionPolicy.RUNTIME)
annotation class FragmentChildScope1
