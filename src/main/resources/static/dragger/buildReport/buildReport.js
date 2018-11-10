angular
		.module("dragger")
		.controller(
				"buildReportController",
				function($scope, $http) {
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
