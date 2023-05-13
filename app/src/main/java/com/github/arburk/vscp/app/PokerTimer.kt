package com.github.arburk.vscp.app

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.arburk.vscp.app.databinding.TimerBinding

class PokerTimer : Fragment() {

  private var _binding: TimerBinding? = null

  // This property is only valid between onCreateView and
  // onDestroyView.
  private val binding get() = _binding!!

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {

    _binding = TimerBinding.inflate(inflater, container, false)
    return binding.root

  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    binding.playButton.setOnClickListener {
      Log.i("Action", "play button was pressed")
      pauseTimer(false)
    }

    binding.pauseButton.setOnClickListener {
      Log.i("Action", "play button was pressed")
      pauseTimer(true)
    }
  }

  private fun pauseTimer(input: Boolean) {
    // TODO: implement logic to pause timer as soon as timer is implemented
    if (input) {
      binding.playButton.visibility = View.VISIBLE
      binding.pauseButton.visibility = View.GONE
    } else {
      binding.playButton.visibility = View.GONE
      binding.pauseButton.visibility = View.VISIBLE
    }
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }
}