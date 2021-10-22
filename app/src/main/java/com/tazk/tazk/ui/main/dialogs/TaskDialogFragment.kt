package com.tazk.tazk.ui.main.dialogs

import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.datepicker.MaterialDatePicker
import com.tazk.tazk.R
import com.tazk.tazk.databinding.DialogTaskBinding
import com.tazk.tazk.entities.network.response.ImageResponse
import com.tazk.tazk.entities.task.Task
import com.tazk.tazk.ui.main.MainViewModel
import com.tazk.tazk.ui.main.adapters.AttachmentsAdapter
import com.tazk.tazk.util.Tools
import com.tazk.tazk.util.listeners.CustomClickListener
import org.koin.android.viewmodel.ext.android.sharedViewModel
import java.util.*

class TaskDialogFragment: DialogFragment(), CustomClickListener {

    private lateinit var mView: View
    private lateinit var mBinding: DialogTaskBinding

    private val model: MainViewModel by sharedViewModel()
    private val attachmentsAdapter = AttachmentsAdapter(this)



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding:DialogTaskBinding = DataBindingUtil.inflate(inflater, R.layout.dialog_task, container, false)
        val view = binding.root

        binding.mainViewModel = model

        this.mBinding = binding
        this.mView = view
        setViewMetric()

        initializeSpnCategory()
        initializeRvAttachments()
        setObservables()

