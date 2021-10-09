package com.example.knurling_dietinbody

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.inbody_list_view.view.*
import java.text.SimpleDateFormat

class InbodyitemAdapter(private val context: Context): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var firestore : FirebaseFirestore? = null
    private var auth : FirebaseAuth

    var weight : Long? = null
    var time : Long? = null
    var muscleMass : Long? = null
    var bodyFatMass : Long? = null
    var bodyFat : Long? = null
    var bmi : Long? = null
    var inbody_itemList: ArrayList<inbody_item> = arrayListOf()

    init {
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        firestore?.collection("InbodyRecords")?.whereEqualTo("uid", auth.currentUser?.uid)//
            ?.get()?.addOnSuccessListener { documents ->
                inbody_itemList.clear()
                for (doc in documents) {
                    weight = (doc?.data?.get("weight").toString()).toLong()
                    time = (doc?.data?.get("date").toString()).toLong()
                    muscleMass = (doc?.data?.get("muscleMass").toString()).toLong()
                    bodyFatMass = (doc?.data?.get("bodyFatMass").toString()).toLong()
                    bodyFat = (doc?.data?.get("bodyFat").toString()).toLong()
                    bmi = (doc?.data?.get("bmi").toString()).toLong()

                    var inbodyItem = inbody_item(weight, time, muscleMass, bodyFatMass, bodyFat, bmi)
                    inbody_itemList.add(inbodyItem!!)
                }
                notifyDataSetChanged()//새로고침
            }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.inbody_list_view, parent, false)
        return ViewHolder(view)
    }


    override fun getItemCount(): Int {
        return inbody_itemList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var viewHolder = (holder as ViewHolder).itemView
        viewHolder.inbody_textView2.text = SimpleDateFormat("yyyy.MM.dd").format(inbody_itemList[position].time)
        holder.bind(inbody_itemList[position])

    }
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(Item : inbody_item){
            itemView.setOnClickListener {
                Intent(context,InBodyViewActivity::class.java).apply {
                    putExtra("date", Item.time.toString()) //리사이클러 뷰에선 인바디의 날짜만 보이게끔.
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }.run { context.startActivity(this) }
            }
        }
    }

}