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

import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.knime.base.node.mine.cluster.hierarchical.view.ClusterViewNode;
import org.knime.base.node.viz.plotter.dendrogram.DendrogramNode;
import org.knime.core.data.DataTableSpec;
import org.knime.js.core.CSSUtils;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 *
 * @author Alison Walter
 */
@JsonAutoDetect
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class JSClusterModelTreeNode {

    private long id;

    private double distance;

    private String rowKey;

    private String color;

    private boolean hasRowData;

    private JSClusterModelTreeNode[] children;

    /**
     * @param node {@link DendrogramNode} to represent as JSON
     * @param ids a {@code Map} of all {@link DendrogramNode}s in the tree, to their unique ids
     * @param spec {@link DataTableSpec} for table used to create tree
     */
    public JSClusterModelTreeNode(final DendrogramNode node, final Map<DendrogramNode, Integer> ids, final DataTableSpec spec) {
        id = ids.get(node);
        distance = node.getDist();
        if (node.isLeaf() && (node instanceof ClusterViewNode)) {
            children = null;
            rowKey = ((ClusterViewNode)node).getLeafRowKey().getString();
            color = CSSUtils.cssHexStringFromColor(spec.getRowColor(node.getLeafDataPoint()).getColor());
            hasRowData = node.getLeafDataPoint() != null;
        } else {
            children = new JSClusterModelTreeNode[]{new JSClusterModelTreeNode(node.getFirstSubnode(), ids, spec),
                new JSClusterModelTreeNode(node.getSecondSubnode(), ids, spec)};
            rowKey = null;
            color = null;
            hasRowData = false;
        }
    }

    /**
     * @return id of node
     */
    public long getId() {
        return id;
    }

    /**
     * @return distance between two children in dendrogram
     */
    public double getDistance() {
        return distance;
    }

    /**
     * @return if leaf node returns rowKey in corresponding table, else empty string
     */
    public String getRowKey() {
        return rowKey;
    }

    /**
     * @return if this is a leaf node then the hex representation of the color of the node, else {@code null}
     */
    public String getColor() {
        return color;
    }

    /**
     * @return {@code true} if the node is a leaf which represents a row in the table, {@code false} if the node is not
     *         a leaf or if it is a leaf node which represents a row not in the data table
     */
    public boolean getHasRowData() {
        return hasRowData;
    }

    /**
     * @return children of node
     */
    public JSClusterModelTreeNode[] getChildren() {
        return children;
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

        final JSClusterModelTreeNode other = (JSClusterModelTreeNode)obj;

        // Check children equality
        if (children != null && other.getChildren() == null) {
            return false;
        }
        if (children == null && other.getChildren() != null) {
            return false;
        }
        boolean childrenEqual = children == null && other.getChildren() == null;
        if (children != null && other.getChildren() != null) {
            childrenEqual = children[0].equals(other.getChildren()[0]) && children[1].equals(other.getChildren()[1]);
        }
        return new EqualsBuilder()
                .append(id, other.getId())
                .append(distance, other.getDistance())
                .append(rowKey, other.getRowKey())
                .append(hasRowData, other.getHasRowData())
                .isEquals() && childrenEqual;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(id)
                .append(distance)
                .append(children)
                .append(rowKey)
                .append(hasRowData)
                .toHashCode();
    }

}
