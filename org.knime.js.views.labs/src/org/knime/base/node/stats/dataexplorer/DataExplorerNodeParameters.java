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
 * ------------------------------------------------------------------------
 */

package org.knime.base.node.stats.dataexplorer;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.node.parameters.NodeParameters;
import org.knime.node.parameters.Widget;
import org.knime.node.parameters.layout.After;
import org.knime.node.parameters.layout.Layout;
import org.knime.node.parameters.layout.Section;
import org.knime.node.parameters.migration.LoadDefaultsForAbsentFields;
import org.knime.node.parameters.persistence.NodeParametersPersistor;
import org.knime.node.parameters.persistence.Persist;
import org.knime.node.parameters.persistence.Persistor;
import org.knime.node.parameters.updates.Effect;
import org.knime.node.parameters.updates.Effect.EffectType;
import org.knime.node.parameters.updates.EffectPredicate;
import org.knime.node.parameters.updates.EffectPredicateProvider;
import org.knime.node.parameters.updates.ParameterReference;
import org.knime.node.parameters.updates.ValueReference;
import org.knime.node.parameters.widget.number.NumberInputWidget;
import org.knime.node.parameters.widget.number.NumberInputWidgetValidation.MinValidation.IsNonNegativeValidation;
import org.knime.node.parameters.widget.number.NumberInputWidgetValidation.MinValidation.IsPositiveIntegerValidation;
import org.knime.node.parameters.widget.text.TextInputWidget;
import org.knime.node.parameters.widget.text.TextInputWidgetValidation.PatternValidation;

/**
 * Node parameters for Data Explorer.
 *
 * @author Magnus Gohm, KNIME GmbH, Konstanz, Germany
 * @author AI Migration Pipeline v1.2
 */
@LoadDefaultsForAbsentFields
@SuppressWarnings("restriction")
final class DataExplorerNodeParameters implements NodeParameters {

    @Section(title = "Columns")
    interface ColumnsSection {}

    @Section(title = "Histograms")
    @After(ColumnsSection.class)
    interface HistogramsSection {}

    @Section(title = "Titles")
    @After(HistogramsSection.class)
    interface TitlesSection {}

    @Section(title = "Number Formatter")
    @After(TitlesSection.class)
    interface NumberFormatterSection {}

    @Section(title = "Nominal Values")
    @After(NumberFormatterSection.class)
    interface NominalValuesSection {}

    @Section(title = "Rows")
    @After(NominalValuesSection.class)
    interface RowsSection {}

    @Section(title = "Interactivity")
    @After(RowsSection.class)
    interface InteractivitySection {}

    @Layout(ColumnsSection.class)
    @Persist(configKey = DataExplorerConfig.CFG_ENABLE_FREQ_VAL_DISPLAY)
    @Widget(title = "Show most frequent/infrequent nominal values", description = """
            If you check this option, the Nominal tab of the interactive view will create a column with the n
            most frequent nominal values and another column with the n most infrequent nominal values, for
            some number n that you select. If the selected number is equal to the total number of distinct
            values in a column, only one column with all values will be created. In all cases values are
            listed in decreasing order of frequency.
            """)
    @ValueReference(EnableFreqValDisplayRef.class)
    boolean m_enableFreqValDisplay = DataExplorerConfig.DEFAULT_ENABLE_FREQ_VAL_DISPLAY;

    @Layout(ColumnsSection.class)
    @Persist(configKey = DataExplorerConfig.CFG_FREQ_VALUES_NUMBER)
    @Widget(title = "Number of most freq./infreq. values", description = """
            For any number n you select, the Nominal tab of the interactive view will list the n most
            frequent and the n most infrequent nominal values in separate columns.
            """)
    @NumberInputWidget(minValidation = IsPositiveIntegerValidation.class)
    @Effect(predicate = EnableFreqValDisplayPredicate.class, type = EffectType.ENABLE)
    int m_freqValuesNumber = DataExplorerConfig.DEFAULT_FREQ_VALUES_NUMBER;

    @Layout(ColumnsSection.class)
    @Persist(configKey = DataExplorerConfig.CFG_SHOW_MEDIAN)
    @Widget(title = "Show median (computationally expensive)", description = """
            If you check this option, the interactive view will show the median of the numerical values in
            the input data set.
            """)
    boolean m_showMedian = DataExplorerConfig.DEFAULT_SHOW_MEDIAN;

    @Layout(ColumnsSection.class)
    @Persist(configKey = DataExplorerConfig.CFG_DISPLAY_ROW_IDS)
    @Widget(title = "Display Row ID in Data Preview", description = """
            If you check this option, the Data Preview tab of the interactive view will create a column with
            Row ID of the data values.
            """)
    boolean m_displayRowIds = DataExplorerConfig.DEFAULT_DISPLAY_ROW_IDS;

