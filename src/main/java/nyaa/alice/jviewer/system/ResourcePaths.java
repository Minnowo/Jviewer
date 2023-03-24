package nyaa.alice.jviewer.system;

import java.nio.file.Path;

public class ResourcePaths 
{
	public static final String MAIN_FORM_ICON_PATH = "/mainIcon.png";

	
	public static final String HOME_PATH = System.getProperty("user.home");
	
	public static final Path CONFIG_PATH = Path.of(HOME_PATH, ".config");
	public static final Path LOGS_PATH = Path.of(HOME_PATH, ".local", "share", "jviewer");
    
}
