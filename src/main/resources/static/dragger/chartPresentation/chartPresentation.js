angular
		.module("dragger")
		.controller(
				"chartPresentationController",
				function($scope, $http,$q) {
				$scope.chart = {
				        id:0,
				        name:'',
				        labels: [''],
				        data: [],
				        colors: ['#565cc1'],
				        emptyPie: true,
				        self: null
				};

                $scope.generatedFiltersDescriptionForChartName = '';
                $scope.chartFilterColumns = [];
                $scope.filters = [];
                $scope.operators = [];
                $scope.dataTypes = {
                    VARCHAR : {
                        name : "TEXT",
                        multivalue : true,
                        getValue : function() {
                            return;
                        }
                    },
                    NUMERIC : {
                        name : "NUMBER",
                        multivalue : false,
                        getValue : function() {
                            return;
                        }
                    },
                    BOOLEAN : {
                        multivalue : true,
                        getValues : function() {
                            return [ {
                                name : 'TRUE',
                                value : 'TRUE'
                            }, {
                                name : 'FALSE',
                                value : 'FALSE'
                            } ];
                        }
                    },
                    DATE : {
                        name : "DATE",
                        multivalue : false,
                        getValue : function() {
                            return;
                        }
                    }
                };

                $scope.chartFilters = [];
				$scope.lastBuild = {selectedSource: null,selectedColumn: null,allowAddition: false};
                $scope.selectedSource = {text: '[...] ', selected:false};
                $scope.selectedColumn = {text: '[...]', selected:false}

                $scope.addFilter = function() {
                    $scope.chartFilters.push({
                        "valueObj" : null,
                        "selectValue" : null,
                        "filter" : null,
                        "column" : null,
                        isCurrentDate: null
                    });
                };

                $scope.removeFilter = function(filterIndex) {
                    $scope.chartFilters.splice(filterIndex, 1);
                };

				    $scope.filterSources =function(source)
                    {
                        if(!(source && source.name))
                        {
                            return false;
                        }

                        if(!$scope.searchSources)
                        {
                            $scope.searchSources = '';
                        }

                        if(source.name.toLowerCase().includes($scope.searchSources.toLowerCase()))
                        {
                            return true;
                        }

                        return false;
                    };

                    $scope.selectedSourceEvent = function(selectedSource)
                    {
                        selectedSource.selected = !selectedSource.selected;
                        if(!selectedSource.selected)
                        {
                            $scope.selectedSource.data = null;
                            $scope.selectedSource.text = '[...]';
                            $scope.selectedSource.selected = false;
                            $scope.lastBuild.allowAddition = false;
                            return;
                        }

                        $scope.selectedSource.data = selectedSource;
                        $scope.selectedSource.text = selectedSource.name;
                        $scope.selectedSource.selected = true;

                        $scope.models.lists['Sources'].forEach(function(checkedSource)
                        {
                            if(checkedSource != selectedSource)
                            {
                                checkedSource.selected = false;
                            }
                        })

                        $scope.fetchFilterSuggestions();
                        $scope.validateChartAddition();
                        $scope.isLinked();
                    }

                    $scope.selectedColumnEvent = function(selectedColumn)
                    {
                        selectedColumn.selected = !selectedColumn.selected;
                        if(!selectedColumn.selected)
                        {
                            $scope.selectedColumn.data = null;
                            $scope.selectedColumn.text = '[...]';
                            $scope.selectedColumn.selected = false;
                            $scope.lastBuild.allowAddition = false;
                            return;
                        }

                        $scope.selectedColumn.data = selectedColumn;
                        $scope.selectedColumn.text = selectedColumn.type + ' לפי ' + selectedColumn.data.name;
                        $scope.selectedColumn.selected = true;

                        Object.getOwnPropertyNames($scope.models.lists).forEach(function(listName)
                        {
                            if(listName !== 'Sources' && listName !== 'Count' && listName !== 'GroupBy')
                            {
                                $scope.models.lists[listName].columns.forEach(function(checkedColumn)
                                {
                                    if(checkedColumn != selectedColumn)
                                    {
                                        checkedColumn.selected = false;
                                    }
                                })
                            }
                        })

                        $scope.fetchFilterSuggestions();
                        $scope.validateChartAddition();
                        $scope.isLinked();
                    }

                    $scope.fetchFilterSuggestions = function()
                    {
                        if($scope.selectedSource.selected && $scope.selectedColumn.selected)
                        {
                            $scope.chartFilterColumns = [];
                            var requestParams = '?columns=' + $scope.selectedColumn.data.data.columnId + '&columns=';
                            $scope.getColumn($scope.selectedSource.data)
                            .then(function successCallback(response){
                            requestParams += response.columnId;
                            $http({
                                method : 'GET',
                                url : 'api/charts/filterColumnsSuggestion' + requestParams
                            })
                            .then(
                                function successCallback(response){
                                    var columnsForSuggestionPromises = [];
                                    response.data.forEach(function(columnId){
                                        columnsForSuggestionPromises.push($http({method: 'GET', url: 'api/queryColumns/'+columnId}));
                                        });
                                    $q.all(columnsForSuggestionPromises).then(function(columns)
                                    {
                                        columns.forEach(function(column)
                                        {
                                            $scope.chartFilterColumns.push(column.data);
                                        });
                                    });

                                });
                                });
                        }
                    };

                    $scope.validateChartAddition = function()
                    {
                        if($scope.selectedColumn && $scope.lastBuild.selectedColumn && $scope.selectedColumn.data.data.columnId === $scope.lastBuild.selectedColumn.data.columnId &&
                            ($scope.selectedSource && $scope.lastBuild.selectedSource && $scope.selectedSource.data._links.self.href === $scope.lastBuild.selectedSource._links.self.href))
                        {
                            $scope.lastBuild.allowAddition = true;
                        }
                        else
                        {
                            $scope.lastBuild.allowAddition = false;
                        }
                    }

                    $scope.filterSourcesList =function(sourceName, source)
                    {
                                if(!(source && sourceName) || $scope.models.listsData[sourceName])
                                {
                                    return false;
                                }

                                if(!$scope.searchSourcesAndColumns)
                                {
                                    $scope.searchSourcesAndColumns = '';
                                }

                                if(sourceName.toLowerCase().includes($scope.searchSourcesAndColumns.toLowerCase()))
                                {
                                    return true;
                                }

                                for (let i = 0; i < source.columns.length; i++) {
                                    if(source.columns[i].data.name && source.columns[i].data.name.toLowerCase().includes($scope.searchSourcesAndColumns.toLowerCase()) &&
                                        source.columns[i].data.visible)
                                    {
                                        return true;
                                    }
                                }

                                return false;
                            };

                    $scope.getColumn = function(source)
                    {
                        return $http(
                            {
                                method : 'GET',
                                url : source._links.columns.href
                            })
                            .then(
                                function successCallback(response)
                                {
                                return response.data._embedded.queryColumns[0];
                                }
                            );
                    };

                    $scope.addChartToDashboard = function()
                    {
                        $http(
                            {
                                method : 'GET',
                                url : 'api/dashboards/1'
                            })
                            .then(
                                function successCallback(response){
                                var chartName;
                                Swal.fire({
                                  title: 'בחר שם לתרשים',
                                  input: 'text',
                                  inputValue:$scope.chart.name,
                                  inputAttributes: {
                                    autocapitalize: 'off'
                                  },
                                  showCancelButton: true,
                                  confirmButtonText: 'הוסף',
                                  cancelButtonText: 'בטל',
                                  showLoaderOnConfirm: false,
                                  preConfirm: (name) => {
                                    chartName = name;
                                  },
                                  allowOutsideClick: false
                                }).then((result) => {
                                	if(result.dismiss && result.dismiss ==='cancel'){
                                		return}
                                    var chartAlreadyAddedToDashboard = false;
                                    $scope.chart.name = chartName;
                                    $http(
                                            {
                                                method : 'PUT',
                                                url : 'api/charts/updateChartName?chartId=' + $scope.chart.id,
                                                data: $scope.chart.name
                                            })
                                     response.data.charts.forEach(function(chart)
                                     {
                                        if(chart.id === $scope.chart.id)
                                        {
                                            chartAlreadyAddedToDashboard = true;
                                        }
                                     });

                                     if(!chartAlreadyAddedToDashboard)
                                     {
                                         response.data.charts.push($scope.chart);

                                         $http(
                                         {
                                             method : 'PUT',
                                             url : 'api/dashboard/1/addChart/' + $scope.chart.id
                                         }).then(
                                         function successCallback(response){
                                             if (!response) {
                                             Swal.fire({
                                               title: "הוספת התרשים כשלה"
                                             })
                                           }
                                           else
                                           {
                                             Swal.fire({
                                               title: "התרשים נוסף בהצלחה!"
                                             });
                                           }
                                           });
                                       }
                                       else
                                       {
                                           Swal.fire({
                                              title: "שם התרשים שונה!"
                                            });
                                       }
                                    });
                                    });
                    };

                    $scope.isLinked = function()
                    {
                        if ($scope.selectedSource.selected && $scope.selectedColumn.selected) {
                            var columnsRetrievalPromise = [];

                             columnsRetrievalPromise.push($scope.getColumn($scope.selectedSource.data));

                            $q.all(columnsRetrievalPromise).then(function(columns){
                            var columnIds = [];

                            columns.forEach(function(column)
                            {
                                columnIds.push(column.columnId);
                            });

                            columnIds.push($scope.selectedColumn.data.data.columnId);

                            $http(
                                    {
                                        method : 'POST',
                                        url : 'api/queries/isQueryLinked',
                                        data : columnIds
                                    })
                                    .then(
                                            function successCallback(
                                                    response) {
                                                if (response.data === false) {
                                                    alert("המקור והעמודה שבחרת לא מקושרים");
                                                }
                                            });
                                    });
                        }
                    }

					$scope.createChart = function()
					{
						var columns = [];
						var countColumnsPromises = [];
						var groupBysPromises = [];
                        var name;
                        var countSources = [];
                        var filters = [];
                        var filterPromises = [];

						if(!$scope.selectedColumn.selected)
                        {
                            alert("יש לבחור עמודה");
                            return;
                        }

                        groupBysPromises.push($scope.selectedColumn.data);

						if(!$scope.selectedSource.selected)
						{
						    alert("יש לבחור מקור");
						    return;
						}

						if (!$scope.filtersValidation()) {
						    return;
						}

                        $scope.chartFilters.forEach(function(filter)
                        {
                            filters.push({filterId:filter.filter.id, column: filter.column._links.self.href,value: filter.value,chart:null})
                        });

                        countSources.push($scope.selectedSource.data._links.self.href);

                        $q.all(groupBysPromises).then(function(groupBysResponse){
                        var groupBys = [];

                        groupBysResponse.forEach(function(groupBy)
                        {
                            groupBys.push(groupBy.data._links.self.href);
                            columns.push(groupBy.data._links.self.href);
                        })

                        name = $scope.generateNameForChart();

						$http({
							method : 'POST',
							url : 'api/charts',
							data : {
								query : {columns, countSources, groupBys},
								name: name
							}
						}).then(function successCallback(response) {

						    if(!response.data.id)
						    {
                                 alert("בניית התרשים כשלה")
                                 return;
						    }
						    else
						    {
						        $scope.chart = response.data;
                                $scope.chart.name = name;
						    }

						    if(filters.length > 0)
						    {
						        filters.forEach(function(filter)
						        {
						            filter.chart = response.data._links.self.href;
						            filterPromises.push($http({
                                         method : 'POST',
                                         url : 'api/chartQueryFilters',
                                         data : filter
                                     }));
						        })

						    $q.all(filterPromises).then(function successCallback(response) {
                                $scope.executeChartQuery();
						    });
						    }
						    else
						    {
						        $scope.executeChartQuery();
						    }
						}, function errorCallback(response) {
							alert("בניית התרשים כשלה");
						});
						});
}

                    $scope.generateNameForChart = function()
                    {
                        var chartName = "כמות ה" + $scope.selectedSource.text + " עבור " + $scope.selectedColumn.text;

                        $scope.generateFiltersDescriptionForChartName();
                        chartName += $scope.generatedFiltersDescriptionForChartName;

                        return chartName;
                    };

                    $scope.generateFiltersDescriptionForChartName = function()
                    {
                        $scope.generatedFiltersDescriptionForChartName = '';

                        if($scope.chartFilters.length > 0)
                        {
                            $scope.generatedFiltersDescriptionForChartName += ' מסונן ע"פ ';
                        }

                        $scope.chartFilters.forEach(function(filter, index)
                        {
                            $scope.generatedFiltersDescriptionForChartName += filter.column.name + " " + filter.filter.name +  filter.value;

                            if(index < ($scope.chartFilters.length - 1))
                            {
                                $scope.generatedFiltersDescriptionForChartName += ",";
                            }
                        });
                    }

                    $scope.filtersValidation = function()
                    {
                        var validationCheck = true;
                            $scope.chartFilters.forEach(function(filter, index) {
                            if ($scope.dataTypes[filter.column.dataType].multivalue)
                            {
                                filter.value = Awesomplete.$("#columnValueDropDown"+index).value;
                            }
                            else if($scope.dataTypes[filter.column.dataType].name === 'DATE' && filter.isCurrentDate)
                            {
                                filter.value = "current_date";
                            }
                                else{
                                filter.value = filter.valueObj;
                            }

                            if (!filter.filter) {
                                validationCheck = false;
                                alert("האופרטור בשורה "
                                        + (index + 1)
                                        + "לא אמור להיות ריק ");
                                return;
                            } else if (!filter.column) {
                                validationCheck = false;
                                alert("העמודה בשורה "
                                        + (index + 1)
                                        + "לא אמור להיות ריקה ");
                                return;
                            } else if (!filter.value) {
                                validationCheck = false;
                                alert(" הערך בשורה"
                                        + (index + 1)
                                        + "לא אמור להיות ריק ");
                                return;
                            }
                            filter.columnId = filter.column.columnId;
                            filter.filterId = filter.filter.id;
                        });

                        return validationCheck;
                    };

                    $scope.executeChartQuery = function()
                    {
                    $http({
                        method : 'GET',
                        url : 'api/charts/executeCountChartQuery?chartId=' + $scope.chart.id
                        }).then(
                        function successCallback(response) {
                        $scope.chart.labels = [];
                        $scope.chart.data = [];
                        $scope.chart.colors = [];
                        $scope.chart.emptyPie = true;
                        $scope.lastBuild.allowAddition = true;
                        $scope.lastBuild.selectedColumn = $scope.selectedColumn.data;
                        $scope.lastBuild.selectedSource = $scope.selectedSource.data;

                        if(response.data.length > 0 )
                        {
                        $scope.chart.emptyPie = false;
                        }
                        else
                        {
                        $scope.chart.colors = ['#565cc1'];
                        return;
                        }

                        response.data.forEach(function(slice,index)
                        {
                        $scope.chart.labels.push(slice.label);
                        $scope.chart.data.push(slice.count);
                        })

                        if($scope.chart.colors.length > $scope.chart.labels.length)
                        {
                        $scope.chart.colors = $scope.chart.colors.slice(0, $scope.chart.labels.length - 1);
                        }
                        },
                        function failureCallback(response) { console.log("couldn't retrieve chart data");
                        return;
                        });
                    }
					$scope.models = {
						selected : null,
						lists : {
							"GroupBy" : [],
							"Count" : [],
							"Sources" : []
						},
						listsData : {
                            "GroupBy" : {},
                            "Count" : {},
                            "Sources" : {}
                        }
					};
                    $scope.models.lists['Sources'] = [];
                    $scope.models.lists['Sources'].allowedTypes = [];
                    $scope.models.listsData['GroupBy'] = {};
                    $scope.models.listsData['Count'] = {};
                    $scope.models.listsData['GroupBy'].allowedTypes = [];
                    $scope.models.listsData['Count'].allowedTypes = ['Sources'];
                    $scope.models.listsData['Sources'].staticList = true;
                    $scope.models.listsData['GroupBy'].staticList = true;
                    $scope.models.listsData['Count'].staticList = true;

					$http({
						method : 'GET',
						url : '/api/querySources'
					})
							.then(
									function successCallback(response) {
										angular
												.forEach(
														response.data._embedded.querySources,
														function(source) {
                                                            if(source && source.visible)
                                                            {
                                                                source.selected = false;
                                                                $scope.models.lists['Sources'].push(source);
                                                                $scope.models.lists[source.name] = {};
                                                                $scope.models.lists[source.name].columns = [];
                                                                $scope.models.lists[source.name].allowedTypes = [];
                                                                $scope.models.lists[source.name].visible = source.visible;
                                                                $scope.models.lists[source.name].allowedTypes.push(source.name);
                                                                $scope.models.listsData['GroupBy'].allowedTypes.push(source.name);

                                                                $http(
                                                                    {
                                                                        method : 'GET',
                                                                        url : source._links.columns.href
                                                                    })
                                                                    .then(
                                                                            function successCallback(
                                                                                    response) {
                                                                                angular
                                                                                        .forEach(
                                                                                                response.data._embedded.queryColumns,
                                                                                                function(
                                                                                                        column) {
                                                                                                    var columnItem = {
                                                                                                        data : column,
                                                                                                        type : source.name
                                                                                                    };
                                                                                                    var columnExists = false;
                                                                                                    $scope.models.lists[source.name].columns.forEach(function(columnInReceivedReport)
                                                                                                    {
                                                                                                        if(columnItem.data.columnId == columnInReceivedReport.data.columnId)
                                                                                                        {
                                                                                                            columnExists = true;
                                                                                                        }
                                                                                                    })

                                                                                                    if(!columnExists && columnItem.data.visible) {
                                                                                                    columnItem.selected = false;
                                                                                                        $scope.models.lists[source.name].columns
                                                                                                            .push(columnItem);
                                                                                                    }
                                                                                                });
                                                                            });

                                                            }
														});
									});

					$scope.changeColumn = function(filterIndex)
					{
                        $scope.chartFilters[filterIndex].selectValue = null;
                        if($scope.chartFilters[filterIndex].comboplete)
                        {
                            $scope.chartFilters[filterIndex].comboplete.destroy();
                        }
                        var comboplete = new Awesomplete('#columnValueDropDown' + filterIndex, {
                            minChars: 0,
                        });
                        comboplete.maxItems = 1000000;
                        $scope.chartFilters[filterIndex].comboplete = comboplete;

                        Awesomplete.$('#dropdown-btn' + filterIndex).addEventListener("click", function() {
                            if(comboplete._list.length === 0)
                            {
                                $http({
                                    method : 'GET',
                                    url : '/api/columns/suggestValues?columnId='
                                    + $scope.chartFilters[filterIndex].column.columnId
                                }).then(
                                        function successCallback(response) {
                                            comboplete._list = response.data;
                                            if (comboplete.ul.childNodes.length === 0) {
                                            comboplete.minChars = 0;
                                            comboplete.evaluate();
                                            }
                                            else if (comboplete.ul.hasAttribute('hidden')) {
                                                comboplete.open();
                                            }
                                            else {
                                                comboplete.close();
                                            }
                                        },
                                        function successCallback(response) {
                                            alert("אין ערכים להצעה עבור עמודה זו");
                                        });
                            }

                            if (comboplete.ul.childNodes.length === 0) {
                                comboplete.minChars = 0;
                                comboplete.evaluate();
                            }
                            else if (comboplete.ul.hasAttribute('hidden')) {
                                comboplete.open();
                            }
                            else {
                                comboplete.close();
                            }
                        });

                        Awesomplete.$('#dropdown-btn' + filterIndex).addEventListener('focusout',function(){
                                                        if (!comboplete.ul.hasAttribute('hidden')) {
                                                                comboplete.close();
                                                        }
                                                    });
                    }

                    $http({
                        method : 'GET',
                        url : '/api/filters'
                    }).then(
                            function successCallback(response) {
                                angular.forEach(
                                        response.data._embedded.filters,
                                        function(filter) {
                                            $scope.operators.push(filter)
                                        });
                            });
				});