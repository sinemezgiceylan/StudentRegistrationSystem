package com.sinemezgiceylan.studentregistrationsystem

import android.content.Intent
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.sinemezgiceylan.studentregistrationsystem.databinding.ActivityRegistrationBinding
import java.io.ByteArrayOutputStream

class RegistrationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistrationBinding
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    var selectedBitmap : Bitmap? = null
    private lateinit var database: SQLiteDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        database = this.openOrCreateDatabase("Students", MODE_PRIVATE, null)

        registerLauncher()

        val intent = intent
        val info = intent.getStringExtra("info")
        if (info.equals("new")) {
            binding.dateText.setText("")
            binding.studentNameText.setText("")
            binding.levelText.setText("")
            binding.dateOfBirthText.setText("")
            binding.telText.setText("")
            binding.emailText.setText("")
            binding.motherNameText.setText("")
            binding.fatherNameText.setText("")
            binding.button.visibility = View.VISIBLE
            binding.imageView.setImageResource(R.drawable.selectimage)

        } else {

            binding.button.visibility = View.INVISIBLE
            val selectedId = intent.getIntExtra("id",1)

            val cursor = database.rawQuery("SELECT * FROM students WHERE id = ?", arrayOf(selectedId.toString()))

            val dateIx = cursor.getColumnIndex("date")
            val studentNameIx = cursor.getColumnIndex("studentname")
            val levelIx = cursor.getColumnIndex("level")
            val dateOfBirthIx = cursor.getColumnIndex("dateofbirth")
            val telIx = cursor.getColumnIndex("tel")
            val emailIx = cursor.getColumnIndex("email")
            val motherNameIx = cursor.getColumnIndex("mothername")
            val fatherNameIx = cursor.getColumnIndex("fathername")
            val imageIx = cursor.getColumnIndex("image")

            while(cursor.moveToNext()) {
                binding.dateText.setText(cursor.getString(dateIx))
                binding.studentNameText.setText(cursor.getString(studentNameIx))
                binding.levelText.setText(cursor.getString(levelIx))
                binding.dateOfBirthText.setText(cursor.getString(dateOfBirthIx))
                binding.telText.setText(cursor.getString(telIx))
                binding.emailText.setText(cursor.getString(emailIx))
                binding.motherNameText.setText(cursor.getString(motherNameIx))
                binding.fatherNameText.setText(cursor.getString(fatherNameIx))

                val byteArray = cursor.getBlob(imageIx)
                val bitmap = BitmapFactory.decodeByteArray(byteArray,0,byteArray.size)
                binding.imageView.setImageBitmap(bitmap)

            }
            cursor.close()

        }

    }

    fun save(view: View) {

        val date = binding.dateText.text.toString()
        val studentName = binding.studentNameText.text.toString()
        val level = binding.levelText.text.toString()
        val dateOfBirth = binding.dateOfBirthText.text.toString()
        val tel = binding.telText.text.toString()
        val email = binding.emailText.text.toString()
        val motherName = binding.motherNameText.text.toString()
        val fatherName = binding.fatherNameText.text.toString()

        if (selectedBitmap != null) {
            val smallBitmap = makeSmallerBitmap(selectedBitmap!!, 300)

            val outputStream = ByteArrayOutputStream()
            smallBitmap.compress(Bitmap.CompressFormat.PNG, 50, outputStream)
            val byteArray = outputStream.toByteArray()

            try {
                database.execSQL("CREATE TABLE IF NOT EXISTS students (id INTEGER PRIMARY KEY, date VARCHAR, studentname VARCHAR, level VARCHAR, dateofbirth VARCHAR, tel VARCHAR, email VARCHAR, mothername VARCHAR, fathername VARCHAR, image BLOB)")

                val sqlString = "INSERT INTO students (date, studentname, level, dateofbirth, tel, email, mothername, fathername, image) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)"
                val statement = database.compileStatement(sqlString)

                statement.bindString(1, date)
                statement.bindString(2, studentName)
                statement.bindString(3, level)
                statement.bindString(4, dateOfBirth)
                statement.bindString(5, tel)
                statement.bindString(6, email)
                statement.bindString(7, motherName)
                statement.bindString(8, fatherName)
                statement.bindBlob(9, byteArray)
                statement.execute()

            } catch (e: Exception) {
                e.printStackTrace()
            }

            Toast.makeText(this@RegistrationActivity,"Saved!",Toast.LENGTH_LONG).show()

            val intent = Intent(this@RegistrationActivity,MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)


        }


    }

    private fun makeSmallerBitmap(image: Bitmap, maximumSize: Int) : Bitmap {

        var width = image.width
        var height = image.height

        val bitmapRatio : Double = width.toDouble() / height.toDouble()

        if(bitmapRatio >1 ) {
            //landscape
            width = maximumSize
            val scaledHeight = width / bitmapRatio
            height = scaledHeight.toInt()

        } else {
            //portrait
            height = maximumSize
            val scaledWidth = height / bitmapRatio
            width = scaledWidth.toInt()
        }

        return Bitmap.createScaledBitmap(image,width,height,true)
    }


    fun selectImage(view: View) {

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,android.Manifest.permission.READ_MEDIA_IMAGES)) {
                    // rationale
                    Snackbar.make(view,"Permission needed for gallery", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission",View.OnClickListener {
                        // request permission
                        permissionLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES)
                    }).show()

                } else {
                    // request permission
                    permissionLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES)
                }


            } else {
                val intentToGallery = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            }
        } else {
            if (ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    // rationale
                    Snackbar.make(view,"Permission needed for gallery", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission",View.OnClickListener {
                        // request permission
                        permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    }).show()

                } else {
                    // request permission
                    permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                }


            } else {
                val intentToGallery = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            }
        }

    }

    private fun registerLauncher() {

        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if(result.resultCode == RESULT_OK) {
                val intentFromResult = result.data
                if(intentFromResult != null) {
                    val imageData = intentFromResult.data
                    //binding.imageView.setImageURI(imageData)
                    if (imageData != null) {
                        try {
                            if(Build.VERSION.SDK_INT >= 28) {
                                // Seçilen görseli imageView'da gösterdik.
                                val source = ImageDecoder.createSource(this@RegistrationActivity.contentResolver, imageData)
                                selectedBitmap = ImageDecoder.decodeBitmap(source)
                                binding.imageView.setImageBitmap(selectedBitmap)
                            } else {
                                selectedBitmap = MediaStore.Images.Media.getBitmap(contentResolver,imageData)
                                binding.imageView.setImageBitmap((selectedBitmap))
                            }

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }

        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
            if(result) {
                // İzin verildiyse
                val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            } else {
                // İzin verilmediyse
                Toast.makeText(this@RegistrationActivity, "Permission needed!" , Toast.LENGTH_LONG).show()
            }
        }
    }



}