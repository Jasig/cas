
var viewConfigs = (function () {

    var origData;


    var createDataTable = function() {
        $('#viewConfigsTable').DataTable( {
            "initComplete": function(settings, json) {
                if (!json) {
                    $('#loadingMessage').hide();
                    $('#viewConfigError').show();
                    $("#view-configuration").hide();
                } else {
                    $('#loadingMessage').hide();
                    $('#viewConfigError').hide();
                    $("#view-configuration").show();
                }
            },
            "drawCallback": function( settings ) {
                var api = this.api();
                if (api.page.info().pages > 1) {
                    $('#' + $.fn.dataTable.tables()[0].id + '_paginate')[0].style.display = "block";
                } else {
                    $('#' + $.fn.dataTable.tables()[0].id + '_paginate')[0].style.display = "none";
                }

                editTable();
            },
            "processing": true,
            "ajax": {
                "url": urls.getConfiguration,
                "dataSrc": function (json) {
                    var return_data = new Array();
                    for (var section in json) {
                        var sectionName;
                        if (section.indexOf("applicationConfig") != -1) {
                            sectionName = "applicationConfig";
                        } else {
                            sectionName = section;
                        }
                        var object = json[section];
                        for (var item in object) {
                            return_data.push({
                                'key': sectionName + "." + item,
                                'value'  : object[item],
                            })
                        }

                    }
                    return return_data;
                }
            },
            "columns": [
                { "data": "key", 'className': 'col-xs-6 key' },
                { "data": "value", 'className': 'col-xs-6 value' }
            ],
        } );
    };

    var getRowData = function(row) {
        var tds = row.find("td");
        var tmp = {};
        $.each(tds, function(i) {
            if (i%2 == 0) {
                tmp.key = $(this).text();
            } else {
                tmp.value = $(this).text();
            }
        });
        return tmp;
    };
    
    var editTable = function() {
        $('#viewConfigsTable').editableTableWidget({editor: $('<textarea>')});

        $('#viewConfigsTable td').on('focus', function(evt, newValue) {
            delete origData;

            origData = getRowData( $(this).closest("tr") );
        });

        $('#viewConfigsTable tr').on('change', function(evt, newValue) {

            newChanges = getRowData( $(this) );

            var data = { old: origData, newData: newChanges };

            var jqxhr = $.post( "/", data, function( resp ) {
                console.log( "success" );
            })
            .fail(function() {
                console.log( "error" );
            })
        });


    };
    
    // initialization *******
    ( function init () {
        createDataTable();
    })();

    // Public Methods
    return {
        /**
         * Not used
         */
    };
})();
