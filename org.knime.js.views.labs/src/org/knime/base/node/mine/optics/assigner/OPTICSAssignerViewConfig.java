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
 * ---------------------------------------------------------------------
 *
 * History
 *   Nov 11, 2008 (wiswedel): created
 */
package org.knime.base.node.mine.optics.assigner;

import static org.knime.core.node.util.CheckUtils.checkSetting;

import org.knime.core.data.DataTableSpec;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;

/**
 * Settings proxy for distance matrix calculate node.
 *
 * @author Anastasia Zhukova, University of Konstanz
 * @author Christian Albrecht, KNIME AG, Zurich, Switzerland, University of Konstanz
 */
final class OPTICSAssignerViewConfig {

    static final boolean DEFAULT_HIDE_IN_WIZARD = false;
    static final Double DEFAULT_EPS_PRIME = 0.65;
    static final int DEFAULT_MAX_ROWS = 2000;
    static final int DEFAULT_WIDTH = 800;
    static final int DEFAULT_HEIGHT = 600;
    static final boolean DEFAULT_DISPLAY_FULLSCREEN_BUTTON = true;
    static final boolean DEFAULT_PUBLISH_SELECTION = true;
    static final boolean DEFAULT_ENABLE_SHOW_SELECTED_ONLY = true;
    static final boolean DEFAULT_SUBSCRIBE_SELECTION = true;
    static final boolean DEFAULT_SHOW_WARNING_IN_VIEW = true;
    static final boolean DEFAULT_CALC_EPS_PRIME_MEAN = true;
    static final boolean DEFAULT_CALC_EPS_PRIME_MEDIAN = false;

    static final String HIDE_IN_WIZARD = "hideInWizard";
    static final String EPSILON_PR = "epsilonPrime";
    static final String RESIZE_TO_WINDOW = "resizeToWindow";
    static final String SHOW_LEGEND = "showLegend";
    static final String GENERATE_IMAGE = "generateImage";
    static final String CFG_DISPLAY_FULLSCREEN_BUTTON = "displayFullscreenButton";
    static final String ENABLE_CONFIG = "enableViewConfiguration";
    static final String ENABLE_TTILE_CHANGE = "enableTitleChange";
    static final String ENABLE_EPSILON_PRIME_CHANGE = "enableEpsilonPrimeChange";
    static final String ENABLE_SELECTION = "enableSelection";
    static final String ENABLE_RECTANGLE_SELECTION = "enableRectangleSelection";
    static final String CFG_PUBLISH_SELECTION = "publishSelection";
    static final String CFG_ENABLE_SHOW_SELECTED_ONLY = "enableShowSelectedOnly";
    static final String CFG_SUBSCRIBE_SELECTION = "subscribeSelection";
    static final String CHART_TITLE = "chartTitle";
    static final String CHART_SUBTITLE = "chartSubtitle";
    static final String MAX_ROWS = "maxRows";
    static final String IMAGE_WIDTH = "imageWidth";
    static final String IMAGE_HEIGHT = "imageHeight";
    static final String SHOW_WARNING_IN_VIEW = "showWarningInView";
    static final String CALC_EPS_PRIME_MEDIAN = "epsPrimeMedian";
    static final String CALC_EPS_PRIME_MEAN = "epsPrimeMean";
    static final String MANUAL_EPS_PRIME = "manualEpsPrime";
    static final String EPS_CALC_METHOD = "epsCalcMethod";
    static final String WAS_REDRAWN = "wasRedrawn";

    private boolean m_hideInWizard = DEFAULT_HIDE_IN_WIZARD;
    private Double m_eps_pr = DEFAULT_EPS_PRIME;
    private boolean m_generateImage = true;
    private boolean m_showLegend = true;
    private boolean m_displayFullscreenButton = DEFAULT_DISPLAY_FULLSCREEN_BUTTON;
    private boolean m_resizeToWindow = true;
    private boolean m_enableViewConfiguration = true;
    private boolean m_enableTitleChange = true;
    private boolean m_enableEpsilonPrimeChange = true;
    private boolean m_enableSwitchLegend = true;
    private boolean m_enableSelection = true;
    private boolean m_enableRectangleSelection = true;
    private boolean m_publishSelection = DEFAULT_PUBLISH_SELECTION;
    private boolean m_subscribeSelection = DEFAULT_SUBSCRIBE_SELECTION;
    private boolean m_enableShowSelectedOnly = DEFAULT_ENABLE_SHOW_SELECTED_ONLY;
    private int m_maxRows = DEFAULT_MAX_ROWS;
    private String m_chartTitle;
    private String m_chartSubtitle;
    private int m_imageWidth = DEFAULT_WIDTH;
    private int m_imageHeight = DEFAULT_HEIGHT;
    private boolean m_showWarningInView = DEFAULT_SHOW_WARNING_IN_VIEW;
    private boolean m_calcEpsPrimeMedian = DEFAULT_CALC_EPS_PRIME_MEDIAN;
    private boolean m_calcEpsPrimeMean = DEFAULT_CALC_EPS_PRIME_MEAN;
    private boolean m_manualEpsPrime = false;
    private String m_epsCalcMethod = CALC_EPS_PRIME_MEAN;
    private boolean m_wasRedrawn = false;

