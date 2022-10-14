package Configuration;

import javax.swing.UIManager;

public class GUISettings 
{
	public static final boolean DEBUG_MODE = true;
	
	public static int MAIN_SPLIT_PANE_DIVISOR_SIZE = (Integer) UIManager.get("SplitPane.dividerSize");
	
	public static double MAIN_ZOOM_SPINNER_CHANGE_VALUE = 15d;
}
