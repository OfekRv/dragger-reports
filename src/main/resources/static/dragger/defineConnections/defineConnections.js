angular
		.module("dragger")
		.controller(
				"defineConnectionsController",
				function($scope, $http) {
					$scope.sources = [];
					$scope.firstSourceSelected = null;
					$scope.secondSourceSelected = null;
					$scope.firstSourceColumns = [];
					$scope.secondSourceColumns = [];
					$scope.value = null;

					$http({
						method : 'GET',
						url : '/api/querySources'
					}).then(
							function successCallback(response) {
								angular.forEach(
										response.data._embedded.querySources,
										function(source) {
											$scope.sources.push(source);
										});
							});

					$scope.updateFirstSourceColumns = function() {
						$scope.firstSourceColumns = [];

						$http({
							method : 'GET',
							url : $scope.firstSourceSelected,
						})
								.then(
										function successCallback(response) {
											angular
													.forEach(
															response.data._embedded.queryColumns,
															function(column) {
																$scope.firstSourceColumns
																		.push(column);
															});
										});
						;
					}

					$scope.updateSecondSourceColumns = function() {
						$scope.secondSourceColumns = [];

						$http({
							method : 'GET',
							url : $scope.secondSourceSelected,
						})
								.then(
										function successCallback(response) {
											angular
													.forEach(
															response.data._embedded.queryColumns,
															function(column) {
																$scope.secondSourceColumns
																		.push(column);
															});
										});
						;
					}

					$scope.defineConnection = function(firstEdge, secondEdge) {
						$http({
							method : 'POST',
							url : 'api/sourceConnections',
							data : {
								edges : [ firstEdge, secondEdge ]
							}
						}).then(function successCallback(response) {
							alert("Connection defined!");
						}, function errorCallback(response) {
							alert("Failed defining the connection :(");
						});
						;
					}
				});
