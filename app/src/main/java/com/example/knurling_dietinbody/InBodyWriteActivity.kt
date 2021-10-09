package com.example.knurling_dietinbody

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_inbody_write.*
import java.text.SimpleDateFormat
import java.util.*
import android.util.Log
import android.widget.DatePicker
import com.example.knurling_dietinbody.model.inbodyModel

class InBodyWriteActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    var firestore :FirebaseFirestore = FirebaseFirestore.getInstance()
    private var storage : FirebaseStorage? = null
    var timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    var imgFileName = "IMG_" + timeStamp + "_.jpg"
    var selectedPhotoUri: Uri? = null
    var cal = Calendar.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inbody_write)
        auth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()
        val dateSetListener = object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int,
                                   dayOfMonth: Int) {
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            }
        }
        back_btn.setOnClickListener {
            var intent = Intent(this, DiaryListActivity::class.java)
            startActivity(intent)
            finish()
        }
        inbody_cal_view.setOnClickListener {
            // 달력 뷰 보여주기
            DatePickerDialog(
                    this,
                    dateSetListener,
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)

            ).apply{}.show()

            val calString = cal.get(Calendar.YEAR).toString()+"년 "+(cal.get(Calendar.MONTH)+1).toString()+"월 "+cal.get(Calendar.DAY_OF_MONTH).toString()+"일"
            inbody_cal_view.text = calString

        }
        img_btn.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK) //photo selector intent만들기
            intent.type = "image/*"  //우리가 원하는 intent type
            startActivityForResult(intent,0) //비트맵을 이용해서 이미지 로딩하는 function.
        }
        save_btn.setOnClickListener {
            var InbodyModel = inbodyModel()
            InbodyModel.UID = auth?.currentUser?.uid
            InbodyModel.weight = (weight_txt.text.toString()).toLong()
            InbodyModel.date = cal.timeInMillis
            InbodyModel.muscleMass = (muscleMass_txt.text.toString()).toLong()
            InbodyModel.bodyFatMass = (bodyFatMass_txt.text.toString()).toLong()
            InbodyModel.bodyFat = (bodyFat_txt.text.toString()).toLong()
            InbodyModel.bmi = (bmi_txt.text.toString()).toLong()


            firestore?.collection("InbodyRecords")?.document("record_${auth?.currentUser?.uid}_${cal.timeInMillis}")?.set(InbodyModel)

            uploadImageToFirebaseStorage()//사진을 파이어베이스 스토리지에 저장.
            var intent = Intent(this, InBodyListActivity::class.java)
            startActivity(intent)
            finish()
        }
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //프로필 사진으로 선택한 이미지 보이게 하는 과정
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 0 && resultCode == Activity.RESULT_OK && data != null){
            //proceed and check what the selected image was ...
            //선택한 이미지가 보이게 하는 과정
            selectedPhotoUri = data.data //uri는 그 이미지가 저장된 location을 나타냄.
            //bitmap으로 우리가 선택한 이미지에 access.
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)

            if(bitmap != null){
                img_btn.setImageBitmap(bitmap)
                //img_btn.alpha = 0f
            }else{
                val icon = BitmapFactory.decodeResource(getResources(), R.drawable.photo_default)
                img_btn.setImageBitmap(icon)
                //img_btn.alpha = 0f
            }

        }
    }
    private fun uploadImageToFirebaseStorage() {

        //getInctance()를 이용해서 FirebaseStorage에 접근.
        //ref는 파이어베이스 upload area에 대한 정보.
        val ref = FirebaseStorage.getInstance().getReference("/images/$imgFileName")

        if(selectedPhotoUri == null){
            val uri = Uri.parse("android.resource://com.example.mycafediary/drawable/photo_default")
            // val stream: InputStream? = contentResolver.openInputStream(uri)
            ref.putFile(uri)//selected Photo된거 uri를 file형태로 ref에 넣음.
                    //이미지 업로드
                    .addOnSuccessListener {
                        ref.downloadUrl.addOnSuccessListener {
                            firestore?.collection("InbodyRecords")?.document("record_${auth?.currentUser?.uid}_${cal.timeInMillis}")?.update("img_src",imgFileName)

                        }
                    }
        }else{
            ref.putFile(selectedPhotoUri!!) //selected Photo된거 uri를 file형태로 ref에 넣음.
                    //이미지 업로드
                    .addOnSuccessListener {
                        ref.downloadUrl.addOnSuccessListener {
                            firestore?.collection("InbodyRecords")?.document("record_${auth?.currentUser?.uid}_${cal.timeInMillis}")?.update("img_src",imgFileName)
                        }
                    }
                    .addOnFailureListener{ //실패하면
                    }
        }
    }


}