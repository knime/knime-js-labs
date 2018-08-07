window.dendrogram_namespace = (function () {
    const dendrogram = {};

    var _representation,
        _value,
        selectedRows = [];

    // view related settings
    const xAxisHeight = 100,
        yAxisWidth = 30,
        labelMargin = 10,
        xAxisLabelWidth = 15,
        linkStrokeWidth = 1,
        clusterMarkerRadius = 4,
        viewportMarginTop = 10,
        leafWidth = 8,
        leafHeight = 20,
        thresholdHandleHeight = 2;

    // hierarchy related variables
    var nodes,
        leaves,
        links;

    // view related variables
    var svg,
        svgSize,
        viewportWidth,
        viewportHeight,
        dendrogramEl,
        clusterMarkerEl,
        leafEl,
        linkEl,
        thresholdEl,
        xAxis,
        xAxisEl,
        xScale,
        yAxis,
        yAxisEl,
        yScale;

    dendrogram.init = function (representation, value) {
        _representation = representation;
        _value = value;

        if (!_representation.tree || !_representation.tree.root) {
            d3.select('body').append('p').text('Error: No data available');
            return;
        }

        drawSVG();
        createHierarchyFromTree();
        drawXAxis();
        drawYAxis();
        drawDendrogram();
        drawThresholdHandle();

        initZoomingAndPanning();
        initSelection();

        // TODO handle window resize
        d3.select(window).on('resize', function () {
            alert('window resize not supported yet, press "reset" to reload');
        });
    };

    const drawSVG = function () {
        // TODO detect image export mode
        //const isImageExport = false;
        const resizeToWindow = _representation.resizeToWindow;

        // create SVG
        svg = d3.select('body').insert('svg:svg')
            .attr('width', resizeToWindow ? '100%' : _representation.imageWidth)
            .attr('height', resizeToWindow ? '100%' : _representation.imageHeight);

        svgSize = d3.select('svg').node().getClientRects()[0];
        viewportWidth = svgSize.width - yAxisWidth;
        viewportHeight = svgSize.height - xAxisHeight;

        // create clipping path for viewport (needed for zooming & panning)
        const defs = svg.append('defs');
        defs.append('clipPath')
            .attr('id', 'viewportClip')
            .append('rect')
            .style('width', 'calc(100% - ' + yAxisWidth + 'px)')
            .style('height', 'calc(100% - ' + (xAxisHeight - viewportMarginTop) + 'px)');

        defs.append('clipPath')
            .attr('id', 'xAxisClip')
            .append('rect')
            .style('width', 'calc(100% - ' + yAxisWidth + 'px)')
            .attr('height', xAxisHeight);

        dendrogramEl = svg.append('g').attr('class', 'viewport').attr('transform', 'translate(' + yAxisWidth + ',0)').append('g').attr('transform', 'translate(0,' + viewportMarginTop + ')').append('g');
    };

    const createHierarchyFromTree = function () {
        // load data into d3 hierarchy representation
        const root_node = d3.hierarchy(_representation.tree.root);
        const cluster = d3.cluster().size([viewportWidth, viewportHeight - viewportMarginTop]).separation(function () {
            return 1;
        });
        nodes = cluster(root_node);
        links = nodes.links();
        leaves = nodes.leaves();
    };

    const drawXAxis = function () {
        const labels = leaves.map(function (n) { return n.data.rowKey; });
        xScale = d3.scaleBand()
            .domain(labels)
            .range([0, svgSize.width - yAxisWidth]);
        var xShowNthTicks = Math.round(xScale.domain().length / (viewportWidth / xAxisLabelWidth));
        xAxis = d3.axisBottom(xScale)
            .tickValues(xScale.domain().filter(function (d, i) { return !(i % xShowNthTicks); }));
        xAxisEl = svg.append('g')
            .attr('class', 'knime-axis knime-x')
            .attr('transform', 'translate(' + yAxisWidth + ',' + (viewportHeight + viewportMarginTop) + ')')
            .call(xAxis)
            .on('click', function () { // register event listener on container to also catch dynamically added labels
                if (d3.event.target.nodeName === 'text') {
                    var rowKey = d3.event.target.__data__;
                    toggleSelectRow(rowKey);
                }
            });
        xAxisEl.selectAll('.tick').classed('knime-tick', true);
        xAxisEl.selectAll('line').classed('knime-tick-line', true);
        xAxisEl.selectAll('text').classed('knime-tick-label', true)
            .attr('dx', labelMargin * -1 + 'px')
            .attr('dy', '-5px');
    };

    const drawYAxis = function () {
        const maxDistance = nodes.data.distance;
        yScale = d3.scaleLinear()
            .domain([0, maxDistance])
            .range([viewportHeight, 0])
            .nice();
        yAxis = d3.axisLeft(yScale)
            .ticks(5);
        yAxisEl = svg.append('g')
            .attr('class', 'knime-axis knime-y')
            .attr('transform', 'translate(' + yAxisWidth + ',' + viewportMarginTop + ')')
            .call(yAxis);
        yAxisEl.selectAll('.tick').classed('knime-tick', true);
        yAxisEl.selectAll('line').classed('knime-tick-line', true);
        yAxisEl.selectAll('text').classed('knime-tick-label', true);

        // apply the distance of each node
        nodes.each(function (n) {
            n.y = yScale(n.data.distance);
        });
    };

    const drawDendrogram = function () {
        // draw links
        linkEl = dendrogramEl.selectAll('.link').data(links).enter().append('path').attr('class', 'link').attr('stroke-width', linkStrokeWidth).attr('d', function (l) {
            return 'M' + l.source.x + ' ' + l.source.y + ' L' + l.target.x + ' ' + l.source.y + ' L' + l.target.x + ' ' + l.target.y;
        });

        // draw leaves
        leafEl = dendrogramEl.selectAll('.leaf').data(leaves).enter().append('rect').attr('class', 'leaf').attr('x', -leafWidth / 2).attr('y', -leafHeight).attr('width', leafWidth).attr('height', leafHeight).attr('transform', function (d) {
            return 'translate(' + d.x + ',' + d.y + ')';
        }).attr('fill', function (d) {
            return d.data.color;
        });

        // draw cluster markers
        clusterMarkerEl = dendrogramEl.selectAll('.cluster').data(nodes.descendants().filter(function (n) {
            return n.children != null;
        })).enter().append('circle').attr('class', 'cluster').attr('r', clusterMarkerRadius).attr('transform', function (d) {
            return 'translate(' + d.x + ',' + d.y + ')';
        });
        clusterMarkerEl.append('title').text(function (d) {
            return d.data.distance;
        });
        dendrogramEl.on('click', function () { // make use of event bubbling and only register one listener
            if (d3.event.target.nodeName === 'circle') {
                toggleSelectCluster(d3.event.target.__data__);
            }
        });
    };

    const drawThresholdHandle = function () {
        const maxDistance = yScale.domain()[1];
        const thresholdFormat = d3.format('.3f');

        thresholdEl = dendrogramEl.append('rect').attr('class', 'threshold')
            .attr('width', '100%').attr('height', thresholdHandleHeight)
            .attr('transform', 'translate(0,' + yScale(_value.threshold) + ')')
            .call(d3.drag()
                .on('drag', function () {
                    // abort if dragged outside min or max distance
                    var newThreshold = yScale.invert(d3.event.y);
                    if (newThreshold <= 0 || newThreshold >= maxDistance) {
                        return false;
                    }

                    // move threshold handle
                    thresholdEl.attr('transform', 'translate(0,' + d3.event.y + ')');

                    onThresholdChange(newThreshold);

                    // save new threshold
                    _value.threshold = newThreshold;
                }));

        const thresholdDisplayEl = svg.append('text').attr('class', 'thresholdDisplay')
            .attr('transform', 'translate(' + (yAxisWidth + 5) + ' ,' + 20 + ')');

        const thresholdClusterDisplayEl = svg.append('text').attr('class', 'thresholdClusterDisplay')
            .attr('transform', 'translate(' + (yAxisWidth + 5) + ' ,' + 35 + ')');

        const onThresholdChange = function (threshold) {
            var numberOfRootCluster = 0;
            clusterMarkerEl.each(function (n) {
                // mark nodes out of threshold
                d3.select(this).classed('outOfThreshold', n.data.distance >= threshold);

                // mark after-threshold root nodes
                if (n.data.distance <= threshold && (!n.parent || n.parent && n.parent.data.distance >= threshold)) {
                    d3.select(this).classed('root', true);
                    numberOfRootCluster++;
                }
                else {
                    d3.select(this).classed('root', false);
                }
            });

            // count all leaves which represent a single cluster
            leaves.forEach(function (leaf) {
                if (leaf.parent.data.distance >= threshold) {
                    numberOfRootCluster++;
                }
            });

            // update threshold display
            const thresholdFormatted = thresholdFormat(threshold);
            thresholdDisplayEl.text('Threshold: ' + thresholdFormatted);
            thresholdClusterDisplayEl.text('Cluster: ' + numberOfRootCluster);
            // mark links
            linkEl.each(function (n) {
                d3.select(this).classed('outOfThreshold', n.source.data.distance >= threshold);
            });
        };

        // set initial threshold
        onThresholdChange(_value.threshold);
    };

    const initZoomingAndPanning = function () {
        svg.call(d3.zoom()
            .scaleExtent([1, 20])
            .translateExtent([[0, 0], [svgSize.width, svgSize.height]])
            .on('zoom', function () {
                dendrogramEl.attr('transform', d3.event.transform);

                // TODO refactor this
                const xShowNthTicks = Math.round(xScale.domain().length / (viewportWidth * d3.event.transform.k / 15));
                xAxis.tickValues(xScale.domain().filter(function (d, i) { return !(i % xShowNthTicks); }));
                xScale.range([0, viewportWidth].map(function (d) { return d3.event.transform.applyX(d); }));
                xAxisEl.selectAll('.tick').classed('knime-tick', true).classed('selected', function (rowKey) {
                    return selectedRows.indexOf(rowKey) !== -1;
                });
                xAxisEl.selectAll('line').classed('knime-tick-line', true);
                xAxisEl.selectAll('text').classed('knime-tick-label', true)
                    .attr('dx', labelMargin * -1 + 'px')
                    .attr('dy', '-5px');

                xAxisEl.call(xAxis);
                yAxisEl.call(yAxis.scale(d3.event.transform.rescaleY(yScale)));

                // rescale line widths and markers
                linkEl.attr('stroke-width', linkStrokeWidth / d3.event.transform.k);
                clusterMarkerEl.attr('r', clusterMarkerRadius / d3.event.transform.k);
                leafEl.attr('width', leafWidth / d3.event.transform.k)
                    .attr('height', leafHeight / d3.event.transform.k)
                    .attr('x', -(leafWidth / d3.event.transform.k) / 2)
                    .attr('y', -(leafHeight / d3.event.transform.k));
                thresholdEl.attr('height', thresholdHandleHeight / d3.event.transform.k);
            }))
            .on('dblclick.zoom', null); // prevent zoom on double click
    };

    const updateSelectionInView = function () {
        // add selected flag for leaves
        leaves.forEach(function (n) {
            if (selectedRows.indexOf(n.data.rowKey) != -1) {
                n.selected = true;
            } else {
                n.selected = false;
            }
        });

        // also select cluster if both children are selected
        nodes.eachAfter(function (n) {
            if (n.children) {
                n.selected = n.children[0].selected && n.children[1].selected;
            }
        });

        // set/remove styles for selected rows and cluster and links
        clusterMarkerEl.classed('selected', function (d) {
            return d.selected;
        });
        linkEl.classed('selected', function (d) {
            return d.source.selected && d.target.selected;
        });
        xAxisEl.selectAll('.tick').classed('selected', function (rowKey) {
            return selectedRows.indexOf(rowKey) !== -1;
        });
    };

    const onSelectionChange = function (data) {
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

        updateSelectionInView();
    };

    const toggleSelectRow = function (rowKey) {
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
        knimeService.setSelectedRows(_representation.dataTableID, selectedRows, onSelectionChange);
    };

    const toggleSelectCluster = function (clusterNode) {
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
        knimeService.setSelectedRows(_representation.dataTableID, selectedRows, onSelectionChange);
    };

    const initSelection = function () {
        knimeService.subscribeToSelection(_representation.dataTableID, onSelectionChange);
    };


    dendrogram.validate = function () {
        return true;
    };

    dendrogram.getComponentValue = function () {
        return _value;
    };

    dendrogram.getSVG = function () {
        var svg = d3.select('svg');
        if (!svg.empty()) {
            var svgElement = d3.select('svg')[0][0];
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
