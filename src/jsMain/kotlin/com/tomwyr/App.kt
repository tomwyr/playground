package com.tomwyr

import com.tomwyr.features.stream.streamView
import io.kvision.Application
import io.kvision.panel.root
import io.kvision.require

class App : Application() {
    init {
        require("css/kvapp.css")
    }

    override fun start(state: Map<String, Any>) {
        root("kvapp") {
            streamView()
        }
    }
}
