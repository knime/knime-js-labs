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
 *   Jul 23, 2018 (awalter): created
 */
package org.knime.base.node.mine.cluster.hierarchical.js;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.knime.core.data.DataTableSpec;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.port.PortObjectSpec;

/**
 *
 * @author Alison Walter
 */
public class HierarchicalClusterAssignerDialog extends NodeDialogPane {

    private static final int TEXT_FIELD_SIZE = 20;

    private final JCheckBox m_showWarningsInViewCheckBox;
    private final JCheckBox m_generateImageCheckBox;
    private final JSpinner m_imageWidthSpinner;
    private final JSpinner m_imageHeightSpinner;

    private final JCheckBox m_resizeToWindowCheckBox;
    private final JTextField m_titleTextField;
    private final JTextField m_subtitleTextField;
    private final JCheckBox m_enableClusterLabelsCheckBox;
    private final JCheckBox m_useLogScaleCheckBox;
    private final JComboBox<HierarchicalClusterAssignerOrientation> m_orientationComboBox;

    private final JCheckBox m_enableViewEditCheckBox;
    private final JCheckBox m_enableTitleEditCheckBox;
    private final JCheckBox m_displayFullscreenButtonCheckBox;
    private final JCheckBox m_enableNumClusterEditCheckBox;
    private final JCheckBox m_enableThresholdValueCheckBox;
    private final JCheckBox m_enableLogScaleToggleCheckBox;
    private final JCheckBox m_enableChangeOrientationCheckBox;

    private final JCheckBox m_enableSelectionCheckBox;
    private final JCheckBox m_publishSelectionEventsCheckBox;
    private final JCheckBox m_subscribeSelectionEventsCheckBox;
    private final JTextField m_selectionColumnNameTextField;

    private final JSpinner m_numClustersSpinner;
    private final JSpinner m_distanceThresholdSpinner;
    private final JRadioButton m_clusterCountModeRadioButton;
    private final JRadioButton m_distanceThresholdModeRadioButton;
    private final JTextField m_clusterColumnNameTextField;
    private final JCheckBox m_useNormalizedDistancesCheckBox;

    private final JCheckBox m_enablePanningCheckBox;

    private final JCheckBox m_enableZoomCheckBox;
    private final JCheckBox m_showZoomResetButtonCheckBox;

    private final JRadioButton m_enableClusterColorRadioButton;
    private final JRadioButton m_enableTableColorRadioButton;
    private final JRadioButton m_useColorPaletteSet1RadioButton;
    private final JRadioButton m_useColorPaletteSet2RadioButton;
    private final JRadioButton m_useColorPaletteSet3RadioButton;

    private final JCheckBox m_subscribeFilterEventsCheckBox;

