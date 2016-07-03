package extrace.loader;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import extrace.misc.smodel.LocXY;
import extrace.net.HttpAsyncTask;
import extrace.net.HttpResponseParam.RETURN_STATUS;
import extrace.net.JsonUtils;
import extrace.ui.main.ExTraceApplication;

public class RouteLoader extends HttpAsyncTask{

	private static final int REGISTRATION_TIMEOUT = 3 * 1000;
	private static final int WAIT_TIMEOUT = 5 * 1000;

	private static final String TAG = "ExTraceHttpUtils";
	private static final String USER_AGENT = "Mozilla/4.5";
	
	private Handler handler;
	String url;
	private Context context;
	
	public RouteLoader(Activity context,Handler myhand) {
		super(context);
		handler  = myhand;
		this.context = context;
		url = ((ExTraceApplication)context.getApplicationContext()).getDomainServiceUrl();
	}

	@Override
	public void onDataReceive(String class_name, String json_data) {
		LocXY[] li = null;
		try{
			li = JsonUtils.fromJson(json_data,LocXY[].class);
			Log.v("shihu", li+"");
		}catch(Exception e){
			e.printStackTrace();
		}
		Bundle b = new Bundle();
		Message msg = new Message();
		msg.what = 0x123;
		b.putSerializable("route", li);
		msg.setData(b);
		handler.sendMessage(msg);
 	}
	@Override
	public void onStatusNotify(RETURN_STATUS status, String str_response) {
		
	}
	public void getRoute(String sheetid){
		url += "getPackageRoutePos/"+sheetid;
		try{
			execute(url,"GET");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void getRoute(String sheetid,String time){
		url += "getPackageRoutePos/"+sheetid+"/"+time;
		Log.v("shihu", "getRoute");
		try{
			execute(url,"GET");
		}catch(Exception e){
			Log.v("shihu", ""+e);
		}
		
	}
	
}


