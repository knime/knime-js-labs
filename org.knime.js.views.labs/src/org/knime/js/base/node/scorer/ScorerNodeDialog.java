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
 *   30 May 2018 (albrecht): created
 */
package org.knime.js.base.node.scorer;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import org.apache.commons.lang3.StringUtils;
import org.knime.base.util.SortingStrategy;
import org.knime.core.data.DataTableSpec;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.defaultnodesettings.DialogComponentColorChooser;
import org.knime.core.node.defaultnodesettings.SettingsModelColor;
import org.knime.core.node.util.ColumnSelectionPanel;
import org.knime.js.core.settings.DialogUtil;

/**
 *
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 */
public class ScorerNodeDialog extends NodeDialogPane {

    private final ScorerConfig m_config;

    private final JCheckBox m_showWarningsCheckBox;
    private final ColumnSelectionPanel m_firstColumnSelection;
    private final ColumnSelectionPanel m_secondColumnSelection;
    private final JComboBox<SortingStrategy> m_sortingStrategyComboBox;
    private final JCheckBox m_reverseSortingCheckBox;
    private final JCheckBox m_ignoreMissingValuesCheckBox;
    private final JTextField m_titleTextField;
    private final JTextField m_subtitleTextField;
    private final JCheckBox m_displayLabelsCheckBox;
    private final JCheckBox m_displayClassNatureCheckBox;
    private final JCheckBox m_displayTotalRowsCheckBox;
    private final JCheckBox m_displayConfusionMatrixRatesCheckBox;
    private final DialogComponentColorChooser m_headerColorChooser;
    private final DialogComponentColorChooser m_diagonalColorChooser;
    private final JCheckBox m_displayFloatAsPercentCheckBox;
    private final JCheckBox m_displayFullscreenButtonCheckBox;

    private final JCheckBox m_displayClassStatsTableCheckBox;
    private final JCheckBox m_classTruePositivesCheckBox;
    private final JCheckBox m_classFalsePositivesCheckBox;
    private final JCheckBox m_classTrueNegativesCheckBox;
    private final JCheckBox m_classFalseNegativesCheckBox;
    private final JCheckBox m_classAccuracyCheckBox;
    private final JCheckBox m_classBalancedAccuracyCheckBox;
    private final JCheckBox m_classErrorRateCheckBox;
    private final JCheckBox m_classFalseNegativeRateCheckBox;
    private final JCheckBox m_classRecallCheckBox;
    private final JCheckBox m_classPrecisionCheckBox;
    private final JCheckBox m_classSensitivityCheckBox;
    private final JCheckBox m_classSpecificityCheckBox;
    private final JCheckBox m_classFMeasureCheckBox;

    private final JCheckBox m_displayOverallStatsTableCheckBox;
    private final JCheckBox m_overallAccuracyCheckBox;
    private final JCheckBox m_overallErrorCheckBox;
    private final JCheckBox m_overallCohensKappaCheckBox;
    private final JCheckBox m_overallCorrectClassifiedCheckBox;
    private final JCheckBox m_overallWrongClassifiedCheckBox;

    private final JCheckBox m_enableViewControlsCheckBox;
    private final JCheckBox m_enableTitleEditingCheckBox;
    private final JCheckBox m_enableSubtitleEditingCheckBox;
    private final JCheckBox m_enableLabelsDisplayConfigCheckBox;
    private final JCheckBox m_enableRowsNumberConfigCheckBox;
    private final JCheckBox m_enableConfusionMatrixRatesCheckBox;
    private final JCheckBox m_enableClassStatisticsConfigCheckBox;
    private final JCheckBox m_enableOverallStatisticsConfigCheckBox;

