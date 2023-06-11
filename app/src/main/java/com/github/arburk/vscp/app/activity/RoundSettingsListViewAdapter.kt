package com.github.arburk.vscp.app.activity

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.LiveData
import com.github.arburk.vscp.app.MainActivity
import com.github.arburk.vscp.app.R
import com.github.arburk.vscp.app.model.Blind
import com.github.arburk.vscp.app.service.TimerService
import kotlin.math.ceil

class RoundSettingsListViewAdapter(

  private val context: Context,
  private val arrayList: List<PokerTimerViewModel>
) : BaseAdapter() {

  val timerService: TimerService = (context as MainActivity).getTimerService()

  override fun getCount(): Int {
    return arrayList.size
  }

  override fun getItem(position: Int): PokerTimerViewModel {
    return arrayList[position]
  }

  override fun getItemId(position: Int): Long {
    return position.toLong()
  }

  override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
    var linearLayoutView = convertView
    val currentBlind = getItem(position)

    if (convertView == null) {
      linearLayoutView = LayoutInflater.from(context)
        .inflate(R.layout.round_settings_row, parent, false)

      val smallBlindTextView = linearLayoutView.findViewById<EditText>(R.id.small_blind)
      // add initial text before addTextChangedListener to prevent obsolete events fired
      smallBlindTextView.setText(currentBlind.blind.value!!.small.toString())
      // addTextChangedListener only once after initial inflating. Otherwise, multiple instances are created/registered
      smallBlindTextView.addTextChangedListener(BlindTextWatcher(currentBlind.blind))

      linearLayoutView.findViewById<ImageButton>(R.id.derease_button)
        .setOnClickListener { decreaseBlind(currentBlind.blind) }
      linearLayoutView.findViewById<ImageButton>(R.id.inrease_button)
        .setOnClickListener { increaseBlind(currentBlind.blind) }
    }

    linearLayoutView!!.findViewById<TextView>(R.id.round_id).text =
      context.getString(R.string.round_id, formattedNumberOfCurrentRound(position))

    linearLayoutView.findViewById<TextView>(R.id.big_blind).text = currentBlind.blind.value!!.getBigAsString()

    return linearLayoutView
  }

  private fun increaseBlind(currentBlind: LiveData<Blind>) {
    // TODO("Not yet implemented")
    Log.v("RoundSettingsListViewAdapter", "TODO: increaseBlind for $currentBlind")

  }

  private fun decreaseBlind(currentBlind: LiveData<Blind>) {
    // TODO("Not yet implemented")
    Log.v("RoundSettingsListViewAdapter", "TODO: decreaseBlind for $currentBlind")
  }

  private fun formattedNumberOfCurrentRound(position: Int) =
    (position + 1).toString().padStart(calculatePadLength(), '0')

  /**
   * dynamically get the number of leading zeros based on configured blind rounds
   */
  private fun calculatePadLength() = ceil(count.toDouble() / 10).toInt() + 1


  inner class BlindTextWatcher(currentBlind: LiveData<Blind>) : TextWatcher {

    private var blind: Blind
    private var oldSmallBlindAsText: String

    init {
      blind = currentBlind.value!!
      oldSmallBlindAsText = blind.small.toString()
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
      if (s != null) {
        oldSmallBlindAsText = s.toString()
      }
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
      Log.v("RoundSettingsListViewAdapter", "TODO: onTextChanged $s for $blind")
      if (s != null && s.toString().isNotBlank()) {
        val oldSmallAsInt: Int = oldSmallBlindAsText.let {
          if (it.isEmpty()) 0 else oldSmallBlindAsText.toInt()
        }
        try {
          timerService.updateBlind(oldSmallAsInt, s.toString().toInt())
        } catch (e: NumberFormatException) {
          Log.e("RoundSettingsListViewAdapter", "Failed to assign value '$s'", e)
          timerService.updateBlind(oldSmallAsInt, oldSmallAsInt)
        }
      }
    }

    override fun afterTextChanged(s: Editable?) {
      (context as MainActivity).findViewById<ListView?>(R.id.rounds_row_list_view).adapter =
        RoundSettingsListViewAdapter(context, timerService.getRoundsAsPokerTimerModel())
    }
  }
}