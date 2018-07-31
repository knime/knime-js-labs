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

import org.knime.core.data.DataTableSpec;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;

/**
 *
 * @author Alison Walter
 */
public class HierarchicalClusterAssignerConfig {

    final static String CFG_HIDE_IN_WIZARD = "hideInWizard";
    final static boolean DEFAULT_HIDE_IN_WIZARD = false;
    private boolean m_hideInWizard = DEFAULT_HIDE_IN_WIZARD;

    final static String CFG_GENERATE_IMAGE = "generateImage";
    final static boolean DEFAULT_GENERATE_IMAGE = true;
    private boolean m_generateImage = DEFAULT_GENERATE_IMAGE;

    final static String CFG_IMAGE_WIDTH = "imageWidth";
    final static int DEFAULT_IMAGE_WIDTH = 800;
    private int m_imageWidth = DEFAULT_IMAGE_WIDTH;

    final static String CFG_IMAGE_HEIGHT = "imageHeight";
    final static int DEFAULT_IMAGE_HEIGHT = 600;
    private int m_imageHeight = DEFAULT_IMAGE_HEIGHT;

    final static String CFG_DISPLAY_FULLSCREEN_BUTTON = "displayFullscreenButton";
    final static boolean DEFAULT_DISPLAY_FULLSCREEN_BUTTON = true;
    private boolean m_displayFullscreenButton = DEFAULT_DISPLAY_FULLSCREEN_BUTTON;

    final static String CFG_RESIZE_TO_WINDOW = "resizeToWindow";
    final static boolean DEFAULT_RESIZE_TO_WINDOW = true;
    private boolean m_resizeToWindow = DEFAULT_RESIZE_TO_WINDOW;

    final static String CFG_TITLE = "title";
    final static String DEFAULT_TITLE = "";
    private String m_title = DEFAULT_TITLE;

    final static String CFG_SUBTITLE = "subtitle";
    final static String DEFAULT_SUBTITLE = "";
    private String m_subtitle = DEFAULT_SUBTITLE;

    final static String CFG_CUSTOM_CSS = "customCSS";
    final static String DEFAULT_CUSTOM_CSS = "";
    private String m_customCSS = DEFAULT_CUSTOM_CSS;

    final static String CFG_ENABLE_VIEW_EDIT = "enableViewEdit";
    final static boolean DEFAULT_ENABLE_VIEW_EDIT = true;
    private boolean m_enableViewEdit = DEFAULT_ENABLE_VIEW_EDIT;

    final static String CFG_ENABLE_TITLE_EDIT = "enableTitleEdit";
    final static boolean DEFAULT_ENABLE_TITLE_EDIT = true;
    private boolean m_enableTitleEdit = DEFAULT_ENABLE_TITLE_EDIT;

    final static String CFG_ENABLE_NUM_CLUSTER_EDIT = "enableNumClusterEdit";
    final static boolean DEFAULT_ENABLE_NUM_CLUSTER_EDIT = true;
    private boolean m_enableNumClusterEdit = DEFAULT_ENABLE_NUM_CLUSTER_EDIT;

    final static String CFG_ENABLE_CLUSTER_LABELS = "enableClusterLabels";
    final static boolean DEFAULT_ENABLE_CLUSTER_LABELS = true;
    private boolean m_enableClusterLabels = DEFAULT_ENABLE_CLUSTER_LABELS;

    final static String CFG_CLUSTER_LABELS = "clusterLabels";
    final static String[] DEFAULT_CLUSTER_LABELS = new String[0];
    private String[] m_clusterLabels = DEFAULT_CLUSTER_LABELS;

    final static String CFG_ENABLE_SELECTION = "enableSelection";
    final static boolean DEFAULT_ENABLE_SELECTION = true;
    private boolean m_enableSelection = DEFAULT_ENABLE_SELECTION;

    final static String CFG_PUBLISH_SELECTION_EVENTS = "publishSelectionEvents";
    final static boolean DEFAULT_PUBLISH_SELECTION_EVENTS = true;
    private boolean m_publishSelectionEvents = DEFAULT_PUBLISH_SELECTION_EVENTS;

    final static String CFG_SUBSCRIBE_SELECTION_EVENTS = "subscribeSelectionEvents";
    final static boolean DEFAULT_SUBSCRIBE_SELECTION_EVENTS = true;
    private boolean m_subscribeSelectionEvents = DEFAULT_SUBSCRIBE_SELECTION_EVENTS;

