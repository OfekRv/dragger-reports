angular
		.module("dragger")
		.controller(
				"buildReportController",
				function($scope, $http) {
					$scope.createReport = function() {
						var columns = [];
						angular.forEach($scope.models.lists.Report, function(
								value, key) {
							columns.push(value._links.self.href);
						});
						/*
						 * var sources = [];
						 * angular.forEach($scope.models.lists.Report, function(
						 * value, key) { $http( { method : 'GET', url :
						 * value._links.source.href }) .then( function
						 * successCallback( response) {
						 * sources.push(response.data._links.self.href); }); });
						 * 
						 * var connections = []; if (sources.length > 1) {
						 * $http( { method : 'POST', url :
						 * '/api/reports/findConnections', data: sources })
						 * .then( function successCallback( response) {
						 * connections = response.data; }); }
						 */
						return $http({
							method : 'POST',
							url : 'api/reports',
							data : {
								name : $scope.report.name,
								query : {
									columns
								}
							}
						});
					}
					
					 $scope.$watchCollection('models.lists.Report', function(newReports, oldReports) {
						var columns = [];
						angular.forEach($scope.models.lists.Report, function(
								value, key) {
							columns.push(value.columnId);
						});
						
						if (columns.length > 1)
						{
							var isLinked = $http({
								method : 'POST',
								url : 'api/queries/isQueryLinked',
								data : columns
							});
							
							if (!isLinked)
							{
								alert("This column cannot be linked to your report. \n maybe you need to add other columns to allow that?");
							}
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
															$scope.models.lists[source.name] = [];

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
																									$scope.models.lists[source.name]
																											.push(column);
																								});
																			});
														});
									});
				});
