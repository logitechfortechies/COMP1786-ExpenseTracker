package com.example.expensetracker.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.expensetracker.models.Expense;
import com.example.expensetracker.models.Project;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "ExpenseTracker.db";
    private static final int DB_VERSION = 1;

    public static final String T_PROJ = "projects";
    public static final String P_ID = "id";
    public static final String P_CODE = "project_code";
    public static final String P_NAME = "project_name";
    public static final String P_DESC = "description";
    public static final String P_START = "start_date";
    public static final String P_END = "end_date";
    public static final String P_MGR = "manager";
    public static final String P_STATUS = "status";
    public static final String P_BUDGET = "budget";
    public static final String P_SPECIAL = "special_requirements";
    public static final String P_CLIENT = "client_info";
    public static final String P_ADDINFO = "additional_info";
    public static final String P_SYNCED = "synced";

    public static final String T_EXP = "expenses";
    public static final String E_ID = "id";
    public static final String E_PID = "project_id";
    public static final String E_CODE = "expense_code";
    public static final String E_DATE = "date";
    public static final String E_AMOUNT = "amount";
    public static final String E_CURR = "currency";
    public static final String E_TYPE = "type";
    public static final String E_PMTH = "payment_method";
    public static final String E_CLAIM = "claimant";
    public static final String E_PSTAT = "payment_status";
    public static final String E_DESC = "description";
    public static final String E_LOC = "location";
    public static final String E_SYNCED = "synced";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + T_PROJ + " (" +
                P_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                P_CODE + " TEXT NOT NULL, " +
                P_NAME + " TEXT NOT NULL, " +
                P_DESC + " TEXT NOT NULL, " +
                P_START + " TEXT NOT NULL, " +
                P_END + " TEXT NOT NULL, " +
                P_MGR + " TEXT NOT NULL, " +
                P_STATUS + " TEXT NOT NULL, " +
                P_BUDGET + " REAL NOT NULL, " +
                P_SPECIAL + " TEXT, " +
                P_CLIENT + " TEXT, " +
                P_ADDINFO + " TEXT, " +
                P_SYNCED + " INTEGER DEFAULT 0)");

        db.execSQL("CREATE TABLE " + T_EXP + " (" +
                E_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                E_PID + " INTEGER NOT NULL, " +
                E_CODE + " TEXT NOT NULL, " +
                E_DATE + " TEXT NOT NULL, " +
                E_AMOUNT + " REAL NOT NULL, " +
                E_CURR + " TEXT NOT NULL, " +
                E_TYPE + " TEXT NOT NULL, " +
                E_PMTH + " TEXT NOT NULL, " +
                E_CLAIM + " TEXT NOT NULL, " +
                E_PSTAT + " TEXT NOT NULL, " +
                E_DESC + " TEXT, " +
                E_LOC + " TEXT, " +
                E_SYNCED + " INTEGER DEFAULT 0, " +
                "FOREIGN KEY(" + E_PID + ") REFERENCES " + T_PROJ + "(" + P_ID + "))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        db.execSQL("DROP TABLE IF EXISTS " + T_EXP);
        db.execSQL("DROP TABLE IF EXISTS " + T_PROJ);
        onCreate(db);
    }

    public long insertProject(Project p) {
        SQLiteDatabase db = getWritableDatabase();
        long id = db.insert(T_PROJ, null, projectToCV(p));
        db.close();
        return id;
    }

    public List<Project> getAllProjects() {
        List<Project> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + T_PROJ + " ORDER BY " + P_NAME, null);
        if (c.moveToFirst()) do {
            list.add(rowToProject(c));
        } while (c.moveToNext());
        c.close();
        db.close();
        return list;
    }

    public Project getProjectById(int id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(T_PROJ, null, P_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null);
        Project p = null;
        if (c.moveToFirst()) p = rowToProject(c);
        c.close();
        db.close();
        return p;
    }

    public int updateProject(Project p) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = projectToCV(p);
        cv.put(P_SYNCED, 0);
        int rows = db.update(T_PROJ, cv, P_ID + "=?",
                new String[]{String.valueOf(p.getId())});
        db.close();
        return rows;
    }

    public void deleteProject(int id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(T_EXP, E_PID + "=?", new String[]{String.valueOf(id)});
        db.delete(T_PROJ, P_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }

    public void resetDatabase() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + T_EXP);
        db.execSQL("DELETE FROM " + T_PROJ);
        db.close();
    }

    public List<Project> searchProjects(String keyword) {
        List<Project> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String like = "%" + keyword + "%";
        Cursor c = db.rawQuery("SELECT * FROM " + T_PROJ + " WHERE " + P_NAME + " LIKE ? OR " + P_DESC + " LIKE ? OR " + P_MGR + " LIKE ?",
                new String[]{like, like, like});
        if (c.moveToFirst()) do {
            list.add(rowToProject(c));
        } while (c.moveToNext());
        c.close();
        db.close();
        return list;
    }

    public List<Project> advancedSearch(String status, String manager, String fromDate, String toDate) {
        List<Project> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        StringBuilder q = new StringBuilder("SELECT * FROM " + T_PROJ + " WHERE 1=1");
        List<String> args = new ArrayList<>();

        if (status != null && !status.isEmpty()) {
            q.append(" AND ").append(P_STATUS).append("=?");
            args.add(status);
        }
        if (manager != null && !manager.isEmpty()) {
            q.append(" AND ").append(P_MGR).append(" LIKE ?");
            args.add("%" + manager + "%");
        }
        if (fromDate != null && !fromDate.isEmpty()) {
            q.append(" AND ").append(P_START).append(">=?");
            args.add(fromDate);
        }
        if (toDate != null && !toDate.isEmpty()) {
            q.append(" AND ").append(P_END).append("<=?");
            args.add(toDate);
        }

        Cursor c = db.rawQuery(q.toString(), args.toArray(new String[0]));
        if (c.moveToFirst()) do {
            list.add(rowToProject(c));
        } while (c.moveToNext());
        c.close();
        db.close();
        return list;
    }

    public List<Project> getUnsyncedProjects() {
        List<Project> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + T_PROJ + " WHERE " + P_SYNCED + "=0", null);
        if (c.moveToFirst()) do {
            list.add(rowToProject(c));
        } while (c.moveToNext());
        c.close();
        db.close();
        return list;
    }

    public void markProjectSynced(int id) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(P_SYNCED, 1);
        db.update(T_PROJ, cv, P_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }

    public long insertExpense(Expense e) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = expenseToCV(e);
        cv.put(E_SYNCED, 0);
        long id = db.insert(T_EXP, null, cv);
        db.close();
        return id;
    }

    public List<Expense> getExpensesForProject(int projectId) {
        List<Expense> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(T_EXP, null, E_PID + "=?", new String[]{String.valueOf(projectId)},
                null, null, E_DATE + " DESC");
        if (c.moveToFirst()) do {
            list.add(rowToExpense(c));
        } while (c.moveToNext());
        c.close();
        db.close();
        return list;
    }

    public Expense getExpenseById(int id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(T_EXP, null, E_ID + "=?", new String[]{String.valueOf(id)}, null, null, null);
        Expense e = null;
        if (c.moveToFirst()) e = rowToExpense(c);
        c.close();
        db.close();
        return e;
    }

    public int updateExpense(Expense e) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = expenseToCV(e);
        cv.put(E_SYNCED, 0);
        int rows = db.update(T_EXP, cv, E_ID + "=?", new String[]{String.valueOf(e.getId())});
        db.close();
        return rows;
    }

    public void deleteExpense(int id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(T_EXP, E_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }

    public List<Expense> getUnsyncedExpenses() {
        List<Expense> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + T_EXP + " WHERE " + E_SYNCED + "=0", null);
        if (c.moveToFirst()) do {
            list.add(rowToExpense(c));
        } while (c.moveToNext());
        c.close();
        db.close();
        return list;
    }

    public void markExpenseSynced(int id) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(E_SYNCED, 1);
        db.update(T_EXP, cv, E_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }

    public double getProjectTotalExpenses(int projectId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(" + E_AMOUNT + ") FROM " + T_EXP + " WHERE " + E_PID + " = ?",
                new String[]{String.valueOf(projectId)});
        double total = 0;
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0);
        }
        cursor.close();
        return total;
    }

    private ContentValues projectToCV(Project p) {
        ContentValues cv = new ContentValues();
        cv.put(P_CODE, p.getProjectCode());
        cv.put(P_NAME, p.getProjectName());
        cv.put(P_DESC, p.getDescription());
        cv.put(P_START, p.getStartDate());
        cv.put(P_END, p.getEndDate());
        cv.put(P_MGR, p.getManager());
        cv.put(P_STATUS, p.getStatus());
        cv.put(P_BUDGET, p.getBudget());
        cv.put(P_SPECIAL, p.getSpecialRequirements());
        cv.put(P_CLIENT, p.getClientInfo());
        cv.put(P_ADDINFO, p.getAdditionalInfo());
        return cv;
    }

    private Project rowToProject(Cursor c) {
        Project p = new Project();
        p.setId(c.getInt(c.getColumnIndexOrThrow(P_ID)));
        p.setProjectCode(c.getString(c.getColumnIndexOrThrow(P_CODE)));
        p.setProjectName(c.getString(c.getColumnIndexOrThrow(P_NAME)));
        p.setDescription(c.getString(c.getColumnIndexOrThrow(P_DESC)));
        p.setStartDate(c.getString(c.getColumnIndexOrThrow(P_START)));
        p.setEndDate(c.getString(c.getColumnIndexOrThrow(P_END)));
        p.setManager(c.getString(c.getColumnIndexOrThrow(P_MGR)));
        p.setStatus(c.getString(c.getColumnIndexOrThrow(P_STATUS)));
        p.setBudget(c.getDouble(c.getColumnIndexOrThrow(P_BUDGET)));
        p.setSpecialRequirements(c.getString(c.getColumnIndexOrThrow(P_SPECIAL)));
        p.setClientInfo(c.getString(c.getColumnIndexOrThrow(P_CLIENT)));
        p.setAdditionalInfo(c.getString(c.getColumnIndexOrThrow(P_ADDINFO)));
        p.setSynced(c.getInt(c.getColumnIndexOrThrow(P_SYNCED)));
        return p;
    }

    private ContentValues expenseToCV(Expense e) {
        ContentValues cv = new ContentValues();
        cv.put(E_PID, e.getProjectId());
        cv.put(E_CODE, e.getExpenseCode());
        cv.put(E_DATE, e.getDate());
        cv.put(E_AMOUNT, e.getAmount());
        cv.put(E_CURR, e.getCurrency());
        cv.put(E_TYPE, e.getType());
        cv.put(E_PMTH, e.getPaymentMethod());
        cv.put(E_CLAIM, e.getClaimant());
        cv.put(E_PSTAT, e.getPaymentStatus());
        cv.put(E_DESC, e.getDescription());
        cv.put(E_LOC, e.getLocation());
        return cv;
    }

    private Expense rowToExpense(Cursor c) {
        Expense e = new Expense();
        e.setId(c.getInt(c.getColumnIndexOrThrow(E_ID)));
        e.setProjectId(c.getInt(c.getColumnIndexOrThrow(E_PID)));
        e.setExpenseCode(c.getString(c.getColumnIndexOrThrow(E_CODE)));
        e.setDate(c.getString(c.getColumnIndexOrThrow(E_DATE)));
        e.setAmount(c.getDouble(c.getColumnIndexOrThrow(E_AMOUNT)));
        e.setCurrency(c.getString(c.getColumnIndexOrThrow(E_CURR)));
        e.setType(c.getString(c.getColumnIndexOrThrow(E_TYPE)));
        e.setPaymentMethod(c.getString(c.getColumnIndexOrThrow(E_PMTH)));
        e.setClaimant(c.getString(c.getColumnIndexOrThrow(E_CLAIM)));
        e.setPaymentStatus(c.getString(c.getColumnIndexOrThrow(E_PSTAT)));
        e.setDescription(c.getString(c.getColumnIndexOrThrow(E_DESC)));
        e.setLocation(c.getString(c.getColumnIndexOrThrow(E_LOC)));
        e.setSynced(c.getInt(c.getColumnIndexOrThrow(E_SYNCED)));
        return e;
    }
}