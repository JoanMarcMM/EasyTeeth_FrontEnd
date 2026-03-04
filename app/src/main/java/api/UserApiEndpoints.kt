package api

import model.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface UserApiEndpoints {
    @POST("user/login")
    suspend fun login(@Body user: User): Response<User>
}