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
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 *
 * @author Alison Walter
 */
@JsonAutoDetect
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class HierarchicalClusterAssignerValue extends JSONViewContent {

    private String m_title;
    private String m_subtitle;
    private int m_numClusters;
    private double m_threshold;
    private String[] m_selection;
    private String[] m_clusterLabels;

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
     * {@inheritDoc}
     */
    @Override
    public void saveToNodeSettings(final NodeSettingsWO settings) {
        settings.addString(HierarchicalClusterAssignerConfig.CFG_TITLE, getTitle());
        settings.addString(HierarchicalClusterAssignerConfig.CFG_SUBTITLE, getSubtitle());
        settings.addInt(HierarchicalClusterAssignerConfig.CFG_NUM_CLUSTERS, getNumClusters());
        settings.addDouble(HierarchicalClusterAssignerConfig.CFG_THRESHOLD, getThreshold());
        settings.addStringArray(HierarchicalClusterAssignerConfig.CFG_SELECTION, getSelection());
        settings.addStringArray(HierarchicalClusterAssignerConfig.CFG_CLUSTER_LABELS, getClusterLabels());
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
        m_selection = settings.getStringArray(HierarchicalClusterAssignerConfig.CFG_SELECTION);
        m_clusterLabels = settings.getStringArray(HierarchicalClusterAssignerConfig.CFG_CLUSTER_LABELS);
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
                .toHashCode();
    }

}
