package extrace.ui.domain;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.BaseColumns;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.provider.MediaStore.MediaColumns;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.GetChars;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import extrace.loader.ExpressLoader;
import extrace.loader.ImgSender;
import extrace.misc.model.CustomerInfo;
import extrace.misc.model.ExpressSheet;
import extrace.net.HttpUtil;
import extrace.net.IDataAdapter;
import extrace.ui.main.ExTraceApplication;
import extrace.ui.main.R;
import extrace.ui.misc.CustomerListActivity;
import zxing.util.CaptureActivity;

@SuppressLint("NewApi")
public class ExpressEditActivity extends ActionBarActivity
		implements ActionBar.TabListener, IDataAdapter<ExpressSheet> {

	// public static final int INTENT_NEW = 1;
	// public static final int INTENT_EDIT = 2;

	public static final int REQUEST_CAPTURE = 100;
	public static final int REQUEST_RCV = 101;
	public static final int REQUEST_SND = 102;
	
	public static final int TAKE_PHOTO = 1;
	public static final int CROP_PHOTO = 2;
	public static final int CHOOSE_PHOTO = 3;
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	private static ExpressSheet mItem;

	private ExpressLoader mLoader;
	private Intent mIntent;
	private ExpressEditFragment1 baseFragment;
	private ExpressEditFragment2 externFragment;
	private MenuItem action_menu_item;
	private boolean new_es = false; // 新建
	
	static Button bt_takePhoto;
	static ImageView image;
    static Button bt_choose;
    static Uri imageUri;
    static Button uploadImage;
    String imagePath;
    
    //我在这里添加的

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_express_edit);

		// Set up the action bar.
		final ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the activity.
		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
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
			actionBar.addTab(actionBar.newTab().setText(mSectionsPagerAdapter.getPageTitle(i)).setTabListener(this));
		}

		mIntent = getIntent();
		if (mIntent.hasExtra("Action")) {
			if (mIntent.getStringExtra("Action").equals("New")) {
				new_es = true;
				StartCapture();
			} else if (mIntent.getStringExtra("Action").equals("Query")) {
				StartCapture();
			} else if (mIntent.getStringExtra("Action").equals("Edit")) {
				ExpressSheet es;
				if (mIntent.hasExtra("ExpressSheet")) {
					es = (ExpressSheet) mIntent.getSerializableExtra("ExpressSheet");
					Refresh(es.getID());
				} else {
					this.setResult(RESULT_CANCELED, mIntent);
					this.finish();
				}
			} else if (mIntent.getStringExtra("Action").equals("SEND")){
				StartCapture();
			}else {
				this.setResult(RESULT_CANCELED, mIntent);
				this.finish();
			}
		} else {
			this.setResult(RESULT_CANCELED, mIntent);
			this.finish();
		}

	}

	
	/*
	 * w外部写一个静态方法
	 * 在这里添加的东西
	 * */
    //下面是两种获得路径处理图片的方法
	private void handleImageBeforeKitKat(Intent data) {
		Uri uri=data.getData();
		imagePath=getImagePath(uri, null);
		displayImage(imagePath);
	}

	private void handleImageOnKitKat(Intent data) {
		imagePath = null;
		Uri uri = data.getData();
		if (DocumentsContract.isDocumentUri(this, uri)) {
			// 如果是document类型的uri，则通过document id处理
			String docid = DocumentsContract.getDocumentId(uri);
			if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
				String id = docid.split(":")[1];// 解析出数字格式的id
				String selection = BaseColumns._ID + "=" + id;
				imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
			} else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
				Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),
						Long.valueOf(docid));
				imagePath=getImagePath(contentUri,null);
			}
		}else if ("content".equalsIgnoreCase(uri.getScheme())) {
			//如果不是document类型的Uri，则使用普通方式处理
			imagePath=getImagePath(uri,null);
		}
		displayImage(imagePath);//根据图片路径显示图片
	}

	private void displayImage(String imagePath) {
		if (imagePath!=null) {
			Bitmap bitmap=BitmapFactory.decodeFile(imagePath);
			image.setImageBitmap(bitmap);
		}else {
			Toast.makeText(this, "failed to get image",Toast.LENGTH_SHORT).show();
		}
	}

	public String getImagePath(Uri externalContentUri, String selection) {
		String path = null;
		// 通过uri和selection来获取真实的图片路径
		Cursor cursor = getContentResolver().query(externalContentUri, null, selection, null, null);
		if (cursor != null) {
			if (cursor.moveToNext()) {
				path = cursor.getString(cursor.getColumnIndex(MediaColumns.DATA));
			}
			cursor.close();
		}
		return path;
		
	}
	
	public static Context getCotext(){
		return getCotext();
	}
	
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.express_edit, menu);
		action_menu_item = menu.findItem(R.id.action_action);
		action_menu_item.setVisible(false);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) {
		case R.id.action_action:
			if (item.getTitle().equals("收件")) {
				Receive(mItem.getID());
			} else if (item.getTitle().equals("交付")) {
				Delivery(mItem.getID());
			} else if (item.getTitle().equals("追踪")) {
				Tracert(mItem.getID());
			}
			return true;
		case R.id.action_ok:
			Save();
			return true;
		case R.id.action_refresh:
			if (mItem != null) {
				Refresh(mItem.getID());
			}
			return true;
		case (android.R.id.home):
			mIntent.putExtra("ExpressSheet", mItem);
			this.setResult(RESULT_OK, mIntent);
			this.finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
	}

	@Override
	public ExpressSheet getData() {
		return mItem;
	}

	@Override
	public void setData(ExpressSheet data) {
		mItem = data;
	}

	@Override
	public void notifyDataSetChanged() {
		if (baseFragment != null) {
			baseFragment.RefreshUI(mItem);

		}
		MenuDisplay(mItem.getStatus());
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		CustomerInfo customer;
		//在这里添加的
		switch (requestCode) {
		case TAKE_PHOTO:
			if (resultCode == RESULT_OK) {
			    /*Bundle bundle = data.getExtras();
			    Bitmap bitmap = (Bitmap) bundle.get("data");
			    image.setImageBitmap(bitmap);*/
			    //image.setBackgroundColor(color.red);
			    Intent intent = new Intent("com.android.camera.action.CROP");
				intent.setDataAndType(imageUri, "image/*");
				intent.putExtra("scale", true);
				intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
				Bitmap bitmap;
				try {
					InputStream in = getContentResolver().openInputStream(imageUri);
					byte[] a = new byte[in.available()];
					in.read(a);
					
					bitmap = BitmapFactory.decodeByteArray(a, 0, a.length);
					image.setImageBitmap(bitmap);
					System.out.println(a);
					ExpressEditFragment2.setImg2(a);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			/*	startActivityForResult(intent, CROP_PHOTO);// 启动剪裁程序
			*/	
			}
			 
			break;
		
		case CHOOSE_PHOTO:
			if (resultCode == RESULT_OK) {
				// 判断手机的系统版本号
				if (Build.VERSION.SDK_INT >= 19) {
					// 4.4以上系统使用这个方法处理图片
					handleImageOnKitKat(data);
				}else {
					//4.4以下系统使用这个方法处理图片
					handleImageBeforeKitKat(data);
				}
			}
			break;
		default:
			break;
		}
		
		
	    switch (resultCode) {
		case RESULT_OK:
			switch (requestCode) {    
			case REQUEST_CAPTURE:
				if (data.hasExtra("BarCode")) {
					String id = data.getStringExtra("BarCode");
					try {
						mLoader = new ExpressLoader(this, this);
						if (new_es) {
							new_es = false;
							Log.v("lodad",id);
							mLoader.New(id);
						} else {
							mLoader.Load(id);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				break;
			case REQUEST_RCV:
				if (data.hasExtra("CustomerInfo")) {
					customer = (CustomerInfo) data.getSerializableExtra("CustomerInfo");
					mItem.setRecever(customer);
					baseFragment.displayRcv(mItem);
				}
				break;
			case REQUEST_SND:
				if (data.hasExtra("CustomerInfo")) {
					customer = (CustomerInfo) data.getSerializableExtra("CustomerInfo");
					mItem.setSender(customer);
					baseFragment.displaySnd(mItem);
				}
				break;
			}
			break;
		case RESULT_CANCELED:
			this.finish();
			break;
		default:
			break;
		}
	}

	void MenuDisplay(int status) {
		action_menu_item.setVisible(true);
		action_menu_item.setEnabled(true);
		switch (status) {
		case ExpressSheet.STATUS.STATUS_CREATED:
			action_menu_item.setTitle("收件");
			break;
		case ExpressSheet.STATUS.STATUS_TRANSPORT:
			action_menu_item.setTitle("交付");
			break;
		case ExpressSheet.STATUS.STATUS_DELIVERIED:
			action_menu_item.setTitle("追踪");
			break;
		default:
			action_menu_item.setVisible(false);
			break;
		}
	}

	void Refresh(String id) {
		try {
			mLoader = new ExpressLoader(this, this);
			mLoader.Load(id);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void Receive(String id) {
		try {
			mLoader = new ExpressLoader(this, this);
			mLoader.Receive(id);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void Delivery(String id) {
		try {
			mLoader = new ExpressLoader(this, this);
			mLoader.Delivery(id);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void Tracert(String id) {
		// 快件追踪
	}

	void Save() {
		mLoader = new ExpressLoader(this, this);
		mLoader.Save(mItem);
	}

	private void StartCapture() {
		Intent intent = new Intent();
		intent.putExtra("Action", "Captrue");
		intent.setClass(this, CaptureActivity.class);
		startActivityForResult(intent, REQUEST_CAPTURE);
	}

	private void GetCustomer(int intent_code) {
		Intent intent = new Intent();
		intent.setClass(this, CustomerListActivity.class);
		if (intent_code == REQUEST_RCV) {
			if (baseFragment.mRcvNameView.getTag() == null) {
				intent.putExtra("Action", "New");
			} else {
				intent.putExtra("Action", "New");
				intent.putExtra("CustomerInfo", (CustomerInfo) baseFragment.mRcvNameView.getTag());
			}
		} else if (intent_code == REQUEST_SND) {
			if (baseFragment.mSndNameView.getTag() == null) {
				intent.putExtra("Action", "New");
			} else {
				intent.putExtra("Action", "New");
				intent.putExtra("CustomerInfo", (CustomerInfo) baseFragment.mSndNameView.getTag());
			}
		}
		startActivityForResult(intent, intent_code);
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			switch (position) {
			case 0:
				baseFragment = ExpressEditFragment1.newInstance();
				return baseFragment;
			case 1:
				externFragment = ExpressEditFragment2.newInstance();
				return externFragment;
			default:
				return null;
			}
			// return ExpressEditFragment1.newInstance();
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
	
	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class ExpressEditFragment1 extends Fragment {

		private TextView mIDView;
		private TextView mRcvNameView;
		private TextView mRcvTelCodeView;
		private TextView mRcvDptView;
		private TextView mRcvAddrView;
		private TextView mRcvRegionView;

		private TextView mSndNameView;
		private TextView mSndTelCodeView;
		private TextView mSndDptView;
		private TextView mSndAddrView;
		private TextView mSndRegionView;

		private TextView mRcverView;
		private TextView mRcvTimeView;

		private TextView mSnderView;
		private TextView mSndTimeView;

		private TextView mStatusView;

		private ImageView mbtnCapture;
		private ImageView mbtnRcv;
		private ImageView mbtnSnd;

		public static ExpressEditFragment1 newInstance() {
			ExpressEditFragment1 fragment = new ExpressEditFragment1();
			return fragment;
		}

		public ExpressEditFragment1() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_express_edit1, container, false);
			mIDView = (TextView) rootView.findViewById(R.id.expressId);
			mRcvNameView = (TextView) rootView.findViewById(R.id.expressRcvName);
			mRcvTelCodeView = (TextView) rootView.findViewById(R.id.expressRcvTel);
			mRcvAddrView = (TextView) rootView.findViewById(R.id.expressRcvAddr);
			mRcvDptView = (TextView) rootView.findViewById(R.id.expressRcvDpt);
			mRcvRegionView = (TextView) rootView.findViewById(R.id.expressRcvRegion);

			mSndNameView = (TextView) rootView.findViewById(R.id.expressSndName);
			mSndTelCodeView = (TextView) rootView.findViewById(R.id.expressSndTel);
			mSndAddrView = (TextView) rootView.findViewById(R.id.expressSndAddr);
			mSndDptView = (TextView) rootView.findViewById(R.id.expressSndDpt);
			mSndRegionView = (TextView) rootView.findViewById(R.id.expressSndRegion);

			mRcvTimeView = (TextView) rootView.findViewById(R.id.expressAccTime);
			mSndTimeView = (TextView) rootView.findViewById(R.id.expressDlvTime);

			mStatusView = (TextView) rootView.findViewById(R.id.expressStatus);

			mbtnCapture = (ImageView) rootView.findViewById(R.id.action_ex_capture_icon);
			mbtnCapture.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					((ExpressEditActivity) getActivity()).StartCapture();
				}
			});
			mbtnRcv = (ImageView) rootView.findViewById(R.id.action_ex_rcv_icon);
			mbtnRcv.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					((ExpressEditActivity) getActivity()).GetCustomer(REQUEST_RCV);
				}
			});
			mbtnSnd = (ImageView) rootView.findViewById(R.id.action_ex_snd_icon);
			mbtnSnd.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					((ExpressEditActivity) getActivity()).GetCustomer(REQUEST_SND);
				}
			});
			return rootView;
		}

		void RefreshUI(ExpressSheet es) {
			mIDView.setText(es.getID());
			displayRcv(es);
			displaySnd(es);
			if (es.getAccepteTime() != null){
				Log.v("estime", es.getAccepteTime()+"");
				mRcvTimeView.setText(DateFormat.format("yyyy-MM-dd hh:mm:ss", es.getAccepteTime()));
			}else
				mRcvTimeView.setText(null);
			if (es.getDeliveTime() != null)
				mSndTimeView.setText(DateFormat.format("yyyy-MM-dd hh:mm:ss", es.getDeliveTime()));
			else
				mSndTimeView.setText(null);

			String stText = "";
			switch (es.getStatus()) {
			case ExpressSheet.STATUS.STATUS_CREATED:
				stText = "正在创建";
				break;
			case ExpressSheet.STATUS.STATUS_TRANSPORT:
				stText = "运送途中";
				break;
			case ExpressSheet.STATUS.STATUS_DELIVERIED:
				stText = "已交付";
				break;
			}
			mStatusView.setText(stText);
			displayBtn(es);
		}

		void displayBtn(ExpressSheet es) { // 按钮状态控制
			if (es.getStatus() == ExpressSheet.STATUS.STATUS_CREATED) {
				mbtnRcv.setVisibility(View.VISIBLE);
				mbtnSnd.setVisibility(View.VISIBLE);
			} else {
				mbtnRcv.setVisibility(View.INVISIBLE);
				mbtnSnd.setVisibility(View.INVISIBLE);
			}
		}

		void displayRcv(ExpressSheet es) {
			if (es.getRecever() != null) {
				mRcvNameView.setText(es.getRecever().getName());
				mRcvTelCodeView.setText(es.getRecever().getTelCode());
				mRcvNameView.setTag(es.getRecever());
				mRcvAddrView.setText(es.getRecever().getAddress());
				mRcvDptView.setText(es.getRecever().getDepartment());
				mRcvRegionView.setText(es.getRecever().getRegionString());
			} else {
				mRcvNameView.setText(null);
				mRcvTelCodeView.setText(null);
				mRcvNameView.setTag(null);
				mRcvAddrView.setText(null);
				mRcvDptView.setText(null);
				mRcvRegionView.setText(null);
			}
		}

		void displaySnd(ExpressSheet es) {
			if (es.getSender() != null) {
				mSndNameView.setText(es.getSender().getName());
				mSndTelCodeView.setText(es.getSender().getTelCode());
				mSndNameView.setTag(es.getSender());
				mSndAddrView.setText(es.getSender().getAddress());
				mSndDptView.setText(es.getSender().getDepartment());
				mSndRegionView.setText(es.getSender().getRegionString());
			} else {
				mSndNameView.setText(null);
				mSndTelCodeView.setText(null);
				mSndNameView.setTag(null);
				mSndAddrView.setText(null);
				mSndDptView.setText(null);
				mSndRegionView.setText(null);
			}
		}
	}

	/*
	 * public static class ExpressEditFragment2 extends Fragment {
	 * 
	 *//**
		 * Returns a new instance of this fragment for the given section number.
		 *//*
		 * private ImageView cam; 
		 * private ImageView image;
		 * 
		 * public static ExpressEditFragment2 newInstance() {
		 * ExpressEditFragment2 fragment = new ExpressEditFragment2(); 
		 * // Bundle args = new Bundle(); 
		 * // args.putInt(ARG_SECTION_NUMBER,sectionNumber); 
		 * // fragment.setArguments(args); 
		 * return fragment; 
		 * }
		 * 
		 * 
		 * public ExpressEditFragment2() {
		 * 
		 * }
		 * 
		 * liushuo 处理快件的额外信息 图片2张
		 * 
		 * 
		 * @Override public View onCreateView(LayoutInflater inflater, ViewGroup
		 * container, Bundle savedInstanceState) { View rootView =
		 * inflater.inflate(R.layout.fragment_express_edit2, container, false);
		 * cam = (ImageView) rootView.findViewById(R.id.cam);
		 * cam.setOnClickListener( new View.OnClickListener() {
		 * 
		 * @Override 
		 * public void onClick(View view) { 
		 * Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
		 * startActivityForResult(intent,Activity.DEFAULT_KEYS_DIALER);
		 * 
		 * } });
		 * 
		 * image = (ImageView) rootView.findViewById(R.id.image); return
		 * rootView; } public void onActivityResult(int requestCode, int
		 * resultCode, Intent data) { super.onActivityResult(requestCode,
		 * resultCode, data);
		 * 
		 * if (resultCode == Activity.RESULT_OK) { String sdStatus =
		 * Environment.getExternalStorageState(); if
		 * (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用 return; }
		 * Bundle bundle = data.getExtras(); Bitmap bitmap = (Bitmap)
		 * bundle.get("data");// 获取相机返回的数据，并转换为Bitmap图片格式 FileOutputStream b =
		 * null; File file = new File("/sdcard/myImage/"); file.mkdirs(); //
		 * 创建文件夹，名称为myimage
		 * 
		 * //照片的命名，目标文件夹下，以当前时间数字串为名称，即可确保每张照片名称不相同。 ExpressSheet es=mItem;
		 * String str=es.getID(); String str=null; Date date=null;
		 * SimpleDateFormat format = new
		 * SimpleDateFormat("yyyyMMddHHmmss");//获取当前时间，进一步转化为字符串 date =new
		 * Date(); str=format.format(date); String fileName =
		 * "/sdcard/myImage/"+str+".jpg"; try { b = new
		 * FileOutputStream(fileName);
		 * bitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);// 把数据写入文件 }
		 * catch (FileNotFoundException e) { e.printStackTrace(); } finally {
		 * try { b.flush(); b.close(); } catch (IOException e) {
		 * e.printStackTrace(); } if (data!= null) { Bitmap cameraBitmap =
		 * (Bitmap) data.getExtras().get("data");
		 * System.out.println("fdf================="+data.getDataString());
		 * image.setImageBitmap(cameraBitmap);
		 * 
		 * System.out.println("成功======"+cameraBitmap.getWidth()+cameraBitmap.
		 * getHeight()); } }//finally } }
		 */
	
	public static class ExpressEditFragment2 extends Fragment {
		public static byte[] img2;
		public static void setImg2(byte[] a){
			img2 = a;
		}
		public static ExpressEditFragment2 newInstance() {
			ExpressEditFragment2 fragment = new ExpressEditFragment2();
			// Bundle args = new Bundle();
			// args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			// fragment.setArguments(args);
			return fragment;
		}
       
		public ExpressEditFragment2() {

		}
        
		
	   @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_express_edit2, container, false);
			bt_takePhoto = (Button) rootView.findViewById(R.id.bt_takePhoto);
			image = (ImageView) rootView.findViewById(R.id.image);
			bt_choose = (Button) rootView.findViewById(R.id.bt_chooseAlbum);
			uploadImage=(Button) rootView.findViewById(R.id.uploadImage);
			
			//image.setBackgroundColor(Color.LTGRAY);
			
		    
			bt_takePhoto.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ExpressSheet es=mItem;
				String str=es.getID();
				File file = new File(Environment.getExternalStorageDirectory(), str+".jpg");
				try {
					if (file.exists()) {
						file.delete();
					}
					file.createNewFile();
				} catch (Exception e) {
					e.printStackTrace();
				}
				imageUri = Uri.fromFile(file);
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
				getActivity().startActivityForResult(intent, TAKE_PHOTO);// 启动相机程序
				//image.setBackgroundColor(color.lightgray);
				
			}
	  });
			bt_choose.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent2 = new Intent("android.intent.action.GET_CONTENT");
					intent2.setType("image/*");
					getActivity().startActivityForResult(intent2, CHOOSE_PHOTO);// 打开相册
				}
			});
			uploadImage.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					String w = byte2hex(img2);
					System.out.println(w+"shihuhuhuhuhu");
					new ImgSender(getActivity()).fun(""+mItem.getID(),w);
				}
			});
			return rootView;
	    }
	   public String byte2hex(byte[] b) // 二进制转字符串
		{
		   StringBuffer sb = new StringBuffer();
		   String stmp = "";
		   for (int n = 0; n < b.length; n++) {
		    stmp = Integer.toHexString(b[n] & 0XFF);
		    if (stmp.length() == 1){
		    sb.append("0" + stmp);
		    }else{
		    sb.append(stmp);
		    }
		    
		   }
		   return sb.toString();
		}

		//这里的onActivityResult跟上面的重复么？为什么不能处理来自ExpressEditFragment2的startActivity

	}
	

}
