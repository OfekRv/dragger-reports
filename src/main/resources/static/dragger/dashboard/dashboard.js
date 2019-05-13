angular
		.module("dragger")
		.controller(
				"dashboardController",
				function($scope, $http) {

					$scope.removeChart = function(indexOfChart, item) {
					if(confirm("אתה בטוח שברצונך למחוק את הדוח?"))
                    {
                        $http({method : 'DELETE',
                            url : 'api/dashboards/1',
                            data : {
                                dashboard:1,
                                chartToRemove:item
                            }
                        }).then(function successCallback(response){
                        $scope.models.lists['Charts'].splice(indexOfChart,1);});
                        };
                    }

                    $scope.initialize = function(){

                        $scope.models = {
                            selected : null,
                            lists : {
                                "Charts" : []
                            }
                        };

                        $http({method:'GET',
                            url : 'api/dashboards/1'
                        }).then(function successCallback(response)
                        {
                        response.data.charts.forEach(function(chart)
                        {
                        $http({
                            url : 'api/charts/executeCountChartQuery?chartId='+chart.id
                        }).then(
                                function successCallback(response) {

                                            chart.labels = [];
                                            chart.data = [];

                                            response.data.forEach(function(slice,index)
                                            {
                                                chart.labels.push(slice.label);
                                                chart.data.push(slice.count);
                                            })

                                                console.log(chart);
                                                $scope.models.lists['Charts']
                                                        .push(chart);
                                });
                            });
                        });
					}

					$scope.initialize();
				});