    /**
     * Saves the current settings the argument. If none have been set (distance is null), nothing will be saved.
     *
     * @param settings To write to.
     */
    void saveConfiguration(final NodeSettingsWO settings) {
        settings.addBoolean(HIDE_IN_WIZARD, m_hideInWizard);
        settings.addString(EPSILON_PR, getEpsPrime().toString());
        settings.addBoolean(GENERATE_IMAGE, getGenerateImage());
        settings.addBoolean(RESIZE_TO_WINDOW, getResizeToWindow());
        settings.addBoolean(ENABLE_CONFIG, getEnableViewConfiguration());
        settings.addBoolean(ENABLE_TTILE_CHANGE, getEnableTitleChange());
        settings.addBoolean(ENABLE_EPSILON_PRIME_CHANGE, getEnableEpsilonPrimeChange());
        settings.addBoolean(ENABLE_SELECTION, getEnableSelection());
        settings.addBoolean(ENABLE_RECTANGLE_SELECTION, getEnableRectangleSelection());
        settings.addString(CHART_TITLE, getChartTitle());
        settings.addString(CHART_SUBTITLE, getChartSubtitle());
        settings.addInt(MAX_ROWS, getMaxRows());
        settings.addInt(IMAGE_WIDTH, getImageWidth());
        settings.addInt(IMAGE_HEIGHT, getImageHeight());
        settings.addBoolean(CFG_DISPLAY_FULLSCREEN_BUTTON, getDisplayFullscreenButton());
        settings.addBoolean(CFG_PUBLISH_SELECTION, getPublishSelection());
        settings.addBoolean(CFG_SUBSCRIBE_SELECTION, getSubscribeSelection());
        settings.addBoolean(CFG_ENABLE_SHOW_SELECTED_ONLY, getEnableShowSelectedOnly());
        settings.addBoolean(SHOW_WARNING_IN_VIEW, getShowWarningInView());
        settings.addBoolean(CALC_EPS_PRIME_MEAN, getCalcEpsPrimeMean());
        settings.addBoolean(CALC_EPS_PRIME_MEDIAN, getCalcEpsPrimeMedian());
        settings.addBoolean(MANUAL_EPS_PRIME, getManualEpsPrime());
        settings.addString(EPS_CALC_METHOD, getEpsCalcMethod());
        settings.addBoolean(WAS_REDRAWN, getWasRedrawn());
    }

