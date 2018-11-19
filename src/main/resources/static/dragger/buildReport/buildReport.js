angular
    .module("dragger")
    .controller(
        "buildReportController",
        function ($scope, $http) {
            $scope.createReport = function () {
                var columns = [];
                angular.forEach($scope.models.lists.Report, function (value, key) {
                    columns.push(value._links.self.href);
                });
                /*
                 * var sources = [];
                 * angular.forEach($scope.models.lists.Report, function(
                 * value, key) { $http( { method : 'GET', url :
                 * value._links.source.href }) .then( function
                 * successCallback( response) {
                 * sources.push(response.data._links.self.href); }); });
                 *
                 * var connections = []; if (sources.length > 1) {
                 * $http( { method : 'POST', url :
                 * '/api/reports/findConnections', data: sources })
                 * .then( function successCallback( response) {
                 * connections = response.data; }); }
                 */
                return $http({
                    method: 'POST',
                    url: 'api/reports',
                    data: {
                        name: $scope.report.name,
                        query: {}
                    }
                }).then(
                    function successCallback(response) {
                        alert("Report created!");
                    }, function errorCallback(response) {
                        alert("Failed creating the report  :(");
                    });


            }

            $scope.dropCallback = function (index, item) {
                $scope.models.lists[item.type].columns.push(item);
                $scope.models.lists['Columns'] = $scope.models.lists['Columns'].filter(
                    function(column)
                    {
                        return !(column.data.name === item.data.name && column.data.type === item.data.type);
                    }
                )
            };

            $scope.$watchCollection('models.lists.Report', function (newReports, oldReports) {
                var columns = [];
                angular.forEach($scope.models.lists.Report, function (value, key) {
                    columns.push(value.columnId);
                });

                if (columns.length > 1) {
                    var isLinked = $http({
                        method: 'POST',
                        url: 'api/queries/isQueryLinked',
                        data: columns
                    }).then(
                        function successCallback(response) {
                            if (!response.data) {
                                alert("This column cannot be linked to your report. \n maybe you need to add other columns to allow that?");
                            }
                        });
                }
            });

            $scope.models = {
                selected: null,
                lists: {
                    "Columns": []
                }
            };

            $http({
                method: 'GET',
                url: '/api/querySources'
            })
                .then(
                    function successCallback(response) {
                        angular
                            .forEach(
                                response.data._embedded.querySources,
                                function (source) {
                                    $scope.models.lists[source.name] = {};
                                    $scope.models.lists[source.name].columns = [];
                                    $scope.models.lists[source.name].allowedTypes = [];
                                    $scope.models.lists[source.name].allowedTypes.push(source.name);

                                    $http(
                                        {
                                            method: 'GET',
                                            url: source._links.columns.href
                                        })
                                        .then(
                                            function successCallback(response) {
                                                angular
                                                    .forEach(
                                                        response.data._embedded.queryColumns,
                                                        function (column) {
                                                            var columnItem = {data:column, type:source.name};
                                                            $scope.models.lists[source.name].columns
                                                                .push(columnItem);
                                                        });
                                            });
                                });
                    });
        });
