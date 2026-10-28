package com.example.midtrem

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.midtrem.databinding.ItemStudentBinding

class StudentAdapter(
    private var students: List<Student>,
    private val onItemClick: (Student) -> Unit
) : RecyclerView.Adapter<StudentAdapter.StudentViewHolder>() {

    class StudentViewHolder(val binding: ItemStudentBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val binding = ItemStudentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StudentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        val student = students[position]
        holder.binding.tvStudentName.text = student.name
        holder.binding.tvStudentId.text = "ID: ${student.studentId}"
        holder.binding.tvStudentAge.text = "Age: ${student.age} yrs"

        holder.itemView.setOnClickListener {
            onItemClick(student)
        }
    }

    override fun getItemCount(): Int = students.size

    fun updateData(newStudents: List<Student>) {
        students = newStudents
        notifyDataSetChanged()
    }
}
