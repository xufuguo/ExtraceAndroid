package extrace.ui.domain;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import extrace.loader.TransPackageLoader;
import extrace.misc.model.TransPackage;
import extrace.net.IDataAdapter;
import extrace.ui.main.R;
import zxing.util.CaptureActivity;

public class PackageEditActivity extends ActionBarActivity implements IDataAdapter<TransPackage> {

	public static final int REQUEST_CAPTURE = 100;
	public static final int REQUEST_OPE = 101;
	public static final int REQUEST_CLO = 102; // 拆包

	TransPackage mPackage =null;

	ImageView iv;
	Button button;
	Intent mintent;
	TransPackageLoader mLoader;
	SharedPreferences mPreferences;
	android.app.ActionBar bar;
	private boolean openOrClose = true;// 拆包还是打包

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_transpackage);

		bar = getActionBar(); // 获取ActionBar的对象，从这个方法也可知action bar是activity的一个属性
		bar.setDisplayHomeAsUpEnabled(true); // 显示返回的箭头，并可通过onOptionsItemSelected()进行监听
		button = (Button) findViewById(R.id.bt_commit);
		iv = (ImageView) findViewById(R.id.action_pk_capture_icon);
		iv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				StartCapture();
			}
		});
		mintent = getIntent();
		if (mintent.hasExtra("Action")) {
			if (mintent.getStringExtra("Action").equals("Open")) {
				// 拆包
				openOrClose = false;
				this.setTitle("包裹拆包");
				StartCapture();
			} else if (mintent.getStringExtra("Action").equals("Close")) {
				// 打包
				button.setVisibility(8);// button不可见
				openOrClose = true;
				this.setTitle("包裹打包");
				StartCapture();
			}
		} else {
			// intent中不含有action时
			this.setResult(RESULT_CANCELED, mintent);
			this.finish();
		}
	}

	@Override
	public TransPackage getData() {
		return mPackage;
	}

	@Override
	public void setData(TransPackage data) {
		mPackage = data;
	}

	@Override
	public void notifyDataSetChanged() {
		if (mPackage != null) {
			RefreshUI(mPackage);
		}
	}

	private void RefreshUI(TransPackage mk) {
		TextView pkid = (TextView) findViewById(R.id.packageid);
		pkid.setText(mk.getID());
		TextView pkSource = (TextView) findViewById(R.id.pk_source_node_name);
		pkSource.setText(mk.getSourceNode());
		TextView pkTarget = (TextView) findViewById(R.id.pk_target_node_name);
		pkTarget.setText(mk.getTargetNode());
		TextView pkClose = (TextView) findViewById(R.id.pk_close_time);
		if (mk.getCreateTime() != null) {
			pkClose.setText(DateFormat.format("yyyy-MM-dd hh:mm:ss", mk.getCreateTime()));
		} else {
			pkClose.setText(null);
		}
		TextView pkStatus = (TextView) findViewById(R.id.pk_status);
		switch (mk.getStatus()) {
		case 0:
			pkStatus.setText("包裹新建中...");
			mPreferences = getSharedPreferences("es_data", Context.MODE_PRIVATE);
			mPreferences.edit().clear().commit();//清除sharedPerences
			break;
		case 1:
			pkStatus.setText("包裹打包中...");
			break;
		case 2:
			pkStatus.setText("包裹转运中...");
			button.setBackground(getResources().getDrawable(R.drawable.round_corner));
			button.setEnabled(true);
			break;
		case 3:
			pkStatus.setText("包裹分拣中...");  
			break;
		case 4:
			pkStatus.setText("包裹揽收货篮中...");  
			button.setEnabled(true);
			break;
		case 5:
			pkStatus.setText("包裹派送货篮中...");
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) {
		case RESULT_OK:// RESULT_OK=-1
			switch (requestCode) {
			case REQUEST_CAPTURE:
				if (data.hasExtra("BarCode")) {
					String id = data.getStringExtra("BarCode");
					try {
						mLoader = new TransPackageLoader(this, this);
						mLoader.Load(id);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					Toast.makeText(PackageEditActivity.this, "扫描失败,请重新扫描", Toast.LENGTH_SHORT).show();

					finish();
				}
				break;
			case REQUEST_OPE:
				mPackage.setStatus(1);
				RefreshUI(mPackage);
			default:
				break;
			}
			break;
		default:
			break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (openOrClose) {
			getMenuInflater().inflate(R.menu.package_edit, menu);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) {
		case R.id.action_ok:
			if (mPackage != null && mPackage.getStatus() == 1) {
				mPackage.setStatus(2);// 设置包裹的状态为转运中

				mPreferences = getSharedPreferences("es_data", Context.MODE_PRIVATE);
				int list_size = mPreferences.getInt("list_size", 0);
				List<String> eslist=new ArrayList<String>();
				for (int i = 0; i < list_size; i++) {
					String itemBeanString = mPreferences.getString("" + i, "");
					String[] items = itemBeanString.split("\\*");
					eslist.add(items[0]);
					//moveToPk(items[0], mPackage.getID());
				}
				moveToPk(eslist, mPackage.getID());
				mPreferences.edit().clear().commit();// 清空SharedPerences
				finish();
				// 保存包裹？
				// save();
			} else {
				Toast.makeText(PackageEditActivity.this, "请添加快件!", Toast.LENGTH_SHORT).show();
			}
			return true;
		case R.id.action_refresh:
			if (mPackage != null) {
				RefreshUI(mPackage);
			}
			return true;
		case R.id.action_add:
			// 添加快件
			if (mPackage != null) {
				Intent intent = new Intent();
				intent.putExtra("packageId", mPackage.getID());
				intent.setClass(this, PackageAddActivity.class);
				startActivityForResult(intent, REQUEST_OPE);
			} else {
				Toast.makeText(PackageEditActivity.this, "无效的包裹!", Toast.LENGTH_SHORT).show();
			}
			return true;
		case (android.R.id.home):
			mintent.putExtra("TransPackage", mPackage);
			this.setResult(RESULT_OK, mintent);
			this.finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void moveToPk(List eslist, String pkId) {
		try {
			mLoader = new TransPackageLoader(this, this);
			mLoader.moveEsToPk(eslist, pkId);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void commitOpenPkBt(View view) {
		switch (view.getId()) {
		// 确认拆包
		case R.id.bt_commit:
			mPackage.setStatus(3);// 设置包裹状态为分拣中(拆包)
			mLoader = new TransPackageLoader(this, this);
			mLoader.Open(mPackage.getID());
			button.setEnabled(false);
			break;
		default:
			break;
		}
	}

	private void StartCapture() {
		Intent intent = new Intent();
		intent.putExtra("Action", "Captrue");
		intent.setClass(this, CaptureActivity.class);
		startActivityForResult(intent, REQUEST_CAPTURE);
	}
}
