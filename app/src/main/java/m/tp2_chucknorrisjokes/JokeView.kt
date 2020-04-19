package m.tp2_chucknorrisjokes

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout

class JokeView @JvmOverloads constructor(context: Context): ConstraintLayout(context)
{

    init {
             LayoutInflater.from(context).inflate(R.layout.joke_layout, this, true)
    }


    /**
     * Change the view to match with the model
     */
    fun setupView(model: Model)
    {
       //update textView text
       val myTextView: TextView = findViewById(R.id.joke_layout)
       myTextView.setText(model.textview.text)

       //update share button
        val myshareButton: ImageButton = findViewById(R.id.share_button)
        myshareButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                model.shareButtonBehavior(v,model.textview.text.toString())
            }
        })


        // update star button
        val mystarButton: ImageButton = findViewById(R.id.star_button)
        Log.i("star button tag",(model.starButtom.getTag()==null).toString())
        val image :Int = model.starButtom.getTag() as Int
        mystarButton.setImageResource(image)
        mystarButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                model.starButtonBehavior(v,model.textview.text.toString(),this@JokeView)
            }
        })

    }


    data class Model(
        val textview: TextView,
        val shareButtom : ImageButton,
        val starButtom : ImageButton,
        val starButtonBehavior :(v:View,jokeValue:String ,holder: JokeView) -> Unit,
        val shareButtonBehavior : (v:View,jokeValue: String) -> Unit

    )


}