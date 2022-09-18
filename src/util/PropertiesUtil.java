package util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.Properties;

import constraint.MessageConstraint;

public class PropertiesUtil {
	// 使わなくてもいいけど、用意しておくのがUtil⇒「あくまでこの範疇のものを使ってね。」というもの
	// Javaのオブジェクト指向は「管理がしやすいもの（外れたモノを見つける方が楽）」＝ルール（100％正しいとは言えないが、楽にするためのもの）
	
	// 変数
	// プロパティファイルのパス
	private String propertiesFilePath = MessageConstraint.MESSAGE_PROPERTIES_PATH;
	// プロパティファイルの文字コード
	private String charsetName = "UTF-8";

	public Properties getProperty() {
		Properties pro = new Properties();
		try {
			pro.load(new InputStreamReader(new FileInputStream(propertiesFilePath), charsetName));
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
		// 数字として扱う数と表示として扱う数
		// 数字を扱うときに数値と文字列の両方がある（ヒント）
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
