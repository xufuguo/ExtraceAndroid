package extrace.loader;

import java.util.List;

import com.google.gson.reflect.TypeToken;

import android.app.Activity;
import android.widget.Toast;
import extrace.misc.model.TransPackage;
import extrace.net.HttpAsyncTask;
import extrace.net.HttpResponseParam.RETURN_STATUS;
import extrace.net.IDataAdapter;
import extrace.net.JsonUtils;
import extrace.ui.main.ExTraceApplication;

public class TransPackageListLoader extends HttpAsyncTask{

	String url;
	IDataAdapter<List<TransPackage>> adapter;
	private Activity context;
	
	public TransPackageListLoader(IDataAdapter<List<TransPackage>> adpt, Activity context) {
		super(context);
		this.context = context;
		adapter = adpt;
		url = ((ExTraceApplication)context.getApplication()).getDomainServiceUrl();
	}

	@Override
	public void onDataReceive(String class_name, String json_data) {
		/**
		 * 4/21 修改
		 */
		if(json_data.equals("Deleted")){
			//adapter.getData().remove(0);	//这个地方不好处理
			Toast.makeText(context, "包裹信息已删除!", Toast.LENGTH_SHORT).show();
		}
		else{
			List<TransPackage> tPackages = JsonUtils.fromJson(json_data, new TypeToken<List<TransPackage>>(){});
			adapter.setData(tPackages);
			adapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onStatusNotify(RETURN_STATUS status, String str_response) {
		// TODO Auto-generated method stub
		
	}

}
