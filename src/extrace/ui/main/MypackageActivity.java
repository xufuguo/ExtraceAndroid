package extrace.ui.main;

import java.util.Locale;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import extrace.ui.domain.ExpressListFragment.OnFragmentInteractionListener;

public class MypackageActivity  extends ActionBarActivity implements ActionBar.TabListener,OnFragmentInteractionListener{
	
	FragmentPagerAdapter mSectionsPagerAdapter;
	 
	 ViewPager mViewPager;
	 
	 @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_main);

	        final ActionBar actionBar = getSupportActionBar();
	        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

	        mSectionsPagerAdapter = ((ExTraceApplication)getApplication()).getLoginUser()
	        		.packageAdapter(new SectionsPagerAdapter(getSupportFragmentManager())
	        				,getSupportFragmentManager());
	        mViewPager = (ViewPager) findViewById(R.id.pager);
	        mViewPager.setAdapter(mSectionsPagerAdapter);

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

	  
	
	    /**
	     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	     * one of the sections/tabs/pages.
	     */
	    public class SectionsPagerAdapter extends FragmentPagerAdapter {

	        public SectionsPagerAdapter(FragmentManager fm) {
	            super(fm);
	        }

	        @Override
	        public Fragment getItem(int position) {
	        	switch(position){
	        	case 0:
	                return MyPackageFragment.newInstance("ExRCV"); //揽收
	        	case 1:
	                return MyPackageFragment.newInstance("ExDLV");	//派送	
	        	case 2:	
	                return MyPackageFragment.newInstance("ExTAN");	//转运
	        	}
	        	return null;
	        }

	        @Override
	        public int getCount() {
	            // ×Ü¹²3Ò³.
	            return 3;
	        }

	        @Override
	        public CharSequence getPageTitle(int position) {
	            Locale l = Locale.getDefault();
	            switch (position) {
	                case 0:
	                    return getString(R.string.title_receive_mypackage).toUpperCase(l);
	                case 1:
	                    return getString(R.string.title_dispatch_mypackage).toUpperCase(l);
	                case 2:
	                    return getString(R.string.title_delivery_mypackage).toUpperCase(l);
	                
	            }
	            return null;
	        }
	    }

		@Override
		public void onFragmentInteraction(String id) {
			// TODO Auto-generated method stub
			
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


}
