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
 *   Oct 19, 2018 (dewi): created
 */
package org.knime.js.base.node.viz.DocumentViewer;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.knime.core.data.DataCell;
import org.knime.core.data.RowIterator;
import org.knime.core.data.RowIteratorBuilder;
import org.knime.core.data.container.CloseableRowIterator;
import org.knime.core.data.container.ColumnRearranger;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.port.PortObject;
import org.knime.ext.textprocessing.data.Document;
import org.knime.ext.textprocessing.data.DocumentValue;
import org.knime.ext.textprocessing.data.IndexedTerm;
import org.knime.ext.textprocessing.util.DocumentUtil;
import org.knime.js.core.JSONDataTable;
import org.knime.js.core.node.table.AbstractTableNodeModel;

/**
 * The {@link NodeModel} for the Brat Document Viewer. This node visualizes only the first document.
 *
 * @author Daniel Bogenrieder, KNIME GmbH, Konstanz, Germany
 */
final class DocumentViewerNodeModel extends AbstractTableNodeModel<DocumentViewerRepresentation, DocumentViewerValue> {

    /**
     * The constructor of the Brat Document Viewer node. The node has one input port and no output port.
     *
     * @param viewName the name for the interactive view
     */
    DocumentViewerNodeModel(final String viewName) {
        super(viewName, new DocumentViewerConfig());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DocumentViewerRepresentation createEmptyViewRepresentation() {
        return new DocumentViewerRepresentation();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DocumentViewerValue createEmptyViewValue() {
        return new DocumentViewerValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getJavascriptObjectID() {
        return "org.knime.js.base.node.viz.documentviewer";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PortObject[] performExecute(final PortObject[] inObjects, final ExecutionContext exec) throws Exception {
        BufferedDataTable out = (BufferedDataTable)inObjects[0];
        synchronized (getLock()) {
            final DocumentViewerRepresentation viewRepresentation = getViewRepresentation();
            if (viewRepresentation.getSettings().getTable() == null) {
                m_table = (BufferedDataTable)inObjects[0];
                final JSONDataTable jsonTable =
                    createJSONTableFromBufferedDataTable(m_table, exec.createSubExecutionContext(0.33));
                viewRepresentation.getSettings().setTable(jsonTable);
                copyConfigToRepresentation();
            }

            if (m_config.getSettings().getRepresentationSettings().getEnableSelection()) {
                final DocumentViewerValue viewValue = getViewValue();
                List<String> selectionList = null;
                if (viewValue != null) {
                    if (viewValue.getSettings().getSelection() != null) {
                        selectionList = Arrays.asList(viewValue.getSettings().getSelection());
                    }
                }
                final ColumnRearranger rearranger = createColumnAppender(m_table.getDataTableSpec(), selectionList);
                out = exec.createColumnRearrangeTable(m_table, rearranger, exec.createSubExecutionContext(0.33));
            }
            viewRepresentation.getSettings()
                .setSubscriptionFilterIds(getSubscriptionFilterIds(m_table.getDataTableSpec()));
            if ((viewRepresentation.getBratDocuments() == null || viewRepresentation.getBratDocuments().size() == 0)
                && m_table != null) {
                createDocuments(exec, m_table, viewRepresentation);
            }
        }
        exec.setProgress(1);
        return new PortObject[]{out};
    }

    @Override
    protected JSONDataTable.Builder getJsonDataTableBuilder(final BufferedDataTable table) {
        JSONDataTable.Builder builder = super.getJsonDataTableBuilder(table);
        builder.excludeRowsWithMissingValues(true);
        return builder;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DocumentViewerRepresentation getViewRepresentation() {
        DocumentViewerRepresentation rep = super.getViewRepresentation();
        synchronized (getLock()) {
            if ((rep.getBratDocuments() == null || rep.getBratDocuments().size() == 0) && m_table != null) {
                // set internal table
                createDocuments(null, m_table, rep);
            }
        }
        return rep;
    }

    public void createDocuments(final ExecutionContext exec, final BufferedDataTable in,
        final DocumentViewerRepresentation viewRepresentation) {
        String documentCol = ((DocumentViewerConfig)m_config).getDocumentCol();
        if (documentCol == null || documentCol == "") {
            throw new IllegalArgumentException("No column selected for the document column.");
        }

        final int docColIndex = in.getDataTableSpec().findColumnIndex(documentCol);
        ExecutionContext docContext = null;
        int counter = 0;

        RowIteratorBuilder<? extends CloseableRowIterator> builder = in.iteratorBuilder();
        builder.filterColumns(documentCol);
        @SuppressWarnings("resource")
        final RowIterator it = builder.build();
        if (exec != null) {
            docContext = exec.createSubExecutionContext(0.33);
            docContext.setMessage("Extracting documents...");
        }
        while (it.hasNext()) {
            counter++;
            if (docContext != null) {
                try {
                    docContext.checkCanceled();
                    docContext.setProgress((double)in.size() / counter);
                } catch (CanceledExecutionException e) {
                    break;
                }
            }
            // take only the first row
            final DataCell docCell = it.next().getCell(docColIndex);
            if (!docCell.isMissing()) {
                // get the document
                final Document doc = ((DocumentValue)docCell).getDocument();
                // get the indexed terms from the doc
                final List<IndexedTerm> terms = DocumentUtil.getIndexedTerms(doc, false, "\n");
                // set the values in the representation class
                viewRepresentation.add(doc, terms);
            } else {
                // If there is no document in the first row
                // set warning message
                setWarningMessage("There are missing values in the Document Column");
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        m_config.loadSettings(settings);
    }

    @Override
    protected String[] determineExcludedColumns(final BufferedDataTable table) {
        String[] excluded = table.getSpec().getColumnNames();
        String labelColumn = ((DocumentViewerConfig)m_config).getDocumentCol();
        if (labelColumn == null) {
            return excluded;
        }
        Stream<String> result = Arrays.stream(excluded).filter(columnName -> !labelColumn.equals(columnName));
        return result.toArray(String[]::new);
    }

    /**
     * Copies the settings from dialog into representation and values objects.
     */
    @Override
    protected void copyConfigToRepresentation() {
        synchronized (getLock()) {
            final DocumentViewerRepresentation viewRepresentation = getViewRepresentation();
            // Use setSettingsFromDialog, it ensures the table that got set on the representation settings is preserved
            viewRepresentation.setSettingsFromDialog(m_config.getSettings().getRepresentationSettings());

            final DocumentViewerValue viewValue = getViewValue();
            if (isViewValueEmpty()) {
                viewValue.setSettings(m_config.getSettings().getValueSettings());
            }
        }
    }

}
