angular
		.module("dragger")
		.controller(
				"manageReportsController",
				function($scope, $http) {
					$scope.removeReport = function(index, item) {
                        Swal.fire({
                          title: 'את/ה בטוח/ה שברצונך להסיר את הדוח?',
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
                                url : '/api/reports/'+ $scope.models.lists['Reports'][index].id
                            }).then(function successCallback(response){
                            $scope.initialize();
                          });
                          }
                        });
//                            $http({
//                                method : 'DELETE',
//                                url : '/api/reports/'+ $scope.models.lists['Reports'][index].id
//                            }).then(function(){
//                                $scope.initialize();
//                            });
//                        };
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