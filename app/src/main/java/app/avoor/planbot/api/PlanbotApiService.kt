package app.avoor.planbot.api

import app.avoor.planbot.api.models.AccessTokenResponse
import app.avoor.planbot.api.models.UserApi
import app.avoor.planbot.api.models.UserWithoutTokens
import app.avoor.planbot.api.requests.AddPlancoinsBody
import app.avoor.planbot.api.requests.CreateTaskBody
import app.avoor.planbot.api.requests.EmailPasswordBody
import app.avoor.planbot.api.requests.EmailPasswordNameBody
import app.avoor.planbot.api.requests.FeedbackBody
import app.avoor.planbot.api.requests.StreakUpdateBody
import app.avoor.planbot.api.requests.TokenBody
import app.avoor.planbot.api.requests.UpdateTaskBody
import app.avoor.planbot.api.requests.UseStreakFreezeBody
import app.avoor.planbot.api.responses.AddPlancoinsResponse
import app.avoor.planbot.api.responses.BouncerResponse
import app.avoor.planbot.api.responses.CreateTaskResponse
import app.avoor.planbot.api.responses.MessageResponse
import app.avoor.planbot.api.responses.PlancoinHistoryResponse
import app.avoor.planbot.api.responses.SignupEndpointResponse
import app.avoor.planbot.api.responses.StreakFreezeResponse
import app.avoor.planbot.api.responses.StreakResponse
import app.avoor.planbot.api.responses.StreakUpdateResponse
import app.avoor.planbot.api.responses.TasksResponse
import app.avoor.planbot.api.responses.VerifyEndpointResponse
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface PlanbotApiService {
    // Authentication Routes
    @POST("api/register")
    @Headers("Content-Type: application/json;charset=UTF-8")
    suspend fun signup(@Body epnb: EmailPasswordNameBody): SignupEndpointResponse

    @POST("api/login")
    @Headers("Content-Type: application/json;charset=UTF-8")
    suspend fun login(@Body epb: EmailPasswordBody): UserApi

    @POST("api/confirm")
    @Headers("Content-Type: application/json;charset=UTF-8")
    suspend fun confirm(@Body tb: TokenBody): VerifyEndpointResponse

    @POST("api/resend")
    @Headers("Content-Type: application/json;charset=UTF-8")
    suspend fun resendVerifyEmail(): MessageResponse

    @POST("api/refreshToken")
    @Headers("Content-Type: application/json;charset=UTF-8")
    suspend fun refreshToken(): AccessTokenResponse

    // User Routes
    @GET("api/me")
    @Headers("Content-Type: application/json;charset=UTF-8")
    suspend fun retrieveCurrentUser(): UserWithoutTokens

    @DELETE("api/me")
    @Headers("Content-Type: application/json;charset=UTF-8")
    suspend fun deleteMe(): MessageResponse

    @POST("api/setPicture")
    @Multipart
    suspend fun uploadPicture(@Part file: MultipartBody.Part): VerifyEndpointResponse

    // Task Management Routes
    @GET("api/tasks")
    @Headers("Content-Type: application/json;charset=UTF-8")
    suspend fun getTasks(): TasksResponse

    @POST("api/tasks")
    @Headers("Content-Type: application/json;charset=UTF-8")
    suspend fun createTask(@Body taskBody: CreateTaskBody): CreateTaskResponse

    @PUT("api/tasks/{task_id}")
    @Headers("Content-Type: application/json;charset=UTF-8")
    suspend fun updateTask(@Path("task_id") taskId: Int, @Body taskBody: UpdateTaskBody): MessageResponse

    @DELETE("api/tasks/{task_id}")
    @Headers("Content-Type: application/json;charset=UTF-8")
    suspend fun deleteTask(@Path("task_id") taskId: Int): MessageResponse

    // Plancoin Routes
    @POST("api/plancoins/add")
    @Headers("Content-Type: application/json;charset=UTF-8")
    suspend fun addPlancoins(@Body body: AddPlancoinsBody): AddPlancoinsResponse

    @GET("api/plancoins/history")
    @Headers("Content-Type: application/json;charset=UTF-8")
    suspend fun getPlancoinHistory(): PlancoinHistoryResponse

    // Feedback Routes
    @POST("api/sendFeedback")
    @Headers("Content-Type: application/json;charset=UTF-8")
    suspend fun sendFeedback(@Body feedbackBody: FeedbackBody): MessageResponse

    // Streak Routes
    @GET("api/streaks")
    @Headers("Content-Type: application/json;charset=UTF-8")
    suspend fun getStreak(): StreakResponse

    @POST("api/streaks")
    @Headers("Content-Type: application/json;charset=UTF-8")
    suspend fun postStreakUpdate(@Body body: StreakUpdateBody): StreakUpdateResponse

    @POST("api/streaks/freeze")
    @Headers("Content-Type: application/json;charset=UTF-8")
    suspend fun useStreakFreeze(@Body body: UseStreakFreezeBody): StreakFreezeResponse

    @GET("api/bouncer")
    @Headers("Content-Type: application/json;charset=UTF-8")
    suspend fun getBouncerResponse(): BouncerResponse
}