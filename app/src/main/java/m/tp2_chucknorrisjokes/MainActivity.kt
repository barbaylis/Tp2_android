package m.tp2_chucknorrisjokes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.serialization.*
import kotlinx.serialization.Optional
import kotlinx.serialization.json.Json
import m.tp2_chucknorrisjokes.ListJoke.listJokes
import okhttp3.MediaType
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.http.GET
import io.reactivex.schedulers.Schedulers


import java.io.File
import java.util.*

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

    private var disposable = CompositeDisposable()
    private var adapt = JokeAdapter(mutableListOf())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recycler: RecyclerView = findViewById(R.id.recycler)
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapt

         newJoke()

        //button, display a joke
        val button = findViewById<Button>(R.id.button_add)
        button.setOnClickListener(object : View.OnClickListener
        {
            override fun onClick(v: View)
            {
                newJoke()
            }
        })



    }

    override fun onDestroy() {
        disposable.clear()
        super.onDestroy()
    }

    /**
     * Create a joke with a JokeApiService and add it to the adapter for display
     */
    fun newJoke()
    {
        val JokeService = JokeApiServiceFactory.buildJokeApiService()
        val jokeCreated: Single<Joke> = JokeService.giveMeAJoke()


        val subscription = jokeCreated
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onError = { println("error") },
                onSuccess = {
                    println("joke caught:" + it)
                    adapt.addJoke(it)
                })

        disposable.add(subscription)

    }






}


