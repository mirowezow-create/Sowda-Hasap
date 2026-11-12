package tm.sowdahasap.keygen

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File
import java.security.KeyFactory
import java.security.KeyPairGenerator
import java.security.PrivateKey
import java.security.PublicKey
import java.security.SecureRandom
import java.security.Signature
import java.security.spec.ECGenParameterSpec
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

/** Gizlin açar parol bilen şifrlenip saklanýar */
@Serializable
data class SealedKey(val v: Int = 1, val salt: String, val iv: String, val ct: String, val pub: String)

@Serializable
data class Issued(val device: String, val expiry: String, val note: String, val key: String, val created: Long)

class Unlocked(val priv: PrivateKey, val pub: PublicKey, val pubB64: String)

object KeyCrypto {
    private val json = Json { ignoreUnknownKeys = true }
    private val b64 = Base64.getEncoder()
    private val d64 = Base64.getDecoder()
    private const val EXPORT_PREFIX = "SHKEY1."
    private val YMD = DateTimeFormatter.ofPattern("yyyyMMdd")

    private fun aesKey(password: String, salt: ByteArray): SecretKeySpec {
        val spec = PBEKeySpec(password.toCharArray(), salt, 150_000, 256)
        return SecretKeySpec(SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).encoded, "AES")
    }

    fun newKey(password: String): Pair<SealedKey, Unlocked> {
        val g = KeyPairGenerator.getInstance("EC")
        g.initialize(ECGenParameterSpec("secp256r1"), SecureRandom())
        val kp = g.generateKeyPair()
        return seal(kp.private, kp.public, password) to Unlocked(kp.private, kp.public, b64.encodeToString(kp.public.encoded))
    }

    fun seal(priv: PrivateKey, pub: PublicKey, password: String): SealedKey {
        val salt = ByteArray(16).also { SecureRandom().nextBytes(it) }
        val iv = ByteArray(12).also { SecureRandom().nextBytes(it) }
        val c = Cipher.getInstance("AES/GCM/NoPadding")
        c.init(Cipher.ENCRYPT_MODE, aesKey(password, salt), GCMParameterSpec(128, iv))
        val ct = c.doFinal(priv.encoded)
        return SealedKey(1, b64.encodeToString(salt), b64.encodeToString(iv), b64.encodeToString(ct), b64.encodeToString(pub.encoded))
    }

    /** Parol nädogry bolsa null */
    fun unseal(s: SealedKey, password: String): Unlocked? = try {
        val c = Cipher.getInstance("AES/GCM/NoPadding")
        c.init(Cipher.DECRYPT_MODE, aesKey(password, d64.decode(s.salt)), GCMParameterSpec(128, d64.decode(s.iv)))
        val pkcs8 = c.doFinal(d64.decode(s.ct))
        val kf = KeyFactory.getInstance("EC")
        val priv = kf.generatePrivate(PKCS8EncodedKeySpec(pkcs8))
        val pub = kf.generatePublic(X509EncodedKeySpec(d64.decode(s.pub)))
        Unlocked(priv, pub, s.pub)
    } catch (e: Exception) {
        null
    }

    /** Başga enjama geçirmek üçin tekst (parol bilen şifrlenen) */
    fun exportText(s: SealedKey): String =
        EXPORT_PREFIX + Base64.getUrlEncoder().withoutPadding()
            .encodeToString(json.encodeToString(SealedKey.serializer(), s).toByteArray())

    fun importText(text: String): SealedKey? = try {
        val t = text.trim().filterNot { it.isWhitespace() }
        require(t.startsWith(EXPORT_PREFIX))
        json.decodeFromString(SealedKey.serializer(), String(Base64.getUrlDecoder().decode(t.removePrefix(EXPORT_PREFIX))))
    } catch (e: Exception) {
        null
    }

    fun normDevice(code: String): String? {
        val c = code.uppercase().filter { it in '0'..'9' || it in 'A'..'F' }
        if (c.length != 16) return null
        return c.chunked(4).joinToString("-")
    }

    /** Lisenziýa açary: "YYYYMMDD-<gol>" (Söwda Hasap programmasy şuny barlaýar) */
    fun makeLicense(k: Unlocked, device: String, expiry: LocalDate): String {
        val date = expiry.format(YMD)
        val sig = Signature.getInstance("SHA256withECDSA")
        sig.initSign(k.priv)
        sig.update("SH1|$device|$date".toByteArray(Charsets.UTF_8))
        val raw = derToP1363(sig.sign())
        val key = date + "-" + Base64.getUrlEncoder().withoutPadding().encodeToString(raw)
        check(verify(k.pub, device, key)) { "Açar barlagdan geçmedi" }
        return key
    }

    fun verify(pub: PublicKey, device: String, key: String): Boolean = try {
        val date = key.substringBefore('-')
        val raw = Base64.getUrlDecoder().decode(key.substringAfter('-'))
        val v = Signature.getInstance("SHA256withECDSA")
        v.initVerify(pub)
        v.update("SH1|$device|$date".toByteArray(Charsets.UTF_8))
        v.verify(p1363ToDer(raw))
    } catch (e: Exception) {
        false
    }

    fun derToP1363(der: ByteArray): ByteArray {
        var i = 2
        if ((der[1].toInt() and 0x80) != 0) i += der[1].toInt() and 0x7F
        fun readInt(): ByteArray {
            require(der[i] == 0x02.toByte())
            val len = der[i + 1].toInt() and 0xFF
            val v = der.copyOfRange(i + 2, i + 2 + len)
            i += 2 + len
            val trimmed = v.dropWhile { it == 0.toByte() }.toByteArray()
            return ByteArray(32 - trimmed.size) + trimmed
        }
        return readInt() + readInt()
    }

    fun p1363ToDer(sig: ByteArray): ByteArray {
        fun derInt(b: ByteArray): ByteArray {
            var i = 0
            while (i < b.size - 1 && b[i] == 0.toByte()) i++
            var v = b.copyOfRange(i, b.size)
            if ((v[0].toInt() and 0x80) != 0) v = byteArrayOf(0) + v
            return byteArrayOf(0x02, v.size.toByte()) + v
        }
        val body = derInt(sig.copyOfRange(0, 32)) + derInt(sig.copyOfRange(32, 64))
        return byteArrayOf(0x30, body.size.toByte()) + body
    }
}

/** Faýllar: açar we berlen açarlaryň taryhy */
class KeygenStore(private val dir: File) {
    private val json = Json { ignoreUnknownKeys = true; prettyPrint = true }
    private val keyFile get() = File(dir, "key.json")
    private val histFile get() = File(dir, "issued.json")

    fun loadKey(): SealedKey? = try { json.decodeFromString(SealedKey.serializer(), keyFile.readText()) } catch (e: Exception) { null }
    fun saveKey(k: SealedKey) { dir.mkdirs(); keyFile.writeText(json.encodeToString(SealedKey.serializer(), k)) }

    fun history(): List<Issued> = try {
        json.decodeFromString(kotlinx.serialization.builtins.ListSerializer(Issued.serializer()), histFile.readText())
    } catch (e: Exception) { emptyList() }

    fun addHistory(i: Issued) {
        dir.mkdirs()
        val list = listOf(i) + history()
        histFile.writeText(json.encodeToString(kotlinx.serialization.builtins.ListSerializer(Issued.serializer()), list))
    }
}
