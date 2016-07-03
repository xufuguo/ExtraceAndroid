package extrace.ui.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import extrace.auto.Imp.Courier;
import extrace.auto.Imp.Customer;
import extrace.auto.Imp.Transporter;
import extrace.auto.Interface.User;
import extrace.loader.UserLoader;
import extrace.misc.model.UserInfo;
import extrace.net.IDataAdapter;

public class LoginActivity extends ActionBarActivity implements IDataAdapter<UserInfo> {

	private static final String PREFS_NAME = "ExTrace.cfg";
	EditText et_acc;
	EditText et_psw;
	String account;
	String password;
	UserLoader mLoader;
	UserInfo userinfo;
	// boolean isConfirm = false;// ÓÃ»§µÇÂ½ÑéÖ¤ÊÇ·ñ³É¹¦

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		ActionBar actionBar =getSupportActionBar();
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
		et_acc = (EditText) findViewById(R.id.et_account);
		et_psw = (EditText) findViewById(R.id.et_password);
		SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		String acc = preferences.getString("account", "");
		String psw = preferences.getString("password", "");
		et_acc.setText(acc);
		et_psw.setText(psw);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.login, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			this.finish();
			return true;
		case R.id.exit:
			// 退出
			finish();
			return true;
		case R.id.about:
			// 关于
			Toast.makeText(LoginActivity.this, "瓒瓒开发小组，跟我一起嗨！嗨！嗨！嗨！", Toast.LENGTH_LONG).show();
			return true;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	// 登录
	public void login(View view) {
		switch (view.getId()) {
		case R.id.bt_login:
			account = et_acc.getText().toString().trim();
			password = et_psw.getText().toString().trim();
			if (TextUtils.isEmpty(account) || TextUtils.isEmpty(password)) {
				Toast.makeText(LoginActivity.this, "账号或密码不能为空", Toast.LENGTH_SHORT).show();
				return;
			}
			confirmUesr(account, password);// 
			break;

		default:
			break;
		}
	}

	public void findpsw(View view) {
		switch (view.getId()) {
		case R.id.tv_forgetpsw:
			/*
			 * Intent intent=new Intent(this,SMSConfirm.class);
			 * startActivity(intent);
			 */
			break;

		default:
			break;
		}
	}

	public void register(View view) {
		switch (view.getId()) {
		case R.id.tv_register:
			Intent intent = new Intent(this, RegisterActivity.class);
			startActivity(intent);
			break;
		default:
			break;
		}
	}

	void confirmUesr(String acc, String psw) {
		try {
			mLoader = new UserLoader(this, this);
			mLoader.getUser(acc, psw);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// ±£´æÓÃ»§ÃûºÍÃÜÂë
	void saveAccAndPsw(String account, String password) {
		SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
		editor.putString("NICE", "OK");
		editor.putString("account", account);
		editor.putString("password", password);
		editor.commit();
	}

	@Override
	public UserInfo getData() {	
		return userinfo;
	}

	@Override
	public void setData(UserInfo data) {
		userinfo = data;
	}

	@Override
	public void notifyDataSetChanged() {
		if (userinfo != null) {
			saveAccAndPsw(account, password);
			setUserInfo(userinfo);
			Intent intent = new Intent(this, MainActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			this.finish();
			
		} else {
			Toast.makeText(LoginActivity.this, "登录失败，请重新登录", Toast.LENGTH_SHORT).show();
		}
	}

	void setUserInfo(UserInfo userinfo) {
		switch(userinfo.getStatus()){
		case User.CUSTOMER:
			((ExTraceApplication)getApplication()).setLoginUser(new Customer());
			((ExTraceApplication)getApplication()).getLoginUser()
				.setUserinfo(userinfo);
			break;
		case User.COURIER:
			((ExTraceApplication)getApplication()).setLoginUser(new Courier());
			((ExTraceApplication)getApplication()).getLoginUser()
				.setUserinfo(userinfo);
			break;
		case User.TRANSPOTER:
			((ExTraceApplication)getApplication()).setLoginUser(new Transporter());
			((ExTraceApplication)getApplication()).getLoginUser()
				.setUserinfo(userinfo);
			break;
		}
		((ExTraceApplication)getApplication()).getLoginUser().startLocationService(getApplication());
	}
}
