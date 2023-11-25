package com.inmoment.moments.program

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.inmoment.moments.R
import com.inmoment.moments.databinding.FragmentAccountProgramPickerBinding
import com.inmoment.moments.framework.common.AppConstants
import com.inmoment.moments.framework.persist.SharedPrefsInf
import com.inmoment.moments.framework.ui.BaseFragment
import com.inmoment.moments.home.helper.CustomSpinner
import com.inmoment.moments.home.model.AccountProgram
import com.inmoment.moments.program.model.Program
import com.lysn.clinician.utility.extensions.inflate
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AccountProgramPickerFragment : BaseFragment() {

    private val viewModel: ProgramViewsViewModel by viewModels()
    private lateinit var mBinding: FragmentAccountProgramPickerBinding

    @Inject
    lateinit var sharedPrefsInf: SharedPrefsInf
    private lateinit var defaultAccountId: String
    private lateinit var programPickerAdapter: ProgramPickerAdapter
    lateinit var defaultProgramId: String
    var selectedSpinnerItemPosition = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = baseBinding(
            R.layout.fragment_account_program_picker,
            inflater,
            container,
            FragmentAccountProgramPickerBinding::class.java
        )
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.cxLabel.setOnClickListener {
            mNavController.popBackStack()
        }

        defaultAccountId = sharedPrefsInf.getDefaultAccountAndProgramId().first
        defaultProgramId = sharedPrefsInf.getDefaultAccountAndProgramId().second

        programRecycleViewSetUp()
        viewModel.programList.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                accountSpinnerSetup()
                accountHeaderSectionSetup()
            }
        }
    }

    private fun accountSpinnerSetup() {

        val spinnerArrayAdapter: ArrayAdapter<AccountProgram> =
            spinnerArrayAdapter(
                viewModel.programList.value ?: listOf(),
                mBinding.accountSpinner
            )
        mBinding.accountSpinner.setSpinnerEventsListener(onSpinnerEventsListener(mBinding.accountSpinner))
        mBinding.accountSpinner.onItemSelectedListener =
            onItemSelectedListener(mBinding.accountSpinner)

        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        mBinding.accountSpinner.adapter = spinnerArrayAdapter

    }

    private fun accountHeaderSectionSetup() {
        viewModel.programList.value?.forEachIndexed { index, it ->
            if (defaultAccountId == it.account.accountId) {
                selectedSpinnerItemPosition = index
                it.programList.forEach { pro ->
                    if (defaultProgramId == pro.id) {
                        (mBinding.cxIV.background as GradientDrawable).setColor(pro.domainColor)
                        mBinding.cxIV.text = pro.cloudShortText
                        mBinding.cxLabel.text = pro.programName
                    }
                }

                mBinding.accountSpinner.setSelection(index)
            }
        }
    }

    private fun programRecycleViewSetUp() {
        mBinding.programsRV.layoutManager = LinearLayoutManager(this.requireContext())
        programPickerAdapter =
            object : ProgramPickerAdapter(this.requireActivity(), sharedPrefsInf) {
                override fun onItemClick(model: Program, position: Int) {
                    programItemSelected(model)
                }
            }
        mBinding.programsRV.adapter = programPickerAdapter
    }

    private fun programItemSelected(model: Program) {
        sharedPrefsInf.setDefaultAccountAndProgramId(
            model.accountId,
            model.id,
            model.userProgramId
        )
        if (defaultProgramId != model.id)
            mNavController.previousBackStackEntry?.savedStateHandle?.set(
                AppConstants.NAV_BACK_PROGRAM,
                model
            )
        viewModel.updateSelectedProgramData(model)
        viewModel.onDatabaseUpdate.observe(viewLifecycleOwner) {
            mNavController.popBackStack()
        }
    }

    private fun spinnerArrayAdapter(
        categories: List<AccountProgram>,
        accountSpinner: CustomSpinner
    ) = object : ArrayAdapter<AccountProgram>(
        this.requireContext(), R.layout.spinner_header_account_picker, categories
    ) {
        override fun getDropDownView(
            position: Int, convertView: View?,
            parent: ViewGroup
        ): View {
            accountSpinner.dropDownWidth = accountSpinner.width
            accountSpinner.background =
                ContextCompat.getDrawable(context, R.drawable.account_picker_spinner_selected_bg)
            // It is used to set our custom view.
            val view: View = parent.inflate(R.layout.spinner_item_account_picker)
            view.findViewById<TextView>(R.id.accountName).text = categories[position].account.name
            if (selectedSpinnerItemPosition == position) {
                view.findViewById<ImageView>(R.id.imgSelected).visibility = View.VISIBLE
            }
            return view
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view: View = parent.inflate(R.layout.spinner_header_account_picker)
            view.findViewById<TextView>(R.id.accountName).text = categories[position].account.name
            return view
        }

    }

    private fun onItemSelectedListener(
        accountSpinner: CustomSpinner
    ): OnItemSelectedListener {
        return object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                // selectedSpinnerItemPosition1 = position
                viewModel.programList.value?.get(position)
                    ?.let { programPickerAdapter.addItems(it.programList) }
                accountSpinner.background =
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.account_picker_spinner_bg
                    )
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                accountSpinner.background =
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.account_picker_spinner_bg
                    )
            }
        }
    }

    private fun onSpinnerEventsListener(accountSpinner: CustomSpinner) =
        object : CustomSpinner.OnSpinnerEventsListener {
            override fun onSpinnerOpened(spinner: Spinner?) {
                accountSpinner.background =
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.account_picker_spinner_selected_bg
                    )
            }

            override fun onSpinnerClosed(spinner: Spinner?) {
                accountSpinner.background =
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.account_picker_spinner_bg
                    )
            }

        }
}