(function(){angular
		.module("dragger", [ "ngRoute", "dndLists", "chart.js","ngMaterial"])
		.config(
				function($routeProvider) {
					$routeProvider
							.when(
									'/buildReport',
									{
										templateUrl : 'dragger/buildReport/buildReport-frame.html',
										controller : 'buildReportController'
									})
                            .when(
                                    '/chartPresentation',
                                    {
                                        templateUrl : 'dragger/chartPresentation/chartPresentation.html',
                                        controller : 'chartPresentationController'
                                    })
                            .when(
                                    '/dashboard',
                                    {
                                        templateUrl : 'dragger/dashboard/dashboard.html',
                                        controller : 'dashboardController'
                                    })
							.when(
									'/generateReport',
									{
										templateUrl : 'dragger/generateReport/generateReport-frame.html',
										controller : 'generateReportController'
									})

							.when(
									'/manageReports',
									{
										templateUrl : 'dragger/manageReports/manageReports-frame.html',
										controller : 'manageReportsController'
									})

							.when(
									'/createSource',
									{
										templateUrl : 'dragger/createSource/createSource-frame.html',
										controller : 'createSourceController'
									})

							.when(
									'/defineColumns',
									{
										templateUrl : 'dragger/defineColumns/defineColumns-frame.html',
										controller : 'defineColumnsController'
									})
							.when(
									'/defineConnections',
									{
										templateUrl : 'dragger/defineConnections/defineConnections-frame.html',
										controller : 'defineConnectionsController'
									}).otherwise({
								redirectTo : '/generateReport'
							})
				})

		.directive(
				'navigation',
				function($rootScope, $location, $http) {
					return {
						template : '<li class="dragger-logo"><a ng-href=#!/dashboard></a></li>'+
						'<li ng-repeat="option in options" ng-class="{active: isActive(option)}">'
								+ '    <a ng-href="{{option.href}}">{{option.label}}</a>'
								+ '</li>',
						link : function(scope, element, attr) {

							$http({
								method : 'GET',
								url : 'api/isDeveloperMode',
							}).then(function successCallback(response) {
								var isDevMode = response.data

								if (isDevMode != false) {
									scope.options = [{
                                        label : "Dashboard",
                                        href : "#!/dashboard"
                                    }, {
                                        label : "Generate report",
                                        href : "#!/generateReport"
                                    }, {
										label : "Build report",
										href : "#!/buildReport"
									}, {
										label : "Manage reports",
										href : "#!/manageReports"
									}, {
                                       label : "Chart",
                                       href : "#!/chartPresentation"
                                    }, {
										label : "Create Source",
										href : "#!/createSource"
									}, {
										label : "Define Columns",
										href : "#!/defineColumns"
									}, {
										label : "Define Connections",
										href : "#!/defineConnections"
									} ];
								} else {
									scope.options = [ {
                                        label : "Dashboard",
                                        href : "#!/dashboard"
                                    },{
										label : "Generate report",
										href : "#!/generateReport"
									},  {
										label : "Build report",
										href : "#!/buildReport"
									}  ,{
                                        label : "Manage reports",
                                        href : "#!/manageReports"
                                    }, {
                                        label : "Chart",
                                        href : "#!/chartPresentation"
                                    }];
								}
							});

							scope.isActive = function(option) {
								return option.href.indexOf(scope.location) === 1;
							};

							$rootScope.$on("$locationChangeSuccess", function(
									event, next, current) {
								scope.location = $location.path();
							});
						}
					};
				});
})();
