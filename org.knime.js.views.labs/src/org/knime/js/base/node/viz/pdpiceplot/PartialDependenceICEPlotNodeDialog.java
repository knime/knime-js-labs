/*
 * ------------------------------------------------------------------------
 *
 *  Copyright by KNIME AG, Zurich, Switzerland
 *  Website: http://www.knime.com; Email: contact@knime.com
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License, Version 3, as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 *  Additional permission under GNU GPL version 3 section 7:
 *
 *  KNIME interoperates with ECLIPSE solely via ECLIPSE's plug-in APIs.
 *  Hence, KNIME and ECLIPSE are both independent programs and are not
 *  derived from each other. Should, however, the interpretation of the
 *  GNU GPL Version 3 ("License") under any applicable laws result in
 *  KNIME and ECLIPSE being a combined program, KNIME AG herewith grants
 *  you the additional permission to use and propagate KNIME together with
 *  ECLIPSE with only the license terms in place for ECLIPSE applying to
 *  ECLIPSE and the GNU GPL Version 3 applying for KNIME, provided the
 *  license terms of ECLIPSE themselves allow for the respective use and
 *  propagation of ECLIPSE together with KNIME.
 *
 *  Additional permission relating to nodes for KNIME that extend the Node
 *  Extension (and in particular that are based on subclasses of NodeModel,
 *  NodeDialog, and NodeView) and that only interoperate with KNIME through
 *  standard APIs ("Nodes"):
 *  Nodes are deemed to be separate and independent programs and to not be
 *  covered works.  Notwithstanding anything to the contrary in the
 *  License, the License does not apply to Nodes, you are not required to
 *  license Nodes under the License, and you are granted a license to
 *  prepare and propagate Nodes, in each case even if such Nodes are
 *  propagated with or for interoperation with KNIME.  The owner of a Node
 *  may freely choose the license terms applicable to such Node, including
 *  when such Node is propagated with or for interoperation with KNIME.
 * ---------------------------------------------------------------------
 *
 * History
 *   12.04.2019 (Ben Laney): created
 */
package org.knime.js.base.node.viz.pdpiceplot;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DoubleValue;
import org.knime.core.data.LongValue;
import org.knime.core.data.StringValue;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.defaultnodesettings.DialogComponentColorChooser;
import org.knime.core.node.defaultnodesettings.SettingsModelColor;
import org.knime.core.node.util.ColumnSelectionPanel;
import org.knime.core.node.util.DataValueColumnFilter;
import org.knime.core.node.util.filter.column.DataColumnSpecFilterConfiguration;
import org.knime.core.node.util.filter.column.DataColumnSpecFilterPanel;

/**
 *
 * @author Ben Laney, KNIME GmbH, Konstanz, Germany
 */
public class PartialDependenceICEPlotNodeDialog extends NodeDialogPane {

    private static final int TEXT_FIELD_SIZE = 20;

    private final PartialDependenceICEPlotConfig m_config;

    private final JCheckBox m_generateImageCheckBox;

    private final JSpinner m_maxNumRowsSpinner;

    private DataColumnSpecFilterConfiguration m_colSpecFilter = null;

    private DataTableSpec m_tableSpec;

    private final DataColumnSpecFilterPanel m_colFilterPanel;

    private final ColumnSelectionPanel m_rowIDColComboBox;

    private final ColumnSelectionPanel m_predictionColComboBox;

    private final JCheckBox m_showPDPCheckBox;

    private final DialogComponentColorChooser m_PDPColorChooserComponent;

    private final JSpinner m_PDPLineWeightSpinner;

    private final JCheckBox m_showPDPMarginCheckBox;

    private final JComboBox<String> m_PDPMarginTypeComboBox;

    private final JSpinner m_PDPMarginMultiplierSpinner;

    private final JSpinner m_PDPMarginAlphaValSpinner;

    private final JCheckBox m_showICECheckBox;

    private final DialogComponentColorChooser m_ICEColorChooserComponent;

    private final JSpinner m_ICEWeightSpinner;

    private final JSpinner m_ICEAlphaValSpinner;

    private final JCheckBox m_showDataPointsCheckBox;

    private final DialogComponentColorChooser m_dataPointColorChooserComponent;

    private final JSpinner m_dataPointWeightSpinner;

    private final JSpinner m_dataPointAlphaValSpinner;

    private final JTextField m_xAxisLabelTextField;

    private final JTextField m_yAxisLabelTextField;

    private final JTextField m_chartTitleTextField;

    private final JTextField m_chartSubtitleTextField;

    private final JSpinner m_viewWidthSpinner;

    private final JSpinner m_viewHeightSpinner;

    private final JSpinner m_yAxisMarginSpinner;

    private final JCheckBox m_resizeToFillCheckBox;

    private final JCheckBox m_fullscreenButtonCheckBox;

    private final DialogComponentColorChooser m_backgroundColorChooserComponent;

    private final DialogComponentColorChooser m_dataAreaColorChooserComponent;

    private final JCheckBox m_showGridCheckBox;

    private final DialogComponentColorChooser m_gridColorChooserComponent;

    private final JCheckBox m_showWarningCheckBox;

    private final JCheckBox m_subscribeToSelectionCheckBox;

    private final JCheckBox m_publishSelectionCheckBox;

    private final JCheckBox m_subscribeToFilterCheckBox;

    private final JCheckBox m_showStaticLineCheckBox;

    private final DialogComponentColorChooser m_staticLineColorChooserComponent;

    private final JSpinner m_staticLineWeightSpinner;

    private final JSpinner m_staticLineYValueSpinner;

    private final JCheckBox m_enableSelectionCheckBox;

    private final JCheckBox m_enableInteractiveCtrlsCheckBox;

    private final JCheckBox m_enableMouseCrosshairCheckBox;

    private final JCheckBox m_enablePanningCheckBox;

    private final JCheckBox m_enableScrollZoomCheckBox;

    private final JCheckBox m_enableDragZoomCheckBox;

    private final JCheckBox m_showZoomResetCheckBox;

    private final JCheckBox m_enableTitleControlsCheckBox;

    private final JCheckBox m_enableAxisLabelControlsCheckBox;

    private final JCheckBox m_enablePDPControlsCheckBox;

    private final JCheckBox m_enablePDPMarginControlsCheckBox;

    private final JCheckBox m_enableICEControlsCheckBox;

    private final JCheckBox m_enableStaticLineControlsCheckBox;

    private final JCheckBox m_enableDataPointControlsCheckBox;

    private final JCheckBox m_enableSelectionFilterControlsCheckBox;

    private final JCheckBox m_enableSelectionControlsCheckBox;

    private final JCheckBox m_enableYAxisMarginControlsCheckBox;

    private final JCheckBox m_enableSmartZoomControlsCheckBox;

    private final JCheckBox m_enableGridControlsCheckBox;

    private final JCheckBox m_enableMouseCrosshairControlsCheckBox;

    private final JCheckBox m_enableAdvancedOptionsCheckBox;

