package m.tp2_chucknorrisjokes


import kotlinx.serialization.json.Json

import kotlinx.serialization.json.JsonConfiguration

import org.junit.Assert

import org.junit.Test





/**

 * /!\ THIS FILE MUST NOT BE EDITED /!\

 */

class JokeSerializationTest {

    private val baseJson =

        """{"icon_url":"https://assets.chucknorris.host/img/avatar/chuck-norris.png","id":"kvbADxuyS36ug4MJ7KMBMA","url":"https://api.chucknorris.io/jokes/kvbADxuyS36ug4MJ7KMBMA","value":"Chuck Norris puts mustard gas on his hotdogs."}"""



    private val baseJoke = Joke(

        categories = emptyList(),

        createdAt = "2020-01-05 13:42:29.296379",

        iconUrl = "https://assets.chucknorris.host/img/avatar/chuck-norris.png",

        id = "p3GHoau2SLGTv04XhtIeeg",

        updatedAt = "2020-01-05 13:42:29.296379",

        url = "https://api.chucknorris.io/jokes/p3GHoau2SLGTv04XhtIeeg",

        value = "Which came first - the chicken or the egg? Chuck Norris said it was the beer which came first, the hell with these damn animals!!!"

    )



    @Test

    fun `serialization is correct`() {

        val json = Json(JsonConfiguration.Stable).stringify(Joke.serializer(), baseJoke)

        Assert.assertEquals(baseJson, json)

    }



    @Test

    fun `deserialization is correct`() {

        val joke = Json(JsonConfiguration.Stable).parse(Joke.serializer(), baseJson)

        Assert.assertEquals(baseJoke, joke)

    }

}