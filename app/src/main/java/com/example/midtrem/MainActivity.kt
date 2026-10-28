package com.example.midtrem

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.midtrem.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var studentAdapter: StudentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        dbHelper = DatabaseHelper(this)

        setupAgeSlider()
        setupRecyclerView()
        setupListeners()
        loadStudents()
    }

    private fun setupAgeSlider() {
        binding.sliderAge.addOnChangeListener { _, value, _ ->
            val age = value.toInt()
            binding.tvAgeLabel.text = "Student Age: $age years old"
        }
    }

    private fun setupRecyclerView() {
        studentAdapter = StudentAdapter(emptyList()) { student ->
            // Populate form input when an item is selected from list
            binding.etStudentName.setText(student.name)
            binding.etStudentId.setText(student.studentId)
            val ageValue = student.age.toFloat().coerceIn(17f, 50f)
            binding.sliderAge.value = ageValue
            binding.tvAgeLabel.text = "Student Age: ${ageValue.toInt()} years old"
            Toast.makeText(this, "Selected: ${student.name}. Tap 'Clear Form' anytime to add a new entry.", Toast.LENGTH_LONG).show()
        }

        binding.rvStudents.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = studentAdapter
        }
    }

    private fun setupListeners() {
        // ADD Button
        binding.btnADD.setOnClickListener {
            if (validateInputs()) {
                val name = binding.etStudentName.text.toString().trim()
                val id = binding.etStudentId.text.toString().trim()
                val age = binding.sliderAge.value.toInt()

                val student = Student(studentId = id, name = name, age = age)
                val success = dbHelper.addStudent(student)

                if (success) {
                    Toast.makeText(this, "Student added successfully!", Toast.LENGTH_SHORT).show()
                    clearInputFields()
                    loadStudents()
                } else {
                    Toast.makeText(this, "Error: Student ID '$id' already exists! Click EDIT to update existing student.", Toast.LENGTH_LONG).show()
                }
            }
        }

        // EDIT Button
        binding.btnEDIT.setOnClickListener {
            if (validateInputs()) {
                val name = binding.etStudentName.text.toString().trim()
                val id = binding.etStudentId.text.toString().trim()
                val age = binding.sliderAge.value.toInt()

                val student = Student(studentId = id, name = name, age = age)
                val success = dbHelper.updateStudent(student)

                if (success) {
                    Toast.makeText(this, "Student updated successfully!", Toast.LENGTH_SHORT).show()
                    clearInputFields()
                    loadStudents()
                } else {
                    Toast.makeText(this, "Error: Student ID '$id' not found in database!", Toast.LENGTH_LONG).show()
                }
            }
        }

        // DELETE Button
        binding.btnDELETE.setOnClickListener {
            val id = binding.etStudentId.text.toString().trim()
            if (id.isEmpty()) {
                binding.etStudentId.error = "Please enter Student ID to delete"
                Toast.makeText(this, "Please enter Student ID to delete", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            binding.etStudentId.error = null
            val success = dbHelper.deleteStudent(id)

            if (success) {
                Toast.makeText(this, "Student deleted successfully!", Toast.LENGTH_SHORT).show()
                clearInputFields()
                loadStudents()
            } else {
                Toast.makeText(this, "Error: Student ID '$id' not found!", Toast.LENGTH_LONG).show()
            }
        }

        // VIEW ALL Button
        binding.btnVIEW.setOnClickListener {
            loadStudents(showToastInfo = true)
        }

        // CLEAR FORM Button
        binding.btnCLEAR.setOnClickListener {
            clearInputFields()
            Toast.makeText(this, "Form cleared. Ready for new student entry!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun validateInputs(): Boolean {
        var isValid = true

        val name = binding.etStudentName.text.toString().trim()
        val id = binding.etStudentId.text.toString().trim()
        val age = binding.sliderAge.value.toInt()

        binding.etStudentName.error = null
        binding.etStudentId.error = null

        // Validate Student Name
        if (name.isEmpty()) {
            binding.etStudentName.error = "Student Name is required"
            isValid = false
        }

        // Validate Student ID
        if (id.isEmpty()) {
            binding.etStudentId.error = "Student ID is required"
            isValid = false
        }

        // Validate Student Age Range (min 17, max 50)
        if (age !in 17..50) {
            val warningMsg = "Validation Error: Age must be between 17 and 50 years old!"
            Toast.makeText(this, warningMsg, Toast.LENGTH_LONG).show()
            isValid = false
        }

        return isValid
    }

    private fun loadStudents(showToastInfo: Boolean = false) {
        val studentList = dbHelper.getAllStudents()
        studentAdapter.updateData(studentList)

        if (studentList.isEmpty()) {
            binding.tvEmptyState.visibility = View.VISIBLE
            binding.rvStudents.visibility = View.GONE
            if (showToastInfo) {
                Toast.makeText(this, "No student records found in database.", Toast.LENGTH_SHORT).show()
            }
        } else {
            binding.tvEmptyState.visibility = View.GONE
            binding.rvStudents.visibility = View.VISIBLE
            if (showToastInfo) {
                Toast.makeText(this, "Loaded ${studentList.size} student record(s).", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun clearInputFields() {
        binding.etStudentName.setText("")
        binding.etStudentId.setText("")
        binding.sliderAge.value = 17f
        binding.tvAgeLabel.text = "Student Age: 17 years old"

        binding.etStudentName.error = null
        binding.etStudentId.error = null

        binding.etStudentName.clearFocus()
        binding.etStudentId.clearFocus()
    }
}
