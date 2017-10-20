package com.vedro401.reallifeachievement.utils

import android.view.LayoutInflater
import android.view.ViewGroup

/**
 * Created by someone on 18.09.17.
 */

val FIRETAG = "FIRETAG"
val LOGTAG = "LOGTAG"
val RXRVTAG = "rxrv"
val CAKE_HUNTER = "cakeHunter"
val RXICL = "rxicl"
val STORY = "myStory"

fun coolBigNumbers(num : String) : String = coolBigNumbers(num.toLong())

fun ViewGroup.inflate(layoutRes: Int) =
        LayoutInflater.from(context).inflate(layoutRes, this, false)

fun coolBigNumbers(num : Long) : String{
    if(num <= 999)
        return num.toString()
    if(num in 1000..999999)
        return "${num/1000}k"
    if(num in 1000000..999999999)
        return "${num/1000000}m"
    return "Many."
}

fun wordDifficulty(v : Int) = when(v){
    in 0..10  -> "Ez"
    in 11..20 -> "Easy"
    in 21..30 -> "Normas"
    in 31..40 -> "Norm"
    in 41..50 -> "Normal"
    in 51..60 -> "So it's done."
    in 61..70 -> "I did it!"
    in 71..80 -> "Finally I did it!! "
    in 81..90 -> "Ppc pot"
    in 91..100-> "Hard as fuck"
    else -> {
        "Impossible"
    }
}

fun randomTitle() : String {
    val whatToDo = arrayOf("Catch the", "Create the", "Kill the", "Fight with","Find the",
                           "Sing to", "Dance with", "Eat the", "Bite the", "Conquer the" )
    val which = arrayOf("fat","dirty","evil","dark","sweet",
                        "homeless","rich","dead","bearded","retarded","hot", "cold", "frozen",
            "white", "black")
    val who = arrayOf("zombie", "spider", "robot", "alcoholic", "punk", "pussy", "cop",
            "Gabe Newell", "android developer", "pig")

    return "${whatToDo[(Math.random()*whatToDo.size).toInt()]} " +
            "${which[(Math.random()*which.size).toInt()]} " +
            "${who[(Math.random()*who.size).toInt()]}"
}

