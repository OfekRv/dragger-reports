angular.module("dragger").controller(
		"defineColumnsController",
		function($scope, $http) {
			$scope.sources = [];
			$scope.dataTypes = [];
			$scope.selected = null;
			$scope.value = null;
			$scope.visible = true;
			$scope.isId = false;

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
						dataType : dataType,
						source : source,
						visible : $scope.visible,
						id : $scope.isId
					}
				}).then(function successCallback(response) {
					alert("Column defined!");
				}, function errorCallback(response) {
					alert("Failed defining the column :(");
				});
				;
			}
		});
