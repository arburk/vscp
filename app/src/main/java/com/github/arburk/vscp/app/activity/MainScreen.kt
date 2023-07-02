package com.github.arburk.vscp.app.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.github.arburk.vscp.app.R
import com.github.arburk.vscp.app.databinding.MainScreenBinding

class MainScreen : Fragment() {

  private var _binding: MainScreenBinding? = null

  // This property is only valid between onCreateView and
  // onDestroyView.
  private val binding get() = _binding!!

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View {
    _binding = MainScreenBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    binding.pokerTimer.setOnClickListener {
      findNavController().navigate(R.id.action_MainScreen_to_Timer)
    }
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }
}