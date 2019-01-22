angular
		.module("dragger")
		.controller(
				"manageReportsController",
				function($scope, $http) {
					$scope.dropCallback = function(index, item) {
					if(confirm("אתה בטוח שברצונך למחוק את הדוח?"))
                    {
                            $http({
                                method : 'DELETE',
                                url : '/api/reports/'+ $scope.models.lists['Reports'][index].id
                            }).then(function(){
                                $scope.initialize();
                            });
                        };
                    }

                    $scope.initialize = function(){
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
                                            response.data._embedded.reports,
                                            function(report) {
                                                $scope.models.lists['Reports']
                                                        .push(report);
                                            });
                                });
					}

					$scope.initialize();
				});