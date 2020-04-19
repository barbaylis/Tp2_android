package m.tp2_chucknorrisjokes

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView


/**
 * class for the behavior of touch screen
 * ex: drag, swip
 */
class JokeTouchHelper(private val onJokeRemoved: (position: Int) -> Unit , private val onItemMoved:(ini: Int, final : Int) -> Unit ):
    ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(UP or DOWN,LEFT or RIGHT)
    {

        /**
         * Behavior when move View
         */
        override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,target: RecyclerView.ViewHolder): Boolean
        {
            val initialPosition=viewHolder.adapterPosition
            val targetPosition=target.adapterPosition
            onItemMoved(initialPosition,targetPosition)
            return true
        }


        /**
         * Behavior when swiped View
         */
        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int)
        {
            val position =viewHolder.adapterPosition
            onJokeRemoved(position)
        }

    }


)