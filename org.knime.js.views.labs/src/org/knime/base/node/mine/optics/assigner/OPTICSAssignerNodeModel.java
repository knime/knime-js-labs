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
package org.knime.base.node.mine.optics.assigner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.knime.base.data.xml.SvgCell;
import org.knime.base.node.mine.optics.OPTICSPortObject;
import org.knime.base.node.mine.optics.compute.OptPoint;
import org.knime.base.util.flowvariable.FlowVariableProvider;
import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.RowKey;
import org.knime.core.data.container.ColumnRearranger;
import org.knime.core.data.container.SingleCellFactory;
import org.knime.core.data.def.BooleanCell;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.data.def.DoubleCell;
import org.knime.core.data.def.IntCell;
import org.knime.core.data.def.LongCell;
import org.knime.core.data.def.StringCell;
import org.knime.core.data.vector.bitvector.DenseBitVector;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortType;
import org.knime.core.node.port.image.ImagePortObject;
import org.knime.core.node.port.image.ImagePortObjectSpec;
import org.knime.core.node.port.inactive.InactiveBranchPortObjectSpec;
import org.knime.core.node.web.ValidationError;
import org.knime.core.util.MutableInteger;
import org.knime.js.core.datasets.JSONKeyedValues2DDataset;
import org.knime.js.core.datasets.JSONKeyedValuesRow;
import org.knime.js.core.layout.LayoutTemplateProvider;
import org.knime.js.core.layout.bs.JSONLayoutViewContent;
import org.knime.js.core.layout.bs.JSONLayoutViewContent.ResizeMethod;
//import org.knime.distmatrix.calculate2.DistanceMatrixCalculate2Config;
import org.knime.js.core.node.AbstractSVGWizardNodeModel;
import org.knime.js.core.node.CSSModifiable;

