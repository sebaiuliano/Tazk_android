package com.tazk.tazk.ui.main.dialogs

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
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

    private lateinit var speechRecognizer : SpeechRecognizer
    private val REQUEST_CODE = 1
    private var descriptionSelected = false



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
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(requireContext())
        setViewMetric()
        setMic()
        setDescriptionFocusListener()

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

    private fun initializeTaskData() {
        model.selectedTask?.let {
            mBinding.etTitle.setText(it.title)
            mBinding.etDescription.setText(it.description)
            mBinding.etDate.setText(Tools.gregorianCalendarToString(it.date, "dd/MM/yyyy"))
            model.attachments = it.image.toMutableList()
            attachmentsAdapter.setAttachments(model.attachments)
            checkShowAttachments()
            it.notificationDate?.let { notificationDate ->
                model.reminderDate = notificationDate
                mBinding.etReminderDate.setText(Tools.gregorianCalendarToString(notificationDate, "dd/MM/yyyy"))
                mBinding.etReminderTime.setText(Tools.gregorianCalendarToString(notificationDate, "HH:mm"))
                mBinding.swReminder.isChecked = true
                showReminderFields()
            }
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
                    model.taskDate.atStartOfDay(),
                    model.selectedTask?.category ?: model.selectedCategory,
                    if (model.hasReminder) model.reminderDate else null
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

        model.reminderCheckChangeMutableHandler.observe(this) {
            if (it) {
                model.reminderCheckChangeMutableHandler.value = false
                if (mBinding.swReminder.isChecked) {
                    showReminderFields()
                } else {
                    hideReminderFields()
                }
            }
        }

        model.reminderDateClickMutableHandler.observe(this) {
            if (it) {
                model.reminderDateClickMutableHandler.postValue(false)
                openReminderDatePicker()
            }
        }

        model.reminderTimeClickMutableHandler.observe(this) {
            if (it) {
                model.reminderTimeClickMutableHandler.postValue(false)
                openReminderTimePicker()
            }
        }

        model.micClickMutableHandler.observe(this) {
            if (it) {
                model.micClickMutableHandler.postValue(false)
                goTextToSpeech()
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
            var dateStart = Date()
            dateStart.time = it
            dateStart = Tools.dateWithOffset(dateStart)
            val gc = GregorianCalendar()
            gc.timeInMillis = dateStart.time
            model.taskDate = gc.atStartOfDay()
            mBinding.etDate.setText(Tools.gregorianCalendarToString(gc, "dd/MM/yyyy"))
            datePicker.dismiss()
        }

        datePicker.addOnNegativeButtonClickListener {
            datePicker.dismiss()
        }

        datePicker.show(childFragmentManager, "date_picker")
    }

    private fun openReminderDatePicker() {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText(resources.getString(R.string.select_date))
            .setSelection(Calendar.getInstance().timeInMillis)
            .build()

        datePicker.addOnPositiveButtonClickListener {
            var dateStart = Date()
            dateStart.time = it
            dateStart = Tools.dateWithOffset(dateStart)
            val gc = GregorianCalendar()
            gc.timeInMillis = dateStart.time
            model.reminderDate = gc.atStartOfDay()
            mBinding.etReminderDate.setText(Tools.gregorianCalendarToString(model.reminderDate, "dd/MM/yyyy"))
            datePicker.dismiss()
        }

        datePicker.addOnNegativeButtonClickListener {
            datePicker.dismiss()
        }

        datePicker.show(childFragmentManager, "reminder_date_picker")
    }

    private fun openReminderTimePicker() {
        val timePicker = model.selectedTask?.notificationDate?.let {
            val hour = Tools.gregorianCalendarToString(it, "HH")
            val minutes = Tools.gregorianCalendarToString(it, "mm")
            MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(hour.toIntOrNull() ?: 0)
                .setMinute(minutes.toIntOrNull() ?: 0)
                .setTitleText(resources.getString(R.string.select_time))
                .build()
        } ?: run {
            MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setTitleText(resources.getString(R.string.select_time))
                .build()
        }

        timePicker.addOnPositiveButtonClickListener {
            model.reminderDate.add(GregorianCalendar.HOUR, timePicker.hour)
            model.reminderDate.add(GregorianCalendar.MINUTE, timePicker.minute)
            mBinding.etReminderTime.setText("${timePicker.hour.toString().padStart(2, '0')}:${timePicker.minute.toString().padStart(2, '0')}")
            timePicker.dismiss()
        }

        timePicker.show(childFragmentManager, "reminder_time_picker")
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        model.attachments = ArrayList()
        model.taskDate = GregorianCalendar()
        model.reminderDate = GregorianCalendar()
    }

    private fun GregorianCalendar.atStartOfDay() : GregorianCalendar {
        this.set(Calendar.HOUR_OF_DAY, 0)
        this.set(Calendar.MINUTE, 0)
        this.set(Calendar.SECOND, 0)
        this.set(Calendar.MILLISECOND, 0)
        return this
    }

    private fun showReminderFields() {
        model.hasReminder = true
        with(mBinding) {
            tvReminderDate.visibility = View.VISIBLE
            etReminderDate.visibility = View.VISIBLE
            ibReminderDate.visibility = View.VISIBLE
            tvReminderTime.visibility = View.VISIBLE
            etReminderTime.visibility = View.VISIBLE
            ibReminderTime.visibility = View.VISIBLE
        }
    }

    private fun hideReminderFields() {
        model.hasReminder = false
        with(mBinding){
            tvReminderDate.visibility = View.GONE
            etReminderDate.visibility = View.GONE
            ibReminderDate.visibility = View.GONE
            tvReminderTime.visibility = View.GONE
            etReminderTime.visibility = View.GONE
            ibReminderTime.visibility = View.GONE
        }
    }

    private fun goTextToSpeech() {
        if(ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
            checkPermission()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        speechRecognizer.destroy()
    }

    private fun checkPermission() {
        val list : Array<String> = arrayOf(android.Manifest.permission.RECORD_AUDIO)
        ActivityCompat.requestPermissions(requireActivity(), list, REQUEST_CODE);
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_CODE && grantResults.isNotEmpty()){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
                Toast.makeText(requireContext(),"Permission Granted",Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setMic() {
        val speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())

        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
            }

            override fun onBeginningOfSpeech() {
            }

            override fun onRmsChanged(rmsdB: Float) {
            }

            override fun onBufferReceived(buffer: ByteArray?) {
            }

            override fun onEndOfSpeech() {
            }

            override fun onError(error: Int) {
            }

            override fun onResults(results: Bundle?) {
                mBinding.ibMic.setImageResource(R.drawable.ic_mic)
                val data: ArrayList<String>? = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (data != null) {
                    if (!descriptionSelected) {
                        mBinding.etTitle.setText(data[0])
                    } else {
                        mBinding.etDescription.setText(data[0])
                    }
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {
            }

            override fun onEvent(eventType: Int, params: Bundle?) {
            }
        })

        mBinding.ibMic.setOnTouchListener{ v, event ->
            when (event?.action) {
                MotionEvent.ACTION_UP -> {
                    speechRecognizer.stopListening()
                }
                MotionEvent.ACTION_DOWN -> {
                    mBinding.ibMic.setImageResource(R.drawable.ic_mic_listening)
                    speechRecognizer.startListening(speechRecognizerIntent)
                }
            }
            false
        }
    }

    private fun setDescriptionFocusListener() {
        mBinding.etDescription.setOnFocusChangeListener { v, hasFocus ->
            descriptionSelected = hasFocus
        }
    }
}