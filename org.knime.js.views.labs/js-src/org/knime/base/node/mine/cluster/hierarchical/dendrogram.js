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

		var labelHeight = 100,
			labelMargin = 10;

		// create SVG
		var svg = d3.select('body').insert('svg:svg')
			.attr('width', isImageExport ? _representation.imageWidth : '100%')
			.attr('height', isImageExport ? _representation.imageHeight : '100%')
			.call(d3.zoom().on('zoom', function () {
				svg.attr('transform', d3.event.transform)
			}))
			.append('g');

		var svgSize = d3.select('svg').node().getClientRects()[0];

		// load data into d3 hierarchy representation
		var root_node = d3.hierarchy(_representation.tree.root);
		var cluster = d3.cluster().size([svgSize.width, svgSize.height - labelHeight - labelMargin]).separation(function (a, b) {
			return 1;
		});
		var nodes = cluster(root_node);
		var links = nodes.links();

		// draw links
		svg.selectAll('.link').data(links).enter().append('path').attr('class', 'link').attr('d', function (l) {
			return 'M' + l.source.x + ' ' + l.source.y + ' L' + l.target.x + ' ' + l.source.y + ' L' + l.target.x + ' ' + l.target.y;
		});

		// draw leaves
		svg.selectAll('.leaf').data(nodes.leaves()).enter().append('rect').attr('class', 'leaf').attr('x', -4).attr('y', -20).attr('width', 8).attr('height', 20).attr('transform', function (d) {
			return 'translate(' + d.x + ',' + d.y + ')';
		}).attr('fill', function (d) {
			return d.data.color;
		});

		// draw labels
		svg.selectAll('.label').data(nodes.leaves()).enter().append('text').attr('class', 'label').attr('y', function (d, i) { return d.x + 5 }).attr('x', (svgSize.height - labelHeight) * -1)
			.text(function (d) { return d.data.rowKey })
			.attr('transform', 'rotate(-90)');

		// calculate interpolated colors to fill cluster markers
		nodes.eachAfter(function (n) {
			if (n.children) {
				n.color = d3.interpolateRgb(n.children[0].data.color, n.children[1].data.color)(0.5);
			}
		});

		// draw cluster markers
		svg.selectAll('.cluster').data(nodes.descendants().filter(function (n) {
			return n.children != null;
		})).enter().append('circle').attr('class', 'cluster').attr('r', 4).attr('transform', function (d) {
			return 'translate(' + d.x + ',' + d.y + ')';
		}).attr('fill', function (d) {
			return d.color;
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