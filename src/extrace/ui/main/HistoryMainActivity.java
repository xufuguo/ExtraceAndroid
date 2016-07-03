package extrace.ui.main;

import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.ActionBarActivity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.ToggleButton;
import extrace.loader.RouteLoader;
import extrace.ui.domain.HistoryAcitivity;
import extrace.ui.domain.MapActivity;
import zxing.util.CaptureActivity;

public class HistoryMainActivity extends ActionBarActivity {
	
	
	ToggleButton but;
	String barid;
	FragmentManager manager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_history_main);
		but = (ToggleButton) findViewById(R.id.change);
		manager  = getFragmentManager();
		
		but.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(barid != null){
					Bundle arguments = new Bundle();
					arguments.putString("id", barid);
					if(isChecked){
						startHistoryFragement(arguments);
					}
					else{
						startMapFragement(arguments);
					}
					
				}
				
			}
		});
		Intent intent = new Intent(this, CaptureActivity.class);
		startActivityForResult(intent, 0);
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data){
		if(resultCode == RESULT_OK){
			if(data.hasExtra("BarCode")){
				barid = data.getStringExtra("BarCode");
				Bundle arguments = new Bundle();
				arguments.putString("id", barid);
				startHistoryFragement(arguments);
			}
		}else{
			this.finish();
		}
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.history_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	private void startHistoryFragement(Bundle bundle){
		Fragment fra = new HistoryAcitivity();
		fra.setArguments(bundle);
		FragmentTransaction transaction = manager.beginTransaction();
		transaction.replace(R.id.container, fra);
		transaction.addToBackStack(null);
		transaction.commit();
	}
	private void startMapFragement(Bundle bundle){
		Fragment fra = new MapActivity();
		fra.setArguments(bundle);
		FragmentTransaction transaction = manager.beginTransaction();
		transaction.replace(R.id.container, fra);
		transaction.addToBackStack(null);
		transaction.commit();
	}
}
