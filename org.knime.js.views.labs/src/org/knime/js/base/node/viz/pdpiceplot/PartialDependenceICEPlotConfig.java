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

import java.awt.Color;

import org.knime.core.data.DataTableSpec;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.defaultnodesettings.SettingsModelBoolean;
import org.knime.core.node.defaultnodesettings.SettingsModelColor;
import org.knime.core.node.defaultnodesettings.SettingsModelDouble;
import org.knime.core.node.defaultnodesettings.SettingsModelDoubleBounded;
import org.knime.core.node.defaultnodesettings.SettingsModelIntegerBounded;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

/**
 *
 * @author Ben Laney
 */
public final class PartialDependenceICEPlotConfig {

    /*
     * Constants unavailable to user
     */
    static final String SELECTED_COLUMN_NAME = "Selected";

    static final String INTERNAL_JSON_MODEL_VALUE_COL_NAME = "modelOutputColumn";

    static final String CFG_CSS_CUSTOM = "customCSS";

    static final String CFG_RUNNING_IN_VIEW = "runningInView";

    /*
     * Execution Static CFG Keys
     */
    static final String CFG_HIDE_IN_WIZARD = "hideInWizard";

    static final String CFG_GENERATE_IMAGE = "generateImage";

    static final String CFG_MAX_NUM_ROWS = "maxNumRows";

    static final String CFG_FEATURE_COLUMN = "featureColumn";

    static final String CFG_ROW_ID_COLUMN = "rowIDColumn";

    static final String CFG_PREDICTION_COLUMN = "predictionColumn";

    static final String CFG_ORIGINAL_FEATURE_COLUMN = "originalFeatureCol";

    /*
     * PDP/ICE Static CFG Keys
     */
    static final String CFG_SHOW_PDP = "showPDP";

    static final String CFG_PDP_COLOR = "pdpColor";

    static final String CFG_PDP_LINE_WEIGHT = "pdpWeight";

    static final String CFG_SHOW_PDP_MARGIN = "pdpShowMargin";

    static final String CFG_PDP_MARGIN_TYPE = "pdpMarginType";

    static final String CFG_PDP_MARGIN_MULTIPLIER = "pdpMarginMultiplier";

    static final String CFG_PDP_MARGIN_ALPHA_VAL = "pdpMarginAlphaVal";

    static final String CFG_SHOW_ICE = "showIce";

    static final String CFG_ICE_COLOR = "iceColor";

    static final String CFG_ICE_WEIGHT = "iceWeight";

    static final String CFG_ICE_ALPHA_VAL = "lineAlphaVal";

    static final String CFG_SHOW_DATA_POINTS = "showDataPoints";

    static final String CFG_DATA_POINT_COLOR = "dataPointColor";

    static final String CFG_DATA_POINT_WEIGHT = "dataPointWeight";

    static final String CFG_DATA_POINT_ALPHA_VAL = "dataPointAlphaVal";

    /*
     * General View Static CFG Keys
     */
    static final String CFG_X_AXIS_MIN = "xAxisMin";

    static final String CFG_X_AXIS_MAX = "xAxisMax";

    static final String CFG_Y_AXIS_MIN = "yAxisMin";

    static final String CFG_Y_AXIS_MAX = "yAxisMax";

    static final String CFG_X_AXIS_LABEL = "xAxisLabel";

    static final String CFG_Y_AXIS_LABEL = "yAxisLabel";

    static final String CFG_CHART_TITLE = "chartTitle";

    static final String CFG_CHART_SUBTITLE = "chartSubtitle";

    static final String CFG_VIEW_WIDTH = "viewWidth";

    static final String CFG_VIEW_HEIGHT = "viewHeight";

    static final String CFG_Y_AXIS_MARGIN = "yAxisMargin";

    static final String CFG_RESIZE_TO_FILL = "resizeToFill";

    static final String CFG_FULLSCREEN_BUTTON = "fullscreenButton";

    static final String CFG_BACKGROUND_COLOR = "backgroundColor";

    static final String CFG_DATA_AREA_COLOR = "dataAreaColor";

    static final String CFG_SHOW_GRID = "showGrid";

    static final String CFG_GRID_COLOR = "gridColor";

    static final String CFG_SHOW_WARNINGS = "showWarnings";

    /*
     * View Controls Static CFG Keys
     */
    static final String CFG_ENABLE_SELECTION = "enableSelection";

    static final String CFG_ENABLE_INTERACTIVE_CTRLS = "enableJSCtrls";

    static final String CFG_ENABLE_MOUSE_CROSSHAIR = "enableCrosshair";

    static final String CFG_ENABLE_PANNING = "enablePanning";

    static final String CFG_ENABLE_SCROLL_ZOOM = "enableScrollZoom";

    static final String CFG_ENABLE_DRAG_ZOOM = "enableDragZoom";

    static final String CFG_SHOW_ZOOM_RESET = "showZoomReset";

    static final String CFG_SUBSCRIBE_TO_SELECTION = "subscribeToSelection";

    static final String CFG_PUBLISH_SELECTION = "publishSelection";

    static final String CFG_SUBSCRIBE_TO_FILTERS = "subscribeToFilters";

    static final String CFG_SHOW_STATIC_THRESHOLD_LINE = "staticThreshold";

    static final String CFG_STATIC_LINE_COLOR = "staticLineColor";

    static final String CFG_STATIC_LINE_WEIGHT = "staticLineWeight";

    static final String CFG_STATIC_LINE_Y_VALUE = "staticLineYValue";

    /*
     * JS Menu CFG Keys
    */
    static final String CFG_ENABLE_TITLE_CONTROLS = "enableTitleControls";

    static final String CFG_ENABLE_AXIS_LABEL_CONTROLS = "enableAxisLabelControls";

    static final String CFG_ENABLE_PDP_CONTROLS = "enablePDPControls";

    static final String CFG_ENABLE_PDP_MARGIN_CONTROLS = "enablePDPMarginControls";

    static final String CFG_ENABLE_ICE_CONTROLS = "enableICEControls";

    static final String CFG_ENABLE_STATIC_LINE_CONTROLS = "enableStaticLineControls";

    static final String CFG_ENABLE_DATA_POINT_CONTROLS = "enableDataPointControls";

    static final String CFG_ENABLE_SELECTION_FILTER_CONTROLS = "enableSelectionFilterControls";

    static final String CFG_ENABLE_SELECTION_CONTROLS = "enableSelectionControls";

    static final String CFG_ENABLE_Y_AXIS_MARGIN_CONTROLS = "enableYAxisMarginControls";

    static final String CFG_ENABLE_SMART_ZOOM_CONTROLS = "enableSmartZoomControls";

    static final String CFG_ENABLE_GRID_CONTROLS = "enableGridControls";

    static final String CFG_ENABLE_MOUSE_CROSSHAIR_CONTROLS = "enableMouseCrosshairControls";

    static final String CFG_ENABLE_ADVANCED_OPTIONS_CONTROLS = "enableAdvancedOptionsControls";

    /*
     * Execution Default Values
     */
    static final boolean DEFAULT_HIDE_IN_WIZARD = false;

    static final boolean DEFAULT_GENERATE_IMAGE = false;

    static final int DEFAULT_MAX_NUM_ROWS = 2500;

    static final String DEFAULT_FEATURE_COLUMN = "Feature";

    static final String DEFAULT_ROW_ID_COLUMN = "RowID";

    static final String DEFAULT_PREDICTION_COLUMN = "prediction";

    static final String DEFAULT_ORIGINAL_FEATURE_COLUMN = "";

    /*
     * PDP/ICE Default Values
     */
    static final boolean DEFAULT_SHOW_PDP = true;

    static final Color DEFAULT_PDP_COLOR = new Color(0, 0, 255);

    static final double DEFAULT_PDP_LINE_WEIGHT = 2;

    static final boolean DEFAULT_SHOW_PDP_MARGIN = true;

    static final String DEFAULT_PDP_MARGIN_TYPE = "Standard Deviation";

    static final double DEFAULT_PDP_MARGIN_MULTIPLIER = .5;

    static final double DEFAULT_PDP_MARGIN_ALPHA_VAL = .2;

    static final boolean DEFAULT_SHOW_ICE = false;

    static final Color DEFAULT_ICE_COLOR = new Color(0, 0, 0);

    static final double DEFAULT_ICE_WEIGHT = 1;

    static final double DEFAULT_ICE_ALPHA_VAL = .5;

    static final boolean DEFAULT_SHOW_DATA_POINTS = false;

    static final Color DEFAULT_DATA_POINT_COLOR = new Color(0, 0, 0);

    static final double DEFAULT_DATA_POINT_WEIGHT = 2;

    static final double DEFAULT_DATA_POINT_ALPHA_VAL = 1;

    /*
     * General View Default Values
     */
    static final Double DEFAULT_X_AXIS_MIN = 0.0;

    static final Double DEFAULT_X_AXIS_MAX = 0.0;

    static final Double DEFAULT_Y_AXIS_MIN = 0.0;

    static final Double DEFAULT_Y_AXIS_MAX = 0.0;

    static final String DEFAULT_X_AXIS_LABEL = "Feature";

    static final String DEFAULT_Y_AXIS_LABEL = "Prediction";

    static final String DEFAULT_CHART_TITLE = "Partial Dependence Plot";

