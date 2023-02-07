package nyaa.alice.jviewer.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Set;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import nyaa.alice.jviewer.drawing.imaging.dithering.Dithers;
import nyaa.alice.jviewer.drawing.imaging.dithering.IErrorDiffusion;
import nyaa.alice.jviewer.drawing.imaging.dithering.IPixelTransform;
import nyaa.alice.jviewer.drawing.imaging.dithering.Transforms;
import nyaa.alice.jviewer.drawing.imaging.dithering.Transforms.SimpleIndexedPalettePixelTransform;
import nyaa.alice.jviewer.drawing.imaging.dithering.Transforms.SimpleIndexedPalettePixelTransform16;
import nyaa.alice.jviewer.drawing.imaging.dithering.Transforms.SimpleIndexedPalettePixelTransform256;
import nyaa.alice.jviewer.drawing.imaging.dithering.Transforms.SimpleIndexedPalettePixelTransform8;
import nyaa.alice.jviewer.ui.events.DitherPanelListener;
import nyaa.alice.jviewer.ui.events.ImageDisplayListener;

public class DitherPanel extends JPanel
{
    /**
     * listeners to evens created by this control
     */
    private Set<DitherPanelListener> listeners = new HashSet<DitherPanelListener>();

    IErrorDiffusion ditherAlgorithm = null;
    IPixelTransform colorTransform = null;

    JRadioButton radioButton_ColorTransformNone, radioButton_ColorTransformCustom, radioButton_ColorTransformColor,
            radioButton_ColorTransformMonochrome;

    JRadioButton radioButton_DitherNone, radioButton_DitherFloydSteinberg, radioButton_DitherSierra,
            radioButton_DitherBurkes, radioButton_Dither2RowSierra, radioButton_DitherJarvis,
            radioButton_DitherSierraLite, radioButton_DitherStucki, radioButton_DitherAtkinson,
            radioButton_DitherBayer2, radioButton_DitherBayer4, radioButton_DitherBayer3, radioButton_DitherBayer8,
            radioButton_DitherRandomNoise;

    JSpinner spinner;
    SpinnerNumberModel gifFrameNumberModel = new SpinnerNumberModel(Integer.valueOf(128), Integer.valueOf(0),
            Integer.valueOf(255), Integer.valueOf(1));

    private static class ColorPallete
    {
        public SimpleIndexedPalettePixelTransform transform;
        public String display;

        public ColorPallete(SimpleIndexedPalettePixelTransform trans, String display)
        {
            this.transform = trans;
            this.display = display;
        }

        @Override
        public String toString()
        {
            return this.display;
        }
    }

    JComboBox<ColorPallete> combobox_ColorTransformPalleteList, comboBox_2;

    ActionListener colorTransformActionListener = new ActionListener()
    {
        public void actionPerformed(ActionEvent e)
        {
            Object src = e.getSource();

            spinner.setEnabled(false);
            combobox_ColorTransformPalleteList.setEnabled(false);

            if (src == radioButton_ColorTransformNone)
            {
                colorTransform = null;
            }
            else if (src == radioButton_ColorTransformMonochrome)
            {
                spinner.setEnabled(true);
                colorTransform = new Transforms.MonochromePixelTransform((Integer) spinner.getValue());
            }
            else if (src == radioButton_ColorTransformColor)
            {
                combobox_ColorTransformPalleteList.setEnabled(true);
                ColorPallete p = (ColorPallete) combobox_ColorTransformPalleteList.getSelectedItem();
                colorTransform = p.transform;
            }
            else if (src == radioButton_ColorTransformCustom)
            {

            }
        }
    };
    ActionListener ditherActionListener = new ActionListener()
    {
        public void actionPerformed(ActionEvent e)
        {
            Object src = e.getSource();

            if (src == radioButton_DitherNone)
            {
                ditherAlgorithm = null;
            }
            else if (src == radioButton_DitherSierra)
            {
                ditherAlgorithm = new Dithers.Sierra3Dithering();
            }
            else if (src == radioButton_DitherFloydSteinberg)
            {
                ditherAlgorithm = new Dithers.FloydSteinbergDithering();
            }
            else if (src == radioButton_DitherBurkes)
            {
                ditherAlgorithm = new Dithers.BurksDithering();
            }
            else if (src == radioButton_Dither2RowSierra)
            {
                ditherAlgorithm = new Dithers.Sierra2Dithering();
            }
            else if (src == radioButton_DitherJarvis)
            {
                ditherAlgorithm = new Dithers.JarvisJudiceNinkeDithering();
            }
            else if (src == radioButton_DitherSierraLite)
            {
                ditherAlgorithm = new Dithers.SierraLiteDithering();
            }
            else if (src == radioButton_DitherStucki)
            {
                ditherAlgorithm = new Dithers.StuckiDithering();
            }
            else if (src == radioButton_DitherAtkinson)
            {
                ditherAlgorithm = new Dithers.AtkinsonDithering();
            }
            else if (src == radioButton_DitherBayer2)
            {
                ditherAlgorithm = new Dithers.Bayer2();
            }
            else if (src == radioButton_DitherBayer3)
            {
                ditherAlgorithm = new Dithers.Bayer3();
            }
            else if (src == radioButton_DitherBayer4)
            {
                ditherAlgorithm = new Dithers.Bayer4();
            }
            else if (src == radioButton_DitherBayer8)
            {
                ditherAlgorithm = new Dithers.Bayer8();
            }
            else if (src == radioButton_DitherRandomNoise)
            {
                ditherAlgorithm = new Dithers.RandomDithering();
            }
        }
    };
    private JButton btnNewButton_3;
    private JButton btnNewButton_4;

