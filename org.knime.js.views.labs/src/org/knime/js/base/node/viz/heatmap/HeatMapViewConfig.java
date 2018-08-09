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

import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DoubleValue;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.util.filter.InputFilter;
import org.knime.core.node.util.filter.column.DataColumnSpecFilterConfiguration;

/**
 * Configuration for the JavaScript Heatmap node.
 *
 * @author Alison Walter, KNIME GmbH, Konstanz, Germany
 */
public final class HeatMapViewConfig {

    // General
    final static String CFG_CUSTOM_CSS = "customCSS";
    private final static String DEFAULT_CUSTOM_CSS = "";
    private String m_customCSS = DEFAULT_CUSTOM_CSS;

    final static String CFG_HIDE_IN_WIZARD = "hideInWizard";
    final static boolean DEFAULT_HIDE_IN_WIZARD = false;
    private boolean m_hideInWizard = DEFAULT_HIDE_IN_WIZARD;

    final static String CFG_SHOW_WARNING_IN_VIEW = "showWarningInView";
    final static boolean DEFAULT_SHOW_WARNING_IN_VIEW = true;
    private boolean m_showWarningInView = DEFAULT_SHOW_WARNING_IN_VIEW;

    final static String CFG_GENERATE_IMAGE = "generateImage";
    final static boolean DEFAULT_GENERATE_IMAGE = true;
    private boolean m_generateImage = DEFAULT_GENERATE_IMAGE;

    final static String CFG_IMAGE_WIDTH = "imageWidth";
    final static int DEFAULT_WIDTH = 800;
    private int m_imageWidth = DEFAULT_WIDTH;

    final static String CFG_IMAGE_HEIGHT = "imageHeight";
    final static int DEFAULT_HEIGHT = 600;
    private int m_imageHeight = DEFAULT_HEIGHT;

    final static String CFG_RESIZE_TO_WINDOW = "resizeToWindow";
    final static boolean DEFAULT_RESIZE_TO_WINDOW = true;
    private boolean m_resizeToWindow = DEFAULT_RESIZE_TO_WINDOW;

    final static String CFG_DISPLAY_FULLSCREEN_BUTTON = "displayFullscreenButton";
    final static boolean DEFAULT_DISPLAY_FULLSCREEN_BUTTON = true;
    private boolean m_displayFullscreenButton = DEFAULT_DISPLAY_FULLSCREEN_BUTTON;

    final static String CFG_CHART_TITLE = "chartTitle";
    final static String DEFAULT_CHART_TITLE = "";
    private String m_chartTitle = DEFAULT_CHART_TITLE;

    final static String CFG_CHART_SUBTITLE = "chartSubtitle";
    final static String DEFAULT_CHART_SUBTITLE = "";
    private String m_chartSubtitle = DEFAULT_CHART_SUBTITLE;

    // View edit controls
    final static String CFG_ENABLE_CONFIG = "enableViewConfiguration";
    final static boolean DEFAULT_ENABLE_CONFIG = true;
    private boolean m_enableViewConfiguration = DEFAULT_ENABLE_CONFIG;

    final static String CFG_ENABLE_TTILE_CHANGE = "enableTitleChange";
    final static boolean DEFAULT_ENABLE_TTILE_CHANGE = true;
    private boolean m_enableTitleChange = DEFAULT_ENABLE_TTILE_CHANGE;

    final static String CFG_ENABLE_COLOR_MODE_EDIT = "enableColorModeEdit";
    final static boolean DEFAULT_ENABLE_COLOR_MODE_EDIT = true;
    private boolean m_enableColorModeEdit = DEFAULT_ENABLE_COLOR_MODE_EDIT;

    // Gradient
    final static String CFG_THREE_COLOR_GRADIENT = "threeColorGradient";
    final static String[] DEFAULT_THREE_COLOR_GRADIENT = new String[] {"#5e3c99", "#f7f7f7", "#e66101"};
    private String[] m_threeColorGradient = DEFAULT_THREE_COLOR_GRADIENT;

    final static String CFG_DISCRETE_GRADIENT_COLORS = "discreteGradientColors";
    private String[] m_discreteGradientColors = DEFAULT_THREE_COLOR_GRADIENT;

