package com.github.lx200916.fishboneplugin.settings

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil

@State(name = "FishBone Settings", storages = [Storage("\$APP_CONFIG$/PasteMe-settings.xml")])
class pluginSettings:PersistentStateComponent<pluginSettings> {
    var defaultPass:String = "";
    var isCopy=false;
    var isOpenBrowser=false;
    var isFormat=false;
    override fun getState(): pluginSettings? {
        return this;
    }

    override fun loadState(state: pluginSettings) {
        XmlSerializerUtil.copyBean(state, this)

    }
    override fun noStateLoaded() {
        defaultPass = ""
        isCopy = false
        isOpenBrowser = false
        isFormat = false
    }

}