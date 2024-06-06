package com.morningstar.snapshots

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.morningstar.snapshots.databinding.FragmentHomeBinding
import com.morningstar.snapshots.databinding.ItemSnapshotBinding


class HomeFragment : Fragment() {

    private lateinit var mBinding: FragmentHomeBinding
    private lateinit var mFireBaseAdapter: FirebaseRecyclerAdapter<Snapshot, Snapsholder>
    private lateinit var mLayoutManager: RecyclerView.LayoutManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentHomeBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val query = FirebaseDatabase.getInstance().reference.child("snapshots")
        val options = FirebaseRecyclerOptions.Builder<Snapshot>()
            .setQuery(query, Snapshot::class.java).build()

        mFireBaseAdapter = object : FirebaseRecyclerAdapter<Snapshot, Snapsholder>(options){

            private lateinit var mContext: Context

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Snapsholder {
                mContext = parent.context

                val view = LayoutInflater.from(mContext)
                    .inflate(R.layout.item_snapshot, parent, false)

                return Snapsholder(view)
            }

            override fun onBindViewHolder(holder: Snapsholder, position: Int, model: Snapshot) {
                val snapshot = getItem(position)

                with(holder){
                    setListener(snapshot)

                    bindig.tvTittle.text = snapshot.title
                    Glide.with(mContext)
                        .load(snapshot.photoUrl)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .centerCrop()
                        .into(bindig.imgPhoto)
                }
            }

            @SuppressLint("NotifyDataSetChanged")//Corrige error interno firebase ui 8.0.0
            override fun onDataChanged() {
                super.onDataChanged()
                mBinding.progressBar.visibility = View.GONE
                notifyDataSetChanged()
            }

            override fun onError(error: DatabaseError) {
                super.onError(error)
                Toast.makeText(mContext, error.message, Toast.LENGTH_SHORT).show()
            }
        }

        mLayoutManager = LinearLayoutManager(context)

        mBinding.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = mLayoutManager
            adapter = mFireBaseAdapter
        }
    }

    override fun onStart() {
        super.onStart()
        mFireBaseAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        mFireBaseAdapter.stopListening()
    }

    inner class Snapsholder(view: View) : RecyclerView.ViewHolder(view){
        val bindig = ItemSnapshotBinding.bind(view)

        fun setListener(snapshot: Snapshot){

        }
    }
}