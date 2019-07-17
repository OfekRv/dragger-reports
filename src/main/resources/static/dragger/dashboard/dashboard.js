angular
		.module("dragger")
		.controller(
				"dashboardController",
				function($scope, $http, $mdDialog) {

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
                        }).then(function(response){$scope.chartResultsSetting(response,chart);});
                            });
                        });
					}


                        $scope.chartResultsSetting = function (response, chart) {

                                        chart.labels = [];
                                        chart.data = [];

                                        response.data.forEach(function(slice,index)
                                        {
                                            chart.labels.push(slice.label);
                                            chart.data.push(slice.count);
                                        })

                                            chart.historyLineValue = 1;
                                            $scope.models.lists['Charts']
                                                    .push(chart);
                            }

                    $scope.openChartDialog = function(ev, chart)
                    {
                    console.log(angular.element(document.body));
                        $mdDialog.show({
                              controller: DialogController(chart),
                              templateUrl: 'dragger/dashboard/chartDialog.tmpl.html',
                              parent: angular.element(document.body),
                              targetEvent: ev,
                              clickOutsideToClose:true,
                              fullscreen: false
                            });
                    };

                    function DialogController(chart) {

                        return ($scope, $mdDialog) =>
                        {
                        $scope.chart = chart;
                        $scope.chart.historyLineValue = 1;//document.getElementsByClassName("md-thumb-text").value


                        $scope.valueChanged = function()
                        {
                            document.getElementsByClassName("md-thumb-text")[0].innerHTML=$scope.newDate($scope.chart.historyLineValue);

                            $http({method:'GET',
                                url : 'api/chartExecutionResults/23'
                            }).then(function successCallback(response)
                            {
                                $scope.chartResultsSetting(response,$scope.chart);
                            });
                        };

                        $scope.hide = function() {

                          $mdDialog.hide();
                        };

                        $scope.cancel = function() {
                          $mdDialog.cancel();
                        };

                        $scope.newDate = function(numDaysToSubtract){
                        var day = new Date();
                         day.setDate(day.getDate()-parseInt(numDaysToSubtract,10));
                        var dd = day.getDate();
                        var mm = day.getMonth()+1; //As January is 0.
                        var yyyy = day.getFullYear();

                        if(dd<10) dd='0'+dd;
                        if(mm<10) mm='0'+mm;
                        return (dd + "/" + mm);
                        };

                        $scope.chartResultsSetting = function (response, chart) {

                                chart.labels = [];
                                chart.data = [];

                                response.data.forEach(function(slice,index)
                                {
                                    chart.labels.push(slice.label);
                                    chart.data.push(slice.count);
                                })

                                    chart.historyLineValue = 1;
                                    $scope.models.lists['Charts']
                                            .push(chart);
                        }
                      }
                      }

					$scope.initialize();
				});