    static final String DEFAULT_CHART_SUBTITLE = "";

    static final double DEFAULT_Y_AXIS_MARGIN = 0.02;

    static final int DEFAULT_VIEW_WIDTH = 800;

    static final int DEFAULT_VIEW_HEIGHT = 600;

    static final boolean DEFAULT_RESIZE_TO_FILL = true;

    static final boolean DEFAULT_FULLSCREEN_BUTTON = true;

    static final Color DEFAULT_BACKGROUND_COLOR = new Color(255, 255, 255);

    static final Color DEFAULT_DATA_AREA_COLOR = new Color(255, 255, 255);

    static final boolean DEFAULT_SHOW_GRID = true;

    static final Color DEFAULT_GRID_COLOR = new Color(0, 0, 0);

    static final boolean DEFAULT_SHOW_WARNINGS = true;

    /*
     * View Controls Default Values
     */
    static final boolean DEFAULT_ENABLE_SELECTION = true;

    static final boolean DEFAULT_ENABLE_INTERACTIVE_CTRLS = true;

    static final boolean DEFAULT_ENABLE_MOUSE_CROSSHAIR = false;

    static final boolean DEFAULT_ENABLE_PANNING = true;

    static final boolean DEFAULT_ENABLE_SCROLL_ZOOM = true;

    static final boolean DEFAULT_ENABLE_DRAG_ZOOM = true;

    static final boolean DEFAULT_SHOW_ZOOM_RESET = true;

    static final boolean DEFAULT_SUBSCRIBE_TO_SELECTION = true;

    static final boolean DEFAULT_PUBLISH_SELECTION = true;

    static final boolean DEFAULT_SUBSCRIBE_TO_FILTERS = true;

    static final boolean DEFAULT_SHOW_STATIC_THRESHOLD_LINE = false;

    static final Color DEFAULT_STATIC_LINE_COLOR = new Color(255, 40, 40);

    static final double DEFAULT_STATIC_LINE_WEIGHT = 1.5;

    static final double DEFAULT_STATIC_LINE_Y_VALUE = 0.0;

    /*
     * JS Menu Default Values
    */
    static final boolean DEFAULT_ENABLE_TITLE_CONTROLS = false;

    static final boolean DEFAULT_ENABLE_AXIS_LABEL_CONTROLS = false;

    static final boolean DEFAULT_ENABLE_PDP_CONTROLS = true;

    static final boolean DEFAULT_ENABLE_PDP_MARGIN_CONTROLS = true;

    static final boolean DEFAULT_ENABLE_ICE_CONTROLS = true;

    static final boolean DEFAULT_ENABLE_STATIC_LINE_CONTROLS = true;

    static final boolean DEFAULT_ENABLE_DATA_POINT_CONTROLS = true;

    static final boolean DEFAULT_ENABLE_SELECTION_FILTER_CONTROLS = false;

    static final boolean DEFAULT_ENABLE_SELECTION_CONTROLS = true;

    static final boolean DEFAULT_ENABLE_Y_AXIS_MARGIN_CONTROLS = false;

    static final boolean DEFAULT_ENABLE_SMART_ZOOM_CONTROLS = true;

    static final boolean DEFAULT_ENABLE_GRID_CONTROLS = false;

    static final boolean DEFAULT_ENABLE_MOUSE_CROSSHAIR_CONTROLS = false;

    static final boolean DEFAULT_ENABLE_ADVANCED_OPTIONS_CONTROLS = false;


    /*
     * Internal Constants
    */
    static final String DEFAULT_CUSTOM_CSS = "";

    static final int MODEL_OUTPUT_TABLE_INPORT = 0;

    static final int ORIGINAL_DATA_TABLE_INPORT = 1;

    static final int COLOR_TABLE_INPORT = 2;

    static final String COLOR_STRING_PREFIX = "rgba(";

    static final Boolean DEFAULT_RUNNING_IN_VIEW = false;

    /*
     * INTERNAL FIELDS
     *
     *
     */
    private SettingsModelBoolean m_hideInWizard = new SettingsModelBoolean(CFG_HIDE_IN_WIZARD, DEFAULT_HIDE_IN_WIZARD);

    private SettingsModelBoolean m_generateImage = new SettingsModelBoolean(CFG_GENERATE_IMAGE, DEFAULT_GENERATE_IMAGE);

    private SettingsModelIntegerBounded m_maxNumRows =
        new SettingsModelIntegerBounded(CFG_MAX_NUM_ROWS, DEFAULT_MAX_NUM_ROWS, 0, Integer.MAX_VALUE);

    private SettingsModelString m_featureCol = new SettingsModelString(CFG_FEATURE_COLUMN, DEFAULT_FEATURE_COLUMN);

    private SettingsModelString m_rowIDCol = new SettingsModelString(CFG_ROW_ID_COLUMN, DEFAULT_ROW_ID_COLUMN);

    private SettingsModelString m_predictionCol =
        new SettingsModelString(CFG_PREDICTION_COLUMN, DEFAULT_PREDICTION_COLUMN);

    private SettingsModelString m_origFeatureCol =
        new SettingsModelString(CFG_ORIGINAL_FEATURE_COLUMN, DEFAULT_ORIGINAL_FEATURE_COLUMN);

    private SettingsModelBoolean m_showPDP = new SettingsModelBoolean(CFG_SHOW_PDP, DEFAULT_SHOW_PDP);

    private SettingsModelColor m_PDPColor = new SettingsModelColor(CFG_PDP_COLOR, DEFAULT_PDP_COLOR);

    private SettingsModelDoubleBounded m_PDPLineWeight =
        new SettingsModelDoubleBounded(CFG_PDP_LINE_WEIGHT, DEFAULT_PDP_LINE_WEIGHT, 0, 100);

    private SettingsModelBoolean m_showPDPMargin =
        new SettingsModelBoolean(CFG_SHOW_PDP_MARGIN, DEFAULT_SHOW_PDP_MARGIN);

    private SettingsModelString m_PDPMarginType = new SettingsModelString(CFG_PDP_MARGIN_TYPE, DEFAULT_PDP_MARGIN_TYPE);

    private SettingsModelDoubleBounded m_PDPMarginMultiplier =
        new SettingsModelDoubleBounded(CFG_PDP_MARGIN_MULTIPLIER, DEFAULT_PDP_MARGIN_MULTIPLIER, 0, Double.MAX_VALUE);

    private SettingsModelDoubleBounded m_PDPMarginAlphaVal =
        new SettingsModelDoubleBounded(CFG_PDP_MARGIN_ALPHA_VAL, DEFAULT_PDP_MARGIN_ALPHA_VAL, 0, 1);

    private SettingsModelBoolean m_showICE = new SettingsModelBoolean(CFG_SHOW_ICE, DEFAULT_SHOW_ICE);

    private SettingsModelColor m_ICEColor = new SettingsModelColor(CFG_ICE_COLOR, DEFAULT_ICE_COLOR);

    private SettingsModelDoubleBounded m_ICEWeight =
        new SettingsModelDoubleBounded(CFG_ICE_WEIGHT, DEFAULT_ICE_WEIGHT, 0, 100);

    private SettingsModelDoubleBounded m_ICEAlphaVal =
        new SettingsModelDoubleBounded(CFG_ICE_ALPHA_VAL, DEFAULT_ICE_ALPHA_VAL, 0, 1);

    private SettingsModelBoolean m_showDataPoints =
        new SettingsModelBoolean(CFG_SHOW_DATA_POINTS, DEFAULT_SHOW_DATA_POINTS);

    private SettingsModelColor m_dataPointColor =
        new SettingsModelColor(CFG_DATA_POINT_COLOR, DEFAULT_DATA_POINT_COLOR);

    private SettingsModelDoubleBounded m_dataPointWeight =
        new SettingsModelDoubleBounded(CFG_DATA_POINT_WEIGHT, DEFAULT_DATA_POINT_WEIGHT, 0, 100);

    private SettingsModelDoubleBounded m_dataPointAlphaVal =
        new SettingsModelDoubleBounded(CFG_DATA_POINT_ALPHA_VAL, DEFAULT_DATA_POINT_ALPHA_VAL, 0, 1);

    private SettingsModelDouble m_xAxisMin = new SettingsModelDouble(CFG_X_AXIS_MIN, DEFAULT_X_AXIS_MIN);

    private SettingsModelDouble m_xAxisMax = new SettingsModelDouble(CFG_X_AXIS_MAX, DEFAULT_X_AXIS_MAX);

    private SettingsModelDouble m_yAxisMin = new SettingsModelDouble(CFG_Y_AXIS_MIN, DEFAULT_Y_AXIS_MIN);

    private SettingsModelDouble m_yAxisMax = new SettingsModelDouble(CFG_Y_AXIS_MAX, DEFAULT_Y_AXIS_MAX);

    private SettingsModelString m_xAxisLabel = new SettingsModelString(CFG_X_AXIS_LABEL, DEFAULT_X_AXIS_LABEL);

    private SettingsModelString m_yAxisLabel = new SettingsModelString(CFG_Y_AXIS_LABEL, DEFAULT_Y_AXIS_LABEL);

    private SettingsModelString m_chartTitle = new SettingsModelString(CFG_CHART_TITLE, DEFAULT_CHART_TITLE);

    private SettingsModelString m_chartSubtitle = new SettingsModelString(CFG_CHART_SUBTITLE, DEFAULT_CHART_SUBTITLE);

