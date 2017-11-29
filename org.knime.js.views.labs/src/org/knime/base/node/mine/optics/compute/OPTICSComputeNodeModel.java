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
 *  KNIME and ECLIPSE being a combined program, KNIME GMBH herewith grants
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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

import org.knime.base.node.mine.optics.OPTICSPortObject;
import org.knime.base.util.flowvariable.FlowVariableProvider;
import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.RowKey;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.data.def.DoubleCell;
import org.knime.core.data.def.IntCell;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortType;
import org.knime.distance.DistanceMeasure;
import org.knime.distance.DistanceMeasurePortObject;
import org.knime.distance.DistanceMeasurePortSpec;
import org.knime.distance.DistanceMeasurementException;
import org.knime.js.core.layout.LayoutTemplateProvider;
import org.knime.js.core.layout.bs.JSONLayoutViewContent;

/**
 * This is the model implementation of OPTICS.
 *
 * @author Anastasia Zhukova, KNIME GmbH, Konstanz, Germany
 * @author Oliver Sampson, University of Konstanz, Germany
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 */
final class OPTICSComputeNodeModel extends NodeModel implements FlowVariableProvider, LayoutTemplateProvider {

    private static final int PORT_IN_DATA = 0;

    private static final int PORT_IN_DIST_MAT = 1;

    private OPTICSComputeViewConfig m_config;

    private ArrayList<OptPoint> m_optPointContainer;

    private BufferedDataTable m_intermediateTable;

