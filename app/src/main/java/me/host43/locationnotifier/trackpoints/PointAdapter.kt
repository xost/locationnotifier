package me.host43.locationnotifier.trackpoints

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import me.host43.locationnotifier.database.Point
import me.host43.locationnotifier.databinding.ItemPointLayoutBinding

class PointAdapter : ListAdapter<Point,PointAdapter.ViewHolder>(PointsDiffCallback()) {
    class ViewHolder private constructor (private val b: ItemPointLayoutBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(item: Point){
            b.point=item
            b.executePendingBindings()
        }
        companion object{
            fun from(parent: ViewGroup): ViewHolder{
                val layoutInflater = LayoutInflater.from(parent.context)
                val b = ItemPointLayoutBinding.inflate(layoutInflater,parent,false)
                return ViewHolder(b)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }
}

class PointsDiffCallback : DiffUtil.ItemCallback<Point>() {
    override fun areItemsTheSame(oldItem: Point, newItem: Point): Boolean {
        return newItem.pointId == oldItem.pointId
    }

    override fun areContentsTheSame(oldItem: Point, newItem: Point): Boolean {
        return newItem == oldItem
    }

}