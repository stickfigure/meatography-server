//
//

(function() {
	var app = angular.module('meatography', []);

	app.controller('GraphController', ["$http", function ($http) {
		var graph = this;
		graph.data = [
			[new Date(), 1, 1],
		];
		graph.opts = {
			labels: ["Date", "Temp", "Humidity"]
		};

		$http.get("measurements/funky").success(function (data) {
			function farenheit(centigrade) {
				return centigrade * 1.8 + 32;
			}

			graph.data = [];

			for (var i = 0; i < data.when.length; i++) {
				graph.data.push([new Date(data.when[i]), farenheit(data.temperature[i]), data.humidity[i]]);
			};

			graph.opts = {
				labels: ["Date", "Temp", "Humidity"]
			};
		});
	}]);

	// Borrowed from https://github.com/robotnic/angular-dygraphs/blob/master/js/dygraphs-directive.js
	app.directive('dygraph', function() {
		return {
			restrict: 'E', // Use as element
			scope: {
				data: '=',
				opts: '=?', // '?' means optional
				view: '=?' // '?' means optional
			},
			template: "<div class='graph'></div>", // We need a div to attach graph to
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

