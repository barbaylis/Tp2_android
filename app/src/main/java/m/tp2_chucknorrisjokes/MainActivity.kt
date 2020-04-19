package m.tp2_chucknorrisjokes


import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.http.GET
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.serialization.json.JsonConfiguration


@Serializable
data class Joke(

    val categories: List<String>,

    @SerialName("created_at")
    val createdAt: String,

    @SerialName("icon_url")
    val iconUrl: String,

    val id: String,

    @SerialName("updated_at")
    val updatedAt: String,

    val url: String,
    val value: String

)


interface JokeApiService {
    @GET("jokes/random/")
    fun giveMeAJoke(): Single<Joke>

}

object JokeApiServiceFactory {
    fun buildJokeApiService(): JokeApiService {
        val builder = Retrofit.Builder()
            .baseUrl("https://api.chucknorris.io/")
            .addConverterFactory(Json.asConverterFactory(MediaType.get("application/json")))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()

        return builder.create(JokeApiService::class.java)
    }
}

class MainActivity : AppCompatActivity() {

    var numJokeToDisplay: Int=10

    private var disposable = CompositeDisposable()
    private var adapt = JokeAdapter(mutableListOf(),(this::newJoke), (this::addPreference), (this::removePreference))

    val itemHelper = JokeTouchHelper((adapt::onJokeRemoved),(adapt::onItemMoved )) // choose the behavior of touch screen

    //file for jokes preferences
    val filePref ="jokes_prefs"
    var jokesPrefsDirectory :SharedPreferences? = null

    // Logs of shared Jokes
    var logsShare : MutableList<String> =mutableListOf()
    //Logs of star Jokes
    var logsStar : MutableList<String> =mutableListOf()

    var listJoke : MutableList<Joke> =mutableListOf()


    /**
     * Called at the launch of the application
     */
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        //recycler
        val recycler: RecyclerView = findViewById(R.id.recycler)
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapt
        itemHelper.attachToRecyclerView(recycler)

        // for manage pref jokes
        jokesPrefsDirectory = getSharedPreferences(filePref, Context.MODE_PRIVATE) // file private at this activity

        /////display of Jokes

        if(savedInstanceState!=null) // == rotation
        {
            val jokesSavedString=savedInstanceState.getString("List_joke")
            val jokesSharedString=savedInstanceState.getString("List_share")
            val jokesStaredString=savedInstanceState.getString("List_star")
            val json = Json(JsonConfiguration.Stable)

            //restore old id stared  list
            if (jokesStaredString!=null)
            {
                val idSaved = json.parse(String.serializer().list, jokesStaredString)
                logsStar=idSaved.toMutableList()

            }

            //restore old id shared list
            if(jokesSharedString!= null)
            {
                val jokesShared = json.parse(String.serializer().list, jokesSharedString)
                logsShare=jokesShared.toMutableList()
            }

            // restore old jokes displayed
            if(jokesSavedString!=null)
            {
                val jokesSaved = json.parse(Joke.serializer().list, jokesSavedString)
               jokesSaved.forEach {

                   listJoke.add(it)

                   //creation of the model

                   val text=TextView(recycler.context)
                   text.setText(it.value)

                   val shareButton = ImageButton(recycler.context)
                   val starButton = ImageButton(recycler.context)

                   // check if the jokes was stared
                    if(logsStar.contains(it.id))
                    {
                         starButton.setImageResource(R.drawable.star_softpink) // put image star plenty
                         starButton.setTag(R.drawable.star_softpink)
                    }
                    else
                    {
                        starButton.setImageResource(R.drawable.star_softpink_notpressed) // put image star plenty
                        starButton.setTag(R.drawable.star_softpink_notpressed)
                    }

                   val newModel :JokeView.Model= JokeView.Model(text,shareButton,starButton,(this::onClickStar),(this::onClickShare))
                   val models: MutableList<JokeView.Model> = mutableListOf()
                   models.add(newModel)
                   adapt.updateData(models) //sent to the adapter
                }

            }
        }
        else{

                /* management of file jokes preferences */
                if (jokesPrefsDirectory!= null)
                {
                    val jokesPref :Map<String,*> = jokesPrefsDirectory!!.getAll()
                    val modelsPref: MutableList<JokeView.Model> = mutableListOf()

                    if (jokesPref.isNotEmpty()) // there are already star jokes
                    {
                        if(jokesPref.size >=10) // check if it's needed to display other jokes after
                        {numJokeToDisplay=0}
                        else
                        {numJokeToDisplay=numJokeToDisplay-jokesPref.size} // complement up to 10 jokes display


                        for( (key,jokeString) in jokesPref) // for each item
                        {
                            if (jokeString is String)// check if it's well a joke
                            {
                                //parse String into Joke thanks to JSON
                                val json = Json(JsonConfiguration.Stable)
                                val joke= json.parse(Joke.serializer(), jokeString)

                                logsStar.add(joke.id) // add it's id to the logs_star list
                                listJoke.add(joke)    // add the joke to the listJoke

                                //creation of the model
                                val text = TextView(recycler.context)
                                text.setText(joke.value)

                                val shareButton = ImageButton(recycler.context)

                                val starButton = ImageButton(recycler.context)
                                starButton.setImageResource(R.drawable.star_softpink)
                                starButton.setTag(R.drawable.star_softpink)

                                val newModel :JokeView.Model= JokeView.Model(text,shareButton,starButton,(this::onClickStar),(this::onClickShare))
                                modelsPref.add(newModel)



                            }
                        }
                        adapt.updateData(modelsPref) //sent to the adapter
                    }

                }


            if (numJokeToDisplay !=0)
              {
                  newJoke(numJokeToDisplay,true) //display new Joke up to 10 jokes in all
              }
        }


