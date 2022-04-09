package com.github.lx200916.fishboneplugin.ui

import com.github.lx200916.fishboneplugin.settings.pluginSettings
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.options.SearchableConfigurable
import com.intellij.ui.TitledSeparator
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import com.intellij.util.ui.UI
import com.intellij.util.ui.UI.PanelFactory
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.border.TitledBorder

class settings : SearchableConfigurable {
    lateinit var panel: JPanel
    val isCopyC = JBCheckBox("Copy url to Clipbord")
    val isBroswerOpen = JBCheckBox("Open in broswer")
    val isFormatCode = JBCheckBox("Format code Before Upload")
    val DefaultPass = JBTextField("Default Paste Password")
    var pluginSetting: pluginSettings;

    override fun createComponent(): JComponent? {
        DefaultPass.toolTipText = ""
        val form = PanelFactory.panel(DefaultPass)
            .withComment("Set the default password when uploads to FishBone.\nWill fill into <strong>Password</strong> field.\nIf you don't want to set the password, just leave it blank.")
            .withLabel("Default Password").createPanel()
        val AfterForm =
             PanelFactory.grid().add(PanelFactory.panel(isCopyC)).add(PanelFactory.panel(isBroswerOpen)).createPanel()
        val BeforeForm = UI.PanelFactory.grid().add(PanelFactory.panel(isFormatCode)).createPanel()

        panel = PanelFactory.grid().splitColumns()
            .add(PanelFactory.panel(TitledSeparator("FishBone Settings")))
            .add(PanelFactory.panel(form))

            .add(PanelFactory.panel(TitledSeparator("Before Upload")))
            .add(PanelFactory.panel(BeforeForm))
            .add(PanelFactory.panel(TitledSeparator("After Upload")))
            .add(PanelFactory.panel(AfterForm))
            .createPanel()
//        panel.alignmentY=0.0f

        return panel


    }
    init{
        pluginSetting= ServiceManager.getService(pluginSettings::class.java)
        reset()
    }

    override fun isModified(): Boolean {
        return isCopyC.isSelected != pluginSetting.isCopy ||
                isBroswerOpen.isSelected != pluginSetting.isOpenBrowser ||
                isFormatCode.isSelected != pluginSetting.isFormat ||
                DefaultPass.text != pluginSetting.defaultPass
    }

    override fun apply() {
        pluginSetting.defaultPass= DefaultPass.text
        pluginSetting.isCopy = isCopyC.isSelected
        pluginSetting.isOpenBrowser = isBroswerOpen.isSelected
        pluginSetting.isFormat = isFormatCode.isSelected
    }
    override fun reset() {
        DefaultPass.text = pluginSetting.defaultPass
        isCopyC.isSelected = pluginSetting.isCopy
        isBroswerOpen.isSelected = pluginSetting.isOpenBrowser
        isFormatCode.isSelected = pluginSetting.isFormat
    }


    override fun getDisplayName(): String {
        return "Upload to FishBone"
    }

    override fun getId(): String {
        return "fishboneplugin.settings"
    }

}