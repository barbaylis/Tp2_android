package m.tp2_chucknorrisjokes



import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import java.util.*



class JokeAdapter(val models: MutableList<JokeView.Model>, val OnBottomReached: (numRepeat: Int, progressToAppear: Boolean ) -> Unit, val addPreference: (joke: Joke) -> Unit, val removePreference: (joke: Joke) -> Unit) :
    RecyclerView.Adapter<JokeAdapter.JokeViewHolder>()  {

    class JokeViewHolder(val jokeView: JokeView) : RecyclerView.ViewHolder(jokeView)

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

    /**
     *
     */
    override fun getItemCount(): Int {
        return models.count()
    }


    /**
     * Bind the model in the ViewHolder
     */
    override fun onBindViewHolder(holder: JokeViewHolder, position: Int) {
        if (position == this.itemCount - 1) {
            OnBottomReached(10,true)
        }

        holder.jokeView.setupView(models.get(position)) // send the model to update the view

    }

    /**
     * Add the model to be displayed
     */
    fun updateData(newModels: List<JokeView.Model>)
    {
       newModels.forEach {
           models.add(it)
           this.notifyDataSetChanged()
       }

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
                Collections.swap(models, i,i+1)
            }
        }

        else // View must go up
        {
            for (i in initial_position downTo  target_position+1 )
            {
                Collections.swap(models, i,i-1)
            }
        }
        notifyItemMoved(initial_position,target_position)
    }


    /**
     * Remove a view
     */
    fun onJokeRemoved(position: Int)
    {

        models.removeAt(position)
        notifyItemRemoved(position)
    }
}