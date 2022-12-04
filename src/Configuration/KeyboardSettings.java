package Configuration;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

public class KeyboardSettings 
{
	public static final KeyStroke PASTE_IMAGE_KEY = KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK);
	
	public static final KeyStroke COPY_IMAGE_KEY = KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK);
	
	public static final KeyStroke NEXT_IMAGE_KEY = KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.CTRL_DOWN_MASK);
	
	public static final KeyStroke PREV_IMAGE_KEY = KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK);
}
