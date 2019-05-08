/* eslint-env jquery */
/* global KnimeBaseTableViewer:false, Util:false */
window.docViewer = (function () {
    var _representation = null;
    var updateTimer,
        tempScrollTop, tempScrollHeight;
    var lastWidthWhenUpdating = 0;
    // need an initial value which takes the scroll bar into consideration. The problem is, that when the table is
    // created, the height of the table is small enough to not have a vertical scroll bar. But when the Brat documents
    // are injected it might happen, that the scroll bar appears end therefore some space is reserved.
    var scrollBarWidth = 51;
    var svgOffset = $(window).width() - scrollBarWidth;
    var dispatchCounter = 0;
    
    // time after which the resizing should happen.
    var updateInterval = 200;

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
        this._infoColsCount = 0;
        this._nonSelectableColsCount = 0;
        this._rowIdColInd = null;
        this._nonHiddenDataIndexes = [];
    };

    var htmlEncode = function (x) {
        return x.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;').replace(
            /'/g, '&apos;');
    };

    BratDocumentViewer.prototype = Object.create(KnimeBaseTableViewer.prototype);
    BratDocumentViewer.prototype.constructor = BratDocumentViewer;
    
    function doneResizing() {
        if (Math.abs(lastWidthWhenUpdating - window.innerWidth) > 10) {
            tempScrollTop = $(window).scrollTop();
            tempScrollHeight = $(document).height() - $(window).height();
            var tempHeights = [];
            // save all of the heights from each visible document
            $('.knime-table-row').each(function (index, element) {
                tempHeights.push(element.children[1].children[1].children[0].style.height);
            });
            var rows = $('#knimePagedTable').DataTable().rows(); // eslint-disable-line new-cap
            // invalidate data to clear all the documents
            rows.invalidate('data');
            // measure the width of the unoccupied document to detect the width which should be used by the brat
            // Document
            svgOffset = $('.knime-table-row')[0].children[1].children[1].clientWidth - 10;
            // create the documents
            rows.draw(false);
            // set each of the rows to the previous saved height to make the transition smooth
            $('.knime-table-row').each(function (index, element) {
                element.children[1].children[1].style.height = tempHeights[index];
                element.children[1].children[1].children[0].style.height = tempHeights[index];
            });

            lastWidthWhenUpdating = window.innerWidth;
        }
    }

    BratDocumentViewer.prototype.init = function (representation, value) {
        _representation = representation;
        var overrides = {
            displayRowIds: representation.useRowID,
            displayRowIndex: false,
            enableClearSortButton: false,
            enableColumnSearching: false,
            enableSearching: false,
            enableSorting: false,
            singleSelection: false,
            displayRowColors: false
        };
        var options = Object.assign({}, representation, overrides);
        // super call
        KnimeBaseTableViewer.prototype.init.call(this, options, value);
        
        // eslint-disable-next-line new-cap
        $('#knimePagedTable').DataTable().on('page.dt', function () {
            doneResizing();
        });
    };
    
    
    $(window).resize(function (event) {
        clearTimeout(updateTimer);
        updateTimer = setTimeout(doneResizing, updateInterval);
    });

    // filtering
    BratDocumentViewer.prototype._buildMenu = function () {
        this._representation.subscriptionFilterIds = this._knimeTable.getFilterIds();
        KnimeBaseTableViewer.prototype._buildMenu.apply(this);
    };
    
    function checkScrollPosition() {
        var scrollRatio = tempScrollTop / tempScrollHeight;
        // amount of pixels to allow moving and still scrolling to the bottom when resizing.
        var scrollOffset = 50;
        if (tempScrollHeight - tempScrollTop < scrollOffset) {
            $(window).scrollTop($(document).outerHeight() - $(window).outerHeight());
        } else {
            $(window).scrollTop(scrollRatio * ($(document).height() - $(window).height()));
        }
    }

    BratDocumentViewer.prototype._renderBratDocument = function (id) {
        var tags = _representation.bratDocuments[id].tags;
        var text = _representation.bratDocuments[id].docText;
        var ids = _representation.bratDocuments[id].termIds;
        var terms = _representation.bratDocuments[id].terms;
        var startIdx = _representation.bratDocuments[id].startIndexes;
        var stopIdx = _representation.bratDocuments[id].stopIndexes;
        var colors = _representation.bratDocuments[id].colors;
        var showLineNumbers = _representation.showLineNumbers;
        
        // Translates escaped newline signs into actual newline signs
        text = text.replace(new RegExp(/\\n/g), '\n');

        var tagsUnique = tags.filter(function (item, i, ar) {
            return ar.indexOf(item) === i;
        });
        var collData = {
            entity_types: [] // eslint-disable-line camelcase
        };
        var obj;
        for (var j = 0; j < tagsUnique.length; j++) {
            obj = {};
            obj.type = tagsUnique[j];
            obj.labels = [tagsUnique[j]];
            obj.bgColor = colors[j];
            obj.borderColor = 'darken';
            collData.entity_types.push(obj);
        }

        var docData = {
            text: text,
            entities: []
        };
        for (var i = 0; i < terms.length; i++) {
            obj = [ids[i], tags[i]];
            var idx = [[startIdx[i], stopIdx[i]]];
            obj.push(idx);
            docData.entities.push(obj);
        }
        var dispatcher = Util.embed(id, $.extend({}, collData), $.extend({}, docData), showLineNumbers);
        // Delete the loading text and show the svg when the svg creation is finished.
        dispatcher.on('doneRendering', function () {
            if ($('#' + id)[0].childNodes[0].nodeType === Node.TEXT_NODE) {
                $('#' + id)[0].childNodes[1].style.visibility = 'block';
                $('#' + id)[0].removeChild($('#' + id)[0].childNodes[0]);
            }
            dispatchCounter++;
            if (dispatchCounter === $('#knimePagedTable')[0].childNodes[0].childNodes.length) {
                checkScrollPosition();
                dispatchCounter = 0;
            }
        });
        
        // Set the visibility to hidden until the svg creation is finished
        if ($('#' + id)[0].childNodes[1]) {
            $('#' + id)[0].childNodes[1].style.visibility = 'hidden';
        }
        dispatcher.post('svgWidth', [svgOffset]);
        return dispatcher;
    };

    BratDocumentViewer.prototype._buildColumnDefinitions = function () {
        KnimeBaseTableViewer.prototype._buildColumnDefinitions.call(this);
        var colDefs = this._dataTableConfig.columns;
        var labelCol = this._representation.labelCol;
        var useRowID = this._representation.useRowID;
        if (labelCol || useRowID) {
            for (var i = 0; i < colDefs.length; i++) {
                if (colDefs[i].title === labelCol || useRowID && i === this._rowIdColInd) {
                    this._labelColIndex = i;
                    var colDef = colDefs[i];
                    colDef.className += ' knime-document-title';
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
            var titlePrefix = '<span class="knime-documents-rowtitle">' + column.title + ':</span> ';
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
                column.render = function (name, display, data, position) {
                    if (typeof data === 'undefined' || data === null) {
                        return null;
                    }

                    return '<div class="knime-document-title knime-table-cell" style="min-height:20px">' +
                        _representation.bratDocuments[position.row].docTitle + '</div><div style="width:100%" id=' +
                        position.row + '>Loading...</div>';
                };
            }
            column.defaultContent = titlePrefix + (column.defaultContent || '');
        });
    };

    // helper for _addColumnTitles()
    BratDocumentViewer.prototype._shouldShowTitleOnColumn = function (column) {
        if (/\b(selection-cell|knime-document-title|knime-svg|knime-png)\b/.test(column.className)) {
            return false;
        }
        if (!column.hasOwnProperty('title') || !column.title || column.title === 'RowID') {
            return false;
        }
        return true;
    };

    // auto-size cell heights
    BratDocumentViewer.prototype._dataTableDrawCallback = function () {
        KnimeBaseTableViewer.prototype._dataTableDrawCallback.apply(this);
        $('#knimePagedTable thead').remove();
        $('#knimePagedTableContainer .dataTables_scrollHead').remove();
        BratDocumentViewer.prototype._setDocumentViewerStyle();
        var infoColsCount = this._infoColsCount;
        var columns = this._dataTableConfig.columns;
        for (var colIndex = infoColsCount; colIndex < columns.length; colIndex++) {
            var cells = Array.prototype.slice.call(document
                .querySelectorAll('#knimePagedTable .knime-table-cell:nth-child(' + (colIndex + 1) + ')'));
            cells.forEach(function (cell) {
                BratDocumentViewer.prototype._renderBratDocument(cell.children[1].id);
            });
        }
    };

    BratDocumentViewer.prototype._setDocumentViewerStyle = function () {
        $('#knimePagedTable').removeClass('table-striped');
        $('#knimePagedTable').removeClass('table-bordered');
        $('.dt-body-center.knime-table-cell').css('border-top', '0px');
        $('.knime-table-cell.knime-string').css('border-top', '0px');
    };

    BratDocumentViewer.prototype._cellMouseDownHandler = function () {
        // blocks the cell_mouse_down event so that the cell is not being selected
    };

    return new BratDocumentViewer();

})();
