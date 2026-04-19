package dev.alejandrorosas.leaguepedia.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import retrofit2.http.GET
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

    @Serializable
    class CargoResponse(
        @SerialName("cargoquery") val cargoquery: List<CargoQueryItem> = emptyList(),
        @SerialName("error") val error: ApiError? = null,
    )

    @Serializable
    class QueryResponse(
        @SerialName("query") val query: QueryPages? = null,
        @SerialName("error") val error: ApiError? = null,
    )

    @Serializable
    class ApiError(
        @SerialName("code") val code: String,
        @SerialName("info") val info: String,
    )

    @Serializable
    class QueryPages(
        @SerialName("pages") val pages: Map<String, QueryPage>,
    )

    @Serializable
    class QueryPage(
        @SerialName("title") val title: String? = null,
        @SerialName("imageinfo") val imageInfo: List<ImageInfo>? = null,
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
