package com.example.route

import com.example.dao.post.PostDao
import com.example.model.*
import com.google.gson.Gson
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.SerializationException
import java.io.File


fun Routing.postRoute(dao: PostDao) {
    route("/posts") {
        get {
            val posts = dao.allPosts()
            if (posts.isNotEmpty()) {
                call.respond(posts)
            } else {
                call.respondText("No se ha encontrado ofertas de trabajo", status = HttpStatusCode.OK)
            }
        }

        get("/{tittle?}") {
            val name = call.parameters["tittle"] ?: return@get call.respondText(
                "Missing title",
                status = HttpStatusCode.BadRequest
            )
            val tesoros = dao.allPosts() ?: return@get call.respondText(
                "No hay tesoros con el nombre $name",
                status = HttpStatusCode.NotFound
            )
            val tesorosRespond = mutableListOf<Post>()
            for (i in tesoros) {
                if (name in i.tittle) {
                    tesorosRespond.add(i)
                }
            }
        }


        get("/{postId}") {
            val postId = call.parameters["postId"]?.toIntOrNull()
            if (postId == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid postId")
            } else {
                val post = dao.postId(postId)
                if (post == null) {
                    call.respond(HttpStatusCode.NotFound, "Post not found")
                } else {
                    call.respond(post)
                }
            }
        }

        post {
            val data = call.receiveMultipart()
            var ofertas: Post? = null
            val gson = Gson()

            var tittle = ""
            var owner = 0
            var reciver = 0
            var offers = ""
            var fileName = ""
            var description = ""
            var serviceType = ""
            var serviceTime = ""
            var postDate = ""
            var reward = ""
            var location = ""

            data.forEachPart { part ->
                when (part) {
                    is PartData.FormItem -> {
                        if (part.name == "post_data") {
                            ofertas = gson.fromJson(part.value, Post::class.java)
                        } else {
                            when (part.name) {
                                "tittle" -> tittle = part.value
                                "owner" -> owner = part.value.toInt()
                                "reciver" -> reciver = part.value.toInt()
                                "offers" -> offers = part.value
                                "description" -> description = part.value
                                "serviceType" -> serviceType = part.value
                                "serviceTime" -> serviceTime = part.value
                                "postDate" -> postDate = part.value
                                "reward" -> reward = part.value
                                "location" -> location = part.value
                            }
                        }
                    }

                    is PartData.FileItem -> {
                        fileName = part.originalFileName as String
                        var fileBytes = part.streamProvider().readBytes()
                        File("./src/main/resources/imagenes/$fileName").writeBytes(fileBytes)
                    }

                    else -> {}
                }
            }
            ofertas = gson.fromJson(
                """{"tittle":${tittle},"owner":${owner},"reciver":${reciver},"offers":${offers},"description":${description},"serviceType":${serviceType},"serviceTime":${serviceTime},"postDate":${postDate},"reward":${reward}, "location":${location}}""",
                Post::class.java
            )

            val ofertaToPost = ofertas?.let { it1 ->
                dao.addNewPost(
                    it1.tittle,
                    ofertas!!.owner,
                    ofertas!!.reciver,
                    ofertas!!.offers,
                    fileName,
                    ofertas!!.description,
                    ofertas!!.serviceType,
                    ofertas!!.serviceTime,
                    ofertas!!.postDate,
                    ofertas!!.reward,
                    ofertas!!.location
                )
            }
            call.respondRedirect("/posts/${ofertaToPost?.postId}")

        }

        //NO QUITAR ESTA PARTE DE CODIGO, NECESARIA POR SI SE HA DE AÑADIR DESDE LA API ALGUNA OFERTA
        /*
        post {
            val post = try {
                call.receive<Post>()
            } catch (e: SerializationException) {
                call.respond(HttpStatusCode.BadRequest, "Invalid post data")
                return@post
            }
            val addedPost = dao.addNewPost(
                owner = post.owner,
                reciver = post.reciver,
                offers = post.offers,
                tittle = post.tittle,
                postPhoto = post.postPhoto,
                description = post.description,
                serviceType = post.serviceType,
                serviceTime = post.serviceTime,
                postDate = post.postDate,
                reward = post.reward,
                location = post.location
            )
            if (addedPost == null) {
                call.respond(HttpStatusCode.InternalServerError, "Failed to add post")
            } else {
                call.respond(addedPost)
            }
        }

         */


        put("/{post_id}") {
            val id = call.parameters["post_id"] ?: return@put call.respond(HttpStatusCode.BadRequest)
            val data = call.receiveMultipart()
            var ofertas: Post?
            val gson = Gson()
            var tittle = ""
            var owner = 0
            var reciver = 0
            var offers = ""
            var fileName = ""
            var postPhoto = ""
            var description = ""
            var serviceType = ""
            var serviceTime = ""
            var postDate = ""
            var reward = ""
            var location = ""
            var fileUpdated = false

            data.forEachPart { part ->
                when (part) {
                    is PartData.FormItem -> {
                        if (part.name == "post_data") {
                            ofertas = gson.fromJson(part.value, Post::class.java)
                        } else {
                            when (part.name) {
                                "tittle" -> tittle = part.value.replace("'", "")
                                "owner" -> owner = part.value.toInt()
                                "reciver" -> reciver = part.value.toInt()
                                "offers" -> offers = part.value
                                "postPhoto" -> postPhoto = part.value.replace("'", "")
                                "description" -> description = part.value.replace("'", "")
                                "serviceType" -> serviceType = part.value.replace("'", "")
                                "serviceTime" -> serviceTime = part.value.replace("'", "")
                                "postDate" -> postDate = part.value.replace("'", "")
                                "reward" -> reward = part.value.replace("'", "")
                                "location" -> location = part.value.replace("'", "")
                            }
                        }

                    }

                    is PartData.FileItem -> {
                        fileName = part.originalFileName as String
                        fileUpdated = true
                        var fileBytes = part.streamProvider().readBytes()
                        File("./src/main/resources/imagenes/$fileName").writeBytes(fileBytes)
                    }

                    else -> {}
                }
            }
            if (fileUpdated) {
                ofertas =
                    Post(id.toInt(), tittle, owner, reciver, offers, fileName, description, serviceType, serviceTime, postDate, reward, location)
            } else {
                ofertas = Post(id.toInt(), tittle, owner, reciver, offers, postPhoto, description, serviceType, serviceTime, postDate, reward, location)
            }
            dao.editPost(
                id.toInt(),
                ofertas!!.tittle,
                ofertas!!.postPhoto,
                ofertas!!.description,
                ofertas!!.serviceType,
                ofertas!!.serviceTime,
                ofertas!!.postDate,
                ofertas!!.reward,
                ofertas!!.location
            )
            call.respondText("Oferta de trabajo con id $id modificado correctamente.", status = HttpStatusCode.Accepted)
        }

        put("/{postId}") {
            val postId = call.parameters["postId"]?.toIntOrNull()
            if (postId == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid postId")
            } else {
                val post = try {
                    call.receive<Post>()
                } catch (e: SerializationException) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid post data")
                    return@put
                }
                val updatedPost = dao.editPost(
                    postId = postId,
                    tittle = post.tittle,
                    postPhoto = post.postPhoto,
                    description = post.description,
                    serviceType = post.serviceType,
                    serviceTime = post.serviceTime,
                    postDate = post.postDate,
                    reward = post.reward,
                    location = post.location
                )
                if (updatedPost) {
                    call.respond(HttpStatusCode.OK, "Post updated successfully")
                } else {
                    call.respond(HttpStatusCode.InternalServerError, "Failed to update post")
                }
            }
        }



        delete("/{postId}") {
            val id = call.parameters["postId"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
            dao.deletePost(id.toInt())
            call.respondText("Oferta de trabajo eliminada", status = HttpStatusCode.Accepted)
        }

        delete("/{postId}") {
            val postId = call.parameters["postId"]?.toIntOrNull()
            if (postId == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid postId")
            } else {
                val deletedPost = dao.deletePost(postId)
                if (deletedPost) {
                    call.respond(HttpStatusCode.OK, "Post deleted successfully")
                } else {
                    call.respond(HttpStatusCode.InternalServerError, "Failed to delete post")
                }
            }
        }



    }

    route("/posts") {

        get("/imagenes/{imageName}") {
            val imageName = call.parameters["imageName"]
            println(imageName)
            val file = File("./images/$imageName")
            println(file)
            if (file.exists()) {
                call.respondFile(File("./images/$imageName"))
            } else {
                call.respondText("Image not found", status = HttpStatusCode.NotFound)
            }
        }

        get("/imagenespost/{imageName}") {
            val imageName = call.parameters["imageName"]
            var file = File("./src/main/resources/imagenes/$imageName")
            if (file.exists()) {
                call.respondFile(File("./src/main/resources/imagenes/$imageName"))
            } else {
                call.respondText("Image not found", status = HttpStatusCode.NotFound)
            }
        }
    }

}