    final static String CFG_CONTINUOUS_GRADIENT = "continuousGradient";
    final static boolean DEFAULT_CONTINUOUS_GRADIENT = true;
    private boolean m_continuousGradient = DEFAULT_CONTINUOUS_GRADIENT;

    final static String CFG_NUM_DISCRETE_COLORS = "numDiscreteColors";
    final static int DEFAULT_NUM_DISCRETE_COLORS = 3;
    private int m_numDiscreteColors = DEFAULT_NUM_DISCRETE_COLORS;

    final static String CFG_MISSING_VALUE_COLOR = "missingValueColor";
    final static String DEFAULT_MISSING_VALUE_COLOR = "#000000";
    private String m_missingValueColor = DEFAULT_MISSING_VALUE_COLOR;

    // Columns
    final static String CFG_COLUMNS = "columns";
    final static DataColumnSpecFilterConfiguration DEFAULT_COLUMNS =
        new DataColumnSpecFilterConfiguration(CFG_COLUMNS, new InputFilter<DataColumnSpec>() {
            @Override
            public boolean include(final DataColumnSpec name) {
                if (name == null) {
                    return false;
                }
                return name.getType().isCompatible(DoubleValue.class);
            }
        });
    private DataColumnSpecFilterConfiguration m_columns = DEFAULT_COLUMNS;

    final static String CFG_LABEL_COLUMN = "labelColumn";
    private String m_labelColumn;

    final static String CFG_SVG_LABEL_COLUMN = "svgLabelColumn";
    private String m_svgLabelColumn;

    // Filter
    final static String CFG_SUBSCRIBE_FILTER = "subscribeFilter";
    final static boolean DEFAULT_SUBSCRIBE_FILTER = true;
    private boolean m_subscribeFilter = DEFAULT_SUBSCRIBE_FILTER;

    // Selection
    final static String CFG_ENABLE_SELECTION = "enableSelection";
    final static boolean DEFAULT_ENABLE_SELECTION = true;
    private boolean m_enableSelection = DEFAULT_ENABLE_SELECTION;

    final static String CFG_PUBLISH_SELECTION = "publishSelection";
    final static boolean DEFAULT_PUBLISH_SELECTION = true;
    private boolean m_publishSelection = DEFAULT_PUBLISH_SELECTION;

    final static String CFG_SUBSCRIBE_SELECTION = "subscribeSelection";
    final static boolean DEFAULT_SUBSCRIBE_SELECTION = true;
    private boolean m_subscribeSelection = DEFAULT_SUBSCRIBE_SELECTION;

    final static String CFG_SELECTION_COLUMN_NAME = "selectionColumnName";
    final static String DEFAULT_SELECTION_COLUMN_NAME = "Selected (Heatmap)";
    private String m_selectionColumnName = DEFAULT_SELECTION_COLUMN_NAME;

    final static String CFG_SELECTION = "selection";
    final static String[] DEFAULT_SELECTION = new String[0];
    private String[] m_selection = DEFAULT_SELECTION;

    // Paging
    final static String CFG_ENABLE_PAGING = "enablePaging";
    final static boolean DEFAULT_ENABLE_PAGING = true;
    private boolean m_enablePaging = DEFAULT_ENABLE_PAGING;

    final static String CFG_INITIAL_PAGE_SIZE = "initialPageSize";
    final static int DEFAULT_INITIAL_PAGE_SIZE = 100;
    private int m_initialPageSize = DEFAULT_INITIAL_PAGE_SIZE;

    final static String CFG_ENABLE_PAGE_SIZE_CHANGE = "enablePageSizeChange";
    final static boolean DEFAULT_ENABLE_PAGE_SIZE_CHANGE = true;
    private boolean m_enablePageSizeChange = DEFAULT_ENABLE_PAGE_SIZE_CHANGE;

    final static String CFG_PAGE_SIZES = "allowedPageSizes";
    final static int[] DEFAULT_PAGE_SIZES = new int[]{100, 250, 500, 1000};
    private int[] m_allowedPageSizes = DEFAULT_PAGE_SIZES;

