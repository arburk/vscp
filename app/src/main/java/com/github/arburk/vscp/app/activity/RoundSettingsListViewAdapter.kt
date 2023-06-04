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
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import com.github.arburk.vscp.app.R
import com.github.arburk.vscp.app.model.Blind
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
    val smallBlindEditText = linearLayoutView.findViewById<EditText>(R.id.small_blind)
    smallBlindEditText.addTextChangedListener { BlindTextWatcher(currentBlind) }
    smallBlindEditText.setText(currentBlind.small.toString())

    linearLayoutView.findViewById<TextView>(R.id.big_blind).text = currentBlind.big.toString()

    linearLayoutView.findViewById<ImageButton>(R.id.derease_button).setOnClickListener { decreaseBlind(currentBlind) }
    linearLayoutView.findViewById<ImageButton>(R.id.inrease_button).setOnClickListener { increaseBlind(currentBlind) }

    return linearLayoutView
  }

  private fun increaseBlind(currentBlind: Blind) {
    // TODO("Not yet implemented")
    Log.v("RoundSettingsListViewAdapter", "TODO: increaseBlind for $currentBlind")
  }

  private fun decreaseBlind(currentBlind: Blind) {
    // TODO("Not yet implemented")
    Log.v("RoundSettingsListViewAdapter", "TODO: decreaseBlind for $currentBlind")
  }

  private fun formattedNumberOfCurrentRound(position: Int) =
    (position + 1).toString().padStart(calculatePadLength(), '0')

  /**
   * dynamically get the number of leading zeros based on configured blind rounds
   */
  private fun calculatePadLength() = ceil(count.toDouble() / 10).toInt() + 1


  inner class BlindTextWatcher(currentBlind: Blind) : TextWatcher {

    private var blind: Blind

    init {
      blind = currentBlind
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
      Log.v("RoundSettingsListViewAdapter", "TODO: beforeTextChanged $blind")
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
      Log.v("RoundSettingsListViewAdapter", "TODO: onTextChanged $blind")
    }

    override fun afterTextChanged(s: Editable?) {
      Log.v("RoundSettingsListViewAdapter", "TODO: afterTextChanged $blind")
    }
  }
}