    @Layout(HistogramsSection.class)
    @Persist(configKey = DataExplorerConfig.CFG_MISSING_VALUES_IN_HIST)
    @Widget(title = "Show missing values in histograms", description = """
            If you check this option, the Nominal tab of the interactive view will show missing values as an
            additional bar in the histograms.
            """)
    boolean m_missingValuesInHist = DataExplorerConfig.DEFAULT_MISSING_VALUES_IN_HIST;

    @Layout(HistogramsSection.class)
    @Persist(configKey = DataExplorerConfig.CFG_ADAPT_NUMBER_OF_HISTOGRAM_BARS)
    @Widget(title = "Enable automatic number of histogram bars", description = """
            If you check this option, the Numeric tab of the interactive view will adjust the number of
            histogram bar of each numeric data column depending on the values occurring in the column. If you
            uncheck this option, you will be able to enter a number of histogram bars used for all columns.
            """)
    @ValueReference(AdaptHistogramBarsRef.class)
    boolean m_adaptNumberOfHistogramBars = DataExplorerConfig.DEFAULT_ADAPT_NUMBER_OF_HISTOGRAM_BARS;

    @Layout(HistogramsSection.class)
    @Persist(configKey = DataExplorerConfig.CFG_NUMBER_OF_HISTOGRAM_BARS)
    @Widget(title = "Number of numeric histogram bars", description = """
            Number of histogram bars used for all numeric columns when automatic adjustment is disabled.
            """)
    @NumberInputWidget(minValidation = IsPositiveIntegerValidation.class)
    @Effect(predicate = AdaptHistogramBarsPredicate.class, type = EffectType.ENABLE)
    int m_numberOfHistogramBars = DataExplorerConfig.DEFAULT_NUMBER_OF_HISTOGRAM_BARS;

    @Layout(TitlesSection.class)
    @Persist(configKey = DataExplorerConfig.CFG_TITLE)
    @Widget(title = "Title", description = "Adds a title to the interactive view.")
    String m_title = DataExplorerConfig.DEFAULT_TITLE;

    @Layout(TitlesSection.class)
    @Persist(configKey = DataExplorerConfig.CFG_SUBTITLE)
    @Widget(title = "Subtitle", description = "Adds a subtitle to the interactive view.")
    String m_subtitle = DataExplorerConfig.DEFAULT_SUBTITLE;

    @Layout(NumberFormatterSection.class)
    @Persist(configKey = DataExplorerConfig.CFG_ENABLE_GLOBAL_NUMBER_FORMAT)
    @Widget(title = "Enable global number format (double cells)", description = """
            Enables the same number format for all double values in the interactive view.
            """)
    @ValueReference(EnableGlobalNumberFormatRef.class)
    boolean m_enableGlobalNumberFormat = DataExplorerConfig.DEFAULT_ENABLE_GLOBAL_NUMBER_FORMAT;

    @Layout(NumberFormatterSection.class)
    @Persist(configKey = DataExplorerConfig.CFG_GLOBAL_NUMBER_FORMAT_DECIMALS)
    @Widget(title = "Decimal places", description = """
            Specifies the number of decimal places for all values in the interactive view.
            """)
    @NumberInputWidget(minValidation = IsNonNegativeValidation.class)
    @Effect(predicate = EnableGlobalNumberFormatPredicate.class, type = EffectType.ENABLE)
    int m_globalNumberFormatDecimals = DataExplorerConfig.DEFAULT_GLOBAL_NUMBER_FORMAT_DECIMALS;

    @Layout(NominalValuesSection.class)
    @Persist(configKey = DataExplorerConfig.CFG_MAX_NOMINAL_VALUES)
    @Widget(title = "Max number of nominal values", description = """
            Specifies the maximum number of unique values considered in one nominal column.
            """)
    @NumberInputWidget(minValidation = IsPositiveIntegerValidation.class)
    int m_maxNominalValues = DataExplorerConfig.DEFAULT_MAX_NOMINAL_VALUES;

    @Layout(InteractivitySection.class)
    @Persist(configKey = DataExplorerConfig.CFG_DISPLAY_FULLSCREEN_BUTTON)
    @Widget(title = "Display fullscreen button", description = """
            If you check this option, the interactive view will enable option to enlarge the interactive
            view.
            """)
    boolean m_displayFullscreenButton = DataExplorerConfig.DEFAULT_DISPLAY_FULLSCREEN_BUTTON;

    @Layout(InteractivitySection.class)
    @Persist(configKey = DataExplorerConfig.CFG_ENABLE_SELECTION)
    @Widget(title = "Enable selection", description = """
            Enables selection of the data columns to be excluded from further analysis.
            """)
    boolean m_enableSelection = DataExplorerConfig.DEFAULT_ENABLE_SELECTION;

