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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.js.core.JSONViewContent;

/**
 *
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 */
public class ScorerViewValue extends JSONViewContent {

    private String m_title;
    private String m_subtitle;
    private boolean m_displayLabels;
    private boolean m_displayClassNature;
    private boolean m_displayTotalRows;
    private boolean m_displayConfusionMatrixRates;
    private boolean m_displayFloatAsPercent;
    private boolean m_displayClassStatsTable;
    private boolean m_displayOverallStats;

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
    public final boolean isDisplayClassNature() {
        return m_displayClassNature;
    }

    /**
     * @param displayClassNature the displayClassNature to set
     */
    public final void setDisplayClassNature(final boolean displayClassNature) {
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
     * {@inheritDoc}
     */
    @Override
    public void saveToNodeSettings(final NodeSettingsWO settings) {
        settings.addString(ScorerConfig.CFG_TITLE, m_title);
        settings.addString(ScorerConfig.CFG_SUBTITLE, m_subtitle);
        settings.addBoolean(ScorerConfig.CFG_DISPLAY_LABELS, m_displayLabels);
        settings.addBoolean(ScorerConfig.CFG_DISPLAY_CLASS_NATURE, m_displayClassNature);
        settings.addBoolean(ScorerConfig.CFG_DISPLAY_TOTAL_ROWS, m_displayTotalRows);
        settings.addBoolean(ScorerConfig.CFG_DISPLAY_CONFUSION_MATRIX_RATES, m_displayConfusionMatrixRates);
        settings.addBoolean(ScorerConfig.CFG_DISPLAY_FLOAT_AS_PERCENT, m_displayFloatAsPercent);
        settings.addBoolean(ScorerConfig.CFG_DISPLAY_CLASS_STATS_TABLE, m_displayClassStatsTable);
        settings.addBoolean(ScorerConfig.CFG_DISPLAY_OVERALL_STATS, m_displayOverallStats);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadFromNodeSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        m_title = settings.getString(ScorerConfig.CFG_TITLE);
        m_subtitle = settings.getString(ScorerConfig.CFG_SUBTITLE);
        m_displayLabels = settings.getBoolean(ScorerConfig.CFG_DISPLAY_LABELS);
        m_displayClassNature = settings.getBoolean(ScorerConfig.CFG_DISPLAY_CLASS_NATURE);
        m_displayTotalRows = settings.getBoolean(ScorerConfig.CFG_DISPLAY_TOTAL_ROWS);
        m_displayConfusionMatrixRates = settings.getBoolean(ScorerConfig.CFG_DISPLAY_CONFUSION_MATRIX_RATES);
        m_displayFloatAsPercent = settings.getBoolean(ScorerConfig.CFG_DISPLAY_FLOAT_AS_PERCENT);
        m_displayClassStatsTable = settings.getBoolean(ScorerConfig.CFG_DISPLAY_CLASS_STATS_TABLE);
        m_displayOverallStats = settings.getBoolean(ScorerConfig.CFG_DISPLAY_OVERALL_STATS);
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
        ScorerViewValue other = (ScorerViewValue)obj;
        return new EqualsBuilder()
                .append(m_title, other.m_title)
                .append(m_subtitle, other.m_subtitle)
                .append(m_displayLabels, other.m_displayLabels)
                .append(m_displayClassNature, other.m_displayClassNature)
                .append(m_displayTotalRows, other.m_displayTotalRows)
                .append(m_displayConfusionMatrixRates, other.m_displayConfusionMatrixRates)
                .append(m_displayFloatAsPercent, other.m_displayFloatAsPercent)
                .append(m_displayClassStatsTable, other.m_displayClassStatsTable)
                .append(m_displayOverallStats, other.m_displayOverallStats)
                .isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(m_title)
                .append(m_subtitle)
                .append(m_displayLabels)
                .append(m_displayClassNature)
                .append(m_displayTotalRows)
                .append(m_displayConfusionMatrixRates)
                .append(m_displayFloatAsPercent)
                .append(m_displayClassStatsTable)
                .append(m_displayOverallStats)
                .toHashCode();
    }

}
