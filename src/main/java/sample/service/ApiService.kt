package sample.service

import io.reactivex.Observable
import io.reactivex.Single
import jdk.nashorn.internal.objects.annotations.Getter
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.jackson.JacksonConverterFactory
import retrofit2.http.*
import sample.dto.ChangeSimulationDetailsRequest
import sample.dto.GetSavedBoardResponse
import sample.dto.SaveBoardRequest
import java.io.IOException
import org.reactivestreams.Subscriber
import okio.BufferedSource



interface ApiService {
    @GET(value = "all")
    fun getAllNames(): Observable<List<String>>

    @GET(value = "creator/{name}")
    fun uploadBoard(@Path("name") name: String): Observable<GetSavedBoardResponse>

    @POST(value = "save")
    fun saveBoard(@Body request: SaveBoardRequest) : Observable<Void>

    @GET(value = "{name}")
    fun openSimulation(@Path("name") name: String) :Observable<GetSavedBoardResponse>


    companion object {
        fun create(): ApiService {

            val retrofit = Retrofit.Builder()
                    .client(OkHttpClient())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(JacksonConverterFactory.create())
                    .baseUrl("http://localhost:8080/api/simulation/")
                    .build()

            return retrofit.create(ApiService::class.java)
        }


    }

}
