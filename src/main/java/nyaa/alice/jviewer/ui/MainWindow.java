package nyaa.alice.jviewer.ui;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.im4java.process.ProcessStarter;
import org.tinylog.Logger;

import nyaa.alice.jviewer.data.ClipboardUtil;
import nyaa.alice.jviewer.drawing.imaging.ImageBase;
import nyaa.alice.jviewer.drawing.imaging.ImageUtil;
import nyaa.alice.jviewer.drawing.imaging.MultiFrameImage;
import nyaa.alice.jviewer.drawing.imaging.SingleFrameImage;
import nyaa.alice.jviewer.drawing.imaging.dithering.IErrorDiffusion;
import nyaa.alice.jviewer.drawing.imaging.dithering.IPixelTransform;
import nyaa.alice.jviewer.drawing.imaging.enums.ImageFormat;
import nyaa.alice.jviewer.drawing.imaging.exceptions.ImageUnsupportedException;
import nyaa.alice.jviewer.system.GeneralSettings;
import nyaa.alice.jviewer.system.KeyboardSettings;
import nyaa.alice.jviewer.system.OS;
import nyaa.alice.jviewer.system.ResourceLoader;
import nyaa.alice.jviewer.system.ResourcePaths;
import nyaa.alice.jviewer.threading.NotifyingThread;
import nyaa.alice.jviewer.threading.ThreadCompleteListener;
import nyaa.alice.jviewer.ui.components.ComboBoxItemInt;
import nyaa.alice.jviewer.ui.components.ImageTabPage;
import nyaa.alice.jviewer.ui.enums.AntiAliasing;
import nyaa.alice.jviewer.ui.enums.ImageDrawMode;
import nyaa.alice.jviewer.ui.enums.InterpolationMode;
import nyaa.alice.jviewer.ui.enums.RenderQuality;
import nyaa.alice.jviewer.ui.events.DitherPanelListener;
import nyaa.alice.jviewer.ui.events.ImageDisplayImageSizeChangedEvent;
import nyaa.alice.jviewer.ui.events.ImageDisplayListener;
import nyaa.alice.jviewer.ui.events.ImageDisplayZoomChangedEvent;

