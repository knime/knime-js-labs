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
import org.knime.js.core.JSONDataTable;
import org.knime.js.core.JSONViewContent;
import org.knime.js.core.warnings.JSONWarnings;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;


/**
 *
 * @author Ben Laney, KNIME GmbH, Konstanz, Germany
 */
@JsonAutoDetect
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class PartialDependenceICEPlotNodeViewRepresentation extends JSONViewContent {

	private JSONDataTable m_table;

	private int m_maxNumRows;
	private Boolean m_generateImage;
	private String[] m_sampledFeatureColumns;
	private String  m_rowIDCol;
	private String[] m_predictionColumns;
	private double[][] m_featureDomains;
	private double[][] m_predictionDomains;
	private int[] m_predictionIndicies;
	private int m_viewWidth;
	private int m_viewHeight;
	private Boolean m_resizeToFill;
	private Boolean m_fullscreenButton;
	private Boolean m_enablePanning;
	private Boolean m_enableScrollZoom;
	private Boolean m_enableDragZoom;
	private Boolean m_showZoomReset;
	private Boolean m_enableSelection;
	private Boolean m_enableInteractiveCtrls;
    private Boolean m_enableTitleControls;
    private Boolean m_enableAxisLabelControls;
    private Boolean m_enablePDPControls;
    private Boolean m_enablePDPMarginControls;
    private Boolean m_enableICEControls;
    private Boolean m_enableStaticLineControls;
    private Boolean m_enableDataPointControls;
    private Boolean m_enableSelectionFilterControls;
    private Boolean m_enableSelectionControls;
    private Boolean m_enableYAxisMarginControls;
    private Boolean m_enableSmartZoomControls;
    private Boolean m_enableGridControls;
    private Boolean m_enableMouseCrosshairControls;
    private Boolean m_enableAdvancedOptionsControls;
    private Boolean m_showWarnings;
    private JSONWarnings m_JSONwarnings = new JSONWarnings();
    private Boolean m_runningInView;

    /**
     * @return the JSONDataTable
     */
    public JSONDataTable getDataTable() {
        return m_table;
    }

    /**
     * @param dataTable the JSONDataTable to set
     */
    public void setDataTable(final JSONDataTable dataTable) {
        this.m_table = dataTable;
    }

	/**
	 * @return the maxNumRows
	 */
	public int getMaxNumRows() {
		return m_maxNumRows;
	}

	/**
	 * @param maxNumRows the maxNumRows to set
	 */
	public void setMaxNumRows(final int maxNumRows) {
		this.m_maxNumRows = maxNumRows;
	}

	/**
	 * @return the generateImage
	 */
	public Boolean getGenerateImage() {
		return m_generateImage;
	}

	/**
	 * @param generateImage the generateImage to set
	 */
	public void setGenerateImage(final Boolean generateImage) {
		this.m_generateImage = generateImage;
	}

	/**
	 * @return the sampledFeatureColumns
	 */
	public String[] getSampledFeatureColumns() {
		return m_sampledFeatureColumns;
	}

	/**
	 * @param sampledFeatureColumns the sampledFeatureColumns to set
	 */
	public void setSampledFeatureColumns(final String[] sampledFeatureColumns) {
		this.m_sampledFeatureColumns = sampledFeatureColumns;
	}

	/**
	 * @return the rowIDCol
	 */
	public String getRowIDCol() {
		return m_rowIDCol;
	}

	/**
	 * @param rowIDCol the rowIDCol to set
	 */
	public void setRowIDCol(final String rowIDCol) {
		this.m_rowIDCol = rowIDCol;
	}

    /**
     * @return the predictionColumns
     */
    public String[] getPredictionColumns() {
        return m_predictionColumns;
    }

    /**
     * @param predictionColumns the predictionColumns to set
     */
    public void setPredictionColumns(final String[] predictionColumns) {
        this.m_predictionColumns = predictionColumns;
    }

    /**
     * @return the predictionIndicies
     */
    public int[] getPredictionIndicies() {
        return m_predictionIndicies;
    }

    /**
     * @return featureDomains
     */
    public double[][] getFeatureDomains() {
        return m_featureDomains;
    }

    /**
     * @param featureDomains
     */
    public void setFeatureDomains(final double[][] featureDomains) {
        this.m_featureDomains = featureDomains;
    }

    /**
     * @return predictionDomains
     */
    public double[][] getPredictionDomains() {
        return m_predictionDomains;
    }

    /**
     * @param predictionDomains
     */
    public void setPredictionDomains(final double[][] predictionDomains) {
        this.m_predictionDomains = predictionDomains;
    }

    /**
     * @param predictionIndicies the predictionIndicies to set
     */
    public void setPredictionIndicies(final int[] predictionIndicies) {
        this.m_predictionIndicies = predictionIndicies;
    }

	/**
	 * @return the viewWidth
	 */
	public int getViewWidth() {
		return m_viewWidth;
	}

	/**
	 * @param viewWidth the viewWidth to set
	 */
	public void setViewWidth(final int viewWidth) {
		this.m_viewWidth = viewWidth;
	}

	/**
	 * @return the viewHeight
	 */
	public int getViewHeight() {
		return m_viewHeight;
	}

	/**
	 * @param viewHeight the viewHeight to set
	 */
	public void setViewHeight(final int viewHeight) {
		this.m_viewHeight = viewHeight;
	}

	/**
	 * @return the resizeToFill
	 */
	public Boolean getResizeToFill() {
		return m_resizeToFill;
	}

	/**
	 * @param resizeToFill the resizeToFill to set
	 */
	public void setResizeToFill(final Boolean resizeToFill) {
		this.m_resizeToFill = resizeToFill;
	}

	/**
	 * @return the fullscreenButton
	 */
	public Boolean getFullscreenButton() {
		return m_fullscreenButton;
	}

	/**
	 * @param fullscreenButton the fullscreenButton to set
	 */
	public void setFullscreenButton(final Boolean fullscreenButton) {
		this.m_fullscreenButton = fullscreenButton;
	}

	/**
	 * @return the enablePanning
	 */
	public Boolean getEnablePanning() {
		return m_enablePanning;
	}

	/**
	 * @param enablePanning the enablePanning to set
	 */
	public void setEnablePanning(final Boolean enablePanning) {
		this.m_enablePanning = enablePanning;
	}

	/**
	 * @return the enableScrollZoom
	 */
	public Boolean getEnableScrollZoom() {
		return m_enableScrollZoom;
	}

	/**
	 * @param enableScrollZoom the enableScrollZoom to set
	 */
	public void setEnableScrollZoom(final Boolean enableScrollZoom) {
		this.m_enableScrollZoom = enableScrollZoom;
	}

	/**
	 * @return the enableDragZoom
	 */
	public Boolean getEnableDragZoom() {
		return m_enableDragZoom;
	}

	/**
	 * @param enableDragZoom the enableDragZoom to set
	 */
	public void setEnableDragZoom(final Boolean enableDragZoom) {
		this.m_enableDragZoom = enableDragZoom;
	}

	/**
	 * @return the showZoomReset
	 */
	public Boolean getShowZoomReset() {
		return m_showZoomReset;
	}

	/**
	 * @param showZoomReset the showZoomReset to set
	 */
	public void setShowZoomReset(final Boolean showZoomReset) {
		this.m_showZoomReset = showZoomReset;
	}

	/**
	 * @return the enableSelection
	 */
	public Boolean getEnableSelection() {
		return m_enableSelection;
	}

	/**
	 * @param enableSelection the enableSelection to set
	 */
	public void setEnableSelection(final Boolean enableSelection) {
		this.m_enableSelection = enableSelection;
	}

	/**
	 * @return the enableInteractiveCtrls
	 */
	public Boolean getEnableInteractiveCtrls() {
		return m_enableInteractiveCtrls;
	}

	/**
	 * @param enableInteractiveCtrls the enableInteractiveCtrls to set
	 */
	public void setEnableInteractiveCtrls(final Boolean enableInteractiveCtrls) {
		this.m_enableInteractiveCtrls = enableInteractiveCtrls;
	}

    /**
     * @return enableTitleControls
     */
    public boolean getEnableTitleControls() {
        return m_enableTitleControls;
    }

    /**
     * @param enableTitleControls
     */
    public void setEnableTitleControls(final boolean enableTitleControls) {
        this.m_enableTitleControls = enableTitleControls;
    }

    /**
     * @return enableAxisLabelControls
     */
    public boolean getEnableAxisLabelControls() {
        return m_enableAxisLabelControls;
    }

    /**
     * @param enableAxisLabelControls
     */
    public void setEnableAxisLabelControls(final boolean enableAxisLabelControls) {
        this.m_enableAxisLabelControls = enableAxisLabelControls;
    }

    /**
     * @return enablePDPControls
     */
    public boolean getEnablePDPControls() {
        return m_enablePDPControls;
    }

    /**
     * @param enablePDPControls
     */
    public void setEnablePDPControls(final boolean enablePDPControls) {
        this.m_enablePDPControls = enablePDPControls;
    }

    /**
     * @return enablePDPMarginControls
     */
    public boolean getEnablePDPMarginControls() {
        return m_enablePDPMarginControls;
    }

    /**
     * @param enablePDPMarginControls
     */
    public void setEnablePDPMarginControls(final boolean enablePDPMarginControls) {
        this.m_enablePDPMarginControls = enablePDPMarginControls;
    }

    /**
     * @return enableICEControls
     */
    public boolean getEnableICEControls() {
        return m_enableICEControls;
    }

    /**
     * @param enableICEControls
     */
    public void setEnableICEControls(final boolean enableICEControls) {
        this.m_enableICEControls = enableICEControls;
    }

    /**
     * @return enableStaticLineControls
     */
    public boolean getEnableStaticLineControls() {
        return m_enableStaticLineControls;
    }

    /**
     * @param enableStaticLineControls
     */
    public void setEnableStaticLineControls(final boolean enableStaticLineControls) {
        this.m_enableStaticLineControls = enableStaticLineControls;
    }

    /**
     * @return enableDataPointControls
     */
    public boolean getEnableDataPointControls() {
        return m_enableDataPointControls;
    }

    /**
     * @param enableDataPointControls
     */
    public void setEnableDataPointControls(final boolean enableDataPointControls) {
        this.m_enableDataPointControls = enableDataPointControls;
    }

    /**
     * @return enableSelectionFilterControls
     */
    public boolean getEnableSelectionFilterControls() {
        return m_enableSelectionFilterControls;
    }

    /**
     * @param enableSelectionFilterControls
     */
    public void setEnableSelectionFilterControls(final boolean enableSelectionFilterControls) {
        this.m_enableSelectionFilterControls = enableSelectionFilterControls;
    }

    /**
     * @return enableSelectionControls
     */
    public boolean getEnableSelectionControls() {
        return m_enableSelectionControls;
    }

    /**
     * @param enableSelectionControls
     */
    public void setEnableSelectionControls(final boolean enableSelectionControls) {
        this.m_enableSelectionControls = enableSelectionControls;
    }

    /**
     * @return enableYAxisMarginControls
     */
    public boolean getEnableYAxisMarginControls() {
        return m_enableYAxisMarginControls;
    }

    /**
     * @param enableYAxisMarginControls
     */
    public void setEnableYAxisMarginControls(final boolean enableYAxisMarginControls) {
        this.m_enableYAxisMarginControls = enableYAxisMarginControls;
    }

    /**
     * @return enableSmartZoomControls
     */
    public boolean getEnableSmartZoomControls() {
        return m_enableSmartZoomControls;
    }

    /**
     * @param enableSmartZoomControls
     */
    public void setEnableSmartZoomControls(final boolean enableSmartZoomControls) {
        this.m_enableSmartZoomControls = enableSmartZoomControls;
    }

    /**
     * @return enableGridControls
     */
    public boolean getEnableGridControls() {
        return m_enableGridControls;
    }

    /**
     * @param enableGridControls
     */
    public void setEnableGridControls(final boolean enableGridControls) {
        this.m_enableGridControls = enableGridControls;
    }

    /**
     * @return enableMouseCrosshairControls
     */
    public boolean getEnableMouseCrosshairControls() {
        return m_enableMouseCrosshairControls;
    }

    /**
     * @param enableMouseCrosshairControls
     */
    public void setEnableMouseCrosshairControls(final boolean enableMouseCrosshairControls) {
        this.m_enableMouseCrosshairControls = enableMouseCrosshairControls;
    }

    /**
     * @return enableAdvancedOptionsControls
     */
    public boolean getEnableAdvancedOptionsControls() {
        return m_enableAdvancedOptionsControls;
    }

    /**
     * @param enableAdvancedOptionsControls
     */
    public void setEnableAdvancedOptionsControls(final boolean enableAdvancedOptionsControls) {
        this.m_enableAdvancedOptionsControls = enableAdvancedOptionsControls;
    }

	/**
	 * @return the showWarnings
	 */
	public Boolean getShowWarnings() {
		return m_showWarnings;
	}

	/**
	 * @param showWarnings the showWarnings to set
	 */
	public void setShowWarnings(final Boolean showWarnings) {
		this.m_showWarnings = showWarnings;
	}

    /**
     * @return the warnings
     */
    public JSONWarnings getJSONWarnings() {
        return m_JSONwarnings;
    }

    /**
     * @param warnings the warnings to set
     */
    public void setJSONWarnings(final JSONWarnings warnings) {
        m_JSONwarnings = warnings;
    }

    /**
     * @return the runningInView
     */
    public Boolean getRunningInView() {
        return m_runningInView;
    }

    /**
     * @param runningInView the runningInView to set
     */
    public void setRunningInView(final Boolean runningInView) {
        this.m_runningInView = runningInView;
    }

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void saveToNodeSettings(final NodeSettingsWO settings) {
		settings.addInt(PartialDependenceICEPlotConfig.CFG_MAX_NUM_ROWS, getMaxNumRows());
		settings.addBoolean(PartialDependenceICEPlotConfig.CFG_GENERATE_IMAGE, getGenerateImage());
		settings.addStringArray(PartialDependenceICEPlotConfig.CFG_FEATURE_COLUMN_STRINGS, getSampledFeatureColumns());
		settings.addString(PartialDependenceICEPlotConfig.CFG_ROW_ID_COLUMN, getRowIDCol());
		settings.addStringArray(PartialDependenceICEPlotConfig.CFG_PREDICTION_COLUMNS, getPredictionColumns());
		settings.addIntArray(PartialDependenceICEPlotConfig.CFG_PREDICTION_INDICIES, getPredictionIndicies());
		settings.addInt(PartialDependenceICEPlotConfig.CFG_VIEW_WIDTH, getViewWidth());
		settings.addInt(PartialDependenceICEPlotConfig.CFG_VIEW_HEIGHT, getViewHeight());
		settings.addBoolean(PartialDependenceICEPlotConfig.CFG_RESIZE_TO_FILL, getResizeToFill());
		settings.addBoolean(PartialDependenceICEPlotConfig.CFG_FULLSCREEN_BUTTON, getFullscreenButton());
		settings.addBoolean(PartialDependenceICEPlotConfig.CFG_ENABLE_PANNING, getEnablePanning());
		settings.addBoolean(PartialDependenceICEPlotConfig.CFG_ENABLE_SCROLL_ZOOM, getEnableScrollZoom());
		settings.addBoolean(PartialDependenceICEPlotConfig.CFG_ENABLE_DRAG_ZOOM, getEnableDragZoom());
		settings.addBoolean(PartialDependenceICEPlotConfig.CFG_SHOW_ZOOM_RESET, getShowZoomReset());
		settings.addBoolean(PartialDependenceICEPlotConfig.CFG_ENABLE_SELECTION, getEnableSelection());
		settings.addBoolean(PartialDependenceICEPlotConfig.CFG_ENABLE_INTERACTIVE_CTRLS, getEnableInteractiveCtrls());
		settings.addBoolean(PartialDependenceICEPlotConfig.CFG_ENABLE_TITLE_CONTROLS, getEnableTitleControls());
		settings.addBoolean(PartialDependenceICEPlotConfig.CFG_ENABLE_AXIS_LABEL_CONTROLS, getEnableAxisLabelControls());
		settings.addBoolean(PartialDependenceICEPlotConfig.CFG_ENABLE_PDP_CONTROLS, getEnablePDPControls());
		settings.addBoolean(PartialDependenceICEPlotConfig.CFG_ENABLE_PDP_MARGIN_CONTROLS, getEnablePDPMarginControls());
		settings.addBoolean(PartialDependenceICEPlotConfig.CFG_ENABLE_ICE_CONTROLS, getEnableICEControls());
		settings.addBoolean(PartialDependenceICEPlotConfig.CFG_ENABLE_STATIC_LINE_CONTROLS, getEnableStaticLineControls());
		settings.addBoolean(PartialDependenceICEPlotConfig.CFG_ENABLE_DATA_POINT_CONTROLS, getEnableDataPointControls());
		settings.addBoolean(PartialDependenceICEPlotConfig.CFG_ENABLE_SELECTION_FILTER_CONTROLS, getEnableSelectionFilterControls());
		settings.addBoolean(PartialDependenceICEPlotConfig.CFG_ENABLE_SELECTION_CONTROLS, getEnableSelectionControls());
		settings.addBoolean(PartialDependenceICEPlotConfig.CFG_ENABLE_Y_AXIS_MARGIN_CONTROLS, getEnableYAxisMarginControls());
		settings.addBoolean(PartialDependenceICEPlotConfig.CFG_ENABLE_SMART_ZOOM_CONTROLS, getEnableSmartZoomControls());
		settings.addBoolean(PartialDependenceICEPlotConfig.CFG_ENABLE_GRID_CONTROLS, getEnableGridControls());
		settings.addBoolean(PartialDependenceICEPlotConfig.CFG_ENABLE_MOUSE_CROSSHAIR_CONTROLS, getEnableMouseCrosshairControls());
		settings.addBoolean(PartialDependenceICEPlotConfig.CFG_ENABLE_ADVANCED_OPTIONS_CONTROLS, getEnableAdvancedOptionsControls());
		settings.addBoolean(PartialDependenceICEPlotConfig.CFG_SHOW_WARNINGS, getShowWarnings());
		settings.addBoolean(PartialDependenceICEPlotConfig.CFG_RUNNING_IN_VIEW, getRunningInView());
		m_table.saveJSONToNodeSettings(settings);
		m_JSONwarnings.saveToNodeSettings(settings);
        settings.addInt(PartialDependenceICEPlotConfig.CFG_NUM_FEATURE_DOMAINS, getFeatureDomains().length);
        int count = 0;
        for (double[] featDomain : m_featureDomains) {
            settings.addDoubleArray(PartialDependenceICEPlotConfig.CFG_FEATURE_DOMAINS + count, featDomain);
        }

        settings.addInt(PartialDependenceICEPlotConfig.CFG_NUM_PREDICTION_DOMAINS, getPredictionDomains().length);
        count = 0;
        for (double[] predDomain : m_predictionDomains) {
            settings.addDoubleArray(PartialDependenceICEPlotConfig.CFG_PREDICTION_DOMAINS + count, predDomain);
        }
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void loadFromNodeSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
		setMaxNumRows(settings.getInt(PartialDependenceICEPlotConfig.CFG_MAX_NUM_ROWS));
		setGenerateImage(settings.getBoolean(PartialDependenceICEPlotConfig.CFG_GENERATE_IMAGE));
		setSampledFeatureColumns(settings.getStringArray(PartialDependenceICEPlotConfig.CFG_FEATURE_COLUMN_STRINGS));
		setRowIDCol(settings.getString(PartialDependenceICEPlotConfig.CFG_ROW_ID_COLUMN));
		setPredictionColumns(settings.getStringArray(PartialDependenceICEPlotConfig.CFG_PREDICTION_COLUMNS));
		setPredictionIndicies(settings.getIntArray(PartialDependenceICEPlotConfig.CFG_PREDICTION_INDICIES));
		setViewWidth(settings.getInt(PartialDependenceICEPlotConfig.CFG_VIEW_WIDTH));
		setViewHeight(settings.getInt(PartialDependenceICEPlotConfig.CFG_VIEW_HEIGHT));
		setResizeToFill(settings.getBoolean(PartialDependenceICEPlotConfig.CFG_RESIZE_TO_FILL));
		setFullscreenButton(settings.getBoolean(PartialDependenceICEPlotConfig.CFG_FULLSCREEN_BUTTON));
		setEnablePanning(settings.getBoolean(PartialDependenceICEPlotConfig.CFG_ENABLE_PANNING));
		setEnableScrollZoom(settings.getBoolean(PartialDependenceICEPlotConfig.CFG_ENABLE_SCROLL_ZOOM));
		setEnableDragZoom(settings.getBoolean(PartialDependenceICEPlotConfig.CFG_ENABLE_DRAG_ZOOM));
		setShowZoomReset(settings.getBoolean(PartialDependenceICEPlotConfig.CFG_SHOW_ZOOM_RESET));
		setEnableSelection(settings.getBoolean(PartialDependenceICEPlotConfig.CFG_ENABLE_SELECTION));
		setEnableInteractiveCtrls(settings.getBoolean(PartialDependenceICEPlotConfig.CFG_ENABLE_INTERACTIVE_CTRLS));
        setEnableTitleControls(settings.getBoolean(PartialDependenceICEPlotConfig.CFG_ENABLE_TITLE_CONTROLS));
        setEnableAxisLabelControls(settings.getBoolean(PartialDependenceICEPlotConfig.CFG_ENABLE_AXIS_LABEL_CONTROLS));
        setEnablePDPControls(settings.getBoolean(PartialDependenceICEPlotConfig.CFG_ENABLE_PDP_CONTROLS));
        setEnablePDPMarginControls(settings.getBoolean(PartialDependenceICEPlotConfig.CFG_ENABLE_PDP_MARGIN_CONTROLS));
        setEnableICEControls(settings.getBoolean(PartialDependenceICEPlotConfig.CFG_ENABLE_ICE_CONTROLS));
        setEnableStaticLineControls(settings.getBoolean(PartialDependenceICEPlotConfig.CFG_ENABLE_STATIC_LINE_CONTROLS));
        setEnableDataPointControls(settings.getBoolean(PartialDependenceICEPlotConfig.CFG_ENABLE_DATA_POINT_CONTROLS));
        setEnableSelectionFilterControls(settings.getBoolean(PartialDependenceICEPlotConfig.CFG_ENABLE_SELECTION_FILTER_CONTROLS));
        setEnableSelectionControls(settings.getBoolean(PartialDependenceICEPlotConfig.CFG_ENABLE_SELECTION_CONTROLS));
        setEnableYAxisMarginControls(settings.getBoolean(PartialDependenceICEPlotConfig.CFG_ENABLE_Y_AXIS_MARGIN_CONTROLS));
        setEnableSmartZoomControls(settings.getBoolean(PartialDependenceICEPlotConfig.CFG_ENABLE_SMART_ZOOM_CONTROLS));
        setEnableGridControls(settings.getBoolean(PartialDependenceICEPlotConfig.CFG_ENABLE_GRID_CONTROLS));
        setEnableMouseCrosshairControls(settings.getBoolean(PartialDependenceICEPlotConfig.CFG_ENABLE_MOUSE_CROSSHAIR_CONTROLS));
        setEnableAdvancedOptionsControls(settings.getBoolean(PartialDependenceICEPlotConfig.CFG_ENABLE_ADVANCED_OPTIONS_CONTROLS));
		setShowWarnings(settings.getBoolean(PartialDependenceICEPlotConfig.CFG_SHOW_WARNINGS));
		setDataTable(JSONDataTable.loadFromNodeSettings(settings));
		m_JSONwarnings.loadFromNodeSettings(settings);
		setRunningInView(settings.getBoolean(PartialDependenceICEPlotConfig.CFG_RUNNING_IN_VIEW));
        int numFeatDomains = settings.getInt(PartialDependenceICEPlotConfig.CFG_NUM_FEATURE_DOMAINS);
        double[][] featDomains = new double[numFeatDomains][2];
        for (int x = 0; x < numFeatDomains; x++) {
            featDomains[x] = settings.getDoubleArray(PartialDependenceICEPlotConfig.CFG_FEATURE_DOMAINS + x, new double[0]);
        }
        setFeatureDomains(featDomains);
        int numPredDomains = settings.getInt(PartialDependenceICEPlotConfig.CFG_NUM_PREDICTION_DOMAINS);
        double[][] predDomains = new double[numPredDomains][2];
        for (int x = 0; x < numPredDomains; x++) {
            predDomains[x] = settings.getDoubleArray(PartialDependenceICEPlotConfig.CFG_PREDICTION_DOMAINS + x, new double[0]);
        }
        setPredictionDomains(predDomains);
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
        PartialDependenceICEPlotNodeViewRepresentation other = (PartialDependenceICEPlotNodeViewRepresentation) obj;
        return new EqualsBuilder()
        		.append(m_table, other.m_table)
        		.append(m_maxNumRows, other.m_maxNumRows)
        		.append(m_generateImage, other.m_generateImage)
        		.append(m_sampledFeatureColumns, other.m_sampledFeatureColumns)
        		.append(m_rowIDCol, other.m_rowIDCol)
        		.append(m_predictionColumns, other.m_predictionColumns)
        		.append(m_predictionIndicies, other.m_predictionIndicies)
        		.append(m_featureDomains, other.m_featureDomains)
        		.append(m_predictionDomains, other.m_predictionDomains)
        		.append(m_viewWidth, other.m_viewWidth)
        		.append(m_viewHeight, other.m_viewHeight)
        		.append(m_resizeToFill, other.m_resizeToFill)
        		.append(m_fullscreenButton, other.m_fullscreenButton)
        		.append(m_enablePanning, other.m_enablePanning)
        		.append(m_enableScrollZoom, other.m_enableScrollZoom)
        		.append(m_enableDragZoom, other.m_enableDragZoom)
        		.append(m_showZoomReset, other.m_showZoomReset)
        		.append(m_enableSelection, other.m_enableSelection)
        		.append(m_enableInteractiveCtrls, other.m_enableInteractiveCtrls)
        		.append(m_enableTitleControls, other.m_enableTitleControls)
        		.append(m_enableAxisLabelControls, other.m_enableAxisLabelControls)
        		.append(m_enablePDPControls, other.m_enablePDPControls)
        		.append(m_enablePDPMarginControls, other.m_enablePDPMarginControls)
        		.append(m_enableICEControls, other.m_enableICEControls)
        		.append(m_enableStaticLineControls, other.m_enableStaticLineControls)
        		.append(m_enableDataPointControls, other.m_enableDataPointControls)
        		.append(m_enableSelectionFilterControls, other.m_enableSelectionFilterControls)
        		.append(m_enableSelectionControls, other.m_enableSelectionControls)
        		.append(m_enableYAxisMarginControls, other.m_enableYAxisMarginControls)
        		.append(m_enableSmartZoomControls, other.m_enableSmartZoomControls)
        		.append(m_enableGridControls, other.m_enableGridControls)
        		.append(m_enableMouseCrosshairControls, other.m_enableMouseCrosshairControls)
        		.append(m_enableAdvancedOptionsControls, other.m_enableAdvancedOptionsControls)
                .append(m_showWarnings, other.m_showWarnings)
        		.append(m_JSONwarnings, other.m_JSONwarnings)
        		.append(m_runningInView, other.m_runningInView)
        		.isEquals();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
				.append(m_table)
				.append(m_maxNumRows)
				.append(m_generateImage)
				.append(m_sampledFeatureColumns)
				.append(m_rowIDCol)
				.append(m_predictionColumns)
				.append(m_predictionIndicies)
				.append(m_featureDomains)
				.append(m_featureDomains)
				.append(m_viewWidth)
				.append(m_viewHeight)
				.append(m_resizeToFill)
				.append(m_fullscreenButton)
				.append(m_enablePanning)
				.append(m_enableScrollZoom)
				.append(m_enableDragZoom)
				.append(m_showZoomReset)
				.append(m_enableSelection)
				.append(m_enableInteractiveCtrls)
                .append(m_enableTitleControls)
                .append(m_enableAxisLabelControls)
                .append(m_enablePDPControls)
                .append(m_enablePDPMarginControls)
                .append(m_enableICEControls)
                .append(m_enableStaticLineControls)
                .append(m_enableDataPointControls)
                .append(m_enableSelectionFilterControls)
                .append(m_enableSelectionControls)
                .append(m_enableYAxisMarginControls)
                .append(m_enableSmartZoomControls)
                .append(m_enableGridControls)
                .append(m_enableMouseCrosshairControls)
                .append(m_enableAdvancedOptionsControls)
                .append(m_showWarnings)
				.append(m_JSONwarnings)
				.append(m_runningInView)
				.toHashCode();
	}
}

