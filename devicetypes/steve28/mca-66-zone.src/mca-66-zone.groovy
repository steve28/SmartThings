/**
 *  MCA-66 Zone
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
metadata {
	definition (name: "MCA-66 Zone", namespace: "steve28", author: "Steve Sell") {
		capability "Polling"
		capability "Refresh"
		capability "Switch"
        
        attribute "volume", "number"
        attribute "sourceName", "string"
        attribute "sourceNum", "number"
        
        command "setVol"
        command "source1"
        command "source2"
        command "source3"
        command "allOn"
        command "allOff"
        command "allInside"
        command "volUp"
        command "volDown"
	}

	simulator {
		// TODO: define status and reply messages here
	}

    tiles(scale: 2) {
        multiAttributeTile(name:"switch", type:"generic", width:6, height:4) {
            tileAttribute ("device.switch", key: "PRIMARY_CONTROL") {
                attributeState "on", label:'On', action:"switch.off", icon:"st.Electronics.electronics16", backgroundColor:"#79b821"
                attributeState "off", label:'Off', action:"switch.on", icon:"st.Electronics.electronics16", backgroundColor:"#ffffff", defaultState: true
            }
            tileAttribute ("device.sourceName", key: "SECONDARY_CONTROL") {
                attributeState "sourceName", label:'${currentValue}'
            }
            tileAttribute ("device.volume", key: "SLIDER_CONTROL") {
                attributeState "volume", action:"setVol"
            }
            tileAttribute("device.volume", key: "VALUE_CONTROL") {
                attributeState "VALUE_UP", action: "volUp"
                attributeState "VALUE_DOWN", action: "volDown"
            }
        }
        standardTile("source1", "device.source1", decoration: "flat", width: 1, height: 1) {
            state("off", label:"Source 1", action:"source1", icon:"https://raw.githubusercontent.com/redloro/smartthings/master/images/indicator-dot-gray.png", backgroundColor:"#ffffff")
            state("on", label:"Source 1", action:"source1", icon:"https://raw.githubusercontent.com/redloro/smartthings/master/images/indicator-dot-green.png", backgroundColor:"#ffffff")
        }
        standardTile("source2", "device.source2", decoration: "flat", width: 1, height: 1) {
            state("off", label:"Source 2", action:"source2", icon:"https://raw.githubusercontent.com/redloro/smartthings/master/images/indicator-dot-gray.png", backgroundColor:"#ffffff")
            state("on", label:"Source 2", action:"source2", icon:"https://raw.githubusercontent.com/redloro/smartthings/master/images/indicator-dot-green.png", backgroundColor:"#ffffff")
        }
        standardTile("source3", "device.source3", decoration: "flat", width: 1, height: 1) {
            state("off", label:"Source 3", action:"source3", icon:"https://raw.githubusercontent.com/redloro/smartthings/master/images/indicator-dot-gray.png", backgroundColor:"#ffffff")
            state("on", label:"Source 3", action:"source3", icon:"https://raw.githubusercontent.com/redloro/smartthings/master/images/indicator-dot-green.png", backgroundColor:"#ffffff")   
        }
        standardTile("allInside", "device.source3", decoration: "flat", width: 1, height: 1) {
            state("default", label:"All Inside", action:"allInside", icon:"https://raw.githubusercontent.com/redloro/smartthings/master/images/indicator-dot-power.png", backgroundColor:"#ffffff")
        }
        standardTile("allOn", "device.source3", decoration: "flat", width: 1, height: 1) {
            state("default", label:"All On", action:"allOn", icon:"https://raw.githubusercontent.com/redloro/smartthings/master/images/indicator-dot-power.png", backgroundColor:"#ffffff")
        }
        standardTile("allOff", "device.source3", decoration: "flat", width: 1, height: 1) {
            state("default", label:"All Off", action:"allOff", icon:"https://raw.githubusercontent.com/redloro/smartthings/master/images/indicator-dot-power.png", backgroundColor:"#ffffff")
        }

    }
}

// parse events into attributes
def parse(String description) {
	log.debug "Parsing '${description}'"
	// Nothing to do here, my parent handles this.

}

// handle commands
def poll() {
	log.debug "Executing 'poll'"
	// TODO: handle 'poll' command
}

def refresh() {
	def cmd = "/mca66?command=status&zone=${device.name.split("_")[2]}"
    log.debug "Refresh: "+cmd
    parent.sendCommand(cmd)
}

def on() {
    def cmd = "/mca66?command=pwr&zone=${device.name.split("_")[2]}&value=1"
    log.debug "On: "+cmd
    parent.sendCommand(cmd)
}

def off() {
    def cmd = "/mca66?command=pwr&zone=${device.name.split("_")[2]}&value=0"
    log.debug "Off: "+cmd
    parent.sendCommand(cmd)
}

def source1() { source_switch(1)}
def source2() { source_switch(2)}
def source3() { source_switch(3)}
def source_switch(new_source) {
    def cmd = "/mca66?command=setinput&zone=${device.name.split("_")[2]}&value=${new_source}"
    log.debug "Source: "+cmd
    parent.sendCommand(cmd)
}

def volUp() {
    def cmd = "/mca66?command=volup&zone=${device.name.split("_")[2]}"
    log.debug "VolUp: "+ cmd
    parent.sendCommand(cmd)
}

def volDown() {
    def cmd = "/mca66?command=voldwn&zone=${device.name.split("_")[2]}"
    log.debug "VolDown: "+cmd
    parent.sendCommand(cmd)
}

def setVol(value) {
	def vol = Math.round(value*0.60)
	def cmd = "/mca66?command=setvol&zone=${device.name.split("_")[2]}&value=${vol}"
    log.debug "SetVol: " + cmd
    parent.sendCommand(cmd)
}

def allInside() {
	log.debug "parent.followMe(${state.volume}, ${state.source}, false)"
    parent.followMe(state.volume, state.source, false)
}

def allOn() {
    log.debug "parent.followMe(${state.volume}, ${state.source}, true)"
    parent.followMe(state.volume, state.source, true)
}

def allOff() {
	def cmd = "/mca66?command=allOff"
    log.debug "Off: "+cmd
    parent.sendCommand(cmd)
}

	
def updateZone(state_msg) {
	log.debug "Status: ${state_msg}"
    
    // Extract the zone info (received from Parent)
    state.zone_name = state_msg['name']
    state.power_state = state_msg['power']
    state.volume = state_msg['vol'].toInteger()
    state.source = state_msg['input']
    state.source_name = state_msg['input_name']
	
    // Update the power state
    sendEvent(name: "switch", value: state.power_state)
    
    // Update the volume slider
    sendEvent(name: "volume", value: Math.round(state.volume/0.60))
    
    // highlight the correct source and update source display
    sendEvent(name: "sourceNum", value: state.source)
    for (def i = 1; i < 4; i++) {
        if (i == state.source) {
            sendEvent(name: "source${i}", value: "on")
            sendEvent(name: "sourceName", value: "Source ${i}: ${state.source_name}")
        }
        else {
            sendEvent(name: "source${i}", value: "off")
        }
    }
}

