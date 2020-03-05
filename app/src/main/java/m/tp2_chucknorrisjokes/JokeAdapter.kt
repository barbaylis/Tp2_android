package m.tp2_chucknorrisjokes

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class JokeAdapter(var listJokes :List<Joke>) : RecyclerView.Adapter<JokeAdapter.JokeViewHolder>()
{
    class JokeViewHolder(val textview : TextView) : RecyclerView.ViewHolder(textview)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JokeViewHolder
    {
        //creation of a joke layout and his holder
        val textviewCreated = LayoutInflater.from(parent.context)
            .inflate(R.layout.joke_layout, parent, false) as TextView

        return JokeViewHolder(textviewCreated)
    }

    override fun getItemCount(): Int
    {
        return listJokes.count()
    }

    override fun onBindViewHolder(holder: JokeViewHolder, position: Int) {
        holder.textview.text=listJokes[position].value
    }

}