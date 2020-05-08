package com.example.wallettracker.utils

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import java.io.InvalidObjectException

interface ViewHolder<E> {
    fun bind(item: E)
}
interface BaseEntity {
    val id : String
    override fun equals(other: Any?) : Boolean
}
class GenericViewHolder<E : BaseEntity>(private val binding: ViewDataBinding)
    : RecyclerView.ViewHolder(binding.root),
    ViewHolder<E> {
    override fun bind(item: E) {
        try {
            binding::class.java.declaredMethods.first { it.name == "setData" }.invoke(binding, item)
        }
        catch (e : NoSuchElementException){
            throw InvalidObjectException(
                "Data binding object ${binding::class.simpleName} used in GenericRecyclerView " +
                "doesn't have a variable of name \"data\".\n" +
                "This variable is what the view holder binds ${item::class.simpleName} into."
            )
        }
    }
}

class GenericDiff<T : BaseEntity> : DiffUtil.ItemCallback<T>() {
    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
        if(oldItem::class.java.isAssignableFrom(newItem::class.java)) {
            return oldItem.id == newItem.id
        }
        return false
    }
    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
        if(oldItem::class.java.isAssignableFrom(newItem::class.java)) {
            return oldItem == newItem
        }
        return false
    }
}

class GenericRecyclerAdapter<E : BaseEntity, VH> : ListAdapter<E, VH>
    where VH : ViewHolder<E>, VH : RecyclerView.ViewHolder {
        private val viewHolderFactory : (ViewGroup, Int) -> VH
        private val itemViewType : (E) -> Int
        constructor(viewHolderFactory: (ViewGroup, Int) -> VH, itemViewType: (E) -> Int) : super(GenericDiff<E>()){
            this.viewHolderFactory = viewHolderFactory
            this.itemViewType = itemViewType
        }
        constructor(viewHolderFactory: (ViewGroup) -> VH) : super(GenericDiff<E>()){
            this.viewHolderFactory = {a, _ -> viewHolderFactory(a)}
            this.itemViewType = { 0 }
        }
        override fun getItemViewType(position: Int): Int {
            return itemViewType(getItem(position))
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = viewHolderFactory(parent, viewType)
        override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(getItem(position))
        companion object{
            fun<E : BaseEntity, B : ViewDataBinding> create(@LayoutRes layout : Int) =
                GenericRecyclerAdapter { parent: ViewGroup ->
                    val binding: B = DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context), layout, parent, false
                    )
                    GenericViewHolder<E>(binding)
                }
        }
}