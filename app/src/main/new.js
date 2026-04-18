const express = require('express');
const bodyParser = require('body-parser');
const cors = require('cors');

const app = express();
const PORT = 3000;

app.use(cors());
app.use(bodyParser.json());

// In-memory data storage (simulating a database)
let projects = [];
let expenses = [];

// Logger middleware
app.use((req, res, next) => {
    console.log(`${new Date().toISOString()} - ${req.method} ${req.url}`);
    next();
});

// --- Project Endpoints ---

// POST: Upload a new project
app.post('/projects', (req, res) => {
    const project = req.body;
    // Basic ID generation for demo
    if (!project.id) project.id = projects.length + 1;
    projects.push(project);
    console.log("Project Uploaded:", project.projectName);
    res.status(201).json(project);
});

// PUT: Update existing project
app.put('/projects/:id', (req, res) => {
    const id = parseInt(req.params.id);
    const index = projects.findIndex(p => p.id === id);
    if (index !== -1) {
        projects[index] = { ...projects[index], ...req.body };
        res.json(projects[index]);
    } else {
        res.status(404).json({ message: "Project not found" });
    }
});

// GET: Get all projects
app.get('/projects', (req, res) => {
    res.json(projects);
});

// --- Expense Endpoints ---

// POST: Upload a new expense
app.post('/expenses', (req, res) => {
    const expense = req.body;
    if (!expense.id) expense.id = expenses.length + 1;
    expenses.push(expense);
    console.log("Expense Uploaded:", expense.expenseCode, "for Project ID:", expense.projectId);
    res.status(201).json(expense);
});

// GET: Get expenses for a specific project
app.get('/projects/:id/expenses', (req, res) => {
    const projectId = parseInt(req.params.id);
    const filtered = expenses.filter(e => e.projectId === projectId);
    res.json(filtered);
});

// Health Check
app.get('/', (req, res) => {
    res.send('Expense Tracker Backend is Running!');
});

app.listen(PORT, '0.0.0.0', () => {
    console.log(`Server is running on http://localhost:${PORT}`);
    console.log(`For Android Emulator, use: http://10.0.2.2:${PORT}`);
});