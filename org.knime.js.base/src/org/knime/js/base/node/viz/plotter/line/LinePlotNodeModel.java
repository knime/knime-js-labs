/*
 * ------------------------------------------------------------------------
 *
 *  Copyright (C) 2003 - 2013
 *  University of Konstanz, Germany and
 *  KNIME GmbH, Konstanz, Germany
 *  Website: http://www.knime.org; Email: contact@knime.org
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
 *  propagated with or for interoperation with KNIME. The owner of a Node
 *  may freely choose the license terms applicable to such Node, including
 *  when such Node is propagated with or for interoperation with KNIME.
 * ------------------------------------------------------------------------
 *
 * History
 *   13.05.2014 (Christian Albrecht, KNIME.com AG, Zurich, Switzerland): created
 */
package org.knime.js.base.node.viz.plotter.line;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.knime.base.data.xml.SvgCell;
import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataType;
import org.knime.core.data.DoubleValue;
import org.knime.core.data.StringValue;
import org.knime.core.data.container.CellFactory;
import org.knime.core.data.container.ColumnRearranger;
import org.knime.core.data.container.SingleCellFactory;
import org.knime.core.data.def.BooleanCell;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
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
import org.knime.core.node.util.filter.NameFilterConfiguration.EnforceOption;
import org.knime.core.node.util.filter.NameFilterConfiguration.FilterResult;
import org.knime.core.node.web.ValidationError;
import org.knime.js.core.JSONDataTable;
import org.knime.js.core.JSONDataTable.JSONDataTableRow;
import org.knime.js.core.JSONDataTableSpec;
import org.knime.js.core.JSONDataTableSpec.JSTypes;
import org.knime.js.core.datasets.JSONKeyedValues2DDataset;
import org.knime.js.core.datasets.JSONKeyedValuesRow;
import org.knime.js.core.node.AbstractSVGWizardNodeModel;

/**
 *
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland, University of Konstanz
 */
