package com.example.katsumikusumi.instagramcloneapp.Models

//ex) data class User(val name : String = defaultParam, val age : Int)
data class Comment(
        var comment : String? = null,
        var user_id : String? = null,
        var likes : List<Like>? = null,
        var date_created : String? = null
) {

    override fun toString(): String {
        return "Comment(comment=$comment," +
                " user_id=$user_id," +
                " likes=$likes," +
                " date_created=$date_created)"
    }
}