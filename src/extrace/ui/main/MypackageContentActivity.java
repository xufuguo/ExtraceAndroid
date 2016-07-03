package extrace.ui.main;

import java.util.Locale;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBarActivity;
import extrace.ui.domain.ExpressListFragment;
import extrace.ui.domain.ExpressListFragment.OnFragmentInteractionListener;

public class MypackageContentActivity extends ActionBarActivity  implements ActionBar.TabListener,OnFragmentInteractionListener{
	
	private ViewPager mViewPager;
	private Intent mIntent;
	private PagerAdapter mSectionsPagerAdapter;
	private String mPkgType;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
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
        
        
        
        mIntent = getIntent();
		if (mIntent.hasExtra("Action")) {
			if(mIntent.getStringExtra("Action").equals("Content")){
			    if (mIntent.hasExtra("mType")) {
					//2432424
			    	mPkgType=mIntent.getStringExtra("mType");
				} else {
					this.setResult(RESULT_CANCELED, mIntent);
					this.finish();
				}
			}
			else{
				this.setResult(RESULT_CANCELED, mIntent);
				this.finish();
			}
		}
		else{
			this.setResult(RESULT_CANCELED, mIntent);
			this.finish();
		}
   }
	
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
        	switch(position){
        	case 0:
               return ExpressListFragment.newInstance(mPkgType);	
        	}
        	return null;
        }

        @Override
        public int getCount() {
            // ×Ü¹²1Ò³.
            return 1;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_mypackage_now).toUpperCase(l);
            }
            return null;
        }
    }

	@Override
	public void onFragmentInteraction(String id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTabReselected(Tab arg0, FragmentTransaction arg1) {
		// TODO Auto-generated method stub
		
	}

	 @Override
	    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
	        // When the given tab is selected, switch to the corresponding page in
	        // the ViewPager.
	        mViewPager.setCurrentItem(tab.getPosition());
	    }


	@Override
	public void onTabUnselected(Tab arg0, FragmentTransaction arg1) {
		// TODO Auto-generated method stub
		
	}

	
 

}
