package app.avoor.planbot

import com.google.gson.GsonBuilder
import app.avoor.planbot.data.auth.AuthTokenProvider
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitInstance {
    private const val BASE_URL = "https://avoor-app.oa.r.appspot.com"
    private const val LOCAL_URL = "http://192.168.10.80:8080/"

    /**
     * Get an instance of Retrofit.
     */
    fun getInstance(authTokenProvider: AuthTokenProvider, production: Boolean = true): Retrofit {
        // Create a custom Gson instance
        val gson = GsonBuilder()
            .setDateFormat("yyyy-MM-dd")
            .create()
        // Create a Retrofit builder
        val bld = Retrofit.Builder().baseUrl(if (production) BASE_URL else LOCAL_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
        // Create a custom OkHTTP client with an interceptor
        val client: OkHttpClient = OkHttpClient.Builder().addInterceptor { chain ->
            // Customize the request
            val reqBuilder: Request.Builder = chain.request().newBuilder()
                // Add a custom user agent
                .addHeader("User-Agent",
                    "PlanbotPlus/A${BuildConfig.VERSION_NAME}${(if(BuildConfig.DEBUG)"d" else "r")} (${BuildConfig.VERSION_CODE})")
            // If there is an authorization token, add it
            // runBlocking should be safe to use here as Retrofit runs okhttp3 requests
            // on another thread if used with suspend functions (which should be the case).
            runBlocking {
                val authToken = authTokenProvider.getAccessOrRefreshToken()
                if (authToken != null) {
                    reqBuilder.addHeader("Authorization", "Bearer $authToken")
                }
            }
            // Build the customized request and proceed
            chain.proceed(reqBuilder.build())
        }.build()
        // Add the custom client
        bld.client(client)
        // Build it and return the instance
        return bld.build()
    }
}