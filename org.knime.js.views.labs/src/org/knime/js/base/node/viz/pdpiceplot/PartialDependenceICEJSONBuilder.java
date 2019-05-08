/**
 *
 */
package org.knime.js.base.node.viz.pdpiceplot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.def.DoubleCell;
import org.knime.core.data.def.IntCell;
import org.knime.core.data.def.LongCell;
import org.knime.core.data.def.StringCell;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.BufferedDataTableHolder;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.js.core.JSONDataTable;
import org.knime.js.core.JSONDataTable.JSONDataTableRow;
import org.knime.js.core.JSONDataTableSpec;

/**
 * @author Ben Laney, KNIME GmbH, Konstanz, Germany
 *
 */
public class PartialDependenceICEJSONBuilder implements BufferedDataTableHolder {

    private BufferedDataTable m_originalDataTable;

    private BufferedDataTable m_modelTable;

    private int m_modelFeatureColInd;

    private int m_rowIDColInd;

    private int m_predictionColInd;

    private String m_origFeatureColName;

    private int m_maxRows;

    private boolean m_hasCorrectColumns = false;

    private boolean m_isContiguous = false;

    private boolean m_isOrdered = false;

    private boolean m_failedExec = false;

    private int m_totalSamplesPerRow = -1;

    private ArrayList<String> m_warnings;

    /**
     * creates new PDP/ICE JSON Builder
     */
    public PartialDependenceICEJSONBuilder() {
        m_warnings = new ArrayList<String>();
    }

    /**
     * @param originalDataTable
     * @param modelTable
     * @return Builder
     */
    public PartialDependenceICEJSONBuilder setDataTables(final BufferedDataTable originalDataTable,
        final BufferedDataTable modelTable) {
        setInternalTables(new BufferedDataTable[]{originalDataTable, modelTable});
        return this;
    }

    /**
     * @param modelFeatureColumnIndex
     * @param rowIDColumnIndex
     * @param predictionColIndex
     * @return Builder
     */
    public PartialDependenceICEJSONBuilder setColumnIndicies(final int modelFeatureColumnIndex,
        final int rowIDColumnIndex, final int predictionColIndex) {
        m_modelFeatureColInd = modelFeatureColumnIndex;
        m_rowIDColInd = rowIDColumnIndex;
        m_predictionColInd = predictionColIndex;
        return this;
    }

    /**
     * @param maxRows
     * @return Builder
     */
    public PartialDependenceICEJSONBuilder setMaxRows(final int maxRows) {
        m_maxRows = maxRows;
        return this;
    }

    /**
     * @param hasCorrectColumns
     * @return Builder
     */
    public PartialDependenceICEJSONBuilder setHasCorrectColumns(final boolean hasCorrectColumns) {
        m_hasCorrectColumns = hasCorrectColumns;
        return this;
    }

    /**
     * @param isContigouos
     * @return Builder
     */
    public PartialDependenceICEJSONBuilder setIsContiguous(final boolean isContigouos) {
        m_isContiguous = isContigouos;
        return this;
    }

    /**
     * @param origFeatureColName
     * @return Builder
     */
    public PartialDependenceICEJSONBuilder setOrigFeatureColName(final String origFeatureColName) {
        m_origFeatureColName = origFeatureColName;
        return this;
    }

    /**
     * @param isOrdered
     * @return Builder
     */
    public PartialDependenceICEJSONBuilder setIsOrdered(final boolean isOrdered) {
        m_isOrdered = isOrdered;
        return this;
    }

