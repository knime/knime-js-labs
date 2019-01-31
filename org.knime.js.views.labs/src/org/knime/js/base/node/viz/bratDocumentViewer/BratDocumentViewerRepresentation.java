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
package org.knime.js.base.node.viz.bratDocumentViewer;

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

/**
 * Representation class for Brat Document Viewer node. It contains the values needed for the visualization. The values
 * stay unchanged throughout the view.
 *
 * @author Andisa Dewi, KNIME AG, Berlin, Germany
 */
@JsonAutoDetect
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public final class BratDocumentViewerRepresentation extends AbstractTableRepresentation {

    /**
     * The configuration key to store the document text.
     */
    private static final String CFG_DOC_TEXT = "documentText";

    /**
     * The configuration key to store the document title.
     */
    private static final String CFG_DOC_TITLE = "documentTitle";

    /**
     * The configuration key to store the term IDs.
     */
    private static final String CFG_DOC_TERM_IDS = "documentTermIds";

    /**
     * The configuration key to store the terms.
     */
    private static final String CFG_DOC_TERMS = "documentTerms";

    /**
     * The configuration key to store the tags.
     */
    private static final String CFG_DOC_TAGS = "documentTags";

    /**
     * The configuration key to store the start indexes.
     */
    private static final String CFG_DOC_START_IDX = "documentStartIndexes";

    /**
     * The configuration key to store the stop indexes.
     */
    private static final String CFG_DOC_STOP_IDX = "documentStopIndexes";

    /**
     * The configuration key to store the tag colors.
     */
    private static final String CFG_DOC_COLORS = "documentTagColors";

    private TableRepresentationSettings m_settings = new TableRepresentationSettings();

    private boolean m_useNumCols;
    private boolean m_useColWidth;
    private int m_numCols;
    private int m_colWidth;
    private String m_labelCol;
    private boolean m_useRowID;

    private boolean m_alignLeft;
    private boolean m_alignRight;
    private boolean m_alignCenter;
    private ArrayList<BratDocument> m_bratDocuments = new ArrayList<>();

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
        // we don't want to include the title in the body text
        int titleLength = bratDocument.getDocTitle().length();

        // index used for term ID
        int idx = 1;
        for (IndexedTerm term : terms) {
            for (String tag : term.getTagValues()) {
                int firstPos = term.getStartIndex() - titleLength;
                int lastPos = term.getStopIndex() - titleLength;
                // if firstPos < 0, it means the current term is the title, which we don't want, so skip it
                if (firstPos >= 0) {
                    bratDocument.getTermIds().add("T" + idx++);
                    bratDocument.getTags().add(tag);
                    bratDocument.getTerms().add(term.getTermValue());
                    bratDocument.getStartIndexes().add(Integer.toString(firstPos));
                    bratDocument.getStopIndexes().add(Integer.toString(lastPos));
                }
            }
        }
        // generate colors for tags
        bratDocument.setRandColorToTags();
        m_bratDocuments.add(bratDocument);
    }

    /**
     * @return the useNumCols
     */
    public boolean getUseNumCols() {
        return m_useNumCols;
    }

    /**
     * @param useNumCols the useNumCols to set
     */
    public void setUseNumCols(final boolean useNumCols) {
        m_useNumCols = useNumCols;
    }

    /**
     * @return the useColWidth
     */
    public boolean getUseColWidth() {
        return m_useColWidth;
    }

    /**
     * @param useColWidth the useColWidth to set
     */
    public void setUseColWidth(final boolean useColWidth) {
        m_useColWidth = useColWidth;
    }

    /**
     * @return the numCols
     */
    public int getNumCols() {
        return m_numCols;
    }

    /**
     * @param numCols the numCols to set
     */
    public void setNumCols(final int numCols) {
        m_numCols = numCols;
    }

    /**
     * @return the colWidth
     */
    public int getColWidth() {
        return m_colWidth;
    }

    /**
     * @param colWidth the colWidth to set
     */
    public void setColWidth(final int colWidth) {
        m_colWidth = colWidth;
    }

    /**
     * @return the labelCol
     */
    public String getLabelCol() {
        return m_labelCol;
    }

    /**
     * @param labelCol the labelCol to set
     */
    public void setLabelCol(final String labelCol) {
        m_labelCol = labelCol;
    }

    /**
     * @return the useRowID
     */
    public boolean getUseRowID() {
        return m_useRowID;
    }

    /**
     * @param useRowID the useRowID to set
     */
    public void setUseRowID(final boolean useRowID) {
        m_useRowID = useRowID;
    }

    /**
     * @return the alignLeft
     */
    public boolean getAlignLeft() {
        return m_alignLeft;
    }

    /**
     * @param alignLeft the alignLeft to set
     */
    public void setAlignLeft(final boolean alignLeft) {
        m_alignLeft = alignLeft;
    }

    /**
     * @return the alignRight
     */
    public boolean getAlignRight() {
        return m_alignRight;
    }

    /**
     * @param alignRight the alignRight to set
     */
    public void setAlignRight(final boolean alignRight) {
        m_alignRight = alignRight;
    }

    /**
     * @return the alignCenter
     */
    public boolean getAlignCenter() {
        return m_alignCenter;
    }

    /**
     * @param alignCenter the alignCenter to set
     */
    public void setAlignCenter(final boolean alignCenter) {
        m_alignCenter = alignCenter;
    }

    public ArrayList<BratDocument> getBratDocuments() {
        return m_bratDocuments;
    }

    public void setBratDocuments(final ArrayList<BratDocument> bratDocument) {
        m_bratDocuments = bratDocument;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveToNodeSettings(final NodeSettingsWO settings) {
        m_settings.saveSettings(settings);
//        if (m_docText != null && m_terms != null) {
//            settings.addString(CFG_DOC_TEXT, m_docText);
//            settings.addString(CFG_DOC_TITLE, m_docTitle);
//            settings.addStringArray(CFG_DOC_TERM_IDS, m_termIds.toArray(new String[]{}));
//            settings.addStringArray(CFG_DOC_TERMS, m_terms.toArray(new String[]{}));
//            settings.addStringArray(CFG_DOC_TAGS, m_tags.toArray(new String[]{}));
//            settings.addStringArray(CFG_DOC_START_IDX, m_startIndexes.toArray(new String[]{}));
//            settings.addStringArray(CFG_DOC_STOP_IDX, m_stopIndexes.toArray(new String[]{}));
//            settings.addStringArray(CFG_DOC_COLORS, m_colors.toArray(new String[]{}));
            settings.addBoolean(BratDocumentViewerConfig.CFG_USE_NUM_COLS, m_useNumCols);
            settings.addBoolean(BratDocumentViewerConfig.CFG_USE_COL_WIDTH, m_useColWidth);
            settings.addInt(BratDocumentViewerConfig.CFG_NUM_COLS, m_numCols);
            settings.addInt(BratDocumentViewerConfig.CFG_COL_WIDTH, m_colWidth);
            settings.addString(BratDocumentViewerConfig.CFG_LABEL_COL, m_labelCol);
            settings.addBoolean(BratDocumentViewerConfig.CFG_USE_ROW_ID, m_useRowID);
            settings.addBoolean(BratDocumentViewerConfig.CFG_ALIGN_LEFT, m_alignLeft);
            settings.addBoolean(BratDocumentViewerConfig.CFG_ALIGN_RIGHT, m_alignRight);
            settings.addBoolean(BratDocumentViewerConfig.CFG_ALIGN_CENTER, m_alignCenter);
//        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadFromNodeSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        m_settings.loadSettings(settings);
        m_useNumCols = settings.getBoolean(BratDocumentViewerConfig.CFG_USE_NUM_COLS);
        m_useColWidth = settings.getBoolean(BratDocumentViewerConfig.CFG_USE_COL_WIDTH);
        m_numCols = settings.getInt(BratDocumentViewerConfig.CFG_NUM_COLS);
        m_colWidth = settings.getInt(BratDocumentViewerConfig.CFG_COL_WIDTH);
        m_labelCol = settings.getString(BratDocumentViewerConfig.CFG_LABEL_COL);
        m_useRowID = settings.getBoolean(BratDocumentViewerConfig.CFG_USE_ROW_ID);
        m_alignLeft = settings.getBoolean(BratDocumentViewerConfig.CFG_ALIGN_LEFT);
        m_alignRight = settings.getBoolean(BratDocumentViewerConfig.CFG_ALIGN_RIGHT);
        m_alignCenter = settings.getBoolean(BratDocumentViewerConfig.CFG_ALIGN_CENTER);

//        setDocumentText(settings.getString(CFG_DOC_TEXT));
//        setDocumentTitle(settings.getString(CFG_DOC_TITLE));
//        setTermIds(Arrays.asList(settings.getStringArray(CFG_DOC_TERM_IDS)));
//        setTerms(Arrays.asList(settings.getStringArray(CFG_DOC_TERMS)));
//        setTags(Arrays.asList(settings.getStringArray(CFG_DOC_TAGS)));
//        setStartIndexes(Arrays.asList(settings.getStringArray(CFG_DOC_START_IDX)));
//        setStopIndexes(Arrays.asList(settings.getStringArray(CFG_DOC_STOP_IDX)));
//        setColors(Arrays.asList(settings.getStringArray(CFG_DOC_COLORS)));
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

        BratDocumentViewerRepresentation other = (BratDocumentViewerRepresentation)obj;
        return new EqualsBuilder()
                .append(m_settings, other.getSettings())
                .append(m_bratDocuments, other.getBratDocuments())
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
                .toHashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
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
        final JSONDataTable table = settings.getTable();
        m_settings = settings;
        m_settings.setTable(table);
    }

    /**
     *
     * @author Daniel Bogenrieder
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
            m_termIds = new ArrayList<String>();
            m_tags = new ArrayList<String>();
            m_terms = new ArrayList<String>();
            m_startIndexes = new ArrayList<String>();
            m_stopIndexes = new ArrayList<String>();
        }

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
         * @return
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
         * @return
         */
        public List<String> getTermIds() {
            return m_termIds;
        }

        /**
         * @param getTermIds
         */
        public void setTermIds(final List<String> getTermIds) {
            m_termIds = getTermIds;
        }

        /**
         * @return
         */
        public List<String> getTags() {
            return m_tags;
        }

        /**
         * @param tags
         */
        public void setTags(final List<String> tags) {
            m_tags = tags;
        }

        /**
         * @return
         */
        public List<String> getTerms() {
            return m_terms;
        }

        /**
         * @param terms
         */
        public void setTerms(final List<String> terms) {
            m_terms = terms;
        }

        /**
         * @return
         */
        public List<String> getStartIndexes() {
            return m_startIndexes;
        }

        /**
         * @param startIndexes
         */
        public void setStartIndexes(final List<String> startIndexes) {
            m_startIndexes = startIndexes;
        }

        /**
         * @return
         */
        public List<String> getStopIndexes() {
            return m_stopIndexes;
        }

        /**
         * @param stopIndexes
         */
        public void setStopIndexes(final List<String> stopIndexes) {
            m_stopIndexes = stopIndexes;
        }

        /**
         * @return
         */
        public List<String> getColors() {
            return m_colors;
        }

        /**
         * @param colors
         */
        public void setColors(final List<String> colors) {
            m_colors = colors;
        }

        /**
         * Generate random static color for each tag.
         */
        public void setRandColorToTags() {
            m_colors = new ArrayList<String>();
            // get unique tags
            List<String> tags = m_tags.stream().distinct().collect(Collectors.toList());
            // set random color
            for (String tag : tags) {
                Color randColor = new Color((int)(new Random(tag.hashCode()).nextDouble() * 0x1000000)).brighter();
                m_colors.add(String.format("#%02x%02x%02x", randColor.getRed(), randColor.getGreen(), randColor.getBlue()));
            }
        }
    }
}
