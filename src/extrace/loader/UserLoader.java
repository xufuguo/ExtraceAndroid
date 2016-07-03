package extrace.loader;

import com.google.gson.reflect.TypeToken;

import android.app.Activity;
import android.util.Log;
import extrace.misc.model.UserInfo;
import extrace.net.HttpAsyncTask;
import extrace.net.HttpResponseParam.RETURN_STATUS;
import extrace.net.IDataAdapter;
import extrace.net.JsonUtils;
import extrace.ui.main.ExTraceApplication;

public class UserLoader extends HttpAsyncTask {

	String url;
	IDataAdapter<UserInfo> adapter;
	private Activity context;

	public UserLoader(IDataAdapter<UserInfo> adpt, Activity context) {
		super(context);
		this.context = context;
		adapter = adpt;
		url = ((ExTraceApplication) context.getApplication()).getMiscServiceUrl();
	}	

	@Override
	public void onDataReceive(String class_name, String json_data) {
		UserInfo ci = JsonUtils.fromJson(json_data, new TypeToken<UserInfo>() {});
		Log.v("1231", ""+ci);
		Log.v("sds", ci+"");
		if (ci != null) {
			adapter.setData(ci);
			adapter.notifyDataSetChanged();
		}else {
			adapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onStatusNotify(RETURN_STATUS status, String str_response) {
		// TODO Auto-generated method stub
		if(status != RETURN_STATUS.Ok){
			adapter.notifyDataSetChanged();
		}
		
	}
	public void getUser(String uid,String psw) {
		url += "getUser/" + uid+ "/" +psw+"?_type=json";
		try {
			execute(url, "GET");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void confirm(String acc, String psw) {
		url += "doLogin/" + acc + "/" + psw;
		try {
			execute(url, "GET");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void registerUser(String name, String phone, String password) {
		UserInfo userInfo = new UserInfo();
		userInfo.setName(name);
		userInfo.setPWD(password);
		userInfo.setTelCode(phone);

		registerUser(userInfo);
	}

	public void registerUser(UserInfo uInfo) {
		String jsonObj = JsonUtils.toJson(uInfo, true);
		url += "doRegister";
		try {
			execute(url, "POST", jsonObj);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
