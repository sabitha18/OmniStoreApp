package com.armada.storeapp.ui.login.forgot_password

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.armada.storeapp.databinding.FragmentVerifyMailBinding

class VerifyEmailFragment : Fragment() {
    private var fragmentVerifyMailBinding: FragmentVerifyMailBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        fragmentVerifyMailBinding =
            FragmentVerifyMailBinding.inflate(inflater, container, false)

        return fragmentVerifyMailBinding?.root
    }
}