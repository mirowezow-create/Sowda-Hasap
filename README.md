# Söwda Hasap — Android söwda programmasy

Kotlin + Jetpack Compose. Aýlyk lisenziýaly, serwer bilen sinhronlaşýan haryt, ammar, satuw we müşderi hasaby.

## Bukjalar

| Bukja | Näme |
|---|---|
| `android/` | Android programmasy (Android Studio-da açylýar) |
| `server/` | Sinhronlaşdyryş we lisenziýa serweri (Node.js, goşmaça kitaphana gerek däl) |
| `tools/keygen.js` | Lisenziýa açar generatory (diňe sizde durmaly) |

## Funksiýalar

- **Harytlar**: ady, barkod, birlik, alyş/satuw bahasy, iň az galyndy duýduryşy
- **Barkod**: telefonyň kamerasy bilen okamak (haryt gözlemek, faktura goşmak, täze haryt)
- **Ammar**: birnäçe ammar, giriş, çykyş, ammarlaryň arasynda geçiriş, inwentar düzedişi
- **Faktura**: garalama → tassyklamak, arzanladyş, bölekleýin töleg (galany bergä), ýatyrmak, PDF paýlaşmak
- **Müşderiler**: bergi, tölegler, hereketleriň sanawy, jaň etmek
- **Bergi SMS**: bergililere köpçülikleýin SMS ({ady}, {bergi}, {firma} nusgasy bilen), türkmen harplaryny latyna öwürmek (SMS arzan bolar)
- **Hasabatlar**: satuw, faktura sany, ortaça çek, arassa girdeji, tölegler, umumy bergi, günler boýunça grafik, iň köp satylanlar, az galan harytlar, CSV (Excel) eksport
- **Çek printeri**: Bluetooth termal printer (ESC/POS, 58 mm we 80 mm)
- **Ulanyjylar we rollar**: Admin (hemme zat), Satyjy (satuw, müşderi, töleg, SMS), Ammarçy (harytlar, ammar)
- **Sinhronlaşdyrma**: birnäçe telefon bir serwer arkaly şol bir maglumatlary görýär
- **Lisenziýa**: aýlyk açar, enjama baglanan, internetsiz işleýär, internet bar bolsa serwerden awtomatik alýar

## 1. APK ýygnamak

1. [Android Studio](https://developer.android.com/studio) guruň.
2. `File → Open` bilen `android` bukjasyny açyň. Ilkinji gezek Gradle zerur zatlary internetden ýükleýär (birnäçe minut).
3. `Build → Build App Bundle(s) / APK(s) → Build APK(s)`.
4. APK: `android/app/build/outputs/apk/debug/app-debug.apk`

Satuwa çykarmazdan öň öz gol açaryňyzy dörediň: `Build → Generate Signed App Bundle / APK`. Bu açary ýitirmäň, ýogsam programmany täzeläp bilmersiňiz.

## 2. Lisenziýa (iň möhüm)

Bu taslamada **synag açar jübüti** bar. Satmazdan öň hökman täzesini dörediň:

```bash
cd tools
node keygen.js init --force
```

Çykan uzyn açyk açary `android/app/src/main/java/tm/sowdahasap/license/LicenseManager.kt` faýlyndaky `PUBLIC_KEY` ýerine goýuň we APK-ny täzeden ýygnaň.

**`tools/private.pem` faýly gizlin.** Kim alsa, açar ýasap biler. Ony GitHub-a ýa-da müşderä bermäň, ätiýaçlyk nusgasyny saklaň.

### Müşderä açar bermek

1. Müşderi programmany açýar, ekranda **enjam kody** çykýar (mysal `D8F7-9735-AD90-7F2E`). Ol size ugradýar.
2. Siz:
   ```bash
   node keygen.js make D8F7-9735-AD90-7F2E 1            # 1 aý
   node keygen.js make D8F7-9735-AD90-7F2E 3            # 3 aý
   node keygen.js make D8F7-9735-AD90-7F2E 2026-12-31   # belli sene çenli
   ```
3. Çykan açary müşderä ugradýarsyňyz (Telegram, IMO...). Ol göçürip goýýar ýa-da QR edip okadýar.
   Ýa-da serwer işleýän bolsa, müşderi **"Onlaýn al"** düwmesine basýar we açar özi gelýär.

Gutarmagyna 5 gün galanda programma duýduryş görkezýär. Telefonyň senesini yzyna süýşürip aldamak tutulýar.

## 3. Serwer

Haýsydyr bir kompýuterde ýa-da VPS-de (Node.js 18+):

```bash
cd server
SYNC_TOKEN=uzyn_gizlin_soz PORT=8080 node server.js
```

Windows-da:
```bat
set SYNC_TOKEN=uzyn_gizlin_soz
node server.js
```

Programmada: **Sazlamalar → Serwer**: salgy `http://192.168.1.10:8080` (ýerli ulgam) ýa-da `https://domen.com`, token şol `SYNC_TOKEN`.

- Maglumatlar `server/data/db.json` faýlynda. Ony yzygiderli göçürip ätiýaçlandyryň.
- Internetden işletjek bolsaňyz, öňüne nginx goýup **https** ulanyň.
- `keygen.js make` açary `server/data/licenses.json`-a hem ýazýar, şonuň üçin keygen-i serweriň ýanynda işletseňiz "Onlaýn al" göni işleýär.

## 4. Ilkinji işletmek

1. Lisenziýa açaryny giriziň.
2. Administrator dörediň (ady, login, parol).
3. Sazlamalar: firmanyň ady, çek üçin salgy/telefon, printer, serwer.
4. Ulanyjylar: satyjylary we ammarçylary goşuň.
5. Başga telefonda: lisenziýa → giriş ekranynda "Serwerdäki ulanyjylary ýükle", şonda şol loginler bilen girip bolýar.

## Bellikler

- **SMS**: göni SEND_SMS rugsady bilen ugradylýar, operatoryň tarifi boýunça töleg alynýar. Google Play SMS rugsady bolan programmalary kabul etmeýär. APK-ny göni ýaýratmak üçin bu kadaly.
- **Printer**: ilki telefonyň Bluetooth sazlamalarynda printeri jübütläň, soňra Sazlamalar → "Printerleri gözle". Çekde türkmen harplary latyna öwrülýär (ä→a, ş→s...), sebäbi arzan printerleriň köpüsi olary goldamaýar.
- **Pul** teňňede saklanýar, tegeleklemek ýalňyşlygy bolmaýar.
- **Sinhronlaşdyrma**: "iň soňky üýtgeşik ýeňýär". Iki adam bir harydy bir wagtda üýtgetse, soňky saklan galýar.
- Programmanyň dili häzir diňe türkmençe. Rusça goşmak üçin tekstleri `strings.xml`-e geçirmeli.
