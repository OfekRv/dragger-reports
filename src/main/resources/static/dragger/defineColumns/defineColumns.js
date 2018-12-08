angular.module("dragger").controller(
		"defineColumnsController",
		function($scope, $http) {
			$scope.sources = [];
			$scope.dataTypes = [];
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

			$http({
				method : 'GET',
				url : '/api/columns/availableDataTypes'
			}).then(function successCallback(response) {
				angular.forEach(response.data, function(dataType) {
					$scope.dataTypes.push(dataType);
				});
			});

			$scope.defineColumn = function(name, raw, dataType, source) {
				$http({
					method : 'POST',
					url : 'api/queryColumns',
					data : {
						name : name,
						raw : raw,
						dataType : dataType.type,
						source : source
					}
				}).then(function successCallback(response) {
					alert("Column defined!");
				}, function errorCallback(response) {
					alert("Failed defining the column :(");
				});
				;
			}
		});
