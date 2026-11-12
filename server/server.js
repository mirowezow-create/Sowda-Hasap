#!/usr/bin/env node
// Söwda Hasap — sinhronlaşdyryş we lisenziýa serweri (goşmaça kitaphana gerek däl)
//
//   SYNC_TOKEN=gizlin_soz PORT=8080 node server.js
//
// Uç nokatlar:
//   GET  /api/health
//   POST /api/sync          (Authorization: Bearer <SYNC_TOKEN>)
//   GET  /api/license?device=XXXX-XXXX-XXXX-XXXX
'use strict';
const http = require('http');
const fs = require('fs');
const path = require('path');

const PORT = parseInt(process.env.PORT || '8080', 10);
const TOKEN = process.env.SYNC_TOKEN || '';
const DATA_DIR = process.env.DATA_DIR || path.join(__dirname, 'data');
const DB_FILE = path.join(DATA_DIR, 'db.json');
const LICENSES_FILE = process.env.LICENSES_FILE || path.join(DATA_DIR, 'licenses.json');
const TABLES = ['warehouses', 'products', 'stockMoves', 'customers', 'invoices', 'invoiceLines', 'payments', 'users', 'units', 'productUnits', 'barcodes', 'cashbooks'];

if (!TOKEN) console.warn('DUÝDURYŞ: SYNC_TOKEN berilmedi — sinhronlaşdyrma ýapyk.');
fs.mkdirSync(DATA_DIR, { recursive: true });

let db = { tables: {}, clock: 0 };
try { db = JSON.parse(fs.readFileSync(DB_FILE, 'utf8')); } catch { /* täze baza */ }
for (const t of TABLES) db.tables[t] = db.tables[t] || {};

let saveTimer = null;
function save() {
  clearTimeout(saveTimer);
  saveTimer = setTimeout(() => {
    const tmp = DB_FILE + '.tmp';
    fs.writeFileSync(tmp, JSON.stringify(db));
    fs.renameSync(tmp, DB_FILE);
  }, 200);
}

// Serwer sagady hemişe ösýär (enjamlaryň sagady gabat gelmese hem ýitgi bolmaz)
function tick() {
  db.clock = Math.max(db.clock + 1, Date.now());
  return db.clock;
}

function send(res, code, obj) {
  const body = JSON.stringify(obj);
  res.writeHead(code, { 'Content-Type': 'application/json; charset=utf-8', 'Content-Length': Buffer.byteLength(body) });
  res.end(body);
}

function readBody(req, limit = 50 * 1024 * 1024) {
  return new Promise((resolve, reject) => {
    let size = 0;
    const chunks = [];
    req.on('data', (c) => {
      size += c.length;
      if (size > limit) { reject(new Error('too large')); req.destroy(); return; }
      chunks.push(c);
    });
    req.on('end', () => resolve(Buffer.concat(chunks).toString('utf8')));
    req.on('error', reject);
  });
}

function authorized(req) {
  if (!TOKEN) return false;
  const h = req.headers['authorization'] || '';
  const a = Buffer.from(h);
  const b = Buffer.from('Bearer ' + TOKEN);
  return a.length === b.length && require('crypto').timingSafeEqual(a, b);
}

function handleSync(payload) {
  const since = Number(payload.since) || 0;
  const changes = payload.changes || {};
  let accepted = 0;
  for (const t of TABLES) {
    const rows = Array.isArray(changes[t]) ? changes[t] : [];
    const table = db.tables[t];
    for (const row of rows) {
      if (!row || typeof row.id !== 'string') continue;
      const old = table[row.id];
      // Iň soňky üýtgeşik ýeňýär (updatedAt boýunça)
      if (!old || Number(row.updatedAt) >= Number(old.row.updatedAt)) {
        table[row.id] = { row, srv: tick() };
        accepted++;
      }
    }
  }
  if (accepted) save();
  const out = {};
  for (const t of TABLES) {
    out[t] = Object.values(db.tables[t]).filter((e) => e.srv > since).map((e) => e.row);
  }
  return { now: db.clock, accepted, changes: out };
}

const server = http.createServer(async (req, res) => {
  const url = new URL(req.url, 'http://x');
  try {
    if (req.method === 'GET' && url.pathname === '/api/health') {
      return send(res, 200, { ok: true, app: 'sowdahasap', time: Date.now() });
    }
    if (req.method === 'GET' && url.pathname === '/api/license') {
      const device = String(url.searchParams.get('device') || '').toUpperCase();
      let lic = {};
      try { lic = JSON.parse(fs.readFileSync(LICENSES_FILE, 'utf8')); } catch { /* ýok */ }
      const e = lic[device];
      if (!e) return send(res, 404, { error: 'Bu enjam üçin açar ýok' });
      return send(res, 200, { key: e.key, expiry: e.expiry });
    }
    if (req.method === 'POST' && url.pathname === '/api/sync') {
      if (!authorized(req)) return send(res, 401, { error: 'Token nädogry' });
      const payload = JSON.parse(await readBody(req));
      const result = handleSync(payload);
      console.log(`${new Date().toISOString()} sync ${payload.deviceId || '?'} kabul=${result.accepted}`);
      return send(res, 200, result);
    }
    send(res, 404, { error: 'not found' });
  } catch (e) {
    console.error(e);
    send(res, 400, { error: String(e.message || e) });
  }
});

if (require.main === module) {
  server.listen(PORT, () => console.log(`Söwda Hasap serweri: http://0.0.0.0:${PORT}`));
}
module.exports = { server, handleSync };
