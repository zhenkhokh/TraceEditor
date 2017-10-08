package ru.android.zheka.gmapexample1;

import ru.android.zheka.gmapexample1.R;

public class TimerFragment extends TravelModeFragment{

	public TimerFragment(int arrayDataID, int userDataID, String fConfigName) {
		super(arrayDataID, userDataID, fConfigName);
	}
	public TimerFragment(){
		this(SettingsActivity.timerArrayDataID
				, SettingsActivity.timerUserDataID
				, SettingsActivity.timerConfigName);
	}
}
