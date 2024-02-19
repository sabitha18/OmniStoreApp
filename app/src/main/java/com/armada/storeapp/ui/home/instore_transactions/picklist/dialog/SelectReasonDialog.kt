package com.armada.storeapp.ui.home.instore_transactions.picklist.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.*
import android.widget.SpinnerAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.armada.storeapp.R
import com.armada.storeapp.data.model.response.SkipReasonListResponse
import com.armada.storeapp.databinding.DialogReasonBinding
import com.armada.storeapp.ui.home.instore_transactions.picklist.adapter.ReasonSpinnerAdapter


class SelectReasonDialog(
    reasonlistener: ReasonInterface?,
    list: ArrayList<SkipReasonListResponse.SkipReasons>
) : DialogFragment() {

    var reasonInterface: ReasonInterface? = null
    var dialogReasonBinding: DialogReasonBinding? = null
    var reasonList: ArrayList<SkipReasonListResponse.SkipReasons> =
        list

    lateinit var reasonAdapter: SpinnerAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, android.R.style.Theme_Translucent)
        isCancelable = false
    }

    init {
        reasonInterface = reasonlistener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        dialogReasonBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.dialog_reason,
            null,
            false
        )

        dialogReasonBinding?.spinner?.setAdapter(
            ReasonSpinnerAdapter(
                requireContext(), reasonList!!
            )
        )
        dialogReasonBinding?.btnProcess?.setOnClickListener(View.OnClickListener {
            val selectedReason =
                dialogReasonBinding?.spinner?.selectedItem as SkipReasonListResponse.SkipReasons
            reasonInterface?.OnReasonSelected(selectedReason)
            dismiss()
        })

        dialogReasonBinding?.btnCancel?.setOnClickListener {
            dismiss()
        }

        return dialogReasonBinding?.getRoot()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog: Dialog = super.onCreateDialog(savedInstanceState)

        //set the dialog to non-modal and disable dim out fragment behind
        val window: Window? = dialog.getWindow()
        window?.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
        )
        val windowParams = window!!.attributes
        windowParams.dimAmount = 0.80f
        windowParams.flags = windowParams.flags or WindowManager.LayoutParams.FLAG_DIM_BEHIND
        window!!.attributes = windowParams
        return dialog
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window!!.setLayout(width, height)
        }
    }
}