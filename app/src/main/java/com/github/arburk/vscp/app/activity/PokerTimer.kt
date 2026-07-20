package com.github.arburk.vscp.app.activity

import android.Manifest
import android.os.Build
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
import com.github.arburk.vscp.app.model.Blind
import com.github.arburk.vscp.app.service.NotificationManagerWrapper
import com.github.arburk.vscp.app.service.TimerService

class PokerTimer : Fragment() {

  private var _binding: TimerBinding? = null

  // This property is only valid between onCreateView and onDestroyView.
  private val binding get() = _binding!!

  private val pokerTimerViewModel by viewModels<PokerTimerViewModel>()
  private var timerService: TimerService? = null

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
    _binding = TimerBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    Log.v("PokerTimer", "onViewCreated")

    val mainActivity = requireActivity() as MainActivity
    val service = mainActivity.timerService
    if (service != null) {
      setupWithService(service)
    } else {
      mainActivity.timerServiceLiveData.observe(viewLifecycleOwner) { svc ->
        if (svc != null && timerService == null) {
          setupWithService(svc)
        }
      }
    }
  }

  private fun setupWithService(service: TimerService) {
    timerService = service
    launchPermissionActivity()
    service.registerViewModel(pokerTimerViewModel)
    registerUiElements()
  }

  private fun launchPermissionActivity() {
    NotificationManagerWrapper().also { nmw ->
      (requireActivity() as MainActivity).also { activity ->
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && nmw.isLackOfNotificationPermission(activity)) {
          nmw.createNotificationChannel(activity)
          activity.permissionActivity.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
      }
    }
  }

  private fun registerUiElements() {
    if (timerService?.isRunning() == true) {
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

    pokerTimerViewModel.blind.observe(viewLifecycleOwner) { newBlind: Blind ->
      binding.smallBlind.text = newBlind.small.toString()
      binding.bigBlind.text = newBlind.getBigAsString()
    }
    pokerTimerViewModel.remainingTime.observe(viewLifecycleOwner) { remainingTime: String ->
      binding.timeLeft.text = remainingTime
    }

    binding.fab.setOnClickListener { findNavController().navigate(R.id.action_Timer_to_TimerSettings) }
  }

  private fun switchPlayPauseButtonsVisibility() {
    binding.playButton.visibility = binding.pauseButton.visibility.also {
      binding.pauseButton.visibility = binding.playButton.visibility
    }
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
    timerService?.unregisterViewModel(pokerTimerViewModel)
  }
}
