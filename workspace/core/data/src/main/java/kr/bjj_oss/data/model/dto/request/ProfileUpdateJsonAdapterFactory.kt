package kr.bjj_oss.data.model.dto.request

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi
import java.lang.reflect.Type

/** Preserve omission for untouched fields, but send an explicit null when a favorite is cleared. */
internal class ProfileUpdateJsonAdapterFactory : JsonAdapter.Factory {
    override fun create(type: Type, annotations: Set<Annotation>, moshi: Moshi): JsonAdapter<*>? {
        if (type != UpdateCommunityProfileRequest::class.java || annotations.isNotEmpty()) return null
        val delegate = moshi.nextAdapter<UpdateCommunityProfileRequest>(this, type, annotations)
        val valueAdapter = moshi.adapter(Any::class.java)
        return object : JsonAdapter<UpdateCommunityProfileRequest>() {
            override fun fromJson(reader: JsonReader): UpdateCommunityProfileRequest? = delegate.fromJson(reader)
            override fun toJson(writer: JsonWriter, value: UpdateCommunityProfileRequest?) {
                if (value == null) { writer.nullValue(); return }
                val field = when (value.profileRequestType) {
                    "POSITION" -> "favoritePosition"
                    "TECHNIQUE" -> "favoriteTechnique"
                    "SUBMISSION" -> "favoriteSubmission"
                    else -> null
                }
                if (field == null) { delegate.toJson(writer, value); return }
                @Suppress("UNCHECKED_CAST")
                val fields = (delegate.toJsonValue(value) as Map<String, Any?>).toMutableMap()
                if (!fields.containsKey(field)) fields[field] = null
                val previous = writer.serializeNulls
                writer.serializeNulls = true
                try { valueAdapter.toJson(writer, fields) } finally { writer.serializeNulls = previous }
            }
        }
    }
}
