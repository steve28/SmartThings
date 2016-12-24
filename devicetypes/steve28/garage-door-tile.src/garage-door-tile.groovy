/**
 *  Copyright 2015 SmartThings
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 *  On/Off Button Tile
 *
 *  Author: SmartThings
 *
 *  Date: 2013-05-01
 */
metadata {
	definition (name: "Garage Door Tile", namespace: "Steve28", author: "Steve28") {
		capability "Switch"
        capability "Contact Sensor"
        
        command "contactOpen"
		command "contactClose"
	}

	// simulator metadata
	simulator {
	}

	// UI tile definitions
	tiles (scale: 2) {
   	 	multiAttributeTile(name:"garageDoor", type: "generic", width: 6, height: 4){
        	tileAttribute ("device.switch", key: "PRIMARY_CONTROL") {
            	attributeState "open", label:'${name}', icon:"st.contact.contact.open", backgroundColor:"#ffa81e"
            	attributeState "closed", label:'${name}', icon:"st.contact.contact.closed", backgroundColor:"#79b821"
            	attributeState "garage-open", label:'Open', icon:"st.doors.garage.garage-open", backgroundColor:"#ffa81e"
            	attributeState "garage-closed", label:'Closed', icon:"st.doors.garage.garage-closed", backgroundColor:"#79b821"
        	}
        }
//        standardTile("garageDoor", "device.switch", width: 1, height: 1, canChangeIcon: true, canChangeBackground: true) {
//			state "off", label: 'Closed', action: "switch.on", icon: "st.doors.garage.garage-closed", backgroundColor: "#79b821", nextState: "opening"
//            state "on", label: 'Open', action: "switch.off", icon: "st.doors.garage.garage-open", backgroundColor: "#ffa81e", nextState: "closing"
//            state "opening", label: "Opening", action: "switch.off", icon: "st.doors.garage.garage-opening", backgroundColor: "89C2E8"
//            state "closing", label: "Closing", action: "switch.on", icon: "st.doors.garage.garage-closing", backgroundColor: "89C2E8"
// 		}
		main "garageDoor"
		details "garageDoor"
	}
}

def parse(String description) {
	log.debug description
}

def on() {
	sendEvent(name: "switch", value: "opening")
}

def off() {
	sendEvent(name: "switch", value: "closing")
}

def contactOpen() {
	sendEvent(name: "contact", value: "open")
    sendEvent(name: "switch", value: "on")
}

def contactClose() {
	sendEvent(name: "contact", value: "closed")
    sendEvent(name: "switch", value: "off")
}