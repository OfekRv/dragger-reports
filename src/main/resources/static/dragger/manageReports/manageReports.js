angular
		.module("dragger")
		.controller(
				"manageReportsController",
				function($scope, $http, $mdDialog) {
					$scope.removeReport = function(ev, item) {

					var confirm = $mdDialog.confirm()
                              .title('את/ה בטוח/ה שברצונך להסיר את הדוח?')
                              .textContent('')
                              .ariaLabel('Lucky day')
                              .targetEvent(ev)
                              .ok('הסר')
                              .cancel('בטל');

                        $mdDialog.show(confirm).then(function() {
                          $http({method : 'DELETE',
                                                          url : '/api/reports/'+ item.id
                                                      }).then(function successCallback(response){
                                                      $scope.initialize();
                                                    });
                        });
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