package com.tazk.tazk.ui.main.dialogs

import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.squareup.picasso.Picasso
import com.tazk.tazk.R
import com.tazk.tazk.databinding.DialogLargeImageBinding
import com.tazk.tazk.ui.main.MainViewModel
import org.koin.android.viewmodel.ext.android.sharedViewModel

class LargeImageDialogFragment : DialogFragment() {
    private lateinit var mView: View
    private lateinit var mBinding: DialogLargeImageBinding

    private val model: MainViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding:DialogLargeImageBinding = DataBindingUtil.inflate(inflater, R.layout.dialog_large_image, container, false)
        val view = binding.root

        binding.mainViewModel = model

        this.mBinding = binding
        this.mView = view
        setViewMetric()
        setImage()

        return view
    }

    //Configuro el tamaño del dialogo en pantalla
    private fun setViewMetric(){
        val displayMetrics = resources.displayMetrics

        val displayWidth = displayMetrics.widthPixels
        val displayHeight = displayMetrics.heightPixels
        val dialogWindowWidth = (displayWidth * 0.7f).toInt()
        val dialogWindowHeight = (displayHeight * 0.6f).toInt()
        val layoutParams = WindowManager.LayoutParams()

        mBinding.clDialogLargeImage.minimumHeight = dialogWindowHeight
        mBinding.clDialogLargeImage.minimumWidth = dialogWindowWidth

        layoutParams.copyFrom(dialog?.window!!.attributes)
        layoutParams.width = dialogWindowWidth
        layoutParams.height = dialogWindowHeight

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window!!.attributes = layoutParams
    }

    private fun setImage() {
        Picasso.get().load(model.attachments[model.selectedAttachmentPosition].url).into(mBinding.ivImg)
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        model.selectedAttachmentPosition = 0
    }
}