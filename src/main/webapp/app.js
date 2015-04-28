//
//

(function() {
	var app = angular.module('meatography', []);

	app.controller('GraphController', ['$http', function ($http) {
		var graph = this;
		graph.measurements = [];

		$http.get("measurements").success(function (data) {
			graph.measurements = data;
		});
	}]);
})();

