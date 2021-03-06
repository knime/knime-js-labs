window.dendrogram_namespace = (function () {
    const dendrogram = {};

    var _representation,
        _value,
        table,
        selectedRows = [],
        filteredRows = [];

    // view related settings
    const xAxisHeight = 100,
        xAxisLabelWidth = 15,
        yAxisWidthDefault = 40,
        linkStrokeWidth = 1,
        clusterMarkerRadius = 4,
        viewportMarginTop = 10,
        leafWidth = 8,
        leafHeight = 20,
        thresholdHandleHeight = 2,
        thresholdFormat = d3.format('.3f'),
        animationDuration = 400;

    // hierarchy related variables
    var cluster,
        nodes,
        leaves,
        links,
        clusterMarker;

    // view related variables
    var svg,
        svgSize,
        viewportEl,
        viewportWidth,
        viewportHeight,
        wrapperEl,
        viewportClipEl,
        xAxisClipEl,
        titleEl,
        subtitleEl,
        dendrogramEl,
        clusterMarkerEl,
        leafClusterMarkerContainerEl,
        leafClusterMarkerEl,
        leafEl,
        linkEl,
        thresholdEl,
        thresholdDisplayEl,
        thresholdClusterDisplayEl,
        xAxis,
        xAxisEl,
        xAxisLabelEl,
        xScale,
        xShowNthTicks,
        xEllipsisNthTick,
        yAxis,
        yAxisEl,
        yAxisLabelEl,
        yAxisWidth,
        yScale,
        zoom,
        numClustersField;

    dendrogram.init = function (representation, value) {
        _representation = representation;
        _value = value;

        if (!_representation.tree || !_representation.tree.root) {
            if (_representation.showWarningsInView) {
                d3.select('body').append('p').text('Error: No data available');
            }
            return;
        }

        table = new kt();
        table.setDataTable(_representation.table);

        createHierarchyFromTree();

        if (_representation.enableViewEdit) {
            drawControls();
        }

        drawSVG();
        drawTitle();
        drawXAxis();
        setYScale();
        drawYAxis();
        drawAxisLabels();
        drawDendrogram();

        if (_representation.showThresholdBar) {
            drawThresholdHandle();
        }

        initZoomingAndPanning();

        resizeDiagram(true);

        if (_representation.enableSelection) {
            toggleShowSelectedOnly();
            initSelection();
            toggleSubscribeSelection();
        }

        toggleSubscribeFilter();

        if (_representation.resizeToWindow) {
            initWindowResize();
        }
    };

    const drawControls = function () {
        if (_representation.displayFullscreenButton) {
            knimeService.allowFullscreen();
        }

        if (_representation.enableTitleEdit) {
            knimeService.addMenuItem('Chart Title:', 'header', knimeService.createMenuTextField(
                'chartTitleText', _value.title, function () {
                    if (_value.title != this.value) {
                        const resize = !!_value.title.length !== !!this.value.length;
                        _value.title = this.value;
                        drawTitle();
                        if (resize) {
                            resizeDiagram();
                        }
                    }
                }, true));

            knimeService.addMenuItem('Chart Subtitle:', 'header', knimeService.createMenuTextField(
                'chartSubtitleText', _value.subtitle, function () {
                    if (_value.subtitle != this.value) {
                        const resize = !!_value.subtitle.length !== !!this.value.length;
                        _value.subtitle = this.value;
                        drawTitle();
                        if (resize) {
                            resizeDiagram();
                        }
                    }
                }, true), null, knimeService.SMALL_ICON);
        }

        if (_representation.enableAxisLabelEdit) {
            knimeService.addMenuDivider();
            knimeService.addMenuItem('X Axis Label:', 'ellipsis-h', knimeService.createMenuTextField(
                'xAxisLabel', _value.xAxisLabel, function () {
                    if (_value.xAxisLabel != this.value) {
                        const resize = !!_value.xAxisLabel.length !== !!this.value.length;
                        _value.xAxisLabel = this.value;
                        drawAxisLabels();
                        if (resize) {
                            resizeDiagram();
                        }
                    }
                }, true));

            knimeService.addMenuItem('Y Axis Label:', 'ellipsis-v', knimeService.createMenuTextField(
                'yAxisLabel', _value.yAxisLabel, function () {
                    if (_value.yAxisLabel != this.value) {
                        const resize = !!_value.yAxisLabel.length !== !!this.value.length;
                        _value.yAxisLabel = this.value;
                        drawAxisLabels();
                        if (resize) {
                            resizeDiagram();
                        }
                    }
                }, true));
        }

        if (_representation.enableZoomAndPanning && _representation.showZoomResetButton) {
            knimeService.addButton('zoom-reset-button', 'search-minus', 'Reset Zoom', function () {
                resetZoom();
            });
        }

        if (_representation.enableSelection && _representation.showClearSelectionButton) {
            knimeService.addButton('selection-reset-button', 'minus-square-o', 'Reset Selection', function () {
                clearSelection();
            });
        }

        if (_representation.enableNumClusterEdit) {
            knimeService.addMenuDivider();
            const minNumClusters = 1;
            const maxNumClusters = clusterMarker.length + 1;
            numClustersField = knimeService.createMenuNumberField('numClusters', _value.numClusters, minNumClusters, maxNumClusters, 1, function () {
                var newValue = parseInt(this.value);
                if (this.value.length) {
                    if (newValue < minNumClusters) {
                        newValue = minNumClusters;
                    } else if (newValue > maxNumClusters) {
                        newValue = maxNumClusters;
                    }

                    setThresholdByNumClusters(newValue);
                }
            }, true);
            knimeService.addMenuItem('Number of clusters', 'sitemap', numClustersField);
        }

        // toggle log scale
        if (_representation.enableLogScaleToggle) {
            knimeService.addMenuDivider();
            knimeService.addMenuItem('Use log scale', 'arrows-v', knimeService.createMenuCheckbox('useLogScale', _value.useLogScale, function () {
                _value.useLogScale = this.checked;
                toggleLogScale();
            }));
        }

        // show selection only
        if (_representation.enableSelection && _representation.showSelectedOnlyToggle) {
            knimeService.addMenuDivider();
            knimeService.addMenuItem('Show selected rows only', 'filter', knimeService.createMenuCheckbox('showSelectedOnlyCheckbox', _value.showSelectedOnly, function () {
                _value.showSelectedOnly = this.checked;
                toggleShowSelectedOnly();
            }));
        }

        // Selection / Filter configuration
        knimeService.addMenuDivider();
        if (_representation.enableSelection) {
            knimeService.addMenuItem('Publish selection',
                knimeService.createStackedIcon('check-square-o', 'angle-right', 'faded left sm', 'right bold'),
                knimeService.createMenuCheckbox('publishSelectionCheckbox', _value.publishSelectionEvents, function () {
                    _value.publishSelectionEvents = this.checked;
                }));

            knimeService.addMenuItem('Subscribe to selection',
                knimeService.createStackedIcon('check-square-o', 'angle-double-right', 'faded right sm', 'left bold'),
                knimeService.createMenuCheckbox('subscribeSelectionCheckbox', _value.subscribeSelectionEvents, function () {
                    _value.subscribeSelectionEvents = this.checked;
                    toggleSubscribeSelection();
                }));
        }

        knimeService.addMenuItem('Subscribe to filter',
            knimeService.createStackedIcon('filter', 'angle-double-right', 'faded right sm', 'left bold'),
            knimeService.createMenuCheckbox('subscribeFilterCheckbox', _value.subscribeFilterEvents, function () {
                _value.subscribeFilterEvents = this.checked;
                toggleSubscribeFilter();
            }));
    };

    const calcSVGSize = function () {
        svgSize = d3.select('svg').node().getClientRects()[0];
        yAxisWidth = getYAxisWidth();
        viewportWidth = svgSize.width - yAxisWidth - getYAxisLabelWidth();
        viewportHeight = svgSize.height - xAxisHeight - getTitleHeight() - getXAxisLabelHeight();
    };

    const drawSVG = function () {
        d3.select('html').style('width', '100%').style('height', '100%');
        d3.select('body').style('width', '100%').style('height', '100%');

        const resizeToWindow = _representation.runningInView && _representation.resizeToWindow;

        const svgContainer = d3.select('body')
            .insert('div').attr('class', 'knime-layout-container')
            .insert('div').attr('class', 'knime-svg-container').style('width', '100%').style('height', '100%');

        // create SVG
        svg = svgContainer.insert('svg:svg')
            .attr('width', resizeToWindow ? '100%' : _representation.imageWidth)
            .attr('height', resizeToWindow ? '100%' : _representation.imageHeight);

        if (_representation.runningInView) {
            // tag element for iframe resizer
            svg.attr('data-iframe-width', '').attr('data-iframe-height', '');
        }

        calcSVGSize();

        // create clipping path for viewport (needed for zooming & panning)
        const defs = svg.append('defs');
        viewportClipEl = defs.append('clipPath')
            .attr('id', 'viewportClip')
            .append('rect');

        xAxisClipEl = defs.append('clipPath')
            .attr('id', 'xAxisClip')
            .append('rect')
            .attr('height', xAxisHeight);

        wrapperEl = svg.append('g').attr('class', 'wrapper');

        viewportEl = wrapperEl.append('g').attr('class', 'viewport');
        dendrogramEl = viewportEl.append('g').attr('transform', 'translate(0,' + viewportMarginTop + ')')
            .append('g');

    };

    const createHierarchyFromTree = function () {
        // load data into d3 hierarchy representation
        nodes = d3.hierarchy(_representation.tree.root);
        cluster = d3.cluster().separation(function () {
            return 1;
        });
        links = nodes.links();
        leaves = nodes.leaves();
        clusterMarker = nodes.descendants().filter(function (n) {
            return n.children != null;
        });
    };

    const drawAxisLabels = function () {
        if (_value.xAxisLabel && _value.xAxisLabel.length) {
            if (!xAxisLabelEl) {
                xAxisLabelEl = svg.append('text')
                    .attr('class', 'knime-axis-label')
                    .attr('x', svgSize.width / 2)
                    .attr('y', svgSize.height - 8)
                    .attr('text-anchor', 'middle');

            }
            xAxisLabelEl.text(_value.xAxisLabel);
        } else {
            if (xAxisLabelEl) {
                xAxisLabelEl.remove();
                xAxisLabelEl = null;
            }
        }

        if (_value.yAxisLabel && _value.yAxisLabel.length) {
            if (!yAxisLabelEl) {
                yAxisLabelEl = svg.append('text')
                    .attr('class', 'knime-axis-label')
                    .attr('x', 0)
                    .attr('y', 0)
                    .attr('text-anchor', 'middle')
                    .attr('transform', 'matrix(0,-1,1,0,12,' + (svgSize.height / 2) + ')');
            }
            yAxisLabelEl.text(_value.yAxisLabel);
        } else {
            if (yAxisLabelEl) {
                yAxisLabelEl.remove();
                yAxisLabelEl = null;
            }
        }
    };

    const getXAxisLabelHeight = function () {
        var height = 0;

        if (_value.xAxisLabel && _value.xAxisLabel.length) {
            height = 12;
        }

        return height;
    };

    const getYAxisLabelWidth = function () {
        var width = 0;

        if (_value.yAxisLabel && _value.yAxisLabel.length) {
            width = 12;
        }

        return width;
    };

    const drawXAxis = function () {
        const labels = leaves.map(function (n) { return n.data.rowKey; });
        xScale = d3.scaleBand()
            .domain(labels);
        xAxis = d3.axisBottom(xScale);

        // meassure and truncate long labels
        const measured = knimeService.measureAndTruncate(labels, {
            classes: 'tick knime-tick',
            tempContainerClasses: 'knime-tick-label',
            container: svg.node(),
            maxWidth: xAxisHeight - 40
        });

        xAxis.tickFormat(function (d, i) {
            // show ellipsis if not all labels are shown
            const showEllipsis = !!(i % xEllipsisNthTick);

            // add knime, selected, filtered and ellipsis classes
            // TODO is there any way to only add these classes when the d3 tick element is created? tickFormat() is called quite often...
            const tickEl = d3.select(this.parentNode)
                .classed('knime-tick', true)
                .classed('selected', function (rowKey) {
                    return selectedRows.indexOf(rowKey) !== -1;
                }).classed('outOfFilter', function (rowKey) {
                    return !!filteredRows.length && !(filteredRows.indexOf(rowKey) !== -1);
                }).classed('ellipsis', showEllipsis);
            tickEl.select('line').classed('knime-tick-line', true);

            // TODO why can't we set this via CSS? works in browsers but not in KNIME SVG output
            if (showEllipsis) {
                d3.select(this).classed('knime-tick-label', true).attr('transform', 'translate(-16,9) rotate(-90)');
            } else {
                d3.select(this).classed('knime-tick-label', true).attr('transform', 'translate(-13,10) rotate(-90)');
            }

            if (showEllipsis) {
                return '…';
            } else {
                const index = labels.indexOf(d);
                const label = measured.values[index].truncated;

                var titleEl = tickEl.select('title');
                if (titleEl.empty()) {
                    titleEl = tickEl.append('title');
                }
                titleEl.text(d);

                return label ? label : d;
            }
        });
        xAxisEl = wrapperEl.append('g').attr('class', 'knime-axis knime-x');
    };

    const updateXAxis = function () {
        // prevent overlapping labels by removing some if there is not enough space to show all
        const transform = d3.zoomTransform(svg.node());
        const scale = transform.k;
        xShowNthTicks = Math.round(xScale.domain().length / (viewportWidth * scale / (xAxisLabelWidth + 2)));
        xEllipsisNthTick = xShowNthTicks <= 1 ? 0 : 2;
        xAxis.tickValues(xScale.domain().filter(function (d, i) { return !(i % xShowNthTicks); }));
        xScale.range([0, viewportWidth].map(function (d) { return transform.applyX(d); }));
        xAxisEl.call(xAxis);
    };

    const setYScale = function () {
        var useLogScale = _value.useLogScale;
        const minDistance = clusterMarker[clusterMarker.length - 1].data.distance;
        const maxDistance = nodes.data.distance;

        if (useLogScale && minDistance === 0) {
            knimeService.setWarningMessage('Minimal distance is 0, therefore log scaling is not possible. Switched to linear scaling of Y axis.');
            useLogScale = false;
        }

        if (useLogScale) {
            // IE11 doesn't support Math.log10...
            const log10 = Math.log10 ? Math.log10 : function (value) { return Math.log(value) / Math.log(10); };
            const lowerPowerOfTen = function (value) {
                //calculate puffers depending on maxDistance: next lower power of ten by one order of magnitude.
                return Math.pow(10, Math.round(log10(value)) - 1);
            };
            yScale = d3.scaleLog()
                .clamp(true)
                .domain([minDistance - lowerPowerOfTen(minDistance), maxDistance + lowerPowerOfTen(maxDistance)]);
        } else {
            yScale = d3.scaleLinear()
                .domain([0, maxDistance])
                .nice();
        }
    };

    const drawYAxis = function () {
        yAxis = d3.axisLeft(yScale)
            .ticks(5);
        yAxisEl = wrapperEl.append('g')
            .attr('class', 'knime-axis knime-y');
    };

    const updateYAxis = function (transformEvent, animate) {
        yScale.range([viewportHeight, 0]);
        if (transformEvent) {
            yAxis.scale(transformEvent.rescaleY(yScale));
        }

        if (animate) {
            yAxisEl.transition()
                .duration(400)
                .call(yAxis.scale(yScale));
        } else {
            yAxisEl.call(yAxis);
        }

        // apply knime classes
        // TODO is there any way to only add these classes when the d3 tick element is created? updateYAxis() is called quite often...
        const yTickEls = yAxisEl.selectAll('.tick').classed('knime-tick', true);
        yTickEls.selectAll('line').classed('knime-tick-line', true);
        yTickEls.selectAll('text').classed('knime-tick-label', true);
    };

    const getYAxisWidth = function () {
        if (!yAxisEl) {
            return yAxisWidthDefault;
        }

        const yAxisTickWidths = yAxisEl.selectAll('.tick > text').nodes().map(function (t) {
            return t.getBoundingClientRect().width + 15;
        });
        yAxisTickWidths.push(yAxisWidthDefault);

        return Math.max.apply(null, yAxisTickWidths);
    };

    const drawDendrogram = function () {
        // draw links
        linkEl = dendrogramEl.selectAll('.link').data(links).enter().append('path').attr('class', 'link')
            .attr('stroke-width', linkStrokeWidth);

        // draw leaves
        leafEl = dendrogramEl.selectAll('.leaf').data(leaves).enter().append('rect').attr('class', 'leaf')
            .attr('x', -leafWidth / 2)
            .attr('y', -leafHeight).attr('width', leafWidth)
            .attr('height', leafHeight)
            .attr('fill', function (d) {
                return d.data.color;
            })
            .attr('stroke-width', linkStrokeWidth);

        // draw cluster markers
        clusterMarkerEl = dendrogramEl.selectAll('.cluster').data(clusterMarker).enter().append('circle').attr('class', 'cluster').attr('r', clusterMarkerRadius);

        // cluster marker title; content will be filled in onThresholdChange()
        clusterMarkerEl.append('title');

        leafClusterMarkerContainerEl = dendrogramEl.append('g');
    };

    const getTitleHeight = function () {
        var height = 0;

        if (_value.subtitle.length) {
            height = 50;
        } else if (_value.title.length) {
            height = 30;
        }

        return height;
    };

    const drawTitle = function () {
        if (_value.title.length) {
            if (!titleEl) {
                titleEl = svg.append('text')
                    .attr('id', 'title')
                    .attr('class', 'knime-title')
                    .attr('x', 2)
                    .attr('y', 25);
            }
            titleEl.text(_value.title);
        } else {
            if (titleEl) {
                titleEl.remove();
                titleEl = null;
            }
        }

        if (_value.subtitle.length) {
            if (!subtitleEl) {
                subtitleEl = svg.append('text')
                    .attr('id', 'subtitle')
                    .attr('class', 'knime-subtitle')
                    .attr('x', 2)
                    .attr('y', 45);
            }
            subtitleEl.text(_value.subtitle);
        } else {
            if (subtitleEl) {
                subtitleEl.remove();
                subtitleEl = null;
            }
        }
    };

    const drawThresholdHandle = function () {
        thresholdEl = dendrogramEl.append('rect').attr('class', 'threshold')
            .attr('width', '100%').attr('height', thresholdHandleHeight);

        if (_representation.enableThresholdModification) {
            const maxDistance = yScale.domain()[1];

            thresholdEl.call(d3.drag()
                .on('drag', function () {
                    // abort if dragged outside min or max distance
                    var newThreshold = yScale.invert(d3.event.y);
                    if (newThreshold <= 0 || newThreshold >= maxDistance) {
                        return false;
                    }

                    // move threshold handle
                    thresholdEl.attr('transform', 'translate(0,' + (d3.event.y - (thresholdEl.attr('height') / 2)) + ')');

                    onThresholdChange(newThreshold);

                    // save that the threshold was manually specified
                    _value.numClustersMode = false;
                }));

            if (_representation.runningInView) {
                svg.classed('thresholdEnabled', true);
            }
        }

        thresholdDisplayEl = wrapperEl.append('text').attr('class', 'thresholdDisplay');
        thresholdClusterDisplayEl = wrapperEl.append('text').attr('class', 'thresholdClusterDisplay');

        // set initial threshold
        onThresholdChange(_value.threshold);
    };

    const onThresholdChange = function (threshold) {
        // reset root prop and cluster labels
        nodes.each(function (n) {
            n.root = false;
            _value.clusterLabels[n.data.id] = '';
        });

        // collect all nodes which form a cluster
        const clusterNodes = [];
        const stack = [nodes];
        while (stack.length) {
            var n = stack.pop();
            if (n.data.distance <= threshold) {
                clusterNodes.push(n);
            }
            else {
                if (n.children) {
                    // so push them in this order to ensure labels increase from left (Cluster_0) to right (Cluster_n)
                    stack.push(n.children[1]);
                    stack.push(n.children[0]);
                }
            }
        }
        const numberOfRootCluster = clusterNodes.length;

        // set root prop and set new cluster labels
        clusterNodes.forEach(function (n, i) {
            n.root = true;
            _value.clusterLabels[n.data.id] = 'Cluster_' + i;
        });

        // update DOM: cluster marker
        clusterMarkerEl.each(function (n) {
            d3.select(this)
                .classed('outOfThreshold', n.data.distance > threshold)
                .classed('root', n.root)
                .select('title').text(function () {
                    if (n.root) {
                        // tooltip should show label and distance
                        return _value.clusterLabels[n.data.id] + '\nDistance: ' + n.data.distance;
                    } else {
                        // tooltip only shows distance
                        return 'Distance: ' + n.data.distance;
                    }
                });
        });

        // get leaves which form a cluster
        const leafCluster = clusterNodes.filter(function (n) {
            return leaves.indexOf(n) !== -1;
        });

        // update DOM: add clusterMarkers for leaves
        leafClusterMarkerEl = leafClusterMarkerContainerEl.selectAll('circle')
            .data(leafCluster);
        leafClusterMarkerEl.exit().remove();
        leafClusterMarkerEl.selectAll('title').remove();
        leafClusterMarkerEl = leafClusterMarkerEl.enter().append('circle').merge(leafClusterMarkerEl)
            .attr('class', 'cluster root')
            .attr('r', clusterMarkerRadius / _value.zoomK)
            .attr('transform', function (d) {
                return 'translate(' + d.x + ',' + (d.y - (leafHeight / _value.zoomK)) + ')';
            });
        leafClusterMarkerEl.append('title').text(function (d) {
            return _value.clusterLabels[d.data.id] + '\n' + d.data.rowKey;
        });

        // update DOM: leaves
        leafEl.each(function (d) {
            const el = d3.select(this);

            var titleEl = el.select('title');
            if (d.root) {
                if (titleEl.empty()) {
                    titleEl = el.append('title');
                }
                titleEl.text(_value.clusterLabels[d.data.id] + '\n' + d.data.rowKey);
            } else {
                titleEl.remove();
            }
        });

        // update threshold display
        if (_representation.showThresholdBar) {
            const thresholdFormatted = thresholdFormat(threshold);
            thresholdDisplayEl.text('Threshold: ' + thresholdFormatted);
            thresholdClusterDisplayEl.text('Clusters: ' + numberOfRootCluster);
        }

        // save new threshold values
        _value.threshold = threshold;
        _value.numClusters = numberOfRootCluster;
        if (numClustersField) {
            numClustersField.value = _value.numClusters;
        }

        // mark links
        linkEl.each(function (n) {
            d3.select(this).classed('outOfThreshold', n.source.data.distance > threshold);
        });
    };

    const setThresholdByNumClusters = function (number) {
        var threshold = nodes.data.distance;
        var clusterCount = 1;

        clusterMarker
            .sort(function (a, b) { return b.data.distance - a.data.distance; })
            .some(function (n) {
                if (number == clusterMarker.length + 1) {
                    threshold = clusterMarker[clusterMarker.length - 1].data.distance / 2;
                    return true;
                } else if (number == clusterCount) {
                    threshold = (n.data.distance + threshold) / 2;
                    return true; // aborts loop
                } else {
                    clusterCount++;
                    threshold = n.data.distance;
                    return false;
                }
            });

        onThresholdChange(threshold);

        // move threshold handle
        thresholdEl.attr('transform', 'translate(0,' + (yScale(threshold) - (thresholdEl.attr('height') / 2)) + ')');
    };

    const resizeDiagram = function (isInitial, animate) {
        calcSVGSize();

        viewportEl.attr('transform', 'translate(' + yAxisWidth + ',0)');
        viewportClipEl.attr('width', viewportWidth)
            .attr('height', viewportHeight + viewportMarginTop);
        xAxisClipEl.attr('width', viewportWidth + yAxisWidth);

        // recalculate cluster
        cluster.size([viewportWidth, viewportHeight - viewportMarginTop]);
        cluster(nodes);

        // re-position wrapper
        wrapperEl.attr('transform', 'translate(' + getYAxisLabelWidth() + ',' + getTitleHeight() + ')');

        // update axis
        xAxisEl.attr('transform', 'translate(' + yAxisWidth + ',' + (viewportHeight + viewportMarginTop) + ')');
        yAxisEl.attr('transform', 'translate(' + yAxisWidth + ',' + viewportMarginTop + ')');
        updateXAxis();
        updateYAxis(null, animate);

        if (xAxisLabelEl) {
            xAxisLabelEl
                .attr('x', svgSize.width / 2)
                .attr('y', svgSize.height - 8);
        }
        if (yAxisLabelEl) {
            yAxisLabelEl.attr('transform', 'matrix(0,-1,1,0,12,' + (svgSize.height / 2) + ')');
        }

        // apply the distance of each node
        nodes.each(function (n) {
            n.y = yScale(n.data.distance);
        });

        // maybe enable animation
        var localLinkEl = linkEl;
        var localClusterMarkerEl = clusterMarkerEl;
        var localThresholdEl = thresholdEl;
        if (animate) {
            localLinkEl = localLinkEl.transition()
                .duration(animationDuration);
            localClusterMarkerEl = localClusterMarkerEl.transition()
                .duration(animationDuration);

            if (localThresholdEl) {
                localThresholdEl = localThresholdEl.transition()
                    .duration(animationDuration);
            }
        }

        // re-position elements
        localLinkEl.attr('d', function (l) {
            return 'M' + l.source.x + ' ' + l.source.y + ' L' + l.target.x + ' ' + l.source.y + ' L' + l.target.x + ' ' + l.target.y;
        });

        leafEl.attr('transform', function (d) {
            return 'translate(' + d.x + ',' + d.y + ')';
        });

        localClusterMarkerEl.attr('transform', function (d) {
            return 'translate(' + d.x + ',' + d.y + ')';
        });

        if (localThresholdEl) {
            localThresholdEl.attr('transform', 'translate(0,' + (yScale(_value.threshold) - (thresholdEl.attr('height') / 2)) + ')');
        }

        if (thresholdDisplayEl) {
            thresholdDisplayEl.attr('transform', 'translate(' + (yAxisWidth + 5) + ' ,' + 25 + ')');
            thresholdClusterDisplayEl.attr('transform', 'translate(' + (yAxisWidth + 5) + ' ,' + 40 + ')');
        }

        zoom.translateExtent([[0, 0], [viewportWidth, viewportHeight]]);
        zoom.extent([[0, 0], [viewportWidth, viewportHeight]]);
        svg.call(zoom).on('dblclick.zoom', null); // prevent zoom on double click

        if (!isInitial) {
            // refresh zoom
            const zoomX = _value.zoomX !== undefined ? _value.zoomX : 0;
            const zoomY = _value.zoomY !== undefined ? _value.zoomY : 0;
            const zoomK = _value.zoomK !== undefined ? _value.zoomK : 1;
            svg.call(zoom.transform, d3.zoomIdentity.translate(zoomX, zoomY).scale(zoomK));
        }
    };

    const initWindowResize = function () {
        const debounce = function (func, delay) {
            var timeout;
            return function () {
                const context = this, args = arguments;
                clearTimeout(timeout);
                timeout = setTimeout(function () {
                    timeout = null;
                    func.apply(context, args);
                }, delay);
            };
        };
        d3.select(window).on('resize', debounce(resizeDiagram, 75));
    };

    const initZoomingAndPanning = function () {
        zoom = d3.zoom()
            .translateExtent([[0, 0], [viewportWidth, viewportHeight]])
            .extent([[0, 0], [viewportWidth, viewportHeight]])
            .on('zoom', function () {
                dendrogramEl.attr('transform', d3.event.transform);

                updateXAxis(d3.event.transform);
                updateYAxis(d3.event.transform);

                // rescale line widths and markers
                linkEl.attr('stroke-width', linkStrokeWidth / d3.event.transform.k);
                clusterMarkerEl.attr('r', clusterMarkerRadius / d3.event.transform.k);
                leafClusterMarkerEl
                    .attr('r', clusterMarkerRadius / d3.event.transform.k)
                    .attr('transform', function (d) {
                        return 'translate(' + d.x + ',' + (d.y - (leafHeight / d3.event.transform.k)) + ')';
                    });

                leafEl.attr('width', leafWidth / d3.event.transform.k)
                    .attr('height', leafHeight / d3.event.transform.k)
                    .attr('x', -(leafWidth / d3.event.transform.k) / 2)
                    .attr('y', -(leafHeight / d3.event.transform.k));

                if (thresholdEl) {
                    thresholdEl.attr('height', thresholdHandleHeight / d3.event.transform.k);
                    thresholdEl.attr('transform', 'translate(0,' + (yScale(_value.threshold) - (thresholdEl.attr('height') / 2)) + ')');
                }
            })
            .on('end', function () {
                // save zoom and pan
                _value.zoomX = d3.event.transform.x;
                _value.zoomY = d3.event.transform.y;
                _value.zoomK = d3.event.transform.k;

                // maybe y axis width has changed, therefore we need to resize the diagram
                resizeDiagram(true, false);
            });

        const zoomX = _value.zoomX !== undefined ? _value.zoomX : 0;
        const zoomY = _value.zoomY !== undefined ? _value.zoomY : 0;
        const zoomK = _value.zoomK !== undefined ? _value.zoomK : 1;
        if (_representation.enableZoomAndPanning) {
            zoom.scaleExtent([1, Infinity]);
        } else {
            zoom.scaleExtent([zoomK, zoomK]);
        }

        svg.call(zoom)
            .on('dblclick.zoom', null); // prevent zoom on double click

        // set initial zoom and pan
        if (zoomK != 1 || zoomX != 0 || zoomY != 0) {
            svg.call(zoom.transform, d3.zoomIdentity.translate(zoomX, zoomY).scale(zoomK));
        }
    };

    const resetZoom = function () {
        svg.transition()
            .duration(750)
            .call(zoom.transform, d3.zoomIdentity.translate(0, 0).scale(1));
    };

    const updateSelectionInView = function () {
        // add selected flag for leaves
        leaves.forEach(function (n) {
            n.selected = selectedRows.indexOf(n.data.rowKey) !== -1;
        });

        // also select cluster if both children are selected
        nodes.eachAfter(function (n) {
            if (n.children) {
                n.selected = n.children[0].selected && n.children[1].selected;
            }
        });

        // set/remove styles for selected rows and cluster and links
        leafEl.classed('selected', function (d) {
            return d.selected;
        });
        clusterMarkerEl.classed('selected', function (d) {
            return d.selected;
        });
        leafClusterMarkerEl.classed('selected', function (d) {
            return d.selected;
        });
        linkEl.classed('selected', function (d) {
            return (d.source.selected && d.target.selected) || d.target.selected && !d.target.children;
        });
        xAxisEl.selectAll('.tick').classed('selected', function (rowKey) {
            return selectedRows.indexOf(rowKey) !== -1;
        });
    };

    const onSelectionChange = function (data) {
        if (data.reevaluate) {
            selectedRows = knimeService.getAllRowsForSelection(table.getTableId());
        } else if (data.changeSet) {
            if (data.changeSet.removed) {
                selectedRows = selectedRows.filter(function (item) {
                    return data.changeSet.removed.indexOf(item) === -1;
                });
            }
            if (data.changeSet.added) {
                data.changeSet.added.forEach(function (item) {
                    if (selectedRows.indexOf(item) === -1) {
                        selectedRows.push(item);
                    }
                });
            }
        }

        updateSelectionInView();
    };

    const toggleSelectRow = function (rowKey) {
        if (filteredRows.length && filteredRows.indexOf(rowKey) === -1) {
            // abort if row is filtered out
            return;
        }
        const select = selectedRows.indexOf(rowKey) === -1 ? true : false;
        const multiSelect = d3.event.ctrlKey || d3.event.shiftKey || d3.event.metaKey;

        if (select) {
            if (multiSelect) {
                if (selectedRows.indexOf(rowKey) === -1) {
                    selectedRows.push(rowKey);
                }
            } else {
                selectedRows = [rowKey];
            }
        } else {
            if (multiSelect) {
                selectedRows = selectedRows.filter(function (item) {
                    return item !== rowKey;
                });
            } else {
                selectedRows = [rowKey];
            }
        }

        updateSelectionInView();
        if (_value.publishSelectionEvents) {
            knimeService.setSelectedRows(table.getTableId(), selectedRows, onSelectionChange);
        }
    };

    const toggleSelectCluster = function (clusterNode) {
        if (clusterNode.outOfFilter) {
            // abort if cluster is filtered out
            return;
        }
        const select = !clusterNode.selected;
        const multiSelect = d3.event.ctrlKey || d3.event.shiftKey || d3.event.metaKey;

        const leaves = clusterNode.leaves();
        const rowsToSelect = leaves.map(function (leaf) { return leaf.data.rowKey; });
        if (select) {
            if (multiSelect) {
                rowsToSelect.forEach(function (rowKey) {
                    if (selectedRows.indexOf(rowKey) === -1) {
                        selectedRows.push(rowKey);
                    }
                });
            } else {
                selectedRows = rowsToSelect;
            }
        } else {
            if (multiSelect) {
                selectedRows = selectedRows.filter(function (item) {
                    return rowsToSelect.indexOf(item) === -1;
                });
            } else {
                selectedRows = selectedRows.filter(function (item) {
                    return rowsToSelect.indexOf(item) !== -1;
                });
            }
        }

        updateSelectionInView();
        if (_value.publishSelectionEvents) {
            knimeService.setSelectedRows(table.getTableId(), selectedRows, onSelectionChange);
        }
    };

    const clearSelection = function () {
        selectedRows = [];
        updateSelectionInView();

        if (_value.publishSelectionEvents) {
            knimeService.setSelectedRows(table.getTableId(), selectedRows, onSelectionChange);
        }
    };

    const initSelection = function () {
        if (_representation.runningInView) {
            svg.classed('selectionEnabled', true);
        }

        dendrogramEl.on('click', function () { // make use of event bubbling and only register one listener
            if (d3.event.target.nodeName === 'circle') {
                toggleSelectCluster(d3.event.target.__data__);
            } else if (d3.event.target.nodeName === 'rect') {
                toggleSelectRow(d3.event.target.__data__.data.rowKey);
            }
        });

        xAxisEl.on('click', function () { // register event listener on container to also catch dynamically added labels
            if (d3.event.target.nodeName === 'text') {
                const rowKey = d3.event.target.__data__;
                toggleSelectRow(rowKey);
            }
        });

        if (Array.isArray(_value.selection) && _value.selection.length) {
            selectedRows = _value.selection;
            updateSelectionInView();
        }
    };

    const toggleSubscribeSelection = function () {
        if (_value.subscribeSelectionEvents) {
            knimeService.subscribeToSelection(table.getTableId(), onSelectionChange);
        } else {
            knimeService.unsubscribeSelection(table.getTableId(), onSelectionChange);
        }
    };

    const updateFilterInView = function () {
        const isFiltering = !!filteredRows.length;

        // add filter flag for leaves
        leaves.forEach(function (n) {
            n.outOfFilter = isFiltering && filteredRows.indexOf(n.data.rowKey) === -1;
        });

        // also mark cluster if both children are filtered out
        nodes.eachAfter(function (n) {
            if (n.children) {
                n.outOfFilter = n.children[0].outOfFilter || n.children[1].outOfFilter;
            }
        });

        // set/remove styles for filtered rows and cluster and links
        clusterMarkerEl.classed('outOfFilter', function (d) {
            return d.outOfFilter;
        });
        leafEl.classed('outOfFilter', function (d) {
            return d.outOfFilter;
        });
        linkEl.classed('outOfFilter', function (d) {
            return d.source.outOfFilter || d.target.outOfFilter;
        });
        xAxisEl.selectAll('.tick').classed('outOfFilter', function (rowKey) {
            return isFiltering && !(filteredRows.indexOf(rowKey) !== -1);
        });

        // also update threshold
        onThresholdChange(_value.threshold);
    };

    const onFilterChange = function (data) {
        filteredRows = leaves.map(function (leaf) {
            return leaf.data.rowKey;
        }).filter(function (rowKey) {
            return table.isRowIncludedInFilter(rowKey, data);
        });

        updateFilterInView();
    };

    const toggleSubscribeFilter = function () {
        if (_value.subscribeFilterEvents) {
            knimeService.subscribeToFilter(table.getTableId(), onFilterChange, table.getFilterIds());
        } else {
            knimeService.unsubscribeFilter(table.getTableId(), onFilterChange);
        }

        if (!_value.subscribeFilterEvents) {
            filteredRows = [];
            updateFilterInView();
        }
    };

    const toggleShowSelectedOnly = function () {
        svg.classed('showSelectedOnly', _value.showSelectedOnly);
    };

    const toggleLogScale = function () {
        setYScale();
        resizeDiagram(true, true);
    };

    dendrogram.validate = function () {
        return true;
    };

    dendrogram.getComponentValue = function () {
        _value.selection = selectedRows;
        return _value;
    };

    dendrogram.getSVG = function () {
        if (!svg.empty()) {
            var svgElement = svg.node();
            knimeService.inlineSvgStyles(svgElement);
            // Return the SVG as a string.
            return (new XMLSerializer()).serializeToString(svgElement);
        } else {
            var w = _representation.imageWidth;
            var h = _representation.imageHeight;
            return '<svg height="' + h + '" width="' + w + '"><text x="0" y="15" fill="red">Error: No data available</text></svg>';
        }
    };

    return dendrogram;
}());