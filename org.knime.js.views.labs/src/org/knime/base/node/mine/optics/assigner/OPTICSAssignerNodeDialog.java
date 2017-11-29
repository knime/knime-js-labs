/*
 * ------------------------------------------------------------------------
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
 *  KNIME and ECLIPSE being a combined program, KNIME GMBH herewith grants
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
 * ------------------------------------------------------------------------
 */
package org.knime.base.node.mine.optics.assigner;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
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
import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.port.PortObjectSpec;

/**
 * <code>NodeDialog</code> for the "OPTICS" Node.
 *
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows creation of a simple dialog with standard
 * components.
 *
 * @author Anastasia Zhukova, KNIME GmbH, Konstanz, Germany
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 */
final class OPTICSAssignerNodeDialog extends NodeDialogPane {

    private static final int TEXT_FIELD_SIZE = 20;

    private final JSpinner m_eps_pr;

    private final JCheckBox m_generateImageCheckBox;

    private final JCheckBox m_displayFullscreenButtonCheckBox;

    private final JCheckBox m_resizeViewToWindow;

    private final JCheckBox m_enableViewConfigCheckBox;

    private final JCheckBox m_enableTitleChangeCheckBox;

    private final JCheckBox m_enableEpsilonPrimeChangeCheckBox;

    private final JCheckBox m_enableSelectionCheckBox;

    private final JCheckBox m_allowRectangleSelectionCheckBox;

    private final JCheckBox m_publishSelectionCheckBox;

    private final JCheckBox m_enableShowSelectedOnlyCheckBox;

    private final JCheckBox m_subscribeSelectionCheckBox;

    private final JRadioButtonMenuItem m_calcEpsPrimeMean;

    private final JRadioButtonMenuItem m_calcEpsPrimeMedian;

    private final JRadioButtonMenuItem m_manualEpsPrime;

    private final ButtonGroup m_epsilonGroup;

    private final JTextField m_chartTitleTextField;

    private final JTextField m_chartSubTitleTextField;

    private final JSpinner m_maxRowsSpinner;

    private final JSpinner m_imageWidthSpinner;

    private final JSpinner m_imageHeightSpinner;

    private final JCheckBox m_showWarningInViewCheckBox;

    private final JComboBox<String> m_epsGroup;

