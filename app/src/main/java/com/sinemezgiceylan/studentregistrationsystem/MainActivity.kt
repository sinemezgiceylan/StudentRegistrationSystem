package com.sinemezgiceylan.studentregistrationsystem

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.sinemezgiceylan.studentregistrationsystem.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    fun studentsButtonClicked(view: View) {

        val intent = Intent(this@MainActivity,StudentsActivity::class.java)
        startActivity(intent)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.add_student_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.add_student_item){
            val intent = Intent(this@MainActivity,RegistrationActivity::class.java)
            intent.putExtra("info","new")
            startActivity(intent)

        }
        return super.onOptionsItemSelected(item)
    }

}