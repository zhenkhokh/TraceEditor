package ru.android.zheka.gmapexample1.geo

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.fragment.app.testing.FragmentScenario
import androidx.lifecycle.Lifecycle
import com.activeandroid.ActiveAndroid
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.robolectric.RobolectricTestRunner
import ru.android.zheka.fragment.Geo
import ru.android.zheka.fragment.IGeo
import ru.android.zheka.gmapexample1.BaseRobolectricTest
import ru.android.zheka.model.IGeoModel
import ru.android.zheka.vm.GeoVM
import ru.android.zheka.vm.IGeoVM

@RunWith(RobolectricTestRunner::class)
open class GeoTest : BaseRobolectricTest() {

    val handler = GeoMockRule()

    @Mock
    lateinit var model: IGeoModel

    @Mock
    lateinit var vm_: IGeoVM

    @InjectMocks
    lateinit var vm: GeoVM

    @Mock
    var view: IGeo? = null

    @get:Rule
    val mockitoRule = handler.rule()

    var activity: Activity? = null

    @Before
    fun setup() {
        super.setUp()
        activity = Mockito.mock(Activity::class.java)
    }

    @Test
    fun homeTest() {
        val intent = initMock()
        vm.initPosition()
        vm.home()
        checkJump(intent)
    }

    @Test
    fun traceTest() {
        val intent = initMock()
        vm.initPosition()
        vm.pointToTrace()
        checkJump(intent)
    }

    @Test
    fun trace1Test() {
        initMock()
        lateinit var geo:Geo
        val launcher = FragmentScenario.launchInContainer(Geo::class.java)
        launcher.moveToState(Lifecycle.State.RESUMED)
        launcher.onFragment { fragment1: Geo? -> geo = fragment1!! }
        geo.viewModel.pointToTrace()
    }

    private fun initMock(): Intent {
        Mockito.`when`(view!!.activity).thenReturn(activity)
        val intent = Intent()
        intent.data = Uri.parse("geo:55.9823964,37.1690829?z=10.0?pass=false")
//        intent.component.packageName="ru.android.zheka.gmapexample1.geo"
//        intent.component = Mockito.mock(ComponentName::class.java)
//        Mockito.`when`(intent.component.flattenToShortString()).thenReturn("")
        Mockito.`when`(activity!!.intent).thenReturn(intent)
        Mockito.`when`(view!!.context).thenReturn(activity)
        return intent
    }

    private fun checkJump(intent: Intent) {
//        intent.component = ComponentName.createRelative("",
//            "ru.android.zheka.gmapexample1.MainActivity\$Companion")
//        intent.`package` = "1"
//        Mockito.verify(activity!!).startActivity(intent)
        Mockito.verify(activity!!).finish()
    }

    @After
    fun close() {
        ActiveAndroid.getDatabase().close()
    }
}