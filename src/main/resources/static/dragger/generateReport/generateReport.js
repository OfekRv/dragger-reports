angular.module("dragger").controller(
		"generateReportController",
		function($scope, $http) {
			$scope.reports = [];
			$scope.selected = null;
			$scope.value = null;

			$http({
				method : 'GET',
				url : '/api/reports'
			}).then(
					function successCallback(response) {
						angular.forEach(response.data._embedded.reports,
								function(report) {
									$scope.reports.push(report);
								});
					});
		});
