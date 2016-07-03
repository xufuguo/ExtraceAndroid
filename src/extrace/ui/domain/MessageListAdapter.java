package extrace.ui.domain;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import extrace.misc.model.Message;
import extrace.net.IDataAdapter;
import extrace.ui.main.R;

public class MessageListAdapter  extends ArrayAdapter<Message> implements IDataAdapter<List<Message>>{


	private List<Message> itemList;
	private Context context;
	
	
	public MessageListAdapter(List<Message> itemList, int resource, Context ctx) {
		super(ctx, resource, itemList);
		this.itemList = itemList;
		this.context = ctx;
	}
	
	public int getCount() {
		if (itemList != null)
			return itemList.size();
		return 0;
	}

	public Message getItem(int position) {
		if (itemList != null)
			return itemList.get(position);
		return null;
	}

	/*public void setItem(Message ci, int position) {
		if (itemList != null)
			itemList.set(position,ci);
	}*/

	public long getItemId(int position) {
		if (itemList != null)
			return itemList.get(position).hashCode();
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		View v = convertView;
		if (v == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(R.layout.message_content, null);
		}
		Log.v("positon:", position + "    ");
		Message c = itemList.get(position);
		TextView sender = (TextView) v.findViewById(R.id.senderName);
		sender.setText(c.getSenderName());
		TextView tel = (TextView) v.findViewById(R.id.tel);
		tel.setText(c.getTel());
		TextView time = (TextView) v.findViewById(R.id.time);
		time.setText(c.getTime().toLocaleString());
		TextView loc = (TextView) v.findViewById(R.id.isrecv);
		loc.setText(c.isIsrecv()? "已被揽收" : "快去揽收(>﹏<)");
	/*	sender.setTag(position);
		expId.setTag(position);
		tel.setTag(position);
		loc.setTag(position);*/
		return v;		
	}

	@Override
	public List<Message> getData() {
		return itemList;
	}

	@Override
	public void setData(List<Message> data) {
		this.itemList = data;
	}	

}
