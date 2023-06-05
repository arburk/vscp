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
    val currentBlind = getItem(position).rounds.value!!

    if (convertView == null) {
      linearLayoutView = LayoutInflater.from(context)
        .inflate(R.layout.round_settings_row, parent, false)

      val findViewById = linearLayoutView.findViewById<EditText>(R.id.small_blind)
      // add initial text before addTextChangedListener to prevent obsolete events fired
      findViewById.setText(currentBlind.small.toString())
      // addTextChangedListener only once after initial inflating. Otherwise multiple instances are created/registered
      findViewById.addTextChangedListener(BlindTextWatcher(currentBlind))

      linearLayoutView.findViewById<ImageButton>(R.id.derease_button).setOnClickListener { decreaseBlind(currentBlind) }
      linearLayoutView.findViewById<ImageButton>(R.id.inrease_button).setOnClickListener { increaseBlind(currentBlind) }
    }

    linearLayoutView!!.findViewById<TextView>(R.id.round_id).text =
      context.getString(R.string.round_id, formattedNumberOfCurrentRound(position))

    linearLayoutView.findViewById<TextView>(R.id.big_blind).text = currentBlind.getBigAsString()

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
    private var oldText: String

    init {
      blind = currentBlind
      oldText = blind.small.toString()
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
      if (s != null) {
        oldText = s.toString()
      }
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
      Log.v("RoundSettingsListViewAdapter", "TODO: onTextChanged $s for $blind")
      if (s != null && s.toString().isNotBlank()) {
        try {
          blind.small = s.toString().toInt()
        } catch (e: Exception) {
          Log.e("RoundSettingsListViewAdapter", "Failed to assign value '$s'", e)
          blind.small = oldText.toInt()
        }
      }
    }

    override fun afterTextChanged(s: Editable?) {
      // TODO : update view
    }
  }
}