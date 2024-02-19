package com.armada.storeapp.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.armada.storeapp.databinding.FragmentNotificationsBinding

class NotificationFragment : Fragment() {

    private var fragmentNotificationsBinding: FragmentNotificationsBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        fragmentNotificationsBinding =
            FragmentNotificationsBinding.inflate(inflater, container, false)

        return fragmentNotificationsBinding?.root
    }

}