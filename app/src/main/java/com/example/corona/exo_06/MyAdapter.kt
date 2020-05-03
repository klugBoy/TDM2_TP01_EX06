package com.example.corona.exo_06

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat

class MyAdapter(private val myDataset: Array<Seance>) :
    RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var salle: TextView
        var enseignant : TextView
        var module : TextView
        var date : TextView
        var debut : TextView
        var fin : TextView

        init {
            this.salle = itemView.findViewById(R.id.salle_txt)
            this.enseignant = itemView.findViewById(R.id.enseignant_txt)
            this.module = itemView.findViewById(R.id.module_txt)
            this.date= itemView.findViewById(R.id.date_txt)
            this.debut= itemView.findViewById(R.id.debut_txt)
            this.fin= itemView.findViewById(R.id.fin_txt)
        }
    }


    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): MyAdapter.MyViewHolder {
        // create a new view
        val listview = LayoutInflater.from(parent.context)
            .inflate(R.layout.listview, parent, false) as View
        return MyViewHolder(listview)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        val formatter = SimpleDateFormat("dd/MM/yyyy")
        val formatterTime = SimpleDateFormat("HH:mm")
        holder.salle.setText(myDataset[position].salle)
        holder.enseignant.setText(myDataset[position].enseignant)
        holder.module.setText(myDataset[position].module)
        holder.date.setText(formatter.format(myDataset[position].dateDebut))
        holder.debut.setText(formatterTime.format(myDataset[position].dateDebut))
        holder.fin.setText(formatterTime.format(myDataset[position].dateFin))
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = myDataset.size
}
