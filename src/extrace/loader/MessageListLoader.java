package extrace.loader;

import java.util.List;

import com.google.gson.reflect.TypeToken;

import android.app.Activity;
import android.util.Log;
import extrace.misc.model.Message;
import extrace.net.HttpAsyncTask;
import extrace.net.HttpResponseParam.RETURN_STATUS;
import extrace.ui.main.ExTraceApplication;
import extrace.net.JsonUtils;
import extrace.net.IDataAdapter;;

public class MessageListLoader extends HttpAsyncTask {

	String url;
	IDataAdapter<List<Message>> adapter;//message����
	private Activity context;
	
	public MessageListLoader(IDataAdapter<List<Message>> adpt, Activity context) {
		super(context);
		adapter = adpt;
		this.context = context;
		url = ((ExTraceApplication)context.getApplication()).getMiscServiceUrl();
	}
	public void loadMessage(int uid){//���Ա������������Ϣ
		url += "loadMessageForUser/" + uid;
		try {
			execute(url, "GET");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void loadMessage(String tel){//�ͻ�������ѯ���͵���Ϣ
		url += "loadMessageForCustomer/" + tel;
		try {
			execute(url, "GET");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void isRecv(int SN){//ȷ���յ���Ϣ
		url += "isReceive/" + SN;
		try{
			execute(url, "GET");
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onDataReceive(String class_name, String json_data) {
		// TODO Auto-generated method stub
		List<Message> cstm = JsonUtils.fromJson(json_data, new TypeToken<List<Message>>(){});
		adapter.setData(cstm);
		adapter.notifyDataSetChanged();
		
	}

	@Override
	public void onStatusNotify(RETURN_STATUS status, String str_response) {
		// TODO Auto-generated method stub
		Log.i("onStatusNotify", "onStatusNotify: " + str_response);
	}

}