    /**
     * Loads the settings from the argument object, throws exception if invalid or incomplete.
     *
     * @param settings To read from
     * @throws InvalidSettingsException If incomplete or wrong.
     */
    void loadConfigurationInModel(final NodeSettingsRO settings) throws InvalidSettingsException {
        m_hideInWizard = settings.getBoolean(HIDE_IN_WIZARD);
	    String epsPr = settings.getString(EPSILON_PR);
	    setEpsPrime(epsPr == null ? null : Double.parseDouble(epsPr));
	    checkSetting(m_eps_pr > 0, "epsilon prime must be > 0: " + m_eps_pr);
        setGenerateImage(settings.getBoolean(GENERATE_IMAGE));
        setResizeToWindow(settings.getBoolean(RESIZE_TO_WINDOW));
        setEnableViewConfiguration(settings.getBoolean(ENABLE_CONFIG));
        setEnableTitleChange(settings.getBoolean(ENABLE_TTILE_CHANGE));
        setEnableEpsilonPrimeChange(settings.getBoolean(ENABLE_EPSILON_PRIME_CHANGE));
        setEnableSelection(settings.getBoolean(ENABLE_SELECTION));
        setEnableRectangleSelection(settings.getBoolean(ENABLE_RECTANGLE_SELECTION));
        setChartTitle(settings.getString(CHART_TITLE));
        setChartSubtitle(settings.getString(CHART_SUBTITLE));
        setMaxRows(settings.getInt(MAX_ROWS,DEFAULT_MAX_ROWS));
        setImageWidth(settings.getInt(IMAGE_WIDTH));
        setImageHeight(settings.getInt(IMAGE_HEIGHT));
        setDisplayFullscreenButton(settings.getBoolean(CFG_DISPLAY_FULLSCREEN_BUTTON, DEFAULT_DISPLAY_FULLSCREEN_BUTTON));
        setPublishSelection(settings.getBoolean(CFG_PUBLISH_SELECTION, DEFAULT_PUBLISH_SELECTION));
        setSubscribeSelection(settings.getBoolean(CFG_SUBSCRIBE_SELECTION, DEFAULT_SUBSCRIBE_SELECTION));
        setEnableShowSelectedOnly(settings.getBoolean(CFG_ENABLE_SHOW_SELECTED_ONLY, DEFAULT_ENABLE_SHOW_SELECTED_ONLY));
        setShowWarningInView(settings.getBoolean(SHOW_WARNING_IN_VIEW, DEFAULT_SHOW_WARNING_IN_VIEW));
        setCalcEpsPrimeMean(settings.getBoolean(CALC_EPS_PRIME_MEAN, DEFAULT_CALC_EPS_PRIME_MEAN));
        setCalcEpsPrimeMedian(settings.getBoolean(CALC_EPS_PRIME_MEDIAN, DEFAULT_CALC_EPS_PRIME_MEDIAN));
        setManualEpsPrime(settings.getBoolean(MANUAL_EPS_PRIME, false));
        setEpsCalcMethod(settings.getString(EPS_CALC_METHOD, CALC_EPS_PRIME_MEAN));
        setWasRedrawn(settings.getBoolean(WAS_REDRAWN, false));
    }

