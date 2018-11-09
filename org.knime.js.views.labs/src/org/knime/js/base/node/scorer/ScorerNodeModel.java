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
 *   30 May 2018 (albrecht): created
 */
package org.knime.js.base.node.scorer;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.knime.base.node.mine.scorer.accuracy.AccuracyScorerCalculator;
import org.knime.base.node.mine.scorer.accuracy.AccuracyScorerCalculator.ClassStatisticsConfiguration;
import org.knime.base.node.mine.scorer.accuracy.AccuracyScorerCalculator.OverallStatisticsConfiguration;
import org.knime.base.node.mine.scorer.accuracy.AccuracyScorerCalculator.ScorerCalculatorConfiguration;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.NominalValue;
import org.knime.core.data.RowKey;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.BufferedDataTableHolder;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortType;
import org.knime.core.node.web.ValidationError;
import org.knime.core.node.wizard.CSSModifiable;
import org.knime.js.core.CSSUtils;
import org.knime.js.core.JSONDataTable;
import org.knime.js.core.layout.LayoutTemplateProvider;
import org.knime.js.core.layout.bs.JSONLayoutViewContent;
import org.knime.js.core.layout.bs.JSONLayoutViewContent.ResizeMethod;
import org.knime.js.core.node.AbstractWizardNodeModel;

