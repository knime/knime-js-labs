dataExplorerNamespace = function() {
	
	var view = {};
	var _representation, _value;
	var knimeTable = null;
    var previewTable = null;
    var previewDataTable = null;
    var nominalTable = null;
    var nominalDataTable = null;
	var dataTable = null;
	var selection = {};
	var allCheckboxes = [];
	var initialized = false;
    var svgWidth = 120;
    var svgHeight = 30;
    var svgWsmall = 120;
    var svgHsmall = 30;
    var svgWbig = 500;
    var svgHbig = 300;
    var xScale, yScale, xScaleNom;
    var xAxis, yAxis, barsScale;
    var margin = {top:0.8*svgHeight, left: 0.5*svgWidth, bottom: 0.7*svgHeight, right:0.2*svgHeight};
    var content;
    var hideUnselected = false;
    var histSizes = [];
    var histNomSizes = [];
    var histCol;
    var histColNom;
    var pageLength;
    var pageLengths;
    var order = [];
    var buttons = [];
    var respOpenedNum = new Map();
    var respOpenedNom = new Map();
    var showWarningMessage = true;
    var warningMessageCutOffValues = "Some nominal values were cut off by the number of unique nominal values. Change settings in the dialog window.";
    var prevRowsPerPage;
    var openedNomRows = 0;
    var openedNumRows = 0;
    
    
	
	//register neutral ordering method for clear selection button
	$.fn.dataTable.Api.register('order.neutral()', function () {
	    return this.iterator('table', function (s) {
	        s.aaSorting.length = 0;
	        s.aiDisplay.sort( function (a,b) {
	            return a-b;
	        });
	        s.aiDisplayMaster.sort( function (a,b) {
	            return a-b;
	        } );
	    } );
	});
	
	view.init = function(representation, value) {
		if (!representation || !representation.statistics) {
			$('body').append("p").append("Error: No data available");
			return;
		}
		_representation = representation;
	    _value = value;
	    if (parent && parent.KnimePageLoader) {
			_init();
		} else {
			$(document).ready(function() {
                _init();
            });
		}
	}
	
	_init = function() {
		var tabs = $('<div />').attr('id', 'tabs').attr('class', 'knime-tab').appendTo('body');
        var listOfTabNames = $('<ul />').attr("class", "nav nav-tabs").attr('role', 'tabList').appendTo(tabs);
        content = $('<div />').attr('class', 'tab-content').appendTo(tabs);
        
        $('<li class="active"><a href="#tabs-knimeDataExplorerContainer" data-toggle="tab" aria-expanded="true" class="knime-label">' + 'Numeric' + '</a></li>').appendTo(listOfTabNames);
        
        $('<li class=""><a href="#tabs-knimeNominalContainer" data-toggle="tab" aria-expanded="false" class="knime-label">' + 'Nominal' + '</a></li>').appendTo(listOfTabNames);

        $('<li class=""><a href="#tabs-knimePreviewContainer" data-toggle="tab" aria-expanded="false" class="knime-label">' + 'Data Preview' + '</a></li>').appendTo(listOfTabNames);
        
        
		drawNumericTable();
        drawDataPreviewTable();
        drawNominalTable();

        _setControlCssStyles();
        
        $('a[data-toggle="tab"]').on( 'shown.bs.tab', function (e) {
            var table = $.fn.dataTable.tables( {visible: true, api: true} );
            table.columns.adjust().responsive.recalc();
            
            //artificial action of the empty table to represent that no data avaliable in the table correctly
            if (table.rows()[0].length == 0) {
                table.order([0, 'asc']).draw(true);
            }
        });
	}
    
    drawNominalTable = function() {

        try {
            nominalTable = new kt();
			nominalTable.setDataTable(_representation.nominal);
            
			var wrapper = $('<div id="tabs-knimeNominalContainer">').attr("class", "tab-pane knime-table-container");
			content.append(wrapper);
            
			if (_representation.title != null && _representation.title != '') {
				wrapper.append('<h1 class="knime-title">' + _representation.title + '</h1>')
			}
			if (_representation.subtitle != null && _representation.subtitle != '') {
				wrapper.append('<h2 class="knime-subtitle">' + _representation.subtitle + '</h2>')
			}
			var table = $('<table id="knimeNominal" class="table table-striped table-bordered knimeDataTable knime-table" width="100%">');
			wrapper.append(table);
            
            if (_representation.jsNominalHistograms != null) {
                for (var i = 0; i < _representation.jsNominalHistograms.length; i++) {
                    _representation.jsNominalHistograms[i].bins.sort(function(x,y){
                        return d3.descending(x.count, y.count);
                    })
                }   
            }

			var colArray = [];
			var colDefs = [];

            //column names
            colArray.push({
                'title': 'Column', 
                'orderable': true,
                'className': 'no-break knime-table-cell knime-string'
            });

            
            if (_representation.enableSelection) {
				var all = _value.selectAll;
				colArray.push({'title': 
                               /*'<input name="select_all" value="1" id="checkbox-select-all" type="checkbox"' + (all ? ' checked' : '')  + ' />'*/
                               'Exclude Column'})
				colDefs.push({
					'targets': 1,
					'searchable':false,
					'orderable':false,
					'className': 'dt-body-center knime-table-cell knime-boolean',
					'render': function (data, type, full, meta) {
						//var selected = selection[data] ? !all : all;
						setTimeout(function(){
							var el = $('#checkbox-select-all').get(0);
							/*if (all && selection[data] && el && ('indeterminate' in el)) {
								el.indeterminate = true;
							}*/
						}, 0);
						return '<input type="checkbox" name="id[]" class="knime-boolean"'
							+ (selection[full[0]] ? ' checked' : '')
							+' value="' + $('<div/>').text(full[0]).html() + '">';
					}
				});
			}
            
            for (var i = 0; i < nominalTable.getColumnNames().length; i++) {

                //check if we need to add last 3 columns of freq values

				var colType = nominalTable.getColumnTypes()[i];
				var knimeColType = nominalTable.getKnimeColumnTypes()[i];
                
                //check if freq columns are included
                if (colType == "string") {
                    if (!_representation.enableFreqValDisplay) {
                        continue;
                    }
                }
				var colDef = {
					'title': nominalTable.getColumnNames()[i],
					'orderable' : isColumnSortable(colType),
                    'searchable': isColumnSearchable(colType),
                    'className': 'knime-table-cell'
				}
                
                if ( _representation.maxNomValueReached.length != 0) {
                    colDefs.push({
                        "targets": 3,
                        "createdCell": function (cell, cellData, rowData, rowIndex, colIndex) {
                            if (_representation.maxNomValueReached.indexOf(rowData[0]) > -1) {
                                $(cell).css('backgroundColor', '#f2c4c4')
                            }
                        }, 
                        'render': function (data, type, full, meta) {
                            if (_representation.maxNomValueReached.indexOf(full[0]) > -1) {
                                return ">"+data;
                            }
                            return data;
                        }
                    })
                }
                
                
				if (_representation.displayMissingValueAsQuestionMark) {
					colDef.defaultContent = '<span class="knime-missing-value-cell">?</span>';
				}
                
                if (colType == 'number') {
                    if (nominalTable.getKnimeColumnTypes()[i].indexOf('double') > -1) {
                        colDef.className += ' knime-double';
                        if (_representation.enableGlobalNumberFormat) {
                            colDef.render = function (data, type, full, meta) {
                                if (!$.isNumeric(data)) {
                                    return data;
                                }
                                return isInt(data) ? data : Number(data).toFixed(_representation.globalNumberFormatDecimals);
                            }
                        }
                    } else {
                        colDef.className += ' knime-integer';
                    }
                }

                if (colType == "string") {
                    colDef.className += ' knime-string';
                    if (nominalTable.getKnimeColumnTypes()[i].indexOf('String') > -1) {
                        colDef.render = function(data, type, full, meta) {
                            if (data != null) {
                                var nomList1 = "";
                                var nomList2 = "";
                                var switchToList2 = false;
                                var comma = ''

                                for (var j = 0; j < data.length; j++) {
                                    if (data[j] == _representation.otherErrorValuesNotation) {
                                        switchToList2 = true;
                                        continue;
                                    }
                                    if (switchToList2) {
                                        nomList2 = nomList2.concat("<span class='freqSpan'>", data[j], j == data.length-1? "" : ", ", "</span>");
                                    } else {
                                        nomList1 = nomList1.concat("<span class='freqSpan'>", data[j], j == data.length-1? "" : ", ", "</span>");
                                    }

                                }
                                var testDiv = document.createElement("div");
                                var testsvg = d3.select(testDiv).attr("class", "testDiv")
                                    .append("svg")
                                    .attr("height", 20)
                                    .attr("width", 30)

                                testsvg.append("text")
                                    .attr("class", "knime-label")
                                    .attr("x", 2)
                                    .attr("y", 14)
                                    .style("fill", "red")
                                    .text(function() {
                                        if (nomList2 != '') {
                                            comma = ", ";
                                        }
                                        return _representation.otherErrorValuesNotation + comma;
                                    })

                                testsvg.append("rect")
                                    .attr("class", "knime-label")
                                    .attr("height", 20)
                                    .attr("width", 27)
                                    .attr("fill", "white")
                                    .attr("opacity", 0.01)
                                    .append("title")
                                    .text(warningMessageCutOffValues)

                                if (switchToList2) {
                                    return $('<div/>').append(nomList1).append(testDiv).append(nomList2).html();
                                }
                                return $('<div/>').append(nomList1).html();
                            }

                        }
                    }
                }

				colArray.push(colDef);
			}
            
            xScaleNom = d3.scale.ordinal(), 
            yScale = d3.scale.linear();
            
            var colDef = {
                'title': 'Frequency Bar Chart',
                'orderable': false, 
                'searchable': false,
                'className': 'knime-table-cell knime-image knime-svg'
            }
            
            if (_representation.displayMissingValueAsQuestionMark) {
                colDef.defaultContent = '<span class="knime-missing-value-cell">?</span>';
            }
            
            histColNom = colArray.length ;
            
            if (_representation.jsNominalHistograms != null) {
                _representation.jsNominalHistograms.forEach(function(d) {histNomSizes.push(d.bins.length)});
                
                colDef.render = function(data, type, full, meta) {
                    svgHeight = svgHsmall + margin.top;
                    svgWidth = svgWsmall;

                    //window.alert(full);

                    xScaleNom.rangeBands([0, svgWidth])
                        .domain(full[histColNom].bins.map(function(d){return d.value}));
                    yScale.range([svgHsmall, 0])
                        .domain([0, full[histColNom].maxCount]);

                    var histDiv = document.createElement("div");

                    var svg = d3.select(histDiv).attr("class", "histNom")
                        .append("svg")
                        .attr("height", svgHeight)
                        .attr("width", svgWidth)
                        .attr("class", "svg_hist_nom knime-image knime-svg")
                        .attr("id", "svgNom"+full[histColNom].colIndex);
                    
                    if (_representation.maxNomValueReached.indexOf(full[0]) < 0) {

                        var bar_group = svg.append("g")
                            .attr("transform", "translate(" + [0 , margin.top] + ")")
                            .attr("class", "bars")
                            .attr("id", "svgNom"+meta.row);

                        var bars = bar_group.selectAll("rect")
                            .data(full[histColNom].bins)
                                .enter()
                            .append("rect")
                            .attr("class", "rect"+full[histColNom].colIndex)
                            .attr("x", function (d) {return xScaleNom(d.value);})
                            .attr("y", function(d) {return yScale(d.count);})
                            .attr("width", function(d) {return xScaleNom.rangeBand()})
                            .attr("height", function(d){return svgHsmall - yScale(d.count);})
                            .attr("fill", "#547cac")
                            .attr("stroke", "black")
                            .attr("stroke-width", "1px")
                            .append("title")
                            .text(function(d, i) { return d.value+": "+d.count; });

                    } else {
                        var errorMessage = ["Not all nominal", "values calculated."]
                        var errorText = svg.selectAll("text").data(errorMessage)
                                .enter()
                            .append('text')
                            .attr("x", 0)
                            .attr("y", function(d, i){return svgHeight/3*(1 + i)})
                            .text(function(d){return d})
                    }
  
                    return $('<div/>').append(histDiv).html();
                }
            }

            colArray.push(colDef);
            
            pageLength = _representation.initialPageSize;
			if (_value.pageSize) {
				pageLength = _value.pageSize;
			}
            prevRowsPerPage = pageLength;
			pageLengths = _representation.allowedPageSizes;
			if (_representation.pageSizeShowAll) {
				var first = pageLengths.slice(0);
				first.push(-1);
				var second = pageLengths.slice(0);
				second.push("All");
				pageLengths = [first, second];
			}
            
			if (_value.currentOrder) {
				order = _value.currentOrder;
			}

			var firstChunk = getDataSlice(0, _representation.initialPageSize, nominalTable);
            
			var searchEnabled = _representation.enableSearching || (knimeService && knimeService.isInteractivityAvailable());
            
			nominalDataTable = $('#knimeNominal').DataTable( {
                'columns': colArray,
				'columnDefs': colDefs,
				'order': order,
                //'retrieve': true,
				'paging': _representation.enablePaging,
				'pageLength': pageLength,
				'lengthMenu': pageLengths,
				'lengthChange': _representation.enablePageSizeChange,
				'searching': searchEnabled,
				'ordering': _representation.enableSorting,
				'processing': true,
				'deferRender': !_representation.enableSelection,
				'data': firstChunk,
				'buttons': buttons,
                'responsive': true,
                'scrollX':false,
				'fnDrawCallback': function() {
					if (!_representation.displayColumnHeaders) {
						$("#knimeNominal thead").remove();
				  	}
					if (searchEnabled && !_representation.enableSearching) {
						$('#knimeNominal_filter').remove();
                    }
                    _setDynamicCssStyles();
                }, 
                "oLanguage": { "sEmptyTable": "No nominal columns in the dataset." } 
			});
            
            
            drawControls("knimeNominal", nominalTable, nominalDataTable);
            
            if (_representation.enableSelection) {
				// Handle click on "Select all" control
				var selectAllCheckbox = $('#checkbox-select-all').get(0);
				if (selectAllCheckbox) {
					if (selectAllCheckbox.checked && ('indeterminate' in selectAllCheckbox)) {
						selectAllCheckbox.indeterminate = _value.selectAllIndeterminate;
					}
					selectAllCheckbox.addEventListener('click', function() {
						selectAll(this.checked);
					});
				}

				// Handle click on checkbox to set state of "Select all" control
				$('#knimeNominal tbody').on('change', 'input[type="checkbox"]', function() {
					//var el = $('#checkbox-select-all').get(0);
					//var selected = el.checked ? !this.checked : this.checked;
					// we could call delete _value.selection[this.value], but the call is very slow 
					// and we can assume that a user doesn't click on a lot of checkboxes
					selection[this.value] = this.checked;
					
					if (this.checked) {
						/*if (knimeService && knimeService.isInteractivityAvailable() && _value.publishSelection) {
							knimeService.addRowsToSelection(_representation.table.id, [this.value], selectionChanged);
						}*/
					} else {
						// If "Select all" control is checked and has 'indeterminate' property
						if(selectAllCheckbox && selectAllCheckbox.checked && ('indeterminate' in selectAllCheckbox)){
							// Set visual state of "Select all" control as 'indeterminate'
							selectAllCheckbox.indeterminate = true;
							_value.selectAllIndeterminate = true;
						}
						if (hideUnselected) {
							nominalTable.draw('full-hold');
						}
						/*if (knimeService && knimeService.isInteractivityAvailable() && _value.publishSelection) {
							knimeService.removeRowsFromSelection(_representation.table.id, [this.value], selectionChanged);
						}*/
					}
				});
			}
			
			//load all data
			setTimeout(function() {
				var initialChunkSize = 10;
				addDataToTable(_representation.initialPageSize, initialChunkSize, nominalTable, nominalDataTable, "knimeNominal");
			}, 0);
            
            
            
            nominalDataTable.on("responsive-display", function(e, datatable, row, showHide, update) {
                
                var data = row.data()[histColNom];
                var pageLength = datatable.page.info().length < 0? datatable.page.info().recordsTotal : datatable.page.info().length;
                //var pageNum = Math.floor(data.colIndex / pageLength);
                
                openedNomRows = updateResponsiveContainer(respOpenedNom, datatable.page.info().page, data.colIndex, showHide, update, openedNomRows, pageLength)

                if (_representation.maxNomValueReached.indexOf(data.columnName) < 0) {
                
                    var textScale = d3.scale.linear()
                        .range([8, 11])
                        .domain([d3.max(histNomSizes), 16]);

                    var charecterScale = d3.scale.linear()
                    
                    if (d3.max(histNomSizes) >= 13) {
                        charecterScale.range([3, 7]).domain([d3.max(histNomSizes), 13])
                    } else {
                        charecterScale.range([7, 10]).domain([12, d3.max(histNomSizes)])
                    }
                        
                    //when responsive is opened it creates an additional div of the same class right under its original one
                    var bigHist = $(".histNom")[data.colIndex % pageLength + respOpenedNom.get(datatable.page.info().page).col.indexOf(data.colIndex) + 1];
                    
                    svgWidth = svgWbig;
                    svgHeight = svgHbig;
                    var svgBigHist = d3.select(bigHist).select("#svgNom"+data.colIndex)[0][0]

                    xScaleNom.rangeBands([0, svgWidth - margin.left - margin.right])
                        .domain(data.bins.map(function(d){return d.value}));
                    yScale.range([svgHeight - 2*margin.top - margin.bottom, 0])
                        .domain([0, data.maxCount]);

                    var svg = d3.select(svgBigHist).attr("width", svgWidth).attr("height", svgHeight);

                    var bar_group = svg.selectAll(".bars")
                        .attr("transform", "translate("+[margin.left , margin.top]+")");

                    var bars = svg.selectAll(".bars")
                        .selectAll(".rect"+data.colIndex)
                        .data(data.bins)
                        .attr("x", function (d) {return xScaleNom(d.value);})
                        .attr("y", function(d) {return yScale(d.count);})
                        .attr("width", function(d) {return xScaleNom.rangeBand();})
                        .attr("height", function(d){return svgHeight - 2*margin.top - margin.bottom -  yScale(d.count);})

                    var text_group = svg.append("g")
                        .attr("class", "caption knime-label")
                        .attr("transform", "translate(" + [margin.left , margin.top] + ")")
                        .attr("id", "id"+data.colIndex);

                    var texts = text_group.selectAll("text")
                        .data(data.bins)
                        .enter()
                        .append("text")
                        .attr("x", function (d) {return xScaleNom(d.value) + xScaleNom.rangeBand()/2;})
                        .attr("y", function(d) {return yScale(d.count) - 2;})
                        .text(function(d) {return d.count;})
                        .attr("font-size", Math.round(Math.min(svgHeight/15, 11))+"px")
                        .attr("text-anchor", "middle")
                        .attr("class", "knime-label");

                    xAxis = d3.svg.axis()
                        .scale(xScaleNom)
                        .ticks(0)
                        .orient("bottom");

                    yAxis = d3.svg.axis()
                        .scale(yScale)
                        .orient("left")
                        .ticks(5);

                    if (data.maxCount < 10000) {
                        yAxis.tickFormat(d3.format(".0f"));
                    } else {
                        yAxis.tickFormat(d3.format(".0e"))
                    }

                    var axisX = svg.append("g")
                        .attr("class", "x nom axis knime-x knime-axis")
                        .attr("id", "xAxis"+data.colIndex)
                        .attr("transform", "translate(" + [margin.left, svgHeight - margin.bottom - margin.top] + ")")
                        .call(xAxis)

                    //different patterns of labels depending on number of elements in one column
                    if (histNomSizes[data.colIndex] > 12) {
                        axisX.selectAll("text")
                            .attr("y", -xScaleNom.rangeBand()/4)
                            .attr("x", -2)
                            .attr("class", "knime-label")
                            .attr("transform", "rotate(-90)")
                            .style("text-anchor", "end")
                            .text(function(d){ 
                                return d.slice(0,3)+".";
                            })
                            .style("font-size", textScale(data.bins.length)+"px");
                    } else {
                        axisX.selectAll("text")
                            .attr("y", 2)
                            .attr("x", 0)
                            .attr("class", "knime-label")
                            //.attr("dy", ".35em")
                            .attr("transform", "rotate(0)")
                            .style("text-anchor", "middle")
                            .text(function(d){ 
                                return d.slice(0,charecterScale(histNomSizes[data.colIndex]));
                            })
                            .style("font-size", "11px");
                    }

                    var axisY = svg.append("g")
                        .attr("class", "y axis knime-y knime-axis")
                        .attr("id", "yAxis"+data.colIndex)
                        .attr("transform", "translate(" + [margin.left, margin.top] + ")")
                        .style("font-size", Math.round(Math.min(svgHeight/15, 12))+"px")
                        .call(yAxis);

                    d3.selectAll(".domain")
                        .classed("knime-axis-line", true);
                    var ticks = d3.selectAll(".tick")
                        .classed("knime-tick", true);
                    ticks.selectAll("line")
                        .classed("knime-tick-line", true);
                    ticks.selectAll("text")
                        .classed("knime-tick-label", true);
                }

                _setCollapsedColumnsStyles();
                
                if (!update && !showHide) {
                    mapDeleteElement(respOpenedNom, datatable.page.info().page, data.colIndex)
                }
            })
            
        } catch (err) {
			if (err.stack) {
				alert(err.stack);
			} else {
				alert (err);
			}
		}
    }
    
    mapInitFill = function(map, num) {
        map.set(num, {});
        map.get(num).col = [];
        map.get(num).colNum = 0;
    }
    
    mapAddElement = function(map, num, colIndex) {
        map.get(num).col.push(colIndex);
        map.get(num).colNum = map.get(num).col.length ;
        map.get(num).col.sort(function(a, b){return a-b});
    }
    
    mapDeleteElement = function(map, num, colIndex) {
        map.get(num).col.splice(map.get(num).col.indexOf(colIndex), 1);
        map.get(num).colNum = map.get(num).col.length;
    }
    
    updateResponsiveContainer = function(map, num, colIndex, showHide, update, openedRows, pageLength) {
        if (showHide) {
            if (update) {
                if (prevRowsPerPage != pageLength) {
                    //delete old value and put into new location in the map
                    var oldPageNum = Math.floor(colIndex / prevRowsPerPage)
                    mapDeleteElement(map, oldPageNum, colIndex)
                    if (!map.has(num)) {
                        mapInitFill(map, num);
                    }
                    mapAddElement(map, num, colIndex)
                    openedRows++
                } 
                var controlSum = d3.nest()
                    .rollup(function(v) { return d3.sum(v, function(d) {return d.colNum})})
                    .entries(Array.from(map.values()))
                if (controlSum == openedRows) {
                    prevRowsPerPage = pageLength;
                    openedRows = 0;
                }
            } else {
                if (!map.has(num)) {
                    mapInitFill(map, num);
                } 
                mapAddElement(map, num, colIndex)
            }
        }
        return openedRows;
    }
	
    
	drawNumericTable = function() {
		if (_representation.enableSelection && _value.selection) {
			for (var i = 0; i < _value.selection.length; i++) {
				selection[_value.selection[i]] = true;
			}
		}
		try {
			knimeTable = new kt();
			knimeTable.setDataTable(_representation.statistics);
            
			var wrapper = $('<div id="tabs-knimeDataExplorerContainer">').attr("class", "tab-pane active knime-table-container");
			content.append(wrapper);
            
			if (_representation.title != null && _representation.title != '') {
				wrapper.append('<h1 class="knime-title">' + _representation.title + '</h1>')
			}
			if (_representation.subtitle != null && _representation.subtitle != '') {
				wrapper.append('<h2 class="knime-subtitle">' + _representation.subtitle + '</h2>')
			}
			var table = $('<table id="knimeDataExplorer" class="table table-striped table-bordered knimeDataTable knime-table" width="100%">');
			wrapper.append(table);
			
			var colArray = [];
			var colDefs = [];
            
            //column names
            colArray.push({
                'title': 'Column', 
                'orderable': true,
                'className': 'no-break knime-table-cell knime-string'
            });
            
			if (_representation.enableSelection) {
				var all = _value.selectAll;
				colArray.push({'title': /*'<input name="select_all" value="1" id="checkbox-select-all" type="checkbox"' + (all ? ' checked' : '')  + ' />'*/ 'Exclude Column'})
				colDefs.push({
					'targets': 1,
					'searchable':false,
					'orderable':false,
					'className': 'dt-body-center knime-table-cell knime-boolean',
					'render': function (data, type, full, meta) {
						//var selected = selection[data] ? !all : all;
						setTimeout(function(){
							var el0 = $('#checkbox-select-all').get(0);
							/*if (all && selection[data] && el && ('indeterminate' in el)) {
								el.indeterminate = true;
							}*/
						}, 0);
						return '<input type="checkbox" name="id[]" class="knime-boolean"'
							+ (selection[full[0]] ? ' checked' : '')
							+' value="' + $('<div/>').text(full[0]).html() + '">';
					}
				});
			}
			
			for (var i = 0; i < knimeTable.getColumnNames().length; i++) {
				var colType = knimeTable.getColumnTypes()[i];
				var knimeColType = knimeTable.getKnimeColumnTypes()[i];
				var colDef = {
					'title': knimeTable.getColumnNames()[i],
					'orderable' : isColumnSortable(colType),
					'searchable': isColumnSearchable(colType),
                    'className': 'knime-table-cell'
				}
				if (_representation.displayMissingValueAsQuestionMark) {
					colDef.defaultContent = '<span class="knime-missing-value-cell">?</span>';
				}
				if (colType == 'number') {
                    if (knimeTable.getKnimeColumnTypes()[i].indexOf('double') > -1) {
                        colDef.className += ' knime-double';
                        if (_representation.enableGlobalNumberFormat) {
                            colDef.render = function (data, type, full, meta) {
                                if (!$.isNumeric(data)) {
                                    return data;
                                }
                                return isInt(data) ? data : Number(data).toFixed(_representation.globalNumberFormatDecimals);
                            }
                        }
                    } else {
                        colDef.className += ' knime-integer';
                    }
                }
				colArray.push(colDef);
			}
            
            xScale = d3.scale.linear(), 
            yScale = d3.scale.linear();
            
            var colDef = {
                'title' :"Histogram",
                'orderable': false, 
                'searchable': false,
                'defaultContent':  '<span class="knime-missing-value-cell">?</span>',
                'className': 'knime-table-cell knime-image knime-svg'
            }
            
            if (_representation.jsNumericHistograms != null) {
                _representation.jsNumericHistograms.forEach(function(d) {histSizes.push(d.bins.length)});
                colDef.render = function(data, type, full, meta) {
                    svgHeight = svgHsmall + margin.top;
                    svgWidth = svgWsmall;

                    var min = data.bins[0].min;
                    var max = data.bins[data.bins.length-1].max;
                    var barWidth = (max - min)/data.bins.length;
                    var dataRange = max - min;

                    xScale.range([0, svgWidth])
                    if (dataRange == 0) {
                        xScale.domain([0, max])
                    } else{ 
                        xScale.domain([0, max - min]);
                    }
                    yScale.range([svgHsmall, 0])
                        .domain([0, data.maxCount]);

                    var histDiv = document.createElement("div");

                    var svg = d3.select(histDiv).attr("class", "hist")
                        .append("svg")
                        .attr("height", svgHeight)
                        .attr("width", svgWidth)
                        .attr("class", "svg_hist knime-image knime-svg")
                        .attr("id", "svg"+data.colIndex);

                    var bar_group = svg.append("g")
                        .attr("transform", "translate(" + [0 , margin.top] + ")")
                        .attr("class", "bars")
                        .attr("id", "svg"+meta.row);

                    var bars = bar_group.selectAll("rect")
                        .data(data.bins)
                            .enter()
                        .append("rect")
                        .attr("class", "rect"+data.colIndex)
                        .attr("x", function (d) {return xScale(d.min - min);})
                        .attr("y", function(d) {return yScale(d.count);})
                        .attr("width", function(d) {return xScale(dataRange == 0? d.min : barWidth)})
                        .attr("height", function(d){return svgHsmall - yScale(d.count);})
                        .attr("fill", "#547cac")
                        .attr("stroke", "black")
                        .attr("stroke-width", "1px")
                        .append("title")
                        .text(function(d, i) { return "|[" + d.min + "; " + d.max + ">|= " + d.count; });

                    return $('<div/>').append(histDiv).html();
                }
                colArray.push(colDef);
                //number of histogram column to use in the next calculations
                histCol = colArray.length - 1;
            } else {
                colArray.push(colDef);
            }
            
			pageLength = _representation.initialPageSize;
			if (_value.pageSize) {
				pageLength = _value.pageSize;
			}
			pageLengths = _representation.allowedPageSizes;
			if (_representation.pageSizeShowAll) {
				var first = pageLengths.slice(0);
				first.push(-1);
				var second = pageLengths.slice(0);
				second.push("All");
				pageLengths = [first, second];
			}
			//var order = [];
			if (_value.currentOrder) {
				order = _value.currentOrder;
			}
			//var buttons = [];
			if (_representation.enableSorting && _representation.enableClearSortButton) {
				var unsortButton = {
						'text': "Clear Sorting",
						'action': function (e, dt, node, config) {
							dt.order.neutral();
							dt.draw();
						},
						'enabled': (order.length > 0)
				}
				buttons.push(unsortButton);
			}
			var firstChunk = getDataSlice(0, _representation.initialPageSize, knimeTable);
            
			var searchEnabled = _representation.enableSearching || (knimeService && knimeService.isInteractivityAvailable());
            
			dataTable = $('#knimeDataExplorer').DataTable( {
                'columns': colArray,
				'columnDefs': colDefs,
				'order': order,
                //'retrieve': true,
				'paging': _representation.enablePaging,
				'pageLength': pageLength,
				'lengthMenu': pageLengths,
				'lengthChange': _representation.enablePageSizeChange,
				'searching': searchEnabled,
				'ordering': _representation.enableSorting,
				'processing': true,
				'deferRender': !_representation.enableSelection,
				'data': firstChunk,
				'buttons': buttons,
                'responsive': true,
                'scrollX':false,
				'fnDrawCallback': function() {
					if (!_representation.displayColumnHeaders) {
						$("#knimeDataExplorer thead").remove();
				  	}
					if (searchEnabled && !_representation.enableSearching) {
						$('#knimeDataExplorer_filter').remove();
                    }
                    _setDynamicCssStyles();
				},
                "oLanguage": { "sEmptyTable": "No numeric columns in the dataset." }  
			});
            
            drawControls("knimeDataExplorer", knimeTable, dataTable);
			
			if (_representation.enableSelection) {
				// Handle click on "Select all" control
				var selectAllCheckbox = $('#checkbox-select-all').get(0);
				if (selectAllCheckbox) {
					if (selectAllCheckbox.checked && ('indeterminate' in selectAllCheckbox)) {
						selectAllCheckbox.indeterminate = _value.selectAllIndeterminate;
					}
					selectAllCheckbox.addEventListener('click', function() {
						selectAll(this.checked);
					});
				}

				// Handle click on checkbox to set state of "Select all" control
				$('#knimeDataExplorer tbody').on('change', 'input[type="checkbox"]', function() {
					//var el = $('#checkbox-select-all').get(0);
					//var selected = el.checked ? !this.checked : this.checked;
					// we could call delete _value.selection[this.value], but the call is very slow 
					// and we can assume that a user doesn't click on a lot of checkboxes
					selection[this.value] = this.checked;
					
					if (this.checked) {
						/*if (knimeService && knimeService.isInteractivityAvailable() && _value.publishSelection) {
							knimeService.addRowsToSelection(_representation.table.id, [this.value], selectionChanged);
						}*/
					} else {
						// If "Select all" control is checked and has 'indeterminate' property
						if(selectAllCheckbox && selectAllCheckbox.checked && ('indeterminate' in selectAllCheckbox)){
							// Set visual state of "Select all" control as 'indeterminate'
							selectAllCheckbox.indeterminate = true;
							_value.selectAllIndeterminate = true;
						}
						if (hideUnselected) {
							dataTable.draw('full-hold');
						}
						/*if (knimeService && knimeService.isInteractivityAvailable() && _value.publishSelection) {
							knimeService.removeRowsFromSelection(_representation.table.id, [this.value], selectionChanged);
						}*/
					}
				});
			}
			
			//load all data
			setTimeout(function() {
				var initialChunkSize = 10;
				addDataToTable(_representation.initialPageSize, initialChunkSize, knimeTable, dataTable, "knimeDataExplorer");
			}, 0);
            
            
            
            
            dataTable.on("responsive-display", function(e, datatable, row, showHide, update) {
                
                var data = row.data()[histCol];
                var pageLength = datatable.page.info().length < 0? datatable.page.info().recordsTotal : datatable.page.info().length;
                
                openedNumRows = updateResponsiveContainer(respOpenedNum, datatable.page.info().page, data.colIndex, showHide, update, openedNumRows, pageLength)

                //when responsive is opened it creates an additional div of the same class right under its original one
                var bigHist = $(".hist")[data.colIndex % pageLength + respOpenedNum.get(datatable.page.info().page).col.indexOf(data.colIndex) + 1];

                var textScale = d3.scale.linear()
                    .range([8, 11])
                    .domain([d3.max(histSizes), 12]);
                
                svgWidth = svgWbig;
                svgHeight = svgHbig;
                var svgBigHist = d3.select(bigHist).select("#svg"+data.colIndex)[0][0]
                
                var min = data.bins[0].min;
                var max = data.bins[data.bins.length - 1].max;
                var barWidth = (max - min)/data.bins.length;
                var dataRange = max - min;
        
                //strange rescaling allows having correct ticks on x axis
                xScale.range([0,  svgWidth - margin.left - margin.right])
                if (dataRange == 0) {
                    xScale.domain([0, 1.5*min])
                } else{ 
                    xScale.domain([min, (max + barWidth * 0.5)]);
                }

                yScale.range([svgHeight - 2*margin.top - margin.bottom, 0])
                    .domain([0, data.maxCount]);
                
                var barWidthScale = xScale((barWidth + min))
                
                var svg = d3.select(svgBigHist).attr("width", svgWidth).attr("height", svgHeight);
                
                var bar_group = svg.selectAll(".bars")
                    .attr("transform", "translate("+[margin.left , margin.top]+")");
                
                var bars = svg.selectAll(".bars")
                    .selectAll(".rect"+data.colIndex)
                    .data(data.bins)
                    .attr("x", function (d) {return xScale(dataRange == 0? 0: d.min);})
                    .attr("y", function(d) {return yScale(d.count);})
                    .attr("width", function(d) {return dataRange == 0? xScale(min) : barWidthScale;})
                    .attr("height", function(d){return svgHeight - 2*margin.top - margin.bottom -  yScale(d.count);})
                
                var text_group = svg.append("g")
                    .attr("class", "caption knime-label")
                    .attr("transform", "translate(" + [margin.left , margin.top] + ")")
                    .attr("id", "id"+data.colIndex);
                
                var texts = text_group.selectAll("text")
                    .data(data.bins)
                    .enter()
                    .append("text")
                    .attr("x", function (d) {return dataRange == 0? xScale(min)/2 : xScale(d.min) + barWidthScale/2;})
                    .attr("y", function(d) {return yScale(d.count) - 2;})
                    .text(function(d,i) { 
                        if (dataRange == 0) {
                            return  i == 0? d.count : "";
                        }
                        return d.count;
                    })
                    .attr("font-size", Math.round(Math.min(svgHeight/15, 11))+"px")
                    .attr("text-anchor", "middle")
                    .attr("class", "knime-label");
                
                var ticks = [];
                data.bins.forEach(function(d,i) {
                    ticks.push(d.min);
                })
                ticks.push(data.bins[data.bins.length - 1].max)
                
                var xAxis = d3.svg.axis()
                    .scale(xScale)
                    .orient("bottom")
                    .tickValues(ticks);
                
                var yAxis = d3.svg.axis()
                    .scale(yScale)
                    .orient("left")
                    //.tickFormat(d3.format(".0e"))
                    .ticks(5);
                
                if (data.bins.length < 15 && max < 10000) {
                    if (!isInt(max) || !isInt(min))  {
                        xAxis.tickFormat(d3.format(".1f"));
                    }
                } else {
                    //add if statements for 0.1, 0.01
                    if (max < 1000) {
                         if (!isInt(max) || !isInt(min)) {
                             xAxis.tickFormat(d3.format(".1f"));
                         } else {
                             xAxis.tickFormat(d3.format(".0f"));
                         }
                    } else {
                        if (!isInt(max) || !isInt(min)) {
                             xAxis.tickFormat(d3.format(".1e"));
                         } else {
                             if (max >= 10000) {
                                 xAxis.tickFormat(d3.format(".1e"));
                             } else {
                                 xAxis.tickFormat(d3.format(".0f"));
                             }
                         }
                    }
                }
                
                if (data.maxCount < 10000) {
                    yAxis.tickFormat(d3.format(".0f"));
                } else {
                    yAxis.tickFormat(d3.format(".0e"))
                }
                
                var axisX = svg.append("g")
                    .attr("class", "x axis knime-x knime-axis")
                    .attr("id", "xAxis"+data.colIndex)
                    .attr("transform", "translate(" + [margin.left, svgHeight - margin.bottom - margin.top] + ")")
                    .call(xAxis);
                
                if (data.bins.length < 11) {
                    axisX.selectAll("text")
                        .attr("class", "knime-label")
                        .style("font-size", "12px");
                } else {
                    axisX.attr("class", "x axis").selectAll("text")
                        .attr("class", "knime-label")
                        .attr("y", -barWidthScale/10)
                        .attr("x", -7)
                        .attr("transform", "rotate(-90)")
                        .style("text-anchor", "end")
                        .style("font-size", textScale(data.bins.length)+"px");
                }
                
                var axisY = svg.append("g")
                    .attr("class", "y axis knime-y knime-axis")
                    .attr("id", "yAxis"+data.colIndex)
                    .attr("transform", "translate(" + [margin.left, margin.top] + ")")
                    .style("font-size", Math.round(Math.min(svgHeight/15, 12))+"px")
                    .call(yAxis);

                d3.selectAll(".domain")
                    .classed("knime-axis-line", true);
                var ticks = d3.selectAll(".tick")
                    .classed("knime-tick", true);
                ticks.selectAll("line")
                    .classed("knime-tick-line", true);
                ticks.selectAll("text")
                    .classed("knime-tick-label", true);

                _setCollapsedColumnsStyles();
                
                if (!update && !showHide) {
                     mapDeleteElement(respOpenedNum, datatable.page.info().page, data.colIndex)
                }
            })
			
		} catch (err) {
			if (err.stack) {
				alert(err.stack);
			} else {
				alert (err);
			}
		}
	}
    
    drawControls = function(tableName, knTable, jsDataTable) {
        if (_representation.enableSorting && _representation.enableClearSortButton) {
            jsDataTable.buttons().container().appendTo('#'+ tableName +'_wrapper .col-sm-6:eq(0)');
            $('#' + tableName + '_length').css({'display': 'inline-block', 'margin-right': '10px'});
            jsDataTable.on('order.dt', function () {
                var order = dataTable.order();
                jsDataTable.button(0).enable(order.length > 0);
            });
        }

        $('#' + tableName +'_paginate').css('display', 'none');

        $('#' + tableName + '_info').html(
            '<strong>Loading data</strong> - Displaying '
            + 1 + ' to ' + Math.min(knTable.getNumRows(), _representation.initialPageSize)
            + ' of ' + knTable.getNumRows() + ' entries.');

        if (knimeService) {
            if (_representation.enableSearching && !_representation.title) {
                knimeService.floatingHeader(false);
            }
            if (_representation.displayFullscreenButton) {
                knimeService.allowFullscreen();
            }
            if (_representation.maxNomValueReached.length != 0) {
                knimeService.setWarningMessage(warningMessageCutOffValues);
            }
        }
    }
    
    drawDataPreviewTable = function() {
		try {
			previewTable = new kt();
			previewTable.setDataTable(_representation.dataPreview);
			
			var wrapper = $('<div id="tabs-knimePreviewContainer">').attr("class", "tab-pane knime-table-container");
			content.append(wrapper);
			var table = $('<table id="knimePreview" class="table table-striped table-bordered knimeDataTable knime-table" width="100%">');
			wrapper.append(table);
			
			var colArray = [];
			var colDefs = [];
            
            if (_representation.displayRowIds) {
				var title = 'Row ID';
				var orderable = _representation.displayRowIds;
				colArray.push({
					'title': title, 
					'orderable': orderable,
					'className': 'no-break knime-table-cell knime-string'
				});
			}
            
            for (var i = 0; i < previewTable.getColumnNames().length; i++) {
				var colType = previewTable.getColumnTypes()[i];
				var knimeColType = previewTable.getKnimeColumnTypes()[i];
				var colDef = {
					'title': previewTable.getColumnNames()[i],
					'orderable' : isColumnSortable(colType),
					'searchable': isColumnSearchable(colType),
                    'className': 'knime-table-cell'
				}
				if (_representation.displayMissingValueAsQuestionMark) {
					colDef.defaultContent = '<span class="knime-missing-value-cell">?</span>';
				}
				if (colType == 'number') {
                    if (previewTable.getKnimeColumnTypes()[i].indexOf('double') > -1) {
                        colDef.className += ' knime-double';
                        colDef.type = 'num';
                        if (_representation.enableGlobalNumberFormat) {
                            colDef.render = function (data, type, full, meta) {
                                if (!$.isNumeric(data)) {
                                    return data;
                                }
                                return isInt(data) ? data : Number(data).toFixed(_representation.globalNumberFormatDecimals);
                            }
                        }
                    } else {
                        colDef.className += ' knime-integer';
                        colDef.type = 'num';
                    }
                }
                if (colType == 'png') {
					colDef.className += ' knime-image knime-png';
				}
				if (colType == 'svg') {
					colDef.className += ' knime-image knime-svg';
				}
				if (colType == 'boolean') {
					colDef.className += ' knime-boolean';
				}
				if (colType == 'string') {
					colDef.className += ' knime-string';
				}
				colArray.push(colDef);
			}
            
            var dataPreview = []
            for (var i = 0; i < previewTable.getRows().length; i++) {
                if (previewTable.getRow(i) == null) {
                    continue;
                }
                dataPreview.push(previewTable.getRow(i).data);
            }
            
            var firstChunk = getDataSlice(0, _representation.initialPageSize, previewTable);
            
            var searchEnabled = _representation.enableSearching || (knimeService && knimeService.isInteractivityAvailable());
            previewDataTable = $('#knimePreview').DataTable( {
                'columns': colArray,
				'columnDefs': colDefs,
				'order': order,
				'paging': _representation.enablePaging,
                'pageLength': pageLength,
				'lengthMenu': pageLengths,
				'lengthChange': _representation.enablePageSizeChange,
				'searching': searchEnabled,
				'ordering': _representation.enableSorting,
				'processing': true,
				'data': firstChunk,
				'buttons': buttons,
                'responsive': true,
                'drawCallback': function() {
                    _setDynamicCssStyles();
                },
                "oLanguage": { "sEmptyTable": "The dataset is empty." }  
			});
            
            drawControls("knimePreview", previewTable, previewDataTable);
            
            //load all data
			setTimeout(function() {
				var initialChunkSize = 10;
				addDataToTable(_representation.initialPageSize, initialChunkSize, previewTable, previewDataTable, "knimePreview");
            }, 0);
            
            previewDataTable.on("responsive-display", function() {
                _setCollapsedColumnsStyles();
            });
            
        } catch (err) {
			if (err.stack) {
				alert(err.stack);
			} else {
				alert (err);
			}
		}
    }
    
    isInt = function(n) {
       return n % 1 === 0;
    }
	
	addDataToTable = function(startIndex, chunkSize, knTable, jsDataTable, tableName) {
		var startTime = new Date().getTime();
		var tableSize = knTable.getNumRows()
		var endIndex  = Math.min(tableSize, startIndex + chunkSize);
		var chunk = getDataSlice(startIndex, endIndex, knTable);
        //console.log("datarow",chunk)
		jsDataTable.rows.add(chunk);
		var endTime = new Date().getTime();
		var chunkDuration = endTime - startTime;
		var newChunkSize = chunkSize;
		if (startIndex + chunkSize < tableSize) {
			$('#' + tableName + '_info').html(
				'<strong>Loading data ('
				+ endIndex + ' of ' + tableSize + ' records)</strong> - Displaying '
				+ 1 + ' to ' + Math.min(tableSize, _representation.initialPageSize) 
				+ ' of ' + tableSize + ' entries.');
			if (chunkDuration > 300) {
				newChunkSize = Math.max(1, Math.floor(chunkSize / 2));
			} else if (chunkDuration < 100) {
				newChunkSize = chunkSize * 2;
			}
			setTimeout((function(i, c, t, js, tn) {
				return function() {
					addDataToTable(i, c, t, js, tn);
				};
			})(startIndex + chunkSize, newChunkSize, knTable, jsDataTable, tableName), chunkDuration);
		} else {
			$('#' + tableName + '_paginate').css('display', 'block');
			applyViewValue(jsDataTable);
			jsDataTable.draw();
            if (knTable.getTableId() == "numeric") {
                finishInit(jsDataTable);
            }
		}
	}
	
	getDataSlice = function(start, end, knTable) {
		if (typeof end == 'undefined') {
			end = knTable.getNumRows();
		}
		var data = [];
		for (var i = start; i < Math.min(end, knTable.getNumRows()); i++) {
			var row = knTable.getRows()[i];
            if (row == null) {
                continue;
            }
			var dataRow = [];
            
			if (_representation.enableSelection) {
                switch (knTable.getTableId()) {
                    case "numeric":
                        dataRow.push(row.rowKey);
                        break;
                    case "preview":
                        break;
                    case "nominal":
                        dataRow.push(row.rowKey);
                        break;
                    default: 
                        break;
                }
			}
            
//			if (_representation.displayRowIndex) {
//				dataRow.push(i);
//			
            
            switch (knTable.getTableId()) {
                case "numeric":
                    dataRow.push('<span class="rowKey">' + row.rowKey + '</span>');
                    break;
                case "preview":
                    if (_representation.displayRowIds) {
                        dataRow.push('<span class="rowKey">' + row.rowKey + '</span>');
                    }
                    break;
                case "nominal":
                    dataRow.push('<span class="rowKey">' + row.rowKey + '</span>');
                    break;
                default: 
                    break;
            }
            
//			if (_representation.displayRowIds) {
//				var string = '<span class="rowKey">' + row.rowKey + '</span>';
//				dataRow.push('<span class="rowKey">' + row.rowKey + '</span>');
//			}
            
            if (knTable.getTableId() == "nominal") {
                var numericPart = row.data.slice(knTable.getColumnTypes().indexOf("number"), knTable.getColumnTypes().indexOf("string"));
                var nominalPart = row.data.slice(knTable.getColumnTypes().indexOf("string"), knTable.getColumnTypes().length);
                //window.alert(nominalPart)
                var nominalPartArrays = [];
                var nominalPartPlain = [];
                for (var j = 0; j < nominalPart.length; j++) {
                    if (Array.isArray(nominalPart[j]) && nominalPart[j] != null) {
                        nominalPartArrays.push(nominalPart[j]);
                        
                    } else {
                        nominalPartPlain.push(nominalPart[j]);
                    }
                }
                var dataRow = dataRow.concat(numericPart);
            } else {
                dataRow = dataRow.concat(row.data)
            }

            
            if (_representation.enableFreqValDisplay && knTable.getTableId() == "nominal") {
                var dataRow = dataRow.concat(nominalPartArrays);
                if (nominalPart.length == 1 && nominalPart[0] == null) {
                    var dataRow = dataRow.concat(nominalPart);
                }
            }
            
            switch (knTable.getTableId()) {
                case "numeric":
                    if (_representation.jsNumericHistograms != null) {
                        dataRow.push(_representation.jsNumericHistograms[i]);
                    }
                    break;
                case "preview":
                    break;
                case "nominal":
                    if (_representation.jsNominalHistograms != null) {
                        dataRow.push(_representation.jsNominalHistograms[i]);
                    }
                    //window.alert(_representation.jsNominalHistograms[i])
                    break;
                default: 
                    break;
            }
			data.push(dataRow);
		}
		return data;
	}
	
	applyViewValue = function(jsDataTable) {
		if (_representation.enableSearching && _value.filterString) {
			jsDataTable.search(_value.filterString);
		}
		if (_representation.enablePaging && _value.currentPage) {
			setTimeout(function() {
				jsDataTable.page(_value.currentPage).draw('page');
			}, 0);
		}
	}
	
	finishInit = function(jsDataTable) {
		allCheckboxes = jsDataTable.column(1).nodes().to$().find('input[type="checkbox"]');
		initialized = true;
	}
	
	selectAll = function(all) {
		// cannot select all rows before all data is loaded
		if (!initialized) {
			setTimeout(function() {
				selectAll(all);
			}, 500);
		}
		
		// Check/uncheck all checkboxes in the table
		selection = {};
		_value.selectAllIndeterminate = false;
		allCheckboxes.each(function() {
			this.checked = all;
			if ('indeterminate' in this && this.indeterminate) {
				this.indeterminate = false;
			}
			if (all) {
				selection[this.value] = true;
			}
		});
		_value.selectAll = all ? true : false;
		if (hideUnselected) {
			dataTable.draw();
		}
		//publishCurrentSelection();
	}
	
	isColumnSortable = function (colType) {
		var allowedTypes = ['boolean', 'string', 'number', 'dateTime'];
		return allowedTypes.indexOf(colType) >= 0;
	}
	
	isColumnSearchable = function (colType) {
		var allowedTypes = ['boolean', 'string', 'number', 'dateTime', 'undefined'];
		return allowedTypes.indexOf(colType) >= 0;
    }
    
    /**
	 * Set CSS styles for table controls
	 */
	_setControlCssStyles = function() {
		$('.dataTables_length').addClass('knime-table-length');
		$('.dataTables_length label').addClass('knime-table-control-text');
		$('.dataTables_length select').addClass('knime-table-control-text knime-single-line');
		$('.dt-buttons').addClass('knime-table-buttons');
		$('.dt-buttons span').addClass('knime-table-control-text');
		$('.dataTables_filter').addClass('knime-table-search');
		$('.dataTables_filter label').addClass('knime-table-control-text');
		$('.dataTables_filter input').addClass('knime-filter knime-single-line');
		$('.dataTables_paginate').addClass('knime-table-paging');
		$('.dataTables_info').addClass('knime-table-info knime-table-control-text');		
	}

	/**
	 * Set CSS styles for dynamically loaded objects controls
	 */
	_setDynamicCssStyles = function() {
		$('.knime-table tr').addClass('knime-table-row');	
		$('.knime-table-paging ul').addClass('knime-table-control-text');
		$('.knime-table thead tr').addClass('knime-table-header');
		$('.knime-table thead th').addClass('knime-table-header');
    }
    
    /**
     * Set CSS styles for collapsed columns
     */
    _setCollapsedColumnsStyles = function() {
        d3.selectAll('tr.child')
            .classed('knime-table-row', true);
        d3.selectAll('td.child')
            .classed('knime-table-cell', true);
        d3.selectAll('td.child li')
            .classed('knime-tooltip', true);
        d3.selectAll('.dtr-title')
            .classed('knime-tooltip-caption', true);
        d3.selectAll('.dtr-data')
            .classed('knime-tooltip-value', true);
    }
	
	view.validate = function() {
	    return true;
	}
	
	view.getComponentValue = function() {
		if (!_value) {
			return null;
		}
		_value.selection = [];
		for (var id in selection) {
			if (selection[id]) {
				_value.selection.push(id);
			}
		}
		if (_value.selection.length == 0) {
			_value.selection = null;
		}
		var pageNumber = dataTable.page();
		if (pageNumber > 0) {
			_value.currentPage = pageNumber;
		}
		var pageSize = dataTable.page.len();
		if (pageSize != _representation.initialPageSize) {
			_value.pageSize = pageSize;
		}
		var searchString = dataTable.search();
		if (searchString.length) {
			_value.filterString = searchString;
		}
		var order = dataTable.order();
		if (order.length > 0) {
			_value.currentOrder = order;
		}
		if (_representation.enableColumnSearching) {
			_value.columnFilterStrings = [];
			var filtered = false;
			dataTable.columns().every(function (index) {
		        var input = $('input', this.footer());
		        if (input.length) {
		        	var filterString = input.val();
		        	_value.columnFilterStrings.push(filterString);
		        	filtered |= filterString.length;
		        } else {
		        	_value.columnFilterStrings.push("");
		        }
		    });
			if (!filtered) {
				_value.columnFilterStrings = null;
			}
		}
		hideUnselected = document.getElementById('showSelectedOnlyCheckbox');
		if (hideUnselected) {
			_value.hideUnselected = hideUnselected.checked;
		}
		return _value;
	}
	
	return view;
	
}();