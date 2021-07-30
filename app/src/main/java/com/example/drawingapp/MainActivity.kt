package com.example.drawingapp

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Gallery
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.get
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_brush_size.*

class MainActivity : AppCompatActivity() {
    private var mImageButtonCurrentPaint: ImageButton? = null
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main)

            mImageButtonCurrentPaint = ll_paint_colors.get(1) as ImageButton
            mImageButtonCurrentPaint!!.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.pallet_pressed
                )
            )
            drawing_view.setSizeForBrush(20.toFloat())
            ib_brush.setOnClickListener {
                showBrushSizeChooserDialog()
            }
            ib_undo.setOnClickListener {
                drawing_view.onClickUndo()
            }
            ib_gallery.setOnClickListener{
                if(isReadStorageAllowed()){
                    val pickPhotoIntent = Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(pickPhotoIntent, GALLERY)

                }else {
                    requestStoragePermission()
                }
            }
        }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == GALLERY){
                try {
                    if(data!!.data != null){
                        iv_background.visibility = View.VISIBLE
                        iv_background.setImageURI(data.data)
                    }else{
                        Toast.makeText(
                            this,
                            "Error in parsing image or its corrupted",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }catch (e: Exception){
                    e.printStackTrace()
                }
            }
        }
    }
    private fun showBrushSizeChooserDialog(){
        val brushDialog = Dialog(this)
        brushDialog.setContentView(R.layout.dialog_brush_size)
        brushDialog.setTitle("Brush Size:")
        val smallBtn = brushDialog.ib_small_brush
        smallBtn.setOnClickListener{
            drawing_view.setSizeForBrush(10.toFloat())
            brushDialog.dismiss()
        }
        val mediumBtn = brushDialog.ib_medium_brush
        mediumBtn.setOnClickListener{
            drawing_view.setSizeForBrush(20.toFloat())
            brushDialog.dismiss()
        }
        val largeBtn = brushDialog.ib_large_brush
        largeBtn .setOnClickListener{
            drawing_view.setSizeForBrush(30.toFloat())
            brushDialog.dismiss()
        }
        brushDialog.show()
    }
    fun paintClicked(view: View){
        if(view !== mImageButtonCurrentPaint){
            val  imageButton = view as ImageButton
            val colorTag = imageButton.tag.toString()
            drawing_view.setColor(colorTag)
            imageButton.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.pallet_pressed
                )
            )
            mImageButtonCurrentPaint!!.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.pallet_normal
                )
            )
            mImageButtonCurrentPaint = view
        }
    }
    private fun requestStoragePermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE).toString())){
            Toast.makeText(this, "Need permission to add bg", Toast.LENGTH_LONG).show()
        }
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE), STORAGE_PERMISSION_CODE)
         fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
        ) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            if(requestCode == STORAGE_PERMISSION_CODE){
                if(grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(
                        this,
                        "Permission granted",
                        Toast.LENGTH_LONG).show()
                }else {
                    Toast.makeText(
                        this,
                        "Permission needed",
                        Toast.LENGTH_LONG).show()
                }
            }
        }
    }
    private fun isReadStorageAllowed():Boolean{
        val result = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
        return result == PackageManager.PERMISSION_GRANTED
    }
    companion object{
        private const val STORAGE_PERMISSION_CODE = 1
        private const val GALLERY = 2
    }
}