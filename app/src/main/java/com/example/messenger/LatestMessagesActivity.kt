package com.example.messenger

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.renderscript.Sampler
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.messenger.NewMessageActivity.Companion.USER_KEY
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_latest_messages.*
import kotlinx.android.synthetic.main.latest_message_row.view.*

class LatestMessagesActivity : AppCompatActivity() {

    companion object {
        var currentUser: User? =  null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_latest_messages)
        recyclerview_latestMessages.adapter
        recyclerview_latestMessages.addItemDecoration(DividerItemDecoration(this,DividerItemDecoration.VERTICAL))


        // set onclickListener on the latest message activity
        adapter.setOnItemClickListener { item, view ->
            val intent = Intent(this,ChatLogActivity::class.java)
            val row = item as LatestMessageRow
            intent.putExtra(USER_KEY, row.chatPartenerUser)
            startActivity(intent)
        }

       //setupDummyRows()
        listenForLatestMessage()

        fetchCurrentUser()
        // fuction to check if user is logged in
       verifyUserIsLoggedIn()
    }

    val latestMessagesMap = HashMap<String,ChatLogActivity.ChatMessage>()

    private fun listenForLatestMessage() {
        val fromid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromid")
        ref.addChildEventListener(object : ChildEventListener{
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatLogActivity.ChatMessage::class.java) ?: return

                latestMessagesMap[p0.key!!] = chatMessage
                refreshRecyclerViewMessages()

                adapter.add(LatestMessageRow(chatMessage))
            }
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatLogActivity.ChatMessage::class.java)?: return
                adapter.add(LatestMessageRow(chatMessage))
                latestMessagesMap[p0.key!!] = chatMessage
                refreshRecyclerViewMessages()
            }

            override fun onChildRemoved(p0: DataSnapshot) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        })
    }

    private fun refreshRecyclerViewMessages() {
        adapter.clear()
        latestMessagesMap.values.forEach{
            adapter.add(LatestMessageRow(it))
        }
    }

    val adapter = GroupAdapter<ViewHolder>()

    class  LatestMessageRow(val chatMessage: ChatLogActivity.ChatMessage):Item<ViewHolder>(){
        var chatPartenerUser: User ? =null
        override fun getLayout(): Int {
            return R.layout.latest_message_row
        }
        override fun bind(viewHolder: ViewHolder, position: Int) {
            viewHolder.itemView.latest_latestMessage.text = chatMessage.text
            var chatPartnerId = String()
                if (chatMessage.fromid == FirebaseAuth.getInstance().uid){
                chatPartnerId = chatMessage.toId
            }else{
                chatPartnerId = chatMessage.fromid
            }

            val ref =FirebaseDatabase.getInstance().getReference("/users/$chatPartnerId")
            ref.addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onDataChange(p0: DataSnapshot) {
                    chatPartenerUser = p0.getValue(User::class.java)
                    viewHolder.itemView.username_latestMessage.text =chatPartenerUser?.username
                    val targetImageView = viewHolder.itemView.imageView_latest_messages
                    Picasso.get().load(chatPartenerUser?.profileImageUrl).into(targetImageView)
                }
            })

        }
    }
    private fun fetchCurrentUser() {
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/user/$uid")
        ref.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot) {
                currentUser=p0.getValue(User::class.java)
            }

        })
    }

    private fun verifyUserIsLoggedIn() {
        // to check if the user is logged into firebase
        val uid = FirebaseAuth.getInstance().uid
        // if user is not logged in this should happen
        if (uid == null){
            val intent = Intent(this,Login::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    // putting up a menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }
        // when something is selected in the menu bar
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId){
            R.id.menu_new_message ->{
                val intent = Intent(this,NewMessageActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)

            }
            R.id.menu_sign_out ->{
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this,Login::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