    /**
     * Constructor for the node model.
     */
    protected OPTICSComputeNodeModel() {
        super(new PortType[]{BufferedDataTable.TYPE, DistanceMeasurePortObject.TYPE_OPTIONAL},
            new PortType[]{OPTICSPortObject.TYPE});
        m_config = new OPTICSComputeViewConfig();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PortObjectSpec[] configure(final PortObjectSpec[] inSpecs) throws InvalidSettingsException {
        DataTableSpec inDataSpec = (DataTableSpec)inSpecs[PORT_IN_DATA];

        if ((DistanceMeasurePortSpec)inSpecs[PORT_IN_DIST_MAT] != null) {
            ((DistanceMeasurePortSpec)inSpecs[PORT_IN_DIST_MAT]).validate((DataTableSpec)inSpecs[PORT_IN_DATA]);
        } else {
            m_config.createDistanceConfig(inDataSpec);
        }

        return new PortObjectSpec[]{inSpecs[0]};
    }

    @Override
    protected PortObject[] execute(final PortObject[] inData, final ExecutionContext exec) throws Exception {
        exec.setMessage("Starting OPTICS...");
        DataTableSpec inDataSpec = (DataTableSpec)inData[PORT_IN_DATA].getSpec();
        final DistanceMeasurePortObject distMat;

        final BufferedDataTable dataTable = (BufferedDataTable)inData[PORT_IN_DATA];
        if (inData[PORT_IN_DIST_MAT] != null) {
            distMat = (DistanceMeasurePortObject)inData[PORT_IN_DIST_MAT];
        } else {
            distMat = new DistanceMeasurePortObject(m_config.createDistanceConfig(inDataSpec));
        }

        final double eps = m_config.getEps();
        final int minPts = m_config.getminPTS();

        DistanceMeasure<?> distMeas = distMat.createDistanceMeasure(inDataSpec, this);
        Iterable<DataRow> data = dataTable;

        double clusterComputationWorkProgress = 0.3;

        ExecutionMonitor clusterComputation = exec.createSubProgress(clusterComputationWorkProgress);

        ArrayList<OptPoint> intermediateTable = new ArrayList<>();
        long index = 0;
        List<OptPoint> tableAsOptPointList = new ArrayList<>();
        Hashtable<Long, OptPoint> pointMap = new Hashtable<>();
        double progressBar = 0;

        // Transform data table into a list of OptPoint objects
        exec.setMessage("Computing Neighbors");
        for (DataRow rowX : data) {
            OptPoint curPoint = new OptPoint(index);
            HashMap<Long, Double> neighb = new HashMap<>((int)dataTable.size(), 1.0f);
            neighb = findNeighbors(rowX, eps, data, dataTable.size(), distMeas, exec, pointMap);
            // A cluster is formed if there are minPts number of points, a
            // center is included.
            // Therefore, a number of neighbors should be >= minPts-1
            if (neighb.size() >= minPts - 1) {
                curPoint.setNeighbors(neighb);
                double coreDist = findCoreDistance(minPts, curPoint.getNeighbors());
                curPoint.setCoredist(coreDist);
                curPoint.setNeighbors(updateReachDist(coreDist, neighb));
            }
            tableAsOptPointList.add(curPoint);
            pointMap.put(index, curPoint);
            progressBar = index / (double)dataTable.size();
            clusterComputation.setProgress(progressBar, "Reading row: " + rowX.getKey());
            clusterComputation.checkCanceled();
            index++;
        }

        // Basing on computed list of OptPoint, create an intermediate table
        // with points arranged by reachability distance
        exec.setMessage("Computing Arranged List of Points");
        ExecutionMonitor reachDistComputation = exec.createSubProgress(0.7);

        Comparator<OptPoint> comparator = new PointComparator();
        PriorityQueue<OptPoint> seeds = new PriorityQueue<>(149, comparator);
        long rowID = 0;
        double progressBar2 = 0;
        for (OptPoint point : tableAsOptPointList) {
            progressBar2 = rowID / (double)tableAsOptPointList.size();
            reachDistComputation.setProgress(progressBar2, "Processing row: " + tableAsOptPointList.indexOf(point));
            reachDistComputation.checkCanceled();
            if (point.isSeen()) {
                continue;
            }
            point.setSeen(true);
            Set<Long> keySet = point.getNeighbors().keySet();
            for (Long i : keySet) {
                if (!pointMap.get(i).isSeen()) {
                    OptPoint pointFromNeighb = null;
                    pointFromNeighb = pointMap.get(i);
                    pointFromNeighb.setReachdist(point.getNeighbors().get(i));
                    seeds.add(pointFromNeighb);
                }
            }
            intermediateTable.add(point);
            OptPoint pollPoint = null;
            while (!seeds.isEmpty()) {
                pollPoint = seeds.poll();
                updateSeeds(pollPoint, seeds, pointMap);
                pollPoint.setSeen(true);
                intermediateTable.add(pollPoint);
            }
            rowID++;
        }

        // Write down the table to Intermediate Table output
        BufferedDataContainer container2 = exec.createDataContainer(middleOutputSpec());
        int M = middleOutputSpec().getNumColumns();
        for (int j = 0; j < intermediateTable.size(); j++) {
            RowKey key = new RowKey("Row " + intermediateTable.get(j).getID());
            DataCell[] cells = new DataCell[M];
            cells[0] = new IntCell((int)intermediateTable.get(j).getID());
            cells[1] = new DoubleCell(intermediateTable.get(j).getCoredist());
            cells[2] = new DoubleCell(intermediateTable.get(j).getReachdist());
            DataRow row = new DefaultRow(key, cells);
            container2.addRowToTable(row);
            exec.checkCanceled();
        }
        container2.close();
        m_intermediateTable = container2.getTable();
        m_optPointContainer = intermediateTable;
        return new PortObject[]{new OPTICSPortObject(m_intermediateTable.getSpec(),
            m_optPointContainer.toArray(new OptPoint[m_optPointContainer.size()]), m_config.getEps())};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void reset() {
        m_optPointContainer = null;
        m_intermediateTable = null;
    }

    /**
     * Determines which rows in <b>dataTable</b> are within <b>eps</b> and are therefore considered neighbors.
     * Calculated a reachability distance between a current point and all others.
     *
     *
     * @param rowX the row in the dataTable begin examined
     * @param eps the query distance
     * @param dataTable the table for comparison
     * @param rowCount the row count
     * @param distMeas the {@link DistanceMeasure} being used to determine the neighborhood
     * @param exec the current {@link ExecutionContext}
     * @param pointMap is a dictionary, that preserves and maps all OptPoint objects to their ID
     * @return {@link HashMap} with values set as a pair of rowID and distance between current row and a neighbor
     * @throws DistanceMeasurementException
     * @throws CanceledExecutionException
     */
    private static HashMap<Long, Double> findNeighbors(final DataRow rowX, final double eps,
        final Iterable<DataRow> dataTable, final long rowCount, final DistanceMeasure<?> distMeas,
        final ExecutionContext exec, final Hashtable<Long, OptPoint> pointMap)
        throws DistanceMeasurementException, CanceledExecutionException {

        HashMap<Long, Double> neighbors = new HashMap<>((int)rowCount, 1.0f);
        long innerIndex = 0;
        for (DataRow rowY : dataTable) {
            double reachDist = distMeas.computeDistance(rowX, rowY);
            if (reachDist <= eps && reachDist != 0.0) {
                neighbors.put(innerIndex, reachDist);
            }
            exec.checkCanceled();
            innerIndex++;
        }
        return neighbors;
    }

    /**
     * Calculates core distance of a point which is a distance between it and a MinPts-th point
     *
     *
     * @param minPts minimal number of points that form a cluster
     * @param neighbors that are represented in a map key:distance
     * @return {@link double} value of a core distance
     */
    private static double findCoreDistance(final int minPts, final HashMap<Long, Double> neighbors) {
        ArrayList<Double> values = new ArrayList<>(neighbors.values());
        Collections.sort(values);
        // indexes start from 0 AND a center (core) point is also included in
        // neighbors calculations, so we need (minPts - 2) index
        return values.get(minPts - 2);
    }

    /**
     * Updates reachability distance of each point: if it is less than a core distance of its core point it should be
     * extended to the value of core distance.
     *
     *
     * @param coreDist a core distance of the current point
     * @param neighbors that are represented in a map key:distance
     * @return {@link HashMap} with updated points' reachability distances
     */
    private static HashMap<Long, Double> updateReachDist(final double coreDist, final HashMap<Long, Double> neighbors) {
        ArrayList<Double> values = new ArrayList<>(neighbors.values());
        ArrayList<Long> keys = new ArrayList<>(neighbors.keySet());
        // neighborsUpdate needs to be a little bit bigger than neighbors, so
        // the results fit the structure
        HashMap<Long, Double> neighborsUpdate = new HashMap<>(neighbors.size() + 1, 1.0f);
        for (int i = 0; i < values.size(); i++) {
            Double cur = Math.max(coreDist, values.get(i));
            neighborsUpdate.put(keys.get(i), cur);
        }
        return neighborsUpdate;
    }

    /**
     * Checks if points in the neighborhood are present in priority queue. If not it adds a point, if yes and in a queue
     * a point has a reachability distance greater that between a current point and it, the distance is updated.
     *
     *
     * @param point a current point being observed
     * @param seeds priority queue
     * @param pointMap a dictionary with all OptPoint objects to get a main info about each point in the neighborhood.
     *
     *
     */
    private static void updateSeeds(final OptPoint point, final PriorityQueue<OptPoint> seeds,
        final Hashtable<Long, OptPoint> pointMap) {
        // instead of queue we make a HashSet
        HashMap<Long, OptPoint> seedsList = new HashMap<>();
        // create a list with ID of points in seeds
        Set<Long> seedsListID = new HashSet<>();
        for (OptPoint p : seeds) {
            seedsListID.add(p.getID());
            seedsList.put(p.getID(), p);
        }
        Set<Long> keysOfNeighbors = point.getNeighbors().keySet();
        // add neighbors of the point to seeds
        for (Long key : keysOfNeighbors) {
            // calculate required parameters for each point
            double rdist = point.getNeighbors().get(key);
            // take all main info from the dictionary
            OptPoint pointInNeighb = null;
            //OptPoint pointInNeighb = new OPTICSComputeNodeModel(). new OptPoint();
            pointInNeighb = pointMap.get(key);
            if (!pointInNeighb.isSeen()) {
                if (seedsListID.contains(pointInNeighb.getID())) {
                    OptPoint pointInSeeds = seedsList.get(key);
                    if (pointInSeeds.getReachdist() > rdist) {
                        seeds.remove(pointInNeighb);
                        pointInNeighb.setReachdist(rdist);
                        seeds.add(pointInNeighb);
                    }
                } else {
                    pointInNeighb.setReachdist(rdist);
                    seeds.add(pointInNeighb);
                }
            }
        }
    }

    /**
     * Creates the table spec for the intermediate calculations table
     *
     * @return the table spec for intermediate table
     */
    private static DataTableSpec middleOutputSpec() {
        DataColumnSpec[] midCalcColSpec = new DataColumnSpec[3];

        midCalcColSpec[0] = new DataColumnSpecCreator("Point ID", IntCell.TYPE).createSpec();
        midCalcColSpec[1] = new DataColumnSpecCreator("Core Distance", DoubleCell.TYPE).createSpec();
        midCalcColSpec[2] = new DataColumnSpecCreator("Reachability Distance", DoubleCell.TYPE).createSpec();
        return new DataTableSpec(midCalcColSpec);
    }

    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) {
        m_config.saveConfiguration(settings);
    }

    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings) throws InvalidSettingsException {
        m_config = new OPTICSComputeViewConfig();
        m_config.loadConfigurationInModel(settings);
    }

    @Override
    protected void validateSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        new OPTICSComputeViewConfig().loadConfigurationInModel(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JSONLayoutViewContent getLayoutTemplate() {
        return null;
    }

    @Override
    protected void saveInternals(final File nodeInternDir, final ExecutionMonitor exec)
        throws IOException, CanceledExecutionException {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadInternals(final File nodeInternDir, final ExecutionMonitor exec)
        throws IOException, CanceledExecutionException {
    }

    /** Comparator for PriorityQueue */
    final static class PointComparator implements Comparator<OptPoint> {

        @Override
        public int compare(final OptPoint o1, final OptPoint o2) {
            return Double.compare(o1.getReachdist(), o2.getReachdist());
        }
    }

}
