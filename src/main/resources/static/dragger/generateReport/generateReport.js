angular.module("dragger").controller(
		"generateReportController",
		function($scope, $http) {
			$scope.reports = [];
			$scope.selected = null;
			$scope.value = null;

			$http({
				method : 'GET',
				// 100 is a realistic number of report that we wont reach
				url : '/api/reports?size=100'
			}).then(
					function successCallback(response) {
						angular.forEach(response.data._embedded.reports,
								function(report) {
									$scope.reports.push(report);
								});
					});
		});