        // for swipe to refresh, when restart the application, keep old preferences
        val refresh  : SwipeRefreshLayout = findViewById(R.id.refresh)
        refresh.setColorSchemeColors(Color.BLACK)

        refresh.setOnRefreshListener (
             object : SwipeRefreshLayout.OnRefreshListener {
                 override fun onRefresh() {

                     // recreate an adapter
                     adapt.models.clear()
                     val newadapt = JokeAdapter(
                         mutableListOf(),
                         (this@MainActivity::newJoke),
                         (this@MainActivity::addPreference),
                         (this@MainActivity::removePreference)
                     )
                     adapt = newadapt
                     recycler.adapter = adapt
                     newJoke(10,false) // display 10 jokes
                     refresh.setRefreshing(false)
                 }
             }
        )


    }

    /**
     * When the application is destroyed
     */
    override fun onDestroy() {
        disposable.clear()
        super.onDestroy()
    }


    /**
     * Save jokes of listJoke in the bundle in case of rotation
     */
    override fun onSaveInstanceState(outState: Bundle)
    {
        val json = Json(JsonConfiguration.Stable)
        val jokesString=json.stringify(Joke.serializer().list,listJoke)
        val starIdString=json.stringify(String.serializer().list,logsStar)
        val shareIdString=json.stringify(String.serializer().list,logsShare)
        outState.putString("List_joke",jokesString)
        outState.putString("List_star",starIdString)
        outState.putString("List_share",shareIdString)
        super.onSaveInstanceState(outState)
    }


    /**
    * Create a joke with a JokeApiService and add it to the adapter for display
    */
    fun newJoke(numRepeat :Int ,progressToAppeare :Boolean)
    {



        val bar = findViewById<ProgressBar>(R.id.progress_bar)
        val jokeService = JokeApiServiceFactory.buildJokeApiService()
        val jokeCreated: Single<Joke> = jokeService.giveMeAJoke()


        val subscription = jokeCreated
            .repeat(numRepeat.toLong()) // display numRepeat jokes
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe{
                if(progressToAppeare){bar.visibility = VISIBLE}}
            .doAfterTerminate{if(progressToAppeare){bar.visibility = INVISIBLE}}
            .subscribeBy(
                onError = { },
                onNext = // new data
                {

                    listJoke.add(it)

                    //creattion of the model

                    val text = TextView(recycler.context)
                    text.setText(it.value)

                    val shareButton = ImageButton(recycler.context)

                    val starButton = ImageButton(recycler.context)
                    starButton.setImageResource(R.drawable.star_softpink_notpressed)
                    starButton.setTag(R.drawable.star_softpink_notpressed)

                    val newModel :JokeView.Model= JokeView.Model(text,shareButton,starButton,(this::onClickStar),(this::onClickShare))
                    val modelToPush :MutableList<JokeView.Model> = mutableListOf()
                    modelToPush.add(newModel)
                    adapt.updateData(modelToPush)
                })


        disposable.add(subscription)
    }


    /**
     * Update the file pref with a new joke preference
     */
    fun addPreference (oneJoke: Joke)
    {
        jokesPrefsDirectory = getSharedPreferences(filePref, Context.MODE_PRIVATE)
        val editorPref= jokesPrefsDirectory!!.edit() //editor to write into file pref


        //parse Joke to String thanks to Json
        val json = Json(JsonConfiguration.Stable)
        val jokeString=json.stringify(Joke.serializer(),oneJoke)
        editorPref.putString(oneJoke.id,jokeString)
        editorPref.apply()
    }



    /**
     * Update the file pref in removing a joke preference
     */
    fun removePreference (oneJoke: Joke)
    {
        jokesPrefsDirectory = getSharedPreferences(filePref, Context.MODE_PRIVATE)
        val editorPref= jokesPrefsDirectory!!.edit() //editor to write into file pref
        editorPref.remove(oneJoke.id)
        editorPref.apply()
    }


    /**
    *  Behavior for click on star button
    */
    fun onClickStar(v:View,valueJoke: String,holder: JokeView)
    {
        var id :String  = ""
        var jokeClicked : Joke?= null
        //find the id of the joke of value valueJoke
        listJoke.forEach{
            if(it.value==valueJoke)
            {
                id=it.id
                jokeClicked=it

            }
        }

        val jokeFound=jokeClicked

        if (logsStar.contains(id ) ) //already clicked
        {
            logsStar.remove(id)//remove from the star jokes list
            if(jokeFound!=null)
            {removePreference(jokeFound) }//remove the joke from the preference file

            val starBV=holder.findViewById<ImageButton>(R.id.star_button)
            starBV.setImageResource(R.drawable.star_softpink_notpressed) // put image star plenty
            starBV.setTag(R.drawable.star_softpink_notpressed)

        }


        else //not already clicked
        {
            logsStar.add(id) //add the id of the joke in logsStar list
            if (jokeFound!= null)addPreference(jokeFound) // add the joke to the preference file


            val starBV=holder.findViewById<ImageButton>(R.id.star_button)
            starBV.setImageResource(R.drawable.star_softpink) // put image star plenty
            starBV.setTag(R.drawable.star_softpink)

        }
    }

    /**
     *  Behavior for click on share button
     */
    fun onClickShare(v:View,valueJoke: String)
    {

        var id :String  = ""
        //find the id of the joke of value valueJoke
        listJoke.forEach{
            if(it.value==valueJoke)
            {
                id=it.id
            }
        }

        logsShare.add(id) //add the id of the joke in logsShare list

        //share of the joke
        val sendIntent = Intent().apply {
            action= Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, valueJoke) //text to sent
            type = "text/plain"
        }
        val shareIntent= Intent.createChooser(sendIntent, "Partager" )
        ContextCompat.startActivity(this, shareIntent, null)
    }

}


