package com.armada.storeapp.ui.login.forgot_password

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.armada.storeapp.databinding.FragmentForgotPasswordBinding

class ForgotPasswordFragment : Fragment() {

    private var fragmentForgotPasswordBinding: FragmentForgotPasswordBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        fragmentForgotPasswordBinding =
            FragmentForgotPasswordBinding.inflate(inflater, container, false)

        return fragmentForgotPasswordBinding?.root
    }
}