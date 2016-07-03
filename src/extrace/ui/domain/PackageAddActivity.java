package extrace.ui.domain;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import extrace.loader.ExpressLoader;
import extrace.misc.model.ExpressSheet;
import extrace.net.IDataAdapter;
import extrace.ui.main.R;
import zxing.util.CaptureActivity;

public class PackageAddActivity extends ActionBarActivity implements IDataAdapter<ExpressSheet> {
	public static final int REQUEST_CAPTURE = 100;

	ListView listView;
	List<ItemBean> list = new ArrayList<ItemBean>();
	ExpressSheet es;
	String packageId;
	ExpressLoader mLoader;
	Intent mIntent;
	int mposition;// listview的position
	ActionBar bar;
	SharedPreferences mPreferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.packageadd_edit);

		this.setTitle("快件添加中");
	
		bar = getActionBar(); // 获取ActionBar的对象，从这个方法也可知action bar是activity的一个属性
		bar.setDisplayHomeAsUpEnabled(true); // 显示返回的箭头，并可通过onOptionsItemSelected()进行监听

		listView = (ListView) findViewById(R.id.lv);
		// 添加长按事件
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				view.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {

					public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
						menu.add(0, 0, 0, "移除该快件");
					}
				});
				mposition = position;
				return false;
			}

		});
		mIntent = getIntent();
		if (mIntent.hasExtra("packageId")) {
			packageId = mIntent.getStringExtra("packageId");
		}
		mPreferences = getSharedPreferences("es_data", Context.MODE_PRIVATE);
		int list_size = mPreferences.getInt("list_size", 0);
		for (int i = 0; i < list_size; i++) {
			String itemBeanString = mPreferences.getString("" + i, "");
			String[] items = itemBeanString.split("\\*");
			list.add(new ItemBean(items[0], items[1], items[2]));
		}

		StartCapture();// 首次进入时
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 0:
			list.remove(mposition);
			refreshUI();
			break;
		default:
			break;
		}
		return super.onContextItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.packageaddactivity, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_add:
			// 继续添加快件
			StartCapture();
			return true;
		case R.id.action_ok:
		case (android.R.id.home):
			mPreferences = getSharedPreferences("es_data", Context.MODE_PRIVATE);
			Editor editor = mPreferences.edit();
			editor.putInt("list_size", list.size());// 保存list的大小
			int i = 0;
			for (ItemBean itemBean : list) {
				editor.putString("" + i++, itemBean.toString());
			}
			editor.commit();
			Intent intent=new Intent();
			setResult(RESULT_OK,intent);
			this.finish();
			return true;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		mPreferences = getSharedPreferences("es_data", Context.MODE_PRIVATE);
		Editor editor = mPreferences.edit();
		editor.putInt("list_size", list.size());// 保存list的大小
		int i = 0;
		for (ItemBean itemBean : list) {
			editor.putString("" + i++, itemBean.toString());
		}
		editor.commit();
		Intent intent=new Intent();
		setResult(RESULT_OK,intent);
		this.finish();
		super.onBackPressed();
	}

	private class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder;
			if (convertView == null) {
				viewHolder = new ViewHolder();
				// 加载布局文件
				convertView = LayoutInflater.from(PackageAddActivity.this).inflate(R.layout.express_list, null);
				viewHolder.esId = (TextView) convertView.findViewById(R.id.tv);
				viewHolder.senderAdd = (TextView) convertView.findViewById(R.id.tv_acc1);
				viewHolder.receiveAdd = (TextView) convertView.findViewById(R.id.tv_acc2);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			ItemBean itemBean = list.get(position);
			viewHolder.esId.setText(itemBean.getesId());
			viewHolder.senderAdd.setText(itemBean.getSenderAdd());
			viewHolder.receiveAdd.setText(itemBean.getReceiveAdd());
			return convertView;
		}

		class ViewHolder {
			TextView esId;
			TextView senderAdd;
			TextView receiveAdd;
		}
	}

	private void StartCapture() {
		Intent intent = new Intent();
		intent.putExtra("Action", "Captrue");
		intent.setClass(this, CaptureActivity.class);
		startActivityForResult(intent, REQUEST_CAPTURE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (resultCode) {
		case RESULT_OK:
			switch (requestCode) {
			case REQUEST_CAPTURE:
				if (data.hasExtra("BarCode")) {
					String id = data.getStringExtra("BarCode");
					try {
						mLoader = new ExpressLoader(this, this);
						mLoader.Load(id);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				break;

			default:
				break;
			}
		case RESULT_CANCELED:
			// 测试刷新UI
			//refreshUI();
			break;

		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public ExpressSheet getData() {
		return es;
	}

	@Override
	public void setData(ExpressSheet data) {
		es = data;
	}

	@Override
	public void notifyDataSetChanged() {
		if (es != null) {
			String id = es.getID();
			boolean flag = true;
			for (ItemBean itemBean : list) {
				if (itemBean.getesId().equals(id)) {
					flag = false;
					break;
				}
			}
			if (flag) {
				refreshUI(es);
			} else {
				Toast.makeText(PackageAddActivity.this, "此快件已添加", Toast.LENGTH_SHORT).show();
			}

		} else {
			Toast.makeText(PackageAddActivity.this, "此快件状态错误", Toast.LENGTH_SHORT).show();
		}
	}

	private void refreshUI(ExpressSheet es2) {
		ItemBean ib = new ItemBean();
		ib.esId = es2.getID();
		ib.senderAdd = es2.getSender().getAddress();
		ib.receiveAdd = es2.getRecever().getAddress();
		list.add(ib);
		listView.setAdapter(new MyAdapter());
	}

	private void refreshUI() {
		listView.setAdapter(new MyAdapter());
	}
}

class ItemBean {
	String esId;
	String senderAdd;
	String receiveAdd;

	public ItemBean(String esId, String senderAdd, String receiveAdd) {
		this.esId = esId;
		this.senderAdd = senderAdd;
		this.receiveAdd = receiveAdd;
	}

	public ItemBean() {

	}

	public String getesId() {
		return esId;
	}

	public void setesId(String esId) {
		this.esId = esId;
	}

	public String getSenderAdd() {
		return senderAdd;
	}

	public void setSenderAdd(String senderAdd) {
		this.senderAdd = senderAdd;
	}

	public String getReceiveAdd() {
		return receiveAdd;
	}

	public void setReceiveAdd(String receiveAdd) {
		this.receiveAdd = receiveAdd;
	}

	public String toString() {
		return getesId() + "*" + getSenderAdd() + "*" + getReceiveAdd();
	}
}
