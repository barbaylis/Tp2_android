package m.tp2_chucknorrisjokes

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView




class JokeAdapter(var listJokes: MutableList<Joke>, val OnBottomReached: () -> Unit) :
    RecyclerView.Adapter<JokeAdapter.JokeViewHolder>()
    {
    class JokeViewHolder(val textview: TextView) : RecyclerView.ViewHolder(textview)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JokeViewHolder {
        val textviewCreated = LayoutInflater.from(parent.context)
            .inflate(R.layout.joke_layout, parent, false) as TextView

        return JokeViewHolder(textviewCreated)
    }

    override fun getItemCount(): Int {
        return listJokes.count()
    }

    override fun onBindViewHolder(holder: JokeViewHolder, position: Int) {
        if (position==this.itemCount-1)
        {OnBottomReached()}
        holder.textview.text = listJokes[position].value
    }

    fun addJoke(joke: Joke) {
        this.listJokes.add(joke)
        this.notifyDataSetChanged()
    }

}