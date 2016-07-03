package extrace.ui.domain;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import extrace.loader.ExpressListLoader;
import extrace.misc.model.ExpressSheet;
import extrace.ui.domain.ExpressListFragment.OnFragmentInteractionListener;
import extrace.ui.main.ExTraceApplication;

/**
 * A fragment representing a list of Items.
 * <p />
 * <p />
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class ExpressListFragment2 extends ListFragment {

	private static final String ARG_EX_TYPE = "ExType";
	
	// TODO: Rename and change types of parameters
	private String mExType;

	private ExpressListAdapter mAdapter;
	private ExpressListLoader mLoader;

	Intent mIntent;  
	
	private OnFragmentInteractionListener mListener;

	// TODO: Rename and change types of parameters
	public static Fragment newInstance(String ex_type) {
		
		ExpressListFragment2 fragment = new ExpressListFragment2();
		Bundle args = new Bundle();
		args.putString(ARG_EX_TYPE, ex_type);	//���췽���������,ʹ��Bundle����Ϊ����������
		fragment.setArguments(args);
		return fragment;
	}

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public ExpressListFragment2() {
	}

	@Override 
	public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getArguments() != null) {	//��һ�ֶ�����������ķ�ʽ
			mExType = getArguments().getString(ARG_EX_TYPE);
		}
        // Give some text to display if there is no data.  In a real
        // application this would come from a resource.
        setEmptyText("����б��յ�!");
        
        mAdapter = new ExpressListAdapter(new ArrayList<ExpressSheet>(), 
        		this.getActivity(), mExType);
        setListAdapter(mAdapter);

        getListView().setChoiceMode(AbsListView.CHOICE_MODE_SINGLE); 
        registerForContextMenu(getListView());
        
        RefreshList();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener =  (OnFragmentInteractionListener)activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnFragmentInteractionListener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		if (null != mListener) {
			// Notify the active callbacks interface (the activity, if the
			// fragment is attached to one) that an item has been selected.
			mListener.onFragmentInteraction(mAdapter.getItem(position).getID());
		}
		EditExpress(mAdapter.getItem(position));
	}

	/**
	 * This interface must be implemented by activities that contain this
	 * fragment to allow an interaction in this fragment to be communicated to
	 * the activity and potentially other fragments contained in that activity.
	 * <p>
	 * See the Android Training lesson <a href=
	 * "http://developer.android.com/training/basics/fragments/communicating.html"
	 * >Communicating with Other Fragments</a> for more information.
	 */
	
	
	private void RefreshList()
	{
		String pkgId = null;
		switch(mExType){
		case "ExDLV":
			pkgId = ((ExTraceApplication)this.getActivity().getApplication()).getLoginUser().getDelivePackageID();
			break;
		case "ExRCV":
			pkgId = ((ExTraceApplication)this.getActivity().getApplication()).getLoginUser().getReceivePackageID();
			break;
		case "ExTAN":
			pkgId = ((ExTraceApplication)this.getActivity().getApplication()).getLoginUser().geTransPackageID();
			break;
		}
		mLoader = new ExpressListLoader(mAdapter, this.getActivity());
		mLoader.LoadExpressListInPackage2(pkgId);
		//mLoader.LoadExpressList();
	}

	void EditExpress(ExpressSheet es)
    {
		Intent intent = new Intent();
		intent.putExtra("Action","Edit");
		intent.putExtra("ExpressSheet",es);
		intent.setClass(this.getActivity(), ExpressEditActivity.class);
		startActivityForResult(intent, 0);  	
    }

}