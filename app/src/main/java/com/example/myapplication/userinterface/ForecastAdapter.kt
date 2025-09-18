package com.example.myapplication.userinterface


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.data.DailyForecastSummary
import com.squareup.picasso.Picasso

class ForecastAdapter(
    private var items: List<DailyForecastSummary>
) : RecyclerView.Adapter<ForecastAdapter.ForecastViewHolder>() {

    class ForecastViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDate: TextView = view.findViewById(R.id.tvDate)
        val tvDescription: TextView = view.findViewById(R.id.tvDescription)
        val tvTemp: TextView = view.findViewById(R.id.tvTemp)
        val ivWeatherIcon: ImageView = view.findViewById(R.id.ivWeatherIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForecastViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_forecast, parent, false)
        return ForecastViewHolder(view)
    }

    override fun onBindViewHolder(holder: ForecastViewHolder, position: Int) {
        val item = items[position]
        holder.tvDate.text = item.date
        holder.tvDescription.text = item.description
        holder.tvTemp.text = "${item.avgTemp.toInt()}°C"

        // Load icon from OpenWeather
        val iconUrl = "https://openweathermap.org/img/wn/${item.icon}@2x.png"
        Picasso.get().load(iconUrl).into(holder.ivWeatherIcon)
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<DailyForecastSummary>) {
        items = newItems
        notifyDataSetChanged()
    }
}
