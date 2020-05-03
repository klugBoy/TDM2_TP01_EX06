package com.example.corona.exo_06

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.room.Room
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import org.json.JSONObject
import java.io.IOException
import java.nio.charset.Charset
import org.json.JSONArray
import kotlin.reflect.typeOf
import android.content.Intent
import android.graphics.Color
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.RoomDatabase
import com.aditya.filebrowser.Constants
import com.aditya.filebrowser.FileBrowser
import com.aditya.filebrowser.Constants.SELECTION_MODES
import com.aditya.filebrowser.Constants.SELECTION_MODE
import com.aditya.filebrowser.FileChooser
import kotlinx.android.synthetic.main.activity_main.*
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*


class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    var db : AppDatabase? = null
    val PICK_FILE_REQUEST = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialiser la base de données
        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "database-name"
        ).fallbackToDestructiveMigration().allowMainThreadQueries().build()



        // Importer le fichier json
        emploi.setOnClickListener {
            chooseFile()

        }


        // filtre par salle ,enseignant et module
        spinnerFilter()


        // filtre par date
        dayFilter()

    }

    // Initialiser date Picker
    fun dayFilter(){
        var  myCalendar : Calendar = Calendar.getInstance()
        var date_listener = object : DatePickerDialog.OnDateSetListener{
            override fun onDateSet(p0: DatePicker?, p1: Int, p2: Int, p3: Int) {
                myCalendar.set(Calendar.YEAR, p1);
                myCalendar.set(Calendar.MONTH, p2);
                myCalendar.set(Calendar.DAY_OF_MONTH, p3)
                myCalendar.set(Calendar.HOUR_OF_DAY, 0);
                myCalendar.set(Calendar.MINUTE, 0)
                myCalendar.set(Calendar.SECOND,0)
                myCalendar.set(Calendar.MILLISECOND, 0)
                updateLabel(myCalendar)
            }
        }

        date_picker.setOnClickListener {
            DatePickerDialog(this, date_listener, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show()

        }
    }

    // mise à jour de la date ,filtrer par date
    fun updateLabel(myCalendar : Calendar) {
        var myFormat : String = "yyyy-MM-dd"
        var sdf : SimpleDateFormat = SimpleDateFormat(myFormat)

        var tdate : String = sdf.format(myCalendar.getTime())

        var end_cal = Calendar.getInstance()
        end_cal.set(Calendar.YEAR, myCalendar.get(Calendar.YEAR));
        end_cal.set(Calendar.MONTH, myCalendar.get(Calendar.MONTH));
        end_cal.set(Calendar.DAY_OF_MONTH, myCalendar.get(Calendar.DAY_OF_MONTH))
        end_cal.set(Calendar.HOUR_OF_DAY, 23);
        end_cal.set(Calendar.MINUTE, 59)
        end_cal.set(Calendar.SECOND,59)
        end_cal.set(Calendar.MILLISECOND, 999)
        date_picker.setText(tdate)
        var debut_date : Long = myCalendar.timeInMillis
        var fin_date : Long = end_cal.timeInMillis
        loadRecycleView(db?.SeanceDao()?.findByDate(debut_date,fin_date)!!)
    }

    // les 2 spinners , filtre par salle ,enseignant et module
    fun spinnerFilter(){

        val spinner: Spinner = findViewById(R.id.spinner)
        val sub_spinner: Spinner = findViewById(R.id.spinner2)
        var position : Int = 0
        var liste_strings : MutableList<String>? = mutableListOf("")
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            this,
            R.array.array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner.adapter = adapter
            spinner.setSelection(0)
            spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    position = p2
                    liste_strings!!.clear()
                    liste_strings.add("")
                    when(position){

                        1->{
                            sub_spinner.setSelection(0)
                            liste_strings!!.addAll(db?.SeanceDao()?.getAllModule()!!.toTypedArray())
                        }
                        2->{
                            sub_spinner.setSelection(0)
                            liste_strings!!.addAll(db?.SeanceDao()?.getAllEnseignant()!!.toTypedArray())}
                        3->{
                            sub_spinner.setSelection(0)
                            liste_strings!!.addAll(db?.SeanceDao()?.getAllSalle()!!.toTypedArray())}
                        else->{
                            sub_spinner.setSelection(0)
                            var liste_seance  = db?.SeanceDao()?.getAll()!!
                            loadRecycleView(liste_seance)
                        }
                    }



                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }
            }

        }

        val spinnerArrayAdapter =
            ArrayAdapter(applicationContext, android.R.layout.simple_spinner_item, liste_strings!!)
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner
        sub_spinner.adapter =spinnerArrayAdapter
        sub_spinner.setSelection(0)
        sub_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                var selected : String?= spinnerArrayAdapter.getItem(p2)
                var liste_seance : List<Seance> = listOf()
                when(position){
                    1->{
                        liste_seance=db?.SeanceDao()?.findByModule(selected!!)!!
                        loadRecycleView(liste_seance)
                    }
                    2->{
                        liste_seance=db?.SeanceDao()?.findByEnseignant(selected!!)!!
                        loadRecycleView(liste_seance)
                    }
                    3->{
                        liste_seance=db?.SeanceDao()?.findBySalle(selected!!)!!
                        loadRecycleView(liste_seance)
                    }

                }

            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        }
    }

    // Initialiser loadRecycle
    fun setData(data : String){
        var obj = JSONObject(data)
        var jsonArray: JSONArray = obj.getJSONArray("seances")
        var gson = Gson()
        var jsonObject: JSONObject = JSONObject()

        db?.SeanceDao()?.deleteAll()
        //transformer la réponse en une liste de Country
        for (i in 0 until jsonArray.length()) {
            jsonObject = jsonArray.getJSONObject(i)
            db?.SeanceDao()?.insertAll(gson.fromJson(jsonObject.toString(), Seance::class.java))
        }
        var liste : List<Seance>? = db?.SeanceDao()?.getAll()
        loadRecycleView(liste!!)
    }

    // Remplir liste de seances (Recycle View)
    fun loadRecycleView(liste : List<Seance>){
        viewManager = LinearLayoutManager(this)
        viewAdapter = MyAdapter(liste!!.toTypedArray())

        recyclerView = findViewById<RecyclerView>(R.id.recyclerView).apply {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)

            // use a linear layout manager
            layoutManager = viewManager

            // specify an viewAdapter (see also next example)
            adapter = viewAdapter

        }


    }

    // Charger un fichier json depuis votre smartphone
    fun chooseFile(){
        val i2 = Intent(applicationContext, FileChooser::class.java)
        i2.putExtra(Constants.SELECTION_MODE, Constants.SELECTION_MODES.SINGLE_SELECTION.ordinal)
        startActivityForResult(i2, PICK_FILE_REQUEST)
    }

    // Recevoir le path de votre fichier json
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_FILE_REQUEST && resultCode == RESULT_OK) {

            var d = data!!.data.toString()
            var file = File(d.subSequence(5,d.length).toString())
            val bufferedReader: BufferedReader = file.bufferedReader()
            val inputString = bufferedReader.use { it.readText() }
            setData(inputString)
        }
    }

}
