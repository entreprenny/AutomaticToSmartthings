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
 
metadata {
	definition (name: "Automatic", namespace: "randymxj", author: "Xiaojing Ma") {
		capability "Presence Sensor"
		capability "Sensor"
        
		attribute "label", "string"
		attribute "distance", "number"
		attribute "fule", "number"
		attribute "voltage", "number"
		attribute "mpg", "number"
		attribute "location", "string"

		command "updatePresence", ["JSON_OBJECT"]
	}

	tiles {
		standardTile("presence", "device.presence", width: 2, height: 2, canChangeBackground: true) {
			state("present", labelIcon:"st.presence.tile.mobile-present", backgroundColor:"#53a7c0")
			state("not present", labelIcon:"st.presence.tile.mobile-not-present", backgroundColor:"#ebeef2")
		}
		valueTile("label", "device.label", decoration: "flat", width: 1, height: 1) {
			state("default", label:'${currentValue}')
		}
		valueTile("distance", "device.distance", decoration: "flat", width: 1, height: 1) {
			state("default", label:'${currentValue} Km Away')
		}
		valueTile("fule", "device.fule", decoration: "flat", width: 1, height: 1) {
			state("default", label:'Fule ${currentValue}%')
		}
		valueTile("voltage", "device.voltage", decoration: "flat", width: 1, height: 1) {
			state("default", label:'Voltage ${currentValue} V')
		}
		valueTile("mpg", "device.mpg", decoration: "flat", width: 1, height: 1) {
			state("default", label:'MPG ${currentValue}')
		}
		valueTile("location", "device.location", decoration: "flat", width: 2, height: 1) {
			state("default", label:'@${currentValue}')
		}
		main "presence"
		details(["presence", "label", "distance", "type", "fule", "voltage", "mpg", "location"])
	}
}

def parse(String description) {
	return null
}

def updatePresence(map) {
	// Report type
	def type = map.type
	// Vehicle
	def vehicle = map.vehicle
	// trip
	def trip = map.trip
	// Home location
	def myLat = 42.459984
	def myLon = -71.20599
	// Distance from home
	def distance = getDistance(map.location.lat, map.location.lon, myLat, myLon);

	def presence = (distance > 1 || type == "ignition:on") ? "not present" : "present"        
	def linkText = getLinkText(device)
	def isPresenceChange = isStateChange(device, "presence", presence)

	def evt_presence = [
		translatable: true,
		name: "presence",
		value: presence,
		unit: null,
		linkText: linkText,
		descriptionText: parseDescriptionText(linkText, presence),
		handlerName: (presence == "present") ? "arrived" : "left",
		isStateChange: isPresenceChange,
		displayed: displayed("Automatic", isPresenceChange)
	]

	// Presence
	sendEvent(evt_presence)

	// Distance
	sendEvent(name: "distance", value: distance, unit: "Km")

	// Label
	if (type == "ignition:on") {
		sendEvent(name: "label", value: "Driving")
	}
	else if (type == "ignition:off") {
		if (distance < 1) {
			sendEvent(name: "label", value: "At Home")
		}
		else {
			sendEvent(name: "label", value: "Parking")
		}
	}
	else if (type == "trip:finished") {
		if (distance < 1) {
			sendEvent(name: "label", value: "At Home")
		}
		else {
			sendEvent(name: "label", value: "Parked")
		}
	}

	// Status Report
	if (type == "vehicle:status_report") {
		sendEvent(name: "fule", value: vehicle.fuel_level_percent)
		sendEvent(name: "voltage", value: vehicle.battery_voltage)
	}

	// Trip
	if (trip) {
		sendEvent(name: "mpg", value: Math.round(trip.average_mpg * 100.0) / 100.0)
		sendEvent(name: "location", value: trip.end_location.display_name)
	}

	return null
}

private parseDescriptionText(String linkText, String value) {
	switch(value) {
		case "present": return "{{ linkText }} has arrived"
		case "not present": return "{{ linkText }} has left"
		default: return value
	}
}

private getDistance(lat1, lon1, lat2, lon2) {
	def R = 6371

	def latDistance = Math.toRadians(lat2 - lat1)
	def lonDistance = Math.toRadians(lon2 - lon1)
	def a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2)
	def c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
	def distance = R * c

	return Math.round(distance * 100.0) / 100.0
}
