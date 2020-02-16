package ru.android.zheka.gmapexample1;

import org.robolectric.RuntimeEnvironment;

import it.cosenonjaviste.daggermock.DaggerMockRule;
import ru.android.zheka.vm.IPanelHomeVM;

public class RobolectricMockTestRule  extends DaggerMockRule <TestAppComponent> {
    public IPanelHomeVM vm;

    public RobolectricMockTestRule() {
        super (TestAppComponent.class, new TestApplicationModule ());
        customizeBuilder (new BuilderCustomizer <TestAppComponent.Builder> () {
            @Override
            public TestAppComponent.Builder customize(TestAppComponent.Builder builder) {
                return builder.application (getApplication());
            }
        }).set (component -> {
//                component.inject (getApplication ());//TODO
                vm = component.homeVM ();
            }
        );
    }

    static public RobolectricMainApp getApplication() {
        return ((RobolectricMainApp) RuntimeEnvironment.application);
    }
}