    final static String CFG_PAGE_SIZE_SHOW_ALL = "enableShowAll";
    final static boolean DEFAULT_PAGE_SIZE_SHOW_ALL = false;
    private boolean m_pageSizeShowAll = DEFAULT_PAGE_SIZE_SHOW_ALL;

    // Tool tip
    final static String CFG_DISPLAY_DATA_CELL_TOOL_TIP = "displayDataCellToolTip";
    final static boolean DEFAULT_DISPLAY_DATA_CELL_TOOL_TIP = true;
    private boolean m_displayDataCellToolTip = DEFAULT_DISPLAY_DATA_CELL_TOOL_TIP;

    final static String CFG_DISPLAY_ROW_TOOL_TIP = "displayRowToolTip";
    final static boolean DEFAULT_DISPLAY_ROW_TOOL_TIP = true;
    private boolean m_displayRowToolTip = DEFAULT_DISPLAY_ROW_TOOL_TIP;

    // Zooming & Panning
    final static String CFG_ENABLE_ZOOM = "enableZoom";
    final static boolean DEFAULT_ENABLE_ZOOM = true;
    private boolean m_enableZoom = DEFAULT_ENABLE_ZOOM;

    final static String CFG_ENABLE_PANNING = "enablePanning";
    final static boolean DEFAULT_ENABLE_PANNING = true;
    private boolean m_enablePanning = DEFAULT_ENABLE_PANNING;

    final static String CFG_SHOW_ZOOM_RESET_BUTTON = "showZoomResetButton";
    final static boolean DEFAULT_SHOW_ZOOM_RESET_BUTTON = false;
    private boolean m_showZoomResetButton = DEFAULT_SHOW_ZOOM_RESET_BUTTON;

    final static String CFG_X_MIN = "xMin";
    final static String DEFAULT_X_MIN = null;
    private String m_xMin = DEFAULT_X_MIN;

    final static String CFG_X_MAX = "xMax";
    final static String DEFAULT_X_MAX = null;
    private String m_xMax = DEFAULT_X_MAX;

    final static String CFG_Y_MIN = "yMin";
    final static String DEFAULT_Y_MIN = null;
    private String m_yMin = DEFAULT_Y_MIN;

    final static String CFG_Y_MAX = "yMax";
    final static String DEFAULT_Y_MAX = null;
    private String m_yMax = DEFAULT_Y_MAX;

    // -- General getters & setters --

    /**
     * @return the customCSS
     */
    public String getCustomCSS() {
        return m_customCSS;
    }

    /**
     * @param customCSS the customCSS to set
     */
    public void setCustomCSS(final String customCSS) {
        m_customCSS = customCSS;
    }

    /**
     * @return the hideInWizard
     */
    public boolean getHideInWizard() {
        return m_hideInWizard;
    }

    /**
     * @param hideInWizard the hideInWizard to set
     */
    public void setHideInWizard(final boolean hideInWizard) {
        m_hideInWizard = hideInWizard;
    }

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

    /**
     * @return the chartTitle
     */
    public String getChartTitle() {
        return m_chartTitle;
    }

    /**
     * @param chartTitle the chartTitle to set
     */
    public void setChartTitle(final String chartTitle) {
        m_chartTitle = chartTitle;
    }

    /**
     * @return the chartSubtitle
     */
    public String getChartSubtitle() {
        return m_chartSubtitle;
    }

