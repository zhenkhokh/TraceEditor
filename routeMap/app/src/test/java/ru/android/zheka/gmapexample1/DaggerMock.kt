package ru.android.zheka.gmapexample1

import it.cosenonjaviste.daggermock.DaggerMockRule

class DaggerMock<C>(val rule: DaggerMockRule<C>) {

    inline fun <reified M> providesMock(): DaggerMockRule<C> =
            rule.providesMock(M::class.java)

    inline fun <reified M> providesMock(noinline initializer: (M) -> Unit): DaggerMockRule<C> =
            rule.providesMock(M::class.java, initializer)

    fun set(componentSetter: (C) -> Unit): DaggerMockRule<C> =
            rule.set(componentSetter)

    inline fun <reified DC> setDependentComponent(noinline componentSetter: (DC) -> Unit): DaggerMockRule<C> =
            rule.set(DC::class.java, componentSetter)

    inline fun <reified S> provides(newObject: S): DaggerMockRule<C> =
            rule.provides(S::class.java, newObject)

    inline fun <reified S> provides(newObject: S, name: String): DaggerMockRule<C> =
            rule.provides(S::class.java, name, newObject)

    inline fun <reified S, reified A> providesAnnotatedObject(newObject: S): DaggerMockRule<C> =
            rule.provides(S::class.java, A::class.java, newObject)

    inline fun <reified S> provides(noinline provider: () -> S): DaggerMockRule<C> =
            rule.provides(S::class.java, provider)

    inline fun <reified S> provides(name: String, noinline provider: () -> S): DaggerMockRule<C> =
            rule.provides(S::class.java, name, provider)

    inline fun <reified S, reified A> providesAnnotatedObject(noinline provider: () -> S): DaggerMockRule<C> =
            rule.provides(S::class.java, A::class.java, provider)

    inline fun <reified S> decorates(noinline decorator: (S) -> S): DaggerMockRule<C> =
            rule.decorates(S::class.java, decorator)

    inline fun <reified CC> addComponentDependency(vararg modules: Any): DaggerMockRule<C> =
            rule.addComponentDependency(CC::class.java, *modules)

    fun addComponentDependency(parentComponentClasses: Class<*>, childComponentClasses: Class<*>, vararg modules: Any): DaggerMockRule<C> =
            rule.addComponentDependency(parentComponentClasses, childComponentClasses, *modules)

    fun <B> customizeBuilder(customizer: (B) -> B): DaggerMockRule<C> =
            rule.customizeBuilder(DaggerMockRule.BuilderCustomizer<B> {
                customizer(it)
            })

    companion object {
        inline fun <reified C> rule(vararg modules: Any, noinline init: DaggerMock<C>.() -> Unit = {}): DaggerMockRule<C> {
            val rule = DaggerMockRule(C::class.java, *modules)
            DaggerMock<C>(rule).init()
            return rule
        }
    }
}