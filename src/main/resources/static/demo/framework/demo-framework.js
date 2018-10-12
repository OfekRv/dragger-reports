angular.module("dragger", ["ngRoute", "dndLists"])
    .config(function($routeProvider) {
        $routeProvider
            .when('/simple', {
                templateUrl: 'demo/simple/simple-frame.html',
                controller: 'buildReportController'
            })
        })

    .directive('navigation', function($rootScope, $location) {
        return {
            template: '<li ng-repeat="option in options" ng-class="{active: isActive(option)}">' +
                      '    <a ng-href="{{option.href}}">{{option.label}}</a>' +
                      '</li>',
            link: function (scope, element, attr) {
                scope.options = [
                    {label: "Simple Demo", href: "#/simple"}
                ];

                scope.isActive = function(option) {
                    return option.href.indexOf(scope.location) === 1;
                };

                $rootScope.$on("$locationChangeSuccess", function(event, next, current) {
                    scope.location = $location.path();
                });
            }
        };
    });
