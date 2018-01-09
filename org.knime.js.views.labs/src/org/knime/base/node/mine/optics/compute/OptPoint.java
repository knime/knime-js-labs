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
 * ------------------------------------------------------------------------
 */
package org.knime.base.node.mine.optics.compute;

import java.util.HashMap;

/**
 * Object to maintain characteristics of points in OPTICS algorithm.
 *
 * @author Anastasia Zhukova, University of Konstanz
 */
public class OptPoint implements Comparable<OptPoint> {

    /** Value of Infinity */
    public final static double UNDEFINED = Double.POSITIVE_INFINITY;

    private long m_id;

    private double m_coredist = UNDEFINED;

    private double m_reachdist = UNDEFINED;

    private boolean m_seen = false;

    private HashMap<Long, Double> m_neighbors = new HashMap<>();

    /**
     * OptPoint constructor with only id of a point
     *
     * @param id OptPoint ID
     * */
    public OptPoint(final long id) {
        m_id = id;
    }

    /**
     * OptPoint constructor to create a copy of a given OptPoint
     *
     * @param p OptPoint to copy
     * */
    OptPoint(final OptPoint p) {
        m_id = p.getID();
        m_coredist = p.getCoredist();
        m_reachdist = p.getReachdist();
        m_seen = p.isSeen();
        m_neighbors = p.getNeighbors();
    }

    /** OptPoint constructor with id and calculated reachability distance of a point
    *
    * @param id OptPoint ID
    * @param reachdist reachability distance of a point
    * */
    OptPoint(final long ID, final double reachdist) {
        m_id = ID;
        m_reachdist = reachdist;
    }

    /**
     * Set ID to a OptPoint
     *
     * @param id OptPoint ID
     * */
    public void setID(final long id) {
        m_id = id;
    }

    /**
     * Get ID of a OptPoint
     * @return OptPoint ID
     * */
    public long getID() {
        return m_id;
    }

    /**
     * Get reachability distance of a point
     * @return reachability distance of a point
     */
    public double getReachdist() {
        return m_reachdist;
    }

    /**
     * Set reachability distance to a point
     * @param reachdist reachability distance
     * */
    public void setReachdist(final double reachdist) {
        m_reachdist = reachdist;
    }

    /**
     * Get core distance of a point
     * @return core distance of a point
     * */
    public double getCoredist() {
        return m_coredist;
    }

    /**
     * Set core distance to a point
     * @param coredist core distance of a point
     */
    public void setCoredist(final double coredist) {
        m_coredist = coredist;

    }

    @Override
    public int compareTo(final OptPoint point2) {
        if (getReachdist() < point2.getReachdist()) {
            return -1;
        }
        if (getReachdist() > point2.getReachdist()) {
            return 1;
        }
        if (getReachdist() == point2.getReachdist()) {
            if (getID() < point2.getID()) {
                return -1;
            }
            if (getID() > point2.getID()) {
                return 1;
            }
            return 0;
        }
        return 0;
    }

    /**
     * Check if a point was already added to a container ordered by reachability distance
     * @return true if a point was already processed
     */
    public boolean isSeen() {
        return m_seen;
    }

    /**
     * Set true after a point was added to a container ordered by reachability distance, otherwise is false
     * @param seen flag of being processed
     */
    public void setSeen(final boolean seen) {
        m_seen = seen;
    }

    /**
     * Get neighbors of a point
     * @return neighbors of a point
     */
    public HashMap<Long, Double> getNeighbors() {
        return m_neighbors;
    }

    /**
     * Set neighbors of a point
     * @param neighborList neighbors of a point
     * */
    public void setNeighbors(final HashMap<Long, Double> neighborList) {
        m_neighbors = neighborList;
    }

    @Override
    public String toString() {
        String str = Long.toString(m_id) + ": coreDist=" + Double.toString(m_coredist) + ", reachDist="
            + Double.toString(m_reachdist) + ", isSeen=" + Boolean.toString(m_seen) + ", neighbors="
            + m_neighbors.toString();
        return str;
    }

}
