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
 *   Jun 6, 2017 (annaz): created
 */
package org.knime.base.node.mine.optics;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.swing.JComponent;

//import org.knime.base.node.mine.optics.compute.OPTICSComputeNodeModel;
import org.knime.base.node.mine.optics.compute.OptPoint;
import org.knime.core.data.DataTableSpec;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortObjectZipInputStream;
import org.knime.core.node.port.PortObjectZipOutputStream;
import org.knime.core.node.port.PortType;
import org.knime.core.node.port.PortTypeRegistry;

/**
 * @author Anastasia Zhukova, KNIME GmbH, Konstanz, Germany
 */
public class OPTICSPortObject implements PortObject {

    /** Port Type */
    public static final PortType TYPE = PortTypeRegistry.getInstance().getPortType(OPTICSPortObject.class);

    private DataTableSpec m_spec;

    private OptPoint[] m_optPoints;

    private double m_eps;

    /**
     * @author Anastasia Zhukova, KNIME GmbH, Konstanz, Germany
     */
    public static final class Serializer extends PortObjectSerializer<OPTICSPortObject>{

        /**
         * {@inheritDoc}
         */
        @Override
        public void savePortObject(final OPTICSPortObject portObject, final PortObjectZipOutputStream out, final ExecutionMonitor exec)
            throws IOException, CanceledExecutionException {
            DataOutputStream dout = new DataOutputStream(out);
            dout.writeDouble(portObject.getEps());
            OptPoint[] optPoints = portObject.getOptPoints();
            dout.writeInt(optPoints.length);
            for (OptPoint p : optPoints) {
                dout.writeLong(p.getID());
                dout.writeDouble(p.getCoredist());
                dout.writeDouble(p.getReachdist());
            }
            dout.flush();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public OPTICSPortObject loadPortObject(final PortObjectZipInputStream in, final PortObjectSpec spec, final ExecutionMonitor exec)
            throws IOException, CanceledExecutionException {
            DataInputStream din = new DataInputStream(in);
            double eps = din.readDouble();
            OptPoint[] optPoints = new OptPoint[din.readInt()];
            OptPoint p = null;
            for (int i = 0; i < optPoints.length; i++) {
                p = new OptPoint(din.readLong());
                p.setCoredist(din.readDouble());
                p.setReachdist(din.readDouble());
                optPoints[i] = p;
            }
            return new OPTICSPortObject((DataTableSpec)spec, optPoints, eps);
        }
    }

    /**
     * A constructor
     * @param spec
     * @param optPoints
     * @param eps
     */
    public OPTICSPortObject(final DataTableSpec spec, final OptPoint[] optPoints, final double eps){
        this.m_optPoints = optPoints;
        this.m_spec = spec;
        this.m_eps = eps;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSummary() {
        return m_optPoints.length + " number of rows";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PortObjectSpec getSpec() {
        //intermediate table spec
        return m_spec;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JComponent[] getViews() {
        return new JComponent[0];
    }

    /**
     * Get an array of computed OptPoints
     * @return an array of OptPoint
     */
    public OptPoint[] getOptPoints() {
        return m_optPoints;
    }

    /**
     * Get epsilon value
     * @return epsilon
     */
    public double getEps() {
        return m_eps;
    }
}
