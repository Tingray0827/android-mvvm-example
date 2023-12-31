package com.example.myapplication.view

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.insertSeparators
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.base.model.data.PagingModel
import com.example.base.model.data.UserData
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.view.custom.DividerItemDecoration
import com.example.myapplication.viewmodel.UserViewModel
import com.example.myapplication.viewmodel.ViewModelFactory

class MainActivity : AppCompatActivity() {

    private lateinit var userViewModel: UserViewModel

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userViewModel = ViewModelProvider(this, ViewModelFactory)[UserViewModel::class.java]
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.rvUser.run {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = UserPagingAdapter()
            addItemDecoration(
                DividerItemDecoration(context, RecyclerView.VERTICAL, isContainFistItem = true).apply {
                    ContextCompat
                        .getDrawable(context, R.drawable.divider_line_black)
                        ?.let { setDrawable(it) }
                }
            )
        }

        userViewModel.run {
            errorLiveData.observe(this@MainActivity) {
                Toast.makeText(
                    this@MainActivity,
                    "Receive error:${it.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }

            userPagingDataLiveData.observe(this@MainActivity) {
                (binding.rvUser.adapter as? UserPagingAdapter)
                    ?.submitData(
                        lifecycle,
                        it.insertSeparators { before: PagingModel?, after: PagingModel? ->
                            return@insertSeparators when {
                                before == null -> PagingModel.PageNumber
                                checkIfNeedAddHeader(before, after) -> PagingModel.PageNumber
                                else -> null
                            }
                        }
                    )
            }

            lifecycleScope.launchWhenStarted {
                queryUser()
            }
        }
    }

    private fun checkIfNeedAddHeader(before: PagingModel?, after: PagingModel?): Boolean =
        before is PagingModel.Content<*> && after is PagingModel.Content<*>
                && before.data is UserData && after.data is UserData
                // Show page number at group's top if in different page.
                && (before.data as UserData).page?.page != (after.data as UserData).page?.page
}