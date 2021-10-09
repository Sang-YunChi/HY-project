package com.example.knurling_dietinbody

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_diary_list.*
import kotlinx.android.synthetic.main.activity_inbody_list.back_btn
import kotlinx.android.synthetic.main.activity_inbody_list.write_btn
import kotlinx.android.synthetic.main.activity_inbody_list.*
import kotlinx.android.synthetic.main.list_view.*
import kotlinx.android.synthetic.main.list_view.view.*
import java.text.SimpleDateFormat

class InBodyListActivity : AppCompatActivity() {
    private var firestore : FirebaseFirestore? = null
    private lateinit var auth : FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inbody_list)
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        back_btn.setOnClickListener {
            finish()
        }
        write_btn.setOnClickListener {
            var intent = Intent(this, InBodyWriteActivity::class.java)
            startActivity(intent)
            finish()
        }

        val layoutManager = LinearLayoutManager(this)
        layoutManager.setReverseLayout(true)
        layoutManager.setStackFromEnd(true)
        inbody_list.layoutManager = layoutManager
        inbody_list.adapter = InbodyitemAdapter(this)

    }

}