    @Layout(InteractivitySection.class)
    @Persist(configKey = DataExplorerConfig.CFG_ENABLE_SEARCHING)
    @Widget(title = "Enable searching", description = "Enables searching the data table.")
    boolean m_enableSearching = DataExplorerConfig.DEFAULT_ENABLE_SEARCHING;

    @Layout(InteractivitySection.class)
    @Persist(configKey = DataExplorerConfig.CFG_ENABLE_SORTING)
    @Widget(title = "Enable sorting on columns", description = "Enables sorting of columns.")
    @ValueReference(EnableSortingRef.class)
    boolean m_enableSorting = DataExplorerConfig.DEFAULT_ENABLE_SORTING;

    @Layout(InteractivitySection.class)
    @Persist(configKey = DataExplorerConfig.CFG_ENABLE_CLEAR_SORT_BUTTON)
    @Widget(title = "Enable \"Clear Sorting\" button", description = """
            Enables undoing any sorting by clicking on the "Clear Sorting" button.
            """)
    @Effect(predicate = EnableSortingPredicate.class, type = EffectType.ENABLE)
    boolean m_enableClearSortButton = DataExplorerConfig.DEFAULT_ENABLE_CLEAR_SORT_BUTTON;

    @Layout(RowsSection.class)
    @Persist(configKey = DataExplorerConfig.CFG_DISPLAY_ROW_NUMBER)
    @Widget(title = "Number of rows for data preview", description = """
            Determines the total number of rows displayed on the Data Preview tab of the interactive view.
            """)
    @NumberInputWidget(minValidation = IsPositiveIntegerValidation.class)
    int m_displayRowNumber = DataExplorerConfig.DEFAULT_DISPLAY_ROW_NUMBER;

    @Layout(RowsSection.class)
    @Persist(configKey = DataExplorerConfig.CFG_ENABLE_PAGING)
    @Widget(title = "Enable pagination", description = """
            Enables spreading the table over several pages of the interactive view.
            """)
    @ValueReference(EnablePagingRef.class)
    boolean m_enablePaging = DataExplorerConfig.DEFAULT_ENABLE_PAGING;

    @Layout(RowsSection.class)
    @Persist(configKey = DataExplorerConfig.CFG_INITIAL_PAGE_SIZE)
    @Widget(title = "Initial page size", description = """
            Determines the number of rows per page in the interactive view.
            """)
    @NumberInputWidget(minValidation = IsPositiveIntegerValidation.class)
    @Effect(predicate = EnablePagingPredicate.class, type = EffectType.ENABLE)
    int m_initialPageSize = DataExplorerConfig.DEFAULT_INITIAL_PAGE_SIZE;

    @Layout(RowsSection.class)
    @Persist(configKey = DataExplorerConfig.CFG_ENABLE_PAGE_SIZE_CHANGE)
    @Widget(title = "Enable page size change control", description = """
            Enables changing the number of rows per page in the interactive view.
            """)
    @ValueReference(EnablePageSizeChangeRef.class)
    @Effect(predicate = EnablePagingPredicate.class, type = EffectType.ENABLE)
    boolean m_enablePageSizeChange = DataExplorerConfig.DEFAULT_ENABLE_PAGE_SIZE_CHANGE;

    @Layout(RowsSection.class)
    @Widget(title = "Selectable page sizes", description = """
            Determines the page sizes you can select in the interactive view. Values need to be
            comma-separated.
            """)
    @TextInputWidget(patternValidation = IsPageSelectionString.class)
    @Effect(predicate = EnablePagingAndPageSizeChangePredicate.class, type = EffectType.ENABLE)
    @Persistor(AllowedPageSizesPersistor.class)
    String m_allowedPageSizesString = "10, 25, 50, 100";

    @Layout(RowsSection.class)
    @Persist(configKey = DataExplorerConfig.CFG_PAGE_SIZE_SHOW_ALL)
    @Widget(title = "Add \"All\" option to page sizes", description = """
            Adds the option of having all rows on one page to the page sizes you can select in the
            interactive view.
            """)
    @Effect(predicate = EnablePagingAndPageSizeChangePredicate.class, type = EffectType.ENABLE)
    boolean m_pageSizeShowAll = DataExplorerConfig.DEFAULT_PAGE_SIZE_SHOW_ALL;

    @Layout(RowsSection.class)
    @Persist(configKey = DataExplorerConfig.CFG_ENABLE_JUMP_TO_PAGE)
    @Widget(title = "Display field to jump to a page directly", description = """
            Enables jumping to a specific page in the interactive view.
            """)
    @Effect(predicate = EnablePagingPredicate.class, type = EffectType.ENABLE)
    boolean m_enableJumpToPage = DataExplorerConfig.DEFAULT_ENABLE_JUMP_TO_PAGE;

