package com.github.arburk.vscp.app.activity

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.github.arburk.vscp.app.MainActivity
import com.github.arburk.vscp.app.R
import com.github.arburk.vscp.app.databinding.TimerBinding
import com.github.arburk.vscp.app.model.Blind
import com.github.arburk.vscp.app.service.TimerService

class PokerTimer : Fragment() {

  private var _binding: TimerBinding? = null

  // This property is only valid between onCreateView and onDestroyView.
  private val binding get() = _binding!!

  private val pokerTimerViewModel by viewModels<PokerTimerViewModel>()
  private var timerService: TimerService? = null

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
    _binding = TimerBinding.inflate(inflater, container, false)
    timerService = (activity as MainActivity).timerService
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    Log.v("PokerTimer", "onViewCreated")
    super.onViewCreated(view, savedInstanceState)
    timerService?.registerViewModel(pokerTimerViewModel)
    registerUiElements()
  }

  private fun registerUiElements() {
    if (timerService?.isRunning()!!) {
      switchPlayPauseButtonsVisibility()
    }

    binding.playButton.setOnClickListener {
      switchPlayPauseButtonsVisibility()
      timerService?.startTimer()
    }

    binding.pauseButton.setOnClickListener {
      switchPlayPauseButtonsVisibility()
      timerService?.pauseTimer()
    }

    binding.prevBlind.setOnClickListener { timerService?.jumpLevel(-1) }
    binding.nextBlind.setOnClickListener { timerService?.jumpLevel(1) }

    pokerTimerViewModel.blind.observe(activity as MainActivity, blindObserver)
    pokerTimerViewModel.remainingTime.observe(activity as MainActivity, remainingTimeObserver)

    binding.fab.setOnClickListener { findNavController().navigate(R.id.action_Timer_to_TimerSettings) }
  }

  private val blindObserver = Observer<Blind> { newBlind ->
    binding.smallBlind.text = newBlind.small.toString()
    binding.bigBlind.text = newBlind.getBigAsString()
  }

  private val remainingTimeObserver = Observer<String> { remainingtime -> binding.timeLeft.text = remainingtime }

  private fun switchPlayPauseButtonsVisibility() {
    // Swap variables
    binding.playButton.visibility = binding.pauseButton.visibility.also {
      binding.pauseButton.visibility = binding.playButton.visibility
    }
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
    pokerTimerViewModel.blind.removeObserver(blindObserver)
    pokerTimerViewModel.remainingTime.removeObserver(remainingTimeObserver)
    timerService?.unregisterViewModel(pokerTimerViewModel)
  }

}