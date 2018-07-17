(optics_namespace = function() {

	var optics = {};
	var layoutContainer = "layoutContainer";
	var MIN_HEIGHT = 300, MIN_WIDTH = 400;
	var _representation, _value;
	var chart, svg;
    var _keyedDataset = null;

    var containerID = "opticsContainer";
    
    var max_val = 0;
    var margin = {top: 50, bottom: 10, left:40, right:40};
    var dataset_for_vis = [];
    var rows;
    var pseudo_infinity;
    var resizeCounter = 0;
    var yScale, xScale;
    var cluster_id = -1;
    var color;
    var tooBigEpsPr = false;
    var selection = [];
    var binnedWarning = false;
    var initEpsPrime;
    var allNoise = false;
	
	optics.init = function(representation, value) {  
		_representation = representation;
		_value = value;

		if ((!representation.keyedDataset)
				|| representation.keyedDataset.rows.length < 1) {
			d3.select("body").append("p").text("Error: No data available");
			return;
		}
        
		try {
            initEpsPrime = _value.epsPrime;
			_keyedDataset = new jsfc.KeyedValues2DDataset();
			for (var rowIndex = 0; rowIndex < _representation.keyedDataset.rows.length; rowIndex++) {
				var rowKey = _representation.keyedDataset.rows[rowIndex].rowKey.replace(/ /i, "");
				var row = _representation.keyedDataset.rows[rowIndex];
				var properties = row.properties;
				for (var col = 0; col < _representation.keyedDataset.columnKeys.length; col++) {
					var columnKey = _representation.keyedDataset.columnKeys[col];
                    if (row.values[col] != "Infinity" && row.values[col] > max_val && col == 2) {
                        max_val = row.values[col]
                    }
					_keyedDataset.add(rowKey, columnKey, row.values[col] == "Infinity"? Infinity : row.values[col] );
				}
				for ( var propertyKey in properties) {
					_keyedDataset.setRowProperty(rowKey, propertyKey,
							properties[propertyKey]);
				}
			}
			for (var col = 0; col < _representation.keyedDataset.columnKeys.length; col++) {
				var symbolProp = _representation.keyedDataset.symbols[col];
				if (symbolProp) {
					var columnKey = _representation.keyedDataset.columnKeys[col];
					var symbols = [];
					for ( var symbolKey in symbolProp) {
						symbols.push({
							"symbol" : symbolProp[symbolKey],
							"value" : symbolKey
						});
					}
					_keyedDataset.setColumnProperty(columnKey, "symbols",
							symbols);
				}
			}
              
            var dataset_preprocessed = dataTransformation();
            if (dataset_preprocessed.length > _representation.maxBins) {
                dataset_for_vis = equiWidthBinning(dataset_preprocessed);
                binnedWarning = true;
            } else {
                dataset_for_vis = dataset_preprocessed;
            }
                    
            if (_value.epsPrime > pseudo_infinity) {
                _value.epsPrime = pseudo_infinity;
                tooBigEpsPr = true;
            }
            
			d3.select("body")
                .attr("id", "body")
                .append("div")
                .attr("id", layoutContainer)
                .attr("class", "knime-layout-container")
                .style("min-width", MIN_WIDTH + "px")
                .style("min-height", MIN_HEIGHT + "px");

            if (representation.showWarningInView && representation.warnings !== null) {
				var map = representation.warnings.warningMap;
				for (var id in map) {
			        if (map.hasOwnProperty(id)) {
			        	knimeService.setWarningMessage(map[id], id);			           
			        }
			    }
			}
            
            window.addEventListener("resize", function() {resizeCounter++;});
            drawChart(layoutContainer, false);
            drawControls();
            setTitle();
            setSubtitle();
            
            tooBigEpsMessage();
            
            var win = document.defaultView || document.parentWindow;
            win.onresize = resize;
 
		} catch (err) {
			if (err.stack) {
				alert(err.stack);
			} else {
				alert(err);
			}
		}
        if (parent != undefined && parent.KnimePageLoader != undefined) {
			parent.KnimePageLoader.autoResize(window.frameElement.id);
		}
	}
    
    //destribute data points between bins: each bin contain equal number of data points + additional bin for a "tail"
    equiWidthBinning = function (dataset_prepprocessed) {
        var dataset = [];
        var add_bin = dataset_prepprocessed.length %  _representation.maxBins == 0? 0 : 1;
        var num_points_in_bin = Math.floor(dataset_prepprocessed.length/ _representation.maxBins);
        var bins =  _representation.maxBins + add_bin;
        for (var i = 0; i < bins; i++) {
            var included_points = [];
            for (var j = 0; j < num_points_in_bin; j++) {
                //console.log(dataset_prepprocessed[num_points_in_bin * i + j])
                included_points.push(dataset_prepprocessed[num_points_in_bin * i + j]);
            }    
            dataset.push({
                cluster: parseInt(majorityCluster(included_points)),
                key: "Bin" + i,
                value: avgClusterValue(included_points),
                incl_points: included_points
            })
        }
        //console.log(dataset)
        return dataset;
    }
    
    //find a majority cluster within one bin 
    majorityCluster = function(included_points) {
        var majority_cluster_count = d3.nest()
                .key(function(d) { return d.cluster; })
                .rollup(function(v) { return v.length; })
                .entries(included_points);
        return d3.entries(majority_cluster_count)
                .sort(function(a, b) { return d3.descending(a.value.values, b.value.values) 
                    || d3.descending(a.value.key, b.value.key); })[0].value.key; 
    }
    
    //count average value between all points in a bin
    avgClusterValue = function(included_points) {
        return d3.nest()
                .rollup(function(v) { return d3.mean(v, function(d) { return d.value; }); })
                .entries(included_points);
    }
    
    maxValCalc = function(data) {
        return d3.nest()
                .rollup(function(v) { return d3.max(v, function(d) { return d.value; }); })
                .entries(data);
    }

    tooBigEpsMessage = function() {
        if (allNoise) {
                //alert("Epsilon prime is too big to plot: the value is reduced but leads to the same results as the previous.")
                alert("All points are noise points.")
            }
        allNoise = false;
    }
    
    dataTransformation = function() {
        rows = _keyedDataset.data.rows;
        pseudo_infinity = _representation.eps < 1.3*max_val || max_val == 0? _representation.eps : 1.3*max_val;
        if (max_val == 0) {
            allNoise = true;
            
        }
        dataset = [];
        cluster_id = -1;
        for (var r = 0; r<rows.length;r++ ){
            dataset.push({key: rows[r].key.replace(/ /i, ""),
                      value: rows[r].values[2] == Infinity || rows[r].values[2] > _representation.eps ? 1.005*pseudo_infinity : rows[r].values[2],
                      coredist: rows[r].values[1],
                      cluster: clusterCount(rows[r].values[2], rows[r].values[1], _value.epsPrime),
                      incl_points: []});
        }
        return dataset;
    }
    
    maxOrMinPlot = function() {
        if (resizeCounter % 2 == 0) {
            return false;
        }
        return true;
    }
    
    resize = function(event) {
        d3.select("#chart_svg").remove();
        //setChartDimensions();
        drawChart(layoutContainer, maxOrMinPlot);
        updateEpsPrime();
        setTitle();
        setSubtitle();
    };

    getChartDimensions = function() {
        var container = document.getElementById(layoutContainer);
        var w = _representation.imageWidth;
        var h = _representation.imageHeight;
        if (_representation.resizeToWindow) {
            w = Math.max(MIN_WIDTH, container.clientWidth);
            h = Math.max(MIN_HEIGHT, container.clientHeight);
        }
        return [w, h]
    };

// The code is not finished yet: overlay on the clusters
//    overlayData = function (data) {
//        console.log("Overlay init data", data)
//        var fildata = data.filter(function(d) {return d.cluster >= 0})
//        return d3.nest()
//            .key(function(d) {return d.cluster; })
//            //.rollup(function(v) {return d3.min(v, function(d) { return xScale(d.key); }); })
//            .rollup(function(v) {
//                return {
//                    xVal: d3.min(v, function(d) { return xScale(d.key); }),
//                    width: d3.max(v, function(d) { return xScale(d.key); }) - d3.min(v, function(d) { return xScale(d.key); }) + xScale.rangeBand(),
//                    hight: yScale(d3.max(v, function(d) {return d.value}))
//                }
//            })
//            .entries(fildata);
//    }

	drawChart = function(layoutContainer, redraw) {
		var chartWidth = _representation.imageWidth + "px";
        var chartHeight = _representation.imageHeight + "px";
        if (_representation.resizeToWindow) {
            chartWidth = "100%";
            chartHeight = "100%";
        }
            
        if (redraw) {
            div = d3.select("#"+containerID)
                .attr("width", chartWidth)
                .attr("height", chartHeight);
        } else {
            div = d3.select("#"+layoutContainer).append("div")
            .attr("id", containerID)
            .attr("class", "knime-svg-container")
            .style("width", chartWidth)
            .style("height", chartHeight)
            .style("min-width", MIN_WIDTH + "px")
            .style("min-height", MIN_HEIGHT + "px");
        }
        
        var w_h = getChartDimensions();
        chartWidth = w_h[0];
        chartHeight = w_h[1];
        
        var svg = div.append("svg")
            .attr("id", "chart_svg")
            .attr("width", chartWidth  + "px")
            .attr("height",  chartHeight  + "px")
            //.style("min-width", MIN_WIDTH + "px")
            //.style("min-height", MIN_HEIGHT + "px")
            .style("box-sizing", "border-box")
            .on("mousemove", function(){
                var coordinates = [0, 0];
                coordinates = d3.mouse(this);
                x = coordinates[0];
                y = coordinates[1];
            });
        
        var svgHeight = chartHeight,
            svgWidth = chartWidth;

        xScale = d3.scale.ordinal()
            .domain(dataset_for_vis.map(function(d){return d.key}))
            .rangeBands([0, svgWidth - margin.left - margin.right], .2);
        
        //create a pseudo infinity value to draw infinity values correctly
        pseudo_infinity = maxValCalc(dataset_for_vis).toFixed(3);
        
        yScale = d3.scale.linear()
            .domain([0, pseudo_infinity])
            .range([(svgHeight - margin.top - margin.bottom),0]),
        
        //currently the colors of clusters will be repeated and have no meaning
        color = d3.scale.category10()
                .domain(function(){
                    var arr = [];
                    for (var j = 0; j < dataset_for_vis.length/2; j++){
                        arr.push(i);
                    }
                    return arr;
                });

        var yAxis = d3.svg.axis()
            .scale(yScale)
            .orient("left")
            .tickSize(Math.min(3, svgWidth/500));

        var vis_hist = svg.append("g")
            .attr("class", "barchartGroup")
            .attr("transform", "translate(" + [(margin.left), margin.top] + ")");

        var axis = svg.append("g")
            .attr("transform", "translate(" + [(0.9*margin.left), margin.top] + ")")
            .attr("class", "axisY knime-axis knime-y")
            .call(yAxis);
                    
        if (allNoise) {
            d3.selectAll(".axisY").selectAll("text").remove();
        }

        var ticks = d3.selectAll(".tick")
            .classed("knime-tick", true);
        ticks.selectAll("line")
            .classed("knime-tick-line", true);
        ticks.selectAll("text")
            .classed("knime-tick-label", true);        
        
        //plot histogram bars
        var rec = vis_hist
            .selectAll("rect")
            .data(dataset_for_vis)
            .enter().append("rect")
            .attr("class", "barchart")
            .attr("id", function(d){return "barchart" + d.cluster})
            .attr("x", function (d,i) {
                return xScale(d.key);})
            .attr("y", function(d){
                return yScale(d.value);})
            .attr("width", xScale.rangeBand())
            .attr("height", function (d) {
                return (svgHeight  -margin.top - margin.bottom) - yScale(d.value);
            })
            .attr("fill", function (d) {
                return colorFill(d.cluster)
            })
            .on("mouseover", function() { rectTitle.style("display", null); })
            .on("mouseout", function() { rectTitle.style("display", "none"); })
            .on("mousemove", mousemove)

        if (_representation.enableSelection) {
            rec.on("click", showSelection)
        }

// The code is not finished yet: overlay on the clusters
//        var overlay_data = d3.nest()
//                .key(function(d) {return d.cluster; })
//                //.rollup(function(v) {return d3.min(v, function(d) { return xScale(d.key); }); })
//                .rollup(function(v) {
//                    return {
//                        xVal: d3.min(v, function(d) { return xScale(d.key); }),
//                        width: d3.max(v, function(d) { return xScale(d.key); }) - d3.min(v, function(d) { return xScale(d.key); }) + xScale.rangeBand(),
//                        hight: yScale(d3.max(v, function(d) {return d.value}))
//                    }
//                })
//                .entries(dataset_for_vis.filter(function(d) {return d.cluster >= 0}));
        
//        var overlayClusters = vis_hist.selectAll("rectOverlay")
//              .data(overlayData(dataset_for_vis))
//              .enter().append("rect")
//              .attr("class", "overlay")
//              .attr("id", function (d,i){
//                  d.cluster = parseInt(d.key);
//                  return "overlayRec" + d.key})
//              .attr("width", function(d) {return d.values.width;})
//              .attr("height", function(d) {return (svgHeight  - margin.top - margin.bottom) - d.values.hight})
//              .attr("x", function(d) {return d.values.xVal;})
//              .attr("y", function(d) {return d.values.hight;})
//              .on("mouseover", function() { rectTitle.style("display", null); })
//              .on("mouseout", function() { rectTitle.style("display", "none"); })
//              .on("mousemove", mousemove)
//        
//        if (_representation.enableSelection) {
//            overlayClusters.on("click", showSelection)
//        }
        
        //labels of clusters
        var rectTitle = vis_hist.append("g")
            .attr("class", "label knime-label")
            .style("display", "none");
        
        rectTitle.append("rect")
            .attr("width", 70)
            .attr("height", 17)
        
        rectTitle.append("text")
            .attr("id", "textID")
            .attr("dy", ".90em")
            .attr("dx", ".40em");
            
        function mousemove() {
            var xPos = d3.mouse(this)[0],
                yPos = d3.mouse(this)[1];
            var val = d3.select(this).data()[0];
            rectTitle
                .attr("transform", "translate(" + xPos + "," + (yPos - 20) + ")")
            rectTitle.select("text")
                .text(function() {
                    if (val.cluster >= 0) {
                        return "Cluster " + val.cluster
                    } 
                    return "Noise"})
            rectTitle.select("rect")
                .attr("width", d3.select("#textID")[0][0].getBBox().width*1.3)
        }
        
        var selectionLinesArea = svg.append("g")
            .attr("transform", "translate(" +[margin.left, svgHeight - margin.bottom]+ ")")
        
        //perfoms selection of clusters and publish this selestion on point-level
        function showSelection (d, i) {
            var index = selection.indexOf(parseInt(d.cluster));
            if (index < 0) {
                
                //if shift is pressed, add to the current selection, if not - start anew selection
                if (d3.event.shiftKey) {
                    //add to selection array only clusters, not noise
                    if (d.cluster >= 0) {
                        selection.push(parseInt(d3.select(this).data()[0].cluster));
                    } 
                } else {
                    d3.selectAll(".selection").remove();
                    selection = [];
                    selection.push(parseInt(d3.select(this).data()[0].cluster));
                }
                
                //selection lines under the cluster bars
                drawSelection();
                publishSelection();
            } else {
                //delete already selected element from selection array
                selection.splice(index,1);
                d3.select("#sel" + d.cluster).remove();
                publishSelection();
            }
            console.log("Clusters selected: " + selection)
        }
        
        if (redraw) {
            drawSelection();
        }
        
        function drawSelection() {
            selectionLinesArea.selectAll("rect")
                    .data(selection)
                    .enter().append("rect")
                    .attr("class", "selection knime-selected")
                    .attr("id", function (d,i){return "sel" + d})
                    .attr("x", function(d) {
                        return d >= 0 ? xScale(d3.select("#barchart"+d).data()[0].key) : 0;
                    })
                    .attr("y", 2)
                    .attr("height", 5)
                    .attr("width", function (d) {
                        var select_data = d3.selectAll("#barchart"+d).data();
                        var output = xScale(select_data[select_data.length - 1].key) + xScale.rangeBand() - xScale(select_data[0].key);
                        return d >= 0? output : 0;
                    })
                    .attr("fill", "orange")
        }

        var y_corr = _value.epsPrime;

        var drag = d3.behavior.drag()
            .on("drag", function(d,i) {
                //correction not to leave the boarders of histogram
                if(y > svgHeight -margin.bottom){
                    y_corr = svgHeight - margin.bottom;
                } else if (y < margin.top){
                    y_corr = margin.top;
                } else {
                    y_corr = y;
                }
                
                //clean selection when the level of a eps-prime line change
                d3.select(".selection").remove();
                selection = []
                publishSelection();
                
                //update clusters: colors and labels of points
                changeBarsAndLine(this);
                document.getElementById("chartEpsilonPrimeText").value = yScale.invert(y_corr - margin.top).toFixed(3);
                _value.epsPrime = yScale.invert(y_corr - margin.top).toFixed(3);
                

// The code is not finished yet: overlay on the clusters
                //overlayClusters.data(overlayData(d3.selectAll("#barchart").data()[0]));
//                d3.selectAll(".barchart").data().forEach(function(d) {
//                    d.cluster = parseInt(d.cluster)
//                });
//                overlayClusters.exit().remove()
//                overlayClusters.data(overlayData(d3.selectAll(".barchart").data()))
//                    .attr("id", function (d,i){
//                          d.cluster = parseInt(d.key);
//                          return "overlayRec" + d.key})
//                      .attr("width", function(d) {return d.values.width;})
//                      .attr("height", function(d) {return (svgHeight  - margin.top - margin.bottom) - d.values.hight})
//                      .attr("x", function(d) {return d.values.xVal;})
//                      .attr("y", function(d) {return d.values.hight;})
//                overlayClusters.exit().remove()
                //console.log("from barchart", a)
                
            });
            
        if (!allNoise) {
            var epsLine = vis_hist
                .append("rect")
                .attr("id", "epsPrimeLine")
                .attr("transform", "translate(" + [-0.1 * margin.left, yScale(_value.epsPrime)] + ")")
                .attr("width", svgWidth - margin.left)
                .attr("height", Math.min(3, svgHeight/300))
                .style("fill", "red")
                .style("cursor","ns-resize")
              .call(drag);

            var epsText = vis_hist
                .append("text")
                .attr("id", "epsPrimeText")
                .attr("class", "knime-tooltip knime-tooltip-value")
                .attr("y", setYforEpsText(_value.epsPrime, yScale(_value.epsPrime) - margin.bottom))
                .attr("x", svgWidth - 1.9 * margin.right)
                .attr("dy", ".35em")                
                .text(_value.epsPrime)  
        
        }


// The code is not finished yet: implementation of the information scrolling window for binned data
//        if (binnedWarning) {
//            d3.select("#chart_svg")
//                .append("text")
//                .attr("id","binnedWarning")
//                .attr("x", margin.left + 10)
//                .attr("y", margin.top*2)
//                .attr("fill", "red")
//                .style("font-size", "20px")
//                .text("Warning: the data is binned")
//            
//            //code is reused from http://bl.ocks.org/billdwhite/36d15bc6126e6f6365d0
//            var scrollSVG = d3.select(".viewport").append("svg")
//                .attr("class", "scroll-svg");
//
//            var chartGroup = scrollSVG.append("g")
//                .attr("class", "chartGroup")
//            
//            chartGroup.append("rect")
//                .attr("fill", "#FFFFFF");
//            
//            var rowEnter = function(rowSelection) {
//                rowSelection.append("rect")
//                    .attr("rx", 3)
//                    .attr("ry", 3)
//                    .attr("width", "250")
//                    .attr("height", "24")
//                    .attr("fill-opacity", 0.25)
//                    .attr("stroke", "#999999")
//                    .attr("stroke-width", "2px");
//                rowSelection.append("text")
//                    .attr("transform", "translate(10,15)");
//            };
//            var rowUpdate = function(rowSelection) {
//                rowSelection.select("rect")
//                    .attr("fill", function(d) {
//                        return colorScale(d.id);
//                    });
//                rowSelection.select("text")
//                    .text(function (d) {
//                        return (d.index + 1) + ". " + d.label;
//                    });
//            };
//
//            var rowExit = function(rowSelection) {
//            };
//            
//            var clusterGrouping = d3.nest()
//                .key(function(d) { return d.cluster; })
//                .rollup(function(v) { return v.length; })
//                .entries(d3.select(".barchart"+d).data()[0].incl_points);
//            
//            virtualScroller.data(clusterGrouping);
//
//            chartGroup.call(virtualScroller);
//        }
        
    }
    
    //recalculate clusters based on new eps prime value
    clusterCount = function(reachD, coreD, eps){ 
        if (reachD > eps) {
            if (coreD <= eps) {
                return ++cluster_id;
            }
            return -1;
        } else {
            return cluster_id;
        }
    }
    
    colorFill = function(cluster_id) {
        if (cluster_id >= 0) {
            return color(cluster_id);
        }
        return "#050d11";
    }
    
    updateTitle = function() {
		var oldTitle = _value.chartTitle;
		_value.chartTitle = document.getElementById("chartTitleText").value;
		if (_value.chartTitle !== oldTitle || typeof _value.chartTitle !== typeof oldTitle) {
			d3.select("#title").text(_value.chartTitle);
		}
	};
    
    updateSubtitle = function() {
		var oldTitle = _value.chartSubtitle;
		_value.chartSubtitle = document.getElementById("chartSubtitleText").value;
		if (_value.chartSubtitle !== oldTitle || typeof _value.chartSubtitle !== typeof oldTitle) {
			d3.select("#subtitle").text(_value.chartSubtitle);
		}
	};
    
    //If eps line has reached the top level of the image, plot the caption under the line
    setYforEpsText = function(eps, value){
        if (eps <= pseudo_infinity && eps >= 0.97*pseudo_infinity){
               return value + 0.8*margin.top;
            } 
            return value;
    }
    
    //update position of the eps line, text, and parameters of barchart
    changeBarsAndLine = function(line) {
        d3.select(line)
            .attr("transform", "translate(" + [-0.1 * margin.left, yScale(_value.epsPrime)] + ")")
        d3.select("#epsPrimeText")
            .attr("y", setYforEpsText(_value.epsPrime, yScale(_value.epsPrime) - margin.bottom))
            .text(_value.epsPrime)
        cluster_id = -1;
        d3.selectAll(".barchart").attr("fill", function (d, i) {
                if (binnedWarning) {
                    d.incl_points.forEach(function(d,i) {
                        d.cluster = clusterCount(d.value, d.coredist, _value.epsPrime);
                    })
                    d.cluster = majorityCluster(d.incl_points);
                } else {
                    d.cluster = clusterCount(d.value, d.coredist, _value.epsPrime);
                }

                return colorFill(d.cluster);
            })
            .attr("id", function(d){return "barchart" + d.cluster})
    }
    
    //update eps-prime in the text field
    updateEpsPrime = function() {
		var oldTitle = _value.epsPrime;
        if (document.getElementById("chartEpsilonPrimeText").value > pseudo_infinity) {
            _value.epsPrime = pseudo_infinity;
            tooBigEpsPr = true;
        } else {
            _value.epsPrime = document.getElementById("chartEpsilonPrimeText").value;
        }
		if (_value.epsPrime !== oldTitle || typeof _value.epsPrime !== typeof oldTitle) {
            console.log(_value.epsPrime)
            changeBarsAndLine("#epsPrimeLine");
// The code is not finished yet: overlay on the clusters
//            var overlays = d3.selectAll(".overlay").data(overlayData(dataset_for_vis))
            tooBigEpsMessage();
		}
        publishSelection();
	};
	
	setTitle = function() {
        d3.select("#chart_svg")
            .append("text")
            .attr("id","title")
            .attr("class", "knime-title")
            .attr("x", margin.left + 20)
            .attr("y", margin.top - 16)
            .text(_value.chartTitle)
	}
    
    setSubtitle = function() {
        d3.select("#chart_svg")
            .append("text")
            .attr("id","subtitle")
            .attr("class", "knime-subtitle")
            .attr("x", margin.left + 20)
            .attr("y", margin.top - 2)
            .text(_value.chartSubtitle)
	}
    
    saveSelectedKeys = function () {
        _value.selectedKeys = []
        selection.forEach(function(d,i){
            d3.selectAll("#barchart"+d)
                .forEach(function(d) {
                    d.forEach(function(d){
                        _value.selectedKeys.push(d.id.replace(/_/i, ""));
                    })
            })
        })
    }
    
    drawControls = function(layoutContainer) {
		
		if (!knimeService) {
			// TODO: error handling?
			return;
		}
		
		// -- Buttons --
		if (_representation.displayFullscreenButton) {
			knimeService.allowFullscreen();
		}

        var selectionEnabled = _representation.enableSelection;
        var recSelEnabled = _representation.enableRectangleSelection;
        
        if (selectionEnabled) {
        	var selectionButtonClicked = function() {
                d3.selectAll(".selection").remove();
                selection = [];
                publishSelection();
        	}
        	knimeService.addButton('scatter-mouse-mode-select', 'minus', 'Clean Selection', selectionButtonClicked);
        }
        
        if (binnedWarning) {
            knimeService.setWarningMessage("The data points are binned.");
        }
        
        if (!_representation.enableViewConfiguration) return;
        var pre = false;
        
        if (_representation.enableEpsilonPrimeChange) {
            var epsPrimeText = knimeService.createMenuTextField('chartEpsilonPrimeText', _value.epsPrime, updateEpsPrime, false);
            knimeService.addMenuItem('Epsilon Prime:', 'arrows-v', epsPrimeText);
	    }
        
        if (_representation.enableTitleChange) {
	    	pre = true;
	    	if (_representation.enableTitleChange) {
	    		var chartTitleText = knimeService.createMenuTextField('chartTitleText', _value.chartTitle, updateTitle, false);
	    		knimeService.addMenuItem('Chart Title:', 'header', chartTitleText);
                var chartSubtitleText = knimeService.createMenuTextField('chartSubtitleText', _value.chartSubtitle, updateSubtitle, false);
	    		knimeService.addMenuItem('Chart Subtitle:', 'header', chartSubtitleText, null, knimeService.SMALL_ICON);
	    	}
            
	    }
        
        if (knimeService.isInteractivityAvailable()) {
	    	if (pre) {
	    		knimeService.addMenuDivider();
	    	}
	    	if (_representation.enableSelection) {
	    		var pubSelIcon = knimeService.createStackedIcon('check-square-o', 'angle-right', 'faded left sm', 'right bold');
				var pubSelCheckbox = knimeService.createMenuCheckbox('publishSelectionCheckbox', _value.publishSelection, function() {
                    //this.checked =  _value.publishSelection;
					if (this.checked) {
						_value.publishSelection = true;
						knimeService.setSelectedRows(_representation.keyedDataset.id, getSelection());
					} else {
						_value.publishSelection = false;
					}
				});
				knimeService.addMenuItem('Publish selection', pubSelIcon, pubSelCheckbox);
                pre = true;
	    	}
        }
    }
    
    publishSelection = function() {
		if (_value.publishSelection) {
			knimeService.setSelectedRows(_representation.keyedDataset.id, getSelection());
		}
	};
    
    getSelection = function() {
		var selectionsArray = [];
        if (binnedWarning) {
            for (var i = 0; i < selection.length; i++) {
                var select_data = d3.selectAll("#barchart"+selection[i]).data();
                for (var j = 0; j < select_data.length; j++) {
                    for (var k = 0; k < select_data[j].incl_points.length; k++) {
                        if (selection.indexOf(parseInt(select_data[j].incl_points[k].cluster)) >= 0) {
                            selectionsArray.push(select_data[j].incl_points[k].key)
                        }
                    }
                }
            }
        } else {
            for (var i = 0; i < selection.length; i++) {
                var select_data = d3.selectAll("#barchart"+selection[i]).data();
                for (var j = 0; j < select_data.length; j++) {
                    selectionsArray.push(select_data[j].key)
                }
            }
        }

        //console.log(selectionsArray)
		if (selectionsArray.length == 0) {
			return null;
		}
		return selectionsArray;
	};
    

   
	optics.validate = function() {
		return true;
	}
	
	optics.getComponentValue = function() {
        
        if (selection.length > 0){
            saveSelectedKeys();
        }
        if (initEpsPrime != _value.epsPrime) {
            _value.calcEpsPrimeMean = false;
            _value.calcEpsPrimeMedian = false;
            _value.manualEpsPrime = true;
            _value.epsCalcMethod = "manualEpsPrime";
        }
        
        if (!_representation.wasRedrawn) {
            _representation.wasRedrawn = true;
        }
        
		return _value;
	}
	
	

	optics.getSVG = function() {
		var svg = d3.select("svg");
		if (!svg.empty()) {
			var svgElement = d3.select("svg")[0][0];
			knimeService.inlineSvgStyles(svgElement);
			// Return the SVG as a string.
			return (new XMLSerializer()).serializeToString(svgElement);
		} else {
			var w = _representation.imageWidth;
	        var h = _representation.imageHeight;
			return '<svg height="' + h + '" width="' + w + '"><text x="0" y="15" fill="red">Error: No data available</text></svg>';
		}
	}

	return optics;

}());