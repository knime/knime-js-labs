window.bratDocViewer = (function () {
    var view = {};
    var _representation = null;
    var VIZ_ID = 'viz';
    var minWidth = 100;
    var minHeight = 100;
    var layoutContainerID = "layoutContainer";
    var containerID = "documentContainer";

    var BratDocumentViewer = function () {
        this._representation = null;
        this._value = null;
        this._knimeTable = null;
        this._dataTable = null;
        this._selection = {};
        this._partialSelectedRows = [];
        this._currentFilter = null;
        this._initialized = false;
        this._dataTableConfig = null;
    }
    
    BratDocumentViewer.prototype = Object.create(KnimeBaseTableViewer.prototype);
    BratDocumentViewer.prototype.constructor = BratDocumentViewer;
    
    
    BratDocumentViewer.prototype.init = function (representation, value) {
        try {
            _representation = representation;

            debugger;
            var tags = _representation.tags;
            var title = _representation.documentTitle;
            var text = _representation.documentText;
            var ids = _representation.termIds;
            var terms = _representation.terms;
            var startIdx = _representation.startIndexes;
            var stopIdx = _representation.stopIndexes;
            var colors = _representation.colors;

            var tagsUnique = tags.filter(function (item, i, ar) {
                return ar.indexOf(item) === i;
            });
            var collData = {
                entityTypes: []
            };
            for (var j = 0; j < tagsUnique.length; j++) {
                var obj = {};
                obj.type = tagsUnique[j];
                obj.labels = [tagsUnique[j]];
                obj.bgColor = colors[j];
                obj.borderColor = 'darken';
                collData.entityTypes.push(obj);
            }

            var docData = {
                text: text,
                entities: []
            };
            for (var i = 0; i < terms.length; i++) {
                var obj = [ids[i], tags[i]];
                var idx = [[startIdx[i], stopIdx[i]]];
                obj.push(idx);
                docData.entities.push(obj);
            }

            var prefixTitle = 'Document Title: ';
            if (!title) {
                title = 'Untitled';
            }

            var body = document.getElementsByTagName('body')[0];
            body.innerHTML = '<h1>' + prefixTitle + title + '</h1>';

            var div = document.createElement('div');
            div.id = VIZ_ID;
            body.appendChild(div);

            Util.embed(VIZ_ID, $.extend({}, collData), $.extend({}, docData));
            
            var overrides = {
                displayRowIds: representation.useRowID,
                displayRowIndex: false,
                enableClearSortButton: false,
                enableColumnSearching: false,
                enableSearching: false,
                enableSorting: false,
                singleSelection: false,
            };
            
            var options = Object.assign({}, representation, overrides);
            
            // super call
            KnimeBaseTableViewer.prototype.init.call(this, options, value);

        } catch (err) {
            if (err.stack) {
                alert(err.stack);
            } else {
                alert(err);
            }
        }
    };
    
    // filtering
    BratDocumentViewer.prototype._buildMenu = function () {
        this._representation.subscriptionFilterIds = this._knimeTable.getFilterIds();
        KnimeBaseTableViewer.prototype._buildMenu.apply(this);
    };
    
    BratDocumentViewer.prototype._buildColumnDefinitions = function () {
        KnimeBaseTableViewer.prototype._buildColumnDefinitions.call(this);
        var colDefs = this._dataTableConfig.columns;
        var labelCol = this._representation.labelCol;
        var useRowID = this._representation.useRowID;
        if (labelCol || useRowID) {
            for (var i = 0; i < colDefs.length; i++) {
                if ((colDefs[i].title === labelCol) || (useRowID && i === this._rowIdColInd)) {
                    this._labelColIndex = i;
                    var colDef = colDefs[i];
                    colDef.className += ' knime-tile-title';
                    if (useRowID) {
                        // title column is in front already
                        colDef.className += ' knime-row-id';
                    } else {
                        // push title column to the front
                        colDefs.splice(i, 1);
                        colDefs.splice(this._infoColsCount, 0, colDef);
                    }
                    break;
                }
            }
        }
         // render SVGs as <img>s
        colDefs.forEach(function (colDef) {
            if (/\bknime-svg\b/.test(colDef.className)) {
                colDef.render = function (data) {
                    return '<img src="data:image/svg+xml;charset=utf-8,' + htmlEncode(data) + '" />';
                };
            }
        });
        if (this._representation.displayColumnHeaders) {
            this._addColumnTitles();
        }
    };
    
    // render columns along with cell entries
    BratDocumentViewer.prototype._addColumnTitles = function () {
        var self = this;
        this._dataTableConfig.columns.forEach(function (column) {
            if (!self._shouldShowTitleOnColumn(column)) {
                return;
            }
            var titlePrefix = '<span class="knime-tiles-rowtitle">' + column.title + ':</span> ';
            if (column.hasOwnProperty('render')) {
                column.render = (function (original) {
                    return function (data) {
                        if (typeof data === 'undefined' || data === null) {
                            return null;
                        }
                        return titlePrefix + (original.call(self, data) || '');
                    };
                })(column.render);
            } else {
                column.render = function (data) {
                    if (typeof data === 'undefined' || data === null) {
                        return null;
                    }
                    return titlePrefix + data;
                };
            }
            column.defaultContent = titlePrefix + (column.defaultContent || '');
        });
    };
    
    // helper for _addColumnTitles()
    BratDocumentViewer.prototype._shouldShowTitleOnColumn = function (column) {
        if (/\b(selection-cell|knime-tile-title|knime-svg|knime-png)\b/.test(column.className)) {
            return false;
        }
        if (!column.hasOwnProperty('title') || !column.title || column.title === 'RowID') {
            return false;
        }
        return true;
    };

    return new BratDocumentViewer();

})();
