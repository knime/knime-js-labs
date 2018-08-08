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
 *   Aug 3, 2018 (awalter): created
 */
package org.knime.js.base.node.viz.heatmap;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.js.core.JSONViewContent;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * The view value of the Heatmap node.
 *
 * @author Alison Walter, KNIME GmbH, Konstanz, Germany
 */
@JsonAutoDetect
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class HeatMapViewValue extends JSONViewContent {

    private String m_chartTitle;
    private String m_chartSubtitle;

    private String[] m_threeColorGradient;
    private boolean m_continuousGradient;
    private int m_numDiscreteColors;
    private String m_missingValueColor;

    private String[] m_selection;

    private int m_initialPageSize;

    private String m_xMin;
    private String m_xMax;
    private String m_yMin;
    private String m_yMax;

    // -- General getters & setters --

    /**
     * @return the chartTitle
     */
    public String getChartTitle() {
        return m_chartTitle;
    }

    /**
     * @param chartTitle the chartTitle to set
     */
    public void setChartTitle(final String chartTitle) {
        m_chartTitle = chartTitle;
    }

    /**
     * @return the chartSubtitle
     */
    public String getChartSubtitle() {
        return m_chartSubtitle;
    }

    /**
     * @param chartSubtitle the chartSubtitle to set
     */
    public void setChartSubtitle(final String chartSubtitle) {
        m_chartSubtitle = chartSubtitle;
    }

    // -- Gradient getters & setters --

    /**
     * @return the threeColorGradient
     */
    public String[] getThreeColorGradient() {
        return m_threeColorGradient;
    }

    /**
     * @param threeColorGradient the threeColorGradient to set
     */
    public void setThreeColorGradient(final String[] threeColorGradient) {
        m_threeColorGradient = threeColorGradient;
    }

    /**
     * @return the continuousGradient
     */
    public boolean getContinuousGradient() {
        return m_continuousGradient;
    }

    /**
     * @param continuousGradient the continuousGradient to set
     */
    public void setContinuousGradient(final boolean continuousGradient) {
        m_continuousGradient = continuousGradient;
    }

    /**
     * @return the numDiscreteColors
     */
    public int getNumDiscreteColors() {
        return m_numDiscreteColors;
    }

    /**
     * @param numDiscreteColors the numDiscreteColors to set
     */
    public void setNumDiscreteColors(final int numDiscreteColors) {
        m_numDiscreteColors = numDiscreteColors;
    }

    /**
     * @return the missingValueColor
     */
    public String getMissingValueColor() {
        return m_missingValueColor;
    }

    /**
     * @param missingValueColor the missingValueColor to set
     */
    public void setMissingValueColor(final String missingValueColor) {
        m_missingValueColor = missingValueColor;
    }

    // -- Selection getters & setters --

    /**
     * @return the selection
     */
    public String[] getSelection() {
        return m_selection;
    }

    /**
     * @param selection the selection to set
     */
    public void setSelection(final String[] selection) {
        m_selection = selection;
    }

    // -- Paging getters & setters --

    /**
     * @return the initialPageSize
     */
    public int getInitialPageSize() {
        return m_initialPageSize;
    }

    /**
     * @param initialPageSize the initialPageSize to set
     */
    public void setInitialPageSize(final int initialPageSize) {
        m_initialPageSize = initialPageSize;
    }

    // -- Zoom & Panning getters & setters --

    /**
     * @return the xMin
     */
    public String getXMin() {
        return m_xMin;
    }

    /**
     * @param xMin the xMin to set
     */
    public void setXMin(final String xMin) {
        m_xMin = xMin;
    }

    /**
     * @return the xMax
     */
    public String getXMax() {
        return m_xMax;
    }

    /**
     * @param xMax the xMax to set
     */
    public void setXMax(final String xMax) {
        m_xMax = xMax;
    }

    /**
     * @return the yMin
     */
    public String getYMin() {
        return m_yMin;
    }

    /**
     * @param yMin the yMin to set
     */
    public void setYMin(final String yMin) {
        m_yMin = yMin;
    }

    /**
     * @return the yMax
     */
    public String getYMax() {
        return m_yMax;
    }

    /**
     * @param yMax the yMax to set
     */
    public void setYMax(final String yMax) {
        m_yMax = yMax;
    }

    // -- Load & Save Settings --

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveToNodeSettings(final NodeSettingsWO settings) {
        settings.addString(HeatMapViewConfig.CFG_CHART_TITLE, m_chartTitle);
        settings.addString(HeatMapViewConfig.CFG_CHART_SUBTITLE, m_chartSubtitle);

        settings.addStringArray(HeatMapViewConfig.CFG_THREE_COLOR_GRADIENT, m_threeColorGradient);
        settings.addBoolean(HeatMapViewConfig.CFG_CONTINUOUS_GRADIENT, m_continuousGradient);
        settings.addInt(HeatMapViewConfig.CFG_NUM_DISCRETE_COLORS, m_numDiscreteColors);
        settings.addString(HeatMapViewConfig.CFG_MISSING_VALUE_COLOR, m_missingValueColor);

        settings.addStringArray(HeatMapViewConfig.CFG_SELECTION, m_selection);

        settings.addInt(HeatMapViewConfig.CFG_INITIAL_PAGE_SIZE, m_initialPageSize);

        settings.addString(HeatMapViewConfig.CFG_X_MIN, m_xMin);
        settings.addString(HeatMapViewConfig.CFG_X_MAX, m_xMax);
        settings.addString(HeatMapViewConfig.CFG_Y_MIN, m_yMin);
        settings.addString(HeatMapViewConfig.CFG_Y_MAX, m_yMax);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadFromNodeSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        m_chartTitle = settings.getString(HeatMapViewConfig.CFG_CHART_TITLE);
        m_chartSubtitle = settings.getString(HeatMapViewConfig.CFG_CHART_SUBTITLE);

        m_threeColorGradient = settings.getStringArray(HeatMapViewConfig.CFG_THREE_COLOR_GRADIENT);
        m_continuousGradient = settings.getBoolean(HeatMapViewConfig.CFG_CONTINUOUS_GRADIENT);
        m_numDiscreteColors = settings.getInt(HeatMapViewConfig.CFG_NUM_DISCRETE_COLORS);
        m_missingValueColor = settings.getString(HeatMapViewConfig.CFG_MISSING_VALUE_COLOR);

        m_selection = settings.getStringArray(HeatMapViewConfig.CFG_SELECTION);

        m_initialPageSize = settings.getInt(HeatMapViewConfig.CFG_INITIAL_PAGE_SIZE);

        m_xMin = settings.getString(HeatMapViewConfig.CFG_X_MIN);
        m_xMax = settings.getString(HeatMapViewConfig.CFG_X_MAX);
        m_yMin = settings.getString(HeatMapViewConfig.CFG_Y_MIN);
        m_yMax = settings.getString(HeatMapViewConfig.CFG_Y_MAX);
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
        final HeatMapViewValue other = (HeatMapViewValue) obj;
        return new EqualsBuilder()
                .append(m_chartTitle, other.getChartTitle())
                .append(m_chartSubtitle, other.getChartSubtitle())
                .append(m_threeColorGradient, other.getThreeColorGradient())
                .append(m_continuousGradient, other.getContinuousGradient())
                .append(m_numDiscreteColors, other.getNumDiscreteColors())
                .append(m_missingValueColor, other.getMissingValueColor())
                .append(m_selection, other.getSelection())
                .append(m_initialPageSize, other.getInitialPageSize())
                .append(m_xMin, other.getXMin())
                .append(m_xMax, other.getXMax())
                .append(m_yMin, other.getYMin())
                .append(m_yMax, other.getYMax())
                .isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(m_chartTitle)
                .append(m_chartSubtitle)
                .append(m_threeColorGradient)
                .append(m_continuousGradient)
                .append(m_numDiscreteColors)
                .append(m_missingValueColor)
                .append(m_selection)
                .append(m_initialPageSize)
                .append(m_xMin)
                .append(m_xMax)
                .append(m_yMin)
                .append(m_yMax)
                .toHashCode();
    }
}
