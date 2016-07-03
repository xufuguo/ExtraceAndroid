package extrace.auto.Imp;

import java.util.Locale;

import android.R.integer;
import android.R.integer;
import android.app.Application;
import android.app.Service;
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
import extrace.service.BackGroundLocation;
import extrace.ui.domain.ExpressListFragement;
import extrace.ui.domain.ExpressListFragment;
import extrace.ui.main.MainFragment;
import extrace.ui.main.MypackageActivity;
import extrace.ui.main.MypackageInActivity;
import extrace.ui.main.MypackageInActivity.MypackageInFragment1;
import extrace.ui.main.MypackageInActivity.MypackageInFragment2;
import extrace.ui.main.R;

public class Courier implements User {
	UserInfo userinfo = null;
	final int time  = 30;
	private Intent intent;
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
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
        	switch(position){
        	case 0:
                return MainFragment.newInstance();
        	case 1:
                return ExpressListFragement.newInstance("ExDLV");	//派送快件
        	case 2:
                return ExpressListFragment.newInstance("ExRCV");	//揽收快件
        	}
        	return null;
        }

        @Override
        public int getCount() {
            // 总共4页.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return "功能选择";
                case 1:
                    return "派送快件";
                case 2:
                	return "揽收快件";
            }
            return null;
        }
    }
	@Override
	public String getDelivePackageID() {
		// TODO Auto-generated method stub
		return userinfo.getDelivePackageID();
	}

	@Override
	public String getReceivePackageID() {
		// TODO Auto-generated method stub
		return userinfo.getReceivePackageID();
	}

	@Override
	public String geTransPackageID() {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public View rootViewAdapter(View view) {
		((LinearLayout)view.findViewById(R.id.l3)).setVisibility(View.GONE);
		((TextView)view.findViewById(R.id.t3)).setVisibility(View.GONE);
		((LinearLayout)view.findViewById(R.id.l4)).setVisibility(View.GONE);
		((TextView)view.findViewById(R.id.t4)).setVisibility(View.GONE);
		((LinearLayout)view.findViewById(R.id.l6)).setVisibility(View.GONE);
		((TextView)view.findViewById(R.id.t6)).setVisibility(View.GONE);
		return view;
	}

	@Override
	public Menu MenuAdapAdapter(Menu menu) {
		return menu;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return userinfo.getName();
	}

	@Override
	public FragmentPagerAdapter packageAdapter(
			final FragmentPagerAdapter adapter, 
			FragmentManager fm) {
		// TODO Auto-generated method stub
		return new FragmentPagerAdapter(fm){
			@Override
			public Fragment getItem(int position) {
				switch(position){
				case 0:
					return adapter.getItem(0);
				case 1:
					return adapter.getItem(1);
				}
				return adapter.getItem(0);
			}

			@Override
			public int getCount() {
				return 2;
			}

			@Override
			public CharSequence getPageTitle(int position) {
				switch (position) {
				case 0:
					return adapter.getPageTitle(0);
				case 1:
					return adapter.getPageTitle(2);
				}
				return adapter.getPageTitle(0);
			}
		 
		};
	}

	@Override
	public int getScanTime() {
		// TODO Auto-generated method stub
		return time;
	}
	
	public Intent getIntent() {
		return intent;
	}

	public void setIntent(Intent intent) {
		this.intent = intent;
	}

	@Override
	public void startLocationService(Application context) {
		// TODO Auto-generated method stub
		Intent inte = new Intent("extrace.service.BackGroundLocation");
		inte.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		setIntent(inte);
		context.startService(getIntent());
	}
	@Override
	
	public void stopLocationService(Application context){
		context.stopService(getIntent());
	}


}
