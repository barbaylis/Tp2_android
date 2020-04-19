package m.tp2_chucknorrisjokes

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

/* class for the behavior of touch screen
* ex: drag, swipe*/
class JokeTouchHelper(private val onJokeRemoved: (position: Int) -> Unit , private val onItemMoved:(ini: Int, final : Int) -> Unit ):
    ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(UP or DOWN,LEFT or RIGHT)
    {

        override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,target: RecyclerView.ViewHolder): Boolean
        {
            val initial_position=viewHolder.adapterPosition
            val target_position=target.adapterPosition
            onItemMoved(initial_position,target_position)
            return true
        }// TODO("use it to reorder items")



        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int)
        {
            val position =viewHolder.adapterPosition
            onJokeRemoved(position)
        }//TODO("use it to delete items")

    }


)