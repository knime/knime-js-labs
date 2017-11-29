/*
 * ------------------------------------------------------------------------
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
 *  KNIME and ECLIPSE being a combined program, KNIME GMBH herewith grants
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
 * ------------------------------------------------------------------------
 *
 * History
 *   24.04.2015 (Christian Albrecht, KNIME AG, Zurich, Switzerland): created
 */
package org.knime.base.node.mine.optics.assigner;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.js.core.JSONViewContent;
import org.knime.js.core.datasets.JSONKeyedValues2DDataset;
import org.knime.js.core.warnings.JSONWarnings;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * @author Anastasia Zhukova, KNIME GmbH, Konstanz, Germany
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 * @since 3.4
 */
@JsonAutoDetect
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public final class OPTICSAssignerViewRepresentation extends JSONViewContent {

	private JSONKeyedValues2DDataset m_keyedDataset;

	private boolean m_showLegend;
    private boolean m_displayFullscreenButton;
    private boolean m_resizeToWindow;
    private boolean m_enableViewConfiguration;
    private boolean m_enableTitleChange;
    private boolean m_enableEpsilonPrimeChange;
    private boolean m_enableSelection;
    private boolean m_enableRectangleSelection;
    private boolean m_enableShowSelectedOnly;
    private int m_imageWidth;
    private int m_imageHeight;
    private int m_maxBins;
    private boolean m_wasRedrawn = OPTICSAssignerViewConfig.DEFAULT_WAS_REDRAWN;
    private boolean m_showWarningInView;
    private JSONWarnings m_warnings = new JSONWarnings();
    private double m_eps;

    /**
     * @return the keyedDataset
     */
    public JSONKeyedValues2DDataset getKeyedDataset() {
        return m_keyedDataset;
    }

    /**
     * @param keyedDataset the keyedDataset to set
     */
    public void setKeyedDataset(final JSONKeyedValues2DDataset keyedDataset) {
        m_keyedDataset = keyedDataset;
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
     * @return the allowViewConfiguration
     */
    public boolean getEnableViewConfiguration() {
        return m_enableViewConfiguration;
    }

    /**
     * @param enableViewConfiguration the allowViewConfiguration to set
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
     * @return the enableEpsilonPrimeChange
     */
    public boolean getEnableEpsilonPrimeChange() {
        return m_enableEpsilonPrimeChange;
    }

    /**
     * @param enableEpsilonPrimeChange the enableEpsilonPrimeChange to set
     */
    public void setEnableEpsilonPrimeChange(final boolean enableEpsilonPrimeChange) {
        m_enableEpsilonPrimeChange = enableEpsilonPrimeChange;
    }

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
     * @return the enableRectangleSelection
     */
    public boolean getEnableRectangleSelection() {
        return m_enableRectangleSelection;
    }

    /**
     * @param enableRectangleSelection the enableRectangleSelection to set
     */
    public void setEnableRectangleSelection(final boolean enableRectangleSelection) {
        m_enableRectangleSelection = enableRectangleSelection;
    }

    /**
     * @return the enableShowSelectedOnly
     */
    public boolean getEnableShowSelectedOnly() {
        return m_enableShowSelectedOnly;
    }

    /**
     * @param enableShowSelectedOnly the enableShowSelectedOnly to set
     */
    public void setEnableShowSelectedOnly(final boolean enableShowSelectedOnly) {
        m_enableShowSelectedOnly = enableShowSelectedOnly;
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
     * @return the warnings
     */
    public JSONWarnings getWarnings() {
        return m_warnings;
    }

    /**
     * @param warnings the warnings to set
     */
    public void setWarnings(final JSONWarnings warnings) {
        m_warnings = warnings;
    }

    /**
     * @return the showLegend
     */
    public boolean getShowLegend() {
        return m_showLegend;
    }

    /**
     * @param showLegend the showLegend to set
     */
    public void setShowLegend(final boolean showLegend) {
        m_showLegend = showLegend;
    }

    /**
     * @return the maxBins
     */
    public int getMaxBins() {
        return m_maxBins;
    }

    /**
     * @param maxBins the maxBins to set
     */
    public void setMaxBins(final int maxBins) {
        m_maxBins = maxBins;
    }

    /**
     * @return the wasRedrawn
     */
    public boolean getWasRedrawn() {
        return m_wasRedrawn;
    }

    /**
     * @param wasRedrawn the wasRedrawn to set
     */
    public void setWasRedrawn(final boolean wasRedrawn) {
        m_wasRedrawn = wasRedrawn;
    }

    /**
     * @return the m_eps
     */
    public double getEps() {
        return m_eps;
    }

    /**
     * @param eps the m_eps to set
     */
    public void setEps(final double eps) {
        this.m_eps = eps;
    }

	@Override
	public void saveToNodeSettings(final NodeSettingsWO settings) {
	    settings.addBoolean(OPTICSAssignerViewConfig.CFG_DISPLAY_FULLSCREEN_BUTTON, getDisplayFullscreenButton());
	    settings.addBoolean(OPTICSAssignerViewConfig.CFG_RESIZE_TO_WINDOW, getResizeToWindow());
	    settings.addBoolean(OPTICSAssignerViewConfig.CFG_ENABLE_TTILE_CHANGE, getEnableTitleChange());
	    settings.addBoolean(OPTICSAssignerViewConfig.ENABLE_EPSILON_PRIME_CHANGE, getEnableEpsilonPrimeChange());
	    settings.addBoolean(OPTICSAssignerViewConfig.CFG_ENABLE_CONFIG, getEnableViewConfiguration());
	    settings.addBoolean(OPTICSAssignerViewConfig.CFG_ENABLE_SELECTION, getEnableSelection());
	    settings.addBoolean(OPTICSAssignerViewConfig.CFG_ENABLE_RECTANGLE_SELECTION, getEnableRectangleSelection());
	    settings.addBoolean(OPTICSAssignerViewConfig.CFG_ENABLE_SHOW_SELECTED_ONLY, getEnableShowSelectedOnly());
        settings.addInt(OPTICSAssignerViewConfig.CFG_IMAGE_WIDTH, getImageWidth());
        settings.addInt(OPTICSAssignerViewConfig.CFG_IMAGE_HEIGHT, getImageHeight());
        settings.addInt(OPTICSAssignerViewConfig.CFG_MAX_ROWS, getMaxBins());
        settings.addBoolean(OPTICSAssignerViewConfig.CFG_WAS_REDRAWN, getWasRedrawn());
        settings.addBoolean("hasDataset", m_keyedDataset != null);
        if (m_keyedDataset != null) {
            NodeSettingsWO datasetSettings = settings.addNodeSettings("dataset");
            m_keyedDataset.saveToNodeSettings(datasetSettings);
        }
        settings.addBoolean(OPTICSAssignerViewConfig.CFG_SHOW_WARNING_IN_VIEW, getShowWarningInView());
        settings.addDouble(OPTICSAssignerViewConfig.EPSILON, getEps());
        m_warnings.saveToNodeSettings(settings);
	}

	@Override
	public void loadFromNodeSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
	    setDisplayFullscreenButton(settings.getBoolean(OPTICSAssignerViewConfig.CFG_DISPLAY_FULLSCREEN_BUTTON, OPTICSAssignerViewConfig.DEFAULT_DISPLAY_FULLSCREEN_BUTTON));
	    setResizeToWindow(settings.getBoolean(OPTICSAssignerViewConfig.CFG_RESIZE_TO_WINDOW));
	    setEnableViewConfiguration(settings.getBoolean(OPTICSAssignerViewConfig.CFG_ENABLE_CONFIG));
	    setEnableTitleChange(settings.getBoolean(OPTICSAssignerViewConfig.CFG_ENABLE_TTILE_CHANGE));
	    setEnableEpsilonPrimeChange(settings.getBoolean(OPTICSAssignerViewConfig.ENABLE_EPSILON_PRIME_CHANGE));
	    setEnableSelection(settings.getBoolean(OPTICSAssignerViewConfig.CFG_ENABLE_SELECTION));
	    setEnableRectangleSelection(settings.getBoolean(OPTICSAssignerViewConfig.CFG_ENABLE_RECTANGLE_SELECTION));
	    setEnableShowSelectedOnly(settings.getBoolean(OPTICSAssignerViewConfig.CFG_ENABLE_SHOW_SELECTED_ONLY));
	    setImageWidth(settings.getInt(OPTICSAssignerViewConfig.CFG_IMAGE_WIDTH));
	    setImageHeight(settings.getInt(OPTICSAssignerViewConfig.CFG_IMAGE_HEIGHT));
	    setMaxBins(settings.getInt(OPTICSAssignerViewConfig.CFG_MAX_ROWS));
	    setWasRedrawn(settings.getBoolean(OPTICSAssignerViewConfig.CFG_WAS_REDRAWN));
	    m_keyedDataset = null;
        boolean hasDataset = settings.getBoolean("hasDataset");
        if (hasDataset) {
            NodeSettingsRO datasetSettings = settings.getNodeSettings("dataset");
            m_keyedDataset = new JSONKeyedValues2DDataset();
            m_keyedDataset.loadFromNodeSettings(datasetSettings);
        }
        setShowWarningInView(settings.getBoolean(OPTICSAssignerViewConfig.CFG_SHOW_WARNING_IN_VIEW, OPTICSAssignerViewConfig.DEFAULT_SHOW_WARNING_IN_VIEW));
        setEps(settings.getDouble(OPTICSAssignerViewConfig.EPSILON));
        m_warnings.loadFromNodeSettings(settings);
	}

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
        OPTICSAssignerViewRepresentation other = (OPTICSAssignerViewRepresentation)obj;
        return new EqualsBuilder()
            .append(m_keyedDataset, other.m_keyedDataset)
            .append(m_resizeToWindow, other.m_resizeToWindow)
            .append(m_imageWidth, other.m_imageWidth)
            .append(m_imageHeight, other.m_imageHeight)
            .append(m_displayFullscreenButton, other.m_displayFullscreenButton)
            .append(m_enableTitleChange, other.m_enableTitleChange)
            .append(m_enableEpsilonPrimeChange, other.m_enableEpsilonPrimeChange)
            .append(m_enableSelection, other.m_enableSelection)
            .append(m_enableRectangleSelection, other.m_enableRectangleSelection)
            .append(m_enableShowSelectedOnly, other.m_enableShowSelectedOnly)
            .append(m_showWarningInView, other.m_showWarningInView)
            .append(m_warnings, other.m_warnings)
            .append(m_enableViewConfiguration, other.m_enableViewConfiguration)
            .append(m_maxBins, other.m_maxBins)
            .append(m_wasRedrawn, other.m_wasRedrawn)
            .append(m_eps, other.m_eps)
            .isEquals();
    }

	@Override
    public int hashCode() {
        return new HashCodeBuilder()
            .append(m_keyedDataset)
            .append(m_resizeToWindow)
            .append(m_displayFullscreenButton)
            .append(m_resizeToWindow)
            .append(m_enableTitleChange)
            .append(m_enableEpsilonPrimeChange)
            .append(m_enableSelection)
            .append(m_enableRectangleSelection)
            .append(m_imageWidth)
            .append(m_imageHeight)
            .append(m_showWarningInView)
            .append(m_warnings)
            .append(m_enableViewConfiguration)
            .append(m_maxBins)
            .append(m_wasRedrawn)
            .append(m_eps)
            .toHashCode();
    }



}
