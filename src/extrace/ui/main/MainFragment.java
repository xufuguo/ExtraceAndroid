package extrace.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import extrace.auto.Interface.User;
import extrace.ui.domain.ExpressEditActivity;
import extrace.ui.domain.HistoryAcitivity;
import extrace.ui.domain.MapActivity;
import extrace.ui.domain.PackageEditActivity;
import extrace.ui.misc.CustomerListActivity;

public class MainFragment extends Fragment {

	// 工厂模式，实例化类,并传值
	public static MainFragment newInstance() {
		MainFragment fragment = new MainFragment();
		Bundle args = new Bundle();
		// args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		fragment.setArguments(args);
		return fragment;
	}

	public MainFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_main, container, false);

		// 快件揽收
		rootView.findViewById(R.id.action_ex_receive_icon).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				StartReceiveExpress();
			}
		});
		rootView.findViewById(R.id.action_ex_receive).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				StartReceiveExpress();
			}
		});
		// 快件派送
		rootView.findViewById(R.id.action_ex_transfer_icon).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				StartSendExpress();
			}
		});
		rootView.findViewById(R.id.action_ex_transfer).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				StartSendExpress();
			}
		});

		/**
		 * 2016/2/20修改 加入打包 拆包监听器 
		 * 设置 拆包textview id=action_pk_open 设置打包 textview
		 * id=action_pk_close
		 */
		// 包裹拆包
		rootView.findViewById(R.id.action_pk_exp).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				startOpenPackage();
			}
		});
		rootView.findViewById(R.id.action_pk_open).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				startOpenPackage();
			}
		});
		// 包裹打包
		rootView.findViewById(R.id.action_pk_pkg).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				startClosePackage();
			}
		});
		rootView.findViewById(R.id.action_pk_close).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				startClosePackage();
			}
		});
		// 客户管理
		rootView.findViewById(R.id.action_cu_mng_icon).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				StartCustomerList();
			}
		});
		rootView.findViewById(R.id.action_cu_mng).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				StartCustomerList();
			}
		});
		// 快件查询
		rootView.findViewById(R.id.action_ex_qur_icon).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				StartQueryExpress();
			}
		});
		rootView.findViewById(R.id.action_ex_qur).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				StartQueryExpress();
			}
		});
		User user = ((ExTraceApplication)getActivity().getApplication())
				.getLoginUser();
		return user.rootViewAdapter(rootView);
	}
	
	//快件揽收
	void StartReceiveExpress() {
		Intent intent = new Intent();
		intent.putExtra("Action", "New");
		intent.setClass(this.getActivity(), ExpressEditActivity.class);
		startActivityForResult(intent, 0);
	}
	//快件派送
	void StartSendExpress(){
		Intent intent = new Intent();
		intent.putExtra("Action", "SEND");
		intent.setClass(this.getActivity(),ExpressEditActivity.class);
		startActivityForResult(intent, 1);
	}
	//快件派送和快件查询
	void StartQueryExpress() {
		Intent intent = new Intent();
		intent.putExtra("Action", "Query");
		intent.setClass(this.getActivity(), HistoryMainActivity.class);
		startActivityForResult(intent, 0);
	}
	//包裹拆包
	void startOpenPackage(){
		Intent intent=new Intent();
		intent.putExtra("Action", "Open");
		intent.setClass(this.getActivity(),PackageEditActivity.class);
		startActivityForResult(intent, 0);
	}
	//包裹打包
	void startClosePackage(){
		Intent intent=new Intent();
		intent.putExtra("Action", "Close");
		intent.setClass(this.getActivity(),PackageEditActivity.class);
		startActivityForResult(intent, 0);
	}
	//客户管理
	void StartCustomerList() {
		Intent intent = new Intent();
		intent.putExtra("Action", "None");
		intent.setClass(this.getActivity(), CustomerListActivity.class);
		startActivityForResult(intent, 0);
	}
}
