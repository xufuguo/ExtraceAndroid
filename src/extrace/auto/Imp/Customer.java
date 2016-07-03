package extrace.auto.Imp;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import extrace.auto.Interface.User;
import extrace.misc.model.UserInfo;
import extrace.ui.domain.ExpressListFragment;
import extrace.ui.main.MainFragment;
import extrace.ui.main.MypackageActivity;
import extrace.ui.main.MypackageActivity.SectionsPagerAdapter;
import extrace.ui.main.MypackageInActivity;
import extrace.ui.main.R;

public class Customer implements User {
	private int time = 0;
	private Intent intent = null;
	
	UserInfo userinfo = null;
	
	private Intent getIntent() {
		return intent;
	}
	private void setIntent(Intent intent) {
		this.intent = intent;
	}
	@Override
	public UserInfo getUserinfo() {
		return userinfo;
	}
	@Override
	public void setUserinfo(UserInfo userinfo) {
		this.userinfo = userinfo;
	}
	@Override
	public FragmentPagerAdapter getFragmentPagerAdapter(FragmentManager fm) {
		return new SectionsPagerAdapter(fm);
	}
	class SectionsPagerAdapter extends FragmentPagerAdapter {

	    public SectionsPagerAdapter(FragmentManager fm) {
	        super(fm);
	    }

	    @Override
	    public Fragment getItem(int position) {
	    	switch(position){
	    	case 0:
	            return MainFragment.newInstance();
	    	
	    	}
	    	return null;
	    }

	    @Override
	    public int getCount() {
	        return 1;
	    }

	    @Override
	    public CharSequence getPageTitle(int position) {
	        Locale l = Locale.getDefault();
	        switch (position) {
	            case 0:
	                return DateFormat.getDateTimeInstance(DateFormat.FULL,DateFormat.DEFAULT, new Locale("zh","CN")).format(new Date());
	        }
	        return null;
	    }
	}
	@Override
	public String getDelivePackageID() {
		// TODO Auto-generated method stub
		return "";
	}
	@Override
	public String getReceivePackageID() {
		// TODO Auto-generated method stub
		return "";
	}
	@Override
	public String geTransPackageID() {
		// TODO Auto-generated method stub
		return "";
	}
	@Override
	public View rootViewAdapter(View view) {
		((LinearLayout)view.findViewById(R.id.l1)).setVisibility(View.GONE);
		((TextView)view.findViewById(R.id.t1)).setVisibility(View.GONE);
		((LinearLayout)view.findViewById(R.id.l1)).setVisibility(View.GONE);
		((TextView)view.findViewById(R.id.t2)).setVisibility(View.GONE);
		((LinearLayout)view.findViewById(R.id.l2)).setVisibility(View.GONE);
		((TextView)view.findViewById(R.id.t1)).setVisibility(View.GONE);
		((LinearLayout)view.findViewById(R.id.l3)).setVisibility(View.GONE);
		((TextView)view.findViewById(R.id.t3)).setVisibility(View.GONE);
		((LinearLayout)view.findViewById(R.id.l4)).setVisibility(View.GONE);
		((TextView)view.findViewById(R.id.t4)).setVisibility(View.GONE);
		((LinearLayout)view.findViewById(R.id.l5)).setVisibility(View.GONE);
		((TextView)view.findViewById(R.id.t5)).setVisibility(View.GONE);
		return view;
	}
	
	@Override
	public Menu MenuAdapAdapter(Menu menu) {
		menu.findItem(R.id.action_my_package).setVisible(false);
		menu.findItem(R.id.action_my_message).setVisible(false);
		return menu;
	}
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return userinfo.getName();
	}
	@Override
	public FragmentPagerAdapter packageAdapter(FragmentPagerAdapter adapter, FragmentManager fm) {
		// TODO Auto-generated method stub
		return adapter;
	}
	@Override
	public int getScanTime() {
		// TODO Auto-generated method stub
		return time;
	}
	@Override
	public void startLocationService(Application context) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void stopLocationService(Application context) {
		// TODO Auto-generated method stub
	}
	
}
