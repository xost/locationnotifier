package me.host43.locationnotifier.trackpoints

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import me.host43.locationnotifier.database.Point
import me.host43.locationnotifier.databinding.ItemPointLayoutBinding
import timber.log.Timber

class PointAdapter(
    private val clickListener: PointItemListener,
    private val switchClickListener: PointItemSwitchListener
) :
    ListAdapter<Point, PointAdapter.ViewHolder>(PointsDiffCallback()) {
    class ViewHolder private constructor(private val b: ItemPointLayoutBinding) :
        RecyclerView.ViewHolder(b.root) {
        fun bind(
            item: Point,
            clickListener: PointItemListener,
            switchClickListener: PointItemSwitchListener
        ) {
            b.point = item
            b.clickListener = clickListener
            b.switchClickListener = switchClickListener
            b.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val b = ItemPointLayoutBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(b)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)!!
        holder.bind(item, clickListener, switchClickListener)
    }
}

class PointsDiffCallback : DiffUtil.ItemCallback<Point>() {
    override fun areItemsTheSame(oldItem: Point, newItem: Point): Boolean {
        return newItem.pointId == oldItem.pointId
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: Point, newItem: Point): Boolean {
        return newItem == oldItem
    }

}

class PointItemListener(val clickListener: (pointId: Long) -> Unit) {
    fun onClick(point: Point) = clickListener(point.pointId)
}

class PointItemSwitchListener(val clickListener: (pointId: Long) -> Unit) {
    fun onClick(point: Point) = clickListener(point.pointId)
}