    private SettingsModelIntegerBounded m_viewWidth =
        new SettingsModelIntegerBounded(CFG_VIEW_WIDTH, DEFAULT_VIEW_WIDTH, 0, 4000);

    private SettingsModelIntegerBounded m_viewHeight =
        new SettingsModelIntegerBounded(CFG_VIEW_HEIGHT, DEFAULT_VIEW_HEIGHT, 0, 4000);

    private SettingsModelDoubleBounded m_yAxisMargin =
        new SettingsModelDoubleBounded(CFG_Y_AXIS_MARGIN, DEFAULT_Y_AXIS_MARGIN, 0, 1);

    private SettingsModelBoolean m_resizeToFill = new SettingsModelBoolean(CFG_RESIZE_TO_FILL, DEFAULT_RESIZE_TO_FILL);

    private SettingsModelBoolean m_fullscreenButton =
        new SettingsModelBoolean(CFG_FULLSCREEN_BUTTON, DEFAULT_FULLSCREEN_BUTTON);

    private SettingsModelColor m_backgroundColor =
        new SettingsModelColor(CFG_BACKGROUND_COLOR, DEFAULT_BACKGROUND_COLOR);

    private SettingsModelColor m_dataAreaColor = new SettingsModelColor(CFG_DATA_AREA_COLOR, DEFAULT_DATA_AREA_COLOR);

    private SettingsModelBoolean m_showGrid = new SettingsModelBoolean(CFG_SHOW_GRID, DEFAULT_SHOW_GRID);

    private SettingsModelColor m_gridColor = new SettingsModelColor(CFG_GRID_COLOR, DEFAULT_GRID_COLOR);

    private SettingsModelBoolean m_showWarnings = new SettingsModelBoolean(CFG_SHOW_WARNINGS, DEFAULT_SHOW_WARNINGS);

    private SettingsModelBoolean m_subscribeToSelection =
        new SettingsModelBoolean(CFG_SUBSCRIBE_TO_SELECTION, DEFAULT_SUBSCRIBE_TO_SELECTION);

    private SettingsModelBoolean m_publishSelection =
        new SettingsModelBoolean(CFG_PUBLISH_SELECTION, DEFAULT_PUBLISH_SELECTION);

    private SettingsModelBoolean m_subscribeToFilters =
        new SettingsModelBoolean(CFG_SUBSCRIBE_TO_FILTERS, DEFAULT_SUBSCRIBE_TO_FILTERS);

    private SettingsModelBoolean m_showStaticLine =
        new SettingsModelBoolean(CFG_SHOW_STATIC_THRESHOLD_LINE, DEFAULT_SHOW_STATIC_THRESHOLD_LINE);

    private SettingsModelColor m_staticLineColor =
        new SettingsModelColor(CFG_STATIC_LINE_COLOR, DEFAULT_STATIC_LINE_COLOR);

    private SettingsModelDoubleBounded m_staticLineWeight =
        new SettingsModelDoubleBounded(CFG_STATIC_LINE_WEIGHT, DEFAULT_STATIC_LINE_WEIGHT, 0, 100);

    private SettingsModelDouble m_staticLineYValue =
        new SettingsModelDouble(CFG_STATIC_LINE_Y_VALUE, DEFAULT_STATIC_LINE_Y_VALUE);

    private SettingsModelBoolean m_enableSelection =
        new SettingsModelBoolean(CFG_ENABLE_SELECTION, DEFAULT_ENABLE_SELECTION);

    private SettingsModelBoolean m_enableInteractiveCtrls =
        new SettingsModelBoolean(CFG_ENABLE_INTERACTIVE_CTRLS, DEFAULT_ENABLE_INTERACTIVE_CTRLS);

    private SettingsModelBoolean m_enableMouseCrosshair =
        new SettingsModelBoolean(CFG_ENABLE_MOUSE_CROSSHAIR, DEFAULT_ENABLE_MOUSE_CROSSHAIR);

    private SettingsModelBoolean m_enablePanning = new SettingsModelBoolean(CFG_ENABLE_PANNING, DEFAULT_ENABLE_PANNING);

    private SettingsModelBoolean m_enableScrollZoom =
        new SettingsModelBoolean(CFG_ENABLE_SCROLL_ZOOM, DEFAULT_ENABLE_SCROLL_ZOOM);

    private SettingsModelBoolean m_enableDragZoom =
        new SettingsModelBoolean(CFG_ENABLE_DRAG_ZOOM, DEFAULT_ENABLE_DRAG_ZOOM);

    private SettingsModelBoolean m_showZoomReset =
        new SettingsModelBoolean(CFG_SHOW_ZOOM_RESET, DEFAULT_SHOW_ZOOM_RESET);

    private SettingsModelBoolean m_enableTitleControls =
            new SettingsModelBoolean(CFG_ENABLE_TITLE_CONTROLS, DEFAULT_ENABLE_TITLE_CONTROLS);

    private SettingsModelBoolean m_enableAxisLabelControls =
            new SettingsModelBoolean(CFG_ENABLE_AXIS_LABEL_CONTROLS, DEFAULT_ENABLE_AXIS_LABEL_CONTROLS);

    private SettingsModelBoolean m_enablePDPControls =
            new SettingsModelBoolean(CFG_ENABLE_PDP_CONTROLS, DEFAULT_ENABLE_PDP_CONTROLS);

    private SettingsModelBoolean m_enablePDPMarginControls =
            new SettingsModelBoolean(CFG_ENABLE_PDP_MARGIN_CONTROLS, DEFAULT_ENABLE_PDP_MARGIN_CONTROLS);

    private SettingsModelBoolean m_enableICEControls =
            new SettingsModelBoolean(CFG_ENABLE_ICE_CONTROLS, DEFAULT_ENABLE_ICE_CONTROLS);

    private SettingsModelBoolean m_enableStaticLineControls =
            new SettingsModelBoolean(CFG_ENABLE_STATIC_LINE_CONTROLS, DEFAULT_ENABLE_STATIC_LINE_CONTROLS);

    private SettingsModelBoolean m_enableDataPointControls =
            new SettingsModelBoolean(CFG_ENABLE_DATA_POINT_CONTROLS, DEFAULT_ENABLE_DATA_POINT_CONTROLS);

    private SettingsModelBoolean m_enableSelectionFilterControls =
            new SettingsModelBoolean(CFG_ENABLE_SELECTION_FILTER_CONTROLS, DEFAULT_ENABLE_SELECTION_FILTER_CONTROLS);

    private SettingsModelBoolean m_enableSelectionControls =
            new SettingsModelBoolean(CFG_ENABLE_SELECTION_CONTROLS, DEFAULT_ENABLE_SELECTION_CONTROLS);

    private SettingsModelBoolean m_enableYAxisMarginControls =
            new SettingsModelBoolean(CFG_ENABLE_Y_AXIS_MARGIN_CONTROLS, DEFAULT_ENABLE_Y_AXIS_MARGIN_CONTROLS);

    private SettingsModelBoolean m_enableSmartZoomControls =
            new SettingsModelBoolean(CFG_ENABLE_SMART_ZOOM_CONTROLS, DEFAULT_ENABLE_SMART_ZOOM_CONTROLS);

    private SettingsModelBoolean m_enableGridControls =
            new SettingsModelBoolean(CFG_ENABLE_GRID_CONTROLS, DEFAULT_ENABLE_GRID_CONTROLS);

    private SettingsModelBoolean m_enableMouseCrosshairControls =
            new SettingsModelBoolean(CFG_ENABLE_MOUSE_CROSSHAIR_CONTROLS, DEFAULT_ENABLE_MOUSE_CROSSHAIR_CONTROLS);

    private SettingsModelBoolean m_enableAdvancedOptionsControls =
            new SettingsModelBoolean(CFG_ENABLE_ADVANCED_OPTIONS_CONTROLS, DEFAULT_ENABLE_ADVANCED_OPTIONS_CONTROLS);

    private SettingsModelString m_customCSS = new SettingsModelString(CFG_CSS_CUSTOM, DEFAULT_CUSTOM_CSS);

    private SettingsModelBoolean m_runningInView = new SettingsModelBoolean(CFG_RUNNING_IN_VIEW, DEFAULT_RUNNING_IN_VIEW);


    /**
     * @return HideInWizard
     */
    public boolean getHideInWizard() {
        return m_hideInWizard.getBooleanValue();
    }

    /**
     * @param hideInWizard
     */
    public void setHideInWizard(final boolean hideInWizard) {
        this.m_hideInWizard.setBooleanValue(hideInWizard);
    }

    /**
     * @return generateImage
     */
    public boolean getGenerateImage() {
        return m_generateImage.getBooleanValue();
    }

    /**
     * @param generateImage
     */
    public void setGenerateImage(final boolean generateImage) {
        this.m_generateImage.setBooleanValue(generateImage);
    }

    /**
     * @return maxNumRows
     */
    public int getMaxNumRows() {
        return m_maxNumRows.getIntValue();
    }

    /**
     * @param maxNumRows
     */
    public void setMaxNumRows(final int maxNumRows) {
        this.m_maxNumRows.setIntValue(maxNumRows);
    }

    /**
     * @return featureCol
     */
    public String getFeatureCol() {
        return m_featureCol.getStringValue();
    }

