heatmap_namespace = (function() {
    var heatmap = {};
    var _representation,
        _value,
        _table,
        _colNames,
        _axis,
        _scales,
        _drawCellQueue,
        _tooltip,
        _colorRange,
        _filteredData,
        _zoomDimensions,
        _cellWidth,
        _cellHeight,
        _wrapper,
        _transformer,
        _cellHighlighter,
        _maxExtensionY,
        _maxExtensionX;

    // Hardcoded Default Settings
    var _minCellSize = 12;
    var _devicePixelRatio = window.devicePixelRatio;
    var _maxCanvasHeight = 8000; // canvas has native size limits
    var _defaultMargin = { top: 10, left: 10, right: 10, bottom: 10 };
    var _margin = {};
    var _defaultZoomX = 0;
    var _defaultZoomY = 0;
    var _defaultZoomK = 1;
    var _legendWidth = 140;
    var _legendHeight = 50;
    var _legendColorRangeHeight = 20;
    var _legendMargin = 5;
    var _infoWrapperMinHeight = 80;
    var _xAxisLabelTransform = 'rotate(-65) translate(10 8)';
    var _titlesHeight = 25;

    // State management object
    var defaultViewValues = {
        selection: [],
        currentPage: 1,
        zoomX: _defaultZoomX,
        zoomY: _defaultZoomY,
        zoomK: _defaultZoomK
    };

    heatmap.init = function(representation, value) {
        if (!representation.table) {
            // todo: error handling
            return;
        }

        if (!representation.columns.length) {
            // todo: error handling
            return;
        }

        // prepare data
        _representation = representation;
        _value = Object.assign(defaultViewValues, value);
        _table = new kt();
        _table.setDataTable(representation.table);

        // Get valid indexes for heatmap columns by comparing them to input colNames
        _colNames = [];
        var repColNames = _representation.table.spec.colNames;
        _representation.columns.map(function(hmColName) {
            _colNames[repColNames.indexOf(hmColName)] = hmColName;
        });

        toggleSubscribeFilter();
        toggleSubscribeSelection();

        drawControls();

        document.body.insertAdjacentHTML('beforeend', '<div class="knime-layout-container"></div>');
        drawChart();
    };

    heatmap.getComponentValue = function() {
        return _value;
    };

    heatmap.validate = function() {
        return true;
    };

    heatmap.getSVG = function() {
        var svgElement = d3.select('.heatmap').node();
        knimeService.inlineSvgStyles(svgElement);
        return new XMLSerializer().serializeToString(svgElement);
    };

    function toggleSubscribeSelection() {
        if (_value.subscribeSelection) {
            knimeService.subscribeToSelection(_table.getTableId(), onSelectionChange);
        } else {
            knimeService.unsubscribeSelection(_table.getTableId(), onSelectionChange);
        }
    }

    function toggleSubscribeFilter() {
        if (_value.subscribeFilter) {
            knimeService.subscribeToFilter(_table.getTableId(), onFilterChange, _table.getFilterIds());
        } else {
            knimeService.unsubscribeFilter(_table.getTableId(), onFilterChange);
        }
    }

    function onFilterChange(data) {
        _filteredData = _table.getRows().filter(function(row) {
            return _table.isRowIncludedInFilter(row.rowKey, data);
        });
        drawChart();
    }

    function onSelectionChange(data) {
        if (data.reevaluate) {
            _value.selection = knimeService.getAllRowsForSelection(_table.getTableId());
        } else if (data.changeSet) {
            if (data.changeSet.added) {
                data.changeSet.added.map(function(rowId) {
                    var index = _value.selection.indexOf(rowId);
                    if (index === -1) {
                        _value.selection.push(rowId);
                    }
                });
            }
            if (data.changeSet.removed) {
                data.changeSet.removed.map(function(rowId) {
                    var index = _value.selection.indexOf(rowId);
                    if (index > -1) {
                        _value.selection.splice(index, 1);
                    }
                });
            }
        }

        styleSelectedRows();

        if (_value.showSelectedRowsOnly) {
            drawChart();
            resetZoom(true);
        }
    }

    /**
     * Filter the available data to only the selected rows
     * @param {Array} data
     */
    function getSelectionData(data) {
        if (_value.showSelectedRowsOnly) {
            return data.filter(function(row) {
                return _value.selection.indexOf(row.rowKey) > -1;
            });
        }

        return data;
    }

    /**
     * Draw and re-draw the whole chart
     */
    function drawChart() {
        var container = document.querySelector('.knime-layout-container');
        container.innerHTML = '';

        var svgWrapper =
            '<div class="knime-svg-container" data-iframe-height>\
                <span class="gradient-y"></span>\
                <span class="gradient-x"></span>\
                <svg class="heatmap"></svg>\
            </div>';
        var toolTipWrapper = '<div class="knime-tooltip"></div>';
        var infoWrapperEl = '<div class="info-wrapper"></div>';
        var progressBar = '<div class="progress-bar">Rendering ...<span class="progress"></span></div>';
        container.innerHTML = svgWrapper + infoWrapperEl + toolTipWrapper + progressBar;

        var data = _filteredData ? getSelectionData(_filteredData) : getSelectionData(_table.getRows());

        // Meta info
        var paginationData = createPagination(data);

        drawMetaInfo(paginationData);

        // Build svg based on the paginated data
        drawContents(paginationData.rows);
    }

    function getProgressBar(totalRowsCount) {
        if (!_drawCellQueue) {
            return;
        }
        var progressBar = document.querySelector('.progress-bar');
        var progressIndicator = progressBar.querySelector('.progress');

        var interval = requestInterval(function() {
            if (!_drawCellQueue) {
                interval.clear();
            }
            var percentageComplete = 100 - (_drawCellQueue.remaining() / totalRowsCount) * 100;
            if (percentageComplete < 60) {
                // only display progress bar if initial rendered percentage is low
                progressBar.style.opacity = 1;
            }
            percentageComplete = Math.min(100, percentageComplete);
            progressIndicator.style.width = percentageComplete + '%';
            if (percentageComplete >= 100) {
                progressBar.style.opacity = 0;
                interval.clear();
            }
        }, 10);
    }

    function drawMetaInfo(paginationData) {
        var paginationHtml = _representation.enablePaging ? getPaginationHtml(paginationData) : '';

        var displayedRows =
            '<div><p>Showing ' +
            (paginationData.totalRowCount > 1 ? paginationData.pageRowStartIndex + 1 : paginationData.totalRowCount) +
            ' to ' +
            paginationData.pageRowEndIndex +
            ' of ' +
            paginationData.totalRowCount +
            ' entries</p>';

        displayedRows += '<p class="partially-displayed-hint">(Partially displayed)</p></div>';

        var infoWrapper = document.body.querySelector('.info-wrapper');
        infoWrapper.innerHTML = displayedRows + paginationHtml;
        infoWrapper.style.minHeight = _infoWrapperMinHeight + 'px';
    }

    function updateTitles() {
        document.querySelector('.knime-title').textContent = _value.chartTitle;
        document.querySelector('.knime-subtitle').textContent = _value.chartSubtitle;

        if (!_representation.enableViewConfiguration && (_value.chartTitle || _value.chartSubtitle)) {
            _margin.top += _titlesHeight;
        }
    }

    function drawControls() {
        if (!_representation.enableViewConfiguration) {
            return;
        }

        if (_representation.displayFullscreenButton) {
            knimeService.allowFullscreen();
        }

        if (
            _representation.enablePanning ||
            _representation.enableZoom ||
            _representation.enableSelection ||
            _representation.showZoomResetButton
        ) {
            knimeService.addNavSpacer();
        }

        if (_representation.enableSelection) {
            var selectionButtonClicked = function() {
                _value.enableSelection = !_value.enableSelection;
                var button = document.getElementById('heatmap-selection-mode');
                button.classList.toggle('active');

                toggleSelectionClass();
            };
            knimeService.addButton(
                'heatmap-selection-mode',
                'check-square-o',
                'Mouse Mode "Select"',
                selectionButtonClicked
            );
            if (_representation.enableSelection && !_representation.enablePanning) {
                selectionButtonClicked();
            }

            knimeService.addButton('heatmap-clear-selection-button', 'minus-square-o', 'Clear selection', function() {
                _value.selection = [];
                if (_value.publishSelection) {
                    knimeService.setSelectedRows(_table.getTableId(), _value.selection);
                }
                styleSelectedRows();
            });
            knimeService.addNavSpacer();
        }

        if (_representation.enableZoom) {
            var zoomButtonClicked = function(evt, initialize) {
                _value.enableZoom = !_value.enableZoom;
                var button = document.getElementById('heatmap-mouse-mode-zoom');
                button.classList.toggle('active');
                if (!initialize) {
                    setZoomEvents();
                }
            };
            knimeService.addButton('heatmap-mouse-mode-zoom', 'search', 'Mouse Mode "Zoom"', zoomButtonClicked);
            if (_representation.enableZoom && !_representation.enableSelection && !_representation.enablePanning) {
                zoomButtonClicked(false, true);
            }
        }

        if (_representation.enablePanning) {
            var panButtonClicked = function(evt, initialize) {
                _value.enablePanning = !_value.enablePanning;
                var button = document.getElementById('heatmap-mouse-mode-pan');
                button.classList.toggle('active');

                if (!initialize) {
                    setZoomEvents();
                }

                togglePanningClass();
            };
            knimeService.addButton('heatmap-mouse-mode-pan', 'arrows', 'Mouse Mode "Pan"', panButtonClicked);
            if (_representation.enablePanning) {
                panButtonClicked(false, true);
            }
        }

        if (_representation.showZoomResetButton) {
            knimeService.addButton('scatter-zoom-reset-button', 'search-minus', 'Reset Zoom', function() {
                resetZoom(true);
            });
        }
        // Create menu items
        if (_representation.enableTitleChange) {
            var chartTitleText = knimeService.createMenuTextField(
                'chartTitleText',
                _value.chartTitle,
                function() {
                    if (_value.chartTitle !== this.value) {
                        _value.chartTitle = this.value;
                        drawChart();
                    }
                },
                true
            );
            knimeService.addMenuItem('Chart Title:', 'header', chartTitleText);
            var chartSubtitleText = knimeService.createMenuTextField(
                'chartSubtitleText',
                _value.chartSubtitle,
                function() {
                    if (_value.chartSubtitle !== this.value) {
                        _value.chartSubtitle = this.value;
                        drawChart();
                    }
                },
                true
            );
            knimeService.addMenuItem('Chart Subtitle:', 'header', chartSubtitleText, null, knimeService.SMALL_ICON);
            knimeService.addMenuDivider();
        }

        if (_representation.enableShowToolTips) {
            var showToolTips = knimeService.createMenuCheckbox('showToolTips', _value.showToolTips, function() {
                _value.showToolTips = this.checked;
            });
            knimeService.addMenuItem('Show Tooltips', 'info', showToolTips);
        }

        if (_representation.enableShowSelectedRowsOnly) {
            var showSelectedRowsOnly = knimeService.createMenuCheckbox(
                'showSelectedRowsOnly',
                _value.showSelectedRowsOnly,
                function() {
                    _value.showSelectedRowsOnly = this.checked;
                    drawChart();
                    resetZoom(true);
                }
            );
            knimeService.addMenuItem('Show Selected Rows Only', 'filter', showSelectedRowsOnly);
        }

        // Selection / Filter configuration
        knimeService.addMenuDivider();
        if (_representation.enableSelection) {
            knimeService.addMenuItem(
                'Publish selection',
                knimeService.createStackedIcon('check-square-o', 'angle-right', 'faded left sm', 'right bold'),
                knimeService.createMenuCheckbox('publishSelectionCheckbox', _value.publishSelection, function() {
                    _value.publishSelection = this.checked;
                    if (_value.publishSelection) {
                        knimeService.setSelectedRows(_table.getTableId(), _value.selection);
                    }
                })
            );

            knimeService.addMenuItem(
                'Subscribe to selection',
                knimeService.createStackedIcon('check-square-o', 'angle-double-right', 'faded right sm', 'left bold'),
                knimeService.createMenuCheckbox('subscribeSelectionCheckbox', _value.subscribeSelection, function() {
                    _value.subscribeSelection = this.checked;
                    toggleSubscribeSelection();
                })
            );

            knimeService.addMenuDivider();
        }

        knimeService.addMenuItem(
            'Subscribe to filter',
            knimeService.createStackedIcon('filter', 'angle-double-right', 'faded right sm', 'left bold'),
            knimeService.createMenuCheckbox('subscribeFilterCheckbox', _value.subscribeFilter, function() {
                _value.subscribeFilter = this.checked;
                toggleSubscribeFilter();
            })
        );

        knimeService.addMenuDivider();
        if (_representation.enableColorModeEdit) {
            var updateScaleType = function() {
                _value.continuousGradient = this.value === 'linear';
                drawChart();
            };
            var linearRadio = knimeService.createMenuRadioButton('linearRadio', 'scaleType', 'linear', updateScaleType);
            linearRadio.checked = _value.continuousGradient;
            knimeService.addMenuItem('Continuous gradient', 'align-left fa-rotate-270', linearRadio);

            var quantizeRadio = knimeService.createMenuRadioButton(
                'quantizeRadio',
                'scaleType',
                'quantize',
                updateScaleType
            );
            quantizeRadio.checked = !_value.continuousGradient;
            knimeService.addMenuItem('Discrete colors', 'tasks fa-rotate-270', quantizeRadio);

            knimeService.addMenuDivider();
        }

        if (_representation.enablePageSizeChange) {
            var options = _representation.allowedPageSizes;
            if (_representation.enableShowAll) {
                options.push('all');
            }
            var pageSize = knimeService.createMenuSelect('pageSize', _value.initialPageSize, options, function() {
                var isNewSizeSmaller = this.value < _value.initialPageSize;
                _value.initialPageSize = this.value;
                drawChart();
                if (isNewSizeSmaller) {
                    // prevent not showing any rows
                    resetZoom(true);
                }
            });

            knimeService.addMenuItem('Rows per Page', 'table', pageSize);
        }
    }

    function togglePanningClass() {
        // add general css classes
        var layoutContainer = document.querySelector('.knime-layout-container');
        if (!layoutContainer) {
            return;
        }
        if (_value.enablePanning) {
            layoutContainer.classList.add('panning-enabled');
        } else {
            layoutContainer.classList.remove('panning-enabled');
        }
    }

    function toggleSelectionClass() {
        var layoutContainer = document.querySelector('.knime-layout-container');
        if (!layoutContainer) {
            return;
        }
        if (_value.enableSelection) {
            layoutContainer.classList.add('selection-enabled');
        } else {
            layoutContainer.classList.remove('selection-enabled');
        }
    }

    function togglePartiallyDisplayedClass() {
        var layoutContainer = document.querySelector('.knime-layout-container');
        if (!layoutContainer) {
            return;
        }
        var yAxis = document.querySelector('.knime-axis.knime-y');
        var xAxis = document.querySelector('.knime-axis.knime-x');
        var axisExist = yAxis && xAxis;
        if (!axisExist || areAxisCompletelyVisible(xAxis, yAxis)) {
            layoutContainer.classList.remove('partially-displayed');
        } else {
            layoutContainer.classList.add('partially-displayed');
        }
    }

    function getPaginationHtml(pagination) {
        var paginationRange = createPaginationIntervals(pagination);

        if (paginationRange.pages.length <= 1 || !_representation.enablePaging) {
            return '';
        }
        var html = '<ul class="pagination">';

        if (paginationRange.prev) {
            html += '<li><a href="#' + paginationRange.prev + '">&laquo;</a></li>';
        } else {
            html += '<li class="disabled"><span>&laquo;</span></li>';
        }

        paginationRange.pages.map(function(item) {
            if (item === '...') {
                html += '<li class="disabled"><span>' + item + '</span></li>';
            } else {
                html +=
                    '<li class="' +
                    (_value.currentPage === item ? 'active' : '') +
                    '"><a href="#' +
                    item +
                    '">' +
                    item +
                    '</a></li>';
            }
        });

        if (paginationRange.next) {
            html += '<li><a href="#' + paginationRange.next + '">&raquo;</a></li>';
        } else {
            html += '<li class="disabled"><span>&raquo;</span></li>';
        }
        html += '</ul>';
        return html;
    }

    function registerEvents() {
        var pagination = document.body.querySelector('.pagination');
        if (pagination) {
            document.body.querySelector('.pagination').addEventListener('click', function(e) {
                if (e.target.tagName === 'A') {
                    var pageNumber = parseInt(e.target.getAttribute('href').substr(1), 10);
                    _value.currentPage = pageNumber;
                    drawChart();
                }
            });
        }

        window.addEventListener('resize', drawChart);

        // Events for the svg are native js event listeners not
        // d3 event listeners for better performance
        var domWrapper = document.querySelector('.knime-svg-container svg .transformer');
        // Highlight mouseover cell and show tooltip
        domWrapper.addEventListener('mouseover', function(e) {
            domWrapper.addEventListener('mousemove', onMousemove);
        });

        var onMousemove = function(e) {
            var data = lookupCell(e);

            if (!data) {
                return;
            }
            var toolTipInnerHTML =
                '<span class="knime-tooltip-caption">x:' +
                data.x +
                ' y:' +
                data.y +
                '</span><span class="knime-tooltip-value">' +
                (data.value === null
                    ? '<span class="missing-value">?</span>'
                    : '<span class="knime-double">' + data.value + '</span>') +
                '</span>';

            showTooltip(e, toolTipInnerHTML);
        };

        domWrapper.addEventListener('mouseout', function(e) {
            hideTooltip();
            domWrapper.removeEventListener('mouseover', onMousemove);
        });

        // Row selection
        domWrapper.addEventListener('mousedown', function(e) {
            var data = lookupCell(e);
            if (!data) {
                return;
            }
            if (e.shiftKey) {
                return selectDeltaRow(data.y);
            }
            if (e.ctrlKey || e.metaKey) {
                return selectSingleRow(data.y, true);
            }
            return selectSingleRow(data.y);
        });
    }

    /**
     * Create intervals from the pagination data to limit the amount of links shown
     * @param {object} pagination data
     */
    function createPaginationIntervals(pagination) {
        var delta = 2; // number of pages displayed left and right to "center"
        var left = _value.currentPage - delta;
        var right = _value.currentPage + delta;
        var range = [];
        var paginationRange = [];
        var curPage;

        for (var i = 1; i <= pagination.pageCount; i++) {
            if (i === 1 || i === pagination.pageCount || (i >= left && i <= right)) {
                range.push(i);
            }
        }

        range.map(function(page) {
            if (curPage) {
                if (page - curPage !== 1) {
                    paginationRange.push('...');
                }
            }
            paginationRange.push(page);
            curPage = page;
        });

        return {
            prev: pagination.prev,
            next: pagination.next,
            pages: paginationRange
        };
    }

    /**
     * Create very basic pagination data from rows
     * @param {Array} data
     */
    function createPagination(data) {
        if (!_representation.enablePaging || !data) {
            return { rows: data };
        }
        var pageSize = _value.initialPageSize === 'all' ? data.length : _value.initialPageSize;

        var pageCount = Math.ceil(data.length / pageSize);

        // jump to page 1 if total number of pages exceeds current page
        _value.currentPage = _value.currentPage <= pageCount ? _value.currentPage : 1;

        var pageRowEndIndex = pageSize * _value.currentPage;
        var pageRowStartIndex = pageSize * (_value.currentPage - 1);
        var rows = data.slice(pageRowStartIndex, pageRowEndIndex);

        return {
            totalRowCount: data.length,
            rows: rows,
            pageCount: pageCount,
            pageRowEndIndex: pageRowEndIndex > data.length ? data.length : pageRowEndIndex,
            pageRowStartIndex: pageRowStartIndex,
            next: pageRowEndIndex < data.length ? _value.currentPage + 1 : false,
            prev: pageRowStartIndex > 0 ? _value.currentPage - 1 : false
        };
    }

    /**
     * Format the data on a per-row level and
     * - get current row names
     * - get row label images
     * - get labels
     * @param {Array} rows
     */
    function formatPageData(rows) {
        var rowLabelImages = [];
        var rowNames = [];
        var rowLabels = [];

        var allValues = rows.reduce(function(accumulator, row) {
            rowNames.push(row.rowKey);

            var label = _representation.labelColumn
                ? _table.getCell(row.rowKey, _representation.labelColumn)
                : row.rowKey;
            rowLabels[row.rowKey] = label;

            // Storing images in an separate array is enough
            if (_representation.svgLabelColumn) {
                rowLabelImages[row.rowKey] = _table.getCell(row.rowKey, _representation.svgLabelColumn);
            }

            return accumulator.concat(row);
        }, []);

        var measuredLabels = setMarginsForLabels(rowLabels);

        return {
            rowLabelImages: rowLabelImages,
            data: allValues,
            rowNames: rowNames,
            measuredLabels: measuredLabels
        };
    }

    /**
     * Search for a data cell based on the mouse position
     * This needs some offsetting as the position calculated via d3 doesn't match the mouseposition
     * due to relative positioning of the canvas
     *
     * @param {MouseEvent} the current mouse event
     */
    function lookupCell(e) {
        if (!_value.showToolTips && !_value.enableSelection) {
            return;
        }
        var left = document.querySelectorAll('canvas')[0].getBoundingClientRect().left;
        var top = document.querySelectorAll('canvas')[0].getBoundingClientRect().top;

        var offsetX = (e.clientX - left) / _value.zoomK;
        var offsetY = (e.clientY - top) / _value.zoomK;

        // Todo: extract onetime operations
        var xEachBand = _scales.x.step();
        var xIndex = Math.floor(offsetX / xEachBand);
        var xVal = _scales.x.domain()[xIndex];
        var xPos = _scales.x(xVal);

        var yEachBand = _scales.y.step();
        var yIndex = Math.floor(offsetY / yEachBand);
        var yVal = _scales.y.domain()[yIndex];
        var yPos = _scales.y(yVal);

        var value;
        var cell = {};
        if (xVal && yVal) {
            value = _table.getCell(yVal, xVal);

            cell = {
                x: xVal,
                y: yVal,
                value: value
            };
        }

        _cellHighlighter.style.left = Math.floor(xPos) + 'px';
        _cellHighlighter.style.top = Math.floor(yPos) + 'px';
        _cellHighlighter.style.width = Math.ceil(_cellWidth) + 1 + 'px';
        _cellHighlighter.style.height = Math.ceil(_cellHeight) + 1 + 'px';

        return cell;
    }

    /**
     * Interpolate values to create a value range of all the colors
     * @param {Number} minimum
     * @param {Number} maximum
     */
    function getLinearColorDomain(minimum, maximum) {
        var domain = [];
        var interpolator = d3.interpolateNumber(minimum, maximum);
        for (var i = 0; i < _colorRange.length; i++) {
            domain.push(interpolator(i / (_colorRange.length - 1)));
        }
        return domain;
    }

    function createScales(formattedDataset) {
        return {
            x: d3
                .scaleBand()
                .range([_margin.left, _colNames.length * _cellWidth + _margin.left])
                .domain(_colNames),
            y: d3
                .scaleBand()
                .domain(formattedDataset.rowNames)
                .range([_margin.top, formattedDataset.rowNames.length * _cellHeight + _margin.top]),
            colorScale: _value.continuousGradient
                ? d3
                      .scaleLinear()
                      .domain(getLinearColorDomain(_representation.minValue, _representation.maxValue))
                      .range(_colorRange)
                : d3
                      .scaleQuantize()
                      .domain([_representation.minValue, _representation.maxValue])
                      .range(_colorRange)
        };
    }

    function createAxis(formattedDataset) {
        return {
            x: d3.axisTop(_scales.x).tickFormat(function(d) {
                var label = formattedDataset.measuredLabels.x.values.find(function(value) {
                    return value.originalData === d;
                });
                this.parentNode.insertAdjacentHTML('beforeend', '<title>' + d + '</title>');
                return label && label.truncated ? label.truncated : label.originalData;
            }),

            y: d3.axisLeft(_scales.y).tickFormat(function(d) {
                var index = formattedDataset.rowNames.indexOf(d);
                var label = formattedDataset.measuredLabels.y.values[index];
                this.parentNode.insertAdjacentHTML('beforeend', '<title>' + d + '</title>');
                return label && label.truncated ? label.truncated : label.originalData;
            })
        };
    }

    function formatImage(string) {
        return 'data:image/svg+xml;base64,' + window.btoa(string);
    }

    /**
     * One time initialization of the zoom
     */
    function initializeZoom() {
        var svgD3 = d3.select('.knime-svg-container svg');

        var xAxisD3El = svgD3.select('.knime-axis.knime-x');
        var yAxisD3El = svgD3.select('.knime-axis.knime-y');
        var xAxisEl = xAxisD3El.node();
        var yAxisEl = yAxisD3El.node();
        var xAxisWidth = xAxisEl ? xAxisEl.getBoundingClientRect().width : 0;
        var yAxisHeight = yAxisEl ? yAxisEl.getBoundingClientRect().height : 0;

        // Set transform origins
        xAxisD3El.attr('transform-origin', _margin.left + 'px 0');
        yAxisD3El.attr('transform-origin', '0 ' + _margin.top + 'px');

        _transformer.node().style.transformOrigin = _margin.left + 'px ' + _margin.top + 'px';
        var infoWrapperHeight = document.querySelector('.info-wrapper').getBoundingClientRect().height || 0;

        _zoomDimensions = {
            xAxisWidth: xAxisWidth,
            yAxisHeight: yAxisHeight,
            minimalZoomLevel: Math.min(
                (window.innerWidth - _margin.left - _margin.right) / xAxisWidth,
                (window.innerHeight - _margin.top - infoWrapperHeight - _margin.bottom) / yAxisHeight
            )
        };
        return setZoomEvents();
    }

    /**
     * Repeatedly called method to change zoom properties
     * or disable/enable the zoom
     */
    function setZoomEvents() {
        var svgD3 = d3.select('.knime-svg-container svg');
        var xAxisD3El = svgD3.select('.knime-axis.knime-x');
        var yAxisD3El = svgD3.select('.knime-axis.knime-y');
        var transformerNode = _transformer.node();

        var zoom = d3
            .zoom()
            .translateExtent([
                [0, 0],
                [_zoomDimensions.xAxisWidth + _margin.left + _margin.right, _zoomDimensions.yAxisHeight]
            ])
            .scaleExtent([_zoomDimensions.minimalZoomLevel, 1])
            .constrain(function(transform, extent, translateExtent) {
                // see https://github.com/d3/d3-zoom/blob/master/README.md#zoom_constrain
                // the translate extent needs to dynamically append to the zoom level, therefore we need to overwrite it
                var theight = translateExtent[1][1] + (_margin.top + _margin.bottom - 1) / transform.k;

                var dx0 = transform.invertX(extent[0][0]) - translateExtent[0][0];
                var dx1 = transform.invertX(extent[1][0]) - translateExtent[1][0];
                var dy0 = transform.invertY(extent[0][1]) - translateExtent[0][1];
                var dy1 = transform.invertY(extent[1][1]) - theight;

                return transform.translate(
                    dx1 > dx0 ? (dx0 + dx1) / 2 : Math.min(0, dx0) || Math.max(0, dx1),
                    dy1 > dy0 ? (dy0 + dy1) / 2 : Math.min(0, dy0) || Math.max(0, dy1)
                );
            })
            .on('zoom', function() {
                var t = d3.event.transform;

                // prevent jumpy layout
                t.x = t.x > 0 ? 0 : t.x;
                t.y = t.y > 0 ? 0 : t.y;

                xAxisD3El.attr('transform', 'translate(' + t.x + ', ' + _margin.top + ') scale(' + t.k + ')');
                yAxisD3El.attr('transform', 'translate(' + _margin.left + ', ' + t.y + ') scale(' + t.k + ')');
                transformerNode.style.transform = 'translate(' + t.x + 'px, ' + t.y + 'px) scale(' + t.k + ')';

                _value.zoomX = t.x;
                _value.zoomY = t.y;
                _value.zoomK = t.k;

                // hack: force canvas refresh as sometimes canvas gets not fully painted
                _transformer.node().style.opacity = 0.999;
                setTimeout(function() {
                    _transformer.node().style.opacity = 1;
                }, 0);
            })
            .on('end', function() {
                togglePartiallyDisplayedClass();

                // style borders
                var borderWidth = getCurrentBorderWidth();
                _cellHighlighter.style.borderWidth = borderWidth;
                var rowHighlighters = svgD3.node().querySelectorAll('.row-highlighter');
                if (rowHighlighters.length) {
                    Array.from(rowHighlighters).map(function(rowHighlighter) {
                        rowHighlighter.style.borderWidth = borderWidth;
                    });
                }
            });

        // reset
        svgD3.on('.zoom', null);

        // init
        if (_value.enableZoom || _value.enablePanning) {
            svgD3.call(zoom);
        }

        // disable zoom events
        if (!_value.enableZoom) {
            svgD3.on('wheel.zoom', null).on('dblclick.zoom', null);
        }

        // disable panning events
        if (!_value.enablePanning) {
            svgD3
                .on('mousedown.zoom', null)
                .on('touchstart.zoom', null)
                .on('touchmove.zoom', null)
                .on('touchend.zoom', null);
        }
        return zoom;
    }

    function getCurrentBorderWidth() {
        var borderZoomFactor = 5;
        return Math.max(2, 1 + borderZoomFactor * (1 - _value.zoomK)) + 'px';
    }

    function showTooltip(e, innerHtml) {
        if (!_value.showToolTips && innerHtml) {
            return;
        }
        _tooltip.classList.add('active');
        _tooltip.innerHTML = innerHtml;
        var tooltipWidth = _tooltip.getBoundingClientRect().width;
        var tooltipHeight = _tooltip.getBoundingClientRect().height;
        var leftPos = e.clientX;
        var topPos = e.clientY - tooltipHeight;

        // make sure tooltip is visible on the right
        if (leftPos + tooltipWidth >= (window.innerWidth || document.documentElement.clientWidth)) {
            leftPos = leftPos - tooltipWidth;
        }
        _tooltip.style.left = leftPos + 'px';
        _tooltip.style.top = topPos + 'px';
    }

    function hideTooltip() {
        _tooltip.classList.remove('active');
    }

    /**
     * Because the browser has canvas size limits, we insert a new canvas "quadrant"
     * before the maxium height is reached.
     *
     * @param {Number} yExtension the y position the row is at
     * @param {Number} xExtension the x position the column is at
     */
    function appendCanvasQuadrant(xExtension, yExtension, canvasHeight, canvasWidth) {
        var canvas = _transformer
            .append('canvas')
            .attr(
                'style',
                'position: absolute;top:' +
                    yExtension +
                    'px;left:  ' +
                    xExtension +
                    'px;width:' +
                    canvasWidth +
                    'px;height:' +
                    canvasHeight +
                    'px'
            )
            .attr('id', 'c' + Math.round(_maxExtensionX) + '-' + Math.round(_maxExtensionY))
            .attr('width', _devicePixelRatio * canvasWidth + 'px')
            .attr('height', _devicePixelRatio * canvasHeight + 'px');

        return canvas.node().getContext('2d');
    }

    /**
     * Get the current canvas context to draw on
     * @param {Number} y position of the current row/cell
     */
    function getContext(x, y) {
        var yExtension = _scales.y(y);
        var xExtension = _scales.x(x);
        var maxRows = Math.ceil(_maxCanvasHeight / _devicePixelRatio / _cellHeight);
        var maxCols = Math.ceil(_maxCanvasHeight / _devicePixelRatio / _cellWidth);
        _maxExtensionX = _maxExtensionX || 0;
        _maxExtensionY = _maxExtensionY || 0;

        var canvasHeight = Math.min(_scales.y.domain().length * _cellHeight, maxRows * _cellHeight);
        var canvasWidth = Math.min(_scales.x.domain().length * _cellWidth, maxCols * _cellWidth);
        var context;
        var rowEls = _scales.x.domain();

        if (rowEls.indexOf(x) === 0) {
            // at the start of the row, reset max extension for x
            _maxExtensionX = xExtension + canvasWidth;
        }

        // Extend the limits of the canvas
        if (xExtension + _cellWidth > _maxExtensionX) {
            _maxExtensionX = xExtension + canvasWidth;
        }
        if (yExtension + _cellHeight > _maxExtensionY) {
            _maxExtensionY = yExtension + canvasHeight;
        }

        // Check if canvas already exists
        var currentCanvas = document.querySelector(
            '#c' + Math.round(_maxExtensionX) + '-' + Math.round(_maxExtensionY)
        );
        if (currentCanvas) {
            return currentCanvas.getContext('2d');
        } else {
            // else create a new quadrant
            context = appendCanvasQuadrant(xExtension, yExtension, canvasHeight, canvasWidth);
            context.scale(_devicePixelRatio, _devicePixelRatio);
            context.translate(-xExtension, -yExtension);
        }

        return context;
    }

    function getCellColor(value) {
        if (value === null) {
            return _representation.missingValueColor;
        }
        if (value > _representation.maxValue) {
            return _representation.upperOutOfRangeColor;
        }
        if (value < _representation.minValue) {
            return _representation.lowerOutOfRangeColor;
        }
        return _scales.colorScale(value);
    }

    function drawCanvasRow(row) {
        var y = row.rowKey;
        row.data.map(function(value, currentIndex) {
            if (_colNames[currentIndex] === undefined) {
                return;
            }
            var x = _colNames[currentIndex];

            var context = getContext(x, y);
            context.fillStyle = getCellColor(value);
            context.fillRect(_scales.x(x), _scales.y(y), _cellWidth, _cellHeight);
        }, []);
    }

    function drawSvgRow(row) {
        var y = row.rowKey;
        var cellGroup = _transformer.append('g');
        row.data.map(function(value, currentIndex) {
            if (_colNames[currentIndex] === undefined) {
                return;
            }
            var cell = {
                x: _colNames[currentIndex],
                y: y,
                value: value
            };

            cellGroup
                .data([cell])
                .append('rect')
                .attr('class', 'cell')
                .attr('width', _cellWidth)
                .attr('height', _cellHeight)
                .attr('y', function(d) {
                    return _scales.y(d.y);
                })
                .attr('x', function(d) {
                    return _scales.x(d.x);
                })
                .attr('fill', function(d) {
                    return getCellColor(d.value);
                });
        });
    }

    /**
     * Set new margins based on the label sizes
     */
    function setMarginsForLabels(rowLabels) {
        var container = document.querySelector('svg.heatmap');
        var maxWidth = container.getBoundingClientRect().width * 0.33;
        var maxHeight = container.getBoundingClientRect().height * 0.33;

        var measuredLabelsY = knimeService.meassureAndTruncate(d3.values(rowLabels), {
            classes: 'active',
            container: container,
            tempContainerClasses: 'knime-tick-label',
            maxWidth: maxWidth
        });

        var tempContainerAttributes = [];
        tempContainerAttributes['transform'] = _xAxisLabelTransform;

        var measuredLabelsX = knimeService.meassureAndTruncate(_colNames, {
            container: container,
            tempContainerClasses: 'knime-tick-label',
            tempContainerAttributes: tempContainerAttributes,
            maxHeight: maxHeight
        });

        var headerHeight = Math.max(knimeService.headerHeight(), _titlesHeight);

        _margin = Object.assign({}, _defaultMargin, {
            top: measuredLabelsX.max.maxHeight + _defaultMargin.top + headerHeight,
            left: measuredLabelsY.max.maxWidth + _defaultMargin.left
        });

        return {
            y: measuredLabelsY,
            x: measuredLabelsX
        };
    }

    function drawContents(rows) {
        if (_drawCellQueue) {
            _drawCellQueue.invalidate();
        }

        if (!rows || rows.length === 0) {
            d3.select('.heatmap .wrapper').remove();
            d3.select('.heatmap .axis-wrapper').remove();
            return;
        }

        var formattedDataset = formatPageData(rows);
        _colorRange = _value.continuousGradient
            ? _representation.threeColorGradient
            : _representation.discreteGradientColors;

        var svg = d3.select('.knime-svg-container svg');

        // Create titles
        svg.append('text')
            .attr('class', 'knime-title')
            .attr('x', _defaultMargin.left)
            .attr('y', 30)
            .text(_value.chartTitle);
        svg.append('text')
            .attr('class', 'knime-subtitle')
            .attr('x', _defaultMargin.left)
            .attr('y', 50)
            .text(_value.chartSubtitle);
        updateTitles();

        // Determine cell sizes
        var infoWrapperHeight = document.querySelector('.info-wrapper').getBoundingClientRect().height || 0;
        var extraAxisLabelBuffer = 30; // TODO: calculate programatically
        var headerHeight = Math.max(knimeService.headerHeight(), _titlesHeight);
        var containerWidth = _representation.resizeToWindow ? window.innerWidth : _representation.imageWidth;
        var containerHeight = _representation.resizeToWindow ? window.innerHeight : _representation.imageHeight;
        _cellWidth = Math.max(
            _minCellSize,
            (containerWidth - _margin.left - _margin.right - extraAxisLabelBuffer) / _colNames.length
        );
        _cellHeight = Math.max(
            _minCellSize,
            (containerHeight - _margin.top - headerHeight - infoWrapperHeight) / rows.length
        );

        _tooltip = document.querySelector('.knime-tooltip');
        _scales = createScales(formattedDataset);
        _axis = createAxis(formattedDataset);

        var defs = svg.append('defs');
        defs.append('clipPath')
            .attr('id', 'clip')
            .append('rect')
            .attr('y', _margin.top + 1)
            .attr('x', _margin.left + 1)
            .attr('width', '100%')
            .attr('height', '100%');

        if (_representation.runningInView) {
            _wrapper = svg
                .append('foreignObject')
                .attr('width', '100%')
                .attr('height', '100%')
                .append('xhtml:div')
                .attr('class', 'wrapper')
                .attr('style', 'clip-path:url(#clip)');
            _transformer = _wrapper.append('div').attr('class', 'transformer');

            // Improve performance: render cells progressivley
            _maxExtensionY = 0;
            _maxExtensionX = 0;
            _drawCellQueue = renderQueue(drawCanvasRow).rate(1000);
            _drawCellQueue(formattedDataset.data);
        } else {
            _transformer = svg
                .append('g')
                .attr('clip-path', 'url(#clip)')
                .append('g')
                .attr('class', 'transformer');
            // Render cells at once for image rendering
            formattedDataset.data.map(function(row) {
                drawSvgRow(row);
            });
        }

        _cellHighlighter = document.createElement('span');
        _cellHighlighter.classList.add('cell-highlighter');
        document
            .querySelector('.knime-svg-container .transformer')
            .insertAdjacentElement('beforeend', _cellHighlighter);

        registerEvents();

        getProgressBar(formattedDataset.data.length);

        drawAxis(svg, formattedDataset.rowLabelImages);

        drawLegend(svg);

        // Initialize and reset zoom
        resetZoom();

        styleSelectedRows();

        // Set a bottom padding to prevent footer overlapping the chart
        var infoWrapperHeight = document.querySelector('.info-wrapper').getBoundingClientRect().height || 0;
        document.querySelector('.knime-svg-container').style.paddingBottom = infoWrapperHeight + 'px';
        document.querySelector('.gradient-x').style.bottom = infoWrapperHeight + 'px';

        // Set gradient overlays
        document.querySelector('.gradient-x').style.height = _margin.bottom + 'px';
        document.querySelector('.gradient-y').style.width = _margin.right + 'px';

        // add some general CSS classes
        togglePanningClass();
        toggleSelectionClass();
        togglePartiallyDisplayedClass();

        resizeSvg(svg);
    }

    function resetZoom(setToDefault) {
        var zoom = initializeZoom();
        var svg = d3.select('.knime-svg-container svg');
        var resetLevel = setToDefault
            ? {
                  x: _defaultZoomX,
                  y: _defaultZoomY,
                  k: _defaultZoomK
              }
            : {
                  x: _value.zoomX,
                  y: _value.zoomY,
                  k: _value.zoomK
              };
        zoom.transform(svg, function() {
            return d3.zoomIdentity.translate(resetLevel.x, resetLevel.y).scale(resetLevel.k);
        });
        return zoom;
    }

    function drawAxis(svg, rowLabelImages) {
        // Append axis
        var maskAxis = svg
            .select('defs')
            .append('mask')
            .attr('id', 'maskAxis');
        maskAxis
            .append('rect')
            .attr('y', 0)
            .attr('x', 0)
            .attr('width', '100%')
            .attr('height', '100%')
            .attr('fill', 'white');
        maskAxis
            .append('rect')
            .attr('y', 0)
            .attr('x', 0)
            .attr('width', _margin.left + 2)
            .attr('height', _margin.top + 2)
            .attr('fill', 'black');
        maskAxis
            .append('rect')
            .attr('height', 2)
            .attr('width', 2)
            .attr('x', _margin.left)
            .attr('y', _margin.top)
            .attr('fill', 'white');

        var axisWrapper = svg
            .append('g')
            .attr('class', 'axis-wrapper')
            .attr('mask', 'url(#maskAxis)');
        axisWrapper
            .append('g')
            .attr('class', 'knime-axis knime-y')
            .call(_axis.y)
            .selectAll('text')
            .attr('font-weight', 'normal')
            .on('mouseover', function(d) {
                if (!rowLabelImages[d]) {
                    return;
                }
                d3.event.target.classList.add('active');
                var tooltipInnerHTML = '<img src="' + formatImage(rowLabelImages[d]) + '" alt/>';
                showTooltip(d3.event, tooltipInnerHTML);
            })
            .on('mouseleave', function() {
                hideTooltip();
                d3.event.target.classList.remove('active');
            });

        axisWrapper
            .append('g')
            .attr('class', 'knime-axis knime-x')
            .call(_axis.x)
            .selectAll('text')
            .attr('font-weight', 'normal')
            .style('text-anchor', 'start')
            .attr('transform', _xAxisLabelTransform);

        // general tick styling
        var ticks = axisWrapper.selectAll('.tick').attr('class', 'knime-tick');
        ticks.select('text').attr('class', 'knime-label knime-tick-label');
        ticks.select('line').attr('class', 'knime-tick-line');

        axisWrapper
            .selectAll('.knime-axis.knime-y .knime-tick')
            .attr('class', function(d) {
                if (_value.selection.indexOf(d) > -1) {
                    return 'knime-tick active';
                } else {
                    return 'knime-tick';
                }
            })
            .attr('data-id', function(d) {
                return d;
            })
            .on('click', function(d) {
                selectSingleRow(d);
            });
    }

    function resizeSvg(svg) {
        if (_representation.runningInView && !_representation.resizeToWindow) {
            var container = document.querySelector('.knime-layout-container');
            if (_representation.imageHeight) {
                container.style.height = _representation.imageHeight + 'px';
            }
            if (_representation.imageWidth) {
                container.style.width = _representation.imageWidth + 'px';
            }
        }
        if (!_representation.runningInView) {
            var imageMargin = 50;
            var legendMargin = 15;
            var imageModeMarginTop = _scales.y.domain().length * _cellHeight + _margin.top + legendMargin;
            var calcImageHeight = imageModeMarginTop + _legendHeight + imageMargin;
            var calcImageWidth = _colNames.length * _cellWidth + _margin.left + _margin.right + imageMargin;
            svg.attr('viewBox', '0 0 ' + calcImageWidth + ' ' + calcImageHeight);
            if (_representation.imageHeight) {
                svg.attr('height', _representation.imageHeight + 'px');
            }
            if (_representation.imageWidth) {
                svg.attr('width', _representation.imageWidth + 'px');
            }
        }
    }

    function drawLegend(svg) {
        var legend;

        if (_representation.runningInView) {
            // append a separate svg
            legend = d3
                .select('.info-wrapper')
                .append('svg')
                .attr('class', 'knime-legend')
                .attr('width', _legendWidth + 2 * _legendMargin)
                .attr('height', _legendHeight);
        } else {
            // append in existing svg
            var imageModeMarginTop = _scales.y.domain().length * _cellWidth + _margin.top + 15;
            var transform = 'translate(0 ' + imageModeMarginTop + ')';

            legend = svg
                .append('g')
                .attr('class', 'knime-legend')
                .attr('width', _legendWidth + 2 * _legendMargin)
                .attr('height', _legendHeight)
                .attr('transform', transform);
        }

        var legendDefs = legend.append('defs');
        var legendGradient = legendDefs.append('linearGradient').attr('id', 'legendGradient');

        var colorDomain = getLinearColorDomain(_representation.minValue, _representation.maxValue);

        // append a single rect to display a gradient
        legend
            .append('rect')
            .attr('y', 0)
            .attr('x', _legendMargin)
            .attr('class', 'knime-legend-symbol')
            .attr('width', _legendWidth)
            .attr('height', _legendColorRangeHeight)
            .attr('right', _legendMargin)
            .attr('fill', 'url(#legendGradient)');

        // set gradient stops
        if (_value.continuousGradient) {
            for (var i = 0; i < colorDomain.length; i++) {
                var percentage = (100 / (colorDomain.length - 1)) * i;
                legendGradient
                    .append('stop')
                    .attr('offset', percentage + '%')
                    .attr('style', 'stop-opacity:1; stop-color:' + _scales.colorScale(colorDomain[i]));
            }
        } else if (!_value.continuousGradient) {
            var legendCellPercentage = 100 / colorDomain.length;
            var previousPercentage = 0;
            var interpolator = d3.interpolateNumber(_representation.minValue, _representation.maxValue);
            var tickValues = [];
            tickValues.push(_representation.minValue, _representation.maxValue);

            for (var j = 0; j < colorDomain.length; j++) {
                var currentPercentage = legendCellPercentage * (j + 1);

                tickValues.push(interpolator(currentPercentage / 100));

                legendGradient
                    .append('stop')
                    .attr('offset', previousPercentage + '%')
                    .attr('style', 'stop-opacity:1; stop-color:' + _scales.colorScale(colorDomain[j]));
                legendGradient
                    .append('stop')
                    .attr('offset', currentPercentage + '%')
                    .attr('style', 'stop-opacity:1; stop-color:' + _scales.colorScale(colorDomain[j]));
                previousPercentage = currentPercentage;
            }
        }

        var legendScale = d3
            .scaleLinear()
            .domain([_representation.minValue, _representation.maxValue])
            .range([0, _legendWidth]);

        var legendAxis = d3
            .axisBottom(legendScale)
            .tickValues(tickValues || colorDomain)
            .tickFormat(function(d) {
                return Math.round(d * 100) / 100;
            });

        var axis = legend
            .append('g')
            .attr('transform', 'translate(' + _legendMargin + ', ' + _legendColorRangeHeight + ')')
            .attr('class', 'legend-axis')
            .call(legendAxis);

        var text = axis
            .selectAll('text')
            .attr('class', 'knime-legend-label')
            .attr('font-weight', 'normal');

        if (axis && axis.node().getBoundingClientRect().width > _legendWidth) {
            // make legend svg wider if axis is wider, for example if tick values are too long
            legend.attr('width', axis.node().getBoundingClientRect().width);
        }
    }

    /**
     * Select multiple rows via shiftkey
     *
     * @param {String} selectedRowId
     */
    function selectDeltaRow(selectedRowId) {
        if (!_value.selection.length) {
            // Delta selection is not possible if no row is selected
            return;
        }
        // Get closest selected row to newly selected row
        var rowNames = _scales.y.domain();
        var currentIndex = rowNames.indexOf(selectedRowId);
        var closestRow = _value.selection.reduce(
            function(closestRow, rowId) {
                var rowIdIndex = rowNames.indexOf(rowId);
                var indexDistance = Math.abs(currentIndex - rowIdIndex);
                if (indexDistance < closestRow.distance) {
                    return {
                        distance: indexDistance,
                        index: rowIdIndex
                    };
                }
                return closestRow;
            },
            {
                distance: Number.POSITIVE_INFINITY,
                index: Number.POSITIVE_INFINITY
            }
        );

        var startIndex = Math.min(closestRow.index, currentIndex);
        var endIndex = Math.max(closestRow.index, currentIndex);
        var rowKey;
        for (var i = startIndex; i <= endIndex; i++) {
            rowKey = rowNames[i];
            if (!_value.selection.indexOf(rowKey) > -1) {
                _value.selection.push(rowKey);
            }
        }

        styleSelectedRows();

        if (_value.publishSelection) {
            knimeService.setSelectedRows(_table.getTableId(), _value.selection);
        }
    }

    function selectSingleRow(selectedRowId, keepCurrentSelections) {
        if (!_value.enableSelection) {
            return;
        }

        // Cast optional parameter to boolean
        keepCurrentSelections = !!keepCurrentSelections;

        if (!keepCurrentSelections) {
            // Remove all selections
            _value.selection = [];
            if (_value.publishSelection) {
                knimeService.setSelectedRows(_table.getTableId(), []);
            }
            styleSelectedRows();
        }

        if (_value.selection.indexOf(selectedRowId) > -1) {
            _value.selection = _value.selection.filter(function(rowId) {
                return rowId !== selectedRowId;
            });
            styleSelectedRows();
            if (_value.publishSelection) {
                knimeService.removeRowsFromSelection(_table.getTableId(), [selectedRowId]);
            }
        } else {
            _value.selection.push(selectedRowId);
            styleSelectedRows();
            if (_value.publishSelection) {
                knimeService.addRowsToSelection(_table.getTableId(), [selectedRowId]);
            }
        }

        if (_value.showSelectedRowsOnly) {
            drawChart();
        }
    }

    function styleSelectedRows() {
        d3.selectAll('.knime-axis.knime-y .knime-tick').attr('class', 'knime-tick');

        // Style row labels
        _value.selection.map(function(selectedRowId) {
            d3.select('.knime-axis.knime-y [data-id="' + selectedRowId + '"]').attr('class', 'knime-tick active');
        });

        // remove row highlighters
        var rowHighlighters = document.querySelectorAll('.row-highlighter');
        if (rowHighlighters.length) {
            rowHighlighters = Array.from(rowHighlighters);
            rowHighlighters.map(function(highlighter) {
                highlighter.outerHTML = '';
            });
        }

        _value.selection = sortByDatasetRows(getUniques(_value.selection));
        var startRowId = false;
        var selectionEnded = true;
        var yDomain = _scales.y.domain();
        yDomain.map(function(rowId, rowIndex) {
            if (_value.selection.indexOf(rowId) > -1) {
                if (!startRowId && selectionEnded) {
                    selectionEnded = false;
                    startRowId = rowId;
                }
            } else {
                if (!selectionEnded && startRowId) {
                    selectionEnded = true;
                    endSelection(startRowId, yDomain[rowIndex - 1]);
                    startRowId = false;
                }
            }
        });
    }

    function endSelection(startRowId, endRowId) {
        var startPosition = _scales.y(startRowId);
        var endPosition = _scales.y(endRowId) + Math.ceil(_cellHeight);
        var highlighter = document.createElement('SPAN');
        highlighter.classList.add('row-highlighter');

        highlighter.style.top = Math.floor(startPosition) + 'px';

        highlighter.style.left = Math.ceil(_margin.left) + 'px';
        highlighter.style.height = Math.ceil(endPosition - startPosition) + 1 + 'px';
        highlighter.style.width = Math.floor(_colNames.length * _cellWidth) + 'px';
        highlighter.style.borderWidth = getCurrentBorderWidth();
        _transformer.node().insertAdjacentElement('beforeend', highlighter);
    }

    function getUniques(arr) {
        return Array.from(new Set(arr));
    }

    function sortByDatasetRows(arr) {
        var yDomain = _scales.y.domain();
        return arr.sort(function(a, b) {
            return yDomain.indexOf(a) - yDomain.indexOf(b);
        });
    }

    /**
     * Check if the axis overlap with the 'fade-out' gradients
     * to see if all the content is displayed
     */
    function areAxisCompletelyVisible(xAxis, yAxis) {
        var yAxisRect = yAxis.getBoundingClientRect();
        var xAxisRect = xAxis.getBoundingClientRect();
        var yAxisEndPos = yAxisRect.height + yAxisRect.top;
        var xAxisEndPos = xAxisRect.width + yAxisRect.left;
        var gradientXTopPos = document.querySelector('.gradient-x').getBoundingClientRect().top;
        var gradientYLeftPos = document.querySelector('.gradient-y').getBoundingClientRect().left;

        return Math.floor(yAxisEndPos) <= gradientXTopPos && Math.floor(xAxisEndPos) <= gradientYLeftPos;
    }

    /**
     * Create a callback to be called repeatedly via requestanimationframe
     * @param {function} callback to be called repeatedly
     * @param {Number} interval delay
     */
    function requestInterval(callback, delay) {
        var dateNow = Date.now;
        var start = dateNow();
        var stop;
        var interval = function() {
            if (dateNow() - start > delay) {
                start += delay;
                callback();
            }
            return stop || window.requestAnimationFrame(interval);
        };
        window.requestAnimationFrame(interval);
        return {
            clear: function() {
                stop = true;
            }
        };
    }

    return heatmap;
})();
