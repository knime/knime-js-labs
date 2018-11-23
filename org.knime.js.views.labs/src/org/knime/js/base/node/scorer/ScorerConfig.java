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

import java.awt.Color;

import org.knime.base.util.SortingStrategy;
import org.knime.core.data.DataTableSpec;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.js.core.CSSUtils;

/**
 *
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 */
public class ScorerConfig {

    private final static String CFG_HIDE_IN_WIZARD = "hideInWizard";
    private final static boolean DEFAULT_HIDE_IN_WIZARD = false;
    private boolean m_hideInWizard = DEFAULT_HIDE_IN_WIZARD;

    static final String CFG_WARNINGS_IN_VIEW = "warningsInView";
    private final static boolean DEFAULT_WARNINGS_IN_VIEW = true;
    private boolean m_showWarningsInView = DEFAULT_WARNINGS_IN_VIEW;

    private static final String CFG_REPORT_MISSING_VALUES = "reportMissingValues";
    private final static boolean DEFAULT_REPORT_MISSING_VALUES = true;
    private boolean m_reportMissingValues = DEFAULT_REPORT_MISSING_VALUES;

    private static final String CFG_FIRST_COLUMN = "firstColumn";
    private final static String DEFAULT_FIRST_COLUMN = "";
    private String m_firstColumn = DEFAULT_FIRST_COLUMN;

    private static final String CFG_SECOND_COLUMN = "secondColumn";
    private final static String DEFAULT_SECOND_COLUMN = "";
    private String m_secondColumn = DEFAULT_SECOND_COLUMN;

    private static final String CFG_SORTING_STRATEGY = "sortingStrategy";
    private final static SortingStrategy DEFAULT_SORTING_STRATEGY = SortingStrategy.Lexical;
    private SortingStrategy m_sortingStragegy = DEFAULT_SORTING_STRATEGY;

    private static final String CFG_REVERSE_ORDER = "reverseOrder";
    private final static boolean DEFAULT_REVERSE_ORDER = false;
    private boolean m_reverseOrder = DEFAULT_REVERSE_ORDER;

    private static final String CFG_IGNORE_MISSING_VALUES = "ignoreMissingValues";
    private final static boolean DEFAULT_IGNORE_MISSING_VALUES = true;
    private boolean m_ignoreMissingValues = DEFAULT_IGNORE_MISSING_VALUES;

    static final String CFG_TITLE = "title";
    private final static String DEFAULT_TITLE = "Scorer View";
    private String m_title = DEFAULT_TITLE;

    static final String CFG_SUBTITLE = "subtitle";
    private final static String DEFAULT_SUBTITLE = "";
    private String m_subtitle = DEFAULT_SUBTITLE;

    static final String CFG_DISPLAY_LABELS = "displayLabels";
    private final static boolean DEFAULT_DISPLAY_LABELS = true;
    private boolean m_displayLabels = DEFAULT_DISPLAY_LABELS;

    static final String CFG_DISPLAY_CLASS_NATURE = "displayClassNature";
    private final static boolean DEFAULT_DISPLAY_CLASS_NATURE = true;
    private boolean m_displayClassNature = DEFAULT_DISPLAY_CLASS_NATURE;

    static final String CFG_DISPLAY_TOTAL_ROWS = "displayTotalRows";
    private final static boolean DEFAULT_DISPLAY_TOTAL_ROWS = false;
    private boolean m_displayTotalRows = DEFAULT_DISPLAY_TOTAL_ROWS;

    static final String CFG_DISPLAY_CONFUSION_MATRIX_RATES = "displayConfusionMatrixRates";
    private final static boolean DEFAULT_DISPLAY_CONFUSION_MATRIX_RATES = true;
    private boolean m_displayConfusionMatrixRates = DEFAULT_DISPLAY_CONFUSION_MATRIX_RATES;

    static final String CFG_HEADER_COLOR = "headerColor";
    private final static Color DEFAULT_HEADER_COLOR = new Color(255, 240, 199);
    private Color m_headerColor = DEFAULT_HEADER_COLOR;

    static final String CFG_DIAGONAL_COLOR = "diagonalColor";
    private final static Color DEFAULT_DIAGONAL_COLOR = new Color(166, 192, 197);
    private Color m_diagonalColor = DEFAULT_DIAGONAL_COLOR;

    static final String CFG_DISPLAY_FLOAT_AS_PERCENT = "displayFloatAsPercent";
    private final static boolean DEFAULT_DISPLAY_FLOAT_AS_PERCENT = true;
    private boolean m_displayFloatAsPercent = DEFAULT_DISPLAY_FLOAT_AS_PERCENT;

    static final String CFG_DISPLAY_FULLSCREEN_BUTTON = "displayFullscreenButton";
    private final static boolean DEFAULT_DISPLAY_FULLSCREEN_BUTTON = true;
    private boolean m_displayFullscreenButton = DEFAULT_DISPLAY_FULLSCREEN_BUTTON;

    static final String CFG_DISPLAY_CLASS_STATS_TABLE = "displayClassStatsTable";
    private final static boolean DEFAULT_DISPLAY_CLASS_STATS_TABLE = false;
    private boolean m_displayClassStatsTable = DEFAULT_DISPLAY_CLASS_STATS_TABLE;

    private static final String CFG_CLASS_TRUE_POSITIVES = "classTruePositives";
    private final static boolean DEFAULT_CLASS_TRUE_POSITIVES = true;
    private boolean m_classTruePositives = DEFAULT_CLASS_TRUE_POSITIVES;

    private static final String CFG_CLASS_FALSE_POSITIVES = "classFalsePositives";
    private final static boolean DEFAULT_CLASS_FALSE_POSITIVES = true;
    private boolean m_classFalsePositives = DEFAULT_CLASS_FALSE_POSITIVES;

    private static final String CFG_CLASS_TRUE_NEGATIVES = "classTrueNegatives";
    private final static boolean DEFAULT_CLASS_TRUE_NEGATIVES = true;
    private boolean m_classTrueNegatives = DEFAULT_CLASS_TRUE_NEGATIVES;

