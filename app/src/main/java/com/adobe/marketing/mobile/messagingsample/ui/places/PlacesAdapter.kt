package com.adobe.marketing.mobile.messagingsample.ui.places

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.adobe.marketing.mobile.messagingsample.R

class PlacesAdapter(
    private val onPoiClick: (PlaceItem) -> Unit = {}
) : RecyclerView.Adapter<PlacesAdapter.PlaceViewHolder>() {

    private val items = mutableListOf<PlaceItem>()

    fun submitList(newItems: List<PlaceItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_place, parent, false)
        return PlaceViewHolder(view, onPoiClick)
    }

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    class PlaceViewHolder(
        view: View,
        private val onPoiClick: (PlaceItem) -> Unit
    ) : RecyclerView.ViewHolder(view) {

        private val txtName: TextView = view.findViewById(R.id.txtPlaceName)
        private val txtId: TextView = view.findViewById(R.id.txtPlaceId)
        private val txtStatus: TextView = view.findViewById(R.id.txtPlaceStatus)

        fun bind(item: PlaceItem) {
            txtName.text = item.name
            txtId.text = item.identifier
            txtStatus.text = if (item.inside) "Estado: Dentro ✓" else "Estado: Cercano"
            
            itemView.setOnClickListener {
                onPoiClick(item)
            }
        }
    }
}
