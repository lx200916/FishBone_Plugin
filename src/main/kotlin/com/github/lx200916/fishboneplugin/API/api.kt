package com.github.lx200916.fishboneplugin.API

import com.github.lx200916.fishboneplugin.API.pb.Message.Paste
import com.github.lx200916.fishboneplugin.actions.PasteLifeTime
import com.github.lx200916.fishboneplugin.API.pb.newPaste
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
private const val keySize = 256
private const val ivSize = 128
private val salt ="fishbone".toByteArray()

const val HOST="https://bone.saltedfish.fun/_api/paste"
@Throws(NoSuchAlgorithmException::class)
private fun EvpKDF(
    password: ByteArray,
    salt: ByteArray,
    resultKey: ByteArray,
    resultIv: ByteArray
): ByteArray {
    return EvpKDF(password, salt, 1, "MD5", resultKey, resultIv)
}

@Throws(NoSuchAlgorithmException::class)
private fun EvpKDF(
    password: ByteArray,
    salt: ByteArray,
    iterations: Int,
    hashAlgorithm: String,
    resultKey: ByteArray,
    resultIv: ByteArray
): ByteArray {
    var keySize = keySize/32
    var ivSize = ivSize/32
    val targetKeySize = keySize + ivSize
    val derivedBytes = ByteArray(targetKeySize * 4)
    var numberOfDerivedWords = 0
    var block: ByteArray? = null
    val hasher = MessageDigest.getInstance(hashAlgorithm)
    while (numberOfDerivedWords < targetKeySize) {
        if (block != null) {
            hasher.update(block)
        }
        hasher.update(password)
        block = hasher.digest(salt)
        hasher.reset()

        // Iterations
        for (i in 1 until iterations) {
            block = hasher.digest(block)
            hasher.reset()
        }
        System.arraycopy(
            block, 0, derivedBytes, numberOfDerivedWords * 4,
            Math.min(block!!.size, (targetKeySize - numberOfDerivedWords) * 4)
        )
        numberOfDerivedWords += block.size / 4
    }
    System.arraycopy(derivedBytes, 0, resultKey, 0, keySize * 4)
    System.arraycopy(derivedBytes, keySize * 4, resultIv, 0, ivSize * 4)
    return derivedBytes // key + iv
}
fun generateNonce(size:Int): String{
    val nonceScope = "1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
    val scopeSize = nonceScope.length
    val nonceItem: (Int) -> Char = { nonceScope[(scopeSize * Math.random()).toInt()] }
    return Array(size, nonceItem).joinToString("")
}

class API {

    companion object {
        val client:OkHttpClient by lazy { OkHttpClient().newBuilder().build() }

     fun  encrypt(content: String,pass: String) : String{
        if (pass.isEmpty()) return content
        val key = ByteArray(keySize / 8)
        val iv = ByteArray(ivSize / 8)
        EvpKDF(pass.toByteArray(),salt, key, iv);
        val cipher: Cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, SecretKeySpec(key, "AES"),)
        val cipherBytes = cipher.doFinal("##PasteMe##$content".toByteArray())
        return  Base64.getEncoder().encodeToString(cipherBytes);

    }
    fun decrypt(content: String,pass: String) : String{
        val key = ByteArray(keySize / 8)
        val iv = ByteArray(ivSize / 8)
        EvpKDF(pass.toByteArray(),salt, key, iv);
        val cipher: Cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        cipher.init(Cipher.DECRYPT_MODE, SecretKeySpec(key, "AES"))
        val cipherBytes = cipher.doFinal(Base64.getDecoder().decode(content))
        return  String(cipherBytes);

    }
    fun getToken():String{
        val req=Request.Builder().url("$HOST/?method=beat").header("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.149 Safari/537.36").build()
        client.newCall(req).execute().use {
            return Base64.getEncoder().encodeToString(Paste.parseFrom(Base64.getDecoder().decode(it.body!!.string())).tokenEncryption.toByteArray()).replace("=","!")
        }
    }
    fun createPaste(content:String,pass:String,lang:String,type:PasteLifeTime,DeleteToken:String=""):Paste{
        val paste = newPaste{
            this.content=encrypt(content,pass)
            password=pass.isNotEmpty()
            this.lang=lang
            timeline=type.ordinal
            xtoken=getToken()
            if (DeleteToken.isNotEmpty()) {
                this.token=DeleteToken
                this.tokenEncryption=encrypt(DeleteToken,pass)
            }
        }

        val url=if (type==PasteLifeTime.Once) "$HOST/once" else "$HOST/"


        val req=Request.Builder().url(url).header("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.149 Safari/537.36").post(Base64.getEncoder().encode(paste.toByteArray()).toRequestBody()).build()
        client.newCall(req).execute().use {
            val res= Paste.parseFrom(Base64.getDecoder().decode(it.body!!.string()))
//            res.tokenEncryption=paste.token
            return res
        }






    }}
}