    /**
     *
     */
    public ScorerNodeDialog() {
        m_config = new ScorerConfig();

        m_showWarningsCheckBox = new JCheckBox("Show warnings in view");
        m_firstColumnSelection = new ColumnSelectionPanel("Actual column");
        m_secondColumnSelection= new ColumnSelectionPanel("Predicted column");
        m_sortingStrategyComboBox = new JComboBox<SortingStrategy>();
        m_sortingStrategyComboBox.addItem(SortingStrategy.Lexical);
        m_sortingStrategyComboBox.addItem(SortingStrategy.InsertionOrder);
        m_reverseSortingCheckBox = new JCheckBox("Reverse Order");
        m_ignoreMissingValuesCheckBox = new JCheckBox("Ignore missing values");
        m_titleTextField = new JTextField(DialogUtil.DEF_TEXTFIELD_WIDTH);
        m_subtitleTextField = new JTextField(DialogUtil.DEF_TEXTFIELD_WIDTH);
        m_displayLabelsCheckBox = new JCheckBox("Display table headers");
        m_displayClassNatureCheckBox = new JCheckBox("Display class nature (Actual or Predicted)");
        m_displayTotalRowsCheckBox = new JCheckBox("Display number of rows");
        m_displayConfusionMatrixRatesCheckBox = new JCheckBox("Display confusion matrix rates");
        m_headerColorChooser = new DialogComponentColorChooser(
            new SettingsModelColor("headerColor", null), "Header color: ", true);
        m_diagonalColorChooser = new DialogComponentColorChooser(
            new SettingsModelColor("diagonalColor", null), "Diagonal color: ", true);
        m_displayFloatAsPercentCheckBox = new JCheckBox("Display float values as percentages");
        m_displayFullscreenButtonCheckBox = new JCheckBox("Display full screen button");

        m_displayClassStatsTableCheckBox = new JCheckBox("Display class statistics table");
        m_classTruePositivesCheckBox = new JCheckBox("True positives");
        m_classFalsePositivesCheckBox = new JCheckBox("False positives");
        m_classTrueNegativesCheckBox = new JCheckBox("True negatives");
        m_classFalseNegativesCheckBox = new JCheckBox("False negatives");
        m_classAccuracyCheckBox = new JCheckBox("Accuracy");
        m_classBalancedAccuracyCheckBox = new JCheckBox("Balanced Accuracy");
        m_classErrorRateCheckBox = new JCheckBox("Error rate");
        m_classFalseNegativeRateCheckBox = new JCheckBox("False negative rate");
        m_classPrecisionCheckBox = new JCheckBox("Precision");
        m_classRecallCheckBox = new JCheckBox("Recall");
        m_classSensitivityCheckBox = new JCheckBox("Sensitivity");
        m_classSpecificityCheckBox = new JCheckBox("Specificity");
        m_classFMeasureCheckBox = new JCheckBox("F-Measure");

        m_displayOverallStatsTableCheckBox = new JCheckBox("Display overall statistics table");
        m_overallAccuracyCheckBox = new JCheckBox("Accuracy");
        m_overallErrorCheckBox = new JCheckBox("Error");
        m_overallCohensKappaCheckBox = new JCheckBox("Cohen's kappa");
        m_overallCorrectClassifiedCheckBox = new JCheckBox("Correctly classified");
        m_overallWrongClassifiedCheckBox = new JCheckBox("Incorrectly classified");

        m_enableViewControlsCheckBox = new JCheckBox("Enable view edit controls");
        m_enableViewControlsCheckBox.addActionListener(action -> enableControlOptions());
        m_enableTitleEditingCheckBox = new JCheckBox("Enable title edit controls");
        m_enableSubtitleEditingCheckBox = new JCheckBox("Enable subtitle edit controls");
        m_enableLabelsDisplayConfigCheckBox = new JCheckBox("Enable display table headers toggle");
        m_enableRowsNumberConfigCheckBox = new JCheckBox("Enable display rows number toggle");
        m_enableConfusionMatrixRatesCheckBox = new JCheckBox("Enable display confusion matrix rates toggle");
        m_enableClassStatisticsConfigCheckBox = new JCheckBox("Enable display class statistics table toggle");
        m_enableOverallStatisticsConfigCheckBox = new JCheckBox("Enable display overall statistics table toggle");

        addTab("Scorer Options", initScorerOptions());
        addTab("Statistics Options", initStatisticsOptions());
        addTab("Control Options", initControlOptions());
    }

