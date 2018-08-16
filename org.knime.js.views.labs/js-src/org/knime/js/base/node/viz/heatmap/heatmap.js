heatmap_namespace = (function() {
    var heatmap = {};
    var _representation,
        _value,
        _table,
        _colNames,
        _wrapper,
        _axis,
        _scales,
        _drawCellQueue,
        _tooltip,
        _colorRange,
        _filteredData,
        _zoomDimensions;

    // Hardcoded Default Settings
    var _cellSize = 15;
    var _margin = { top: 80, left: 50, right: 10 };
    var _defaultZoomLevel = {
        x: 0,
        y: 0,
        k: 1
    };
    var _legendWidth = 140;
    var _legendHeight = 50;
    var _legendColorRangeHeight = 20;
    var _legendMargin = 5;

    // State managment objects
    var defaultViewValues = {
        selectedRowsBuffer: [],
        currentPage: 1,
        currentZoomLevel: _defaultZoomLevel,
        titlesHeight: 50
    };

    heatmap.init = function(representation, value) {
        if (!representation.table) {
            //todo: error
            return;
        }

        if (!representation.columns.length) {
            //todo: error
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

        if (_representation.subscribeSelection) {
            knimeService.subscribeToSelection(_table.getTableId(), onSelectionChange);
        }
        if (_representation.subscribeFilter) {
            var filterIds = _table.getFilterIds();
            for (var i = 0; i < filterIds.length; i++) {
                knimeService.subscribeToFilter(_table.getTableId(), onFilterChange, filterIds[i]);
            }
        }

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
        var xmlSerializer = new XMLSerializer();
        return xmlSerializer.serializeToString(svgElement);
    };

    function onFilterChange(data) {
        _filteredData = _table.getRows().filter(function(row) {
            return _table.isRowIncludedInFilter(row.rowKey, data);
        });
        drawChart();
    }

    function onSelectionChange(data) {
        var removed = data.changeSet.removed;
        var added = data.changeSet.added;
        if (added) {
            added.map(function(rowId) {
                var index = _value.selectedRowsBuffer.indexOf(rowId);
                if (index === -1) {
                    _value.selectedRowsBuffer.push(rowId);
                }
            });
            styleSelectedRows(added, true);
        }
        if (removed) {
            removed.map(function(rowId) {
                var index = _value.selectedRowsBuffer.indexOf(rowId);
                if (index > -1) {
                    _value.selectedRowsBuffer.splice(index, 1);
                }
            });
            styleSelectedRows(removed, false);
        }
        if (_value.showOnlySelectedRows) {
            drawChart();
        }
    }

    /**
     * Filter the available data to only the selected rows
     * @param {Array} data
     */
    function getSelectionData(data) {
        if (_value.showOnlySelectedRows) {
            return data.filter(function(row) {
                return _value.selectedRowsBuffer.indexOf(row.rowKey) > -1;
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
            '<div class="knime-svg-container" data-iframe-height><span class="gradient" style="width:' +
            _margin.right +
            'px"></span></div>';
        var toolTipWrapper = '<div class="knime-tooltip"></div>';
        var infoWrapperEl = '<div class="info-wrapper"></div>';
        container.innerHTML = svgWrapper + infoWrapperEl + toolTipWrapper;

        var data = _filteredData ? getSelectionData(_filteredData) : getSelectionData(_table.getRows());

        // Meta info
        var paginationData = createPagination(data);
        drawMetaInfo(paginationData);

        // Build svg based on the paginated data
        drawSvg(paginationData.rows);

        // Events
        registerDomEvents();
    }

    function drawMetaInfo(paginationData) {
        var paginationHtml = _representation.enablePaging ? getPaginationHtml(paginationData) : '';
        var displayedRows =
            '<p>Showing ' +
            (paginationData.totalRowCount > 1 ? paginationData.pageRowStartIndex + 1 : paginationData.totalRowCount) +
            ' to ' +
            paginationData.pageRowEndIndex +
            ' of ' +
            paginationData.totalRowCount +
            ' entries</p>';
        document.body.querySelector('.info-wrapper').innerHTML = displayedRows + paginationHtml;
    }

    function updateTitles() {
        document.querySelector('.knime-title').textContent = _value.chartTitle;
        document.querySelector('.knime-subtitle').textContent = _value.chartSubtitle;

        if (_value.titlesExist === undefined) {
            // on first draw check if titles exist
            _value.titlesExist = !!_value.chartTitle || !!_value.chartSubtitle;
            _margin.top = _value.titlesExist ? _margin.top + _value.titlesHeight : _margin.top;
        }

        // Decide whether or not a redraw is neccessary
        if (!_value.chartTitle && !_value.chartSubtitle) {
            if (_value.titlesExist === true) {
                _value.titlesExist = false;
                _margin.top = _margin.top - _value.titlesHeight;
                drawChart();
            }
        }
        if (_value.chartTitle || _value.chartSubtitle) {
            if (_value.titlesExist === false) {
                _value.titlesExist = true;
                _margin.top = _margin.top + _value.titlesHeight;
                drawChart();
            }
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

        if (_representation.enableSelection) {
            var selectionButtonClicked = function() {
                _value.enableSelection = !_value.enableSelection;
                var button = document.getElementById('heatmap-selection-mode');
                button.classList.toggle('active');
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
        }

        if (_representation.enablePanning) {
            var panButtonClicked = function(evt, initialize) {
                _value.enablePanning = !_value.enablePanning;
                var button = document.getElementById('heatmap-mouse-mode-pan');
                button.classList.toggle('active');
                if (!initialize) {
                    setZoomEvents();
                }
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
                    if (_value.chartTitle != this.value) {
                        _value.chartTitle = this.value;
                        updateTitles();
                    }
                },
                true
            );
            knimeService.addMenuItem('Chart Title:', 'header', chartTitleText);
            var chartSubtitleText = knimeService.createMenuTextField(
                'chartSubtitleText',
                _value.chartSubtitle,
                function() {
                    if (_value.chartSubtitle != this.value) {
                        _value.chartSubtitle = this.value;
                        updateTitles();
                    }
                },
                true
            );
            knimeService.addMenuItem('Chart Subtitle:', 'header', chartSubtitleText, null, knimeService.SMALL_ICON);
            knimeService.addMenuDivider();
        }

        if (_representation.enableShowToolTips) {
            var displayDataCellToolTip = knimeService.createMenuCheckbox(
                'displayDataCellToolTip',
                _value.displayDataCellToolTip,
                function() {
                    _value.displayDataCellToolTip = this.checked;
                }
            );
            knimeService.addMenuItem('Show Tooltips', 'info', displayDataCellToolTip);
        }

        var showOnlySelectedRows = knimeService.createMenuCheckbox(
            'showOnlySelectedRows',
            _value.showOnlySelectedRows,
            function() {
                _value.showOnlySelectedRows = this.checked;
                drawChart();
            }
        );
        knimeService.addMenuItem('Show Selected Rows Only', 'filter', showOnlySelectedRows);

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
            var initialPageSize = knimeService.createMenuSelect(
                'initialPageSize',
                _value.initialPageSize,
                _representation.allowedPageSizes,
                function() {
                    _value.initialPageSize = this.value;
                    drawChart();
                }
            );
            knimeService.addMenuItem('Rows per Page', 'table', initialPageSize);
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

    function registerDomEvents() {
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
    }

    /**
     * Create intervals from the pagination data to limit the amount of links shown
     * @param {object} pagination data
     */
    function createPaginationIntervals(pagination) {
        var delta = 2; // number of pages displayed left and right to "center"
        var left = _value.currentPage - delta;
        var right = _value.currentPage + delta + 1;
        var range = [];
        var paginationRange = [];
        var curPage;

        for (var i = 1; i <= pagination.pageCount; i++) {
            if (i == 1 || i == pagination.pageCount || (i >= left && i < right)) {
                range.push(i);
            }
        }

        range.map(function(page) {
            if (curPage) {
                if (page - curPage === delta) {
                    paginationRange.push(page + 1);
                } else if (page - curPage !== 1) {
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
        var pageCount = Math.ceil(data.length / _value.initialPageSize);

        // jump to page 1 if total number of pages exceeds current page
        _value.currentPage = _value.currentPage <= pageCount ? _value.currentPage : 1;

        var pageRowEndIndex = _value.initialPageSize * _value.currentPage;
        var pageRowStartIndex = _value.initialPageSize * (_value.currentPage - 1);
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
     * Format the data on a per-cell level and
     * - get current row names
     * - get images
     * - get labels
     * @param {Array} rows
     */
    function formatPageData(rows) {
        var images = [];
        var rowNames = [];
        var rowLables = [];

        var allValues = rows.reduce(function(accumulator, row) {
            rowNames.push(row.rowKey);
            var rowIsSelected = _value.selectedRowsBuffer.indexOf(row.rowKey) > -1;

            var label = _representation.labelColumn
                ? _table.getCell(row.rowKey, _representation.labelColumn)
                : row.rowKey;
            rowLables[row.rowKey] = label;

            // Storing images in an separate array is enough
            if (_representation.svgLabelColumn) {
                images[row.rowKey] = _table.getCell(row.rowKey, _representation.svgLabelColumn);
            }

            // Set values for each cell
            var vals = row.data.reduce(function(rowAcc, value, currentIndex) {
                if (_colNames[currentIndex] === undefined) {
                    return rowAcc;
                }
                var newItem = {};
                newItem.y = row.rowKey;
                newItem.x = _colNames[currentIndex];
                newItem.value = value;
                newItem.initallySelected = rowIsSelected;

                rowAcc.push(newItem);
                return rowAcc;
            }, []);
            return accumulator.concat(vals);
        }, []);

        return {
            images: images,
            data: allValues,
            rowNames: rowNames,
            rowLables: rowLables
        };
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
                .range([_margin.left, _colNames.length * _cellSize + _margin.left])
                .domain(_colNames),
            y: d3
                .scaleBand()
                .domain(formattedDataset.rowNames)
                .range([_margin.top, formattedDataset.rowNames.length * _cellSize + _margin.top]),
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
                return d;
            }),

            y: d3.axisLeft(_scales.y).tickFormat(function(d) {
                return formattedDataset.rowLables[d];
            })
        };
    }

    function formatImage(string) {
        return 'data:image/svg+xml;base64,' + btoa(string);
    }

    function initializeZoom() {
        var svgD3 = d3.select('.knime-svg-container svg');

        var xAxisD3El = svgD3.select('.knime-axis.knime-x');
        var yAxisD3El = svgD3.select('.knime-axis.knime-y');
        var xAxisEl = xAxisD3El.node();
        var yAxisEl = yAxisD3El.node();
        var xAxisWidth = xAxisEl.getBoundingClientRect().width;
        var yAxisHeight = yAxisEl.getBoundingClientRect().height;

        // Set transform origins
        document
            .querySelector('.knime-svg-container .wrapper')
            .setAttribute('transform-origin', _margin.left + 'px ' + _margin.top + 'px ');
        xAxisD3El.attr('transform-origin', _margin.left + 'px 0');
        yAxisD3El.attr('transform-origin', '0 ' + _margin.top + 'px');

        _zoomDimensions = {
            xAxisWidth: xAxisWidth,
            yAxisHeight: yAxisHeight,
            minimalZoomLevel: Math.max(window.innerWidth / xAxisWidth, window.innerHeight / yAxisHeight)
        };
        return setZoomEvents();
    }

    function setZoomEvents() {
        var svgD3 = d3.select('.knime-svg-container svg');
        var xAxisD3El = svgD3.select('.knime-axis.knime-x');
        var yAxisD3El = svgD3.select('.knime-axis.knime-y');
        var infoWrapperHeight = document.querySelector('.info-wrapper').getBoundingClientRect().height || 0;
        var maxZoomLevel = 1;

        var zoom = d3
            .zoom()
            .translateExtent([
                [0, 0],
                [
                    _zoomDimensions.xAxisWidth + (_margin.left + _margin.right) * 2,
                    _zoomDimensions.yAxisHeight + (_margin.top + infoWrapperHeight) * 2
                ]
            ])
            .scaleExtent([_zoomDimensions.minimalZoomLevel, maxZoomLevel])
            .on('zoom', function() {
                var t = d3.event.transform;

                // prevent jumpy layout
                t.x = t.x > 0 ? 0 : t.x;
                t.y = t.y > 0 ? 0 : t.y;
                t.k = t.k > maxZoomLevel ? maxZoomLevel : t.k;

                xAxisD3El.attr('transform', 'translate(' + t.x + ', ' + _margin.top + ') scale(' + t.k + ')');
                yAxisD3El.attr('transform', 'translate(' + _margin.left + ', ' + t.y + ') scale(' + t.k + ')');
                _wrapper.attr('transform', t);

                _value.currentZoomLevel = t;
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

    function showTooltip(e, innerHtml) {
        if (!_value.displayDataCellToolTip && innerHtml) {
            return;
        }
        _tooltip.classList.add('active');
        e.target.classList.add('active');
        _tooltip.innerHTML = innerHtml;
        var tooltipWidth = _tooltip.getBoundingClientRect().width;
        var leftPos = event.clientX + _cellSize;
        var topPos = event.clientY - _cellSize;

        // make sure tooltip is visible on the right
        if (leftPos + tooltipWidth >= (window.innerWidth || document.documentElement.clientWidth)) {
            leftPos = leftPos - tooltipWidth - _cellSize * 2;
        }
        _tooltip.style.left = leftPos + 'px';
        _tooltip.style.top = topPos + 'px';
    }

    function hideTooltip() {
        _tooltip.classList.remove('active');
    }

    function drawCell(cell) {
        _wrapper
            .data([cell])
            .append('g')
            .append('rect')
            .attr('class', 'cell')
            .attr('width', _cellSize)
            .attr('height', _cellSize)
            .attr('y', function(d) {
                return _scales.y(d.y);
            })
            .attr('x', function(d) {
                return _scales.x(d.x);
            })
            .attr('fill', function(d) {
                if (d.value === null) {
                    return _representation.missingValueColor;
                }
                return _scales.colorScale(d.value);
            })
            .attr('selection', function(d) {
                //initialize selection if already selected
                return d.initallySelected ? 'active' : 'inactive';
            });
    }

    /**
     * Sort cells
     * - in viewport
     * - not in viewport
     * @param {Number} viewportSize
     * @param {Array} cell data
     */
    function prioritizeCells(viewportSize, data) {
        var inViewportCells = [];
        var notInViewportCells = [];
        var rowItemsWidth = 0;
        var currentRowKey = data[0].y;

        data.forEach(function(cell) {
            rowItemsWidth = rowItemsWidth + _cellSize * _value.currentZoomLevel.k;
            if (cell.y == currentRowKey) {
                if (rowItemsWidth < viewportSize.width - _value.currentZoomLevel.x) {
                    inViewportCells.push(cell);
                } else {
                    notInViewportCells.push(cell);
                }
            } else {
                rowItemsWidth = 0;
                currentRowKey = cell.y;
                inViewportCells.push(cell);
            }
        });
        return {
            inViewportCells: inViewportCells,
            notInViewportCells: notInViewportCells
        };
    }

    function drawSvg(rows) {
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

        _tooltip = document.querySelector('.knime-tooltip');
        _scales = createScales(formattedDataset);
        _axis = createAxis(formattedDataset);

        var svg = d3
            .select('.knime-svg-container')
            .append('svg')
            .attr('class', 'heatmap');

        // Create titles
        svg.append('text')
            .attr('class', 'knime-title')
            .attr('x', _margin.left)
            .attr('y', 30)
            .text(_value.chartTitle);
        svg.append('text')
            .attr('class', 'knime-subtitle')
            .attr('x', _margin.left)
            .attr('y', 50)
            .text(_value.chartSubtitle);
        updateTitles();

        var defs = svg.append('defs');
        var clipPath = defs
            .append('clipPath')
            .attr('id', 'clip')
            .append('rect')
            .attr('y', _margin.top)
            .attr('x', _margin.left)
            .attr('width', '100%')
            .attr('height', '100%');

        var viewport = svg
            .append('g')
            .attr('class', 'viewport')
            .attr('clip-path', 'url(#clip)');

        _wrapper = viewport.append('g').attr('class', 'wrapper');

        var sortedData;
        if (_representation.runningInView) {
            // Improve performance: render cells progressivley
            var viewportSize = clipPath.node().getBoundingClientRect();
            sortedData = prioritizeCells(viewportSize, formattedDataset.data);
            _drawCellQueue = renderQueue(drawCell).rate(250);
            _drawCellQueue(sortedData.inViewportCells);
            _drawCellQueue.add(sortedData.notInViewportCells);
        } else {
            // Render cells at once for image rendering
            formattedDataset.data.map(function(cell) {
                drawCell(cell);
            });
        }

        // Events for the svg are native js event listeners not
        // d3 event listeners for better performance
        var domWrapper = document.querySelector('.knime-svg-container svg .wrapper');

        // Highlight mouseover cell and show tooltip
        domWrapper.addEventListener('mouseover', function(e) {
            if (!e.target.classList.contains('cell')) {
                return;
            }

            var data = d3.select(e.target).data()[0];

            toolTipInnerHTML =
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
        });

        // Deactivation relies on gaps in the wrapper between the cells
        domWrapper.addEventListener('mouseout', function(e) {
            hideTooltip();
            e.target.classList.remove('active');
        });

        // Row selection
        domWrapper.addEventListener('mousedown', function(e) {
            if (e.target.tagName !== 'rect') {
                return;
            }
            var data = d3.select(e.target).data()[0];
            if (e.shiftKey) {
                return selectDeltaRow(data.y, formattedDataset);
            }
            if (event.ctrlKey || event.metaKey) {
                return selectSingleRow(data.y, true);
            }
            return selectSingleRow(data.y);
        });

        // Append axis
        var maskAxis = defs.append('mask').attr('id', 'maskAxis');
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
            .attr('width', _margin.left + 1)
            .attr('height', _margin.top + 1)
            .attr('fill', 'black');

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
                if (!formattedDataset.images[d]) {
                    return;
                }
                d3.event.target.classList.add('active');
                tooltipInnerHTML = '<img src="' + formatImage(formattedDataset.images[d]) + '" alt/>';
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
            .attr('transform', 'rotate(-65) translate(10 8)');

        // general tick styling
        var ticks = axisWrapper.selectAll('.tick').attr('class', 'knime-tick');
        ticks.select('text').attr('class', 'knime-tick-label');
        ticks.select('line').attr('class', 'knime-tick-line');

        axisWrapper
            .selectAll('.knime-axis.knime-y .knime-tick')
            .attr('class', function(d) {
                if (_value.selectedRowsBuffer.indexOf(d) > -1) {
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
            var extraImageMargin = 50;
            var imageModeMarginTop = formattedDataset.rowNames.length * _cellSize + _margin.top + 15;
            var transform = 'translate(0 ' + imageModeMarginTop + ')';
            legend = svg
                .append('g')
                .attr('class', 'knime-legend')
                .attr('width', _legendWidth + 2 * _legendMargin)
                .attr('height', _legendHeight)
                .attr('transform', transform);

            // Adjust viewbox of svg
            var imageHeight = imageModeMarginTop + _legendHeight + extraImageMargin;
            var imageWidth = _colNames.length * _cellSize + _margin.left + _margin.right + extraImageMargin;
            svg.attr('viewBox', '0 0 ' + imageWidth + ' ' + imageHeight);

            if (_representation.imageHeight) {
                svg.attr('height', _representation.imageHeight + 'px');
            }
            if (_representation.imageWidth) {
                svg.attr('width', _representation.imageWidth + 'px');
            }
        }

        var legendDefs = legend.append('defs');
        var legendGradient = legendDefs.append('linearGradient').attr('id', 'legendGradient');

        var colorDomain = getLinearColorDomain(_representation.minValue, _representation.maxValue);

        // append a single rect to display a gradient
        legend
            .append('rect')
            .attr('y', 0)
            .attr('x', _legendMargin)
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

            for (var i = 0; i < colorDomain.length; i++) {
                var currentPercentage = legendCellPercentage * (i + 1);

                tickValues.push(interpolator(currentPercentage / 100));

                legendGradient
                    .append('stop')
                    .attr('offset', previousPercentage + '%')
                    .attr('style', 'stop-opacity:1; stop-color:' + _scales.colorScale(colorDomain[i]));
                legendGradient
                    .append('stop')
                    .attr('offset', currentPercentage + '%')
                    .attr('style', 'stop-opacity:1; stop-color:' + _scales.colorScale(colorDomain[i]));
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

        legend
            .append('g')
            .attr('transform', 'translate(' + _legendMargin + ', ' + _legendColorRangeHeight + ')')
            .attr('class', 'legend-axis')
            .call(legendAxis)
            .selectAll('text')
            .attr('font-weight', 'normal');

        // Initialize and reset zoom
        resetZoom();

        var maxHeight = _margin.top + formattedDataset.rowNames.length * _cellSize + 20;
        document.querySelector('.knime-layout-container').setAttribute('style', 'max-height: ' + maxHeight + 'px');
    }

    function resetZoom(setToDefault) {
        var zoom = initializeZoom();
        var svg = d3.select('.knime-svg-container svg');
        var resetLevel = setToDefault ? _defaultZoomLevel : _value.currentZoomLevel;
        zoom.transform(svg, function() {
            return d3.zoomIdentity.translate(resetLevel.x, resetLevel.y).scale(resetLevel.k);
        });
        return zoom;
    }

    /**
     * Select multiple rows via shiftkey
     *
     * @param {String} selectedRowId
     * @param {Object} formattedDataset
     */
    function selectDeltaRow(selectedRowId, formattedDataset) {
        // Get closest selected row to newly selected row
        var currentIndex = formattedDataset.rowNames.indexOf(selectedRowId);
        var closestRow = _value.selectedRowsBuffer.reduce(
            function(closestRow, rowId) {
                var rowIdIndex = formattedDataset.rowNames.indexOf(rowId);
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
            rowKey = formattedDataset.rowNames[i];
            if (!_value.selectedRowsBuffer.indexOf(rowKey) > -1) {
                _value.selectedRowsBuffer.push(rowKey);
            }
        }

        var tableId = _table.getTableId();
        styleSelectedRows(_value.selectedRowsBuffer, true);
        knimeService.setSelectedRows(tableId, _value.selectedRowsBuffer);
    }

    function selectSingleRow(selectedRowId, keepCurrentSelections) {
        if (!_value.enableSelection) {
            return;
        }

        // Cast optional parameter to boolean
        keepCurrentSelections = !!keepCurrentSelections;

        var tableId = _table.getTableId();

        if (!keepCurrentSelections) {
            // Remove all selections
            _value.selectedRowsBuffer = [];
            if (_representation.publishSelection) {
                knimeService.setSelectedRows(tableId, []);
            }
            styleSelectedRows();
        }

        if (_value.selectedRowsBuffer.indexOf(selectedRowId) != -1) {
            styleSelectedRows([selectedRowId], false);
            _value.selectedRowsBuffer = _value.selectedRowsBuffer.filter(function(rowId) {
                return rowId !== selectedRowId;
            });
            if (_representation.publishSelection) {
                knimeService.removeRowsFromSelection(tableId, [selectedRowId]);
            }
        } else {
            styleSelectedRows([selectedRowId], true);
            _value.selectedRowsBuffer.push(selectedRowId);
            if (_representation.publishSelection) {
                knimeService.addRowsToSelection(tableId, [selectedRowId]);
            }
        }

        if (_value.showOnlySelectedRows) {
            drawChart();
        }
    }

    function styleSelectedRows(selectedRowIds, select) {
        // If no selectedRowId is given, only reset everything
        if (!selectedRowIds || !selectedRowIds.length) {
            d3.selectAll('.knime-axis.knime-y .knime-tick').attr('class', 'knime-tick');
            d3.selectAll('.cell').attr('selection', 'inactive');
            return;
        }

        // Style row labels
        if (select) {
            selectedRowIds.map(function(selectedRowId) {
                d3.select('.knime-axis.knime-y [data-id="' + selectedRowId + '"]').attr('class', 'knime-tick active');
            });
        } else {
            selectedRowIds.map(function(selectedRowId) {
                d3.select('.knime-axis.knime-y [data-id="' + selectedRowId + '"]').attr('class', 'knime-tick');
            });
        }

        // Style row cells
        d3.selectAll('.wrapper .cell').attr('selection', function(d) {
            if (select && selectedRowIds.indexOf(d.y) > -1) {
                return 'active';
            }
            if (!select && selectedRowIds.indexOf(d.y) > -1) {
                return 'inactive';
            }
            // else return current state
            return d3.select(this).attr('selection');
        });
    }

    return heatmap;
})();