    // This configuration key is not used in the legacy dialog but can be set as flow variable.
    @Persist(configKey = DataExplorerConfig.CFG_CUSTOM_CSS)
    String m_customCSS = DataExplorerConfig.DEFAULT_CUSTOM_CSS;

    // This configuration key is not used in the legacy dialog but can be set as flow variable.
    @Persist(configKey = DataExplorerConfig.CFG_HIDE_IN_WIZARD)
    boolean m_hideInWizard = DataExplorerConfig.DEFAULT_HIDE_IN_WIZARD;

    // This configuration key is not used in the legacy dialog but can be set as flow variable.
    @Persist(configKey = DataExplorerConfig.CFG_DISPLAY_COLUMN_HEADERS)
    boolean m_displayColumnHeaders = DataExplorerConfig.DEFAULT_DISPLAY_COLUMN_HEADERS;

    // This configuration key is not used in the legacy dialog but can be set as flow variable.
    @Persist(configKey = DataExplorerConfig.CFG_FIXED_HEADERS)
    boolean m_fixedHeaders = DataExplorerConfig.DEFAULT_FIXED_HEADERS;

    static final class EnableFreqValDisplayRef implements ParameterReference<Boolean> {}

    static final class AdaptHistogramBarsRef implements ParameterReference<Boolean> {}

    static final class EnableGlobalNumberFormatRef implements ParameterReference<Boolean> {}

    static final class EnablePagingRef implements ParameterReference<Boolean> {}

    static final class EnablePageSizeChangeRef implements ParameterReference<Boolean> {}

    static final class EnableSortingRef implements ParameterReference<Boolean> {}

    static final class EnableFreqValDisplayPredicate implements EffectPredicateProvider {

        @Override
        public EffectPredicate init(final PredicateInitializer i) {
            return i.getBoolean(EnableFreqValDisplayRef.class).isTrue();
        }

    }

    static final class AdaptHistogramBarsPredicate implements EffectPredicateProvider {

        @Override
        public EffectPredicate init(final PredicateInitializer i) {
            return i.getBoolean(AdaptHistogramBarsRef.class).isFalse();
        }

    }

    static final class EnableGlobalNumberFormatPredicate implements EffectPredicateProvider {

        @Override
        public EffectPredicate init(final PredicateInitializer i) {
            return i.getBoolean(EnableGlobalNumberFormatRef.class).isTrue();
        }

    }

    static final class EnablePagingPredicate implements EffectPredicateProvider {

        @Override
        public EffectPredicate init(final PredicateInitializer i) {
            return i.getBoolean(EnablePagingRef.class).isTrue();
        }

    }

    static final class EnablePagingAndPageSizeChangePredicate implements EffectPredicateProvider {

        @Override
        public EffectPredicate init(final PredicateInitializer i) {
            return i.getBoolean(EnablePagingRef.class).isTrue()
                    .and(i.getBoolean(EnablePageSizeChangeRef.class).isTrue());
        }

    }

    static final class EnableSortingPredicate implements EffectPredicateProvider {

        @Override
        public EffectPredicate init(final PredicateInitializer i) {
            return i.getBoolean(EnableSortingRef.class).isTrue();
        }

    }

    static final class AllowedPageSizesPersistor implements NodeParametersPersistor<String> {

        @Override
        public String load(final NodeSettingsRO settings) throws InvalidSettingsException {
            int[] sizes = settings.getIntArray(
                DataExplorerConfig.CFG_PAGE_SIZES, DataExplorerConfig.DEFAULT_PAGE_SIZES);
            if (sizes.length < 1) {
                return "";
            }
            StringBuilder builder = new StringBuilder(String.valueOf(sizes[0]));
            for (int i = 1; i < sizes.length; i++) {
                builder.append(", ");
                builder.append(sizes[i]);
            }
            return builder.toString();
        }

        @Override
        public void save(final String value, final NodeSettingsWO settings) {
            try {
                String[] sizesArray = value.split(",");
                int[] allowedPageSizes = new int[sizesArray.length];
                for (int i = 0; i < sizesArray.length; i++) {
                    allowedPageSizes[i] = Integer.parseInt(sizesArray[i].trim());
                }
                settings.addIntArray(DataExplorerConfig.CFG_PAGE_SIZES, allowedPageSizes);
            } catch (NumberFormatException e) {
                // This should never happen due to prior validation
            }
        }

        @Override
        public String[][] getConfigPaths() {
            return new String[][]{{DataExplorerConfig.CFG_PAGE_SIZES}};
        }

    }

    private static final class IsPageSelectionString extends PatternValidation {

        @Override
        protected String getPattern() {
            // this regex allows values of the form e.g. "10, 25, 50, 100"
            return "^\\d+(?:\\s*,\\s*\\d+)*$";
        }

        @Override
        public String getErrorMessage() {
            return "Please enter a valid page selection (comma-separated positive integers).";
        }

    }

}