final class LinePlotNodeModel extends
    AbstractSVGWizardNodeModel<LinePlotViewRepresentation, LinePlotViewValue> {

    private static final NodeLogger LOGGER = NodeLogger.getLogger(LinePlotNodeModel.class);

    private final LinePlotViewConfig m_config;
    private BufferedDataTable m_table;

    /**
     * Creates a new model instance.
     */
    LinePlotNodeModel() {
        super(new PortType[]{BufferedDataTable.TYPE, BufferedDataTable.TYPE_OPTIONAL},
            new PortType[]{ImagePortObject.TYPE, BufferedDataTable.TYPE});
        m_config = new LinePlotViewConfig();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PortObjectSpec[] configure(final PortObjectSpec[] inSpecs) throws InvalidSettingsException {
        List<String> allAllowedCols = new LinkedList<String>();

        DataTableSpec tableSpec = (DataTableSpec)inSpecs[0];

        for (DataColumnSpec colspec : tableSpec) {
            if (colspec.getType().isCompatible(DoubleValue.class)
                    || colspec.getType().isCompatible(StringValue.class)) {
                allAllowedCols.add(colspec.getName());
            }
        }

        if (tableSpec.getNumColumns() < 1
                || allAllowedCols.size() < 1) {
            throw new InvalidSettingsException("Data table must have"
                    + " at least one numerical or categorical column.");
        }

        DataTableSpec out = tableSpec;
        if (m_config.getEnableSelection()) {
            ColumnRearranger rearranger = createColumnAppender(tableSpec, null);
            out = rearranger.createSpec();
        }

        m_config.getyColumnsConfig(true, tableSpec);

        PortObjectSpec imageSpec;
        if (generateImage()) {
            imageSpec = new ImagePortObjectSpec(SvgCell.TYPE);
        } else {
            imageSpec = InactiveBranchPortObjectSpec.INSTANCE;
        }
        return new PortObjectSpec[]{imageSpec, out};
    }

    private ColumnRearranger createColumnAppender(final DataTableSpec spec, final List<String> selectionList) {
        String newColName = m_config.getSelectionColumnName();
        if (newColName == null || newColName.trim().isEmpty()) {
            newColName = LinePlotViewConfig.DEFAULT_SELECTION_COLUMN_NAME;
        }
        newColName = DataTableSpec.getUniqueColumnName(spec, newColName);

        DataColumnSpec outColumnSpec =
                new DataColumnSpecCreator(newColName, DataType.getType(BooleanCell.class)).createSpec();
        ColumnRearranger rearranger = new ColumnRearranger(spec);
        CellFactory fac = new SingleCellFactory(outColumnSpec) {
            private int m_rowIndex = 0;
            @Override
            public DataCell getCell(final DataRow row) {
                if (++m_rowIndex > m_config.getMaxRows()) {
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
        rearranger.append(fac);
        return rearranger;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LinePlotViewRepresentation createEmptyViewRepresentation() {
        return new LinePlotViewRepresentation();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LinePlotViewValue createEmptyViewValue() {
        return new LinePlotViewValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getJavascriptObjectID() {
        return "org.knime.js.base.node.viz.plotter.line";
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
    public ValidationError validateViewValue(final LinePlotViewValue viewContent) {
        synchronized (getLock()) {
            // validate value, nothing to do atm
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveCurrentValue(final NodeSettingsWO content) {
        // do nothing
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void performExecuteCreateView(final PortObject[] inData, final ExecutionContext exec)
        throws Exception {
        synchronized (getLock()) {
            m_table = (BufferedDataTable)inData[0];
            BufferedDataTable colorTable = (BufferedDataTable)inData[1];
            LinePlotViewRepresentation representation = getViewRepresentation();
            // don't use staggered rendering and resizing for image creation
            representation.setEnableStaggeredRendering(false);
            representation.setResizeToWindow(false);
            if (representation.getKeyedDataset() == null) {
                // create dataset for view
                copyConfigToView(m_table.getDataTableSpec());
                representation.setKeyedDataset(createKeyedDataset(colorTable, exec));
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PortObject[] performExecuteCreatePortObjects(final PortObject svgImageFromView,
            final ExecutionContext exec) throws Exception {
        BufferedDataTable out = m_table;
        synchronized (getLock()) {
            LinePlotViewRepresentation representation = getViewRepresentation();
            // enable staggered rendering and resizing for interactive view
            representation.setEnableStaggeredRendering(true);
            representation.setResizeToWindow(m_config.getResizeToWindow());

            LinePlotViewValue viewValue = getViewValue();
            if (m_config.getEnableSelection()) {
                List<String> selectionList = null;
                if (viewValue != null && viewValue.getSelection() != null) {
                    selectionList = Arrays.asList(viewValue.getSelection());
                }
                ColumnRearranger rearranger = createColumnAppender(m_table.getDataTableSpec(), selectionList);
                out = exec.createColumnRearrangeTable(m_table, rearranger, exec);
            }
        }
        exec.setProgress(1);
        return new PortObject[]{svgImageFromView, out};
    }

    private JSONKeyedValues2DDataset createKeyedDataset(final BufferedDataTable colorTable, final ExecutionContext exec)
            throws CanceledExecutionException {

        ColumnRearranger c = createNumericColumnRearranger(m_table.getDataTableSpec());
        BufferedDataTable filteredTable =
            exec.createColumnRearrangeTable(m_table, c, exec.createSubProgress(0.1));
        exec.setProgress(0.1);
        //construct dataset
        if (m_config.getMaxRows() < filteredTable.getRowCount()) {
            setWarningMessage("Only the first " + m_config.getMaxRows() + " rows are displayed.");
        }
        final JSONDataTable table =
            new JSONDataTable(filteredTable, 1, m_config.getMaxRows(), exec.createSubProgress(0.79));
        JSONDataTable jsonColorTable = null;
        if (colorTable != null) {
            jsonColorTable = new JSONDataTable(colorTable, 1, colorTable.getRowCount(),
                exec.createSilentSubProgress(0.01));
        }
        exec.setProgress(0.9);
        ExecutionMonitor datasetExecutionMonitor = exec.createSubProgress(0.1);
        final JSONDataTableSpec tableSpec = table.getSpec();
        int numColumns = tableSpec.getNumColumns();
        String[] rowKeys = new String[tableSpec.getNumRows()];
        JSONKeyedValuesRow[] rowValues = new JSONKeyedValuesRow[tableSpec.getNumRows()];
        JSONDataTableRow[] tableRows = table.getRows();
        for (int rowID = 0; rowID < rowValues.length; rowID++) {
            JSONDataTableRow currentRow = tableRows[rowID];
            rowKeys[rowID] = currentRow.getRowKey();
            double[] rowData = new double[numColumns];
            Object[] tableData = currentRow.getData();
            for (int colID = 0; colID < numColumns; colID++) {
                if (tableData[colID] instanceof Double) {
                    rowData[colID] = (double)tableData[colID];
                } else if (tableData[colID] instanceof Long) {
                    rowData[colID] = (long)tableData[colID];
                } else if (tableData[colID] instanceof String) {
                    rowData[colID] = getOrdinalFromStringValue((String)tableData[colID], table, colID);
                }
            }
            rowValues[rowID] = new JSONKeyedValuesRow(currentRow.getRowKey(), rowData);
            rowValues[rowID].setColor(tableSpec.getRowColorValues()[rowID]);
            datasetExecutionMonitor.setProgress(((double)rowID) / rowValues.length,
                "Creating dataset, processing row " + rowID + " of " + rowValues.length + ".");
        }

        JSONKeyedValues2DDataset dataset =
            new JSONKeyedValues2DDataset(tableSpec.getColNames(), rowValues);
        for (int col = 0; col < tableSpec.getNumColumns(); col++) {
            String colColor = getColorForColumn(tableSpec.getColNames()[col], jsonColorTable);
            if (colColor != null) {
                dataset.setColumnColor(colColor, col);
            }
            if (tableSpec.getColTypes()[col].equals(JSTypes.STRING.getName())
                && tableSpec.getPossibleValues().get(col) != null) {
                dataset.setSymbol(getSymbolMap(tableSpec.getPossibleValues().get(col)), col);
            }
            if (tableSpec.getColTypes()[col].equals(JSTypes.DATE_TIME.getName())) {
                dataset.setDateTimeFormat(m_config.getDateFormat(), col);
            }
        }

        LinePlotViewValue viewValue = getViewValue();

        final String[] yColumns = viewValue.getyColumns();
        if (yColumns == null || !Arrays.asList(tableSpec.getColNames()).containsAll(Arrays.asList(yColumns))) {
            viewValue.setyColumns(new String[]{tableSpec.getColNames()[tableSpec.getNumColumns() > 1 ? 1 : 0]});
        }

        return dataset;
    }

    private ColumnRearranger createNumericColumnRearranger(final DataTableSpec in) {
        ColumnRearranger c = new ColumnRearranger(in);
        for (DataColumnSpec colSpec : in) {
            DataType type = colSpec.getType();
            if (!type.isCompatible(DoubleValue.class) && !type.isCompatible(StringValue.class)) {
                c.remove(colSpec.getName());
            }
        }
        return c;
    }

    private int getOrdinalFromStringValue(final String stringValue, final JSONDataTable table, final int colID) {
        LinkedHashSet<Object> possibleValues = table.getSpec().getPossibleValues().get(colID);
        if (possibleValues != null) {
            int ordinal = 0;
            for (Object value : possibleValues) {
                if (value != null && value.equals(stringValue)) {
                    return ordinal;
                }
                ordinal++;
            }
        }
        return -1;
    }

    private Map<String, String> getSymbolMap(final LinkedHashSet<Object> linkedHashSet) {
        Map<String, String> symbolMap = new HashMap<String, String>();
        Integer ordinal = 0;
        for (Object value: linkedHashSet) {
            symbolMap.put(ordinal.toString(), value.toString());
            ordinal++;
        }
        return symbolMap;
    }

    private String getColorForColumn(final String colKey, final JSONDataTable colorTable) {
        if (colKey != null && colorTable != null) {
            for (int row = 0; row < colorTable.getRows().length; row++) {
                if (colKey.equals(colorTable.getRows()[row].getData()[0].toString())) {
                    return colorTable.getSpec().getRowColorValues()[row];
                }
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void performReset() {
        m_table = null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getInteractiveViewName() {
        return (new LinePlotNodeFactory()).getInteractiveViewName();
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
        new LinePlotViewConfig().loadSettings(settings);
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
    protected void useCurrentValueAsDefault() {
        synchronized (getLock()) {
            copyValueToConfig();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean generateImage() {
        return m_config.getGenerateImage();
    }

    private void copyConfigToView(final DataTableSpec spec) {
        LinePlotViewRepresentation representation = getViewRepresentation();
        representation.setShowLegend(m_config.getShowLegend());
        representation.setAutoRangeAxes(m_config.getAutoRangeAxes());
        representation.setUseDomainInformation(m_config.getUseDomainInfo());
        representation.setShowGrid(m_config.getShowGrid());
        representation.setShowCrosshair(m_config.getShowCrosshair());
        representation.setSnapToPoints(m_config.getSnapToPoints());

        representation.setEnableViewConfiguration(m_config.getEnableViewConfiguration());
        representation.setEnableTitleChange(m_config.getEnableTitleChange());
        representation.setEnableSubtitleChange(m_config.getEnableSubtitleChange());
        representation.setEnableXColumnChange(m_config.getEnableXColumnChange());
        representation.setEnableYColumnChange(m_config.getEnableYColumnChange());
        representation.setEnableXAxisLabelEdit(m_config.getEnableXAxisLabelEdit());
        representation.setEnableYAxisLabelEdit(m_config.getEnableYAxisLabelEdit());
        representation.setEnableDotSizeChange(m_config.getEnableDotSizeChange());

        representation.setEnablePanning(m_config.getEnablePanning());
        representation.setEnableZooming(m_config.getEnableZooming());
        representation.setEnableDragZooming(m_config.getEnableDragZooming());
        representation.setShowZoomResetButton(m_config.getShowZoomResetButton());
        representation.setEnableSelection(m_config.getEnableSelection());
        representation.setEnableRectangleSelection(m_config.getEnableRectangleSelection());
        representation.setEnableLassoSelection(m_config.getEnableLassoSelection());

        representation.setImageWidth(m_config.getImageWidth());
        representation.setImageHeight(m_config.getImageHeight());
        representation.setDateTimeFormat(m_config.getDateFormat());
        representation.setBackgroundColor(m_config.getBackgroundColorString());
        representation.setDataAreaColor(m_config.getDataAreaColorString());
        representation.setGridColor(m_config.getGridColorString());

        LinePlotViewValue viewValue = getViewValue();
        viewValue.setChartTitle(m_config.getChartTitle());
        viewValue.setChartSubtitle(m_config.getChartSubtitle());
        viewValue.setxColumn(m_config.getxColumn());
        FilterResult filter = m_config.getyColumnsConfig().applyTo(spec);
        viewValue.setyColumns(filter.getIncludes());
        viewValue.setxAxisLabel(m_config.getxAxisLabel());
        viewValue.setyAxisLabel(m_config.getyAxisLabel());
        if ((m_config.getxAxisMin() == null) && m_config.getUseDomainInfo() && (m_config.getxColumn() != null)) {
            viewValue.setxAxisMin(getMinimumFromColumns(spec, m_config.getxColumn()));
        } else {
            viewValue.setxAxisMin(m_config.getxAxisMin());
        }
        if ((m_config.getxAxisMax() == null) && m_config.getUseDomainInfo() && (m_config.getxColumn() != null)) {
            viewValue.setxAxisMax(getMaximumFromColumns(spec, m_config.getxColumn()));
        } else {
            viewValue.setxAxisMax(m_config.getxAxisMax());
        }
        if (m_config.getyAxisMin() == null && m_config.getUseDomainInfo()) {
            viewValue.setyAxisMin(getMinimumFromColumns(spec, filter.getIncludes()));
        } else {
            viewValue.setyAxisMin(m_config.getyAxisMin());
        }
        if (m_config.getyAxisMax() == null && m_config.getUseDomainInfo()) {
            viewValue.setyAxisMax(getMaximumFromColumns(spec, filter.getIncludes()));
        } else {
            viewValue.setyAxisMax(m_config.getyAxisMax());
        }
        viewValue.setDotSize(m_config.getDotSize());
    }

    private void copyValueToConfig() {
        LinePlotViewValue viewValue = getViewValue();
        m_config.setChartTitle(viewValue.getChartTitle());
        m_config.setChartSubtitle(viewValue.getChartSubtitle());
        m_config.setxColumn(viewValue.getxColumn());
        m_config.getyColumnsConfig().loadDefaults(viewValue.getyColumns(), null, EnforceOption.EnforceInclusion);
        m_config.setxAxisLabel(viewValue.getxAxisLabel());
        m_config.setyAxisLabel(viewValue.getyAxisLabel());
        m_config.setxAxisMin(viewValue.getxAxisMin());
        m_config.setxAxisMax(viewValue.getxAxisMax());
        m_config.setyAxisMin(viewValue.getyAxisMin());
        m_config.setyAxisMax(viewValue.getyAxisMax());
        m_config.setDotSize(viewValue.getDotSize());
    }

    private Double getMinimumFromColumns(final DataTableSpec spec, final String... columnNames) {
        double minimum = Double.MAX_VALUE;
        for (String column : columnNames) {
            DataCell lowerCell = spec.getColumnSpec(column).getDomain().getLowerBound();
            if ((lowerCell != null) && lowerCell.getType().isCompatible(DoubleValue.class)) {
                minimum = Math.min(minimum, ((DoubleValue)lowerCell).getDoubleValue());
            }
        }
        if (minimum < Double.MAX_VALUE) {
            return minimum;
        }
        return null;
    }

    private Double getMaximumFromColumns(final DataTableSpec spec, final String... columnNames) {
        double maximum = Double.MIN_VALUE;
        for (String column : columnNames) {
            DataCell upperCell = spec.getColumnSpec(column).getDomain().getUpperBound();
            if ((upperCell != null) && upperCell.getType().isCompatible(DoubleValue.class)) {
                maximum = Math.max(maximum, ((DoubleValue)upperCell).getDoubleValue());
            }
        }
        if (maximum > Double.MIN_VALUE) {
            return maximum;
        }
        return null;
    }


}
