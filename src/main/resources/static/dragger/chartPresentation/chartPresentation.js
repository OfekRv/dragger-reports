angular
		.module("dragger")
		.controller(
				"chartPresentationController",
				function($scope, $http,$q, $mdDialog) {
				$scope.chart = {
				        id:0,
				        name:'',
				        labels: [''],
				        data: [],
				        description: [],
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
                        if(($scope.selectedColumn.data && $scope.selectedSource.data) && $scope.selectedColumn.data.type != $scope.selectedSource.data.name)
                        {
                            $scope.isLinked();
                        }
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
                        $scope.selectedColumn.text = selectedColumn.type + selectedColumn.data.name;
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

                        if(($scope.selectedColumn.data && $scope.selectedSource.data) && $scope.selectedColumn.data.type != $scope.selectedSource.data.name)
                        {
                            $scope.isLinked();
                        }
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

                    $scope.addChartToDashboard = function(ev)
                    {
                        $http(
                            {
                                method : 'GET',
                                url : 'api/dashboards/1'
                            })
                            .then(
                                function successCallback(response){
                                var chartName;
                                var confirm = $mdDialog.prompt()
                                      .title("Choose name")
                                      .textContent('')
                                      .placeholder('Chart name')
                                      .ariaLabel('Dog name')
                                      .initialValue($scope.chart.name)
                                      .targetEvent(ev)
                                      .required(true)
                                      .ok('Add')
                                      .cancel('Cancel');

                                    $mdDialog.show(confirm).then(function(result) {
                                    $scope.chart.name = result;
                                	if(result.dismiss && result.dismiss ==='cancel'){
                                		return}
                                    var chartAlreadyAddedToDashboard = false;
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
                                             $mdDialog.show(
                                               $mdDialog.alert()
                                                 .clickOutsideToClose(true)
                                                 .textContent('')
                                                 .title("הוספת התרשים כשלה")
                                                 .ariaLabel('Alert Dialog Demo')
                                                 .ok('סבבה')
                                                 .targetEvent(ev)
                                                 );
                                           }
                                           else
                                           {
                                             $mdDialog.show(
                                               $mdDialog.alert()
                                                 .clickOutsideToClose(true)
                                                 .textContent('')
                                                 .title("Chart added successfully :)")
                                                 .ariaLabel('Alert Dialog Demo')
                                                 .ok('ok')
                                                 .targetEvent(ev)
                                                 );
                                           }
                                           });
                                       }
                                       else
                                       {
                                           $mdDialog.show(
                                             $mdDialog.alert()
                                               .clickOutsideToClose(true)
                                               .textContent('')
                                               .title("Chart renamed successfully :)")
                                               .ariaLabel('Alert Dialog Demo')
                                               .ok('ok')
                                               .targetEvent(ev)
                                               );
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
                                                $mdDialog.show(
                                                  $mdDialog.alert()
                                                    .clickOutsideToClose(true)
                                                    .textContent('')
                                                    .title("Source and column are not connected")
                                                    .ariaLabel('Alert Dialog Demo')
                                                    .ok('ok')
                                                    .targetEvent(ev)
                                                    );
                                                }
                                            });
                                    });
                        }
                    }

					$scope.createChart = function(ev)
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
                            $mdDialog.show(
                              $mdDialog.alert()
                                .clickOutsideToClose(true)
                                .textContent('')
                                .title("Choose column!")
                                .ariaLabel('Alert Dialog Demo')
                                .ok('ok')
                                .targetEvent(ev)
                                );
                            return;
                        }

                        groupBysPromises.push($scope.selectedColumn.data);

						if(!$scope.selectedSource.selected)
						{
						    $mdDialog.show(
                              $mdDialog.alert()
                                .clickOutsideToClose(true)
                                .textContent('')
                                .title("Choose source!")
                                .ariaLabel('Alert Dialog Demo')
                                .ok('ok')
                                .targetEvent(ev)
                                );
						    return;
						}

						if (!$scope.filtersValidation(ev)) {
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
						        $mdDialog.show(
                                  $mdDialog.alert()
                                    .clickOutsideToClose(true)
                                    .textContent('')
                                    .title("Chart building failed")
                                    .ariaLabel('Alert Dialog Demo')
                                    .ok('ok')
                                    .targetEvent(ev)
                                    );
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
							$mdDialog.show(
                                  $mdDialog.alert()
                                    .clickOutsideToClose(true)
                                    .textContent('')
                                    .title("Chart building failed")
                                    .ariaLabel('Alert Dialog Demo')
                                    .ok('ok')
                                    .targetEvent(ev)
                                    );
						});
						});
}

                    $scope.generateNameForChart = function()
                    {
                        var chartName = "Count of " + $scope.selectedSource.text + " per " + $scope.selectedColumn.text;

                        chartName += $scope.generatedFiltersDescriptionForChartName;

                        return chartName;
                    };

                    $scope.filtersValidation = function(ev)
                    {
                        var validationCheck = true;
                            $scope.chartFilters.forEach(function(filter, index) {
                            if (filter.column && $scope.dataTypes[filter.column.dataType].multivalue)
                            {
                                filter.value = Awesomplete.$("#columnValueDropDown"+index).value;
                            }

                            if (!filter.filter) {
                                validationCheck = false;
                                $mdDialog.show(
                                      $mdDialog.alert()
                                        .clickOutsideToClose(true)
                                        .textContent('')
                                        .title(" Operator in line "
                                        + (index + 1)
                                        + "shouldn't be empty! ")
                                        .ariaLabel('Alert Dialog Demo')
                                        .ok('ok')
                                        .targetEvent(ev)
                            );
                                return;
                            } else if (!filter.column) {
                                validationCheck = false;
                                $mdDialog.show(
                                      $mdDialog.alert()
                                        .clickOutsideToClose(true)
                                        .textContent('')
                                        .title(" Column in line "
                                        + (index + 1)
                                        + " shouldn't be empty! ")
                                        .ariaLabel('Alert Dialog Demo')
                                        .ok('ok')
                                        .targetEvent(ev)
                            );
                                return;
                            } else if (!filter.value) {
                                validationCheck = false;
                                $mdDialog.show(
                                      $mdDialog.alert()
                                        .clickOutsideToClose(true)
                                        .textContent('')
                                        .title(" Value in line "
                                        + (index + 1)
                                        + " shouldn't be empty! ")
                                        .ariaLabel('Alert Dialog Demo')
                                        .ok('ok')
                                        .targetEvent(ev)
                            );
                                return;
                            }
                            filter.columnId = filter.column.columnId;
                            filter.filterId = filter.filter.id;
                        });

                        return validationCheck;
                    };

                    $scope.generateColor = function()
                    {
                        h = 240;
                        s = Math.floor(Math.random() * 100);
                        l = Math.floor(Math.random() * 100);
                        return 'hsl(' + h + ', ' + s + '%, ' + l + '%)';
                    }

                    $scope.executeChartQuery = function()
                    {
                    $http({
                        method : 'GET',
                        url : 'api/charts/executeCountChartQuery?chartId=' + $scope.chart.id
                        }).then(
                        function successCallback(response) {
                        $scope.chart.labels = [];
                        $scope.chart.data = [];
                        $scope.chart.description = [];
                        $scope.chart.colors = [];
                        $scope.chart.emptyPie = true;
                        $scope.lastBuild.allowAddition = true;
                        $scope.lastBuild.selectedColumn = $scope.selectedColumn.data;
                        $scope.lastBuild.selectedSource = $scope.selectedSource.data;

                        if(response.data.length === 0 )
                        {
                            return;
                        }

                        $scope.chart.emptyPie = false;

                        response.data.forEach(function(slice,index)
                        {
                            if(slice.label !== '')
                            {
                                $scope.chart.labels.push(slice.label);
                                $scope.chart.data.push(slice.count);
                                $scope.chart.description.push(slice.label + ": " + slice.count);
                            }
                        })

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
                                            alert("No values for this column!");
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
