package com.tazk.tazk.ui.main.dialogs

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.tazk.tazk.R
import com.tazk.tazk.databinding.DialogAttachBinding
import com.tazk.tazk.ui.main.MainViewModel
import org.koin.android.viewmodel.ext.android.sharedViewModel
import java.io.File
import java.io.FileOutputStream

class AttachDialogFragment : BottomSheetDialogFragment() {

    private lateinit var mView: View
    private lateinit var mBinding: DialogAttachBinding
    private val model: MainViewModel by sharedViewModel()

    private val REQUEST_SELECT_IMAGE = 1001
    private val REQUEST_IMAGE_CAPTURE = 1002
    private val REQUEST_CAMERA_PERMISSIONS_CODE = 2000
    private var isCamera = false
    private var outputFileUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: DialogAttachBinding = DataBindingUtil.inflate(inflater, R.layout.dialog_attach, container, false)
        val view = binding.root

        binding.mainViewModel = model

        this.mBinding = binding
        this.mView = view
        setObservers()

        return view
    }

    private fun setObservers() {
        model.attachImageMutableHandler.observe(this) {
            if (it) {
                model.attachImageMutableHandler.value = false
                goGetImage()
            }
        }

        model.attachPhotoMutableHandler.observe(this) {
            if (it) {
                model.attachPhotoMutableHandler.value = false
                if (askForPermissions()) {
                    goTakePhoto()
                }
            }
        }
    }

    private fun goGetImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        isCamera = false
        val chooserIntent = Intent.createChooser(intent, "Elegir imagen")
        startActivityForResult(chooserIntent, REQUEST_SELECT_IMAGE)
    }

    private fun goTakePhoto() {
        isCamera = true
        val imgDirPath = File(requireActivity().filesDir, "images")
        imgDirPath.mkdirs()
        val fname = "IMG_${System.currentTimeMillis()}.jpg"
        model.file = File(imgDirPath, fname)
        model.file?.let {
            it.createNewFile()
            outputFileUri = FileProvider.getUriForFile(requireContext(), requireActivity().applicationContext.packageName + ".provider", it)
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri)
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == AppCompatActivity.RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE || requestCode == REQUEST_SELECT_IMAGE) {
                onSelectedImage(data)
                model.attachImage()
            }
        }
    }

    private fun onSelectedImage(data: Intent?): File? {
        //Genero una imagen temporal comprimida
        val selectedImageUri: Uri? = if (!isCamera) { data?.data } else { outputFileUri }
        val bmp = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, selectedImageUri)
        model.file = File.createTempFile("tmp_image", null, requireActivity().cacheDir)
        val fos = FileOutputStream(model.file)
        bmp.compress(Bitmap.CompressFormat.JPEG, 90, fos)
        fos.close()
        return model.file
    }

    private fun isPermissionsAllowed(): Boolean {
        return ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    }

    private fun askForPermissions(): Boolean {
        if (!isPermissionsAllowed()) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.CAMERA)) {
                showPermissionDeniedDialog()
            } else {
                ActivityCompat.requestPermissions(requireActivity(),arrayOf(Manifest.permission.CAMERA),REQUEST_CAMERA_PERMISSIONS_CODE)
            }
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int,permissions: Array<String>,grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CAMERA_PERMISSIONS_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission is granted, you can perform your operation here
                    goTakePhoto()
                } else {
                    // permission is denied, you can ask for permission again, if you want
                    askForPermissions()
                }
                return
            }
        }
    }

    private fun showPermissionDeniedDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Permission Denied")
            .setMessage("Permission is denied, Please allow permissions from App Settings.")
            .setPositiveButton("App Settings") { dialogInterface, i ->
                // send to app settings if permission is denied permanently
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val uri = Uri.fromParts("package", requireActivity().packageName, null)
                intent.data = uri
                startActivity(intent)
            }
            .setNegativeButton("Cancel",null)
            .show()
    }



}