    public DitherPanel()
    {
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[] { 128, 0 };
        gridBagLayout.rowHeights = new int[] { 150, 326, 0 };
        gridBagLayout.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
        gridBagLayout.rowWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
        setLayout(gridBagLayout);

        JPanel panel = new JPanel();
        GridBagConstraints gbc_panel = new GridBagConstraints();
        gbc_panel.fill = GridBagConstraints.BOTH;
        gbc_panel.insets = new Insets(0, 0, 5, 0);
        gbc_panel.gridx = 0;
        gbc_panel.gridy = 0;
        add(panel, gbc_panel);
        GridBagLayout gbl_panel = new GridBagLayout();
        gbl_panel.columnWidths = new int[] { 4, 4, 109, 109, 8, 0 };
        gbl_panel.rowHeights = new int[] { 23, 0, 0, 0, 0, 0 };
        gbl_panel.columnWeights = new double[] { 0.0, 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE };
        gbl_panel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
        panel.setLayout(gbl_panel);

        JLabel lblNewLabel = new JLabel("Color Conversion");
        GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
        gbc_lblNewLabel.anchor = GridBagConstraints.WEST;
        gbc_lblNewLabel.gridwidth = 2;
        gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
        gbc_lblNewLabel.gridx = 1;
        gbc_lblNewLabel.gridy = 0;
        panel.add(lblNewLabel, gbc_lblNewLabel);

        ButtonGroup colorConversion = new ButtonGroup();
        radioButton_ColorTransformNone = new JRadioButton("None");
        radioButton_ColorTransformNone.addActionListener(colorTransformActionListener);
        GridBagConstraints gbc_rdbtnNewRadioButton = new GridBagConstraints();
        gbc_rdbtnNewRadioButton.anchor = GridBagConstraints.NORTHWEST;
        gbc_rdbtnNewRadioButton.insets = new Insets(0, 0, 5, 5);
        gbc_rdbtnNewRadioButton.gridx = 2;
        gbc_rdbtnNewRadioButton.gridy = 1;
        colorConversion.add(radioButton_ColorTransformNone);
        panel.add(radioButton_ColorTransformNone, gbc_rdbtnNewRadioButton);

        radioButton_ColorTransformMonochrome = new JRadioButton("MonoChrome");
        radioButton_ColorTransformMonochrome.addActionListener(colorTransformActionListener);
        GridBagConstraints gbc_rdbtnNewRadioButton_3 = new GridBagConstraints();
        gbc_rdbtnNewRadioButton_3.anchor = GridBagConstraints.NORTHWEST;
        gbc_rdbtnNewRadioButton_3.insets = new Insets(0, 0, 5, 5);
        gbc_rdbtnNewRadioButton_3.gridx = 2;
        gbc_rdbtnNewRadioButton_3.gridy = 2;
        colorConversion.add(radioButton_ColorTransformMonochrome);
        panel.add(radioButton_ColorTransformMonochrome, gbc_rdbtnNewRadioButton_3);

        spinner = new JSpinner();
        spinner.setModel(gifFrameNumberModel);
        GridBagConstraints gbc_spinner = new GridBagConstraints();
        gbc_spinner.fill = GridBagConstraints.HORIZONTAL;
        gbc_spinner.insets = new Insets(0, 0, 5, 5);
        gbc_spinner.gridx = 3;
        gbc_spinner.gridy = 2;
        panel.add(spinner, gbc_spinner);

        radioButton_ColorTransformColor = new JRadioButton("Color");
        radioButton_ColorTransformColor.addActionListener(colorTransformActionListener);
        GridBagConstraints gbc_rdbtnNewRadioButton_2 = new GridBagConstraints();
        gbc_rdbtnNewRadioButton_2.anchor = GridBagConstraints.NORTHWEST;
        gbc_rdbtnNewRadioButton_2.insets = new Insets(0, 0, 5, 5);
        gbc_rdbtnNewRadioButton_2.gridx = 2;
        gbc_rdbtnNewRadioButton_2.gridy = 3;
        colorConversion.add(radioButton_ColorTransformColor);
        panel.add(radioButton_ColorTransformColor, gbc_rdbtnNewRadioButton_2);

        combobox_ColorTransformPalleteList = new JComboBox();
        combobox_ColorTransformPalleteList
                .addItem(new ColorPallete(new SimpleIndexedPalettePixelTransform8(), "8 Colors"));
        combobox_ColorTransformPalleteList
                .addItem(new ColorPallete(new SimpleIndexedPalettePixelTransform16(), "16 Colors"));
        combobox_ColorTransformPalleteList
                .addItem(new ColorPallete(new SimpleIndexedPalettePixelTransform256(), "256 Colors"));
        GridBagConstraints gbc_comboBox_1 = new GridBagConstraints();
        gbc_comboBox_1.fill = GridBagConstraints.HORIZONTAL;
        gbc_comboBox_1.anchor = GridBagConstraints.NORTH;
        gbc_comboBox_1.insets = new Insets(0, 0, 5, 5);
        gbc_comboBox_1.gridx = 3;
        gbc_comboBox_1.gridy = 3;
        panel.add(combobox_ColorTransformPalleteList, gbc_comboBox_1);

        radioButton_ColorTransformCustom = new JRadioButton("Custom Color");
        radioButton_ColorTransformCustom.addActionListener(colorTransformActionListener);
        GridBagConstraints gbc_rdbtnNewRadioButton_1 = new GridBagConstraints();
        gbc_rdbtnNewRadioButton_1.anchor = GridBagConstraints.NORTHWEST;
        gbc_rdbtnNewRadioButton_1.insets = new Insets(0, 0, 0, 5);
        gbc_rdbtnNewRadioButton_1.gridx = 2;
        gbc_rdbtnNewRadioButton_1.gridy = 4;
        colorConversion.add(radioButton_ColorTransformCustom);
        panel.add(radioButton_ColorTransformCustom, gbc_rdbtnNewRadioButton_1);

        comboBox_2 = new JComboBox();
        GridBagConstraints gbc_comboBox_2 = new GridBagConstraints();
        gbc_comboBox_2.insets = new Insets(0, 0, 0, 5);
        gbc_comboBox_2.fill = GridBagConstraints.HORIZONTAL;
        gbc_comboBox_2.gridx = 3;
        gbc_comboBox_2.gridy = 4;
        panel.add(comboBox_2, gbc_comboBox_2);

        JPanel panel_1 = new JPanel();
        GridBagConstraints gbc_panel_1 = new GridBagConstraints();
        gbc_panel_1.anchor = GridBagConstraints.NORTH;
        gbc_panel_1.fill = GridBagConstraints.HORIZONTAL;
        gbc_panel_1.gridx = 0;
        gbc_panel_1.gridy = 1;
        add(panel_1, gbc_panel_1);
        GridBagLayout gbl_panel_1 = new GridBagLayout();
        gbl_panel_1.columnWidths = new int[] { 4, 4, 128, 128, 8, 0 };
        gbl_panel_1.rowHeights = new int[] { 23, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        gbl_panel_1.columnWeights = new double[] { 0.0, 0.0, 1.0, 1.0, 0.0, Double.MIN_VALUE };
        gbl_panel_1.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
                0.0, Double.MIN_VALUE };
        panel_1.setLayout(gbl_panel_1);

        ButtonGroup ditherAlgorithms = new ButtonGroup();
        JLabel lblDitheringAlgorithm = new JLabel("Dithering Algorithm");
        GridBagConstraints gbc_lblDitheringAlgorithm = new GridBagConstraints();
        gbc_lblDitheringAlgorithm.anchor = GridBagConstraints.WEST;
        gbc_lblDitheringAlgorithm.gridwidth = 2;
        gbc_lblDitheringAlgorithm.insets = new Insets(0, 0, 5, 5);
        gbc_lblDitheringAlgorithm.gridx = 1;
        gbc_lblDitheringAlgorithm.gridy = 0;

        panel_1.add(lblDitheringAlgorithm, gbc_lblDitheringAlgorithm);

        radioButton_DitherNone = new JRadioButton("None (Nearest Color)");
        GridBagConstraints gbc_rdbtnNonenearestColor = new GridBagConstraints();
        gbc_rdbtnNonenearestColor.fill = GridBagConstraints.HORIZONTAL;
        gbc_rdbtnNonenearestColor.anchor = GridBagConstraints.NORTH;
        gbc_rdbtnNonenearestColor.insets = new Insets(0, 0, 5, 5);
        gbc_rdbtnNonenearestColor.gridx = 2;
        gbc_rdbtnNonenearestColor.gridy = 1;
        ditherAlgorithms.add(radioButton_DitherNone);
        panel_1.add(radioButton_DitherNone, gbc_rdbtnNonenearestColor);

        JLabel lblNewLabel_1 = new JLabel("Error Diffusion");
        GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
        gbc_lblNewLabel_1.anchor = GridBagConstraints.WEST;
        gbc_lblNewLabel_1.insets = new Insets(0, 0, 5, 5);
        gbc_lblNewLabel_1.gridx = 2;
        gbc_lblNewLabel_1.gridy = 2;
        panel_1.add(lblNewLabel_1, gbc_lblNewLabel_1);

        radioButton_DitherFloydSteinberg = new JRadioButton("Floyd-Steinberg");
        GridBagConstraints gbc_rdbtnNewRadioButton_2_1 = new GridBagConstraints();
        gbc_rdbtnNewRadioButton_2_1.fill = GridBagConstraints.HORIZONTAL;
        gbc_rdbtnNewRadioButton_2_1.anchor = GridBagConstraints.NORTH;
        gbc_rdbtnNewRadioButton_2_1.insets = new Insets(0, 0, 5, 5);
        gbc_rdbtnNewRadioButton_2_1.gridx = 2;
        gbc_rdbtnNewRadioButton_2_1.gridy = 3;
        ditherAlgorithms.add(radioButton_DitherFloydSteinberg);
        panel_1.add(radioButton_DitherFloydSteinberg, gbc_rdbtnNewRadioButton_2_1);

        radioButton_DitherSierra = new JRadioButton("Sierra");
        GridBagConstraints gbc_rdbtnNewRadioButton_4 = new GridBagConstraints();
        gbc_rdbtnNewRadioButton_4.fill = GridBagConstraints.HORIZONTAL;
        gbc_rdbtnNewRadioButton_4.insets = new Insets(0, 0, 5, 5);
        gbc_rdbtnNewRadioButton_4.gridx = 3;
        gbc_rdbtnNewRadioButton_4.gridy = 3;
        ditherAlgorithms.add(radioButton_DitherSierra);
        panel_1.add(radioButton_DitherSierra, gbc_rdbtnNewRadioButton_4);

        radioButton_DitherBurkes = new JRadioButton("Burkes");
        GridBagConstraints gbc_rdbtnNewRadioButton_1_1 = new GridBagConstraints();
        gbc_rdbtnNewRadioButton_1_1.fill = GridBagConstraints.HORIZONTAL;
        gbc_rdbtnNewRadioButton_1_1.anchor = GridBagConstraints.NORTH;
        gbc_rdbtnNewRadioButton_1_1.insets = new Insets(0, 0, 5, 5);
        gbc_rdbtnNewRadioButton_1_1.gridx = 2;
        gbc_rdbtnNewRadioButton_1_1.gridy = 4;
        ditherAlgorithms.add(radioButton_DitherBurkes);
        panel_1.add(radioButton_DitherBurkes, gbc_rdbtnNewRadioButton_1_1);

        radioButton_Dither2RowSierra = new JRadioButton("Two Row Sierra");
        GridBagConstraints gbc_rdbtnNewRadioButton_5 = new GridBagConstraints();
        gbc_rdbtnNewRadioButton_5.fill = GridBagConstraints.HORIZONTAL;
        gbc_rdbtnNewRadioButton_5.insets = new Insets(0, 0, 5, 5);
        gbc_rdbtnNewRadioButton_5.gridx = 3;
        gbc_rdbtnNewRadioButton_5.gridy = 4;
        ditherAlgorithms.add(radioButton_Dither2RowSierra);
        panel_1.add(radioButton_Dither2RowSierra, gbc_rdbtnNewRadioButton_5);

        radioButton_DitherJarvis = new JRadioButton("Jarvis, Judice");
        GridBagConstraints gbc_rdbtnNewRadioButton_6 = new GridBagConstraints();
        gbc_rdbtnNewRadioButton_6.fill = GridBagConstraints.HORIZONTAL;
        gbc_rdbtnNewRadioButton_6.insets = new Insets(0, 0, 5, 5);
        gbc_rdbtnNewRadioButton_6.gridx = 2;
        gbc_rdbtnNewRadioButton_6.gridy = 5;
        ditherAlgorithms.add(radioButton_DitherJarvis);
        panel_1.add(radioButton_DitherJarvis, gbc_rdbtnNewRadioButton_6);

        radioButton_DitherSierraLite = new JRadioButton("Sierra Lite");
        GridBagConstraints gbc_rdbtnNewRadioButton_7 = new GridBagConstraints();
        gbc_rdbtnNewRadioButton_7.fill = GridBagConstraints.HORIZONTAL;
        gbc_rdbtnNewRadioButton_7.insets = new Insets(0, 0, 5, 5);
        gbc_rdbtnNewRadioButton_7.gridx = 3;
        gbc_rdbtnNewRadioButton_7.gridy = 5;
        ditherAlgorithms.add(radioButton_DitherSierraLite);
        panel_1.add(radioButton_DitherSierraLite, gbc_rdbtnNewRadioButton_7);

        radioButton_DitherStucki = new JRadioButton("Stucki");
        GridBagConstraints gbc_rdbtnNewRadioButton_8 = new GridBagConstraints();
        gbc_rdbtnNewRadioButton_8.fill = GridBagConstraints.HORIZONTAL;
        gbc_rdbtnNewRadioButton_8.insets = new Insets(0, 0, 5, 5);
        gbc_rdbtnNewRadioButton_8.gridx = 2;
        gbc_rdbtnNewRadioButton_8.gridy = 6;
        ditherAlgorithms.add(radioButton_DitherStucki);
        panel_1.add(radioButton_DitherStucki, gbc_rdbtnNewRadioButton_8);

        radioButton_DitherAtkinson = new JRadioButton("Atkinson");
        GridBagConstraints gbc_rdbtnNewRadioButton_9 = new GridBagConstraints();
        gbc_rdbtnNewRadioButton_9.fill = GridBagConstraints.HORIZONTAL;
        gbc_rdbtnNewRadioButton_9.insets = new Insets(0, 0, 5, 5);
        gbc_rdbtnNewRadioButton_9.gridx = 3;
        gbc_rdbtnNewRadioButton_9.gridy = 6;
        ditherAlgorithms.add(radioButton_DitherAtkinson);
        panel_1.add(radioButton_DitherAtkinson, gbc_rdbtnNewRadioButton_9);

        JLabel lblNewLabel_2 = new JLabel("Ordered");
        GridBagConstraints gbc_lblNewLabel_2 = new GridBagConstraints();
        gbc_lblNewLabel_2.anchor = GridBagConstraints.WEST;
        gbc_lblNewLabel_2.insets = new Insets(0, 0, 5, 5);
        gbc_lblNewLabel_2.gridx = 2;
        gbc_lblNewLabel_2.gridy = 7;

        panel_1.add(lblNewLabel_2, gbc_lblNewLabel_2);

        radioButton_DitherBayer2 = new JRadioButton("Bayer2");
        GridBagConstraints gbc_rdbtnNewRadioButton_10 = new GridBagConstraints();
        gbc_rdbtnNewRadioButton_10.fill = GridBagConstraints.HORIZONTAL;
        gbc_rdbtnNewRadioButton_10.insets = new Insets(0, 0, 5, 5);
        gbc_rdbtnNewRadioButton_10.gridx = 2;
        gbc_rdbtnNewRadioButton_10.gridy = 8;
        ditherAlgorithms.add(radioButton_DitherBayer2);
        panel_1.add(radioButton_DitherBayer2, gbc_rdbtnNewRadioButton_10);

        radioButton_DitherBayer4 = new JRadioButton("Bayer4");
        GridBagConstraints gbc_rdbtnNewRadioButton_11 = new GridBagConstraints();
        gbc_rdbtnNewRadioButton_11.fill = GridBagConstraints.HORIZONTAL;
        gbc_rdbtnNewRadioButton_11.insets = new Insets(0, 0, 5, 5);
        gbc_rdbtnNewRadioButton_11.gridx = 3;
        gbc_rdbtnNewRadioButton_11.gridy = 8;
        ditherAlgorithms.add(radioButton_DitherBayer4);
        panel_1.add(radioButton_DitherBayer4, gbc_rdbtnNewRadioButton_11);

        radioButton_DitherBayer3 = new JRadioButton("Bayer3");
        GridBagConstraints gbc_rdbtnNewRadioButton_12 = new GridBagConstraints();
        gbc_rdbtnNewRadioButton_12.fill = GridBagConstraints.HORIZONTAL;
        gbc_rdbtnNewRadioButton_12.insets = new Insets(0, 0, 5, 5);
        gbc_rdbtnNewRadioButton_12.gridx = 2;
        gbc_rdbtnNewRadioButton_12.gridy = 9;
        ditherAlgorithms.add(radioButton_DitherBayer3);
        panel_1.add(radioButton_DitherBayer3, gbc_rdbtnNewRadioButton_12);

        radioButton_DitherBayer8 = new JRadioButton("Bayer8");
        GridBagConstraints gbc_rdbtnNewRadioButton_13 = new GridBagConstraints();
        gbc_rdbtnNewRadioButton_13.fill = GridBagConstraints.HORIZONTAL;
        gbc_rdbtnNewRadioButton_13.insets = new Insets(0, 0, 5, 5);
        gbc_rdbtnNewRadioButton_13.gridx = 3;
        gbc_rdbtnNewRadioButton_13.gridy = 9;
        ditherAlgorithms.add(radioButton_DitherBayer8);
        panel_1.add(radioButton_DitherBayer8, gbc_rdbtnNewRadioButton_13);

        JLabel lblNewLabel_3 = new JLabel("Other");
        GridBagConstraints gbc_lblNewLabel_3 = new GridBagConstraints();
        gbc_lblNewLabel_3.anchor = GridBagConstraints.WEST;
        gbc_lblNewLabel_3.insets = new Insets(0, 0, 5, 5);
        gbc_lblNewLabel_3.gridx = 2;
        gbc_lblNewLabel_3.gridy = 10;
        panel_1.add(lblNewLabel_3, gbc_lblNewLabel_3);

        radioButton_DitherRandomNoise = new JRadioButton("Random Noise");
        GridBagConstraints gbc_rdbtnNewRadioButton_14 = new GridBagConstraints();
        gbc_rdbtnNewRadioButton_14.fill = GridBagConstraints.HORIZONTAL;
        gbc_rdbtnNewRadioButton_14.insets = new Insets(0, 0, 5, 5);
        gbc_rdbtnNewRadioButton_14.gridx = 2;
        gbc_rdbtnNewRadioButton_14.gridy = 11;
        ditherAlgorithms.add(radioButton_DitherRandomNoise);
        panel_1.add(radioButton_DitherRandomNoise, gbc_rdbtnNewRadioButton_14);

        JButton btnNewButton = new JButton("Refresh");
        GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
        gbc_btnNewButton.fill = GridBagConstraints.HORIZONTAL;
        gbc_btnNewButton.insets = new Insets(0, 0, 5, 5);
        gbc_btnNewButton.gridx = 3;
        gbc_btnNewButton.gridy = 11;
        panel_1.add(btnNewButton, gbc_btnNewButton);

        JButton btnNewButton_1 = new JButton("Apply");
        btnNewButton_1.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                onApplyDither();
            }
        });

        btnNewButton_3 = new JButton("Preview");
        btnNewButton_3.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                onPreviewDither();
            }
        });
        GridBagConstraints gbc_btnNewButton_3 = new GridBagConstraints();
        gbc_btnNewButton_3.fill = GridBagConstraints.HORIZONTAL;
        gbc_btnNewButton_3.insets = new Insets(0, 0, 5, 5);
        gbc_btnNewButton_3.gridx = 2;
        gbc_btnNewButton_3.gridy = 13;
        panel_1.add(btnNewButton_3, gbc_btnNewButton_3);
        
        btnNewButton_4 = new JButton("Cancel");
        btnNewButton_4.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancelDither();
            }
        });
        GridBagConstraints gbc_btnNewButton_4 = new GridBagConstraints();
        gbc_btnNewButton_4.fill = GridBagConstraints.HORIZONTAL;
        gbc_btnNewButton_4.insets = new Insets(0, 0, 5, 5);
        gbc_btnNewButton_4.gridx = 3;
        gbc_btnNewButton_4.gridy = 13;
        panel_1.add(btnNewButton_4, gbc_btnNewButton_4);
        GridBagConstraints gbc_btnNewButton_1 = new GridBagConstraints();
        gbc_btnNewButton_1.fill = GridBagConstraints.HORIZONTAL;
        gbc_btnNewButton_1.insets = new Insets(0, 0, 0, 5);
        gbc_btnNewButton_1.gridx = 2;
        gbc_btnNewButton_1.gridy = 14;
        panel_1.add(btnNewButton_1, gbc_btnNewButton_1);

        JButton btnNewButton_2 = new JButton("Discard");
        btnNewButton_2.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                onDiscardDither();
            }
        });
        GridBagConstraints gbc_btnNewButton_2 = new GridBagConstraints();
        gbc_btnNewButton_2.fill = GridBagConstraints.HORIZONTAL;
        gbc_btnNewButton_2.insets = new Insets(0, 0, 0, 5);
        gbc_btnNewButton_2.gridx = 3;
        gbc_btnNewButton_2.gridy = 14;
        panel_1.add(btnNewButton_2, gbc_btnNewButton_2);
        setupComponents();
    }

    public void showDialog()
    {
        this.setVisible(true);

    }

    protected void setupComponents()
    {
        radioButton_DitherNone.setSelected(true);
        radioButton_ColorTransformNone.setSelected(true);
        spinner.setEnabled(false);
        combobox_ColorTransformPalleteList.setEnabled(false);

        radioButton_DitherNone.addActionListener(ditherActionListener);
        radioButton_DitherFloydSteinberg.addActionListener(ditherActionListener);
        radioButton_DitherSierra.addActionListener(ditherActionListener);
        radioButton_DitherBurkes.addActionListener(ditherActionListener);
        radioButton_Dither2RowSierra.addActionListener(ditherActionListener);
        radioButton_DitherJarvis.addActionListener(ditherActionListener);
        radioButton_DitherSierraLite.addActionListener(ditherActionListener);
        radioButton_DitherStucki.addActionListener(ditherActionListener);
        radioButton_DitherAtkinson.addActionListener(ditherActionListener);
        radioButton_DitherBayer2.addActionListener(ditherActionListener);
        radioButton_DitherBayer4.addActionListener(ditherActionListener);
        radioButton_DitherBayer3.addActionListener(ditherActionListener);
        radioButton_DitherBayer8.addActionListener(ditherActionListener);
        radioButton_DitherRandomNoise.addActionListener(ditherActionListener);
    }

    protected void onPreviewDither()
    {
        if (colorTransform instanceof Transforms.MonochromePixelTransform)
        {
            colorTransform = new Transforms.MonochromePixelTransform((Integer) spinner.getValue());
        }

        else if (colorTransform instanceof Transforms.SimpleIndexedPalettePixelTransform)
        {
            ColorPallete p = (ColorPallete) combobox_ColorTransformPalleteList.getSelectedItem();
            colorTransform = p.transform;
        }

        for (DitherPanelListener ls : this.listeners)
        {
            ls.previewDither(this.colorTransform, this.ditherAlgorithm);
        }
    }

    protected void onApplyDither()
    {
        for (DitherPanelListener ls : this.listeners)
        {
            ls.applyDither();
        }
    }
    
    protected void onDiscardDither()
    {
        for (DitherPanelListener ls : this.listeners)
        {
            ls.discardDither();
        }
    }
    protected void onCancelDither()
    {
        for (DitherPanelListener ls : this.listeners)
        {
            ls.cancelDither();
        }
    }

    /**
     * register the given object to the event listener to receive events from this
     * class
     * 
     * @param lis a class which implements {@link ImageDisplayListener}
     */
    public void addDitherListener(DitherPanelListener lis)
    {
        this.listeners.add(lis);
    }
}
