angular
		.module("dragger")
		.controller(
				"generateReportController",
				function($scope, $http) {
					$scope.reports = [];
					$scope.selectedReport = null;
					$scope.loading = false;

					$scope.filters = [];
					$scope.operators = [];
					$scope.dataTypes = {};

					$scope.filtered = "";

					$http({
						method : 'GET',
						url : '/api/reports'
					}).then(
							function successCallback(response) {
								angular.forEach(
										response.data._embedded.reports,
										function(report) {
											$scope.reports.push(report);
										});
							});

					$http({
						method : 'GET',
						url : '/api/columns/availableDataTypes'
					}).then(function successCallback(response) {
						angular.forEach(response.data, function(dataType) {
							$scope.dataTypes[dataType.type] = dataType.name;
						});
					});

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

					$scope.addFilter = function() {
						$scope.filters.push({
							"value" : null,
							"filter" : null,
							"column" : null
						});
						$scope.filtered = "Filtered";
					};

					$scope.removeFilter = function(filterIndex) {
						$scope.filters.splice(filterIndex, 1);
						$scope.filtered = $scope.filters.length > 0 ? "Filtered"
								: "";
					};

					$scope.changeReport = function() {
						$scope.filters = [];
						$scope.selectedReport.columns = [];
						var report = $scope.selectedReport;

						if (Array.isArray(report.query._links.columns)) {
							angular.forEach(report.query._links.columns,
									function(column) {
										var columnDataPromise = $http({
											method : 'GET',
											url : column.href
										}).then(
												function successCallback(
														response) {
													return response.data;
												});
										columnDataPromise.then(function(
												response) {
											if (report.columns == undefined) {
												report.columns = [];
											}

											report.columns.push(response);
										})
									}, report);
						} else {
							$scope.handleReportColumn({
								"href" : report.query._links.columns.href
							}, report);
						}
					}

					$scope.handleReportColumn = function(column, report) {
						var columnDataPromise = $http({
							method : 'GET',
							url : column.href
						}).then(function successCallback(response) {
							return response.data;
						});
						columnDataPromise.then(function(response) {
							if (report.columns == undefined) {
								report.columns = [];
							}
							report.columns.push(response);
						})
					}
					$scope.downloadUrl = function() {
						var validationCheck = true;
						angular.forEach($scope.filters,
								function(filter, index) {
									if (!filter.filter) {
										validationCheck = false;
										alert("האופרטור בשורה " + (index + 1)
												+ "לא אמור להיות ריק ");
										return;
									} else if (!filter.column) {
										validationCheck = false;
										alert("העמודה בשורה " + (index + 1)
												+ "לא אמור להיות ריקה ");
										return;
									} else if (!filter.value) {
										validationCheck = false;
										alert(" הערך בשורה" + (index + 1)
												+ "לא אמור להיות ריק ");
										return;
									}
									filter.columnId = filter.column.columnId;
									filter.filterId = filter.filter.id;
								});

						if (validationCheck) {
							$scope.loading = true;

							$http(
									{
										method : 'POST',
										url : '/api/reports/generateFilteredReport?reportId='
												+ $scope.selectedReport.id,
										data : $scope.filters,
										responseType : 'arraybuffer'
									})
									.success(
											function(data, status, headers) {
												var headers = headers();

												var fileNameHeader = headers['content-disposition']
														.split(';')[1].trim()
														.split('=')[1];

												var filename = decodeURIComponent(fileNameHeader
														.replace(/"/g, ''));
												var contentType = headers['content-type'];

												var linkElement = document
														.createElement('a');
												try {
													var blob = new Blob(
															[ data ],
															{
																type : contentType
															});
													var url = window.URL
															.createObjectURL(blob);

													linkElement.setAttribute(
															'href', url);
													linkElement.setAttribute(
															"download",
															filename);

													var clickEvent = new MouseEvent(
															"click",
															{
																"view" : window,
																"bubbles" : true,
																"cancelable" : false
															});
													linkElement
															.dispatchEvent(clickEvent);
												} catch (ex) {
													console.log(ex);
												}
												$scope.loading = false;
											}).error(function(data) {
										console.log(data);
										$scope.loading = false;
									});
						}
					}
				});
