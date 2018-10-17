angular
		.module("dragger", [ "ngRoute", "dndLists" ])
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
									'/generateReport',
									{
										templateUrl : 'dragger/generateReport/generateReport-frame.html',
										controller : 'generateReportController'
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
				})

		.directive(
				'navigation',
				function($rootScope, $location) {
					return {
						template : '<li ng-repeat="option in options" ng-class="{active: isActive(option)}">'
								+ '    <a ng-href="{{option.href}}">{{option.label}}</a>'
								+ '</li>',
						link : function(scope, element, attr) {
							scope.options = [ {
								label : "Generate Report",
								href : "#/generateReport"
							}, {
								label : "Build Report",
								href : "#/buildReport"
							}, {
								label : "Create Source",
								href : "#/createSource"
							}, {
								label : "Define Columns",
								href : "#/defineColumns"
							} ];

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
