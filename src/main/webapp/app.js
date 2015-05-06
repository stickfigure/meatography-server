//
//

(function() {
	var app = angular.module('meatography', []);

	app.controller('GraphController', function ($scope, $http) {
		var graph = this;
		graph.measurements = [];

		$scope.graph = {
			data: [],
			opts: {
				labels: []
			}
		};

		$http.get("measurements/funky").success(function (data) {
			graph.measurements = data;

			$scope.graph = {
				data: [
					[new Date(), 25.3, 12.9],
					[new Date(), 29.3, 8.9]
				],
				opts: {
					labels: ["date", "temperature", "humidity"]
				}
			};

		});
	});

	// Borrowed from https://github.com/robotnic/angular-dygraphs/blob/master/js/dygraphs-directive.js
	app.directive('dygraph', function() {
		return {
			restrict: 'E', // Use as element
			scope: { // Isolate scope
				data: '=', // Two-way bind data to local scope
				opts: '=?', // '?' means optional
				view: '=' // '?' means optional
			},
			template: "<div></div>", // We need a div to attach graph to
			link: function(scope, elem, attrs) {
				var graph = new Dygraph(elem.children()[0], scope.data, scope.opts);

				scope.$watch("data", function() {
					graph.updateOptions({file: scope.data, drawCallback: scope.drawCallback});
				}, true);

				scope.drawCallback = function(data) {
					var xAxisRange = data.xAxisRange();
					if (!scope.view) scope.view = {};
					scope.view.from = xAxisRange[0];
					scope.view.to = xAxisRange[1];
					if (!scope.$root.$$phase) {
						scope.$apply();
					}
				};
			}
		};
	});

})();

