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
 *   Jul 19, 2018 (awalter): created
 */
package org.knime.base.node.mine.cluster.hierarchical.js;

import java.util.Arrays;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.js.core.JSONDataTable;
import org.knime.js.core.JSONViewContent;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 *
 * @author Alison Walter
 */
@JsonAutoDetect
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class HierarchicalClusterAssignerRepresentation extends JSONViewContent {

    private boolean m_generateImage;
    private int m_imageWidth;
    private int m_imageHeight;

    private boolean m_resizeToWindow;
    private boolean m_enableClusterColor;
    private boolean m_enableClusterLabels;

    private boolean m_enableViewEdit;
    private boolean m_enableTitleEdit;
    private boolean m_displayFullscreenButton;
    private boolean m_enableNumClusterEdit;

    private boolean m_enableSelection;
    private boolean m_showClearSelectionButton;
    private boolean m_showSelectedOnlyToggle;

    private boolean m_showWarningsInView;

    private boolean m_enableZoomAndPanning;
    private boolean m_showZoomResetButton;
    private boolean m_enableLocalScaleToggle;

    private boolean m_enableChangeOrientation;

    private String[] m_colorPalette;

    private boolean m_showThresholdBar;
    private boolean m_enableThresholdModification;

    private boolean m_enableAxisLabelEdit;

    private final static String CFG_DATA_TABLE_ID = "dataTableID";
    private String m_dataTableID;
    private final static String CFG_FILTER_IDS = "filterIds";
    private String[] m_filterIds;
    private final static String CFG_RUNNING_IN_VIEW = "runningInView";
    private boolean m_runningInView;

    private JSClusterModelTree m_tree;
    private JSONDataTable m_table;

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
     * @return the enableClusterColor
     */
    public boolean getEnableClusterColor() {
        return m_enableClusterColor;
    }

    /**
     * @param enableClusterColor the enableClusterColor to set
     */
    public void setEnableClusterColor(final boolean enableClusterColor) {
        m_enableClusterColor = enableClusterColor;
    }

    /**
     * @return the enableClusterLabels
     */
    public boolean getEnableClusterLabels() {
        return m_enableClusterLabels;
    }

    /**
     * @param enableClusterLabels the enableClusterLabels to set
     */
    public void setEnableClusterLabels(final boolean enableClusterLabels) {
        m_enableClusterLabels = enableClusterLabels;
    }

    /**
     * @return the enableViewEdit
     */
    public boolean getEnableViewEdit() {
        return m_enableViewEdit;
    }

    /**
     * @param enableViewEdit the enableViewEdit to set
     */
    public void setEnableViewEdit(final boolean enableViewEdit) {
        m_enableViewEdit = enableViewEdit;
    }

    /**
     * @return the enableTitleEdit
     */
    public boolean getEnableTitleEdit() {
        return m_enableTitleEdit;
    }

    /**
     * @param enableTitleEdit the enableTitleEdit to set
     */
    public void setEnableTitleEdit(final boolean enableTitleEdit) {
        m_enableTitleEdit = enableTitleEdit;
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
     * @return the enableNumClusterEdit
     */
    public boolean getEnableNumClusterEdit() {
        return m_enableNumClusterEdit;
    }

    /**
     * @param enableNumClusterEdit the enableNumClusterEdit to set
     */
    public void setEnableNumClusterEdit(final boolean enableNumClusterEdit) {
        m_enableNumClusterEdit = enableNumClusterEdit;
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
     * @return the showClearSelectionButton
     */
    public boolean getShowClearSelectionButton() {
        return m_showClearSelectionButton;
    }

    /**
     * @param showClearSelectionButton the showClearSelectionButton to set
     */
    public void setShowClearSelectionButton(final boolean showClearSelectionButton) {
        m_showClearSelectionButton = showClearSelectionButton;
    }

    /**
     * @return the showSelectedOnlyToggle
     */
    public boolean getShowSelectedOnlyToggle() {
        return m_showSelectedOnlyToggle;
    }

    /**
     * @param showSelectedOnlyToggle the showSelectedOnlyToggle to set
     */
    public void setShowSelectedOnlyToggle(final boolean showSelectedOnlyToggle) {
        m_showSelectedOnlyToggle = showSelectedOnlyToggle;
    }

    /**
     * @return the showWarningsInView
     */
    public boolean getShowWarningsInView() {
        return m_showWarningsInView;
    }

    /**
     * @param showWarningsInView the showWarningsInView to set
     */
    public void setShowWarningsInView(final boolean showWarningsInView) {
        m_showWarningsInView = showWarningsInView;
    }

    /**
     * @return the enableZoomAndPanning
     */
    public boolean getEnableZoomAndPanning() {
        return m_enableZoomAndPanning;
    }

    /**
     * @param enableZoomAndPanning the enableZoomAndPanning to set
     */
    public void setEnableZoom(final boolean enableZoomAndPanning) {
        m_enableZoomAndPanning = enableZoomAndPanning;
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
     * @return the enableLogScaleToggle
     */
    public boolean getEnableLogScaleToggle() {
        return m_enableLocalScaleToggle;
    }

    /**
     * @param enableLogScaleToggle the enableLogScaleToggle to set
     */
    public void setEnableLogScaleToggle(final boolean enableLogScaleToggle) {
        m_enableLocalScaleToggle = enableLogScaleToggle;
    }

    /**
     * @return the enableChangeOrientation
     */
    public boolean getEnableChangeOrientation() {
        return m_enableChangeOrientation;
    }

    /**
     * @param enableChangeOrientation the enableChangeOrientation to set
     */
    public void setEnableChangeOrientation(final boolean enableChangeOrientation) {
        m_enableChangeOrientation = enableChangeOrientation;
    }

    /**
     * @return the colorPalette
     */
    public String[] getColorPalette() {
        return m_colorPalette;
    }

    /**
     * @param colorPalette the colorPalette to set
     */
    public void setColorPalette(final String[] colorPalette) {
        m_colorPalette = colorPalette;
    }

    /**
     * @return the showThresholdBar
     */
    public boolean getShowThresholdBar() {
        return m_showThresholdBar;
    }

    /**
     * @param showThresholdBar the showThresholdBar to set
     */
    public void setShowThresholdBar(final boolean showThresholdBar) {
        m_showThresholdBar = showThresholdBar;
    }

    /**
     * @return the enableThresholdModification
     */
    public boolean getEnableThresholdModification() {
        return m_enableThresholdModification;
    }

    /**
     * @param enableThresholdModification the enableThresholdModification to set
     */
    public void setEnableThresholdModification(final boolean enableThresholdModification) {
        m_enableThresholdModification = enableThresholdModification;
    }

    /**
     * @return the enableAxisLabelEdit
     */
    public boolean getEnableAxisLabelEdit() {
        return m_enableAxisLabelEdit;
    }

    /**
     * @param enableAxisLabelEdit the enableAxisLabelEdit to set
     */
    public void setEnableAxisLabelEdit(final boolean enableAxisLabelEdit) {
        m_enableAxisLabelEdit = enableAxisLabelEdit;
    }

    /**
     * @return the dataTableID
     */
    @JsonIgnore
    public String getDataTableID() {
        return m_dataTableID;
    }

    /**
     * @param dataTableID the dataTableID to set
     */
    @JsonIgnore
    public void setDataTableID(final String dataTableID) {
        m_dataTableID = dataTableID;
    }

    /**
     * @return the filterIds
     */
    @JsonIgnore
    public String[] getFilterIds() {
        return m_filterIds;
    }

    /**
     * @param filterIds the filterIds to set
     */
    @JsonIgnore
    public void setFilterIds(final String[] filterIds) {
        m_filterIds = filterIds;
    }

    /**
     * @return the runningInView
     */
    public boolean getRunningInView() {
        return m_runningInView;
    }

    /**
     * @param runningInView the runningInView to set
     */
    public void setRunningInView(final boolean runningInView) {
        m_runningInView = runningInView;
    }

    /**
     * @return the tree
     */
    public JSClusterModelTree getTree() {
        return m_tree;
    }

    /**
     * @param tree the tree to set
     */
    public void setTree(final JSClusterModelTree tree) {
        m_tree = tree;
    }

    /**
     * @return the table
     */
    public JSONDataTable getTable() {
        return m_table;
    }

    /**
     * @param table the table to set
     */
    public void setTable(final JSONDataTable table) {
        m_table = table;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveToNodeSettings(final NodeSettingsWO settings) {
        settings.addBoolean(HierarchicalClusterAssignerConfig.CFG_GENERATE_IMAGE, getGenerateImage());
        settings.addInt(HierarchicalClusterAssignerConfig.CFG_IMAGE_WIDTH, getImageWidth());
        settings.addInt(HierarchicalClusterAssignerConfig.CFG_IMAGE_HEIGHT, getImageHeight());

        settings.addBoolean(HierarchicalClusterAssignerConfig.CFG_RESIZE_TO_WINDOW, getResizeToWindow());
        settings.addBoolean(HierarchicalClusterAssignerConfig.CFG_ENABLE_CLUSTER_COLOR, getEnableClusterColor());
        settings.addBoolean(HierarchicalClusterAssignerConfig.CFG_ENABLE_CLUSTER_LABELS, getEnableClusterLabels());


        settings.addBoolean(HierarchicalClusterAssignerConfig.CFG_ENABLE_VIEW_EDIT, getEnableViewEdit());
        settings.addBoolean(HierarchicalClusterAssignerConfig.CFG_ENABLE_TITLE_EDIT, getEnableTitleEdit());
        settings.addBoolean(HierarchicalClusterAssignerConfig.CFG_DISPLAY_FULLSCREEN_BUTTON, getDisplayFullscreenButton());
        settings.addBoolean(HierarchicalClusterAssignerConfig.CFG_ENABLE_NUM_CLUSTER_EDIT, getEnableNumClusterEdit());

        settings.addBoolean(HierarchicalClusterAssignerConfig.CFG_ENABLE_SELECTION, getEnableSelection());
        settings.addBoolean(HierarchicalClusterAssignerConfig.CFG_SHOW_CLEAR_SELECTION_BUTTON, getShowClearSelectionButton());
        settings.addBoolean(HierarchicalClusterAssignerConfig.CFG_SHOW_SELECTED_ONLY_TOGGLE, getShowSelectedOnlyToggle());

        settings.addBoolean(HierarchicalClusterAssignerConfig.CFG_SHOW_WARNINGS_IN_VIEW, getShowWarningsInView());

        settings.addBoolean(HierarchicalClusterAssignerConfig.CFG_ENABLE_ZOOM_AND_PANNING, getEnableZoomAndPanning());
        settings.addBoolean(HierarchicalClusterAssignerConfig.CFG_SHOW_ZOOM_RESET_BUTTON, getShowZoomResetButton());
        settings.addBoolean(HierarchicalClusterAssignerConfig.CFG_ENABLE_LOG_SCALE_TOGGLE, getEnableLogScaleToggle());

        settings.addBoolean(HierarchicalClusterAssignerConfig.CFG_ENABLE_CHANGE_ORIENTATION, getEnableChangeOrientation());

        settings.addStringArray(HierarchicalClusterAssignerConfig.CFG_COLOR_PALETTE, getColorPalette());

        settings.addBoolean(HierarchicalClusterAssignerConfig.CFG_SHOW_THRESHOLD_BAR, m_showThresholdBar);
        settings.addBoolean(HierarchicalClusterAssignerConfig.CFG_ENABLE_THRESHOLD_MODIFICATION, m_enableThresholdModification);

        settings.addBoolean(HierarchicalClusterAssignerConfig.CFG_ENABLE_AXIS_LABEL_EDIT, m_enableAxisLabelEdit);

        settings.addString(CFG_DATA_TABLE_ID, getDataTableID());
        settings.addStringArray(CFG_FILTER_IDS, m_filterIds);
        settings.addBoolean(CFG_RUNNING_IN_VIEW, m_runningInView);
        // Don't store JSON representation of tree
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadFromNodeSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        setGenerateImage(settings.getBoolean(HierarchicalClusterAssignerConfig.CFG_GENERATE_IMAGE));
        setImageWidth(settings.getInt(HierarchicalClusterAssignerConfig.CFG_IMAGE_WIDTH));
        setImageHeight(settings.getInt(HierarchicalClusterAssignerConfig.CFG_IMAGE_HEIGHT));

        setResizeToWindow(settings.getBoolean(HierarchicalClusterAssignerConfig.CFG_RESIZE_TO_WINDOW));
        setEnableClusterColor(settings.getBoolean(HierarchicalClusterAssignerConfig.CFG_ENABLE_CLUSTER_COLOR));
        setEnableClusterLabels(settings.getBoolean(HierarchicalClusterAssignerConfig.CFG_ENABLE_CLUSTER_LABELS));

        setEnableViewEdit(settings.getBoolean(HierarchicalClusterAssignerConfig.CFG_ENABLE_VIEW_EDIT));
        setEnableTitleEdit(settings.getBoolean(HierarchicalClusterAssignerConfig.CFG_ENABLE_TITLE_EDIT));
        setDisplayFullscreenButton(settings.getBoolean(HierarchicalClusterAssignerConfig.CFG_DISPLAY_FULLSCREEN_BUTTON));
        setEnableNumClusterEdit(settings.getBoolean(HierarchicalClusterAssignerConfig.CFG_ENABLE_NUM_CLUSTER_EDIT));

        setEnableSelection(settings.getBoolean(HierarchicalClusterAssignerConfig.CFG_ENABLE_SELECTION));
        setShowClearSelectionButton(settings.getBoolean(HierarchicalClusterAssignerConfig.CFG_SHOW_CLEAR_SELECTION_BUTTON));
        setShowSelectedOnlyToggle(settings.getBoolean(HierarchicalClusterAssignerConfig.CFG_SHOW_SELECTED_ONLY_TOGGLE));

        setShowWarningsInView(settings.getBoolean(HierarchicalClusterAssignerConfig.CFG_SHOW_WARNINGS_IN_VIEW));

        setEnableZoom(settings.getBoolean(HierarchicalClusterAssignerConfig.CFG_ENABLE_ZOOM_AND_PANNING));
        setShowZoomResetButton(settings.getBoolean(HierarchicalClusterAssignerConfig.CFG_SHOW_ZOOM_RESET_BUTTON));
        setEnableLogScaleToggle(settings.getBoolean(HierarchicalClusterAssignerConfig.CFG_ENABLE_LOG_SCALE_TOGGLE));

        setEnableChangeOrientation(settings.getBoolean(HierarchicalClusterAssignerConfig.CFG_ENABLE_CHANGE_ORIENTATION));

        setColorPalette(settings.getStringArray(HierarchicalClusterAssignerConfig.CFG_COLOR_PALETTE));

        setShowThresholdBar(settings.getBoolean(HierarchicalClusterAssignerConfig.CFG_SHOW_THRESHOLD_BAR));
        setEnableThresholdModification(settings.getBoolean(HierarchicalClusterAssignerConfig.CFG_ENABLE_THRESHOLD_MODIFICATION));

        setEnableAxisLabelEdit(settings.getBoolean(HierarchicalClusterAssignerConfig.CFG_ENABLE_AXIS_LABEL_EDIT));

        setDataTableID(settings.getString(CFG_DATA_TABLE_ID));
        setFilterIds(settings.getStringArray(CFG_FILTER_IDS));
        setRunningInView(settings.getBoolean(CFG_RUNNING_IN_VIEW));
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        if (this == obj) {
            return true;
        }

        final HierarchicalClusterAssignerRepresentation other = (HierarchicalClusterAssignerRepresentation) obj;
        return new EqualsBuilder()
                .append(m_generateImage, other.getGenerateImage())
                .append(m_imageWidth, other.getImageWidth())
                .append(m_imageHeight, other.getImageHeight())
                .append(m_resizeToWindow, other.getResizeToWindow())
                .append(m_enableClusterColor, other.getEnableClusterColor())
                .append(m_enableClusterLabels, other.getEnableClusterLabels())
                .append(m_enableViewEdit, other.getEnableViewEdit())
                .append(m_enableTitleEdit, other.getEnableTitleEdit())
                .append(m_displayFullscreenButton, other.getDisplayFullscreenButton())
                .append(m_enableNumClusterEdit, other.getEnableNumClusterEdit())
                .append(m_enableSelection, other.getEnableSelection())
                .append(m_showClearSelectionButton, other.getShowClearSelectionButton())
                .append(m_showSelectedOnlyToggle, other.getShowSelectedOnlyToggle())
                .append(m_showWarningsInView, other.getShowWarningsInView())
                .append(m_enableZoomAndPanning, other.getEnableZoomAndPanning())
                .append(m_showZoomResetButton, other.getShowZoomResetButton())
                .append(m_enableLocalScaleToggle, other.getEnableLogScaleToggle())
                .append(m_enableChangeOrientation, other.getEnableChangeOrientation())
                .append(m_showThresholdBar, other.getShowThresholdBar())
                .append(m_enableThresholdModification, other.getEnableThresholdModification())
                .append(m_enableAxisLabelEdit, other.getEnableAxisLabelEdit())
                .append(m_dataTableID, other.getDataTableID())
                .append(m_filterIds, other.getFilterIds())
                .append(m_runningInView, other.getRunningInView())
                .append(m_tree, other.getTree())
                .append(m_table, other.getTable())
                .isEquals() && Arrays.equals(m_colorPalette, other.getColorPalette());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(m_generateImage)
                .append(m_imageWidth)
                .append(m_imageHeight)
                .append(m_resizeToWindow)
                .append(m_enableClusterColor)
                .append(m_enableClusterLabels)
                .append(m_enableViewEdit)
                .append(m_enableTitleEdit)
                .append(m_displayFullscreenButton)
                .append(m_enableNumClusterEdit)
                .append(m_enableSelection)
                .append(m_showClearSelectionButton)
                .append(m_showSelectedOnlyToggle)
                .append(m_showWarningsInView)
                .append(m_enableZoomAndPanning)
                .append(m_showZoomResetButton)
                .append(m_enableLocalScaleToggle)
                .append(m_enableChangeOrientation)
                .append(m_colorPalette)
                .append(m_showThresholdBar)
                .append(m_enableThresholdModification)
                .append(m_enableAxisLabelEdit)
                .append(m_dataTableID)
                .append(m_filterIds)
                .append(m_runningInView)
                .append(m_tree)
                .append(m_table)
                .toHashCode();
    }

}
