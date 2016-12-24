/**
 *  Garage Door
 *
 *  Author: Steve Sell
 *  Date: 2014-02-02
 */

metadata {
	// Automatically generated. Make future change here.
	definition (name: "GarageDoor", author: "steve.sell@gmail.com") {
    	capability "Actuator"
        capability "Sensor"
		capability "Relative Humidity Measurement"
		capability "Polling"
		capability "Refresh"
        capability "Temperature Measurement"

		attribute "leftDoor", "string"
		attribute "rightDoor", "string"

		command "pushLeft"
		command "pushRight"
	}

	// Preferences
    
	
	// tile definitions
	tiles (scale: 2) {
		standardTile("leftDoor", "device.leftDoor", width: 3, height: 3, canChangeIcon: true, canChangeBackground: true) {
			state "closed", label: 'Closed', action: "pushLeft", icon: "st.doors.garage.garage-closed", backgroundColor: "#79b821", nextState: "opening"
            state "open", label: 'Open', action: "pushLeft", icon: "st.doors.garage.garage-open", backgroundColor: "#ffa81e", nextState: "closing"
            state "opening", label: "Opening", icon: "st.doors.garage.garage-opening", backgroundColor: "89C2E8"
            state "closing", label: "Closing", icon: "st.doors.garage.garage-closing", backgroundColor: "89C2E8"
 		}
        standardTile("rightDoor", "device.rightDoor", width: 3, height: 3, canChangeIcon: true, canChangeBackground: true) {
			state "closed", label: 'Closed', action: "pushRight", icon: "st.doors.garage.garage-closed", backgroundColor: "#79b821", nextState: "opening"
            state "open", label: 'Open', action: "pushRight", icon: "st.doors.garage.garage-open", backgroundColor: "#ffa81e", nextState: "closing"
            state "opening", label: "Opening", icon: "st.doors.garage.garage-opening", backgroundColor: "89C2E8"
            state "closing", label: "Closing", icon: "st.doors.garage.garage-closing", backgroundColor: "89C2E8"
 		}
        valueTile("temperature", "device.temperature",  width: 3, height: 3,inactiveLabel: false, decoration: "flat") {
            state "temperature", label:'${currentValue} °F', unit:"F"
        }
        valueTile("humidity", "device.humidity",  width: 3, height: 3,inactiveLabel: false, decoration: "flat") {
            state "humidity", label:'${currentValue} %RH', unit:""
        }


		main (["leftDoor", "rightDoor"])
		details(["leftDoor","rightDoor","temperature","humidity"])
	}
    
}
 
Map parse(String description) {
 	def name = null
	def value = zigbee.parse(description)?.text
	def linkText = getLinkText(device)
	def descriptionText = getDescriptionText(description, linkText, value)
	def handlerName = value
	def isStateChange = value != "ping"
	def displayed = value && isStateChange
    
    def incoming_cmd = value.split()
    
    if (incoming_cmd.size() > 1)
    {
    	name = incoming_cmd[0]
    	value = incoming_cmd[1]
    }
    else
    {
    	displayed = false
        isStateChange = false
    }
	def result = [
		value: value,
        name: name,
		handlerName: handlerName,
		linkText: linkText,
		descriptionText: descriptionText,
		isStateChange: isStateChange,
		displayed: displayed
	]
 	log.debug result
	result
}
 
def pushLeft() {
	log.debug "Sending pushLeft"
    zigbee.smartShield(text: "pushLeft").format()
}

def pushRight() {
	log.debug "Sending pushRight"
    zigbee.smartShield(text: "pushRight").format()
}

def poll() {
	log.debug "Executing poll."
    zigbee.smartShield(text: "poll").format()
}

def refresh() {
	log.debug "Executing refresh"
    zigbee.smartShield(text: "poll").format()

}
    
    
