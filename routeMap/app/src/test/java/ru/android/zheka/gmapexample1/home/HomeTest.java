package ru.android.zheka.gmapexample1.home;

import android.app.Activity;
import android.content.Intent;

import com.activeandroid.ActiveAndroid;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.lifecycle.Lifecycle;
import ru.android.zheka.coreUI.IActivity;
import ru.android.zheka.fragment.Home;
import ru.android.zheka.gmapexample1.BaseRobolectricTest;
import ru.android.zheka.gmapexample1.RobolectricMockTestRule;
import ru.android.zheka.model.HomeModel;
import ru.android.zheka.vm.IPanelHomeVM;
import ru.android.zheka.vm.PanelHomeVM;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class HomeTest extends BaseRobolectricTest {
    @Mock
    PanelHomeVM panelHomeVM;

    @InjectMocks
    PanelHomeVM homeVM;

    @Mock
    IPanelHomeVM ipanelHomeVM;
    //@InjectFromComponent
    Home home;
    @Mock
    HomeModel homeModel;

    @Mock
    IActivity view;

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
        Activity activity = mock (Activity.class);
        when (view.getActivity ()).thenReturn (activity);
        Intent intent = new Intent ();
        when (activity.getIntent ()).thenReturn (intent);
        when (view.getContext ()).thenReturn (activity);
        homeVM.editTraces ();
//        panelHomeVM.editTraces ();
//        verify (panelHomeVM).editItem (anyString (),anyByte (),anyByte ());//("Trace", string.traces_column_name, string.traces_column_name1);
        verify (activity).startActivity (intent);
    }

    @After
    public void close() {
        ActiveAndroid.getDatabase ().close ();
    }
}
