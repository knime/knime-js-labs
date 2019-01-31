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
package org.knime.js.base.node.viz.bratDocumentViewer;

import java.util.Arrays;
import java.util.List;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.RowIterator;
import org.knime.core.data.container.ColumnRearranger;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.web.ValidationError;
import org.knime.ext.textprocessing.data.Document;
import org.knime.ext.textprocessing.data.DocumentValue;
import org.knime.ext.textprocessing.data.IndexedTerm;
import org.knime.ext.textprocessing.util.ColumnSelectionVerifier;
import org.knime.ext.textprocessing.util.DataTableSpecVerifier;
import org.knime.ext.textprocessing.util.DocumentUtil;
import org.knime.js.core.JSONDataTable;
import org.knime.js.core.node.table.AbstractTableNodeModel;

/**
 * The {@link NodeModel} for the Brat Document Viewer. This node visualizes only the first document.
 *
 * @author Andisa Dewi, KNIME AG, Berlin, Germany
 */
final class BratDocumentViewerNodeModel
    extends AbstractTableNodeModel<BratDocumentViewerRepresentation, BratDocumentViewerValue> {

    /**
     * The SettingsModelString for the document column.
     */
    private final SettingsModelString m_docColModel = BratDocumentViewerNodeDialog.getDocColModel();


    /**
     * The constructor of the Brat Document Viewer node. The node has one input port and no output port.
     *
     * @param viewName the name for the interactive view
     */
    BratDocumentViewerNodeModel(final String viewName) {
        super(viewName, new BratDocumentViewerConfig());
    }

    /**
     * Check the input data table spec.
     *
     * @param spec The data table spec to check
     * @throws InvalidSettingsException
     */
    private final void checkDataTableSpec(final DataTableSpec spec) throws InvalidSettingsException {
        // check input spec
        DataTableSpecVerifier verifier = new DataTableSpecVerifier(spec);
        verifier.verifyMinimumDocumentCells(1, true);

        ColumnSelectionVerifier.verifyColumn(m_docColModel, spec, DocumentValue.class, null)
            .ifPresent(msg -> setWarningMessage(msg));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) {
        m_docColModel.saveSettingsTo(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        m_docColModel.validateSettings(settings);
        m_config.loadSettings(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings) throws InvalidSettingsException {
        m_docColModel.loadSettingsFrom(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BratDocumentViewerRepresentation createEmptyViewRepresentation() {
        return new BratDocumentViewerRepresentation();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BratDocumentViewerValue createEmptyViewValue() {
        return new BratDocumentViewerValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getJavascriptObjectID() {
        return "org.knime.js.base.node.viz.bratdocumentviewer";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isHideInWizard() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setHideInWizard(final boolean hide) {
        // nothing to do
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ValidationError validateViewValue(final BratDocumentViewerValue viewContent) {
        synchronized (getLock()) {
            // validate value, nothing to do
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveCurrentValue(final NodeSettingsWO content) {
        // nothing to do
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void performReset() {
        // nothing to do
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void useCurrentValueAsDefault() {
        // nothing to do
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PortObject[] performExecute(final PortObject[] inObjects, final ExecutionContext exec) throws Exception {
        BufferedDataTable out = (BufferedDataTable)inObjects[0];
        synchronized (getLock()) {
            final BratDocumentViewerRepresentation viewRepresentation = getViewRepresentation();
            if (viewRepresentation.getSettings().getTable() == null) {
                m_table = (BufferedDataTable)inObjects[0];
                final JSONDataTable jsonTable = createJSONTableFromBufferedDataTable(m_table,
                    exec.createSubExecutionContext(0.5));
                viewRepresentation.getSettings().setTable(jsonTable);
                copyConfigToRepresentation();
            }

            if (m_config.getSettings().getRepresentationSettings().getEnableSelection()) {
                final BratDocumentViewerValue viewValue = getViewValue();
                List<String> selectionList = null;
                if (viewValue != null) {
                    if (viewValue.getSettings().getSelection() != null) {
                        selectionList = Arrays.asList(viewValue.getSettings().getSelection());
                    }
                }
                final ColumnRearranger rearranger = createColumnAppender(m_table.getDataTableSpec(), selectionList);
                out = exec.createColumnRearrangeTable(m_table, rearranger, exec.createSubExecutionContext(0.5));
            }
            viewRepresentation.getSettings().setSubscriptionFilterIds(
                getSubscriptionFilterIds(m_table.getDataTableSpec()));
            BufferedDataTable in = (BufferedDataTable)inObjects[0];
            checkDataTableSpec(in.getDataTableSpec());
            final int docColIndex = in.getDataTableSpec().findColumnIndex(m_docColModel.getStringValue());

            final RowIterator it = in.iterator();
            if (it.hasNext()) {
                while (it.hasNext()) {
                    // take only the first row
                    final DataCell docCell = it.next().getCell(docColIndex);
                    if (!docCell.isMissing()) {
                        // get the document
                        final Document doc = ((DocumentValue)docCell).getDocument();
                        // get the indexed terms from the doc
                        final List<IndexedTerm> terms = DocumentUtil.getIndexedTerms(doc);
                        final BratDocumentViewerRepresentation rep = getViewRepresentation();
                        // set the values in the representation class
                        rep.add(doc, terms);
//                        if (it.hasNext()) {
//                            setWarningMessage("Only document in the first row is visualized in the view.");
//                        }
                    } else {
                        // If there is no document in the first row
                        // set warning message
                        setWarningMessage("There is no document in the first row.");
                    }
                }
            } else {
                setWarningMessage("Input table is empty.");
            }
        }
        return new PortObject[]{out};
    }

    /**
     * Copies the settings from dialog into representation and values objects.
     */
    @Override
    protected void copyConfigToRepresentation() {
        synchronized (getLock()) {
            final BratDocumentViewerConfig conf = (BratDocumentViewerConfig)m_config;
            final BratDocumentViewerRepresentation viewRepresentation = getViewRepresentation();
            // Use setSettingsFromDialog, it ensures the table that got set on the representation settings is preserved
            viewRepresentation.setSettingsFromDialog(m_config.getSettings().getRepresentationSettings());
            viewRepresentation.setUseNumCols(conf.getUseNumCols());
            viewRepresentation.setUseColWidth(conf.getUseColWidth());
            viewRepresentation.setNumCols(conf.getNumCols());
            viewRepresentation.setColWidth(conf.getColWidth());
            viewRepresentation.setLabelCol(conf.getLabelCol());
            viewRepresentation.setUseRowID(conf.getUseRowID());
            viewRepresentation.setAlignLeft(conf.getAlignLeft());
            viewRepresentation.setAlignRight(conf.getAlignRight());
            viewRepresentation.setAlignCenter(conf.getAlignCenter());

            final BratDocumentViewerValue viewValue = getViewValue();
            if (isViewValueEmpty()) {
                viewValue.setSettings(m_config.getSettings().getValueSettings());
            }
        }
    }

}
