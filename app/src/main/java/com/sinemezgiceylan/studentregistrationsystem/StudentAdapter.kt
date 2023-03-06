package com.sinemezgiceylan.studentregistrationsystem

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sinemezgiceylan.studentregistrationsystem.databinding.RecyclerRowBinding

class StudentAdapter(val studentList: ArrayList<Student>) : RecyclerView.Adapter<StudentAdapter.StudentHolder>() {

    class StudentHolder(val binding: RecyclerRowBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentHolder {
        val binding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return StudentHolder(binding)
    }

    override fun getItemCount(): Int {
        return studentList.size
    }

    override fun onBindViewHolder(holder: StudentHolder, position: Int) {
        holder.binding.recyclerViewTextView.text = studentList.get(position).name
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context,RegistrationActivity::class.java)
            intent.putExtra("info","old")
            intent.putExtra("id",studentList.get(position).id)
            holder.itemView.context.startActivity(intent)
        }
    }
}