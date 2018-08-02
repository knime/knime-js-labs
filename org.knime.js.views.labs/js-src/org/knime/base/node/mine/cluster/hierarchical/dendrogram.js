(dendrogram_namespace = function () {

	var dendrogram = {};
	var _representation, _value;

	dendrogram.init = function (representation, value) {
		_representation = representation;
		_value = value;

		if (!_representation.tree || !_representation.tree.root) {
			d3.select('body').append('p').text('Error: No data available');
			return;
		}

		// TODO detect image export mode
		var isImageExport = false;
		var resizeToWindow = _representation.resizeToWindow;

		var xAxisHeight = 100,
			yAxisWidth = 30,
			labelMargin = 10,
			xAxisLabelWidth = 15,
			linkStrokeWidth = 1,
			clusterMarkerRadius = 4,
			viewportMarginTop = 10,
			leafWidth = 8,
			leafHeight = 20,
			thresholdHeight = 2;

		// create SVG
		var svg = d3.select('body').insert('svg:svg')
			.attr('width', resizeToWindow ? '100%' : _representation.imageWidth)
			.attr('height', resizeToWindow ? '100%' : _representation.imageHeight);

		var svgSize = d3.select('svg').node().getClientRects()[0];
		var viewportWidth = svgSize.width - yAxisWidth;
		var viewportHeight = svgSize.height - xAxisHeight;

		// create clipping path for viewport (needed for zooming & panning)
		var defs = svg.append('defs');
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

		var dendrogramEl = svg.append('g').attr('class', 'viewport').attr('transform', 'translate(' + yAxisWidth + ',0)').append('g').attr('transform', 'translate(0,' + viewportMarginTop + ')').append('g');

		// load data into d3 hierarchy representation
		var root_node = d3.hierarchy(_representation.tree.root);
		var cluster = d3.cluster().size([viewportWidth, viewportHeight - viewportMarginTop]).separation(function (a, b) {
			return 1;
		});
		var nodes = cluster(root_node);
		var links = nodes.links();

		// draw x axis
		var labels = nodes.leaves().map(function (n) { return n.data.rowKey; });
		var x = d3.scaleBand()
			.domain(labels)
			.range([0, svgSize.width - yAxisWidth]);
		var xShowNthTicks = Math.round(x.domain().length / (viewportWidth / xAxisLabelWidth));
		var xAxis = d3.axisBottom(x)
			.tickValues(x.domain().filter(function (d, i) { return !(i % xShowNthTicks) }));
		var xAxisEl = svg.append('g')
			.attr('class', 'knime-axis knime-x')
			.attr('transform', 'translate(' + yAxisWidth + ',' + (viewportHeight + viewportMarginTop) + ')')
			.call(xAxis);
		xAxisEl.selectAll('.tick').attr('class', 'tick knime-tick');
		xAxisEl.selectAll('line').attr('class', 'knime-tick-line');
		xAxisEl.selectAll('text').attr('class', 'knime-tick-label')
			.attr('dx', labelMargin * -1 + 'px')
			.attr('dy', '-5px');

		// draw y axis
		var y = d3.scaleLinear()
			.domain([0, root_node.data.distance])
			.range([viewportHeight, 0])
			.nice();
		var yAxis = d3.axisLeft(y)
			.ticks(5);
		var yAxisEl = svg.append('g')
			.attr('class', 'knime-axis knime-y')
			.attr('transform', 'translate(' + yAxisWidth + ',' + viewportMarginTop + ')')
			.call(yAxis);
		yAxisEl.selectAll('.tick').attr('class', 'tick knime-tick');
		yAxisEl.selectAll('line').attr('class', 'knime-tick-line');
		yAxisEl.selectAll('text').attr('class', 'knime-tick-label')

		// apply the distance of each node
		nodes.each(function (n) {
			n.y = y(n.data.distance);
		});

		// draw links
		var linkEls = dendrogramEl.selectAll('.link').data(links).enter().append('path').attr('class', 'link').attr('stroke-width', linkStrokeWidth).attr('d', function (l) {
			return 'M' + l.source.x + ' ' + l.source.y + ' L' + l.target.x + ' ' + l.source.y + ' L' + l.target.x + ' ' + l.target.y;
		});

		// draw leaves
		var leafEls = dendrogramEl.selectAll('.leaf').data(nodes.leaves()).enter().append('rect').attr('class', 'leaf').attr('x', -leafWidth / 2).attr('y', -leafHeight).attr('width', leafWidth).attr('height', leafHeight).attr('transform', function (d) {
			return 'translate(' + d.x + ',' + d.y + ')';
		}).attr('fill', function (d) {
			return d.data.color;
		});

		// draw cluster markers
		var clusterMarkerEl = dendrogramEl.selectAll('.cluster').data(nodes.descendants().filter(function (n) {
			return n.children != null;
		})).enter().append('circle').attr('class', 'cluster').attr('r', clusterMarkerRadius).attr('transform', function (d) {
			return 'translate(' + d.x + ',' + d.y + ')';
		});

		// draw threshold handle
		var maxDistance = y.domain()[1];
		var thresholdFormat = d3.format('.3f');
		var onThresholdChange = function (threshold) {
			var numberOfRootClusters = 0;
			clusterMarkerEl.each(function (n) {
				// mark nodes out of threshold
				d3.select(this).classed('outOfThreshold', n.data.distance >= threshold);
				// mark after-threshold root nodes
				if (n.data.distance <= threshold && (!n.parent || n.parent && n.parent.data.distance >= threshold)) {
					d3.select(this).classed('root', true);
					numberOfRootClusters++;
				}
				else {
					d3.select(this).classed('root', false);
				}
			});
			// update threshold display
			var thresholdFormatted = thresholdFormat(threshold);
			thresholdDisplayEl.text('Threshold: ' + thresholdFormatted);
			thresholdClusterDisplayEl.text('Clusters: ' + numberOfRootClusters);
			// mark links
			linkEls.each(function (n) {
				d3.select(this).classed('outOfThreshold', n.source.data.distance >= threshold);
			});
		};
		var thresholdEl = dendrogramEl.append('rect').attr('class', 'threshold')
			.attr('width', '100%').attr('height', thresholdHeight)
			.attr('transform', 'translate(0,' + y(value.threshold) + ')')
			.call(d3.drag()
				.on('drag', function () {
					// abort if dragged outside min or max distance
					var newThreshold = y.invert(d3.event.y);
					if (newThreshold <= 0 || newThreshold >= maxDistance) {
						return false;
					}

					// move threshold handle
					thresholdEl.attr('transform', 'translate(0,' + d3.event.y + ')');

					onThresholdChange(newThreshold);

					// save new threshold
					_value.threshold = newThreshold;
				}));

		var thresholdDisplayEl = svg.append('text').attr('class', 'thresholdDisplay')
			.attr('transform', 'translate(' + (yAxisWidth + 5) + ' ,' + 20 + ')');

		var thresholdClusterDisplayEl = svg.append('text').attr('class', 'thresholdClusterDisplay')
			.attr('transform', 'translate(' + (yAxisWidth + 5) + ' ,' + 35 + ')');

		// init zooming & panning
		svg.call(d3.zoom()
			.scaleExtent([1, 20])
			.translateExtent([[0, 0], [svgSize.width, svgSize.height]])
			.on('zoom', function () {
				dendrogramEl.attr('transform', d3.event.transform);

				// TODO refactor this
				var xShowNthTicks = Math.round(x.domain().length / (viewportWidth * d3.event.transform.k / 15));
				xAxis.tickValues(x.domain().filter(function (d, i) { return !(i % xShowNthTicks) }));
				x.range([0, viewportWidth].map(function (d) { return d3.event.transform.applyX(d); }));
				xAxisEl.selectAll('.tick').attr('class', 'tick knime-tick');
				xAxisEl.selectAll('line').attr('class', 'knime-tick-line');
				xAxisEl.selectAll('text').attr('class', 'knime-tick-label')
					.attr('dx', labelMargin * -1 + 'px')
					.attr('dy', '-5px');

				xAxisEl.call(xAxis);
				yAxisEl.call(yAxis.scale(d3.event.transform.rescaleY(y)));

				// rescale line widths and markers
				linkEls.attr('stroke-width', linkStrokeWidth / d3.event.transform.k);
				clusterMarkerEl.attr('r', clusterMarkerRadius / d3.event.transform.k);
				leafEls.attr('width', leafWidth / d3.event.transform.k);
				leafEls.attr('height', leafHeight / d3.event.transform.k);
				leafEls.attr('x', -(leafWidth / d3.event.transform.k) / 2);
				leafEls.attr('y', -(leafHeight / d3.event.transform.k));
				thresholdEl.attr('height', thresholdHeight / d3.event.transform.k);
			}));

		// initial threshold
		onThresholdChange(value.threshold);


		// TODO handle window resize
		d3.select(window).on('resize', function () {
			alert('window resize not supported yet, press "reset" to reload');
		});
	}

	dendrogram.validate = function () {
		return true;
	}

	dendrogram.getComponentValue = function () {
		return _value;
	}

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
	}

	return dendrogram;

}());
