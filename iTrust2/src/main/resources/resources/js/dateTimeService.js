/**
 * Helper functions for managing dates/times.
 * 
 * @author Matt Dzwonczyk
 */
'use strict';

angular.module('dateTimeServices', [])
    .service('dateTimeService', function () {

        /**
         * Helper that converts a JavaScript Date 
         * object to an ISO date string.
         * @param date The JavaScript Date to convert.
         */
        this.toDateString = function(date) {
            // Set the time to zero for accurate time conversion
            date.setHours(0, 0);
            return date.toISOString().substring(0, 10);
        }

        /**
         * Validates prescription date.
         * @param input The input object to validate.
         */
        this.isValidDate = (input) => input instanceof Date && !isNaN(input);

        /**
         * Gets the age given a birth date.
         * @param date The date to get the age from (optional, default is now)
         * @param birthDate The birthdate for which to calculate age.
         */
        this.getAge = function(birthDate, date = new Date()) {
            // Provides accurate comparison despite timezone conversion
            birthDate = new Date(`${birthDate.getUTCFullYear()}/${birthDate.getUTCMonth() + 1}/${birthDate.getUTCDate()}`);
            
            var age = date.getFullYear() - birthDate.getFullYear();
            var monthDiff = date.getMonth() - birthDate.getMonth();

            if (monthDiff < 0 || (monthDiff === 0 && date.getDate() < birthDate.getDate())) {
                age--;
            }

            return age;
        }
        
    });