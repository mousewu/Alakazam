package com.example.trackbaidu;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class PostWifiData extends Service {
	WifiBSSIDDetection mWifiBSSIDDetection;


	// �ͻ�ͨ��mBinder�ͷ������ͨ��
	private final IBinder mBinder = new WifiPostBinder();
	
	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}
	
	/**
     * ͨѶ�࣬���ںͿͻ��˰󶨡�  
     * ��ΪStepDetectionService����ͻ�������ͬһ�����̣����Բ���ҪIPC��
     */
	public class WifiPostBinder extends Binder {
		public PostWifiData getService() {
			// ����StepDetectionServiceʵ���������ͻ��Ϳ��Ե��÷���Ĺ�������
			return PostWifiData.this;
		}
	}

	public void onStart(Intent intent, int startId){
		mWifiBSSIDDetection.startScan();
	}
	public void onCreate(){
		Log.e("enter","enter");
		//mStepDetection = new StepDetection(this, this);
		mWifiBSSIDDetection = new WifiBSSIDDetection(this);
		mWifiBSSIDDetection.startScan();
	}

}
