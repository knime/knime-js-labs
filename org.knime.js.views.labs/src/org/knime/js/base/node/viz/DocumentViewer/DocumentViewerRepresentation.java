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
 *   Oct 25, 2018 (dewi): created
 */
package org.knime.js.base.node.viz.DocumentViewer;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.ext.textprocessing.data.Document;
import org.knime.ext.textprocessing.data.IndexedTerm;
import org.knime.js.core.JSONDataTable;
import org.knime.js.core.node.table.AbstractTableRepresentation;
import org.knime.js.core.settings.table.TableRepresentationSettings;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

/**
 * Representation class for Brat Document Viewer node. It contains the values needed for the visualization. The values
 * stay unchanged throughout the view.
 *
 * @author Daniel Bogenrieder, KNIME GmbH, Konstanz, Germany
 */
@JsonAutoDetect
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class DocumentViewerRepresentation extends AbstractTableRepresentation {


    private String m_documentCol;
    private ArrayList<BratDocument> m_bratDocuments = new ArrayList<>();

    private boolean m_showLineNumbers;

    private TableRepresentationSettings m_settings = new TableRepresentationSettings();

    /** Serialization constructor. Don't use. */
    public DocumentViewerRepresentation() { }

    /**
     * Initialize the lists with terms and tags.
     *
     * @param doc the document
     * @param terms the indexed terms of the document
     */
    public void add(final Document doc, final List<IndexedTerm> terms) {
        BratDocument bratDocument = new BratDocument();

        bratDocument.setDocText(doc.getDocumentBodyText());
        bratDocument.setDocTitle(doc.getTitle());

        // index used for term ID
        int idx = 1;
        for (IndexedTerm term : terms) {
            for (String tag : term.getTagValues()) {
                int firstPos = term.getStartIndex();
                int lastPos = term.getStopIndex();
                // if firstPos < 0, it means the current term is the title, which we don't want, so skip it
                bratDocument.getTermIds().add("T" + idx++);
                bratDocument.getTags().add(tag);
                bratDocument.getTerms().add(term.getTermValue());
                bratDocument.getStartIndexes().add(Integer.toString(firstPos));
                bratDocument.getStopIndexes().add(Integer.toString(lastPos));
            }
        }
        // generate colors for tags
        bratDocument.setRandColorToTags();
        m_bratDocuments.add(bratDocument);
    }

    /**
     * @param documentCol the labelCol to set
     */
    public void setDocumentCol(final String documentCol) {
        m_documentCol = documentCol;
    }

    /**
     * @return the brat documents
     */
    public ArrayList<BratDocument> getBratDocuments() {
        return m_bratDocuments;
    }

    /**
     * @param showLineNumbers the boolean if line numbers should be shown
     */
    public void setShowLineNumbers(final boolean showLineNumbers) {
        m_showLineNumbers = showLineNumbers;
    }

    /**
     * @return if line numbers should be displayed
     */
    public boolean getShowLineNumbers() {
        return m_showLineNumbers;
    }

    /**
     * @param bratDocument the brat documents to set
     */
    public void setBratDocuments(final ArrayList<BratDocument> bratDocument) {
        m_bratDocuments = bratDocument;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @JsonUnwrapped
    public TableRepresentationSettings getSettings() {
        return m_settings;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSettings(final TableRepresentationSettings settings) {
        m_settings = settings;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSettingsFromDialog(final TableRepresentationSettings settings) {
        final JSONDataTable table = m_settings.getTable();
        m_settings = settings;
        m_settings.setTable(table);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveToNodeSettings(final NodeSettingsWO settings) {
        m_settings.saveSettings(settings);
        settings.addString(DocumentViewerConfig.CFG_DOCUMENT_COL, m_documentCol);
        settings.addBoolean(DocumentViewerConfig.CFG_SHOW_LINE_NUMBERS, m_showLineNumbers);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadFromNodeSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        m_settings.loadSettings(settings);
        m_documentCol = settings.getString(DocumentViewerConfig.CFG_DOCUMENT_COL);
        m_showLineNumbers = settings.getBoolean(DocumentViewerConfig.CFG_SHOW_LINE_NUMBERS);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }

        DocumentViewerRepresentation other = (DocumentViewerRepresentation)obj;
        return new EqualsBuilder()
                .append(m_settings, other.getSettings())
                .append(m_bratDocuments, other.getBratDocuments())
                .append(m_showLineNumbers, other.getShowLineNumbers())
                .isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(m_settings)
                .append(m_bratDocuments)
                .append(m_showLineNumbers)
                .toHashCode();
    }


    /**
     *
     * Class to save BratDocuments with all its attributes
     */
    @JsonAutoDetect
    public static final class BratDocument {

        private String m_docText;

        private String m_docTitle;

        private List<String> m_termIds;

        private List<String> m_tags;

        private List<String> m_terms;

        private List<String> m_startIndexes;

        private List<String> m_stopIndexes;

        private List<String> m_colors;

        BratDocument() {
            m_termIds = new ArrayList<>();
            m_tags = new ArrayList<>();
            m_terms = new ArrayList<>();
            m_startIndexes = new ArrayList<>();
            m_stopIndexes = new ArrayList<>();
        }

        /**
         * @return the document text
         */
        public String getDocText() {
            return m_docText;
        }

        /**
         * @param docText
         */
        public void setDocText(final String docText) {
            m_docText = docText;
        }

        /**
         * @return the document title
         */
        public String getDocTitle() {
            return m_docTitle;
        }

        /**
         * @param docTitle
         */
        public void setDocTitle(final String docTitle) {
            m_docTitle = docTitle;
        }

        /**
         * @return the term ids
         */
        public List<String> getTermIds() {
            return m_termIds;
        }

        /**
         * @param term ids to set
         */
        public void setTermIds(final List<String> termIds) {
            m_termIds = termIds;
        }

        /**
         * @return document tags
         */
        public List<String> getTags() {
            return m_tags;
        }

        /**
         * @param tags tags to set
         */
        public void setTags(final List<String> tags) {
            m_tags = tags;
        }

        /**
         * @return terms
         */
        public List<String> getTerms() {
            return m_terms;
        }

        /**
         * @param terms terms to set
         */
        public void setTerms(final List<String> terms) {
            m_terms = terms;
        }

        /**
         * @return start indexes
         */
        public List<String> getStartIndexes() {
            return m_startIndexes;
        }

        /**
         * @param startIndexes to set
         */
        public void setStartIndexes(final List<String> startIndexes) {
            m_startIndexes = startIndexes;
        }

        /**
         * @return stop indexes
         */
        public List<String> getStopIndexes() {
            return m_stopIndexes;
        }

        /**
         * @param stopIndexes to set
         */
        public void setStopIndexes(final List<String> stopIndexes) {
            m_stopIndexes = stopIndexes;
        }

        /**
         * @return list of colors
         */
        public List<String> getColors() {
            return m_colors;
        }

        /**
         * @param colors to set
         */
        public void setColors(final List<String> colors) {
            m_colors = colors;
        }

        /**
         * Generate random static color for each tag.
         */
        public void setRandColorToTags() {
            m_colors = new ArrayList<>();
            // get unique tags
            List<String> tags = m_tags.stream().distinct().collect(Collectors.toList());
            // set random color
            for (String tag : tags) {
                Color randColor = new Color((int)(new Random(tag.hashCode()).nextDouble() * 0x1000000)).brighter();
                m_colors
                    .add(String.format("#%02x%02x%02x", randColor.getRed(), randColor.getGreen(), randColor.getBlue()));
            }
        }
    }
}
