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

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * @author Anastasia Zhukova, KNIME GmbH, Konstanz, Germany
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 * @since 3.4
 */
@JsonAutoDetect
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class OPTICSAssignerViewValue extends JSONViewContent {

    static final String SELECTED_KEYS = "selectedKeys";

    private static final String CFG_SHOW_SELECTED_ONLY = "showSelectedOnly";
    private static final boolean DEFAULT_SHOW_SELECTED_ONLY = false;

    private String m_chartTitle;
    private String m_chartSubtitle;
    private boolean m_showSelectedOnly;
    private String[] m_selection;
	private Double m_eps_prime;
	private boolean m_calcEpsPrimeMedian;
	private boolean m_calcEpsPrimeMean;
    private boolean m_manualEpsPrime;
    private boolean m_publishSelection;
    private boolean m_subscribeSelection;
    private String m_epsCalcMethod;

	@Override
	public void saveToNodeSettings(final NodeSettingsWO settings) {
		settings.addString(OPTICSAssignerViewConfig.CFG_EPSILON_PR,
				getEpsPrime() == null ? null : getEpsPrime().toString());
		settings.addBoolean(OPTICSAssignerViewConfig.CFG_CALC_EPS_PRIME_MEAN, getCalcEpsPrimeMean());
		settings.addBoolean(OPTICSAssignerViewConfig.CFG_CALC_EPS_PRIME_MEDIAN, getCalcEpsPrimeMedian());
		settings.addBoolean(OPTICSAssignerViewConfig.CFG_MANUAL_EPS_PRIME, getManualEpsPrime());
		settings.addString(OPTICSAssignerViewConfig.CFG_EPS_CALC_METHOD, getEpsCalcMethod());
        settings.addString(OPTICSAssignerViewConfig.CFG_CHART_TITLE, getChartTitle());
        settings.addString(OPTICSAssignerViewConfig.CFG_CHART_SUBTITLE, getChartSubtitle());
        settings.addStringArray(SELECTED_KEYS, m_selection);
        settings.addBoolean(OPTICSAssignerViewConfig.CFG_PUBLISH_SELECTION, getPublishSelection());
        settings.addBoolean(OPTICSAssignerViewConfig.CFG_SUBSCRIBE_SELECTION, getSubscribeSelection());
        settings.addBoolean(CFG_SHOW_SELECTED_ONLY, getShowSelectedOnly());
	}

    @Override
	public void loadFromNodeSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
		setEpsPrime(Double.parseDouble(settings.getString(OPTICSAssignerViewConfig.CFG_EPSILON_PR)));
		setCalcEpsPrimeMean(settings.getBoolean(OPTICSAssignerViewConfig.CFG_CALC_EPS_PRIME_MEAN));
		setCalcEpsPrimeMedian(settings.getBoolean(OPTICSAssignerViewConfig.CFG_CALC_EPS_PRIME_MEDIAN));
		setManualEpsPrime(settings.getBoolean(OPTICSAssignerViewConfig.CFG_MANUAL_EPS_PRIME));
		setEpsCalcMethod(settings.getString(OPTICSAssignerViewConfig.CFG_EPS_CALC_METHOD));
        setChartTitle(settings.getString(OPTICSAssignerViewConfig.CFG_CHART_TITLE));
        setChartSubtitle(settings.getString(OPTICSAssignerViewConfig.CFG_CHART_SUBTITLE));
        setSelectedKeys(settings.getStringArray(SELECTED_KEYS));
        setPublishSelection(settings.getBoolean(OPTICSAssignerViewConfig.CFG_PUBLISH_SELECTION, OPTICSAssignerViewConfig.DEFAULT_PUBLISH_SELECTION));
        setSubscribeSelection(settings.getBoolean(OPTICSAssignerViewConfig.CFG_SUBSCRIBE_SELECTION, OPTICSAssignerViewConfig.DEFAULT_SUBSCRIBE_SELECTION));
        setShowSelectedOnly(settings.getBoolean(CFG_SHOW_SELECTED_ONLY, DEFAULT_SHOW_SELECTED_ONLY));

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
        OPTICSAssignerViewValue other = (OPTICSAssignerViewValue)obj;
        return new EqualsBuilder()
                .append(m_eps_prime, other.m_eps_prime)
                .append(m_calcEpsPrimeMean, other.m_calcEpsPrimeMean)
                .append(m_calcEpsPrimeMedian, other.m_calcEpsPrimeMedian)
                .append(m_manualEpsPrime, other.m_manualEpsPrime)
                .append(m_epsCalcMethod, other.m_epsCalcMethod)
                .append(m_chartTitle, other.m_chartTitle)
                .append(m_chartSubtitle, other.m_chartSubtitle)
                .append(m_publishSelection, other.m_publishSelection)
                .append(m_subscribeSelection, other.m_subscribeSelection)
                .append(m_showSelectedOnly, other.m_showSelectedOnly)
                .append(m_selection, other.m_selection)
                .isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(m_eps_prime)
                .append(m_calcEpsPrimeMean)
                .append(m_calcEpsPrimeMedian)
                .append(m_manualEpsPrime)
                .append(m_epsCalcMethod)
                .append(m_chartTitle)
                .append(m_chartSubtitle)
                .append(m_publishSelection)
                .append(m_subscribeSelection)
                .append(m_showSelectedOnly)
                .append(m_selection)
                .toHashCode();
    }

	/**
	 * @return the m_eps_prime
	 */
	public Double getEpsPrime() {
		return m_eps_prime;
	}

	/**
	 * @param eps_prime the eps_prime to set
	 */
	public void setEpsPrime(final Double eps_prime) {
		this.m_eps_prime = eps_prime;
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
     * @return the selection
     */
    public String[] getSelectedKeys() {
        return m_selection;
    }

    /**
     * @param selection the selection to set
     */
    public void setSelectedKeys(final String[] selection) {
        m_selection = selection;
    }

    /**
     * @return the showSelectedOnly
     */
    public boolean getShowSelectedOnly() {
        return m_showSelectedOnly;
    }

    /**
     * @param showSelectedOnly the showSelectedOnly to set
     */
    public void setShowSelectedOnly(final boolean showSelectedOnly) {
        m_showSelectedOnly = showSelectedOnly;
    }

    /**
     * @return the calcEpsPrimeMedian
     */
    public boolean getCalcEpsPrimeMedian() {
        return m_calcEpsPrimeMedian;
    }

    /**
     * @param calcEpsPrimeMedian the calcEpsPrimeMedian to set
     */
    public void setCalcEpsPrimeMedian(final boolean calcEpsPrimeMedian) {
        m_calcEpsPrimeMedian = calcEpsPrimeMedian;
    }

    /**
     * @return the calcEpsPrimeMean
     */
    public boolean getCalcEpsPrimeMean() {
        return m_calcEpsPrimeMean;
    }

    /**
     * @param calcEpsPrimeMean the calcEpsPrimeMean to set
     */
    public void setCalcEpsPrimeMean(final boolean calcEpsPrimeMean) {
        m_calcEpsPrimeMean = calcEpsPrimeMean;
    }

    /**
     * @return the manualEpsPrime
     */
    public boolean getManualEpsPrime() {
        return m_manualEpsPrime;
    }

    /**
     * @param manualEpsPrime the manualEpsPrime to set
     */
    public void setManualEpsPrime(final boolean manualEpsPrime) {
        m_manualEpsPrime = manualEpsPrime;
    }

    /**
     * @return the epsCalcMethod
     */
    public String getEpsCalcMethod() {
        return m_epsCalcMethod;
    }

    /**
     * @param epsCalcMethod the epsCalcMethod to set
     */
    public void setEpsCalcMethod(final String epsCalcMethod) {
        m_epsCalcMethod = epsCalcMethod;
    }

}