    private static final String CFG_CLASS_FALSE_NEGATIVES = "classFalseNegatives";
    private final static boolean DEFAULT_CLASS_FALSE_NEGATIVES = true;
    private boolean m_classFalseNegatives = DEFAULT_CLASS_FALSE_NEGATIVES;

    private static final String CFG_CLASS_ACCURACY = "classAccuracy";
    private final static boolean DEFAULT_CLASS_ACCURACY = false;
    private boolean m_classAccuracy = DEFAULT_CLASS_ACCURACY;

    private static final String CFG_CLASS_BALANCED_ACCURACY = "classBalancedAccuracy";
    private final static boolean DEFAULT_CLASS_BALANCED_ACCURACY = false;
    private boolean m_classBalancedAccuracy = DEFAULT_CLASS_BALANCED_ACCURACY;

    private static final String CFG_CLASS_ERROR_RATE = "classErrorRate";
    private final static boolean DEFAULT_CLASS_ERROR_RATE = false;
    private boolean m_classErrorRate = DEFAULT_CLASS_ERROR_RATE;

    private static final String CFG_CLASS_FALSE_NEGATIVE_RATE = "classFalseNegativeRate";
    private final static boolean DEFAULT_CLASS_FALSE_NEGATIVE_RATE = false;
    private boolean m_classFalseNegativeRate = DEFAULT_CLASS_FALSE_NEGATIVE_RATE;

    private static final String CFG_CLASS_RECALL = "classRecall";
    private final static boolean DEFAULT_CLASS_RECALL = true;
    private boolean m_classRecall = DEFAULT_CLASS_RECALL;

    private static final String CFG_CLASS_PRECISION = "classPrecision";
    private final static boolean DEFAULT_CLASS_PRECISION = true;
    private boolean m_classPrecision = DEFAULT_CLASS_PRECISION;

    private static final String CFG_CLASS_SENSITIVITY = "classSensitivity";
    private final static boolean DEFAULT_CLASS_SENSITIVITY = true;
    private boolean m_classSensitivity = DEFAULT_CLASS_SENSITIVITY;

    private static final String CFG_CLASS_SPECIFICITY = "classSpecificity";
    private final static boolean DEFAULT_CLASS_SPECIFICITY = true;
    private boolean m_classSpecificity = DEFAULT_CLASS_SPECIFICITY;

    private static final String CFG_CLASS_F_MEASURE = "classFMeasure";
    private final static boolean DEFAULT_CLASS_F_MEASURE = true;
    private boolean m_classFMeasure = DEFAULT_CLASS_F_MEASURE;

    static final String CFG_DISPLAY_OVERALL_STATS = "displayOverallStats";
    private final static boolean DEFAULT_DISPLAY_OVERALL_STATS = true;
    private boolean m_displayOverallStats = DEFAULT_DISPLAY_OVERALL_STATS;

    private static final String CFG_OVERALL_ACCURACY = "overallAccuracy";
    private final static boolean DEFAULT_OVERALL_ACCURACY = true;
    private boolean m_overallAccuracy = DEFAULT_OVERALL_ACCURACY;

    private static final String CFG_OVERALL_ERROR = "overallError";
    private final static boolean DEFAULT_OVERALL_ERROR = true;
    private boolean m_overallError = DEFAULT_OVERALL_ERROR;

    private static final String CFG_OVERALL_COHENS_KAPPA = "overallCohensKappa";
    private final static boolean DEFAULT_OVERALL_COHENS_KAPPA = true;
    private boolean m_overallCohensKappa = DEFAULT_OVERALL_COHENS_KAPPA;

    private static final String CFG_OVERALL_CORRECT_CLASSIFIED = "overallCorrectClassified";
    private final static boolean DEFAULT_OVERALL_CORRECT_CLASSIFIED = true;
    private boolean m_overallCorrectClassified = DEFAULT_OVERALL_CORRECT_CLASSIFIED;

    private static final String CFG_OVERALL_WRONG_CLASSIFIED = "overallWrongClassified";
    private final static boolean DEFAULT_OVERALL_WRONG_CLASSIFIED = true;
    private boolean m_overallWrongClassified = DEFAULT_OVERALL_WRONG_CLASSIFIED;

    static final String CFG_ENABLE_VIEW_CONTROLS = "enableViewControls";
    private final static boolean DEFAULT_ENABLE_VIEW_CONTROLS = true;
    private boolean m_enableViewControls = DEFAULT_ENABLE_VIEW_CONTROLS;

    static final String CFG_ENABLE_TITLE_EDITING = "enableTitleEditing";
    private final static boolean DEFAULT_ENABLE_TITLE_EDITING = true;
    private boolean m_enableTitleEditing = DEFAULT_ENABLE_TITLE_EDITING;

    static final String CFG_ENABLE_SUBTITLE_EDITING = "enableSubtitleEditing";
    private final static boolean DEFAULT_ENABLE_SUBTITLE_EDITING = true;
    private boolean m_enableSubtitleEditing = DEFAULT_ENABLE_SUBTITLE_EDITING;

    static final String CFG_ENABLE_LABELS_DISPLAY_CONFIG = "enableLabelsDisplayConfig";
    private final static boolean DEFAULT_ENABLE_LABELS_DISPLAY_CONFIG = true;
    private boolean m_enableLabelsDisplayConfig = DEFAULT_ENABLE_LABELS_DISPLAY_CONFIG;

    static final String CFG_ENABLE_ROWS_NUMBER_CONFIG = "enableRowsNumberConfig";
    private final static boolean DEFAULT_ENABLE_ROWS_NUMBER_CONFIG = true;
    private boolean m_enableRowsNumberConfig = DEFAULT_ENABLE_ROWS_NUMBER_CONFIG;

