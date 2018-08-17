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

    final static String CFG_ENABLE_SELECTION = "enableSelection";
    final static boolean DEFAULT_ENABLE_SELECTION = true;
    private boolean m_enableSelection = DEFAULT_ENABLE_SELECTION;

    final static String CFG_PUBLISH_SELECTION_EVENTS = "publishSelectionEvents";
    final static boolean DEFAULT_PUBLISH_SELECTION_EVENTS = true;
    private boolean m_publishSelectionEvents = DEFAULT_PUBLISH_SELECTION_EVENTS;

    final static String CFG_SUBSCRIBE_SELECTION_EVENTS = "subscribeSelectionEvents";
    final static boolean DEFAULT_SUBSCRIBE_SELECTION_EVENTS = true;
    private boolean m_subscribeSelectionEvents = DEFAULT_SUBSCRIBE_SELECTION_EVENTS;

    final static String CFG_SHOW_CLEAR_SELECTION_BUTTON = "showClearSelectionButton";
    final static boolean DEFAULT_SHOW_CLEAR_SELECTION_BUTTON = true;
    private boolean m_showClearSelectionButton = DEFAULT_SHOW_CLEAR_SELECTION_BUTTON;

    final static String CFG_NUM_CLUSTERS = "numClusters";
    final static int DEFAULT_NUM_CLUSTERS = 1;
    private int m_numClusters = DEFAULT_NUM_CLUSTERS;

    final static String CFG_THRESHOLD = "threshold";
    final static double DEFAULT_THRESHOLD = 0;
    private double m_threshold = DEFAULT_THRESHOLD;

    final static String CFG_NUM_CLUSTERS_MODE = "numClustersMode";
    final static boolean DEFAULT_NUM_CLUSTERS_MODE = true;
    private boolean m_numClustersMode = DEFAULT_NUM_CLUSTERS_MODE;

    final static String CFG_USE_NORMALIZED_DISTANCES = "useNormalizedDistances";
    final static boolean DEFAULT_USE_NORMALIZED_DISTANCES = true;
    private boolean m_useNormalizedDistances = DEFAULT_USE_NORMALIZED_DISTANCES;

    final static String CFG_NORMALIZED_THRESHOLD = "normalizedThreshold";
    final static double DEFAULT_NORMALIZED_THRESHOLD = 0;
    private double m_normalizedThreshold = DEFAULT_NORMALIZED_THRESHOLD;

    final static String CFG_ENABLE_CLUSTER_COLOR = "enableClusterColor";
    final static boolean DEFAULT_ENABLE_CLUSTER_COLOR = true;
    private boolean m_enableClusterColor = DEFAULT_ENABLE_CLUSTER_COLOR;

    final static String CFG_SELECTION_COLUMN_NAME = "selectionColumnName";
    final static String DEFAULT_SELECTION_COLUMN_NAME = "Selected (Hierarchical Cluster Assigner)";
    private String m_selectionColumnName = DEFAULT_SELECTION_COLUMN_NAME;

    final static String CFG_CLUSTER_COLUMN_NAME = "clusterColumnName";
    final static String DEFAULT_CLUSTER_COLUMN_NAME = "Cluster (Hierarchical Cluster Assigner)";
    private String m_clusterColumnName = DEFAULT_CLUSTER_COLUMN_NAME;

    final static String CFG_SHOW_WARNINGS_IN_VIEW = "showWarningsInView";
    final static boolean DEFAULT_SHOW_WARNINGS_IN_VIEW = true;
    private boolean m_showWarningsInView = DEFAULT_SHOW_WARNINGS_IN_VIEW;

    final static String CFG_ENABLE_ZOOM_AND_PANNING = "enableZoomAndPanning";
    final static boolean DEFAULT_ENABLE_ZOOM_AND_PANNING = true;
    private boolean m_enableZoomAndPanning = DEFAULT_ENABLE_ZOOM_AND_PANNING;

    final static String CFG_SHOW_ZOOM_RESET_BUTTON = "showZoomResetButton";
    final static boolean DEFAULT_SHOW_ZOOM_RESET_BUTTON = false;
    private boolean m_showZoomResetButton = DEFAULT_SHOW_ZOOM_RESET_BUTTON;

    final static String CFG_ENABLE_LOG_SCALE_TOGGLE = "enableLogScaleToggle";
    final static boolean DEFAULT_ENABLE_LOG_SCALE_TOGGLE = true;
    private boolean m_enableLogScaleToggle = DEFAULT_ENABLE_LOG_SCALE_TOGGLE;

    final static String CFG_USE_LOG_SCALE = "useLogScale";
    final static boolean DEFAULT_USE_LOG_SCALE = false;
    private boolean m_useLogScale = DEFAULT_USE_LOG_SCALE;

    final static String CFG_ENABLE_CHANGE_ORIENTATION = "enableChangeOrientation";
    final static boolean DEFAULT_ENABLE_CHANGE_ORIENTATION = true;
    private boolean m_enableChangeOrientation = DEFAULT_ENABLE_CHANGE_ORIENTATION;

    final static String CFG_ORIENTATION = "orientation";
    final static HierarchicalClusterAssignerOrientation DEFAULT_ORIENTATION =
        HierarchicalClusterAssignerOrientation.VERTICAL;
    private HierarchicalClusterAssignerOrientation m_orientation = DEFAULT_ORIENTATION;

    final static String CFG_COLOR_PALETTE = "colorPalette";
    final static String[] DEFAULT_COLOR_PALETTE = ColorPaletteUtil.PALETTE_SET1;
    private String[] m_colorPalette = DEFAULT_COLOR_PALETTE;

    final static String CFG_SUBSCRIBE_FILTER_EVENTS = "subscribeFilterEvents";
    final static boolean DEFAULT_SUBSCRIBE_FILTER_EVENTS = true;
    private boolean m_subscribeFilterEvents = DEFAULT_SUBSCRIBE_FILTER_EVENTS;

    final static String CFG_SHOW_THRESHOLD_BAR = "showThresholdBar";
    final static boolean DEFAULT_SHOW_THRESHOLD_BAR = true;
    private boolean m_showThresholdBar = DEFAULT_SHOW_THRESHOLD_BAR;

    final static String CFG_ENABLE_THRESHOLD_MODIFICATION = "enableThresholdModification";
    final static boolean DEFAULT_ENABLE_THRESHOLD_MODIFICATION = true;
    private boolean m_enableThresholdModification = DEFAULT_ENABLE_THRESHOLD_MODIFICATION;

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

        // The dialog only stores one threshold, so if
        // one is set the other must be the default
        m_normalizedThreshold = DEFAULT_NORMALIZED_THRESHOLD;
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
     * @return the useNormalizedDistances
     */
    public boolean getUseNormalizedDistances() {
        return m_useNormalizedDistances;
    }

    /**
     * @param useNormalizedDistances the useNormalizedDistances to set
     */
    public void setUseNormalizedDistances(final boolean useNormalizedDistances) {
        m_useNormalizedDistances = useNormalizedDistances;
    }

    /**
     * @return the normalizedThreshold
     */
    public double getNormalizedThreshold() {
        return m_normalizedThreshold;
    }

    /**
     * @param normalizedThreshold the normalizedThreshold to set
     */
    public void setNormalizedThreshold(final double normalizedThreshold) {
        m_normalizedThreshold = normalizedThreshold;

        // The dialog only stores one threshold, so if
        // one is set the other must be the default
        m_threshold = DEFAULT_THRESHOLD;
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
     * @return the enableZoomAndPanning
     */
    public boolean getEnableZoomAndPanning() {
        return m_enableZoomAndPanning;
    }

    /**
     * @param enableZoomAndPanning the enableZoomAndPanning to set
     */
    public void setEnableZoomAndPanning(final boolean enableZoomAndPanning) {
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
        return m_enableLogScaleToggle;
    }

    /**
     * @param enableLogScaleToggle the enableLogScaleToggle to set
     */
    public void setEnableLogScaleToggle(final boolean enableLogScaleToggle) {
        m_enableLogScaleToggle = enableLogScaleToggle;
    }

    /**
     * @return the useLogScale
     */
    public boolean getUseLogScale() {
        return m_useLogScale;
    }

    /**
     * @param useLogScale the useLogScale to set
     */
    public void setUseLogScale(final boolean useLogScale) {
        m_useLogScale = useLogScale;
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
     * @return the orientation
     */
    public HierarchicalClusterAssignerOrientation getOrientation() {
        return m_orientation;
    }

    /**
     * @param orientation the orientation to set
     */
    public void setOrientation(final HierarchicalClusterAssignerOrientation orientation) {
        m_orientation = orientation;
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
     * @return the subscribeFilterEvents
     */
    public boolean getSubscribeFilterEvents() {
        return m_subscribeFilterEvents;
    }

    /**
     * @param subscribeFilterEvents the subscribeFilterEvents to set
     */
    public void setSubscribeFilterEvents(final boolean subscribeFilterEvents) {
        m_subscribeFilterEvents = subscribeFilterEvents;
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
        settings.addBoolean(CFG_ENABLE_SELECTION, m_enableSelection);
        settings.addBoolean(CFG_PUBLISH_SELECTION_EVENTS, m_publishSelectionEvents);
        settings.addBoolean(CFG_SUBSCRIBE_SELECTION_EVENTS, m_subscribeSelectionEvents);
        settings.addBoolean(CFG_SHOW_CLEAR_SELECTION_BUTTON, m_showClearSelectionButton);
        settings.addInt(CFG_NUM_CLUSTERS, m_numClusters);
        settings.addDouble(CFG_THRESHOLD, m_threshold);
        settings.addBoolean(CFG_NUM_CLUSTERS_MODE, m_numClustersMode);
        settings.addBoolean(CFG_USE_NORMALIZED_DISTANCES, m_useNormalizedDistances);
        settings.addDouble(CFG_NORMALIZED_THRESHOLD, m_normalizedThreshold);
        settings.addBoolean(CFG_ENABLE_CLUSTER_COLOR, m_enableClusterColor);
        settings.addString(CFG_SELECTION_COLUMN_NAME, m_selectionColumnName);
        settings.addString(CFG_CLUSTER_COLUMN_NAME, m_clusterColumnName);
        settings.addBoolean(CFG_SHOW_WARNINGS_IN_VIEW, m_showWarningsInView);
        settings.addBoolean(CFG_ENABLE_ZOOM_AND_PANNING, m_enableZoomAndPanning);
        settings.addBoolean(CFG_SHOW_ZOOM_RESET_BUTTON, m_showZoomResetButton);
        settings.addBoolean(CFG_ENABLE_LOG_SCALE_TOGGLE, m_enableLogScaleToggle);
        settings.addBoolean(CFG_USE_LOG_SCALE, m_useLogScale);
        settings.addBoolean(CFG_ENABLE_CHANGE_ORIENTATION, m_enableChangeOrientation);
        settings.addString(CFG_ORIENTATION, m_orientation.toValue());
        settings.addStringArray(CFG_COLOR_PALETTE, m_colorPalette);
        settings.addBoolean(CFG_SUBSCRIBE_FILTER_EVENTS, m_subscribeFilterEvents);
        settings.addBoolean(CFG_SHOW_THRESHOLD_BAR, m_showThresholdBar);
        settings.addBoolean(CFG_ENABLE_THRESHOLD_MODIFICATION, m_enableThresholdModification);
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
        m_enableSelection = settings.getBoolean(CFG_ENABLE_SELECTION);
        m_publishSelectionEvents = settings.getBoolean(CFG_PUBLISH_SELECTION_EVENTS);
        m_subscribeSelectionEvents = settings.getBoolean(CFG_SUBSCRIBE_SELECTION_EVENTS);
        m_showClearSelectionButton = settings.getBoolean(CFG_SHOW_CLEAR_SELECTION_BUTTON);
        m_enableClusterColor = settings.getBoolean(CFG_ENABLE_CLUSTER_COLOR);
        m_selectionColumnName = settings.getString(CFG_SELECTION_COLUMN_NAME);
        m_clusterColumnName = settings.getString(CFG_CLUSTER_COLUMN_NAME);
        m_showWarningsInView = settings.getBoolean(CFG_SHOW_WARNINGS_IN_VIEW);
        m_enableZoomAndPanning = settings.getBoolean(CFG_ENABLE_ZOOM_AND_PANNING);
        m_showZoomResetButton = settings.getBoolean(CFG_SHOW_ZOOM_RESET_BUTTON);
        m_enableLogScaleToggle = settings.getBoolean(CFG_ENABLE_LOG_SCALE_TOGGLE);
        m_useLogScale = settings.getBoolean(CFG_USE_LOG_SCALE);
        m_enableChangeOrientation = settings.getBoolean(CFG_ENABLE_CHANGE_ORIENTATION);
        m_orientation = HierarchicalClusterAssignerOrientation.forValue(settings.getString(CFG_ORIENTATION));
        m_colorPalette = settings.getStringArray(CFG_COLOR_PALETTE);
        m_subscribeFilterEvents = settings.getBoolean(CFG_SUBSCRIBE_FILTER_EVENTS);
        m_showThresholdBar = settings.getBoolean(CFG_SHOW_THRESHOLD_BAR);
        m_enableThresholdModification = settings.getBoolean(CFG_ENABLE_THRESHOLD_MODIFICATION);

        m_numClusters = settings.getInt(CFG_NUM_CLUSTERS);
        if (m_numClusters < 1) {
            throw new InvalidSettingsException(
                "Invalid number of clusters: " + m_numClusters + ". There must be at least 1 cluster.");
        }
        m_useNormalizedDistances = settings.getBoolean(CFG_USE_NORMALIZED_DISTANCES);
        m_normalizedThreshold = settings.getDouble(CFG_NORMALIZED_THRESHOLD);
        if (m_useNormalizedDistances && (m_normalizedThreshold > 1 || m_normalizedThreshold < 0)) {
            throw new InvalidSettingsException(
                "Invalid normalized threshold " + m_normalizedThreshold + ". Normalized threshold must be between 0 and 1");
        }
        m_threshold = settings.getDouble(CFG_THRESHOLD);
        if (m_threshold < 0 && !m_useNormalizedDistances) {
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
        m_enableSelection = settings.getBoolean(CFG_ENABLE_SELECTION, DEFAULT_ENABLE_SELECTION);
        m_publishSelectionEvents = settings.getBoolean(CFG_PUBLISH_SELECTION_EVENTS, DEFAULT_PUBLISH_SELECTION_EVENTS);
        m_subscribeSelectionEvents = settings.getBoolean(CFG_SUBSCRIBE_SELECTION_EVENTS, DEFAULT_SUBSCRIBE_SELECTION_EVENTS);
        m_showClearSelectionButton = settings.getBoolean(CFG_SHOW_CLEAR_SELECTION_BUTTON, DEFAULT_SHOW_CLEAR_SELECTION_BUTTON);
        m_enableClusterColor = settings.getBoolean(CFG_ENABLE_CLUSTER_COLOR, DEFAULT_ENABLE_CLUSTER_COLOR);
        m_numClusters = settings.getInt(CFG_NUM_CLUSTERS, DEFAULT_NUM_CLUSTERS);
        m_threshold = settings.getDouble(CFG_THRESHOLD, DEFAULT_THRESHOLD);
        m_numClustersMode = settings.getBoolean(CFG_NUM_CLUSTERS_MODE, DEFAULT_NUM_CLUSTERS_MODE);
        m_useNormalizedDistances = settings.getBoolean(CFG_USE_NORMALIZED_DISTANCES, DEFAULT_USE_NORMALIZED_DISTANCES);
        m_normalizedThreshold = settings.getDouble(CFG_NORMALIZED_THRESHOLD, DEFAULT_NORMALIZED_THRESHOLD);
        m_selectionColumnName = settings.getString(CFG_SELECTION_COLUMN_NAME, DEFAULT_SELECTION_COLUMN_NAME);
        m_clusterColumnName = settings.getString(CFG_CLUSTER_COLUMN_NAME, DEFAULT_CLUSTER_COLUMN_NAME);
        m_showWarningsInView = settings.getBoolean(CFG_SHOW_WARNINGS_IN_VIEW, DEFAULT_SHOW_WARNINGS_IN_VIEW);
        m_enableZoomAndPanning = settings.getBoolean(CFG_ENABLE_ZOOM_AND_PANNING, DEFAULT_ENABLE_ZOOM_AND_PANNING);
        m_showZoomResetButton = settings.getBoolean(CFG_SHOW_ZOOM_RESET_BUTTON, DEFAULT_SHOW_ZOOM_RESET_BUTTON);
        m_enableLogScaleToggle = settings.getBoolean(CFG_ENABLE_LOG_SCALE_TOGGLE, DEFAULT_ENABLE_LOG_SCALE_TOGGLE);
        m_useLogScale = settings.getBoolean(CFG_USE_LOG_SCALE, DEFAULT_USE_LOG_SCALE);
        m_enableChangeOrientation = settings.getBoolean(CFG_ENABLE_CHANGE_ORIENTATION, DEFAULT_ENABLE_CHANGE_ORIENTATION);
        m_orientation = HierarchicalClusterAssignerOrientation
            .forValue(settings.getString(CFG_ORIENTATION, DEFAULT_ORIENTATION.toValue()));
        m_colorPalette = settings.getStringArray(CFG_COLOR_PALETTE, DEFAULT_COLOR_PALETTE);
        m_subscribeFilterEvents = settings.getBoolean(CFG_SUBSCRIBE_FILTER_EVENTS, DEFAULT_SUBSCRIBE_FILTER_EVENTS);
        m_showThresholdBar = settings.getBoolean(CFG_SHOW_THRESHOLD_BAR, DEFAULT_SHOW_THRESHOLD_BAR);
        m_enableThresholdModification = settings.getBoolean(CFG_ENABLE_THRESHOLD_MODIFICATION, DEFAULT_ENABLE_THRESHOLD_MODIFICATION);
    }
}
