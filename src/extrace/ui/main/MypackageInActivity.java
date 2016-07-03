package extrace.ui.main;

import java.util.Locale;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.util.Log;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import extrace.loader.TransPackageLoader;
import extrace.misc.model.TransPackage;
import extrace.misc.smodel.NamePair;
import extrace.net.IDataAdapter;


public class MypackageInActivity extends ActionBarActivity implements ActionBar.TabListener, IDataAdapter<TransPackage> {
	
	
	public static final int REQUEST_CAPTURE = 100; 
	public static final int REQUEST_RCV = 101; 
	public static final int REQUEST_SND = 102; 

	 
	 SectionsPagerAdapter mSectionsPagerAdapter;
	 ViewPager mViewPager;
	 private Intent mIntent;
	 boolean new_es = true;
	 private MypackageInFragment1 baseFragment; 
	 private MypackageInFragment2 externFragment; 
	 private String pkgId=null;
	 private static TransPackage mpackage;
	 private TransPackageLoader mLoader;
	 private String mpkgType;
	
	
	
	@Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_main);
	        final ActionBar actionBar = getSupportActionBar();
	        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
	        mSectionsPagerAdapter =  new SectionsPagerAdapter(getSupportFragmentManager());
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
				if(mIntent.getStringExtra("Action").equals("In")){
				    if (mIntent.hasExtra("mType")) {
						//2432424
				    	mpkgType=mIntent.getStringExtra("mType");
				    	RefreshList();
				    					
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
	 
	 private void RefreshList() {
		// TODO Auto-generated method stub
		//String pkgId = null;
			switch(mpkgType){
			case "ExDLV":
				pkgId = ((ExTraceApplication)this.getApplication()).getLoginUser().getDelivePackageID();
				Log.v("shihu2", pkgId);
				break;
			case "ExRCV":
				pkgId = ((ExTraceApplication)this.getApplication()).getLoginUser().getReceivePackageID();
				break;
			case "ExTAN":
				pkgId = ((ExTraceApplication)this.getApplication()).getLoginUser().geTransPackageID();
				break;
			}			
	    	//mpackage=(TransPackage) mIntent.getSerializableExtra(pkgId);
	    	mLoader = new TransPackageLoader(this, this);
	    	Log.v("shihu",pkgId);
			mLoader.Load(pkgId);
	}

	public class SectionsPagerAdapter extends FragmentPagerAdapter {

			public SectionsPagerAdapter(FragmentManager fm) {
				super(fm);
			}

			@Override
			public Fragment getItem(int position) {
				switch(position){
				case 0:
					baseFragment = MypackageInFragment1.newInstance();
					return baseFragment;
				case 1:
					externFragment = MypackageInFragment2.newInstance();
					return externFragment;
				}
				return MypackageInFragment1.newInstance();
			}

			@Override
			public int getCount() {
				return 2;
			}

			@Override
			public CharSequence getPageTitle(int position) {
				Locale l = Locale.getDefault();
				switch (position) {
				case 0:
					return getString(R.string.title_ex_edit1).toUpperCase(l);
				case 1:
					return getString(R.string.title_ex_edit2).toUpperCase(l);
				}
				return null;
			}
		}
	 
	 
	 @Override
		public TransPackage getData() {
			// TODO Auto-generated method stub
			return mpackage;
		}

		@Override
		public void setData(TransPackage data) {
			// TODO Auto-generated method stub
			mpackage=data;
		}

		@Override
		public void notifyDataSetChanged() {
			// TODO Auto-generated method stub
			if(baseFragment != null){
				baseFragment.RefreshIn(mpackage);			
				TransPackageLoader lod = new TransPackageLoader(baseFragment.new NamePairAdapter() ,
						this);
				NamePair a = new NamePair();	
				a.setA(mpackage.getSourceNode());
				a.setB(mpackage.getTargetNode());
//				a.setA("1001");
//				a.setB("1000");
				//lod.getTransNamePair(a);
				
			}
			//MenuDisplay(mpackage.getStatus());
			
		}
	 
	 
	
	 
	 
	 public static class MypackageInFragment1 extends Fragment {
			
		    private TextView mPKGView;
			private TextView mUserNameView;
			private TextView mSendNameView;
			private TextView mRecxeiveNameView;

		    class NamePairAdapter implements IDataAdapter<NamePair>{
				NamePair namepair;
				@Override
				public NamePair getData() {
					// TODO Auto-generated method stub
					return namepair;
				}

				@Override
				public void setData(NamePair data) {
					// TODO Auto-generated method stub
					this.namepair = data;
				}

				@Override
				public void notifyDataSetChanged() {
					// TODO Auto-generated method stub
					mSendNameView.setText(namepair.getA());
					mRecxeiveNameView.setText(namepair.getB());
					
				}
				
			}
				
			public static MypackageInFragment1 newInstance() {
				MypackageInFragment1 fragment = new MypackageInFragment1();
				return fragment;
			}

			public MypackageInFragment1() {
			}

			@Override
			public View onCreateView(LayoutInflater inflater, ViewGroup container,
					Bundle savedInstanceState) {
				View rootView = inflater.inflate(R.layout.mypackage_in,	container, false);
				mPKGView = (TextView) rootView.findViewById(R.id.mypackageName);
				mUserNameView = (TextView) rootView.findViewById(R.id.userName);
				mSendNameView = (TextView) rootView.findViewById(R.id.sendName);
				mRecxeiveNameView = (TextView) rootView.findViewById(R.id.receiveName);
				
				return rootView;
			}
			void RefreshIn(TransPackage tp){
				
				mPKGView.setText(tp.getID());				
				mUserNameView.setText(((ExTraceApplication)this.getActivity().getApplication()).getLoginUser().getName());
			
			}
		
			
		}

		public static class MypackageInFragment2 extends Fragment {

			/**
			 * Returns a new instance of this fragment for the given section number.
			 */
			public static MypackageInFragment2 newInstance() {
				MypackageInFragment2 fragment = new MypackageInFragment2();
				return fragment;
			}

			public MypackageInFragment2() {
			}

			@Override
			public View onCreateView(LayoutInflater inflater, ViewGroup container,
					Bundle savedInstanceState) {
				View rootView = inflater.inflate(R.layout.fragment_express_edit2,
						container, false);
                return rootView;
			}
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
