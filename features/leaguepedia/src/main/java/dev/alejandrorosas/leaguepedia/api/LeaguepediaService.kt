package dev.alejandrorosas.leaguepedia.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface LeaguepediaService {
    @GET("api.php?action=cargoquery&limit=50&format=json&utf8=1")
    suspend fun getCargoQuery(
        @Query("tables") tables: String,
        @Query("fields") fields: String,
        @Query("where") where: String,
        @Query("join_on") joinOn: String,
        @Query("order_by") orderBy: String? = null,
    ): CargoResponse

    @GET("api.php?action=query&format=json&prop=imageinfo&iiprop=url")
    suspend fun getImageInfo(
        @Query("titles") title: String,
    ): QueryResponse

    @POST("api.php?action=login&format=json")
    @FormUrlEncoded
    suspend fun login(
        @Field("lgname") name: String,
        @Field("lgpassword") password: String,
        @Field("lgtoken") token: String,
    ): LoginResponse

    @GET("api.php?action=query&meta=tokens&type=login&format=json")
    suspend fun getToken(): QueryTokensResponse

    @Serializable
    class CargoResponse(
        @SerialName("cargoquery") val cargoquery: List<CargoQueryItem>,
    )

    @Serializable
    class QueryResponse(
        @SerialName("query") val query: QueryPages,
    )

    @Serializable
    class QueryTokensResponse(
        @SerialName("query") val query: QueryTokens,
    )

    @Serializable
    class QueryTokens(
        @SerialName("tokens") val tokens: Tokens,
    )

    @Serializable
    class Tokens(
        @SerialName("logintoken") val logintoken: String,
    )

    @Serializable
    class LoginResponse(
        @SerialName("login") val login: Login?,
    )

    @Serializable
    class Login(
        @SerialName("lguserid") val userId: String?,
        @SerialName("result") val result: String,
    )

    @Serializable
    class QueryPages(
        @SerialName("pages") val pages: Map<String, QueryPage>,
    )

    @Serializable
    class QueryPage(
        @SerialName("imageinfo") val imageInfo: List<ImageInfo>,
    )

    @Serializable
    class ImageInfo(
        @SerialName("url") val url: String,
    )

    @Serializable
    class CargoQueryItem(
        @SerialName("title") val content: Map<String, String?>,
    )
}
