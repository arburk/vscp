package com.github.arburk.vscp.app.activity

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.github.arburk.vscp.app.R

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
      context.getString(R.string.round_id, position + 1)

    val currentBlind = getItem(position).rounds.value!!
    linearLayoutView.findViewById<TextView>(R.id.small_big).text =
      context.getString(R.string.small_big_blind_values, currentBlind.small, currentBlind.big)

    return linearLayoutView
  }


}