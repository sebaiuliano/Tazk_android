package com.tazk.tazk.ui.main.dialogs

import android.app.ActionBar
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Constraints
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.tazk.tazk.R
import com.tazk.tazk.databinding.DialogFilterBinding
import com.tazk.tazk.ui.main.MainViewModel
import org.koin.android.viewmodel.ext.android.sharedViewModel

class FilterDialogFragment : BottomSheetDialogFragment() {

    private lateinit var mView: View
    private lateinit var mBinding: DialogFilterBinding

    private val model: MainViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: DialogFilterBinding = DataBindingUtil.inflate(inflater, R.layout.dialog_filter, container, false)
        val view = binding.root

        binding.mainViewModel = model

        this.mBinding = binding
        this.mView = view
//        setViewMetric()
        setCategories()
        setObservers()

        return view
    }

    private fun setCategories() {
        for (category in model.categoriesList) {
            val button = MaterialButton(requireActivity(), null, R.attr.materialButtonOutlinedStyle)
            button.tag = category
            button.text = category
            button.setOnClickListener {
                model.selectedCategoryFilter = if ((it as? MaterialButton)?.isChecked == true) {
                    button.text.toString()
                } else {
                    null
                }
            }
            val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            layoutParams.weight = 1F
            mBinding.grpBtnCategoryFilter.addView(button, layoutParams)
        }
        if (model.selectedCategoryFilter != null) {
            val btn = mBinding.grpBtnCategoryFilter.findViewWithTag<MaterialButton>(model.selectedCategoryFilter)
            mBinding.grpBtnCategoryFilter.check(btn.id)
        }
    }

    private fun setObservers() {
        model.filterApplyMutableHandler.observe(this) {
            if (it) {
                model.filterApplyMutableHandler.postValue(false)
                model.getTasks()
                dismiss()
            }
        }
    }
}