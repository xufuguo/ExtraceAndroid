package extrace.ui.main;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import extrace.loader.UserLoader;
import extrace.misc.model.UserInfo;
import extrace.net.IDataAdapter;

public class RegisterActivity extends ActionBarActivity implements IDataAdapter<UserInfo> {
	ActionBar actionbar;
	EditText et_name;
	EditText et_phone;
	EditText et_password;
	EditText et_confirm_psw;
	UserLoader mLoader;
	UserInfo userInfo;
	//Boolean isRegister = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		actionbar = getActionBar();
		actionbar.setDisplayHomeAsUpEnabled(true);
		actionbar.setDisplayShowHomeEnabled(true);
		et_name = (EditText) findViewById(R.id.et_name);
		et_phone = (EditText) findViewById(R.id.et_phone);
		et_password = (EditText) findViewById(R.id.et_password);
		et_confirm_psw = (EditText) findViewById(R.id.et_confirmpsw);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			this.finish();

			return true;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	// ×¢²á°´Å¥´¥·¢
	public void register(View view) {
		switch (view.getId()) {
		case R.id.bt_register:
			String name = et_name.getText().toString().trim();
			String phone = et_phone.getText().toString().trim();
			String password = et_password.getText().toString().trim();
			String confirmpsw = et_confirm_psw.getText().toString().trim();
			if (TextUtils.isEmpty(name) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(password)
					|| TextUtils.isEmpty(confirmpsw)) {
				Toast.makeText(RegisterActivity.this, "请填写信息完整", Toast.LENGTH_SHORT).show();
				return;
			}
			if (!password.equals(confirmpsw)) {
				Toast.makeText(RegisterActivity.this, "两次密码输入不一致", Toast.LENGTH_SHORT).show();
				return;
			}
			registerUser(name, phone, password);
			break;

		default:
			break;
		}
	}

	void registerUser(String name, String phone, String password) {
		try {
			mLoader = new UserLoader(this, this);
			mLoader.registerUser(name, phone, password);
			;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public UserInfo getData() {
		return userInfo;
	}

	@Override
	public void setData(UserInfo data) {
		userInfo = data;
	}

	@Override
	public void notifyDataSetChanged() {
		if (userInfo!=null) {
			//注册失败
			Intent intent = new Intent(this, LoginActivity.class);
			startActivity(intent);
		} else {
			Toast.makeText(RegisterActivity.this, "注册失败", Toast.LENGTH_SHORT).show();
		}
	}
}