    /**
     * Creates a new dialog pane.
     */
    @SuppressWarnings("unchecked")
    public PartialDependenceICEPlotNodeDialog() {

        m_config = new PartialDependenceICEPlotConfig();
        m_generateImageCheckBox = new JCheckBox("Create image at outport");
        m_maxNumRowsSpinner = new JSpinner(
            new SpinnerNumberModel(PartialDependenceICEPlotConfig.DEFAULT_MAX_NUM_ROWS, 1, Integer.MAX_VALUE, 1));
        TitledBorder colSelectionBorder = BorderFactory.createTitledBorder("Choose the feature column (model table)");
        DataValueColumnFilter colSelectionFilter = new DataValueColumnFilter(DoubleValue.class, LongValue.class);
        m_colFilterPanel = new DataColumnSpecFilterPanel();
        m_colFilterPanel.setBorder(BorderFactory.createTitledBorder("Features sampled by pre-processor"));
        colSelectionBorder = BorderFactory.createTitledBorder("Choose the row ID column (model table)");
        colSelectionFilter = new DataValueColumnFilter(StringValue.class);
        m_rowIDColComboBox = new ColumnSelectionPanel(colSelectionBorder, colSelectionFilter, false, false);
        colSelectionBorder = BorderFactory.createTitledBorder("Choose the prediction column (model table)");
        colSelectionFilter = new DataValueColumnFilter(DoubleValue.class, LongValue.class);
        m_predictionColComboBox = new ColumnSelectionPanel(colSelectionBorder, colSelectionFilter, false, false);
        colSelectionBorder = BorderFactory.createTitledBorder("Choose the feature column (original table)");
        colSelectionFilter = new DataValueColumnFilter(DoubleValue.class, LongValue.class);
        m_showPDPCheckBox = new JCheckBox("Show partial dependence plot");
        m_PDPColorChooserComponent =
            new DialogComponentColorChooser(new SettingsModelColor(PartialDependenceICEPlotConfig.CFG_PDP_COLOR,
                PartialDependenceICEPlotConfig.DEFAULT_PDP_COLOR), "Partial dependence plot color", true);
        m_PDPLineWeightSpinner =
            new JSpinner(new SpinnerNumberModel(PartialDependenceICEPlotConfig.DEFAULT_PDP_LINE_WEIGHT, 1, 100, 1));
        m_showPDPMarginCheckBox = new JCheckBox("Show partial dependence margin");
        m_PDPMarginTypeComboBox = new JComboBox<String>();
        m_PDPMarginTypeComboBox.addItem("Standard deviation");
        m_PDPMarginTypeComboBox.addItem("Variance");
        m_PDPMarginMultiplierSpinner =
            new JSpinner(new SpinnerNumberModel(PartialDependenceICEPlotConfig.DEFAULT_PDP_MARGIN_MULTIPLIER, 0,
                Double.MAX_VALUE, 0.1));
        m_PDPMarginAlphaValSpinner = new JSpinner(
            new SpinnerNumberModel(PartialDependenceICEPlotConfig.DEFAULT_PDP_MARGIN_ALPHA_VAL, 0, 1, 0.1));
        m_showICECheckBox = new JCheckBox("Show individual conditional expectation lines");
        m_ICEColorChooserComponent =
            new DialogComponentColorChooser(
                new SettingsModelColor(PartialDependenceICEPlotConfig.CFG_ICE_COLOR,
                    PartialDependenceICEPlotConfig.DEFAULT_ICE_COLOR),
                "Individual conditional expectation color", true);
        m_ICEWeightSpinner =
            new JSpinner(new SpinnerNumberModel(PartialDependenceICEPlotConfig.DEFAULT_ICE_WEIGHT, 1, 100, 1));
        m_ICEAlphaValSpinner =
            new JSpinner(new SpinnerNumberModel(PartialDependenceICEPlotConfig.DEFAULT_ICE_ALPHA_VAL, 0, 1, 0.1));
        m_showDataPointsCheckBox = new JCheckBox("Show original data points");
        m_dataPointColorChooserComponent =
            new DialogComponentColorChooser(new SettingsModelColor(PartialDependenceICEPlotConfig.CFG_DATA_POINT_COLOR,
                PartialDependenceICEPlotConfig.DEFAULT_DATA_POINT_COLOR), "Data point color", true);
        m_dataPointWeightSpinner =
            new JSpinner(new SpinnerNumberModel(PartialDependenceICEPlotConfig.DEFAULT_DATA_POINT_WEIGHT, 1, 100, 1));
        m_dataPointAlphaValSpinner = new JSpinner(
            new SpinnerNumberModel(PartialDependenceICEPlotConfig.DEFAULT_DATA_POINT_ALPHA_VAL, 0, 1, 0.1));
        m_xAxisLabelTextField = new JTextField(TEXT_FIELD_SIZE);
        m_yAxisLabelTextField = new JTextField(TEXT_FIELD_SIZE);
        m_chartTitleTextField = new JTextField(TEXT_FIELD_SIZE);
        m_chartSubtitleTextField = new JTextField(TEXT_FIELD_SIZE);
        m_viewWidthSpinner =
            new JSpinner(new SpinnerNumberModel(PartialDependenceICEPlotConfig.DEFAULT_VIEW_WIDTH, 0, 7680, 1));
        m_viewHeightSpinner =
            new JSpinner(new SpinnerNumberModel(PartialDependenceICEPlotConfig.DEFAULT_VIEW_HEIGHT, 0, 4320, 1));
        m_yAxisMarginSpinner =
            new JSpinner(new SpinnerNumberModel(PartialDependenceICEPlotConfig.DEFAULT_Y_AXIS_MARGIN, 0, 100, 5));
        m_resizeToFillCheckBox = new JCheckBox("Resize to fill screen");
        m_fullscreenButtonCheckBox = new JCheckBox("Enable fullscreen button");
        m_backgroundColorChooserComponent =
            new DialogComponentColorChooser(new SettingsModelColor(PartialDependenceICEPlotConfig.CFG_BACKGROUND_COLOR,
                PartialDependenceICEPlotConfig.DEFAULT_BACKGROUND_COLOR), "Background color: ", true);
        m_dataAreaColorChooserComponent =
            new DialogComponentColorChooser(new SettingsModelColor(PartialDependenceICEPlotConfig.CFG_DATA_AREA_COLOR,
                PartialDependenceICEPlotConfig.DEFAULT_DATA_AREA_COLOR), "Data area color: ", true);
        m_showGridCheckBox = new JCheckBox("Show grid");
        m_gridColorChooserComponent =
            new DialogComponentColorChooser(new SettingsModelColor(PartialDependenceICEPlotConfig.CFG_GRID_COLOR,
                PartialDependenceICEPlotConfig.DEFAULT_GRID_COLOR), "Grid color :", true);
        m_showWarningCheckBox = new JCheckBox("Show warnings in interactive view");
        m_subscribeToSelectionCheckBox = new JCheckBox("Subscribe to selection events");
        m_publishSelectionCheckBox = new JCheckBox("Publish selection events");
        m_subscribeToFilterCheckBox = new JCheckBox("Subscribe to filter events");
        m_showStaticLineCheckBox = new JCheckBox("Display static line");
        m_staticLineColorChooserComponent =
            new DialogComponentColorChooser(new SettingsModelColor(PartialDependenceICEPlotConfig.CFG_STATIC_LINE_COLOR,
                PartialDependenceICEPlotConfig.DEFAULT_STATIC_LINE_COLOR), "Static line color: ", true);
        m_staticLineWeightSpinner =
            new JSpinner(new SpinnerNumberModel(PartialDependenceICEPlotConfig.DEFAULT_STATIC_LINE_WEIGHT, 0, 100, 1));
        m_staticLineYValueSpinner =
            new JSpinner(new SpinnerNumberModel(PartialDependenceICEPlotConfig.DEFAULT_STATIC_LINE_Y_VALUE,
                (-1) * Double.MAX_VALUE, Double.MAX_VALUE, 1));
        m_enableSelectionCheckBox = new JCheckBox("Enable selection in view");
        m_enableInteractiveCtrlsCheckBox = new JCheckBox("Enable controls in view");
        m_enableMouseCrosshairCheckBox = new JCheckBox("Enable mouse crosshairs");
        m_enablePanningCheckBox = new JCheckBox("Enable panning in view");
        m_enableScrollZoomCheckBox = new JCheckBox("Enable scroll-zoom");
        //toggles smart-zoom settings
        m_enableDragZoomCheckBox = new JCheckBox("Enable smart-zoom");
        m_showZoomResetCheckBox = new JCheckBox("Show zoom reset button");
        m_enableTitleControlsCheckBox = new JCheckBox("Enable title controls");
        m_enableAxisLabelControlsCheckBox = new JCheckBox("Enable axis label controls");
        m_enablePDPControlsCheckBox = new JCheckBox("Enable PDP controls");
        m_enablePDPMarginControlsCheckBox = new JCheckBox("Enable PDP margin controls");
        m_enableICEControlsCheckBox = new JCheckBox("Enable ICE controls");
        m_enableStaticLineControlsCheckBox = new JCheckBox("Enable static line controls");
        m_enableDataPointControlsCheckBox = new JCheckBox("Enable data point controls");
        m_enableSelectionFilterControlsCheckBox = new JCheckBox("Enable selection/filter event contorls");
        m_enableSelectionControlsCheckBox = new JCheckBox("Enable selection controls");
        m_enableYAxisMarginControlsCheckBox = new JCheckBox("Enable Y-Axis margin controls");
        m_enableSmartZoomControlsCheckBox = new JCheckBox("Enable smart zoom controls");
        m_enableGridControlsCheckBox = new JCheckBox("Enable grid controls");
        m_enableMouseCrosshairControlsCheckBox = new JCheckBox("Enable mouse-crosshairs controls");
        m_enableAdvancedOptionsCheckBox = new JCheckBox("Enable advanced view options");

        m_showPDPCheckBox.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(final ChangeEvent e) {
                togglePDPControls();
            }
        });

        m_showPDPMarginCheckBox.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(final ChangeEvent e) {
                togglePDPMarginControls();
            }
        });

        m_showICECheckBox.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(final ChangeEvent e) {
                toggleICEControls();
            }
        });

        m_showStaticLineCheckBox.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(final ChangeEvent e) {
                toggleStaticLineControls();
            }
        });

        m_resizeToFillCheckBox.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(final ChangeEvent e) {
                toggleSizeControls();
            }
        });

        m_showGridCheckBox.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(final ChangeEvent e) {
                toggleGridColorControls();
            }
        });

        m_enableInteractiveCtrlsCheckBox.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(final ChangeEvent e) {
                toggleInteractiveControls();
                toggleAdvancedOptionControls();
            }
        });

        m_enablePDPControlsCheckBox.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(final ChangeEvent e) {
                toggleAdvancedOptionControls();
            }
        });

        m_enablePDPMarginControlsCheckBox.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(final ChangeEvent e) {
                toggleAdvancedOptionControls();
            }
        });

        m_enableICEControlsCheckBox.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(final ChangeEvent e) {
                toggleAdvancedOptionControls();
            }
        });

        m_enableDataPointControlsCheckBox.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(final ChangeEvent e) {
                toggleAdvancedOptionControls();
            }
        });

        m_enablePanningCheckBox.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(final ChangeEvent e) {
                toggleZoomControls();
            }
        });

        m_enableSelectionCheckBox.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(final ChangeEvent e) {
                toggleSelectionControls();
            }
        });

        addTab("Options", initOptionsPanel());
        addTab("PDP/ICE", initPDPICEPanel());
        addTab("Plot Options", initGeneralPanel());
        addTab("View Controls", initControlsPanel());
    }

    /**
     * @return the JPanel holding general node options
     */
    private Component initOptionsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridwidth = 1;
        constraints.insets = new Insets(5, 5, 5, 5);
        constraints.anchor = GridBagConstraints.NORTHWEST;
        constraints.gridx = 0;
        constraints.gridy = 0;
        panel.add(m_generateImageCheckBox, constraints);
        JPanel border = new JPanel(new GridBagLayout());
        border.setPreferredSize(new Dimension(150, 50));
        border.setBorder(BorderFactory.createTitledBorder("Maximum number of rows:"));
        GridBagConstraints c2 = new GridBagConstraints();
        c2.anchor = GridBagConstraints.NORTHWEST;
        c2.fill = GridBagConstraints.HORIZONTAL;
        c2.gridx = 0;
        c2.gridy = 0;
        m_maxNumRowsSpinner.setPreferredSize(new Dimension(100, TEXT_FIELD_SIZE));
        border.add(m_maxNumRowsSpinner, c2);
        constraints.gridy ++;
        panel.add(border, constraints);
        constraints.anchor = GridBagConstraints.NORTHWEST;
        constraints.gridx = 0;
        constraints.gridy++;
        panel.add(m_colFilterPanel, constraints);
        constraints.gridx = 0;
        constraints.gridy++;
        Dimension columnSelectionDimension = new Dimension(300, 50);
        m_predictionColComboBox.setPreferredSize(columnSelectionDimension);
        panel.add(m_predictionColComboBox, constraints);
        constraints.anchor = GridBagConstraints.NORTHEAST;
        m_rowIDColComboBox.setPreferredSize(columnSelectionDimension);
        panel.add(m_rowIDColComboBox, constraints);
        constraints.gridx = 0;
        constraints.gridy++;
        return panel;
    }

    /**
     * @return the JPanel holding PDP/ICE options
     */
    private Component initPDPICEPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(5, 5, 5, 5);
        constraints.anchor = GridBagConstraints.NORTHWEST;
        constraints.ipadx = 20;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        JPanel pdpPanel = new JPanel(new GridBagLayout());
        pdpPanel.setBorder(BorderFactory.createTitledBorder("Partial Dependence Plot (PDP)"));
        panel.add(pdpPanel, constraints);
        GridBagConstraints constraints2 = new GridBagConstraints();
        constraints2.insets = new Insets(5, 5, 5, 5);
        constraints2.anchor = GridBagConstraints.CENTER;
        constraints2.gridx = 0;
        constraints2.gridy = 0;
        constraints2.fill = GridBagConstraints.LINE_START;
        pdpPanel.add(m_showPDPCheckBox, constraints2);
        constraints2.anchor = GridBagConstraints.NORTHWEST;
        constraints2.gridy++;
        pdpPanel.add(m_PDPColorChooserComponent.getComponentPanel(), constraints2);
        constraints2.gridy++;
        pdpPanel.add(new JLabel("PDP line weight"), constraints2);
        m_PDPLineWeightSpinner.setPreferredSize(new Dimension(100, TEXT_FIELD_SIZE));
        constraints2.anchor = GridBagConstraints.NORTHEAST;
        pdpPanel.add(m_PDPLineWeightSpinner, constraints2);
        constraints2.gridy++;
        constraints2.anchor = GridBagConstraints.CENTER;
        pdpPanel.add(new JSeparator(SwingConstants.HORIZONTAL), constraints2);
        constraints2.gridy++;
        constraints2.anchor = GridBagConstraints.NORTHWEST;
        pdpPanel.add(m_showPDPMarginCheckBox, constraints2);
        constraints2.gridy++;
        pdpPanel.add(new JLabel("PDP margin value"), constraints2);
        constraints2.anchor = GridBagConstraints.NORTHEAST;
        pdpPanel.add(m_PDPMarginTypeComboBox, constraints2);
        constraints2.anchor = GridBagConstraints.NORTHWEST;
        constraints2.gridy++;
        pdpPanel.add(new JLabel("PDP margin multiplier"), constraints2);
        constraints2.anchor = GridBagConstraints.NORTHEAST;
        m_PDPMarginMultiplierSpinner.setPreferredSize(new Dimension(100, TEXT_FIELD_SIZE));
        pdpPanel.add(m_PDPMarginMultiplierSpinner, constraints2);
        constraints2.anchor = GridBagConstraints.NORTHWEST;
        constraints2.gridy++;
        pdpPanel.add(new JLabel("PDP margin opacity"), constraints2);
        constraints2.anchor = GridBagConstraints.NORTHEAST;
        m_PDPMarginAlphaValSpinner.setPreferredSize(new Dimension(100, TEXT_FIELD_SIZE));
        pdpPanel.add(m_PDPMarginAlphaValSpinner, constraints2);
        JPanel icePanel = new JPanel(new GridBagLayout());
        icePanel.setBorder(BorderFactory.createTitledBorder("Individual Conditional Expectation Plot (ICE)"));
        constraints.gridy++;
        panel.add(icePanel, constraints);
        GridBagConstraints constraints3 = new GridBagConstraints();
        constraints3.insets = new Insets(5, 5, 5, 5);
        constraints3.anchor = GridBagConstraints.CENTER;
        constraints3.gridx = 0;
        constraints3.gridy = 0;
        icePanel.add(m_showICECheckBox, constraints3);
        constraints3.gridy++;
        constraints3.anchor = GridBagConstraints.NORTHWEST;
        icePanel.add(m_ICEColorChooserComponent.getComponentPanel(), constraints3);
        constraints3.gridy++;
        icePanel.add(new JLabel("ICE line weight"), constraints3);
        constraints3.anchor = GridBagConstraints.NORTHEAST;
        m_ICEWeightSpinner.setPreferredSize(new Dimension(100, TEXT_FIELD_SIZE));
        icePanel.add(m_ICEWeightSpinner, constraints3);
        constraints3.gridy++;
        constraints3.anchor = GridBagConstraints.NORTHWEST;
        icePanel.add(new JLabel("ICE line opacity"), constraints3);
        constraints3.anchor = GridBagConstraints.NORTHEAST;
        icePanel.add(m_ICEAlphaValSpinner, constraints3);
        m_ICEAlphaValSpinner.setPreferredSize(new Dimension(100, TEXT_FIELD_SIZE));
        JPanel dataPointsPanel = new JPanel(new GridBagLayout());
        dataPointsPanel.setBorder(BorderFactory.createTitledBorder("Original Data Points"));
        constraints.gridy++;
        panel.add(dataPointsPanel, constraints);
        GridBagConstraints constraints4 = new GridBagConstraints();
        constraints4.insets = new Insets(5, 5, 5, 5);
        constraints4.anchor = GridBagConstraints.CENTER;
        constraints4.gridx = 0;
        constraints4.gridy = 0;
        dataPointsPanel.add(m_showDataPointsCheckBox, constraints4);
        constraints4.gridy++;
        constraints4.anchor = GridBagConstraints.NORTHWEST;
        dataPointsPanel.add(m_dataPointColorChooserComponent.getComponentPanel(), constraints4);
        constraints4.gridy++;
        dataPointsPanel.add(new JLabel("Data point weight"), constraints4);
        constraints4.anchor = GridBagConstraints.NORTHEAST;
        m_dataPointWeightSpinner.setPreferredSize(new Dimension(100, TEXT_FIELD_SIZE));
        dataPointsPanel.add(m_dataPointWeightSpinner, constraints4);
        constraints4.gridy++;
        constraints4.anchor = GridBagConstraints.NORTHWEST;
        dataPointsPanel.add(new JLabel("Data point opacity"), constraints4);
        constraints4.anchor = GridBagConstraints.NORTHEAST;
        m_dataPointAlphaValSpinner.setPreferredSize(new Dimension(100, TEXT_FIELD_SIZE));
        dataPointsPanel.add(m_dataPointAlphaValSpinner, constraints4);
        return panel;
    }

    /**
     * @return the JPanel holding general chart options
     */
    private Component initGeneralPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(5, 5, 5, 5);
        constraints.anchor = GridBagConstraints.NORTHWEST;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;

        constraints.gridy++;

        JPanel plotLabelOptionsJPanel = new JPanel(new GridBagLayout());
        plotLabelOptionsJPanel.setBorder(BorderFactory.createTitledBorder("Chart Labels"));
        panel.add(plotLabelOptionsJPanel, constraints);
        GridBagConstraints constraints3 = new GridBagConstraints();
        constraints3.insets = new Insets(5, 5, 5, 5);
        constraints3.anchor = GridBagConstraints.NORTHWEST;
        constraints3.gridx = 0;
        constraints3.gridy = 0;
        plotLabelOptionsJPanel.add(new JLabel("Chart Title: "), constraints3);
        constraints3.gridx++;
        plotLabelOptionsJPanel.add(m_chartTitleTextField, constraints3);
        constraints3.gridx = 0;
        constraints3.gridy++;
        plotLabelOptionsJPanel.add(new JLabel("Chart Subtitle: "), constraints3);
        constraints3.gridx++;
        plotLabelOptionsJPanel.add(m_chartSubtitleTextField, constraints3);

        constraints.gridy++;

        JPanel axisLabelPanel = new JPanel(new GridBagLayout());
        axisLabelPanel.setBorder(BorderFactory.createTitledBorder("Axis Labels"));
        panel.add(axisLabelPanel, constraints);
        GridBagConstraints constraints2 = new GridBagConstraints();
        constraints2.insets = new Insets(5, 5, 5, 5);
        constraints2.anchor = GridBagConstraints.NORTHWEST;
        constraints2.gridx = 0;
        constraints2.gridy = 0;
        axisLabelPanel.add(new JLabel("X-Axis Label: "), constraints2);
        constraints2.gridx++;
        axisLabelPanel.add(m_xAxisLabelTextField, constraints2);
        constraints2.gridx = 0;
        constraints2.gridy++;
        axisLabelPanel.add(new JLabel("Y-Axis Label: "), constraints2);
        constraints2.gridx++;
        axisLabelPanel.add(m_yAxisLabelTextField, constraints2);

        constraints.gridy++;

        JPanel sizeOptionsPanel = new JPanel(new GridBagLayout());
        sizeOptionsPanel.setBorder(BorderFactory.createTitledBorder("Sizes"));
        panel.add(sizeOptionsPanel, constraints);
        GridBagConstraints constraints5 = new GridBagConstraints();
        constraints5.anchor = GridBagConstraints.CENTER;
        constraints5.insets = new Insets(5, 5, 5, 5);
        constraints5.gridx = 0;
        constraints5.gridy = 0;
        constraints5.anchor = GridBagConstraints.NORTHWEST;
        sizeOptionsPanel.add(new JLabel("Y-Axis +/- (%): "), constraints5);
        constraints5.anchor = GridBagConstraints.NORTHEAST;
        constraints5.gridx++;
        m_yAxisMarginSpinner.setPreferredSize(new Dimension(100, TEXT_FIELD_SIZE));
        sizeOptionsPanel.add(m_yAxisMarginSpinner, constraints5);
        constraints5.anchor = GridBagConstraints.CENTER;
        constraints5.gridx = 0;
        constraints5.gridy++;
        sizeOptionsPanel.add(m_resizeToFillCheckBox, constraints5);
        constraints5.anchor = GridBagConstraints.NORTHWEST;
        constraints5.gridy++;
        sizeOptionsPanel.add(new JLabel("Width (px): "), constraints5);
        constraints5.anchor = GridBagConstraints.NORTHEAST;
        constraints5.gridx++;
        m_viewWidthSpinner.setPreferredSize(new Dimension(100, TEXT_FIELD_SIZE));
        sizeOptionsPanel.add(m_viewWidthSpinner, constraints5);
        constraints5.anchor = GridBagConstraints.NORTHWEST;
        constraints5.gridx = 0;
        constraints5.gridy++;
        sizeOptionsPanel.add(new JLabel("Height (px): "), constraints5);
        constraints5.anchor = GridBagConstraints.NORTHEAST;
        constraints5.gridx++;
        m_viewHeightSpinner.setPreferredSize(new Dimension(100, TEXT_FIELD_SIZE));
        sizeOptionsPanel.add(m_viewHeightSpinner, constraints5);

        constraints.gridy++;

        JPanel backgroundOptionsPanel = new JPanel(new GridBagLayout());
        backgroundOptionsPanel.setBorder(BorderFactory.createTitledBorder("Background"));
        panel.add(backgroundOptionsPanel, constraints);
        GridBagConstraints constraints6 = new GridBagConstraints();
        constraints6.anchor = GridBagConstraints.NORTHWEST;
        constraints6.gridx = 0;
        constraints6.gridy = 0;
        backgroundOptionsPanel.add(m_backgroundColorChooserComponent.getComponentPanel(), constraints6);
        constraints6.gridy++;
        backgroundOptionsPanel.add(m_dataAreaColorChooserComponent.getComponentPanel(), constraints6);
        constraints6.gridy++;
        backgroundOptionsPanel.add(m_showGridCheckBox, constraints6);
        constraints6.gridy++;
        backgroundOptionsPanel.add(m_gridColorChooserComponent.getComponentPanel(), constraints6);

        constraints.gridx = 0;
        constraints.gridy++;
        panel.add(m_showWarningCheckBox, constraints);

        return panel;
    }

    /**
     * @return the JPanel holding interactive control options
     */
    private Component initControlsPanel() {

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(5, 5, 5, 5);
        constraints.anchor = GridBagConstraints.NORTHWEST;
        constraints.ipadx = 20;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.fill = GridBagConstraints.HORIZONTAL;

        JPanel viewControlsPanel = new JPanel(new GridBagLayout());
        viewControlsPanel.setBorder(BorderFactory.createTitledBorder("View Controls"));
        panel.add(viewControlsPanel, constraints);
        GridBagConstraints constraints2 = new GridBagConstraints();
        constraints2.anchor = GridBagConstraints.NORTHWEST;
        constraints2.insets = new Insets(5, 5, 5, 5);
        constraints2.gridx = 0;
        constraints2.gridy = 0;
        viewControlsPanel.add(m_enableInteractiveCtrlsCheckBox, constraints2);
        constraints2.gridy++;
        viewControlsPanel.add(m_enableSmartZoomControlsCheckBox, constraints2);
        constraints2.gridx++;
        viewControlsPanel.add(m_fullscreenButtonCheckBox, constraints2);
        constraints2.gridx = 0;
        constraints2.gridy++;
        viewControlsPanel.add(m_enableTitleControlsCheckBox, constraints2);
        constraints2.gridx++;
        viewControlsPanel.add(m_enableAxisLabelControlsCheckBox, constraints2);
        constraints2.gridx = 0;
        constraints2.gridy++;
        viewControlsPanel.add(m_enablePDPControlsCheckBox, constraints2);
        constraints2.gridx++;
        viewControlsPanel.add(m_enablePDPMarginControlsCheckBox, constraints2);
        constraints2.gridx = 0;
        constraints2.gridy++;
        viewControlsPanel.add(m_enableICEControlsCheckBox, constraints2);
        constraints2.gridx++;
        viewControlsPanel.add(m_enableDataPointControlsCheckBox, constraints2);
        constraints2.gridx = 0;
        constraints2.gridy++;
        viewControlsPanel.add(m_enableStaticLineControlsCheckBox, constraints2);
        constraints2.gridx++;
        viewControlsPanel.add(m_enableYAxisMarginControlsCheckBox, constraints2);
        constraints2.gridx = 0;
        constraints2.gridy++;
        viewControlsPanel.add(m_enableSelectionControlsCheckBox, constraints2);
        constraints2.gridx++;
        viewControlsPanel.add(m_enableSelectionFilterControlsCheckBox, constraints2);
        constraints2.gridx = 0;
        constraints2.gridy++;
        viewControlsPanel.add(m_enableGridControlsCheckBox, constraints2);
        constraints2.gridx++;
        viewControlsPanel.add(m_enableAdvancedOptionsCheckBox, constraints2);

        constraints.gridy++;

        JPanel zoomControlsPanel = new JPanel(new GridBagLayout());
        zoomControlsPanel.setBorder(BorderFactory.createTitledBorder("Zoom Controls"));
        panel.add(zoomControlsPanel, constraints);
        GridBagConstraints constraints3 = new GridBagConstraints();
        constraints3.gridx = 0;
        constraints3.gridy = 0;
        constraints3.anchor = GridBagConstraints.NORTHWEST;
        zoomControlsPanel.add(m_enablePanningCheckBox, constraints3);
        constraints3.anchor = GridBagConstraints.NORTHWEST;
        constraints3.gridx++;
        zoomControlsPanel.add(m_showZoomResetCheckBox, constraints3);
        constraints3.gridx = 0;
        constraints3.gridy++;
        zoomControlsPanel.add(m_enableScrollZoomCheckBox, constraints3);
        constraints3.gridx++;
        zoomControlsPanel.add(m_enableDragZoomCheckBox, constraints3);

        constraints.gridy++;

        JPanel interactivePanel = new JPanel(new GridBagLayout());
        interactivePanel.setBorder(BorderFactory.createTitledBorder("Interactive Events"));
        panel.add(interactivePanel, constraints);
        GridBagConstraints constraints5 = new GridBagConstraints();
        constraints5.anchor = GridBagConstraints.NORTHWEST;
        constraints5.insets = new Insets(5, 5, 5, 5);
        constraints5.gridx = 0;
        constraints5.gridy = 0;
        interactivePanel.add(m_enableSelectionCheckBox, constraints5);
        constraints5.gridx++;
        interactivePanel.add(m_subscribeToFilterCheckBox, constraints5);
        constraints5.gridx = 0;
        constraints5.gridy++;
        interactivePanel.add(m_subscribeToSelectionCheckBox, constraints5);
        constraints5.gridx++;
        interactivePanel.add(m_publishSelectionCheckBox, constraints5);

        constraints.gridy++;

        JPanel staticLinePanel = new JPanel(new GridBagLayout());
        staticLinePanel.setBorder(BorderFactory.createTitledBorder("Static Line"));
        panel.add(staticLinePanel, constraints);
        GridBagConstraints constraints4 = new GridBagConstraints();
        constraints4.anchor = GridBagConstraints.CENTER;
        constraints4.insets = new Insets(5, 5, 5, 5);
        constraints4.gridx = 0;
        constraints4.gridy = 0;
        staticLinePanel.add(m_showStaticLineCheckBox, constraints4);
        constraints4.anchor = GridBagConstraints.NORTHWEST;
        constraints4.gridy++;
        staticLinePanel.add(m_staticLineColorChooserComponent.getComponentPanel(), constraints4);
        constraints4.gridy++;
        staticLinePanel.add(new JLabel("Static line weight"), constraints4);
        constraints4.anchor = GridBagConstraints.NORTHEAST;
        m_staticLineWeightSpinner.setPreferredSize(new Dimension(100, TEXT_FIELD_SIZE));
        staticLinePanel.add(m_staticLineWeightSpinner, constraints4);
        constraints4.anchor = GridBagConstraints.NORTHWEST;
        constraints4.gridy++;
        staticLinePanel.add(new JLabel("Static line y-value"), constraints4);
        constraints4.anchor = GridBagConstraints.NORTHEAST;
        m_staticLineYValueSpinner.setPreferredSize(new Dimension(100, TEXT_FIELD_SIZE));
        staticLinePanel.add(m_staticLineYValueSpinner, constraints4);

        return panel;
    }

    /**
     * toggle enabled PDP swing components
     */
    private void togglePDPControls() {
        boolean isEnabled = m_showPDPCheckBox.isSelected();
        m_PDPColorChooserComponent.getModel().setEnabled(isEnabled);
        m_PDPLineWeightSpinner.setEnabled(isEnabled);
        togglePDPMarginControls();
        m_showPDPMarginCheckBox.setEnabled(isEnabled);
    }

    /**
     * toggle enabled PDP margin swing components
     */
    private void togglePDPMarginControls() {
        boolean isEnabled1 = m_showPDPMarginCheckBox.isSelected();
        boolean isEnabled2 = m_showPDPCheckBox.isSelected();
        if (!isEnabled2) {
            isEnabled1 = false;
        }
        m_PDPMarginTypeComboBox.setEnabled(isEnabled1);
        m_PDPMarginMultiplierSpinner.setEnabled(isEnabled1);
        m_PDPMarginAlphaValSpinner.setEnabled(isEnabled1);
    }

    /**
     * toggle enabled ICE swing components
     */
    private void toggleICEControls() {
        boolean isEnabled = m_showICECheckBox.isSelected();
        m_ICEColorChooserComponent.getModel().setEnabled(isEnabled);
        m_ICEWeightSpinner.setEnabled(isEnabled);
        m_ICEAlphaValSpinner.setEnabled(isEnabled);
    }

    /**
     * toggle enabled Static Line swing components
     */
    private void toggleStaticLineControls() {
        boolean isEnabled = m_showStaticLineCheckBox.isSelected();
        m_staticLineColorChooserComponent.getModel().setEnabled(isEnabled);
        m_staticLineWeightSpinner.setEnabled(isEnabled);
        m_staticLineYValueSpinner.setEnabled(isEnabled);
    }

    /**
     * toggle enabled Grid Color swing components
     */
    private void toggleGridColorControls() {
        boolean isEnabled = m_showGridCheckBox.isSelected();
        m_gridColorChooserComponent.getModel().setEnabled(isEnabled);
    }

    /**
     * toggle enabled View Size components
     */
    private void toggleSizeControls() {
        boolean isEnabled = m_resizeToFillCheckBox.isSelected();
        m_viewWidthSpinner.setEnabled(!isEnabled);
        m_viewHeightSpinner.setEnabled(!isEnabled);
    }

    /**
     * toggle enabled Interactive Control components
     */
    private void toggleInteractiveControls() {
        boolean isEnabled = m_enableInteractiveCtrlsCheckBox.isSelected();
        m_fullscreenButtonCheckBox.setEnabled(isEnabled);
        m_enableMouseCrosshairCheckBox.setEnabled(isEnabled);
        m_enableTitleControlsCheckBox.setEnabled(isEnabled);
        m_enableAxisLabelControlsCheckBox.setEnabled(isEnabled);
        m_enablePDPControlsCheckBox.setEnabled(isEnabled);
        m_enablePDPMarginControlsCheckBox.setEnabled(isEnabled);
        m_enableICEControlsCheckBox.setEnabled(isEnabled);
        m_enableStaticLineControlsCheckBox.setEnabled(isEnabled);
        m_enableDataPointControlsCheckBox.setEnabled(isEnabled);
        m_enableSelectionFilterControlsCheckBox.setEnabled(isEnabled);
        m_enableSelectionControlsCheckBox.setEnabled(isEnabled);
        m_enableYAxisMarginControlsCheckBox.setEnabled(isEnabled);
        m_enableSmartZoomControlsCheckBox.setEnabled(isEnabled);
        m_enableGridControlsCheckBox.setEnabled(isEnabled);
        m_enableMouseCrosshairControlsCheckBox.setEnabled(isEnabled);
        toggleAdvancedOptionControls();
    }

    /**
     * toggle enabled Zoom swing components
     */
    private void toggleZoomControls() {
        boolean isEnabled = m_enablePanningCheckBox.isSelected();
        m_showZoomResetCheckBox.setEnabled(isEnabled);
        m_enableScrollZoomCheckBox.setEnabled(isEnabled);
        m_enableDragZoomCheckBox.setEnabled(isEnabled);
        m_enableSmartZoomControlsCheckBox.setEnabled(isEnabled);
    }

    /**
     * toggle enabled Selection swing components
     */
    private void toggleSelectionControls() {
        boolean isEnabled = m_enableSelectionCheckBox.isSelected();
        m_enableSelectionControlsCheckBox.setEnabled(isEnabled);
    }

    /**
     * toggle enabled Selection swing components
     */
    private void toggleAdvancedOptionControls() {
        boolean pdpEnabled = m_enablePDPControlsCheckBox.isSelected();
        boolean iceEnabled = m_enableICEControlsCheckBox.isSelected();
        boolean pdpMarginEnabled = m_enablePDPMarginControlsCheckBox.isSelected();
        boolean dataPointsEnabled = m_enableDataPointControlsCheckBox.isSelected();
        boolean interactiveControlsEnabled = m_enableInteractiveCtrlsCheckBox.isSelected();
        boolean enableAdvancedOptions = true;
        if(!interactiveControlsEnabled) {
            enableAdvancedOptions = false;
        }else {
            if(!pdpEnabled && !iceEnabled && !dataPointsEnabled && !pdpMarginEnabled) {
                enableAdvancedOptions = false;
            }
        }
        m_enableAdvancedOptionsCheckBox.setEnabled(enableAdvancedOptions);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadSettingsFrom(final NodeSettingsRO settings, final DataTableSpec[] specs)
        throws NotConfigurableException {

        try {
            m_config.loadSettingsForDialog(settings, specs[0]);
        } catch (InvalidSettingsException e) {
            throw new NotConfigurableException(
                "There was a problem configuring the PDP/ICE dialog. Please ensure that your input tables"
                + " are formatted correctly.");
        }

        m_generateImageCheckBox.setSelected(m_config.getGenerateImage());
        m_maxNumRowsSpinner.setValue(m_config.getMaxNumRows());
        m_colSpecFilter = m_config.getSampledFeatureColumns();
        m_tableSpec = specs[0];
        m_colFilterPanel.loadConfiguration(m_colSpecFilter, m_tableSpec);
        // TODO: update col filter panel
        m_rowIDColComboBox.update(specs[0], m_config.getRowIDCol(), false);
        m_predictionColComboBox.update(specs[0], m_config.getPredictionCol(), false);
        m_showPDPCheckBox.setSelected(m_config.getShowPDP());
        m_PDPColorChooserComponent.setColor(m_config.getPDPColor());
        m_PDPLineWeightSpinner.setValue(m_config.getPDPLineWeight());
        m_showPDPMarginCheckBox.setSelected(m_config.getShowPDPMargin());
        m_PDPMarginTypeComboBox.setSelectedItem(m_config.getPDPMarginType());
        m_PDPMarginMultiplierSpinner.setValue(m_config.getPDPMarginMultiplier());
        m_PDPMarginAlphaValSpinner.setValue(m_config.getPDPMarginAlphaVal());
        m_showICECheckBox.setSelected(m_config.getShowICE());
        m_ICEColorChooserComponent.setColor(m_config.getICEColor());
        m_ICEWeightSpinner.setValue(m_config.getICEWeight());
        m_ICEAlphaValSpinner.setValue(m_config.getICEAlphaVal());
        m_showDataPointsCheckBox.setSelected(m_config.getShowDataPoints());
        m_dataPointColorChooserComponent.setColor(m_config.getDataPointColor());
        m_dataPointWeightSpinner.setValue(m_config.getDataPointWeight());
        m_dataPointAlphaValSpinner.setValue(m_config.getDataPointAlphaVal());
        m_xAxisLabelTextField.setText(m_config.getXAxisLabel());
        m_yAxisLabelTextField.setText(m_config.getYAxisLabel());
        m_chartTitleTextField.setText(m_config.getChartTitle());
        m_chartSubtitleTextField.setText(m_config.getChartSubtitle());
        m_viewWidthSpinner.setValue(m_config.getViewWidth());
        m_viewHeightSpinner.setValue(m_config.getViewHeight());
        m_yAxisMarginSpinner.setValue(m_config.getYAxisMargin() * 100);
        m_resizeToFillCheckBox.setSelected(m_config.getResizeToFill());
        m_fullscreenButtonCheckBox.setSelected(m_config.getFullscreenButton());
        m_backgroundColorChooserComponent.setColor(m_config.getBackgroundColor());
        m_dataAreaColorChooserComponent.setColor(m_config.getDataAreaColor());
        m_showGridCheckBox.setSelected(m_config.getShowGrid());
        m_gridColorChooserComponent.setColor(m_config.getGridColor());
        m_showWarningCheckBox.setSelected(m_config.getShowWarnings());
        m_subscribeToSelectionCheckBox.setSelected(m_config.getSubscribeToSelection());
        m_publishSelectionCheckBox.setSelected(m_config.getPublishSelection());
        m_subscribeToFilterCheckBox.setSelected(m_config.getSubscribeToFilters());
        m_showStaticLineCheckBox.setSelected(m_config.getShowStaticLine());
        m_staticLineColorChooserComponent.setColor(m_config.getStaticLineColor());
        m_staticLineWeightSpinner.setValue(m_config.getStaticLineWeight());
        m_staticLineYValueSpinner.setValue(m_config.getStaticLineYValue());
        m_enableSelectionCheckBox.setSelected(m_config.getEnableSelection());
        m_enableInteractiveCtrlsCheckBox.setSelected(m_config.getEnableInteractiveCtrls());
        m_enableMouseCrosshairCheckBox.setSelected(m_config.getEnableMouseCrosshair());
        m_enablePanningCheckBox.setSelected(m_config.getEnablePanning());
        m_enableScrollZoomCheckBox.setSelected(m_config.getEnableScrollZoom());
        m_enableDragZoomCheckBox.setSelected(m_config.getEnableDragZoom());
        m_showZoomResetCheckBox.setSelected(m_config.getShowZoomReset());
        m_enableTitleControlsCheckBox.setSelected(m_config.getEnableTitleControls());
        m_enableAxisLabelControlsCheckBox.setSelected(m_config.getEnableAxisLabelControls());
        m_enablePDPControlsCheckBox.setSelected(m_config.getEnablePDPControls());
        m_enablePDPMarginControlsCheckBox.setSelected(m_config.getEnablePDPMarginControls());
        m_enableICEControlsCheckBox.setSelected(m_config.getEnableICEControls());
        m_enableStaticLineControlsCheckBox.setSelected(m_config.getEnableStaticLineControls());
        m_enableDataPointControlsCheckBox.setSelected(m_config.getEnableDataPointControls());
        m_enableSelectionFilterControlsCheckBox.setSelected(m_config.getEnableSelectionFilterControls());
        m_enableSelectionControlsCheckBox.setSelected(m_config.getEnableSelectionControls());
        m_enableYAxisMarginControlsCheckBox.setSelected(m_config.getEnableYAxisMarginControls());
        m_enableSmartZoomControlsCheckBox.setSelected(m_config.getEnableSmartZoomControls());
        m_enableGridControlsCheckBox.setSelected(m_config.getEnableGridControls());
        m_enableMouseCrosshairControlsCheckBox.setSelected(m_config.getEnableMouseCrosshairControls());
        m_enableAdvancedOptionsCheckBox.setSelected(m_config.getEnableAdvancedOptionsControls());

        togglePDPControls();
        togglePDPMarginControls();
        toggleICEControls();
        toggleStaticLineControls();
        toggleGridColorControls();
        toggleSizeControls();
        toggleZoomControls();
        toggleInteractiveControls();
        toggleSelectionControls();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) throws InvalidSettingsException {
        m_config.setGenerateImage(m_generateImageCheckBox.isSelected());
        m_config.setMaxNumRows((int)m_maxNumRowsSpinner.getValue());
        m_colFilterPanel.saveConfiguration(m_config.getSampledFeatureColumns());
        final String[] includedColumns = m_config.getSampledFeatureColumns().applyTo(m_tableSpec).getIncludes();
        if(includedColumns == null || includedColumns.length < 1) {
            throw new InvalidSettingsException("You must select at least 1 of the columns that were sampled in the "
                + "preprocessing node.");
        }
        m_config.setRowIDCol(m_rowIDColComboBox.getSelectedColumn());
        m_config.setPredictionCol(m_predictionColComboBox.getSelectedColumn());
        m_config.setShowPDP(m_showPDPCheckBox.isSelected());
        m_config.setPDPColor(m_PDPColorChooserComponent.getColor());
        m_config.setPDPLineWeight((double)m_PDPLineWeightSpinner.getValue());
        m_config.setShowPDPMargin(m_showPDPMarginCheckBox.isSelected());
        m_config.setPDPMarginType((String)m_PDPMarginTypeComboBox.getSelectedItem());
        m_config.setPDPMarginMultiplier((double)m_PDPMarginMultiplierSpinner.getValue());
        m_config.setPDPMarginAlphaVal((double)m_PDPMarginAlphaValSpinner.getValue());
        m_config.setShowICE(m_showICECheckBox.isSelected());
        m_config.setICEColor(m_ICEColorChooserComponent.getColor());
        m_config.setICEWeight((double)m_ICEWeightSpinner.getValue());
        m_config.setICEAlphaVal((double)m_ICEAlphaValSpinner.getValue());
        m_config.setShowDataPoints(m_showDataPointsCheckBox.isSelected());
        m_config.setDataPointColor(m_dataPointColorChooserComponent.getColor());
        m_config.setDataPointWeight((double)m_dataPointWeightSpinner.getValue());
        m_config.setDataPointAlphaVal((double)m_dataPointAlphaValSpinner.getValue());
        m_config.setXAxisLabel(m_xAxisLabelTextField.getText() == null ? "" : m_xAxisLabelTextField.getText());
        m_config.setYAxisLabel(m_yAxisLabelTextField.getText() == null ? "" : m_yAxisLabelTextField.getText());
        m_config.setChartTitle(m_chartTitleTextField.getText() == null ? "" : m_chartTitleTextField.getText());
        m_config.setChartSubtitle(m_chartSubtitleTextField.getText() == null ? "" : m_chartSubtitleTextField.getText());
        m_config.setViewWidth((int)m_viewWidthSpinner.getValue());
        m_config.setViewHeight((int)m_viewHeightSpinner.getValue());
        m_config.setYAxisMargin((double)m_yAxisMarginSpinner.getValue() * .01);
        m_config.setResizeToFill(m_resizeToFillCheckBox.isSelected());
        m_config.setFullscreenButton(m_fullscreenButtonCheckBox.isSelected());
        m_config.setBackgroundColor(m_backgroundColorChooserComponent.getColor());
        m_config.setDataAreaColor(m_dataAreaColorChooserComponent.getColor());
        m_config.setShowGrid(m_showGridCheckBox.isSelected());
        m_config.setGridColor(m_gridColorChooserComponent.getColor());
        m_config.setShowWarnings(m_showWarningCheckBox.isSelected());
        m_config.setSubscribeToSelection(m_subscribeToSelectionCheckBox.isSelected());
        m_config.setPublishSelection(m_publishSelectionCheckBox.isSelected());
        m_config.setSubscribeToFilters(m_subscribeToFilterCheckBox.isSelected());
        m_config.setShowStaticLine(m_showStaticLineCheckBox.isSelected());
        m_config.setStaticLineColor(m_staticLineColorChooserComponent.getColor());
        m_config.setStaticLineWeight((double)m_staticLineWeightSpinner.getValue());
        m_config.setStaticLineYValue((double)m_staticLineYValueSpinner.getValue());
        m_config.setEnableSelection(m_enableSelectionCheckBox.isSelected());
        m_config.setEnableInteractiveCtrls(m_enableInteractiveCtrlsCheckBox.isSelected());
        m_config.setEnableMouseCrosshair(m_enableMouseCrosshairCheckBox.isSelected());
        m_config.setEnablePanning(m_enablePanningCheckBox.isSelected());
        m_config.setEnableScrollZoom(m_enableScrollZoomCheckBox.isSelected());
        m_config.setEnableDragZoom(m_enableDragZoomCheckBox.isSelected());
        m_config.setShowZoomReset(m_showZoomResetCheckBox.isSelected());
        m_config.setEnableTitleControls(m_enableTitleControlsCheckBox.isSelected());
        m_config.setEnableAxisLabelControls(m_enableAxisLabelControlsCheckBox.isSelected());
        m_config.setEnablePDPControls(m_enablePDPControlsCheckBox.isSelected());
        m_config.setEnablePDPMarginControls(m_enablePDPMarginControlsCheckBox.isSelected());
        m_config.setEnableICEControls(m_enableICEControlsCheckBox.isSelected());
        m_config.setEnableStaticLineControls(m_enableStaticLineControlsCheckBox.isSelected());
        m_config.setEnableDataPointControls(m_enableDataPointControlsCheckBox.isSelected());
        m_config.setEnableSelectionFilterControls(m_enableSelectionFilterControlsCheckBox.isSelected());
        m_config.setEnableSelectionControls(m_enableSelectionControlsCheckBox.isSelected());
        m_config.setEnableYAxisMarginControls(m_enableYAxisMarginControlsCheckBox.isSelected());
        m_config.setEnableSmartZoomControls(m_enableSmartZoomControlsCheckBox.isSelected());
        m_config.setEnableGridControls(m_enableGridControlsCheckBox.isSelected());
        m_config.setEnableMouseCrosshairControls(m_enableMouseCrosshairControlsCheckBox.isSelected());
        m_config.setEnableAdvancedOptionsControls(m_enableAdvancedOptionsCheckBox.isSelected());
        m_config.saveSettings(settings);
    }
}