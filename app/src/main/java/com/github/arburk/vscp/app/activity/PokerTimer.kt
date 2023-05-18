package com.github.arburk.vscp.app.activity

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.arburk.vscp.app.MainActivity
import com.github.arburk.vscp.app.databinding.TimerBinding

class PokerTimer : Fragment() {

  private var _binding: TimerBinding? = null

  // This property is only valid between onCreateView and onDestroyView.
  private val binding get() = _binding!!

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
    _binding = TimerBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    val timerService = (activity as MainActivity).getTimerService()

    binding.playButton.setOnClickListener {
      Log.i("Action", "play button was pressed")
      switchPlayPauseButtonsVisibility()
      timerService.startTimer()
    }

    binding.pauseButton.setOnClickListener {
      Log.i("Action", "pause button was pressed")
      switchPlayPauseButtonsVisibility()
      timerService.pauseTimer()
    }
  }

  private fun switchPlayPauseButtonsVisibility() {
    val playBtnVisibility = binding.playButton.visibility
    binding.playButton.visibility = binding.pauseButton.visibility
    binding.pauseButton.visibility = playBtnVisibility
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }

}