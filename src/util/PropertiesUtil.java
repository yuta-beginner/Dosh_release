package util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.Properties;

import constraint.MessageConstraint;

public class PropertiesUtil {
	// 変数
	MessageConstraint mc = new MessageConstraint();
	// プロパティファイルのパス
	private String propertiesPath;
	// プロパティファイルの文字コード
	private String charsetName = "UTF-8";
	
	public PropertiesUtil() {
		setPropetiesPath();
	}
	
	public void setPropetiesPath() {
		mc.setMessagePropertiesPath();
		propertiesPath = mc.getMessagePropertiesPath();
	}

	public Properties getProperty() {
		//setPropetiesPath();
		Properties pro = new Properties();
		try {
			//pro.load(new InputStreamReader(new FileInputStream(propertiesFilePath), charsetName));
			pro.load(new InputStreamReader(new FileInputStream(propertiesPath), charsetName));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return pro;
	}

	public String getValueString(String key) {
		return getProperty().getProperty(key);
	}

	// チップの枚数をpropertiesファイルから取ってくるためのメソッド。
	public int getValueInt(String key) {
		return Integer.parseInt(getProperty().getProperty(key));
	}

	public String getValueFormattedString(String key, Object[] obj) {
		return MessageFormat.format(getProperty().getProperty(key), obj);
	}

	public String getValueStringList(String key, int i, int j) {
		String retString = "";
		for (int k = i; k <= j; k++) {
			retString += getProperty().getProperty(key + k);
		}
		return retString;
	}

}
