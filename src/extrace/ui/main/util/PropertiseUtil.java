package extrace.ui.main.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

import android.content.Context;

public class PropertiseUtil {
	//��ȡ�����ļ� 
	public static Properties loadConfig(Context context, String file) {
		Properties properties = new Properties();
		try {
			FileInputStream s = new FileInputStream(file);
			properties.load(s);
		} catch (Exception e) {
			e.printStackTrace();
		return null;
		}
		return properties;
	}
	//���������ļ�
	public static boolean saveConfig(Context context, String file, Properties properties) {
		try {
			File fil=new File(file);
			if(!fil.exists())
			fil.createNewFile();
			FileOutputStream s = new FileOutputStream(fil);
			properties.store(s, "");
		} catch (Exception e) {
			e.printStackTrace();
		return false;
		}
		return true;
		}
}
