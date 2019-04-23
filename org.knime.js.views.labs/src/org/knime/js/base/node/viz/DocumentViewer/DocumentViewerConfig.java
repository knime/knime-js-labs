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

import org.knime.core.data.DataTableSpec;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.js.core.node.table.TableConfig;
import org.knime.js.core.settings.table.TableSettings;

/**
 * The configuration keys for the Brat Document Viewer node.
 *
 * @author Daniel Bogenrieder, KNIME GmbH, Konstanz, Germany
 */
public class DocumentViewerConfig implements TableConfig {

    static final String CFG_DOCUMENT_COL = "documentCol";
    private static final String DEFAULT_DOCUMENT_COL = null;
    private String m_documentCol = DEFAULT_DOCUMENT_COL;

    static final int INITIAL_PAGE_SIZE = 1;

    static final int[] ALLOWED_PAGE_SIZE = new int[]{1, 5, 10, 20};

    private TableSettings m_settings = new TableSettings();

    static final String CFG_SHOW_LINE_NUMBERS = "showLineNumber";
    private static final Boolean DEFAULT_SHOW_LINE_NUMBERS = false;
    private boolean m_showLineNumbers = DEFAULT_SHOW_LINE_NUMBERS;

    @SuppressWarnings("javadoc")
    public DocumentViewerConfig() {
        super();
        m_settings.getRepresentationSettings().setInitialPageSize(INITIAL_PAGE_SIZE);
        m_settings.getRepresentationSettings().setAllowedPageSizes(ALLOWED_PAGE_SIZE);
        m_settings.setSelectionColumnName("Selected (Document Viewer (JavaScript))");
    }

    /**
     * @return the documentCol
     */
    public String getDocumentCol() {
        return m_documentCol;
    }

    /**
     * @param documentCol the documentCol to set
     */
    public void setDocumentCol(final String documentCol) {
        m_documentCol = documentCol;
    }

    public boolean getShowLineNumbers() {
        return m_showLineNumbers;
    }

    public void setShowLineNumbers(final boolean showLineNumbers) {
        m_showLineNumbers = showLineNumbers;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TableSettings getSettings() {
        return m_settings;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSettings(final TableSettings settings) {
        m_settings = settings;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveSettings(final NodeSettingsWO settings) {
        m_settings.saveSettings(settings);
        settings.addString(CFG_DOCUMENT_COL, m_documentCol);
        settings.addBoolean(CFG_SHOW_LINE_NUMBERS, m_showLineNumbers);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
    	final int initPageSize = settings.getInt("initialPageSize");
        final int maxRows = settings.getInt("maxRows");
        final boolean enablePaging = settings.getBoolean("enablePaging");
        validateConfig(initPageSize, maxRows,
            enablePaging);

        m_settings.loadSettings(settings);
        m_documentCol = settings.getString(CFG_DOCUMENT_COL);
        m_showLineNumbers = settings.getBoolean(CFG_SHOW_LINE_NUMBERS);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadSettingsForDialog(final NodeSettingsRO settings, final DataTableSpec spec) {
    	m_settings.loadSettingsForDialog(settings, spec);
        m_documentCol = settings.getString(CFG_DOCUMENT_COL, DEFAULT_DOCUMENT_COL);
        m_showLineNumbers = settings.getBoolean(CFG_SHOW_LINE_NUMBERS, DEFAULT_SHOW_LINE_NUMBERS);
    }

    static void validateConfig(final int initPageSize, final int maxRows,
        final boolean enablePaging ) throws InvalidSettingsException {
        String errorMsg = "";
        if (maxRows < 0) {
            errorMsg += "No. of rows to display (" + maxRows + ") cannot be negative.\n";
        }
        if (initPageSize < 1 && enablePaging) {
            if (!errorMsg.isEmpty()) {
                errorMsg+="\n";
            }
            errorMsg += "Initial page size (" + initPageSize + ") cannot be less than 1.\n";
        }
        if (!errorMsg.isEmpty()) {
            throw new InvalidSettingsException(errorMsg);
        }
    }
}
