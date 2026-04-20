package api

import com.example.easyteeth.model.User
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

data class ChangePasswordRequest(
    val oldPassword: String,
    val newPassword: String
)

interface UserApiEndpoints {
    @POST("user/login")
    suspend fun login(@Body user: User): Response<User>

    @GET("user/{id}")
    suspend fun getUserById(@Path("id") userId: Long): Response<User>

    @PUT("user/{id}/password")
    suspend fun changePassword(@Path("id") userId: Long, @Body request: ChangePasswordRequest): Response<ResponseBody>
}
