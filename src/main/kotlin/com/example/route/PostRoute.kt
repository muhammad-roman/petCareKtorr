package com.example.route

import com.example.dao.post.PostDao
import com.example.dao.post.dao
import com.example.dao.post.daoPostUsuario
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
            call.respond(posts)
        }

        get("/imagenes/{postPhoto?}") {
            val imageName = call.parameters["postPhoto"]
            println(imageName)
            val file = File("./images/$imageName")
            println(file)
            if (file.exists()) {
                call.respondFile(File("./images/$imageName"))
            } else {
                call.respondText("Image not found", status = HttpStatusCode.NotFound)
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

        post {
            val data = call.receiveMultipart()
            var ofertas: Post? = null
            var fileName = ""
            val gson = Gson()

            var owner = 0
            var reciver = 0
            var offers = ""
            var tittle = ""
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
                                "owner" -> owner = part.value.toInt()
                                "reciver" -> reciver = part.value.toInt()
                                "offers" -> offers = part.value
                                "tittle" -> tittle = part.value
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
                """{"owner":${owner},"reciver":${reciver},"offers":${offers},"tittle":${tittle},"description":${description},"serviceType":${serviceType},"serviceTime":${serviceTime},"postDate":${postDate},"reward":${reward}, "location":${location}}""",
                Post::class.java
            )

/*
            val tesoroToPost = ofertas?.let { it1 ->
                dao.addNewPost(
                    it1.tittle.toInt(),
                    it.descripcion,
                    ofertas!!.latitud,
                    ofertas!!.longitud,
                    ofertas!!.valoracion,
                    fileName
                )
            }
            call.respondRedirect("/tesoros/${tesoroToPost?.postId}")
            
 */
        }


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
}



