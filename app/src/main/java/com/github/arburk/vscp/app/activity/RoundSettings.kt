package com.github.arburk.vscp.app.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.github.arburk.vscp.app.MainActivity
import com.github.arburk.vscp.app.R
import com.github.arburk.vscp.app.databinding.FragmentRoundSettingsBinding
import com.github.arburk.vscp.app.service.TimerService
import java.util.*

class RoundSettings : Fragment() {

  private var _binding: FragmentRoundSettingsBinding? = null
  private val binding get() = _binding!!
  private lateinit var timerService: TimerService
  private var arrayList: List<RoundSettingsViewModel> = Collections.emptyList()

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
    _binding = FragmentRoundSettingsBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    timerService = (activity as MainActivity).getTimerService()
    arrayList = timerService.getRounds().map { elem -> RoundSettingsViewModel().apply { initData(elem) } }
    initLayout()
  }

  private fun initLayout() {
    val listViewOfRows = activity?.findViewById<ListView>(R.id.rounds_row_list_view)
    listViewOfRows?.adapter = RoundSettingsListViewAdapter(requireContext(), arrayList)
  }

}