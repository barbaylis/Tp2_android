package m.tp2_chucknorrisjokes

import android.content.ContentValues.TAG
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class JokeAdapter(var listJokes: MutableList<Joke>, val OnBottomReached: () -> Unit) :
    RecyclerView.Adapter<JokeAdapter.JokeViewHolder>()  {

    class JokeViewHolder(val jokeView: JokeView) : RecyclerView.ViewHolder(jokeView)

    // Logs of shared Jokes
    var logs_share : MutableList<String> =mutableListOf()
    //Logs of star Jokes
    var logs_star : MutableList<String> =mutableListOf()



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
        if (position == this.itemCount - 1) {
            OnBottomReached()
        }

        /////Creation of the model////

        //TextView with the newJoke
        val newT = holder.jokeView.findViewById(R.id.joke_layout) as TextView
        newT.text = listJokes[position].value

        //star button behavior
        val newStarB = holder.jokeView.findViewById(R.id.star_button) as ImageButton
        newStarB.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View)
            {
                if (logs_star.contains(listJokes[position].id)) //already clicked
                {
                    logs_star.remove(listJokes[position].id)//remove from the star jokes list
                    newStarB.setImageResource(R.drawable.star_softpink_notpressed) // put image star border
                }

                else //not already clicked
                {
                    logs_star.add(listJokes[position].id) //add the id of the joke in logs_star list
                    newStarB.setImageResource(R.drawable.star_softpink) // put image full star

                }
            }
        })


        //share button behavior
        val newShareB = holder.jokeView.findViewById(R.id.share_button) as ImageButton
        newShareB.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View)
            {
                logs_share.add(listJokes[position].id) //add the id of the joke in logs_share list
            }
        })

        val newModel = JokeView.Model(newT, newShareB, newStarB)

        holder.jokeView.setupView(newModel) // send the model to update the view
    }

    fun addJoke(joke: Joke) {
        this.listJokes.add(joke)
        this.notifyDataSetChanged()
    }

    /*move a view from initial_position to target_position*/
    fun onItemMoved(initial_position: Int, target_position: Int)
    {
        if (initial_position<target_position) // View must to go down
        {
            for (i in initial_position until target_position )
            {
                Collections.swap(listJokes, i,i+1)
            }
        }

        else // View must go up
        {
            for (i in initial_position downTo  target_position+1 )
            {
                Collections.swap(listJokes, i,i-1)
            }
        }
        notifyItemMoved(initial_position,target_position)
    }


    fun onJokeRemoved(position: Int)
    {
        listJokes.removeAt(position)
        notifyItemRemoved(position)
    }
}