angular
		.module("dragger")
		.controller(
				"dashboardController",
				function($scope, $http) {

					$scope.removeChart = function(indexOfChart, chart) {

					Swal.fire({
                      title: 'אתה בטוח שברצונך להסיר את התרשים מהלוח?',
                      text: "",
                      type: 'warning',
                      showCancelButton: true,
                      confirmButtonColor: '#3085d6',
                      cancelButtonColor: '#d33',
                      cancelButtonText: 'בטל',
                      confirmButtonText: 'מחק'
                    }).then((result) => {
                      if (result.value) {
                        $http({method : 'DELETE',
                            url : 'api/dashboard/1/removeChart/' + chart.id
                        }).then(function successCallback(response){
                        $scope.models.lists['Charts'].splice(indexOfChart,1);});
                      }
                    })

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