/**
 * This is the model implementation of OPTICS.
 *
 * @author Anastasia Zhukova, KNIME GmbH, Konstanz, Germany
 * @author Oliver Sampson, University of Konstanz, Germany
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 */
public class OPTICSAssignerNodeModel extends AbstractSVGWizardNodeModel<OPTICSAssignerViewRepresentation,
        OPTICSAssignerViewValue> implements FlowVariableProvider, LayoutTemplateProvider, CSSModifiable {

    private static final int PORT_IN_MODEL = 0;

    private static final int PORT_IN_DATA = 1;

    private static final String CLUSTER_ID_PREFIX = "Cluster_";

    private static final String CLUSTER_ID_NOISE = "Noise";

    private BufferedDataTable m_assignedOutputTable;

    private BufferedDataTable m_summary;

    private OPTICSAssignerViewConfig m_config;

    double m_epsPrime;

    double m_eps;

    /**
     * Constructor for the node model.
     */
    protected OPTICSAssignerNodeModel() {
        super(new PortType[]{OPTICSPortObject.TYPE, BufferedDataTable.TYPE},
            new PortType[]{ImagePortObject.TYPE, BufferedDataTable.TYPE, BufferedDataTable.TYPE}, "OPTICS-plot");
        m_config = new OPTICSAssignerViewConfig();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PortObjectSpec[] configure(final PortObjectSpec[] inSpecs) throws InvalidSettingsException {
        ColumnRearranger createColumnRearranger = null;

        if (m_config.getEnableSelection()) {
            createColumnRearranger = createColumnRearranger((DataTableSpec)inSpecs[PORT_IN_DATA], null,
                new MutableInteger(0), new ArrayList<String>(), true);
        } else {
            createColumnRearranger =
                createColumnRearranger((DataTableSpec)inSpecs[PORT_IN_DATA], null, new MutableInteger(0), null, false);
        }

        PortObjectSpec image;
        if (generateImage()) {
            image = new ImagePortObjectSpec(SvgCell.TYPE);
        } else {
            image = InactiveBranchPortObjectSpec.INSTANCE;
        }

        return new PortObjectSpec[]{image, createColumnRearranger.createSpec(), createSummarySpec()};
    }

    @Override
    protected void performExecuteCreateView(final PortObject[] inData, final ExecutionContext exec) throws Exception {
        exec.setMessage("Starting OPTICS Assigner...");

        OPTICSAssignerViewRepresentation representation = getViewRepresentation();
        OPTICSAssignerViewValue viewValue = getViewValue();

        DataTableSpec inDataSpec = (DataTableSpec)inData[PORT_IN_DATA].getSpec();
        final BufferedDataTable dataTable = (BufferedDataTable)inData[PORT_IN_DATA];
        final OPTICSPortObject modelData = (OPTICSPortObject)inData[PORT_IN_MODEL];

        if (dataTable.size() != modelData.getOptPoints().length) {
            throw new InvalidSettingsException("The length of the model doesn't " + "correspond to the given data. ("
                + String.valueOf(dataTable.size()) + " != " + String.valueOf(modelData.getOptPoints().length) + ")");
        }

        m_eps = modelData.getEps();

        switch (m_config.getEpsCalcMethod()) {
            case OPTICSAssignerViewConfig.CFG_CALC_EPS_PRIME_MEAN:
                m_epsPrime = meanEpsPrime(modelData.getOptPoints());
                break;
            case OPTICSAssignerViewConfig.CFG_CALC_EPS_PRIME_MEDIAN:
                m_epsPrime = medianEpsPrime(modelData.getOptPoints());
                break;
            case OPTICSAssignerViewConfig.CFG_MANUAL_EPS_PRIME:
                m_epsPrime = m_config.getEpsPrime();
                break;
        }

        if (m_epsPrime > m_eps) {
            throw new InvalidSettingsException("Epsilon-prime is greater than epsilon: " + m_epsPrime + " > " + m_eps);
        }

        long n = dataTable.size();
        OptPoint[] intermediateTable = modelData.getOptPoints();

        exec.setMessage("Computing Arranged List of Points");

        // Write down the table to Intermediate Table output
        List<JSONKeyedValuesRow> rows = new ArrayList<JSONKeyedValuesRow>();
        for (int j = 0; j < intermediateTable.length; j++) {
            // JSON row
            String j_key = "Row " + intermediateTable[j].getID();
            rows.add(new JSONKeyedValuesRow(j_key, new Double[]{(double)intermediateTable[j].getID(),
                intermediateTable[j].getCoredist(), intermediateTable[j].getReachdist()}));
        }

        // JSON table
        JSONKeyedValues2DDataset dataset = new JSONKeyedValues2DDataset(getTableId(0),
            new String[]{"Point ID", "Core Distance", "Reachability Distance"},
            rows.toArray(new JSONKeyedValuesRow[rows.size()]));

        // Final cluster computation
        exec.setMessage("Computing Clusters");
        List<DenseBitVector> clusters = new ArrayList<>();
        DenseBitVector new_cluster = new DenseBitVector(n);
        //HashMap<Long, String> test_clusters = new HashMap<>();
        //int idd = 0;
        for (OptPoint p : intermediateTable) {

            if (p.getReachdist() > m_epsPrime) {
                if (p.getCoredist() <= m_epsPrime) {
                    if (!new_cluster.isEmpty()) {
                        // write a previous cluster to an array
                        clusters.add(new_cluster);
                    }
                    //idd++;
                    //test_clusters.put(p.getID(), "Cluster_" + idd);
                    // start a new cluster
                    new_cluster = new DenseBitVector(n);
                    new_cluster.set(p.getID());
                } else {
                    //test_clusters.put(p.getID(), "Noise");
                }

            } else {
                new_cluster.set(p.getID());
                //test_clusters.put(p.getID(), "Cluster_" + idd);
            }
        }
        clusters.add(new_cluster);

        // Main output table
        ExecutionMonitor clusterAssigment = exec.createSubProgress(0.2);
        final MutableInteger noiseCounter = new MutableInteger(0);

        List<String> selectionList = null;
        boolean enableSel = false;
        if (m_config.getEnableSelection()) {
            if (viewValue != null && viewValue.getSelectedKeys() != null) {
                selectionList = Arrays.asList(viewValue.getSelectedKeys());
            }
            enableSel = true;
        }

        ColumnRearranger columnRearranger =
            createColumnRearranger(inDataSpec, clusters, noiseCounter, selectionList, enableSel);
        BufferedDataTable assignedOutputTable =
            exec.createColumnRearrangeTable(dataTable, columnRearranger, clusterAssigment);

        m_assignedOutputTable = assignedOutputTable;

        // Formation of Summary Table
        BufferedDataContainer summaryContainer = exec.createDataContainer(createSummarySpec());
        DataRow outRow = new DefaultRow(new RowKey("Noise"), new LongCell(noiseCounter.longValue()));
        summaryContainer.addRowToTable(outRow);
        for (int i = 0; i < clusters.size(); i++) {
            DenseBitVector cluster = clusters.get(i);
            String id = CLUSTER_ID_PREFIX + i;
            outRow = new DefaultRow(new RowKey(id), new LongCell(cluster.cardinality()));
            summaryContainer.addRowToTable(outRow);
        }
        summaryContainer.close();
        BufferedDataTable summary = summaryContainer.getTable();
        m_summary = summary;

        if (representation.getKeyedDataset() == null) {
            // create dataset for view
            copyConfigToView(m_epsPrime);
            representation.setKeyedDataset(dataset);
        }
        representation.setEps(m_eps);
    }

    /**
     * @param optPoints
     *
     */
    private double meanEpsPrime(final OptPoint[] optPoints) {
        double sum = 0;
        int i = 0;
        for (OptPoint p : optPoints) {
            if (p.getReachdist() != OptPoint.UNDEFINED) {
                sum += p.getReachdist();
                i++;
            }
        }
        return sum / i;
    }

    private double medianEpsPrime(final OptPoint[] optPoints) {
        if (optPoints == null || optPoints.length == 0) {
            return 0;
        }
        OptPoint[] sortOptPoint = new OptPoint[optPoints.length];
        System.arraycopy(optPoints, 0, sortOptPoint, 0, optPoints.length);
        Arrays.sort(sortOptPoint);
        ArrayList<OptPoint> noUndentified = new ArrayList<>(Arrays.asList(sortOptPoint));
        noUndentified.removeAll(Arrays.asList(OptPoint.UNDEFINED));
        if (noUndentified.size() % 2 == 0) {
            int i = noUndentified.size() / 2;
            return (noUndentified.get(i - 1).getReachdist() + noUndentified.get(i).getReachdist()) / 2;
        } else {
            return (noUndentified.get(noUndentified.size() / 2).getReachdist());
        }
    }

    @Override
    protected PortObject[] performExecuteCreatePortObjects(final PortObject svgImageFromView,
        final PortObject[] inObjects, final ExecutionContext exec) throws Exception {
        BufferedDataTable table1 = m_assignedOutputTable;
        BufferedDataTable table3 = m_summary;
        synchronized (getLock()) {
            OPTICSAssignerViewRepresentation representation = getViewRepresentation();
            representation.setResizeToWindow(m_config.getResizeToWindow());

            //OPTICSViewValue viewValue = getViewValue();
            //            if (m_config.getEnableSelection()) {
            //                //List<String> selectionList = null;
            //                if (viewValue != null && viewValue.getSelectedKeys() != null) {
            //                    List<String> selectionList = Arrays.asList(viewValue.getSelectedKeys());
            //                }
            //                //ColumnRearranger rearranger = createColumnAppender(m_table.getDataTableSpec(), selectionList);
            //                //out = exec.createColumnRearrangeTable(m_table, rearranger, exec);
            //            }
            //setSubscriptionFilterIds(m_table.getDataTableSpec());
        }
        return new PortObject[]{svgImageFromView, table1, table3};
    }

    /**
     * Creates the {@link ColumnRearranger} for outputting the cluster identification for each row.
     *
     * @param inDataSpec the {@link DataTableSpec} of the input data Table
     * @param clusters the {@link List} of {@link DenseBitVector} which shows which row is assigned to the indexed
     *            cluster.
     * @return {@link ColumnRearranger} which outputs the new column for each row
     */
    private static ColumnRearranger createColumnRearranger(final DataTableSpec inDataSpec,
        final List<DenseBitVector> clusters, final MutableInteger noiseCounter, final List<String> selectionList,
        final boolean enableSel) {
        ColumnRearranger columnRearranger = new ColumnRearranger(inDataSpec);

        columnRearranger.append(new SingleCellFactory(
            new DataColumnSpecCreator(DataTableSpec.getUniqueColumnName(inDataSpec, "Cluster"), StringCell.TYPE)
                .createSpec()) {
            private long m_rowIndex = 0;

            @Override
            public DataCell getCell(final DataRow row) {

                int clusterIndex = -1;
                for (int i = 0; i < clusters.size(); i++) {
                    if (clusters.get(i).get(m_rowIndex)) {
                        clusterIndex = i;
                    }
                }
                // count the noise points
                if (clusterIndex == -1) {
                    noiseCounter.inc();
                }

                m_rowIndex++;
                String resultClusterName = clusterIndex >= 0 ? CLUSTER_ID_PREFIX + clusterIndex : CLUSTER_ID_NOISE;
                return new StringCell(resultClusterName);
            }
        });
        if (enableSel) {
            columnRearranger.append(new SingleCellFactory(
                new DataColumnSpecCreator(DataTableSpec.getUniqueColumnName(inDataSpec, "Selection"), BooleanCell.TYPE)
                    .createSpec()) {

                @Override
                public DataCell getCell(final DataRow row) {
                    if (selectionList != null) {
                        if (!selectionList.isEmpty()) {
                            if (selectionList.contains(row.getKey().toString())) {
                                return BooleanCell.TRUE;
                            }
                            return BooleanCell.FALSE;
                        }
                        return BooleanCell.FALSE;
                    }
                    return BooleanCell.FALSE;
                }
            });
        }

        return columnRearranger;
    }

    /**
     * Creates the table spec for the summary table.
     *
     * @return the table spec for the cluster summary
     */
    private static DataTableSpec createSummarySpec() {
        DataColumnSpec[] outSummarySpec = new DataColumnSpec[1];
        outSummarySpec[0] = new DataColumnSpecCreator("Count", LongCell.TYPE).createSpec();
        return new DataTableSpec(outSummarySpec);
    }

    /**
     * Creates the table spec for the intermediate calculations table
     *
     * @return the table spec for intermediate table
     */
    protected static DataTableSpec middleOutputSpec() {
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
        m_config = new OPTICSAssignerViewConfig();
        m_config.loadConfigurationInModel(settings);
    }

    @Override
    protected void validateSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        new OPTICSAssignerViewConfig().loadConfigurationInModel(settings);
    }

    @Override
    public OPTICSAssignerViewRepresentation createEmptyViewRepresentation() {
        return new OPTICSAssignerViewRepresentation();
    }

    @Override
    public OPTICSAssignerViewValue createEmptyViewValue() {
        return new OPTICSAssignerViewValue();
    }

    @Override
    public String getJavascriptObjectID() {
        return "org_knime_base_node_mine_optics";
    }

    @Override
    public boolean isHideInWizard() {
        return m_config.getHideInWizard();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setHideInWizard(final boolean hide) {
        m_config.setHideInWizard(hide);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCssStyles() {
        return m_config.getCustomCSS();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCssStyles(final String styles) {
        m_config.setCustomCSS(styles);
    }

    @Override
    public ValidationError validateViewValue(final OPTICSAssignerViewValue viewContent) {
        return null;
    }

    @Override
    public void saveCurrentValue(final NodeSettingsWO content) {
    }

    @Override
    protected boolean generateImage() {
        return m_config.getGenerateImage();
    }

    @Override
    protected void performReset() {
        m_summary = null;
        m_assignedOutputTable = null;
    }

    @Override
    protected void useCurrentValueAsDefault() {
        synchronized (getLock()) {
            copyValueToConfig();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JSONLayoutViewContent getLayoutTemplate() {
        JSONLayoutViewContent template = new JSONLayoutViewContent();
        if (m_config.getResizeToWindow()) {
            template.setResizeMethod(ResizeMethod.ASPECT_RATIO_16by9);
        } else {
            template.setResizeMethod(ResizeMethod.VIEW_LOWEST_ELEMENT);
        }
        return template;
    }

    private void copyConfigToView(final double eps_prime) {
        OPTICSAssignerViewRepresentation representation = getViewRepresentation();
        representation.setEnableViewConfiguration(m_config.getEnableViewConfiguration());
        representation.setEnableTitleChange(m_config.getEnableTitleChange());
        representation.setEnableEpsilonPrimeChange(m_config.getEnableEpsilonPrimeChange());
        representation.setEnableSelection(m_config.getEnableSelection());
        representation.setEnableRectangleSelection(m_config.getEnableRectangleSelection());
        representation.setImageWidth(m_config.getImageWidth());
        representation.setImageHeight(m_config.getImageHeight());
        representation.setShowWarningInView(m_config.getShowWarningInView());
        representation.setDisplayFullscreenButton(m_config.getDisplayFullscreenButton());
        representation.setEnableShowSelectedOnly(m_config.getEnableShowSelectedOnly());
        representation.setMaxBins(m_config.getMaxRows());
        representation.setResizeToWindow(m_config.getResizeToWindow());

        OPTICSAssignerViewValue viewValue = getViewValue();
        if (isViewValueEmpty()) {
            viewValue.setChartTitle(m_config.getChartTitle());
            viewValue.setChartSubtitle(m_config.getChartSubtitle());
            viewValue.setPublishSelection(m_config.getPublishSelection());
            viewValue.setSubscribeSelection(m_config.getSubscribeSelection());
            viewValue.setEpsPrime(eps_prime);
            viewValue.setCalcEpsPrimeMean(m_config.getCalcEpsPrimeMean());
            viewValue.setCalcEpsPrimeMedian(m_config.getCalcEpsPrimeMedian());
            viewValue.setManualEpsPrime(m_config.getManualEpsPrime());
            viewValue.setEpsCalcMethod(m_config.getEpsCalcMethod());
        }

    }

    private void copyValueToConfig() {
        OPTICSAssignerViewValue viewValue = getViewValue();
        m_config.setChartTitle(viewValue.getChartTitle());
        m_config.setChartSubtitle(viewValue.getChartSubtitle());
        m_config.setEpsPrime(viewValue.getEpsPrime());
        m_config.setCalcEpsPrimeMean(viewValue.getCalcEpsPrimeMean());
        m_config.setCalcEpsPrimeMedian(viewValue.getCalcEpsPrimeMedian());
        m_config.setManualEpsPrime(viewValue.getManualEpsPrime());
        m_config.setEpsCalcMethod(viewValue.getEpsCalcMethod());
        m_config.setPublishSelection(viewValue.getPublishSelection());
        m_config.setSubscribeSelection(viewValue.getSubscribeSelection());
    }

}