    /**
     * @return originalDataTable, modelTable; for internal use only
     */
    @Override
    public BufferedDataTable[] getInternalTables() {
        return new BufferedDataTable[]{m_originalDataTable, m_modelTable};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setInternalTables(final BufferedDataTable[] tables) {
        m_originalDataTable = tables[0];
        m_modelTable = tables[1];
    }

    /**
     * @param message
     */
    private void addWarningMessage(final String message) {
        m_warnings.add(message);
    }

    /**
     * @return internal warning messages
     */
    public List<String> getWarningMessage() {
        return m_warnings;
    }

    /**
     * @param exec
     * @return JSONDataTable
     * @throws CanceledExecutionException
     * @throws Exception
     */
    public JSONDataTable build(final ExecutionContext exec) throws CanceledExecutionException, Exception {
        // check for excluded column, only include original feature column
        DataTableSpec originalTableSpec = m_originalDataTable.getDataTableSpec();
        ArrayList<String> m_excludedColumns = new ArrayList<String>();
        for (String colName : originalTableSpec.getColumnNames()) {
            if (!colName.trim().toLowerCase().equals(m_origFeatureColName.trim().toLowerCase())) {
                m_excludedColumns.add(colName);
            } else {
                // nothing, we found our original feature column
            }
        }

        // convert the original data table into a JSONDataTable
        JSONDataTable.Builder builder = JSONDataTable.newBuilder().setDataTable(m_originalDataTable)
            .setExcludeColumns(m_excludedColumns.toArray(new String[0]))
            .setId(String.valueOf(PartialDependenceICEPlotConfig.ORIGINAL_DATA_TABLE_INPORT)).setFirstRow(1)
            .setMaxRows(m_maxRows);
        final JSONDataTable jsonDataTable = builder.build(exec.createSubProgress(.50));
        exec.setProgress(.50);
        ExecutionMonitor subMonitor = exec.createSubProgress(.49);
        final JSONDataTableSpec tableSpec = jsonDataTable.getSpec();
        JSONDataTableRow[] tableRows = jsonDataTable.getRows();
        String[] rowColors = tableSpec.getRowColorValues();
        if (rowColors != null && rowColors.length == tableRows.length) {
            tableSpec.setRowColorValues(rowColors);
        }
        int jsonFeatureInd = tableSpec.getColumnIndex(m_origFeatureColName);
        tableSpec.setNumColumns(tableSpec.getNumColumns() + 1);
        String[] jsonColName = new String[tableSpec.getColNames().length + 1];
        System.arraycopy(tableSpec.getColNames(), 0, jsonColName, 0, tableSpec.getColNames().length);
        jsonColName[jsonColName.length - 1] = PartialDependenceICEPlotConfig.INTERNAL_JSON_MODEL_VALUE_COL_NAME;
        tableSpec.setColNames(jsonColName);

        m_isContiguous = checkIfContiguous();
        m_isOrdered = checkIfOrdered();

        if (m_hasCorrectColumns && m_isContiguous && m_isOrdered && m_totalSamplesPerRow > 0 && !m_failedExec) {
            /*
             * more performant implementation avoiding map IFF : a) its verified that the data is
             * sequential (Row0_Row0, Row0_Row1, Row0_Row2, etc...) b) its verified that the
             * data is ordered; Row0_Row0 = .1, Row0_Row1 = .3, Row0_Row2 = .5, etc. c)
             * (optional) column names, etc. are all the same d) can set warnings if low
             * number of samples per row e) it didn't fail upon first try
             */

            if (m_modelTable.size() % m_totalSamplesPerRow != 0) {
                m_failedExec = true;
                return this.build(exec);
            } else {
                int count = 0;
                int totalRowCount = 0;
                Double[][] modelValues = new Double[m_totalSamplesPerRow][2];
                Set<String> keySet = new HashSet<String>();
                Set<String> missingFeatureValues = new HashSet<String>();
                for (DataRow row : m_modelTable) {
                    subMonitor.setProgress(totalRowCount / m_maxRows);
                    DataCell featureCell = row.getCell(m_modelFeatureColInd);
                    DataCell predictionCell = row.getCell(m_predictionColInd);
                    String currentKeyString = ((StringCell)row.getCell(m_rowIDColInd)).getStringValue();
                    if (count < m_totalSamplesPerRow) {
                        keySet.add(currentKeyString);
                        modelValues[count] =
                            new Double[]{getDoubleFromCell(featureCell), getDoubleFromCell(predictionCell)};
                        count++;
                    } else {
                        if (totalRowCount >= m_maxRows - 1) {
                            break;
                        }
                        JSONDataTableRow currRow = tableRows[totalRowCount];
                        if (keySet.size() == 1 && keySet.contains(currRow.getRowKey())) {
                            if ((currRow.getData()[jsonFeatureInd]) == null) {
                                missingFeatureValues.add(currentKeyString);
                            }
                            Object[] rowData = new Object[]{currRow.getData()[0], modelValues};
                            currRow.setData(rowData);
                            modelValues = new Double[m_totalSamplesPerRow][2];
                            modelValues[0] =
                                new Double[]{getDoubleFromCell(featureCell), getDoubleFromCell(predictionCell)};
                            count = 1;
                            totalRowCount++;
                            keySet = new HashSet<String>();
                        } else {
                            // breakpoint #2
                            m_failedExec = true;
                            return this.build(exec);
                        }
                    }
                }
                JSONDataTableRow currRow = tableRows[totalRowCount];
                if ((currRow.getData()[jsonFeatureInd]) == null) {
                    missingFeatureValues.add(currRow.getRowKey());
                }
                int numMissing = missingFeatureValues.size();
                if (numMissing > 0) {
                    addWarningMessage(String.valueOf(numMissing) + " rows are missing an original feature value. "
                        + "The model output is displayed as an ICE line on the plot, but the data points for this "
                        + String.valueOf(numMissing) + " will not be included.");
                }
                Object[] rowAsList = new Object[]{currRow.getData()[0], modelValues};
                currRow.setData(rowAsList);
                subMonitor.setProgress(1);
                return jsonDataTable;
            }
        } else {
            // manually merging both data sets together. This is the slow implementation
            LinkedHashMap<String, ArrayList<Double[]>> originalDataTableMap =
                new LinkedHashMap<String, ArrayList<Double[]>>();
            ExecutionMonitor firstIterationMonitor = subMonitor.createSubProgress(.25);
            // for each row in the original data table, we map its key to a coordinate array
            for (int rowInd = 0; rowInd < tableRows.length; rowInd++) {
                firstIterationMonitor.setProgress(rowInd / tableRows.length);
                JSONDataTableRow currRow = tableRows[rowInd];
                originalDataTableMap.put(currRow.getRowKey(), new ArrayList<Double[]>());
            }
            ExecutionMonitor secondIterationMonitor = subMonitor.createSubProgress(.50);
            int count = 0;
            int totalRows = ((Long)m_modelTable.size()).intValue();
            for (DataRow row : m_modelTable) {
                secondIterationMonitor.setProgress(count / totalRows);
                String ownerRowID = ((StringCell)row.getCell(m_rowIDColInd)).getStringValue();
                DataCell featureCell = row.getCell(m_modelFeatureColInd);
                DataCell predictionCell = row.getCell(m_predictionColInd);
                originalDataTableMap.getOrDefault(ownerRowID, new ArrayList<Double[]>())
                    .add(new Double[]{getDoubleFromCell(featureCell), getDoubleFromCell(predictionCell)});
                count++;
            }
            count = 0;
            ExecutionMonitor thirdIterationMonitor = subMonitor.createSubProgress(.25);
            for (JSONDataTableRow jsonRow : tableRows) {
                thirdIterationMonitor.setProgress(count / tableRows.length);
                Object[] rowData =
                    new Object[]{jsonRow.getData()[0], originalDataTableMap.get(jsonRow.getRowKey()).toArray()};
                jsonRow.setData(rowData);
                count++;
            }
            return jsonDataTable;
        }
    }

    /**
     * @param dataCell to convert to double. Currently supports DoubleCells, IntCells and LongCells
     * @return double value from the cell
     */
    private static Double getDoubleFromCell(final DataCell dataCell) {
        if (dataCell instanceof DoubleCell) {
            return ((DoubleCell)dataCell).getDoubleValue();
        } else if (dataCell instanceof IntCell) {
            return ((IntCell)dataCell).getDoubleValue();
        } else {
            return ((LongCell)dataCell).getDoubleValue();
        }
    }

    /**
     * @return boolean if the model output table is ordered and, therefore, can be processed more efficiently
     */
    private boolean checkIfOrdered() {

        if (m_totalSamplesPerRow < 1) {
            return false;
        } else {
            int iter = 0;
            double previousValue = -1 * Double.MAX_VALUE;
            for (DataRow row : m_modelTable) {
                if (iter >= m_totalSamplesPerRow) {
                    return true;
                }
                double currRowValue = getDoubleFromCell(row.getCell(m_modelFeatureColInd));
                if (currRowValue >= previousValue) {
                    previousValue = currRowValue;
                    iter++;
                } else {
                    return false;
                }
            }
            return false;
        }

    }

    /**
     * @return boolean if the model output table is contiguous and, therefore, can be processed more efficiently
     */
    private boolean checkIfContiguous() throws Exception {

        int previousRowNumber = -1;
        int previousSampleNumber = -1;
        int currentSamplesPerRow = 0;
        int masterSamplesPerRow = 0;

        for (DataRow row : m_modelTable) {
            String[] rowKey = row.getKey().toString().split("_");
            if (rowKey.length < 2) {
                return false;
            }
            try {
                int rowSampleNumber = Integer.valueOf(rowKey[1].substring(3));
                int origRowNumber = Integer.valueOf(rowKey[0].substring(3));
                //this will only happen if the model output has been intentionally shuffled
                if (origRowNumber < previousRowNumber && rowSampleNumber > previousSampleNumber) {
                    throw new Exception("The model output table has been illegally modified prior to execution."
                        + " Please see KNIME Log for more details.");
                }
                if (rowSampleNumber > previousSampleNumber) {
                    previousSampleNumber = rowSampleNumber;
                    previousRowNumber = Integer.valueOf(rowKey[0].substring(3));
                    currentSamplesPerRow++;
                } else {
                    if (masterSamplesPerRow == 0) {
                        masterSamplesPerRow = currentSamplesPerRow;
                        previousSampleNumber = -1;
                        currentSamplesPerRow = 0;
                    } else {
                        currentSamplesPerRow++;
                        if (masterSamplesPerRow != currentSamplesPerRow) {
                            return false;
                        } else {
                            m_totalSamplesPerRow = masterSamplesPerRow;
                            return true;
                        }
                    }
                }
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return false;
    }
}