    /**
     * @param featureCol
     */
    public void setFeatureCol(final String featureCol) {
        this.m_featureCol.setStringValue(featureCol);
    }

    /**
     * @return rowIDCol
     */
    public String getRowIDCol() {
        return m_rowIDCol.getStringValue();
    }

    /**
     * @param rowIDCol
     */
    public void setRowIDCol(final String rowIDCol) {
        this.m_rowIDCol.setStringValue(rowIDCol);
    }

    /**
     * @return predictionCol
     */
    public String getPredictionCol() {
        return m_predictionCol.getStringValue();
    }

    /**
     * @param predictionCol
     */
    public void setPredictionCol(final String predictionCol) {
        this.m_predictionCol.setStringValue(predictionCol);
    }

    /**
     * @return origFeatureCol
     */
    public String getOrigFeatureCol() {
        return m_origFeatureCol.getStringValue();
    }

    /**
     * @param origFeatureCol
     */
    public void setOrigFeatureCol(final String origFeatureCol) {
        this.m_origFeatureCol.setStringValue(origFeatureCol);
    }

    /**
     * @return showPDP
     */
    public boolean getShowPDP() {
        return m_showPDP.getBooleanValue();
    }

    /**
     * @param showPDP
     */
    public void setShowPDP(final boolean showPDP) {
        this.m_showPDP.setBooleanValue(showPDP);
    }

    /**
     * @return pdpColor
     */
    public Color getPDPColor() {
        return m_PDPColor.getColorValue();
    }

    /**
     * @param PDPColor
     */
    public void setPDPColor(final Color PDPColor) {
        this.m_PDPColor.setColorValue(PDPColor);
    }

    /**
     * @return pdpLineWeight
     */
    public double getPDPLineWeight() {
        return m_PDPLineWeight.getDoubleValue();
    }

    /**
     * @param PDPLineWeight
     */
    public void setPDPLineWeight(final double PDPLineWeight) {
        this.m_PDPLineWeight.setDoubleValue(PDPLineWeight);
    }

    /**
     * @return pdpMargin
     */
    public boolean getShowPDPMargin() {
        return m_showPDPMargin.getBooleanValue();
    }

    /**
     * @param showPDPMargin
     */
    public void setShowPDPMargin(final boolean showPDPMargin) {
        this.m_showPDPMargin.setBooleanValue(showPDPMargin);
    }

    /**
     * @return pdpMarginType
     */
    public String getPDPMarginType() {
        return m_PDPMarginType.getStringValue();
    }

    /**
     * @param PDPMarginType
     */
    public void setPDPMarginType(final String PDPMarginType) {
        this.m_PDPMarginType.setStringValue(PDPMarginType);
    }

    /**
     * @return pdpMarginMultiplier
     */
    public double getPDPMarginMultiplier() {
        return m_PDPMarginMultiplier.getDoubleValue();
    }

    /**
     * @param PDPMarginMultiplier
     */
    public void setPDPMarginMultiplier(final double PDPMarginMultiplier) {
        this.m_PDPMarginMultiplier.setDoubleValue(PDPMarginMultiplier);
    }

    /**
     * @return pdpMarginAlphaVal
     */
    public double getPDPMarginAlphaVal() {
        return m_PDPMarginAlphaVal.getDoubleValue();
    }

    /**
     * @param PDPMarginAlphaVal
     */
    public void setPDPMarginAlphaVal(final double PDPMarginAlphaVal) {
        this.m_PDPMarginAlphaVal.setDoubleValue(PDPMarginAlphaVal);
    }

    /**
     * @return showICE
     */
    public boolean getShowICE() {
        return m_showICE.getBooleanValue();
    }

    /**
     * @param showICE
     */
    public void setShowICE(final boolean showICE) {
        this.m_showICE.setBooleanValue(showICE);
    }

    /**
     * @return iceColor
     */
    public Color getICEColor() {
        return m_ICEColor.getColorValue();
    }

    /**
     * @param ICEColor
     */
    public void setICEColor(final Color ICEColor) {
        this.m_ICEColor.setColorValue(ICEColor);
    }

    /**
     * @return iceWeight
     */
    public double getICEWeight() {
        return m_ICEWeight.getDoubleValue();
    }

    /**
     * @param ICEWeight
     */
    public void setICEWeight(final double ICEWeight) {
        this.m_ICEWeight.setDoubleValue(ICEWeight);
    }

    /**
     * @return iceAlphaVal
     */
    public double getICEAlphaVal() {
        return m_ICEAlphaVal.getDoubleValue();
    }

    /**
     * @param ICEAlphaVal
     */
    public void setICEAlphaVal(final double ICEAlphaVal) {
        this.m_ICEAlphaVal.setDoubleValue(ICEAlphaVal);
    }

    /**
     * @return showDataPoints
     */
    public boolean getShowDataPoints() {
        return m_showDataPoints.getBooleanValue();
    }

    /**
     * @param showDataPoints
     */
    public void setShowDataPoints(final boolean showDataPoints) {
        this.m_showDataPoints.setBooleanValue(showDataPoints);
    }

    /**
     * @return dataPointColor
     */
    public Color getDataPointColor() {
        return m_dataPointColor.getColorValue();
    }

    /**
     * @param dataPointColor
     */
    public void setDataPointColor(final Color dataPointColor) {
        this.m_dataPointColor.setColorValue(dataPointColor);
    }

    /**
     * @return dataPointWeight
     */
    public double getDataPointWeight() {
        return m_dataPointWeight.getDoubleValue();
    }

    /**
     * @param dataPointWeight
     */
    public void setDataPointWeight(final double dataPointWeight) {
        this.m_dataPointWeight.setDoubleValue(dataPointWeight);
    }

    /**
     * @return dataPointAlphaVal
     */
    public double getDataPointAlphaVal() {
        return m_dataPointAlphaVal.getDoubleValue();
    }

    /**
     * @param dataPointAlphaVal
     */
    public void setDataPointAlphaVal(final double dataPointAlphaVal) {
        this.m_dataPointAlphaVal.setDoubleValue(dataPointAlphaVal);
    }

    /**
     * @return xAxisMin
     */
    public Double getXAxisMin() {
        return this.m_xAxisMin.getDoubleValue();
    }

    /**
     * @param xAxisMin
     */
    public void setXAxisMin(final double xAxisMin) {
        this.m_xAxisMin.setDoubleValue(xAxisMin);
    }

    /**
     * @return xAxisMax
     */
    public Double getXAxisMax() {
        return this.m_xAxisMax.getDoubleValue();
    }

    /**
     * @param xAxisMax
     */
    public void setXAxisMax(final double xAxisMax) {
        this.m_xAxisMax.setDoubleValue(xAxisMax);
    }

    /**
     * @return yAxisMin
     */
    public Double getYAxisMin() {
        return this.m_yAxisMin.getDoubleValue();
    }

    /**
     * @param yAxisMin
     */
    public void setYAxisMin(final double yAxisMin) {
        this.m_yAxisMin.setDoubleValue(yAxisMin);
    }

    /**
     * @return yAxisMax
     */
    public Double getYAxisMax() {
        return this.m_yAxisMax.getDoubleValue();
    }

    /**
     * @param yAxisMax
     */
    public void setYAxisMax(final double yAxisMax) {
        this.m_yAxisMax.setDoubleValue(yAxisMax);
    }

    /**
     * @return xAxisLabel
     */
    public String getXAxisLabel() {
        return m_xAxisLabel.getStringValue();
    }

    /**
     * @param xAxisLabel
     */
    public void setXAxisLabel(final String xAxisLabel) {
        this.m_xAxisLabel.setStringValue(xAxisLabel);
    }

    /**
     * @return yAxisLabel
     */
    public String getYAxisLabel() {
        return m_yAxisLabel.getStringValue();
    }

    /**
     * @param yAxisLabel
     */
    public void setYAxisLabel(final String yAxisLabel) {
        this.m_yAxisLabel.setStringValue(yAxisLabel);
    }

    /**
     * @return chartTitle
     */
    public String getChartTitle() {
        return m_chartTitle.getStringValue();
    }

    /**
     * @param chartTitle
     */
    public void setChartTitle(final String chartTitle) {
        this.m_chartTitle.setStringValue(chartTitle);
    }

    /**
     * @return chartSubtitle
     */
    public String getChartSubtitle() {
        return m_chartSubtitle.getStringValue();
    }

    /**
     * @param chartSubtitle
     */
    public void setChartSubtitle(final String chartSubtitle) {
        this.m_chartSubtitle.setStringValue(chartSubtitle);
    }

    /**
     * @return viewWidth
     */
    public int getViewWidth() {
        return m_viewWidth.getIntValue();
    }

    /**
     * @param viewWidth
     */
    public void setViewWidth(final int viewWidth) {
        this.m_viewWidth.setIntValue(viewWidth);
    }

    /**
     * @return viewHeight
     */
    public int getViewHeight() {
        return m_viewHeight.getIntValue();
    }

    /**
     * @param viewHeight
     */
    public void setViewHeight(final int viewHeight) {
        this.m_viewHeight.setIntValue(viewHeight);
    }

    /**
     * @return yAxisMargin
     */
    public double getYAxisMargin() {
        return m_yAxisMargin.getDoubleValue();
    }

