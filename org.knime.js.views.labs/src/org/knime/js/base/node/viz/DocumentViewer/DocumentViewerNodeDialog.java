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
 *   Oct 19, 2018 (dewi): created
 */
package org.knime.js.base.node.viz.DocumentViewer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DoubleValue;
import org.knime.core.data.NominalValue;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeDialog;
import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.defaultnodesettings.DialogComponentNumber;
import org.knime.core.node.defaultnodesettings.SettingsModelIntegerBounded;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.util.ColumnSelectionPanel;
import org.knime.core.node.util.DataValueColumnFilter;
import org.knime.ext.textprocessing.data.DocumentValue;
import org.knime.js.core.settings.DialogUtil;

/**
 * The {@link NodeDialog} for the Brat Document Viewer node.
 *
 * @author Andisa Dewi, KNIME AG, Berlin, Germany
 */
public class DocumentViewerNodeDialog extends NodeDialogPane {

    private static final int TEXT_FIELD_SIZE = DialogUtil.DEF_TEXTFIELD_WIDTH;

    private DocumentViewerConfig m_config;

    private JLabel m_warningLabelOptions;
    private JLabel m_warningLabelInteract;

    // Options
    private DialogComponentNumber m_maxRowsSpinner;
    private JTextField m_titleField;
    private JTextField m_subtitleField;
    private ColumnSelectionPanel m_documentColumnSelectionPanel;
    private DialogComponentNumber m_numColsSpinner;
    private ColumnSelectionPanel m_labelColColumnSelectionPanel;
    private JRadioButton m_alignLeftRadioButton;
    private JRadioButton m_alignRightRadioButton;
    private JRadioButton m_alignCenterRadioButton;

    // Interactivity copied from table view dialog
    private JCheckBox m_enablePagingCheckBox;
    private DialogComponentNumber m_initialPageSizeSpinner;
    private JCheckBox m_enablePageSizeChangeCheckBox;
    private JTextField m_allowedPageSizesField;
    private JCheckBox m_enableSelectionCheckbox;
    private JCheckBox m_enableClearSelectionButtonCheckbox;
    private JTextField m_selectionColumnNameField;
    private JCheckBox m_publishSelectionCheckBox;
    private JCheckBox m_subscribeSelectionCheckBox;
    private JCheckBox m_enableHideUnselectedCheckbox;
    private JCheckBox m_hideUnselectedCheckbox;
    private JCheckBox m_publishFilterCheckBox;
    private JCheckBox m_subscribeFilterCheckBox;


    // warnings
    private Component m_whitespace;
    private JLabel m_maxRowsWarning;

