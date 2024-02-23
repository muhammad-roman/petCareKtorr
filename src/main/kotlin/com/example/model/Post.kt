package com.example.model
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

object PostRow: Table(name = "posts"){
    val tittle = varchar(name = "tittle_post", length = 30)
    val postId = integer(name = "post_id").autoIncrement()
    val owner = integer(name = "owner_id")
    val reciver = integer(name = "reciver_id")
    val offers = varchar(name = "offer_name", length = 250)
    val postPhoto = varchar(name = "photo_post", length = 500)
    val description = text(name = "description_post").default(
        defaultValue = "Hey, What's up ? Look this new offer"
    )
    val serviceType = varchar(name = "serviceType_post", length = 30)
    val serviceTime = varchar(name = "serviceTime_post", length = 30)
    val postDate = varchar(name = "date_post", length = 30)
    val reward = varchar(name = "reward_post", length = 30)
    var location = varchar(name = "location_post", length = 100)

    override val primaryKey: PrimaryKey?
        get() = PrimaryKey(postId)

}

@Serializable
data class Post(
    var postId: Int = 0,
    var tittle: String = "",
    var owner: Int = 0,
    var reciver: Int = 0,
    var offers: String = "",
    var postPhoto: String = "",
    var description: String = "",
    var serviceType: String = "",
    var serviceTime: String = "",
    var postDate: String = "",
    var reward: String = "",
    var location: String = ""

    /*
    // var ubi: Ubi,
    var owner: User?,
    var reciver: User,
    var offers: List<User>,
     */
)

val postsStorage= mutableListOf<Post>()

