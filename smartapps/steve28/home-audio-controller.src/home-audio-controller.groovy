/**
 *  Home Audio Controller
 *
 *  Copyright 2016 Steve Sell
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
 */

import groovy.json.JsonSlurper

definition(
    name: "Home Audio Controller",
    namespace: "steve28",
    author: "Steve Sell",
    description: "HDT MCA-66 Controller SmartApp",
    category: "My Apps",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")
	singleInstance: true

preferences {
  section("SmartThings Hub") {
    input "hostHub", "hub", title: "Select Hub", multiple: false, required: true
  }
  section("MCA-66 Controller") {
    input "ip_address", "text", title: "Proxy Address", description: "(ie. 192.168.1.10)", required: true, defaultValue: "192.168.1.144"
    input "port", "text", title: "Proxy Port", description: "(ie. 8080)", required: true, defaultValue: "8080"
    //input "zoneName1", "text", title: "Proxy Port", description: "Zone Name", required: true, defaultValue: "Zone 1"
    //input "zoneName2", "text", title: "Proxy Port", description: "Zone Name", required: true, defaultValue: "Zone 2"
    //input "zoneName3", "text", title: "Proxy Port", description: "Zone Name", required: true, defaultValue: "Zone 3"
    //input "zoneName4", "text", title: "Proxy Port", description: "Zone Name", required: true, defaultValue: "Zone 4"
    //input "zoneName5", "text", title: "Proxy Port", description: "Zone Name", required: true, defaultValue: "Zone 5"
    //input "zoneName6", "text", title: "Proxy Port", description: "Zone Name", required: true, defaultValue: "Zone 6"

  }
}

def installed() {
	log.debug "Installed with settings: ${settings}"
	subscribeToEvents()
    addZones()
    log.debug "before runin"
    runIn(5, refreshZones)
    log.debug "after runin"
}

def uninstalled() {
	log.debug "Uninstalling"
	deleteZones()
}
def updated() {
	log.debug "Running Updated"
	//deleteZones()
    //runIn(2, addZones)
    //runIn(5, refreshZones)
}

def subscribeToEvents() {
  	subscribe(location, null, lanResponseHandler, [filterEvents:false])
}

def lanResponseHandler(evt) {
	log.debug "Received LAN message"
    def map = parseLanMessage(evt.stringValue)
	log.trace "headers: ${map.headers}"
    log.trace "body: ${map.body}"
    def body_json= new JsonSlurper().parseText(map.body)
    //log.trace body_json["1"]
    
    // Send the zone status messages out to each zone
    for (def i=1;i<7;i++) {
    	def zonedevice = getChildDevice("mca66_zone_${i}")
        if (zonedevice) {
        	log.debug body_json["${i}"]
            zonedevice.updateZone(body_json["${i}"])
        }
    }
    return
}

private sendCommand(path) {
	log.debug "Sending Command: ${path}"
    if (settings.ip_address.length() == 0 ||
        settings.port.length() == 0) {
        log.error "SmartThings Node Proxy configuration not set!"
        return
    }

    def host = settings.ip_address + ":" + settings.port
    def headers = [:]
    headers.put("HOST", host)

    def hubAction = new physicalgraph.device.HubAction(
        method: "GET",
        path: path,
        headers: headers
    )
    sendHubCommand(hubAction)
}

def refreshZones() {
	log.debug "Refreshing Zones"
	sendCommand("/mca66?command=status&zone=1")
}

def followMe(vol, src, all) {
	def endloop=0
	if (all==true) {
    	endloop = 7
    } else {
    	endloop = 6
    }
   	log.trace "Executing Follow Me. vol:${vol} src:${src} endloop:${endloop}"

	for (def i=1;i<endloop;i++) {
    	log.trace "Looping follow me..."
    	sendCommand("/mca66?command=pwr&zone=${i}&value=1")
    	sendCommand("/mca66?command=setinput&zone=${i}&value=${src}")
        sendCommand("/mca66?command=setvol&zone=${i}&value=${vol}")
    }
}
def initialize() {
	// TODO: subscribe to attributes, devices, locations, etc.
}

private addZones() {
    def device_labels = ['FamRoom Speakers','Kitchen Speakers','LivingRoom Speakers','Office Speakers',
            		     'Bedroom Speakers', 'Pattio Speakers']
    log.debug "Adding Children"
    for (def i=1; i<7; i++) {
    	log.debug "Adding mca66_zone_${i} ${device_labels[i-1]}"
        addChildDevice("steve28", "MCA-66 Zone", "mca66_zone_${i}", hostHub.id, 
                       ["name":"mca66_zone_${i}", label:device_labels[i-1]])
    }
}

private deleteZones() {
	log.debug "Deleting Children"
	getAllChildDevices().each { deleteChildDevice(it.deviceNetworkId) }
}