    HierarchicalClusterAssignerDialog() {
        m_numClustersSpinner = new JSpinner(
            new SpinnerNumberModel(HierarchicalClusterAssignerConfig.DEFAULT_NUM_CLUSTERS, 1, Integer.MAX_VALUE, 1));
        // maximum is 1, because use normalized distances is enabled by default
        m_distanceThresholdSpinner = new JSpinner(
            new SpinnerNumberModel(HierarchicalClusterAssignerConfig.DEFAULT_NORMALIZED_THRESHOLD, 0, 1, 0.01));
        m_clusterCountModeRadioButton = new JRadioButton("Cluster count");
        m_distanceThresholdModeRadioButton = new JRadioButton("Distance threshold");
        m_clusterColumnNameTextField = new JTextField(TEXT_FIELD_SIZE);
        m_useNormalizedDistancesCheckBox = new JCheckBox("Use normalized distances for threshold");

        final ActionListener al = new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                m_numClustersSpinner.setEnabled(m_clusterCountModeRadioButton.isSelected());
                m_distanceThresholdSpinner.setEnabled(m_distanceThresholdModeRadioButton.isSelected());
                m_useNormalizedDistancesCheckBox.setEnabled(m_distanceThresholdModeRadioButton.isSelected());
            }
        };
        m_clusterCountModeRadioButton.addActionListener(al);
        m_distanceThresholdModeRadioButton.addActionListener(al);
        m_useNormalizedDistancesCheckBox.addActionListener(al);
        m_useNormalizedDistancesCheckBox.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(final ChangeEvent e) {
                enableNormalizedDistances();
            }
        });

        m_showWarningsInViewCheckBox = new JCheckBox("Show warnings in view");
        m_generateImageCheckBox = new JCheckBox("Create image at outport");
        m_imageWidthSpinner = new JSpinner(new SpinnerNumberModel(100, 100, Integer.MAX_VALUE, 1));
        m_imageHeightSpinner = new JSpinner(new SpinnerNumberModel(100, 100, Integer.MAX_VALUE, 1));
        m_displayFullscreenButtonCheckBox = new JCheckBox("Display fullscreen button");
        m_resizeToWindowCheckBox = new JCheckBox("Resize view to fill window");
        m_titleTextField = new JTextField(TEXT_FIELD_SIZE);
        m_subtitleTextField = new JTextField(TEXT_FIELD_SIZE);
        m_enableViewEditCheckBox = new JCheckBox("Enable view edit controls");
        m_enableTitleEditCheckBox = new JCheckBox("Enable title and subtitle editing");
        m_enableNumClusterEditCheckBox = new JCheckBox("Enable number of clusters specification");
        m_enableThresholdValueCheckBox = new JCheckBox("Enable numeric specification of threshold");
        m_enableClusterLabelsCheckBox = new JCheckBox("Enable cluster labels");
        m_enableSelectionCheckBox = new JCheckBox("Enable selection");
        m_publishSelectionEventsCheckBox = new JCheckBox("Publish selection events");
        m_subscribeSelectionEventsCheckBox = new JCheckBox("Subscribe to selection events");
        m_selectionColumnNameTextField = new JTextField(TEXT_FIELD_SIZE);
        m_enableZoomCheckBox = new JCheckBox("Enable zooming");
        m_showZoomResetButtonCheckBox = new JCheckBox("Show zoom reset button");
        m_enablePanningCheckBox = new JCheckBox("Enable panning");
        m_enableLogScaleToggleCheckBox= new JCheckBox("Enable switching y-axis scale");
        m_useLogScaleCheckBox = new JCheckBox("Use log scale for y-axis");
        m_enableChangeOrientationCheckBox = new JCheckBox("Enable changing chart orientation");
        m_orientationComboBox = new JComboBox<>();
        for (final HierarchicalClusterAssignerOrientation type : HierarchicalClusterAssignerOrientation.values()) {
            m_orientationComboBox.addItem(type);
        }
        m_subscribeFilterEventsCheckBox = new JCheckBox("Subscribe to filter events");

        m_enableViewEditCheckBox.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(final ChangeEvent e) {
                enableEditView();
            }
        });
        m_enableSelectionCheckBox.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(final ChangeEvent e) {
                enableSelections();
            }
        });

        m_enableClusterColorRadioButton = new JRadioButton("Use cluster colors");
        m_enableTableColorRadioButton = new JRadioButton("Use table colors");
        m_useColorPaletteSet1RadioButton = new JRadioButton("Set 1");
        m_useColorPaletteSet2RadioButton = new JRadioButton("Set 2");
        m_useColorPaletteSet3RadioButton = new JRadioButton("Set 3 (color blind safe)");

        m_enableClusterColorRadioButton.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(final ChangeEvent e) {
                enableColors();
            }
        });

        addTab("Options", optionsPanel());
        addTab("View Configuration", viewConfigPanel());
        addTab("Interactivity", interactivityPanel());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) throws InvalidSettingsException {
        final HierarchicalClusterAssignerConfig config = new HierarchicalClusterAssignerConfig();

        config.setShowWarningsInView(m_showWarningsInViewCheckBox.isSelected());
        config.setGenerateImage(m_generateImageCheckBox.isSelected());
        config.setImageWidth((int) m_imageWidthSpinner.getValue());
        config.setImageHeight((int) m_imageHeightSpinner.getValue());

        config.setResizeToWindow(m_resizeToWindowCheckBox.isSelected());
        config.setTitle(m_titleTextField.getText());
        config.setSubtitle(m_subtitleTextField.getText());
        config.setEnableClusterLabels(m_enableClusterLabelsCheckBox.isSelected());
        config.setUseLogScale(m_useLogScaleCheckBox.isSelected());
        config.setOrientation((HierarchicalClusterAssignerOrientation) m_orientationComboBox.getSelectedItem());

        config.setEnableViewEdit(m_enableViewEditCheckBox.isSelected());
        config.setEnableTitleEdit(m_enableTitleEditCheckBox.isSelected());
        config.setDisplayFullscreenButton(m_displayFullscreenButtonCheckBox.isSelected());
        config.setEnableNumClusterEdit(m_enableNumClusterEditCheckBox.isSelected());
        config.setEnableThresholdValue(m_enableThresholdValueCheckBox.isSelected());
        config.setEnableLogScaleToggle(m_enableLogScaleToggleCheckBox.isSelected());
        config.setEnableChangeOrientation(m_enableChangeOrientationCheckBox.isSelected());

        config.setEnableSelection(m_enableSelectionCheckBox.isSelected());
        config.setPublishSelectionEvents(m_publishSelectionEventsCheckBox.isSelected());
        config.setSubscribeSelectionEvents(m_subscribeSelectionEventsCheckBox.isSelected());
        config.setSelectionColumnName(m_selectionColumnNameTextField.getText());

        config.setSubscribeFilterEvents(m_subscribeFilterEventsCheckBox.isSelected());

        config.setNumClusters((int) m_numClustersSpinner.getValue());
        config.setNumClustersMode(m_clusterCountModeRadioButton.isSelected());
        config.setClusterColumnName(m_clusterColumnNameTextField.getText());
        config.setUseNormalizedDistances(m_useNormalizedDistancesCheckBox.isSelected());
        if (m_useNormalizedDistancesCheckBox.isSelected()) {
            config.setNormalizedThreshold((double) m_distanceThresholdSpinner.getValue());
        }
        else {
            config.setThreshold((double) m_distanceThresholdSpinner.getValue());
        }

        config.setEnablePanning(m_enablePanningCheckBox.isSelected());

        config.setEnableZoom(m_enableZoomCheckBox.isSelected());
        config.setShowZoomResetButton(m_showZoomResetButtonCheckBox.isSelected());

        config.setEnableClusterColor(m_enableClusterColorRadioButton.isSelected());
        config.setColorPalette(getColorPalette());

        config.saveSettings(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadSettingsFrom(final NodeSettingsRO settings, final PortObjectSpec[] specs) throws NotConfigurableException {
        final DataTableSpec spec = (DataTableSpec)specs[0];
        final HierarchicalClusterAssignerConfig config = new HierarchicalClusterAssignerConfig();
        config.loadSettingsForDialog(settings, spec);

        m_showWarningsInViewCheckBox.setSelected(config.getShowWarningsInView());
        m_generateImageCheckBox.setSelected(config.getGenerateImage());
        m_imageWidthSpinner.setValue(config.getImageWidth());
        m_imageHeightSpinner.setValue(config.getImageHeight());

        m_resizeToWindowCheckBox.setSelected(config.getResizeToWindow());
        m_titleTextField.setText(config.getTitle());
        m_subtitleTextField.setText(config.getSubtitle());
        m_enableClusterLabelsCheckBox.setSelected(config.getEnableClusterLabels());
        m_useLogScaleCheckBox.setSelected(config.getUseLogScale());
        m_orientationComboBox.setSelectedItem(config.getOrientation());

        m_enableViewEditCheckBox.setSelected(config.getEnableViewEdit());
        m_enableTitleEditCheckBox.setSelected(config.getEnableTitleEdit());
        m_displayFullscreenButtonCheckBox.setSelected(config.getDisplayFullscreenButton());
        m_enableNumClusterEditCheckBox.setSelected(config.getEnableNumClusterEdit());
        m_enableThresholdValueCheckBox.setSelected(config.getEnableThresholdValue());
        m_enableLogScaleToggleCheckBox.setSelected(config.getEnableLogScaleToggle());
        m_enableChangeOrientationCheckBox.setSelected(config.getEnableChangeOrientation());

        m_enableSelectionCheckBox.setSelected(config.getEnableSelection());
        m_publishSelectionEventsCheckBox.setSelected(config.getPublishSelectionEvents());
        m_subscribeSelectionEventsCheckBox.setSelected(config.getSubscribeSelectionEvents());
        m_selectionColumnNameTextField.setText(config.getSelectionColumnName());

        m_subscribeFilterEventsCheckBox.setSelected(config.getSubscribeFilterEvents());

        m_clusterCountModeRadioButton.setSelected(config.getNumClustersMode());
        m_distanceThresholdModeRadioButton.setSelected(!config.getNumClustersMode());
        m_numClustersSpinner.setEnabled(m_clusterCountModeRadioButton.isSelected());
        m_distanceThresholdSpinner.setEnabled(m_distanceThresholdModeRadioButton.isSelected());
        m_clusterColumnNameTextField.setText(config.getClusterColumnName());
        m_useNormalizedDistancesCheckBox.setSelected(config.getUseNormalizedDistances());
        m_useNormalizedDistancesCheckBox.setEnabled(m_distanceThresholdModeRadioButton.isSelected());
        m_numClustersSpinner.setValue(config.getNumClusters());
        if (m_useNormalizedDistancesCheckBox.isSelected()) {
            m_distanceThresholdSpinner.setValue(config.getNormalizedThreshold());
        } else {
            m_distanceThresholdSpinner.setValue(config.getThreshold());
        }

        m_enablePanningCheckBox.setSelected(config.getEnablePanning());

        m_enableZoomCheckBox.setSelected(config.getEnableZoom());
        m_showZoomResetButtonCheckBox.setSelected(config.getShowZoomResetButton());

        m_enableClusterColorRadioButton.setSelected(config.getEnableClusterColor());
        m_enableTableColorRadioButton.setSelected(!config.getEnableClusterColor());
        setColorPaletteRadioButtons(config.getColorPalette());

        setNumberOfFilters((DataTableSpec) specs[1]);
        enableEditView();
        enableSelections();
        enableColors();
        getPanel().repaint();
    }

    // -- Helper methods --

    private void setNumberOfFilters(final DataTableSpec spec) {
        int numFilters = 0;
        for (int i = 0; i < spec.getNumColumns(); i++) {
            if (spec.getColumnSpec(i).getFilterHandler().isPresent()) {
                numFilters++;
            }
        }
        StringBuilder builder = new StringBuilder("Subscribe to filter events");
        builder.append(" (");
        builder.append(numFilters == 0 ? "no" : numFilters);
        builder.append(numFilters == 1 ? " filter" : " filters");
        builder.append(" available)");
        m_subscribeFilterEventsCheckBox.setText(builder.toString());
    }

    private void enableEditView() {
        final boolean enabled = m_enableViewEditCheckBox.isSelected();
        m_enableTitleEditCheckBox.setEnabled(enabled);
        m_enableNumClusterEditCheckBox.setEnabled(enabled);
        m_enableThresholdValueCheckBox.setEnabled(enabled);
        m_displayFullscreenButtonCheckBox.setEnabled(enabled);
        m_enableLogScaleToggleCheckBox.setEnabled(enabled);
        m_enableChangeOrientationCheckBox.setEnabled(enabled);
    }

    private void enableSelections() {
        final boolean enabled = m_enableSelectionCheckBox.isSelected();
        m_publishSelectionEventsCheckBox.setEnabled(enabled);
        m_subscribeSelectionEventsCheckBox.setEnabled(enabled);
        m_selectionColumnNameTextField.setEnabled(enabled);
    }

    private void enableNormalizedDistances() {
        if (m_useNormalizedDistancesCheckBox.isSelected()) {
            if ((double)m_distanceThresholdSpinner.getValue() > 1) {
                m_distanceThresholdSpinner.setValue(1);
            }
            ((SpinnerNumberModel)m_distanceThresholdSpinner.getModel()).setMaximum(new Double(1));
        } else {
            ((SpinnerNumberModel)m_distanceThresholdSpinner.getModel()).setMaximum(Double.MAX_VALUE);
        }
    }

    private void enableColors() {
        final boolean enabled = m_enableClusterColorRadioButton.isSelected();
        m_useColorPaletteSet1RadioButton.setEnabled(enabled);
        m_useColorPaletteSet2RadioButton.setEnabled(enabled);
        m_useColorPaletteSet3RadioButton.setEnabled(enabled);
    }

    private Component optionsPanel() {
        final JPanel p = new JPanel(new GridBagLayout());
        final ButtonGroup bg = new ButtonGroup();
        bg.add(m_clusterCountModeRadioButton);
        bg.add(m_distanceThresholdModeRadioButton);

        final GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridwidth = 2;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridy++;

        p.add(new JLabel("Cluster assignment column name: "), gbc);
        gbc.gridx += 2;
        p.add(m_clusterColumnNameTextField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;

        p.add(new JLabel("Assign clusters based on:"), gbc);
        gbc.gridx = 2;
        p.add(m_clusterCountModeRadioButton, gbc);
        gbc.gridy++;
        p.add(m_distanceThresholdModeRadioButton, gbc);

        gbc.gridx = 0;
        gbc.gridy++;

        p.add(new JLabel("Number of clusters"), gbc);
        gbc.gridx = 2;
        p.add(m_numClustersSpinner, gbc);
        ((JSpinner.NumberEditor)m_numClustersSpinner.getEditor()).getTextField().setColumns(5);

        gbc.gridx = 0;
        gbc.gridy++;

        p.add(new JLabel("Distance threshold"), gbc);
        gbc.gridx = 2;
        p.add(m_distanceThresholdSpinner, gbc);
        ((JSpinner.NumberEditor)m_distanceThresholdSpinner.getEditor()).getTextField().setColumns(5);
        gbc.gridy++;
        p.add(m_useNormalizedDistancesCheckBox, gbc);

        gbc.gridx = 0;
        gbc.gridy++;

        return p;
    }

    private Component viewConfigPanel() {
        final JPanel p = new JPanel(new GridBagLayout());
        final GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridy++;

        // General
        final JPanel generalPanel = new JPanel(new GridBagLayout());
        generalPanel.setBorder(BorderFactory.createTitledBorder("General"));
        p.add(generalPanel, gbc);
        final GridBagConstraints generalConstraints = new GridBagConstraints();
        generalConstraints.insets = new Insets(5, 5, 5, 5);
        generalConstraints.anchor = GridBagConstraints.NORTHWEST;
        generalConstraints.gridx = 0;
        generalConstraints.gridy = 0;
        generalConstraints.weightx = 1;
        generalPanel.add(m_showWarningsInViewCheckBox, generalConstraints);
        generalConstraints.gridx++;
        generalPanel.add(m_generateImageCheckBox, generalConstraints);
        generalConstraints.gridx = 0;
        generalConstraints.gridy++;
        generalPanel.add(m_resizeToWindowCheckBox, generalConstraints);
        generalConstraints.gridx = 0;
        generalConstraints.gridy++;
        generalPanel.add(new JLabel("Width (px): "), generalConstraints);
        generalConstraints.gridx++;
        generalPanel.add(m_imageWidthSpinner, generalConstraints);
        generalConstraints.gridx = 0;
        generalConstraints.gridy++;
        generalPanel.add(new JLabel("Height (px): "), generalConstraints);
        generalConstraints.gridx++;
        generalPanel.add(m_imageHeightSpinner, generalConstraints);
        generalConstraints.gridx = 0;
        generalConstraints.gridy++;

        gbc.gridx = 0;
        gbc.gridy++;

        // Display
        final JPanel displayPanel = new JPanel(new GridBagLayout());
        displayPanel.setBorder(BorderFactory.createTitledBorder("Display"));
        p.add(displayPanel, gbc);
        final GridBagConstraints displayConstraints = new GridBagConstraints();
        displayConstraints.insets = new Insets(5, 5, 5, 5);
        displayConstraints.anchor = GridBagConstraints.NORTHWEST;
        displayConstraints.gridx = 0;
        displayConstraints.gridy = 0;
        displayConstraints.weightx = 1;
        displayPanel.add(new JLabel("Chart title: "), displayConstraints);
        displayConstraints.gridx++;
        displayPanel.add(m_titleTextField, displayConstraints);
        displayConstraints.gridx = 0;
        displayConstraints.gridy++;
        displayPanel.add(new JLabel("Chart subtitle: "), displayConstraints);
        displayConstraints.gridx++;
        displayPanel.add(m_subtitleTextField, displayConstraints);
        displayConstraints.gridx = 0;
        displayConstraints.gridy++;
        displayPanel.add(new JLabel("Chart Orientation: "), displayConstraints);
        displayConstraints.gridx++;
        displayPanel.add(m_orientationComboBox, displayConstraints);
        displayConstraints.gridx = 0;
        displayConstraints.gridy++;
        displayPanel.add(m_enableClusterLabelsCheckBox, displayConstraints);
        displayConstraints.gridx++;
        displayPanel.add(m_useLogScaleCheckBox, displayConstraints);
        displayConstraints.gridx = 0;
        displayConstraints.gridy++;

        gbc.gridx = 0;
        gbc.gridy++;

        // Color
        final JPanel colorPanel = new JPanel(new GridBagLayout());
        colorPanel.setBorder(BorderFactory.createTitledBorder("Color"));
        final Component set1 = ColorPaletteUtil.getColorPaletteSet1AsComponent();
        set1.setEnabled(false);
        p.add(colorPanel, gbc);
        final ButtonGroup clusterOrTableColors = new ButtonGroup();
        clusterOrTableColors.add(m_enableClusterColorRadioButton);
        clusterOrTableColors.add(m_enableTableColorRadioButton);
        final ButtonGroup colorPalettes = new ButtonGroup();
        colorPalettes.add(m_useColorPaletteSet1RadioButton);
        colorPalettes.add(m_useColorPaletteSet2RadioButton);
        colorPalettes.add(m_useColorPaletteSet3RadioButton);
        final GridBagConstraints colorConstraints = new GridBagConstraints();
        colorConstraints.insets = new Insets(5, 5, 5, 5);
        colorConstraints.anchor = GridBagConstraints.NORTHWEST;
        colorConstraints.gridx = 0;
        colorConstraints.gridy = 0;
        colorConstraints.weightx = 1;
        colorPanel.add(m_enableClusterColorRadioButton, colorConstraints);
        colorConstraints.gridx++;
        colorPanel.add(m_enableTableColorRadioButton, colorConstraints);
        colorConstraints.gridx = 0;
        colorConstraints.gridy++;
        colorPanel.add(new JLabel("Select a color palette:"), colorConstraints);
        colorConstraints.gridx = 0;
        colorConstraints.gridy++;
        colorPanel.add(m_useColorPaletteSet1RadioButton, colorConstraints);
        colorConstraints.gridx++;
        colorPanel.add(set1, colorConstraints);
        colorConstraints.gridx = 0;
        colorConstraints.gridy++;
        colorPanel.add(m_useColorPaletteSet2RadioButton, colorConstraints);
        colorConstraints.gridx++;
        colorPanel.add(ColorPaletteUtil.getColorPaletteSet2AsComponent(), colorConstraints);
        colorConstraints.gridx = 0;
        colorConstraints.gridy++;
        colorPanel.add(m_useColorPaletteSet3RadioButton, colorConstraints);
        colorConstraints.gridx++;
        colorPanel.add(ColorPaletteUtil.getColorPaletteSet3AsComponent(), colorConstraints);
        colorConstraints.gridx = 0;
        colorConstraints.gridy++;

        gbc.gridx = 0;
        gbc.gridy++;

        return p;
    }

    private Component interactivityPanel() {
        final JPanel p = new JPanel(new GridBagLayout());
        final GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridy++;

        // View Edit Controls
        final JPanel viewControlsPanel = new JPanel(new GridBagLayout());
        viewControlsPanel.setBorder(BorderFactory.createTitledBorder("View edit controls"));
        p.add(viewControlsPanel, gbc);
        final GridBagConstraints viewControlsConstraints = new GridBagConstraints();
        viewControlsConstraints.insets = new Insets(5, 5, 5, 5);
        viewControlsConstraints.anchor = GridBagConstraints.NORTHWEST;
        viewControlsConstraints.gridx = 0;
        viewControlsConstraints.gridy = 0;
        viewControlsConstraints.weightx = 1;
        viewControlsPanel.add(m_enableViewEditCheckBox, viewControlsConstraints);
        viewControlsConstraints.gridx = 0;
        viewControlsConstraints.gridy++;
        viewControlsPanel.add(m_enableTitleEditCheckBox, viewControlsConstraints);
        viewControlsConstraints.gridx += 2;
        viewControlsPanel.add(m_displayFullscreenButtonCheckBox, viewControlsConstraints);
        viewControlsConstraints.gridx = 0;
        viewControlsConstraints.gridy++;
        viewControlsPanel.add(m_enableNumClusterEditCheckBox, viewControlsConstraints);
        viewControlsConstraints.gridx += 2;
        viewControlsPanel.add(m_enableThresholdValueCheckBox, viewControlsConstraints);
        viewControlsConstraints.gridx = 0;
        viewControlsConstraints.gridy++;
        viewControlsPanel.add(m_enableLogScaleToggleCheckBox, viewControlsConstraints);
        viewControlsConstraints.gridx += 2;
        viewControlsPanel.add(m_enableChangeOrientationCheckBox, viewControlsConstraints);

        gbc.gridx = 0;
        gbc.gridy++;

        // Selection
        final JPanel selectionPanel = new JPanel(new GridBagLayout());
        selectionPanel.setBorder(BorderFactory.createTitledBorder("Selection & Filtering"));
        p.add(selectionPanel, gbc);
        final GridBagConstraints selectionConstraints = new GridBagConstraints();
        selectionConstraints.insets = new Insets(5, 5, 5, 5);
        selectionConstraints.anchor = GridBagConstraints.NORTHWEST;
        selectionConstraints.gridx = 0;
        selectionConstraints.gridy = 0;
        selectionConstraints.weightx = 1;
        selectionPanel.add(m_enableSelectionCheckBox, selectionConstraints);
        selectionConstraints.gridx++;
        selectionPanel.add(m_subscribeFilterEventsCheckBox, selectionConstraints);
        selectionConstraints.gridx = 0;
        selectionConstraints.gridy++;
        selectionPanel.add(new JLabel("Selection column name: "), selectionConstraints);
        selectionConstraints.gridx++;
        selectionPanel.add(m_selectionColumnNameTextField, selectionConstraints);
        selectionConstraints.gridx = 0;
        selectionConstraints.gridy++;
        selectionPanel.add(m_publishSelectionEventsCheckBox, selectionConstraints);
        selectionConstraints.gridx++;
        selectionPanel.add(m_subscribeSelectionEventsCheckBox, selectionConstraints);

        gbc.gridx = 0;
        gbc.gridy++;

        // Zoom
        final JPanel zoomPanel = new JPanel(new GridBagLayout());
        zoomPanel.setBorder(BorderFactory.createTitledBorder("Zoom & Panning"));
        p.add(zoomPanel, gbc);
        final GridBagConstraints zoomConstraints = new GridBagConstraints();
        zoomConstraints.insets = new Insets(5, 5, 5, 5);
        zoomConstraints.anchor = GridBagConstraints.NORTHWEST;
        zoomConstraints.gridx = 0;
        zoomConstraints.gridy = 0;
        zoomConstraints.weightx = 1;
        zoomPanel.add(m_enableZoomCheckBox, zoomConstraints);
        zoomConstraints.gridx++;
        zoomPanel.add(m_enablePanningCheckBox, zoomConstraints);
        zoomConstraints.gridx = 0;
        zoomConstraints.gridy++;
        zoomPanel.add(m_showZoomResetButtonCheckBox, zoomConstraints);

        gbc.gridx = 0;
        gbc.gridy++;

        return p;
    }

    private String[] getColorPalette() {
        if (m_useColorPaletteSet1RadioButton.isSelected()) {
            return ColorPaletteUtil.PALETTE_SET1;
        }
        if (m_useColorPaletteSet2RadioButton.isSelected()) {
            return ColorPaletteUtil.PALETTE_SET2;
        }
        if (m_useColorPaletteSet3RadioButton.isSelected()) {
            return ColorPaletteUtil.PALETTE_SET3;
        }
        return null;
    }

    private void setColorPaletteRadioButtons(final String[] colorPalette) {
        m_useColorPaletteSet1RadioButton.setSelected(Arrays.equals(colorPalette, ColorPaletteUtil.PALETTE_SET1));
        m_useColorPaletteSet2RadioButton.setSelected(Arrays.equals(colorPalette, ColorPaletteUtil.PALETTE_SET2));
        m_useColorPaletteSet3RadioButton.setSelected(Arrays.equals(colorPalette, ColorPaletteUtil.PALETTE_SET3));
    }
}
