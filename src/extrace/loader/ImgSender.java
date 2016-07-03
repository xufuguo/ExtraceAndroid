package extrace.loader;

import android.app.Activity;
import extrace.net.HttpAsyncTask;
import extrace.net.HttpResponseParam.RETURN_STATUS;
import extrace.ui.main.ExTraceApplication;

public class ImgSender extends HttpAsyncTask {
	String url;
	
	public ImgSender(Activity context) {
		super(context);
		// TODO Auto-generated constructor stub
		url = ((ExTraceApplication)context.getApplication()).getDomainServiceUrl();
	}

	@Override
	public void onDataReceive(String class_name, String json_data) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusNotify(RETURN_STATUS status, String str_response) {
		// TODO Auto-generated method stub

	}
	
	public void fun(String id,String w){
		url += "savePic";
		String cc = "id="+id+"&"+"str="+w;
		
		try{
			execute(url,"POST",cc);
		}catch(Exception e){
			System.out.println(e.toString());
		}
	}

}
