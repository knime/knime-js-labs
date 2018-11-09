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

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.js.core.JSONDataTable;
import org.knime.js.core.JSONViewContent;

/**
 *
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 */
public class ScorerViewRepresentation extends JSONViewContent {

    private static final String CFG_CONFUSION_MATRIX = "confusionMatrix";
    private JSONDataTable m_confusionMatrix;
    private static final String CFG_CLASS_STATISTICS_TABLE = "classStatisticsTable";
    private JSONDataTable m_classStatisticsTable;
    private static final String CFG_OVERALL_STATISTICS_TABLE = "overallStatisticsTable";
    private JSONDataTable m_overallStatisticsTable;
    private static final String CFG_KEYSTORE = "keystore";
    private static final String CFG_DIMENSIONS = "dimensions";
    private static final String CFG_STORE_PREFIX = "store_";
    private List<String>[][] m_keystore;
    private static final String CFG_WARNING_MESSAGE = "warningMessage";
    private String m_warningMessage;

    private boolean m_showWarningsInView;
    private String m_headerColor;
    private String m_diagonalColor;
    private boolean m_displayFullscreenButton;
    private boolean m_enableViewControls;
    private boolean m_enableTitleEditing;
    private boolean m_enableSubtitleEditing;
    private boolean m_enableLabelsDisplayConfig;
    private boolean m_enableRowsNumberConfig;
    private boolean m_enableConfusionMatrixRatesConfig;
    private boolean m_enableClassStatisticsConfig;
    private boolean m_enableOverallStatisticsConfig;

    /**
     * @return the confusionMatrix
     */
    public JSONDataTable getConfusionMatrix() {
        return m_confusionMatrix;
    }

    /**
     * @param confusionMatrix the confusionMatrix to set
     */
    public void setConfusionMatrix(final JSONDataTable confusionMatrix) {
        m_confusionMatrix = confusionMatrix;
    }

    /**
     * @return the classStatisticsTable
     */
    public JSONDataTable getClassStatisticsTable() {
        return m_classStatisticsTable;
    }

    /**
     * @param classStatisticsTable the classStatisticsTable to set
     */
    public void setClassStatisticsTable(final JSONDataTable classStatisticsTable) {
        m_classStatisticsTable = classStatisticsTable;
    }

    /**
     * @return the overallStatisticsTable
     */
    public JSONDataTable getOverallStatisticsTable() {
        return m_overallStatisticsTable;
    }

    /**
     * @param overallStatisticsTable the overallStatisticsTable to set
     */
    public void setOverallStatisticsTable(final JSONDataTable overallStatisticsTable) {
        m_overallStatisticsTable = overallStatisticsTable;
    }

    /**
     * @return the keystore
     */
    public List<String>[][] getKeystore() {
        return m_keystore;
    }

    /**
     * @param keystore the keystore to set
     */
    public void setKeystore(final List<String>[][] keystore) {
        m_keystore = keystore;
    }

    /**
     * @return the warningMessage
     */
    public String getWarningMessage() {
        return m_warningMessage;
    }

    /**
     * @param warningMessage the warningMessage to set
     */
    public void setWarningMessage(final String warningMessage) {
        m_warningMessage = warningMessage;
    }

    /**
     * @return the showWarningsInView
     */
    public boolean isShowWarningsInView() {
        return m_showWarningsInView;
    }

    /**
     * @param showWarningsInView the showWarningsInView to set
     */
    public void setShowWarningsInView(final boolean showWarningsInView) {
        m_showWarningsInView = showWarningsInView;
    }

    /**
     * @return the headerColor
     */
    public String getHeaderColor() {
        return m_headerColor;
    }

    /**
     * @param headerColor the headerColor to set
     */
    public void setHeaderColor(final String headerColor) {
        m_headerColor = headerColor;
    }

    /**
     * @return the diagonalColor
     */
    public String getDiagonalColor() {
        return m_diagonalColor;
    }