public class MainWindow extends JFrame
        implements ImageDisplayListener, ChangeListener, ThreadCompleteListener, DitherPanelListener
{

    /**
     * used to prevent events from calling other events in infinite loops
     */
    private boolean _preventOverflow = false;

    private int threadCount = 0;

    private int progressBarUsage = 0;

    Icon iconMenu = UIManager.getIcon("html.pendingImage");

    JPanel mainContentPanel, splitPaneLeftPanel, statusPanel;
    JMenuBar menuBar;
    JToolBar toolBar;
    JButton barSave, barEdit, barClear;
    JSplitPane mainSplitPane;
    // ImageDisplay mainDisplay;
    JSpinner zoomPercentSpinner;
    JLabel statusLabel;
    JComboBox<ComboBoxItemInt> comboboxInterpolationMode;
    private JMenu mnNewMenu_3;
    private JComboBox<ComboBoxItemInt> comboBoxRenderQuality;
    private JComboBox<ComboBoxItemInt> comboBoxAntiAliasingMode;
    private JCheckBoxMenuItem chckbxmntmNewCheckItem;
    private JCheckBoxMenuItem chckbxmntmNewCheckItem_1;
    private JMenuItem mntmNewMenuItem_2;
    private JMenuItem mntmNewMenuItem_3;
    private JMenuItem mntmNewMenuItem_4;

    DropTarget ddTarget = new DropTarget()
    {
        public synchronized void drop(DropTargetDropEvent evt)
        {
            try
            {
                evt.acceptDrop(DnDConstants.ACTION_COPY);

                List<File> droppedFiles = (List<File>) evt.getTransferable()
                        .getTransferData(DataFlavor.javaFileListFlavor);

                openFilesAsync(droppedFiles);
                // for (File file : droppedFiles)
                // {
                // openInNewTab(file);
                // }
            }
            catch (Exception e)
            {
                Logger.warn(e, "Error recieving file drop");
            }
        }
    };

    SpinnerNumberModel gifFrameNumberModel = new SpinnerNumberModel(Integer.valueOf(0), Integer.valueOf(0), null,
            Integer.valueOf(1));

    DefaultListModel listModel1 = new DefaultListModel();

    class KeyAction extends AbstractAction
    {
        public final String actionName;
        public final KeyStroke keystroke;
        private final Runnable action;

        public KeyAction(String actionName, KeyStroke key, final Runnable action)
        {
            this.actionName = actionName;
            this.action = action;
            this.keystroke = key;
        }

        @Override
        public void actionPerformed(ActionEvent e)
        {
            if (SwingUtilities.isEventDispatchThread())
            {
                this.action.run();
            }
            else
            {
                SwingUtilities.invokeLater(this.action);
            }
        }
    }

    public final KeyAction[] KEY_BINDS = new KeyAction[] {
            
            new KeyAction("Fullscreen", KeyboardSettings.FULLSCREEN, this::toggleFullScreen),
            new KeyAction("OpenInNewTab", KeyboardSettings.OPEN_IN_NEW_TAB, this::askOpenFileInNewTab),
            new KeyAction("OpenInCurrentTab", KeyboardSettings.OPEN_IN_CURRENT_TAB, this::askOpenFileInPlace),
            new KeyAction("CloseCurrentTab", KeyboardSettings.CLOSE_CURRENT_TAB, this::closeCurrentTab),
            new KeyAction("NewTab", KeyboardSettings.NEW_TAB, this::openInNewTab),
            new KeyAction("AlwaysOnTop", KeyboardSettings.ALWAYS_ON_TOP, this::toggleAlwaysOnTop),
            new KeyAction("CloseProgram", KeyboardSettings.CLOSE_PROGRAM_KEY, this::exitProgram),
            new KeyAction("PasteImage", KeyboardSettings.PASTE_IMAGE_KEY, this::pasteImage),
            new KeyAction("CopyImage", KeyboardSettings.COPY_IMAGE_KEY, this::copyImage),
            new KeyAction("NextImage", KeyboardSettings.NEXT_IMAGE_KEY, this::nextImage),
            new KeyAction("PrevImage", KeyboardSettings.PREV_IMAGE_KEY, this::prevImage), 
            
            new KeyAction("MoveImageLeft", KeyboardSettings.MOVE_IMAGE_LEFT, () -> moveImageX(GeneralSettings.IMAGE_X_MOVE_AMOUNT*GeneralSettings.IMAGE_X_MOVE_DIRECTION)),
            new KeyAction("MoveImageRight", KeyboardSettings.MOVE_IMAGE_RIGHT, () -> moveImageX(-GeneralSettings.IMAGE_X_MOVE_AMOUNT*GeneralSettings.IMAGE_X_MOVE_DIRECTION)),
            new KeyAction("MoveImageUp", KeyboardSettings.MOVE_IMAGE_UP, () -> moveImageY( GeneralSettings.IMAGE_Y_MOVE_AMOUNT*GeneralSettings.IMAGE_Y_MOVE_DIRECTION)),
            new KeyAction("MoveImageDown", KeyboardSettings.MOVE_IMAGE_DOWN, () -> moveImageY(-GeneralSettings.IMAGE_Y_MOVE_AMOUNT*GeneralSettings.IMAGE_Y_MOVE_DIRECTION)),
            new KeyAction("ZoomImageIn", KeyboardSettings.ZOOM_IMAGE_IN, () -> zoomImage(15)),
            new KeyAction("ZoomImageOut", KeyboardSettings.ZOOM_IMAGE_OUT, () -> zoomImage(-15)), 
            
    };

    private int splitPaneDividorLocation = 0;

    ItemListener ilToggleAlwaysOnTop = new ItemListener()
    {
        public void itemStateChanged(ItemEvent e)
        {
            setAlwaysOnTop(e.getStateChange() == ItemEvent.SELECTED);
        }
    };
    ItemListener ilToggleLeftPane = new ItemListener()
    {

        public void itemStateChanged(ItemEvent e)
        {
            if (e.getStateChange() != ItemEvent.SELECTED)
            {
                splitPaneDividorLocation = mainSplitPane.getDividerLocation();
                mainSplitPane.setDividerSize(0);
                mainSplitPane.getLeftComponent().setVisible(false);
            }
            else
            {
                mainSplitPane.getLeftComponent().setVisible(true);
                mainSplitPane.setDividerLocation(splitPaneDividorLocation);
                mainSplitPane.setDividerSize(GeneralSettings.MAIN_SPLIT_PANE_DIVISOR_SIZE);
            }
        }
    };
    ItemListener ilToggleImageBorder = new ItemListener()
    {
        public void itemStateChanged(ItemEvent e)
        {
            getCurrentDisplay().setDrawBorder(e.getStateChange() == ItemEvent.SELECTED);
        }
    };

    ItemListener ilToggleAnimation = new ItemListener()
    {
        public void itemStateChanged(ItemEvent e)
        {
            if (_preventOverflow)
                return;

            if (e.getStateChange() == ItemEvent.SELECTED)
            {
                getCurrentDisplay().setAnimationPaused(true);

                if (getCurrentDisplay().getImage() != null
                        && getCurrentDisplay().getImage().GetImageFormat() == ImageFormat.GIF)
                {
                    _preventOverflow = true;

                    gifFrameSpinner.setValue(((MultiFrameImage) getCurrentDisplay().getImage()).getFrameIndex());

                    _preventOverflow = false;
                }
            }
            else
            {
                getCurrentDisplay().setAnimationPaused(false);
            }
        }
    };

    ActionListener cropToSelectionAction = new ActionListener()
    {
        public void actionPerformed(ActionEvent e)
        {
            cropToSelection();
        }
    };

    ActionListener askOverrideImage = new ActionListener()
    {
        public void actionPerformed(ActionEvent e)
        {
            askOverwriteImage();
        }
    };

    ActionListener askSaveImage = new ActionListener()
    {
        public void actionPerformed(ActionEvent e)
        {
            askSaveImage();
        }
    };

    ActionListener alAskOpenFileInPlace = new ActionListener()
    {
        public void actionPerformed(ActionEvent e)
        {
            askOpenFileInPlace();
        }
    };

    ActionListener alAskOpenFileInNewTab = new ActionListener()
    {
        public void actionPerformed(ActionEvent e)
        {
            askOpenFileInNewTab();
        }
    };

    ActionListener alCloseCurrentPage = new ActionListener()
    {
        public void actionPerformed(ActionEvent e)
        {
            closeCurrentTab();
        }
    };

    ActionListener alClearCurrentPage = new ActionListener()
    {
        public void actionPerformed(ActionEvent e)
        {
            clearCurrentImage();
        }
    };

    ChangeListener chzoomPercentSpinnerChanged = new ChangeListener()
    {
        public void stateChanged(ChangeEvent e)
        {
            if (_preventOverflow)
                return;

            _preventOverflow = true;

            getCurrentDisplay().setZoomPercentAndZoomCenter((int) (double) zoomPercentSpinner.getValue());

            _preventOverflow = false;
        }
    };

    ChangeListener gifFrameSpinnerChanged = new ChangeListener()
    {
        public void stateChanged(ChangeEvent e)
        {
            if (_preventOverflow)
                return;

            _preventOverflow = true;

            getCurrentDisplay().setAnimationFrame((int) gifFrameSpinner.getValue());

            chckbxmntmNewCheckItem_3.setState(true);

            _preventOverflow = false;
        }
    };

    ItemListener interpolationModeChangedListener = new ItemListener()
    {
        public void itemStateChanged(ItemEvent e)
        {
            if (_preventOverflow)
                return;

            _preventOverflow = true;
            ComboBoxItemInt i = (ComboBoxItemInt) e.getItem();
            getCurrentDisplay().setInterpolationMode(i.getValue());
            _preventOverflow = false;
        }
    };

    ItemListener renderQualityChangedListener = new ItemListener()
    {
        public void itemStateChanged(ItemEvent e)
        {
            if (_preventOverflow)
                return;

            _preventOverflow = true;
            ComboBoxItemInt i = (ComboBoxItemInt) e.getItem();
            getCurrentDisplay().setRenderQuality(i.getValue());
            _preventOverflow = false;
        }
    };
    ItemListener antiAliasingChangedListener = new ItemListener()
    {
        public void itemStateChanged(ItemEvent e)
        {
            if (_preventOverflow)
                return;

            _preventOverflow = true;
            ComboBoxItemInt i = (ComboBoxItemInt) e.getItem();
            getCurrentDisplay().setRenderQuality(i.getValue());
            _preventOverflow = false;
        }
    };
    ItemListener drawModeChangedListener = new ItemListener()
    {
        public void itemStateChanged(ItemEvent e)
        {
            if (_preventOverflow)
                return;

            _preventOverflow = true;
            ComboBoxItemInt i = (ComboBoxItemInt) e.getItem();
            getCurrentDisplay().setDrawMode(i.getValue());

            _preventOverflow = false;
        }
    };
    private JComboBox<ComboBoxItemInt> comboBoxDrawMode;
    private JMenuItem mntmNewMenuItem_5;
    private JMenuItem mntmNewMenuItem_6;
    private JMenuItem mntmNewMenuItem_7;
    private JMenuItem mntmNewMenuItem_8;
    private JMenuItem mntmNewMenuItem_9;
    private JMenuItem mntmNewMenuItem_10;
    private JProgressBar progressBar;

    protected void initComboBox()
    {
        comboboxInterpolationMode = new JComboBox<ComboBoxItemInt>();
        comboboxInterpolationMode.addItemListener(this.interpolationModeChangedListener);

        // NOTE: the order in which these are added are the order of increment
        // 0th index in the combobox is the InterpolationMode.DEFAULT because it has a
        // value of 0
        // this is used when switching tabs to easily set the combobox to the correct
        // value via the index
        // see this.stateChanged

        comboboxInterpolationMode
                .addItem(new ComboBoxItemInt("Interpolation Nearest Neighbor", InterpolationMode.NEAREST_NEIGHBOR));
        comboboxInterpolationMode.addItem(new ComboBoxItemInt("Interpolation Bilinear", InterpolationMode.BILINEAR));
        comboboxInterpolationMode.addItem(new ComboBoxItemInt("Interpolation Bicubic", InterpolationMode.BICUBIC));

        comboBoxRenderQuality = new JComboBox<ComboBoxItemInt>();
        comboBoxRenderQuality.addItemListener(renderQualityChangedListener);

        // NOTE: the order in which these are added are the order of increment
        // 0th index in the combobox is the RenderQuality.DEFAULT because it has a value
        // of 0
        // this is used when switching tabs to easily set the combobox to the correct
        // value via the index
        // see this.stateChanged
        comboBoxRenderQuality.addItem(new ComboBoxItemInt("Render Default", RenderQuality.DEFAULT));
        comboBoxRenderQuality.addItem(new ComboBoxItemInt("Render Fast", RenderQuality.FAST));
        comboBoxRenderQuality.addItem(new ComboBoxItemInt("Render Quality", RenderQuality.QUALITY));

        comboBoxAntiAliasingMode = new JComboBox<ComboBoxItemInt>();
        comboBoxAntiAliasingMode.addItemListener(antiAliasingChangedListener);

        // NOTE: the order in which these are added are the order of increment
        // 0th index in the combobox is the AntiAliasing.DEFAULT because it has a value
        // of 0
        // this is used when switching tabs to easily set the combobox to the correct
        // value via the index
        // see this.stateChanged
        comboBoxAntiAliasingMode.addItem(new ComboBoxItemInt("AntiAliasing Default", AntiAliasing.DEFAULT));
        comboBoxAntiAliasingMode.addItem(new ComboBoxItemInt("AntiAliasing Disabled", AntiAliasing.DISABLED));
        comboBoxAntiAliasingMode.addItem(new ComboBoxItemInt("AntiAliasing Enabled", AntiAliasing.ENABLED));

        comboBoxDrawMode = new JComboBox<ComboBoxItemInt>();
        comboBoxDrawMode.addItemListener(drawModeChangedListener);

        // NOTE: the order in which these are added are the order of increment
        // 0th index in the combobox is ImageDrawMode.RESIZEABLE because it has a value
        // of 0
        // this is used when switching tabs to easily set the combobox to the correct
        // value via the index
        // see this.stateChanged
        comboBoxDrawMode.addItem(new ComboBoxItemInt("Free Move", ImageDrawMode.RESIZEABLE));
        comboBoxDrawMode.addItem(new ComboBoxItemInt("Awlays Fit Image", ImageDrawMode.FIT_IMAGE));
        comboBoxDrawMode.addItem(new ComboBoxItemInt("Downscale Only", ImageDrawMode.DOWNSCALE_IMAGE));
        comboBoxDrawMode.addItem(new ComboBoxItemInt("Actual Size", ImageDrawMode.ACTUAL_SIZE));
        comboBoxDrawMode.addItem(new ComboBoxItemInt("Stretch", ImageDrawMode.STRETCH));
    }

    protected void initToolbar()
    {
        toolBar = new JToolBar(JToolBar.HORIZONTAL);
        getContentPane().add(toolBar);

        barSave = new JButton("Open");
        barSave.addActionListener(alAskOpenFileInPlace);
        toolBar.add(barSave);

        barEdit = new JButton("Save");
        barEdit.addActionListener(askSaveImage);
        toolBar.add(barEdit);

        barClear = new JButton("Clear");
        barClear.addActionListener(alClearCurrentPage);
        toolBar.add(barClear);

        SpinnerModel model = new SpinnerNumberModel(100d, getCurrentDisplay().MIN_ZOOM_PERCENT,
                getCurrentDisplay().MAX_ZOOM_PERCENT, GeneralSettings.MAIN_ZOOM_SPINNER_CHANGE_VALUE);
        zoomPercentSpinner = new JSpinner(model);
        zoomPercentSpinner.setToolTipText("Zoom Percentage");
        zoomPercentSpinner.addChangeListener(chzoomPercentSpinnerChanged);

        btnNewButton = new JButton("Close Page");
        btnNewButton.addActionListener(alCloseCurrentPage);
        toolBar.add(btnNewButton);

        gifFrameSpinner = new JSpinner();
        gifFrameSpinner.addChangeListener(gifFrameSpinnerChanged);
        gifFrameSpinner
                .setModel(new SpinnerNumberModel(Integer.valueOf(0), Integer.valueOf(0), null, Integer.valueOf(1)));
        gifFrameSpinner.setToolTipText("Gif Frame");
        gifFrameSpinner.setVisible(false);

        toolBar.add(comboboxInterpolationMode);

        toolBar.add(comboBoxDrawMode);
        toolBar.add(zoomPercentSpinner);
        toolBar.add(gifFrameSpinner);
    }

    protected void initMenuBar()
    {
        menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        JMenu mnNewMenu = new JMenu("File");
        menuBar.add(mnNewMenu);

        JMenuItem mntmNewMenuItem = new JMenuItem("Open In Place");
        mntmNewMenuItem.addActionListener(alAskOpenFileInPlace);
        mnNewMenu.add(mntmNewMenuItem);

        mntmNewMenuItem_13 = new JMenuItem("Open In New Tab");
        mntmNewMenuItem_13.addActionListener(alAskOpenFileInNewTab);
        mnNewMenu.add(mntmNewMenuItem_13);

        JMenuItem mntmNewMenuItem_1 = new JMenuItem("Save");
        mntmNewMenuItem_1.addActionListener(askOverrideImage);
        mnNewMenu.add(mntmNewMenuItem_1);

        mntmNewMenuItem_17 = new JMenuItem("Reveal In Explorer");
        mntmNewMenuItem_17.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {

                if (getCurrentDisplay() == null)
                    return;

                OS.openFileExplorerToPath(getCurrentDisplay().getCurrentPath());
            }
        });

        mntmNewMenuItem_18 = new JMenuItem("Save As");
        mntmNewMenuItem_18.addActionListener(askSaveImage);
        mnNewMenu.add(mntmNewMenuItem_18);
        mnNewMenu.add(mntmNewMenuItem_17);

        JMenu mnNewMenu_1 = new JMenu("Edit");
        menuBar.add(mnNewMenu_1);

        mntmNewMenuItem_6 = new JMenuItem("Rotate 90 Left");
        mntmNewMenuItem_6.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                rotateImage(270);
            }
        });
        mnNewMenu_1.add(mntmNewMenuItem_6);

        mntmNewMenuItem_7 = new JMenuItem("Rotate 90 Right");
        mntmNewMenuItem_7.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                rotateImage(90);
            }
        });
        mnNewMenu_1.add(mntmNewMenuItem_7);

        mntmNewMenuItem_8 = new JMenuItem("Flip Horizontal");
        mntmNewMenuItem_8.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                flipHorizontal();
            }
        });

        mntmNewMenuItem_10 = new JMenuItem("Rotate By...");
        mntmNewMenuItem_10.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                askRotateImage();
            }
        });
        mnNewMenu_1.add(mntmNewMenuItem_10);
        mnNewMenu_1.add(mntmNewMenuItem_8);

        mntmNewMenuItem_9 = new JMenuItem("Flip Vertical");
        mntmNewMenuItem_9.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                flipVertical();
            }
        });
        mnNewMenu_1.add(mntmNewMenuItem_9);

        mntmNewMenuItem_11 = new JMenuItem("Convert Greyscale");
        mntmNewMenuItem_11.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                convertGreyscale();
            }
        });

        mntmNewMenuItem_16 = new JMenuItem("Crop To Selection");
        mntmNewMenuItem_16.addActionListener(cropToSelectionAction);
        mnNewMenu_1.add(mntmNewMenuItem_16);
        mnNewMenu_1.add(mntmNewMenuItem_11);

        mntmNewMenuItem_12 = new JMenuItem("Invert Image");
        mntmNewMenuItem_12.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                convertInverse();
            }
        });
        mnNewMenu_1.add(mntmNewMenuItem_12);

        mntmNewMenuItem_19 = new JMenuItem("Dither");
        mntmNewMenuItem_19.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                ditherImage();
            }
        });
        mnNewMenu_1.add(mntmNewMenuItem_19);

        mnNewMenu_3 = new JMenu("View");
        menuBar.add(mnNewMenu_3);

        mntmNewMenuItem_3 = new JMenuItem("Actual Size");
        mntmNewMenuItem_3.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                viewActualImageSize();
            }
        });
        mnNewMenu_3.add(mntmNewMenuItem_3);

        mntmNewMenuItem_4 = new JMenuItem("Fit To Screen");
        mntmNewMenuItem_4.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                fitImageToView();
            }
        });
        mnNewMenu_3.add(mntmNewMenuItem_4);

        mntmNewMenuItem_5 = new JMenuItem("Center Image");
        mntmNewMenuItem_5.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                centerImage();
            }
        });
        mnNewMenu_3.add(mntmNewMenuItem_5);

        chckbxmntmNewCheckItem_2 = new JCheckBoxMenuItem("Image Outline");
        chckbxmntmNewCheckItem_2.addItemListener(ilToggleImageBorder);
        mnNewMenu_3.add(chckbxmntmNewCheckItem_2);

        chckbxmntmNewCheckItem_3 = new JCheckBoxMenuItem("Pause Animation");
        chckbxmntmNewCheckItem_3.addItemListener(ilToggleAnimation);
        mnNewMenu_3.add(chckbxmntmNewCheckItem_3);

        // mnNewMenu_3.add(comboBoxRenderQuality);
        // mnNewMenu_3.add(comboBoxAntiAliasingMode);

        JMenu mnNewMenu_2 = new JMenu("Window");
        menuBar.add(mnNewMenu_2);

        chckbxmntmNewCheckItem_1 = new JCheckBoxMenuItem("Toggle Left Pane");
        chckbxmntmNewCheckItem_1.setSelected(true);
        chckbxmntmNewCheckItem_1.addItemListener(ilToggleLeftPane);
        mnNewMenu_2.add(chckbxmntmNewCheckItem_1);

        chckbxmntmNewCheckItem = new JCheckBoxMenuItem("Always On Top");
        chckbxmntmNewCheckItem.addItemListener(ilToggleAlwaysOnTop);
        mnNewMenu_2.add(chckbxmntmNewCheckItem);

        mntmNewMenuItem_2 = new JMenuItem("Settings");
        mntmNewMenuItem_2.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                new SettingsDialog().showDialog();

                if (GeneralSettings.WRAP_TABS)
                {
                    tabbedPane.setTabLayoutPolicy(JTabbedPane.WRAP_TAB_LAYOUT);
                }
                else
                {
                    tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
                }
            }
        });

        allowImageDragMenuItem = new JCheckBoxMenuItem("Allow Image Drag");
        allowImageDragMenuItem.addItemListener(new ItemListener()
        {
            public void itemStateChanged(ItemEvent e)
            {
                if (getCurrentDisplay() != null)
                {
                    getCurrentDisplay().setImageDraggable(e.getStateChange() == ItemEvent.SELECTED);
                }
            }
        });
        allowImageDragMenuItem.setSelected(true);
        mnNewMenu_2.add(allowImageDragMenuItem);

        allowSelectionMenuItem = new JCheckBoxMenuItem("Allow Selection");
        allowSelectionMenuItem.addItemListener(new ItemListener()
        {
            public void itemStateChanged(ItemEvent e)
            {
                if (getCurrentDisplay() != null)
                {
                    getCurrentDisplay().setAllowSelection(e.getStateChange() == ItemEvent.SELECTED);
                }
            }
        });
        allowSelectionMenuItem.setSelected(true);
        mnNewMenu_2.add(allowSelectionMenuItem);
        mnNewMenu_2.add(mntmNewMenuItem_2);

        mnNewMenu_4 = new JMenu("Debug");
        mnNewMenu_2.add(mnNewMenu_4);

        mntmNewMenuItem_14 = new JMenuItem("Print Folder Contents");
        mntmNewMenuItem_14.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                if (getCurrentDisplay() != null)
                {
                    getCurrentDisplay().directory.printTree();
                }
            }
        });
        mnNewMenu_4.add(mntmNewMenuItem_14);

        mntmNewMenuItem_15 = new JMenuItem("Print Folder Successor");
        mntmNewMenuItem_15.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                // if(avlft != null)
                // {
                // if(currentFile == null)
                // {
                //
                // currentFile = avlft.getFirstPath();
                // System.out.println(currentFile);
                // return;
                // }
                //
                // currentFile = avlft.inOrderSuccessor(currentFile);
                // System.out.println(currentFile);
                // }
            }
        });
        mnNewMenu_4.add(mntmNewMenuItem_15);
    }

    public MainWindow()
    {
        this.setIconImage(ResourceLoader.loadIconResource(ResourcePaths.MAIN_FORM_ICON_PATH));
        this.setTitle(GeneralSettings.MAIN_WINDOW_TITLE);
        this.setSize(847, 659);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        tabbedPane = new TabPage();
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        tabbedPane.addTab(ImageTabPage.EMPTY_TAB_PAGE_NAME, new ImageTabPage(tabbedPane));
        tabbedPane.addChangeListener(this);

        tabbedPane.setDropTarget(ddTarget);

        initComboBox();
        initToolbar();
        initMenuBar();

        mainContentPanel = new JPanel();
        mainContentPanel.setLayout(new GridLayout(1, 1));

        mainSplitPane = new JSplitPane();

        splitPaneLeftPanel = new JPanel();

        getCurrentDisplay().addImageDisplayListener(this);

        mainSplitPane.setLeftComponent(splitPaneLeftPanel);
        splitPaneLeftPanel.setLayout(new BorderLayout(0, 0));

        list = new JList();
        list.setModel(listModel1);

        splitPaneLeftPanel.add(list);
        mainSplitPane.setRightComponent(tabbedPane);

        tabbedPane.setBorder(new EmptyBorder(0, 0, 0, 0));
        mainSplitPane.setDividerLocation(250);

        // create the status bar panel and shove it down the bottom of the frame
        statusPanel = new JPanel();
        // statusPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));

        // statusPanel.setPreferredSize(new Dimension(getWidth(), 16));
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.X_AXIS));
        statusLabel = new JLabel("");
        statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
        statusPanel.add(statusLabel);

        mainContentPanel.add(mainSplitPane);
        getContentPane().add(mainContentPanel, BorderLayout.CENTER);

        getContentPane().add(toolBar, BorderLayout.NORTH);

        getContentPane().add(statusPanel, BorderLayout.SOUTH);

        progressBar = new JProgressBar();
        progressBar.setVisible(false);
        statusPanel.add(progressBar);
        chckbxmntmNewCheckItem_1.setSelected(false);
        this.setLocationRelativeTo(null);
        setupKeybindings();
    }

    private void setupKeybindings()
    {
        if (tabbedPane == null)
            return;

        final int IFW = JComponent.WHEN_IN_FOCUSED_WINDOW;

        for (KeyAction k : this.KEY_BINDS)
        {
            tabbedPane.getInputMap(IFW).put(k.keystroke, k.actionName);
            tabbedPane.getActionMap().put(k.actionName, k);
        }
//        tabbedPane.getInputMap(IFW).put(PASTE_IMAGE.keystroke, PASTE_IMAGE.actionName);
//        tabbedPane.getActionMap().put(PASTE_IMAGE.actionName, PASTE_IMAGE);
//
//        tabbedPane.getInputMap(IFW).put(COPY_IMAGE.keystroke, COPY_IMAGE.actionName);
//        tabbedPane.getActionMap().put(COPY_IMAGE.actionName, COPY_IMAGE);
//
//        tabbedPane.getInputMap(IFW).put(NEXT_IMAGE.keystroke, NEXT_IMAGE.actionName);
//        tabbedPane.getActionMap().put(NEXT_IMAGE.actionName, NEXT_IMAGE);
//
//        tabbedPane.getInputMap(IFW).put(PREV_IMAGE.keystroke, PREV_IMAGE.actionName);
//        tabbedPane.getActionMap().put(PREV_IMAGE.actionName, PREV_IMAGE);
    }

    public void setTitle()
    {
        if (getCurrentDisplay().getImage() == null)
        {
            this.setTitle(GeneralSettings.MAIN_WINDOW_TITLE);
            return;
        }

        this.setTitle(GeneralSettings.MAIN_WINDOW_TITLE + " - " + getCurrentDisplay().getCurrentPath());
    }

    public void setStatusLabelText()
    {
        if (getCurrentDisplay().getImage() == null)
        {
            statusLabel.setText("");
            return;
        }

        final ImageBase i = getCurrentDisplay().getImage();
        final String sep1 = "   ";
        final String sep2 = sep1 + "|" + sep1;

        StringBuilder sb = new StringBuilder();

        Logger.debug("Status label changing size to {} x {}", i.getWidth(), i.getHeight());

        sb.append(sep1 + i.getWidth() + " x " + i.getHeight());

        sb.append(sep2 + ImageFormat.getMimeType(i.GetImageFormat()));

        if (i.GetImageFormat() == ImageFormat.GIF)
        {
            MultiFrameImage g = (MultiFrameImage) i;
            sb.append(sep2 + String.format("%d frames", g.getFrameCount()));
        }

        statusLabel.setText(sb.toString() + sep1);
    }

    public Set<String> listFilesUsingJavaIO(String dir)
    {
        return Stream.of(new File(dir).listFiles()).filter(file -> !file.isDirectory()).map(File::getName)
                .collect(Collectors.toSet());
    }

    public void openInNewTab(ImageBase image)
    {
        ImageTabPage img = new ImageTabPage(tabbedPane);
        tabbedPane.addTab(String.format("%d x %d", image.getWidth(), image.getHeight()), img);
        tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);

        img.addImageDisplayListener(this);

        img.setImage(image, true);

        setStatusLabelText();
    }

    public void openInNewTab(File f)
    {
        ImageTabPage img = new ImageTabPage(tabbedPane);
        tabbedPane.addTab("", img);
        tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);

        img.addImageDisplayListener(this);

        img.tryLoadImage(f.getPath(), true);

        setStatusLabelText();
    }

    public void openInPlace(File f)
    {
        getCurrentDisplay().tryLoadImage(f.getPath(), true);

        setStatusLabelText();
    }

    public void openInPlaceAsync(File f)
    {
        NotifyingThread t = new NotifyingThread()
        {
            @Override
            public void doRun()
            {
                showProgressBar();

                openInPlace(f);

                resetProgressbar();
            }
        };

        this.threadCount += 1;
        t.addListener(this);
        t.start();
    }

    public void openFilesAsync(List<File> f)
    {
        NotifyingThread t = new NotifyingThread()
        {
            @Override
            public void doRun()
            {
                showProgressBar();

                for (File f_ : f)
                {
                    openInNewTab(f_);
                }

                resetProgressbar();
            }
        };

        this.threadCount += 1;
        t.addListener(this);
        t.start();
    }

    public void askOpenFileInNewTab()
    {
        File f = DialogHelper.askChooseFile();

        if (!f.exists())
        {
            return;
        }

        NotifyingThread t = new NotifyingThread()
        {
            @Override
            public void doRun()
            {
                showProgressBar();
                openInNewTab(f);
                resetProgressbar();
            }
        };

        this.threadCount += 1;
        t.addListener(this);
        t.start();
    }

    public void askOpenFileInPlace()
    {
        File f = DialogHelper.askChooseFile();

        if (!f.exists())
        {
            return;
        }

        NotifyingThread t = new NotifyingThread()
        {
            @Override
            public void doRun()
            {
                showProgressBar();
                openInPlace(f);
                resetProgressbar();
            }
        };

        this.threadCount += 1;
        t.addListener(this);
        t.start();
    }

    public void cropToSelection()
    {
        if (getCurrentDisplay().getImage() == null)
            return;

        getCurrentDisplay().cropToSelection();
        getCurrentDisplay().clearSelection();
    }

    public void askSaveImage()
    {
        if (getCurrentDisplay().getImage() == null)
            return;

        File f = DialogHelper.askSaveFile();

        if (f.getPath() == "")
            return;

        NotifyingThread t = new NotifyingThread()
        {
            @Override
            public void doRun()
            {
                showProgressBar();

                try
                {
                    ImageUtil.saveImage(getCurrentDisplay().getImage(), f);
                }
                catch (ImageUnsupportedException e1)
                {
                    Logger.warn(e1, "Failed to save {}", f);
                }

                resetProgressbar();
            }
        };

        this.threadCount += 1;
        t.addListener(this);
        t.start();

    }

    public void askOverwriteImage()
    {
        if (getCurrentDisplay().getImage() == null)
            return;

        int result = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to write changes to the current image?");

        switch (result)
        {
        default:
        case JOptionPane.NO_OPTION:
        case JOptionPane.CANCEL_OPTION:
            return;
        case JOptionPane.YES_OPTION:
            break;
        }

        NotifyingThread t = new NotifyingThread()
        {
            @Override
            public void doRun()
            {
                showProgressBar();

                if (!ImageUtil.SaveImageOntoSelf(getCurrentDisplay().getImage(),
                        getCurrentDisplay().getCurrentPathOrTempPath()))
                {
                    Logger.debug("Failed to save image onto itself");
                }

                resetProgressbar();
            }
        };

        this.threadCount += 1;
        t.addListener(this);
        t.start();

    }

    public void askRotateImage()
    {
        if (getCurrentDisplay().getImage() == null)
            return;

        String s = JOptionPane.showInputDialog("Enter the angle to rotate the image");

        if (s == null)
            return;

        try
        {
            rotateImage(Double.parseDouble(s));
        }
        catch (NumberFormatException ex)
        {
        }
    }

    public void viewActualImageSize()
    {
        getCurrentDisplay().showActualImageSize();
    }

    public void centerImage()
    {
        getCurrentDisplay().CenterCurrentImageWithoutResize();
    }

    public void fitImageToView()
    {
        getCurrentDisplay().CenterCurrentImage();
    }

    public void rotateImage(double degree)
    {
        switch ((int) degree)
        {

        case 270:
            getCurrentDisplay().rotate90Left();
            return;

        case 90:
            getCurrentDisplay().rotate90Right();
            return;
        }
        getCurrentDisplay().rotateImage(degree);
    }

    public void flipVertical()
    {
        getCurrentDisplay().flipVertical();
    }

    public void flipHorizontal()
    {
        getCurrentDisplay().flipHorizontal();
    }

    public void convertGreyscale()
    {
        getCurrentDisplay().greyscaleImage();
    }

    public void convertInverse()
    {
        getCurrentDisplay().invertImage();
    }

    DitherPanel ditherPanel = null;

    public void ditherImage()
    {
        if (getCurrentDisplay() == null)
            return;

        checkSetupDitherPanel();

        mainSplitPane.getLeftComponent().setVisible(true);
        mainSplitPane.setDividerLocation(splitPaneDividorLocation);
        mainSplitPane.setDividerSize(GeneralSettings.MAIN_SPLIT_PANE_DIVISOR_SIZE);
        mainSplitPane.setLeftComponent(this.ditherPanel);
    }

    public void checkSetupDitherPanel()
    {
        if (this.ditherPanel != null)
            return;

        this.ditherPanel = new DitherPanel();
        this.ditherPanel.addDitherListener(this);
    }

    Runnable runProgressBar = new Runnable()
    {
        public void run()
        {
            progressBar.setVisible(true);
            progressBar.setIndeterminate(true);
        }
    };

    Runnable resetProgressBar = new Runnable()
    {
        public void run()
        {
            progressBar.setVisible(false);
            progressBar.setIndeterminate(false);
        }
    };
    private JCheckBoxMenuItem chckbxmntmNewCheckItem_2;
    private JMenuItem mntmNewMenuItem_11;
    private JMenuItem mntmNewMenuItem_12;
    private JList list;
    private TabPage tabbedPane;
    private JMenuItem mntmNewMenuItem_13;
    private JButton btnNewButton;

    public void openInNewTab()
    {
        ImageTabPage img = new ImageTabPage(tabbedPane);
        tabbedPane.addTab("---", img);
        tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);

        img.addImageDisplayListener(this);

    }

    public void toggleAlwaysOnTop()
    {
        chckbxmntmNewCheckItem.setSelected(!chckbxmntmNewCheckItem.isSelected());
        setAlwaysOnTop(chckbxmntmNewCheckItem.isSelected());
    }

    public void toggleFullScreen()
    {
        if(this.getExtendedState() == JFrame.MAXIMIZED_BOTH)
        {
            this.setExtendedState(JFrame.NORMAL); 
        }
        else 
        {
            this.setExtendedState(JFrame.MAXIMIZED_BOTH); 
        }        
    }
    
    public void exitProgram()
    {
        this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }

    public synchronized void showProgressBar()
    {
        progressBarUsage++;

        SwingUtilities.invokeLater(runProgressBar);
    }

    public synchronized void resetProgressbar()
    {
        if (progressBarUsage > 0)
            progressBarUsage--;

        if (progressBarUsage == 0)
            SwingUtilities.invokeLater(resetProgressBar);

    }

    public void clearCurrentImage()
    {
        getCurrentDisplay().setImage(null, true);
        setStatusLabelText();
    }

    public void closeCurrentTab()
    {
        if (tabbedPane.getTabCount() == 1)
            return;

        ImageTabPage tp = getCurrentDisplay();

        tp.setImage(null, true);

        tabbedPane.remove(tp.getCurrentTabIndex());

        setStatusLabelText();
    }

    public ImageTabPage getCurrentDisplay()
    {
        return (ImageTabPage) this.tabbedPane.getSelectedComponent();
    }

    int openedImaegs = 0;
    private JCheckBoxMenuItem chckbxmntmNewCheckItem_3;
    private JSpinner gifFrameSpinner;
    private JMenu mnNewMenu_4;
    private JMenuItem mntmNewMenuItem_14;
    private JMenuItem mntmNewMenuItem_15;
    private JCheckBoxMenuItem allowSelectionMenuItem;
    private JCheckBoxMenuItem allowImageDragMenuItem;
    private JMenuItem mntmNewMenuItem_16;
    private JMenuItem mntmNewMenuItem_17;
    private JMenuItem mntmNewMenuItem_18;
    private JMenuItem mntmNewMenuItem_19;

    public void handleStartArgument(String arg)
    {
        if (arg.contains("="))
        {
            int index = arg.indexOf('=');

            String key = arg.substring(0, index).toLowerCase();
            String value = arg.substring(index + 1);

            switch (key)
            {
            case "im4java_toolpath":

                ProcessStarter.setGlobalSearchPath(value);

                return;
            }

            return;
        }

        File f = new File(arg);
        
        Logger.debug("Opening file from start argument {}", f);

        // limit to 5 for now to prevent computer dying here from accidentally opening
        // way more
        // TODO: make this better isntead of just a limit of 5
        if (f.exists() && !(openedImaegs > 5))
        {
            openedImaegs++;

            openInNewTab(f);
        }
        else
        {
            // there's a good chance if the file has text like chinese or japanese
            // characters
            // it will not get the correct string for the file path,
            Logger.warn("File {} does not exist", f);
        }
    }

    public void handleStartArguments(String[] args)
    {
        NotifyingThread t = new NotifyingThread()
        {
            @Override
            public void doRun()
            {
                showProgressBar();

                for (String a : args)
                {
                    handleStartArgument(a);
                }

                openedImaegs = 0;

                resetProgressbar();
            }
        };

        this.threadCount += 1;
        t.addListener(this);
        t.start();
    }

    public void bringToFront()
    {
        if (getState() != Frame.NORMAL)
        {
            setState(Frame.NORMAL);
        }

        toFront();
        repaint();
    }

    public void moveImageX(int pixels)
    {
        if(this.getCurrentDisplay() == null || this.getCurrentDisplay().getImage() == null)
            return;
        
        this.getCurrentDisplay().setImageX(this.getCurrentDisplay().getImageX() + pixels);
    }
    
    public void moveImageY(int pixels)
    {
        if(this.getCurrentDisplay() == null || this.getCurrentDisplay().getImage() == null)
            return;
        
        this.getCurrentDisplay().setImageY(this.getCurrentDisplay().getImageY() + pixels);
    }
    
    public void zoomImage(int percent)
    {
        if(this.getCurrentDisplay() == null || this.getCurrentDisplay().getImage() == null)
            return;
        
        this.getCurrentDisplay().setZoomPercentAndZoomCenter(this.getCurrentDisplay().getZoomPercent() + percent);
//        this.getCurrentDisplay().setZoomPercent();
    }
    
    public void pasteImage()
    {
        BufferedImage img = ClipboardUtil.getClipboardImage();

        if (img == null)
            return;

        openInNewTab(SingleFrameImage.fromBuffered(img));
    }

    public void copyImage()
    {
        if (getCurrentDisplay() == null || getCurrentDisplay().getImage() == null)
            return;

        ClipboardUtil.copyImageToClipboard(getCurrentDisplay().getImage().getBuffered());
    }

    public void nextImage()
    {
        if (getCurrentDisplay() == null)
            return;

        File path = getCurrentDisplay().getCurrentPath();

        while (true)
        {
            if (GeneralSettings.ALWAYS_WAIT_DIR_LOAD_FINISH)
                getCurrentDisplay().directory.waitUntilLoadFinished();

            File f = getCurrentDisplay().directory.inOrderSuccessor(path);

            if (f == null)
                return;

            if (!f.isFile())
            {
                path = f;
                continue;
            }

            openInPlaceAsync(f);
            return;
        }
    }

    public void prevImage()
    {
        if (getCurrentDisplay() == null)
            return;

        File path = getCurrentDisplay().getCurrentPath();

        while (true)
        {
            if (GeneralSettings.ALWAYS_WAIT_DIR_LOAD_FINISH)
                getCurrentDisplay().directory.waitUntilLoadFinished();

            File f = getCurrentDisplay().directory.inOrderPredessor(path);

            if (f == null)
                return;

            if (!f.isFile())
            {
                path = f;
                continue;
            }

            openInPlaceAsync(f);
            return;
        }
    }

    public void updateGifAnimationStuff()
    {
        if (_preventOverflow)
            return;

        _preventOverflow = true;

        ImageTabPage tp = getCurrentDisplay();
        ImageBase i = tp.getImage();

        if (i != null && i.GetImageFormat() == ImageFormat.GIF)
        {
            MultiFrameImage g = (MultiFrameImage) i;

            gifFrameNumberModel.setMaximum(Integer.valueOf(g.getFrameCount() - 1));
            gifFrameNumberModel.setValue(g.getFrameIndex());
            gifFrameSpinner.setModel(gifFrameNumberModel);

            gifFrameSpinner.setVisible(true);

            chckbxmntmNewCheckItem_3.setSelected(tp.getAnimationPaused());
        }
        else
        {
            gifFrameSpinner.setVisible(false);
        }

        _preventOverflow = false;
    }

    @Override
    public void ImageZoomChanged(ImageDisplayZoomChangedEvent e)
    {
        if (_preventOverflow)
            return;

        _preventOverflow = true;

        this.zoomPercentSpinner.setValue(e.getZoomLevelPercent());

        _preventOverflow = false;
    }

    @Override
    public void ImageSizeChanged(ImageDisplayImageSizeChangedEvent e)
    {
        setStatusLabelText();
        updateGifAnimationStuff();
    }

    @Override
    public void stateChanged(ChangeEvent e)
    {
        if (_preventOverflow)
            return;

        _preventOverflow = true;

        ImageTabPage tp = getCurrentDisplay();

        tp.setAllowSelection(allowSelectionMenuItem.isSelected());
        tp.setImageDraggable(allowImageDragMenuItem.isSelected());

        zoomPercentSpinner.setValue(tp.getZoomPercent());

        comboBoxAntiAliasingMode.setSelectedIndex(tp.getAntiAliasing());
        comboBoxDrawMode.setSelectedIndex(tp.getDrawMode());
        comboboxInterpolationMode.setSelectedIndex(tp.getInterpolationMode());
        comboBoxRenderQuality.setSelectedIndex(tp.getRenderQuality());

        _preventOverflow = false;

        updateGifAnimationStuff();
        setStatusLabelText();
        setTitle();
    }

    @Override
    public void ImageChanged()
    {
        updateGifAnimationStuff();
        setStatusLabelText();
        setTitle();
    }

    @Override
    public void notifyOfThreadComplete(NotifyingThread t)
    {
        Logger.debug("Thread {} exiting", t.getName());
        this.threadCount -= 1;
    }

    @Override
    public void previewDither(IPixelTransform transform, IErrorDiffusion dither)
    {
        getCurrentDisplay().cancelDitherBufferCreation();

        NotifyingThread t = new NotifyingThread()
        {
            @Override
            public void doRun()
            {
                showProgressBar();

                getCurrentDisplay().createDitherBufferImage(transform, dither);

                resetProgressbar();
            }
        };

        this.threadCount += 1;
        t.addListener(this);
        t.start();

    }

    @Override
    public void cancelDither()
    {
        getCurrentDisplay().cancelDitherBufferCreation();
    }

    @Override
    public void applyDither()
    {
        getCurrentDisplay().applyDitherFromBuffer();
    }

    @Override
    public void discardDither()
    {
        getCurrentDisplay().clearDitherBuffer();
    }
}
