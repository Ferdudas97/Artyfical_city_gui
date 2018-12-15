package sample.service

import io.reactivex.Observable
import io.reactivex.Single
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.jackson.JacksonConverterFactory
import retrofit2.http.*
import sample.dto.GetSavedBoardResponse
import sample.dto.SaveBoardRequest

interface ApiService {
    @GET(value = "all")
    fun getAllNames(): Observable<List<String>>

    @GET(value = "open/{name}")
    fun uploadBoard(@Path("name") name: String): Observable<GetSavedBoardResponse>

    @POST(value = "save")
    fun saveBoard(@Body request: SaveBoardRequest) : Observable<Void>

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
