package ru.android.zheka.gmapexample1.home;

import com.activeandroid.ActiveAndroid;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.lifecycle.Lifecycle;
import ru.android.zheka.fragment.Home;
import ru.android.zheka.gmapexample1.BaseRobolectricTest;
import ru.android.zheka.gmapexample1.RobolectricMockTestRule;
import ru.android.zheka.vm.IPanelHomeVM;
import ru.android.zheka.vm.PanelHomeVM;

import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
public class HomeTest extends BaseRobolectricTest {
    @Mock
    PanelHomeVM panelHomeVM;
    @Mock
    IPanelHomeVM ipanelHomeVM;
    //@InjectFromComponent
    Home home;

    @Rule
    public final RobolectricMockTestRule mockitoRule = new RobolectricMockTestRule ();

    @Before
    public void setup() {
        super.setUp ();
    }

    @Test
    public void testFragment() {
        FragmentScenario <Home> launcher = FragmentScenario.launchInContainer (Home.class);
        launcher.moveToState (Lifecycle.State.RESUMED);
        launcher.onFragment (fragment1 ->
            home = fragment1
        );
        assert home.viewModel != null;
        assert !home.viewModel.equals (ipanelHomeVM);
    }

    @Test
    public void testInjectFromComponent() {
        assert ipanelHomeVM != null;
        assert ipanelHomeVM.equals (mockitoRule.vm);
    }

    @Test
    public void testPanelSettings() {
        mockitoRule.vm.editTraces ();
        verify (ipanelHomeVM).editTraces ();
        System.out.println ("Home :" + home);
        System.out.println ("panelHomeVM :" + panelHomeVM);
        System.out.println ("ipanelHomeVM :" + ipanelHomeVM);
    }

    @After
    public void close() {
        ActiveAndroid.getDatabase ().close ();
    }
}
