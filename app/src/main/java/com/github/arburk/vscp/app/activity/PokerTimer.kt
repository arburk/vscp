package com.github.arburk.vscp.app.activity

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.github.arburk.vscp.app.MainActivity
import com.github.arburk.vscp.app.R
import com.github.arburk.vscp.app.databinding.TimerBinding
import com.github.arburk.vscp.app.service.TimerService

class PokerTimer : Fragment() {

  private var _binding: TimerBinding? = null

  // This property is only valid between onCreateView and onDestroyView.
  private val binding get() = _binding!!

  private val pokerTimerModel by viewModels<PokerTimerModel>()

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
    _binding = TimerBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    val timerService = (activity as MainActivity).getTimerService()

    pokerTimerModel.initData(timerService.getCurrentBlind())
    bindContent(timerService)
    registerButtons(timerService)
  }

  private fun registerButtons(timerService: TimerService) {
    binding.playButton.setOnClickListener {
      switchPlayPauseButtonsVisibility()
      timerService.startTimer()
    }

    binding.pauseButton.setOnClickListener {
      switchPlayPauseButtonsVisibility()
      timerService.pauseTimer()
    }

    binding.prevBlind.setOnClickListener { handleManualBlindChange(timerService, -1) }
    binding.nextBlind.setOnClickListener { handleManualBlindChange(timerService, 1) }

    binding.fab.setOnClickListener { findNavController().navigate(R.id.action_Timer_to_TimerSettings) }
  }

  private fun handleManualBlindChange(timerService: TimerService, stepToMove: Int) {
    timerService.jumpLevel(stepToMove)
    bindContent(timerService)
  }

  private fun bindContent(timerService: TimerService) {
    pokerTimerModel.liveDataBlind.observe(((activity as MainActivity))) {
      Log.v("PokerTimer", "blind change observed")
      binding.smallBlind.text = timerService.getCurrentBlind().small.toString()
      binding.bigBlind.text = timerService.getCurrentBlind().getBigAsString()
    }
  }

  private fun switchPlayPauseButtonsVisibility() {
    // Swap variables
    binding.playButton.visibility = binding.pauseButton.visibility.also {
      binding.pauseButton.visibility = binding.playButton.visibility
    }
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }

}