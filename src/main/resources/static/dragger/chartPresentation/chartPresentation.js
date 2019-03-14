angular
		.module("dragger")
		.controller(
				"chartPresentationController",
				function($scope, $http,$q) {
				$scope.labels = [];
                $scope.data = [];
                $scope.chartId = 0;
                $scope.labels.push("אין מידע זמין כרגע");
                $scope.data.push(0);

				    $scope.filterSources =function(source)
                    {
                        if(!source || !source.name)
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

                    $scope.filterSourcesList =function(sourceName, source)
                    {
                        if(!source || !sourceName || $scope.models.listsData[sourceName])
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

                    $scope.isLinked = function()
                    {
                        var columns;
                        if ($scope.models.lists.Count.length == 1 &&
                            $scope.models.lists.GroupBy.length == 1) {
                            var columnsRetrievalPromise = [];

                            $scope.models.lists.Count.forEach(function(count) {
                                columnsRetrievalPromise.push($scope.getColumn(count));
                            });

                            $q.all(columnsRetrievalPromise).then(function(columns){
                            var columnIds = [];

                            columns.forEach(function(column)
                            {
                                columnIds.push(column.columnId);
                            });

                            $scope.models.lists.GroupBy.forEach(function(groupBy)
                            {
                                columnIds.push(groupBy.data.columnId);
                            });

                            $http(
                                    {
                                        method : 'POST',
                                        url : 'api/queries/isQueryLinked',
                                        data : columnIds
                                    })
                                    .then(
                                            function successCallback(
                                                    response) {
                                                var isLinked = response.data;
                                                if (isLinked == "false") {
                                                    alert("המקור שאתה מנסה להוסיף לא יכול להיות מקושר לתרשים");
                                                }
                                            });
                                    });
                        }
                    }

					$scope.createChart = function() {
						var columns = [];
						var countColumnsPromises = [];
						var groupBysPromises = [];

						$scope.models.lists.GroupBy.forEach(function(
								value, key) {
							groupBysPromises.push(value);
						});

						$scope.models.lists.Count.forEach(function(
                                value, key) {
                            countColumnsPromises.push($scope.getColumn(value));
                        });

                        $q.all(groupBysPromises).then(function(groupBysResponse){
                        $q.all(countColumnsPromises).then(function(countColumnsResponse){
                        var groupBys = [];
                        var countColumns = [];

                        groupBysResponse.forEach(function(groupBy)
                        {
                            groupBys.push(groupBy.data._links.self.href);
                        })

                        countColumnsResponse.forEach(function(countColumn)
                        {
                            countColumns.push(countColumn._links.self.href);
                        })

                        groupBysResponse.forEach(function(groupBy)
                        {
                            columns.push(groupBy.data._links.self.href);
                        })

						$http({
							method : 'POST',
							url : 'api/charts',
							data : {
								query : {columns, countColumns, groupBys}
							}
						}).then(function successCallback(response) {
						    var hrefOfChart = response.data._links.self.href;
						    var chartId = hrefOfChart.substring(hrefOfChart.lastIndexOf("/") + 1);
                            $http({
                                method : 'GET',
                                url : 'api/charts/executeCountChartQuery?chartId=' + chartId
                                }).then(
                                function successCallback(response) {
                                $scope.labels = [];
                                $scope.data = [];
                                response.data.forEach(function(slice)
                                {
                                    $scope.labels.push(slice.label);
                                    $scope.data.push(slice.count);
                                })},
                                function failureCallback(response) { console.log("couldn't retrieve chart data");
                                return;
                                });
						}, function errorCallback(response) {
							alert("נכשל בבניית התרשים");
						});
						});
						});
					}

					$scope.dropCallbackGroupBy = function(index, item) {
                        $scope.models.lists[item.type].columns.push(item);
                        $scope.models.lists['GroupBy'] = [];
                        $scope.models.listsData['GroupBy'].staticList = true;
                    };

                    $scope.dropCallbackCount = function(index, item) {
                        $scope.models.lists['Sources'].push(item);
                        $scope.models.lists['Count'] = [];
                        $scope.models.listsData['Count'].staticList = true;
                    };

					$scope
							.$watchCollection(
									'models.lists.GroupBy',
									function(newGroupBys, oldGroupBys) {
										$scope.isLinked();
									});

                    $scope
                            .$watchCollection(
                                    'models.lists.Count',
                                    function(newCounts, oldCounts) {
//                                        newCounts.forEach(function(source)
//                                        {
//                                            if(!$scope.models.listsData[source.name].staticList)
//                                            {
//                                                return false;
//                                            }
//                                        })
                                        $scope.isLinked();
                                    });

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
                    $scope.models.lists['GroupBy'].allowedTypes = [];
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
                                                                $scope.models.lists['Sources'].push(source);
                                                                $scope.models.lists[source.name] = {};
                                                                $scope.models.lists[source.name].columns = [];
                                                                $scope.models.lists[source.name].allowedTypes = [];
                                                                $scope.models.lists[source.name].visible = source.visible;
                                                                $scope.models.lists[source.name].allowedTypes.push(source.name);

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
                                                                                                        $scope.models.lists[source.name].columns
                                                                                                            .push(columnItem);
                                                                                                        $scope.models.lists['GroupBy'].allowedTypes.push(columnItem.data.name);
                                                                                                    }
                                                                                                });
                                                                            });

                                                            }
														});
									});
				});