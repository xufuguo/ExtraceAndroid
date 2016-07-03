package extrace.ui.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.support.v4.app.Fragment;
import android.telephony.SmsManager;
import android.util.Log;
import android.R.bool;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.CalendarContract.ExtendedProperties;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SimpleAdapter;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;
import extrace.loader.ExpressListLoader;
import extrace.loader.HistoryLoader;
import extrace.misc.model.ExpressSheet;
import extrace.ui.domain.HistoryAcitivity.ListAdatper;
import extrace.ui.main.ExTraceApplication;
import extrace.ui.main.R;

public class ExpressListFragement extends Fragment {
	private static final String ARG_EX_TYPE = "ExType";
	
	private String mExType;

	private ExpressListLoader mLoader;
	
	private Intent mIntent;  
	
	private ListView  listview;
	private Button button;
	private RadioButton radio;
	private LinearLayout line;
	private boolean listStatus = false;
	private AdapterProxy adapter = null;
	final static ExpressListFragement fragment = new ExpressListFragement();
	public Handler handler = new Handler(){
		@Override
		public void handleMessage(Message message){
			if(message.what == 0x123){
				Log.v("sadsasdasd", ""+listStatus);
				if(listStatus){
					actionOnBack();
				}
			}
		}
	};
	
	

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {	//另一种读出传入参数的方式
			mExType = getArguments().getString(ARG_EX_TYPE);
		}
	}
	

	
	
	public ExpressListFragement(){
		
	}
	public static ExpressListFragement newInstance(String ex_type){
		
		Bundle args = new Bundle();
		args.putString(ARG_EX_TYPE, ex_type);	//构造方法传入参数,使用Bundle来作为参数的容器
		fragment.setArguments(args);
		return fragment;
	}
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.deliver_express_list, container,false);
		listview =(ListView) view.findViewById(R.id.list);
		line = (LinearLayout) view.findViewById(R.id.action);
		radio = (RadioButton) view.findViewById(R.id.all);
		button = (Button) view.findViewById(R.id.button);
		TextView text = new TextView(this.getActivity());
		text.setText("快递列表空的!");
		listview.setEmptyView(text);
		return view;
	}
	private void registerListener(){
		List<Map<String, Object>> cc = new ArrayList<Map<String,Object>>();
		adapter =  new AdapterProxy(this.getActivity());
		listview.setAdapter(adapter);
		listview.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				if(!listStatus){
					for(int i = 0;i < parent.getChildCount();i++){
						View v = parent.getChildAt(i);
						((CheckBox)v.findViewById(R.id.Ok)).setVisibility(View.VISIBLE);
					}
				}
				line.setVisibility(View.VISIBLE);
				listStatus = true;
				return false;
			}

			
		});
		
		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				if(listStatus == true){
					adapter.ok.set(position, !(Boolean)adapter.ok.get(position));
					adapter.notifyDataSetChanged();
				}
				else{
					EditExpress(adapter.getItem(position));
				}
			}
			
		});
		radio.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				for(int i = 0;i < adapter.getCount();i++){
					adapter.ok.set(i, true);
				}
				adapter.notifyDataSetChanged();
			}
		});
		button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//发短信
				Toast.makeText(ExpressListFragement.this.getActivity(), 
						"短信发送中",
						Toast.LENGTH_LONG)
						.show();
				final SmsManager sms = SmsManager.getDefault();
				
				final String textsms = ((ExTraceApplication) getActivity().getApplication())
						.getSms();
				final ArrayList<String> numlist = new ArrayList<>();
				for(int i = 0;i < adapter.getCount();i++){
					if(adapter.ok.get(i)){
						String num = adapter.getData().get(i).getRecever().getTelCode();
						PendingIntent pi = PendingIntent.getActivity(ExpressListFragement.this.getActivity(), 
								0,
								new Intent(),0);
						sms.sendTextMessage(num, null,textsms , pi,null);
						Toast.makeText(ExpressListFragement.this.getActivity(),
								"给"+adapter.getData().get(i).getRecever().getName()+"发送完成", 
								Toast.LENGTH_LONG)
								.show();
						
					}
					
				}
				
				actionOnBack();
			}
		});
	}
	
	
	public void actionOnBack(){
		for(int i = 0;i < adapter.getCount();i++){
			adapter.ok.set(i, false);
			((CheckBox)listview.getChildAt(i).findViewById(R.id.Ok)).setVisibility(View.GONE);
		}
		line.setVisibility(View.GONE);
		adapter.notifyDataSetChanged();
		Log.v("ceee", "asdasdad");
		listStatus = false;
	}
	@Override
	public void onStart(){
		super.onStart();
		registerListener();
		RefreshList();
	}
	private void RefreshList()
	{
		String pkgId = ((ExTraceApplication)this.getActivity().getApplication())
				.getLoginUser().getDelivePackageID();;
		mLoader = new ExpressListLoader(adapter, this.getActivity());
		mLoader.LoadExpressListInPackage(pkgId);
		//mLoader.LoadExpressList();
	}
	
	void EditExpress(ExpressSheet es)
    {
		Intent intent = new Intent();
		intent.putExtra("Action","Edit");
		intent.putExtra("ExpressSheet",es);
		intent.setClass(this.getActivity(), ExpressEditActivity.class);
		startActivityForResult(intent, 0);  	
    }
	
	@Override
	public void onResume(){
		super.onResume();
		getView().setFocusableInTouchMode(true);
		getView().requestFocus();
		getView().setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if(event.getAction() == KeyEvent.ACTION_UP 
						&& keyCode == KeyEvent.KEYCODE_BACK){
					if(listStatus)
						actionOnBack();
				}
				return false;
			}
		});;
	}
	
}
