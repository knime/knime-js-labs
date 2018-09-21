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
 *   Jul 19, 2018 (awalter): created
 */
package org.knime.base.node.mine.cluster.hierarchical.js;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.js.core.JSONViewContent;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 *
 * @author Alison Walter
 */
@JsonAutoDetect
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class HierarchicalClusterAssignerValue extends JSONViewContent {

    final static String CFG_CLUSTER_LABELS = "clusterLabels";
    private String[] m_clusterLabels;
    final static String CFG_ZOOM_X = "xMin";
    private double m_zoomX = 0;
    final static String CFG_ZOOM_Y = "xMax";
    private double m_zoomY = 0;
    final static String CFG_ZOOM_K = "yMin";
    private double m_zoomK = 1;
    final static String CFG_SELECTION = "selection";
    private String[] m_selection;

    private String m_title;
    private String m_subtitle;
    private int m_numClusters;
    private double m_threshold;
    private boolean m_useLogScale;
    private HierarchicalClusterAssignerOrientation m_orientation;

    private boolean m_publishSelectionEvents;
    private boolean m_subscribeSelectionEvents;
    private boolean m_showSelectedOnly;

    private boolean m_subscribeFilterEvents;

    private String m_xAxisLabel;
    private String m_yAxisLabel;

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
     * @return the numClusters
     */
    public int getNumClusters() {
        return m_numClusters;
    }

    /**
     * @param numClusters the numClusters to set
     */
    public void setNumClusters(final int numClusters) {
        m_numClusters = numClusters;
    }

    /**
     * @return the threshold
     */
    public double getThreshold() {
        return m_threshold;
    }

    /**
     * @param threshold the threshold to set
     */
    public void setThreshold(final double threshold) {
        m_threshold = threshold;
    }

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

    /**
     * @return the clusterLabels
     */
    public String[] getClusterLabels() {
        return m_clusterLabels;
    }

    /**
     * @param clusterLabels the clusterLabels to set
     */
    public void setClusterLabels(final String[] clusterLabels) {
        m_clusterLabels = clusterLabels;
    }

    /**
     * @return the zoomX
     */
    public double getZoomX() {
        return m_zoomX;
    }

    /**
     * @param zoomX the zoomX to set
     */
    public void setZoomX(final double zoomX) {
        m_zoomX = zoomX;
    }

    /**
     * @return the zoomY
     */
    public double getZoomY() {
        return m_zoomY;
    }

    /**
     * @param zoomY the zoomY to set
     */
    public void setZoomY(final double zoomY) {
        m_zoomY = zoomY;
    }

    /**
     * @return the zoomK
     */
    public double getZoomK() {
        return m_zoomK;
    }

    /**
     * @param zoomK the zoomK to set
     */
    public void setZoomK(final double zoomK) {
        m_zoomK = zoomK;
    }

    /**
     * @return the useLogScale
     */
    public boolean getUseLogScale() {
        return m_useLogScale;
    }

    /**
     * @param useLogScale the useLogScale to set
     */
    public void setUseLogScale(final boolean useLogScale) {
        m_useLogScale = useLogScale;
    }

    /**
     * @return the orientation
     */
    public HierarchicalClusterAssignerOrientation getOrientation() {
        return m_orientation;
    }

    /**
     * @param orientation the orientation to set
     */
    public void setOrientation(final HierarchicalClusterAssignerOrientation orientation) {
        m_orientation = orientation;
    }

    /**
     * @return the publishSelectionEvents
     */
    public boolean getPublishSelectionEvents() {
        return m_publishSelectionEvents;
    }

    /**
     * @param publishSelectionEvents the publishSelectionEvents to set
     */
    public void setPublishSelectionEvents(final boolean publishSelectionEvents) {
        m_publishSelectionEvents = publishSelectionEvents;
    }

    /**
     * @return the subscribeSelectionEvents
     */
    public boolean getSubscribeSelectionEvents() {
        return m_subscribeSelectionEvents;
    }

    /**
     * @param subscribeSelectionEvents the subscribeSelectionEvents to set
     */
    public void setSubscribeSelectionEvents(final boolean subscribeSelectionEvents) {
        m_subscribeSelectionEvents = subscribeSelectionEvents;
    }

    /**
     * @return the showSelectedOnly
     */
    public boolean getShowSelectedOnly() {
        return m_showSelectedOnly;
    }

    /**
     * @param showSelectedOnly the showSelectedOnly to set
     */
    public void setShowSelectedOnly(final boolean showSelectedOnly) {
        m_showSelectedOnly = showSelectedOnly;
    }

    /**
     * @return the subscribeFilterEvents
     */
    public boolean getSubscribeFilterEvents() {
        return m_subscribeFilterEvents;
    }

    /**
     * @param subscribeFilterEvents the subscribeFilterEvents to set
     */
    public void setSubscribeFilterEvents(final boolean subscribeFilterEvents) {
        m_subscribeFilterEvents = subscribeFilterEvents;
    }

    /**
     * @return the xAxisLabel
     */
    @JsonProperty("xAxisLabel")
    public String getXAxisLabel() {
        return m_xAxisLabel;
    }

    /**
     * @param xAxisLabel the xAxisLabel to set
     */
    @JsonProperty("xAxisLabel")
    public void setXAxisLabel(final String xAxisLabel) {
        m_xAxisLabel = xAxisLabel;
    }

    /**
     * @return the yAxisLabel
     */
    @JsonProperty("yAxisLabel")
    public String getYAxisLabel() {
        return m_yAxisLabel;
    }

    /**
     * @param yAxisLabel the yAxisLabel to set
     */
    @JsonProperty("yAxisLabel")
    public void setYAxisLabel(final String yAxisLabel) {
        m_yAxisLabel = yAxisLabel;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveToNodeSettings(final NodeSettingsWO settings) {
        settings.addString(HierarchicalClusterAssignerConfig.CFG_TITLE, getTitle());
        settings.addString(HierarchicalClusterAssignerConfig.CFG_SUBTITLE, getSubtitle());
        settings.addInt(HierarchicalClusterAssignerConfig.CFG_NUM_CLUSTERS, getNumClusters());
        settings.addDouble(HierarchicalClusterAssignerConfig.CFG_THRESHOLD, getThreshold());
        settings.addStringArray(CFG_SELECTION, getSelection());
        settings.addStringArray(CFG_CLUSTER_LABELS, getClusterLabels());
        settings.addDouble(CFG_ZOOM_X, getZoomX());
        settings.addDouble(CFG_ZOOM_Y, getZoomY());
        settings.addDouble(CFG_ZOOM_K, getZoomK());
        settings.addBoolean(HierarchicalClusterAssignerConfig.CFG_USE_LOG_SCALE, getUseLogScale());
        settings.addString(HierarchicalClusterAssignerConfig.CFG_ORIENTATION, getOrientation().toValue());

        settings.addBoolean(HierarchicalClusterAssignerConfig.CFG_PUBLISH_SELECTION_EVENTS, getPublishSelectionEvents());
        settings.addBoolean(HierarchicalClusterAssignerConfig.CFG_SUBSCRIBE_SELECTION_EVENTS, getSubscribeSelectionEvents());
        settings.addBoolean(HierarchicalClusterAssignerConfig.CFG_SHOW_SELECTED_ONLY, getShowSelectedOnly());
        settings.addBoolean(HierarchicalClusterAssignerConfig.CFG_SUBSCRIBE_FILTER_EVENTS, getSubscribeFilterEvents());
        settings.addString(HierarchicalClusterAssignerConfig.CFG_X_AXIS_LABEL, m_xAxisLabel);
        settings.addString(HierarchicalClusterAssignerConfig.CFG_Y_AXIS_LABEL, m_yAxisLabel);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadFromNodeSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        m_title = settings.getString(HierarchicalClusterAssignerConfig.CFG_TITLE);
        m_subtitle = settings.getString(HierarchicalClusterAssignerConfig.CFG_SUBTITLE);
        m_numClusters = settings.getInt(HierarchicalClusterAssignerConfig.CFG_NUM_CLUSTERS);
        m_threshold = settings.getDouble(HierarchicalClusterAssignerConfig.CFG_THRESHOLD);
        m_selection = settings.getStringArray(CFG_SELECTION);
        m_clusterLabels = settings.getStringArray(CFG_CLUSTER_LABELS);
        m_zoomX = settings.getDouble(CFG_ZOOM_X);
        m_zoomY = settings.getDouble(CFG_ZOOM_Y);
        m_zoomK = settings.getDouble(CFG_ZOOM_K);
        m_useLogScale = settings.getBoolean(HierarchicalClusterAssignerConfig.CFG_USE_LOG_SCALE);
        m_orientation = HierarchicalClusterAssignerOrientation
            .forValue(settings.getString(HierarchicalClusterAssignerConfig.CFG_ORIENTATION));

        setPublishSelectionEvents(settings.getBoolean(HierarchicalClusterAssignerConfig.CFG_PUBLISH_SELECTION_EVENTS));
        setSubscribeSelectionEvents(settings.getBoolean(HierarchicalClusterAssignerConfig.CFG_SUBSCRIBE_SELECTION_EVENTS));
        setShowSelectedOnly(settings.getBoolean(HierarchicalClusterAssignerConfig.CFG_SHOW_SELECTED_ONLY));
        setSubscribeFilterEvents(settings.getBoolean(HierarchicalClusterAssignerConfig.CFG_SUBSCRIBE_FILTER_EVENTS));
        setXAxisLabel(settings.getString(HierarchicalClusterAssignerConfig.CFG_X_AXIS_LABEL));
        setYAxisLabel(settings.getString(HierarchicalClusterAssignerConfig.CFG_Y_AXIS_LABEL));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object obj) {
        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        final HierarchicalClusterAssignerValue other = (HierarchicalClusterAssignerValue)obj;
        return new EqualsBuilder()
                .append(m_title, other.getTitle())
                .append(m_subtitle, other.getSubtitle())
                .append(m_numClusters, other.getNumClusters())
                .append(m_threshold, other.getThreshold())
                .append(m_selection, other.getSelection())
                .append(m_clusterLabels, other.getClusterLabels())
                .append(m_zoomX, other.getZoomX())
                .append(m_zoomY, other.getZoomY())
                .append(m_zoomK, other.getZoomK())
                .append(m_useLogScale, other.getUseLogScale())
                .append(m_orientation, other.getOrientation())
                .append(m_publishSelectionEvents, other.getPublishSelectionEvents())
                .append(m_subscribeSelectionEvents, other.getSubscribeSelectionEvents())
                .append(m_subscribeFilterEvents, other.getSubscribeFilterEvents())
                .append(m_showSelectedOnly, other.getShowSelectedOnly())
                .append(m_xAxisLabel, other.getXAxisLabel())
                .append(m_yAxisLabel, other.getYAxisLabel())
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
                .append(m_numClusters)
                .append(m_threshold)
                .append(m_selection)
                .append(m_clusterLabels)
                .append(m_zoomX)
                .append(m_zoomY)
                .append(m_zoomK)
                .append(m_useLogScale)
                .append(m_orientation)
                .append(m_publishSelectionEvents)
                .append(m_subscribeSelectionEvents)
                .append(m_subscribeFilterEvents)
                .append(m_showSelectedOnly)
                .append(m_xAxisLabel)
                .append(m_yAxisLabel)
                .toHashCode();
    }

}
