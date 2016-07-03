package extrace.ui.domain;

import java.util.LinkedList;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClientOption;
import com.baidu.location.service.LocationService;
import com.baidu.location.service.Utils;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import extrace.loader.LocationSender;
import extrace.loader.MessagSender;
import extrace.misc.model.Message;
import extrace.net.IDataAdapter;
import extrace.service.BackGroundLocation;
import extrace.ui.main.ExTraceApplication;
import extrace.ui.main.R;

public class SendMessageActivity extends Activity implements IDataAdapter<String>{

	String adpt;//返回数据
	ActionBar actionBar;
	Button sendButton = null;
	EditText telText = null;
	CheckBox cb = null;
	Context context = null; 
	String tel = null;
	
	LocationSender locsender = null;
	LocationService loc = null;
	LatLng point = null;
	MessagSender mSender = null;
	final String TAG = "SendMessageActivity";
	
	
	private static final String custel = "custel.cfg";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.activity_sendmessage);
		context = this;
		
		sendButton = (Button) findViewById(R.id.sendbutton);
		telText = (EditText) findViewById(R.id.custel);
		cb = (CheckBox) findViewById(R.id.issave_custel);
		
		sendButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				tel = telText.getText().toString();
				if(!checkTel(tel)){
					Toast.makeText(SendMessageActivity.this, "请正确填写手机号", Toast.LENGTH_SHORT).show();
				}
				else{
					locsender = new LocationSender(SendMessageActivity.this);
					loc = new LocationService(SendMessageActivity.this);
					Log.v("TAG","定位");
					LocationClientOption option = loc.getDefaultLocationClientOption();
					option.setScanSpan((int) ((ExTraceApplication)getApplication())
							.getLoginUser()
							.getScanTime());
					loc.setLocationOption(option);
					loc.registerListener(listener);
					loc.start();
				}
			}
		});
		
		 cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				//这里需要开文件存储用户tel
				tel = telText.getText().toString();
				SharedPreferences.Editor editor = getSharedPreferences(custel, MODE_PRIVATE).edit();
				editor.putString("tel", tel);
				editor.commit();
				//get
				/*SharedPreferences preferences = getSharedPreferences(custel, MODE_PRIVATE);
				String acc = preferences.getString("tel", "");
				Log.v("SendMessageActivity tel", acc);*/
				Toast.makeText(SendMessageActivity.this, "信息已保存", Toast.LENGTH_SHORT).show();
			}
		});
		
	}
	
	@Override
	public String getData() {
		// TODO Auto-generated method stub
		return adpt;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		this.finish();	
		return true;
	}
	@Override
	public void setData(String data) {
		// TODO Auto-generated method stub
		adpt = data;
	}

	@Override
	public void notifyDataSetChanged() {
		// TODO Auto-generated method stub
			refrash();
	}
	
	public void refrash(){
		//刷新界面
		//items.setAdapter(new MessageListAdapter(msgs, R.layout.my_message_lit , this));
		if(adpt.equals("succ"))
			Toast.makeText(SendMessageActivity.this, "消息已发出，请耐心等待(^_^)/", Toast.LENGTH_SHORT).show();
		else if(adpt.equals("unsucc"))
			Toast.makeText(SendMessageActivity.this, "揽收消息发送失败  请重试 :-(", Toast.LENGTH_SHORT).show();
	}
	boolean checkTel(String tel){
		if(tel.length() != 11)
			return false;
		else return true;
	}
	
	private Handler locHander = new Handler(){
		@Override
		public void handleMessage(android.os.Message msg){
			super.handleMessage(msg);
			try{
				BDLocation location = msg.getData().getParcelable("loc");
				int iscal = msg.getData().getInt("iscalculate");
				if (location != null) {
					point = new LatLng(location.getLatitude(), location.getLongitude());
					Message m = new Message();
					m.setTel(tel);
					((ExTraceApplication) context.getApplicationContext()).locationService.getDefaultLocationClientOption();
					Log.v(TAG+"point", point.latitude +" === "+ point.longitude);
					m.setX(point.latitude);
					m.setY(point.longitude);
					mSender = new MessagSender(SendMessageActivity.this, SendMessageActivity.this);
					mSender.newMessage(tel, m);
					Toast.makeText(SendMessageActivity.this, "正在发送...", Toast.LENGTH_SHORT).show();
					loc.stop();
				}
				else
					Toast.makeText(SendMessageActivity.this, "无法定位", Toast.LENGTH_SHORT).show();
					loc.stop();
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
	};
	
	BDLocationListener listener = new BDLocationListener() {
		@Override
		public void onReceiveLocation(BDLocation arg0) {
			if(arg0 != null){
				android.os.Message locMsg = locHander.obtainMessage();
				Bundle locData;
				locData = Algorithm(arg0);
				if (locData != null) {
					locData.putParcelable("loc", arg0);
					locMsg.setData(locData);
					locHander.sendMessage(locMsg);
				}
				
			}
		}
	};
	
	
	private LinkedList<LocationEntity> locationList =
			new LinkedList<LocationEntity>();
	private Bundle Algorithm(BDLocation location) {
		Bundle locData = new Bundle();
		double curSpeed = 0;
		if (locationList.isEmpty() || locationList.size() < 2) {
			LocationEntity temp = new LocationEntity();
			temp.location = location;
			temp.time = System.currentTimeMillis();
			locData.putInt("iscalculate", 0);
			locationList.add(temp);
		} else {
			if (locationList.size() > 5)
				locationList.removeFirst();
			double score = 0;
			for (int i = 0; i < locationList.size(); ++i) {
				LatLng lastPoint = new LatLng(locationList.get(i).location.getLatitude(),
						locationList.get(i).location.getLongitude());
				LatLng curPoint = new LatLng(location.getLatitude(), location.getLongitude());
				double distance = DistanceUtil.getDistance(lastPoint, curPoint);
				curSpeed = distance / (System.currentTimeMillis() - locationList.get(i).time) / 1000;
				score += curSpeed * Utils.EARTH_WEIGHT[i];
			}
			if (score > 0.00000999 && score < 0.00005) { 
				location.setLongitude(
						(locationList.get(locationList.size() - 1).location.getLongitude() + 
								location.getLongitude())
								/ 2);
				location.setLatitude(
						(locationList.get(locationList.size() - 1).location.getLatitude() + 
								location.getLatitude())
								/ 2);
				locData.putInt("iscalculate", 1);
			} else {
				locData.putInt("iscalculate", 0);
			}
			LocationEntity newLocation = new LocationEntity();
			newLocation.location = location;
			newLocation.time = System.currentTimeMillis();
			locationList.add(newLocation);

		}
		return locData;
	}
	
	class LocationEntity {
		BDLocation location;
		long time;
	}
}
