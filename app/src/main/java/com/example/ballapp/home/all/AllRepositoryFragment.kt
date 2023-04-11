package com.example.ballapp.home.all

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.ballball.model.CreateMatchModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class AllRepositoryFragment @Inject constructor(private val firebaseDatabase: FirebaseDatabase) {
    fun loadMatchList(
        userUID: String,
        onSuccess: (ArrayList<CreateMatchModel>) -> Unit,
        onFail: (String) -> Unit,
    ) {
        // truy cập đến nút "Request_Match" trong cơ sử dữ liệu firebase
        // ValueEventListener lắng nghe các thay đổi trong dữ liệu
        firebaseDatabase.getReference("Request_Match")
            .addValueEventListener(object : ValueEventListener {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onDataChange(snapshot: DataSnapshot) {
                    // nếu snapshot có dữ liệu, hàm sẽ lặp qua all các nút con của snapshot
                    if (snapshot.exists()) {
                        val listRequest = ArrayList<CreateMatchModel>()
                        for (requestSnapshot in snapshot.children) {
                            val childName = requestSnapshot.key.toString()
                            // lấy giá trị của nó dưới dạng 1 đối tượng CreateMatchModel
                            // bằng phương thức getValue()
                            // kiểm tra xem giá giá trị đó có tồn tại không bằng cách sử dụng let()
                            requestSnapshot.getValue(CreateMatchModel::class.java)?.let { list ->
                                val currentDate = LocalDate.now()
                                val currentTime = LocalTime.now()
                                val matchDate = list.date
                                val matchTime = list.time
                                val dateFormatter =
                                    DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ENGLISH)
                                val timeFormatter =
                                    DateTimeFormatter.ofPattern("HH:mm", Locale.ENGLISH)
                                val date = LocalDate.parse(matchDate, dateFormatter)
                                val time = LocalTime.parse(matchTime, timeFormatter)
                                // Nếu giá trị CreateMatchModel tồn tại thì , hàm tt kiểm tra xem
                                // người dùng có đang tham gia vào trận đấu nay không bằng cách
                                // ss UserUID vs trường clientUID1 ,clientUID2, clientUID3 trong CreateMatchModel
                                if (userUID != list.userUID && date >= currentDate &&
                                    userUID != list.clientUID1 &&
                                    userUID != list.clientUID2 &&
                                    userUID != list.clientUID3
                                ) {
                                    listRequest.add(0, list)
                                }
                            }
                        }
                        // Nếu người dùng ko thg vào trận đấu and time location >= hiện tại,nó sẽ được thêm vào danh sách listRequest
                        onSuccess(listRequest)
                    }
                    // nếu listRequestr rỗng nó sẽ truyền dữ liệu vào hàm callback onSuccess
                    // nếu snapshot ko tt thì nó sẽ tạo ra 1 ds mới và hàm callback onSuccess
                    else {
                        val listRequest = java.util.ArrayList<CreateMatchModel>()
                        onSuccess(listRequest)
                    }
                }

                // nếu lỗi trong phương thức onCancelled, hàm callback onFail sẽ
                // được gọi vào truyền vào thông báo lỗi
                override fun onCancelled(error: DatabaseError) {
                    onFail(error.message)
                }

            })
    }

    fun highligt(
        matchID: String,
        onSuccess: (String) -> Unit,
        onFail: (String) -> Unit,
    ) {
        val highlight = mapOf(
            "highligt" to 1
        )
        firebaseDatabase.getReference("Request_Match").child(matchID).updateChildren(highlight)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    onSuccess(it.toString())
                } else {
                    onFail(it.exception?.message.orEmpty())
                }
            }
            .addOnFailureListener {
                onFail(it.message.orEmpty())
            }
    }

    fun notHighLight(
        matchID: String,
        onSuccess: (String) -> Unit,
        onFail: (String) -> Unit,
    ) {
        val nothighlight = mapOf(
            "highlight" to 0
        )
        firebaseDatabase.getReference("Request_Match").child(matchID).updateChildren(nothighlight)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    onSuccess(it.toString())
                } else {
                    onFail(it.exception?.message.orEmpty())
                }
            }
            .addOnFailureListener {
                onFail(it.message.orEmpty())
            }
    }
}