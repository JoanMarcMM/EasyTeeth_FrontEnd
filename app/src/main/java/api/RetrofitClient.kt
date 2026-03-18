package api
import com.example.easyteeth.api.*

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASE_URL = "http://10.0.2.2:8080/"

    val instance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val patientApi: PatientApiEndpoints by lazy {
        instance.create(PatientApiEndpoints::class.java)
    }

    val backgroundApi: BackgroundApiEndpoints by lazy {
        instance.create(BackgroundApiEndpoints::class.java)
    }
    val odontogramApi: OdontogramApiEndpoints by lazy {
        instance.create(OdontogramApiEndpoints::class.java)
    }
    val imageApi: ImageApiEndpoints by lazy {
        instance.create(ImageApiEndpoints::class.java)
    }

    val documentApi: DocumentApiEndpoints by lazy {
        instance.create(DocumentApiEndpoints::class.java)
    }
}