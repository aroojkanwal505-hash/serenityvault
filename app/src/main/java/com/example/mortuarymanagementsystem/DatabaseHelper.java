package com.example.mortuarymanagementsystem;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "MortuaryDB";
    private static final int DATABASE_VERSION = 2; // Version update karo

    // Tables
    public static final String TABLE_CASES = "cases";
    public static final String TABLE_USERS = "users";
    public static final String TABLE_SLOTS = "slots";
    public static final String TABLE_NOTIFICATIONS = "notifications";
    public static final String TABLE_DOCTOR_REPORTS = "doctor_reports";

    // Common Columns
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_CREATED_AT = "created_at";

    // Cases Table Columns
    public static final String COLUMN_CASE_ID = "case_id";
    public static final String COLUMN_LOCATION = "location";
    public static final String COLUMN_TIME_FOUND = "time_found";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_STATUS = "status";
    public static final String COLUMN_CURRENT_STAGE = "current_stage";

    // Users Table Columns
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_ROLE = "role";

    // SLOTS Table Columns
    public static final String COLUMN_SLOT_ID = "slot_id";
    public static final String COLUMN_SLOT_NUMBER = "slot_number";
    public static final String COLUMN_SLOT_STATUS = "slot_status"; // Available, Reserved, Occupied
    public static final String COLUMN_RESERVED_FOR_CASE = "reserved_for_case";
    public static final String COLUMN_RESERVED_UNTIL = "reserved_until";

    // NOTIFICATIONS Table Columns
    public static final String COLUMN_NOTIF_ID = "notif_id";
    public static final String COLUMN_NOTIF_TITLE = "notif_title";
    public static final String COLUMN_NOTIF_MESSAGE = "notif_message";
    public static final String COLUMN_NOTIF_FOR_ROLE = "notif_for_role";
    public static final String COLUMN_NOTIF_STATUS = "notif_status"; // Unread, Read, Cleared
    public static final String COLUMN_NOTIF_TYPE = "notif_type"; // NewCase, CaseAccepted, BodyReceived, DoctorReport

    // DOCTOR REPORTS Table Columns
    public static final String COLUMN_REPORT_ID = "report_id";
    public static final String COLUMN_TIME_OF_DEATH = "time_of_death";
    public static final String COLUMN_CAUSE_OF_DEATH = "cause_of_death";
    public static final String COLUMN_MARKS_INJURIES = "marks_injuries";
    public static final String COLUMN_MEDICAL_HISTORY = "medical_history";
    public static final String COLUMN_DOCTOR_NOTES = "doctor_notes";
    public static final String COLUMN_REPORT_STATUS = "report_status"; // Pending, Completed

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create cases table
        String CREATE_CASES_TABLE = "CREATE TABLE " + TABLE_CASES + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_CASE_ID + " TEXT UNIQUE,"
                + COLUMN_LOCATION + " TEXT,"
                + COLUMN_TIME_FOUND + " TEXT,"
                + COLUMN_DESCRIPTION + " TEXT,"
                + COLUMN_STATUS + " TEXT DEFAULT 'Reported',"
                + COLUMN_CURRENT_STAGE + " TEXT DEFAULT 'Initial Report',"
                + COLUMN_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP"
                + ")";
        db.execSQL(CREATE_CASES_TABLE);

        // Create users table
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_USERNAME + " TEXT UNIQUE,"
                + COLUMN_PASSWORD + " TEXT,"
                + COLUMN_ROLE + " TEXT,"
                + COLUMN_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP"
                + ")";
        db.execSQL(CREATE_USERS_TABLE);

        // Create slots table
        String CREATE_SLOTS_TABLE = "CREATE TABLE " + TABLE_SLOTS + "("
                + COLUMN_SLOT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_SLOT_NUMBER + " TEXT UNIQUE,"
                + COLUMN_SLOT_STATUS + " TEXT DEFAULT 'Available',"
                + COLUMN_RESERVED_FOR_CASE + " TEXT,"
                + COLUMN_RESERVED_UNTIL + " TEXT,"
                + COLUMN_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP"
                + ")";
        db.execSQL(CREATE_SLOTS_TABLE);

        // Create notifications table
        String CREATE_NOTIF_TABLE = "CREATE TABLE " + TABLE_NOTIFICATIONS + "("
                + COLUMN_NOTIF_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_NOTIF_TITLE + " TEXT,"
                + COLUMN_NOTIF_MESSAGE + " TEXT,"
                + COLUMN_NOTIF_FOR_ROLE + " TEXT,"
                + COLUMN_NOTIF_STATUS + " TEXT DEFAULT 'Unread',"
                + COLUMN_NOTIF_TYPE + " TEXT,"
                + COLUMN_CASE_ID + " TEXT,"
                + COLUMN_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP"
                + ")";
        db.execSQL(CREATE_NOTIF_TABLE);

        // Create doctor reports table
        String CREATE_DOCTOR_REPORTS_TABLE = "CREATE TABLE " + TABLE_DOCTOR_REPORTS + "("
                + COLUMN_REPORT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_CASE_ID + " TEXT UNIQUE,"
                + COLUMN_TIME_OF_DEATH + " TEXT,"
                + COLUMN_CAUSE_OF_DEATH + " TEXT,"
                + COLUMN_MARKS_INJURIES + " TEXT,"
                + COLUMN_MEDICAL_HISTORY + " TEXT,"
                + COLUMN_DOCTOR_NOTES + " TEXT,"
                + COLUMN_REPORT_STATUS + " TEXT DEFAULT 'Pending',"
                + COLUMN_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP"
                + ")";
        db.execSQL(CREATE_DOCTOR_REPORTS_TABLE);

        // Insert sample users
        insertSampleUsers(db);

        // Insert sample slots (Serenity Vault slots)
        insertSampleSlots(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop all tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CASES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SLOTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTIFICATIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DOCTOR_REPORTS);

        // Recreate tables
        onCreate(db);
    }

    private void insertSampleUsers(SQLiteDatabase db) {
        // Police Officer
        insertUser(db, "police", "123", "Police Officer");
        // Mortuary Staff
        insertUser(db, "mortuary", "123", "Mortuary Staff");
        // Doctor
        insertUser(db, "doctor", "123", "Doctor");
        // Admin
        insertUser(db, "admin", "123", "Admin");
    }

    private void insertUser(SQLiteDatabase db, String username, String password, String role) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PASSWORD, password);
        values.put(COLUMN_ROLE, role);
        db.insert(TABLE_USERS, null, values);
    }

    private void insertSampleSlots(SQLiteDatabase db) {
        // Create 20 slots for mortuary
        for (int i = 1; i <= 20; i++) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_SLOT_NUMBER, "SV-" + String.format("%02d", i));
            values.put(COLUMN_SLOT_STATUS, "Available");
            db.insert(TABLE_SLOTS, null, values);
        }
    }

    // User authentication method
    public boolean authenticateUser(String username, String password, String role) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_ID};
        String selection = COLUMN_USERNAME + " = ? AND " + COLUMN_PASSWORD + " = ? AND " + COLUMN_ROLE + " = ?";
        String[] selectionArgs = {username, password, role};

        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);
        int count = cursor.getCount();
        cursor.close();

        return count > 0;
    }

    // Add new case method
    public boolean addNewCase(String caseId, String location, String time, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CASE_ID, caseId);
        values.put(COLUMN_LOCATION, location);
        values.put(COLUMN_TIME_FOUND, time);
        values.put(COLUMN_DESCRIPTION, description);

        long result = db.insert(TABLE_CASES, null, values);
        return result != -1;
    }

    // Get case by ID method
    public Cursor getCaseById(String caseId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_CASE_ID + " = ?";
        String[] selectionArgs = {caseId};
        return db.query(TABLE_CASES, null, selection, selectionArgs, null, null, null);
    }

    // Update case status method
    public boolean updateCaseStatus(String caseId, String newStatus, String newStage) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_STATUS, newStatus);
        values.put(COLUMN_CURRENT_STAGE, newStage);

        String whereClause = COLUMN_CASE_ID + " = ?";
        String[] whereArgs = {caseId};

        int result = db.update(TABLE_CASES, values, whereClause, whereArgs);
        return result > 0;
    }

    // Update autopsy findings method
    public boolean updateAutopsyFindings(String caseId, String findings) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DESCRIPTION, findings);
        values.put(COLUMN_STATUS, "Autopsy Completed");
        values.put(COLUMN_CURRENT_STAGE, "Medical Examination Done");

        String whereClause = COLUMN_CASE_ID + " = ?";
        String[] whereArgs = {caseId};

        int result = db.update(TABLE_CASES, values, whereClause, whereArgs);
        return result > 0;
    }

    // Get all cases method
    public Cursor getAllCases() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_CASES, null, null, null, null, null, COLUMN_CREATED_AT + " DESC");
    }

    public Cursor getAllCasesWithStatus() {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {
                COLUMN_CASE_ID,
                COLUMN_STATUS,
                COLUMN_LOCATION,
                COLUMN_TIME_FOUND,
                COLUMN_CURRENT_STAGE
        };
        return db.query(TABLE_CASES, columns, null, null, null, null, COLUMN_CREATED_AT + " DESC");
    }

    // ✅ NEW METHOD: Create Notification
    public boolean createNotification(String title, String message, String forRole, String type, String caseId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NOTIF_TITLE, title);
        values.put(COLUMN_NOTIF_MESSAGE, message);
        values.put(COLUMN_NOTIF_FOR_ROLE, forRole);
        values.put(COLUMN_NOTIF_TYPE, type);
        values.put(COLUMN_CASE_ID, caseId);

        long result = db.insert(TABLE_NOTIFICATIONS, null, values);
        return result != -1;
    }

    // ✅ NEW METHOD: Get Notifications for Role
    public Cursor getNotificationsForRole(String role) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_NOTIF_FOR_ROLE + " = ? AND " + COLUMN_NOTIF_STATUS + " != 'Cleared'";
        String[] selectionArgs = {role};
        return db.query(TABLE_NOTIFICATIONS, null, selection, selectionArgs,
                null, null, COLUMN_CREATED_AT + " DESC");
    }

    // ✅ NEW METHOD: Mark Notification as Read
    public boolean markNotificationAsRead(String notifId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NOTIF_STATUS, "Read");

        String whereClause = COLUMN_NOTIF_ID + " = ?";
        String[] whereArgs = {notifId};

        int result = db.update(TABLE_NOTIFICATIONS, values, whereClause, whereArgs);
        return result > 0;
    }

    // ✅ NEW METHOD: Clear Notification
    public boolean clearNotification(String notifId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NOTIF_STATUS, "Cleared");

        String whereClause = COLUMN_NOTIF_ID + " = ?";
        String[] whereArgs = {notifId};

        int result = db.update(TABLE_NOTIFICATIONS, values, whereClause, whereArgs);
        return result > 0;
    }

    // ✅ NEW METHOD: Get Available Slots
    public Cursor getAvailableSlots() {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_SLOT_STATUS + " = 'Available'";
        return db.query(TABLE_SLOTS, null, selection, null, null, null, COLUMN_SLOT_NUMBER);
    }

    // ✅ NEW METHOD: Reserve Slot
    public boolean reserveSlot(String slotNumber, String caseId, String reservedUntil) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_SLOT_STATUS, "Reserved");
        values.put(COLUMN_RESERVED_FOR_CASE, caseId);
        values.put(COLUMN_RESERVED_UNTIL, reservedUntil);

        String whereClause = COLUMN_SLOT_NUMBER + " = ?";
        String[] whereArgs = {slotNumber};

        int result = db.update(TABLE_SLOTS, values, whereClause, whereArgs);
        return result > 0;
    }

    // ✅ NEW METHOD: Occupy Slot
    public boolean occupySlot(String slotNumber, String caseId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_SLOT_STATUS, "Occupied");
        values.put(COLUMN_RESERVED_FOR_CASE, caseId);
        values.put(COLUMN_RESERVED_UNTIL, "");

        String whereClause = COLUMN_SLOT_NUMBER + " = ?";
        String[] whereArgs = {slotNumber};

        int result = db.update(TABLE_SLOTS, values, whereClause, whereArgs);
        return result > 0;
    }

    // ✅ NEW METHOD: Get Slot by Case ID
    public Cursor getSlotByCaseId(String caseId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_RESERVED_FOR_CASE + " = ?";
        String[] selectionArgs = {caseId};
        return db.query(TABLE_SLOTS, null, selection, selectionArgs, null, null, null);
    }

    // ✅ NEW METHOD: Save Doctor Report
    public boolean saveDoctorReport(String caseId, String timeOfDeath, String causeOfDeath,
                                    String marksInjuries, String medicalHistory, String doctorNotes) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CASE_ID, caseId);
        values.put(COLUMN_TIME_OF_DEATH, timeOfDeath);
        values.put(COLUMN_CAUSE_OF_DEATH, causeOfDeath);
        values.put(COLUMN_MARKS_INJURIES, marksInjuries);
        values.put(COLUMN_MEDICAL_HISTORY, medicalHistory);
        values.put(COLUMN_DOCTOR_NOTES, doctorNotes);
        values.put(COLUMN_REPORT_STATUS, "Completed");

        long result = db.insert(TABLE_DOCTOR_REPORTS, null, values);
        return result != -1;
    }

    // ✅ NEW METHOD: Get Doctor Report
    public Cursor getDoctorReport(String caseId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_CASE_ID + " = ?";
        String[] selectionArgs = {caseId};
        return db.query(TABLE_DOCTOR_REPORTS, null, selection, selectionArgs, null, null, null);
    }

    // ✅ NEW METHOD: Get Unread Notification Count
    public int getUnreadNotificationCount(String role) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_NOTIF_FOR_ROLE + " = ? AND " + COLUMN_NOTIF_STATUS + " = 'Unread'";
        String[] selectionArgs = {role};

        Cursor cursor = db.query(TABLE_NOTIFICATIONS, null, selection, selectionArgs, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }
}