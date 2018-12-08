angular
		.module("dragger")
		.controller(
				"buildReportController",
				function($scope, $http) {
					$scope.createReport = function() {
						var columns = [];
						angular.forEach($scope.models.lists.Report, function(
								value, key) {
							columns.push(value.data._links.self.href);
						});

						return $http({
							method : 'POST',
							url : 'api/reports',
							data : {
								name : $scope.report.name,
								query : {columns}
							}
						}).then(function successCallback(response) {
							alert("דוח נבנה בהצלחה!");
						}, function errorCallback(response) {
							alert("נכשל בבניית הדוח");
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
																	alert("העמודה שאתה מנסה להוסיף לא יכולה להיות מקושרת לדוח");
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
                                                                                                    if(!$scope.models.lists[source.name].columns.includes(columnItem)) {
                                                                                                        $scope.models.lists[source.name].columns
                                                                                                            .push(columnItem);
                                                                                                    }
																								});
																			});
														});
									});
				});