        return view
    }

    override fun onStart() {
        super.onStart()
        initializeTaskData()
    }

    //Configuro el tamaño del dialogo en pantalla
    private fun setViewMetric(){
        val displayMetrics = resources.displayMetrics

        val displayWidth = displayMetrics.widthPixels
        val displayHeight = displayMetrics.heightPixels
        val dialogWindowWidth = (displayWidth * 0.7f).toInt()
        val dialogWindowHeight = (displayHeight * 0.6f).toInt()
        val layoutParams = WindowManager.LayoutParams()

        mBinding.clDialogTask.minimumHeight = dialogWindowHeight
        mBinding.clDialogTask.minimumWidth = dialogWindowWidth

        layoutParams.copyFrom(dialog?.window!!.attributes)
        layoutParams.width = dialogWindowWidth
        layoutParams.height = dialogWindowHeight

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window!!.attributes = layoutParams
    }

    private fun initializeSpnCategory(){
        val spnAdapter = ArrayAdapter(requireContext(),
            android.R.layout.simple_spinner_item,
            model.categoriesList)
        mBinding.spnCategory.adapter = spnAdapter
        mBinding.spnCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                model.selectedTask?.let {
                    it.category = model.categoriesList[position]
                } ?: run {
                    model.selectedCategory = model.categoriesList[position]
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

        }
        model.selectedTask?.category?.let { category ->
            mBinding.spnCategory.setSelection(model.categoriesList.indexOf(model.categoriesList.find { it == category }))
        }
    }

    private fun initializeRvAttachments() {
        mBinding.rvAttachments.setHasFixedSize(true)
        mBinding.rvAttachments.adapter = attachmentsAdapter
    }

    private fun initializeTaskData(){
        model.selectedTask?.let {
            mBinding.etTitle.setText(it.title)
            mBinding.etDescription.setText(it.description)
            mBinding.etDate.setText(Tools.gregorianCalendarToString(it.createdAt, "dd/MM/yyyy"))
            model.attachments = it.image.toMutableList()
            attachmentsAdapter.setAttachments(model.attachments)
            checkShowAttachments()
        } ?: run {
            val date = Date(System.currentTimeMillis())
            val gc = GregorianCalendar()
            gc.timeInMillis = date.time
            mBinding.etDate.setText(Tools.gregorianCalendarToString(gc, "dd/MM/yyyy"))
        }
    }

    private fun setObservables() {
        model.saveTaskClickMutableHandler.observe(this) {
            if (it) {
                model.saveTaskClickMutableHandler.value = false
//                val locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                    resources.configuration.locales[0]
//                } else {
//                    resources.configuration.locale
//                }
//                val gc = GregorianCalendar.getInstance(locale) as GregorianCalendar
                val task = Task(
                    model.selectedTask?.id,
                    mBinding.etTitle.text.toString(),
                    mBinding.etDescription.text.toString(),
                    model.taskDate,
                    model.selectedTask?.category ?: model.selectedCategory,
                )
                if (model.attachments.isNotEmpty()) {
                    task.image = model.attachments
                }
                if (task.title != "") {
                    model.saveTask(task)
                    dismiss()
                } else {
                    missingTitle()
                }
            }
        }

        model.attachMutableHandler.observe(this) {
            if (it) {
                model.attachMutableHandler.value = false
                goAttachDialogFragment()
            }
        }

        model.onAttachSuccessMutableHandler.observe(this) {
            if (it) {
                model.onAttachSuccessMutableHandler.value = false
                onAttachSuccess()
            }
        }

        model.onAttachFailureMutableHandler.observe(this) {
            if (it) {
                model.onAttachFailureMutableHandler.value = false
                onAttachFailure()
            }
        }

        model.onDeleteAttachmentSuccessMutableHandler.observe(this) {
            if (it) {
                model.onDeleteAttachmentSuccessMutableHandler.value = false
                onDeleteAttachmentSuccess()
            }
        }

        model.onDeleteAttachmentFailureMutableHandler.observe(this) {
            if (it) {
                model.onDeleteAttachmentFailureMutableHandler.value = false
                onDeleteAttachmentFailure()
            }
        }

        model.dateClickMutableHandler.observe(this) {
            if (it) {
                model.dateClickMutableHandler.value = false
                openDatePicker()
            }
        }
    }

    private fun missingTitle() {
        Toast.makeText(requireContext(), "Por favor ingrese un título para poder guardar la tarea", Toast.LENGTH_SHORT).show()
    }

    private fun goAttachDialogFragment() {
        val fragment = AttachDialogFragment()
        fragment.show(childFragmentManager, "attach")
    }

    private fun onAttachSuccess() {
        model.attachImageResponse?.let {
            model.attachments.add(it)
            attachmentsAdapter.setAttachments(model.attachments)
            closeAttachDialog()
            checkShowAttachments()
        }
    }

    private fun closeAttachDialog() {
        val dialog = childFragmentManager.findFragmentByTag("attach")
        if (dialog is BottomSheetDialogFragment) {
            dialog.dismiss()
        }
    }

    private fun onAttachFailure() {
        Toast.makeText(requireContext(), "Error al adjuntar archivo, por favor reintente", Toast.LENGTH_SHORT).show()
    }

    private fun checkShowAttachments() {
        mBinding.rvAttachments.visibility = if (model.attachments.isNotEmpty()) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    override fun onItemClick(item: Any, position: Int) {
        model.selectedAttachmentPosition = position
        goImageDialogFragment()
    }

    override fun onItemLongClick(item: Any, position: Int) {
        if (item is ImageResponse && position >= 0) {
            model.selectedAttachmentPosition = position
            model.deleteAttachment(item)
        } else {
            Toast.makeText(requireContext(), "No se pudo eliminar la imagen, por favor reintente", Toast.LENGTH_SHORT).show()
        }
    }

    private fun onDeleteAttachmentSuccess() {
        if (model.selectedAttachmentPosition >= 0) {
            attachmentsAdapter.deleteAttachment(model.selectedAttachmentPosition)
        }
        checkShowAttachments()
        resetSelectedAttachment()
    }

    private fun onDeleteAttachmentFailure() {
        Toast.makeText(requireContext(), "Error al eliminar archivo adjunto, por favor reintente", Toast.LENGTH_SHORT).show()
    }

    private fun goImageDialogFragment() {
        val fragment = LargeImageDialogFragment()
        fragment.show(childFragmentManager, "large_image")
    }

    private fun resetSelectedAttachment() {
        model.selectedAttachmentPosition = -1
    }

    private fun openDatePicker() {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText(resources.getString(R.string.select_date))
            .setSelection(Calendar.getInstance().timeInMillis)
            .build()

        datePicker.addOnPositiveButtonClickListener {
            val dateStart = Date()
            dateStart.time = it
            val gc = GregorianCalendar()
            gc.timeInMillis = dateStart.time
            model.taskDate = gc
            mBinding.etDate.setText(Tools.gregorianCalendarToString(gc, "dd/MM/yyyy"))
            datePicker.dismiss()
        }

        datePicker.addOnNegativeButtonClickListener {
            datePicker.dismiss()
        }

        datePicker.show(childFragmentManager, "date_picker")
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        model.attachments = ArrayList()
    }
}