package m.tp2_chucknorrisjokes


import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.*
import android.content.Intent
import androidx.core.content.ContextCompat.startActivity


class JokeAdapter(var listJokes: MutableList<Joke>, val OnBottomReached: (numRepeat: Int, progressToAppear: Boolean ) -> Unit, val addPreference: (joke: Joke) -> Unit, val removePreference: (joke: Joke) -> Unit) :
    RecyclerView.Adapter<JokeAdapter.JokeViewHolder>()  {

    class JokeViewHolder(val jokeView: JokeView) : RecyclerView.ViewHolder(jokeView)

    // Logs of shared Jokes
    var logsShare : MutableList<String> =mutableListOf()
    //Logs of star Jokes
    var logsStar : MutableList<String> =mutableListOf()

    var jokeToStar : Boolean = false

    /**
     * Create View
     */
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


    /**
     * Bind the model in the ViewHolder
     */
    override fun onBindViewHolder(holder: JokeViewHolder, position: Int) {
        if (position == this.itemCount - 1) {
            OnBottomReached(10,true)
        }

        /////Creation of the model////

        //TextView with the newJoke
        val newT = holder.jokeView.findViewById(R.id.joke_layout) as TextView
        newT.text = listJokes[position].value

        //star button behavior
        val newStarB = holder.jokeView.findViewById(R.id.star_button) as ImageButton
        if(jokeToStar) //open the apli with the joke already stared
            {
                newStarB.setImageResource(R.drawable.star_softpink)
            } // put image full star

        newStarB.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View)
            {
                if (logsStar.contains(listJokes[position].id ) ) //already clicked
                {
                    logsStar.remove(listJokes[position].id)//remove from the star jokes list
                    removePreference(listJokes[position]) //remove the joke from the preference file

                    newStarB.setImageResource(R.drawable.star_softpink_notpressed) // put image star border
                }


                else //not already clicked
                {
                    logsStar.add(listJokes[position].id) //add the id of the joke in logsStar list
                    addPreference(listJokes[position]) // add the joke to the preference file
                    newStarB.setImageResource(R.drawable.star_softpink) // put image full star

                }
            }
        })


        //share button behavior
        val newShareB = holder.jokeView.findViewById(R.id.share_button) as ImageButton
        newShareB.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View)
            {
                logsShare.add(listJokes[position].id) //add the id of the joke in logsShare list

                //share of the joke
                val sendIntent = Intent().apply {
                    action=Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, listJokes[position].value) //text to sent
                    type = "text/plain"
                }
                val shareIntent=Intent.createChooser(sendIntent, "Partager" )
                startActivity(holder.jokeView.context,shareIntent,null)
            }
        })


        val newModel = JokeView.Model(newT, newShareB, newStarB)

        holder.jokeView.setupView(newModel) // send the model to update the view

    }


    /**
     * Add joke to be displayed
     */
    fun addJoke(joke: Joke, toStar :Boolean) {


        this.listJokes.add(joke)
        jokeToStar=toStar
        this.notifyDataSetChanged()


    }

    /**
     * Move a view from initial_position to target_position
     */
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


    /**
     * Remove a view
     */
    fun onJokeRemoved(position: Int)
    {
        listJokes.removeAt(position)
        notifyItemRemoved(position)
    }
}