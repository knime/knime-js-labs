/*
 * ------------------------------------------------------------------------
 *
 *  Copyright by KNIME GmbH, Konstanz, Germany
 *  Website: http://www.knime.org; Email: contact@knime.org
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
 *   Jun 12, 2014 (winter): created
 */
package org.knime.js.base.node.quickform.input.listbox;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.js.base.node.quickform.QuickFormFlowVariableConfig;

/**
 *
 * @author winter
 */
public class ListBoxInputQuickFormConfig extends QuickFormFlowVariableConfig<ListBoxInputQuickFormValue> {

    private static final String CFG_REGEX = "regex";
    private static final String DEFAULT_REGEX = "";
    private String m_regex = DEFAULT_REGEX;
    private static final String CFG_ERROR_MESSAGE = "error_message";
    private static final String DEFAULT_ERROR_MESSAGE = "";
    private String m_errorMessage = DEFAULT_ERROR_MESSAGE;
    private static final String CFG_SEPARATOR = "separator";
    private static final String DEFAULT_SEPARATOR = "\\n";
    private String m_separator = DEFAULT_SEPARATOR;
    private static final String CFG_OMIT_EMPTY = "omit_empty";
    private static final boolean DEFAULT_OMIT_EMPTY = true;
    private boolean m_omitEmpty = DEFAULT_OMIT_EMPTY;

    String getRegex() {
        return m_regex;
    }

    void setRegex(final String regex) {
        m_regex = regex;
    }

    String getErrorMessage() {
        return m_errorMessage;
    }

    void setErrorMessage(final String errorMessage) {
        m_errorMessage = errorMessage;
    }

    String getSeparator() {
        return m_separator;
    }

    void setSeparator(final String separator) {
        m_separator = separator;
    }

    boolean getOmitEmpty() {
        return m_omitEmpty;
    }

    void setOmitEmpty(final boolean omitEmpty) {
        m_omitEmpty = omitEmpty;
    }

    /**
     * @return separatorRegex
     */
    public String getSeparatorRegex() {
        if (m_separator == null || m_separator.isEmpty()) {
            return m_separator;
        }
        StringBuilder sepString = new StringBuilder();
        for (int i = 0; i < m_separator.length(); i++) {
            if (i > 0) {
                sepString.append('|');
            }
            char c = m_separator.charAt(i);
            if (c == '|') {
                sepString.append("\\|");
            } else if (c == '\\') {
                if (i + 1 < m_separator.length()) {
                    if (m_separator.charAt(i + 1) == 'n') {
                        sepString.append("\\n");
                        i++;
                    } else if (m_separator.charAt(i + 1) == 't') {
                        sepString.append("\\t");
                        i++;
                    }
                }
            } else {
                // a real, non-specific char
                sepString.append("[" + c + "]");
            }
        }
        return sepString.toString();
    }

    @Override
    public void saveSettings(final NodeSettingsWO settings) {
        super.saveSettings(settings);
        settings.addString(CFG_REGEX, m_regex);
        settings.addString(CFG_ERROR_MESSAGE, m_errorMessage);
        settings.addString(CFG_SEPARATOR, m_separator);
        settings.addBoolean(CFG_OMIT_EMPTY, m_omitEmpty);
    }

    @Override
    public void loadSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        super.loadSettings(settings);
        m_regex = settings.getString(CFG_REGEX);
        m_errorMessage = settings.getString(CFG_ERROR_MESSAGE);
        m_separator = settings.getString(CFG_SEPARATOR);
        m_omitEmpty = settings.getBoolean(CFG_OMIT_EMPTY);
    }

    @Override
    public void loadSettingsInDialog(final NodeSettingsRO settings) {
        super.loadSettingsInDialog(settings);
        m_regex = settings.getString(CFG_REGEX, DEFAULT_REGEX);
        m_errorMessage = settings.getString(CFG_ERROR_MESSAGE, DEFAULT_ERROR_MESSAGE);
        m_separator = settings.getString(CFG_SEPARATOR, DEFAULT_SEPARATOR);
        m_omitEmpty = settings.getBoolean(CFG_OMIT_EMPTY, DEFAULT_OMIT_EMPTY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ListBoxInputQuickFormValue createEmptyValue() {
        return new ListBoxInputQuickFormValue();
    }

}