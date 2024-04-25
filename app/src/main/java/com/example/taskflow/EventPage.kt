import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.taskflow.MainActivity
import com.example.taskflow.R
import com.example.taskflow.databinding.ActivityEventPageBinding
import com.example.taskflow.utils.EventData
import com.example.taskflow.utils.EventNameAdapter
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class EventPage : AppCompatActivity() {

    private lateinit var binding: ActivityEventPageBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: EventNameAdapter
    private lateinit var eventList: MutableList<EventData>
    private var db = Firebase.firestore
    private lateinit var eventDocNameList: MutableList<String>
    private var eventNameList: MutableList<String> = mutableListOf()
    private var e: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_event_page)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding = ActivityEventPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
//        getDataFromFirebase()
//        registerEvents()
    }

    private fun init(){
        auth = FirebaseAuth.getInstance()
        binding.recyclerview.setHasFixedSize(true)
        binding.recyclerview.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        eventList = mutableListOf()
        adapter = EventNameAdapter(eventList)
        recyclerView = binding.recyclerview
        recyclerView.adapter = adapter
        db = FirebaseFirestore.getInstance()
        eventDocNameList = mutableListOf()
    }

    private fun getDataFromFirebase() {
        GlobalScope.launch(Dispatchers.Main) {
            try {
                val snapshot = db.collection("events").get().await()

                if (snapshot != null && !snapshot.isEmpty) {
                    traverseEvents(db.collection("events"))
                }
            } catch (e: Exception) {
                Toast.makeText(this@EventPage, "Error fetching data: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e("EventPage", "Error fetching data", e)
            }
        }
    }

    private suspend fun traverseEvents(eventsCollectionRef: CollectionReference) {
        try {
            val eventsSnapshot = eventsCollectionRef.get().await()

            for (doc in eventsSnapshot.documents) {
                val eventDocName = doc.id
                eventDocNameList.add(eventDocName)
                val eventName = doc.getString("eventName")

                traverseEvents(doc.reference.collection("events"))
            }
        } catch (e: Exception) {
            Log.e("TraverseEvents", "Error traversing events", e)
        }
    }

    private fun registerEvents(){
        binding.logoutBt.setOnClickListener{
            auth.signOut()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}
