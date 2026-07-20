package com.github.arburk.vscp.app.activity

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ListView
import android.widget.TextView
import com.github.arburk.vscp.app.MainActivity
import com.github.arburk.vscp.app.R
import com.github.arburk.vscp.app.model.Blind
import com.github.arburk.vscp.app.service.TimerService
import kotlin.math.ceil

class RoundSettingsListViewAdapter(
  private val context: Context,
  private val timerService: TimerService
) : BaseAdapter() {

  private val rounds: List<Blind> = timerService.getRounds().toList()

  override fun getCount(): Int = rounds.size

  override fun getItem(position: Int): Blind = rounds[position]

  override fun getItemId(position: Int): Long = position.toLong()

  override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
    val blind = getItem(position)

    val rowView = if (convertView == null) {
      LayoutInflater.from(context).inflate(R.layout.round_settings_row, parent, false).also { view ->
        val smallBlindEditText = view.findViewById<EditText>(R.id.small_blind)
        smallBlindEditText.setText(blind.small.toString())
        smallBlindEditText.addTextChangedListener(BlindTextWatcher(position))

        view.findViewById<ImageButton>(R.id.derease_button)
          .setOnClickListener { changeBlind(position, -getIncreaseStep(blind.small)) }
        view.findViewById<ImageButton>(R.id.inrease_button)
          .setOnClickListener { changeBlind(position, +getIncreaseStep(blind.small)) }
      }
    } else {
      convertView
    }

    rowView.findViewById<TextView>(R.id.round_id).text =
      context.getString(R.string.round_id, formattedNumberOfCurrentRound(position))
    rowView.findViewById<TextView>(R.id.big_blind).text = blind.getBigAsString()

    return rowView
  }

  private fun changeBlind(position: Int, delta: Int) {
    val currentSmall = timerService.getRounds().getOrNull(position)?.small ?: return
    timerService.updateBlind(position, maxOf(1, currentSmall + delta))
    refreshAdapter()
  }

  private fun getIncreaseStep(small: Int): Int = when {
    small < 50 -> 1
    small < 100 -> 5
    small < 500 -> 10
    small < 1000 -> 50
    else -> 100
  }

  private fun formattedNumberOfCurrentRound(position: Int) =
    (position + 1).toString().padStart(calculatePadLength(), '0')

  private fun calculatePadLength() = ceil(count.toDouble() / 10).toInt() + 1

  private fun refreshAdapter() {
    (context as? MainActivity)?.findViewById<ListView?>(R.id.rounds_row_list_view)?.adapter =
      RoundSettingsListViewAdapter(context, timerService)
  }

  inner class BlindTextWatcher(private val position: Int) : TextWatcher {

    private var previousText: String = timerService.getRounds().getOrNull(position)?.small?.toString() ?: "0"

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
      if (s != null) previousText = s.toString()
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
      if (s.isNullOrBlank()) return
      val newValue = s.toString().toIntOrNull()
      val fallback = previousText.toIntOrNull() ?: 0
      Log.v("RoundSettingsListViewAdapter", "onTextChanged pos=$position value=$newValue")
      timerService.updateBlind(position, newValue ?: fallback)
    }

    override fun afterTextChanged(s: Editable?) {
      // Defer adapter refresh to next UI frame to break the TextWatcher → setAdapter recursion
      (context as? MainActivity)?.window?.decorView?.post { refreshAdapter() }
    }
  }
}