    private JPanel initScorerOptions() {
        JPanel titlePanel = new JPanel(new GridBagLayout());
        titlePanel.setBorder(new TitledBorder("Titles"));
        GridBagConstraints gbcT = DialogUtil.defaultGridBagConstraints();
        titlePanel.add(new JLabel("Title: "), gbcT);
        gbcT.gridx++;
        titlePanel.add(m_titleTextField, gbcT);
        gbcT.gridx = 0;
        gbcT.gridy++;
        titlePanel.add(new JLabel("Subtitle: "), gbcT);
        gbcT.gridx++;
        titlePanel.add(m_subtitleTextField, gbcT);

        JPanel columnsPanel = new JPanel(new GridBagLayout());
        columnsPanel.setBorder(new TitledBorder("Columns"));
        GridBagConstraints gbcC = DialogUtil.defaultGridBagConstraints();
        gbcC.gridwidth = 2;
        m_firstColumnSelection.setPreferredSize(new Dimension(300, 50));
        columnsPanel.add(m_firstColumnSelection, gbcC);
        gbcC.gridy++;
        m_secondColumnSelection.setPreferredSize(new Dimension(300, 50));
        columnsPanel.add(m_secondColumnSelection, gbcC);

        JPanel sortingPanel = new JPanel(new GridBagLayout());
        sortingPanel.setBorder(new TitledBorder("General settings"));
        GridBagConstraints gbcS = DialogUtil.defaultGridBagConstraints();
        sortingPanel.add(new JLabel("Sorting strategy: "), gbcS);
        gbcS.gridx++;
        sortingPanel.add(m_sortingStrategyComboBox, gbcS);
        gbcS.gridx++;
        sortingPanel.add(m_reverseSortingCheckBox, gbcS);
        gbcS.gridx = 0;
        gbcS.gridy++;
        gbcS.gridwidth = 3;
        gbcS.anchor = GridBagConstraints.CENTER;
        sortingPanel.add(m_ignoreMissingValuesCheckBox, gbcS);

        JPanel colorPanel = new JPanel(new GridBagLayout());
        colorPanel.setBorder(new TitledBorder("Color settings"));
        GridBagConstraints gbcCo = DialogUtil.defaultGridBagConstraints();
        colorPanel.add(m_headerColorChooser.getComponentPanel(), gbcCo);
        gbcCo.gridx++;
        colorPanel.add(m_diagonalColorChooser.getComponentPanel(), gbcCo);

        JPanel displayPanel = new JPanel(new GridBagLayout());
        displayPanel.setBorder(new TitledBorder("Display settings"));
        GridBagConstraints gbcD = DialogUtil.defaultGridBagConstraints();
        displayPanel.add(m_displayTotalRowsCheckBox, gbcD);
        gbcD.gridx++;
        displayPanel.add(m_displayFloatAsPercentCheckBox, gbcD);
        gbcD.gridx = 0;
        gbcD.gridy++;
        displayPanel.add(m_displayConfusionMatrixRatesCheckBox, gbcD);
        gbcD.gridx++;
        displayPanel.add(m_displayFullscreenButtonCheckBox, gbcD);
        gbcD.gridx = 0;
        gbcD.gridy++;
        displayPanel.add(m_showWarningsCheckBox, gbcD);
        gbcD.gridx++;
        displayPanel.add(m_displayLabelsCheckBox, gbcD);
        gbcD.gridx = 0;
        gbcD.gridy++;
        displayPanel.add(m_displayClassNatureCheckBox, gbcD);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = DialogUtil.defaultGridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(titlePanel, gbc);
        gbc.gridy++;
        panel.add(columnsPanel, gbc);
        gbc.gridy++;
        panel.add(sortingPanel, gbc);
        gbc.gridy++;
        panel.add(colorPanel, gbc);
        gbc.gridy++;
        panel.add(displayPanel, gbc);
        return panel;
    }

