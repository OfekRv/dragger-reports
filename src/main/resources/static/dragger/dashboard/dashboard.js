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
                                        $http({method:'GET',
                                            url : 'api/charts/' + chart.id + '/filters'
                                        }).then(function successCallback(response)
                                        {
                                            chart.hasFilterData = response.data._embedded.chartQueryFilters.length > 0;
                                            $scope.models.lists['Charts']                                                                                                          .push(chart);
                                        });
                            }

                    $scope.openChartDialog = function(ev, chart)
                    {
                    console.log(angular.element(document.body));
                        $mdDialog.show({
                              controller: DialogController(chart),
                              templateUrl: 'dragger/dashboard/chartDialogg.tmpl.html',
                              parent: angular.element(document.body),
                              targetEvent: ev,
                              clickOutsideToClose:true,
                              fullscreen: false
                            });
                    };

                     $scope.openFilterDetailsDialog = function(ev, chart)
                        {
                        console.log(angular.element(document.body));
                            $mdDialog.show({
                                  controller: FilterDetailsController(chart),
                                  templateUrl: 'dragger/dashboard/filterDetailsDialogg.tmpl.html',
                                  parent: angular.element(document.body),
                                  targetEvent: ev,
                                  clickOutsideToClose:true,
                                  fullscreen: false
                                });
                        };

                    function FilterDetailsController(chart) {
                        return ($scope, $mdDialog) =>
                        {
                            $scope.chartDetails = {id: chart.id, name: chart.name};
                            $scope.chartFilters = [];
                            $scope.filters = [];

                            $scope.loadFilters = function()
                            {
                                $http({method:'GET',
                                    url : 'api/filters'
                                }).then(function successCallback(response)
                                {
                                    response.data._embedded.filters.forEach(function(filter)
                                   {
                                       $scope.filters[filter.id] = filter.name;
                                   });

                                        $http({method:'GET',
                                           url : 'api/charts/' + $scope.chartDetails.id + '/filters'
                                       }).then(function successCallback(response)
                                       {
                                            response.data._embedded.chartQueryFilters.forEach(function(filter)
                                            {
                                           $http({method:'GET',
                                               url : filter._links.column.href
                                           }).then(function successCallback(response)
                                           {
                                                    filter.columnName = response.data.name;
                                                    filter.filterName = $scope.filters[parseInt(filter.filterId)];
                                                    $scope.chartFilters.push(filter);
                                               });
                                           });
                                       });
                                    });
                            }

                            $scope.hide = function() {
                              $mdDialog.hide();
                            };

                            $scope.cancel = function() {
                              $mdDialog.cancel();
                            };


                            $scope.loadFilters();
                        }
                    }

                    function DialogController(chart) {

                        return ($scope, $mdDialog) =>
                        {
                        $scope.timeLineChart = {id:chart.id,name: chart.name, labels: chart.labels, data: chart.data, options:chart.options,historyLineValue : 1};
                        $scope.weekCounter = 0;
                        $scope.availableResults = true;
                        $scope.datePicked = {isOpen: false, data:null};
                        $scope.lastWeekResults = [];

                        $scope.retrieveLastWeekResults = function()
                        {
                            $http({method:'GET',
                                url : 'api/chartExecutionResults/'+$scope.timeLineChart.id
                            }).then(function successCallback(response)
                            {
                                $scope.lastWeekResults = response.data;
                            });
                        };

                        $scope.handleTimeLinePick = function()
                        {
                            var timeLineValue = $scope.handleLabel();
                            $scope.availableResults = false;
                            $scope.lastWeekResults.forEach(function(result)
                            {
                                if(result.id.executionDate === $scope.newHyphenDate(timeLineValue))
                                {
                                    $scope.availableResults = true;
                                    $scope.chartResultsSetting(result.executionResult,$scope.timeLineChart);
                                }
                            });
                            $scope.headline = $scope.currentWeekRange();
                        }

                        $scope.handleLabel = function()
                        {
                            var timeLineValue = $scope.newDate($scope.timeLineChart.historyLineValue - 1 + ($scope.weekCounter*7));
                            document.getElementsByClassName("md-thumb-text")[0].innerHTML = $scope.newBackslashDate(timeLineValue);
                            return timeLineValue;
                        }

                        $scope.nextPage = function()
                        {
                            $scope.weekCounter++;
                            $scope.headline = $scope.currentWeekRange();
                            $scope.handleTimeLinePick();
                        }

                        $scope.previousPage = function()
                        {
                            if($scope.weekCounter === 0)
                            {
                                return;
                            }
                            $scope.weekCounter--;
                            $scope.headline = $scope.currentWeekRange();
                            $scope.handleTimeLinePick();
                        }

                        $scope.datePicked = function()
                        {
                            var dayDifference = $scope.newDate(0) - new Date($scope.datePicked);
                            $scope.timeLineChart.historyLineValue = dayDifference % 7 ;
                            $scope.weekCounter = dayDifference / 7 ;
                            $scope.headline = $scope.currentWeekRange();
                            $scope.handleTimeLinePick();
                        }

                        $scope.hide = function() {

                          $mdDialog.hide();
                        };

                        $scope.cancel = function() {
                          $mdDialog.cancel();
                        };

                        $scope.newDate = function(numDaysToSubtract){
                        var day = new Date();
                         day.setDate(day.getDate()-parseInt(numDaysToSubtract,10));
                        return day;
                        };

                        $scope.newHyphenDate = function(day){
                        var dd = day.getDate();
                        var mm = day.getMonth()+1; //As January is 0.
                        var yyyy = day.getFullYear();

                        if(dd<10) dd='0'+dd;
                        if(mm<10) mm='0'+mm;
                        return (yyyy + "-" + mm +  "-" + dd);
                        };

                        $scope.newBackslashDate = function(day){
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

                                response.results.forEach(function(slice,index)
                                {
                                    chart.labels.push(slice.label);
                                    chart.data.push(slice.count);
                                })
                        }

                        $scope.currentWeekRange = function()
                        {
                            return $scope.newBackslashDate($scope.newDate(6 + ($scope.weekCounter*7))) + " - "
                            + $scope.newBackslashDate($scope.newDate(0 + ($scope.weekCounter*7))) + "(התאריך הנוכחי:"
                            + $scope.newBackslashDate($scope.newDate(($scope.timeLineChart.historyLineValue - 1) + ($scope.weekCounter*7))) + ")";
                        }

                        $scope.retrieveLastWeekResults();
                        $scope.headline = $scope.currentWeekRange();
                      }
                      }

					$scope.initialize();
				});