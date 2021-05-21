package entities

import javax.persistence.Column
import javax.persistence.Id
import javax.persistence.Table

@Table(name = "user_photos")
class UserPhotos {

    @Id
    @Column(name = "id")
    var id: Long? = null

    @Column(name = "url")
    lateinit var url: String

    @Column(name = "user_id")
    var userId: Long = 0

    @Column(name = "author")
    lateinit var author: String

}