/**
 *
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 */
public class ScorerNodeModel extends AbstractWizardNodeModel<ScorerViewRepresentation, ScorerViewValue>
    implements LayoutTemplateProvider, BufferedDataTableHolder, CSSModifiable {

    private static final NodeLogger LOGGER = NodeLogger.getLogger(ScorerNodeModel.class);

    private final ScorerConfig m_config;
    private BufferedDataTable m_confusionMatrix;
    private BufferedDataTable m_classStatisticsTable;
    private BufferedDataTable m_overallStatisticsTable;

    /**
     * @param viewName
     */
    protected ScorerNodeModel(final String viewName) {
        super(new PortType[]{BufferedDataTable.TYPE},
            new PortType[]{BufferedDataTable.TYPE, BufferedDataTable.TYPE, BufferedDataTable.TYPE}, viewName);
        m_config = new ScorerConfig();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PortObjectSpec[] configure(final PortObjectSpec[] inSpecs) throws InvalidSettingsException {
        DataTableSpec inSpec = (DataTableSpec)inSpecs[0];

        String firstCol = m_config.getFirstColumn();
        String secondCol = m_config.getSecondColumn();
        if (StringUtils.isEmpty(firstCol) || StringUtils.isEmpty(secondCol)) {
            throw new InvalidSettingsException("Please configure the node.");
        }
        if (!inSpec.containsName(firstCol) || !inSpec.containsName(secondCol)) {
            throw new InvalidSettingsException("The chosen columns are not available anymore.");
        }
        DataColumnSpec firstColSpec = inSpec.getColumnSpec(firstCol);
        DataColumnSpec secondColSpec = inSpec.getColumnSpec(secondCol);
        if (!firstColSpec.getType().isCompatible(NominalValue.class)) {
            throw new InvalidSettingsException("Column " +  firstCol + " is not string compatible, its type is " + firstColSpec.getType().toString());
        }
        if (!secondColSpec.getType().isCompatible(NominalValue.class)) {
            throw new InvalidSettingsException("Column " +  secondCol + " is not string compatible, its type is " + secondColSpec.getType().toString());
        }
        ScorerCalculatorConfiguration scorerConfig = getScorerConfig();
        DataTableSpec confusionMatrixSpec =
            AccuracyScorerCalculator.createConfusionMatrixSpec(inSpec, firstCol, secondCol, scorerConfig);

        ClassStatisticsConfiguration classConfig = getClassStatisticsConfig();
        DataTableSpec classStatisticsSpec = AccuracyScorerCalculator.createClassStatsSpec(classConfig);

        OverallStatisticsConfiguration overallConfig = getOverallStatisticsConfig();
        DataTableSpec overallStatisticsSpec = AccuracyScorerCalculator.createOverallStatsSpec(overallConfig);

        return new PortObjectSpec[] {confusionMatrixSpec, classStatisticsSpec, overallStatisticsSpec};
    }

    private ScorerCalculatorConfiguration getScorerConfig() {
        ScorerCalculatorConfiguration scorerConfig = new ScorerCalculatorConfiguration();
        scorerConfig.setSortingStrategy(m_config.getSortingStragegy());
        scorerConfig.setSortingReversed(m_config.isReverseOrder());
        scorerConfig.setIgnoreMissingValues(m_config.isIgnoreMissingValues());
        return scorerConfig;
    }

    private ClassStatisticsConfiguration getClassStatisticsConfig() {
        ClassStatisticsConfiguration classConfig = new ClassStatisticsConfiguration();
        classConfig.withTpCalculated(m_config.isClassTruePositives())
            .withFpCalculated(m_config.isClassFalsePositives())
            .withTnCalculated(m_config.isClassTrueNegatives())
            .withFnCalculated(m_config.isClassFalseNegatives())
            .withAccuracyCalculated(m_config.isClassAccuracy())
            .withBalancedAccuracyCalculated(m_config.isClassBalancedAccuracy())
            .withErrorRateCalculated(m_config.isClassErrorRate())
            .withFalseNegativeRateCalculated(m_config.isClassFalseNegativeRate())
            .withRecallCalculated(m_config.isClassRecall())
            .withPrecisionCalculated(m_config.isClassPrecision())
            .withSensitivityCalculated(m_config.isClassSensitivity())
            .withSpecifityCalculated(m_config.isClassSpecificity())
            .withFmeasureCalculated(m_config.isClassFMeasure());
        return classConfig;
    }

    private OverallStatisticsConfiguration getOverallStatisticsConfig() {
        OverallStatisticsConfiguration overallConfig = new OverallStatisticsConfiguration();
        overallConfig.withOverallAccuracyCalculated(m_config.isOverallAccuracy())
            .withOverallErrorCalculated(m_config.isOverallError())
            .withCohensKappaCalculated(m_config.isOverallCohensKappa())
            .withCorrectClassifiedCalculated(m_config.isOverallCorrectClassified())
            .withWrongClassifiedCalculated(m_config.isOverallWrongClassified());
        return overallConfig;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ScorerViewRepresentation createEmptyViewRepresentation() {
        return new ScorerViewRepresentation();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ScorerViewValue createEmptyViewValue() {
        return new ScorerViewValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getJavascriptObjectID() {
        return "org.knime.js.base.node.scorer";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isHideInWizard() {
        return m_config.getHideInWizard();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setHideInWizard(final boolean hide) {
        m_config.setHideInWizard(hide);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCssStyles() {
        return m_config.getCustomCSS();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCssStyles(final String styles) {
        m_config.setCustomCSS(styles);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ValidationError validateViewValue(final ScorerViewValue viewContent) {
        /* nothing to validate atm */
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveCurrentValue(final NodeSettingsWO content) {
        /* nothing to do */
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JSONLayoutViewContent getLayoutTemplate() {
        JSONLayoutViewContent template = new JSONLayoutViewContent();
        template.setResizeMethod(ResizeMethod.VIEW_LOWEST_ELEMENT);
        return template;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PortObject[] performExecute(final PortObject[] inObjects, final ExecutionContext exec) throws Exception {
        BufferedDataTable table = (BufferedDataTable)inObjects[0];
        synchronized (getLock()) {
            ScorerViewRepresentation representation = getViewRepresentation();
            if (representation.getConfusionMatrix() == null) {
                copyConfigToView();
                String firstColumn = m_config.getFirstColumn();
                String secondColumn = m_config.getSecondColumn();
                AccuracyScorerCalculator calc = AccuracyScorerCalculator.createCalculator(table, firstColumn,
                    secondColumn, getScorerConfig(), exec.createSubExecutionContext(0.9));
                m_confusionMatrix = calc.getConfusionMatrixTable(exec);
                JSONDataTable jsonMatrix = createJSONTableFromBufferedDataTable(m_confusionMatrix,
                    exec.createSubExecutionContext(0.02));
                jsonMatrix.setId(getTableId(0));
                representation.setConfusionMatrix(jsonMatrix);

                m_classStatisticsTable = calc.getClassStatisticsTable(getClassStatisticsConfig(),
                    exec.createSubExecutionContext(0.02));
                representation.setClassStatisticsTable(createJSONTableFromBufferedDataTable(m_classStatisticsTable,
                    exec.createSubExecutionContext(0.02)));

                m_overallStatisticsTable = calc.getOverallStatisticsTable(getOverallStatisticsConfig(),
                    exec.createSubExecutionContext(0.02));
                representation.setOverallStatisticsTable(createJSONTableFromBufferedDataTable(m_overallStatisticsTable,
                    exec.createSubExecutionContext(0.02)));

                List<RowKey>[][] keyStore = calc.getKeyStore();
                @SuppressWarnings("unchecked")
                List<String>[][] keyStoreAsStrings = new List[keyStore.length][keyStore.length];
                for (int i = 0; i < keyStoreAsStrings.length; i++) {
                    for (int j = 0; j < keyStoreAsStrings[i].length; j++) {
                        keyStoreAsStrings[i][j] =
                            keyStore[i][j].stream().map(key -> key.getString()).collect(Collectors.toList());
                    }
                }
                representation.setKeystore(keyStoreAsStrings);

                String warning = calc.getWarnings().stream().collect(Collectors.joining("\n"));
                if (!StringUtils.isEmpty(warning)) {
                    setWarningMessage(warning);
                }
                representation.setWarningMessage(warning);

            }
        }
        return new PortObject[] {m_confusionMatrix, m_classStatisticsTable, m_overallStatisticsTable};
    }

    private static JSONDataTable createJSONTableFromBufferedDataTable(final BufferedDataTable table,
            final ExecutionContext exec) throws CanceledExecutionException {
        return JSONDataTable.newBuilder()
                .setDataTable(table)
                .extractRowColors(false)
                .extractRowSizes(false)
                .build(exec);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ScorerViewRepresentation getViewRepresentation() {
        ScorerViewRepresentation representation = super.getViewRepresentation();
        synchronized (getLock()) {
            if (representation != null && representation.getConfusionMatrix() == null && m_confusionMatrix != null) {
                // set internal tables
                try {
                    JSONDataTable jsonMatrix = createJSONTableFromBufferedDataTable(m_confusionMatrix, null);
                    jsonMatrix.setId(getTableId(0));
                    representation.setConfusionMatrix(jsonMatrix);
                    representation.setClassStatisticsTable(
                        createJSONTableFromBufferedDataTable(m_classStatisticsTable, null));
                    representation.setOverallStatisticsTable(
                        createJSONTableFromBufferedDataTable(m_overallStatisticsTable, null));
                } catch (Exception e) {
                    LOGGER.error("Could not create JSON table: " + e.getMessage(), e);
                }
            }
        }
        return representation;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void performReset() {
        m_confusionMatrix = null;
        m_classStatisticsTable = null;
        m_overallStatisticsTable = null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void useCurrentValueAsDefault() {
        copyValueToConfig();
    }

    private void copyConfigToView() {
        ScorerViewRepresentation presentation = getViewRepresentation();
        presentation.setShowWarningsInView(m_config.getShowWarningsInView());
        presentation.setHeaderColor(CSSUtils.cssHexStringFromColor(m_config.getHeaderColor()));
        presentation.setDiagonalColor(CSSUtils.cssHexStringFromColor(m_config.getDiagonalColor()));
        presentation.setDisplayFullscreenButton(m_config.isDisplayFullscreenButton());
        presentation.setEnableViewControls(m_config.isEnableViewControls());
        presentation.setEnableTitleEditing(m_config.isEnableTitleEditing());
        presentation.setEnableSubtitleEditing(m_config.isEnableSubtitleEditing());
        presentation.setEnableLabelsDisplayConfig(m_config.isEnableLabelsDisplayConfig());
        presentation.setEnableRowsNumberConfig(m_config.isEnableRowsNumberConfig());
        presentation.setEnableConfusionMatrixRatesConfig(m_config.isEnableConfusionMatrixRatesConfig());
        presentation.setEnableClassStatisticsConfig(m_config.isEnableClassStatisticsConfig());
        presentation.setEnableOverallStatisticsConfig(m_config.isEnableOverallStatisticsConfig());

        if (isViewValueEmpty()) {
            ScorerViewValue value = getViewValue();
            value.setTitle(m_config.getTitle());
            value.setSubtitle(m_config.getSubtitle());
            value.setDisplayLabels(m_config.isDisplayLabels());
            value.setDisplayTotalRows(m_config.isDisplayTotalRows());
            value.setDisplayConfusionMatrixRates(m_config.isDisplayConfusionMatrixRates());
            value.setDisplayFloatAsPercent(m_config.isDisplayFloatAsPercent());
            value.setDisplayClassStatsTable(m_config.isDisplayClassStatsTable());
            value.setDisplayOverallStats(m_config.isDisplayOverallStats());
        }
    }

    private void copyValueToConfig() {
        ScorerViewValue value = getViewValue();
        m_config.setTitle(value.getTitle());
        m_config.setSubtitle(value.getSubtitle());
        m_config.setDisplayLabels(value.isDisplayLabels());
        m_config.setDisplayTotalRows(value.isDisplayTotalRows());
        m_config.setDisplayConfusionMatrixRates(value.isDisplayConfusionMatrixRates());
        m_config.setDisplayFloatAsPercent(value.isDisplayFloatAsPercent());
        m_config.setDisplayClassStatsTable(value.isDisplayClassStatsTable());
        m_config.setDisplayOverallStats(value.isDisplayOverallStats());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) {
        m_config.saveSettings(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        (new ScorerConfig()).loadSettings(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings) throws InvalidSettingsException {
        m_config.loadSettings(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BufferedDataTable[] getInternalTables() {
        return new BufferedDataTable[] {m_confusionMatrix, m_classStatisticsTable, m_overallStatisticsTable};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setInternalTables(final BufferedDataTable[] tables) {
        m_confusionMatrix = tables[0];
        m_classStatisticsTable = tables[1];
        m_overallStatisticsTable = tables[2];
    }



}