    /**
     * @param diagonalColor the diagonalColor to set
     */
    public void setDiagonalColor(final String diagonalColor) {
        m_diagonalColor = diagonalColor;
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
     * {@inheritDoc}
     */
    @Override
    public void saveToNodeSettings(final NodeSettingsWO settings) {
        //Tables are stored in node model
        NodeSettingsWO storeSettings = settings.addNodeSettings(CFG_KEYSTORE);
        int dimensions = m_keystore == null ? 0 : m_keystore.length;
        storeSettings.addInt(CFG_DIMENSIONS, dimensions);
        for (int x = 0; x < dimensions; x++) {
            for (int y = 0; y < dimensions; y++) {
                storeSettings.addStringArray(CFG_STORE_PREFIX + x + "_" + y, m_keystore[x][y].toArray(new String[0]));
            }
        }
        settings.addString(CFG_WARNING_MESSAGE, m_warningMessage);

        settings.addBoolean(ScorerConfig.CFG_WARNINGS_IN_VIEW, m_showWarningsInView);
        settings.addString(ScorerConfig.CFG_HEADER_COLOR, m_headerColor);
        settings.addString(ScorerConfig.CFG_DIAGONAL_COLOR, m_diagonalColor);
        settings.addBoolean(ScorerConfig.CFG_DISPLAY_FULLSCREEN_BUTTON, m_displayFullscreenButton);
        settings.addBoolean(ScorerConfig.CFG_ENABLE_VIEW_CONTROLS, m_enableViewControls);
        settings.addBoolean(ScorerConfig.CFG_ENABLE_TITLE_EDITING, m_enableTitleEditing);
        settings.addBoolean(ScorerConfig.CFG_ENABLE_SUBTITLE_EDITING, m_enableSubtitleEditing);
        settings.addBoolean(ScorerConfig.CFG_ENABLE_LABELS_DISPLAY_CONFIG, m_enableLabelsDisplayConfig);
        settings.addBoolean(ScorerConfig.CFG_ENABLE_ROWS_NUMBER_CONFIG, m_enableRowsNumberConfig);
        settings.addBoolean(ScorerConfig.CFG_ENABLE_CONFUSION_MATRIX_RATES_CONFIG, m_enableConfusionMatrixRatesConfig);
        settings.addBoolean(ScorerConfig.CFG_ENABLE_CLASS_STATISTICS_CONFIG, m_enableClassStatisticsConfig);
        settings.addBoolean(ScorerConfig.CFG_ENABLE_OVERALL_STATISTICS_CONFIG, m_enableOverallStatisticsConfig);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public void loadFromNodeSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        //Tables are loaded in node model
        NodeSettingsRO storeSettings = settings.getNodeSettings(CFG_KEYSTORE);
        int dimensions = storeSettings.getInt(CFG_DIMENSIONS);
        if (dimensions > 0) {
            m_keystore = new List[dimensions][dimensions];
            for (int x = 0; x < dimensions; x++) {
                for (int y = 0; y < dimensions; y++) {
                    m_keystore[x][y] = Arrays.asList(storeSettings.getStringArray(CFG_STORE_PREFIX + x + "_" + y));
                }
            }
        }
        m_warningMessage = settings.getString(CFG_WARNING_MESSAGE);

        m_showWarningsInView = settings.getBoolean(ScorerConfig.CFG_WARNINGS_IN_VIEW);
        m_headerColor = settings.getString(ScorerConfig.CFG_HEADER_COLOR);
        m_diagonalColor = settings.getString(ScorerConfig.CFG_DIAGONAL_COLOR);
        m_displayFullscreenButton = settings.getBoolean(ScorerConfig.CFG_DISPLAY_FULLSCREEN_BUTTON);
        m_enableViewControls = settings.getBoolean(ScorerConfig.CFG_ENABLE_VIEW_CONTROLS);
        m_enableTitleEditing = settings.getBoolean(ScorerConfig.CFG_ENABLE_TITLE_EDITING);
        m_enableSubtitleEditing = settings.getBoolean(ScorerConfig.CFG_ENABLE_SUBTITLE_EDITING);
        m_enableLabelsDisplayConfig = settings.getBoolean(ScorerConfig.CFG_ENABLE_LABELS_DISPLAY_CONFIG);
        m_enableRowsNumberConfig = settings.getBoolean(ScorerConfig.CFG_ENABLE_ROWS_NUMBER_CONFIG);
        m_enableConfusionMatrixRatesConfig = settings.getBoolean(ScorerConfig.CFG_ENABLE_CONFUSION_MATRIX_RATES_CONFIG);
        m_enableClassStatisticsConfig = settings.getBoolean(ScorerConfig.CFG_ENABLE_CLASS_STATISTICS_CONFIG);
        m_enableOverallStatisticsConfig = settings.getBoolean(ScorerConfig.CFG_ENABLE_OVERALL_STATISTICS_CONFIG);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        ScorerViewRepresentation other = (ScorerViewRepresentation)obj;
        return new EqualsBuilder()
                .append(m_confusionMatrix, other.m_confusionMatrix)
                .append(m_classStatisticsTable, other.m_classStatisticsTable)
                .append(m_overallStatisticsTable, other.m_overallStatisticsTable)
                .append(m_keystore, other.m_keystore)
                .append(m_showWarningsInView, other.m_showWarningsInView)
                .append(m_headerColor, other.m_headerColor)
                .append(m_diagonalColor, other.m_diagonalColor)
                .append(m_displayFullscreenButton, other.m_displayFullscreenButton)
                .append(m_enableViewControls, other.m_enableViewControls)
                .append(m_enableTitleEditing, other.m_enableTitleEditing)
                .append(m_enableSubtitleEditing, other.m_enableSubtitleEditing)
                .append(m_enableLabelsDisplayConfig, other.m_enableLabelsDisplayConfig)
                .append(m_enableRowsNumberConfig, other.m_enableRowsNumberConfig)
                .append(m_enableConfusionMatrixRatesConfig, other.m_enableConfusionMatrixRatesConfig)
                .append(m_enableClassStatisticsConfig, other.m_enableClassStatisticsConfig)
                .append(m_enableOverallStatisticsConfig, other.m_enableOverallStatisticsConfig)
                .isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(m_confusionMatrix)
                .append(m_classStatisticsTable)
                .append(m_overallStatisticsTable)
                .append(m_keystore)
                .append(m_showWarningsInView)
                .append(m_headerColor)
                .append(m_diagonalColor)
                .append(m_displayFullscreenButton)
                .append(m_enableViewControls)
                .append(m_enableTitleEditing)
                .append(m_enableSubtitleEditing)
                .append(m_enableLabelsDisplayConfig)
                .append(m_enableRowsNumberConfig)
                .append(m_enableConfusionMatrixRatesConfig)
                .append(m_enableClassStatisticsConfig)
                .append(m_enableOverallStatisticsConfig)
                .toHashCode();
    }

}
