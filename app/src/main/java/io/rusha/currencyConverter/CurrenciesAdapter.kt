package io.rusha.currencyConverter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CurrenciesAdapter(
    private val values: List<String>,
    private val onItemClickListener: (String) -> Unit,
    private val current: String
) : RecyclerView.Adapter<CurrenciesAdapter.ViewHolder>() {

    override fun getItemCount() = values.size

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item_view, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currency = values[position]
        holder.itemView.setOnClickListener{onItemClickListener(currency)}
        holder.textView.text = currency
        if (currency == current) {
            holder.radioButton.isChecked = true
        } else {
            holder.radioButton.isChecked = false
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.textItemView)
        val radioButton: RadioButton = itemView.findViewById(R.id.radioButton)
    }
}