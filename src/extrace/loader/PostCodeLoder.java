package extrace.loader;

import android.app.Activity;
import android.widget.EditText;
import extrace.net.HttpAsyncTask;
import extrace.net.HttpResponseParam.RETURN_STATUS;
import extrace.ui.main.ExTraceApplication;

public class PostCodeLoder extends HttpAsyncTask {

	EditText edit;
	String url;
	
	public PostCodeLoder(Activity context,EditText edit) {
		super(context);
		this.edit = edit;
		url += ((ExTraceApplication)context.getApplication()).getDomainServiceUrl();
	}

	@Override
	public void onDataReceive(String class_name, String json_data) {
		edit.setText(json_data);
	}

	@Override
	public void onStatusNotify(RETURN_STATUS status, String str_response) {
		
	}
	
	public void  LodePostCode(String pro,String city,String town){
		url += "getPostCode/"+pro+"/" +city+"/"+town;
		try{
			execute(url,"GET");
		}catch(Exception e){
			e.printStackTrace();
		}
	}	
}
