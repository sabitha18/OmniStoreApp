package com.armada.storeapp.ui.login.signup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.armada.storeapp.databinding.FragmentSignupBinding

class SignupFragment : Fragment() {

    private var fragmentSignupBinding: FragmentSignupBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        fragmentSignupBinding =
            FragmentSignupBinding.inflate(inflater, container, false)

        return fragmentSignupBinding?.root
    }

}
