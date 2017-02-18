/*
 *  Copyright 2017 Xiaojing Ma
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not
 *  use this file except in compliance with the License. You may obtain a copy 
 *  of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software 
 *  distributed under the License is distributed on an "AS IS" BASIS, WITHOUT 
 *  WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the 
 *  License for the specific language governing permissions and limitations 
 *  under the License.
 */
 
/**
 * This is a sample Lambda function that receives webhook from
 * Automatic and forward the message to Smartthings
 */
exports.handler = (event, context, callback) => {    
    var msg = JSON.parse(event.body);
    
    var request = require('request');
    var options = {
	  url: process.env.ss_apiuri + 'automatic',
	  headers: {
        Authorization: 'Bearer ' + process.env.ss_token
      },
	  json: {
	    deviceNetworkId: msg.vehicle.id,
        type: msg.type,
	    location: msg.location,
	    vehicle: msg.vehicle,
	    trip: msg.trip
	  }
	};
		
	request.post(options, function optionalCallback(err, httpResponse, body) {
        if (err) {
            return callback(err);
        }
	});
    
    callback(null, 'Hello from Lambda');
};
