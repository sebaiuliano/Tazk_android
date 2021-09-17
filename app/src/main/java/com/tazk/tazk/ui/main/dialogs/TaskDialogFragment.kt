package com.tazk.tazk.ui.main.dialogs

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.tazk.tazk.R
import com.tazk.tazk.databinding.DialogTaskBinding
import com.tazk.tazk.entities.task.Task
import com.tazk.tazk.ui.main.MainViewModel
import org.koin.android.viewmodel.ext.android.sharedViewModel
import java.util.*

class TaskDialogFragment: DialogFragment() {

    private lateinit var mView: View
    private lateinit var mBinding: DialogTaskBinding

    private val model: MainViewModel by sharedViewModel()

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

    private fun initializeTaskData(){
        model.selectedTask?.let {
            mBinding.etTitle.setText(it.title)
            mBinding.etDescription.setText(it.description)
        }
    }

    private fun setObservables() {
        model.saveTaskClickMutableHandler.observe(this) {
            if (it) {
                model.saveTaskClickMutableHandler.value = false
                val locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    resources.configuration.locales[0]
                } else {
                    resources.configuration.locale
                }
                val gc = GregorianCalendar.getInstance(locale) as GregorianCalendar
                val task = Task(
                    model.selectedTask?.id,
                    mBinding.etTitle.text.toString(),
                    mBinding.etDescription.text.toString(),
                    model.selectedTask?.createdAt ?: gc
                )
                if (task.title != "") {
                    model.saveTask(task)
                    dismiss()
                } else {
                    missingTitle()
                }
            }
        }
    }

    private fun missingTitle() {
        Toast.makeText(requireContext(), "Por favor ingrese un título para poder guardar la tarea", Toast.LENGTH_SHORT).show()
    }

}