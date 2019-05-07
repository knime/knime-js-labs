/* eslint-disable max-lines */
/* eslint-disable no-undef */
window.pdpiceplotNamespace = (function () {

    var PDPICEPlot = function () {
        // internal fields
        this._representation = null;
        this._value = null;
        this.svgWidth = 0;
        this.svgHeight = 0;
        this.viewportWidth = 0;
        this.viewportHeight = 0;
        this.xLabelHeight = 0;
        this.xAxisHeight = 0;
        this.yLabelWidth = 0;
        this.yAxisWidth = 0;
        this.titleHeight = 0;

        // d3 element container
        this.d3Elem = {
            svg: null,
            title: null,
            subtitle: null,
            axis: {
                x: null,
                y: null
            },
            yAxis: null,
            yAxisLabel: null,
            yScale: null,
            defs: null,
            viewportClip: null,
            xAxis: null,
            xAxisLabel: null,
            axisMask: null,
            axisWrapper: null,
            xScale: null,
            wrapper: null,
            viewport: null,
            dataContainer: null,
            zoom: null,
            ticks: null,
            pdpLine: null,
            dataArea: null,
            pdpLineTop: null,
            pdpLineBottom: null,
            iceLineTemplate: null,
            staticLine: null
        };

        // defaults
        this.default = {
            xAxisHeight: 35,
            xLabelHeight: 12,
            yLabelWidth: 12,
            yAxisWidth: 30,
            titleHeight: 30,
            subtitleHeight: 20,
            tickLeeway: 15,
            marginLeft: 10,
            marginRight: 30,
            marginTop: 10,
            maxLineWeight: 100,
            minLineWeight: 0,
            lineWeightStep: .1,
            iceClassSelected: 'ice-line line selected',
            iceClassDef: 'ice-line line'
        };

        // internal data collection
        this.pdpLineY = [];
        this.pdpLineX = [];
        this.pdpMarginTop = [];
        this.pdpMarginBottom = [];
        this.iceRows = [];
        this.tempSelected = [];
        this.dataPoints = [];
        this.mouseLocationData = [];
        this.k = 1;
        this.adjYMax = 0;
        this.adjYMin = 0;
        this._filteredData = null;
        this.onSelectionChange = this.onSelectionChange.bind(this);
        this.onFilterChange = this.onFilterChange.bind(this);
        this.getCorrectData = this.getCorrectData.bind(this);
        this.showOnlySelected = false;

        // view only settings
        this.mouseMode = false;
        this.showExpandedOptions = false;
        this.counter = 0;
    };

    /**
     * @param  {JSON} representation
     * @param  {JSON} value
     * @returns {null}
     */
    PDPICEPlot.prototype.init = function (representation, value) {
        if (!representation.dataTable) {
            d3.select('body')
                .append('p')
                .text('Error: No data available');
            return;
        }
        this._representation = representation;
        this._value = value || {};
        // this._value.selected = value.selected || [];
        this._table = new kt();
        this._table.setDataTable(representation.dataTable);
        this.toggleSubscribeToSelection();
        this.toggleSubscribeToFilter();
        this.render(true);
    };


    PDPICEPlot.prototype.getComponentValue = function () {
        return this._value;
    };

    PDPICEPlot.prototype.validate = function () {
        return true;
    };

    /**
     * used to clear calculated data arrays which may have
     * variable lengths between iterations due to changes in
     * selection
     * @return {null}
     */
    PDPICEPlot.prototype.resetInternalData = function () {
        this.pdpLineY = [];
        this.pdpLineX = [];
        this.pdpMarginTop = [];
        this.pdpMarginBottom = [];
        this.iceRows = [];
        this.tempSelected = [];
        this.dataPoints = [];
    };

    /**
     * @return {String} SVG for KNIME
     */
    PDPICEPlot.prototype.getSVG = function () {
        var svgElem = d3.select('.pdpiceplot').node();
        knimeService.inlineSvgStyles(svgElem);
        var svgImg = new XMLSerializer().serializeToString(svgElem);
        return svgImg;
    };

    /**
     * Full reset for window resizing
     * @return {null}
     */
    PDPICEPlot.prototype.reset = function () {
        d3.select('.knime-layout-container').remove();
        this.d3Elem = {
            axis: {}
        };
    };

    /**
     * creates elements upon initialization and in
     * some select re-render scenarios.
     * @param  {boolean} firstCall only upon init()
     * @return {null}
     */
    PDPICEPlot.prototype.render = function (firstCall) {
        this.drawSVG();
        this.drawTitle();
        this.updateScales();

        if (firstCall) {
            var data = this._filteredData
                ? this.getCorrectData(this._filteredData)
                : this.getCorrectData(this.getColoredRows());
            this.createPDPLine(data);
            this.createICELines(data);
            if (this._representation.enableInteractiveCtrls &&
                !document.getElementById('knime-service-menu')
            ) {
                this.drawControls();
            }
            this.createD3Lines(this.d3Elem.xScale, this.d3Elem.yScale);
        }

        this.drawAxis();
        this.drawAxisLabels();
        this.drawICELine();
        this.drawPDPLine();
        this.drawStaticLine();
        this.mountEvents();
    };

    /**
     * Most often method for updating chart.
     * Used with zoom, selection, filters, etc.
     * @return {null}
     */
    PDPICEPlot.prototype.redrawData = function () {
        var data = this._filteredData
            ? this.getCorrectData(this._filteredData)
            : this.getCorrectData(this.getColoredRows());
        this.createPDPLine(data);
        this.createICELines(data);
        this.drawICELine();
        this.drawPDPLine();
        this.drawStaticLine();
    };

    /**
     * Update available data when filter or ViewOnlySelected changes
     * @param {Array} data
     * @return {Array} data
     */
    PDPICEPlot.prototype.getCorrectData = function (data) {
        var self = this;
        if (this.showOnlySelected) {
            return data.filter(function (row) {
                if (self._value.selected) {
                    return self._value.selected.indexOf(row.rowKey) > -1;
                } else {
                    return false;
                }
            });
        }
        return data;
    };

    /**
     * Draws high-level elements upon init()
     * @return {null}
     */
    PDPICEPlot.prototype.drawSVG = function () {
        var self = this;
        var width = self._representation.viewWidth;
        var height = self._representation.viewHeight;

        if (this._representation.runningInView) {
            width = self._representation.resizeToFill ? '100%' : self._representation.viewWidth;
            height = self._representation.resizeToFill ? '100%' : self._representation.viewHeight;
        }

        console.log(width)
        console.log(height)

        d3.select('html')
            .style('width', '100%')
            .style('height', '100%');
        d3.select('body')
            .style('background-color', d3.color(this._value.backgroundColor).toString())
            .style('width', width)
            .style('height', height);
        this.d3Elem.svg = d3.select('body')
            .insert('div')
            .attr('class', 'knime-layout-container')
            .insert('div')
            .style('width', width)
            .style('height', height)
            .attr('class', 'knime-svg-container')
            .attr('id', 'svg-cont')
            .style('width', width)
            .style('height', height)
            .insert('svg:svg')
            .attr('class', 'pdpiceplot')
            .attr('width', width)
            .attr('height', height);

        this.setDimensions();

        this.d3Elem.defs = this.d3Elem.svg.append('defs');
        this.d3Elem.viewportClip = this.d3Elem.defs.append('clipPath')
            .attr('id', 'viewportClip')
            .append('rect')
            .attr('x', this.totalLeftWidth)
            .attr('y', this.totalTopHeight)
            .attr('width', this.viewportWidth)
            .attr('height', this.viewportHeight);
        this.d3Elem.wrapper = this.d3Elem.svg.append('g')
            .attr('class', 'wrapper')
            .attr('x', this.default.marginLeft + this.yAxisWidth)
            .attr('y', this.default.marginTop * 3 + this.titleHeight)
            .attr('width', this.viewportWidth)
            .attr('height', this.viewportHeight)
        // .attr('clip-path', 'url(#viewportClip)');
        this.d3Elem.viewport = this.d3Elem.wrapper.append('g')
            .attr('class', 'viewport')
            .attr('x', this.default.marginLeft + this.yAxisWidth)
            .attr('y', this.default.marginTop * 3 + this.titleHeight)
            .attr('width', this.viewportWidth)
            .attr('height', this.viewportHeight);
        this.d3Elem.dataContainer = this.d3Elem.viewport.append('g')
            .attr('class', 'dataContainer')
            .attr('x', this.default.marginLeft + this.yAxisWidth)
            .attr('y', this.default.marginTop * 3 + this.titleHeight)
            .attr('width', this.viewportWidth)
            .attr('height', this.viewportHeight);
    };

    /**
     * calculates dimensions when drawSVG via render is called
     * @return {null}
     */
    PDPICEPlot.prototype.setDimensions = function () {
        var yLab = this._value.yaxisLabel;
        var xLab = this._value.xaxisLabel;
        var tit = this._value.chartTitle;
        var sub = this._value.chartSubtitle;
        if (yLab && yLab.length) {
            this.yLabelWidth = this.default.yLabelWidth;
        } else {
            this.yLabelWidth = this.default.yLabelWidth;
        }
        if (xLab && xLab.length) {
            this.xLabelHeight = this.default.xLabelHeight;
        } else {
            this.xLabelHeight = 0;
        }
        this.titleHeight = this.default.marginTop;
        if (tit.length) {
            this.titleHeight += this.default.titleHeight;
        }
        if (sub.length) {
            this.titleHeight += this.default.subtitleHeight;
        }
        if (this.d3Elem.yAxis) {
            this.yAxisWidth = Math.max(this.d3Elem.yAxis
                .selectAll('.tick > text')
                .nodes()
                .map(function (e) {
                    return e.getBoundingClientRect().width + this.default.tickLeeway;
                })
                .push(this.default.yAxisWidth));
        } else {
            this.yAxisWidth = this.default.yAxisWidth;
        }
        var svgDim = d3.select('svg').node().getClientRects()[0];
        this.svgWidth = svgDim.width;
        this.svgHeight = svgDim.height;
        this.xAxisHeight = this.default.xAxisHeight;
        this.totalTopHeight = this.titleHeight;
        this.totalLeftWidth = this.yLabelWidth + this.yAxisWidth + this.default.marginLeft - 2;
        this.viewportWidth = this.svgWidth - this.totalLeftWidth - this.default.marginRight;
        this.viewportHeight = this.svgHeight - this.totalTopHeight - this.xAxisHeight - this.xLabelHeight;
    };

    /**
     * updates scales when zoom is reset or on render
     * @return {null}
     */
    PDPICEPlot.prototype.updateScales = function () {

        this.d3Elem.xScale = d3.scaleLinear()
            .domain([this._value.xaxisMin, this._value.xaxisMax])
            .range([this.totalLeftWidth, this.totalLeftWidth + this.viewportWidth]);
        // .clamp(true);
        var domainAdj = this._value.yaxisMax - this._value.yaxisMin;
        this.adjYMin = this._value.yaxisMin - Math.abs(domainAdj * this._value.yaxisMargin);
        this.adjYMax = this._value.yaxisMax + Math.abs(domainAdj * this._value.yaxisMargin);
        this.d3Elem.yScale = d3.scaleLinear()
            .domain([this.adjYMin, this.adjYMax])
            .range([this.viewportHeight + this.totalTopHeight, this.totalTopHeight]);
        // .clamp(true).nice();

    };

    /**
     * creates a colored data-background based on user preferences
     * @return {null}
     */
    PDPICEPlot.prototype.drawDataArea = function () {
        var self = this;
        d3.select('#dataArea')
            .remove();

        this.d3Elem.dataArea = d3.line()
            .x(function (d) {
                return self.d3Elem.xScale(d[0]);
            })
            .y(function (d) {
                return self.d3Elem.yScale(d[1]);
            });

        var data = [
            [this._value.xaxisMin, this.adjYMin],
            [this._value.xaxisMin, this.adjYMax],
            [this._value.xaxisMax, this.adjYMax],
            [this._value.xaxisMax, this.adjYMin]
        ];

        this.d3Elem.viewport
            .append('path')
            .data([data])
            .attr('d', this.d3Elem.dataArea)
            .attr('id', 'dataArea')
            .attr('fill', d3.color(this._value.dataAreaColor).toString());

    };

    /**
     * draws and re-draws titles
     * @return {null}
     */
    PDPICEPlot.prototype.drawTitle = function () {

        if (!this.d3Elem.title) {
            this.d3Elem.title = this.d3Elem.svg
                .append('text')
                .attr('id', 'title')
                .attr('class', 'knime-title')
                .attr('x', this.default.marginLeft)
                .attr('y', this.default.titleHeight);
        }
        this.d3Elem.title.text(this._value.chartTitle || '');

        if (!this.d3Elem.subtitle) {
            this.d3Elem.subtitle = this.d3Elem.svg
                .append('text')
                .attr('id', 'subtitle')
                .attr('class', 'knime-subtitle')
                .attr('x', this.default.marginLeft)
                .attr('y', this.titleHeight);
        }
        this.d3Elem.subtitle.text(this._value.chartSubtitle || '');
    };

    /**
     * creates axis mask first time drawAxis is called
     * @return {null}
     */
    PDPICEPlot.prototype.drawAxisMask = function () {

        this.d3Elem.axisMask = this.d3Elem.svg
            .select('defs')
            .append('mask')
            .attr('id', 'maskAxis')
            .attr('width', this.viewportWidth + this.totalLeftWidth + 3)
            .attr('height', this.svgHeight)
            .attr('maskUnits', 'userSpaceOnUse')
            .append('rect')
            .attr('x', 0)
            .attr('y', 0)
            .attr('width', this.viewportWidth + this.totalLeftWidth + 3)
            .attr('height', this.svgHeight)
            .attr('fill', 'white');
        this.d3Elem.axisWrapper = this.d3Elem.svg
            .append('g')
            .attr('class', 'axis-wrapper')
            .attr('width', this.viewportWidth + this.totalLeftWidth + 3)
            .attr('height', this.svgHeight)
        // .attr('mask', 'url(#maskAxis)');

    };

    /**
     * updates grid, dynamically calculates # of ticks,
     * and draws axis. Also draws data area and axis mask on init
     * @return {null}
     */
    PDPICEPlot.prototype.drawAxis = function () {

        if (!this.d3Elem.dataArea) {
            this.drawDataArea();
        }

        if (!this.d3Elem.axisMask) {
            this.drawAxisMask();
        }

        var tickHeight = this._value.showGrid ? this.viewportHeight : 6;
        var tickWidth = this._value.showGrid ? this.viewportWidth : 0;
        var numXTicks = Math.min(Math.floor(this.d3Elem.svg.node().clientWidth / 50), 20);
        var numYTicks = Math.min(Math.floor(this.d3Elem.svg.node().clientHeight / 50), 20);
        this.d3Elem.axis.x = d3.axisBottom(this.d3Elem.xScale)
            .ticks(numXTicks)
            .tickFormat(d3.format('.2f'))
            .tickSize(-tickHeight);
        this.d3Elem.axis.y = d3.axisLeft(this.d3Elem.yScale)
            .ticks(numYTicks)
            .tickFormat(d3.format('.2f'))
            .tickSize(-tickWidth);

        this.drawXAxis(this.d3Elem.axis.x);
        this.drawYAxis(this.d3Elem.axis.y);
    };

    /**
     * draws updated axis
     * @param  {d3.axis} axis
     * @return {null}
     */
    PDPICEPlot.prototype.drawXAxis = function (axis) {
        d3.selectAll('.knime-x').remove();

        this.d3Elem.xAxis = this.d3Elem.axisWrapper
            .append('g')
            .attr('class', 'knime-axis knime-x')
            .attr('x', this.default.marginLeft)
            .attr('y', '0')
            .call(axis)
            .attr('stroke-width', .2)
            .attr('stroke-opacity', .4)
            .attr('transform', 'translate( 0 ' + (this.viewportHeight + this.totalTopHeight) + ')')
            .selectAll('text')
            .attr('class', 'x')
            .attr('font-weight', 'normal')
            .style('text-anchor', 'middle');

        this.d3Elem.ticks = d3
            .select('.knime-x')
            .selectAll('.tick')
            .attr('class', 'knime-tick x-tick x')
            .select('text')
            .attr('class', 'knime-label knime-tick-label data-iframe-height');
    };

    /**
     * draws updated axis
     * @param  {d3.axis} axis
     * @return {null}
     */
    PDPICEPlot.prototype.drawYAxis = function (axis) {
        d3.selectAll('.knime-y').remove();

        this.d3Elem.yAxis = this.d3Elem.axisWrapper
            .append('g')
            .attr('class', 'knime-axis knime-y')
            .call(axis)
            .attr('stroke-width', .2)
            .attr('stroke-opacity', .4)
            .attr('transform', 'translate(' + this.totalLeftWidth + ' 0)')
            .selectAll('text')
            .attr('class', 'y')
            .attr('font-weight', 'normal');

        this.d3Elem.ticks = this.d3Elem.axisWrapper
            .selectAll('tick')
            .attr('class', 'knime-tick')
            .select('text')
            .attr('class', 'knime-label knime-tick-label')
            .select('line').attr('class', 'knime-tick-line');

        this.d3Elem.ticks = d3
            .select('.knime-y')
            .selectAll('.tick')
            .attr('class', 'knime-tick y-tick y')
            .select('text')
            .attr('class', 'knime-label knime-tick-label');
    };

    /**
     * draws and re-draws axis labels
     * @return {null}
     */
    PDPICEPlot.prototype.drawAxisLabels = function () {
        var xLab = this._value.xaxisLabel;
        if (xLab && xLab.length) {
            if (!this.d3Elem.xAxisLabel) {
                this.d3Elem.xAxisLabel = this.d3Elem.svg.append('text')
                    .attr('class', 'knime-axis-label')
                    .attr('x', this.svgWidth / 2)
                    .attr('y', this.svgHeight - 8)
                    .attr('text-anchor', 'middle')
                    .attr('font-weight', 'bold');
            }
            this.d3Elem.xAxisLabel.text(xLab);
        } else {
            // eslint-disable-next-line no-lonely-if
            if (this.d3Elem.xAxisLabel) {
                this.d3Elem.xAxisLabel.remove();
                this.d3Elem.xAxisLabel = null;
            }
        }
        var yLab = this._value.yaxisLabel;
        if (yLab && yLab.length) {
            if (!this.d3Elem.yAxisLabel) {
                this.d3Elem.yAxisLabel = this.d3Elem.svg
                    .append('text')
                    .attr('class', 'knime-axis-label')
                    .attr('x', this.marginLeft)
                    .attr('y', 0)
                    .attr('text-anchor', 'middle')
                    .attr('font-weight', 'bold')
                    .attr('transform', 'matrix(0,-1,1,0,12,' +
                        this.svgHeight / 2 + ')');
            }
            this.d3Elem.yAxisLabel.text(yLab);
        } else {
            // eslint-disable-next-line no-lonely-if
            if (this.d3Elem.yAxisLabel) {
                this.d3Elem.yAxisLabel.remove();
                this.d3Elem.yAxisLabel = null;
            }
        }
    };

    /**
     * updates the d3 elements functional svg output to match new scale
     * @param  {d3.scale} xScale
     * @param  {d3.scale} yScale
     * @param  {double} k
     * @return {null}
     */
    PDPICEPlot.prototype.createD3Lines = function (xScale, yScale, k) {
        var self = this;
        if (yScale === null) {
            yScale = this.d3Elem.yScale;
        }
        this.d3Elem.pdpLine = d3.line()
            .x(function (d) {
                return xScale(d[0]);
            })
            .y(function (d) {
                return yScale(d[1]);
            });
        // .curve(d3.curveCatmullRom.alpha(0.0));

        this.d3Elem.pdpLineTop = d3.area()
            .x(function (d) {
                return xScale(d[0]);
            })
            .y1(function (d) {
                return yScale(d[1]);
            })
            .y0(function (d) {
                return yScale(d[2]);
            });
        // .curve(d3.curveCatmullRom.alpha(0.0));

        this.d3Elem.pdpLineBottom = d3.area()
            .x(function (d) {
                return xScale(d[0]);
            })
            .y1(function (d) {
                return yScale(d[2]);
            })
            .y0(function (d) {
                return yScale(d[1]);
            });
        // .curve(d3.curveCatmullRom.alpha(0.0));

        this.d3Elem.iceLineTemplate = d3.line()
            .x(function (d) {
                return xScale(d[0]);
            })
            .y(function (d) {
                return yScale(d[1]);
            });
        // .curve(d3.curveCatmullRom.alpha(0.0));

        this.d3Elem.staticLine = d3.line()
            .x(function (d) {
                return xScale(d);
            })
            .y(function (d) {
                return yScale(self._value.staticLineYValue);
            });

        this.d3Elem.cx = function (d) {
            return xScale(d.data[0]);
        };

        this.d3Elem.cy = function (d) {
            return yScale(d.data[1]);
        };

        this.d3Elem.r = function (radius) {
            if (k) {
                return k * radius;
            } else {
                return radius;
            }
        };
    };

    /**
     * takes in the current array of rows and calculates PDP values
     * @param  {Array} data
     * @return {null}
     */
    PDPICEPlot.prototype.createPDPLine = function (data) {
        this.resetInternalData();
        var selfValue = this._value;
        var pdpXVals = this.pdpLineX;
        var pdpYVals = this.pdpLineY;
        var pdpMarginTopVals = this.pdpMarginTop;
        var pdpMarginBottomVals = this.pdpMarginBottom;
        var sumDataVals = [];
        var numDataPoints = [];
        var calcMarginValues = [];

        var needsReset = true;
        data.forEach(function (row) {
            if (needsReset) {
                row.data[1].forEach(function (pointCoord, ind) {
                    pdpXVals[ind] = 0;
                    pdpYVals[ind] = 0;
                    pdpMarginTopVals[ind] = 0;
                    pdpMarginBottomVals[ind] = 0;
                    sumDataVals[ind] = 0;
                    numDataPoints[ind] = 0;
                    calcMarginValues[ind] = 0;
                });
            }
            row.data[1].forEach(function (pointCoord, ind) {
                if (pdpXVals[ind] !== pointCoord[0]) {
                    pdpXVals[ind] = pointCoord[0];
                }
                sumDataVals[ind] += pointCoord[1];
                numDataPoints[ind]++;
            });
            needsReset = false;
        });

        var rowIndex = 0;
        sumDataVals.forEach(function (sumVal, ind) {
            var mean = sumVal / numDataPoints[ind];
            pdpYVals[ind] = mean;
            if (selfValue.showPDPMargin) {
                var sum = 0;
                data.forEach(function (row) {
                    sum += Math.pow(row.data[1][rowIndex][1] - mean, 2);
                });
                // eslint-disable-next-line no-warning-comments
                // square root is more accurate when computed with ln
                // ALSO- using Bessel's Sample Correction
                var variance = sum / (numDataPoints[ind] - 1);
                if (selfValue.pdpmarginType.toLowerCase() === 'standard deviation') {
                    var stdDev = Math.pow(Math.E, Math.log(variance) / 2);
                    calcMarginValues[ind] = stdDev;
                    pdpMarginTopVals[ind] = pdpYVals[ind] + stdDev * selfValue.pdpmarginMultiplier;
                    pdpMarginBottomVals[ind] = pdpYVals[ind] - stdDev * selfValue.pdpmarginMultiplier;
                } else if (selfValue.pdpmarginType.toLowerCase() === 'variance') {
                    calcMarginValues[ind] = variance;
                    pdpMarginTopVals[ind] = pdpYVals[ind] + variance * selfValue.pdpmarginMultiplier;
                    pdpMarginBottomVals[ind] = pdpYVals[ind] - variance * selfValue.pdpmarginMultiplier;
                }
            }
            rowIndex++;
        });
    };

    /**
     * takes in the current array of rows and calculates ICE values
     * @param  {Array} data
     * @return {null}
     */
    PDPICEPlot.prototype.createICELines = function (data) {
        var iceRows = this.iceRows;
        data.forEach(function (row, ind) {
            var rowData = {
                rowKey: row.rowKey,
                rowXVal: [],
                rowYVal: [],
                color: row.color ? d3.color(row.color).toString() : d3.color(self._value.icecolor).toString(),
                featureVal: row.data[0]
            };
            row.data[1].forEach(function (pointCoord, rowInd) {
                rowData.rowXVal[rowInd] = pointCoord[0];
                rowData.rowYVal[rowInd] = pointCoord[1];
            });
            iceRows[ind] = rowData;
        });
    };

    /**
     * draws ICE lines based on most recently calculated data
     * also produces the new data for points, regardless of whether
     * ice lines drawn
     * @return {null}
     */
    PDPICEPlot.prototype.drawICELine = function () {
        var self = this;
        this.dataPoints = [];
        var dataPoints = this.dataPoints;
        d3.selectAll('.ice-line').remove();
        this.iceRows.forEach(function (row, rowInd) {
            var data = [];
            var prevXVal = 0;
            var prevYVal = 0;
            var actualXVal = 0;
            var actualYVal = 0;
            row.rowXVal.forEach(function (xVal, xInd) {
                var yVal = row.rowYVal[xInd];
                var featVal = parseFloat(row.featureVal);
                data[xInd] = [xVal, yVal];
                // eslint-disable-next-line no-warning-comments
                // Currently using closest neighbor for points
                if (prevXVal < featVal && featVal <= xVal) {
                    actualXVal = featVal;
                    if (row.featureVal === xVal) {
                        actualYVal = yVal;
                    } else {
                        // average between last two points seems to be inaccurate
                        // actualYVal = (prevYVal + yVal) / 2;
                        // eslint-disable-next-line no-lonely-if
                        if (row.featureVal - prevXVal > row.featureVal - xVal) {
                            actualYVal = yVal;
                        } else {
                            actualYVal = prevYVal;
                        }
                    }
                }
                prevXVal = xVal;
                prevYVal = yVal;
            });

            var selectedClassValue = self.default.iceClassDef;
            if (self._value.selected && self._value.selected.indexOf(row.rowKey) > -1) {
                selectedClassValue = self.default.iceClassSelected;
            }
            var dataPoint = {
                data: [actualXVal, actualYVal],
                color: row.color
            };
            dataPoints.push(dataPoint);
            if (self._value.showICE) {
                self.d3Elem.viewport
                    .append('path')
                    .data([data])
                    .attr('class', selectedClassValue)
                    .attr('id', row.rowKey)
                    .attr('d', self.d3Elem.iceLineTemplate)
                    .attr('fill', 'none')
                    .attr('stroke', row.color)
                    .attr('alt-stroke', row.color)
                    .attr('stroke-opacity', self._value.icealphaVal)
                    .attr('stroke-width', self._value.iceweight)
                    .on('mouseover', function (e) {
                        if (!this.getAttribute('class').includes('selected')) {
                            var altColor = this.getAttribute('alt-stroke');
                            this.setAttribute('stroke', d3.color(altColor).brighter());
                        }
                    })
                    .on('mouseleave', function (e) {
                        if (!this.getAttribute('class').includes('selected')) {
                            var altColor = this.getAttribute('alt-stroke');
                            this.setAttribute('stroke', altColor);
                        }
                    })
                    .on('click', function (e) {
                        if (window.event.shiftKey || self.mouseMode) {
                            if (!self._value.selected) {
                                self._value.selected = [];
                            }
                            var rowKey = this.getAttribute('id');
                            var index = self._value.selected.indexOf(rowKey);
                            if (index > -1) {
                                self._value.selected.splice(index, 1);
                                if (self._value.publishSelection) {
                                    knimeService.removeRowsFromSelection(
                                        self._table.getTableId(),
                                        [rowKey]
                                    );
                                }
                            } else {
                                self._value.selected.push(rowKey);
                                if (self._value.publishSelection) {
                                    knimeService.addRowsToSelection(
                                        self._table.getTableId(),
                                        [rowKey]
                                    );
                                }
                            }
                        } else {
                            self._value.selected = [this.getAttribute('id')];
                            knimeService.setSelectedRows(
                                self._table.getTableId(),
                                [this.getAttribute('id')]
                            );
                        }
                        if (self.showOnlySelected) {
                            self.redrawData();
                        }
                        self.styleSelected();
                    });
            }
        });

        if (this._value.showDataPoints) {
            this.drawDataPoints();
        }
    };

    /**
     * draws data points based on most recently calculated data
     * @return {null}
     */
    PDPICEPlot.prototype.drawDataPoints = function () {

        var self = this;
        var dataPoints = this.dataPoints;

        self.d3Elem.viewport
            .selectAll('.point')
            .remove();

        if (dataPoints.length > 0 && this._value.showDataPoints) {
            self.d3Elem.viewport
                .selectAll('.dataPoints')
                .data(dataPoints)
                .enter()
                .append('circle')
                .attr('fill', function (d) {
                    return d3.color(d.color || this._value.dataPointColor).darker().toString();
                })
                .attr('fill-opacity', this._value.dataPointAlphaVal)
                .attr('stroke', 'none')
                .attr('class', 'point')
                .attr('cx', this.d3Elem.cx)
                .attr('cy', this.d3Elem.cy)
                .attr('r', this.d3Elem.r(this._value.dataPointWeight));
        }
    };

    /**
     * draws PDP lines and margins based on most recently
     * calculated data
     * @return {null}
     */
    PDPICEPlot.prototype.drawPDPLine = function () {

        var self = this;
        var pdpData = [];
        var color = d3.color(this._value.pdpcolor);

        if (this.d3Elem.pdpLine !== null) {
            d3.select('#pdp-line')
                .remove();
        }

        if (this.d3Elem.pdpLineTop !== null) {
            d3.select('#pdp-line-margin-top')
                .remove();
            d3.select('#pdp-line-top')
                .remove();
        }

        if (this.d3Elem.pdpLineBottom !== null) {
            d3.select('#pdp-line-margin-bottom')
                .remove();
            d3.select('#pdp-line-bottom')
                .remove();
        }


        this.pdpLineX.forEach(function (val, ind) {
            pdpData[ind] = [val, self.pdpLineY[ind]];
        });

        if (this._value.showPDP) {
            this.d3Elem.viewport
                .append('path')
                .data([pdpData])
                .attr('class', 'line transformer pdp-line')
                .attr('id', 'pdp-line')
                .attr('d', this.d3Elem.pdpLine)
                .attr('fill', 'none')
                .attr('stroke', d3.color(this._value.pdpcolor).toString())
                .attr('stroke-width', this._value.pdplineWeight);
        }

        if (this._value.showPDPMargin &&
            (!this.showOnlySelected || this._value.selected.length > 1)) {
            var pdpMarginTop = [];
            var pdpMarginBottom = [];

            this.pdpLineX.forEach(function (val, ind) {
                pdpMarginTop[ind] = [val, self.pdpMarginTop[ind], self.pdpLineY[ind]];
                pdpMarginBottom[ind] = [val, self.pdpMarginBottom[ind], self.pdpLineY[ind]];
            });

            this.d3Elem.viewport
                .append('path')
                .data([pdpMarginTop])
                .attr('class', 'line transformer pdp-line')
                .attr('id', 'pdp-line-margin-top')
                .attr('d', this.d3Elem.pdpLine)
                .attr('fill', 'none')
                .attr('stroke', color.toString())
                .attr('stroke-width', this._value.pdplineWeight);

            this.d3Elem.viewport
                .append('path')
                .data([pdpMarginBottom])
                .attr('class', 'line  transformer  pdp-line')
                .attr('id', 'pdp-line-margin-bottom')
                .attr('d', this.d3Elem.pdpLine)
                .attr('fill', 'none')
                .attr('stroke', color.toString())
                .attr('stroke-width', this._value.pdplineWeight);

            this.d3Elem.viewport
                .append('path')
                .data([pdpMarginTop])
                .attr('class', 'line  transformer pdp-margin')
                .attr('id', 'pdp-line-top')
                .attr('d', this.d3Elem.pdpLineTop)
                .attr('fill', color.toString())
                .attr('fill-opacity', this._value.pdpmarginAlphaVal);

            this.d3Elem.viewport
                .append('path')
                .data([pdpMarginBottom])
                .attr('class', 'line transformer pdp-margin')
                .attr('id', 'pdp-line-bottom')
                .attr('d', this.d3Elem.pdpLineBottom)
                .attr('fill', color.toString())
                .attr('fill-opacity', this._value.pdpmarginAlphaVal);
        }
    };

    /**
     * draws static line
     * @return {null}
     */
    PDPICEPlot.prototype.drawStaticLine = function () {

        d3.select('#static-line')
            .remove();
        if (this._value.showStaticLine) {
            var data = [[this._value.xaxisMin, this._value.xaxisMax]];
            this.d3Elem.viewport
                .append('path')
                .data(data)
                .attr('class', 'line')
                .attr('id', 'static-line')
                .attr('d', this.d3Elem.staticLine)
                .attr('stroke-width', this._value.staticLineWeight)
                .attr('stroke', d3.color(this._value.staticLineColor));
        }
    };

    /**
     * one-time creation of KNIME menu, if applicable
     * @return {null}
     */
    // eslint-disable-next-line complexity
    PDPICEPlot.prototype.drawControls = function () {
        var self = this;
        if (!this._representation.enableInteractiveCtrls) {
            return;
        }

        if (this._representation.fullscreenButton) {
            knimeService.allowFullscreen();
        }

        if (this._representation.enableDragZoom ||
            this._representation.enablePanning ||
            this._representation.enableScrollZoom ||
            this._representation.showZoomReset ||
            this._representation.enableSelection) {
            knimeService.addNavSpacer();
        }

        if (this._representation.enableSelection && this._representation.enableSelectionControls) {

            var selectionButtonClicked = function () {
                var button = document.getElementById('pdpice-selection-mode');
                button.classList.toggle('active');
                self.mouseMode = button.getAttribute('class').includes('active');
            };

            knimeService.addButton(
                'pdpice-selection-mode',
                'check-square-o',
                'Mouse Mode "Select"',
                selectionButtonClicked
            );

            if (this._representation.enableSelection && !this._representation.enablePanning) {
                selectionButtonClicked();
            }

            knimeService.addButton(
                'pdpice-clear-selection-button',
                'minus-square',
                'Clear Selection',
                function () {
                    self._value.selected = [];
                    if (self._value.publishSelection) {
                        knimeService.setSelectedRows(self._representation.dataTable.id, self._value.selected);
                    }
                    self.redrawData();
                }
            );

            // eslint-disable-next-line no-warning-comments
            // TODO: IMPLEMENT FILTER CREATION
            // if (this._value.subscribeToFilters && this._representation.enableSelectionFilterControls) {

            //     knimeService.addButton(
            //         'pdpice-create-filter-button',
            //         'plus-square',
            //         'Create Filter from Selected',
            //         function () {
            //             if (self._value.selected.length > 0) {
            //                 self.createFilterFromSelected();
            //             }
            //         }
            //     );
            // }

            knimeService.addNavSpacer();
        }

        if (this._representation.showZoomReset) {
            knimeService.addButton(
                'zoom-reset-button',
                'search-minus',
                'Reset Zoom',
                function () {
                    self.resetZoom(true);
                }
            );
        }

        if (this._representation.enablePanning && this._representation.enableSmartZoomControls) {
            var zoomModeClicked = function () {
                var button = document.getElementById('pdpice-smart-zoom');
                button.classList.toggle('active');
                self._value.smartZoom = button.getAttribute('class').includes('active');
                self.resetZoom();
            };

            knimeService.addButton(
                'pdpice-smart-zoom',
                'lock',
                'Smart Zoom',
                zoomModeClicked
            );
        }

        knimeService.addNavSpacer();

        if (this._representation.enableMouseCrosshairControls) {

            knimeService.addButton(
                'mouse-crosshair-button',
                'crosshairs',
                'Toggle Crosshairs',
                function () {
                    var button = document.getElementById('mouse-crosshair-button');
                    button.classList.toggle('active');
                    self._value.enableMouseCrosshair = button.getAttribute('class').includes('active');
                    self.initMouseCrosshair();
                }
            );

            if (self._value.enableMouseCrosshair) {
                var crossHairButton = document.getElementById('mouse-crosshair-button');
                crossHairButton.setAttribute('class', 'service-button active');
            }
        }

        if (this._representation.enableGridControls) {
            knimeService.addButton(
                'show-grid-button',
                'th',
                'Toggle Grid',
                function () {
                    var button = document.getElementById('show-grid-button');
                    button.classList.toggle('active');
                    self._value.showGrid = button.getAttribute('class').includes('active');
                    self.drawAxis();
                }
            );

            if (self._value.showGrid) {
                var gridButton = document.getElementById('show-grid-button');
                gridButton.setAttribute('class', 'service-button active');
            }
        }

        if (this._value.chartTitle.length &&
            this._value.chartSubtitle.length &&
            this._representation.enableTitleControls) {

            var chartTitleTextField = knimeService.createMenuTextField(
                'chart-title-text',
                this._value.chartTitle,
                function () {
                    if (self._value.chartTitle !== this.value) {
                        self._value.chartTitle = this.value;
                        self.drawTitle();
                    }
                },
                true
            );

            knimeService.addMenuItem(
                'Chart Title:',
                'header',
                chartTitleTextField
            );

            var chartSubtitleTextField = knimeService.createMenuTextField(
                'chart-subtitle-text',
                this._value.chartSubtitle,
                function () {
                    if (self._value.chartSubtitle !== this.value) {
                        self._value.chartSubtitle = this.value;
                        self.drawTitle();
                    }
                },
                true
            );

            knimeService.addMenuItem(
                'Chart Subtitle:',
                'header',
                chartSubtitleTextField,
                null,
                knimeService.SMALL_ICON
            );

            knimeService.addMenuDivider();

        }

        if (this._representation.enableAxisLabelControls) {
            var chartXAxisTextField = knimeService.createMenuTextField(
                'chart-xAxis-title',
                this._value.xaxisLabel,
                function () {
                    if (self._value.xaxisLabel !== this.value) {
                        self._value.xaxisLabel = this.value;
                        self.drawAxisLabels();
                    }
                },
                true
            );

            knimeService.addMenuItem(
                'X-Axis Label:',
                'header',
                chartXAxisTextField
            );

            var chartYAxisTextField = knimeService.createMenuTextField(
                'chart-yAxis-title',
                this._value.yaxisLabel,
                function () {
                    if (self._value.yaxisLabel !== this.value) {
                        self._value.yaxisLabel = this.value;
                        self.drawAxisLabels();
                    }
                },
                true
            );

            knimeService.addMenuItem(
                'Y-Axis Label:',
                'header',
                chartYAxisTextField,
                null,
                knimeService.SMALL_ICON
            );

            knimeService.addMenuDivider();

        }

        if (this._representation.enablePDPControls) {
            var showPDPCheckBox = knimeService.createMenuCheckbox(
                'show-pdp-checkbox',
                this._value.showPDP,
                function () {
                    if (self._value.showPDP !== this.checked) {
                        self._value.showPDP = this.checked;
                        self.drawPDPLine();
                    }
                },
                true
            );

            knimeService.addMenuItem(
                'Show PDP:',
                'line-chart',
                showPDPCheckBox
            );

            if (this.showExpandedOptions) {

                var pdpLineWeightMenuItem = knimeService.createMenuNumberField(
                    'pdp-line-weight-menu-item',
                    this._value.pdplineWeight,
                    this.default.minLineWeight,
                    this.default.maxLineWeight,
                    this.default.lineWeightStep,
                    function () {
                        if (self._value.pdplineWeight !== this.value) {
                            self._value.pdplineWeight = this.value;
                            d3.selectAll('.pdp-line')
                                .attr('stroke-width', self._value.pdplineWeight);
                        }
                    },
                    true
                );

                knimeService.addMenuItem(
                    'PDP Line Weight:',
                    'line-chart',
                    pdpLineWeightMenuItem,
                    null,
                    knimeService.SMALL_ICON
                );

            }

            knimeService.addMenuDivider();
        }

        if (this._representation.enablePDPMarginControls) {

            var showPDPMarginCheckBox = knimeService.createMenuCheckbox(
                'show-pdp-margin-checkbox',
                this._value.showPDPMargin,
                function () {
                    if (self._value.showPDPMargin !== this.checked) {
                        self._value.showPDPMargin = this.checked;
                        self.drawPDPLine();
                    }
                },
                true
            );

            knimeService.addMenuItem(
                'Show PDP Margin:',
                'area-chart',
                showPDPMarginCheckBox
            );

            if (this.showExpandedOptions) {

                var pdpMarginTypeSelection = knimeService.createMenuSelect(
                    'pdp-margin-type-selection-menu-item',
                    0,
                    ['standard deviation', 'variance'],
                    function () {
                        if (self._value.pdpmarginType !== this.value) {
                            self._value.pdpmarginType = this.value;
                            self.redrawData();
                            self.styleSelected();
                        }
                    }
                );

                knimeService.addMenuItem(
                    'Margin Calculation',
                    'calculator',
                    pdpMarginTypeSelection,
                    'null',
                    knimeService.SMALL_ICON
                );

                var pdpMarginOpacityMenuItem = knimeService.createMenuNumberField(
                    'pdp-margin-opacity-menu-item',
                    this._value.pdpmarginAlphaVal,
                    0,
                    1,
                    this.default.lineWeightStep,
                    function () {
                        if (self._value.pdpmarginAlphaVal !== this.value) {
                            self._value.pdpmarginAlphaVal = this.value;
                            d3.selectAll('.pdp-margin')
                                .attr('fill-opacity', this.value);
                        }
                    },
                    true
                );

                knimeService.addMenuItem(
                    'PDP Margin Opacity:',
                    'area-chart',
                    pdpMarginOpacityMenuItem,
                    null,
                    knimeService.SMALL_ICON
                );

                var pdpMarginMultiplierMenuItem = knimeService.createMenuNumberField(
                    'pdp-margin-multiplier-menu-item',
                    this._value.pdpmarginMultiplier,
                    0,
                    10,
                    this.default.lineWeightStep,
                    function () {
                        if (self._value.pdpmarginMultiplier !== this.value) {
                            self._value.pdpmarginMultiplier = this.value;
                            self.redrawData();
                            self.styleSelected();
                        }
                    },
                    true
                );

                knimeService.addMenuItem(
                    'PDP Margin Mult:',
                    'area-chart',
                    pdpMarginMultiplierMenuItem,
                    null,
                    knimeService.SMALL_ICON
                );

            }

            knimeService.addMenuDivider();
        }

        if (this._representation.enableICEControls) {

            var showICECheckBox = knimeService.createMenuCheckbox(
                'show-ice-checkbox',
                this._value.showICE,
                function () {
                    if (self._value.showICE !== this.checked) {
                        self._value.showICE = this.checked;
                        self.drawICELine();
                        self.styleSelected();
                    }
                },
                true
            );

            knimeService.addMenuItem(
                'Show ICE:',
                'snowflake-o',
                showICECheckBox
            );

            if (this.showExpandedOptions) {

                var iceLineWeightMenuItem = knimeService.createMenuNumberField(
                    'ice-line-weight-menu-item',
                    this._value.iceweight,
                    this.default.minLineWeight,
                    this.default.maxLineWeight,
                    this.default.lineWeightStep,
                    function () {
                        if (self._value.iceweight !== this.value) {
                            self._value.iceweight = this.value;
                            d3.selectAll('.ice-line')
                                .attr('stroke-width', self._value.iceweight);
                            self.styleSelected();
                        }
                    },
                    true
                );

                knimeService.addMenuItem(
                    'ICE Line Weight:',
                    'snowflake-o',
                    iceLineWeightMenuItem,
                    null,
                    knimeService.SMALL_ICON
                );

                var iceMarginOpacityMenuItem = knimeService.createMenuNumberField(
                    'ice-margin-opacity-menu-item',
                    this._value.icealphaVal,
                    0,
                    1,
                    this.default.lineWeightStep,
                    function () {
                        if (self._value.icealphaVal !== this.value) {
                            self._value.icealphaVal = this.value;
                            d3.selectAll('.ice-line')
                                .attr('stroke-opacity', self._value.icealphaVal);
                        }
                    },
                    true
                );

                knimeService.addMenuItem(
                    'ICE Opacity:',
                    'snowflake-o',
                    iceMarginOpacityMenuItem,
                    null,
                    knimeService.SMALL_ICON
                );
            }
            knimeService.addMenuDivider();
        }

        if (this._representation.enableDataPointControls) {

            var showDataPointsCheckbox = knimeService.createMenuCheckbox(
                'show-data-points-checkbox',
                this._value.showDataPoints,
                function () {
                    if (self._value.showDataPoints !== this.checked) {
                        self._value.showDataPoints = this.checked;
                        self.drawDataPoints();
                    }
                },
                true
            );

            knimeService.addMenuItem(
                'Show Data Points',
                'dot-circle-o',
                showDataPointsCheckbox
            );

            if (this.showExpandedOptions) {

                var dataPointWeightMenuItem = knimeService.createMenuNumberField(
                    'data-point-weight-menu-item',
                    this._value.dataPointWeight,
                    this.default.minLineWeight,
                    this.default.maxLineWeight,
                    this.default.lineWeightStep,
                    function () {
                        if (self._value.dataPointWeight !== this.value) {
                            self._value.dataPointWeight = this.value;
                            d3.selectAll('.point')
                                .attr('r', self._value.dataPointWeight);
                        }
                    },
                    true
                );

                knimeService.addMenuItem(
                    'Data Point Weight',
                    'dot-circle-o',
                    dataPointWeightMenuItem,
                    null,
                    knimeService.SMALL_ICON
                );

                var dataPointOpacityMenuItem = knimeService.createMenuNumberField(
                    'data-point-opacity-menu-item',
                    this._value.dataPointAlphaVal,
                    0,
                    1,
                    this.default.lineWeightStep,
                    function () {
                        if (self._value.dataPointAlphaVal !== this.value) {
                            self._value.dataPointAlphaVal = this.value;
                            d3.selectAll('.point')
                                .attr('fill-opacity', self._value.dataPointAlphaVal);
                        }
                    },
                    true
                );

                knimeService.addMenuItem(
                    'Data Point Opacity',
                    'dot-circle-o',
                    dataPointOpacityMenuItem,
                    null,
                    knimeService.SMALL_ICON
                );
            }
            knimeService.addMenuDivider();
        }

        if (this._representation.enableStaticLineControls) {
            var showStaticLineCheckBox = knimeService.createMenuCheckbox(
                'show-static-line-checkbox',
                this._value.showStaticLine,
                function () {
                    if (self._value.showStaticLine !== this.checked) {
                        self._value.showStaticLine = this.checked;
                        self.drawStaticLine();
                    }
                },
                true
            );

            knimeService.addMenuItem(
                'Show Static Line:',
                'arrows-h',
                showStaticLineCheckBox
            );

            if (this.showExpandedOptions) {

                var staticLineWeightMenuItem = knimeService.createMenuNumberField(
                    'static-line-weight-menu-item',
                    this._value.staticLineWeight,
                    this.default.minLineWeight,
                    this.default.maxLineWeight,
                    this.default.lineWeightStep,
                    function () {
                        if (self._value.staticLineWeight !== this.value) {
                            self._value.staticLineWeight = this.value;
                            d3.selectAll('#static-line')
                                .attr('stroke-width', self._value.staticLineWeight);
                        }
                    },
                    true
                );

                knimeService.addMenuItem(
                    'Static Line Weight:',
                    'arrows-h',
                    staticLineWeightMenuItem,
                    null,
                    knimeService.SMALL_ICON
                );

            }

            var staticLineYValMenuItem = knimeService.createMenuNumberField(
                'static-line-y-val-menu-item',
                this._value.staticLineYValue,
                this._value.yaxisMin,
                this._value.yaxisMax,
                this.default.lineWeightStep,
                function () {
                    if (self._value.staticLineYValue !== this.value) {
                        self._value.staticLineYValue = this.value;
                        self.drawStaticLine();
                    }
                },
                true
            );

            knimeService.addMenuItem(
                'Static Line Y-Value:',
                'arrows-h',
                staticLineYValMenuItem,
                null,
                knimeService.SMALL_ICON
            );
        }
        knimeService.addMenuDivider();

        if (this._representation.enableYAxisMarginControls) {

            var yAxisMarginMenuItem = knimeService.createMenuNumberField(
                'yAxis-margin-menu-item',
                this._value.yaxisMargin * 100,
                0,
                100,
                5,
                function () {
                    if (self._value.yaxisMargin !== this.value) {
                        self._value.yaxisMargin = this.value / 100;
                        self.resetZoom();
                    }
                },
                true
            );

            knimeService.addMenuItem(
                'Y-Axis +/- (%)',
                'arrows-v',
                yAxisMarginMenuItem,
                null,
                knimeService.SMALL_ICON
            );
        }

        if (this._representation.enableSelectionFilterControls) {
            knimeService.addMenuDivider();

            if (this._representation.enableSelection &&
                this._representation.enableSelectionControls) {

                var showOnlySelectedCheckbox = knimeService.createMenuCheckbox(
                    'show-only-selected-checkbox',
                    this.showOnlySelected,
                    function () {
                        if (self.showOnlySelected !== this.checked) {
                            self.showOnlySelected = this.checked;
                            self.redrawData();
                            self.styleSelected();
                        }
                    },
                    true
                );

                knimeService.addMenuItem(
                    'Show Only Selected',
                    'filter',
                    showOnlySelectedCheckbox,
                    null,
                    knimeService.SMALL_ICON
                );

                knimeService.addMenuDivider();

                var publishSelectionCheckbox = knimeService.createMenuCheckbox(
                    'publish-selection-checkbox',
                    this._value.publishSelection,
                    function () {
                        if (self._value.publishSelection !== this.checked) {
                            self._value.publishSelection = this.checked;
                            if (self._value.publishSelection) {
                                knimeService.setSelectedRows(
                                    self._table.getTableId(),
                                    self._value.selected
                                );
                            }
                        }
                    },
                    true
                );

                knimeService.addMenuItem(
                    'Publish Selection',
                    knimeService.createStackedIcon('check-square-o', 'angle-right', 'faded left sm', 'right bold'),
                    publishSelectionCheckbox,
                    null,
                    knimeService.SMALL_ICON
                );

            }

            var subscribeToSelectionCheckbox = knimeService.createMenuCheckbox(
                'subscribe-to-selection-checkbox',
                this._value.subscribeToSelection,
                function () {
                    if (self._value.subscribeToSelection !== this.checked) {
                        self._value.subscribeToSelection = this.checked;
                        self.toggleSubscribeToSelection();
                    }
                },
                true
            );

            knimeService.addMenuItem(
                'Subscribe to Selection',
                knimeService.createStackedIcon('check-square-o', 'angle-double-right', 'faded right sm', 'left bold'),
                subscribeToSelectionCheckbox,
                null,
                knimeService.SMALL_ICON
            );

            var subscribeToFilterCheckbox = knimeService.createMenuCheckbox(
                'subscribe-to-filter-checkbox',
                this._value.subscribeToFilters,
                function () {
                    if (self._value.subscribeToFilters !== this.checked) {
                        self._value.subscribeToFilters = this.checked;
                        self.toggleSubscribeToFilter();
                    }
                },
                true
            );

            knimeService.addMenuItem(
                'Subscribe to Filter',
                knimeService.createStackedIcon('filter', 'angle-double-right', 'faded right sm', 'left bold'),
                subscribeToFilterCheckbox,
                null,
                knimeService.SMALL_ICON
            );
        }

        if ((this._representation.enableICEControls || this._representation.enablePDPControls ||
            this._representation.enableDataPointControls || this._representation.enablePDPMarginControls) &&
            this._representation.enableAdvancedOptionsControls) {
            knimeService.addMenuDivider();
            var text = this.showExpandedOptions ? 'Show advanced options' : 'Show advanced options';
            var icon = this.showExpandedOptions ? 'minus' : 'plus';

            var extendedOptionsCheckbox = knimeService.createMenuCheckbox(
                'extended-options-checkbox',
                this.showExpandedOptions,
                function () {
                    if (self.showExpandedOptions !== this.checked) {
                        self.showExpandedOptions = this.checked;
                        self.redrawKnimeControls();
                    }
                },
                true
            );

            knimeService.addMenuItem(
                text,
                icon,
                extendedOptionsCheckbox,
                null,
                knimeService.SMALL_ICON
            );
        }
    };

    /**
     * toggles subscription to KNIME selection events
     * @return {null}
     */
    PDPICEPlot.prototype.toggleSubscribeToSelection = function () {
        if (this._value.subscribeToSelection) {
            knimeService.subscribeToSelection(
                this._table.getTableId(),
                this.onSelectionChange
            );
        } else {
            knimeService.unsubscribeSelection(
                this._table.getTableId(),
                this.onSelectionChange
            );
        }
    };

    /**
     * toggles subscription to KNIME filter events
     * @return {null}
     */
    PDPICEPlot.prototype.toggleSubscribeToFilter = function () {
        if (this._value.subscribeToFilters) {
            knimeService.subscribeToFilter(
                this._table.getTableId(),
                this.onFilterChange,
                this._table.getFilterIds()
            );
        } else {
            knimeService.unsubscribeFilter(
                this._table.getTableId(),
                this.onFilterChange
            );
        }
    };

    /**
     * handles changes in KNIME selection events from sister nodes
     * @param {object} data KNIME selection event
     * @return {null}
     */
    PDPICEPlot.prototype.onSelectionChange = function (data) {
        var self = this;
        if (!this._value.selected) {
            this._value.selected = [];
        }
        if (data.reevaluate) {
            this._value.selected = knimeService.getAllRowsForSelection(this._table.getTableId());
        } else if (data.changeSet) {
            if (data.changeSet.added) {
                data.changeSet.added.forEach(function (rowId) {
                    if (self._value.selected.indexOf(rowId) === -1) {
                        self._value.selected.push(rowId);
                    }
                });
            }
            if (data.changeSet.removed) {
                data.changeSet.removed.forEach(function (rowId) {
                    var index = self._value.selected.indexOf(rowId);
                    if (index > -1) {
                        self._value.selected.splice(index, 1);
                    }
                });
            }
        }
        this.redrawData();
        this.styleSelected();
    };

    /**
     * handles changes in KNIME filter events from sister nodes
     * @param {object} data KNIME filter event
     * @return {null}
     */
    PDPICEPlot.prototype.onFilterChange = function (data) {
        var self = this;
        this._filteredData = this.getColoredRows().filter(function (row) {
            return self._table.isRowIncludedInFilter(row.rowKey, data);
        });
        this.redrawData();
        this.styleSelected();
    };

    /**
     * TODO: IMPLEMENT FILTER CREATION
     * attempts to create a filter from currently selected items
     * @todo
     * @return {null}
     */
    PDPICEPlot.prototype.createFilterFromSelected = function () {
        // knimeService.setFilteredRows(
        //     this._table.getTableId(),
        //     this._value.selected
        // );
        // knimeService.subscribeToFilter(this._table.getTableId(), this.onFilterChange);
    };

    /**
     * gets the most updated data and colors it if possible
     * @return {array} data update, colored, filtered rows
     */
    PDPICEPlot.prototype.getColoredRows = function () {
        var self = this;
        var rows = this._table.getRows();
        var colors = this._table.getRowColors();
        var booleanMissingColors = false;
        if (colors.length && rows.length && colors.length === rows.length) {
            rows.forEach(function (row, ind) {
                var color = colors[ind];
                if (color === "#404040") {
                    color = self._value.icecolor;
                    booleanMissingColors = true;
                }
                row.color = color;
            });
        }
        if (booleanMissingColors) {
            knimeService.setWarningMessage("Some color values were " +
                "missing from the data table you provided. Please be aware the displayed colors " +
                "may not accurately represent the color assignments intended.");
        }
        return rows;
    };

    /**
     * mounts window/one time events on init
     * @return {null}
     */
    PDPICEPlot.prototype.mountEvents = function () {
        var self = this;
        var resizeDebouncer;

        this.initZoom();
        this.initMouseCrosshair();
        d3.select('.viewport')
            .call(this.d3Elem.zoom);

        window.addEventListener('resize', function () {
            clearTimeout(resizeDebouncer);
            resizeDebouncer = setTimeout(function () {
                if (self._representation.resizeToFill) {
                    self.reset();
                    self.render(true);
                    self.styleSelected();
                }
            }, 250);
        });
    };

    /**
     * repeatedly called to adjust style for selected
     * lines after re-draw
     * @return {null}
     */
    PDPICEPlot.prototype.styleSelected = function () {
        var self = this;
        var nodeList = d3.selectAll('.selected')._groups[0];
        nodeList.forEach(function (node) {
            d3.select(node)
                .attr('stroke-width', self._value.iceweight)
                .attr('class', self.default.iceClassDef)
                .attr('stroke', node.getAttribute('alt-stroke'));
        });
        if (this._value.selected && this._value.selected.length && this._value.showICE) {
            this._value.selected.forEach(function (rowKey) {
                var lineWeight = self._value.iceweight > .5 ? self._value.iceweight * 1.5 : 1;
                var selectedNode = d3.select('#' + rowKey).node();
                selectedNode.setAttribute('class', self.default.iceClassSelected);
                selectedNode.setAttribute('stroke', d3.color(selectedNode.getAttribute('alt-stroke')).darker(2));
                selectedNode.setAttribute('stroke-width', lineWeight);
            });
        }
    };

    /**
     * resets zoom
     * @return {null}
     */
    PDPICEPlot.prototype.resetZoom = function () {
        this.updateScales();
        this.createD3Lines(this.d3Elem.xScale, this.d3Elem.yScale);
        this.drawAxis();
        this.redrawData();
        this.styleSelected();
    };

    /**
     * used to redraw controls when expanding additional options
     * @return {null}
     */
    PDPICEPlot.prototype.redrawKnimeControls = function () {
        var menuContainer = document.getElementById('knime-service-menu').firstChild;
        while (menuContainer.firstChild) {
            menuContainer.removeChild(menuContainer.firstChild);
        }
        this.drawControls();
    };

    /**
     * creates d3.zoom event to be mounted
     * @return {null}
     */
    PDPICEPlot.prototype.initZoom = function () {
        var self = this;
        this.d3Elem.zoom = d3.zoom().scaleExtent([1, 5])
            .translateExtent([
                [this.totalLeftWidth - this.default.marginRight + 6, this.totalTopHeight],
                [this.totalLeftWidth + this.viewportWidth, this.viewportHeight + this.totalTopHeight + this.default.marginTop * 3 + 6]
            ])
            // eslint-disable-next-line no-empty-function
            .on('start', function () {

            })
            .on('zoom', function () {
                if (self._representation.enablePanning && self._representation.enableScrollZoom) {
                    if (d3.event.sourceEvent && (d3.event.sourceEvent.type === 'wheel' ||
                        d3.event.sourceEvent.type === 'mousemove')) {
                        if (window.event.ctrlKey) {
                            // eslint-disable-next-line no-warning-comments
                            // TODO: additional options
                        } else {
                            var d3El = self.d3Elem;
                            var newScaleX = d3.event.transform.rescaleX(d3El.xScale);
                            var newScaleY = d3.event.transform.rescaleY(d3El.yScale);
                            var newRadiusScale = d3.event.transform.k;
                            if (self._value.smartZoom &&
                                self._representation.enableDragZoom) {
                                self.createD3Lines(newScaleX, null, null);
                                d3.selectAll('.x')
                                    .remove();
                                d3.select('.knime-x').transition()
                                    .duration(50).call(
                                        d3El.axis
                                            .x.scale(newScaleX)
                                    ).attr('stroke-width', .2)
                                    .attr('stroke-opacity', .4);
                            } else {
                                self.createD3Lines(newScaleX, newScaleY, newRadiusScale);
                                d3.selectAll('.x')
                                    .remove();
                                d3.select('.knime-x').transition()
                                    .duration(50).call(
                                        d3El.axis.x.scale(newScaleX)
                                    ).attr('stroke-width', .2);
                                d3.selectAll('.y')
                                    .remove();
                                d3.select('.knime-y').transition()
                                    .duration(50).call(
                                        d3El.axis.y.scale(newScaleY)
                                    ).attr('stroke-width', .2)
                                    .attr('stroke-opacity', .4);
                            }
                            self.redrawData();
                            self.styleSelected();
                        }
                    }
                }
                // eslint-disable-next-line no-empty-function
            }).on('end', function () {

            });
    };

    /**
     * toggles mouse crosshair elements and listeners
     * @returns {null}
     */
    PDPICEPlot.prototype.initMouseCrosshair = function () {

        var self = this;
        var d3El = self.d3Elem;
        var target = d3.select('.viewport');

        d3.selectAll('.ch-el').remove();
        d3.select('.wrapper').on('mouseover mouseout mousemove', null);
        if (!this._value.enableMouseCrosshair) {
            return;
        }
        target.append('line')
            .attr('id', 'crossX')
            .attr('class', 'crosshairLine ch-el')
            .attr('stroke', 'black')
            .attr('stroke-width', .5);
        target.append('line')
            .attr('id', 'crossY')
            .attr('class', 'crosshairLine ch-el')
            .attr('stroke', 'black')
            .attr('stroke-width', .5);
        target.append('circle')
            .attr('id', 'crossTarget')
            .attr('class', 'ch-el')
            .attr('stroke', 'black')
            .attr('stroke-width', 3)
            .attr('r', .1);

        d3.select('.wrapper')
            .on('mouseover', function () {
                d3.selectAll('.ch-el').style('display', '');
            })
            .on('mouseout', function () {
                d3.selectAll('.ch-el').style('display', 'none');
            })
            .on('mousemove', function () {
                var locationData = d3.mouse(this);
                // eslint-disable-next-line no-warning-comments
                // TODO: Tooltip for crosshairs
                // eslint-disable-next-line no-unused-vars
                var displayData = [
                    d3El.xScale.invert(d3.mouse(this)[0]),
                    d3El.yScale.invert(d3.mouse(this)[1])
                ];

                d3.select('#crossTarget')
                    .attr('cx', locationData[0])
                    .attr('cy', locationData[1]);
                d3.select('#crossX')
                    .attr('x1', d3El.xScale.range()[0])
                    .attr('y1', locationData[1])
                    .attr('x2', d3El.xScale.range()[1])
                    .attr('y2', locationData[1]);
                d3.select('#crossY')
                    .attr('x1', locationData[0])
                    .attr('y1', d3El.yScale.range()[0])
                    .attr('x2', locationData[0])
                    .attr('y2', d3El.yScale.range()[1]);
            });
    };

    return new PDPICEPlot();
})();