    /**
     * New pane for configuring OPTICS node dialog.
     */
    OPTICSAssignerNodeDialog() {
        m_eps_pr = new JSpinner(new SpinnerNumberModel(0.5, 0.0001, 10000000, 0.01));
        m_eps_pr.setEnabled(false);
        m_calcEpsPrimeMean = new JRadioButtonMenuItem("Calculate mean on reachability distance", true);
        m_calcEpsPrimeMedian = new JRadioButtonMenuItem("Calculate median on reachability distance", false);
        m_manualEpsPrime = new JRadioButtonMenuItem("Manual input", false);
        m_epsilonGroup = new ButtonGroup();
        m_epsilonGroup.add(m_calcEpsPrimeMean);
        m_epsilonGroup.add(m_calcEpsPrimeMedian);
        m_epsilonGroup.add(m_manualEpsPrime);
        m_epsGroup = new JComboBox<String>();
        m_epsGroup.addItem("Mean on reachability distance");
        m_epsGroup.addItem("Median on reachability distance");
        m_epsGroup.addItem("Manual input");
        m_epsGroup.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                enableManualEpsilonPrime();
            }
        });
        m_generateImageCheckBox = new JCheckBox("Create image at outport");
        m_publishSelectionCheckBox = new JCheckBox("Publish selection events");
        m_subscribeSelectionCheckBox = new JCheckBox("Subscribe to selection events");
        m_displayFullscreenButtonCheckBox = new JCheckBox("Display fullscreen button");
        m_resizeViewToWindow = new JCheckBox("Resize view to fill window");
        m_enableViewConfigCheckBox = new JCheckBox("Enable view edit controls");
        m_enableTitleChangeCheckBox = new JCheckBox("Enable title and subtitle edit controls");
        m_enableEpsilonPrimeChangeCheckBox = new JCheckBox("Enable epsilon prime edit controls");
        m_enableSelectionCheckBox = new JCheckBox("Enable selection");
        m_allowRectangleSelectionCheckBox = new JCheckBox("Enable rectangular selection");
        m_enableShowSelectedOnlyCheckBox = new JCheckBox("Enable 'Show selected points only' option");
        m_chartTitleTextField = new JTextField(TEXT_FIELD_SIZE);
        m_chartSubTitleTextField = new JTextField(TEXT_FIELD_SIZE);
        m_maxRowsSpinner = new JSpinner(new SpinnerNumberModel(2000, 1, 2500, 100));
        m_imageWidthSpinner = new JSpinner(new SpinnerNumberModel(100, 100, Integer.MAX_VALUE, 1));
        m_imageHeightSpinner = new JSpinner(new SpinnerNumberModel(100, 100, Integer.MAX_VALUE, 1));
        m_showWarningInViewCheckBox = new JCheckBox("Show warnings in view");

        m_enableViewConfigCheckBox.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(final ChangeEvent e) {
                enableViewControls();
            }
        });

        m_enableSelectionCheckBox.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(final ChangeEvent e) {
                enableSelectionControls();
            }
        });

        addTab("Distance Calculation", distancePanel());
        addTab("View Configuration", viewConfigPanel());
    }

    private void enableManualEpsilonPrime() {
        if (m_epsGroup.getSelectedIndex() == 2) {
            m_eps_pr.setEnabled(true);
        } else {
            m_eps_pr.setEnabled(false);
        }
    }

    private Component distancePanel() {
        final JPanel p = new JPanel(new GridBagLayout());
        final GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridwidth = 2;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridy++;

        JPanel epsPanel2 = new JPanel(new GridBagLayout());
        epsPanel2.setBorder(BorderFactory.createTitledBorder("Epsilon-prime"));
        p.add(epsPanel2, gbc);
        GridBagConstraints cc2 = new GridBagConstraints();
        cc2.insets = new Insets(5, 5, 5, 5);
        cc2.anchor = GridBagConstraints.NORTHWEST;
        cc2.gridx = 0;
        cc2.gridy = 0;
        epsPanel2.add(m_epsGroup, cc2);
        cc2.gridx++;
        epsPanel2.add(m_eps_pr, cc2);

        gbc.gridx = 0;
        gbc.gridy++;
        JPanel binsPanel = new JPanel(new GridBagLayout());
        binsPanel.setBorder(BorderFactory.createTitledBorder("Number of bins"));
        p.add(binsPanel, gbc);
        GridBagConstraints cc1 = new GridBagConstraints();
        cc1.insets = new Insets(5, 5, 5, 5);
        cc1.anchor = GridBagConstraints.NORTHWEST;
        cc1.gridx = 0;
        cc1.gridy = 0;
        binsPanel.add(new JLabel("Number of bins: "), cc1);
        cc1.gridx += 1;
        m_maxRowsSpinner.setPreferredSize(new Dimension(100, TEXT_FIELD_SIZE));
        binsPanel.add(m_maxRowsSpinner, cc1);
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

        JPanel generalPanel = new JPanel(new GridBagLayout());
        generalPanel.setBorder(BorderFactory.createTitledBorder("General"));
        p.add(generalPanel, gbc);
        GridBagConstraints cc = new GridBagConstraints();
        cc.insets = new Insets(5, 5, 5, 5);
        cc.anchor = GridBagConstraints.NORTHWEST;
        cc.gridx = 0;
        cc.gridy = 0;
        generalPanel.add(m_showWarningInViewCheckBox, cc);
        cc.gridx = 0;
        cc.gridy++;
        generalPanel.add(m_generateImageCheckBox, cc);
        cc.gridx = 0;
        cc.gridy++;

        gbc.gridx = 0;
        gbc.gridy++;
        JPanel titlesPanel = new JPanel(new GridBagLayout());
        titlesPanel.setBorder(BorderFactory.createTitledBorder("Titles"));
        p.add(titlesPanel, gbc);
        //GridBagConstraints cc = new GridBagConstraints();
        //cc.insets = new Insets(5, 5, 5, 5);
        //cc.anchor = GridBagConstraints.NORTHWEST;
        cc.gridx = 0;
        cc.gridy = 0;
        titlesPanel.add(new JLabel("Chart title: "), cc);
        cc.gridx++;
        titlesPanel.add(m_chartTitleTextField, cc);
        cc.gridx = 0;
        cc.gridy++;
        titlesPanel.add(new JLabel("Chart subtitle: "), cc);
        cc.gridx++;
        titlesPanel.add(m_chartSubTitleTextField, cc);
        cc.gridx = 0;
        cc.gridy++;

        gbc.gridx = 0;
        gbc.gridy++;
        JPanel sizesPanel = new JPanel(new GridBagLayout());
        sizesPanel.setBorder(BorderFactory.createTitledBorder("Sizes"));
        p.add(sizesPanel, gbc);
        cc.gridx = 0;
        cc.gridy = 0;
        sizesPanel.add(new JLabel("Width of image (in px): "), cc);
        cc.gridx++;
        m_imageWidthSpinner.setPreferredSize(new Dimension(100, TEXT_FIELD_SIZE));
        sizesPanel.add(m_imageWidthSpinner, cc);
        cc.gridx = 0;
        cc.gridy++;
        sizesPanel.add(new JLabel("Height of image (in px): "), cc);
        cc.gridx++;
        m_imageHeightSpinner.setPreferredSize(new Dimension(100, TEXT_FIELD_SIZE));
        sizesPanel.add(m_imageHeightSpinner, cc);
        cc.gridx = 0;
        cc.gridy++;
        //        cc.anchor = GridBagConstraints.CENTER;
        sizesPanel.add(m_resizeViewToWindow, cc);
        cc.gridx++;
        sizesPanel.add(m_displayFullscreenButtonCheckBox, cc);
        cc.gridx = 0;
        cc.gridy++;

        gbc.gridx = 0;
        gbc.gridy++;
        JPanel viewControlsPanel = new JPanel(new GridBagLayout());
        viewControlsPanel.setBorder(BorderFactory.createTitledBorder("View edit controls"));
        p.add(viewControlsPanel, gbc);
        GridBagConstraints ccc = new GridBagConstraints();
        ccc.insets = new Insets(5, 5, 5, 5);
        ccc.anchor = GridBagConstraints.NORTHWEST;
        ccc.gridx = 0;
        ccc.gridy = 0;
        viewControlsPanel.add(m_enableViewConfigCheckBox, ccc);
        ccc.gridy++;
        viewControlsPanel.add(m_enableTitleChangeCheckBox, ccc);
        ccc.gridx += 2;
        viewControlsPanel.add(m_enableEpsilonPrimeChangeCheckBox, ccc);

        gbc.gridx = 0;
        gbc.gridy++;
        JPanel selectionControlPanel = new JPanel(new GridBagLayout());
        selectionControlPanel.setBorder(BorderFactory.createTitledBorder("Selection"));
        p.add(selectionControlPanel, gbc);
        cc.gridx = 0;
        cc.gridy = 0;
        selectionControlPanel.add(m_enableSelectionCheckBox, cc);
        cc.gridx++;
        selectionControlPanel.add(m_publishSelectionCheckBox, cc);

        return p;
    }

    private void enableViewControls() {
        boolean enable = m_enableViewConfigCheckBox.isSelected();
        m_enableTitleChangeCheckBox.setEnabled(enable);
        m_enableEpsilonPrimeChangeCheckBox.setEnabled(enable);
    }

    private void enableSelectionControls() {
        boolean enable = m_enableSelectionCheckBox.isSelected();
        m_allowRectangleSelectionCheckBox.setEnabled(enable);
        m_publishSelectionCheckBox.setEnabled(enable);
        m_subscribeSelectionCheckBox.setEnabled(enable);
        m_enableShowSelectedOnlyCheckBox.setEnabled(enable);
    }

    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) throws InvalidSettingsException {
        final OPTICSAssignerViewConfig config = new OPTICSAssignerViewConfig();
        config.setCalcEpsPrimeMean(m_calcEpsPrimeMean.isSelected());
        config.setCalcEpsPrimeMedian(m_calcEpsPrimeMedian.isSelected());
        config.setManualEpsPrime(m_manualEpsPrime.isSelected());
        config.setEpsPrime((Double)m_eps_pr.getValue());
        config.setGenerateImage(m_generateImageCheckBox.isSelected());
        config.setDisplayFullscreenButton(m_displayFullscreenButtonCheckBox.isSelected());
        config.setResizeToWindow(m_resizeViewToWindow.isSelected());
        config.setEnableViewConfiguration(m_enableViewConfigCheckBox.isSelected());
        config.setEnableTitleChange(m_enableTitleChangeCheckBox.isSelected());
        config.setEnableEpsilonPrimeChange(m_enableEpsilonPrimeChangeCheckBox.isSelected());
        config.setEnableSelection(m_enableSelectionCheckBox.isSelected());
        config.setEnableRectangleSelection(m_allowRectangleSelectionCheckBox.isSelected());
        config.setPublishSelection(m_publishSelectionCheckBox.isSelected());
        config.setSubscribeSelection(m_subscribeSelectionCheckBox.isSelected());
        config.setEnableShowSelectedOnly(m_enableShowSelectedOnlyCheckBox.isSelected());
        config.setChartTitle(m_chartTitleTextField.getText());
        config.setChartSubtitle(m_chartSubTitleTextField.getText());
        config.setMaxRows((Integer)m_maxRowsSpinner.getValue());
        config.setImageWidth((Integer)m_imageWidthSpinner.getValue());
        config.setImageHeight((Integer)m_imageHeightSpinner.getValue());
        config.setShowWarningInView(m_showWarningInViewCheckBox.isSelected());
        config.setEpsCalcMethod(getEpsCalcMethod());
        config.saveConfiguration(settings);
    }

    @Override
    protected void loadSettingsFrom(final NodeSettingsRO settings, final PortObjectSpec[] specs)
        throws NotConfigurableException {
        DataTableSpec spec = (DataTableSpec)specs[0];
        final OPTICSAssignerViewConfig config = new OPTICSAssignerViewConfig();
        try {
            config.loadConfigurationInDialog(settings, spec);
        } catch (InvalidSettingsException e) {
            e.printStackTrace();
        }
        m_eps_pr.setValue(config.getEpsPrime());
        m_calcEpsPrimeMean.setSelected(config.getCalcEpsPrimeMean());
        m_calcEpsPrimeMedian.setSelected(config.getCalcEpsPrimeMedian());
        m_manualEpsPrime.setSelected(config.getManualEpsPrime());
        m_generateImageCheckBox.setSelected(config.getGenerateImage());
        m_displayFullscreenButtonCheckBox.setSelected(config.getDisplayFullscreenButton());
        m_resizeViewToWindow.setSelected(config.getResizeToWindow());
        m_enableViewConfigCheckBox.setSelected(config.getEnableViewConfiguration());
        m_enableTitleChangeCheckBox.setSelected(config.getEnableTitleChange());
        m_enableEpsilonPrimeChangeCheckBox.setSelected(config.getEnableEpsilonPrimeChange());
        m_enableSelectionCheckBox.setSelected(config.getEnableSelection());
        m_allowRectangleSelectionCheckBox.setSelected(config.getEnableRectangleSelection());
        m_publishSelectionCheckBox.setSelected(config.getPublishSelection());
        m_subscribeSelectionCheckBox.setSelected(config.getSubscribeSelection());
        m_enableShowSelectedOnlyCheckBox.setSelected(config.getEnableShowSelectedOnly());
        m_chartTitleTextField.setText(config.getChartTitle());
        m_chartSubTitleTextField.setText(config.getChartSubtitle());
        m_maxRowsSpinner.setValue(config.getMaxRows());
        m_imageWidthSpinner.setValue(config.getImageWidth());
        m_imageHeightSpinner.setValue(config.getImageHeight());
        m_showWarningInViewCheckBox.setSelected(config.getShowWarningInView());
        setEpsCalcMethod(config.getEpsCalcMethod());
        enableViewControls();
        enableSelectionControls();
        getPanel().repaint();
    }

    private void setEpsCalcMethod(final String method) {
        switch (method) {
            case OPTICSAssignerViewConfig.CFG_CALC_EPS_PRIME_MEDIAN:
                m_epsGroup.setSelectedIndex(1);
                break;
            case OPTICSAssignerViewConfig.CFG_MANUAL_EPS_PRIME:
                m_epsGroup.setSelectedIndex(2);
                break;
            case OPTICSAssignerViewConfig.CFG_CALC_EPS_PRIME_MEAN:
            default:
                m_epsGroup.setSelectedIndex(0);
                break;
        }
    }

    private String getEpsCalcMethod() {
        switch (m_epsGroup.getSelectedIndex()) {
            case 0:
                return OPTICSAssignerViewConfig.CFG_CALC_EPS_PRIME_MEAN;
            case 1:
                return OPTICSAssignerViewConfig.CFG_CALC_EPS_PRIME_MEDIAN;
            case 2:
                return OPTICSAssignerViewConfig.CFG_MANUAL_EPS_PRIME;
        }
        return null;
    }
}
