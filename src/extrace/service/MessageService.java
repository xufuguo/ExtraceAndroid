package extrace.service;

import extrace.ui.main.ExTraceApplication;
import extrace.ui.main.R;

import java.util.ArrayList;
import java.util.List;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import extrace.auto.Imp.Courier;
import extrace.auto.Imp.Customer;
import extrace.auto.Imp.Transporter;
import extrace.auto.Interface.User;
import extrace.misc.model.Message;
import extrace.net.HttpUtil;
import extrace.net.IDataAdapter;
import extrace.net.JsonUtils;
import extrace.ui.domain.MessageActivity;

public class MessageService extends Service{

	private static final String TAG = "MessageService";
    boolean threadDisable;
    //loadMessage
    List<Message> msgList = null;
   
   // private MsgBinder binder = new MsgBinder();
    //获取消息线程  
    private MessageThread messageThread = null;  
    //点击查看  
    private Intent messageIntent = null;  
    private PendingIntent messagePendingIntent = null;  
    private Notification messageNotification = null;
    //通知栏消息  
    
    private int messageNotificationID = 1000;  
    private NotificationManager messageNotificatioManager = null; 
    String url = null;
	public MessageService() {
		// TODO Auto-generated constructor stub
		Log.v(TAG, "构造方法");
	}
	@Override
	public void onCreate() {
		super.onCreate();
		 //初始化  
		Log.v(TAG, "------MessageService Create");
	}
	
	@Override
	public void onDestroy() {
        super.onDestroy();
        /** 服务停止时，终止计数进程 */
        this.threadDisable = true;
        Log.v(TAG, "-------Service Destroy");
    }
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		Log.v(TAG, "-------onBind");
		return null;
	}
	@Override
	public boolean onUnbind( Intent intent){
		Log.v(TAG, "-------unBind");
		return threadDisable;
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId){
        //开启线程 
		url = ((ExTraceApplication) this.getApplicationContext()).getMiscServiceUrl();
        messageNotificatioManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);  
        messageIntent = new Intent(this, MessageActivity.class);  
        messagePendingIntent = PendingIntent.getActivity(this, 0, messageIntent, 0); 
        
        messageThread = new MessageThread();  
        messageThread.isRunning = true;  
        messageThread.start();  
        Log.v(TAG, "线程已开启");
		return super.onStartCommand(intent, flags, startId);
	}
	
	 class MessageThread extends Thread{  
	        //运行状态
	        public boolean isRunning = true;  
	        @SuppressLint("NewApi")
			public void run() {  
	            while(isRunning){  
	                try {  
	                    Thread.sleep(15000);  
	                    //获取服务器消息  
	                    String serverMessage = getServerMessage();
	                   // Log.v(TAG, "线程中 :"+ serverMessage);
	                    if(serverMessage != null && !"".equals(serverMessage)){ 
	                        //更新通知栏  
	                        messageNotification = new Notification.Builder(MessageService.this) 
	                        		 .setContentTitle("新消息") 
	                        		 .setContentText("您收到了新的待收快件") 
	                        		 .setContentIntent(messagePendingIntent)
	                        		 .setSmallIcon(R.drawable.msg)
	                        		 .setDefaults(Notification.DEFAULT_SOUND)
	                        		 .build();
	                        messageNotificatioManager.notify(messageNotificationID, messageNotification);  
	                        //每次通知完，通知ID递增一下，避免消息覆盖掉  
	                        //messageNotificationID++;
	                    }  
	                } catch (InterruptedException e) {  
	                    e.printStackTrace();  
	                }  
	            }  
	        }  
	    }  
	 
	 public String getServerMessage(){
		 User user = ((ExTraceApplication)getApplication()).getLoginUser();
		 if(user.getClass().equals(Customer.class)){
			 Log.v(TAG, "客户状态");
			 return "";
		 }
		 else if(user.getClass().equals(Transporter.class)){
			 Log.v(TAG, "转运员状态");
			 return "";
		 }
		 boolean b = loadForUser(user.getUserinfo().getUID());
		 //Log.v(TAG, "发送查询请求之前;uid="+user.getUserinfo().getUID());
		 //boolean b = loadForCus("13027711597");
		 Log.v(TAG, "getServerMessage");
		 if(b)	return "YES!";
		 else return "";
	    }
	
	
	public boolean loadForUser(int uid){
		final String uri = url + "loadMessageForUser/" + uid;
		String res = HttpUtil.getRequest(uri);
		Log.v("loadForUser:result:" , res);
		
		String[] args = res.split("isrecv");
		if(res == "" || res == null || args.length < 2)
			return false;
		char t = args[1].charAt(2);
		Log.v(TAG, "t=" + t);
		if(t == 'f')	return true;
		else return false;
		/*List<Message> m;
		m = JsonUtils.fromJson(res, new TypeToken<List<Message>>(){});
		if(m.size() > 0) Log.v(TAG, m.get(0).getSenderName());
		return true;*/
		
	}
	
	public boolean loadForCus(String tel){
		final String uri = url + "loadMessageForCustomer/" + tel;
		String res = HttpUtil.getRequest(uri);
		Log.v(TAG + " result:" , res);
		if(res == "" || res == null)
			return false;
		else 
			return true;
		
	}
}