    private JPanel initStatisticsOptions() {
        JPanel displayPanel = new JPanel(new GridBagLayout());
        displayPanel.setBorder(new TitledBorder("For each class calculate/display:"));
        GridBagConstraints gbcD = DialogUtil.defaultGridBagConstraints();
        displayPanel.add(m_classTruePositivesCheckBox, gbcD);
        gbcD.gridx++;
        displayPanel.add(m_classFalsePositivesCheckBox, gbcD);
        gbcD.gridx = 0;
        gbcD.gridy++;
        displayPanel.add(m_classTrueNegativesCheckBox, gbcD);
        gbcD.gridx++;
        displayPanel.add(m_classFalseNegativesCheckBox, gbcD);
        gbcD.gridx = 0;
        gbcD.gridy++;
        /*displayPanel.add(m_classAccuracyCheckBox, gbcD);
        gbcD.gridx++;
        displayPanel.add(m_classBalancedAccuracyCheckBox, gbcD);
        gbcD.gridx = 0;
        gbcD.gridy++;
        displayPanel.add(m_classErrorRateCheckBox, gbcD);
        gbcD.gridx++;
        displayPanel.add(m_classFalseNegativeRateCheckBox, gbcD);
        gbcD.gridx = 0;
        gbcD.gridy++;*/
        displayPanel.add(m_classRecallCheckBox, gbcD);
        gbcD.gridx++;
        displayPanel.add(m_classPrecisionCheckBox, gbcD);
        gbcD.gridx = 0;
        gbcD.gridy++;
        displayPanel.add(m_classSensitivityCheckBox, gbcD);
        gbcD.gridx++;
        displayPanel.add(m_classSpecificityCheckBox, gbcD);
        gbcD.gridx = 0;
        gbcD.gridy++;
        displayPanel.add(m_classFMeasureCheckBox, gbcD);

        JPanel overallPanel = new JPanel(new GridBagLayout());
        overallPanel.setBorder(new TitledBorder("Calculate/display for overall statistics:"));
        GridBagConstraints gbcO = DialogUtil.defaultGridBagConstraints();
        overallPanel.add(m_overallAccuracyCheckBox, gbcO);
        gbcO.gridx++;
        overallPanel.add(m_overallErrorCheckBox, gbcO);
        gbcO.gridx = 0;
        gbcO.gridy++;
        overallPanel.add(m_overallCorrectClassifiedCheckBox, gbcO);
        gbcO.gridx++;
        overallPanel.add(m_overallWrongClassifiedCheckBox, gbcO);
        gbcO.gridx = 0;
        gbcO.gridy++;
        overallPanel.add(m_overallCohensKappaCheckBox, gbcO);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = DialogUtil.defaultGridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(m_displayClassStatsTableCheckBox, gbc);
        gbc.gridy++;
        panel.add(displayPanel, gbc);
        gbc.gridy++;
        panel.add(new JLabel(), gbc);
        gbc.gridy++;
        panel.add(m_displayOverallStatsTableCheckBox, gbc);
        gbc.gridy++;
        panel.add(overallPanel, gbc);
        return panel;
    }