    /**
     * Loads the settings from the argument object and guesses defaults if settings are invalid.
     *
     * @param settings To read from.
     * @param spec The spec to guess the defaults from.
     * @throws InvalidSettingsException
     */
    void loadConfigurationInDialog(final NodeSettingsRO settings, final DataTableSpec spec) throws InvalidSettingsException {
    	m_hideInWizard = settings.getBoolean(HIDE_IN_WIZARD, DEFAULT_HIDE_IN_WIZARD);
        setEpsPrime(Double.parseDouble(settings.getString(EPSILON_PR, DEFAULT_EPS_PRIME.toString())));
        setGenerateImage(settings.getBoolean(GENERATE_IMAGE, true));
        setResizeToWindow(settings.getBoolean(RESIZE_TO_WINDOW, true));
        setEnableViewConfiguration(settings.getBoolean(ENABLE_CONFIG, true));
        setEnableTitleChange(settings.getBoolean(ENABLE_TTILE_CHANGE, true));
        setEnableEpsilonPrimeChange(settings.getBoolean(ENABLE_EPSILON_PRIME_CHANGE, true));
        setEnableSelection(settings.getBoolean(ENABLE_SELECTION, true));
        setEnableRectangleSelection(settings.getBoolean(ENABLE_RECTANGLE_SELECTION, true));
        setChartTitle(settings.getString(CHART_TITLE, null));
        setChartSubtitle(settings.getString(CHART_SUBTITLE, null));
        setMaxRows(settings.getInt(MAX_ROWS, DEFAULT_MAX_ROWS));
        setImageWidth(settings.getInt(IMAGE_WIDTH, DEFAULT_WIDTH));
        setImageHeight(settings.getInt(IMAGE_HEIGHT, DEFAULT_HEIGHT));
        setDisplayFullscreenButton(settings.getBoolean(CFG_DISPLAY_FULLSCREEN_BUTTON, DEFAULT_DISPLAY_FULLSCREEN_BUTTON));
        setPublishSelection(settings.getBoolean(CFG_PUBLISH_SELECTION, DEFAULT_PUBLISH_SELECTION));
        setSubscribeSelection(settings.getBoolean(CFG_SUBSCRIBE_SELECTION, DEFAULT_SUBSCRIBE_SELECTION));
        setEnableShowSelectedOnly(settings.getBoolean(CFG_ENABLE_SHOW_SELECTED_ONLY, DEFAULT_ENABLE_SHOW_SELECTED_ONLY));
        setShowWarningInView(settings.getBoolean(SHOW_WARNING_IN_VIEW, DEFAULT_SHOW_WARNING_IN_VIEW));
        setCalcEpsPrimeMean(settings.getBoolean(CALC_EPS_PRIME_MEAN, DEFAULT_CALC_EPS_PRIME_MEAN));
        setCalcEpsPrimeMedian(settings.getBoolean(CALC_EPS_PRIME_MEDIAN, DEFAULT_CALC_EPS_PRIME_MEDIAN));
        setManualEpsPrime(settings.getBoolean(MANUAL_EPS_PRIME, false));
        setEpsCalcMethod(settings.getString(EPS_CALC_METHOD, CALC_EPS_PRIME_MEAN));
        setWasRedrawn(settings.getBoolean(WAS_REDRAWN, false));
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
     * @return the eps_pr
     */
    Double getEpsPrime() {
        return m_eps_pr;
    }

    /**
     * @param eps_pr the eps_pr to set
     */
    void setEpsPrime(final Double eps_pr) {
        this.m_eps_pr = eps_pr;
    }

    /**
     * @return the generateImage
     */
    boolean getGenerateImage() {
        return m_generateImage;
    }

    /**
     * @param generateImage the generateImage to set
     */
    void setGenerateImage(final boolean generateImage) {
        m_generateImage = generateImage;
    }

    /**
     * @return the showLegend
     */
    boolean getShowLegend() {
        return m_showLegend;
    }

    /**
     * @param showLegend the showLegend to set
     */
    void setShowLegend(final boolean showLegend) {
        m_showLegend = showLegend;
    }

    /**
     * @return the displayFullscreenButton
     */
    boolean getDisplayFullscreenButton() {
        return m_displayFullscreenButton;
    }

    /**
     * @param displayFullscreenButton the displayFullscreenButton to set
     */
    void setDisplayFullscreenButton(final boolean displayFullscreenButton) {
        m_displayFullscreenButton = displayFullscreenButton;
    }

    /**
     * @return the enableSelection
     */
    boolean getEnableSelection() {
        return m_enableSelection;
    }

    /**
     * @param enableSelection the enableSelection to set
     */
    void setEnableSelection(final boolean enableSelection) {
        m_enableSelection = enableSelection;
    }

    /**
     * @return the enableRectangleSelection
     */
    boolean getEnableRectangleSelection() {
        return m_enableRectangleSelection;
    }

    /**
     * @param enableRectangleSelection the enableRectangleSelection to set
     */
    void setEnableRectangleSelection(final boolean enableRectangleSelection) {
        m_enableRectangleSelection = enableRectangleSelection;
    }


    /**
     * @return the publishSelection
     */
    boolean getPublishSelection() {
        return m_publishSelection;
    }

    /**
     * @param publishSelection the publishSelection to set
     */
    void setPublishSelection(final boolean publishSelection) {
        m_publishSelection = publishSelection;
    }

    /**
     * @return the enableShowSelectedOnly
     */
    boolean getEnableShowSelectedOnly() {
        return m_enableShowSelectedOnly;
    }

    /**
     * @param enableShowSelectedOnly the enableShowSelectedOnly to set
     */
    void setEnableShowSelectedOnly(final boolean enableShowSelectedOnly) {
        m_enableShowSelectedOnly = enableShowSelectedOnly;
    }

    /**
     * @return the subscribeSelection
     */
    boolean getSubscribeSelection() {
        return m_subscribeSelection;
    }

    /**
     * @param subscribeSelection the subscribeSelection to set
     */
    void setSubscribeSelection(final boolean subscribeSelection) {
        m_subscribeSelection = subscribeSelection;
    }

    /**
     * @return the imageWidth
     */
    int getImageWidth() {
        return m_imageWidth;
    }

    /**
     * @param imageWidth the imageWidth to set
     */
    void setImageWidth(final int imageWidth) {
        m_imageWidth = imageWidth;
    }

    /**
     * @return the imageHeight
     */
    int getImageHeight() {
        return m_imageHeight;
    }

    /**
     * @param imageHeight the imageHeight to set
     */
    void setImageHeight(final int imageHeight) {
        m_imageHeight = imageHeight;
    }

    /**
     * @return the resizeToWindow
     */
    boolean getResizeToWindow() {
        return m_resizeToWindow;
    }

    /**
     * @param resizeToWindow the resizeToWindow to set
     */
    void setResizeToWindow(final boolean resizeToWindow) {
        m_resizeToWindow = resizeToWindow;
    }
    /**
     * @return the chartTitle
     */
    String getChartTitle() {
        return m_chartTitle;
    }

    /**
     * @param chartTitle the chartTitle to set
     */
    void setChartTitle(final String chartTitle) {
        m_chartTitle = chartTitle;
    }

    /**
     * @return the chartSubtitle
     */
    String getChartSubtitle() {
        return m_chartSubtitle;
    }

    /**
     * @param chartSubtitle the chartSubtitle to set
     */
    void setChartSubtitle(final String chartSubtitle) {
        m_chartSubtitle = chartSubtitle;
    }


    /**
     * @return the maxRows
     */
    int getMaxRows() {
        return m_maxRows;
    }

    /**
     * @param maxRows the maxRows to set
     */
    void setMaxRows(final int maxRows) {
        m_maxRows = maxRows;
    }


    /**
     * @return the allowViewConfiguration
     */
    boolean getEnableViewConfiguration() {
        return m_enableViewConfiguration;
    }

    /**
     * @param enableViewConfiguration the allowViewConfiguration to set
     */
    void setEnableViewConfiguration(final boolean enableViewConfiguration) {
        m_enableViewConfiguration = enableViewConfiguration;
    }

    /**
     * @return the allowTitleChange
     */
    boolean getEnableTitleChange() {
        return m_enableTitleChange;
    }

    /**
     * @param enableTitleChange the allowTitleChange to set
     */
    void setEnableTitleChange(final boolean enableTitleChange) {
        m_enableTitleChange = enableTitleChange;
    }

    /**
     * @return the allowEpsilonPrimeChange
     */
    boolean getEnableEpsilonPrimeChange() {
        return m_enableEpsilonPrimeChange;
    }

    /**
     * @param enableEpsilonPrimeChange the allowSubtitleChange to set
     */
    void setEnableEpsilonPrimeChange(final boolean enableEpsilonPrimeChange) {
        m_enableEpsilonPrimeChange = enableEpsilonPrimeChange;
    }

    /**
     * @return the enableSwitchLegend
     */
    boolean getEnableSwitchLegend() {
        return m_enableSwitchLegend;
    }

    /**
     * @param enableSwitchLegend the enableSwitchLegend to set
     */
    void setEnableSwitchLegend(final boolean enableSwitchLegend) {
        m_enableSwitchLegend = enableSwitchLegend;
    }

    /**
     * @return the showWarningInView
     */
    boolean getShowWarningInView() {
        return m_showWarningInView;
    }

    /**
     * @param showWarningInView the showWarningInView to set
     */
    void setShowWarningInView(final boolean showWarningInView) {
        m_showWarningInView = showWarningInView;
    }

    /**
     * @return the calcEpsPrimeMedian
     */
    boolean getCalcEpsPrimeMedian() {
        return m_calcEpsPrimeMedian;
    }

    /**
     * @param calcEpsPrimeMedian the calcEpsPrimeMedian to set
     */
    void setCalcEpsPrimeMedian(final boolean calcEpsPrimeMedian) {
        m_calcEpsPrimeMedian = calcEpsPrimeMedian;
    }

    /**
     * @return the calcEpsPrimeMean
     */
    boolean getCalcEpsPrimeMean() {
        return m_calcEpsPrimeMean;
    }

    /**
     * @param calcEpsPrimeMean the calcEpsPrimeMean to set
     */
    void setCalcEpsPrimeMean(final boolean calcEpsPrimeMean) {
        m_calcEpsPrimeMean = calcEpsPrimeMean;
    }

    /**
     * @return the manualEpsPrime
     */
    boolean getManualEpsPrime() {
        return m_manualEpsPrime;
    }

    /**
     * @param manualEpsPrime the manualEpsPrime to set
     */
    void setManualEpsPrime(final boolean manualEpsPrime) {
        m_manualEpsPrime = manualEpsPrime;
    }

    /**
     * @return the epsCalcMethod
     */
    String getEpsCalcMethod() {
        return m_epsCalcMethod;
    }

    /**
     * @param epsCalcMethod the epsCalcMethod to set
     */
    void setEpsCalcMethod(final String epsCalcMethod) {
        m_epsCalcMethod = epsCalcMethod;
    }

    /**
     * @return the wasRedrawn
     */
    boolean getWasRedrawn() {
        return m_wasRedrawn;
    }

    /**
     * @param wasRedrawn the wasRedrawn to set
     */
    void setWasRedrawn(final boolean wasRedrawn) {
        m_wasRedrawn = wasRedrawn;
    }
}
