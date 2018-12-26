angular
		.module("dragger")
		.controller(
				"manageReportsController",
				function($scope, $http) {
					$scope.dropCallback = function(index, item) {
						$scope.models.lists[item.type].reports.push(item);
						$scope.models.lists['Reports'] = $scope.models.lists['Reports']
								.filter(function(report) {
									return !(report.data.name === item.data.name && report.type === item.type);
								})
					};

					$scope.models = {
						selected : null,
						lists : {
							"Reports" : []
						}
					};

					$http({
						method : 'GET',
						url : '/api/reports'
					}).then(
							function successCallback(response) {
								angular.forEach(
										response.data._embedded.querySources,
										function(report) {
											$scope.models.lists['Reports']
													.push(source.name);
										});
							});
				});