    private JPanel initControlOptions() {
        JPanel menuPanel = new JPanel(new GridBagLayout());
        menuPanel.setBorder(new TitledBorder("View menu options"));
        GridBagConstraints gbcM = DialogUtil.defaultGridBagConstraints();
        menuPanel.add(m_enableTitleEditingCheckBox, gbcM);
        gbcM.gridy++;
        menuPanel.add(m_enableSubtitleEditingCheckBox, gbcM);
        gbcM.gridx = 0;
        gbcM.gridy++;
        menuPanel.add(m_enableLabelsDisplayConfigCheckBox, gbcM);
        gbcM.gridy++;
        menuPanel.add(m_enableRowsNumberConfigCheckBox, gbcM);
        gbcM.gridy++;
        menuPanel.add(m_enableConfusionMatrixRatesCheckBox, gbcM);
        gbcM.gridx = 0;
        gbcM.gridy++;
        menuPanel.add(m_enableClassStatisticsConfigCheckBox, gbcM);
        gbcM.gridy++;
        menuPanel.add(m_enableOverallStatisticsConfigCheckBox, gbcM);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = DialogUtil.defaultGridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(m_enableViewControlsCheckBox, gbc);
        gbc.gridy++;
        panel.add(menuPanel, gbc);
        return panel;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) throws InvalidSettingsException {
        m_config.setShowWarningsInView(m_showWarningsCheckBox.isSelected());
        m_config.setFirstColumn(m_firstColumnSelection.getSelectedColumn());
        m_config.setSecondColumn(m_secondColumnSelection.getSelectedColumn());
        m_config.setSortingStragegy((SortingStrategy)m_sortingStrategyComboBox.getSelectedItem());
        m_config.setReverseOrder(m_reverseSortingCheckBox.isSelected());
        m_config.setIgnoreMissingValues(m_ignoreMissingValuesCheckBox.isSelected());
        m_config.setTitle(m_titleTextField.getText());
        m_config.setSubtitle(m_subtitleTextField.getText());
        m_config.setDisplayLabels(m_displayLabelsCheckBox.isSelected());
        m_config.setDisplayClassNature(m_displayClassNatureCheckBox.isSelected());
        m_config.setDisplayTotalRows(m_displayTotalRowsCheckBox.isSelected());
        m_config.setDisplayConfusionMatrixRates(m_displayConfusionMatrixRatesCheckBox.isSelected());
        m_config.setHeaderColor(m_headerColorChooser.getColor());
        m_config.setDiagonalColor(m_diagonalColorChooser.getColor());
        m_config.setDisplayFloatAsPercent(m_displayFloatAsPercentCheckBox.isSelected());
        m_config.setDisplayFullscreenButton(m_displayFullscreenButtonCheckBox.isSelected());
        m_config.setDisplayClassStatsTable(m_displayClassStatsTableCheckBox.isSelected());
        m_config.setClassTruePositives(m_classTruePositivesCheckBox.isSelected());
        m_config.setClassFalsePositives(m_classFalsePositivesCheckBox.isSelected());
        m_config.setClassTrueNegatives(m_classTrueNegativesCheckBox.isSelected());
        m_config.setClassFalseNegatives(m_classFalseNegativesCheckBox.isSelected());
        /*m_config.setClassAccuracy(m_classAccuracyCheckBox.isSelected());
        m_config.setClassBalancedAccuracy(m_classBalancedAccuracyCheckBox.isSelected());
        m_config.setClassErrorRate(m_classErrorRateCheckBox.isSelected());
        m_config.setClassFalseNegativeRate(m_classFalseNegativeRateCheckBox.isSelected());*/
        m_config.setClassRecall(m_classRecallCheckBox.isSelected());
        m_config.setClassPrecision(m_classPrecisionCheckBox.isSelected());
        m_config.setClassSensitivity(m_classSensitivityCheckBox.isSelected());
        m_config.setClassSpecificity(m_classSpecificityCheckBox.isSelected());
        m_config.setClassFMeasure(m_classFMeasureCheckBox.isSelected());
        m_config.setDisplayOverallStats(m_displayOverallStatsTableCheckBox.isSelected());
        m_config.setOverallAccuracy(m_overallAccuracyCheckBox.isSelected());
        m_config.setOverallError(m_overallErrorCheckBox.isSelected());
        m_config.setOverallCohensKappa(m_overallCohensKappaCheckBox.isSelected());
        m_config.setOverallCorrectClassified(m_overallCorrectClassifiedCheckBox.isSelected());
        m_config.setOverallWrongClassified(m_overallWrongClassifiedCheckBox.isSelected());
        m_config.setEnableViewControls(m_enableViewControlsCheckBox.isSelected());
        m_config.setEnableTitleEditing(m_enableTitleEditingCheckBox.isSelected());
        m_config.setEnableSubtitleEditing(m_enableSubtitleEditingCheckBox.isSelected());
        m_config.setEnableLabelsDisplayConfig(m_enableLabelsDisplayConfigCheckBox.isSelected());
        m_config.setEnableRowsNumberConfig(m_enableRowsNumberConfigCheckBox.isSelected());
        m_config.setEnableConfusionMatrixRatesConfig(m_enableConfusionMatrixRatesCheckBox.isSelected());
        m_config.setEnableClassStatisticsConfig(m_enableClassStatisticsConfigCheckBox.isSelected());
        m_config.setEnableOverallStatisticsConfig(m_enableOverallStatisticsConfigCheckBox.isSelected());

        m_config.saveSettings(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadSettingsFrom(final NodeSettingsRO settings, final DataTableSpec[] specs) throws NotConfigurableException {
        DataTableSpec spec = specs[0];
        if (spec.getNumColumns() < 2) {
            throw new NotConfigurableException("The input needs to contain at least two columns for comparison");
        }
        m_config.loadSettingsForDialog(settings, specs[0]);
        m_showWarningsCheckBox.setSelected(m_config.getShowWarningsInView());
        String firstColName = m_config.getFirstColumn();
        String secondColName = m_config.getSecondColumn();
        if (StringUtils.isEmpty(firstColName) && StringUtils.isEmpty(secondColName)) {
            String[] colNames = guessColumnNames(spec);
            firstColName = colNames[0];
            secondColName = colNames[1];
        }
        m_firstColumnSelection.update(spec, firstColName, false, true);
        m_secondColumnSelection.update(spec, secondColName, false, true);
        m_sortingStrategyComboBox.setSelectedItem(m_config.getSortingStragegy());
        m_reverseSortingCheckBox.setSelected(m_config.isReverseOrder());
        m_ignoreMissingValuesCheckBox.setSelected(m_config.isIgnoreMissingValues());
        m_titleTextField.setText(m_config.getTitle());
        m_subtitleTextField.setText(m_config.getSubtitle());
        m_displayLabelsCheckBox.setSelected(m_config.isDisplayLabels());
        m_displayClassNatureCheckBox.setSelected(m_config.isDisplayClassNature());
        m_displayTotalRowsCheckBox.setSelected(m_config.isDisplayTotalRows());
        m_displayConfusionMatrixRatesCheckBox.setSelected(m_config.isDisplayConfusionMatrixRates());
        m_headerColorChooser.setColor(m_config.getHeaderColor());
        m_diagonalColorChooser.setColor(m_config.getDiagonalColor());
        m_displayFloatAsPercentCheckBox.setSelected(m_config.isDisplayFloatAsPercent());
        m_displayFullscreenButtonCheckBox.setSelected(m_config.isDisplayFullscreenButton());
        m_displayClassStatsTableCheckBox.setSelected(m_config.isDisplayClassStatsTable());
        m_classTruePositivesCheckBox.setSelected(m_config.isClassTruePositives());
        m_classFalsePositivesCheckBox.setSelected(m_config.isClassFalsePositives());
        m_classTrueNegativesCheckBox.setSelected(m_config.isClassTrueNegatives());
        m_classFalseNegativesCheckBox.setSelected(m_config.isClassFalseNegatives());
        /*m_classAccuracyCheckBox.setSelected(m_config.isClassAccuracy());
        m_classBalancedAccuracyCheckBox.setSelected(m_config.isClassBalancedAccuracy());
        m_classErrorRateCheckBox.setSelected(m_config.isClassErrorRate());
        m_classFalseNegativeRateCheckBox.setSelected(m_config.isClassFalseNegativeRate());*/
        m_classRecallCheckBox.setSelected(m_config.isClassRecall());
        m_classPrecisionCheckBox.setSelected(m_config.isClassPrecision());
        m_classSensitivityCheckBox.setSelected(m_config.isClassSensitivity());
        m_classSpecificityCheckBox.setSelected(m_config.isClassSpecificity());
        m_classFMeasureCheckBox.setSelected(m_config.isClassFMeasure());
        m_displayOverallStatsTableCheckBox.setSelected(m_config.isDisplayOverallStats());
        m_overallAccuracyCheckBox.setSelected(m_config.isOverallAccuracy());
        m_overallErrorCheckBox.setSelected(m_config.isOverallError());
        m_overallCohensKappaCheckBox.setSelected(m_config.isOverallCohensKappa());
        m_overallCorrectClassifiedCheckBox.setSelected(m_config.isOverallCorrectClassified());
        m_overallWrongClassifiedCheckBox.setSelected(m_config.isOverallWrongClassified());
        m_enableViewControlsCheckBox.setSelected(m_config.isEnableViewControls());
        m_enableTitleEditingCheckBox.setSelected(m_config.isEnableTitleEditing());
        m_enableSubtitleEditingCheckBox.setSelected(m_config.isEnableSubtitleEditing());
        m_enableLabelsDisplayConfigCheckBox.setSelected(m_config.isEnableLabelsDisplayConfig());
        m_enableRowsNumberConfigCheckBox.setSelected(m_config.isEnableRowsNumberConfig());
        m_enableConfusionMatrixRatesCheckBox.setSelected(m_config.isEnableConfusionMatrixRatesConfig());
        m_enableClassStatisticsConfigCheckBox.setSelected(m_config.isEnableClassStatisticsConfig());
        m_enableOverallStatisticsConfigCheckBox.setSelected(m_config.isEnableOverallStatisticsConfig());

        enableControlOptions();
    }

    private static String[] guessColumnNames(final DataTableSpec spec) {
        String[] cols = spec.getColumnNames();
        for (String firstCol : cols) {
            for (String secondCol : cols) {
                //See if there is a column + prediction combination in the spec
                if (secondCol.equals("Prediction (" + firstCol + ")")) {
                    return new String[] {firstCol, secondCol};
                }
            }
        }
        // otherwise naively return the last two columns
        return new String[] {cols[cols.length - 2], cols[cols.length - 1]};
    }

    private void enableControlOptions() {
        boolean enable = m_enableViewControlsCheckBox.isSelected();
        m_enableTitleEditingCheckBox.setEnabled(enable);
        m_enableSubtitleEditingCheckBox.setEnabled(enable);
        m_enableRowsNumberConfigCheckBox.setEnabled(enable);
        m_enableConfusionMatrixRatesCheckBox.setEnabled(enable);
        m_enableClassStatisticsConfigCheckBox.setEnabled(enable);
        m_enableOverallStatisticsConfigCheckBox.setEnabled(enable);
    }

}
