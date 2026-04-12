const express    = require('express');
const cors       = require('cors');
const bodyParser = require('body-parser');
const Database   = require('better-sqlite3');
const path       = require('path');

const app  = express();
const PORT = process.env.PORT || 3000;

app.use(cors());
app.use(bodyParser.json());

// SQLite server-side database
const db = new Database(path.join(__dirname, 'expense_tracker.db'));
db.pragma('journal_mode = WAL');
db.pragma('foreign_keys = ON');

db.exec(`
  CREATE TABLE IF NOT EXISTS projects (
    id                   INTEGER PRIMARY KEY AUTOINCREMENT,
    project_code         TEXT NOT NULL,
    project_name         TEXT NOT NULL,
    description          TEXT NOT NULL,
    start_date           TEXT NOT NULL,
    end_date             TEXT NOT NULL,
    manager              TEXT NOT NULL,
    status               TEXT NOT NULL,
    budget               REAL NOT NULL,
    special_requirements TEXT,
    client_info          TEXT,
    additional_info      TEXT,
    created_at           DATETIME DEFAULT CURRENT_TIMESTAMP
  );

  CREATE TABLE IF NOT EXISTS expenses (
    id             INTEGER PRIMARY KEY AUTOINCREMENT,
    project_id     INTEGER NOT NULL,
    expense_code   TEXT NOT NULL,
    date           TEXT NOT NULL,
    amount         REAL NOT NULL,
    currency       TEXT NOT NULL,
    type           TEXT NOT NULL,
    payment_method TEXT NOT NULL,
    claimant       TEXT NOT NULL,
    payment_status TEXT NOT NULL,
    description    TEXT,
    location       TEXT,
    created_at     DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE
  );
`);

// ── PROJECT ROUTES ──────────────────────────────────────────

app.get('/api/projects', (req, res) => {
  try {
    res.json(db.prepare('SELECT * FROM projects ORDER BY project_name').all());
  } catch (err) { res.status(500).json({ error: err.message }); }
});

app.get('/api/projects/:id', (req, res) => {
  try {
    const p = db.prepare('SELECT * FROM projects WHERE id = ?').get(req.params.id);
    if (!p) return res.status(404).json({ error: 'Not found' });
    res.json(p);
  } catch (err) { res.status(500).json({ error: err.message }); }
});

app.post('/api/projects', (req, res) => {
  try {
    const { project_code, project_name, description, start_date, end_date,
            manager, status, budget, special_requirements,
            client_info, additional_info } = req.body;
    if (!project_code || !project_name) {
      return res.status(400).json({ error: 'Required fields missing' });
    }
    const result = db.prepare(`
      INSERT INTO projects
        (project_code, project_name, description, start_date, end_date,
         manager, status, budget, special_requirements, client_info, additional_info)
      VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
    `).run(project_code, project_name, description, start_date, end_date,
           manager, status, budget, special_requirements, client_info, additional_info);
    res.status(201).json({ id: result.lastInsertRowid, ...req.body });
  } catch (err) { res.status(500).json({ error: err.message }); }
});

app.put('/api/projects/:id', (req, res) => {
  try {
    const { project_code, project_name, description, start_date, end_date,
            manager, status, budget, special_requirements,
            client_info, additional_info } = req.body;
    db.prepare(`
      UPDATE projects SET
        project_code=?, project_name=?, description=?, start_date=?, end_date=?,
        manager=?, status=?, budget=?, special_requirements=?,
        client_info=?, additional_info=?
      WHERE id=?
    `).run(project_code, project_name, description, start_date, end_date,
           manager, status, budget, special_requirements,
           client_info, additional_info, req.params.id);
    res.json({ id: parseInt(req.params.id), ...req.body });
  } catch (err) { res.status(500).json({ error: err.message }); }
});

app.delete('/api/projects/:id', (req, res) => {
  try {
    db.prepare('DELETE FROM projects WHERE id = ?').run(req.params.id);
    res.json({ message: 'Deleted' });
  } catch (err) { res.status(500).json({ error: err.message }); }
});

app.get('/api/projects/search/:keyword', (req, res) => {
  try {
    const like = '%' + req.params.keyword + '%';
    res.json(db.prepare(
      'SELECT * FROM projects WHERE project_name LIKE ? OR description LIKE ?'
    ).all(like, like));
  } catch (err) { res.status(500).json({ error: err.message }); }
});

// ── EXPENSE ROUTES ──────────────────────────────────────────

app.get('/api/projects/:id/expenses', (req, res) => {
  try {
    res.json(db.prepare(
      'SELECT * FROM expenses WHERE project_id = ? ORDER BY date DESC'
    ).all(req.params.id));
  } catch (err) { res.status(500).json({ error: err.message }); }
});

app.post('/api/expenses', (req, res) => {
  try {
    const { project_id, expense_code, date, amount, currency, type,
            payment_method, claimant, payment_status, description, location } = req.body;
    const result = db.prepare(`
      INSERT INTO expenses
        (project_id, expense_code, date, amount, currency, type,
         payment_method, claimant, payment_status, description, location)
      VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
    `).run(project_id, expense_code, date, amount, currency, type,
           payment_method, claimant, payment_status, description, location);
    res.status(201).json({ id: result.lastInsertRowid, ...req.body });
  } catch (err) { res.status(500).json({ error: err.message }); }
});

// ── Start ───────────────────────────────────────────────────

app.listen(PORT, () => {
  console.log(`✅  Expense Tracker API running → http://localhost:${PORT}`);
  console.log(`     GET  /api/projects`);
  console.log(`     POST /api/projects`);
  console.log(`     GET  /api/projects/:id/expenses`);
  console.log(`     POST /api/expenses`);
});