    @SuppressWarnings("unchecked")
    DocumentViewerNodeDialog() {
        m_config = new DocumentViewerConfig();

        // copied from table view start
        m_maxRowsSpinner =
            new DialogComponentNumber(new SettingsModelIntegerBounded("maxRows", 1, 0, Integer.MAX_VALUE),
                "", 1, 1, null, true, "Value cannot be negative.");
        m_enablePagingCheckBox = new JCheckBox("Enable pagination");
        m_enablePagingCheckBox.addChangeListener(e -> enablePagingFields());
        m_initialPageSizeSpinner = new DialogComponentNumber(new SettingsModelIntegerBounded("initialPageSize",
            1, 1, Integer.MAX_VALUE), "Initial page size: ", 1, 15, null, true, "Value must be greater than 0.");
        m_enablePageSizeChangeCheckBox = new JCheckBox("Enable page size change control");
        m_enablePageSizeChangeCheckBox.addChangeListener(e -> enablePagingFields());
        m_allowedPageSizesField = new JTextField(TEXT_FIELD_SIZE);
        m_titleField = new JTextField(TEXT_FIELD_SIZE);
        m_subtitleField = new JTextField(TEXT_FIELD_SIZE);
        m_documentColumnSelectionPanel = new ColumnSelectionPanel(
            BorderFactory.createTitledBorder("Choose a document column: "),
            new DataValueColumnFilter(DocumentValue.class), false, false);
        m_enableSelectionCheckbox = new JCheckBox("Enable selection");
        m_enableSelectionCheckbox.addChangeListener(e -> enableSelectionFields());
        m_enableClearSelectionButtonCheckbox = new JCheckBox("Enable 'Clear Selection' button");
        m_selectionColumnNameField = new JTextField(TEXT_FIELD_SIZE);
        m_publishSelectionCheckBox = new JCheckBox("Publish selection events");
        m_subscribeSelectionCheckBox = new JCheckBox("Subscribe to selection events");
        m_hideUnselectedCheckbox = new JCheckBox("Show selected rows only");
        m_enableHideUnselectedCheckbox = new JCheckBox("Enable 'Show selected rows only' option");
        m_publishFilterCheckBox = new JCheckBox("Publish filter events");
        m_subscribeFilterCheckBox = new JCheckBox("Subscribe to filter events");
        // end


        m_labelColColumnSelectionPanel = new ColumnSelectionPanel(
            BorderFactory.createTitledBorder("Choose a title column: "),
            new DataValueColumnFilter(NominalValue.class, DoubleValue.class), true, true);

        m_warningLabelOptions = new JLabel("");
        m_warningLabelOptions.setForeground(Color.RED);
        m_warningLabelOptions.setVisible(false);
        m_warningLabelInteract = new JLabel("");
        m_warningLabelInteract.setForeground(Color.RED);
        m_warningLabelInteract.setVisible(false);

        final JSpinner initSpin =  m_initialPageSizeSpinner.getSpinner();
        final Dimension spinnerDim = new Dimension(TEXT_FIELD_SIZE, 20);
        initSpin.setPreferredSize(spinnerDim);

        // ensure space for warnings without scroll bar
        m_maxRowsWarning = m_maxRowsSpinner.getWarningLabel().orElse(null);
        m_maxRowsWarning.addPropertyChangeListener(e -> changeWhiteSpaceVisibility());
        m_warningLabelOptions.addPropertyChangeListener(e -> changeWhiteSpaceVisibility());
        m_whitespace = Box.createVerticalStrut(25);
        m_whitespace.setVisible(true);

        addTab("Options", initOptions());
        addTab("Interactivity", initInteractivity());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) throws InvalidSettingsException {
        // use spinner values, since if the value is out of range it isn't propagated to the model
        final int maxRows = (int)m_maxRowsSpinner.getSpinner().getValue();
        final int initPageSize = (int)m_initialPageSizeSpinner.getSpinner().getValue();

        // copied from table view start

        m_config.getSettings().getRepresentationSettings().setMaxRows(maxRows);
        m_config.getSettings().getRepresentationSettings().setEnablePaging(m_enablePagingCheckBox.isSelected());
        m_config.getSettings().getRepresentationSettings().setInitialPageSize(initPageSize);
        m_config.getSettings().getRepresentationSettings()
            .setEnablePageSizeChange(m_enablePageSizeChangeCheckBox.isSelected());
        m_config.getSettings().getRepresentationSettings().setAllowedPageSizes(getAllowedPageSizes());
        m_config.getSettings().getRepresentationSettings().setTitle(m_titleField.getText());
        m_config.getSettings().getRepresentationSettings().setSubtitle(m_subtitleField.getText());
        m_config.getSettings().getRepresentationSettings().setEnableSelection(m_enableSelectionCheckbox.isSelected());
        m_config.getSettings().getRepresentationSettings()
            .setEnableClearSelectionButton(m_enableClearSelectionButtonCheckbox.isSelected());
        m_config.getSettings().setSelectionColumnName(m_selectionColumnNameField.getText());
        m_config.getSettings().getValueSettings().setHideUnselected(m_hideUnselectedCheckbox.isSelected());
        m_config.getSettings().getRepresentationSettings()
            .setEnableHideUnselected(m_enableHideUnselectedCheckbox.isSelected());
        m_config.getSettings().getValueSettings().setPublishSelection(m_publishSelectionCheckBox.isSelected());
        m_config.getSettings().getValueSettings().setSubscribeSelection(m_subscribeSelectionCheckBox.isSelected());
        m_config.getSettings().getValueSettings().setPublishFilter(m_publishFilterCheckBox.isSelected());
        m_config.getSettings().getValueSettings().setSubscribeFilter(m_subscribeFilterCheckBox.isSelected());
        m_config.setDocumentCol(m_documentColumnSelectionPanel.getSelectedColumn());
        // end

        m_config.saveSettings(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadSettingsFrom(final NodeSettingsRO settings, final PortObjectSpec[] specs)
        throws NotConfigurableException {
        // copied from table view start
        final DataTableSpec inSpec = (DataTableSpec)specs[0];
        m_config.loadSettingsForDialog(settings, inSpec);
        // use loadSettingsFrom method to ensure listeners are notified to sync model and spinner
        m_maxRowsSpinner.loadSettingsFrom(settings, specs);
        m_enablePagingCheckBox.setSelected(m_config.getSettings().getRepresentationSettings().getEnablePaging());
        m_initialPageSizeSpinner.loadSettingsFrom(settings, specs);
        m_enablePageSizeChangeCheckBox
            .setSelected(m_config.getSettings().getRepresentationSettings().getEnablePageSizeChange());
        m_allowedPageSizesField.setText(
            getAllowedPageSizesString(m_config.getSettings().getRepresentationSettings().getAllowedPageSizes()));
        m_titleField.setText(m_config.getSettings().getRepresentationSettings().getTitle());
        m_subtitleField.setText(m_config.getSettings().getRepresentationSettings().getSubtitle());

        m_enableSelectionCheckbox.setSelected(m_config.getSettings().getRepresentationSettings().getEnableSelection());
        m_enableClearSelectionButtonCheckbox
            .setSelected(m_config.getSettings().getRepresentationSettings().getEnableClearSelectionButton());
        m_selectionColumnNameField.setText(m_config.getSettings().getSelectionColumnName());
        m_hideUnselectedCheckbox.setSelected(m_config.getSettings().getValueSettings().getHideUnselected());
        m_enableHideUnselectedCheckbox
            .setSelected(m_config.getSettings().getRepresentationSettings().getEnableHideUnselected());
        m_publishSelectionCheckBox.setSelected(m_config.getSettings().getValueSettings().getPublishSelection());
        m_subscribeSelectionCheckBox.setSelected(m_config.getSettings().getValueSettings().getSubscribeSelection());
        m_publishFilterCheckBox.setSelected(m_config.getSettings().getValueSettings().getPublishFilter());
        m_subscribeFilterCheckBox.setSelected(m_config.getSettings().getValueSettings().getSubscribeFilter());
        // end
        m_documentColumnSelectionPanel.update(inSpec, m_config.getDocumentCol());

        enableSelectionFields();
        setNumberOfFilters(inSpec);
        m_whitespace.setVisible(true);
    }

    // -- Helper methods --

    private JPanel initOptions() {
        // copied from table view start
        final JPanel pagingPanel = new JPanel(new GridBagLayout());
        pagingPanel.setBorder(new TitledBorder("Paging"));
        final GridBagConstraints gbcP = DialogUtil.defaultGridBagConstraints();
        gbcP.fill = GridBagConstraints.HORIZONTAL;
        gbcP.weightx = 1;
        gbcP.gridwidth = 2;
        pagingPanel.add(m_enablePagingCheckBox, gbcP);
        gbcP.gridy++;
        pagingPanel.add(m_initialPageSizeSpinner.getComponentPanel(), gbcP);
        gbcP.gridx = 0;
        gbcP.gridy++;
        pagingPanel.add(m_warningLabelInteract, gbcP);
        gbcP.gridx = 0;
        gbcP.gridy++;
        pagingPanel.add(m_enablePageSizeChangeCheckBox, gbcP);
        gbcP.gridx = 0;
        gbcP.gridy++;
        gbcP.gridwidth = 1;
        pagingPanel.add(new JLabel("Selectable page sizes: "), gbcP);
        gbcP.gridx++;
        pagingPanel.add(m_allowedPageSizesField, gbcP);
        gbcP.gridx = 0;
        gbcP.gridy++;
        gbcP.gridwidth = 2;

        final ButtonGroup alignment = new ButtonGroup();
        alignment.add(m_alignCenterRadioButton);
        alignment.add(m_alignLeftRadioButton);
        alignment.add(m_alignRightRadioButton);

        // copied from table view start
        final JPanel generalPanel = new JPanel(new GridBagLayout());
        generalPanel.setBorder(new TitledBorder("General Options"));
        final GridBagConstraints gbcG = DialogUtil.defaultGridBagConstraints();
        gbcG.fill = GridBagConstraints.HORIZONTAL;
        gbcG.gridwidth = 1;
        gbcG.gridx = 0;
        gbcG.gridy = 0;
        final JSpinner spin = m_maxRowsSpinner.getSpinner();
        final JLabel l = new JLabel("No. of rows to display: ");
        generalPanel.add(l, gbcG);
        gbcG.gridx++;
        generalPanel.add(spin, gbcG);
        gbcG.gridwidth = 2;
        gbcG.gridx = 0;
        gbcG.gridy++;
        generalPanel.add(m_maxRowsWarning, gbcG);
        gbcG.gridwidth = 1;
        gbcG.gridx = 0;
        gbcG.gridy++;
        generalPanel.add(new JLabel("Title:"), gbcG);
        gbcG.gridx++;
        generalPanel.add(m_titleField, gbcG);
        gbcG.gridx = 0;
        gbcG.gridy++;
        generalPanel.add(new JLabel("Subtitle:"), gbcG);
        gbcG.gridx++;
        generalPanel.add(m_subtitleField, gbcG);

        final JPanel displayPanel = new JPanel(new GridBagLayout());
        displayPanel.setBorder(new TitledBorder("Columns to display: "));
        final GridBagConstraints gbcD = DialogUtil.defaultGridBagConstraints();
        gbcD.weightx = 1;
        gbcD.fill = GridBagConstraints.HORIZONTAL;
        // end
        displayPanel.add(m_documentColumnSelectionPanel, gbcD);

        final JPanel panel = new JPanel(new GridBagLayout());
        final GridBagConstraints gbc = DialogUtil.defaultGridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(pagingPanel, gbc);
        gbc.gridy++;
        panel.add(generalPanel, gbc);
        gbc.gridy++;
        panel.add(displayPanel, gbc);
        gbc.gridy++;
        panel.add(m_whitespace, gbc);
        return panel;
        // end
    }

    private JPanel initInteractivity() {
        final JPanel selectionPanel = new JPanel(new GridBagLayout());
        // section name change not in table view
        selectionPanel.setBorder(new TitledBorder("Selection & Filtering"));
        final GridBagConstraints gbcS = DialogUtil.defaultGridBagConstraints();
        gbcS.fill = GridBagConstraints.HORIZONTAL;
        gbcS.weightx = 1;
        gbcS.gridwidth = 1;
        selectionPanel.add(m_enableSelectionCheckbox, gbcS);
        gbcS.gridx++;
        selectionPanel.add(m_subscribeFilterCheckBox, gbcS);
        gbcS.gridx = 0;
        gbcS.gridy++;
        selectionPanel.add(m_enableClearSelectionButtonCheckbox, gbcS);
        gbcS.gridx++;
        selectionPanel.add(m_hideUnselectedCheckbox, gbcS);
        gbcS.gridx = 0;
        gbcS.gridy++;
        selectionPanel.add(m_enableHideUnselectedCheckbox, gbcS);
        gbcS.gridx++;
        selectionPanel.add(m_publishSelectionCheckBox, gbcS);
        gbcS.gridx = 0;
        gbcS.gridy++;
        selectionPanel.add(m_subscribeSelectionCheckBox, gbcS);
        gbcS.gridx = 0;
        gbcS.gridy++;
        selectionPanel.add(new JLabel("Selection column name: "), gbcS);
        gbcS.gridx++;
        selectionPanel.add(m_selectionColumnNameField, gbcS);
        // end

        final JPanel panel = new JPanel(new GridBagLayout());
        final GridBagConstraints gbc = DialogUtil.defaultGridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(selectionPanel, gbc);
        gbc.gridy++;
        return panel;
    }

    private static String getAllowedPageSizesString(final int[] sizes) {
        if (sizes.length < 1) {
            return "";
        }
        final StringBuilder builder = new StringBuilder(String.valueOf(sizes[0]));
        for (int i = 1; i < sizes.length; i++) {
            builder.append(", ");
            builder.append(sizes[i]);
        }
        return builder.toString();
    }
    
    private void setNumberOfFilters(final DataTableSpec spec) {
        int numFilters = 0;
        for (int i = 0; i < spec.getNumColumns(); i++) {
            if (spec.getColumnSpec(i).getFilterHandler().isPresent()) {
                numFilters++;
            }
        }
        final StringBuilder builder = new StringBuilder("Subscribe to filter events");
        builder.append(" (");
        builder.append(numFilters == 0 ? "no" : numFilters);
        builder.append(numFilters == 1 ? " filter" : " filters");
        builder.append(" available)");
        m_subscribeFilterCheckBox.setText(builder.toString());
    }

    private int[] getAllowedPageSizes() throws InvalidSettingsException {
        final String[] sizesArray = m_allowedPageSizesField.getText().split(",");
        final int[] allowedPageSizes = new int[sizesArray.length];
        try {
            for (int i = 0; i < sizesArray.length; i++) {
                allowedPageSizes[i] = Integer.parseInt(sizesArray[i].trim());
            }
        } catch (final NumberFormatException e) {
            throw new InvalidSettingsException(e.getMessage(), e);
        }
        return allowedPageSizes;
    }

    private void enablePagingFields() {
        final boolean enableGlobal = m_enablePagingCheckBox.isSelected();
        final boolean enableSizeChange = m_enablePageSizeChangeCheckBox.isSelected();
        m_initialPageSizeSpinner.getModel().setEnabled(enableGlobal);
        m_enablePageSizeChangeCheckBox.setEnabled(enableGlobal);
        m_allowedPageSizesField.setEnabled(enableGlobal && enableSizeChange);
    }

    private void enableSelectionFields() {
        final boolean enable = m_enableSelectionCheckbox.isSelected();

        m_enableClearSelectionButtonCheckbox.setEnabled(enable);
        m_hideUnselectedCheckbox.setEnabled(enable);
        m_enableHideUnselectedCheckbox.setEnabled(enable);
        m_publishSelectionCheckBox.setEnabled(enable);
        m_subscribeSelectionCheckBox.setEnabled(enable);
        m_selectionColumnNameField.setEnabled(enable);
    }

    private void changeWhiteSpaceVisibility() {
        if (m_maxRowsWarning.isVisible()
            || m_warningLabelOptions.isVisible()) {
            m_whitespace.setVisible(false);
        } else {
            m_whitespace.setVisible(true);
        }
    }
}
