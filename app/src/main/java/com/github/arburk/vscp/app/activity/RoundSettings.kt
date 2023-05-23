package com.github.arburk.vscp.app.activity

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.arburk.vscp.app.R

class RoundSettings : Fragment() {

  companion object {
    fun newInstance() = RoundSettings()
  }

  private lateinit var viewModel: RoundSettingsViewModel

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.fragment_round_settings, container, false)
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
    viewModel = ViewModelProvider(this).get(RoundSettingsViewModel::class.java)
    // TODO: Use the ViewModel
  }

}