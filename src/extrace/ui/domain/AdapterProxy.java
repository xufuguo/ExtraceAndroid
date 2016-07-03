package extrace.ui.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.gesture.GestureOverlayView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import extrace.misc.model.ExpressSheet;
import extrace.net.IDataAdapter;
import extrace.ui.main.R;

public class AdapterProxy extends BaseAdapter implements IDataAdapter<List<ExpressSheet>> {
	

	private List<ExpressSheet> es = null;
	List<Boolean> ok = new ArrayList<>();
	Context context;
	public AdapterProxy(Context context) {
		this.context = context;
	}
	@Override
	public int getCount() {
		if(es != null){
			return es.size();
		}
		return 0;
	}

	@Override
	public ExpressSheet getItem(int position){
		Log.v("::::", position+"");
		if(null != es.get(position)){
			Log.v("::s", es.get(position)+"");
			return es.get(position);
		}
			
		return null;
	}
	
	@Override
	public long getItemId(int position) {
		if(es != null){
			 es.get(position).hashCode();
		}
		return 0;
	}
	
	
	@Override
	public List<ExpressSheet> getData() {
		return es;
	}

	@Override
	public void setData(List<ExpressSheet> data) {
		es = data;
		Log.v("tagshihu", es+"");
		for(int i = 0;i < data.size();i++){
			ok.add(false);
		}
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		View v = convertView;
		hold hd = null;
		if(v==null){	
			hd = new hold();
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(R.layout.express_list_item, null);
			hd.name = (TextView)v.findViewById(R.id.name);
			hd.telCode = (TextView)v.findViewById(R.id.tel);
			hd.address = (TextView)v.findViewById(R.id.addr);
			hd.time = (TextView)v.findViewById(R.id.time);
			hd.status = (TextView)v.findViewById(R.id.st);
			hd.check = (CheckBox) v.findViewById(R.id.Ok);
			
			v.setTag(hd);
		}else{
			hd = (hold)v.getTag();
		}
		
		ExpressSheet esi = getItem(position);
		Log.v("recever", esi.getRecever()+"");
		if(es != null && esi.getRecever()!=null){
			hd.name.setText(esi.getRecever().getName());
			hd.telCode.setText(esi.getRecever().getTelCode());	//�����ߵ绰
			hd.address.setText(esi.getRecever().getAddress());
			if(esi.getAccepteTime() != null){
				//SimpleDateFormat myFmt=new SimpleDateFormat("MM��dd�� HH:mm");
				hd.time.setText(DateFormat.format("MM��dd�� HH:mm",esi.getAccepteTime()));
			}
			
		}
		
		
		String stText = "";
		switch(esi.getStatus()){
		case ExpressSheet.STATUS.STATUS_CREATED:
			stText = "������";
			break;
		case ExpressSheet.STATUS.STATUS_TRANSPORT:
			stText = "������";
			break;
		case ExpressSheet.STATUS.STATUS_DELIVERIED:
			stText = "�ѽ���";
			break;
		}
		hd.status.setText(stText);
		hd.check.setChecked(ok.get(position));
		return v;
	}
	private class hold{
		TextView name;
		TextView telCode;
		TextView address;
		TextView time;
		TextView status;
		CheckBox check;
	}
	
}
