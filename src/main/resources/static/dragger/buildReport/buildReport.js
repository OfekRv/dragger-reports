angular
		.module("dragger")
		.controller(
				"buildReportController",
				function($scope, $http, $mdDialog) {
				    $scope.filterSources =function(sourceName, source)
                    				    {
                    				        if(!(source && sourceName) || sourceName.includes('Report') || !source.visible)
                    				        {
                    				            return false;
                    				        }

                    				        if(!$scope.searchSources)
                                            {
                                                $scope.searchSources = '';
                                            }

                                            if(sourceName.toLowerCase().includes($scope.searchSources.toLowerCase()))
                                            {
                                                return true;
                                            }

                    				        for (let i = 0; i < source.columns.length; i++) {
                                                if(source.columns[i].data.name && source.columns[i].data.name.toLowerCase().includes($scope.searchSources.toLowerCase()) &&
                                                    source.columns[i].data.visible)
                                                {
                                                    return true;
                                                }
                                            }

                                            return false;
                    				    };

				    $scope.filterColumns =function(column)
                    				    {
                    				        return column.data.visible;
                    				    };

					$scope.createReport = function(ev) {
						var columns = [];
						var countColumns = [];
						var groupBys = [];
						if(!$scope.report || !$scope.report.name)
						{
						    $mdDialog.show(
                               $mdDialog.alert()
                                 .clickOutsideToClose(true)
                                 .textContent('')
                                 .title("אנא מלא שם דוח")
                                 .ariaLabel('Alert Dialog Demo')
                                 .ok('סבבה')
                                 .targetEvent(ev)
                                 );
						    return;
						}
						angular.forEach($scope.models.lists.Report, function(
								value, key) {
							columns.push(value.data._links.self.href);
						});

						return $http({
							method : 'POST',
							url : 'api/reports',
							data : {
								name : $scope.report.name,
								query : {columns, countColumns, groupBys}
							}
						}).then(function successCallback(response) {
							$mdDialog.show(
                               $mdDialog.alert()
                                 .clickOutsideToClose(true)
                                 .textContent('')
                                 .title("דוח נבנה בהצלחה")
                                 .ariaLabel('Alert Dialog Demo')
                                 .ok('סבבה')
                                 .targetEvent(ev)
                                 );
						}, function errorCallback(response) {
							$mdDialog.show(
                               $mdDialog.alert()
                                 .clickOutsideToClose(true)
                                 .textContent('')
                                 .title("נכשל בבניית הדוח")
                                 .ariaLabel('Alert Dialog Demo')
                                 .ok('סבבה')
                                 .targetEvent(ev)
                                 );
						});
					}

					$scope.dropCallback = function(index, item) {
						$scope.models.lists[item.type].columns.push(item);
						$scope.models.lists['Report'] = $scope.models.lists['Report']
								.filter(function(column) {
									return !(column.data.name === item.data.name && column.type === item.type);
								})
					};

					$scope
							.$watchCollection(
									'models.lists.Report',
									function(newReports, oldReports) {
										var columns = [];
										angular
												.forEach(
														$scope.models.lists.Report,
														function(value, key) {
															columns
																	.push(value.data.columnId);
														});

										if (columns.length > 1) {
											$http(
													{
														method : 'POST',
														url : 'api/queries/isQueryLinked',
														data : columns
													})
													.then(
															function successCallback(
																	response) {
																var isLinked = response.data;
																if (isLinked == "false") {
																	alert("העמודה שאת/ה מנסה להוסיף לא יכולה להיות מקושרת לדוח");
																}
															});
										}
									});

					$scope.models = {
						selected : null,
						lists : {
							"Report" : []
						}
					};

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
                                                                                                    angular.forEach($scope.models.lists[source.name].columns, function(columnInReceivedReport)
                                                                                                    {
                                                                                                        if(columnItem.data.columnId == columnInReceivedReport.data.columnId)
                                                                                                        {
                                                                                                            columnExists = true;
                                                                                                        }
                                                                                                    })

                                                                                                    if(!columnExists && columnItem.data.visible) {
                                                                                                        $scope.models.lists[source.name].columns
                                                                                                            .push(columnItem);
                                                                                                    }
																								});
																			});
														});
									});
				});