    static final String CFG_ENABLE_CONFUSION_MATRIX_RATES_CONFIG = "enableConfusionMatrixRatesConfig";
    private final static boolean DEFAULT_ENABLE_CONFUSION_MATRIX_RATES_CONFIG = true;
    private boolean m_enableConfusionMatrixRatesConfig = DEFAULT_ENABLE_CONFUSION_MATRIX_RATES_CONFIG;

    static final String CFG_ENABLE_CLASS_STATISTICS_CONFIG = "enableClassStatisticsConfig";
    private final static boolean DEFAULT_ENABLE_CLASS_STATISTICS_CONFIG = true;
    private boolean m_enableClassStatisticsConfig = DEFAULT_ENABLE_CLASS_STATISTICS_CONFIG;

    static final String CFG_ENABLE_OVERALL_STATISTICS_CONFIG = "enableOverallStatisticsConfig";
    private final static boolean DEFAULT_ENABLE_OVERALL_STATISTICS_CONFIG = true;
    private boolean m_enableOverallStatisticsConfig = DEFAULT_ENABLE_OVERALL_STATISTICS_CONFIG;

    static final String CFG_CUSTOM_CSS = "customCSS";
    private final static String DEFAULT_CUSTOM_CSS = "";
    private String m_customCSS = DEFAULT_CUSTOM_CSS;

    /**
     * @return the hideInWizard
     */
    public boolean getHideInWizard() {
        return m_hideInWizard;
    }

    /**
     * @param hideInWizard the hideInWizard to set
     */
    public void setHideInWizard(final boolean hideInWizard) {
        m_hideInWizard = hideInWizard;
    }

    /**
     * @return the showWarningsInView
     */
    public boolean getShowWarningsInView() {
        return m_showWarningsInView;
    }

    /**
     * @param showWarningsInView the showWarningsInView to set
     */
    public void setShowWarningsInView(final boolean showWarningsInView) {
        m_showWarningsInView = showWarningsInView;
    }

    /**
     * @return the reportMissingValues
     */
    public boolean getReportMissingValues() {
        return m_reportMissingValues;
    }

    /**
     * @param reportMissingValues the reportMissingValues to set
     */
    public void setReportMissingValues(final boolean reportMissingValues) {
        m_reportMissingValues = reportMissingValues;
    }

    /**
     * @return the firstColumn
     */
    public String getFirstColumn() {
        return m_firstColumn;
    }

    /**
     * @param firstColumn the firstColumn to set
     */
    public void setFirstColumn(final String firstColumn) {
        m_firstColumn = firstColumn;
    }

    /**
     * @return the secondColumn
     */
    public String getSecondColumn() {
        return m_secondColumn;
    }

    /**
     * @param secondColumn the secondColumn to set
     */
    public void setSecondColumn(final String secondColumn) {
        m_secondColumn = secondColumn;
    }

    /**
     * @return the sortingStragegy
     */
    public SortingStrategy getSortingStragegy() {
        return m_sortingStragegy;
    }

    /**
     * @param sortingStragegy the sortingStragegy to set
     */
    public void setSortingStragegy(final SortingStrategy sortingStragegy) {
        m_sortingStragegy = sortingStragegy;
    }

    /**
     * @return the reverseOrder
     */
    public boolean isReverseOrder() {
        return m_reverseOrder;
    }

    /**
     * @param reverseOrder the reverseOrder to set
     */
    public void setReverseOrder(final boolean reverseOrder) {
        m_reverseOrder = reverseOrder;
    }

    /**
     * @return the ignoreMissingValues
     */
    public boolean isIgnoreMissingValues() {
        return m_ignoreMissingValues;
    }

