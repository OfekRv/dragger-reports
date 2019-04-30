angular
		.module("dragger")
		.controller(
				"dashboardController",
				function($scope, $http) {
				    var REFRESH_INTERVAL_MILLISECONDS = 60000;

					$scope.dropCallback = function(index, item) {
					if(confirm("אתה בטוח שברצונך למחוק את הדוח?"))
                    {

                        };
                    }

                    $scope.initialize = function(){
                        $scope.models = {
                            selected : null,
                            lists : {
                                "Charts" : []
                            }
                        };
                        $http({
                            url : 'api/charts/executeCountChartQuery?chartId=505'
                        }).then(
                                function successCallback(response) {
                                    angular.forEach(
                                            response.data,
                                            function(report) {
                                                $scope.models.lists['Charts']
                                                        .push(report);
                                            });
                                });
					}

					$scope.initialize();
				});