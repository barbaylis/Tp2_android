package m.tp2_chucknorrisjokes

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout

class JokeView @JvmOverloads constructor(context: Context): ConstraintLayout(context)
{

    init {
             LayoutInflater.from(context).inflate(R.layout.joke_layout, this, true)
        }


    fun setupView(model: Model)
    {
       //update textView text
       var myTextView: TextView = findViewById(R.id.joke_layout)
       myTextView=model.textview

       //update share button
        var myshareButton: ImageButton = findViewById(R.id.share_button)
        myshareButton=model.shareButtom

        // update star button
        var mystarButton: ImageButton = findViewById(R.id.star_button)
        mystarButton=model.starButtom

    }


    data class Model(
        val textview: TextView,
        val shareButtom : ImageButton,
        val starButtom : ImageButton
    )


}