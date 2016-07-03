package extrace.auto.Interface;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.Menu;
import android.view.View;
import extrace.misc.model.UserInfo;

public interface User {
	public static final int CUSTOMER 	=0;
	public static final int COURIER		=1;
	public static final int TRANSPOTER  =2;
	public FragmentPagerAdapter getFragmentPagerAdapter(FragmentManager fm);
	public UserInfo getUserinfo();
	public void setUserinfo(UserInfo userinfo);
	public String getDelivePackageID();
	public String getReceivePackageID();
	public String geTransPackageID();
	public String getName();
	public View rootViewAdapter(View view);
	public Menu MenuAdapAdapter(Menu menu);
	public FragmentPagerAdapter packageAdapter(final FragmentPagerAdapter adapter,FragmentManager fm);
	public int getScanTime();
	public void startLocationService(Application context);
	public void stopLocationService(Application context);
}
