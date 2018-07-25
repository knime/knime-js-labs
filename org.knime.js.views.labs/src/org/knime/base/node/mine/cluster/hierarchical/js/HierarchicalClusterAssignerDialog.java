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

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
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
    private final JCheckBox m_enableClusterColorCheckBox;

    private final JCheckBox m_enableViewEditCheckBox;
    private final JCheckBox m_enableTitleEditCheckBox;
    private final JCheckBox m_displayFullscreenButtonCheckBox;
    private final JCheckBox m_enableNumClusterEditCheckBox;
    private final JCheckBox m_enableThresholdValueCheckBox;

    private final JCheckBox m_enableSelectionCheckBox;
    private final JCheckBox m_publishSelectionEventsCheckBox;
    private final JCheckBox m_subscribeSelectionEventsCheckBox;
    private final JTextField m_selectionColumnNameTextField;

    private final JSpinner m_numClustersSpinner;
    private final JSpinner m_distanceThresholdSpinner;
    private final JRadioButton m_clusterCountModeRadioButton;
    private final JRadioButton m_distanceThresholdModeRadioButton;
    private final JTextField m_clusterColumnNameTextField;

    HierarchicalClusterAssignerDialog() {
        m_numClustersSpinner = new JSpinner(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));
        m_distanceThresholdSpinner = new JSpinner(new SpinnerNumberModel(0.5, 0, Double.MAX_VALUE, 0.01));
        m_clusterCountModeRadioButton = new JRadioButton("Cluster count");
        m_distanceThresholdModeRadioButton = new JRadioButton("Distance threshold");
        m_clusterColumnNameTextField = new JTextField(TEXT_FIELD_SIZE);

        final ActionListener al = new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                m_numClustersSpinner.setEnabled(m_clusterCountModeRadioButton.isSelected());
                m_distanceThresholdSpinner.setEnabled(m_distanceThresholdModeRadioButton
                        .isSelected());
            }
        };
        m_clusterCountModeRadioButton.addActionListener(al);
        m_distanceThresholdModeRadioButton.addActionListener(al);

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
        m_enableClusterColorCheckBox = new JCheckBox("Enable cluster colors");
        m_enableSelectionCheckBox = new JCheckBox("Enable selection");
        m_publishSelectionEventsCheckBox = new JCheckBox("Publish selection events");
        m_subscribeSelectionEventsCheckBox = new JCheckBox("Subscribe to selection events");
        m_selectionColumnNameTextField = new JTextField(TEXT_FIELD_SIZE);

        m_generateImageCheckBox.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(final ChangeEvent e) {
                enableGenerateImage();
            }
        });
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

        addTab("Options", optionsPanel());
        addTab("View Configuration", viewConfigPanel());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) throws InvalidSettingsException {
        final HierarchicalClusterAssignerConfig config = new HierarchicalClusterAssignerConfig();

        config.setShowWarningsInView(m_showWarningsInViewCheckBox.isSelected());
        config.setGenerateImage(m_generateImageCheckBox.isSelected());
        config.setImageWidth((Integer) m_imageWidthSpinner.getValue());
        config.setImageHeight((Integer) m_imageHeightSpinner.getValue());

        config.setResizeToWindow(m_resizeToWindowCheckBox.isSelected());
        config.setTitle(m_titleTextField.getText());
        config.setSubtitle(m_subtitleTextField.getText());
        config.setEnableClusterLabels(m_enableClusterLabelsCheckBox.isSelected());
        config.setEnableClusterColor(m_enableClusterColorCheckBox.isSelected());

        config.setEnableViewEdit(m_enableViewEditCheckBox.isSelected());
        config.setEnableTitleEdit(m_enableTitleEditCheckBox.isSelected());
        config.setDisplayFullscreenButton(m_displayFullscreenButtonCheckBox.isSelected());
        config.setEnableNumClusterEdit(m_enableNumClusterEditCheckBox.isSelected());
        config.setEnableThresholdValue(m_enableThresholdValueCheckBox.isSelected());

        config.setEnableSelection(m_enableSelectionCheckBox.isSelected());
        config.setPublishSelectionEvents(m_publishSelectionEventsCheckBox.isSelected());
        config.setSubscribeSelectionEvents(m_subscribeSelectionEventsCheckBox.isSelected());
        config.setSelectionColumnName(m_selectionColumnNameTextField.getText());

        config.setNumClusters((Integer) m_numClustersSpinner.getValue());
        config.setThreshold((Double) m_distanceThresholdSpinner.getValue());
        config.setNumClustersMode(m_clusterCountModeRadioButton.isSelected());
        config.setClusterColumnName(m_clusterColumnNameTextField.getText());

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
        m_enableClusterColorCheckBox.setSelected(config.getEnableClusterColor());

        m_enableViewEditCheckBox.setSelected(config.getEnableViewEdit());
        m_enableTitleEditCheckBox.setSelected(config.getEnableTitleEdit());
        m_displayFullscreenButtonCheckBox.setSelected(config.getDisplayFullscreenButton());
        m_enableNumClusterEditCheckBox.setSelected(config.getEnableNumClusterEdit());
        m_enableThresholdValueCheckBox.setSelected(config.getEnableThresholdValue());

        m_enableSelectionCheckBox.setSelected(config.getEnableSelection());
        m_publishSelectionEventsCheckBox.setSelected(config.getPublishSelectionEvents());
        m_subscribeSelectionEventsCheckBox.setSelected(config.getSubscribeSelectionEvents());
        m_selectionColumnNameTextField.setText(config.getSelectionColumnName());

        m_numClustersSpinner.setValue(config.getNumClusters());
        m_distanceThresholdSpinner.setValue(config.getThreshold());
        m_clusterCountModeRadioButton.setSelected(config.getNumClustersMode());
        m_distanceThresholdModeRadioButton.setSelected(!config.getNumClustersMode());
        m_numClustersSpinner.setEnabled(m_clusterCountModeRadioButton.isSelected());
        m_distanceThresholdSpinner.setEnabled(m_distanceThresholdModeRadioButton.isSelected());
        m_clusterColumnNameTextField.setText(config.getClusterColumnName());

        enableGenerateImage();
        enableEditView();
        enableSelections();
        getPanel().repaint();
    }

    // -- Helper methods --

    private void enableGenerateImage() {
        final boolean enabled = m_generateImageCheckBox.isSelected();
        m_imageWidthSpinner.setEnabled(enabled);
        m_imageHeightSpinner.setEnabled(enabled);
    }

    private void enableEditView() {
        final boolean enabled = m_enableViewEditCheckBox.isSelected();
        m_enableTitleEditCheckBox.setEnabled(enabled);
        m_enableNumClusterEditCheckBox.setEnabled(enabled);
        m_enableThresholdValueCheckBox.setEnabled(enabled);
        m_displayFullscreenButtonCheckBox.setEnabled(enabled);
    }

    private void enableSelections() {
        final boolean enabled = m_enableSelectionCheckBox.isSelected();
        m_publishSelectionEventsCheckBox.setEnabled(enabled);
        m_subscribeSelectionEventsCheckBox.setEnabled(enabled);
        m_selectionColumnNameTextField.setEnabled(enabled);
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

        return p;
    }

    private Component viewConfigPanel() {
        final JPanel p = new JPanel(new GridBagLayout());
        final GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridwidth = 2;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridy++;

        // Image
        final JPanel generalPanel = new JPanel(new GridBagLayout());
        generalPanel.setBorder(BorderFactory.createTitledBorder("General"));
        p.add(generalPanel, gbc);
        final GridBagConstraints generalConstraints = new GridBagConstraints();
        generalConstraints.insets = new Insets(5, 5, 5, 5);
        generalConstraints.anchor = GridBagConstraints.NORTHWEST;
        generalConstraints.gridx = 0;
        generalConstraints.gridy = 0;
        generalPanel.add(m_showWarningsInViewCheckBox, generalConstraints);
        generalConstraints.gridx = 0;
        generalConstraints.gridy++;
        generalPanel.add(m_generateImageCheckBox, generalConstraints);
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
        displayPanel.add(m_enableClusterLabelsCheckBox, displayConstraints);
        displayConstraints.gridx++;
        displayPanel.add(m_enableClusterColorCheckBox, displayConstraints);
        displayConstraints.gridx = 0;
        displayConstraints.gridy++;
        displayPanel.add(m_resizeToWindowCheckBox, displayConstraints);
        displayConstraints.gridx = 0;
        displayConstraints.gridy++;

        gbc.gridx = 0;
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

        gbc.gridx = 0;
        gbc.gridy++;

        // Selection
        final JPanel selectionPanel = new JPanel(new GridBagLayout());
        selectionPanel.setBorder(BorderFactory.createTitledBorder("Selection"));
        p.add(selectionPanel, gbc);
        final GridBagConstraints selectionConstraints = new GridBagConstraints();
        selectionConstraints.insets = new Insets(5, 5, 5, 5);
        selectionConstraints.anchor = GridBagConstraints.NORTHWEST;
        selectionConstraints.gridx = 0;
        selectionConstraints.gridy = 0;
        selectionPanel.add(m_enableSelectionCheckBox, selectionConstraints);
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

        return p;
    }
}
