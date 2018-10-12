angular.module("dragger").controller(
		"buildReportController",
		function($scope, $http) {
			$scope.models = {
				selected : null,
				lists : {
					"Columns" : [],
					"Report" : []
				}
			};

			$http({
				method : 'GET',
				url : '/api/queryColumns'
			}).then(
					function successCallback(response) {
						// $scope.models.lists.Columns = response.data;
						angular.forEach(response.data._embedded.queryColumns,
								function(column) {
									$scope.models.lists.Columns.push({
										data : column
									});
								});
					});

			// Model to JSON for demo purpose
			$scope.$watch('models', function(model) {
				$scope.modelAsJson = angular.toJson(model, true);
			}, true);

		});