    /**
     * @param yAxisMargin
     */
    public void setYAxisMargin(final double yAxisMargin) {
        this.m_yAxisMargin.setDoubleValue(yAxisMargin);
    }

    /**
     * @return resizeToFill
     */
    public boolean getResizeToFill() {
        return m_resizeToFill.getBooleanValue();
    }

    /**
     * @param resizeToFill
     */
    public void setResizeToFill(final boolean resizeToFill) {
        this.m_resizeToFill.setBooleanValue(resizeToFill);
    }

    /**
     * @return fullscreenButton
     */
    public boolean getFullscreenButton() {
        return m_fullscreenButton.getBooleanValue();
    }

    /**
     * @param fullscreenButton
     */
    public void setFullscreenButton(final boolean fullscreenButton) {
        this.m_fullscreenButton.setBooleanValue(fullscreenButton);
    }

    /**
     * @return backgroundColor
     */
    public Color getBackgroundColor() {
        return m_backgroundColor.getColorValue();
    }

    /**
     * @param backgroundColor
     */
    public void setBackgroundColor(final Color backgroundColor) {
        this.m_backgroundColor.setColorValue(backgroundColor);
    }

    /**
     * @return dataAreaColor
     */
    public Color getDataAreaColor() {
        return m_dataAreaColor.getColorValue();
    }

    /**
     * @param dataAreaColor
     */
    public void setDataAreaColor(final Color dataAreaColor) {
        this.m_dataAreaColor.setColorValue(dataAreaColor);
    }

    /**
     * @return showGrid
     */
    public boolean getShowGrid() {
        return m_showGrid.getBooleanValue();
    }

    /**
     * @param showGrid
     */
    public void setShowGrid(final boolean showGrid) {
        this.m_showGrid.setBooleanValue(showGrid);
    }

    /**
     * @return gridColor
     */
    public Color getGridColor() {
        return m_gridColor.getColorValue();
    }

    /**
     * @param gridColor
     */
    public void setGridColor(final Color gridColor) {
        this.m_gridColor.setColorValue(gridColor);
    }

    /**
     * @return showWarnings
     */
    public boolean getShowWarnings() {
        return m_showWarnings.getBooleanValue();
    }

    /**
     * @param showWarnings
     */
    public void setShowWarnings(final boolean showWarnings) {
        this.m_showWarnings.setBooleanValue(showWarnings);
    }

    /**
     * @return subscribeToSelection
     */
    public boolean getSubscribeToSelection() {
        return m_subscribeToSelection.getBooleanValue();
    }

    /**
     * @param subscribeToSelection
     */
    public void setSubscribeToSelection(final boolean subscribeToSelection) {
        this.m_subscribeToSelection.setBooleanValue(subscribeToSelection);
    }

    /**
     * @return publishSelection
     */
    public boolean getPublishSelection() {
        return m_publishSelection.getBooleanValue();
    }

    /**
     * @param publishSelection
     */
    public void setPublishSelection(final boolean publishSelection) {
        this.m_publishSelection.setBooleanValue(publishSelection);
    }

    /**
     * @return subscribeToFilters
     */
    public boolean getSubscribeToFilters() {
        return m_subscribeToFilters.getBooleanValue();
    }

    /**
     * @param subscribeToFilters
     */
    public void setSubscribeToFilters(final boolean subscribeToFilters) {
        this.m_subscribeToFilters.setBooleanValue(subscribeToFilters);
    }

    /**
     * @return showStaticLines
     */
    public boolean getShowStaticLine() {
        return m_showStaticLine.getBooleanValue();
    }

    /**
     * @param showStaticLine
     */
    public void setShowStaticLine(final boolean showStaticLine) {
        this.m_showStaticLine.setBooleanValue(showStaticLine);
    }

    /**
     * @return staticLineColor
     */
    public Color getStaticLineColor() {
        return m_staticLineColor.getColorValue();
    }

    /**
     * @param staticLineColor
     */
    public void setStaticLineColor(final Color staticLineColor) {
        this.m_staticLineColor.setColorValue(staticLineColor);
    }

    /**
     * @return staticLineWeight
     */
    public double getStaticLineWeight() {
        return m_staticLineWeight.getDoubleValue();
    }

    /**
     * @param staticLineWeight
     */
    public void setStaticLineWeight(final double staticLineWeight) {
        this.m_staticLineWeight.setDoubleValue(staticLineWeight);
    }

    /**
     * @return staticLineYValue
     */
    public double getStaticLineYValue() {
        return m_staticLineYValue.getDoubleValue();
    }

    /**
     * @param staticLineYValue
     */
    public void setStaticLineYValue(final double staticLineYValue) {
        this.m_staticLineYValue.setDoubleValue(staticLineYValue);
    }

    /**
     * @return enableSelection
     */
    public boolean getEnableSelection() {
        return m_enableSelection.getBooleanValue();
    }

    /**
     * @param enableSelection
     */
    public void setEnableSelection(final boolean enableSelection) {
        this.m_enableSelection.setBooleanValue(enableSelection);
    }

    /**
     * @return enableInteractiveCtrls
     */
    public boolean getEnableInteractiveCtrls() {
        return m_enableInteractiveCtrls.getBooleanValue();
    }

    /**
     * @param enableInteractiveCtrls
     */
    public void setEnableInteractiveCtrls(final boolean enableInteractiveCtrls) {
        this.m_enableInteractiveCtrls.setBooleanValue(enableInteractiveCtrls);
    }

    /**
     * @return enableMouseCrosshair
     */
    public boolean getEnableMouseCrosshair() {
        return m_enableMouseCrosshair.getBooleanValue();
    }

    /**
     * @param enableMouseCrosshair
     */
    public void setEnableMouseCrosshair(final boolean enableMouseCrosshair) {
        this.m_enableMouseCrosshair.setBooleanValue(enableMouseCrosshair);
    }

    /**
     * @return enablePanning
     */
    public boolean getEnablePanning() {
        return m_enablePanning.getBooleanValue();
    }

    /**
     * @param enablePanning
     */
    public void setEnablePanning(final boolean enablePanning) {
        this.m_enablePanning.setBooleanValue(enablePanning);
    }

    /**
     * @return enableScrollZoom
     */
    public boolean getEnableScrollZoom() {
        return m_enableScrollZoom.getBooleanValue();
    }

    /**
     * @param enableScrollZoom
     */
    public void setEnableScrollZoom(final boolean enableScrollZoom) {
        this.m_enableScrollZoom.setBooleanValue(enableScrollZoom);
    }

    /**
     * @return enableDragZoom
     */
    public boolean getEnableDragZoom() {
        return m_enableDragZoom.getBooleanValue();
    }

    /**
     * @param enableDragZoom
     */
    public void setEnableDragZoom(final boolean enableDragZoom) {
        this.m_enableDragZoom.setBooleanValue(enableDragZoom);
    }

    /**
     * @return showZoomReset
     */
    public boolean getShowZoomReset() {
        return m_showZoomReset.getBooleanValue();
    }

    /**
     * @param showZoomReset
     */
    public void setShowZoomReset(final boolean showZoomReset) {
        this.m_showZoomReset.setBooleanValue(showZoomReset);
    }

    /**
     * @return enableTitleControls
     */
    public boolean getEnableTitleControls() {
        return m_enableTitleControls.getBooleanValue();
    }

    /**
     * @param enableTitleControls
     */
    public void setEnableTitleControls(final boolean enableTitleControls) {
        this.m_enableTitleControls.setBooleanValue(enableTitleControls);
    }

    /**
     * @return enableAxisLabelControls
     */
    public boolean getEnableAxisLabelControls() {
        return m_enableAxisLabelControls.getBooleanValue();
    }

    /**
     * @param enableAxisLabelControls
     */
    public void setEnableAxisLabelControls(final boolean enableAxisLabelControls) {
        this.m_enableAxisLabelControls.setBooleanValue(enableAxisLabelControls);
    }

    /**
     * @return enablePDPControls
     */
    public boolean getEnablePDPControls() {
        return m_enablePDPControls.getBooleanValue();
    }

    /**
     * @param enablePDPControls
     */
    public void setEnablePDPControls(final boolean enablePDPControls) {
        this.m_enablePDPControls.setBooleanValue(enablePDPControls);
    }

    /**
     * @return enablePDPMarginControls
     */
    public boolean getEnablePDPMarginControls() {
        return m_enablePDPMarginControls.getBooleanValue();
    }

    /**
     * @param enablePDPMarginControls
     */
    public void setEnablePDPMarginControls(final boolean enablePDPMarginControls) {
        this.m_enablePDPMarginControls.setBooleanValue(enablePDPMarginControls);
    }

    /**
     * @return enableICEControls
     */
    public boolean getEnableICEControls() {
        return m_enableICEControls.getBooleanValue();
    }

    /**
     * @param enableICEControls
     */
    public void setEnableICEControls(final boolean enableICEControls) {
        this.m_enableICEControls.setBooleanValue(enableICEControls);
    }

    /**
     * @return enableStaticLineControls
     */
    public boolean getEnableStaticLineControls() {
        return m_enableStaticLineControls.getBooleanValue();
    }

    /**
     * @param enableStaticLineControls
     */
    public void setEnableStaticLineControls(final boolean enableStaticLineControls) {
        this.m_enableStaticLineControls.setBooleanValue(enableStaticLineControls);
    }

