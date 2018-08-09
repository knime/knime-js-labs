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
 *   Aug 3, 2018 (awalter): created
 */
package org.knime.js.base.node.viz.heatmap;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.js.core.JSONDataTable;
import org.knime.js.core.JSONViewContent;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * The view representation of the Heatmap node.
 *
 * @author Alison Walter, KNIME GmbH, Konstanz, Germany
 */
@JsonAutoDetect
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class HeatMapViewRepresentation extends JSONViewContent {

    private boolean m_showWarningInView;
    private boolean m_generateImage;
    private int m_imageWidth;
    private int m_imageHeight;
    private boolean m_resizeToWindow;
    private boolean m_displayFullscreenButton;

    private boolean m_enableViewConfiguration;
    private boolean m_enableTitleChange;
    private boolean m_enableColorModeEdit;

    final static String CFG_INCLUDED_COLUMNS = "includedColumns";
    private String[] m_columns;
    private String m_labelColumn;
    private String m_svgLabelColumn;

    private boolean m_subscribeFilter;

    private boolean m_enableSelection;
    private boolean m_publishSelection;
    private boolean m_subscribeSelection;
    private String m_selectionColumnName;

    private boolean m_enablePaging;
    private boolean m_enablePageSizeChange;
    private int[] m_allowedPageSizes;
    private boolean m_pageSizeShowAll;

    private boolean m_displayDataCellToolTip;
    private boolean m_displayRowToolTip;

    private boolean m_enableZoom;
    private boolean m_enablePanning;
    private boolean m_showZoomResetButton;

    private final static String CFG_DATA_TABLE_ID = "dataTableId";
    private String m_dataTableId;
    private JSONDataTable m_table;

    // -- General getters & setters --

    /**
     * @return the showWarningInView
     */
    public boolean getShowWarningInView() {
        return m_showWarningInView;
    }

    /**
     * @param showWarningInView the showWarningInView to set
     */
    public void setShowWarningInView(final boolean showWarningInView) {
        m_showWarningInView = showWarningInView;
    }

    /**
     * @return the generateImage
     */
    public boolean getGenerateImage() {
        return m_generateImage;
    }

    /**
     * @param generateImage the generateImage to set
     */
    public void setGenerateImage(final boolean generateImage) {
        m_generateImage = generateImage;
    }

    /**
     * @return the imageWidth
     */
    public int getImageWidth() {
        return m_imageWidth;
    }

    /**
     * @param imageWidth the imageWidth to set
     */
    public void setImageWidth(final int imageWidth) {
        m_imageWidth = imageWidth;
    }

    /**
     * @return the imageHeight
     */
    public int getImageHeight() {
        return m_imageHeight;
    }

    /**
     * @param imageHeight the imageHeight to set
     */
    public void setImageHeight(final int imageHeight) {
        m_imageHeight = imageHeight;
    }

    /**
     * @return the resizeToWindow
     */
    public boolean getResizeToWindow() {
        return m_resizeToWindow;
    }

    /**
     * @param resizeToWindow the resizeToWindow to set
     */
    public void setResizeToWindow(final boolean resizeToWindow) {
        m_resizeToWindow = resizeToWindow;
    }

    /**
     * @return the displayFullscreenButton
     */
    public boolean getDisplayFullscreenButton() {
        return m_displayFullscreenButton;
    }

    /**
     * @param displayFullscreenButton the displayFullscreenButton to set
     */
    public void setDisplayFullscreenButton(final boolean displayFullscreenButton) {
        m_displayFullscreenButton = displayFullscreenButton;
    }

    // -- View edit controls getters & setters --

    /**
     * @return the enableViewConfiguration
     */
    public boolean getEnableViewConfiguration() {
        return m_enableViewConfiguration;
    }

    /**
     * @param enableViewConfiguration the enableViewConfiguration to set
     */
    public void setEnableViewConfiguration(final boolean enableViewConfiguration) {
        m_enableViewConfiguration = enableViewConfiguration;
    }

    /**
     * @return the enableTitleChange
     */
    public boolean getEnableTitleChange() {
        return m_enableTitleChange;
    }

    /**
     * @param enableTitleChange the enableTitleChange to set
     */
    public void setEnableTitleChange(final boolean enableTitleChange) {
        m_enableTitleChange = enableTitleChange;
    }

    /**
     * @return the enableColorModeEdit
     */
    public boolean getEnableColorModeEdit() {
        return m_enableColorModeEdit;
    }

    /**
     * @param enableColorModeEdit the enableColorModeEdit to set
     */
    public void setEnableColorModeEdit(final boolean enableColorModeEdit) {
        m_enableColorModeEdit = enableColorModeEdit;
    }

    // -- Columns getters & setters --

    /**
     * @return the columns
     */
    public String[] getColumns() {
        return m_columns;
    }

    /**
     * @param columns the columns to set
     */
    public void setColumns(final String[] columns) {
        m_columns = columns;
    }

    /**
     * @return the labelColumn
     */
    public String getLabelColumn() {
        return m_labelColumn;
    }

    /**
     * @param labelColumn the labelColumn to set
     */
    public void setLabelColumn(final String labelColumn) {
        m_labelColumn = labelColumn;
    }

    /**
     * @return the svgLabelColumn
     */
    public String getSvgLabelColumn() {
        return m_svgLabelColumn;
    }

    /**
     * @param svgLabelColumn the svgLabelColumn to set
     */
    public void setSvgLabelColumn(final String svgLabelColumn) {
        m_svgLabelColumn = svgLabelColumn;
    }

    // -- Filter getters & setters --

    /**
     * @return the subscribeFilter
     */
    public boolean getSubscribeFilter() {
        return m_subscribeFilter;
    }

    /**
     * @param subscribeFilter the subscribeFilter to set
     */
    public void setSubscribeFilter(final boolean subscribeFilter) {
        m_subscribeFilter = subscribeFilter;
    }

    // -- Selection getters & setters --

    /**
     * @return the enableSelection
     */
    public boolean getEnableSelection() {
        return m_enableSelection;
    }

    /**
     * @param enableSelection the enableSelection to set
     */
    public void setEnableSelection(final boolean enableSelection) {
        m_enableSelection = enableSelection;
    }

    /**
     * @return the publishSelection
     */
    public boolean getPublishSelection() {
        return m_publishSelection;
    }

    /**
     * @param publishSelection the publishSelection to set
     */
    public void setPublishSelection(final boolean publishSelection) {
        m_publishSelection = publishSelection;
    }

    /**
     * @return the subscribeSelection
     */
    public boolean getSubscribeSelection() {
        return m_subscribeSelection;
    }

    /**
     * @param subscribeSelection the subscribeSelection to set
     */
    public void setSubscribeSelection(final boolean subscribeSelection) {
        m_subscribeSelection = subscribeSelection;
    }

    /**
     * @return the selectionColumnName
     */
    public String getSelectionColumnName() {
        return m_selectionColumnName;
    }

    /**
     * @param selectionColumnName the selectionColumnName to set
     */
    public void setSelectionColumnName(final String selectionColumnName) {
        m_selectionColumnName = selectionColumnName;
    }

    // -- Paging getters & setters --

    /**
     * @return the enablePaging
     */
    public boolean getEnablePaging() {
        return m_enablePaging;
    }

    /**
     * @param enablePaging the enablePaging to set
     */
    public void setEnablePaging(final boolean enablePaging) {
        m_enablePaging = enablePaging;
    }

    /**
     * @return the enablePageSizeChange
     */
    public boolean getEnablePageSizeChange() {
        return m_enablePageSizeChange;
    }

    /**
     * @param enablePageSizeChange the enablePageSizeChange to set
     */
    public void setEnablePageSizeChange(final boolean enablePageSizeChange) {
        m_enablePageSizeChange = enablePageSizeChange;
    }

    /**
     * @return the allowedPageSizes
     */
    public int[] getAllowedPageSizes() {
        return m_allowedPageSizes;
    }

    /**
     * @param allowedPageSizes the allowedPageSizes to set
     */
    public void setAllowedPageSizes(final int[] allowedPageSizes) {
        m_allowedPageSizes = allowedPageSizes;
    }

    /**
     * @return the enableShowAll
     */
    public boolean getEnableShowAll() {
        return m_pageSizeShowAll;
    }

    /**
     * @param enableShowAll the enableShowAll to set
     */
    public void setEnableShowAll(final boolean enableShowAll) {
        m_pageSizeShowAll = enableShowAll;
    }

    // -- Tool tip getters & setters --

    /**
     * @return the displayDataCellToolTip
     */
    public boolean getDisplayDataCellToolTip() {
        return m_displayDataCellToolTip;
    }

    /**
     * @param displayDataCellToolTip the displayDataCellToolTip to set
     */
    public void setDisplayDataCellToolTip(final boolean displayDataCellToolTip) {
        m_displayDataCellToolTip = displayDataCellToolTip;
    }

    /**
     * @return the displayRowToolTip
     */
    public boolean getDisplayRowToolTip() {
        return m_displayRowToolTip;
    }

    /**
     * @param displayRowToolTip the displayRowToolTip to set
     */
    public void setDisplayRowToolTip(final boolean displayRowToolTip) {
        m_displayRowToolTip = displayRowToolTip;
    }

    // -- Zoom & Panning getters & setters --

    /**
     * @return the enableZoom
     */
    public boolean getEnableZoom() {
        return m_enableZoom;
    }

    /**
     * @param enableZoom the enableZoom to set
     */
    public void setEnableZoom(final boolean enableZoom) {
        m_enableZoom = enableZoom;
    }

    /**
     * @return the enablePanning
     */
    public boolean getEnablePanning() {
        return m_enablePanning;
    }

    /**
     * @param enablePanning the enablePanning to set
     */
    public void setEnablePanning(final boolean enablePanning) {
        m_enablePanning = enablePanning;
    }

    /**
     * @return the showZoomResetButton
     */
    public boolean getShowZoomResetButton() {
        return m_showZoomResetButton;
    }

    /**
     * @param showZoomResetButton the showZoomResetButton to set
     */
    public void setShowZoomResetButton(final boolean showZoomResetButton) {
        m_showZoomResetButton = showZoomResetButton;
    }

    // -- Data table getters & setters --

    /**
     * @return the dataTableId
     */
    public String getDataTableId() {
        return m_dataTableId;
    }

    /**
     * @param dataTableId the dataTableId to set
     */
    public void setDataTableId(final String dataTableId) {
        m_dataTableId = dataTableId;
    }

    /**
     * @return The JSON data table.
     */
    @JsonProperty("table")
    public JSONDataTable getTable() {
        return m_table;
    }

    /**
     * @param table The table to set.
     */
    @JsonProperty("table")
    public void setTable(final JSONDataTable table) {
        m_table = table;
    }

    // -- Save & Load Settings --

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveToNodeSettings(final NodeSettingsWO settings) {
        settings.addBoolean(HeatMapViewConfig.CFG_SHOW_WARNING_IN_VIEW, m_showWarningInView);
        settings.addBoolean(HeatMapViewConfig.CFG_GENERATE_IMAGE, m_generateImage);
        settings.addInt(HeatMapViewConfig.CFG_IMAGE_WIDTH, m_imageWidth);
        settings.addInt(HeatMapViewConfig.CFG_IMAGE_HEIGHT, m_imageHeight);
        settings.addBoolean(HeatMapViewConfig.CFG_RESIZE_TO_WINDOW, m_resizeToWindow);
        settings.addBoolean(HeatMapViewConfig.CFG_DISPLAY_FULLSCREEN_BUTTON, m_displayFullscreenButton);

        settings.addBoolean(HeatMapViewConfig.CFG_ENABLE_CONFIG, m_enableViewConfiguration);
        settings.addBoolean(HeatMapViewConfig.CFG_ENABLE_TTILE_CHANGE, m_enableTitleChange);
        settings.addBoolean(HeatMapViewConfig.CFG_ENABLE_COLOR_MODE_EDIT, m_enableColorModeEdit);

        settings.addStringArray(CFG_INCLUDED_COLUMNS, m_columns);
        settings.addString(HeatMapViewConfig.CFG_LABEL_COLUMN, m_labelColumn);
        settings.addString(HeatMapViewConfig.CFG_SVG_LABEL_COLUMN, m_svgLabelColumn);

        settings.addBoolean(HeatMapViewConfig.CFG_SUBSCRIBE_FILTER, m_subscribeFilter);

        settings.addBoolean(HeatMapViewConfig.CFG_ENABLE_SELECTION, m_enableSelection);
        settings.addBoolean(HeatMapViewConfig.CFG_PUBLISH_SELECTION, m_publishSelection);
        settings.addBoolean(HeatMapViewConfig.CFG_SUBSCRIBE_SELECTION, m_subscribeSelection);
        settings.addString(HeatMapViewConfig.CFG_SELECTION_COLUMN_NAME, m_selectionColumnName);

        settings.addBoolean(HeatMapViewConfig.CFG_ENABLE_PAGING, m_enablePaging);
        settings.addBoolean(HeatMapViewConfig.CFG_ENABLE_PAGE_SIZE_CHANGE, m_enablePageSizeChange);
        settings.addIntArray(HeatMapViewConfig.CFG_PAGE_SIZES, m_allowedPageSizes);
        settings.addBoolean(HeatMapViewConfig.CFG_PAGE_SIZE_SHOW_ALL, m_pageSizeShowAll);

        settings.addBoolean(HeatMapViewConfig.CFG_DISPLAY_DATA_CELL_TOOL_TIP, m_displayDataCellToolTip);
        settings.addBoolean(HeatMapViewConfig.CFG_DISPLAY_ROW_TOOL_TIP, m_displayRowToolTip);

        settings.addBoolean(HeatMapViewConfig.CFG_ENABLE_ZOOM, m_enableZoom);
        settings.addBoolean(HeatMapViewConfig.CFG_ENABLE_PANNING, m_enablePanning);
        settings.addBoolean(HeatMapViewConfig.CFG_SHOW_ZOOM_RESET_BUTTON, m_showZoomResetButton);

        settings.addString(CFG_DATA_TABLE_ID, m_dataTableId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadFromNodeSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        m_showWarningInView = settings.getBoolean(HeatMapViewConfig.CFG_SHOW_WARNING_IN_VIEW);
        m_generateImage = settings.getBoolean(HeatMapViewConfig.CFG_GENERATE_IMAGE);
        m_imageWidth = settings.getInt(HeatMapViewConfig.CFG_IMAGE_WIDTH);
        m_imageHeight = settings.getInt(HeatMapViewConfig.CFG_IMAGE_HEIGHT);
        m_resizeToWindow = settings.getBoolean(HeatMapViewConfig.CFG_RESIZE_TO_WINDOW);
        m_displayFullscreenButton = settings.getBoolean(HeatMapViewConfig.CFG_DISPLAY_FULLSCREEN_BUTTON);

        m_enableViewConfiguration = settings.getBoolean(HeatMapViewConfig.CFG_ENABLE_CONFIG);
        m_enableTitleChange = settings.getBoolean(HeatMapViewConfig.CFG_ENABLE_TTILE_CHANGE);
        m_enableColorModeEdit = settings.getBoolean(HeatMapViewConfig.CFG_ENABLE_COLOR_MODE_EDIT);

        m_columns = settings.getStringArray(CFG_INCLUDED_COLUMNS);
        m_labelColumn = settings.getString(HeatMapViewConfig.CFG_LABEL_COLUMN);
        m_svgLabelColumn = settings.getString(HeatMapViewConfig.CFG_SVG_LABEL_COLUMN);

        m_subscribeFilter = settings.getBoolean(HeatMapViewConfig.CFG_SUBSCRIBE_FILTER);

        m_enableSelection = settings.getBoolean(HeatMapViewConfig.CFG_ENABLE_SELECTION);
        m_publishSelection = settings.getBoolean(HeatMapViewConfig.CFG_PUBLISH_SELECTION);
        m_subscribeSelection = settings.getBoolean(HeatMapViewConfig.CFG_SUBSCRIBE_SELECTION);
        m_selectionColumnName = settings.getString(HeatMapViewConfig.CFG_SELECTION_COLUMN_NAME);

        m_enablePaging = settings.getBoolean(HeatMapViewConfig.CFG_ENABLE_PAGING);
        m_enablePageSizeChange = settings.getBoolean(HeatMapViewConfig.CFG_ENABLE_PAGE_SIZE_CHANGE);
        m_allowedPageSizes = settings.getIntArray(HeatMapViewConfig.CFG_PAGE_SIZES);
        m_pageSizeShowAll = settings.getBoolean(HeatMapViewConfig.CFG_PAGE_SIZE_SHOW_ALL);

        m_displayDataCellToolTip = settings.getBoolean(HeatMapViewConfig.CFG_DISPLAY_DATA_CELL_TOOL_TIP);
        m_displayRowToolTip = settings.getBoolean(HeatMapViewConfig.CFG_DISPLAY_ROW_TOOL_TIP);

        m_enableZoom = settings.getBoolean(HeatMapViewConfig.CFG_ENABLE_ZOOM);
        m_enablePanning = settings.getBoolean(HeatMapViewConfig.CFG_ENABLE_PANNING);
        m_showZoomResetButton = settings.getBoolean(HeatMapViewConfig.CFG_SHOW_ZOOM_RESET_BUTTON);

        m_dataTableId = settings.getString(CFG_DATA_TABLE_ID);
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
        final HeatMapViewRepresentation other = (HeatMapViewRepresentation) obj;
        return new EqualsBuilder()
                .append(m_showWarningInView, other.getShowWarningInView())
                .append(m_generateImage, other.getGenerateImage())
                .append(m_imageWidth, other.getImageWidth())
                .append(m_imageHeight, other.getImageHeight())
                .append(m_resizeToWindow, other.getResizeToWindow())
                .append(m_displayFullscreenButton, other.getDisplayFullscreenButton())
                .append(m_enableViewConfiguration, other.getEnableViewConfiguration())
                .append(m_enableTitleChange, other.getEnableTitleChange())
                .append(m_enableColorModeEdit, other.getEnableColorModeEdit())
                .append(m_columns, other.getColumns())
                .append(m_labelColumn, other.getLabelColumn())
                .append(m_svgLabelColumn, other.getSvgLabelColumn())
                .append(m_subscribeFilter, other.getSubscribeFilter())
                .append(m_enableSelection, other.getEnableSelection())
                .append(m_publishSelection, other.getPublishSelection())
                .append(m_subscribeSelection, other.getSubscribeSelection())
                .append(m_selectionColumnName, other.getSelectionColumnName())
                .append(m_enablePaging, other.getEnablePaging())
                .append(m_enablePageSizeChange, other.getEnablePageSizeChange())
                .append(m_allowedPageSizes, other.getAllowedPageSizes())
                .append(m_pageSizeShowAll, other.getEnableShowAll())
                .append(m_displayDataCellToolTip, other.getDisplayDataCellToolTip())
                .append(m_displayRowToolTip, other.getDisplayRowToolTip())
                .append(m_enableZoom, other.getEnableZoom())
                .append(m_enablePanning, other.getEnablePanning())
                .append(m_showZoomResetButton, other.getShowZoomResetButton())
                .append(m_dataTableId, other.getDataTableId())
                .append(m_table, other.getTable())
                .isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(m_showWarningInView)
                .append(m_generateImage)
                .append(m_imageWidth)
                .append(m_imageHeight)
                .append(m_resizeToWindow)
                .append(m_displayFullscreenButton)
                .append(m_enableViewConfiguration)
                .append(m_enableTitleChange)
                .append(m_enableColorModeEdit)
                .append(m_columns)
                .append(m_labelColumn)
                .append(m_svgLabelColumn)
                .append(m_subscribeFilter)
                .append(m_enableSelection)
                .append(m_publishSelection)
                .append(m_subscribeSelection)
                .append(m_selectionColumnName)
                .append(m_enablePaging)
                .append(m_enablePageSizeChange)
                .append(m_allowedPageSizes)
                .append(m_pageSizeShowAll)
                .append(m_displayDataCellToolTip)
                .append(m_displayRowToolTip)
                .append(m_enableZoom)
                .append(m_enablePanning)
                .append(m_showZoomResetButton)
                .append(m_dataTableId)
                .append(m_table)
                .toHashCode();
    }
}
