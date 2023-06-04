package com.github.arburk.vscp.app.activity

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.EditText
import android.widget.TextView
import com.github.arburk.vscp.app.R
import kotlin.math.ceil

class RoundSettingsListViewAdapter(

  private val context: Context,
  private val arrayList: List<RoundSettingsViewModel>
) : BaseAdapter() {
  override fun getCount(): Int {
    return arrayList.size
  }

  override fun getItem(position: Int): RoundSettingsViewModel {
    return arrayList[position]
  }

  override fun getItemId(position: Int): Long {
    return position.toLong()
  }

  override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
    var linearLayoutView = convertView
    if (convertView == null) {
      linearLayoutView = LayoutInflater.from(context)
        .inflate(R.layout.round_settings_row, parent, false)
    }

    linearLayoutView!!.findViewById<TextView>(R.id.round_id).text =
      context.getString(R.string.round_id, formattedNumberOfCurrentRound(position))

    val currentBlind = getItem(position).rounds.value!!
    linearLayoutView.findViewById<EditText>(R.id.small_blind).setText(currentBlind.small.toString())
    linearLayoutView.findViewById<TextView>(R.id.big_blind).text = currentBlind.big.toString()

    return linearLayoutView
  }

  private fun formattedNumberOfCurrentRound(position: Int) =
    (position + 1).toString().padStart(calculatePadLength(), '0')

  /**
   * dynamically get the number of leading zeros based on configured blind rounds
   */
  private fun calculatePadLength() = ceil(count.toDouble() / 10).toInt() + 1


}