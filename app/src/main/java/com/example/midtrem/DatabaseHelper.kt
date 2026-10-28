package com.example.midtrem

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "StudentDatabase.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_STUDENTS = "students"
        const val COLUMN_ID = "student_id"
        const val COLUMN_NAME = "name"
        const val COLUMN_AGE = "age"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = """
            CREATE TABLE $TABLE_STUDENTS (
                $COLUMN_ID TEXT PRIMARY KEY,
                $COLUMN_NAME TEXT NOT NULL,
                $COLUMN_AGE INTEGER NOT NULL
            )
        """.trimIndent()
        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_STUDENTS")
        onCreate(db)
    }

    // CREATE / ADD Student
    fun addStudent(student: Student): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_ID, student.studentId)
            put(COLUMN_NAME, student.name)
            put(COLUMN_AGE, student.age)
        }
        val result = db.insert(TABLE_STUDENTS, null, values)
        db.close()
        return result != -1L
    }

    // UPDATE Student
    fun updateStudent(student: Student): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, student.name)
            put(COLUMN_AGE, student.age)
        }
        val rowsAffected = db.update(
            TABLE_STUDENTS,
            values,
            "$COLUMN_ID = ?",
            arrayOf(student.studentId)
        )
        db.close()
        return rowsAffected > 0
    }

    // DELETE Student
    fun deleteStudent(studentId: String): Boolean {
        val db = writableDatabase
        val rowsAffected = db.delete(
            TABLE_STUDENTS,
            "$COLUMN_ID = ?",
            arrayOf(studentId)
        )
        db.close()
        return rowsAffected > 0
    }

    // READ / VIEW All Students
    fun getAllStudents(): List<Student> {
        val studentList = mutableListOf<Student>()
        val selectQuery = "SELECT * FROM $TABLE_STUDENTS"
        val db = readableDatabase
        val cursor = db.rawQuery(selectQuery, null)

        if (cursor.moveToFirst()) {
            val idIndex = cursor.getColumnIndexOrThrow(COLUMN_ID)
            val nameIndex = cursor.getColumnIndexOrThrow(COLUMN_NAME)
            val ageIndex = cursor.getColumnIndexOrThrow(COLUMN_AGE)

            do {
                val student = Student(
                    studentId = cursor.getString(idIndex),
                    name = cursor.getString(nameIndex),
                    age = cursor.getInt(ageIndex)
                )
                studentList.add(student)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return studentList
    }

    // Check if Student exists by ID
    fun getStudentById(studentId: String): Student? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_STUDENTS,
            null,
            "$COLUMN_ID = ?",
            arrayOf(studentId),
            null, null, null
        )

        var student: Student? = null
        if (cursor.moveToFirst()) {
            val idIndex = cursor.getColumnIndexOrThrow(COLUMN_ID)
            val nameIndex = cursor.getColumnIndexOrThrow(COLUMN_NAME)
            val ageIndex = cursor.getColumnIndexOrThrow(COLUMN_AGE)

            student = Student(
                studentId = cursor.getString(idIndex),
                name = cursor.getString(nameIndex),
                age = cursor.getInt(ageIndex)
            )
        }

        cursor.close()
        db.close()
        return student
    }
}
