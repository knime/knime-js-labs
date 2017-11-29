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
 */
package org.knime.base.node.mine.optics.compute;

import static org.knime.core.node.util.CheckUtils.checkSetting;

import org.knime.base.distance.measure.DistanceSelectionPanelConfig;
import org.knime.core.data.DataTableSpec;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.distance.DistanceMeasureConfig;

/**
 * Settings proxy for distance matrix calculate node.
 *
 * @author Anastasia Zhukova, KNIME GmbH, Konstanz, Germany
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 */
final class OPTICSComputeViewConfig {

    private static final Integer DEFAULT_MIN_POINTS = 3;

    private static final Double DEFAULT_EPS = 0.7;

    private static final String DISTANCE_CONFIG = "distance-config";

    private static final String MIN_POINTS = "min_pts";

    private static final String EPSILON = "epsilon";

    private DistanceSelectionPanelConfig m_distanceSelectionPanelConfig =
        new DistanceSelectionPanelConfig(DISTANCE_CONFIG);

    private Integer m_min_pts = DEFAULT_MIN_POINTS;

    private Double m_eps = DEFAULT_EPS;

    /**
     * Saves the current settings the argument. If none have been set (distance is null), nothing will be saved.
     *
     * @param settings To write to.
     */
    void saveConfiguration(final NodeSettingsWO settings) {
        settings.addString(EPSILON, getEps().toString());
        settings.addString(MIN_POINTS, getminPTS().toString());
        m_distanceSelectionPanelConfig.saveConfiguration(settings);
    }

    /**
     * Loads the settings from the argument object, throws exception if invalid or incomplete.
     *
     * @param settings To read from
     * @throws InvalidSettingsException If incomplete or wrong.
     */
    void loadConfigurationInModel(final NodeSettingsRO settings) throws InvalidSettingsException {
        String minPts = settings.getString(MIN_POINTS);
        String eps = settings.getString(EPSILON);

        setminPTS(minPts == null ? null : Integer.parseInt(minPts));
        seteps(eps == null ? null : Double.parseDouble(eps));

        checkSetting(m_min_pts > 0, "min points must be > 0: " + m_min_pts);
        checkSetting(m_eps > 0, "epsilon must be > 0: " + m_eps);

        m_distanceSelectionPanelConfig.loadConfigurationInModel(settings);
    }

    /**
     * Loads the settings from the argument object and guesses defaults if settings are invalid.
     *
     * @param settings To read from.
     * @param spec The spec to guess the defaults from.
     * @throws InvalidSettingsException
     */
    void loadConfigurationInDialog(final NodeSettingsRO settings, final DataTableSpec spec)
        throws InvalidSettingsException {
        setminPTS(Integer.parseInt(settings.getString(MIN_POINTS, DEFAULT_MIN_POINTS.toString())));
        seteps(Double.parseDouble(settings.getString(EPSILON, DEFAULT_EPS.toString())));
        m_distanceSelectionPanelConfig.loadConfigurationInDialog(settings, spec);
    }

    /**
     * @return the minPTS
     */
    Integer getminPTS() {
        return m_min_pts;
    }

    /**
     * @param m_minPTS the m_minPTS to set
     */
    void setminPTS(final Integer min_pts) {
        this.m_min_pts = min_pts;
    }

    /**
     * @return the m_eps
     */
    Double getEps() {
        return m_eps;
    }

    /**
     * @param m_eps the m_eps to set
     */
    void seteps(final Double eps) {
        this.m_eps = eps;
    }

    /**
     * @return the oldDistanceSelectionPanelConfig
     */
    DistanceSelectionPanelConfig getDistanceSelectionPanelConfig() {
        return m_distanceSelectionPanelConfig;
    }

    /**
     * @param dataTableSpec the spec
     * @return creates a distance measure configuration for an
     * @throws InvalidSettingsException if no distance is set, or no compatible columns are available
     */
    DistanceMeasureConfig<?> createDistanceConfig(final DataTableSpec dataTableSpec) throws InvalidSettingsException {
        return m_distanceSelectionPanelConfig.createOldDistanceConfigAndValidate(dataTableSpec);
    }

}
