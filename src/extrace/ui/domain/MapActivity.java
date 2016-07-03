package extrace.ui.domain;

import android.support.v4.util.TimeUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import java.io.PrintWriter;
import java.security.Timestamp;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.location.service.LocationService;
import com.baidu.location.service.Utils;
import com.baidu.location.service.WriteLog;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import extrace.loader.RouteLoader;
import extrace.misc.model.PackageRoute;
import extrace.misc.model.TransHistory;
import extrace.misc.smodel.LocXY;
import extrace.ui.main.ExTraceApplication;
import extrace.ui.main.R;
import zxing.util.CaptureActivity;

@SuppressLint("NewApi")
public class MapActivity extends Fragment {

	private MapView mapview;
	private BaiduMap baidumap;
	private RouteLoader Rloder;
	private String id;
	private Timer timer = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(getArguments().containsKey("id")){
			id = getArguments().getString("id");
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data){
		if(requestCode == 15){
			if(data.hasExtra("BarCode")){
				final String id = data.getStringExtra("BarCode");
				try{
					Rloder = new RouteLoader(getActivity(),myhand);
					Rloder.getRoute(id);
//					new Timer().schedule(new TimerTask() {
//						@Override
//						public void run() {
//							Rloder = new RouteLoader(MapActivity.this,myhand);
//							Rloder.getRoute(id,""+(System.currentTimeMillis()-30*1000));
//						}
//					}, 30*1000,30*1000);
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_map, container,false);
		mapview = (MapView) view.findViewById(R.id.bmapView);
		baidumap = mapview.getMap();
		baidumap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
		baidumap.setMapStatus(MapStatusUpdateFactory.zoomTo(18));
		return view;

	}
	final Handler myhand = new Handler(){
		Marker las = null;
		List<LatLng> li = new ArrayList<>();
		@Override
		public void handleMessage(Message msg){
			if(msg.what == 0x123){
				LocXY[] lit = (LocXY[]) msg.getData().getSerializable("route");
				

				for(LocXY t: lit){
					li.add(new LatLng(t.getY(),t.getX()));
				}
				
				if(li.size() >= 1){
					if(li.size() == 1){
						li.add(li.get(0));
					}
				
					BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_openmap_focuse_mark);
					PolylineOptions linetoption = new PolylineOptions().color(Color.RED)
							.dottedLine(true).visible(true).focus(true).points(li);
					baidumap.addOverlay(linetoption);
					if(las !=null){
						las.remove();
					}
					OverlayOptions option = new MarkerOptions().position(li.get(li.size()-1)).icon(bitmap);
					las  = (Marker) baidumap.addOverlay(option);
					baidumap.setMapStatus(MapStatusUpdateFactory.newMapStatus(new MapStatus.Builder().zoom(baidumap.getMapStatus().zoom).build()));
					baidumap.setMapStatus(MapStatusUpdateFactory.newLatLng(li.get(li.size()-1)));
					if(li.size() >= 1){
						li = li.subList(li.size()-1, li.size());
					}
				}
			}
		}
	};
	
	final Handler sendhand = new Handler(){
		public void handleMessage(Message msg){
			if(msg.what == 0x124){
				RouteLoader Rloder = new RouteLoader(MapActivity.this.getActivity(),myhand);
				Rloder.getRoute(id,""+(System.currentTimeMillis()-30*1000));
			}
			
		}
	};
	
	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if(id == android.R.id.home){
			return true;
		}
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onStart(){
		super.onStart();
		
		if(id != null){
			try{
				Rloder = new RouteLoader(getActivity(),myhand);
				Rloder.getRoute(id);
				timer =new Timer();
				timer.schedule(new TimerTask() {
					@Override
					public void run() {
						sendhand.sendEmptyMessage(0x124);
					}
				}, 30*1000,30*1000);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
		mapview.onDestroy();
		timer.cancel();
		
	}

	@Override
	public void onResume() {
		super.onResume();
		mapview.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
		mapview.onPause();
		
	}

}
