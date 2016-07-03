package extrace.loader;

import android.content.Context;
import extrace.net.HttpUtil;
import extrace.ui.main.ExTraceApplication;

public class LocationSender {
	private String url;
	
	public LocationSender(Context context){
		url = ((ExTraceApplication)context.getApplicationContext()).getDomainServiceUrl();
	}
	public void SendRoute(String packid,double x,double y){
		final String uri = url+ "savePackageRoutePos/"+packid+"/"+x+"/"+y;
		new Thread(){
			@Override
			public void run(){
				HttpUtil.getRequest(uri);
			}
		}.start();
	}
}	