    /**
     * @param ignoreMissingValues the ignoreMissingValues to set
     */
    public void setIgnoreMissingValues(final boolean ignoreMissingValues) {
        m_ignoreMissingValues = ignoreMissingValues;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return m_title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(final String title) {
        m_title = title;
    }

    /**
     * @return the subtitle
     */
    public String getSubtitle() {
        return m_subtitle;
    }

    /**
     * @param subtitle the subtitle to set
     */
    public void setSubtitle(final String subtitle) {
        m_subtitle = subtitle;
    }

    /**
     * @return the displayLabels
     */
    public boolean isDisplayLabels() {
        return m_displayLabels;
    }

    /**
     * @param displayLabels the displayLabels to set
     */
    public void setDisplayLabels(final boolean displayLabels) {
        m_displayLabels = displayLabels;
    }

    /**
     * @return the displayClassNature
     */
    public boolean isDisplayClassNature() {
        return m_displayClassNature;
    }

    /**
     * @param displayClassNature the displayClassNature to set
     */
    public void setDisplayClassNature(final boolean displayClassNature) {
        m_displayClassNature = displayClassNature;
    }

    /**
     * @return the displayTotalRows
     */
    public boolean isDisplayTotalRows() {
        return m_displayTotalRows;
    }

    /**
     * @param displayTotalRows the displayTotalRows to set
     */
    public void setDisplayTotalRows(final boolean displayTotalRows) {
        m_displayTotalRows = displayTotalRows;
    }

    /**
     * @return the displayConfusionMatrixRates
     */
    public boolean isDisplayConfusionMatrixRates() {
        return m_displayConfusionMatrixRates;
    }

    /**
     * @param displayConfusionMatrixRates the displayConfusionMatrixRates to set
     */
    public void setDisplayConfusionMatrixRates(final boolean displayConfusionMatrixRates) {
        m_displayConfusionMatrixRates = displayConfusionMatrixRates;
    }

    /**
     * @return the headerColor
     */
    public Color getHeaderColor() {
        return m_headerColor;
    }

    /**
     * @param headerColor the headerColor to set
     */
    public void setHeaderColor(final Color headerColor) {
        m_headerColor = headerColor;
    }

    /**
     * @return the diagonalColor
     */
    public Color getDiagonalColor() {
        return m_diagonalColor;
    }

    /**
     * @param diagonalColor the diagonalColor to set
     */
    public void setDiagonalColor(final Color diagonalColor) {
        m_diagonalColor = diagonalColor;
    }

    /**
     * @return the displayFloatAsPercent
     */
    public boolean isDisplayFloatAsPercent() {
        return m_displayFloatAsPercent;
    }

    /**
     * @param displayFloatAsPercent the displayFloatAsPercent to set
     */
    public void setDisplayFloatAsPercent(final boolean displayFloatAsPercent) {
        m_displayFloatAsPercent = displayFloatAsPercent;
    }

    /**
     * @return the displayFullscreenButton
     */
    public boolean isDisplayFullscreenButton() {
        return m_displayFullscreenButton;
    }

    /**
     * @param displayFullscreenButton the displayFullscreenButton to set
     */
    public void setDisplayFullscreenButton(final boolean displayFullscreenButton) {
        m_displayFullscreenButton = displayFullscreenButton;
    }

    /**
     * @return the displayClassStatsTable
     */
    public boolean isDisplayClassStatsTable() {
        return m_displayClassStatsTable;
    }

    /**
     * @param displayClassStatsTable the displayClassStatsTable to set
     */
    public void setDisplayClassStatsTable(final boolean displayClassStatsTable) {
        m_displayClassStatsTable = displayClassStatsTable;
    }

    /**
     * @return the classTruePositives
     */
    public boolean isClassTruePositives() {
        return m_classTruePositives;
    }

    /**
     * @param classTruePositives the classTruePositives to set
     */
    public void setClassTruePositives(final boolean classTruePositives) {
        m_classTruePositives = classTruePositives;
    }

    /**
     * @return the classFalsePositives
     */
    public boolean isClassFalsePositives() {
        return m_classFalsePositives;
    }

    /**
     * @param classFalsePositives the classFalsePositives to set
     */
    public void setClassFalsePositives(final boolean classFalsePositives) {
        m_classFalsePositives = classFalsePositives;
    }

    /**
     * @return the classTrueNegatives
     */
    public boolean isClassTrueNegatives() {
        return m_classTrueNegatives;
    }

    /**
     * @param classTrueNegatives the classTrueNegatives to set
     */
    public void setClassTrueNegatives(final boolean classTrueNegatives) {
        m_classTrueNegatives = classTrueNegatives;
    }

    /**
     * @return the classFalseNegatives
     */
    public boolean isClassFalseNegatives() {
        return m_classFalseNegatives;
    }

    /**
     * @param classFalseNegatives the classFalseNegatives to set
     */
    public void setClassFalseNegatives(final boolean classFalseNegatives) {
        m_classFalseNegatives = classFalseNegatives;
    }

    /**
     * @return the classAccuracy
     */
    public boolean isClassAccuracy() {
        return m_classAccuracy;
    }

    /**
     * @param classAccuracy the classAccuracy to set
     */
    public void setClassAccuracy(final boolean classAccuracy) {
        m_classAccuracy = classAccuracy;
    }

    /**
     * @return the classBalancedAccuracy
     */
    public boolean isClassBalancedAccuracy() {
        return m_classBalancedAccuracy;
    }

    /**
     * @param classBalancedAccuracy the classBalancedAccuracy to set
     */
    public void setClassBalancedAccuracy(final boolean classBalancedAccuracy) {
        m_classBalancedAccuracy = classBalancedAccuracy;
    }

    /**
     * @return the classErrorRate
     */
    public boolean isClassErrorRate() {
        return m_classErrorRate;
    }

    /**
     * @param classErrorRate the classErrorRate to set
     */
    public void setClassErrorRate(final boolean classErrorRate) {
        m_classErrorRate = classErrorRate;
    }

    /**
     * @return the classFalseNegativeRate
     */
    public boolean isClassFalseNegativeRate() {
        return m_classFalseNegativeRate;
    }

    /**
     * @param classFalseNegativeRate the classFalseNegativeRate to set
     */
    public void setClassFalseNegativeRate(final boolean classFalseNegativeRate) {
        m_classFalseNegativeRate = classFalseNegativeRate;
    }

    /**
     * @return the classRecall
     */
    public boolean isClassRecall() {
        return m_classRecall;
    }

    /**
     * @param classRecall the classRecall to set
     */
    public void setClassRecall(final boolean classRecall) {
        m_classRecall = classRecall;
    }

    /**
     * @return the classPrecision
     */
    public boolean isClassPrecision() {
        return m_classPrecision;
    }

    /**
     * @param classPrecision the classPrecision to set
     */
    public void setClassPrecision(final boolean classPrecision) {
        m_classPrecision = classPrecision;
    }

    /**
     * @return the classSensitivity
     */
    public boolean isClassSensitivity() {
        return m_classSensitivity;
    }

    /**
     * @param classSensitivity the classSensitivity to set
     */
    public void setClassSensitivity(final boolean classSensitivity) {
        m_classSensitivity = classSensitivity;
    }

    /**
     * @return the classSpecificity
     */
    public boolean isClassSpecificity() {
        return m_classSpecificity;
    }

    /**
     * @param classSpecificity the classSpecificity to set
     */
    public void setClassSpecificity(final boolean classSpecificity) {
        m_classSpecificity = classSpecificity;
    }

    /**
     * @return the classFMeasure
     */
    public boolean isClassFMeasure() {
        return m_classFMeasure;
    }

    /**
     * @param classFMeasure the classFMeasure to set
     */
    public void setClassFMeasure(final boolean classFMeasure) {
        m_classFMeasure = classFMeasure;
    }

    /**
     * @return the displayOverallStats
     */
    public boolean isDisplayOverallStats() {
        return m_displayOverallStats;
    }

    /**
     * @param displayOverallStats the displayOverallStats to set
     */
    public void setDisplayOverallStats(final boolean displayOverallStats) {
        m_displayOverallStats = displayOverallStats;
    }

    /**
     * @return the overallAccuracy
     */
    public boolean isOverallAccuracy() {
        return m_overallAccuracy;
    }

    /**
     * @param overallAccuracy the overallAccuracy to set
     */
    public void setOverallAccuracy(final boolean overallAccuracy) {
        m_overallAccuracy = overallAccuracy;
    }

    /**
     * @return the overallError
     */
    public boolean isOverallError() {
        return m_overallError;
    }

    /**
     * @param overallError the overallError to set
     */
    public void setOverallError(final boolean overallError) {
        m_overallError = overallError;
    }

    /**
     * @return the overallCohensKappa
     */
    public boolean isOverallCohensKappa() {
        return m_overallCohensKappa;
    }

    /**
     * @param overallCohensKappa the overallCohensKappa to set
     */
    public void setOverallCohensKappa(final boolean overallCohensKappa) {
        m_overallCohensKappa = overallCohensKappa;
    }

    /**
     * @return the overallCorrectClassified
     */
    public boolean isOverallCorrectClassified() {
        return m_overallCorrectClassified;
    }

    /**
     * @param overallCorrectClassified the overallCorrectClassified to set
     */
    public void setOverallCorrectClassified(final boolean overallCorrectClassified) {
        m_overallCorrectClassified = overallCorrectClassified;
    }

    /**
     * @return the overallWrongClassified
     */
    public boolean isOverallWrongClassified() {
        return m_overallWrongClassified;
    }

    /**
     * @param overallWrongClassified the overallWrongClassified to set
     */
    public void setOverallWrongClassified(final boolean overallWrongClassified) {
        m_overallWrongClassified = overallWrongClassified;
    }

    /**
     * @return the enableViewControls
     */
    public boolean isEnableViewControls() {
        return m_enableViewControls;
    }

    /**
     * @param enableViewControls the enableViewControls to set
     */
    public void setEnableViewControls(final boolean enableViewControls) {
        m_enableViewControls = enableViewControls;
    }

    /**
     * @return the enableTitleEditing
     */
    public boolean isEnableTitleEditing() {
        return m_enableTitleEditing;
    }

    /**
     * @param enableTitleEditing the enableTitleEditing to set
     */
    public void setEnableTitleEditing(final boolean enableTitleEditing) {
        m_enableTitleEditing = enableTitleEditing;
    }

    /**
     * @return the enableSubtitleEditing
     */
    public boolean isEnableSubtitleEditing() {
        return m_enableSubtitleEditing;
    }

    /**
     * @param enableSubtitleEditing the enableSubtitleEditing to set
     */
    public void setEnableSubtitleEditing(final boolean enableSubtitleEditing) {
        m_enableSubtitleEditing = enableSubtitleEditing;
    }

    /**
     * @return the enableLabelsDisplayConfig
     */
    public boolean isEnableLabelsDisplayConfig() {
        return m_enableLabelsDisplayConfig;
    }

    /**
     * @param enableLabelsDisplayConfig the enableLabelsDisplayConfig to set
     */
    public void setEnableLabelsDisplayConfig(final boolean enableLabelsDisplayConfig) {
        m_enableLabelsDisplayConfig = enableLabelsDisplayConfig;
    }

    /**
     * @return the enableRowsNumberConfig
     */
    public boolean isEnableRowsNumberConfig() {
        return m_enableRowsNumberConfig;
    }

    /**
     * @param enableRowsNumberConfig the enableRowsNumberConfig to set
     */
    public void setEnableRowsNumberConfig(final boolean enableRowsNumberConfig) {
        m_enableRowsNumberConfig = enableRowsNumberConfig;
    }

    /**
     * @return the enableConfusionMatrixRatesConfig
     */
    public boolean isEnableConfusionMatrixRatesConfig() {
        return m_enableConfusionMatrixRatesConfig;
    }

    /**
     * @param enableConfusionMatrixRatesConfig the enableConfusionMatrixRatesConfig to set
     */
    public void setEnableConfusionMatrixRatesConfig(final boolean enableConfusionMatrixRatesConfig) {
        m_enableConfusionMatrixRatesConfig = enableConfusionMatrixRatesConfig;
    }

    /**
     * @return the enableClassStatisticsConfig
     */
    public boolean isEnableClassStatisticsConfig() {
        return m_enableClassStatisticsConfig;
    }

    /**
     * @param enableClassStatisticsConfig the enableClassStatisticsConfig to set
     */
    public void setEnableClassStatisticsConfig(final boolean enableClassStatisticsConfig) {
        m_enableClassStatisticsConfig = enableClassStatisticsConfig;
    }

    /**
     * @return the enableOverallStatisticsConfig
     */
    public boolean isEnableOverallStatisticsConfig() {
        return m_enableOverallStatisticsConfig;
    }

    /**
     * @param enableOverallStatisticsConfig the enableOverallStatisticsConfig to set
     */
    public void setEnableOverallStatisticsConfig(final boolean enableOverallStatisticsConfig) {
        m_enableOverallStatisticsConfig = enableOverallStatisticsConfig;
    }

    /**
     * @return the customCSS
     */
    public String getCustomCSS() {
        return m_customCSS;
    }

    /**
     * @param customCSS the customCSS to set
     */
    public void setCustomCSS(final String customCSS) {
        m_customCSS = customCSS;
    }

    /**
     * Saves current parameters to settings object.
     *
     * @param settings To save to.
     */
    public void saveSettings(final NodeSettingsWO settings) {
        settings.addBoolean(CFG_HIDE_IN_WIZARD, m_hideInWizard);
        settings.addBoolean(CFG_WARNINGS_IN_VIEW, m_showWarningsInView);
        settings.addBoolean(CFG_REPORT_MISSING_VALUES, m_reportMissingValues);
        settings.addString(CFG_FIRST_COLUMN, m_firstColumn);
        settings.addString(CFG_SECOND_COLUMN, m_secondColumn);
        settings.addString(CFG_SORTING_STRATEGY, m_sortingStragegy.name());
        settings.addBoolean(CFG_REVERSE_ORDER, m_reverseOrder);
        settings.addBoolean(CFG_IGNORE_MISSING_VALUES, m_ignoreMissingValues);
        settings.addString(CFG_TITLE, m_title);
        settings.addString(CFG_SUBTITLE, m_subtitle);
        settings.addBoolean(CFG_DISPLAY_TOTAL_ROWS, m_displayTotalRows);
        settings.addBoolean(CFG_DISPLAY_CONFUSION_MATRIX_RATES, m_displayConfusionMatrixRates);
        settings.addString(CFG_HEADER_COLOR, CSSUtils.cssHexStringFromColor(m_headerColor));
        settings.addString(CFG_DIAGONAL_COLOR, CSSUtils.cssHexStringFromColor(m_diagonalColor));
        settings.addBoolean(CFG_DISPLAY_FLOAT_AS_PERCENT, m_displayFloatAsPercent);
        settings.addBoolean(CFG_DISPLAY_FULLSCREEN_BUTTON, m_displayFullscreenButton);
        settings.addBoolean(CFG_DISPLAY_CLASS_STATS_TABLE, m_displayClassStatsTable);
        settings.addBoolean(CFG_CLASS_TRUE_POSITIVES, m_classTruePositives);
        settings.addBoolean(CFG_CLASS_FALSE_POSITIVES, m_classFalsePositives);
        settings.addBoolean(CFG_CLASS_TRUE_NEGATIVES, m_classTrueNegatives);
        settings.addBoolean(CFG_CLASS_FALSE_NEGATIVES, m_classFalseNegatives);
        settings.addBoolean(CFG_CLASS_ACCURACY, m_classAccuracy);
        settings.addBoolean(CFG_CLASS_BALANCED_ACCURACY, m_classBalancedAccuracy);
        settings.addBoolean(CFG_CLASS_ERROR_RATE, m_classErrorRate);
        settings.addBoolean(CFG_CLASS_FALSE_NEGATIVE_RATE, m_classFalseNegativeRate);
        settings.addBoolean(CFG_CLASS_RECALL, m_classRecall);
        settings.addBoolean(CFG_CLASS_PRECISION, m_classPrecision);
        settings.addBoolean(CFG_CLASS_SENSITIVITY, m_classSensitivity);
        settings.addBoolean(CFG_CLASS_SPECIFICITY, m_classSpecificity);
        settings.addBoolean(CFG_CLASS_F_MEASURE, m_classFMeasure);
        settings.addBoolean(CFG_DISPLAY_OVERALL_STATS, m_displayOverallStats);
        settings.addBoolean(CFG_OVERALL_ACCURACY, m_overallAccuracy);
        settings.addBoolean(CFG_OVERALL_ERROR, m_overallError);
        settings.addBoolean(CFG_OVERALL_COHENS_KAPPA, m_overallCohensKappa);
        settings.addBoolean(CFG_OVERALL_CORRECT_CLASSIFIED, m_overallCorrectClassified);
        settings.addBoolean(CFG_OVERALL_WRONG_CLASSIFIED, m_overallWrongClassified);
        settings.addBoolean(CFG_ENABLE_VIEW_CONTROLS, m_enableViewControls);
        settings.addBoolean(CFG_ENABLE_TITLE_EDITING, m_enableTitleEditing);
        settings.addBoolean(CFG_ENABLE_SUBTITLE_EDITING, m_enableSubtitleEditing);
        settings.addBoolean(CFG_ENABLE_ROWS_NUMBER_CONFIG, m_enableRowsNumberConfig);
        settings.addBoolean(CFG_ENABLE_CONFUSION_MATRIX_RATES_CONFIG, m_enableConfusionMatrixRatesConfig);
        settings.addBoolean(CFG_ENABLE_CLASS_STATISTICS_CONFIG, m_enableClassStatisticsConfig);
        settings.addBoolean(CFG_ENABLE_OVERALL_STATISTICS_CONFIG, m_enableOverallStatisticsConfig);
        settings.addString(CFG_CUSTOM_CSS, m_customCSS);

        //added with 3.7
        settings.addBoolean(CFG_DISPLAY_LABELS, m_displayLabels);
        settings.addBoolean(CFG_DISPLAY_CLASS_NATURE, m_displayClassNature);
        settings.addBoolean(CFG_ENABLE_LABELS_DISPLAY_CONFIG, m_enableLabelsDisplayConfig);
    }

    /**
     * Loads parameters in NodeModel.
     *
     * @param settings To load from.
     * @throws InvalidSettingsException If incomplete or wrong.
     */
    public void loadSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        m_hideInWizard = settings.getBoolean(CFG_HIDE_IN_WIZARD);
        m_showWarningsInView = settings.getBoolean(CFG_WARNINGS_IN_VIEW);
        m_reportMissingValues = settings.getBoolean(CFG_REPORT_MISSING_VALUES);
        m_firstColumn = settings.getString(CFG_FIRST_COLUMN);
        m_secondColumn = settings.getString(CFG_SECOND_COLUMN);
        m_sortingStragegy = SortingStrategy.valueOf(settings.getString(CFG_SORTING_STRATEGY));
        m_reverseOrder = settings.getBoolean(CFG_REVERSE_ORDER);
        m_ignoreMissingValues = settings.getBoolean(CFG_IGNORE_MISSING_VALUES);
        m_title = settings.getString(CFG_TITLE);
        m_subtitle = settings.getString(CFG_SUBTITLE);
        m_displayTotalRows = settings.getBoolean(CFG_DISPLAY_TOTAL_ROWS);
        m_displayConfusionMatrixRates = settings.getBoolean(CFG_DISPLAY_CONFUSION_MATRIX_RATES);
        m_headerColor = CSSUtils.colorFromCssHexString(settings.getString(CFG_HEADER_COLOR));
        m_diagonalColor = CSSUtils.colorFromCssHexString(settings.getString(CFG_DIAGONAL_COLOR));
        m_displayFloatAsPercent = settings.getBoolean(CFG_DISPLAY_FLOAT_AS_PERCENT);
        m_displayFullscreenButton = settings.getBoolean(CFG_DISPLAY_FULLSCREEN_BUTTON);
        m_displayClassStatsTable = settings.getBoolean(CFG_DISPLAY_CLASS_STATS_TABLE);
        m_classTruePositives = settings.getBoolean(CFG_CLASS_TRUE_POSITIVES);
        m_classFalsePositives = settings.getBoolean(CFG_CLASS_FALSE_POSITIVES);
        m_classTrueNegatives = settings.getBoolean(CFG_CLASS_TRUE_NEGATIVES);
        m_classFalseNegatives = settings.getBoolean(CFG_CLASS_FALSE_NEGATIVES);
        m_classAccuracy = settings.getBoolean(CFG_CLASS_ACCURACY);
        m_classBalancedAccuracy = settings.getBoolean(CFG_CLASS_BALANCED_ACCURACY);
        m_classErrorRate = settings.getBoolean(CFG_CLASS_ERROR_RATE);
        m_classFalseNegativeRate = settings.getBoolean(CFG_CLASS_FALSE_NEGATIVE_RATE);
        m_classRecall = settings.getBoolean(CFG_CLASS_RECALL);
        m_classPrecision = settings.getBoolean(CFG_CLASS_PRECISION);
        m_classSensitivity = settings.getBoolean(CFG_CLASS_SENSITIVITY);
        m_classSpecificity = settings.getBoolean(CFG_CLASS_SPECIFICITY);
        m_classFMeasure = settings.getBoolean(CFG_CLASS_F_MEASURE);
        m_displayOverallStats = settings.getBoolean(CFG_DISPLAY_OVERALL_STATS);
        m_overallAccuracy = settings.getBoolean(CFG_OVERALL_ACCURACY);
        m_overallError = settings.getBoolean(CFG_OVERALL_ERROR);
        m_overallCohensKappa = settings.getBoolean(CFG_OVERALL_COHENS_KAPPA);
        m_overallCorrectClassified = settings.getBoolean(CFG_OVERALL_CORRECT_CLASSIFIED);
        m_overallWrongClassified = settings.getBoolean(CFG_OVERALL_WRONG_CLASSIFIED);
        m_enableViewControls = settings.getBoolean(CFG_ENABLE_VIEW_CONTROLS);
        m_enableTitleEditing = settings.getBoolean(CFG_ENABLE_TITLE_EDITING);
        m_enableSubtitleEditing = settings.getBoolean(CFG_ENABLE_SUBTITLE_EDITING);
        m_enableRowsNumberConfig = settings.getBoolean(CFG_ENABLE_ROWS_NUMBER_CONFIG);
        m_enableConfusionMatrixRatesConfig = settings.getBoolean(CFG_ENABLE_CONFUSION_MATRIX_RATES_CONFIG);
        m_enableClassStatisticsConfig = settings.getBoolean(CFG_ENABLE_CLASS_STATISTICS_CONFIG);
        m_enableOverallStatisticsConfig = settings.getBoolean(CFG_ENABLE_OVERALL_STATISTICS_CONFIG);
        m_customCSS = settings.getString(CFG_CUSTOM_CSS);

        //added with 3.7
        m_displayLabels = settings.getBoolean(CFG_DISPLAY_LABELS, DEFAULT_DISPLAY_LABELS);
        m_displayClassNature = settings.getBoolean(CFG_DISPLAY_CLASS_NATURE, DEFAULT_DISPLAY_CLASS_NATURE);
        m_enableLabelsDisplayConfig =
                settings.getBoolean(CFG_ENABLE_LABELS_DISPLAY_CONFIG, DEFAULT_ENABLE_LABELS_DISPLAY_CONFIG);
    }

