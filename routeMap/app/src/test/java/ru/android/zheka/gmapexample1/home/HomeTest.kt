package ru.android.zheka.gmapexample1.home

import com.activeandroid.ActiveAndroid
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.mockito.InjectMocks
import org.mockito.Mock
import ru.android.zheka.fragment.Home
import ru.android.zheka.fragment.IHome
import ru.android.zheka.gmapexample1.BaseRobolectricTest
import ru.android.zheka.model.HomeModel
import ru.android.zheka.vm.IPanelHomeVM
import ru.android.zheka.vm.PanelHomeVM

//@RunWith(RobolectricTestRunner::class)
open class HomeTest : BaseRobolectricTest() {

    val handler = HomeMockRule()

    @Mock
    var panelHomeVM: PanelHomeVM? = null

    @InjectMocks
    lateinit var homeVM: PanelHomeVM

    @Mock
    lateinit var ipanelHomeVM: IPanelHomeVM

    //@InjectFromComponent
    var home: Home? = null

    @Mock
    var homeModel: HomeModel? = null

    @Mock
    var view: IHome? = null

    @get:Rule
    val mockitoRule = handler.rule()

    @Before
    fun setup() {
        super.setUp()
    }

//    @Test
//    fun testFragment() {
//        val launcher = FragmentScenario.launchInContainer(Home::class.java)
//        launcher.moveToState(Lifecycle.State.RESUMED)
//        launcher.onFragment { fragment1: Home? -> home = fragment1 }
//        assert(home!!.viewModel != null)
//        assert(home!!.viewModel == ipanelHomeVM)
//    }
//
//    @Test
//    fun testInjectFromComponent() {
//        assert(ipanelHomeVM != null)
//        assert(ipanelHomeVM == handler.vm)
//    }

    @After
    fun close() {
        ActiveAndroid.getDatabase().close()
    }
}