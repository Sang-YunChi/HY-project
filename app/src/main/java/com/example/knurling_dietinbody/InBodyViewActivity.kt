package com.example.knurling_dietinbody

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_inbody_view.*
import java.text.SimpleDateFormat
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide

class InBodyViewActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    var firestore : FirebaseFirestore = FirebaseFirestore.getInstance()
    var storage : FirebaseStorage?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inbody_view)
        auth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()
        var date : String? = null
        if(intent.hasExtra("date")){
            date = intent.getStringExtra("date")
            Log.d("실험--2--","${date}")
        }
        else{
            Toast.makeText(this, "Error!", Toast.LENGTH_SHORT).show()
        }

        back_btn.setOnClickListener {
            finish()
        }
        firestore?.collection("InbodyRecords")?.whereEqualTo("date",date?.toLong())//1619271434933
                ?.get()?.addOnSuccessListener { documents ->
                    for(doc in documents){
                        weight_text.text = doc?.data?.get("weight").toString()
                        inbody_date_txt.text = SimpleDateFormat("yyyy.MM.dd").format(doc?.data?.get("date"))
                        muscleMass_text.text = doc?.data?.get("muscleMass").toString()
                        bodyFatMass_text.text = doc?.data?.get("bodyFatMass").toString()
                        bodyFat_text.text = doc?.data?.get("bodyFat").toString()
                        bmi_text.text = doc?.data?.get("bmi").toString()

                        var storageRef = storage?.reference?.child("images")?.child(doc?.data?.get("img_src").toString())
                        storageRef?.downloadUrl?.addOnSuccessListener { uri ->
                            Glide.with(applicationContext)
                                    .load(uri)
                                    .into(inbody_img_view)
                            Log.v("IMAGE","Success")
                        }?.addOnFailureListener { //이미지 로드 실패시
                            Toast.makeText(applicationContext, "이미지 로드 실패", Toast.LENGTH_SHORT).show()
                            Log.v("IMAGE","failed")

                        }
                        //break
                    }
                }
        delete_btn.setOnClickListener {
            val mAlertDialog = AlertDialog.Builder(this)
            mAlertDialog.setTitle("인바디 기록 삭제")
            mAlertDialog.setMessage("삭제된 인바디 기록은 복구할 수 없으며 데이터베이스에서 완전히 지워집니다. " +
                    "정말 삭제하겠습니까?")
            mAlertDialog.setPositiveButton("Yes") { dialog, id ->
                //perform some tasks here
                firestore.collection("InbodyRecords")?.document("record_${auth.currentUser?.uid}_${date}")
                        .delete()
                        .addOnSuccessListener {
                            Toast.makeText(this, "삭제되었습니다.",Toast.LENGTH_LONG).show()
                        }
                val intent = Intent(this, InBodyListActivity :: class.java )//리스트화면으로 넘어감
                startActivity(intent)
                finish()
            }
            mAlertDialog.setNegativeButton("No") { dialog, id ->
                //perform som tasks here
            }
            mAlertDialog.show()
        }
    }
}