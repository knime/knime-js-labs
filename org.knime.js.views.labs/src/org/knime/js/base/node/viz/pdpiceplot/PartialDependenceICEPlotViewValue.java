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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.js.core.JSONViewContent;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 *
 * @author Ben Laney, KNIME GmbH, Konstanz, Germany
 */
@JsonAutoDetect
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class PartialDependenceICEPlotViewValue extends JSONViewContent {

    static final String SELECTED_KEYS = "selectedKeys";

    static final String IMAGE = "image";

    static final String SMART_ZOOM_ENABLED = "smartZoomEnabled";

    static final String ZOOM_K = "zoomK";

    static final String ZOOM_X = "zoomX";

    static final String ZOOM_Y = "zoomY";

    static final String CURRENT_FEATURE = "currentFeature";

    static final String CURRENT_PREDICTION = "currentPrediction";

    private Boolean m_showPDP;

    private String m_PDPColor;

    private double m_PDPLineWeight;

    private Boolean m_showPDPMargin;

    private String m_PDPMarginType;

    private double m_PDPMarginMultiplier;

    private double m_PDPMarginAlphaVal;

    private Boolean m_showICE;

    private String m_ICEColor;

    private double m_ICEWeight;

    private double m_ICEAlphaVal;

    private boolean m_showDataPoints;

    private String m_dataPointColor;

    private double m_dataPointWeight;

    private double m_dataPointAlphaVal;

    private String m_xAxisLabel;

    private String m_yAxisLabel;

    private double m_xAxisMin;

    private double m_xAxisMax;

    private double m_yAxisMin;

    private double m_yAxisMax;

    private double m_yAxisMargin;

    private String m_chartTitle;

    private String m_chartSubtitle;

    private Boolean m_showGrid;

    private Boolean m_subscribeToSelection;

    private Boolean m_publishSelection;

    private Boolean m_subscribeToFilters;

    private Boolean m_showStaticLine;

    private String m_staticLineColor;

    private double m_staticLineWeight;

    private double m_staticLineYValue;

    private Boolean m_enableMouseCrosshair;

    private String m_backgroundColor;

    private String m_dataAreaColor;

    private String m_gridColor;

    private String[] m_selected;

    private String m_image;

    private int m_featureInd;

    private int m_predictionInd;

    private boolean m_smartZoomEnabled = true;

    private double m_zoomK = 1;

    private double m_zoomX = 0;

    private double m_zoomY = 0;

    private String m_currentFeature = "";

    private String m_currentPrediction = "";

    /**
     * @return the showPDP
     */
    public Boolean getShowPDP() {
        return m_showPDP;
    }

    /**
     * @param showPDP the showPDP to set
     */
    public void setShowPDP(final Boolean showPDP) {
        this.m_showPDP = showPDP;
    }

    /**
     * @return the pDPColor
     */
    public String getPDPColor() {
        return m_PDPColor;
    }

    /**
     * @param pDPColor the pDPColor to set
     */
    public void setPDPColor(final String pDPColor) {
        m_PDPColor = pDPColor;
    }

    /**
     * @return the pDPLineWeight
     */
    public double getPDPLineWeight() {
        return m_PDPLineWeight;
    }

    /**
     * @param pDPLineWeight the pDPLineWeight to set
     */
    public void setPDPLineWeight(final double pDPLineWeight) {
        m_PDPLineWeight = pDPLineWeight;
    }

    /**
     * @return the showPDPMargin
     */
    public Boolean getShowPDPMargin() {
        return m_showPDPMargin;
    }

    /**
     * @param showPDPMargin the showPDPMargin to set
     */
    public void setShowPDPMargin(final Boolean showPDPMargin) {
        this.m_showPDPMargin = showPDPMargin;
    }

    /**
     * @return the pDPMarginType
     */
    public String getPDPMarginType() {
        return m_PDPMarginType;
    }

    /**
     * @param pDPMarginType the pDPMarginType to set
     */
    public void setPDPMarginType(final String pDPMarginType) {
        m_PDPMarginType = pDPMarginType;
    }

    /**
     * @return the pDPMarginMultiplier
     */
    public double getPDPMarginMultiplier() {
        return m_PDPMarginMultiplier;
    }

    /**
     * @param pDPMarginMultiplier the pDPMarginMultiplier to set
     */
    public void setPDPMarginMultiplier(final double pDPMarginMultiplier) {
        m_PDPMarginMultiplier = pDPMarginMultiplier;
    }

    /**
     * @return the pDPMarginAlphaVal
     */
    public double getPDPMarginAlphaVal() {
        return m_PDPMarginAlphaVal;
    }

    /**
     * @param pDPMarginAlphaVal the pDPMarginAlphaVal to set
     */
    public void setPDPMarginAlphaVal(final double pDPMarginAlphaVal) {
        m_PDPMarginAlphaVal = pDPMarginAlphaVal;
    }

    /**
     * @return the showICE
     */
    public Boolean getShowICE() {
        return m_showICE;
    }

    /**
     * @param showICE the showICE to set
     */
    public void setShowICE(final Boolean showICE) {
        this.m_showICE = showICE;
    }

    /**
     * @return the iCEColor
     */
    public String getICEColor() {
        return m_ICEColor;
    }

    /**
     * @param iCEColor the iCEColor to set
     */
    public void setICEColor(final String iCEColor) {
        m_ICEColor = iCEColor;
    }

    /**
     * @return the iCEWeight
     */
    public double getICEWeight() {
        return m_ICEWeight;
    }

    /**
     * @param iCEWeight the iCEWeight to set
     */
    public void setICEWeight(final double iCEWeight) {
        m_ICEWeight = iCEWeight;
    }

    /**
     * @return the iCEAlphaVal
     */
    public double getICEAlphaVal() {
        return m_ICEAlphaVal;
    }

    /**
     * @param iCEAlphaVal the iCEAlphaVal to set
     */
    public void setICEAlphaVal(final double iCEAlphaVal) {
        m_ICEAlphaVal = iCEAlphaVal;
    }

    /**
     * @return showDataPoints
     */
    public boolean getShowDataPoints() {
        return m_showDataPoints;
    }

    /**
     * @param showDataPoints
     */
    public void setShowDataPoints(final boolean showDataPoints) {
        this.m_showDataPoints = showDataPoints;
    }

    /**
     * @return dataPointColor
     */
    public String getDataPointColor() {
        return m_dataPointColor;
    }

    /**
     * @param dataPointColor
     */
    public void setDataPointColor(final String dataPointColor) {
        this.m_dataPointColor = dataPointColor;
    }

    /**
     * @return dataPointWeight
     */
    public double getDataPointWeight() {
        return m_dataPointWeight;
    }

    /**
     * @param dataPointWeight
     */
    public void setDataPointWeight(final double dataPointWeight) {
        this.m_dataPointWeight = dataPointWeight;
    }

    /**
     * @return dataPointAlphaVal
     */
    public double getDataPointAlphaVal() {
        return m_dataPointAlphaVal;
    }

    /**
     * @param dataPointAlphaVal
     */
    public void setDataPointAlphaVal(final double dataPointAlphaVal) {
        this.m_dataPointAlphaVal = dataPointAlphaVal;
    }

    /**
     * @return the xAxisLabel
     */
    public String getXAxisLabel() {
        return m_xAxisLabel;
    }

    /**
     * @param xAxisLabel the xAxisLabel to set
     */
    public void setXAxisLabel(final String xAxisLabel) {
        this.m_xAxisLabel = xAxisLabel;
    }

    /**
     * @return the yAxisLabel
     */
    public String getYAxisLabel() {
        return m_yAxisLabel;
    }

    /**
     * @param yAxisLabel the yAxisLabel to set
     */
    public void setYAxisLabel(final String yAxisLabel) {
        this.m_yAxisLabel = yAxisLabel;
    }

    /**
     * @return the xAxisMin
     */
    public double getXAxisMin() {
        return m_xAxisMin;
    }

    /**
     * @param xAxisMin the xAxisMin to set
     */
    public void setXAxisMin(final double xAxisMin) {
        this.m_xAxisMin = xAxisMin;
    }

    /**
     * @return the xAxisMax
     */
    public double getXAxisMax() {
        return m_xAxisMax;
    }

    /**
     * @param xAxisMax the xAxisMax to set
     */
    public void setXAxisMax(final double xAxisMax) {
        this.m_xAxisMax = xAxisMax;
    }

    /**
     * @return the yAxisMin
     */
    public double getYAxisMin() {
        return m_yAxisMin;
    }

    /**
     * @param yAxisMin the yAxisMin to set
     */
    public void setYAxisMin(final double yAxisMin) {
        this.m_yAxisMin = yAxisMin;
    }

    /**
     * @return the yAxisMax
     */
    public double getYAxisMax() {
        return m_yAxisMax;
    }

    /**
     * @param yAxisMax the yAxisMax to set
     */
    public void setYAxisMax(final double yAxisMax) {
        this.m_yAxisMax = yAxisMax;
    }

    /**
     * @return yAxisMargin
     */
    public double getYAxisMargin() {
        return m_yAxisMargin;
    }

    /**
     * @param yAxisMargin
     */
    public void setYAxisMargin(final double yAxisMargin) {
        this.m_yAxisMargin = yAxisMargin;
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
        this.m_chartTitle = chartTitle;
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
        this.m_chartSubtitle = chartSubtitle;
    }

    /**
     * @return the showGrid
     */
    public Boolean getShowGrid() {
        return m_showGrid;
    }

    /**
     * @param showGrid the showGrid to set
     */
    public void setShowGrid(final Boolean showGrid) {
        this.m_showGrid = showGrid;
    }

    /**
     * @return subscribeToSelection
     */
    public boolean getSubscribeToSelection() {
        return m_subscribeToSelection;
    }

    /**
     * @param subscribeToSelection
     */
    public void setSubscribeToSelection(final boolean subscribeToSelection) {
        this.m_subscribeToSelection = subscribeToSelection;
    }

    /**
     * @return publishSelection
     */
    public boolean getPublishSelection() {
        return m_publishSelection;
    }

    /**
     * @param publishSelection
     */
    public void setPublishSelection(final boolean publishSelection) {
        this.m_publishSelection = publishSelection;
    }

    /**
     * @return subscribeToFilters
     */
    public boolean getSubscribeToFilters() {
        return m_subscribeToFilters;
    }

    /**
     * @param subscribeToFilters
     */
    public void setSubscribeToFilters(final boolean subscribeToFilters) {
        this.m_subscribeToFilters = subscribeToFilters;
    }

    /**
     * @return the showStaticLine
     */
    public Boolean getShowStaticLine() {
        return m_showStaticLine;
    }

    /**
     * @param showStaticLine the showStaticLine to set
     */
    public void setShowStaticLine(final Boolean showStaticLine) {
        this.m_showStaticLine = showStaticLine;
    }

    /**
     * @return the staticLineColor
     */
    public String getStaticLineColor() {
        return m_staticLineColor;
    }

    /**
     * @param staticLineColor the staticLineColor to set
     */
    public void setStaticLineColor(final String staticLineColor) {
        this.m_staticLineColor = staticLineColor;
    }

    /**
     * @return the staticLineWeight
     */
    public double getStaticLineWeight() {
        return m_staticLineWeight;
    }

    /**
     * @param staticLineWeight the staticLineWeight to set
     */
    public void setStaticLineWeight(final double staticLineWeight) {
        this.m_staticLineWeight = staticLineWeight;
    }

    /**
     * @return the staticLineYValue
     */
    public double getStaticLineYValue() {
        return m_staticLineYValue;
    }

    /**
     * @param staticLineYValue the staticLineYValue to set
     */
    public void setStaticLineYValue(final double staticLineYValue) {
        this.m_staticLineYValue = staticLineYValue;
    }

    /**
     * @return the enableMouseCrosshair
     */
    public Boolean getEnableMouseCrosshair() {
        return m_enableMouseCrosshair;
    }

    /**
     * @param enableMouseCrosshair the enableMouseCrosshair to set
     */
    public void setEnableMouseCrosshair(final Boolean enableMouseCrosshair) {
        this.m_enableMouseCrosshair = enableMouseCrosshair;
    }

    /**
     * @return the backgroundColor
     */
    public String getBackgroundColor() {
        return m_backgroundColor;
    }

    /**
     * @param backgroundColor the backgroundColor to set
     */
    public void setBackgroundColor(final String backgroundColor) {
        this.m_backgroundColor = backgroundColor;
    }

    /**
     * @return the dataAreaColor
     */
    public String getDataAreaColor() {
        return m_dataAreaColor;
    }

    /**
     * @param dataAreaColor the dataAreaColor to set
     */
    public void setDataAreaColor(final String dataAreaColor) {
        this.m_dataAreaColor = dataAreaColor;
    }

    /**
     * @return the gridColor
     */
    public String getGridColor() {
        return m_gridColor;
    }

    /**
     * @param gridColor the gridColor to set
     */
    public void setGridColor(final String gridColor) {
        this.m_gridColor = gridColor;
    }

    /**
     * @return the selection
     */
    public String[] getSelected() {
        return m_selected;
    }

    /**
     * @param selected the selected to set
     */
    public void setSelected(final String[] selected) {
        m_selected = selected;
    }

    /**
     * @return the SVG image as a string
     */
    public String getImage() {
        return m_image;
    }

    /**
     * @param image as an SVG string to set the internal string representation of the image
     */
    public void setImage(final String image) {
        this.m_image = image;
    }

    /**
     * @return featureInd
     */
    public int getFeatureInd() {
        return m_featureInd;
    }

    /**
     * @param featureInd
     */
    public void setFeatureInd(final int featureInd) {
        this.m_featureInd = featureInd;
    }

    /**
     * @return predictionInd
     */
    public int getPredictionInd() {
        return m_predictionInd;
    }

    /**
     * @param predictionInd
     */
    public void setPredictionInd(final int predictionInd) {
        this.m_predictionInd = predictionInd;
    }

    /**
     * @return m_smartZoomEnabled
     */
    public boolean getSmartZoomEnabled() {
        return m_smartZoomEnabled;
    }

    /**
     * @param smartZoomEnabled
     */
    public void setSmartZoomEnabled(final boolean smartZoomEnabled) {
        this.m_smartZoomEnabled = smartZoomEnabled;
    }

    /**
     * @return the zoomK
     */
    public double getZoomK() {
        return m_zoomK;
    }

    /**
     * @param zoomK the zoomK to set
     */
    public void setZoomK(final double zoomK) {
        this.m_zoomK = zoomK;
    }

    /**
     * @return the zoomX
     */
    public double getZoomX() {
        return m_zoomX;
    }

    /**
     * @param zoomX the zoomX to set
     */
    public void setZoomX(final double zoomX) {
        this.m_zoomX = zoomX;
    }

    /**
     * @return the zoomY
     */
    public double getZoomY() {
        return m_zoomY;
    }

    /**
     * @param zoomY the zoomY to set
     */
    public void setZoomY(final double zoomY) {
        this.m_zoomY = zoomY;
    }

    /**
     * @param currentFeature the currentFeature to set
     */
    public void setCurrentFeature(final String currentFeature) {
        this.m_currentFeature = currentFeature;
    }

    /**
     * @return the currentFeature
     */
    public String getCurrentFeature() {
        return m_currentFeature;
    }

    /**
     * @param currentPrediction the currentPrediction to set
     */
    public void setCurrentPrediction(final String currentPrediction) {
        this.m_currentPrediction = currentPrediction;
    }

    /**
     * @return the currentPrediction
     */
    public String getCurrentPrediction() {
        return m_currentPrediction;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void saveToNodeSettings(final NodeSettingsWO settings) {
        settings.addBoolean(PartialDependenceICEPlotConfig.CFG_SHOW_PDP, m_showPDP);
        settings.addString(PartialDependenceICEPlotConfig.CFG_PDP_COLOR, m_PDPColor);
        settings.addDouble(PartialDependenceICEPlotConfig.CFG_PDP_LINE_WEIGHT, m_PDPLineWeight);
        settings.addBoolean(PartialDependenceICEPlotConfig.CFG_SHOW_PDP_MARGIN, m_showPDPMargin);
        settings.addString(PartialDependenceICEPlotConfig.CFG_PDP_MARGIN_TYPE, m_PDPMarginType);
        settings.addDouble(PartialDependenceICEPlotConfig.CFG_PDP_MARGIN_MULTIPLIER, m_PDPMarginMultiplier);
        settings.addDouble(PartialDependenceICEPlotConfig.CFG_PDP_MARGIN_ALPHA_VAL, m_PDPMarginAlphaVal);
        settings.addBoolean(PartialDependenceICEPlotConfig.CFG_SHOW_ICE, m_showICE);
        settings.addString(PartialDependenceICEPlotConfig.CFG_ICE_COLOR, m_ICEColor);
        settings.addDouble(PartialDependenceICEPlotConfig.CFG_ICE_WEIGHT, m_ICEWeight);
        settings.addDouble(PartialDependenceICEPlotConfig.CFG_ICE_ALPHA_VAL, m_ICEAlphaVal);
        settings.addBoolean(PartialDependenceICEPlotConfig.CFG_SHOW_DATA_POINTS, m_showDataPoints);
        settings.addString(PartialDependenceICEPlotConfig.CFG_DATA_POINT_COLOR, m_dataPointColor);
        settings.addDouble(PartialDependenceICEPlotConfig.CFG_DATA_POINT_WEIGHT, m_dataPointWeight);
        settings.addDouble(PartialDependenceICEPlotConfig.CFG_DATA_POINT_ALPHA_VAL, m_dataPointAlphaVal);
        settings.addString(PartialDependenceICEPlotConfig.CFG_X_AXIS_LABEL, m_xAxisLabel);
        settings.addString(PartialDependenceICEPlotConfig.CFG_Y_AXIS_LABEL, m_yAxisLabel);
        settings.addDouble(PartialDependenceICEPlotConfig.CFG_X_AXIS_MIN, m_xAxisMin);
        settings.addDouble(PartialDependenceICEPlotConfig.CFG_X_AXIS_MAX, m_xAxisMax);
        settings.addDouble(PartialDependenceICEPlotConfig.CFG_Y_AXIS_MIN, m_yAxisMin);
        settings.addDouble(PartialDependenceICEPlotConfig.CFG_Y_AXIS_MAX, m_yAxisMax);
        settings.addDouble(PartialDependenceICEPlotConfig.CFG_Y_AXIS_MARGIN, m_yAxisMargin);
        settings.addString(PartialDependenceICEPlotConfig.CFG_CHART_TITLE, m_chartTitle);
        settings.addString(PartialDependenceICEPlotConfig.CFG_CHART_SUBTITLE, m_chartSubtitle);
        settings.addBoolean(PartialDependenceICEPlotConfig.CFG_SHOW_GRID, m_showGrid);
        settings.addBoolean(PartialDependenceICEPlotConfig.CFG_SUBSCRIBE_TO_SELECTION, m_subscribeToSelection);
        settings.addBoolean(PartialDependenceICEPlotConfig.CFG_PUBLISH_SELECTION, m_publishSelection);
        settings.addBoolean(PartialDependenceICEPlotConfig.CFG_SUBSCRIBE_TO_FILTERS, m_subscribeToFilters);
        settings.addBoolean(PartialDependenceICEPlotConfig.CFG_SHOW_STATIC_THRESHOLD_LINE, m_showStaticLine);
        settings.addString(PartialDependenceICEPlotConfig.CFG_STATIC_LINE_COLOR, m_staticLineColor);
        settings.addDouble(PartialDependenceICEPlotConfig.CFG_STATIC_LINE_WEIGHT, m_staticLineWeight);
        settings.addDouble(PartialDependenceICEPlotConfig.CFG_STATIC_LINE_Y_VALUE, m_staticLineYValue);
        settings.addBoolean(PartialDependenceICEPlotConfig.CFG_ENABLE_MOUSE_CROSSHAIR, m_enableMouseCrosshair);
        settings.addString(PartialDependenceICEPlotConfig.CFG_BACKGROUND_COLOR, m_backgroundColor);
        settings.addString(PartialDependenceICEPlotConfig.CFG_DATA_AREA_COLOR, m_dataAreaColor);
        settings.addString(PartialDependenceICEPlotConfig.CFG_GRID_COLOR, m_gridColor);
        settings.addStringArray(PartialDependenceICEPlotViewValue.SELECTED_KEYS, m_selected);
        settings.addString(PartialDependenceICEPlotViewValue.IMAGE, m_image);
        settings.addInt(PartialDependenceICEPlotConfig.CFG_FEATURE_INDEX, m_featureInd);
        settings.addInt(PartialDependenceICEPlotConfig.CFG_PREDICTION_INDEX, m_predictionInd);
        settings.addBoolean(PartialDependenceICEPlotViewValue.SMART_ZOOM_ENABLED, m_smartZoomEnabled);
        settings.addDouble(PartialDependenceICEPlotViewValue.ZOOM_K, m_zoomK);
        settings.addDouble(PartialDependenceICEPlotViewValue.ZOOM_X, m_zoomX);
        settings.addDouble(PartialDependenceICEPlotViewValue.ZOOM_Y, m_zoomY);
        settings.addString(PartialDependenceICEPlotViewValue.CURRENT_FEATURE, m_currentFeature);
        settings.addString(PartialDependenceICEPlotViewValue.CURRENT_PREDICTION, m_currentPrediction);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadFromNodeSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        setShowPDP(settings.getBoolean(PartialDependenceICEPlotConfig.CFG_SHOW_PDP));
        setPDPColor(settings.getString(PartialDependenceICEPlotConfig.CFG_PDP_COLOR));
        setPDPLineWeight(settings.getDouble(PartialDependenceICEPlotConfig.CFG_PDP_LINE_WEIGHT));
        setShowPDPMargin(settings.getBoolean(PartialDependenceICEPlotConfig.CFG_SHOW_PDP_MARGIN));
        setPDPMarginType(settings.getString(PartialDependenceICEPlotConfig.CFG_PDP_MARGIN_TYPE));
        setPDPMarginMultiplier(settings.getDouble(PartialDependenceICEPlotConfig.CFG_PDP_MARGIN_MULTIPLIER));
        setPDPMarginAlphaVal(settings.getDouble(PartialDependenceICEPlotConfig.CFG_PDP_MARGIN_ALPHA_VAL));
        setShowICE(settings.getBoolean(PartialDependenceICEPlotConfig.CFG_SHOW_ICE));
        setICEColor(settings.getString(PartialDependenceICEPlotConfig.CFG_ICE_COLOR));
        setICEWeight(settings.getDouble(PartialDependenceICEPlotConfig.CFG_ICE_WEIGHT));
        setICEAlphaVal(settings.getDouble(PartialDependenceICEPlotConfig.CFG_ICE_ALPHA_VAL));
        setShowDataPoints(settings.getBoolean(PartialDependenceICEPlotConfig.CFG_SHOW_DATA_POINTS));
        setDataPointColor(settings.getString(PartialDependenceICEPlotConfig.CFG_DATA_POINT_COLOR));
        setDataPointWeight(settings.getDouble(PartialDependenceICEPlotConfig.CFG_DATA_POINT_WEIGHT));
        setDataPointAlphaVal(settings.getDouble(PartialDependenceICEPlotConfig.CFG_DATA_POINT_ALPHA_VAL));
        setXAxisLabel(settings.getString(PartialDependenceICEPlotConfig.CFG_X_AXIS_LABEL));
        setYAxisLabel(settings.getString(PartialDependenceICEPlotConfig.CFG_Y_AXIS_LABEL));
        setXAxisMin(settings.getDouble(PartialDependenceICEPlotConfig.CFG_X_AXIS_MIN));
        setXAxisMax(settings.getDouble(PartialDependenceICEPlotConfig.CFG_X_AXIS_MAX));
        setYAxisMin(settings.getDouble(PartialDependenceICEPlotConfig.CFG_Y_AXIS_MIN));
        setYAxisMax(settings.getDouble(PartialDependenceICEPlotConfig.CFG_Y_AXIS_MAX));
        setYAxisMargin(settings.getDouble(PartialDependenceICEPlotConfig.CFG_Y_AXIS_MARGIN));
        setChartTitle(settings.getString(PartialDependenceICEPlotConfig.CFG_CHART_TITLE));
        setChartSubtitle(settings.getString(PartialDependenceICEPlotConfig.CFG_CHART_SUBTITLE));
        setShowGrid(settings.getBoolean(PartialDependenceICEPlotConfig.CFG_SHOW_GRID));
        setSubscribeToSelection(settings.getBoolean(PartialDependenceICEPlotConfig.CFG_SUBSCRIBE_TO_SELECTION));
        setPublishSelection(settings.getBoolean(PartialDependenceICEPlotConfig.CFG_PUBLISH_SELECTION));
        setSubscribeToFilters(settings.getBoolean(PartialDependenceICEPlotConfig.CFG_SUBSCRIBE_TO_FILTERS));
        setShowStaticLine(settings.getBoolean(PartialDependenceICEPlotConfig.CFG_SHOW_STATIC_THRESHOLD_LINE));
        setStaticLineColor(settings.getString(PartialDependenceICEPlotConfig.CFG_STATIC_LINE_COLOR));
        setStaticLineWeight(settings.getDouble(PartialDependenceICEPlotConfig.CFG_STATIC_LINE_WEIGHT));
        setStaticLineYValue(settings.getDouble(PartialDependenceICEPlotConfig.CFG_STATIC_LINE_Y_VALUE));
        setEnableMouseCrosshair(settings.getBoolean(PartialDependenceICEPlotConfig.CFG_ENABLE_MOUSE_CROSSHAIR));
        setBackgroundColor(settings.getString(PartialDependenceICEPlotConfig.CFG_BACKGROUND_COLOR));
        setDataAreaColor(settings.getString(PartialDependenceICEPlotConfig.CFG_DATA_AREA_COLOR));
        setGridColor(settings.getString(PartialDependenceICEPlotConfig.CFG_GRID_COLOR));
        setSelected(settings.getStringArray(PartialDependenceICEPlotViewValue.SELECTED_KEYS, new String[0]));
        setImage(settings.getString(PartialDependenceICEPlotViewValue.IMAGE));
        setFeatureInd(settings.getInt(PartialDependenceICEPlotConfig.CFG_FEATURE_INDEX));
        setPredictionInd(settings.getInt(PartialDependenceICEPlotConfig.CFG_PREDICTION_INDEX));
        setSmartZoomEnabled(settings.getBoolean(PartialDependenceICEPlotViewValue.SMART_ZOOM_ENABLED));
        setZoomK(settings.getDouble(PartialDependenceICEPlotViewValue.ZOOM_K));
        setZoomX(settings.getDouble(PartialDependenceICEPlotViewValue.ZOOM_X));
        setZoomY(settings.getDouble(PartialDependenceICEPlotViewValue.ZOOM_Y));
        setCurrentFeature(settings.getString(PartialDependenceICEPlotViewValue.CURRENT_FEATURE));
        setCurrentPrediction(settings.getString(PartialDependenceICEPlotViewValue.CURRENT_PREDICTION));

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
        PartialDependenceICEPlotViewValue other = (PartialDependenceICEPlotViewValue)obj;
        return new EqualsBuilder().append(m_showPDP, other.m_showPDP).append(m_PDPColor, other.m_PDPColor)
            .append(m_PDPLineWeight, other.m_PDPLineWeight).append(m_showPDPMargin, other.m_showPDPMargin)
            .append(m_PDPMarginType, other.m_PDPMarginType).append(m_PDPMarginMultiplier, other.m_PDPMarginMultiplier)
            .append(m_PDPMarginAlphaVal, other.m_PDPMarginAlphaVal).append(m_showICE, other.m_showICE)
            .append(m_ICEColor, other.m_ICEColor).append(m_ICEWeight, other.m_ICEWeight)
            .append(m_ICEAlphaVal, other.m_ICEAlphaVal).append(m_showDataPoints, other.m_showDataPoints)
            .append(m_dataPointColor, other.m_dataPointColor).append(m_dataPointWeight, other.m_dataPointWeight)
            .append(m_dataPointAlphaVal, other.m_dataPointAlphaVal).append(m_xAxisLabel, other.m_xAxisLabel)
            .append(m_yAxisLabel, other.m_yAxisLabel).append(m_xAxisMin, other.m_xAxisMin)
            .append(m_xAxisMax, other.m_xAxisMax).append(m_yAxisMin, other.m_yAxisMin)
            .append(m_yAxisMax, other.m_yAxisMax).append(m_yAxisMargin, other.m_yAxisMargin)
            .append(m_chartTitle, other.m_chartTitle).append(m_chartSubtitle, other.m_chartSubtitle)
            .append(m_showGrid, other.m_showGrid).append(m_subscribeToSelection, other.m_subscribeToSelection)
            .append(m_publishSelection, other.m_publishSelection)
            .append(m_subscribeToFilters, other.m_subscribeToFilters).append(m_showStaticLine, other.m_showStaticLine)
            .append(m_staticLineColor, other.m_staticLineColor).append(m_staticLineWeight, other.m_staticLineWeight)
            .append(m_staticLineYValue, other.m_staticLineYValue)
            .append(m_enableMouseCrosshair, other.m_enableMouseCrosshair)
            .append(m_backgroundColor, other.m_backgroundColor).append(m_dataAreaColor, other.m_dataAreaColor)
            .append(m_gridColor, other.m_gridColor).append(m_image, other.m_image).append(m_selected, other.m_selected)
            .append(m_featureInd, other.m_featureInd).append(m_predictionInd, other.m_predictionInd)
            .append(m_smartZoomEnabled, other.m_smartZoomEnabled).append(m_zoomK, other.m_zoomK).append(m_zoomX, other.m_zoomX)
            .append(m_zoomY, other.m_zoomY).append(m_currentFeature, other.m_currentFeature).append(m_currentPrediction, other.m_currentPrediction)
            .isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(m_showPDP).append(m_PDPColor).append(m_PDPLineWeight)
            .append(m_showPDPMargin).append(m_PDPMarginType).append(m_PDPMarginMultiplier).append(m_PDPMarginAlphaVal)
            .append(m_showICE).append(m_ICEColor).append(m_ICEWeight).append(m_ICEAlphaVal).append(m_showDataPoints)
            .append(m_dataPointColor).append(m_dataPointWeight).append(m_dataPointAlphaVal).append(m_xAxisLabel)
            .append(m_yAxisLabel).append(m_xAxisMin).append(m_xAxisMax).append(m_yAxisMin).append(m_yAxisMax)
            .append(m_yAxisMargin).append(m_chartTitle).append(m_chartSubtitle).append(m_showGrid)
            .append(m_subscribeToSelection).append(m_publishSelection).append(m_subscribeToFilters)
            .append(m_showStaticLine).append(m_staticLineColor).append(m_staticLineWeight).append(m_staticLineYValue)
            .append(m_enableMouseCrosshair).append(m_backgroundColor).append(m_dataAreaColor).append(m_gridColor)
            .append(m_image).append(m_selected).append(m_featureInd).append(m_predictionInd)
            .append(m_smartZoomEnabled).append(m_zoomK).append(m_zoomX)
            .append(m_zoomY).append(m_currentFeature).append(m_currentPrediction)
            .toHashCode();
    }
}
