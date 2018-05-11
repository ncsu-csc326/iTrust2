'use strict';

angular.module("logsApp").controller('logsCtrl', function($scope, $http) {
	var self = this;
	self.logs = [];
	self.requestParams = {startDate: null, endDate: null, page: 1, pageLength: 10};
	
	self.startDateStr = "";
	self.endDateStr   = "";
	
	self.updateTable = function(){
		self.requestParams.startDate = null;
		self.requestParams.endDate = null;
		if(isDate(self.startDateStr) && isDate(self.endDateStr)){
			self.requestParams.startDate = new Date(self.startDateStr);
			self.requestParams.endDate = new Date(self.endDateStr);
		}
		
		$http.get("/api/v1/logentries/range/", self.requestParams).then(function (response) {
			self.logs = response.data;
		});
	}
	

	var isDate = function(input) {
		if (!input) {
			return false;
		}
		var match = /^(\d?\d)\/(\d?\d)\/(\d{4})$/.exec(input);
		if (!match) {
			return false;
		}
		var parsedDate = {
			year : +match[3],
			month : +match[1] - 1,
			day : +match[2]
		};
		if (parsedDate.year < 0 || parsedDate.month < 0
				|| parsedDate.month > 12 || parsedDate.day < 0
				|| parsedDate.day > 31) {
			return false;
		}
		var date = new Date(parsedDate.year, parsedDate.month,
				parsedDate.day);
		return date.getFullYear() === parsedDate.year
				&& date.getMonth() === parsedDate.month
				&& date.getDate() === parsedDate.day;
	};

	var checkValidDateRange = function(dateRange) {
		var err = [];
		if (!isDate(dateRange.startDate)) {
			err.push("Start date is in an invalid format");
		}
		if (!isDate(dateRange.endDate)) {
			err.push("End date is in an invalid format");
		}
		if (dateRange.startDate.getFullYear() > dateRange.endDate
				.getFullYear()) {
			err.push("Start date must come before end date.");
		}
		if (dateRange.startDate.getFullYear() == dateRange.endDate
				.getFullYear()
				&& dateRange.startDate.getFullMonth() > dateRange.endDate
						.getMonth()) {
			err.push("Start date must come before end date.");
		}
		if (dateRange.startDate.getFullYear() == dateRange.endDate
				.getFullYear()
				&& dateRange.startDate.getFullMonth() == dateRange.endDate
						.getMonth()
				&& dateRange.startDate.getDate() > dateRange.endDate
						.getDate()) {
			err.push("Start date must come before end date.");
		}

		return err.join(". ");
	}

	
	updateTable();
	
	
	/*
	$scope.loadTable = function() {
		$http.get("/iTrust2/api/v1/logs").then(function(response) {
			$scope.logs = response.data;
			$scope.message = "";
		}, function(rejection) {
			$scope.logs = [];
			$scope.message = "Could not display logs";
		});
	}
	
	$scope.searchLogs = function() {
		var err = checkValidDateRange($scope.dateRange);
		if (err) {
			$scope.errorSearching = err;
			return;
		}
		$http.get("/iTrust2/api/v1/logs").then(function(response) {
			$scope.logs = response.data;
			$scope.message = "";
			for (log in $scope.logs) {
				if (log.getTime() < $scope.dateRange.startDate
						&& log.getTime() > $scope.dateRange.endDate) {
					var index = $scope.logs
							.indedOf(log);
					$scope.logs
							.splice(index, 1);
				}
			}
		},
		function(rejection) {
			$scope.logs = [];
			$scope.errorGetting = "Could not obtain log table";
		});
	}
	*/
	
});