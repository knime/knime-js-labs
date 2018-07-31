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

		var xAxisHeight = 100,
			yAxisWidth = 30,
			labelMargin = 10,
			linkStrokeWidth = 1,
			clusterMarkerRadius = 4;

		// create SVG
		var svg = d3.select('body').insert('svg:svg')
			.attr('width', isImageExport ? _representation.imageWidth : '100%')
			.attr('height', isImageExport ? _representation.imageHeight : '100%');

		var svgSize = d3.select('svg').node().getClientRects()[0];

		// create clipping path for viewport (needed for zooming & panning)
		var defs = svg.append('defs');
		defs.append('clipPath')
			.attr('id', 'viewportClip')
			.append('rect')
			.style('width', 'calc(100% - ' + yAxisWidth + 'px)')
			.style('height', 'calc(100% - ' + xAxisHeight + 'px)');

		defs.append('clipPath')
			.attr('id', 'xAxisClip')
			.append('rect')
			.style('width', 'calc(100% - ' + yAxisWidth + 'px)')
			.attr('height', xAxisHeight);

		var dendrogramEl = svg.append('g').attr('class', 'viewport').attr('transform', 'translate(' + yAxisWidth + ',0)').append('g');


		// load data into d3 hierarchy representation
		var root_node = d3.hierarchy(_representation.tree.root);
		var cluster = d3.cluster().size([svgSize.width - yAxisWidth, svgSize.height - xAxisHeight]).separation(function (a, b) {
			return 1;
		});
		var nodes = cluster(root_node);
		var links = nodes.links();

		// draw x axis
		var labels = nodes.leaves().map(function (n) { return n.data.rowKey; });
		var x = d3.scaleBand()
			.domain(labels)
			.range([0, svgSize.width - yAxisWidth]);
		var xAxis = d3.axisBottom(x);
		var xAxisEl = svg.append('g')
			.attr('class', 'knime-axis knime-x')
			.attr('transform', 'translate(' + yAxisWidth + ',' + (svgSize.height - xAxisHeight) + ')')
			.call(xAxis);
		xAxisEl.selectAll('.tick').attr('class', 'tick knime-tick');
		xAxisEl.selectAll('line').attr('class', 'knime-tick-line');
		xAxisEl.selectAll('text').attr('class', 'knime-tick-label')
			.attr('dx', labelMargin * -1 + 'px')
			.attr('dy', '-5px');

		// draw y axis
		var y = d3.scaleLinear()
			.domain([0, root_node.data.distance])
			.range([svgSize.height - xAxisHeight, 0]);
		var yAxis = d3.axisLeft(y)
			.ticks(5);
		var yAxisEl = svg.append('g')
			.attr('class', 'knime-axis knime-y')
			.attr('transform', 'translate(' + yAxisWidth + ',0)')
			.call(yAxis);
		yAxisEl.selectAll('.tick').attr('class', 'tick knime-tick');
		yAxisEl.selectAll('line').attr('class', 'knime-tick-line');
		yAxisEl.selectAll('text').attr('class', 'knime-tick-label')


		// draw links
		var linkEls = dendrogramEl.selectAll('.link').data(links).enter().append('path').attr('class', 'link').attr('stroke-width', linkStrokeWidth).attr('d', function (l) {
			return 'M' + l.source.x + ' ' + l.source.y + ' L' + l.target.x + ' ' + l.source.y + ' L' + l.target.x + ' ' + l.target.y;
		});

		// draw leaves
		dendrogramEl.selectAll('.leaf').data(nodes.leaves()).enter().append('rect').attr('class', 'leaf').attr('x', -4).attr('y', -20).attr('width', 8).attr('height', 20).attr('transform', function (d) {
			return 'translate(' + d.x + ',' + d.y + ')';
		}).attr('fill', function (d) {
			return d.data.color;
		});

		// calculate interpolated colors to fill cluster markers
		nodes.eachAfter(function (n) {
			if (n.children) {
				n.color = d3.interpolateRgb(n.children[0].data.color, n.children[1].data.color)(0.5);
			}
		});

		// draw cluster markers
		var clusterMarkerEl = dendrogramEl.selectAll('.cluster').data(nodes.descendants().filter(function (n) {
			return n.children != null;
		})).enter().append('circle').attr('class', 'cluster').attr('r', clusterMarkerRadius).attr('transform', function (d) {
			return 'translate(' + d.x + ',' + d.y + ')';
		}).attr('fill', function (d) {
			return d.color;
		});


		// init zooming & panning
		svg.call(d3.zoom()
			.scaleExtent([1, 20])
			.translateExtent([[0, 0], [svgSize.width, svgSize.height]])
			.on('zoom', function () {
				dendrogramEl.attr('transform', d3.event.transform);

				x.range([0, svgSize.width - yAxisWidth].map(function (d) { return d3.event.transform.applyX(d); }));
				xAxisEl.call(xAxis);
				yAxisEl.call(yAxis.scale(d3.event.transform.rescaleY(y)));

				// rescale line widths and markers
				linkEls.attr('stroke-width', linkStrokeWidth / d3.event.transform.k);
				clusterMarkerEl.attr('r', clusterMarkerRadius / d3.event.transform.k);
			}))



		// TODO handle window resize
		d3.select(window).on('resize', function () {
			//alert('TODO handle window resize');
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