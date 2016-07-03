package extrace.loader;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.baidu.location.service.BaiduApiUtils;
import com.google.gson.reflect.TypeToken;

import android.app.Activity;
import android.util.Log;
import android.widget.ListView;
import extrace.misc.smodel.History;
import extrace.net.HttpAsyncTask;
import extrace.net.HttpResponseParam.RETURN_STATUS;
import extrace.net.JsonUtils;
import extrace.ui.domain.HistoryAcitivity;
import extrace.ui.main.ExTraceApplication;

public class HistoryLoader extends HttpAsyncTask{
	String url;
	HistoryAcitivity.ListAdatper adapter;
	ListView listView;
	
	public HistoryLoader(Activity context,HistoryAcitivity.ListAdatper adapter) {
		super(context);
		url =((ExTraceApplication)context.getApplication()).getDomainServiceUrl();
		this.adapter = adapter;
	}

	@Override
	public void onDataReceive(String class_name, String json_data) {
		// TODO Auto-generated method stub
		Log.v("data", json_data);
		List<History> li = JsonUtils.fromJson(json_data, new TypeToken<List<History>>(){});
		List<Map<String, Object>> res = new ArrayList<Map<String,Object>>();
		ExecutorService exe = Executors.newCachedThreadPool();
		
		for(final History ni:li){
			final Map<String,Object> item = new HashMap<>();
			exe.submit(new Runnable() {
				
				@Override
				public void run() {
					item.put("loc", BaiduApiUtils.getFormatted_address(ni.getY(), ni.getX()));
					Log.v("ok","ok");
				}
			});
			
			item.put("from", ni.getNameFrom().toString());
			Log.v("sss", ni.getNameTo());
			
			item.put("to", ni.getNameTo().toString());
			item.put("time", DateFormat.getDateTimeInstance(DateFormat.DEFAULT,DateFormat.DEFAULT).format(ni.getTime()));
			res.add(item);
		}
		exe.shutdown();
		adapter.setData(res);
		adapter.setExe(exe);
		adapter.notifyDataSetChanged();
	}
	
	@Override
	public void onStatusNotify(RETURN_STATUS status, String str_response) {
		// TODO Auto-generated method stub
		
	}
	
	public void getTransHistory(String id){
		url += "getTransHistory/"+id;
		try{
			execute(url,"GET");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
}