    /**
     * @return enableDataPointControls
     */
    public boolean getEnableDataPointControls() {
        return m_enableDataPointControls.getBooleanValue();
    }

    /**
     * @param enableDataPointControls
     */
    public void setEnableDataPointControls(final boolean enableDataPointControls) {
        this.m_enableDataPointControls.setBooleanValue(enableDataPointControls);
    }

    /**
     * @return enableSelectionFilterControls
     */
    public boolean getEnableSelectionFilterControls() {
        return m_enableSelectionFilterControls.getBooleanValue();
    }

    /**
     * @param enableSelectionFilterControls
     */
    public void setEnableSelectionFilterControls(final boolean enableSelectionFilterControls) {
        this.m_enableSelectionFilterControls.setBooleanValue(enableSelectionFilterControls);
    }

    /**
     * @return enableSelectionControls
     */
    public boolean getEnableSelectionControls() {
        return m_enableSelectionControls.getBooleanValue();
    }

    /**
     * @param enableSelectionControls
     */
    public void setEnableSelectionControls(final boolean enableSelectionControls) {
        this.m_enableSelectionControls.setBooleanValue(enableSelectionControls);
    }

    /**
     * @return enableYAxisMarginControls
     */
    public boolean getEnableYAxisMarginControls() {
        return m_enableYAxisMarginControls.getBooleanValue();
    }

    /**
     * @param enableYAxisMarginControls
     */
    public void setEnableYAxisMarginControls(final boolean enableYAxisMarginControls) {
        this.m_enableYAxisMarginControls.setBooleanValue(enableYAxisMarginControls);
    }

    /**
     * @return enableSmartZoomControls
     */
    public boolean getEnableSmartZoomControls() {
        return m_enableSmartZoomControls.getBooleanValue();
    }

    /**
     * @param enableSmartZoomControls
     */
    public void setEnableSmartZoomControls(final boolean enableSmartZoomControls) {
        this.m_enableSmartZoomControls.setBooleanValue(enableSmartZoomControls);
    }

    /**
     * @return enableGridControls
     */
    public boolean getEnableGridControls() {
        return m_enableGridControls.getBooleanValue();
    }

    /**
     * @param enableGridControls
     */
    public void setEnableGridControls(final boolean enableGridControls) {
        this.m_enableGridControls.setBooleanValue(enableGridControls);
    }

    /**
     * @return enableMouseCrosshairControls
     */
    public boolean getEnableMouseCrosshairControls() {
        return m_enableMouseCrosshairControls.getBooleanValue();
    }

    /**
     * @param enableMouseCrosshairControls
     */
    public void setEnableMouseCrosshairControls(final boolean enableMouseCrosshairControls) {
        this.m_enableMouseCrosshairControls.setBooleanValue(enableMouseCrosshairControls);
    }

    /**
     * @return enableAdvancedOptionsControls
     */
    public boolean getEnableAdvancedOptionsControls() {
        return m_enableAdvancedOptionsControls.getBooleanValue();
    }

    /**
     * @param enableAdvancedOptionsControls
     */
    public void setEnableAdvancedOptionsControls(final boolean enableAdvancedOptionsControls) {
        this.m_enableAdvancedOptionsControls.setBooleanValue(enableAdvancedOptionsControls);
    }

    /**
     * @return Custom CSS String
     */
    public String getCustomCSS() {
        return m_customCSS.getStringValue();
    }

    /**
     * @param customCSS
     */
    public void setCustomCSS(final String customCSS) {
        this.m_customCSS.setStringValue(customCSS);
    }

    /**
     * @return runningInView
     */
    public boolean getRunningInView() {
        return m_runningInView.getBooleanValue();
    }

    /**
     * @param runningInView
     */
    public void setRunningInView(final boolean runningInView) {
        this.m_runningInView.setBooleanValue(runningInView);
    }

    /**
     * @param color
     * @return RGBAString
     */
    public static String getRGBAStringFromColor(final Color color) {
        if (color == null) {
            return null;
        }
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();
        double a = color.getAlpha() / 255.0;
        StringBuilder builder = new StringBuilder(COLOR_STRING_PREFIX);
        builder.append(r);
        builder.append(",");
        builder.append(g);
        builder.append(",");
        builder.append(b);
        builder.append(",");
        builder.append(a);
        builder.append(")");
        return builder.toString();
    }

    /**
     * @param rgbaString
     * @return Color
     * @throws InvalidSettingsException
     */
    public static Color getColorFromString(final String rgbaString) throws InvalidSettingsException {
        if (rgbaString == null) {
            return null;
        }
        String error = "Could not parse color string: " + rgbaString;
        if (!rgbaString.startsWith(COLOR_STRING_PREFIX)) {
            throw new InvalidSettingsException(error);
        }
        String colorSubstring = rgbaString.substring(COLOR_STRING_PREFIX.length(), rgbaString.length() - 1);
        String[] colorComponents = colorSubstring.split(",");
        if (colorComponents.length != 4) {
            throw new InvalidSettingsException(error);
        }
        try {
            int r = Integer.parseInt(colorComponents[0]);
            int g = Integer.parseInt(colorComponents[1]);
            int b = Integer.parseInt(colorComponents[2]);
            int a = (int)Math.round(Double.parseDouble(colorComponents[3]) * 255);
            return new Color(r, g, b, a);
        } catch (NullPointerException | NumberFormatException e) {
            throw new InvalidSettingsException(error);
        }
    }

    /**
     * @param settings
     */
    public void saveSettings(final NodeSettingsWO settings) {
        m_hideInWizard.saveSettingsTo(settings);
        m_generateImage.saveSettingsTo(settings);
        m_maxNumRows.saveSettingsTo(settings);
        m_featureCol.saveSettingsTo(settings);
        m_rowIDCol.saveSettingsTo(settings);
        m_predictionCol.saveSettingsTo(settings);
        m_origFeatureCol.saveSettingsTo(settings);
        m_showPDP.saveSettingsTo(settings);
        settings.addString(CFG_PDP_COLOR, getRGBAStringFromColor(m_PDPColor.getColorValue()));
        m_PDPLineWeight.saveSettingsTo(settings);
        m_showPDPMargin.saveSettingsTo(settings);
        m_PDPMarginType.saveSettingsTo(settings);
        m_PDPMarginMultiplier.saveSettingsTo(settings);
        m_PDPMarginAlphaVal.saveSettingsTo(settings);
        m_showICE.saveSettingsTo(settings);
        settings.addString(CFG_ICE_COLOR, getRGBAStringFromColor(m_ICEColor.getColorValue()));
        m_ICEWeight.saveSettingsTo(settings);
        m_ICEAlphaVal.saveSettingsTo(settings);
        m_showDataPoints.saveSettingsTo(settings);
        settings.addString(CFG_DATA_POINT_COLOR, getRGBAStringFromColor(m_dataPointColor.getColorValue()));
        m_dataPointWeight.saveSettingsTo(settings);
        m_dataPointAlphaVal.saveSettingsTo(settings);
        m_xAxisMin.saveSettingsTo(settings);
        m_xAxisMax.saveSettingsTo(settings);
        m_yAxisMin.saveSettingsTo(settings);
        m_yAxisMax.saveSettingsTo(settings);
        m_xAxisLabel.saveSettingsTo(settings);
        m_yAxisLabel.saveSettingsTo(settings);
        m_chartTitle.saveSettingsTo(settings);
        m_chartSubtitle.saveSettingsTo(settings);
        m_viewWidth.saveSettingsTo(settings);
        m_viewHeight.saveSettingsTo(settings);
        m_resizeToFill.saveSettingsTo(settings);
        m_yAxisMargin.saveSettingsTo(settings);
        m_fullscreenButton.saveSettingsTo(settings);
        settings.addString(CFG_BACKGROUND_COLOR, getRGBAStringFromColor(m_backgroundColor.getColorValue()));
        settings.addString(CFG_DATA_AREA_COLOR, getRGBAStringFromColor(m_dataAreaColor.getColorValue()));
        m_showGrid.saveSettingsTo(settings);
        settings.addString(CFG_GRID_COLOR, getRGBAStringFromColor(m_gridColor.getColorValue()));
        m_showWarnings.saveSettingsTo(settings);
        m_subscribeToSelection.saveSettingsTo(settings);
        m_publishSelection.saveSettingsTo(settings);
        m_subscribeToFilters.saveSettingsTo(settings);
        m_showStaticLine.saveSettingsTo(settings);
        settings.addString(CFG_STATIC_LINE_COLOR, getRGBAStringFromColor(m_staticLineColor.getColorValue()));
        m_staticLineWeight.saveSettingsTo(settings);
        m_staticLineYValue.saveSettingsTo(settings);
        m_enableSelection.saveSettingsTo(settings);
        m_enableInteractiveCtrls.saveSettingsTo(settings);
        m_enableMouseCrosshair.saveSettingsTo(settings);
        m_enablePanning.saveSettingsTo(settings);
        m_enableScrollZoom.saveSettingsTo(settings);
        m_enableDragZoom.saveSettingsTo(settings);
        m_showZoomReset.saveSettingsTo(settings);
        m_enableTitleControls.saveSettingsTo(settings);
        m_enableAxisLabelControls.saveSettingsTo(settings);
        m_enablePDPControls.saveSettingsTo(settings);
        m_enablePDPMarginControls.saveSettingsTo(settings);
        m_enableICEControls.saveSettingsTo(settings);
        m_enableStaticLineControls.saveSettingsTo(settings);
        m_enableDataPointControls.saveSettingsTo(settings);
        m_enableSelectionFilterControls.saveSettingsTo(settings);
        m_enableSelectionControls.saveSettingsTo(settings);
        m_enableYAxisMarginControls.saveSettingsTo(settings);
        m_enableSmartZoomControls.saveSettingsTo(settings);
        m_enableGridControls.saveSettingsTo(settings);
        m_enableMouseCrosshairControls.saveSettingsTo(settings);
        m_enableAdvancedOptionsControls.saveSettingsTo(settings);
        m_customCSS.saveSettingsTo(settings);
        m_runningInView.saveSettingsTo(settings);
    }

