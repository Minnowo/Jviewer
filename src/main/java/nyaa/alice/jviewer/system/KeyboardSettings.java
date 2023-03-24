package nyaa.alice.jviewer.system;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

public class KeyboardSettings
{
    public static final KeyStroke FULLSCREEN =KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK);
    public static final KeyStroke OPEN_IN_NEW_TAB =KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK);
    public static final KeyStroke OPEN_IN_CURRENT_TAB =KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK);
    public static final KeyStroke CLOSE_CURRENT_TAB =KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.CTRL_DOWN_MASK);
    
    public static final KeyStroke NEW_TAB =KeyStroke.getKeyStroke(KeyEvent.VK_T, InputEvent.CTRL_DOWN_MASK);
    
    public static final KeyStroke ALWAYS_ON_TOP =KeyStroke.getKeyStroke(KeyEvent.VK_T, InputEvent.SHIFT_DOWN_MASK);
    
    public static final KeyStroke CLOSE_PROGRAM_KEY = KeyStroke.getKeyStroke(KeyEvent.VK_Q, 0);
    
    public static final KeyStroke PASTE_IMAGE_KEY = KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK);

    public static final KeyStroke COPY_IMAGE_KEY = KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK);

    public static final KeyStroke NEXT_IMAGE_KEY = KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.CTRL_DOWN_MASK);

    public static final KeyStroke PREV_IMAGE_KEY = KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK);
}
