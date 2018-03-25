package com.newwesterndev.encrypt_keeper.Utilities

import android.content.Context
import android.database.Cursor
import android.nfc.NdefRecord
import android.nfc.tech.Ndef

import android.widget.Toast
import edu.temple.mapchat.Model.Model
import org.spongycastle.openssl.jcajce.JcaPEMWriter
import org.spongycastle.util.encoders.Base64

import java.security.*
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher
import java.io.*

class RSAEncryptUtility {
    init {
        Security.insertProviderAt(org.spongycastle.jce.provider.BouncyCastleProvider(), 1)
    }

    @Throws(NoSuchAlgorithmException::class, InvalidKeyException::class)
    fun generateKey(): KeyPair {
        val keyGen = KeyPairGenerator.getInstance(ALGORITHM)
        keyGen.initialize(1024)
        return keyGen.genKeyPair()
    }

    @Throws(NoSuchAlgorithmException::class, InvalidKeyException::class)
    fun encrypt(textToEncrypt: String, publicKey: PublicKey): ByteArray {
        val mCipherEncrypt = Cipher.getInstance(ALGORITHM)
        mCipherEncrypt.init(Cipher.ENCRYPT_MODE, publicKey)
        return mCipherEncrypt.doFinal(textToEncrypt.toByteArray())
    }

    @Throws(NoSuchAlgorithmException::class, InvalidKeyException::class)
    fun encryptPrivate(textToEncrypt: String, publicKey: PrivateKey): ByteArray {
        val mCipherEncrypt = Cipher.getInstance(ALGORITHM)
        mCipherEncrypt.init(Cipher.ENCRYPT_MODE, publicKey)
        return mCipherEncrypt.doFinal(textToEncrypt.toByteArray())
    }

    @Throws(NoSuchAlgorithmException::class, InvalidKeyException::class)
    fun decrypt(textToDecrypt: ByteArray, privateKey: PrivateKey): String {
        val mCipherDecrypt = Cipher.getInstance(ALGORITHM)
        mCipherDecrypt.init(Cipher.DECRYPT_MODE, privateKey)
        val decryptedBytes = mCipherDecrypt.doFinal(textToDecrypt)
        return String(decryptedBytes)
    }

    @Throws(NoSuchAlgorithmException::class, InvalidKeyException::class)
    fun decryptPublic(textToDecrypt: ByteArray, privateKey: PublicKey): String {
        val mCipherDecrypt = Cipher.getInstance(ALGORITHM)
        mCipherDecrypt.init(Cipher.DECRYPT_MODE, privateKey)
        val decryptedBytes = mCipherDecrypt.doFinal(textToDecrypt)
        return String(decryptedBytes)
    }

    @Throws(NoSuchAlgorithmException::class, InvalidKeyException::class)
    fun getPrivateKeyFromString(key: String): PrivateKey {
        val keyFactory = KeyFactory.getInstance(ALGORITHM)
        val privateKeySpec = PKCS8EncodedKeySpec(Base64.decode(key))
        return keyFactory.generatePrivate(privateKeySpec)
    }

    @Throws(NoSuchAlgorithmException::class, InvalidKeyException::class)
    fun getPublicKeyFromString(key: String): PublicKey {
        val keyFactory = KeyFactory.getInstance(ALGORITHM)
        val publicKeySpec = X509EncodedKeySpec(Base64.decode(key))
        return keyFactory.generatePublic(publicKeySpec)
    }

    fun showToast(message: String, context: Context) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    fun requestKeyPair(cursor: Cursor) : Model.ProviderKeys {
        val publicKeyAsString = cursor.getString(publicKey)
        val privateKeyAsString = cursor.getString(privateKey)
        val generatedKeyPair = KeyPair(getPublicKeyFromString(publicKeyAsString),
                getPrivateKeyFromString(privateKeyAsString))
        return Model.ProviderKeys(generatedKeyPair, publicKeyAsString, privateKeyAsString)
    }

    fun createPEMObject(publicKey: PublicKey) : String {
        val stringWriter = StringWriter()
        val pemWriter = JcaPEMWriter(stringWriter)
        pemWriter.writeObject(publicKey)
        pemWriter.close()
        return stringWriter.toString()
    }

    fun createNdefRecords(pemFileAsString: String, messageToSend: ByteArray): Array<NdefRecord> {
        val keysRecord = NdefRecord.createMime("text/plain", pemFileAsString.toByteArray())
        val messageRecord = NdefRecord.createMime("text/plain", messageToSend)
        return arrayOf(keysRecord, messageRecord)
    }

    fun createTransferNdefRecord(username: String, pemFileAsString: String) : Array<NdefRecord> {
        val usernameRecord = NdefRecord.createMime("text/plain", username.toByteArray())
        val keysRecord = NdefRecord.createMime("text/plain", pemFileAsString.toByteArray())
        return arrayOf(usernameRecord, keysRecord)
    }

    fun formatPemPublicKeyString(pemFileAsString: String) : String {
        var publicKeyPEM = pemFileAsString.replace("-----BEGIN PUBLIC KEY-----\n", "")
        publicKeyPEM = publicKeyPEM.replace("-----END PUBLIC KEY-----\n", "")
        return publicKeyPEM
    }

    companion object {
        private const val ALGORITHM = "RSA"
        private const val publicKey = 0
        private const val privateKey = 1
    }
}