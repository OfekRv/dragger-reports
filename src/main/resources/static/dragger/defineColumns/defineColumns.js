angular.module("dragger").controller(
		"defineColumnsController",
		function($scope, $http) {
			$scope.sources = [];
			$scope.selected = null;
			$scope.value = null;

			$http({
				method : 'GET',
				url : '/api/querySources'
			}).then(
					function successCallback(response) {
						angular.forEach(response.data._embedded.querySources,
								function(source) {
									$scope.sources.push(source);
								});
					});

			$scope.defineColumn = function(name, raw, source) {
				$http({
					method : 'POST',
					url : 'api/queryColumns',
					data : {
						name : name,
						raw : raw,
						source : source
					}
				});
			}
		});
