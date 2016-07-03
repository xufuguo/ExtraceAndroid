package extrace.loader;

import java.util.List;

import org.json.JSONArray;

import com.google.gson.reflect.TypeToken;

import android.app.Activity;
import android.widget.Toast;
import extrace.misc.model.TransPackage;
import extrace.misc.smodel.NamePair;
import extrace.net.HttpAsyncTask;
import extrace.net.HttpUtil;
import extrace.net.HttpResponseParam.RETURN_STATUS;
import extrace.net.IDataAdapter;
import extrace.net.JSONObjectUtils;
import extrace.net.JsonUtils;
import extrace.ui.main.ExTraceApplication;

public class TransPackageLoader extends HttpAsyncTask {

	String url;
	IDataAdapter adapter;
	private Activity context;

	public TransPackageLoader(IDataAdapter adpt, Activity context) {
		super(context);
		this.context = context;
		adapter = adpt;
		url = ((ExTraceApplication) context.getApplication()).getDomainServiceUrl();
	}

	@Override
	public void onDataReceive(String class_name, String json_data) {
		if (class_name.equals("TransPackage")) {
			TransPackage ci = JsonUtils.fromJson(json_data, new TypeToken<TransPackage>() {});
			adapter.setData(ci);
			adapter.notifyDataSetChanged();
		} else if (class_name.equals("R_TransPackage")) // �������
		{
			TransPackage ci = JsonUtils.fromJson(json_data, new TypeToken<TransPackage>() {
			});
			((TransPackage)adapter.getData()).setID(ci.getID());
			((TransPackage)adapter.getData()).onSave();
			adapter.notifyDataSetChanged();
			Toast.makeText(context, "保存成功!", Toast.LENGTH_SHORT).show();
		}
		else if(class_name.equals(" "))
		{
			NamePair a =  JsonUtils.fromJson(json_data, new TypeToken<NamePair>(){});
			adapter.setData(a);
			adapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onStatusNotify(RETURN_STATUS status, String str_response) {
		// TODO Auto-generated method stub

	}

	public void Load(String id) {
		url += "getTransPackage/" + id + "?_type=json";
		try {
			execute(url, "GET");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// �����ִ�еĶ���
	public boolean Open(String id) {
		url += "unpackTransPackage/" + id + "?_type=json";
		try {
			execute(url, "GET");
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	// ���
	public void New(String id) {
		int uid = ((ExTraceApplication) context.getApplication()).getLoginUser().getUserinfo().getUID();
		url += "newTransPackage/id/" + id + "/uid/" + uid + "?_type=json";
		try {
			execute(url, "GET");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void Save(TransPackage tp) {
		String jsonObj = JsonUtils.toJson(tp, true);
		url += "saveTransPackage";
		try {
			execute(url, "POST", jsonObj);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void moveEsToPk(List eslist, String pkId) {
		StringBuilder sb = new StringBuilder();
		for(int i = 0;i < eslist.size();i++){
			if(i != 0)
				sb.append(","+(String)eslist.get(i));
			else
				sb.append((String)eslist.get(i));
		}
		String src=HttpUtil.postPagram("pkId",pkId,"list",sb.toString());
		
		url += "MoveExpressIntoPackage";
		try {
			execute(url, "POST",src);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void getTransNamePair(NamePair a){
		url += "getTransNamePair/"+a.getA()+"/"+a.getB();
		try{
			execute(url,"GET");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
