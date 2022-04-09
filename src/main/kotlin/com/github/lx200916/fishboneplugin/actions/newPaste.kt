package com.github.lx200916.fishboneplugin.actions

import PasteDialog
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import PrivatePasteDialog
import PublicPasteDialog
import com.github.lx200916.fishboneplugin.API.API
import com.github.lx200916.fishboneplugin.API.HOST
import com.github.lx200916.fishboneplugin.API.generateNonce
import com.github.lx200916.fishboneplugin.settings.pluginSettings
import com.intellij.diff.tools.util.DiffNotifications.createNotification
import com.intellij.ide.BrowserUtil
import com.intellij.notification.*
import com.intellij.notification.impl.NotificationGroupEP
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.ide.CopyPasteManager
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.util.NlsSafe
import com.intellij.openapi.util.ThrowableComputable
import com.intellij.openapi.util.text.StringUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.codeStyle.CodeStyleManager
import java.awt.datatransfer.StringSelection
import java.io.IOException
public enum class PasteLifeTime{
    Once,
    OneDay,
    SevenDays,

}
fun getPasteLifeTime(index:Int):PasteLifeTime{
    return when(index){
        0 -> PasteLifeTime.Once
        1 -> PasteLifeTime.OneDay
        2 -> PasteLifeTime.SevenDays
        else -> PasteLifeTime.Once
    }
}
const val PREFIX="https://bone.saltedfish.fun/"
private fun getText(anActionEvent: AnActionEvent, file: VirtualFile): String {
    val editor = anActionEvent.getData(CommonDataKeys.EDITOR)

    val content = ReadAction.compute<String, RuntimeException> {
        try {
            return@compute FileDocumentManager.getInstance().getDocument(file)?.text
                ?: String(file.contentsToByteArray(), file.charset)
        } catch (ioException: IOException) {
            ioException.printStackTrace()
            return@compute null
        }
    }
//    println("file\n$content")
//    println("select\n" + editor?.selectionModel?.selectedText)
//    println("document\n" + editor?.document?.text)

    return (editor?.selectionModel?.selectedText ?: content).orEmpty()
}
fun newPaste(e:AnActionEvent, private:Boolean){

    val pluginSettings:pluginSettings  = ServiceManager.getService(pluginSettings::class.java)
    val file = e.getData(CommonDataKeys.VIRTUAL_FILE)
    val files = e.getData(CommonDataKeys.VIRTUAL_FILE_ARRAY)
    val editor = e.getData(CommonDataKeys.EDITOR)
    if (editor == null && file == null && files == null) {
        println("???No Content")

        return
    }
    file?.let {
        if (file.isDirectory or (files!!.size != 1)) {
            Messages.showMessageDialog("Sorry, Only Single Text File is Supported.", "Error", Messages.getErrorIcon())
            return
        }
        if (file.fileType.isBinary) {
            Messages.showMessageDialog("Sorry, Only Single Text File is Supported.", "Error", Messages.getErrorIcon())
            return
        }
        if (file.length >= 102400 || file.length == 0L) {
            Messages.showMessageDialog("Sorry, Only Single Text File (<100KB) is Supported.", "Error", Messages.getErrorIcon())
            return
        }
        val type = file.fileType.defaultExtension
        val psi=e.getData(CommonDataKeys.PSI_FILE)!!
        val lang: String = psi.language.displayName

        if (pluginSettings.isFormat){
            WriteCommandAction.runWriteCommandAction(e.project!!) {
                val codeStyleManager: CodeStyleManager = CodeStyleManager.getInstance(e.project!!)
                println(e.project!!)
                try {
                    codeStyleManager.reformat(psi).text

                }
                catch (e:Exception){
                    e.printStackTrace()
                }
//
            }


        }
        val content = getText(e, file)
//            println(content)
        if (StringUtil.isEmptyOrSpaces(content)) {
            Messages.showMessageDialog("Sorry, No Content.", "Error", Messages.getErrorIcon())
            return
        }
        createPaste(e, private, content, lang,pluginSettings.defaultPass)
        return
    }
    editor?.let {
        var text = ReadAction.compute(ThrowableComputable { editor.selectionModel.selectedText })
        if (text == null) {
            text = editor.document.text
        }

        if (StringUtil.isEmptyOrSpaces(text)) {
            return
        }
        createPaste(e, private, text, "Plain",pluginSettings.defaultPass)
        return
    }

    }
private fun copyToClip(id: String){
    val copyPasteManager = CopyPasteManager.getInstance()
    val stringSelection = StringSelection(id)
    copyPasteManager.setContents(stringSelection)
}
private fun Browseropen(id: String){
    BrowserUtil.browse("$PREFIX$id")
}
private fun createNoti(id: String, e: AnActionEvent, deleteToken:String){
    val settings:pluginSettings  = ServiceManager.getService(pluginSettings::class.java)
    val project = e.project
    if (settings.isCopy){
        copyToClip("$PREFIX$id")
    }
    if (settings.isOpenBrowser){
        Browseropen(id)
    }


    println("ID:$id")
    run {
        val notification=NotificationGroupManager.getInstance().getNotificationGroup("com.github.lx200916.fishboneplugin.notificationGroup")
            .createNotification("Paste Created", "PasteID is $id ${ if (deleteToken.isNotEmpty()) "Delete Token is $deleteToken" else ""} \n  <a href= '$PREFIX$id'>$PREFIX$id</a>", NotificationType.INFORMATION, NotificationListener.URL_OPENING_LISTENER);

        notification.addAction(object : NotificationAction("Copy URL To ClipBroad") {
            override fun actionPerformed(e: AnActionEvent, notification: Notification) {
                copyToClip(id)
            }

        })
        if (deleteToken.isNotEmpty()){
            notification.addAction(object : NotificationAction("Copy DeleteToken To ClipBroad") {
                override fun actionPerformed(e: AnActionEvent, notification: Notification) {
                    copyToClip(deleteToken)
                }

            })
        }

        notification.addAction(object : NotificationAction("Open in Browser") {
            override fun actionPerformed(e: AnActionEvent, notification: Notification) {
                Browseropen(id)
            }

        })
        notification.notify(project)

    }
}

fun createPaste(e: AnActionEvent, private: Boolean, text:  String, s: String,password:String="") {
    val dialog:PasteDialog;
    if (private) dialog=PrivatePasteDialog(s,text,password)
    else dialog=PublicPasteDialog(s,text)
    if (dialog.showAndGet()){
        var deleteToken:String=""
        if (dialog.hasPasteToken()){

         deleteToken=generateNonce(6)}

        val res=API.createPaste(dialog.pasteContent,dialog.pastePass,dialog.pasteLang,dialog.pasteType,deleteToken)
        println(res)
        if (res.status!=201){
            Messages.showMessageDialog("Sorry, Failed to Create Paste.\n${res.message}", "Error", Messages.getErrorIcon())
            return
        }else{
            createNoti(res.id,e,deleteToken)

        }






    }

//    TODO("Not yet implemented")
}


class newPublicPaste: AnAction() {
    override fun actionPerformed(e: AnActionEvent) {


        newPaste(e,false);
    }


}
class newPrivatePaste:AnAction(){
    override fun actionPerformed(e: AnActionEvent) {

        newPaste(e,true);
    }

}