    /**
     * @param chartSubtitle the chartSubtitle to set
     */
    public void setChartSubtitle(final String chartSubtitle) {
        m_chartSubtitle = chartSubtitle;
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

    // -- Gradient getters & setters --

    /**
     * @return the threeColorGradient
     */
    public String[] getThreeColorGradient() {
        return m_threeColorGradient;
    }

    /**
     * @param threeColorGradient the threeColorGradient to set
     */
    public void setThreeColorGradient(final String[] threeColorGradient) {
        m_threeColorGradient = threeColorGradient;
    }

    /**
     * @return the discreteGradientColors
     */
    public String[] getDiscreteGradientColors() {
        return m_discreteGradientColors;
    }

    /**
     * @param discreteGradientColors the discreteGradientColors to set
     */
    public void setDiscreteGradientColors(final String[] discreteGradientColors) {
        m_discreteGradientColors = discreteGradientColors;
    }

    /**
     * @return the continuousGradient
     */
    public boolean getContinuousGradient() {
        return m_continuousGradient;
    }

    /**
     * @param continuousGradient the continuousGradient to set
     */
    public void setContinuousGradient(final boolean continuousGradient) {
        m_continuousGradient = continuousGradient;
    }

    /**
     * @return the numDiscreteColors
     */
    public int getNumDiscreteColors() {
        return m_numDiscreteColors;
    }

    /**
     * @param numDiscreteColors the numDiscreteColors to set
     */
    public void setNumDiscreteColors(final int numDiscreteColors) {
        m_numDiscreteColors = numDiscreteColors;
    }

    /**
     * @return the missingValueColor
     */
    public String getMissingValueColor() {
        return m_missingValueColor;
    }

    /**
     * @param missingValueColor the missingValueColor to set
     */
    public void setMissingValueColor(final String missingValueColor) {
        m_missingValueColor = missingValueColor;
    }

    // -- Columns getters & setters --

    /**
     * @return the columns
     */
    public DataColumnSpecFilterConfiguration getColumns() {
        return m_columns;
    }

    /**
     * @param columns the columns to set
     */
    public void setColumns(final DataColumnSpecFilterConfiguration columns) {
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

    /**
     * @return the selection
     */
    public String[] getSelection() {
        return m_selection;
    }

    /**
     * @param selection the selection to set
     */
    public void setSelection(final String[] selection) {
        m_selection = selection;
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
     * @return the initialPageSize
     */
    public int getInitialPageSize() {
        return m_initialPageSize;
    }

    /**
     * @param initialPageSize the initialPageSize to set
     */
    public void setInitialPageSize(final int initialPageSize) {
        m_initialPageSize = initialPageSize;
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

    // -- Zoom & Panning getters & setters

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

    /**
     * @return the xMin
     */
    public String getXMin() {
        return m_xMin;
    }

    /**
     * @param xMin the xMin to set
     */
    public void setXMin(final String xMin) {
        m_xMin = xMin;
    }

    /**
     * @return the xMax
     */
    public String getXMax() {
        return m_xMax;
    }

    /**
     * @param xMax the xMax to set
     */
    public void setXMax(final String xMax) {
        m_xMax = xMax;
    }

    /**
     * @return the yMin
     */
    public String getYMin() {
        return m_yMin;
    }

    /**
     * @param yMin the yMin to set
     */
    public void setYMin(final String yMin) {
        m_yMin = yMin;
    }

    /**
     * @return the yMax
     */
    public String getYMax() {
        return m_yMax;
    }

    /**
     * @param yMax the yMax to set
     */
    public void setYMax(final String yMax) {
        m_yMax = yMax;
    }

    // -- Save & Load Settings --

    /** Saves current parameters to settings object.
     * @param settings To save to.
     */
    public void saveSettings(final NodeSettingsWO settings) {
        settings.addString(CFG_CUSTOM_CSS, m_customCSS);
        settings.addBoolean(CFG_HIDE_IN_WIZARD, m_hideInWizard);
        settings.addBoolean(CFG_SHOW_WARNING_IN_VIEW, m_showWarningInView);
        settings.addBoolean(CFG_GENERATE_IMAGE, m_generateImage);
        settings.addInt(CFG_IMAGE_WIDTH, m_imageWidth);
        settings.addInt(CFG_IMAGE_HEIGHT, m_imageHeight);
        settings.addBoolean(CFG_RESIZE_TO_WINDOW, m_resizeToWindow);
        settings.addBoolean(CFG_DISPLAY_FULLSCREEN_BUTTON, m_displayFullscreenButton);
        settings.addString(CFG_CHART_TITLE, m_chartTitle);
        settings.addString(CFG_CHART_SUBTITLE, m_chartSubtitle);

        settings.addBoolean(CFG_ENABLE_CONFIG, m_enableViewConfiguration);
        settings.addBoolean(CFG_ENABLE_TTILE_CHANGE, m_enableTitleChange);
        settings.addBoolean(CFG_ENABLE_COLOR_MODE_EDIT, m_enableColorModeEdit);

        settings.addStringArray(CFG_THREE_COLOR_GRADIENT, m_threeColorGradient);
        settings.addStringArray(CFG_DISCRETE_GRADIENT_COLORS, m_discreteGradientColors);
        settings.addBoolean(CFG_CONTINUOUS_GRADIENT, m_continuousGradient);
        settings.addInt(CFG_NUM_DISCRETE_COLORS, m_numDiscreteColors);
        settings.addString(CFG_MISSING_VALUE_COLOR, m_missingValueColor);

        m_columns.saveConfiguration(settings);
        settings.addString(CFG_LABEL_COLUMN, m_labelColumn);
        settings.addString(CFG_SVG_LABEL_COLUMN, m_svgLabelColumn);

        settings.addBoolean(CFG_SUBSCRIBE_FILTER, m_subscribeFilter);

        settings.addBoolean(CFG_ENABLE_SELECTION, m_enableSelection);
        settings.addBoolean(CFG_PUBLISH_SELECTION, m_publishSelection);
        settings.addBoolean(CFG_SUBSCRIBE_SELECTION, m_subscribeSelection);
        settings.addString(CFG_SELECTION_COLUMN_NAME, m_selectionColumnName);
        settings.addStringArray(CFG_SELECTION, m_selection);

        settings.addBoolean(CFG_ENABLE_PAGING, m_enablePaging);
        settings.addInt(CFG_INITIAL_PAGE_SIZE, m_initialPageSize);
        settings.addBoolean(CFG_ENABLE_PAGE_SIZE_CHANGE, m_enablePageSizeChange);
        settings.addIntArray(CFG_PAGE_SIZES, m_allowedPageSizes);
        settings.addBoolean(CFG_PAGE_SIZE_SHOW_ALL, m_pageSizeShowAll);

        settings.addBoolean(CFG_DISPLAY_DATA_CELL_TOOL_TIP, m_displayDataCellToolTip);
        settings.addBoolean(CFG_DISPLAY_ROW_TOOL_TIP, m_displayRowToolTip);

        settings.addBoolean(CFG_ENABLE_ZOOM, m_enableZoom);
        settings.addBoolean(CFG_ENABLE_PANNING, m_enablePanning);
        settings.addBoolean(CFG_SHOW_ZOOM_RESET_BUTTON, m_showZoomResetButton);
        settings.addString(CFG_X_MIN, m_xMin);
        settings.addString(CFG_X_MAX, m_xMax);
        settings.addString(CFG_Y_MIN, m_yMin);
        settings.addString(CFG_Y_MAX, m_yMax);
    }

    /** Loads parameters in NodeModel.
     * @param settings To load from.
     * @throws InvalidSettingsException If incomplete or wrong.
     */
    public void loadSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        m_customCSS = settings.getString(CFG_CUSTOM_CSS);
        m_hideInWizard = settings.getBoolean(CFG_HIDE_IN_WIZARD);
        m_showWarningInView = settings.getBoolean(CFG_SHOW_WARNING_IN_VIEW);
        m_generateImage = settings.getBoolean(CFG_GENERATE_IMAGE);
        m_imageWidth = settings.getInt(CFG_IMAGE_WIDTH);
        m_imageHeight = settings.getInt(CFG_IMAGE_HEIGHT);
        m_resizeToWindow = settings.getBoolean(CFG_RESIZE_TO_WINDOW);
        m_displayFullscreenButton = settings.getBoolean(CFG_DISPLAY_FULLSCREEN_BUTTON);
        m_chartTitle = settings.getString(CFG_CHART_TITLE);
        m_chartSubtitle = settings.getString(CFG_CHART_SUBTITLE);

        m_enableViewConfiguration = settings.getBoolean(CFG_ENABLE_CONFIG);
        m_enableTitleChange = settings.getBoolean(CFG_ENABLE_TTILE_CHANGE);
        m_enableColorModeEdit = settings.getBoolean(CFG_ENABLE_COLOR_MODE_EDIT);

        m_threeColorGradient = settings.getStringArray(CFG_THREE_COLOR_GRADIENT);
        m_discreteGradientColors = settings.getStringArray(CFG_DISCRETE_GRADIENT_COLORS);
        m_continuousGradient = settings.getBoolean(CFG_CONTINUOUS_GRADIENT);
        m_numDiscreteColors = settings.getInt(CFG_NUM_DISCRETE_COLORS);
        m_missingValueColor = settings.getString(CFG_MISSING_VALUE_COLOR);

        m_columns.loadConfigurationInModel(settings);
        m_labelColumn = settings.getString(CFG_LABEL_COLUMN);
        m_svgLabelColumn = settings.getString(CFG_SVG_LABEL_COLUMN);

        m_subscribeFilter = settings.getBoolean(CFG_SUBSCRIBE_FILTER);

        m_enableSelection = settings.getBoolean(CFG_ENABLE_SELECTION);
        m_publishSelection = settings.getBoolean(CFG_PUBLISH_SELECTION);
        m_subscribeSelection = settings.getBoolean(CFG_SUBSCRIBE_SELECTION);
        m_selectionColumnName = settings.getString(CFG_SELECTION_COLUMN_NAME);
        m_selection = settings.getStringArray(CFG_SELECTION);

        m_enablePaging = settings.getBoolean(CFG_ENABLE_PAGING);
        m_initialPageSize = settings.getInt(CFG_INITIAL_PAGE_SIZE);
        m_enablePageSizeChange = settings.getBoolean(CFG_ENABLE_PAGE_SIZE_CHANGE);
        m_allowedPageSizes = settings.getIntArray(CFG_PAGE_SIZES);
        m_pageSizeShowAll = settings.getBoolean(CFG_PAGE_SIZE_SHOW_ALL);

        m_displayDataCellToolTip = settings.getBoolean(CFG_DISPLAY_DATA_CELL_TOOL_TIP);
        m_displayRowToolTip = settings.getBoolean(CFG_DISPLAY_ROW_TOOL_TIP);

        m_enableZoom = settings.getBoolean(CFG_ENABLE_ZOOM);
        m_enablePanning = settings.getBoolean(CFG_ENABLE_PANNING);
        m_showZoomResetButton = settings.getBoolean(CFG_SHOW_ZOOM_RESET_BUTTON);
        m_xMin = settings.getString(CFG_X_MIN);
        m_xMax = settings.getString(CFG_X_MAX);
        m_yMin = settings.getString(CFG_Y_MIN);
        m_yMax = settings.getString(CFG_Y_MAX);
    }

    /** Loads parameters in Dialog.
     * @param settings To load from.
     * @param spec The spec from the incoming data table
     */
    public void loadSettingsForDialog(final NodeSettingsRO settings, final DataTableSpec spec) {
        m_customCSS = settings.getString(CFG_CUSTOM_CSS, DEFAULT_CUSTOM_CSS);
        m_hideInWizard = settings.getBoolean(CFG_HIDE_IN_WIZARD, DEFAULT_HIDE_IN_WIZARD);
        m_showWarningInView = settings.getBoolean(CFG_SHOW_WARNING_IN_VIEW, DEFAULT_SHOW_WARNING_IN_VIEW);
        m_generateImage = settings.getBoolean(CFG_GENERATE_IMAGE, DEFAULT_GENERATE_IMAGE);
        m_imageWidth = settings.getInt(CFG_IMAGE_WIDTH, DEFAULT_WIDTH);
        m_imageHeight = settings.getInt(CFG_IMAGE_HEIGHT, DEFAULT_HEIGHT);
        m_resizeToWindow = settings.getBoolean(CFG_RESIZE_TO_WINDOW, DEFAULT_RESIZE_TO_WINDOW);
        m_displayFullscreenButton = settings.getBoolean(CFG_DISPLAY_FULLSCREEN_BUTTON, DEFAULT_DISPLAY_FULLSCREEN_BUTTON);
        m_chartTitle = settings.getString(CFG_CHART_TITLE, DEFAULT_CHART_TITLE);
        m_chartSubtitle = settings.getString(CFG_CHART_SUBTITLE, DEFAULT_CHART_SUBTITLE);

        m_enableViewConfiguration = settings.getBoolean(CFG_ENABLE_CONFIG, DEFAULT_ENABLE_CONFIG);
        m_enableTitleChange = settings.getBoolean(CFG_ENABLE_TTILE_CHANGE, DEFAULT_ENABLE_TTILE_CHANGE);
        m_enableColorModeEdit = settings.getBoolean(CFG_ENABLE_COLOR_MODE_EDIT, DEFAULT_ENABLE_COLOR_MODE_EDIT);

        m_threeColorGradient = settings.getStringArray(CFG_THREE_COLOR_GRADIENT, DEFAULT_THREE_COLOR_GRADIENT);
        m_discreteGradientColors = settings.getStringArray(CFG_DISCRETE_GRADIENT_COLORS, DEFAULT_THREE_COLOR_GRADIENT);
        m_continuousGradient = settings.getBoolean(CFG_CONTINUOUS_GRADIENT, DEFAULT_CONTINUOUS_GRADIENT);
        m_numDiscreteColors = settings.getInt(CFG_NUM_DISCRETE_COLORS, DEFAULT_NUM_DISCRETE_COLORS);
        m_missingValueColor = settings.getString(CFG_MISSING_VALUE_COLOR, DEFAULT_MISSING_VALUE_COLOR);

        m_columns.loadConfigurationInDialog(settings, spec);
        m_labelColumn = settings.getString(CFG_LABEL_COLUMN, null);
        m_svgLabelColumn = settings.getString(CFG_SVG_LABEL_COLUMN, null);

        m_subscribeFilter = settings.getBoolean(CFG_SUBSCRIBE_FILTER, DEFAULT_SUBSCRIBE_FILTER);

        m_enableSelection = settings.getBoolean(CFG_ENABLE_SELECTION, DEFAULT_ENABLE_SELECTION);
        m_publishSelection = settings.getBoolean(CFG_PUBLISH_SELECTION, DEFAULT_PUBLISH_SELECTION);
        m_subscribeSelection = settings.getBoolean(CFG_SUBSCRIBE_SELECTION, DEFAULT_SUBSCRIBE_SELECTION);
        m_selectionColumnName = settings.getString(CFG_SELECTION_COLUMN_NAME, DEFAULT_SELECTION_COLUMN_NAME);
        m_selection = settings.getStringArray(CFG_SELECTION, DEFAULT_SELECTION);

        m_enablePaging = settings.getBoolean(CFG_ENABLE_PAGING, DEFAULT_ENABLE_PAGING);
        m_initialPageSize = settings.getInt(CFG_INITIAL_PAGE_SIZE, DEFAULT_INITIAL_PAGE_SIZE);
        m_enablePageSizeChange = settings.getBoolean(CFG_ENABLE_PAGE_SIZE_CHANGE, DEFAULT_ENABLE_PAGE_SIZE_CHANGE);
        m_allowedPageSizes = settings.getIntArray(CFG_PAGE_SIZES, DEFAULT_PAGE_SIZES);
        m_pageSizeShowAll = settings.getBoolean(CFG_PAGE_SIZE_SHOW_ALL, DEFAULT_PAGE_SIZE_SHOW_ALL);

        m_displayDataCellToolTip = settings.getBoolean(CFG_DISPLAY_DATA_CELL_TOOL_TIP, DEFAULT_DISPLAY_DATA_CELL_TOOL_TIP);
        m_displayRowToolTip = settings.getBoolean(CFG_DISPLAY_ROW_TOOL_TIP, DEFAULT_DISPLAY_ROW_TOOL_TIP);

        m_enableZoom = settings.getBoolean(CFG_ENABLE_ZOOM, DEFAULT_ENABLE_ZOOM);
        m_enablePanning = settings.getBoolean(CFG_ENABLE_PANNING, DEFAULT_ENABLE_PANNING);
        m_showZoomResetButton = settings.getBoolean(CFG_SHOW_ZOOM_RESET_BUTTON, DEFAULT_SHOW_ZOOM_RESET_BUTTON);
        m_xMin = settings.getString(CFG_X_MIN, DEFAULT_X_MIN);
        m_xMax = settings.getString(CFG_X_MAX, DEFAULT_X_MAX);
        m_yMin = settings.getString(CFG_Y_MIN, DEFAULT_Y_MIN);
        m_yMax = settings.getString(CFG_Y_MAX, DEFAULT_Y_MAX);
    }

}
