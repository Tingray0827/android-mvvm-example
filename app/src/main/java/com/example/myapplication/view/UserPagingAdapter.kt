package com.example.myapplication.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.base.model.data.UserData
import com.example.base.model.data.PagingModel
import com.example.myapplication.databinding.ItemUserContentBinding
import com.example.myapplication.databinding.ItemUserHeaderBinding

class UserPagingAdapter :
    PagingDataAdapter<PagingModel, RecyclerView.ViewHolder>(COMPARATOR) {
    companion object {
        private val COMPARATOR =
            object : DiffUtil.ItemCallback<PagingModel>() {
                override fun areItemsTheSame(
                    oldItem: PagingModel,
                    newItem: PagingModel
                ): Boolean {
                    return when {
                        oldItem is PagingModel.Content<*> && newItem is PagingModel.Content<*> -> {
                            if (oldItem.data is UserData && newItem.data is UserData) {
                                (oldItem.data as UserData).id == (newItem.data as UserData).id
                            } else false
                        }
                        oldItem is PagingModel.PageNumber && newItem is PagingModel.PageNumber -> {
                            oldItem == newItem
                        }
                        else -> false
                    }
                }

                override fun areContentsTheSame(
                    oldItem: PagingModel,
                    newItem: PagingModel
                ): Boolean {
                    return oldItem == newItem
                }
            }

        private enum class ItemViewType(val value: Int) {
            HEADER(1), CONTENT(2)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is PagingModel.PageNumber -> ItemViewType.HEADER.value
            is PagingModel.Content<*> -> ItemViewType.CONTENT.value
            else -> super.getItemViewType(position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ItemViewType.HEADER.value -> {
                UserHeaderViewHolder(
                    ItemUserHeaderBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            ItemViewType.CONTENT.value -> {
                UserContentViewHolder(
                    ItemUserContentBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            else -> object : RecyclerView.ViewHolder(View(parent.context)) {}
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is UserHeaderViewHolder -> {
                // header doesn't have content data
                val nextData = (getItem(position + 1) as PagingModel.Content<*>).data as UserData
                holder.binding.tvHeader.text = StringBuilder("page").append(nextData.page?.page)
            }
            is UserContentViewHolder -> {
                val data = (getItem(position) as PagingModel.Content<*>).data as UserData
                holder.updateUserData(data)
            }
        }
    }

    class UserHeaderViewHolder(val binding: ItemUserHeaderBinding) : RecyclerView.ViewHolder(binding.root)

    class UserContentViewHolder(private val binding: ItemUserContentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun updateUserData(userData: UserData) {
            binding.userData = userData
            binding.executePendingBindings()
        }
    }
}