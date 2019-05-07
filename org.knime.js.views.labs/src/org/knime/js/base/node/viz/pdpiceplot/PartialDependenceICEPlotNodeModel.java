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

import java.util.Arrays;
import java.util.Collection;

import org.knime.base.data.xml.SvgCell;
import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataType;
import org.knime.core.data.DoubleValue;
import org.knime.core.data.StringValue;
import org.knime.core.data.container.ColumnRearranger;
import org.knime.core.data.container.SingleCellFactory;
import org.knime.core.data.def.BooleanCell;
import org.knime.core.data.def.DoubleCell;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.BufferedDataTableHolder;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
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
 * @author Ben Laney
 */
public class PartialDependenceICEPlotNodeModel extends
    AbstractSVGWizardNodeModel<PartialDependenceICEPlotNodeViewRepresentation, PartialDependenceICEPlotViewValue>
    implements CSSModifiable, BufferedDataTableHolder, LayoutTemplateProvider {

    private static final NodeLogger LOGGER = NodeLogger.getLogger(PartialDependenceICEPlotNodeModel.class);

    private static final String ROW_LIMITATION_WARNING_ID_STRING = "rowLimit";

    private final PartialDependenceICEPlotConfig m_config;

    private final PartialDependenceICEJSONBuilder m_jsonBuilder;

    private BufferedDataTable m_modelOutputTable;

    private BufferedDataTable m_originalDataTable;

    private int m_featureColInd;

    private int m_rowIDColInd;

    private int m_predictionColInd;

    /**
     * @param viewName
     */
    public PartialDependenceICEPlotNodeModel(final String viewName) {
        super(new PortType[]{BufferedDataTable.TYPE, BufferedDataTable.TYPE},
            new PortType[]{ImagePortObject.TYPE, BufferedDataTable.TYPE}, viewName);
        m_config = new PartialDependenceICEPlotConfig();
        m_jsonBuilder = new PartialDependenceICEJSONBuilder();
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

        if(modelOutputSpec == null || originalDataTableSpec == null) {
            throw new InvalidSettingsException("One or more tableSpecs is missing from the tables provided."
                + " Please provide properly formatted data tables to this node.");
        }

        // generic prediction table checking
        int colIndex = 0;
        int numNumericColumns = 0;
        boolean hasPotentialRowIDColumn = false;
        boolean hasDomains = true;
        boolean hasFeatureColumn = false;
        boolean hasRowIDColumn = false;
        boolean hasPredictionColumn = false;
        for (DataColumnSpec colSpec : modelOutputSpec) {
            if (colSpec.getType().isCompatible(DoubleValue.class)) {
                numNumericColumns++;
                if(colSpec.getName().equals(m_config.getFeatureCol())) {
                    if(colSpec.getName().equals("feature")) {
                        hasFeatureColumn = true;
                    }
                    if (colSpec.getDomain() == null) {
                        hasDomains = false;
                    }
                    m_featureColInd = colIndex;
                }else if(colSpec.getName().equals(m_config.getPredictionCol())) {
                    if(colSpec.getName().equals("prediction")) {
                        hasPredictionColumn = true;
                    }
                    if (colSpec.getDomain() == null) {
                        hasDomains = false;
                    }
                    m_predictionColInd = colIndex;
                }
            } else if (colSpec.getType().isCompatible(StringValue.class)) {
                hasPotentialRowIDColumn = true;
                if (colSpec.getName().equals(m_config.getRowIDCol())) {
                    if(colSpec.getName().equals("RowID")){
                        hasRowIDColumn = true;
                    }
                    m_rowIDColInd = colIndex;
                }
            }
            colIndex++;
        }

        if (numNumericColumns < 2) {
            throw new InvalidSettingsException(
                "The processed model table you provided to the node does not appear to have both a"
                    + " feature and prediction column. Please ensure that you have used the PCP/ICE pre-processor node"
                    + "and provided the correct output table.");
        }

        if (!hasPotentialRowIDColumn) {
            throw new InvalidSettingsException(
                "The processed model table you provided to the node does not appear to have a"
                    + " RowID column. Please ensure that you have used the PCP/ICE pre-processor node"
                    + "and provided the correct output table.");
        }

        if (!hasDomains) {
            throw new InvalidSettingsException(
                "The processed model table you provided to the node does not appear to have"
                    + " domains. Please ensure that the table has domains before executing with this node.");
        }

        if (!hasFeatureColumn || !hasRowIDColumn || !hasPredictionColumn) {
            setWarningMessage("Execution will be slower because the data provided does not match the pre-processed format.");
        }

        if (hasFeatureColumn && hasRowIDColumn && hasPredictionColumn) {
            m_jsonBuilder.setHasCorrectColumns(true);
        }

        numNumericColumns = 0;
        for (DataColumnSpec colSpec : originalDataTableSpec) {
            if (colSpec.getType().isCompatible(DoubleValue.class)) {
                numNumericColumns++;
            }
        }

        if (numNumericColumns < 2) {
            throw new InvalidSettingsException(
                "The data table you provided at the InPort 2 to the node does not appear to be correct."
                    + " Data being processed and visualized by this node should contain two or more numeric columns which correspond to a "
                    + "feature (x-axis) and a prediction (y-axis). Please check that you are using the same data table as was used to create "
                    + "the model table at InPort 1.");
        }

        DataTableSpec outTableSpec = originalDataTableSpec;

        if (m_config.getEnableSelection()) {
            ColumnRearranger columnRearranger = createColumnAppender(originalDataTableSpec, null);
            outTableSpec = columnRearranger.createSpec();
        }

        PortObjectSpec imageSpec = InactiveBranchPortObjectSpec.INSTANCE;
        if (generateImage()) {
            imageSpec = new ImagePortObjectSpec(SvgCell.TYPE);
        }

        return new PortObjectSpec[]{imageSpec, outTableSpec};
    }



    /**
     * called if selection is enabled to create the "Selected" column
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
            if (rep.getDataTable() != null) {
                rep.getDataTable().setId(getTableId(PartialDependenceICEPlotConfig.ORIGINAL_DATA_TABLE_INPORT));
            }
        }
        return rep;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void performExecuteCreateView(final PortObject[] inData, final ExecutionContext exec) throws Exception {
        synchronized (getLock()) {
            setInternalTables(new BufferedDataTable[]{
                (BufferedDataTable)inData[PartialDependenceICEPlotConfig.MODEL_OUTPUT_TABLE_INPORT],
                (BufferedDataTable)inData[PartialDependenceICEPlotConfig.ORIGINAL_DATA_TABLE_INPORT]});
            PartialDependenceICEPlotNodeViewRepresentation viewRepresentation = getViewRepresentation();
            if (viewRepresentation.getDataTable() == null) {
                copyConfigToView(m_modelOutputTable.getDataTableSpec());
                viewRepresentation.setDataTable(createJSONDataTable(exec));
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

            viewRepresentation.setRunningInView(true);
            PartialDependenceICEPlotViewValue viewValue = getViewValue();
            if (m_config.getEnableSelection()) {
                Collection<String> selectedList = null;
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
    private JSONDataTable createJSONDataTable(final ExecutionContext exec) throws CanceledExecutionException {

        if (m_config.getMaxNumRows() < m_originalDataTable.size()) {
            String message = "Only the first " + m_config.getMaxNumRows() + " rows are displayed.";
            setWarningMessage(message);
            if (m_config.getShowWarnings()) {
                getViewRepresentation().getJSONWarnings().setWarningMessage(message, ROW_LIMITATION_WARNING_ID_STRING);
            }
        }
        JSONDataTable jsonDataTable = null;
        try {
            jsonDataTable = m_jsonBuilder.setDataTables(m_originalDataTable, m_modelOutputTable)
                    .setColumnIndicies(m_featureColInd, m_rowIDColInd, m_predictionColInd)
                    .setMaxRows(m_config.getMaxNumRows())
                    .setOrigFeatureColName(m_config.getOrigFeatureCol())
                    .build(exec);
            if(jsonDataTable != null && m_jsonBuilder.getWarningMessage().size() > 0) {
                for(String message : m_jsonBuilder.getWarningMessage()) {
                    setWarningMessage(message);
                }
            }
        }catch (CanceledExecutionException e){
            throw new CanceledExecutionException("An error occurred while converting this table to JSON: " + e.getMessage());
        } catch (Exception e) {
            LOGGER.info("The table provided from the model was altered before being passed to the Partial Dependence/ICE plot node."
                    + " Please ensure that the sampled output is ordered by RowID and by sample RowID. If you altered the order of the data"
                    + " output by the model, this is likely the cause of this failed execution. Please try re-ordering the Data Table or "
                    + "providing the original output from the model.");
            throw new CanceledExecutionException("The model output table has been illegally modified prior to execution."
                    + " Please see KNIME Log for more details.");
        }
        return jsonDataTable;
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
        // needed because of AbstractSVGWizard
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
     * applies changes made in the dialog to the ViewRepresentation model and creates the internal settings for the
     * ViewValue model which will be accessible from the interactive view
     */
    private void copyConfigToView(final DataTableSpec spec) {
        PartialDependenceICEPlotNodeViewRepresentation viewRepresentation = getViewRepresentation();
        viewRepresentation.setMaxNumRows(m_config.getMaxNumRows());
        viewRepresentation.setGenerateImage(m_config.getGenerateImage());
        viewRepresentation.setFeatureCol(m_config.getFeatureCol());
        viewRepresentation.setRowIDCol(m_config.getRowIDCol());
        viewRepresentation.setPredictionCol(m_config.getPredictionCol());
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
        viewRepresentation.setRunningInView(m_config.getRunningInView());

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
            viewValue.setDataPointColor(PartialDependenceICEPlotConfig.getRGBAStringFromColor(m_config.getDataPointColor()));
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
            viewValue
                .setDataAreaColor(PartialDependenceICEPlotConfig.getRGBAStringFromColor(m_config.getDataAreaColor()));
            viewValue.setGridColor(PartialDependenceICEPlotConfig.getRGBAStringFromColor(m_config.getGridColor()));
            viewValue.setXAxisMin(getMinFromCol(spec, m_config.getFeatureCol()));
            viewValue.setXAxisMax(getMaxFromCol(spec, m_config.getFeatureCol()));
            viewValue.setYAxisMin(getMinFromCol(spec, m_config.getPredictionCol()));
            viewValue.setYAxisMax(getMaxFromCol(spec, m_config.getPredictionCol()));
            viewValue.setYAxisMargin(m_config.getYAxisMargin());
            viewValue.setSelected(new String[0]);
        }
    }

    /**
     * applies changes made in the interactive view to the internal node settings
     * @throws InvalidSettingsException
     */
    private void copyValueToConfig() throws InvalidSettingsException{
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
        m_config.setXAxisMin(viewValue.getXAxisMin());
        m_config.setYAxisMax(viewValue.getYAxisMax());
        m_config.setXAxisMin(viewValue.getXAxisMin());
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

        // color extraction
        try {
            m_config.setPDPColor(PartialDependenceICEPlotConfig.getColorFromString(viewValue.getPDPColor()));
            m_config.setICEColor(PartialDependenceICEPlotConfig.getColorFromString(viewValue.getICEColor()));
            m_config.setDataPointColor(PartialDependenceICEPlotConfig.getColorFromString(viewValue.getDataPointColor()));
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
     * @param spec
     * @param colName
     * @return column max value
     */
    private static Double getMaxFromCol(final DataTableSpec spec, final String colName) {
        DataColumnSpec colSpec = spec.getColumnSpec(colName);
        if (colSpec != null) {
            DataCell maxCell = colSpec.getDomain().getUpperBound();
            if (maxCell != null && maxCell.getType().isCompatible(DoubleValue.class)
                && ((DoubleCell)maxCell).getDoubleValue() > Double.MIN_VALUE) {
                return ((DoubleCell)maxCell).getDoubleValue();
            }
        }
        return null;
    }

    /**
     * @param spec
     * @param colName
     * @return column min value
     */
    private static Double getMinFromCol(final DataTableSpec spec, final String colName) {
        DataColumnSpec colSpec = spec.getColumnSpec(colName);
        if (colSpec != null) {
            DataCell minCell = colSpec.getDomain().getLowerBound();
            if (minCell != null && minCell.getType().isCompatible(DoubleValue.class)
                && ((DoubleCell)minCell).getDoubleValue() < Double.MAX_VALUE) {
                return ((DoubleCell)minCell).getDoubleValue();
            }
        }
        return null;
    }
}
