package com.example.messengerapp

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.messengerapp.RecyclerView.SearchItems
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.OnItemClickListener
import com.xwray.groupie.Section
import com.xwray.groupie.ViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.activity_search.*

class SearchActivity : AppCompatActivity() {

    val my_uid = FirebaseAuth.getInstance().currentUser!!.uid
    var db = FirebaseFirestore.getInstance()
    lateinit var searchSection: Section
    var shouldinitrecyclerview = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR   // 3shan yezher elkalam beleswed badal elabyad

        setSupportActionBar(toolbar3)
        supportActionBar!!.title = ""
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if(id==android.R.id.home){
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_menu, menu)
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        (menu?.findItem(R.id.app_bar_search)?.actionView as SearchView).apply {

            isIconified = false
            setSearchableInfo(searchManager.getSearchableInfo(componentName))

            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(newText: String?): Boolean {
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    if (newText!!.isEmpty()){
                        return false
                    }
                    val query = db.collection("users").orderBy("name").startAt(newText.trim()).endAt(newText.trim() + "\uf8ff")
                    show_results_of_search(::recycler_view , query)
                    return true
                }

            })
        }
        return true
    }

    private fun show_results_of_search(oncomplete:(List<Item>) -> Unit , query: Query) {

        val items = mutableListOf<Item>()

        query.get().addOnSuccessListener {
            it.documents.forEach{
                val user = it.toObject(User::class.java)!!
                if (it.id != my_uid){
                    items.add(SearchItems(it.id , user,this@SearchActivity))
                }
            }
            oncomplete(items)
        }
    }


    fun recycler_view(items: List<Item>) {

        fun init() {
            search_recyclerView.apply {
                layoutManager = LinearLayoutManager(this@SearchActivity)
                adapter = GroupAdapter<ViewHolder>().apply {
                    searchSection = Section(items)
                    add(searchSection)
                    setOnItemClickListener(lw_dost_3la_7aga)
                }
            }
            shouldinitrecyclerview = false
        }
        fun update() {
            searchSection.update(items)
        }

        //main of function
        if (shouldinitrecyclerview){
            init()
        }
        else{
            update()
        }

    }

    val lw_dost_3la_7aga = OnItemClickListener { item, view ->
        if (item is SearchItems) {
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra("uid", item.uid)
            intent.putExtra("name", item.user.name)
            intent.putExtra("path", item.user.profileImage)
            startActivity(intent)
        }
    }
}