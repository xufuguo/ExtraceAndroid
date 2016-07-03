package extrace.ui.main;

import java.util.Locale;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import extrace.auto.Imp.Customer;
import extrace.auto.Interface.User;
import extrace.ui.domain.ExpressListFragement;
import extrace.ui.domain.ExpressListFragment;
import extrace.ui.domain.ExpressListFragment.OnFragmentInteractionListener;
import extrace.ui.domain.MessageActivity;
import extrace.ui.domain.MyLocationActivity;
import extrace.ui.domain.SendMessageActivity;

public class MainActivity extends ActionBarActivity implements ActionBar.TabListener,OnFragmentInteractionListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
	private static final String PREFS_NAME = "ExTrace.cfg";
    FragmentPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;
    long backtime = 0;
    
    @Override
	public void onBackPressed(){
    	if(mViewPager != null ){
    		if((System.currentTimeMillis()-backtime) > 2000){
	    		Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
	    		backtime = System.currentTimeMillis();
    		}
    		else{
    			this.finish();
    		}
    		
    	}
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = ((ExTraceApplication)getApplication()).getLoginUser()
        		.getFragmentPagerAdapter(getSupportFragmentManager());
        
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
   }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        User user = ((ExTraceApplication)getApplication()).getLoginUser();
        user.MenuAdapAdapter(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch(id){
        case R.id.action_login:
        	Intent intent1 = new Intent(this, LoginActivity.class);
        	startActivity(intent1);
            return true;
        case R.id.action_logout:
        	logOut();
        	return true;
        case R.id.action_settings:
    		Intent intent = new Intent();
    		intent.setClass(this, SettingsActivity.class);
    		startActivityForResult(intent, 0);
            return true;
        case R.id.action_my_location:
        	Intent intent2 = new Intent();
        	intent2.setClass(this, MyLocationActivity.class);
        	startActivityForResult(intent2,1);
        	return true;
        case android.R.id.home:
        	this.finish();
        	return true;
        case R.id.action_my_message:
        	Intent intent3 = new Intent();
        	intent3.setClass(this, MessageActivity.class);
        	startActivityForResult(intent3,2);
        	return true;
        case R.id.action_my_package:
        	Intent intent4 = new Intent();
        	intent4.setClass(this, MypackageActivity.class);
        	startActivityForResult(intent4, 3);
        	return true;
        case R.id.action_send_message:
        	Intent intent6 = new Intent();
        	intent6.setClass(this, SendMessageActivity.class);
        	startActivityForResult(intent6, 6);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }
    public void logOut(){
    	((ExTraceApplication)getApplication()).setLoginUser(new Customer());
    	Intent intent5 = new Intent(this, MainActivity.class);
    	intent5.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    	startActivity(intent5);
    	((ExTraceApplication)getApplication()).getLoginUser()
    		.startLocationService(getApplication());
    	SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
		editor.putString("NICE", "OK");
		editor.putString("account", "");
		editor.putString("password", "");
		editor.commit();
    	finish();
    }
    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
//    public class SectionsPagerAdapter extends FragmentPagerAdapter {
//
//        public SectionsPagerAdapter(FragmentManager fm) {
//            super(fm);
//        }
//
//        @Override
//        public Fragment getItem(int position) {
//        	switch(position){
//        	case 0:
//                return MainFragment.newInstance();
//        	case 1:
//                return ExpressListFragment.newInstance("ExDLV");	//派送快件
//        	case 2:
//                return ExpressListFragment.newInstance("ExRCV");	//揽收快件
//        	case 3:
//                return ExpressListFragment.newInstance("ExTAN");	//转运快件
//        	}
//        	return null;
//        }
//
//        @Override
//        public int getCount() {
//            // 总共4页.
//            return 4;
//        }
//
//        @Override
//        public CharSequence getPageTitle(int position) {
//            Locale l = Locale.getDefault();
//            switch (position) {
//                case 0:
//                    return getString(R.string.title_section1).toUpperCase(l);
//                case 1:
//                    return getString(R.string.title_section2).toUpperCase(l);
//                case 2:
//                    return getString(R.string.title_section3).toUpperCase(l);
//                case 3:
//                    return getString(R.string.title_section4).toUpperCase(l);
//            }
//            return null;
//        }
//    }
   
	@Override
	public void onFragmentInteraction(String id) {
		// TODO Auto-generated method stub
		
	}

}
