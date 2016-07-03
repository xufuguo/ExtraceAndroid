package extrace.ui.main;

import extrace.auto.Imp.Courier;
import extrace.auto.Imp.Customer;
import extrace.auto.Imp.Transporter;
import extrace.auto.Interface.User;
import extrace.loader.UserLoader;
import extrace.misc.model.CustomerInfo;
import extrace.misc.model.UserInfo;
import extrace.net.IDataAdapter;
import extrace.service.BackGroundLocation;
import extrace.service.MessageService;

import com.baidu.location.service.LocationService;
import com.baidu.mapapi.SDKInitializer;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class ExTraceApplication extends Application {
	private static final String PREFS_NAME = "ExTrace.cfg";
    SharedPreferences settings;// = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
//	String mServerUrl;
//	String mMiscService,mDomainService;
    User user;
    
    
    public LocationService locationService;
    public BackGroundLocation backser=null;
    
	public void setLoginUser(User user) {
		this.user = user;
	}
    public String getServerUrl() {  
        return settings.getString("ServerUrl", "");  
    }  
    public String getMiscServiceUrl() {  
        return getServerUrl() + settings.getString("MiscService", ""); 
    }  
    public String getDomainServiceUrl() {  
        return getServerUrl() + settings.getString("DomainService", ""); 
    }  
    public String getSms(){
    	return settings.getString("sms", "");
    }
    public User getLoginUser(){
    	return user;
    }
    @Override  
    public void onCreate() {  
        super.onCreate();  
        settings = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        setLoginUser(new Customer());
		SDKInitializer.initialize(this);
		locationService = new LocationService(getApplicationContext());
		Log.v("ExtraceApp", "qidongMessageService");
		this.startService((new Intent(this, MessageService.class)));
    }  
    
    @Override
	public void onTerminate() {  
        super.onTerminate();  
        //save data of the map  
        
    }  
    
    
    
    
    
    
}
