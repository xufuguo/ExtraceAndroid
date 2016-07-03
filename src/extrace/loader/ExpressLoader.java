package extrace.loader;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;

import extrace.misc.model.ExpressSheet;
import extrace.net.HttpAsyncTask;
import extrace.net.HttpResponseParam.RETURN_STATUS;
import extrace.net.IDataAdapter;
import extrace.net.JsonUtils;
import extrace.ui.main.ExTraceApplication;

public class ExpressLoader extends HttpAsyncTask {

	String url;
	IDataAdapter<ExpressSheet> adapter;
	private Activity context;

	public ExpressLoader(IDataAdapter<ExpressSheet> adpt, Activity context) {
		super(context);
		this.context = context;
		adapter = adpt;
		url = ((ExTraceApplication) context.getApplication()).getDomainServiceUrl();
	}

	@Override
	public void onDataReceive(String class_name, String json_data) {
		if (class_name.equals("ExpressSheet")) {
			Log.v("sasd", json_data);
			ExpressSheet ci = JsonUtils.fromJson(json_data, new TypeToken<ExpressSheet>() {});
			//Log.v("estimeLode", ci.getAccepteTime()+"");
			adapter.setData(ci);
			adapter.notifyDataSetChanged();
		} else if (class_name.equals("E_ExpressSheet")) // �Ѿ�����
		{
			Toast.makeText(context, json_data, Toast.LENGTH_SHORT).show();
			// ExpressSheet ci = JsonUtils.fromJson(json_data, new
			// TypeToken<ExpressSheet>(){});
			// adapter.setData(ci);
			// adapter.notifyDataSetChanged();
			// Toast.makeText(context, "����˵���Ϣ�Ѿ�����!",
			// Toast.LENGTH_SHORT).show();
		} else if (class_name.equals("R_ExpressSheet")) // �������
		{
			ExpressSheet ci = JsonUtils.fromJson(json_data, new TypeToken<ExpressSheet>() {
			});
			adapter.getData().setID(ci.getID());
			adapter.getData().onSave();
			adapter.notifyDataSetChanged();
			Toast.makeText(context, "����˵���Ϣ�������!", Toast.LENGTH_SHORT).show();
		} else if (class_name.equals("...")) {
			String issuccess = JsonUtils.fromJson(json_data, new TypeToken<String>() {
			});
			adapter.setData(null);
			adapter.notifyDataSetChanged();
			if (issuccess.equals("true")) {
				Toast.makeText(context, "�Ƴ��ɹ�!", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(context, "�Ƴ�ʧ��!", Toast.LENGTH_SHORT).show();
			}

		} else {

		}
	}

	@Override
	public void onStatusNotify(RETURN_STATUS status, String str_response) {
		// TODO Auto-generated method stub

	}

	public void Load(String id) {
		url += "getExpressSheet/" + id + "?_type=json";
		try {
			execute(url, "GET");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void New(String id) {
		int uid = ((ExTraceApplication)context.getApplication()).getLoginUser().getUserinfo().getUID();
		Log.v("uid", id +"   "+uid);
		url += "newExpressSheet/id/" + id + "/uid/" + uid + "?_type=json";
		try {
			execute(url, "GET");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void Save(ExpressSheet es) {
		String jsonObj = JsonUtils.toJson(es, true);
		url += "saveExpressSheet";
		try {
			execute(url, "POST", jsonObj);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void Receive(String id) {
		int uid = ((ExTraceApplication) context.getApplication()).getLoginUser().getUserinfo().getUID();
		url += "receiveExpressSheetId/id/" + id + "/uid/" + uid + "?_type=json";
		try {
			execute(url, "GET");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void Delivery(String id) {
		int uid = ((ExTraceApplication) context.getApplication()).getLoginUser().getUserinfo().getUID();
		url += "deliveryExpressSheetId/id/" + id + "/uid/" + uid + "?_type=json";
		try {
			execute(url, "GET");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void MoveExpressFromPackage(String esID, String pkId) {
		url += "MoveExpressFromPackage/id/" + esID + "/sourcePkgId/" + pkId + "?_type=json";
		try {
			execute(url, "GET");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
