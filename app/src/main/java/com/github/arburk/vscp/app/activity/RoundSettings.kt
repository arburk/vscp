package com.github.arburk.vscp.app.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ListView
import androidx.annotation.VisibleForTesting
import androidx.fragment.app.Fragment
import com.github.arburk.vscp.app.MainActivity
import com.github.arburk.vscp.app.R
import com.github.arburk.vscp.app.databinding.FragmentRoundSettingsBinding
import com.github.arburk.vscp.app.model.Blind
import com.github.arburk.vscp.app.service.TimerService

class RoundSettings : Fragment() {

  private var _binding: FragmentRoundSettingsBinding? = null
  private val binding get() = _binding!!

  @VisibleForTesting
  internal var timerService: TimerService? = null

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
    _binding = FragmentRoundSettingsBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    val mainActivity = requireActivity() as MainActivity
    val service = mainActivity.timerService
    if (service != null) {
      timerService = service
      initLayout()
    } else {
      mainActivity.timerServiceLiveData.observe(viewLifecycleOwner) { svc ->
        if (svc != null && timerService == null) {
          timerService = svc
          initLayout()
        }
      }
    }
  }

  private fun initLayout() {
    val service = timerService ?: return
    requireActivity().findViewById<ListView?>(R.id.rounds_row_list_view)?.adapter =
      RoundSettingsListViewAdapter(requireContext(), service)

    requireActivity().findViewById<ImageButton>(R.id.add_blind_button)?.setOnClickListener { addBlind() }
    requireActivity().findViewById<ImageButton>(R.id.remove_last_blind_button)?.setOnClickListener { removeBlind() }
  }

  private fun addBlind() {
    val service = timerService ?: return
    val rounds = service.getRounds()
    val newRound = if (rounds.isNotEmpty()) Blind(rounds.last().getBig()) else Blind(1)
    service.setRounds(rounds + newRound)
    initLayout()
  }

  private fun removeBlind() {
    val service = timerService ?: return
    val rounds = service.getRounds()
    if (rounds.isNotEmpty()) {
      service.setRounds(rounds.dropLast(1))
      initLayout()
    }
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }
}