    final static String CFG_SELECTION = "selection";
    final static String[] DEFAULT_SELECTION = new String[0];
    private String[] m_selection = DEFAULT_SELECTION;

    final static String CFG_NUM_CLUSTERS = "numClusters";
    final static int DEFAULT_NUM_CLUSTERS = 1;
    private int m_numClusters = DEFAULT_NUM_CLUSTERS;

    final static String CFG_THRESHOLD = "threshold";
    final static double DEFAULT_THRESHOLD = 0;
    private double m_threshold = DEFAULT_THRESHOLD;

    final static String CFG_NUM_CLUSTERS_MODE = "numClustersMode";
    final static boolean DEFAULT_NUM_CLUSTERS_MODE = true;
    private boolean m_numClustersMode = DEFAULT_NUM_CLUSTERS_MODE;

    final static String CFG_ENABLE_CLUSTER_COLOR = "enableClusterColor";
    final static boolean DEFAULT_ENABLE_CLUSTER_COLOR = true;
    private boolean m_enableClusterColor = DEFAULT_ENABLE_CLUSTER_COLOR;

    final static String CFG_ENABLE_THRESHOLD_VALUE = "enableThresholdValue";
    final static boolean DEFAULT_ENABLE_THRESHOLD_VALUE = true;
    private boolean m_enableThresholdValue = DEFAULT_ENABLE_THRESHOLD_VALUE;

    final static String CFG_SELECTION_COLUMN_NAME = "selectionColumnName";
    final static String DEFAULT_SELECTION_COLUMN_NAME = "Selected (Hierarchical Cluster Assigner)";
    private String m_selectionColumnName = DEFAULT_SELECTION_COLUMN_NAME;

    final static String CFG_CLUSTER_COLUMN_NAME = "clusterColumnName";
    final static String DEFAULT_CLUSTER_COLUMN_NAME = "Cluster (Hierarchical Cluster Assigner)";
    private String m_clusterColumnName = DEFAULT_CLUSTER_COLUMN_NAME;

    final static String CFG_SHOW_WARNINGS_IN_VIEW = "showWarningsInView";
    final static boolean DEFAULT_SHOW_WARNINGS_IN_VIEW = true;
    private boolean m_showWarningsInView = DEFAULT_SHOW_WARNINGS_IN_VIEW;

    final static String CFG_ENABLE_ZOOM_MOUSE = "enableZoomMouse";
    final static boolean DEFAULT_ENABLE_ZOOM_MOUSE = true;
    private boolean m_enableZoomMouse = DEFAULT_ENABLE_ZOOM_MOUSE;

    final static String CFG_ENABLE_ZOOM_DRAG = "enableZoomDrag";
    final static boolean DEFAULT_ENABLE_ZOOM_DRAG = false;
    private boolean m_enableZoomDrag = DEFAULT_ENABLE_ZOOM_DRAG;

    final static String CFG_SHOW_ZOOM_RESET_BUTTON = "showZoomResetButton";
    final static boolean DEFAULT_SHOW_ZOOM_RESET_BUTTON = false;
    private boolean m_showZoomResetButton = DEFAULT_SHOW_ZOOM_RESET_BUTTON;

    final static String CFG_ENABLE_PANNING = "enablePanning";
    final static boolean DEFAULT_ENABLE_PANNING = true;
    private boolean m_enablePanning = DEFAULT_ENABLE_PANNING;

    final static String CFG_X_MIN = "xMin";
    final static String DEFAULT_X_MIN = null;
    private String m_xMin = DEFAULT_X_MIN;

    final static String CFG_X_MAX = "xMax";
    final static String DEFAULT_X_MAX = null;
    private String m_xMax = DEFAULT_X_MAX;

    final static String CFG_Y_MIN = "yMin";
    final static double DEFAULT_Y_MIN = 0.0;
    private double m_yMin = DEFAULT_Y_MIN;

    final static String CFG_Y_MAX = "yMax";
    final static double DEFAULT_Y_MAX = 0.0;
    private double m_yMax = DEFAULT_Y_MAX;

    final static String CFG_ENABLE_SCALE_OPTIONS = "enableScaleOptions";
    final static boolean DEFAULT_ENABLE_SCALE_OPTIONS = true;
    private boolean m_enableScaleOptions = DEFAULT_ENABLE_SCALE_OPTIONS;

