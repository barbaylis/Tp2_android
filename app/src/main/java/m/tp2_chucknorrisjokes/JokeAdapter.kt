package m.tp2_chucknorrisjokes

import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class JokeAdapter(var listJokes: MutableList<Joke>, val OnBottomReached: () -> Unit) :
        RecyclerView.Adapter<JokeAdapter.JokeViewHolder>()
{
    class JokeViewHolder(val jokeView: JokeView) : RecyclerView.ViewHolder(jokeView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JokeViewHolder {
        val jokeViewCreated = JokeView(parent.context)

        // reapplied layout param because the inflate in JokeView doesn't take account of parent
        val lp = RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        )
        jokeViewCreated.setLayoutParams(lp)

        return JokeViewHolder(jokeViewCreated)
    }

    override fun getItemCount(): Int {
        return listJokes.count()
    }

    override fun onBindViewHolder(holder: JokeViewHolder, position: Int) {
        if (position==this.itemCount-1)
        {OnBottomReached()}

        //recovery of the views'components for the new model
        val newT =holder.jokeView.findViewById(R.id.joke_layout) as TextView
        newT.text=listJokes[position].value
        val newStartB =holder.jokeView.findViewById(R.id.star_button) as ImageButton
        val newShareB =holder.jokeView.findViewById(R.id.share_button) as ImageButton
        val newModel = JokeView.Model(newT,newShareB,newStartB)

        holder.jokeView.setupView(newModel)
    }

    fun addJoke(joke: Joke) {
        this.listJokes.add(joke)
        this.notifyDataSetChanged()
    }

}