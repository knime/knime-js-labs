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
 *   12.04.2019 (Ben Laney): created
 */
package org.knime.js.base.node.viz.pdpiceplot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.knime.base.data.xml.SvgCell;
import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataType;
import org.knime.core.data.collection.CollectionCellFactory;
import org.knime.core.data.collection.ListCell;
import org.knime.core.data.container.ColumnRearranger;
import org.knime.core.data.container.SingleCellFactory;
import org.knime.core.data.def.BooleanCell;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.data.def.DoubleCell;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.BufferedDataTableHolder;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
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
import org.knime.core.node.wizard.CSSModifiable;
import org.knime.js.core.JSONDataTable;
import org.knime.js.core.layout.LayoutTemplateProvider;
import org.knime.js.core.layout.bs.JSONLayoutViewContent;
import org.knime.js.core.layout.bs.JSONLayoutViewContent.ResizeMethod;
import org.knime.js.core.node.AbstractSVGWizardNodeModel;

/**
 *
 * @author Ben Laney, KNIME GmbH, Konstanz, Germany
 */
public class PartialDependenceICEPlotNodeModel extends
    AbstractSVGWizardNodeModel<PartialDependenceICEPlotNodeViewRepresentation, PartialDependenceICEPlotViewValue>
    implements CSSModifiable, BufferedDataTableHolder, LayoutTemplateProvider {

    private static final String ROW_LIMITATION_WARNING_ID_STRING = "rowLimit";

    private final PartialDependenceICEPlotConfig m_config;

    private BufferedDataTable m_modelOutputTable;

    private BufferedDataTable m_originalDataTable;

    private String[] m_sampledFeatures;

    private Integer[] m_featureColModelIndicies;

    private Integer[] m_featureColOrigIndicies;

    private double[][] m_featureDomains;

    private String[] m_predictionColumns;

    private int[] m_predictionColModelIndicies;

    private double[][] m_predictionDomains;

    private String m_rowIDColName;

    private int m_rowIDColInd;

    private boolean m_isApplyingSettings = false;

    /**
     * @param viewName
     */
    public PartialDependenceICEPlotNodeModel(final String viewName) {
        super(new PortType[]{BufferedDataTable.TYPE, BufferedDataTable.TYPE},
            new PortType[]{ImagePortObject.TYPE, BufferedDataTable.TYPE}, viewName);
        m_config = new PartialDependenceICEPlotConfig();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PortObjectSpec[] configure(final PortObjectSpec[] inSpecs) throws InvalidSettingsException {

        DataTableSpec modelOutputSpec =
            (DataTableSpec)inSpecs[PartialDependenceICEPlotConfig.MODEL_OUTPUT_TABLE_INPORT];
        DataTableSpec originalDataTableSpec =
            (DataTableSpec)inSpecs[PartialDependenceICEPlotConfig.ORIGINAL_DATA_TABLE_INPORT];

        if (modelOutputSpec == null || originalDataTableSpec == null) {
            throw new InvalidSettingsException("One or more tableSpecs is missing from the tables provided.\n"
                + " Please provide properly formatted data tables to this node.");
        }

        m_rowIDColName = m_config.getRowIDCol();

        if (m_rowIDColName == null) {
            throw new InvalidSettingsException("Please choose the correct RowID column from the model output table");
        }

        m_rowIDColInd = modelOutputSpec.findColumnIndex(m_rowIDColName);

        m_sampledFeatures = m_config.getSampledFeatureColumns().applyTo(modelOutputSpec).getIncludes();
        m_predictionColumns = m_config.getPredictionColumns().applyTo(modelOutputSpec).getIncludes();

        //check validity of selected "sample" and "prediction" columns
        if (m_sampledFeatures == null || m_sampledFeatures.length < 1 || m_predictionColumns == null
            || m_predictionColumns.length < 1) {
            throw new InvalidSettingsException("The model output table provided does not contain any numeric columns");
        }

        int numFeatureCols = m_sampledFeatures.length;
        m_featureColModelIndicies = new Integer[numFeatureCols];
        m_featureColOrigIndicies = new Integer[numFeatureCols];
        m_featureDomains = new double[numFeatureCols][2];
        Set<String> featureSet = new HashSet<String>(Arrays.asList(m_sampledFeatures));

        int numPredictionCols = m_predictionColumns.length;
        m_predictionColModelIndicies = new int[numPredictionCols];
        m_predictionDomains = new double[numPredictionCols][2];
        Set<String> predictionSet = new HashSet<String>(Arrays.asList(m_predictionColumns));

        //get proper information about EACH chosen "sampled" feature
        int count = 0;

        for (String sampFeat : m_sampledFeatures) {

            //ensure the prediction column hasn't been chosen for a feature
            if (predictionSet.contains(sampFeat)) {
                throw new InvalidSettingsException("The columns selected "
                    + "cannot be duplicated as both features AND prediction columns.\nPlease correct your selections in"
                    + " the column selection panels.");
            }

            int origTableColInd = originalDataTableSpec.findColumnIndex(sampFeat);
            int modelTableColInd = modelOutputSpec.findColumnIndex(sampFeat);

            //collect proper information for internal fields
            m_featureColModelIndicies[count] = modelTableColInd;
            m_featureColOrigIndicies[count] = origTableColInd;

            //initialize min/max values for each feature column domain
            m_featureDomains[count] = new double[]{Double.MAX_VALUE, -Double.MAX_VALUE};

            if (generateImage()) {

                if (modelTableColInd == m_config.getFeatureInd()) {
                    m_config.setFeatureInd(origTableColInd);
                }
            }

            count++;
        }

        //get proper information about EACH chosen "prediction" feature
        count = 0;

        for (String predCol : m_predictionColumns) {

            //ensure the prediction column hasn't been chosen for a feature
            if (featureSet.contains(predCol)) {
                throw new InvalidSettingsException("The columns selected "
                    + "cannot be duplicated as both features AND prediction columns.\nPlease correct your selections in"
                    + " the column selection panels.");
            }

            int modelTableColInd = modelOutputSpec.findColumnIndex(predCol);

            //collect proper information for internal fields
            m_predictionColModelIndicies[count] = modelTableColInd;

            //initialize min/max values for each prediction column domain
            m_predictionDomains[count] = new double[]{Double.MAX_VALUE, -Double.MAX_VALUE};

            if (generateImage()) {
                if (modelTableColInd == m_config.getPredictionInd()) {
                    m_config.setPredictionInd(count);
                }
            }

            count++;
        }

        DataTableSpec outTableSpec = originalDataTableSpec;
        PortObjectSpec imageSpec = InactiveBranchPortObjectSpec.INSTANCE;

        //modify outgoing spec if column will be appended
        if (m_config.getEnableSelection()) {
            ColumnRearranger columnRearranger = createColumnAppender(originalDataTableSpec, null);
            outTableSpec = columnRearranger.createSpec();
        }

        //modify outgoing spec if image will be generated
        if (generateImage()) {
            imageSpec = new ImagePortObjectSpec(SvgCell.TYPE);
        }

        return new PortObjectSpec[]{imageSpec, outTableSpec};
    }

    /**
     * called if selection is enabled to create the "Selected" column
     *
     * @param spec
     * @param selectionList
     * @return ColumnRearranger
     */
    private ColumnRearranger createColumnAppender(final DataTableSpec spec, final Collection<String> selectionList) {

        String selectedColName = PartialDependenceICEPlotConfig.SELECTED_COLUMN_NAME;
        selectedColName = DataTableSpec.getUniqueColumnName(spec, selectedColName);
        DataColumnSpec selectedColumnSpec =
            new DataColumnSpecCreator(selectedColName, DataType.getType(BooleanCell.class)).createSpec();
        ColumnRearranger colRearranger = new ColumnRearranger(spec);
        SingleCellFactory fac = new SingleCellFactory(selectedColumnSpec) {

            private int m_rowIndex = 0;

            @Override
            public DataCell getCell(final DataRow row) {
                if (++m_rowIndex > m_config.getMaxNumRows()) {
                    return DataType.getMissingCell();
                }
                if (selectionList != null) {
                    if (selectionList.contains(row.getKey().toString())) {
                        return BooleanCell.TRUE;
                    }
                }
                return BooleanCell.FALSE;
            }
        };

        colRearranger.append(fac);
        return colRearranger;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PartialDependenceICEPlotNodeViewRepresentation createEmptyViewRepresentation() {
        return new PartialDependenceICEPlotNodeViewRepresentation();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PartialDependenceICEPlotViewValue createEmptyViewValue() {
        return new PartialDependenceICEPlotViewValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PartialDependenceICEPlotNodeViewRepresentation getViewRepresentation() {
        PartialDependenceICEPlotNodeViewRepresentation rep = super.getViewRepresentation();
        synchronized (getLock()) {
            if (rep.getDataTable() == null && m_originalDataTable != null) {
                m_isApplyingSettings = false;
                try {
                    final JSONDataTable jT = createJSONDataTable(null);
                    rep.setDataTable(jT);
                } catch (Exception e) {
                    //TODO: remove try catch
                }
            } else {
                m_isApplyingSettings = true;
            }
        }
        return rep;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void performReset() {
        m_modelOutputTable = null;
        m_originalDataTable = null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void useCurrentValueAsDefault() {
        synchronized (getLock()) {
            try {
                copyValueToConfig();
            } catch (InvalidSettingsException e) {
                setWarningMessage("Your settings may not have been saved: " + e.getMessage());
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean generateImage() {
        return m_config.getGenerateImage();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getJavascriptObjectID() {
        return "org.knime.js.base.node.viz.pdpiceplot";
    }

    /**
     * {@inheritDoc}
     */
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
    public void setInternalTables(final BufferedDataTable[] tables) {
        m_modelOutputTable = tables[PartialDependenceICEPlotConfig.MODEL_OUTPUT_TABLE_INPORT];
        m_originalDataTable = tables[PartialDependenceICEPlotConfig.ORIGINAL_DATA_TABLE_INPORT];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BufferedDataTable[] getInternalTables() {
        return new BufferedDataTable[]{m_modelOutputTable, m_originalDataTable};
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

    /**
     * {@inheritDoc}
     */
    @Override
    public ValidationError validateViewValue(final PartialDependenceICEPlotViewValue viewContent) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveCurrentValue(final NodeSettingsWO content) {
        // needed because of AbstractSVGWizard
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) {
        m_config.saveSettings(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        (new PartialDependenceICEPlotConfig()).loadSettings(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings) throws InvalidSettingsException {
        m_config.loadSettings(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JSONLayoutViewContent getLayoutTemplate() {
        JSONLayoutViewContent template = new JSONLayoutViewContent();
        if (m_config.getResizeToFill()) {
            template.setResizeMethod(ResizeMethod.ASPECT_RATIO_16by9);
        } else {
            template.setResizeMethod(ResizeMethod.VIEW_TAGGED_ELEMENT);
        }
        return template;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void performExecuteCreateView(final PortObject[] inData, final ExecutionContext exec) throws Exception {

        setInternalTables(
            new BufferedDataTable[]{(BufferedDataTable)inData[PartialDependenceICEPlotConfig.MODEL_OUTPUT_TABLE_INPORT],
                (BufferedDataTable)inData[PartialDependenceICEPlotConfig.ORIGINAL_DATA_TABLE_INPORT]});

        synchronized (getLock()) {

            PartialDependenceICEPlotNodeViewRepresentation viewRepresentation = getViewRepresentation();

            if (m_predictionColumns == null) {
                //TODO: handle hard resets with missing settings if it becomes an issue
            }

            if (!m_isApplyingSettings) {
                //fields that need to be set without dialog config
                viewRepresentation.setPredictionColumns(m_predictionColumns);
                viewRepresentation.setPredictionIndicies(m_predictionColModelIndicies);
                viewRepresentation.setSampledFeatureColumns(m_sampledFeatures);
                viewRepresentation.setPredictionColumns(m_predictionColumns);

                viewRepresentation.setMaxNumRows(m_config.getMaxNumRows());
                viewRepresentation.setGenerateImage(m_config.getGenerateImage());
                viewRepresentation.setRowIDCol(m_config.getRowIDCol());
                viewRepresentation.setViewWidth(m_config.getViewWidth());
                viewRepresentation.setViewHeight(m_config.getViewHeight());
                viewRepresentation.setResizeToFill(m_config.getResizeToFill());
                viewRepresentation.setFullscreenButton(m_config.getFullscreenButton());
                viewRepresentation.setEnablePanning(m_config.getEnablePanning());
                viewRepresentation.setEnableScrollZoom(m_config.getEnableScrollZoom());
                viewRepresentation.setEnableDragZoom(m_config.getEnableDragZoom());
                viewRepresentation.setShowZoomReset(m_config.getShowZoomReset());
                viewRepresentation.setEnableSelection(m_config.getEnableSelection());
                viewRepresentation.setEnableInteractiveCtrls(m_config.getEnableInteractiveCtrls());
                viewRepresentation.setShowWarnings(m_config.getShowWarnings());
                viewRepresentation.setEnableTitleControls(m_config.getEnableTitleControls());
                viewRepresentation.setEnableAxisLabelControls(m_config.getEnableAxisLabelControls());
                viewRepresentation.setEnablePDPControls(m_config.getEnablePDPControls());
                viewRepresentation.setEnablePDPMarginControls(m_config.getEnablePDPMarginControls());
                viewRepresentation.setEnableICEControls(m_config.getEnableICEControls());
                viewRepresentation.setEnableStaticLineControls(m_config.getEnableStaticLineControls());
                viewRepresentation.setEnableDataPointControls(m_config.getEnableDataPointControls());
                viewRepresentation.setEnableSelectionFilterControls(m_config.getEnableSelectionFilterControls());
                viewRepresentation.setEnableSelectionControls(m_config.getEnableSelectionControls());
                viewRepresentation.setEnableYAxisMarginControls(m_config.getEnableYAxisMarginControls());
                viewRepresentation.setEnableSmartZoomControls(m_config.getEnableSmartZoomControls());
                viewRepresentation.setEnableGridControls(m_config.getEnableGridControls());
                viewRepresentation.setEnableMouseCrosshairControls(m_config.getEnableMouseCrosshairControls());
                viewRepresentation.setEnableAdvancedOptionsControls(m_config.getEnableAdvancedOptionsControls());

                final JSONDataTable jsonTable = createJSONDataTable(exec);
                viewRepresentation.setDataTable(jsonTable);

                viewRepresentation.setFeatureDomains(m_featureDomains);
                viewRepresentation.setPredictionDomains(m_predictionDomains);
            }

            viewRepresentation.setRunningInView(false);

            PartialDependenceICEPlotViewValue viewValue = getViewValue();
            if (isViewValueEmpty()) {
                viewValue.setShowPDP(m_config.getShowPDP());
                viewValue.setPDPColor(PartialDependenceICEPlotConfig.getRGBAStringFromColor(m_config.getPDPColor()));
                viewValue.setPDPLineWeight(m_config.getPDPLineWeight());
                viewValue.setShowPDPMargin(m_config.getShowPDPMargin());
                viewValue.setPDPMarginType(m_config.getPDPMarginType());
                viewValue.setPDPMarginMultiplier(m_config.getPDPMarginMultiplier());
                viewValue.setPDPMarginAlphaVal(m_config.getPDPMarginAlphaVal());
                viewValue.setShowICE(m_config.getShowICE());
                viewValue.setICEColor(PartialDependenceICEPlotConfig.getRGBAStringFromColor(m_config.getICEColor()));
                viewValue.setICEWeight(m_config.getICEWeight());
                viewValue.setICEAlphaVal(m_config.getICEAlphaVal());
                viewValue.setShowDataPoints(m_config.getShowDataPoints());
                viewValue.setDataPointColor(
                    PartialDependenceICEPlotConfig.getRGBAStringFromColor(m_config.getDataPointColor()));
                viewValue.setDataPointWeight(m_config.getDataPointWeight());
                viewValue.setDataPointAlphaVal(m_config.getDataPointAlphaVal());
                viewValue.setXAxisLabel(m_config.getXAxisLabel());
                viewValue.setYAxisLabel(m_config.getYAxisLabel());
                viewValue.setChartTitle(m_config.getChartTitle());
                viewValue.setChartSubtitle(m_config.getChartSubtitle());
                viewValue.setShowGrid(m_config.getShowGrid());
                viewValue.setSubscribeToSelection(m_config.getSubscribeToSelection());
                viewValue.setPublishSelection(m_config.getPublishSelection());
                viewValue.setSubscribeToFilters(m_config.getSubscribeToFilters());
                viewValue.setShowStaticLine(m_config.getShowStaticLine());
                viewValue.setStaticLineColor(
                    PartialDependenceICEPlotConfig.getRGBAStringFromColor(m_config.getStaticLineColor()));
                viewValue.setStaticLineWeight(m_config.getStaticLineWeight());
                viewValue.setStaticLineYValue(m_config.getStaticLineYValue());
                viewValue.setEnableMouseCrosshair(m_config.getEnableMouseCrosshair());
                viewValue.setBackgroundColor(
                    PartialDependenceICEPlotConfig.getRGBAStringFromColor(m_config.getBackgroundColor()));
                viewValue.setDataAreaColor(
                    PartialDependenceICEPlotConfig.getRGBAStringFromColor(m_config.getDataAreaColor()));
                viewValue.setGridColor(PartialDependenceICEPlotConfig.getRGBAStringFromColor(m_config.getGridColor()));
                viewValue.setYAxisMin(m_config.getYAxisMin());
                viewValue.setYAxisMax(m_config.getYAxisMax());
                viewValue.setYAxisMargin(m_config.getYAxisMargin());
                viewValue.setSelected(new String[0]);
                viewValue.setFeatureInd(m_config.getFeatureInd());
                viewValue.setPredictionInd(m_config.getPredictionInd());
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PortObject[] performExecuteCreatePortObjects(final PortObject svgImageFromView,
        final PortObject[] inObjects, final ExecutionContext exec) throws Exception {

        BufferedDataTable outTable = m_originalDataTable;

        synchronized (getLock()) {

            PartialDependenceICEPlotNodeViewRepresentation viewRepresentation = getViewRepresentation();

            Collection<String> selectedList = null;

            viewRepresentation.setRunningInView(true);

            if (m_config.getEnableSelection()) {
                PartialDependenceICEPlotViewValue viewValue = getViewValue();

                if (viewValue != null && viewValue.getSelected() != null) {
                    selectedList = Arrays.asList(viewValue.getSelected());
                }

                ColumnRearranger colRearranger =
                    createColumnAppender(m_originalDataTable.getDataTableSpec(), selectedList);

                outTable = exec.createColumnRearrangeTable(m_originalDataTable, colRearranger, exec);
            }
        }

        exec.setProgress(1);
        return new PortObject[]{svgImageFromView, outTable};
    }

    /**
     * @param exec
     * @return JSONDataTable
     */
    private JSONDataTable createJSONDataTable(final ExecutionContext exec)
        throws CanceledExecutionException, Exception {

        if (m_config.getMaxNumRows() < m_originalDataTable.size()) {
            String message = "Only the first " + m_config.getMaxNumRows() + " rows are displayed.";
            setWarningMessage(message);

            if (m_config.getShowWarnings()) {
                getViewRepresentation().getJSONWarnings().setWarningMessage(message, ROW_LIMITATION_WARNING_ID_STRING);
            }
        }

        DataTableSpec sampleCollectionsTableSpec = new DataTableSpec(sampleColSpecFactory());
        BufferedDataTable collectionTable = createCollectionTable(sampleCollectionsTableSpec, exec);
        BufferedDataTable newDataTable = exec.createJoinedTable(m_originalDataTable, collectionTable, exec);

        JSONDataTable jsonDataTable = JSONDataTable.newBuilder().setDataTable(newDataTable)
            .setId(getTableId(PartialDependenceICEPlotConfig.ORIGINAL_DATA_TABLE_INPORT)).setFirstRow(1)
            .setMaxRows(m_config.getMaxNumRows()).build(exec);

        //        m_config.setColumnDomains(m_featureDomains, m_predictionDomains);

        return jsonDataTable;
    }

    /**
     * @return DataColumnSpec[] with the column specs for the selected features
     */
    private DataColumnSpec[] sampleColSpecFactory() {
        DataColumnSpec[] colSpecs = new DataColumnSpec[m_sampledFeatures.length];

        for (int i = 0; i < m_sampledFeatures.length; i++) {
            String newColName = m_sampledFeatures[i] + "_model";
            DataColumnSpecCreator colSpecCreator = new DataColumnSpecCreator(newColName,
                ListCell.getCollectionType(ListCell.getCollectionType(ListCell.getCollectionType(DoubleCell.TYPE))));

            colSpecs[i] = colSpecCreator.createSpec();
        }

        return colSpecs;
    }

    /**
     * applies changes made in the interactive view to the internal node settings
     *
     * @throws InvalidSettingsException
     */
    private void copyValueToConfig() throws InvalidSettingsException {
        PartialDependenceICEPlotViewValue viewValue = getViewValue();
        m_config.setShowPDP(viewValue.getShowPDP());
        m_config.setPDPLineWeight(viewValue.getPDPLineWeight());
        m_config.setShowPDPMargin(viewValue.getShowPDPMargin());
        m_config.setPDPMarginType(viewValue.getPDPMarginType());
        m_config.setPDPMarginMultiplier(viewValue.getPDPMarginMultiplier());
        m_config.setPDPMarginAlphaVal(viewValue.getPDPMarginAlphaVal());
        m_config.setShowICE(viewValue.getShowICE());
        m_config.setICEWeight(viewValue.getICEWeight());
        m_config.setICEAlphaVal(viewValue.getICEAlphaVal());
        m_config.setShowDataPoints(viewValue.getShowDataPoints());
        m_config.setDataPointWeight(viewValue.getDataPointWeight());
        m_config.setDataPointAlphaVal(viewValue.getDataPointAlphaVal());
        m_config.setXAxisLabel(viewValue.getXAxisLabel());
        m_config.setYAxisLabel(viewValue.getYAxisLabel());
        m_config.setYAxisMin(viewValue.getYAxisMin());
        m_config.setYAxisMax(viewValue.getYAxisMax());
        m_config.setYAxisMargin(viewValue.getYAxisMargin());
        m_config.setChartTitle(viewValue.getChartTitle());
        m_config.setChartSubtitle(viewValue.getChartSubtitle());
        m_config.setShowGrid(viewValue.getShowGrid());
        m_config.setSubscribeToSelection(viewValue.getSubscribeToSelection());
        m_config.setPublishSelection(viewValue.getPublishSelection());
        m_config.setSubscribeToFilters(viewValue.getSubscribeToFilters());
        m_config.setShowStaticLine(viewValue.getShowStaticLine());
        m_config.setStaticLineWeight(viewValue.getStaticLineWeight());
        m_config.setStaticLineYValue(viewValue.getStaticLineYValue());
        m_config.setEnableMouseCrosshair(viewValue.getEnableMouseCrosshair());
        m_config.setFeatureInd(viewValue.getFeatureInd());
        m_config.setPredictionInd(viewValue.getPredictionInd());

        // color extraction
        try {
            m_config.setPDPColor(PartialDependenceICEPlotConfig.getColorFromString(viewValue.getPDPColor()));
            m_config.setICEColor(PartialDependenceICEPlotConfig.getColorFromString(viewValue.getICEColor()));
            m_config
                .setDataPointColor(PartialDependenceICEPlotConfig.getColorFromString(viewValue.getDataPointColor()));
            m_config
                .setStaticLineColor(PartialDependenceICEPlotConfig.getColorFromString(viewValue.getStaticLineColor()));
            m_config
                .setBackgroundColor(PartialDependenceICEPlotConfig.getColorFromString(viewValue.getBackgroundColor()));
            m_config.setDataAreaColor(PartialDependenceICEPlotConfig.getColorFromString(viewValue.getDataAreaColor()));
            m_config.setGridColor(PartialDependenceICEPlotConfig.getColorFromString(viewValue.getGridColor()));
        } catch (InvalidSettingsException e) {
            throw new InvalidSettingsException("An error occurred while saving your settings: " + e.getMessage());
        }
    }

    /**
     * @param collectionTableSpec
     * @param exec
     * @return new BufferedDataTable to be joined with the original data table and converted to JSON
     * @throws CanceledExecutionException if execution is cancelled by the user
     * @throws Exception if the columns were chosen incorrectly by the user or there is a problem with the model output
     */
    private BufferedDataTable createCollectionTable(final DataTableSpec collectionTableSpec,
        final ExecutionContext exec) throws CanceledExecutionException, Exception {

        BufferedDataContainer collectionSampleContainer = null; //holds the current rows
        BufferedDataTable collectionAggregationTable = null; //the data table that is built through joining during the process
        int numSamples = getNumSamples(); //number of samples per row, calculated in getNumSamples method
        long numOrigRows = m_originalDataTable.size();
        List<DataCell> singleFeatureRowLineList = new ArrayList<DataCell>(); //holds ListCells with two double values
        DataRow previousRow = null; //last row placeholder used when transitioning between two appended model tables
        int sampleCount = 0; //current number of samples processed for this row
        int rowNumber = 0; //current row processed for this sample iteration
        int currentFeatureInd = -1; //current index of the actual sample feature id from m_featureColModelIndicies
        boolean isMissingFeature = false; //if the feature was sampled and model applied, but not selected, true during "n" iterations
        boolean wereMissingFeatures = false; //if features were samples that were not selected, set to true and issue warning after iter.
        int numMissingFeatureIter = 0; //counter for total rows skipped during excluded feature iteration
        int totalTablesCreated = 0; //counter for total sub-tables created during execution
        //        Exception featureMismatchException =
        //            new Exception("One or more of the selected columns was not sampled in the pre-processing node."
        //                + " Please review your column selection and make sure all selected columns were properly sampled upstream.");

        if (numSamples == 0 || numOrigRows == m_modelOutputTable.size()) {
            throw new Exception(
                "The model table (top inport) was not properly sampled by the preprocessor node. \nPlease "
                    + "make sure the data was output by the preprocessor node before (upstream) the model was applied.");
        }

        for (DataRow row : m_modelOutputTable) {

            //if current sampled feature was not selected
            if (isMissingFeature) {

                //check to see if appropriate number of rows have been skipped
                if (numMissingFeatureIter < numSamples * numOrigRows) {
                    numMissingFeatureIter++;
                    continue;
                }

                //if the sub-table for the missing feature is over, reset
                sampleCount = 1;
                rowNumber = 0;
                previousRow = row;
                currentFeatureInd = -1;
                isMissingFeature = false;
                continue;
            }

            //code to execute on every 1st sample of a new "table-break" iteration
            if (previousRow == null) {
                previousRow = row;
                sampleCount++;
                continue;
            }

            //code to execute on every 2nd sample of a new "table-break" iteration
            if (currentFeatureInd < 0) {

                int newFeatureIndex = getCurrentFeature(previousRow, row);

                //if the current table section is the sampling for a feature that wasn't
                //selected, then set isMissingFeature true and initiate "skipping" iterations
                //also set wereMissingFeatures to true to prevent duplicate warnings
                if (newFeatureIndex < 0) {
                    isMissingFeature = true;
                    wereMissingFeatures = true;
                    numMissingFeatureIter = sampleCount + 1;
                    continue;
                }

                //otherwise set the index of the current column index
                currentFeatureInd = newFeatureIndex;

                //re/initialize the DataContainer
                collectionSampleContainer = exec
                    .createDataContainer(new DataTableSpec(collectionTableSpec.getColumnSpec(currentFeatureInd)), true);

                //process the previous row
                DataCell sampledFeatureCell = previousRow.getCell(m_featureColModelIndicies[currentFeatureInd]);
                ArrayList<DataCell> samplePredictionValues = new ArrayList<DataCell>();
                int count = 0;
                for (Integer predInd : m_predictionColModelIndicies) {
                    DataCell predictionCell = previousRow.getCell(predInd);
                    samplePredictionValues.add(predictionCell);

                    //check prediction domains
                    double pNewMin =
                        Math.min(Double.parseDouble(predictionCell.toString()), m_predictionDomains[count][0]);
                    m_predictionDomains[count][0] = pNewMin;
                    double pNewMax =
                        Math.max(Double.parseDouble(predictionCell.toString()), m_predictionDomains[count][1]);
                    m_predictionDomains[count][1] = pNewMax;
                    count++;
                }

                //check feature domains
                double newMin =
                    Math.min(Double.parseDouble(sampledFeatureCell.toString()), m_featureDomains[currentFeatureInd][0]);
                m_featureDomains[currentFeatureInd][0] = newMin;
                double newMax =
                    Math.max(Double.parseDouble(sampledFeatureCell.toString()), m_featureDomains[currentFeatureInd][1]);
                m_featureDomains[currentFeatureInd][1] = newMax;

                ListCell predictionCollection = CollectionCellFactory.createListCell(samplePredictionValues);
                ListCell singleCellCollection = CollectionCellFactory.createListCell(new ArrayList<DataCell>() {
                    {
                        add(sampledFeatureCell);
                    }
                });
                DataCell[] cellArray = new DataCell[]{singleCellCollection, predictionCollection};
                ListCell dataPointCollection = CollectionCellFactory.createListCell(Arrays.asList(cellArray));
                singleFeatureRowLineList.add(dataPointCollection);
                sampleCount++;
            }

            //code to execute following the completion of one complete sub table of samples
            if (rowNumber == numOrigRows) {

                collectionSampleContainer.close();

                //true when the first aggregation of the first sampled feature is true
                if (collectionAggregationTable == null) {

                    collectionAggregationTable = collectionSampleContainer.getTable();

                } else { //reached on every successive data table joining

                    //update the collectionAggregationTable
                    BufferedDataTable updatedTable =
                        exec.createJoinedTable(collectionAggregationTable, collectionSampleContainer.getTable(), exec);
                    collectionAggregationTable = updatedTable;

                }

                //reset the loop internals at "table-break"
                singleFeatureRowLineList = new ArrayList<DataCell>();
                totalTablesCreated++;
                sampleCount = 1;
                rowNumber = 0;
                previousRow = row;
                currentFeatureInd = -1;
                continue;
            }

            DataCell sampledFeatureCell = row.getCell(m_featureColModelIndicies[currentFeatureInd]);
            ArrayList<DataCell> samplePredictionValues = new ArrayList<DataCell>();
            int count = 0;
            for (Integer predInd : m_predictionColModelIndicies) {
                DataCell predictionCell = row.getCell(predInd);
                samplePredictionValues.add(predictionCell);

                //check prediction domains
                double pNewMin = Math.min(Double.parseDouble(predictionCell.toString()), m_predictionDomains[count][0]);
                m_predictionDomains[count][0] = pNewMin;
                double pNewMax = Math.max(Double.parseDouble(predictionCell.toString()), m_predictionDomains[count][1]);
                m_predictionDomains[count][1] = pNewMax;

                count++;
            }

            //check feature domains
            double newMin =
                Math.min(Double.parseDouble(sampledFeatureCell.toString()), m_featureDomains[currentFeatureInd][0]);
            m_featureDomains[currentFeatureInd][0] = newMin;
            double newMax =
                Math.max(Double.parseDouble(sampledFeatureCell.toString()), m_featureDomains[currentFeatureInd][1]);
            m_featureDomains[currentFeatureInd][1] = newMax;

            ListCell predictionCollection = CollectionCellFactory.createListCell(samplePredictionValues);
            ListCell singleCellCollection = CollectionCellFactory.createListCell(new ArrayList<DataCell>() {
                {
                    add(sampledFeatureCell);
                }
            });
            DataCell[] cellArray = new DataCell[]{singleCellCollection, predictionCollection};
            ListCell dataPointCollection = CollectionCellFactory.createListCell(Arrays.asList(cellArray));

            singleFeatureRowLineList.add(dataPointCollection);

            //if still collecting data point for a single sample row, update count, continue
            if (sampleCount < numSamples) {
                sampleCount++;
                continue;
            }

            //else add row
            ListCell finalSingleFeatureCollectionCell = CollectionCellFactory.createListCell(singleFeatureRowLineList);
            final DefaultRow newRow =
                new DefaultRow(row.getCell(m_rowIDColInd).toString(), new DataCell[]{finalSingleFeatureCollectionCell});
            collectionSampleContainer.addRowToTable(newRow);

            //reset sample count if end of row sampling has been reached
            sampleCount = 1;
            rowNumber++;
            //reset interal ListCell collection
            singleFeatureRowLineList = new ArrayList<DataCell>();
        }

        //if a sampled feature was skipped, notify the user
        if (wereMissingFeatures) {
            setWarningMessage("You sampled some features in the Partial Dependence/ICE pre-processing node \n"
                + " that were not selected in the PDP/ICE Plot node. You will not be able to toggle between features "
                + "that were not selected in the View Dialog.");
        }

        //one feature was sampled and the selected feature was chosen incorrectly, this will result in NPE
        //catch and display a more descriptive warning about the incorrectly chosen column.
        try {
            //after the last row of the model table, finish table
            collectionSampleContainer.close();
            totalTablesCreated++;
        } catch (NullPointerException e) {
            throw new Exception("One or more of the selected columns was not sampled in the pre-processing node.\n"
                + " Please review your column selection and make sure all selected columns were properly sampled upstream.");
            //            throw featureMismatchException;
        }

        //if a column was selected, but no samples for that column were found, fail with descriptive msg
        if (totalTablesCreated < m_sampledFeatures.length) {
            throw new Exception("One or more of the selected columns was not sampled in the pre-processing node.\n"
                + " Please review your column selection and make sure all selected columns were properly sampled upstream.");
            //            throw featureMismatchException;
        }

        //we only need to join the last table if there was more than 1 total sampled feature
        //        if (m_sampledFeatures.length > 1 && !isMissingFeature) {
        if (totalTablesCreated > 1 && !isMissingFeature) {

            try {
                //update the collectionAggregationTable to final state
                BufferedDataTable updatedTable =
                    exec.createJoinedTable(collectionAggregationTable, collectionSampleContainer.getTable(), exec);
                collectionAggregationTable = updatedTable;
            } catch (NullPointerException e) {
                throw new Exception("One or more of the selected columns was not sampled in the pre-processing node.\n"
                    + " Please review your column selection and make sure all selected columns were properly sampled upstream.");
            }

            //        } else if (m_sampledFeatures.length == 1) {
        } else if (totalTablesCreated == 1) {

            //if only one sampled feature was chosen, just close the table
            collectionAggregationTable = collectionSampleContainer.getTable();
        }

        return collectionAggregationTable;
    }

    /**
     * @return int representing the number of samples per feature that was chose by the user in the pre-proc node
     */
    private int getNumSamples() {
        String rowKey = null;
        int count = 0;

        //count the number of sequential, identical row keys
        for (DataRow row : m_modelOutputTable) {
            String currRowKey = row.getCell(m_rowIDColInd).toString();
            if (rowKey == null || rowKey.equals(currRowKey)) {
                rowKey = currRowKey;
                count++;
                continue;
            }
            return count;
        }
        return 0;
    }

    /**
     * @param prevRow
     * @param currRow
     * @return the index of the sampled feature in the current sub-table, -1 if not chosen by user
     */
    private int getCurrentFeature(final DataRow prevRow, final DataRow currRow) {
        int colCount = 0;
        boolean correctIndexMissing = true;

        //for each column in the previous row, check to see if the value changed
        for (int featureColModelIndex : m_featureColModelIndicies) {
            String currentRowColumnValue = currRow.getCell(featureColModelIndex).toString();
            String previousRowColumnValue = prevRow.getCell(featureColModelIndex).toString();

            if (currentRowColumnValue.equals(previousRowColumnValue)) {
                colCount++;
                continue;
            }

            correctIndexMissing = false;
            break;
        }

        //if none of the selected features columns changed, then handle
        if (correctIndexMissing) {
            return -1;
        }

        //otherwise set the index of the current column index
        return colCount;
    }
}