    /**
     * Loads parameters in Dialog.
     *
     * @param settings To load from.
     * @param spec The spec from the incoming data table
     */
    public void loadSettingsForDialog(final NodeSettingsRO settings, final DataTableSpec spec) {
        m_hideInWizard = settings.getBoolean(CFG_HIDE_IN_WIZARD, DEFAULT_HIDE_IN_WIZARD);
        m_showWarningsInView = settings.getBoolean(CFG_WARNINGS_IN_VIEW, DEFAULT_WARNINGS_IN_VIEW);
        m_reportMissingValues = settings.getBoolean(CFG_REPORT_MISSING_VALUES, DEFAULT_REPORT_MISSING_VALUES);
        m_firstColumn = settings.getString(CFG_FIRST_COLUMN, DEFAULT_FIRST_COLUMN);
        m_secondColumn = settings.getString(CFG_SECOND_COLUMN, DEFAULT_SECOND_COLUMN);
        m_sortingStragegy =
            SortingStrategy.valueOf(settings.getString(CFG_SORTING_STRATEGY, DEFAULT_SORTING_STRATEGY.name()));
        m_reverseOrder = settings.getBoolean(CFG_REVERSE_ORDER, DEFAULT_REVERSE_ORDER);
        m_ignoreMissingValues = settings.getBoolean(CFG_IGNORE_MISSING_VALUES, DEFAULT_IGNORE_MISSING_VALUES);
        m_title = settings.getString(CFG_TITLE, DEFAULT_TITLE);
        m_subtitle = settings.getString(CFG_SUBTITLE, DEFAULT_SUBTITLE);
        m_displayTotalRows = settings.getBoolean(CFG_DISPLAY_TOTAL_ROWS, DEFAULT_DISPLAY_TOTAL_ROWS);
        m_displayConfusionMatrixRates =
            settings.getBoolean(CFG_DISPLAY_CONFUSION_MATRIX_RATES, DEFAULT_DISPLAY_CONFUSION_MATRIX_RATES);
        m_headerColor = CSSUtils.colorFromCssHexString(
            settings.getString(CFG_HEADER_COLOR, CSSUtils.cssHexStringFromColor(DEFAULT_HEADER_COLOR)));
        m_diagonalColor = CSSUtils.colorFromCssHexString(
            settings.getString(CFG_DIAGONAL_COLOR, CSSUtils.cssHexStringFromColor(DEFAULT_DIAGONAL_COLOR)));
        m_displayFloatAsPercent = settings.getBoolean(CFG_DISPLAY_FLOAT_AS_PERCENT, DEFAULT_DISPLAY_FLOAT_AS_PERCENT);
        m_displayFullscreenButton =
            settings.getBoolean(CFG_DISPLAY_FULLSCREEN_BUTTON, DEFAULT_DISPLAY_FULLSCREEN_BUTTON);
        m_displayClassStatsTable =
            settings.getBoolean(CFG_DISPLAY_CLASS_STATS_TABLE, DEFAULT_DISPLAY_CLASS_STATS_TABLE);
        m_classTruePositives = settings.getBoolean(CFG_CLASS_TRUE_POSITIVES, DEFAULT_CLASS_TRUE_POSITIVES);
        m_classFalsePositives = settings.getBoolean(CFG_CLASS_FALSE_POSITIVES, DEFAULT_CLASS_FALSE_POSITIVES);
        m_classTrueNegatives = settings.getBoolean(CFG_CLASS_TRUE_NEGATIVES, DEFAULT_CLASS_TRUE_NEGATIVES);
        m_classFalseNegatives = settings.getBoolean(CFG_CLASS_FALSE_NEGATIVES, DEFAULT_CLASS_FALSE_NEGATIVES);
        m_classAccuracy = settings.getBoolean(CFG_CLASS_ACCURACY, DEFAULT_CLASS_ACCURACY);
        m_classBalancedAccuracy = settings.getBoolean(CFG_CLASS_BALANCED_ACCURACY, DEFAULT_CLASS_BALANCED_ACCURACY);
        m_classErrorRate = settings.getBoolean(CFG_CLASS_ERROR_RATE, DEFAULT_CLASS_ERROR_RATE);
        m_classFalseNegativeRate =
            settings.getBoolean(CFG_CLASS_FALSE_NEGATIVE_RATE, DEFAULT_CLASS_FALSE_NEGATIVE_RATE);
        m_classRecall = settings.getBoolean(CFG_CLASS_RECALL, DEFAULT_CLASS_RECALL);
        m_classPrecision = settings.getBoolean(CFG_CLASS_PRECISION, DEFAULT_CLASS_PRECISION);
        m_classSensitivity = settings.getBoolean(CFG_CLASS_SENSITIVITY, DEFAULT_CLASS_SENSITIVITY);
        m_classSpecificity = settings.getBoolean(CFG_CLASS_SPECIFICITY, DEFAULT_CLASS_SPECIFICITY);
        m_classFMeasure = settings.getBoolean(CFG_CLASS_F_MEASURE, DEFAULT_CLASS_F_MEASURE);
        m_displayOverallStats = settings.getBoolean(CFG_DISPLAY_OVERALL_STATS, DEFAULT_DISPLAY_OVERALL_STATS);
        m_overallAccuracy = settings.getBoolean(CFG_OVERALL_ACCURACY, DEFAULT_OVERALL_ACCURACY);
        m_overallError = settings.getBoolean(CFG_OVERALL_ERROR, DEFAULT_OVERALL_ERROR);
        m_overallCohensKappa = settings.getBoolean(CFG_OVERALL_COHENS_KAPPA, DEFAULT_OVERALL_COHENS_KAPPA);
        m_overallCorrectClassified =
            settings.getBoolean(CFG_OVERALL_CORRECT_CLASSIFIED, DEFAULT_OVERALL_CORRECT_CLASSIFIED);
        m_overallWrongClassified = settings.getBoolean(CFG_OVERALL_WRONG_CLASSIFIED, DEFAULT_OVERALL_WRONG_CLASSIFIED);
        m_enableViewControls = settings.getBoolean(CFG_ENABLE_VIEW_CONTROLS, DEFAULT_ENABLE_VIEW_CONTROLS);
        m_enableTitleEditing = settings.getBoolean(CFG_ENABLE_TITLE_EDITING, DEFAULT_ENABLE_TITLE_EDITING);
        m_enableSubtitleEditing = settings.getBoolean(CFG_ENABLE_SUBTITLE_EDITING, DEFAULT_ENABLE_SUBTITLE_EDITING);
        m_enableRowsNumberConfig =
            settings.getBoolean(CFG_ENABLE_ROWS_NUMBER_CONFIG, DEFAULT_ENABLE_ROWS_NUMBER_CONFIG);
        m_enableConfusionMatrixRatesConfig =
            settings.getBoolean(CFG_ENABLE_CONFUSION_MATRIX_RATES_CONFIG, DEFAULT_ENABLE_CONFUSION_MATRIX_RATES_CONFIG);
        m_enableClassStatisticsConfig =
            settings.getBoolean(CFG_ENABLE_CLASS_STATISTICS_CONFIG, DEFAULT_ENABLE_CLASS_STATISTICS_CONFIG);
        m_enableOverallStatisticsConfig =
            settings.getBoolean(CFG_ENABLE_OVERALL_STATISTICS_CONFIG, DEFAULT_ENABLE_OVERALL_STATISTICS_CONFIG);
        m_customCSS = settings.getString(CFG_CUSTOM_CSS, DEFAULT_CUSTOM_CSS);

        //added with 3.7
        m_displayLabels = settings.getBoolean(CFG_DISPLAY_LABELS, DEFAULT_DISPLAY_LABELS);
        m_displayClassNature = settings.getBoolean(CFG_DISPLAY_CLASS_NATURE, DEFAULT_DISPLAY_CLASS_NATURE);
        m_enableLabelsDisplayConfig =
                settings.getBoolean(CFG_ENABLE_LABELS_DISPLAY_CONFIG, DEFAULT_ENABLE_LABELS_DISPLAY_CONFIG);
    }
}
