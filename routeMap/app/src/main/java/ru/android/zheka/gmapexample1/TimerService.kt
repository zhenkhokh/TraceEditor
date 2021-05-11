package ru.android.zheka.gmapexample1

import android.app.IntentService
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import ru.android.zheka.db.Config
import ru.android.zheka.db.DbFunctions
import ru.zheka.android.timer.Recievable
import java.util.concurrent.ConcurrentLinkedDeque

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class TimerService : IntentService("TimerService") {
    override fun onHandleIntent(intent: Intent) {
        interrupted = false
        var config = DbFunctions.getModelByName(DbFunctions.DEFAULT_CONFIG_NAME
                , Config::class.java) as Config
        var ms:Int
        try {
            ms = config.tenMSTime?.toInt()?:0
        } catch (e: NumberFormatException) {
            e.printStackTrace()
            return
        }
        intent.action = BROADCAST_ACTION
        val start = System.nanoTime()
        while (ms != 0
                && !interrupted) {
            try {
                Thread.currentThread().join(ms.toLong())
            } catch (e: InterruptedException) {
                interrupted = true
            }
            val time = (System.nanoTime() - start) / 1000000
            println("current time(ms): $time")
            sendBroadcast(intent)
            config = DbFunctions.getModelByName(DbFunctions.DEFAULT_CONFIG_NAME
                    , Config::class.java) as Config
            try {
                ms = config.tenMSTime?.toInt()?:0
            } catch (e: NumberFormatException) {
                e.printStackTrace()
                return
            }
        }
        interrupted = true // finish see doc
    }

    fun add(listner: Recievable): Boolean {
        return mListners!!.add(listner)
    }

    fun remove(listner: Recievable): Boolean {
        return mListners!!.remove(listner)
    }

    override fun sendBroadcast(intent: Intent) {
        println("hello from sendBroadcast, mListners is $mListners")
        val iterator: Iterator<*> = mListners!!.iterator()
        while (iterator.hasNext()) {
            val l = iterator.next() as Recievable
            println("recivabale:$l")
            l.onReceive(applicationContext, intent)
        }
    }

    companion object {
        var interrupted = true
        const val BROADCAST_ACTION = "ru.android.zheka.gmapexample1.Timer"
        var mListners: ConcurrentLinkedDeque<Recievable>? = ConcurrentLinkedDeque()
    }

    init {
        println("TimerService() is called")
        if (mListners == null) mListners = ConcurrentLinkedDeque()
    } /*
	static TimerService instance = null;
	static TimerService getInstance(){
		if (instance==null) instance = new TimerService();
		return instance;
	}*/
}