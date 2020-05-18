package ru.android.zheka.gmapexample1.geo

import android.app.Activity
import android.content.Intent
import android.net.Uri
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
import ru.android.zheka.fragment.IGeo
import ru.android.zheka.gmapexample1.BaseRobolectricTest
import ru.android.zheka.gmapexample1.PositionInterceptor
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
    lateinit var view: IGeo

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
        vm.home()
        checkJump(intent)
    }

    private fun initMock(): Intent {
        val intent = Intent()
        vm.model = model
        Mockito.`when`(vm.model.position).thenReturn(
                Mockito.mock(PositionInterceptor::class.java)
        )
        Mockito.`when`(vm.model.position.newIntent).thenReturn(
                intent
        )
        Mockito.`when`(view!!.activity).thenReturn(activity)
        intent.data = Uri.parse("geo:55.9823964,37.1690829?z=10.0?pass=false")
        Mockito.`when`(activity!!.intent).thenReturn(intent)
        Mockito.`when`(view!!.context).thenReturn(activity)
        return intent
    }

    private fun checkJump(intent: Intent) {
        Mockito.verify(activity!!).finish()
    }

    @After
    fun close() {
        ActiveAndroid.getDatabase().close()
    }
}