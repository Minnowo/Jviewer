package Configuration;

import java.util.logging.Level;

import javax.swing.UIManager;

public class GUISettings 
{
	public static final boolean DEBUG_MODE = true;
	
	public static int MAIN_SPLIT_PANE_DIVISOR_SIZE = (Integer) UIManager.get("SplitPane.dividerSize");
	
	public static double MAIN_ZOOM_SPINNER_CHANGE_VALUE = 15d;
	
	public static boolean WRAP_TABS = false;
	
	public static String MAIN_WINDOW_TITLE = "Jviewer";
	
	public static Level LOG_LEVEL = Level.ALL;
	
	public static boolean CENTER_IMAGE_ON_RESIZE = true;
}
