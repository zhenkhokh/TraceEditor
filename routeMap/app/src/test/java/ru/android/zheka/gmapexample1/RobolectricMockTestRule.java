package ru.android.zheka.gmapexample1;

import org.robolectric.RuntimeEnvironment;

import it.cosenonjaviste.daggermock.DaggerMockRule;
import ru.android.zheka.fragment.Home;
import ru.android.zheka.gmapexample1.home.TestHomeBindingModule;
import ru.android.zheka.vm.IPanelHomeVM;
import ru.android.zheka.vm.PanelHomeVM;

public class RobolectricMockTestRule extends DaggerMockRule <TestAppComponent> {
    public IPanelHomeVM vm;
    public PanelHomeVM vmObj;
    public Home homeFragment;

    public RobolectricMockTestRule() {
        super (TestAppComponent.class, new TestApplicationModule (getApplication ()),
                new TestHomeBindingModule ());
        customizeBuilder (new BuilderCustomizer <TestAppComponent.Builder> () {
            @Override
            public TestAppComponent.Builder customize(TestAppComponent.Builder builder) {
                return builder.application (getApplication ());
            }
        }).set (component -> {
                    homeFragment = new Home ();
                    component.homeSubcomponent ().create (homeFragment).inject (homeFragment);
                    vm = homeFragment.viewModel;
//                    vmObj = (PanelHomeVM) homeFragment.viewModel;
                }
        );
    }

    static public RobolectricMainApp getApplication() {
        return ((RobolectricMainApp) RuntimeEnvironment.application);
    }
}
