package m.tp2_chucknorrisjokes


import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.ImageButton
import android.widget.ProgressBar
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

            //restore old stared jokes list
            if (jokesStaredString!=null)
            {
                val idSaved = json.parse(String.serializer().list, jokesStaredString)
                adapt.logsStar=idSaved.toMutableList()

            }

            //restore old shared jokes list
            if(jokesSharedString!= null)
            {
                val jokesShared = json.parse(String.serializer().list, jokesSharedString)
                adapt.logsShare=jokesShared.toMutableList()
            }

            // restore old jokes displayed
            if(jokesSavedString!=null)
            {
                val jokesSaved = json.parse(Joke.serializer().list, jokesSavedString)
               jokesSaved.forEach {

                   // check if the jokes was shared
                    if(adapt.logsStar.contains(it.id))
                    {
                        adapt.addJoke(it,true) // add the joke

                        //restore image star button
                        val viewHolder=recycler.findViewHolderForAdapterPosition(adapt.listJokes.indexOf(it))
                        if (viewHolder!=null)
                        {
                            val starButton =viewHolder.itemView.findViewById(R.id.star_button) as ImageButton
                            starButton.setImageResource(R.drawable.star_softpink_notpressed) // put image star border
                            adapt.notifyItemChanged(adapt.itemCount)
                        }

                    }
                    else
                    { adapt.addJoke(it,false) }
                }

            }
        }
        else{

                /* management of file jokes preferences */

                if (jokesPrefsDirectory!= null)
                {
                    val jokesPref :Map<String,*> = jokesPrefsDirectory!!.getAll()

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

                                adapt.logsStar.add(joke.id) // add it's id to the logs_star list
                                adapt.addJoke(joke,true) // display joke



                            }
                        }
                    }

                }


            if (numJokeToDisplay !=0)
              {
                  newJoke(numJokeToDisplay,true)//display new Joke up to 10 jokes in all
              }
        }


        // for swipe to refresh, when restart the application, keep old preferences
        val refresh  : SwipeRefreshLayout = findViewById(R.id.refresh)
        refresh.setColorSchemeColors(Color.BLACK)

        refresh.setOnRefreshListener (
             object : SwipeRefreshLayout.OnRefreshListener {
                 override fun onRefresh() {

                     adapt.listJokes.clear()
                     val newadapt = JokeAdapter(
                         mutableListOf(),
                         (this@MainActivity::newJoke),
                         (this@MainActivity::addPreference),
                         (this@MainActivity::removePreference)
                     )
                     adapt = newadapt
                     recycler.adapter = adapt
                     newJoke(10,false)
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
     * Save jokes,logStar and lodShare from the adapter list in the bundle in case of rotation
     */
    override fun onSaveInstanceState(outState: Bundle)
    {
        val json = Json(JsonConfiguration.Stable)
        val jokesString=json.stringify(Joke.serializer().list,adapt.listJokes)
        val starIdString=json.stringify(String.serializer().list,adapt.logsStar)
        val shareIdString=json.stringify(String.serializer().list,adapt.logsShare)
        outState.putString("List_joke",jokesString)
        outState.putString("List_star",starIdString)
        outState.putString("List_share",shareIdString)
        super.onSaveInstanceState(outState)
    }
//

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
                    adapt.addJoke(it,false)
                })


        disposable.add(subscription)
    }

    /**
     * update the file pref with a new joke preference
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
     * update the file pref in removing a joke preference
     */
    fun removePreference (oneJoke: Joke)
    {
        jokesPrefsDirectory = getSharedPreferences(filePref, Context.MODE_PRIVATE)
        val editorPref= jokesPrefsDirectory!!.edit() //editor to write into file pref
        editorPref.remove(oneJoke.id)
        editorPref.apply()
    }


}


