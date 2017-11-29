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
 */
package org.knime.base.node.mine.optics.compute;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import org.knime.base.distance.measure.DistanceSelectionPanel;
import org.knime.core.data.DataTableSpec;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.distance.DistanceMeasurePortSpec;

/**
 * <code>NodeDialog</code> for the "OPTICS" Node.
 *
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows creation of a simple dialog with standard
 * components.
 *
 * @author Anastasia Zhukova, University of Konstanz, Germany
 */
final class OPTICSComputeNodeDialog extends NodeDialogPane {

    private final DistanceSelectionPanel m_distanceSelectionPanel;

    private final JSpinner m_eps;

    private final JSpinner m_minPTS;

    /**
     * New pane for configuring OPTICS node dialog.
     */
    protected OPTICSComputeNodeDialog() {
        m_eps = new JSpinner(new SpinnerNumberModel(0.7, 0.0001, 10000000, 0.01));
        m_minPTS = new JSpinner(new SpinnerNumberModel(3, 1, Integer.MAX_VALUE, 1));
        m_distanceSelectionPanel = new DistanceSelectionPanel();
        addTab("Distance Calculate", distancePanel());
    }

    private Component distancePanel() {
        final JPanel p = new JPanel(new GridBagLayout());
        final GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;

        gbc.gridx = 0;
        gbc.gridy += 1;
        gbc.gridwidth = 2;
        p.add(m_distanceSelectionPanel, gbc);

        gbc.gridwidth = 1;
        gbc.gridy += 1;
        gbc.gridx = 0;
        p.add(new JLabel("Minimum points:"), gbc);
        gbc.gridx += 1;
        p.add(m_minPTS, gbc);

        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy += 1;
        p.add(new JLabel("Epsilon:"), gbc);
        gbc.gridx += 1;
        p.add(m_eps, gbc);

        return p;
    }

    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) throws InvalidSettingsException {
        final OPTICSComputeViewConfig config = new OPTICSComputeViewConfig();
        m_distanceSelectionPanel.saveConfiguration(config.getDistanceSelectionPanelConfig());
        config.setminPTS((Integer)m_minPTS.getValue());
        config.seteps((Double)m_eps.getValue());
        config.saveConfiguration(settings);
    }

    @Override
    protected void loadSettingsFrom(final NodeSettingsRO settings, final PortObjectSpec[] specs)
        throws NotConfigurableException {
        DataTableSpec spec = (DataTableSpec)specs[0];
        final OPTICSComputeViewConfig config = new OPTICSComputeViewConfig();
        try {
            config.loadConfigurationInDialog(settings, spec);
        } catch (InvalidSettingsException e) {
            e.printStackTrace();
        }
        m_eps.setValue(config.getEps());
        m_minPTS.setValue(config.getminPTS());
        //if a distance function is plugged in hide the distance configuration pain
        m_distanceSelectionPanel.loadConfiguration(config.getDistanceSelectionPanelConfig(), spec);
        m_distanceSelectionPanel.setDistanceMeasure((DistanceMeasurePortSpec)specs[1], spec);
        getPanel().repaint();
    }
}
