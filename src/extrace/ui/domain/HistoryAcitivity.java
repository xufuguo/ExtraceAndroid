package extrace.ui.domain;

import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import com.baidu.mapapi.map.Text;

import android.app.Fragment;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import extrace.loader.ExpressLoader;
import extrace.loader.HistoryLoader;
import extrace.misc.smodel.History;
import extrace.net.IDataAdapter;
import extrace.ui.main.R;
import zxing.util.CaptureActivity;

public class HistoryAcitivity extends Fragment {
	ListView list;
	String barid;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(getArguments().containsKey("id")){
			barid = getArguments().getString("id");
		}
		
	}
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_history_acitivity, container,false);
		list =(ListView) view.findViewById(R.id.list);
		return view;

	}
	Myhand han = new Myhand();
	
	class Myhand extends Handler{
		SimpleAdapter adapter;
		public void setAdapter(SimpleAdapter adapter){
			this.adapter = adapter;
		}
		@Override
		public void handleMessage(Message msg){
			if(msg.what ==0x123){
				adapter.notifyDataSetChanged();
			}
		}
	}
	public class ListAdatper implements IDataAdapter<List<Map<String, Object>>>{
		List<Map<String,Object>> liste;
		ExecutorService exe;
		
		@Override
		public List<Map<String, Object>> getData() {
			// TODO Auto-generated method stub
			return liste;
		}
		public void setExe(ExecutorService exe){
			this.exe = exe;
		}
		@Override
		public void setData(List<Map<String, Object>> data) {
			// TODO Auto-generated method stub
			liste = data;
		}
		public void registerListener(final SimpleAdapter a){
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					while(!exe.isTerminated());
					han.sendEmptyMessage(0x123);
				}
			}).start();
		}
		@Override
		public void notifyDataSetChanged() {
			SimpleAdapter a = new SimpleAdapter(getActivity(), 
					liste, 
					R.layout.histroy_item, 
					new String[]{"time","from","to","loc"}, 
					new int[]{R.id.time,R.id.from,R.id.to,R.id.loc});
			list.setAdapter(a);
			a.notifyDataSetChanged();
			han.setAdapter(a);
			registerListener(a);
		}
	}
		
	
	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onStart(){
		super.onStart();
		if(barid != null){
			try{
				HistoryLoader loader = new HistoryLoader(getActivity(), new ListAdatper());
				loader.getTransHistory(barid);
			}catch(Exception e){
				Log.v("history", e.toString());
			}
		}
	}
}
