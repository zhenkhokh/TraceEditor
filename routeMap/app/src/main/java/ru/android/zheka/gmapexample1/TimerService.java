package ru.android.zheka.gmapexample1;

import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedDeque;

import ru.android.zheka.db.Config;
import ru.android.zheka.db.DbFunctions;
import ru.zheka.android.timer.Recievable;
import android.app.IntentService;
import android.content.Intent;

public class TimerService extends IntentService{
	public TimerService() {
		super("TimerService");
		System.out.println("TimerService() is called");
		if (mListners==null) mListners = new ConcurrentLinkedDeque<Recievable>();
	}/*
	static TimerService instance = null;
	static TimerService getInstance(){
		if (instance==null) instance = new TimerService();
		return instance;
	}*/

	static boolean interrupted = true;
	static final String BROADCAST_ACTION = "ru.android.zheka.gmapexample1.Timer";
	static ConcurrentLinkedDeque<Recievable> mListners = new ConcurrentLinkedDeque<Recievable>();
	
	@Override
	protected void onHandleIntent(Intent intent) {
		interrupted = false;
		Config config = (Config) DbFunctions.getModelByName(DbFunctions.DEFAULT_CONFIG_NAME
				, Config.class);
		Integer ms=0;
		try{ms = new Integer(config.tenMSTime);		
		}catch(NumberFormatException e){
			e.printStackTrace();
			return;
		}
		intent.setAction(TimerService.BROADCAST_ACTION);
		long start = System.nanoTime();
		while(ms!=0 
				&& !interrupted){
			try {
				Thread.currentThread().join(ms);
			} catch (InterruptedException e) {
				interrupted = true;
			}
			long time = (System.nanoTime()-start)/1000000;
			System.out.println("current time(ms): "+time);
			sendBroadcast(intent);
			config = (Config) DbFunctions.getModelByName(DbFunctions.DEFAULT_CONFIG_NAME
					, Config.class);
			try{ ms = new Integer(config.tenMSTime);		
			}catch(NumberFormatException e){
				e.printStackTrace();
				return;
			}
		}
		interrupted = true;// finish see doc 
	}
	public boolean add(Recievable listner){
		return mListners.add(listner);
	}
	public boolean remove(Recievable listner){
		return mListners.remove(listner);
	}
	public void sendBroadcast(Intent intent){
		System.out.println("hello from sendBroadcast, mListners is "+mListners);
		for (Iterator iterator = mListners.iterator(); iterator.hasNext();) {
			Recievable l = (Recievable) iterator.next();
			System.out.println("recivabale:"+l);
			l.onReceive(getApplicationContext(), intent);			
		}
	}
}