/*
fun Route.postRoute() {

        route("/posts") {
            /*
            GET que obtiene todos los post.
             */
            get {
                val postList = dao.allPosts()
                if (postList.isNotEmpty()) {
                    call.respond(postList)
                } else {
                    call.respondText("No se ha encontrado ofertas de trabajo", status = HttpStatusCode.OK)
                }
            }

            /*
            GET que busca post por nombre.
             */
            get("/{tittle?}") {
                val name = call.parameters["tittle"] ?: return@get call.respondText(
                    "Missing tittle",
                    status = HttpStatusCode.BadRequest
                )
                val posts = dao.allPosts() ?: return@get call.respondText(
                    "No hay tesoros con el nombre $name",
                    status = HttpStatusCode.NotFound
                )
                val postsRespond = mutableListOf<Post>()
                for (i in posts) {
                    if (name in i.tittle) {
                        postsRespond.add(i)
                    }
                }
                call.respond(postsRespond)
            }

            /*
            GET QUE BUSCA LOS POST DE UN USUARIO
            */
            get("/{user_id}/publicados") {
                if (call.parameters["user_id"].isNullOrBlank()) {
                    return@get call.respondText("El id del usuario no es correcto.", status = HttpStatusCode.BadRequest)
                }
                val user_id = call.parameters["user_id"]?.toInt()
                val postUsuarios = user_id?.let { it1 -> daoPostUsuario.postsEncontrados(it1) }
                if (postUsuarios != null) {
                    if (postUsuarios.isNotEmpty()) {
                        call.respond(postUsuarios)
                    } else {
                        val emptylist = listOf<PostUser>()
                        call.respond(emptylist)
                    }
                }
            }

            /*
            POST QUE AÑADE UN POST OFERTA DE TRABAJO
            */
            post {
                val data = call.receiveMultipart()
                var post: Post? = null
                var fileName = ""
                val gson = Gson()
                var titulo = ""
                var descripcion = ""
                var tipoServicio = ""
                var tiempoServicio = ""
                var fechaPost = ""
                var recompensa = ""
                var localizacion = ""


                data.forEachPart { part ->
                    when (part) {
                        is PartData.FormItem -> {
                            if (part.name == "post_data") {
                                post = gson.fromJson(part.value, Post::class.java)
                            } else {
                                    when (part.name) {
                                    "titulo" -> titulo = part.value
                                    "descripcion" -> descripcion = part.value
                                    "tipoServicio" -> tipoServicio = part.value
                                    "tiempoServicio" -> tiempoServicio = part.value
                                    "fechaPost" -> fechaPost = part.value
                                    "recompensa" -> recompensa = part.value
                                    "localizacion" -> localizacion = part.value

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
                post = gson.fromJson(
                    """{"titulo":${titulo},"foto":${fileName},"descripcion":${descripcion},"tipoServicio":${tipoServicio},"tiempoServicio":${tiempoServicio},"fechaPost":${fechaPost},"recompensa":${recompensa},"localizacion":${localizacion}}""",
                    Post::class.java
                )


                val postToPost = post?.let { it1 ->
                    dao.addNewPost(
                        it1.tittle,
                        post!!.owner,
                        post!!.reciver,
                        post!!.offers,
                        post!!.postPhoto,
                        post!!.description,
                        post!!.serviceType,
                        post!!.serviceTime,
                        post!!.postDate,
                        post!!.reward,
                        post!!.location
                    )
                }
                call.respondRedirect("/posts/${postToPost?.postId}")
            }


            put("/{post_id}") {
                val id = call.parameters["post_id"] ?: return@put call.respond(HttpStatusCode.BadRequest)
                val data = call.receiveMultipart()
                var post: Post?
                var fileName = ""
                val gson = Gson()
                var titulo = ""
                var foto = ""
                var descripcion = ""
                var tipoServicio = ""
                var tiempoServicio = ""
                var fechaPost = ""
                var recompensa = ""
                var localizacion = ""
                var fileUpdated = false
                data.forEachPart { part ->
                    when (part) {
                        is PartData.FormItem -> {
                            if (part.name == "post_data") {
                                post = gson.fromJson(part.value, Post::class.java)
                            } else {
                                when (part.name) {
                                    "titulo" -> titulo = part.value.replace("'", "")
                                    "foto" -> foto = part.value.replace("'", "")
                                    "descripcion" -> descripcion = part.value.replace("'", "")
                                    "tipoServicio" -> tipoServicio = part.value.replace("'", "")
                                    "tiempoServicio" -> tiempoServicio = part.value.replace("'", "")
                                    "fechaPost" -> fechaPost = part.value.replace("'", "")
                                    "recompensa" -> recompensa = part.value.replace("'", "")
                                    "localizacion" -> localizacion = part.value.replace("'", "")

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
                    post =
                        Post(id.toInt(),0,1,"", titulo, fileName, descripcion, tipoServicio, tiempoServicio, fechaPost, recompensa, localizacion)
                } else {
                    post = Post(id.toInt(),0,1,"", titulo, foto,descripcion, tipoServicio, tiempoServicio, fechaPost, recompensa, localizacion)
                }
                dao.editPost(
                    id.toInt(),
                    post!!.tittle,
                    post!!.postPhoto,
                    post!!.description,
                    post!!.serviceType,
                    post!!.serviceTime,
                    post!!.postDate,
                    post!!.reward,
                    post!!.location
                )
                call.respondText("Post con id $id modificado correctamente.", status = HttpStatusCode.Accepted)
            }

            /*
            DELETE QUE BORRA UN POST A PARTIR DE LA ID
            */
            delete("/{post_id}") {
                val id = call.parameters["post_id"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
                dao.deletePost(id.toInt())
                call.respondText("Post eliminado", status = HttpStatusCode.Accepted)
            }
            delete("/{usu_id}/encontrados/{post_id}") {
                val id = call.parameters["post_id"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
                val usu_id = call.parameters["usu_id"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
                daoPostUsuario.deletePost(id.toInt(), usu_id.toInt())
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

        get("/resenas/{imageName}") {
            val imageName = call.parameters["imageName"]
            var file = File("./src/main/resources/resenas/$imageName")
            if (file.exists()) {
                call.respondFile(File("./src/main/resources/resenas/$imageName"))
            } else {
                call.respondText("Image not found", status = HttpStatusCode.NotFound)
            }
        }
    }
}

 */






