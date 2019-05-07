angular
		.module("dragger")
		.controller(
				"dashboardController",
				function($scope, $http) {

					$scope.deleteCard = function(indexOfChart, item) {
					if(confirm("אתה בטוח שברצונך למחוק את הדוח?"))
                    {
                        $http({
                            url : 'api/dashboard/delete'
                        }).then(function successCallback(response){
                        $scope.models.lists['Charts'].splice(indexOfChart,1);});
                        };
                    }

                    $scope.createCard = function(index, item) {

                    }

                    $scope.suggestCharts = function(index, item) {

                    }

                    $scope.initialize = function(){

                        $scope.models = {
                            selected : null,
                            lists : {
                                "Charts" : []
                            }
                        };
//                        $http({
//                            url : 'api/dashboard/'
//                        }).then(function successCallback(response)
//                        response.data.forEach(function(chart)

                        $http({
                            url : 'api/charts/executeCountChartQuery?chartId=100'
                        }).then(
                                function successCallback(response) {
                                            var chart = {labels:[],data:[]};
                                            var chart2 = {labels:[],data:[]};
                                            var chart3 = {labels:[],data:[]};
                                            var chart4 = {labels:[],data:[]};
                                            var chart5 = {labels:[],data:[]};
                                            var chart6 = {labels:[],data:[]};

                                            response.data.forEach(function(slice,index)
                                            {
                                                chart.labels.push(slice.label);
                                                chart.data.push(slice.count);
                                                chart2.labels.push(slice.label);
                                                chart2.data.push(slice.count);
                                                chart3.labels.push(slice.label);
                                                chart3.data.push(slice.count);
                                                chart4.labels.push(slice.label);
                                                chart4.data.push(slice.count);
                                                chart5.labels.push(slice.label);
                                                chart5.data.push(slice.count);
                                                chart6.labels.push(slice.label);
                                                chart6.data.push(slice.count);
                                            })
                                                console.log(chart);
                                                $scope.models.lists['Charts']
                                                        .push(chart);
                                                $scope.models.lists['Charts']
                                                        .push(chart2);
                                                $scope.models.lists['Charts']
                                                        .push(chart3);
                                                $scope.models.lists['Charts']
                                                        .push(chart4);
                                                $scope.models.lists['Charts']
                                                        .push(chart5);
                                                $scope.models.lists['Charts']
                                                        .push(chart6);
                                                $scope.models.lists['Charts']
                                                        .push(chart7);
                                                $scope.models.lists['Charts']
                                                        .push(chart8);
                                });
					}

					$scope.initialize();
				});