table_editor = function() {

	var table_viewer = {};
	var _representation = null;
	var _value = null;
	var knimeTable = null;
	var dataTable = null;
	var selection = {};
	var partialSelectedRows = [];
	//var allCheckboxes = [];
	var currentFilter = null;
	var initialized = false;
	
	var colArray = [];
	var infoColsCount = 0;
	
	var selectedCell = undefined;
	
	/**
	 * Editor abstract class
	 */
	
	var Editor = function() {
		this.component = undefined;
	}
	
	Editor.prototype.getComponent = function() {
		return this.component;
	}
	
	Editor.prototype.getValue = function() {
		return this.component.val();
	}
	
	Editor.prototype.setValue = function(value) {
		this.component.val(value);
	}
	
	/**
	 * String values editor
	 */
	var StringEditor = function() {
		this.component = $('<input type="text"/>');
	}
	
	StringEditor.prototype = Object.create(Editor.prototype);
	
	StringEditor.prototype.getValue = function() {
		var value = this.component.val();		
		if (value == '') {
			return null;
		}
		return value;
	}
	
	/**
	 * Integer or Long values editor
	 */
	var IntEditor = function() {
		this.component = $('<input type="number"/>');
	}
	
	IntEditor.prototype = Object.create(Editor.prototype);
	
	IntEditor.prototype.getValue = function() {
		var value = this.component.val();
		if (value == '') {
			return null;
		} else {
			return parseInt(value, 10);
		}
	}
	
	/**
	 * Double values editor
	 */
	var DoubleEditor = function() {
		this.component = $('<input type="number" class="double-cell" step="any"/>');
	}
	
	DoubleEditor.prototype = Object.create(Editor.prototype);
	
	DoubleEditor.prototype.getValue = function() {
		var value = this.component.val();
		if (value == '') {
			return null;
		} else {
			return parseFloat(value);
		}
	}
	
	/**
	 * Boolean values editor
	 */
	var BooleanEditor = function() {
		this.component = $('<input type="text"/>');
	}
	
	BooleanEditor.prototype = Object.create(Editor.prototype);
	
	BooleanEditor.prototype.getValue = function() {
		var value = this.component.val();
		if (value == '') {
			return null;
		} else {
			return value.toLowerCase() === 'true' || value === '1';
		}
	}
	
	/**
	 * Editor factory
	 */
	
	createEditor = function(type) {
		var editor;
		switch (type) {
			case 'String':
				editor = new StringEditor();
				break;
			case 'Number (integer)':
			case 'Number (long)':
				editor = new IntEditor();
				break;
			case 'Number (double)':
				editor = new DoubleEditor();
				break;
			case 'Boolean value':
				editor = new BooleanEditor();
				break;
		}
		return editor;
	}
	
	
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
	
	table_viewer.init = function(representation, value) {
		if (!representation || !representation.table) {
			$('body').append("Error: No data available");
			return;
		}
		_representation = representation;
		_value = value;

		if (parent && parent.KnimePageLoader) {
			drawTable();
		} else {
			$(document).ready(function() {
				drawTable();
			});
		}
	};
	
	drawTable = function() {
		// Set locale for moment.js.
		if (_representation.dateTimeFormats.globalDateTimeLocale !== 'en') {
			moment.locale(_representation.dateTimeFormats.globalDateTimeLocale);
		}
		
		var body = $('body');
		if (_representation.enableSelection && _value.selection) {
			for (var i = 0; i < _value.selection.length; i++) {
				selection[_value.selection[i]] = true;
			}
		}
		try {
			// apply editor changes
			if (_representation.table.dataHash == _value.tableHash) {
				var editorChanges = _value.editorChanges.changes;
				for (var rowKey in editorChanges) {
					var rowFilter = _representation.table.rows.filter(function(row) { return row.rowKey === rowKey });
					// since not all rows from the input table can be shown, we need to check, if the row is present in the view
					if (rowFilter.length > 0) {
						var row = rowFilter[0];
						var rowEntry = editorChanges[rowKey];
						for (var colName in rowEntry) {
							if (_representation.editableColumns.indexOf(colName) != -1) {  // check, if the column is still editable
								var colIndex = _representation.table.spec.colNames.indexOf(colName);
								if (colIndex != -1) {
									var cellValue = rowEntry[colName];
									row.data[colIndex] = cellValue;
								}
							}
						}
					}					
				}
			} else {
				_value.tableHash = _representation.table.dataHash;
				_value.editorChanges.changes = {};
			}
			
			knimeTable = new kt();
			knimeTable.setDataTable(_representation.table);
			
			var wrapper = $('<div id="knimePagedTableContainer">');
			body.append(wrapper);
			if (_representation.title != null && _representation.title != '') {
				wrapper.append('<h1>' + _representation.title + '</h1>')
			}
			if (_representation.subtitle != null && _representation.subtitle != '') {
				wrapper.append('<h2>' + _representation.subtitle + '</h2>')
			}
			var table = $('<table id="knimePagedTable" class="table table-striped table-bordered" width="100%">');
			wrapper.append(table);
			if (_representation.enableColumnSearching) {
				$('#knimePagedTable').append('<tfoot><tr></tr></tfoot>');
				var footerRow = $('#knimePagedTable tfoot tr');
				if (_representation.enableSelection) {
					footerRow.append('<th></th>');
				}
				if (_representation.displayRowIndex) {
					footerRow.append('<th></th>');						
				}
				if (_representation.displayRowColors || _representation.displayRowIds) {
					footerRow.append('<th></th>');
				}
				for (var i = 0; i < knimeTable.getColumnNames().length; i++) {
					if (isColumnSearchable(knimeTable.getColumnTypes()[i])) {
						footerRow.append('<th>' + knimeTable.getColumnNames()[i] + '</th>')
					} else {
						footerRow.append('<th></th>');
					}
				}
				
				$('#knimePagedTable tfoot th').each(function() {
			        var title = $(this).text();
			        if (title == '') {
			        	return;
			        }
			        $(this).html('<input type="text" placeholder="Search '+title+'" />' );
			    });
			}
			
			colArray = [];
			var colDefs = [];
			if (_representation.enableSelection) {
				if (_representation.singleSelection) {
					var titleElement = _representation.enableClearSelectionButton 
						? ('<button type="button" id="clear-selection-button" class="btn btn-default btn-xs" title="Clear selection">' 
							+ '<span class="glyphicon glyphicon-remove-circle" aria-hidden="true"></span></button>')
						: '';
					colArray.push({'title': titleElement});
					colDefs.push({
						'targets': 0,
						'searchable':false,
						'orderable':false,
						'className': 'dt-body-center selection-cell',
						'render': function (data, type, full, meta) {
							return '<input type="radio" name="radio_single_select"'
							+ (selection[data] ? ' checked' : '')
							+' value="' + $('<div/>').text(data).html() + '">';
						}
					});
				} else {
					var all = _value.selectAll;
					colArray.push({'title': '<input name="select_all" value="1" id="checkbox-select-all" type="checkbox"' + (all ? ' checked' : '')  + ' />'});
					colDefs.push({
						'targets': 0,
						'searchable':false,
						'orderable':false,
						'className': 'dt-body-center selection-cell',
						'render': function (data, type, full, meta) {
							//var selected = selection[data] ? !all : all;
							setTimeout(function(){
								var el = $('#checkbox-select-all').get(0);
								/*if (all && selection[data] && el && ('indeterminate' in el)) {
								el.indeterminate = true;
							}*/
							}, 0);
							return '<input type="checkbox" name="id[]"'
							+ (selection[data] ? ' checked' : '')
							+' value="' + $('<div/>').text(data).html() + '">';
						}
					});
				}
				infoColsCount++;
			}
			if (_representation.displayRowIndex) {
				colArray.push({
					'title': "Row Index",
					'searchable': false
				});
				infoColsCount++;
			}
			if (_representation.displayRowIds || _representation.displayRowColors) {
				var title = _representation.displayRowIds ? 'RowID' : '';
				var orderable = _representation.displayRowIds;
				colArray.push({
					'title': title, 
					'orderable': orderable,
					'className': 'no-break'
				});
				infoColsCount++;
			}
			
			var editableColIndices = [];
			for (var i = 0; i < knimeTable.getColumnNames().length; i++) {
				var colType = knimeTable.getColumnTypes()[i];
				var knimeColType = knimeTable.getKnimeColumnTypes()[i];
				
				var colName = knimeTable.getColumnNames()[i];
				if (_representation.editableColumns.indexOf(colName) !== -1) {
					editableColIndices.push(colArray.length);  // colArray.length <=> index of the current column in colArray
					colName += '<span class="glyphicon glyphicon-pencil"></span>';
				}
				
				var colDef = {
					'title': colName,
					'orderable' : isColumnSortable(colType),
					'searchable': isColumnSearchable(colType)					
				}
				if (_representation.displayMissingValueAsQuestionMark) {
					colDef.defaultContent = '<span class="missing-value-cell">?</span>';
				}
				if (knimeColType == 'Date and Time' && _representation.dateTimeFormats.globalDateTimeFormat) {
					colDef.render = function (data, type, full, meta) {
						// Check if date is given as ISO-string or time stamp (legacy).
						if (isNaN(data)) {
							// ISO-string:
							// date is parsed and rendered in local time. 
							return moment(data).format(type === 'sort' || type === 'type' ? 'x' : _representation.dateTimeFormats.globalDateTimeFormat);
						} else {
							// time stamp (legacy):
							// date is parsed and rendered in UTC.
							return moment(data).utc().format(type === 'sort' || type === 'type' ? 'x' : _representation.dateTimeFormats.globalDateTimeFormat);
						}
					}
				}
				if (knimeColType == 'Local Date' && _representation.dateTimeFormats.globalLocalDateFormat) {
				  colDef.render = function (data, type, full, meta) {
				    return moment(data).format(type === 'sort' || type === 'type' ? 'x' : _representation.dateTimeFormats.globalLocalDateFormat);
				  }
				}

				if (knimeColType == 'Local Date Time' && _representation.dateTimeFormats.globalLocalDateTimeFormat) {
				  colDef.render = function (data, type, full, meta) {
				    return moment(data).format(type === 'sort' || type === 'type' ? 'x' : _representation.dateTimeFormats.globalLocalDateTimeFormat);
				  }
				}

				if (knimeColType == 'Local Time' && _representation.dateTimeFormats.globalLocalTimeFormat) {
				  colDef.render = function (data, type, full, meta) {
				    return moment(data, "hh:mm:ss.SSSSSSSSS").format(type === 'sort' || type === 'type' ? 'x' : _representation.dateTimeFormats.globalLocalTimeFormat);
				  }
				}

				if (knimeColType == 'Zoned Date Time' && _representation.dateTimeFormats.globalZonedDateTimeFormat) {
					colDef.render = function (data, type, full, meta) {
						var regex = /(.*)\[(.*)\]$/
						var match = regex.exec(data);

						if (match == null) {
							var date = moment.tz(data, "");
						} else {
							dateTimeOffset = match[1];
							zone = match[2];

							if (moment.tz.zone(zone) == null) {
								var date = moment.tz(dateTimeOffset, "");
							} else {
								var date = moment.tz(dateTimeOffset, zone);
							}
						}

						return date.format(type === 'sort' || type === 'type' ? 'x' : _representation.dateTimeFormats.globalZonedDateTimeFormat);
					}
				}
				if (colType == 'number' && _representation.enableGlobalNumberFormat) {
					if (knimeTable.getKnimeColumnTypes()[i].indexOf('double') > -1) {
						colDef.render = function(data, type, full, meta) {
							if (!$.isNumeric(data)) {
								return data;
							}
							return Number(data).toFixed(_representation.globalNumberFormatDecimals);
						}
					}
				}
				if (colType == 'png') {
					colDef.render = function (data, type, full, meta) {
						return '<img src="data:image/png;base64,' + data + '" />';
					}
				}
				colArray.push(colDef);
				
			}
			var pageLength = _representation.initialPageSize;
			if (_value.pageSize) {
				pageLength = _value.pageSize;
			}
			var pageLengths = _representation.allowedPageSizes;
			if (_representation.pageSizeShowAll) {
				var first = pageLengths.slice(0);
				first.push(-1);
				var second = pageLengths.slice(0);
				second.push("All");
				pageLengths = [first, second];
			}
			var order = [];
			if (_value.currentOrder) {
				order = _value.currentOrder;
			}
			var buttons = [];
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
			
			var firstChunk = getDataSlice(0, _representation.initialPageSize);
			//search is also used for filtering, so consider all possible options
			var searchEnabled = _representation.enableSearching || _representation.enableColumnSearching
				|| (_representation.enableSelection && (_value.hideUnselected || _representation.enableHideUnselected)) 
				|| (knimeService && knimeService.isInteractivityAvailable());
			
			dataTable = $('#knimePagedTable').DataTable( {
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
				'deferRender': !_representation.enableSelection,
				'data': firstChunk,
				'buttons': buttons,
				'fnDrawCallback': function() {
					if (!_representation.displayColumnHeaders) {
						$("#knimePagedTable thead").remove();
				  	}
					if (searchEnabled && !_representation.enableSearching) {
						$('#knimePagedTable_filter').remove();
					}
					if (dataTable) {
						dataTable.columns({page: 'current'}).nodes().flatten().to$().on('click', cellClickHandler);
						dataTable.columns(editableColIndices, {page: 'current'}).nodes().flatten().to$().on('dblclick', editableCellDoubleClickHandler);
					}
				},
				'preDrawCallback': function() {
					if (dataTable) {
						dataTable.columns({page: 'current'}).nodes().flatten().to$().off('click', cellClickHandler);
						dataTable.columns(editableColIndices, {page: 'current'}).nodes().flatten().to$().off('dblclick', editableCellDoubleClickHandler);
					}
				}
			});
			
			//Clear sorting button placement and enable/disable on order change
			if (_representation.enableSorting && _representation.enableClearSortButton) {
				dataTable.buttons().container().appendTo('#knimePagedTable_wrapper .col-sm-6:eq(0)');
				$('#knimePagedTable_length').css({'display': 'inline-block', 'margin-right': '10px'});
				dataTable.on('order.dt', function () {
					var order = dataTable.order();
					dataTable.button(0).enable(order.length > 0);
				});
			}
			
			$('#knimePagedTable_paginate').css('display', 'none');

			$('#knimePagedTable_info').html(
				'<strong>Loading data</strong> - Displaying '
				+ 1 + ' to ' + Math.min(knimeTable.getNumRows(), _representation.initialPageSize)
				+ ' of ' + knimeTable.getNumRows() + ' entries.');
			
			if (knimeService) {
				if (_representation.enableSearching && !_representation.title) {
					knimeService.floatingHeader(false);
				}
				if (_representation.displayFullscreenButton) {
					knimeService.allowFullscreen();
				}
				if (_representation.enableSelection) {
					$.fn.dataTable.ext.search.push(function(settings, searchData, index, rowData, counter) {
						if (_value.hideUnselected) {
							return selection[rowData[0]] || partialSelectedRows.indexOf(rowData[0]) > -1;
						}
						return true;
					});
					if (_representation.enableHideUnselected && !_representation.singleSelection) {
						var hideUnselectedCheckbox = knimeService.createMenuCheckbox('showSelectedOnlyCheckbox', _value.hideUnselected, function() {
							var prev = _value.hideUnselected;
							_value.hideUnselected = this.checked;
							if (prev !== _value.hideUnselected) {
								dataTable.draw();
							}
						});
						knimeService.addMenuItem('Show selected rows only', 'filter', hideUnselectedCheckbox);
						if (knimeService.isInteractivityAvailable()) {
							knimeService.addMenuDivider();
						}
					}
					
				}
				
				if (knimeService.isInteractivityAvailable()) {
					if (_representation.enableSelection) {
						var pubSelIcon = knimeService.createStackedIcon('check-square-o', 'angle-right', 'faded left sm', 'right bold');
						var pubSelCheckbox = knimeService.createMenuCheckbox('publishSelectionCheckbox', _value.publishSelection, function() {
							if (this.checked) {
								_value.publishSelection = true;
								publishCurrentSelection();
							} else {
								_value.publishSelection = false;
							}
						});
						knimeService.addMenuItem('Publish selection', pubSelIcon, pubSelCheckbox);
						if (_value.publishSelection && selection && Object.keys(selection).length > 0) {
							publishCurrentSelection();
						}
						if (!_representation.singleSelection) {
							var subSelIcon = knimeService.createStackedIcon('check-square-o', 'angle-double-right', 'faded right sm', 'left bold');
							var subSelCheckbox = knimeService.createMenuCheckbox('subscribeSelectionCheckbox', _value.subscribeSelection, function() {
								if (this.checked) {
									knimeService.subscribeToSelection(_representation.table.id, selectionChanged);
								} else {
									knimeService.unsubscribeSelection(_representation.table.id, selectionChanged);
								}
							});
							knimeService.addMenuItem('Subscribe to selection', subSelIcon, subSelCheckbox);
							if (_value.subscribeSelection) {
								knimeService.subscribeToSelection(_representation.table.id, selectionChanged);
							}
						}
					}
					if (_representation.subscriptionFilterIds && _representation.subscriptionFilterIds.length > 0) {
						if (_representation.enableSelection) {
							knimeService.addMenuDivider();
						}

						/*var pubFilIcon = knimeService.createStackedIcon('filter', 'angle-right', 'faded left sm', 'right bold');
						var pubFilCheckbox = knimeService.createMenuCheckbox('publishFilterCheckbox', _value.publishFilter, function() {
							if (this.checked) {
								//publishFilter = true;
							} else {
								//publishFilter = false;
							}
						});
						knimeService.addMenuItem('Publish filter', pubFilIcon, pubFilCheckbox);
						if (_value.publishFilter) {
							//TODO
						}*/
						$.fn.dataTable.ext.search.push(function(settings, searchData, index, rowData, counter) {
							if (currentFilter) {
								return knimeTable.isRowIncludedInFilter(index, currentFilter);
							}
							return true;
						});
						var subFilIcon = knimeService.createStackedIcon('filter', 'angle-double-right', 'faded right sm', 'left bold');
						var subFilCheckbox = knimeService.createMenuCheckbox('subscribeFilterCheckbox', _value.subscribeFilter, function() {
							if (this.checked) {
								knimeService.subscribeToFilter(_representation.table.id, filterChanged, _representation.subscriptionFilterIds);
							} else {
								knimeService.unsubscribeFilter(_representation.table.id, filterChanged);
							}
						});
						knimeService.addMenuItem('Subscribe to filter', subFilIcon, subFilCheckbox);
						if (_value.subscribeFilter) {
							knimeService.subscribeToFilter(_representation.table.id, filterChanged, _representation.subscriptionFilterIds);
						}
					}
				}
			}
			
			if (_representation.enableSelection) {
				if (_representation.singleSelection) {
					// Handle click on clear selection button
					var clearSelectionButton = $('#clear-selection-button').get(0);
					if (clearSelectionButton) {
						clearSelectionButton.addEventListener('click', function() {
							selectAll(false);
						});
					}
					// Handle click on radio button to set selection and publish event
					$('#knimePagedTable tbody').on('change', 'input[type="radio"]', function() {
						selection = {};
						selection[this.value] = this.checked;
						if (knimeService && knimeService.isInteractivityAvailable() && _value.publishSelection) {
							if (this.checked) {
								knimeService.setSelectedRows(_representation.table.id, [this.value], selectionChanged);
							}
						}
					});
				} else {
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
					$('#knimePagedTable tbody').on('change', 'input[type="checkbox"]', function() {
						//var el = $('#checkbox-select-all').get(0);
						//var selected = el.checked ? !this.checked : this.checked;
						// we could call delete _value.selection[this.value], but the call is very slow 
						// and we can assume that a user doesn't click on a lot of checkboxes
						selection[this.value] = this.checked;
						// in either case the row is not partially selected
						var partialIndex = partialSelectedRows.indexOf(this.value);
						if (partialIndex > -1) {
							partialSelectedRows.splice(partialIndex, 1);
						}

						if (this.checked) {
							if (knimeService && knimeService.isInteractivityAvailable() && _value.publishSelection) {
								knimeService.addRowsToSelection(_representation.table.id, [this.value], selectionChanged);
							}
						} else {
							if (_value.hideUnselected) {
								dataTable.draw('full-hold');
							}
							if (knimeService && knimeService.isInteractivityAvailable() && _value.publishSelection) {
								knimeService.removeRowsFromSelection(_representation.table.id, [this.value], selectionChanged);
							}
						}
						checkSelectAllState();
					});
					if (knimeService && _representation.enableClearSelectionButton) {
						knimeService.addButton('pagedTableClearSelectionButton', 'minus-square-o', 'Clear Selection', function() {
							selectAll(false, true);
						});
					}
					dataTable.on('search.dt', function () {
						checkSelectAllState();
					});
				}
				dataTable.on('draw.dt', function () {
					setSelectionOnPage();
				});
			}
			
			if (_representation.enableColumnSearching) {
				dataTable.columns().every(function () {
			        var that = this;
			        $('input', this.footer()).on('keyup change', function () {
			            if (that.search() !== this.value) {
			                that.search(this.value).draw();
			            }
			        });
			    });
			}
			
			//load all data
			setTimeout(function() {
				var initialChunkSize = 100;
				addDataToTable(_representation.initialPageSize, initialChunkSize);
			}, 0);

		} catch (err) {
			if (err.stack) {
				alert(err.stack);
			} else {
				alert (err);
			}
		}
	}
	
	addDataToTable = function(startIndex, chunkSize) {
		var startTime = new Date().getTime();
		var tableSize = knimeTable.getNumRows()
		var endIndex  = Math.min(tableSize, startIndex + chunkSize);
		var chunk = getDataSlice(startIndex, endIndex);
		dataTable.rows.add(chunk);
		var endTime = new Date().getTime();
		var chunkDuration = endTime - startTime;
		var newChunkSize = chunkSize;
		if (startIndex + chunkSize < tableSize) {
			$('#knimePagedTable_info').html(
				'<strong>Loading data ('
				+ endIndex + ' of ' + tableSize + ' records)</strong> - Displaying '
				+ 1 + ' to ' + Math.min(tableSize, _representation.initialPageSize) 
				+ ' of ' + tableSize + ' entries.');
			if (chunkDuration > 300) {
				newChunkSize = Math.max(1, Math.floor(chunkSize / 2));
			} else if (chunkDuration < 100) {
				newChunkSize = chunkSize * 2;
			}
			setTimeout((function(i, c) {
				return function() {
					addDataToTable(i, c);
				};
			})(startIndex + chunkSize, newChunkSize), chunkDuration);
		} else {
			$('#knimePagedTable_paginate').css('display', 'block');
			applyViewValue();
			dataTable.draw();
			finishInit();
		}
	}
	
	getDataSlice = function(start, end) {
		if (typeof end == 'undefined') {
			end = knimeTable.getNumRows();
		}
		var data = [];
		for (var i = start; i < Math.min(end, knimeTable.getNumRows()); i++) {
			var row = knimeTable.getRows()[i];
			var dataRow = [];
			if (_representation.enableSelection) {
				dataRow.push(row.rowKey);
			}
			if (_representation.displayRowIndex) {
				dataRow.push(i);
			}
			if (_representation.displayRowIds || _representation.displayRowColors) {
				var string = '';
				if (_representation.displayRowColors) {
					string += '<div class="knimeTableRowColor" style="background-color: '
							+ knimeTable.getRowColors()[i]
							+ '; width: 16px; height: 16px; '
							+ 'display: inline-block; margin-right: 5px; vertical-align: text-bottom;"></div>'
				}
				if (_representation.displayRowIds) {
					string += '<span class="rowKey">' + row.rowKey + '</span>';
				}
				dataRow.push(string);
			}
			var dataRow = dataRow.concat(row.data);
			data.push(dataRow);
		}
		return data;
	}
	
	applyViewValue = function() {
		if (_representation.enableSearching && _value.filterString) {
			dataTable.search(_value.filterString);
		}
		if (_representation.enableColumnSearching && _value.columnFilterStrings) {
			for (var i = 0; i < _value.columnFilterStrings.length; i++) {
				var curValue = _value.columnFilterStrings[i];
				if (curValue.length > 0) {
					var column = dataTable.column(i);
					$('input', column.footer()).val(curValue);
					column.search(curValue);
				}
			}
		}
		if (_representation.enablePaging && _value.currentPage) {
			setTimeout(function() {
				dataTable.page(_value.currentPage).draw('page');
			}, 0);
		}
	}
	
	finishInit = function() {
		//Used to collect all checkboxes here, 
		//but now keeping selection and checkbox state separate and applying checked state on every call of draw()
		/*allCheckboxes = dataTable.column(0).nodes().to$().children();*/
		initialized = true;
	}
	
	cellClickHandler = function() {
		var td = this;
		var cell = dataTable.cell(td);
		selectCell(cell);
	}
	
	editableCellDoubleClickHandler = function() {
		var td = this;
		var cell = dataTable.cell(td);
		createCellEditor(cell);
	}
	
	selectedCellFocusOutHandler = function(e) {		
		if (!selectedCell) {
			return;
		}
		var $td = $(selectedCell.node());
		if ($(e.target).is($td)) {
			unselectCurrentCell();
		}
	}
	
	selectCell = function(cell) {
		if (!cell) {
			cell = selectedCell;
		}
		
		var $td = $(cell.node());

		if (!isEqualCell(cell, selectedCell)) {
			unselectCurrentCell();
			selectedCell = cell;
			$td.addClass('selected');
			catchTdEventsOn($td);
		}
		
		$td.attr('tabindex', -1);
		$td.focus();
	}
	
	unselectCurrentCell = function() {
		if (!selectedCell) {
			return;
		}
		
		var $td = $(selectedCell.node());
		$td.removeClass('selected');
		catchTdEventsOff($td);
		
		selectedCell = undefined;
	}	
	
	createCellEditor = function(cell, cellValue) {
		var $td = $(cell.node());
		catchTdEventsOff($td);
		
		var editor = createCellEditorComponent(cell, cellValue);
		var editorComponent = editor.getComponent();
		
		var tdHeight = $td.height();				
		$td.empty()
			.append(editorComponent);	
		// need to set up height after adding the editor to the cell, otherwise it won't work in FF
		editorComponent.height(tdHeight);
		editorComponent.focus();

		$td.off('click', cellClickHandler);
		$td.off('dblclick', editableCellDoubleClickHandler);
		
		var editFinishCallback = function() {
			restoreListeners();
			var newValue = editor.getValue();
			$td.empty()
				.append(newValue);
			setCellValue(cell, newValue);
		}
		
		var editCancelCallback = function() {			
			restoreListeners();
			$td.attr('tabindex', -1);
			$td.focus();
			cell.invalidate();
		}
		
		var restoreListeners = function() {
			$td.on('dblclick', editableCellDoubleClickHandler);
			$td.on('click', cellClickHandler);
			catchTdEventsOn($td);
			editorComponent.off('focusout');
			editorComponent.off('keydown');
		}
		
		editorComponent.on('focusout', editFinishCallback);
		editorComponent.on('keydown', function(e) {
			switch (e.key) {
				case 'Enter':
					editFinishCallback();
					selectCell(getCellByShift(cell, 1, 0));
					break;
				case 'Escape':
					e.stopPropagation();
					editCancelCallback();
					break;
				case 'Tab':
					e.preventDefault();
					editFinishCallback();
					if (e.shiftKey) {
						selectCell(getCellByShift(cell, 0, -1));
					} else {
						selectCell(getCellByShift(cell, 0, 1));
					}
				case 'ArrowUp':
				case 'ArrowDown':
				case 'ArrowLeft':
				case 'ArrowRight':
					if (cellValue) {
						editFinishCallback();
						cellArrowKeyDownHandler(e);
					}
					break;
			}
		});
	}
	
	createCellEditorComponent = function(cell, cellValue) {
		// get column type
		var colInd = cell.index().column - infoColsCount;
		var colType = knimeTable.getKnimeColumnTypes()[colInd];
		var editor = createEditor(colType);
		editor.setValue(cellValue !== undefined ? cellValue : cell.data());
		return editor;
	}
	
	getCellByShift = function(cell, rowShift, columnShift) {
		if (!cell) {
			return null;
		}
		var ind = cell.index();
		var newRowInd = ind.row + rowShift;
		var newColInd = ind.column + columnShift - infoColsCount;
		var pageInfo = dataTable.page.info();
		if (newRowInd < pageInfo.start || newRowInd > pageInfo.end - 1 || newColInd < 0 || newColInd >= knimeTable.getColumnNames().length) {			 
			return null;
			// according to documentation https://datatables.net/reference/api/page.info() info.end gives index of the last displayed row on the page,
			// however it returns the value which is +1
		}
		return dataTable.cell(newRowInd, newColInd + infoColsCount);
	}
	
	setCellValue = function(cell, newValue) {
		var index = cell.index();
		dataTable.data()[index.row][index.column] = newValue;
		
		var rowKey = knimeTable.getRows()[index.row].rowKey;
		var colName = knimeTable.getColumnNames()[index.column - infoColsCount];
		if (_value.editorChanges.changes[rowKey] === undefined) {
			_value.editorChanges.changes[rowKey] = {};
		}
		_value.editorChanges.changes[rowKey][colName] = newValue;
		
		cell.invalidate();
	}
	
	catchTdEventsOn = function($td) {
		$td.on('keydown', selectedCellKeyDownHandler);
		$td.on('focusout', selectedCellFocusOutHandler);
		$td.on('paste', pasteHandler);
	}
	
	catchTdEventsOff = function($td) {
		$td.off('keydown', selectedCellKeyDownHandler);
		$td.off('focusout', selectedCellFocusOutHandler);
		$td.off('paste', pasteHandler);
		$td.removeAttr('tabindex');
	}
	
	selectedCellKeyDownHandler = function(e) {
		var ctrlKey = isMacOS() ? e.metaKey : e.ctrlKey;
		switch (e.key) {
			case 'ArrowUp':
			case 'ArrowDown':
		    case 'ArrowLeft':
			case 'ArrowRight':
				cellArrowKeyDownHandler(e);
		    	break;
		    case 'Home':
		    	if (ctrlKey) {
		    		selectCell(getTopLeftCell());
		    	} else {
		    		selectCell(getFirstCellInRow(selectedCell));
		    	}
		    	break;
		    case 'End':
		    	if (ctrlKey) {
		    		selectCell(getBottomRightCell());
		    	} else {
		    		selectCell(getLastCellInRow(selectedCell));
		    	}
		    	break;
			case 'Enter':
				selectCell(getCellByShift(selectedCell, 1, 0));  // same as ArrowDown
				break;
		    case 'Delete':
		    	if (isEditableCell(selectedCell)) {
		    		setCellValue(selectedCell, null);
		    	}
		    	break;
		    case 'Backspace':
		    	if (isEditableCell(selectedCell)) {
		    		e.preventDefault();
		    		createCellEditor(selectedCell, null);
		    	}
		    	break;
			default:
			   if (isEditableCell(selectedCell) && e.key.length == 1 && !ctrlKey) {  // test whether the key is printable and no CTRL is pressed
				   e.preventDefault();
			       createCellEditor(selectedCell, e.key);			       
			   }
		}
	}

	cellArrowKeyDownHandler = function(e) {
		var ctrlKey = isMacOS() ? e.metaKey : e.ctrlKey;
		switch (e.key) {
			case 'ArrowUp':
				if (ctrlKey) {
					selectCell(getFirstCellInColumn(selectedCell));
				} else {
					selectCell(getCellByShift(selectedCell, -1, 0));
				}
				break;
			case 'ArrowDown':
				if (ctrlKey) {
					selectCell(getLastCellInColumn(selectedCell));
				} else {
					selectCell(getCellByShift(selectedCell, 1, 0));
				}
				break;
			case 'ArrowLeft':
				if (ctrlKey) {
					selectCell(getFirstCellInRow(selectedCell));  // same as Home
				} else {
					selectCell(getCellByShift(selectedCell, 0, -1));
				}
				break;
			case 'ArrowRight':
				if (ctrlKey) {
					selectCell(getLastCellInRow(selectedCell));  // same as End
				} else {
					selectCell(getCellByShift(selectedCell, 0, 1));
				}
				break;
		}
	}
	
	pasteHandler = function(e) {		
		var data = e.originalEvent.clipboardData.getData('text');
		var values = [];		
		var lines = data.replace(/\r/g, '').split('\n');
		if (lines.length > 0 && lines[lines.length - 1] === '') {
			// when copying from Excel the last line is always an empty string,
			// while from Google Sheets this is not the case
			lines.pop();
		}
		for (var i = 0; i < lines.length; i++) {
			values.push(lines[i].split('\t'));
		}
		pasteMultipleValues(selectedCell, values);
	}
	
	pasteMultipleValues = function(cell, values) {
		var startCell = cell;

		// validation
		if (!iterateSubtable(startCell, values, function(cell, value) {
			if (!cell) {
				alert('Cannot paste the values. Range out of bounds.');
				return false;
			}

			var colName = knimeTable.getColumnNames()[cell.index().column - infoColsCount];
			if (!isEditableCell(cell)) {
				alert('Cannot paste the values as column "' + colName +'" is not editable.');
				return false;
			}

			var convertRes = convertValueToCellType(cell, value);
			if (!convertRes.status) {
				alert('Cannot paste the values as value "' + value + '" is not compatible with type "' + res.type + '" of column "' + colName + '".');
				return false;
			}

			return true;
		})) {			
			return;
		}

		// pasting
		iterateSubtable(startCell, values, function(cell, value) {			
			setCellValue(cell, convertValueToCellType(cell, value).value);
			return true;
		});		
	}

	iterateSubtable = function(cell, values, callback) {		
		for (var i = 0; i < values.length; i++) {			
			var row = values[i];
			for (var j = 0; j < row.length; j++) {				
				if (!callback(cell, row[j])) {
					return false;
				}
				if (!cell) {
					return;
				}
				if (j < row.length - 1) {
					cell = getCellByShift(cell, 0, 1);
				}
			}
			if (i < values.length - 1) {
				cell = getCellByShift(cell, 1, -(row.length - 1));
			}
		}
		return true;
	}

	convertValueToCellType = function(cell, value) {
		res = { 
			status: false, 
			value: undefined,
			type: knimeTable.getKnimeColumnTypes()[cell.index().column - infoColsCount]
		};
		switch (res.type) {
			case 'String':
				res.value = value.toString();
				res.status = true;
				break;
			case 'Number (integer)':
			case 'Number (long)':
				res.value = filterInt(value);
				res.status = !isNaN(res.value);
				break;
			case 'Number (double)':
				res.value = Number(value.replace(',', '.'));
				res.status = !isNaN(res.value);
				break;
		}
		return res;
	}

	/**
	 * taken from https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/parseInt
	 */
	filterInt = function(value) {
		if (/^(\-|\+)?([0-9]+)$/.test(value)) {
			return Number(value);
		}
		return NaN;
	}
		
	getFirstCellInRow = function(cell) {
		return dataTable.cell(cell.index().row, infoColsCount);
	}
	
	getLastCellInRow = function(cell) {
		return dataTable.cell(cell.index().row, knimeTable.getColumnNames().length + infoColsCount - 1);
	}
	
	getFirstCellInColumn = function(cell) {
		return dataTable.cell(dataTable.page.info().start, cell.index().column);
	}
	
	getLastCellInColumn = function(cell) {
		return dataTable.cell(dataTable.page.info().end - 1, cell.index().column);
		// see the comment in getCellByShift about end - 1
	}
	
	getTopLeftCell = function() {
		return dataTable.cell(dataTable.page.info().start, infoColsCount);
	}
	
	getBottomRightCell = function() {
		return dataTable.cell(dataTable.page.info().end - 1, knimeTable.getColumnNames().length + infoColsCount - 1);
		// see the comment in getCellByShift about end - 1
	}
	
	isEditableCell = function(cell) {
		var index = cell.index();
		var colName = knimeTable.getColumnNames()[index.column - infoColsCount];
		return _representation.editableColumns.indexOf(colName) !== -1;
	}
	
	isEqualCell = function(cell1, cell2) {
		if (cell1 && cell2) {
			var index1 = cell1.index();
			var index2 = cell2.index();
			return index1.row === index2.row && index1.column === index2.column;
		} else {
			return cell1 === cell2;
		}
	}
	
	
	selectAll = function(all, ignoreSearch) {
		// cannot select all rows before all data is loaded
		if (!initialized) {
			setTimeout(function() {
				selectAll(all);
			}, 500);
		}
		
		if (ignoreSearch) {
			selection = {};
			partialSelectedRows = [];
		}
		if (all || !ignoreSearch) {
			var selIndices = dataTable.column(0, { 'search': 'applied' }).data();
			for (var i = 0; i < selIndices.length; i++) {
				selection[selIndices[i]] = all;
				var pIndex = partialSelectedRows.indexOf(selIndices[i]);
				if (pIndex > -1) {
					partialSelectedRows.splice(pIndex, 1);
				}
			}
		}
		checkSelectAllState();
		setSelectionOnPage();
		
		if (_value.hideUnselected) {
			dataTable.draw();
		}
		publishCurrentSelection();
	}
	
	checkSelectAllState = function() {
		var selectAllCheckbox = $('#checkbox-select-all').get(0);
		if (!selectAllCheckbox) { return; }
		var someSelected = false;
		var allSelected = true;
		var selIndices = dataTable.column(0, { 'search': 'applied' }).data();
		if (selIndices.length < 1) {
			allSelected = false;
		}
		for (var i = 0; i < selIndices.length; i++) {
			if (selection[selIndices[i]]) {
				someSelected = true;
			} else {
				allSelected = false;
			}
			if (partialSelectedRows.indexOf(selIndices[i]) > -1) {
				someSelected = true;
				allSelected = false;
			}
			if (someSelected && !allSelected) {
				break;
			}
		}
		_value.selectAll = allSelected;
	    selectAllCheckbox.checked = allSelected;
	    selectAllCheckbox.disabled = (selIndices.length < 1);
	    var indeterminate = someSelected && !allSelected;
	    
	    if('indeterminate' in selectAllCheckbox){
			// Set visual state of "Select all" control as 'indeterminate'
			selectAllCheckbox.indeterminate = indeterminate;
		}
	    _value.selectAllIndeterminate = indeterminate;
	}
	
	setSelectionOnPage = function() {
		var curCheckboxes = dataTable.column(0, {page:'current'}).nodes().to$().children();
		for (var i = 0; i < curCheckboxes.length; i++) {
			var checkbox = curCheckboxes[i];
			checkbox.checked = selection[checkbox.value];
			if ('indeterminate' in checkbox) {
				if (!checkbox.checked && partialSelectedRows.indexOf(checkbox.value) > -1) {
					checkbox.indeterminate = true;
				} else {
					checkbox.indeterminate = false;
				}
			}
		}
	}
	
	publishCurrentSelection = function() {
		if (knimeService && knimeService.isInteractivityAvailable() && _value.publishSelection) {
			var selArray = [];
			for (var rowKey in selection) {
				if (!selection.hasOwnProperty(rowKey)) {
			        continue;
			    }
				if (selection[rowKey]) {
					selArray.push(rowKey);
				}
			}
			knimeService.setSelectedRows(_representation.table.id, selArray, selectionChanged);
		}
	}
	
	selectionChanged = function(data) {
		// cannot apply selection changed event before all data is loaded
		if (!initialized) {
			setTimeout(function() {
				selectionChanged(data);
			}, 500);
		}
		
		// apply changeSet
		if (data.changeSet) {
			if (data.changeSet.removed) {
				for (var i = 0; i < data.changeSet.removed.length; i++) {
					selection[data.changeSet.removed[i]] = false;
				}
			}
			if (data.changeSet.added) {
				for (var i = 0; i < data.changeSet.added.length; i++) {
					selection[data.changeSet.added[i]] = true;
				}
			}
		}
		partialSelectedRows = knimeService.getAllPartiallySelectedRows(_representation.table.id);
		checkSelectAllState();
		setSelectionOnPage();
		if (_value.hideUnselected) {
			dataTable.draw();
		}
	}
	
	filterChanged = function(data) {
		// cannot apply selection changed event before all data is loaded
		if (!initialized) {
			setTimeout(function() {
				filterChanged(data);
			}, 500);
		}
		currentFilter = data;
		dataTable.draw();
	}
	
	isColumnSortable = function (colType) {
		var allowedTypes = ['boolean', 'string', 'number', 'dateTime'];
		return allowedTypes.indexOf(colType) >= 0;
	}
	
	isColumnSearchable = function (colType) {
		var allowedTypes = ['boolean', 'string', 'number', 'dateTime', 'undefined'];
		return allowedTypes.indexOf(colType) >= 0;
	}

	isMacOS = function() {
		return navigator.platform.indexOf('Mac') !== -1;
	}
	
	table_viewer.validate = function() {
		return true;
	};
	
	table_viewer.getComponentValue = function() {
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
		var selSub = document.getElementById('subscribeSelectionCheckbox');
		if (selSub) {
			_value.subscribeSelection = selSub.checked;
		}
		return _value;
	};
	
	return table_viewer;
}();