package com.example.messenger

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*
import kotlinx.android.synthetic.main.user_row.view.*

class ChatLogActivity : AppCompatActivity() {

    val adapter =   GroupAdapter<ViewHolder>()
    var toUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        recyclerview_chatlog.adapter = adapter

         toUser =intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        // support actionbar title
        supportActionBar?.title= "Contacts"

        buSend_Message.setOnClickListener {
            performSendMessage()
        }

       // setupDummyData()

        ListenForMessages()
    }

    private fun ListenForMessages() {
        val ref= FirebaseDatabase.getInstance().getReference("/message")

        ref.addChildEventListener(object :ChildEventListener{

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val ChatMessage = p0.getValue(ChatMessage::class.java)
                if ( ChatMessage!=null){

                    if (ChatMessage.fromid == FirebaseAuth.getInstance().uid){
                        val currentUser = LatestMessagesActivity.currentUser
                        adapter.add(ChatFromItem(ChatMessage.text,currentUser!!))
                    }else {
                        adapter.add(ChatToItem(ChatMessage.text, toUser!!))
                    }

                }


            }
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onChildRemoved(p0: DataSnapshot) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })
    }

    class ChatMessage(val id:String ,val text:String, val fromid:String, val timestamp:Long ){
        constructor():this("","","",-1)
    }

    private fun performSendMessage() {
        //  automatically generates a noder for us in theh firbese called message
        val text = etChat.text.toString()
        val fromid=FirebaseAuth.getInstance().uid
        val reference = FirebaseDatabase.getInstance().getReference("/message").push()

        // we ARE going to create another class called chatmessage
        val chatMessage =ChatMessage(reference.key!!, text, fromid!!, System.currentTimeMillis())
        reference.setValue(chatMessage)
    }


}
    class ChatFromItem(val text:String, val user:User): Item<ViewHolder>(){

        override fun bind(viewHolder: ViewHolder, position: Int) {
            viewHolder.itemView.textView_from.text
            val uri =user.profileImageUrl
            val targetImageView = viewHolder.itemView.imageview_dp_from
            Picasso.get().load(uri).into(targetImageView)
        }
        override fun getLayout(): Int {
            return R.layout.chat_from_row
        }
    }
    class ChatToItem(val text:String, val user: User): Item<ViewHolder>(){

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textView_to.text

        // load our image from firebase into the chatlog screen
        val uri =user.profileImageUrl
        val targetImageView = viewHolder.itemView.imageview_dp_to
        Picasso.get().load(uri).into(targetImageView)

    }
    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }
    }

