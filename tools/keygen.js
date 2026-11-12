#!/usr/bin/env node
// Söwda Hasap — lisenziýa açar generatory
//
//   node keygen.js init                      -> täze açar jübütini döredýär (bir gezek)
//   node keygen.js make ENJAM-KODY 1         -> 1 aýlyk açar (şu günden)
//   node keygen.js make ENJAM-KODY 2026-12-31 -> belli bir sene çenli
//   node keygen.js list                      -> berlen açarlar
//
// private.pem faýly GIZLIN saklanmaly! Kim alsa, açar ýasap biler.
'use strict';
const crypto = require('crypto');
const fs = require('fs');
const path = require('path');

const DIR = __dirname;
const PRIV = path.join(DIR, 'private.pem');
const LICENSES = process.env.LICENSES_FILE || path.join(DIR, '..', 'server', 'data', 'licenses.json');

function normDevice(code) {
  const c = String(code || '').toUpperCase().replace(/[^0-9A-F]/g, '');
  if (c.length !== 16) throw new Error('Enjam kody 16 simwol bolmaly (mysal: 1A2B-3C4D-5E6F-7A8B)');
  return c.match(/.{4}/g).join('-');
}

function ymd(d) {
  return d.getFullYear().toString() + String(d.getMonth() + 1).padStart(2, '0') + String(d.getDate()).padStart(2, '0');
}

function expiryFrom(arg) {
  if (/^\d{4}-\d{2}-\d{2}$/.test(arg)) return arg.replace(/-/g, '');
  const months = parseInt(arg, 10);
  if (!(months >= 1 && months <= 36)) throw new Error('Aý sany 1..36 ýa-da sene YYYY-MM-DD bolmaly');
  const d = new Date();
  d.setMonth(d.getMonth() + months);
  return ymd(d);
}

function makeKey(privPem, device, expiry) {
  const payload = `SH1|${device}|${expiry}`;
  const sig = crypto.sign('sha256', Buffer.from(payload, 'utf8'), { key: privPem, dsaEncoding: 'ieee-p1363' });
  return `${expiry}-${sig.toString('base64url')}`;
}

function verifyKey(pubKey, device, key) {
  const m = /^(\d{8})-([A-Za-z0-9_-]+)$/.exec(key);
  if (!m) return false;
  return crypto.verify('sha256', Buffer.from(`SH1|${device}|${m[1]}`), { key: pubKey, dsaEncoding: 'ieee-p1363' }, Buffer.from(m[2], 'base64url'));
}

function loadLicenses() {
  try { return JSON.parse(fs.readFileSync(LICENSES, 'utf8')); } catch { return {}; }
}

function main() {
  const [cmd, a1, a2, ...rest] = process.argv.slice(2);
  if (cmd === 'init') {
    if (fs.existsSync(PRIV) && a1 !== '--force') {
      console.log('private.pem eýýäm bar. Täzeden döretmek üçin: node keygen.js init --force');
      console.log('(Üns beriň: täze açar bilen öňki programmalar köne açarlary kabul etmez.)');
    } else {
      const { privateKey } = crypto.generateKeyPairSync('ec', { namedCurve: 'prime256v1' });
      fs.writeFileSync(PRIV, privateKey.export({ type: 'pkcs8', format: 'pem' }), { mode: 0o600 });
      console.log('private.pem döredildi.');
    }
    const pub = crypto.createPublicKey(fs.readFileSync(PRIV));
    console.log('\nŞu açyk açary LicenseManager.kt faýlyndaky PUBLIC_KEY ýerine goýuň:\n');
    console.log(pub.export({ type: 'spki', format: 'der' }).toString('base64'));
    return;
  }
  if (cmd === 'make') {
    if (!fs.existsSync(PRIV)) throw new Error('Ilki: node keygen.js init');
    const device = normDevice(a1);
    const expiry = expiryFrom(a2 || '1');
    const priv = fs.readFileSync(PRIV);
    const key = makeKey(priv, device, expiry);
    if (!verifyKey(crypto.createPublicKey(priv), device, key)) throw new Error('Açar barlagdan geçmedi!');
    const db = loadLicenses();
    db[device] = { key, expiry, note: rest.join(' ') || a2 || '', created: new Date().toISOString() };
    fs.mkdirSync(path.dirname(LICENSES), { recursive: true });
    fs.writeFileSync(LICENSES, JSON.stringify(db, null, 2));
    console.log(`Enjam:   ${device}`);
    console.log(`Möhlet:  ${expiry.slice(0, 4)}-${expiry.slice(4, 6)}-${expiry.slice(6)}`);
    console.log(`Açar:\n${key}`);
    console.log(`\n(Serwere hem ýazyldy: ${LICENSES} — programma "Onlaýn al" bilen özi alyp biler.)`);
    return;
  }
  if (cmd === 'list') {
    const db = loadLicenses();
    for (const [d, v] of Object.entries(db)) console.log(`${d}  ->  ${v.expiry}  ${v.note || ''}`);
    return;
  }
  console.log('Ulanylyşy:\n  node keygen.js init\n  node keygen.js make ENJAM-KODY AÝ_SANY|YYYY-MM-DD [bellik]\n  node keygen.js list');
}

try { main(); } catch (e) { console.error('Ýalňyşlyk: ' + e.message); process.exit(1); }

module.exports = { makeKey, verifyKey, normDevice };
