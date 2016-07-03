package extrace.ui.main;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Intent;

public class MyPackageFragment extends Fragment{

	private static final String ARG_PKG_TYPE = "PkgType";
	private String mExType;
	String pkgId = null;
	

	public static MyPackageFragment newInstance(String pkg_type) {
		MyPackageFragment fragment = new MyPackageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PKG_TYPE, pkg_type);	//构造方法传入参数,使用Bundle来作为参数的容器
        fragment.setArguments(args);
        return fragment;
    }


	public MyPackageFragment() {
    }
	
	 @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState) {
	        View rootView = inflater.inflate(R.layout.fragment_mypackage, container, false);
	        if (getArguments() != null) {	//另一种读出传入参数的方式
				mExType = getArguments().getString(ARG_PKG_TYPE);
			}
	        //fragment_mypackage中的操作激发
	        rootView.findViewById(R.id.action_mypackage_history_icon).setOnClickListener(
					new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							StartMypackageHistory();
						}
					});
	        rootView.findViewById(R.id.action_mypackage_history).setOnClickListener(
					new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							StartMypackageHistory();
						}
					});
	        rootView.findViewById(R.id.action_mypackage_content_icon).setOnClickListener(
					new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							StartMypackageContent();
						}
					});
	        rootView.findViewById(R.id.action_mypackage_content).setOnClickListener(
					new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							StartMypackageContent();
						}
					});
	        rootView.findViewById(R.id.action_mypackage_in_icon).setOnClickListener(
					new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							StartMypackageIn();
						}
					});
	        rootView.findViewById(R.id.action_mypackage_in).setOnClickListener(
					new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							StartMypackageIn();
						}
					});


	        return rootView;
	    }
	 
	 


     void StartMypackageIn() 
     {
		// TODO Auto-generated method stub
    	 Intent intent = new Intent();
		 intent.putExtra("Action","In");
    	 intent.putExtra("mType",mExType);
		 intent.setClass(this.getActivity(), MypackageInActivity.class);
		 startActivityForResult(intent, 0); 
	}

     void StartMypackageContent() 
	 {
    	// TODO Auto-generated method stub
    	Intent intent = new Intent();
    	intent.putExtra("Action","Content");
    	intent.putExtra("mType",mExType);
    	intent.setClass(this.getActivity(), MypackageContentActivity.class);
    	startActivityForResult(intent, 0); 
	 }

	 void StartMypackageHistory() {
		// TODO Auto-generated method stub
		 Intent intent = new Intent();
		 intent.putExtra("Action","History");
		 intent.putExtra("mType",mExType);
		 intent.setClass(this.getActivity(), MypackageHistoryActivity.class);
		 startActivityForResult(intent, 0); 
	}
	    
}
