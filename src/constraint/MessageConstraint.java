package constraint;

import java.nio.file.Path;
import java.nio.file.Paths;

public class MessageConstraint {
	// メッセージに関するプロパティファイルを管理する。
	private final String MESSAGE_PROPERTIES="src/message.properties";
	private String messagePropertiesPath;
	
	public void setMessagePropertiesPath() {
        Path path = Paths.get(MESSAGE_PROPERTIES);
        messagePropertiesPath = path.toAbsolutePath().toString();
	}
	
	public String getMessagePropertiesPath() {
		return messagePropertiesPath;
	}
}
