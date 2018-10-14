angular.module("dragger").controller("createSourceController",
		function($scope, $http) {
			function createSource(name, from) {
				return $http({
					method : 'POST',
					url : 'api/querySources',
					data : {
						name : name,
						fromClauseRaw : from
					}
				});
			}
		});
