package com.example.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.jetbrains.exposed.sql.Table
import java.time.LocalDate


object LocalDateSerializer : KSerializer<LocalDate> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("LocalDate", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: LocalDate) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): LocalDate {
        return LocalDate.parse(decoder.decodeString())
    }
}

data class PostUser(
    val postUserId: Int,
    @Serializable(LocalDateSerializer::class)
    val postUserDate: String,
    val postId: Int,
    val id: Int
)
object PostUsersRow: Table(){
    val postUserId = integer("pu_id").autoIncrement()
    val postUserDate = varchar("pu_fecha",50)
    val postId = integer("post_id")
    val id = integer("user_id")

    override val primaryKey = PrimaryKey(postUserId)

}


