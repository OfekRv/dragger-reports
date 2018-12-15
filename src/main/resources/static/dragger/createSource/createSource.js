angular.module("dragger").controller("createSourceController",
		function($scope, $http) {
			$scope.createSource = function() {
				return $http({
					method : 'POST',
					url : 'api/querySources',
					data : {
						name : $scope.source.name,
						fromClauseRaw : $scope.source.from
					}
				}).then(function successCallback(response) {
					alert("source created!");
				}, function errorCallback(response) {
					alert("Failed to create the source :(");
				});
			}
		});