    /**
     * @param settings
     * @throws InvalidSettingsException
     */
    public void loadSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        setHideInWizard(settings.getBoolean(CFG_HIDE_IN_WIZARD));
        setGenerateImage(settings.getBoolean(CFG_GENERATE_IMAGE));
        setMaxNumRows(settings.getInt(CFG_MAX_NUM_ROWS));
        setFeatureCol(settings.getString(CFG_FEATURE_COLUMN));
        setRowIDCol(settings.getString(CFG_ROW_ID_COLUMN));
        setPredictionCol(settings.getString(CFG_PREDICTION_COLUMN));
        setOrigFeatureCol(settings.getString(CFG_ORIGINAL_FEATURE_COLUMN));
        setShowPDP(settings.getBoolean(CFG_SHOW_PDP));
        setPDPColor(getColorFromString(settings.getString(CFG_PDP_COLOR)));
        setPDPLineWeight(settings.getDouble(CFG_PDP_LINE_WEIGHT));
        setShowPDPMargin(settings.getBoolean(CFG_SHOW_PDP_MARGIN));
        setPDPMarginType(settings.getString(CFG_PDP_MARGIN_TYPE));
        setPDPMarginMultiplier(settings.getDouble(CFG_PDP_MARGIN_MULTIPLIER));
        setPDPMarginAlphaVal(settings.getDouble(CFG_PDP_MARGIN_ALPHA_VAL));
        setShowICE(settings.getBoolean(CFG_SHOW_ICE));
        setICEColor(getColorFromString(settings.getString(CFG_ICE_COLOR)));
        setICEWeight(settings.getDouble(CFG_ICE_WEIGHT));
        setICEAlphaVal(settings.getDouble(CFG_ICE_ALPHA_VAL));
        setShowDataPoints(settings.getBoolean(CFG_SHOW_DATA_POINTS));
        setDataPointColor(getColorFromString(settings.getString(CFG_DATA_POINT_COLOR)));
        setDataPointWeight(settings.getDouble(CFG_DATA_POINT_WEIGHT));
        setDataPointAlphaVal(settings.getDouble(CFG_DATA_POINT_ALPHA_VAL));
        setXAxisMin(settings.getDouble(CFG_X_AXIS_MIN));
        setXAxisMax(settings.getDouble(CFG_X_AXIS_MAX));
        setYAxisMin(settings.getDouble(CFG_Y_AXIS_MIN));
        setYAxisMax(settings.getDouble(CFG_Y_AXIS_MAX));
        setXAxisLabel(settings.getString(CFG_X_AXIS_LABEL));
        setYAxisLabel(settings.getString(CFG_Y_AXIS_LABEL));
        setChartTitle(settings.getString(CFG_CHART_TITLE));
        setChartSubtitle(settings.getString(CFG_CHART_SUBTITLE));
        setViewWidth(settings.getInt(CFG_VIEW_WIDTH));
        setViewHeight(settings.getInt(CFG_VIEW_HEIGHT));
        setYAxisMargin(settings.getDouble(CFG_Y_AXIS_MARGIN));
        setResizeToFill(settings.getBoolean(CFG_RESIZE_TO_FILL));
        setFullscreenButton(settings.getBoolean(CFG_FULLSCREEN_BUTTON));
        setBackgroundColor(getColorFromString(settings.getString(CFG_BACKGROUND_COLOR)));
        setDataAreaColor(getColorFromString(settings.getString(CFG_DATA_AREA_COLOR)));
        setShowGrid(settings.getBoolean(CFG_SHOW_GRID));
        setGridColor(getColorFromString(settings.getString(CFG_GRID_COLOR)));
        setShowWarnings(settings.getBoolean(CFG_SHOW_WARNINGS));
        setSubscribeToSelection(settings.getBoolean(CFG_SUBSCRIBE_TO_SELECTION));
        setPublishSelection(settings.getBoolean(CFG_PUBLISH_SELECTION));
        setSubscribeToFilters(settings.getBoolean(CFG_SUBSCRIBE_TO_FILTERS));
        setShowStaticLine(settings.getBoolean(CFG_SHOW_STATIC_THRESHOLD_LINE));
        setStaticLineColor(getColorFromString(settings.getString(CFG_STATIC_LINE_COLOR)));
        setStaticLineWeight(settings.getDouble(CFG_STATIC_LINE_WEIGHT));
        setStaticLineYValue(settings.getDouble(CFG_STATIC_LINE_Y_VALUE));
        setEnableSelection(settings.getBoolean(CFG_ENABLE_SELECTION));
        setEnableInteractiveCtrls(settings.getBoolean(CFG_ENABLE_INTERACTIVE_CTRLS));
        setEnableMouseCrosshair(settings.getBoolean(CFG_ENABLE_MOUSE_CROSSHAIR));
        setEnablePanning(settings.getBoolean(CFG_ENABLE_PANNING));
        setEnableScrollZoom(settings.getBoolean(CFG_ENABLE_SCROLL_ZOOM));
        setEnableDragZoom(settings.getBoolean(CFG_ENABLE_DRAG_ZOOM));
        setShowZoomReset(settings.getBoolean(CFG_SHOW_ZOOM_RESET));
        setEnableTitleControls(settings.getBoolean(CFG_ENABLE_TITLE_CONTROLS));
        setEnableAxisLabelControls(settings.getBoolean(CFG_ENABLE_AXIS_LABEL_CONTROLS));
        setEnablePDPControls(settings.getBoolean(CFG_ENABLE_PDP_CONTROLS));
        setEnablePDPMarginControls(settings.getBoolean(CFG_ENABLE_PDP_MARGIN_CONTROLS));
        setEnableICEControls(settings.getBoolean(CFG_ENABLE_ICE_CONTROLS));
        setEnableStaticLineControls(settings.getBoolean(CFG_ENABLE_STATIC_LINE_CONTROLS));
        setEnableDataPointControls(settings.getBoolean(CFG_ENABLE_DATA_POINT_CONTROLS));
        setEnableSelectionFilterControls(settings.getBoolean(CFG_ENABLE_SELECTION_FILTER_CONTROLS));
        setEnableSelectionControls(settings.getBoolean(CFG_ENABLE_SELECTION_CONTROLS));
        setEnableYAxisMarginControls(settings.getBoolean(CFG_ENABLE_Y_AXIS_MARGIN_CONTROLS));
        setEnableSmartZoomControls(settings.getBoolean(CFG_ENABLE_SMART_ZOOM_CONTROLS));
        setEnableGridControls(settings.getBoolean(CFG_ENABLE_GRID_CONTROLS));
        setEnableMouseCrosshairControls(settings.getBoolean(CFG_ENABLE_MOUSE_CROSSHAIR_CONTROLS));
        setEnableAdvancedOptionsControls(settings.getBoolean(CFG_ENABLE_ADVANCED_OPTIONS_CONTROLS));
        setCustomCSS(settings.getString(CFG_CSS_CUSTOM));
        setRunningInView(settings.getBoolean(CFG_RUNNING_IN_VIEW));
    }

    /**
     * @param settings
     * @param spec
     * @throws InvalidSettingsException
     */
    public void loadSettingsForDialog(final NodeSettingsRO settings, final DataTableSpec spec)
        throws InvalidSettingsException {
        setGenerateImage(settings.getBoolean(CFG_GENERATE_IMAGE, DEFAULT_GENERATE_IMAGE));
        setMaxNumRows(settings.getInt(CFG_MAX_NUM_ROWS, DEFAULT_MAX_NUM_ROWS));
        setFeatureCol(settings.getString(CFG_FEATURE_COLUMN, DEFAULT_FEATURE_COLUMN));
        setRowIDCol(settings.getString(CFG_ROW_ID_COLUMN, DEFAULT_ROW_ID_COLUMN));
        setPredictionCol(settings.getString(CFG_PREDICTION_COLUMN, DEFAULT_PREDICTION_COLUMN));
        setOrigFeatureCol(settings.getString(CFG_ORIGINAL_FEATURE_COLUMN, DEFAULT_ORIGINAL_FEATURE_COLUMN));
        setShowPDP(settings.getBoolean(CFG_SHOW_PDP, DEFAULT_SHOW_PDP));
        setPDPColor(getColorFromString(settings.getString(CFG_PDP_COLOR) == null
            ? getRGBAStringFromColor(DEFAULT_PDP_COLOR) : settings.getString(CFG_PDP_COLOR)));
        setPDPLineWeight(settings.getDouble(CFG_PDP_LINE_WEIGHT, DEFAULT_PDP_LINE_WEIGHT));
        setShowPDPMargin(settings.getBoolean(CFG_SHOW_PDP_MARGIN, DEFAULT_SHOW_PDP_MARGIN));
        setPDPMarginType(settings.getString(CFG_PDP_MARGIN_TYPE, DEFAULT_PDP_MARGIN_TYPE));
        setPDPMarginMultiplier(settings.getDouble(CFG_PDP_MARGIN_MULTIPLIER, DEFAULT_PDP_MARGIN_MULTIPLIER));
        setPDPMarginAlphaVal(settings.getDouble(CFG_PDP_MARGIN_ALPHA_VAL, DEFAULT_PDP_MARGIN_ALPHA_VAL));
        setShowICE(settings.getBoolean(CFG_SHOW_ICE, DEFAULT_SHOW_ICE));
        setICEColor(getColorFromString(settings.getString(CFG_ICE_COLOR) == null
            ? getRGBAStringFromColor(DEFAULT_PDP_COLOR) : settings.getString(CFG_ICE_COLOR)));
        setICEWeight(settings.getDouble(CFG_ICE_WEIGHT, DEFAULT_ICE_WEIGHT));
        setICEAlphaVal(settings.getDouble(CFG_ICE_ALPHA_VAL, DEFAULT_ICE_ALPHA_VAL));
        setShowDataPoints(settings.getBoolean(CFG_SHOW_DATA_POINTS, DEFAULT_SHOW_DATA_POINTS));
        setDataPointColor(getColorFromString(settings.getString(CFG_DATA_POINT_COLOR) == null
            ? getRGBAStringFromColor(DEFAULT_DATA_POINT_COLOR) : settings.getString(CFG_DATA_POINT_COLOR)));
        setDataPointWeight(settings.getDouble(CFG_DATA_POINT_WEIGHT, DEFAULT_DATA_POINT_WEIGHT));
        setDataPointAlphaVal(settings.getDouble(CFG_DATA_POINT_ALPHA_VAL, DEFAULT_DATA_POINT_ALPHA_VAL));
        setXAxisLabel(settings.getString(CFG_X_AXIS_LABEL, DEFAULT_X_AXIS_LABEL));
        setYAxisLabel(settings.getString(CFG_Y_AXIS_LABEL, DEFAULT_Y_AXIS_LABEL));
        setChartTitle(settings.getString(CFG_CHART_TITLE, DEFAULT_CHART_TITLE));
        setChartSubtitle(settings.getString(CFG_CHART_SUBTITLE, DEFAULT_CHART_SUBTITLE));
        setViewWidth(settings.getInt(CFG_VIEW_WIDTH, DEFAULT_VIEW_WIDTH));
        setViewHeight(settings.getInt(CFG_VIEW_HEIGHT, DEFAULT_VIEW_HEIGHT));
        setYAxisMargin(settings.getDouble(CFG_Y_AXIS_MARGIN, DEFAULT_Y_AXIS_MARGIN));
        setResizeToFill(settings.getBoolean(CFG_RESIZE_TO_FILL, DEFAULT_RESIZE_TO_FILL));
        setFullscreenButton(settings.getBoolean(CFG_FULLSCREEN_BUTTON, DEFAULT_FULLSCREEN_BUTTON));
        setBackgroundColor(getColorFromString(settings.getString(CFG_BACKGROUND_COLOR) == null
            ? getRGBAStringFromColor(DEFAULT_BACKGROUND_COLOR) : settings.getString(CFG_BACKGROUND_COLOR)));
        setDataAreaColor(getColorFromString(settings.getString(CFG_DATA_AREA_COLOR) == null
            ? getRGBAStringFromColor(DEFAULT_DATA_AREA_COLOR) : settings.getString(CFG_DATA_AREA_COLOR)));
        setShowGrid(settings.getBoolean(CFG_SHOW_GRID, DEFAULT_SHOW_GRID));
        setGridColor(getColorFromString(settings.getString(CFG_GRID_COLOR) == null
            ? getRGBAStringFromColor(DEFAULT_GRID_COLOR) : settings.getString(CFG_GRID_COLOR)));
        setShowWarnings(settings.getBoolean(CFG_SHOW_WARNINGS, DEFAULT_SHOW_WARNINGS));
        setSubscribeToSelection(settings.getBoolean(CFG_SUBSCRIBE_TO_SELECTION, DEFAULT_SUBSCRIBE_TO_SELECTION));
        setPublishSelection(settings.getBoolean(CFG_PUBLISH_SELECTION, DEFAULT_PUBLISH_SELECTION));
        setSubscribeToFilters(settings.getBoolean(CFG_PUBLISH_SELECTION, DEFAULT_PUBLISH_SELECTION));
        setShowStaticLine(settings.getBoolean(CFG_SHOW_STATIC_THRESHOLD_LINE, DEFAULT_SHOW_STATIC_THRESHOLD_LINE));
        setStaticLineColor(getColorFromString(settings.getString(CFG_STATIC_LINE_COLOR)));
        setStaticLineWeight(settings.getDouble(CFG_STATIC_LINE_WEIGHT, DEFAULT_STATIC_LINE_WEIGHT));
        setStaticLineYValue(settings.getDouble(CFG_STATIC_LINE_Y_VALUE, DEFAULT_STATIC_LINE_Y_VALUE));
        setEnableSelection(settings.getBoolean(CFG_ENABLE_SELECTION, DEFAULT_ENABLE_SELECTION));
        setEnableInteractiveCtrls(settings.getBoolean(CFG_ENABLE_INTERACTIVE_CTRLS, DEFAULT_ENABLE_INTERACTIVE_CTRLS));
        setEnableMouseCrosshair(settings.getBoolean(CFG_ENABLE_MOUSE_CROSSHAIR, DEFAULT_ENABLE_MOUSE_CROSSHAIR));
        setEnablePanning(settings.getBoolean(CFG_ENABLE_PANNING, DEFAULT_ENABLE_PANNING));
        setEnableScrollZoom(settings.getBoolean(CFG_ENABLE_SCROLL_ZOOM, DEFAULT_ENABLE_SCROLL_ZOOM));
        setEnableDragZoom(settings.getBoolean(CFG_ENABLE_DRAG_ZOOM, DEFAULT_ENABLE_DRAG_ZOOM));
        setShowZoomReset(settings.getBoolean(CFG_SHOW_ZOOM_RESET, DEFAULT_SHOW_ZOOM_RESET));
        setEnableTitleControls(settings.getBoolean(CFG_ENABLE_TITLE_CONTROLS, DEFAULT_ENABLE_TITLE_CONTROLS));
        setEnableAxisLabelControls(settings.getBoolean(CFG_ENABLE_AXIS_LABEL_CONTROLS, DEFAULT_ENABLE_AXIS_LABEL_CONTROLS));
        setEnablePDPControls(settings.getBoolean(CFG_ENABLE_PDP_CONTROLS, DEFAULT_ENABLE_PDP_CONTROLS));
        setEnablePDPMarginControls(settings.getBoolean(CFG_ENABLE_PDP_MARGIN_CONTROLS, DEFAULT_ENABLE_PDP_MARGIN_CONTROLS));
        setEnableICEControls(settings.getBoolean(CFG_ENABLE_ICE_CONTROLS, DEFAULT_ENABLE_ICE_CONTROLS));
        setEnableStaticLineControls(settings.getBoolean(CFG_ENABLE_STATIC_LINE_CONTROLS, DEFAULT_ENABLE_STATIC_LINE_CONTROLS));
        setEnableDataPointControls(settings.getBoolean(CFG_ENABLE_DATA_POINT_CONTROLS, DEFAULT_ENABLE_DATA_POINT_CONTROLS));
        setEnableSelectionFilterControls(settings.getBoolean(CFG_ENABLE_SELECTION_FILTER_CONTROLS, DEFAULT_ENABLE_SELECTION_FILTER_CONTROLS));
        setEnableSelectionControls(settings.getBoolean(CFG_ENABLE_SELECTION_CONTROLS, DEFAULT_ENABLE_SELECTION_CONTROLS));
        setEnableYAxisMarginControls(settings.getBoolean(CFG_ENABLE_Y_AXIS_MARGIN_CONTROLS, DEFAULT_ENABLE_Y_AXIS_MARGIN_CONTROLS));
        setEnableSmartZoomControls(settings.getBoolean(CFG_ENABLE_SMART_ZOOM_CONTROLS, DEFAULT_ENABLE_SMART_ZOOM_CONTROLS));
        setEnableGridControls(settings.getBoolean(CFG_ENABLE_GRID_CONTROLS, DEFAULT_ENABLE_GRID_CONTROLS));
        setEnableMouseCrosshairControls(settings.getBoolean(CFG_ENABLE_MOUSE_CROSSHAIR_CONTROLS, DEFAULT_ENABLE_MOUSE_CROSSHAIR_CONTROLS));
        setEnableAdvancedOptionsControls(settings.getBoolean(CFG_ENABLE_ADVANCED_OPTIONS_CONTROLS, DEFAULT_ENABLE_ADVANCED_OPTIONS_CONTROLS));
        setCustomCSS(settings.getString(CFG_CSS_CUSTOM, DEFAULT_CUSTOM_CSS));
        setRunningInView(settings.getBoolean(CFG_RUNNING_IN_VIEW, DEFAULT_RUNNING_IN_VIEW));
    }
}
