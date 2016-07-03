package extrace.loader;

import java.util.List;

import com.google.gson.reflect.TypeToken;

import android.app.Activity;
import android.util.Log;
import extrace.misc.model.Message;
import extrace.net.HttpAsyncTask;
import extrace.net.IDataAdapter;
import extrace.net.HttpResponseParam.RETURN_STATUS;
import extrace.ui.main.ExTraceApplication;

public class MessagSender extends HttpAsyncTask {
	
	String url;
	IDataAdapter<String> adapter;//message数据
	private Activity context;
	
	public MessagSender(IDataAdapter<String> adpt, Activity context) {
		super(context);
		adapter = adpt;
		this.context = context;
		url = ((ExTraceApplication)context.getApplication()).getMiscServiceUrl();
	}
	@Override
	public void onDataReceive(String class_name, String json_data) {
		// TODO Auto-generated method stub
		Log.v("sendOnRevc/zong", json_data);
		adapter.setData(json_data);
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onStatusNotify(RETURN_STATUS status, String str_response) {
		// TODO Auto-generated method stub
		Log.i("onStatusNotify", "onStatusNotify: " + str_response);
	}

	public void newMessage(String tel, Message msg){//客户用,发送上门取件请求
		//int uid = ((ExTraceApplication) context.getApplication()).getLoginUser().getUID();
		url += "recvMessage/" + tel + "/" +  msg.getY() + "/" + msg.getX() ;
		try{
			execute(url, "GET");
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	

}
