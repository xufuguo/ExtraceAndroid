package extrace.ui.domain;

import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClientOption;
import com.baidu.location.service.LocationService;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import extrace.net.IDataAdapter;
import extrace.ui.main.ExTraceApplication;
import extrace.ui.main.R;

public class MyLocationActivity extends ActionBarActivity {
	BaiduMap map;
	MapView baidumap;
	TextView text1;
	LocationService service;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_location);
		baidumap = (MapView) findViewById(R.id.bmapView);
		ActionBar actionBar =getSupportActionBar();
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
		map = baidumap.getMap();
		text1 = (TextView) findViewById(R.id.textView1);
		service =((ExTraceApplication)this.getApplication()).locationService;
		LocationClientOption option = service.getDefaultLocationClientOption();
		option.setScanSpan(0);
		service.setLocationOption(option);
		service.registerListener(mylocation);
		service.start();
	}
	BDLocationListener mylocation = new BDLocationListener() {
		@Override
		public void onReceiveLocation(BDLocation arg0) {
			text1.setText(arg0.getAddrStr());
			map.setMapStatus(MapStatusUpdateFactory
					.newLatLng(new LatLng(arg0.getLatitude(),arg0.getLongitude())));
			BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_openmap_focuse_mark);
			OverlayOptions option = new MarkerOptions().position(new LatLng(arg0.getLatitude(),arg0.getLongitude())).icon(bitmap);
			map.addOverlay(option);
			map.setMapStatus(MapStatusUpdateFactory.newMapStatus(new MapStatus.Builder().zoom(15).build()));
			Log.v("shihu", arg0.getLongitude()+""+arg0.getLatitude());
		}
	};
	class textAdapter implements IDataAdapter<String>{
		TextView text1;
		String data;
		public textAdapter(TextView text1) {
			this.text1 = text1;
		}
		@Override
		public String getData() {
			return null;
		}

		@Override
		public void setData(String data) {
			
		}

		@Override
		public void notifyDataSetChanged() {
			text1.setText(data);
		}
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.my_location, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if(android.R.id.home == id){
			this.finish();
	    	return true;
		}
    	if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(map!=null){
			service.unregisterListener(mylocation);
		}
		baidumap.onDestroy();
		
	}

	@Override
	protected void onResume() {
		super.onResume();
		baidumap.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		baidumap.onPause();
	}
}
