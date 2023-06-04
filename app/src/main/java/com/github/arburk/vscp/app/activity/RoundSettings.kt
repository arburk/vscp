package com.github.arburk.vscp.app.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.github.arburk.vscp.app.MainActivity
import com.github.arburk.vscp.app.R
import com.github.arburk.vscp.app.databinding.FragmentRoundSettingsBinding
import com.github.arburk.vscp.app.model.Blind
import com.github.arburk.vscp.app.service.TimerService

class RoundSettings : Fragment() {

  private var _binding: FragmentRoundSettingsBinding? = null
  private val binding get() = _binding!!
  private lateinit var timerService: TimerService

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
    _binding = FragmentRoundSettingsBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    timerService = (activity as MainActivity).getTimerService()
    initLayout()
  }

  private fun initLayout() {
    val data = timerService.getRounds().map { elem -> RoundSettingsViewModel().apply { initData(elem) } }
    val listViewOfRows = activity?.findViewById<ListView>(R.id.rounds_row_list_view)
    listViewOfRows?.adapter = RoundSettingsListViewAdapter(requireContext(), data)

    activity?.findViewById<ImageButton>(R.id.add_blind_button)?.setOnClickListener { addBlind() }
    activity?.findViewById<ImageButton>(R.id.remove_last_blind_button)?.setOnClickListener { removeBlind() }
  }

  private fun addBlind() {
    var newRound = Blind(1, 2)
    timerService.getRounds().also {
      if (it.isNotEmpty()) {
        val lastRound = it[it.size - 1]
        newRound = Blind(lastRound.big, lastRound.big * 2)
      }
      timerService.setRounds(it.plus(newRound))
    }
    initLayout()
  }

  private fun removeBlind() {
    timerService.getRounds().also {
      if (it.isNotEmpty()) {
        timerService.setRounds(it.dropLast(1).toTypedArray())
        initLayout()
      }
    }
  }
}