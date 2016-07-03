package extrace.ui.main;

import android.support.v4.util.TimeUtils;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import extrace.auto.Imp.Courier;
import extrace.auto.Imp.Customer;
import extrace.auto.Imp.Transporter;
import extrace.auto.Interface.User;
import extrace.loader.UserLoader;
import extrace.misc.model.UserInfo;
import extrace.net.IDataAdapter;

public class WelcomeActivity extends Activity {
	private static final String PREFS_NAME = "ExTrace.cfg";
    SharedPreferences settings;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN ,
				WindowManager.LayoutParams. FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_welcome);
		
        settings = getSharedPreferences(PREFS_NAME, 0);
        
        if(settings != null){
        	Log.v("tag", settings+"");
        	if(settings.contains("NICE")){
            	UserLoader load = new UserLoader(new Useradapter(), this);
            	load.getUser(settings.getString("account",""), settings.getString("password",""));
            	
        	}
        	else{
        		startMain();
        	}
        }else{
        	startMain();
        }
        
	}
	void startMain(){
		new Thread(new Runnable() {
 			
 			@Override
 			public void run() {
 				// TODO Auto-generated method stub
 				try {
 					Thread.sleep(3000);
 				} catch (InterruptedException e) {
 					// TODO Auto-generated catch block
 					e.printStackTrace();
 				}
 				Intent intent = new Intent(WelcomeActivity.this,MainActivity.class);
 				startActivity(intent);
 				WelcomeActivity.this.finish();
 			}
 		}).start();
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.welcome, menu);
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
	private class Useradapter implements IDataAdapter<UserInfo> {
    	UserInfo userinfo;
    	
		@Override
		public UserInfo getData() {
			// TODO Auto-generated method stub
			return userinfo;
		}

		@Override
		public void setData(UserInfo data) {
			// TODO Auto-generated method stub
			this.userinfo = data;
		}

		@Override
		public void notifyDataSetChanged() {
			// TODO Auto-generated method stub
			User user = null;
			if(userinfo != null){
				if(userinfo.getStatus() == User.COURIER){
					user = new Courier();
					user.setUserinfo(userinfo);
					((ExTraceApplication)WelcomeActivity.this.getApplication()).setLoginUser(user);
				}
				else if(userinfo.getStatus() == User.TRANSPOTER){
					user = new Transporter();
					user.setUserinfo(userinfo);
					((ExTraceApplication)WelcomeActivity.this.getApplication()).setLoginUser(user);
				}else if(userinfo.getStatus() == user.CUSTOMER){
					user = new Customer();
					((ExTraceApplication)WelcomeActivity.this.getApplication()).setLoginUser(user);
				}
			}else{
				user = new Customer();
			}
			user.startLocationService(getApplication());
			Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);	
			startActivity(intent);
			WelcomeActivity.this.finish();
		}
    	
    }
}
