package com.armada.storeapp.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.armada.storeapp.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private var fragmentProfileBinding: FragmentProfileBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        fragmentProfileBinding =
            FragmentProfileBinding.inflate(inflater, container, false)

        return fragmentProfileBinding?.root
    }

}