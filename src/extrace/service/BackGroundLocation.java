package extrace.service;

import java.util.LinkedList;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClientOption;
import com.baidu.location.service.LocationService;
import com.baidu.location.service.Utils;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;
import extrace.auto.Imp.Courier;
import extrace.auto.Imp.Transporter;
import extrace.auto.Interface.User;
import extrace.loader.LocationSender;
import extrace.ui.main.ExTraceApplication;

public class BackGroundLocation extends Service{
	LocationSender locsender = null;
	 
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	LocationService loc = null;
	
	@Override
	public void onCreate(){
		super.onCreate();
		locsender = new LocationSender(BackGroundLocation.this.getApplicationContext());
		loc = new LocationService(BackGroundLocation.this);
		Log.v("定位","\\\\\\"
				+ "定位");
		Log.v("shihu",""+loc);
		LocationClientOption option = loc.getDefaultLocationClientOption();
		option.setScanSpan((int) ((ExTraceApplication)getApplication())
				.getLoginUser()
				.getScanTime()*1000);
		loc.setLocationOption(option);
		loc.registerListener(listener);
		loc.start();
		Log.v("服务：","定位服务开始！");
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		Log.v("www", "tingzhi");
		if(loc != null){
			Log.v("shihu", "停止服务！");
			loc.unregisterListener(listener);
			loc.stop();
		}
	}
	
	private Handler locHander = new Handler(){
		@Override
		public void handleMessage(Message msg){
			super.handleMessage(msg);
			try{
				BDLocation location = msg.getData().getParcelable("loc");
				int iscal = msg.getData().getInt("iscalculate");
				if (location != null) {
					LatLng point = new LatLng(location.getLatitude(), location.getLongitude());
					Log.v("nice", point.latitude +" === "+ point.longitude);
					User user = ((ExTraceApplication)BackGroundLocation.this.getApplication()).getLoginUser();
					if(user instanceof Courier){
						locsender.SendRoute(user.getReceivePackageID(),point.longitude,point.latitude);
						locsender.SendRoute(user.getDelivePackageID(), point.longitude,point.latitude);
					}else if(user instanceof Transporter){
						locsender.SendRoute(user.geTransPackageID(), point.longitude,point.latitude);
					}
					
				}
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
	};
	
	BDLocationListener listener = new BDLocationListener() {
		@Override
		public void onReceiveLocation(BDLocation arg0) {
			if(arg0 != null){
				Message locMsg = locHander.obtainMessage();
				Bundle locData;
				locData = Algorithm(arg0);
				Toast.makeText(BackGroundLocation.this.getApplicationContext(),arg0.getLatitude() + "" + arg0.getAltitude(), Toast.LENGTH_LONG);
				if (locData != null) {
					locData.putParcelable("loc", arg0);
					locMsg.setData(locData);
					locHander.sendMessage(locMsg);
				}
				
			}
		}
	};
	
	
	private LinkedList<LocationEntity> locationList =
			new LinkedList<LocationEntity>();
	private Bundle Algorithm(BDLocation location) {
		Bundle locData = new Bundle();
		double curSpeed = 0;
		if (locationList.isEmpty() || locationList.size() < 2) {
			LocationEntity temp = new LocationEntity();
			temp.location = location;
			temp.time = System.currentTimeMillis();
			locData.putInt("iscalculate", 0);
			locationList.add(temp);
		} else {
			if (locationList.size() > 5)
				locationList.removeFirst();
			double score = 0;
			for (int i = 0; i < locationList.size(); ++i) {
				LatLng lastPoint = new LatLng(locationList.get(i).location.getLatitude(),
						locationList.get(i).location.getLongitude());
				LatLng curPoint = new LatLng(location.getLatitude(), location.getLongitude());
				double distance = DistanceUtil.getDistance(lastPoint, curPoint);
				curSpeed = distance / (System.currentTimeMillis() - locationList.get(i).time) / 1000;
				score += curSpeed * Utils.EARTH_WEIGHT[i];
			}
			if (score > 0.00000999 && score < 0.00005) { 
				location.setLongitude(
						(locationList.get(locationList.size() - 1).location.getLongitude() + 
								location.getLongitude())
								/ 2);
				location.setLatitude(
						(locationList.get(locationList.size() - 1).location.getLatitude() + 
								location.getLatitude())
								/ 2);
				locData.putInt("iscalculate", 1);
			} else {
				locData.putInt("iscalculate", 0);
			}
			LocationEntity newLocation = new LocationEntity();
			newLocation.location = location;
			newLocation.time = System.currentTimeMillis();
			locationList.add(newLocation);

		}
		return locData;
	}
	
	class LocationEntity {
		BDLocation location;
		long time;
	}
	
	
}
