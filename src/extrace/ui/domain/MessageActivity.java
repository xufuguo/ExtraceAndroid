package extrace.ui.domain;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;
import extrace.auto.Interface.User;
import extrace.loader.MessageListLoader;
import extrace.misc.model.Message;
import extrace.net.HttpUtil;
import extrace.net.IDataAdapter;
import extrace.service.MessageService;
import extrace.ui.main.ExTraceApplication;
import extrace.ui.main.R;

public class MessageActivity extends ActionBarActivity implements IDataAdapter<List<Message>>{
	ListView items;
	ActionBar actionBar;
	String TAG = "MessageActivity";
	List<Message> msgs = new ArrayList<Message>();
	Intent intent = null;
	String url;
	Message m = null;
	MessageListLoader mLoader = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stubs
		super.onCreate(savedInstanceState);
		actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.my_message_lit);
		mLoader = new MessageListLoader(this, this);
		url =((ExTraceApplication)this.getApplication()).getMiscServiceUrl();
		User user = ((ExTraceApplication)getApplication()).getLoginUser();
		mLoader.loadMessage(user.getUserinfo().getUID());
		//if is Customer
		//mLoader.loadMessageForCustomer(tel);
		//Log.v("LoginInUserId:", "userid="+user.getUserinfo().getUID());
		
		//ibLog.e("MessageLoad:", msgs.toString());
		items = (ListView) findViewById(R.id.my_message);
		
		items.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
				// TODO 
				m = msgs.get(position);
				Log.v("print message", m.toString());
				new AlertDialog.Builder(MessageActivity.this)
				.setTitle("请选择")  
				.setIcon(android.R.drawable.ic_dialog_email)             
				.setSingleChoiceItems(new String[] {"揽收该快件","已揽收"}, 0,   
				  new DialogInterface.OnClickListener() {  
				     public void onClick(DialogInterface dialog, int which) { 
				    	 switch(which){
				    	 case 0:
				    		 Log.v(TAG, "启动揽收activity");
				    		 intent = new Intent(MessageActivity.this, ExpressEditActivity.class);
				    		 intent.putExtra("Action", "New");
				    		 startActivityForResult(intent, 1);
				    		 break;
				    	 case 1:
				    		 mLoader = new MessageListLoader(MessageActivity.this, MessageActivity.this);
				    		 mLoader.isRecv(m.getSN());
				    		 Toast.makeText(MessageActivity.this, "已揽收该快件", Toast.LENGTH_SHORT).show();
				    		 msgs.remove(m);
				    		 refrash();
				    		 break;
				    	 }
				    	 //Toast.makeText(MessageActivity.this, "第"+which+"个", Toast.LENGTH_SHORT).show();
				        dialog.dismiss();  
				     }  
				  }  
				)  
				.setNegativeButton("取消", null)  
				.show(); 
			}
		});
		//启动服务
		//startService(new Intent(this, MessageService.class));
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		this.finish();	
		return true;
	}
	@Override
	public List<Message> getData() {
		// TODO Auto-generated method stub
		return msgs;
	}
	@Override
	public void setData(List<Message> data) {
		// TODO Auto-generated method stub
		if(data.size() == 0)
			return;
		msgs = data;
	}
	@Override
	public void notifyDataSetChanged() {
		// TODO Auto-generated method stub
		if(msgs != null){
			refrash();
		}
	}
	public void refrash(){
		items.setAdapter(new MessageListAdapter(msgs, R.layout.my_message_lit , this));
	}
	
	

	

}
