const sqlite3 = require("sqlite3").verbose();
const fs = require("fs");

let db;

const createTable = () => {
    db.run(
        `CREATE TABLE IF NOT EXISTS users (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            email TEXT,
            password TEXT,
            token TEXT,
            json TEXT
        )`
    );
};

const getUser = (token, cb) => {
    const sqlStr = `SELECT * FROM users WHERE token = ?`;
    db.get(sqlStr, token, cb);
};

const createUser = (email, password, json) => {
    let token =
        new Date().getTime().toString(36) + Math.random().toString(36).slice(2);
    const sqlStr = `INSERT INTO users (email, password, token, json) VALUES (?, ?, ?, ?)`;
    db.run(sqlStr, email, password, token, json);
};

const updateUser = async (token, json) => {
    const sqlStr = `UPDATE users SET json = ? WHERE token = ?`;
    await db.run(sqlStr, json, token);
};

const initDB = () => {
    if (!fs.existsSync("./socketiot.sqlite")) {
        db = new sqlite3.Database("./socketiot.sqlite", createTable);
    } else {
        db = new sqlite3.Database("./socketiot.sqlite");
    }
};

module.exports = { initDB, createUser, getUser, updateUser };
