(scorer_namespace = function() {
	
	var scorer = {};
	var _representation, _value;
	var title;
	var subtitle;
	var confusionMatrix;
	var classes;
	var confusionMatrixWithRates;
	var classStatistics;
	var overallStatistics;
	var rowsNumber;
	var tableID;
	var keyStore;
	var body;
	var confusionTable;


	scorer.init = function(representation, value) {
		_representation = representation;
		_value = value;
		
        if (_representation.warningMessage && _representation.showWarningsInView) {
			knimeService.setWarningMessage(_representation.warnMessage);
		}

		createPage();

		if (_representation.enableViewControls) {
			drawControls();
		}
	}

	function createPage() {
		title = _value.title;
		subtitle = _value.subtitle;
		confusionMatrix = new kt();
		confusionMatrix.setDataTable(_representation.confusionMatrix);
		classes = confusionMatrix.getColumnNames();
		rowsNumber = countRowsNumber(confusionMatrix);
		confusionMatrixWithRates = createConfusionMatrixWithRates(confusionMatrix, classes.length + 1, classes.length + 1);
		classStatistics = new kt();
		classStatistics.setDataTable(_representation.classStatisticsTable);
		overallStatistics = new kt();
		overallStatistics.setDataTable(_representation.overallStatisticsTable);	
		tableID = confusionMatrix.getTableId();
		keyStore = _representation.keystore;

		body = document.querySelector('body');

		//Title and subtitle
		var h1 = document.createElement('h1');
		h1.appendChild(document.createTextNode(title));
		h1.setAttribute('id', 'title');
		h1.setAttribute('class', 'knime-title');
		body.appendChild(h1);
		var h4 = document.createElement('h4');
		h4.appendChild(document.createTextNode(subtitle));
		h4.setAttribute('id', 'subtitle');
		h4.setAttribute('class', 'knime-subtitle');
		h4.setAttribute('align', 'center');
		body.appendChild(h4);

		//Building the confusion matrix table
		createConfusionMatrixTable();

		// //Building the class statistics table
		createClassStatisticsTable();

		// //Table containing the accuracy and Cohen's kappa values
		createOverallStatisticsTable();

		//knimeService.subscribeToSelection(tableID, selectionChanged);
	}

	createConfusionMatrixTable = function() {
		var table = document.createElement('table');
		table.setAttribute('id', 'knime-confusion-matrix');
		table.setAttribute('class', 'center knime-table');
		var caption = document.createElement('caption');
		caption.setAttribute('class', 'knime-label knime-table-header')
		caption.appendChild(document.createTextNode('Confusion Matrix'));
		table.appendChild(caption);
		
		var tHeader = document.createElement('thead');		
		//header row
		var tRow = document.createElement('tr');
		tRow.setAttribute('class', 'knime-table-row knime-table-header')
		var th = document.createElement('th');		
		th.appendChild(document.createTextNode('Rows Number : \n' + rowsNumber));
		th.setAttribute('class', 'rowsNumber knime-table-cell knime-table-header knime-string');
		th.setAttribute('style', 'border-right-width: 2px');
		th.style.backgroundColor = _representation.headerColor;
		tRow.appendChild(th);
		for (var i = 0; i < classes.length; i++) {
			th = document.createElement('th');
			th.appendChild(document.createTextNode(classes[i] + ' (Predicted)'));
			th.style.backgroundColor = _representation.headerColor;
			th.setAttribute('title', classes[i] + ' (Predicted)');
			th.setAttribute('class', 'knime-table-cell knime-table-header knime-integer')
			tRow.appendChild(th);
		}
		tHeader.appendChild(tRow);
		table.appendChild(tHeader);
		
		var tBody = document.createElement('tbody');
		var rateCellDescription = 'This is the the correct prediction on this row (or column) divided by the sum of all values on this line.';
		for (var row = 0; row < confusionMatrix.getNumRows(); row++) {
			tRow = document.createElement('tr');
			tRow.setAttribute('class', 'knime-table-row');
			th = document.createElement('th');
			th.appendChild(document.createTextNode(classes[row] + ' (Actual)'));
			th.style.backgroundColor = _representation.headerColor;
			th.setAttribute('title', classes[row] + ' (Actual)');
			th.setAttribute('class', 'knime-table-cell knime-table-header knime-string');
			tRow.appendChild(th);
			for (var col = 0; col < confusionMatrix.getNumColumns(); col++) {
				var td = document.createElement('td');
				td.appendChild(document.createTextNode(confusionMatrix.getCell(row, col)));
				td.setAttribute('data-row', row);
				td.setAttribute('data-col', col);
				td.setAttribute('class', 'knime-table-cell knime-integer');
				if (row === col) {
					td.style.backgroundColor = _representation.diagonalColor;
				}
				td.onclick = cellClicked;
				tRow.appendChild(td);
			}
			var lastCell = document.createElement('td');
			var cellValue = confusionMatrixWithRates[row][confusionMatrixWithRates.length-1];
			if (_value.displayFloatAsPercent === true) {
        if ( (cellValue * 100).toFixed(2) >= 99.995 ) {
          lastCell.appendChild(document.createTextNode('> 99.99 %'));
        } else {
          lastCell.appendChild(document.createTextNode((cellValue * 100).toFixed(2) + '\xA0%'));
        }
			} else {
				lastCell.appendChild(document.createTextNode(cellValue.toFixed(3)));
			}
			lastCell.setAttribute('class', 'rateCell knime-table-cell knime-double');
			lastCell.setAttribute('title', rateCellDescription);
			tRow.appendChild(lastCell);
			tBody.appendChild(tRow);
		}
		tRow = document.createElement('tr');
		tRow.setAttribute('class', 'knime-table-row');
		td = document.createElement('td');
		td.setAttribute('class', 'no-border');
		tRow.appendChild(td);
		for (var col = 0; col < confusionMatrix.getNumRows(); col++) {
			td = document.createElement('td');			
			var cellValue = confusionMatrixWithRates[confusionMatrixWithRates.length-1][col];
			if (_value.displayFloatAsPercent === true) {
        if ( (cellValue * 100).toFixed(2) >= 99.995 ) {
          td.appendChild(document.createTextNode('> 99.99 %'));
        } else {
          td.appendChild(document.createTextNode((cellValue * 100).toFixed(2) + '\xA0%'));
        }
			} else {
				td.appendChild(document.createTextNode(cellValue.toFixed(3)));
			}
			td.setAttribute('class', 'rateCell knime-table-cell knime-double');
			td.setAttribute('title', rateCellDescription);	
			tRow.appendChild(td);
		}
		tBody.appendChild(tRow);
		table.appendChild(tBody);
		confusionTable = table;
		body.appendChild(table);

		toggleRowsNumberDisplay();
		toggleConfusionMatrixRatesDisplay();
	}
	
	countRowsNumber = function(confusionMatrix) {
		var count = 0;
		for (var i = 0; i < confusionMatrix.getNumRows(); i++) {
			for (var j = 0; j < confusionMatrix.getRow(i).data.length; j++) {
				count += confusionMatrix.getCell(i, j);
			}
		}
		return count;
	}

	createConfusionMatrixWithRates = function(confusionMatrix, numRows, numColumns) {
		var confusionMatrixWithRates = new Array(numRows); 
		for(var i = 0; i < numRows; i++) {
			confusionMatrixWithRates[i] = new Array(numColumns); 
		}
		for (var i = 0; i < confusionMatrix.getNumRows(); i++) {
			var currentRow = confusionMatrix.getRow(i);
            for (var j = 0; j < confusionMatrix.getRow(i).data.length; j++) {
                confusionMatrixWithRates[i][j] = confusionMatrix.getCell(i, j);
            }
        }
        for (var i = 0; i < confusionMatrixWithRates.length-1; i++) {
            var rowSum = 0;
            for (var j = 0; j < confusionMatrixWithRates[i].length-1; j++) {
                rowSum += confusionMatrixWithRates[i][j];
            }
            confusionMatrixWithRates[i][confusionMatrixWithRates.length-1] = confusionMatrixWithRates[i][i] / rowSum;
        }
        for (var i = 0; i < confusionMatrixWithRates.length-1; i++) {
            var columnSum = 0;
            for (var j = 0; j < confusionMatrixWithRates.length-1; j++) {
                columnSum += confusionMatrixWithRates[j][i];
            }
            confusionMatrixWithRates[confusionMatrixWithRates.length-1][i] = confusionMatrixWithRates[i][i] / columnSum;
        }
		return confusionMatrixWithRates;
	}

	cellClicked = function(event) {
		confusionTable.querySelectorAll('td').forEach(function (cell) {
			cell.classList.remove('knime-selected');
		});
		this.classList.add('knime-selected');
		if (knimeService.isInteractivityAvailable()) {
			var rowIds = keyStore[this.dataset.row][this.dataset.col];
			knimeService.setSelectedRows(tableID, rowIds);
		}
	}
	
	createClassStatisticsTable = function() {
		var table = document.createElement('table');
		table.setAttribute('id', 'knime-class-statistics');
		table.setAttribute('class', 'center knime-table');
		if (classStatistics.getNumColumns() !== 0) {
			var caption = document.createElement('caption');
			caption.setAttribute('class', 'knime-label knime-table-header')
			caption.appendChild(document.createTextNode('Class Statistics'));
			table.appendChild(caption);
		}

		var tHeader = document.createElement('thead');
		var tRow = document.createElement('tr');
		tRow.setAttribute('class', 'knime-table-row');
		var statNames = ['Class'];
		Array.prototype.push.apply(statNames, classStatistics.getColumnNames());
		for (var i = 0; i < statNames.length; i++) {
			if (classStatistics.getNumColumns() !== 0) {
				var th = document.createElement('th');
				if (statNames[i] === 'Class') {
					th.setAttribute('class', 'knime-table-cell knime-table-header knime-string');
				} else if (statNames[i] === 'True Positives' || statNames[i] === 'False Positives'
					|| statNames[i] === 'True Negatives' || statNames[i] === 'False Negatives') {
					// cellValue is an integer
					th.setAttribute('class', 'knime-table-cell knime-table-header knime-integer');
				} else {
					// cellValue is a float
					th.setAttribute('class', 'knime-table-cell knime-table-header knime-double');
				}
				th.appendChild(document.createTextNode(statNames[i]));
				th.style.backgroundColor = _representation.headerColor;
				tRow.appendChild(th);
			}
		}
		tHeader.appendChild(tRow);
		table.appendChild(tHeader);

		tBody = document.createElement('tbody');
		var columnNames = classStatistics.getColumnNames();
		for (var row = 0; row < classStatistics.getNumRows(); row++) {
			tRow = document.createElement('tr');
			tRow.setAttribute('class', 'knime-table-row');
			if (classStatistics.getNumColumns() !== 0) {
				th = document.createElement('th');
				th.setAttribute('class', 'knime-table-cell knime-table-header');
				th.appendChild(document.createTextNode(classStatistics.getRow(row).rowKey));
				tRow.appendChild(th);
			}
			for (var col = 0; col < classStatistics.getNumColumns(); col++) {
				var td = document.createElement('td');
				var cellValue  = classStatistics.getCell(row, col);
				if (columnNames[col] === 'True Positives' || columnNames[col] === 'False Positives'
					|| columnNames[col] === 'True Negatives' || columnNames[col] === 'False Negatives') {
					// cellValue is an integer
					td.appendChild(document.createTextNode(cellValue));
					td.setAttribute('class', 'knime-table-cell knime-integer');
				} else {
					// cellValue is a float
					if (_value.displayFloatAsPercent === true) {
            if ( (cellValue * 100).toFixed(2) >= 99.995 ) {
              td.appendChild(document.createTextNode('> 99.99 %'));
            } else {
              td.appendChild(document.createTextNode((cellValue * 100).toFixed(2) + '\xA0%'));
            }
					} else {
						td.appendChild(document.createTextNode(cellValue.toFixed(3)));
					}
					td.setAttribute('class', 'knime-table-cell knime-double');
				}
				tRow.appendChild(td);
			}
			// last cell of each row has no border
			td = document.createElement('td');
			td.setAttribute('class', 'no-border');
			tRow.appendChild(td);
			tBody.appendChild(tRow);
		}
		// last row has one cell without any border
		tRow = document.createElement('tr');
		tRow.setAttribute('class', 'knime-table-row');
		td = document.createElement('td');
		td.setAttribute('class', 'no-border');
		tRow.appendChild(td);
		tBody.appendChild(tRow);
		table.appendChild(tBody);

		body.appendChild(table);

		toggleClassStatisticsDisplay();
	}

	createOverallStatisticsTable = function() {
		var table = document.createElement('table');
		table.setAttribute('id', 'knime-overall-statistics');
		table.setAttribute('class', 'center knime-table');
		if (overallStatistics.getNumColumns() !== 0) {
			var caption = document.createElement('caption');
			caption.setAttribute('class', 'knime-label knime-table-header')
			caption.appendChild(document.createTextNode('Overall Statistics'));
			table.appendChild(caption);		
		}

		var tHeader = document.createElement('thead');
		var tRow = document.createElement('tr');
		tRow.setAttribute('class', 'knime-table-row');
		var th = document.createElement('th');
		var statNames = overallStatistics.getColumnNames()
		for (var i = 0; i < statNames.length; i++) {
			th = document.createElement('th');
			if (statNames[i] === 'Correct Classified' || statNames[i] === 'Wrong Classified') {
				// cellValue is an integer
				th.setAttribute('class', 'knime-table-cell knime-table-header knime-integer');
			} else {
				// cellValue is a float
				th.setAttribute('class', 'knime-table-cell knime-table-header knime-double');
			}
			var headerText = statNames[i];
			if (statNames[i].indexOf('Cohen') > -1) {
				headerText = "Cohen's kappa (𝝹)";
			}
			th.appendChild(document.createTextNode(headerText));
			th.style.backgroundColor = _representation.headerColor;
			tRow.appendChild(th);
		}
		tHeader.appendChild(tRow);
		table.appendChild(tHeader);

		var tBody = document.createElement('tbody');
		tRow = document.createElement('tr');
		tRow.setAttribute('class', 'knime-table-row');
		var columnNames = overallStatistics.getColumnNames();
		for (var col = 0; col < overallStatistics.getNumColumns(); col++) {
			var td = document.createElement('td');
			var cellValue  = overallStatistics.getCell(0, col);
			if (columnNames[col] === 'Correctly Classified' || columnNames[col] === 'Incorrectly Classified') {
				// cellValue is an integer
				td.appendChild(document.createTextNode(cellValue));
				td.setAttribute('class', 'knime-table-cell knime-integer');
			} else {
				// cellValue is a float
				if (!_value.displayFloatAsPercent || columnNames[col].indexOf('Cohen') > -1) {
					td.appendChild(document.createTextNode(cellValue.toFixed(3)));
				} else {
          if ( (columnNames[col] === 'Overall Error') && ((cellValue * 100).toFixed(2) < 0.004) ) {
            td.appendChild(document.createTextNode('< 0.01 %'));
          }
          if ( (columnNames[col] === 'Overall Accuracy') && ((cellValue * 100).toFixed(2) >= 99.995) ) {
            td.appendChild(document.createTextNode('> 99.99 %'));
          }
					td.appendChild(document.createTextNode((cellValue * 100).toFixed(2) + '\xA0%'));
				}
				td.setAttribute('class', 'knime-table-cell knime-double');
			}
			tRow.appendChild(td);
		}
		// last cell of each row has no border
		td = document.createElement('td');
		td.setAttribute('class', 'no-border');
		tRow.appendChild(td);
		tBody.appendChild(tRow);
		// last row has one cell without any border
		tRow = document.createElement('tr');
		tRow.setAttribute('class', 'knime-table-row');
		td = document.createElement('td');
		td.setAttribute('class', 'no-border');
		tRow.appendChild(td);
		tBody.appendChild(tRow);
		table.appendChild(tBody);

		body.appendChild(table);

		toggleOverallStatisticsDisplay();	
	}

	drawControls = function() {
		if (!knimeService) {
			// TODO: error handling?
			return;
		}
		
		if (_representation.displayFullscreenButton) {
			knimeService.allowFullscreen();
		}
		
	    if (!_representation.enableViewControls) return;
	    
	    var titleEdit = _representation.enableTitleEditing;
	    var subtitleEdit = _representation.enableSubtitleEditing;
	    var classStatsDisplay = _representation.enableClassStatisticsConfig;	    
	    var overallStatsDisplay = _representation.enableOverallStatisticsConfig;	    
	    var CMRatesDisplay = _representation.enableConfusionMatrixRatesConfig;	    
	    var RowsNumberDisplay = _representation.enableRowsNumberConfig;	    
	    
	    if (titleEdit || subtitleEdit) {	    	    
	    	if (titleEdit) {
	    		var chartTitleText = knimeService.createMenuTextField('chartTitleText', _value.title, function() {
	    			if (_value.title != this.value) {
						_value.title = this.value;
						updateTitles(true);
					}
	    		}, true);
	    		knimeService.addMenuItem('Chart Title:', 'header', chartTitleText);
	    	}
	    	if (subtitleEdit) {
	    		var chartSubtitleText = knimeService.createMenuTextField('chartSubtitleText', _value.subtitle, function() {
	    			if (_value.subtitle != this.value) {
						_value.subtitle = this.value;
						updateTitles(true);
					}
	    		}, true);
	    		var mi = knimeService.addMenuItem('Chart Subtitle:', 'header', chartSubtitleText, null, knimeService.SMALL_ICON);
	    	}
	    	if (RowsNumberDisplay || CMRatesDisplay || classStatsDisplay || overallStatsDisplay) {
	    		knimeService.addMenuDivider();
	    	}
	    }

	    if (RowsNumberDisplay) {
	    	var switchRowsNumberDisplay = knimeService.createMenuCheckbox('switchRowsNumberDisplay', _value.displayTotalRows, function() {
	    		if (_value.displayTotalRows != this.checked) {
	    			_value.displayTotalRows = this.checked;
	    			toggleRowsNumberDisplay();
	    		}
	    	});
	    	knimeService.addMenuItem("Display number of rows: ", 'table', switchRowsNumberDisplay);
	    }

	    if (CMRatesDisplay) {
	    	var switchCMRatesDisplay = knimeService.createMenuCheckbox('switchCMRatesDisplay', _value.displayConfusionMatrixRates, function() {
	    		if (_value.displayConfusionMatrixRates != this.checked) {
					_value.displayConfusionMatrixRates = this.checked;
					toggleConfusionMatrixRatesDisplay();
				}
	    	});
	    	knimeService.addMenuItem("Display confusion matrix summary values: ", 'table', switchCMRatesDisplay);
	    }

	    if (classStatsDisplay) {
	    	var switchClassStatsDisplay = knimeService.createMenuCheckbox('switchClassStatsDisplay', _value.displayClassStatsTable, function() {
	    		if (_value.displayClassStatsTable != this.checked) {
					_value.displayClassStatsTable = this.checked;
					toggleClassStatisticsDisplay();
				}
	    	});
	    	knimeService.addMenuItem("Display class statistics: ", 'table', switchClassStatsDisplay);
	    }

	    if (overallStatsDisplay) {
	    	var switchOverallStatsDisplay = knimeService.createMenuCheckbox('switchOverallStatsDisplay', _value.displayOverallStats, function() {
	    		if (_value.displayOverallStats != this.checked) {
					_value.displayOverallStats = this.checked;
					toggleOverallStatisticsDisplay();
				}
	    	});
	    	knimeService.addMenuItem("Display overall statistics: ", 'table', switchOverallStatsDisplay);
	    }
	};

	function updateTitles(updateChart) {
		var curTitle = document.querySelector("#title");
		var curSubtitle = document.querySelector("#subtitle");
		if (!_value.title && curTitle) {
			curTitle.parentNode.removeChild(curTitle);
		}
		if (_value.title) {
			if (curTitle === null) {
				curTitle = document.createElement('h1');
				document.querySelector('body').appendChild(curTitle);
				curTitle.setAttribute('id', 'title');
				curTitle.setAttribute('class', 'knime-title');
			}
			curTitle.innerHTML = _value.title;
		}
		if (!_value.subtitle && curSubtitle) {
			curSubtitle.parentNode.removeChild(curSubtitle);
		} 
		if (_value.subtitle) {
			if (curSubtitle === null) {
				curSubtitle = document.createElement('h4');
				document.querySelector('body').appendChild(curSubtitle);
				curSubtitle.setAttribute('id', 'subtitle');
				curSubtitle.setAttribute('class', 'knime-subtitle');
			}
			curSubtitle.innerHTML = _value.subtitle
		}
		
		var isTitle = _value.title || _value.subtitle;
		knimeService.floatingHeader(isTitle);
	}

	function toggleClassStatisticsDisplay() {
		var table = document.querySelector("#knime-class-statistics");
		table.style.display = _value.displayClassStatsTable ? "block" : "none";
	}

	function toggleOverallStatisticsDisplay() {
		var table = document.querySelector("#knime-overall-statistics");
		table.style.display = _value.displayOverallStats ? "block" : "none";
	}	

	function toggleConfusionMatrixRatesDisplay() {
		var rate = document.querySelectorAll(".rateCell");
		var noBorder = document.querySelectorAll(".no-border");
		for (var i = 0; i < rate.length; i++) {
			rate[i].style.display = _value.displayConfusionMatrixRates ? "table-cell" : "none";
		}
		for (var i = 0; i < noBorder.length; i++) {
			noBorder[i].style.display = _value.displayConfusionMatrixRates ? "table-cell" : "none";
		}
	}

	function toggleRowsNumberDisplay() {
		var rowsNumber = document.querySelector(".rowsNumber");
		var enable = _value.displayTotalRows;
		rowsNumber.style.opacity = enable ? "1" : "0";
		//rowsNumber.style.borderRightWidth = enable ? "2px";
		rowsNumber.style.borderLeftWidth = enable ? "2px" : "0px";
		rowsNumber.style.borderTopWidth = enable ? "2px" : "0px";
	}

	scorer.validate = function() {
		return true;
	}
	
	scorer.getComponentValue = function() {
		return _value;
	}
	
	
	return scorer;
	
}());