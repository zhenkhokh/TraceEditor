package ru.android.zheka.gmapexample1.home

import android.app.Activity
import android.content.Intent
import androidx.fragment.app.testing.FragmentScenario
import androidx.lifecycle.Lifecycle
import com.activeandroid.ActiveAndroid
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.*
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.robolectric.RobolectricTestRunner
import ru.android.zheka.coreUI.IActivity
import ru.android.zheka.fragment.Home
import ru.android.zheka.gmapexample1.BaseRobolectricTest
import ru.android.zheka.model.HomeModel
import ru.android.zheka.vm.IPanelHomeVM
import ru.android.zheka.vm.PanelHomeVM

@RunWith(RobolectricTestRunner::class)
open class HomeTest : BaseRobolectricTest() {

    val handler = HomeMockRule()

    @Mock
    var panelHomeVM: PanelHomeVM? = null

    @InjectMocks
    var homeVM: PanelHomeVM? = null

    @Mock
    var ipanelHomeVM: IPanelHomeVM? = null

    //@InjectFromComponent
    var home: Home? = null

    @Mock
    var homeModel: HomeModel? = null

    @Mock
    var view: IActivity? = null

    @get:Rule
//    @JvmField
//    @Rule
    val mockitoRule = handler.rule()

    @Before
    fun setup() {
        super.setUp()
    }

    @Test
    fun testFragment() {
        val launcher = FragmentScenario.launchInContainer(Home::class.java)
        launcher.moveToState(Lifecycle.State.RESUMED)
        launcher.onFragment { fragment1: Home? -> home = fragment1 }
        assert(home!!.viewModel != null)
        assert(home!!.viewModel == ipanelHomeVM)
    }

    @Test
    fun testInjectFromComponent() {
        assert(ipanelHomeVM != null)
        assert(ipanelHomeVM == handler.vm)
    }

    @Test
    fun testPanelSettings() {
        val activity = Mockito.mock(Activity::class.java)
        Mockito.`when`(view!!.activity).thenReturn(activity)
        val intent = Intent()
        Mockito.`when`(activity.intent).thenReturn(intent)
        Mockito.`when`(view!!.context).thenReturn(activity)
        homeVM!!.editTraces()
        Mockito.verify(activity).startActivity(intent)
//        panelHomeVM!!.editTraces ()
//        Mockito.verify (panelHomeVM)!!.editItem (anyString (),anyInt (), anyInt ())//("Trace", string.traces_column_name, string.traces_column_name1);
    }

    @After
    fun close() {
        ActiveAndroid.getDatabase().close()
    }
}