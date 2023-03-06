package com.sinemezgiceylan.studentregistrationsystem

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.sinemezgiceylan.studentregistrationsystem.databinding.ActivityStudentsBinding

class StudentsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStudentsBinding
    private lateinit var studentList: ArrayList<Student>
    private lateinit var studentAdapter: StudentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudentsBinding.inflate(layoutInflater)
        val view =binding.root
        setContentView(view)

        studentList = ArrayList<Student>()

        studentAdapter = StudentAdapter(studentList)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = studentAdapter

        try {

            val database = this.openOrCreateDatabase("Students", MODE_PRIVATE, null)
            val cursor = database.rawQuery("SELECT * FROM students",null)

            val studentNameIx = cursor.getColumnIndex("studentname")
            val idIx = cursor.getColumnIndex("id")

            while (cursor.moveToNext()) {
                val name = cursor.getString(studentNameIx)
                val id = cursor.getInt(idIx)
                val student = Student(name, id)
                studentList.add(student)

            }

            studentAdapter.notifyDataSetChanged()

            cursor.close()

        } catch (e: Exception) {
            e.printStackTrace()
        }





    }
}