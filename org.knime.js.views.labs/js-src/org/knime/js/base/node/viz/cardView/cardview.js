window.cards_namespace = (function () {

    var htmlEncode = function (x) {
        return x.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;')
            .replace(/"/g, '&quot;').replace(/'/g, '&apos;');
    };

    var CardView = function () {
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
    };

    CardView.prototype = Object.create(KnimeBaseTableViewer.prototype);
    CardView.prototype.constructor = CardView;

    CardView.prototype.init = function (representation, value) {
        var textAlignment = representation.alignRight ? 'right' : representation.alignCenter ? 'center' : 'left';

        var overrides = {
            displayColumnHeaders: false,
            displayRowIds: representation.useRowID,
            displayRowIndex: false,
            enableClearSortButton: false,
            enableColumnSearching: false,
            enableSearching: false,
            enableSorting: false,
            singleSelection: false,
            textAlignment: textAlignment
        };

        var options = Object.assign({}, representation, overrides);

        // super call
        KnimeBaseTableViewer.prototype.init.call(this, options, value);
    };

    // filtering
    CardView.prototype._buildMenu = function () {
        this._representation.subscriptionFilterIds = this._knimeTable.getFilterIds();
        KnimeBaseTableViewer.prototype._buildMenu.apply(this);
    };

    // disallow selection of individual cells
    CardView.prototype._cellMouseDownHandler = function () {};

    CardView.prototype._buildColumnDefinitions = function () {
        KnimeBaseTableViewer.prototype._buildColumnDefinitions.call(this);
        var colDefs = this._dataTableConfig.columns;
        var labelCol = this._representation.labelCol;
        var useRowID = this._representation.useRowID;
        if (labelCol || useRowID) {
            for (var i = 0; i < colDefs.length; i++) {
                if ((colDefs[i].title === labelCol) || (useRowID && i === this._rowIdColInd)) {
                    this._labelColIndex = i;
                    var colDef = colDefs[i];
                    colDef.className += ' knime-card-title';
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
    };

    // push title column data to top
    CardView.prototype._getDataSlice = function (start, end) {
        var data = KnimeBaseTableViewer.prototype._getDataSlice.apply(this, arguments);
        if (this._representation.labelCol && this._labelColIndex) {
            var sourceIndex = this._labelColIndex;
            var targetIndex = this._infoColsCount;
            if (!this._representation.useRowID) {
                data.forEach(function (row) {
                    var titleData = row.splice(sourceIndex, 1)[0];
                    row.splice(targetIndex, 0, titleData);
                });
            }
        }
        return data;
    };

    // selection on click
    CardView.prototype._setSelectionHandlers = function () {
        KnimeBaseTableViewer.prototype._setSelectionHandlers.apply(this);
        if (!this._representation.enableSelection) {
            return;
        }
        $('#knimePagedTable tbody').addClass('knime-selection-enabled').on('click', 'tr', function (e) {
            if (e.target && e.target.tagName === 'INPUT' && e.target.type === 'checkbox') {
                return;
            }
            $(e.currentTarget).find('input[type="checkbox"]').click();
        });
    };

    // card width
    CardView.prototype._prepare = function () {
        KnimeBaseTableViewer.prototype._prepare.apply(this);
        var cardWidth;
        if (this._representation.useColWidth) {
            cardWidth = this._representation.colWidth + 'px';
            if (this._representation.useNumCols) {
                var tableWidth = (this._representation.numCols * (this._representation.colWidth + 2 * 5)) + 'px';
                var tableStyle = document.createElement('style');
                tableStyle.textContent = 'table#knimePagedTable { width: ' + tableWidth + ' !important;}';
                document.head.appendChild(tableStyle);
            }
        } else {
            // this._representation.numCols must be set here (ensured by settings dialog)
            cardWidth = 'calc(100% / ' + this._representation.numCols + ' - 2 * 5px)';
        }
        var style = document.createElement('style');
        style.textContent = 'table#knimePagedTable tr { width: ' + cardWidth + ';}';
        document.head.appendChild(style);
    };

    // text alignment support
    CardView.prototype._createHtmlTableContainer = function () {
        KnimeBaseTableViewer.prototype._createHtmlTableContainer.apply(this);
        $('#knimePagedTableContainer').addClass('knime-cards');
        $('#knimePagedTable').removeClass('table-striped').addClass('align-' + this._representation.textAlignment);
    };

    // auto-size cell heights
    CardView.prototype._dataTableDrawCallback = function () {
        KnimeBaseTableViewer.prototype._dataTableDrawCallback.apply(this);
        var infoColsCount = this._infoColsCount;
        var columns = this._dataTableConfig.columns;
        // for some reason, images are rendered with size 0x0 in Chromium at this point, hence the timeout
        setTimeout(function () {
            for (var colIndex = infoColsCount; colIndex < columns.length; colIndex++) {
                var cells = Array.prototype.slice.call(document.querySelectorAll('#knimePagedTable .knime-table-cell:nth-child(' + (colIndex + 1) + ')'));
                var maxCellHeight = cells.reduce(function (max, cell) {
                    var cellHeight = cell.scrollHeight;
                    return cellHeight > max ? cellHeight : max;
                }, 0);
                if (maxCellHeight) {
                    cells.forEach(function (cell) {
                        cell.style.minHeight = maxCellHeight + 'px';
                    });
                }
            }
        }, 0);
    };

    // reset cell heights
    CardView.prototype._dataTablePreDrawCallback = function () {
        KnimeBaseTableViewer.prototype._dataTablePreDrawCallback.apply(this);
        var cells = Array.prototype.slice.call(document.querySelectorAll('#knimePagedTable .knime-table-cell'));
        cells.forEach(function (cell) {
            cell.style.minHeight = '';
        });
    };

    return new CardView();
})();