    final static String CFG_SCALE_MODE = "scaleMode";
    final static String DEFAULT_SCALE_MODE = "linear";
    private String m_scaleMode = DEFAULT_SCALE_MODE;

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
     * @return the title
     */
    public String getTitle() {
        return m_title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(final String title) {
        m_title = title;
    }

    /**
     * @return the subtitle
     */
    public String getSubtitle() {
        return m_subtitle;
    }

    /**
     * @param subtitle the subtitle to set
     */
    public void setSubtitle(final String subtitle) {
        m_subtitle = subtitle;
    }

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
     * @return the enableCustomClusterLabels
     */
    public boolean getEnableClusterLabels() {
        return m_enableClusterLabels;
    }

    /**
     * @param enableCustomClusterLabels the enableCustomClusterLabels to set
     */
    public void setEnableClusterLabels(final boolean enableCustomClusterLabels) {
        m_enableClusterLabels = enableCustomClusterLabels;
    }

    /**
     * @return the clusterLabels
     */
    public String[] getClusterLabels() { return m_clusterLabels; }

    /**
     * @param clusterLabels the clusterLabels to set
     */
    public void setClusterLabels(final String[] clusterLabels) { m_clusterLabels = clusterLabels; }

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
     * @return the publishSelectionEvents
     */
    public boolean getPublishSelectionEvents() {
        return m_publishSelectionEvents;
    }

    /**
     * @param publishSelectionEvents the publishSelectionEvents to set
     */
    public void setPublishSelectionEvents(final boolean publishSelectionEvents) {
        m_publishSelectionEvents = publishSelectionEvents;
    }

    /**
     * @return the subscribeSelectionEvents
     */
    public boolean getSubscribeSelectionEvents() {
        return m_subscribeSelectionEvents;
    }

    /**
     * @param subscribeSelectionEvents the subscribeSelectionEvents to set
     */
    public void setSubscribeSelectionEvents(final boolean subscribeSelectionEvents) {
        m_subscribeSelectionEvents = subscribeSelectionEvents;
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

    /**
     * @return the numClusters
     */
    public int getNumClusters() {
        return m_numClusters;
    }

    /**
     * @param numClusters the numClusters to set
     */
    public void setNumClusters(final int numClusters) {
        m_numClusters = numClusters;
    }

    /**
     * @return the threshold
     */
    public double getThreshold() {
        return m_threshold;
    }

    /**
     * @param threshold the threshold to set
     */
    public void setThreshold(final double threshold) {
        m_threshold = threshold;
    }

    /**
     * @return the numClustersMode
     */
    public boolean getNumClustersMode() {
        return m_numClustersMode;
    }

    /**
     * @param numClustersMode the numClustersMode to set
     */
    public void setNumClustersMode(final boolean numClustersMode) {
        m_numClustersMode = numClustersMode;
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
     * @return the enableThresholdValue
     */
    public boolean getEnableThresholdValue() {
        return m_enableThresholdValue;
    }

    /**
     * @param enableThresholdValue the enableThresholdValue to set
     */
    public void setEnableThresholdValue(final boolean enableThresholdValue) {
        m_enableThresholdValue = enableThresholdValue;
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
     * @return the clusterColumnName
     */
    public String getClusterColumnName() {
        return m_clusterColumnName;
    }

    /**
     * @param clusterColumnName the clusterColumnName to set
     */
    public void setClusterColumnName(final String clusterColumnName) {
        m_clusterColumnName = clusterColumnName;
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
     * @return the enableZoomMouse
     */
    public boolean getEnableZoomMouse() {
        return m_enableZoomMouse;
    }

    /**
     * @param enableZoomMouse the enableZoomMouse to set
     */
    public void setEnableZoomMouse(final boolean enableZoomMouse) {
        m_enableZoomMouse = enableZoomMouse;
    }

    /**
     * @return the enableZoomDrag
     */
    public boolean getEnableZoomDrag() {
        return m_enableZoomDrag;
    }

    /**
     * @param enableZoomDrag the enableZoomDrag to set
     */
    public void setEnableZoomDrag(final boolean enableZoomDrag) {
        m_enableZoomDrag = enableZoomDrag;
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
    public double getYMin() {
        return m_yMin;
    }

    /**
     * @param yMin the yMin to set
     */
    public void setYMin(final double yMin) {
        m_yMin = yMin;
    }

    /**
     * @return the yMax
     */
    public double getYMax() {
        return m_yMax;
    }

    /**
     * @param yMax the yMax to set
     */
    public void setYMax(final double yMax) {
        m_yMax = yMax;
    }

    /**
     * @return the enableScaleOptions
     */
    public boolean getEnableScaleOptions() {
        return m_enableScaleOptions;
    }

    /**
     * @param enableScaleOptions the enableScaleOptions to set
     */
    public void setEnableScaleOptions(final boolean enableScaleOptions) {
        m_enableScaleOptions = enableScaleOptions;
    }

    /**
     * @return the scaleMode
     */
    public String getScaleMode() {
        return m_scaleMode;
    }

    /**
     * @param scaleMode the scaleMode to set
     */
    public void setScaleMode(final String scaleMode) {
        m_scaleMode = scaleMode;
    }

    /**
     * Saves current parameters to settings object.
     *
     * @param settings To save to.
     */
    public void saveSettings(final NodeSettingsWO settings) {
        settings.addBoolean(CFG_HIDE_IN_WIZARD, m_hideInWizard);
        settings.addBoolean(CFG_GENERATE_IMAGE, m_generateImage);
        settings.addInt(CFG_IMAGE_WIDTH, m_imageWidth);
        settings.addInt(CFG_IMAGE_HEIGHT, m_imageHeight);
        settings.addBoolean(CFG_DISPLAY_FULLSCREEN_BUTTON, m_displayFullscreenButton);
        settings.addBoolean(CFG_RESIZE_TO_WINDOW, m_resizeToWindow);
        settings.addString(CFG_TITLE, m_title);
        settings.addString(CFG_SUBTITLE, m_subtitle);
        settings.addString(CFG_CUSTOM_CSS, m_customCSS);
        settings.addBoolean(CFG_ENABLE_VIEW_EDIT, m_enableViewEdit);
        settings.addBoolean(CFG_ENABLE_TITLE_EDIT, m_enableTitleEdit);
        settings.addBoolean(CFG_ENABLE_NUM_CLUSTER_EDIT, m_enableNumClusterEdit);
        settings.addBoolean(CFG_ENABLE_CLUSTER_LABELS, m_enableClusterLabels);
        settings.addStringArray(CFG_CLUSTER_LABELS, m_clusterLabels);
        settings.addBoolean(CFG_ENABLE_SELECTION, m_enableSelection);
        settings.addBoolean(CFG_PUBLISH_SELECTION_EVENTS, m_publishSelectionEvents);
        settings.addBoolean(CFG_SUBSCRIBE_SELECTION_EVENTS, m_subscribeSelectionEvents);
        settings.addStringArray(CFG_SELECTION, m_selection);
        settings.addInt(CFG_NUM_CLUSTERS, m_numClusters);
        settings.addDouble(CFG_THRESHOLD, m_threshold);
        settings.addBoolean(CFG_NUM_CLUSTERS_MODE, m_numClustersMode);
        settings.addBoolean(CFG_ENABLE_CLUSTER_COLOR, m_enableClusterColor);
        settings.addBoolean(CFG_ENABLE_THRESHOLD_VALUE, m_enableThresholdValue);
        settings.addString(CFG_SELECTION_COLUMN_NAME, m_selectionColumnName);
        settings.addString(CFG_CLUSTER_COLUMN_NAME, m_clusterColumnName);
        settings.addBoolean(CFG_SHOW_WARNINGS_IN_VIEW, m_showWarningsInView);
        settings.addBoolean(CFG_ENABLE_ZOOM_MOUSE, m_enableZoomMouse);
        settings.addBoolean(CFG_ENABLE_ZOOM_DRAG, m_enableZoomDrag);
        settings.addBoolean(CFG_SHOW_ZOOM_RESET_BUTTON, m_showZoomResetButton);
        settings.addBoolean(CFG_ENABLE_PANNING, m_enablePanning);
        settings.addString(CFG_X_MIN, m_xMin);
        settings.addString(CFG_X_MAX, m_xMax);
        settings.addDouble(CFG_Y_MIN, m_yMin);
        settings.addDouble(CFG_Y_MAX, m_yMax);
        settings.addBoolean(CFG_ENABLE_SCALE_OPTIONS, m_enableScaleOptions);
        settings.addString(CFG_SCALE_MODE, m_scaleMode);
    }

    /**
     * Loads parameters in NodeModel.
     *
     * @param settings To load from.
     * @throws InvalidSettingsException If incomplete or wrong.
     */
    public void loadSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        m_hideInWizard = settings.getBoolean(CFG_HIDE_IN_WIZARD);
        m_generateImage = settings.getBoolean(CFG_GENERATE_IMAGE);
        m_imageWidth = settings.getInt(CFG_IMAGE_WIDTH);
        m_imageHeight = settings.getInt(CFG_IMAGE_HEIGHT);
        m_displayFullscreenButton = settings.getBoolean(CFG_DISPLAY_FULLSCREEN_BUTTON);
        m_resizeToWindow = settings.getBoolean(CFG_RESIZE_TO_WINDOW);
        m_title = settings.getString(CFG_TITLE);
        m_subtitle = settings.getString(CFG_SUBTITLE);
        m_customCSS = settings.getString(CFG_CUSTOM_CSS);
        m_enableViewEdit = settings.getBoolean(CFG_ENABLE_VIEW_EDIT);
        m_enableTitleEdit = settings.getBoolean(CFG_ENABLE_TITLE_EDIT);
        m_enableNumClusterEdit = settings.getBoolean(CFG_ENABLE_NUM_CLUSTER_EDIT);
        m_enableClusterLabels = settings.getBoolean(CFG_ENABLE_CLUSTER_LABELS);
        m_clusterLabels = settings.getStringArray(CFG_CLUSTER_LABELS);
        m_enableSelection = settings.getBoolean(CFG_ENABLE_SELECTION);
        m_publishSelectionEvents = settings.getBoolean(CFG_PUBLISH_SELECTION_EVENTS);
        m_subscribeSelectionEvents = settings.getBoolean(CFG_SUBSCRIBE_SELECTION_EVENTS);
        m_selection = settings.getStringArray(CFG_SELECTION);
        m_enableClusterColor = settings.getBoolean(CFG_ENABLE_CLUSTER_COLOR);
        m_enableThresholdValue = settings.getBoolean(CFG_ENABLE_THRESHOLD_VALUE);
        m_selectionColumnName = settings.getString(CFG_SELECTION_COLUMN_NAME);
        m_clusterColumnName = settings.getString(CFG_CLUSTER_COLUMN_NAME);
        m_showWarningsInView = settings.getBoolean(CFG_SHOW_WARNINGS_IN_VIEW);
        m_enableZoomMouse = settings.getBoolean(CFG_ENABLE_ZOOM_MOUSE);
        m_enableZoomDrag = settings.getBoolean(CFG_ENABLE_ZOOM_DRAG);
        m_showZoomResetButton = settings.getBoolean(CFG_SHOW_ZOOM_RESET_BUTTON);
        m_enablePanning = settings.getBoolean(CFG_ENABLE_PANNING);
        m_xMin = settings.getString(CFG_X_MIN);
        m_xMax = settings.getString(CFG_X_MAX);
        m_yMin = settings.getDouble(CFG_Y_MIN);
        m_yMax = settings.getDouble(CFG_Y_MAX);
        m_enableScaleOptions = settings.getBoolean(CFG_ENABLE_SCALE_OPTIONS);
        m_scaleMode = settings.getString(CFG_SCALE_MODE);

        m_numClusters = settings.getInt(CFG_NUM_CLUSTERS);
        if (m_numClusters < 1) {
            throw new InvalidSettingsException(
                "Invalid number of clusters: " + m_numClusters + ". There must be at least 1 cluster.");
        }
        m_threshold = settings.getDouble(CFG_THRESHOLD);
        if (m_threshold < 0) {
            throw new InvalidSettingsException("Invalid threshold, " + m_threshold + ". Threshold cannot be negative");
        }

        m_numClustersMode = settings.getBoolean(CFG_NUM_CLUSTERS_MODE);
    }

    /**
     * Loads parameters in Dialog.
     *
     * @param settings To load from.
     * @param spec The spec from the incoming data table
     */
    public void loadSettingsForDialog(final NodeSettingsRO settings, final DataTableSpec spec) {
        m_hideInWizard = settings.getBoolean(CFG_HIDE_IN_WIZARD, DEFAULT_HIDE_IN_WIZARD);
        m_generateImage = settings.getBoolean(CFG_GENERATE_IMAGE, DEFAULT_GENERATE_IMAGE);
        m_imageWidth = settings.getInt(CFG_IMAGE_WIDTH, DEFAULT_IMAGE_WIDTH);
        m_imageHeight = settings.getInt(CFG_IMAGE_HEIGHT, DEFAULT_IMAGE_HEIGHT);
        m_displayFullscreenButton = settings.getBoolean(CFG_DISPLAY_FULLSCREEN_BUTTON, DEFAULT_DISPLAY_FULLSCREEN_BUTTON);
        m_resizeToWindow = settings.getBoolean(CFG_RESIZE_TO_WINDOW, DEFAULT_RESIZE_TO_WINDOW);
        m_title = settings.getString(CFG_TITLE, DEFAULT_TITLE);
        m_subtitle = settings.getString(CFG_SUBTITLE, DEFAULT_SUBTITLE);
        m_customCSS = settings.getString(CFG_CUSTOM_CSS, DEFAULT_CUSTOM_CSS);
        m_enableViewEdit = settings.getBoolean(CFG_ENABLE_VIEW_EDIT, DEFAULT_ENABLE_VIEW_EDIT);
        m_enableTitleEdit = settings.getBoolean(CFG_ENABLE_TITLE_EDIT, DEFAULT_ENABLE_TITLE_EDIT);
        m_enableNumClusterEdit = settings.getBoolean(CFG_ENABLE_NUM_CLUSTER_EDIT, DEFAULT_ENABLE_NUM_CLUSTER_EDIT);
        m_enableClusterLabels = settings.getBoolean(CFG_ENABLE_CLUSTER_LABELS, DEFAULT_ENABLE_CLUSTER_LABELS);
        m_clusterLabels = settings.getStringArray(CFG_CLUSTER_LABELS, DEFAULT_CLUSTER_LABELS);
        m_enableSelection = settings.getBoolean(CFG_ENABLE_SELECTION, DEFAULT_ENABLE_SELECTION);
        m_publishSelectionEvents = settings.getBoolean(CFG_PUBLISH_SELECTION_EVENTS, DEFAULT_PUBLISH_SELECTION_EVENTS);
        m_subscribeSelectionEvents = settings.getBoolean(CFG_SUBSCRIBE_SELECTION_EVENTS, DEFAULT_SUBSCRIBE_SELECTION_EVENTS);
        m_selection = settings.getStringArray(CFG_SELECTION, DEFAULT_SELECTION);
        m_enableClusterColor = settings.getBoolean(CFG_ENABLE_CLUSTER_COLOR, DEFAULT_ENABLE_CLUSTER_COLOR);
        m_enableThresholdValue = settings.getBoolean(CFG_ENABLE_THRESHOLD_VALUE, DEFAULT_ENABLE_THRESHOLD_VALUE);
        m_numClusters = settings.getInt(CFG_NUM_CLUSTERS, DEFAULT_NUM_CLUSTERS);
        m_threshold = settings.getDouble(CFG_THRESHOLD, DEFAULT_THRESHOLD);
        m_numClustersMode = settings.getBoolean(CFG_NUM_CLUSTERS_MODE, DEFAULT_NUM_CLUSTERS_MODE);
        m_selectionColumnName = settings.getString(CFG_SELECTION_COLUMN_NAME, DEFAULT_SELECTION_COLUMN_NAME);
        m_clusterColumnName = settings.getString(CFG_CLUSTER_COLUMN_NAME, DEFAULT_CLUSTER_COLUMN_NAME);
        m_showWarningsInView = settings.getBoolean(CFG_SHOW_WARNINGS_IN_VIEW, DEFAULT_SHOW_WARNINGS_IN_VIEW);
        m_enableZoomMouse = settings.getBoolean(CFG_ENABLE_ZOOM_MOUSE, DEFAULT_ENABLE_ZOOM_MOUSE);
        m_enableZoomDrag = settings.getBoolean(CFG_ENABLE_ZOOM_DRAG, DEFAULT_ENABLE_ZOOM_DRAG);
        m_showZoomResetButton = settings.getBoolean(CFG_SHOW_ZOOM_RESET_BUTTON, DEFAULT_SHOW_ZOOM_RESET_BUTTON);
        m_enablePanning = settings.getBoolean(CFG_ENABLE_PANNING, DEFAULT_ENABLE_PANNING);
        m_xMin = settings.getString(CFG_X_MIN, DEFAULT_X_MIN);
        m_xMax = settings.getString(CFG_X_MAX, DEFAULT_X_MAX);
        m_yMin = settings.getDouble(CFG_Y_MIN, DEFAULT_Y_MIN);
        m_yMax = settings.getDouble(CFG_Y_MAX, DEFAULT_Y_MAX);
        m_enableScaleOptions = settings.getBoolean(CFG_ENABLE_SCALE_OPTIONS, DEFAULT_ENABLE_SCALE_OPTIONS);
        m_scaleMode = settings.getString(CFG_SCALE_MODE, DEFAULT_SCALE_MODE);
    }
}
