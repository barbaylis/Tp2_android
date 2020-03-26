package m.tp2_chucknorrisjokes


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.ProgressBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
import java.util.concurrent.TimeUnit


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
    private var adapt = JokeAdapter(mutableListOf(),(this::newJoke))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recycler: RecyclerView = findViewById(R.id.recycler)
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapt

         newJoke()
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
        val bar = findViewById<ProgressBar>(R.id.progress_bar) // bar de progression, visible lorsque la joke est en cours de création
        val jokeService = JokeApiServiceFactory.buildJokeApiService()
        val jokeCreated: Single<Joke> = jokeService.giveMeAJoke()


        val subscription = jokeCreated
            .delay(2, TimeUnit.SECONDS)
            .repeat(10) /// 10 ajouts de jokes
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe{bar.visibility = VISIBLE}
            .doAfterTerminate{bar.visibility = INVISIBLE}
            .subscribeBy(
                onError = { println("error") },
                onNext = // nouvelle donnée
                {
                    println("joke caught:" + it)
                    adapt.addJoke(it)
                })


        disposable.add(subscription)
    }




}


