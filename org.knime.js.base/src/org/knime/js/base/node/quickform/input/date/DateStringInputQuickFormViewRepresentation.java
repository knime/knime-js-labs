package org.knime.js.base.node.quickform.input.date;

import java.util.Date;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.web.JSONViewContent;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * 
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland
 */
@JsonAutoDetect
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class DateStringInputQuickFormViewRepresentation extends JSONViewContent {
    
    private static final String CFG_USE_MIN = "use_min";
    
    private static final boolean DEFAULT_USE_MIN = false;
    
    private boolean m_useMin = DEFAULT_USE_MIN;
    
    private static final String CFG_USE_MAX = "use_max";
    
    private static final boolean DEFAULT_USE_MAX = false;
    
    private boolean m_useMax = DEFAULT_USE_MAX;
    
    private static final String CFG_MIN = "min";
    
    private Date m_min;
    
    private static final String CFG_MAX = "max";
    
    private Date m_max;

    private static final String CFG_DEFAULT = "default";
    
    private Date m_defaultValue;
    
    /**
     * {@inheritDoc}
     */
    @Override
    @JsonIgnore
    public void loadFromNodeSettings(final NodeSettingsRO settings)
            throws InvalidSettingsException {
        m_useMin = settings.getBoolean(CFG_USE_MIN);
        m_useMax = settings.getBoolean(CFG_USE_MAX);
        String value = settings.getString(CFG_DEFAULT);
        String min = settings.getString(CFG_MIN);
        String max = settings.getString(CFG_MAX);
        try {
            setDefaultValue(DateStringInputQuickFormNodeModel.FORMAT.parse(value));
            m_min = DateStringInputQuickFormNodeModel.FORMAT.parse(min);
            m_max = DateStringInputQuickFormNodeModel.FORMAT.parse(max);
        } catch (Exception e) {
            throw new InvalidSettingsException("Can't parse date: " + value, e);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @JsonIgnore
    public void saveToNodeSettings(final NodeSettingsWO settings) {
        settings.addBoolean(CFG_USE_MIN, m_useMin);
        settings.addBoolean(CFG_USE_MAX, m_useMax);
        settings.addString(CFG_DEFAULT, DateStringInputQuickFormNodeModel.FORMAT.format(getDefaultValue()));
        settings.addString(CFG_MIN, DateStringInputQuickFormNodeModel.FORMAT.format(m_min));
        settings.addString(CFG_MAX, DateStringInputQuickFormNodeModel.FORMAT.format(m_max));
    }

    /**
     * @return the default
     */
    @JsonProperty("default")
    public Date getDefaultValue() {
        return m_defaultValue;
    }

    /**
     * @param defaultValue the default to set
     */
    @JsonProperty("default")
    public void setDefaultValue(final Date defaultValue) {
        m_defaultValue = defaultValue;
    }

    /**
     * @return the useMin
     */
    @JsonProperty("usemin")
    public boolean getUseMin() {
        return m_useMin;
    }

    /**
     * @param useMin the useMin to set
     */
    @JsonProperty("usemin")
    public void setUseMin(final boolean useMin) {
        m_useMin = useMin;
    }

    /**
     * @return the useMax
     */
    @JsonProperty("usemax")
    public boolean getUseMax() {
        return m_useMax;
    }

    /**
     * @param useMax the useMax to set
     */
    @JsonProperty("usemax")
    public void setUseMax(final boolean useMax) {
        m_useMax = useMax;
    }

    /**
     * @return the min
     */
    @JsonProperty("min")
    public Date getMin() {
        return m_min;
    }

    /**
     * @param min the min to set
     */
    @JsonProperty("min")
    public void setMin(final Date min) {
        m_min = min;
    }

    /**
     * @return the max
     */
    @JsonProperty("max")
    public Date getMax() {
        return m_max;
    }

    /**
     * @param max the max to set
     */
    @JsonProperty("max")
    public void setMax(final Date max) {
        m_max = max;
    }

}
