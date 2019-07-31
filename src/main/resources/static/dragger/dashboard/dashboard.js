angular
		.module("dragger")
		.controller(
				"dashboardController",
				function($scope, $http, $mdDialog, $mdPanel) {

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
                                            $scope.models.lists['Charts'].push(chart);
                                        });
                            }

                    $scope.openChartDialog = function(ev, chart)
                    {
                        $mdDialog.show({
                              controller: TimeLineController(chart),
                              templateUrl: 'dragger/dashboard/chartDialogg.tmpl.html',
                              parent: angular.element(document.body),
                              targetEvent: ev,
                              clickOutsideToClose:true,
                              fullscreen: false
                            });
                    };

                     $scope.openFilterDetailsDialog = function(ev, chart)
                        {
                            $mdDialog.show({
                                  controller: FilterDetailsController(chart),
                                  templateUrl: 'dragger/dashboard/filterDetailsDialog.tmpl.html',
                                  parent: angular.element(document.body),
                                  targetEvent: ev,
                                  clickOutsideToClose:true,
                                  fullscreen: false
                                });
                        };

                    $scope.openLegendDialog = function(ev, chart)
                                            {
                                                                      var position = $mdPanel.newPanelPosition()
                                                                          .relativeTo('.legend-button')
                                                                          .addPanelPosition($mdPanel.xPosition.ALIGN_START, $mdPanel.yPosition.BELOW);

                                                                      var config = {
                                                                        attachTo: angular.element(document.body),
                                                                        controller: LegendController(chart),
                                                                        template: '<div class="demo-menu-example" ' +
                                                                                          '     aria-label="Select your favorite dessert."' +
                                                                                          '  <div class="demo-menu-item" ' +
                                                                                          '       ng-repeat="label in chart.labels" ' +
                                                                                          '    <h4>{{chart.data[$index]}}: {{label}}</h4> ' +
                                                                                          '  </div>' +
                                                                                          '</div>',
                                                                        panelClass: 'demo-menu-example',
                                                                        position: position,
                                                                        locals: {
                                                                              'chart': chart
                                                                            },
                                                                        openFrom: ev,
                                                                        clickOutsideToClose: true,
                                                                        escapeToClose: true,
                                                                        focusOnOpen: false,
                                                                        zIndex: 2
                                                                      };

                                                                      $mdPanel.open(config);
                                            };

                    function LegendController(chart)
                    {
                        return ($scope) =>
                        {

                            $scope.chart = {id: chart.id, name: chart.name, options: chart.options, colors : [], labels: chart.labels, data: chart.data};
                        }
                    }

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

                    function TimeLineController(chart) {

                        return ($scope, $mdDialog) =>
                        {
                        $scope.timeLineChart = {id:chart.id,name: chart.name, labels: chart.labels, data: chart.data, options:chart.options,historyLineValue : 1};
                        $scope.weekCounter = 0;
                        $scope.availableResults = true;
                        $scope.pickedDateOpen = false;
                        $scope.datePickerChanged = true;
                        $scope.maxDate = new Date();
                        $scope.pickedDate = new Date();
                        $scope.weekResults = [];
                        $scope.$watch(function($scope) { return $scope.pickedDate },
                              function() {
                              if(!$scope.datePickerChanged)
                              {
                                    $scope.datePickerChanged = !$scope.datePickerChanged;
                                    return;
                              }

                                var todayDate = new Date();
                                todayDate.setHours(0,0,0,0);
                                $scope.pickedDate.setHours(0,0,0,0);
                                var dayDifference = $scope.diff_days(todayDate, $scope.pickedDate);
                                    if(Math.floor(dayDifference / 7 ) != $scope.weekCounter)
                                    {
                                        $http({method:'GET',
                                            url : 'api/chartExecutionResults/'+$scope.timeLineChart.id + '/' + $scope.newHyphenDate($scope.pickedDate)}).then(function(response){
                                                $scope.weekResults = response.data;
                                                $scope.loadNewChart();
                                           });

                                        $scope.weekCounter = Math.floor(dayDifference / 7 );
                                        $scope.headline = $scope.currentWeekRange();
                                    }
                                    else
                                    {
                                        $scope.loadNewChart();
                                    }

                                    $scope.timeLineChart.historyLineValue = (dayDifference % 7) + 1;
                          });

                        $scope.retrieveWeekResults = function(receivedDate, loadChart)
                        {
                            return $http({method:'GET',
                                url : 'api/chartExecutionResults/'+$scope.timeLineChart.id + '/' + receivedDate
                            }).then(function successCallback(response)
                            {
                                $scope.weekResults = response.data;
                                if(loadChart)
                                {
                                    $scope.loadNewChart();
                                }
                            });
                        };

                        $scope.handleTimeLinePick = function()
                        {
                            $scope.handleLabel();
                            $scope.loadNewChart();
                        }

                        $scope.handleLabel = function()
                        {
                            var timeLineValue = $scope.newDate($scope.timeLineChart.historyLineValue - 1 + ($scope.weekCounter*7));
                            document.getElementsByClassName("md-thumb-text")[0].innerHTML = $scope.newBackslashDate(timeLineValue);
                            timeLineValue.setHours(0,0,0,0);
                            $scope.datePickerChanged = false;
                            $scope.pickedDate = timeLineValue;
                            return timeLineValue;
                        }

                        $scope.loadNewChart = function()
                        {
                            $scope.availableResults = false;
                            $scope.weekResults.forEach(function(result)
                            {
                                if(result.id.executionDate === $scope.newHyphenDate($scope.pickedDate))
                                {
                                    $scope.availableResults = true;
                                    $scope.chartResultsSetting(result.executionResult,$scope.timeLineChart);
                                }
                            });
                            $scope.headline = $scope.currentWeekRange();
                        }

                        $scope.nextPage = function()
                        {
                            $scope.datePickerChanged = false;
                            $scope.weekCounter++;
                            $scope.headline = $scope.currentWeekRange();
                            $scope.handleLabel();
                            $scope.retrieveWeekResults($scope.newHyphenDate($scope.pickedDate), true);
                        }

                        $scope.previousPage = function()
                        {
                            if($scope.weekCounter === 0)
                            {
                                return;
                            }

                            $scope.datePickerChanged = false;
                            $scope.weekCounter--;
                            $scope.headline = $scope.currentWeekRange();
                            $scope.handleLabel();
                            $scope.retrieveWeekResults($scope.newHyphenDate($scope.pickedDate), true);
                        }

                        $scope.hide = function() {

                          $mdDialog.hide();
                        };

                        $scope.cancel = function() {
                          $mdDialog.cancel();
                        };

                        $scope.diff_days = function(dt2, dt1)
                        {
                             var diff =(dt2.getTime() - dt1.getTime()) / 1000;
                             diff /= (60 * 60 * 24);
                             return Math.abs(Math.round(diff));
                        }

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
                            + $scope.newBackslashDate($scope.newDate(0 + ($scope.weekCounter*7)));
                        }

                        $scope.retrieveWeekResults($scope.newHyphenDate(new Date()), true);
                      }
                      }

					$scope.initialize();
				});