package com.example.messenger

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_latest_messages.*
import kotlinx.android.synthetic.main.activity_new_message.*
import kotlinx.android.synthetic.main.user_row.view.*

class NewMessageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)
        supportActionBar?.title ="Contact"

       val username = intent.getStringExtra(NewMessageActivity.USER_KEY)
        supportActionBar?.title = username

        recyclerview_newmessage.adapter
        val adapter = GroupAdapter<ViewHolder>()

       //  adapter.add(UserItem())

        recyclerview_newmessage.adapter =adapter

        fetchUsers()

    }
    companion object {
        val USER_KEY = "USER_KEY"
    }
        // function to fetch users from firebase
    private fun fetchUsers() {
       val ref = FirebaseDatabase.getInstance().getReference("/users")
        ref.addListenerForSingleValueEvent(object: ValueEventListener{

            override fun onDataChange(p0: DataSnapshot) {
                val adapter = GroupAdapter<ViewHolder>()
                p0.children.forEach{
                    Log.d("NewMessageActivity",it.toString())
                    val user = it.getValue(User::class.java)
                    if(user!=null){
                        adapter.add(UserItem(user))
                    }
                }// making the contacts click
                adapter.setOnItemClickListener{item, view ->

                    val userItem =item as UserItem
                    val intent  = Intent(view.context,ChatLogActivity::class.java)
                    intent.putExtra(USER_KEY, userItem.user.username)
                    startActivity(intent)
                    finish()
                }
                recyclerview_newmessage.adapter = adapter
            }
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })
    }
}

// building the newmessage screen
// binding  the user in the dadtbase to the interface
class UserItem(val user:User): Item<ViewHolder>(){

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.contact_name.text = user.username
        Picasso.get().load(user.profileImageUrl).into(viewHolder.itemView.imageview_contact)
    }
    override fun getLayout(): Int {
        return R.layout.user_row
    }
}



