package com.example.memorylane.data

import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import java.util.ArrayList
import java.util.UUID

class JournalEntryDO : RealmObject {
    @PrimaryKey
    var id: String = UUID.randomUUID().toString()
    var date: String = ""
    var prompt: String = ""
    var entry: String = ""
    var happinessRating: Float = 0f
    var themes: RealmList<String> = realmListOf()
    var positives: RealmList<String> = realmListOf()
    var negatives: RealmList<String> = realmListOf()
    var workOn: RealmList<String> = realmListOf()
}

class JournalEntryResponseDO {
    @PrimaryKey
    var id: String = UUID.randomUUID().toString()
    var date: String = ""
    var prompt: String = ""
    var entry: String = ""
    var happinessRating: Float = 0f
    var themes: ArrayList<String> = ArrayList()
    var positives: ArrayList<String> = ArrayList()
    var negatives: ArrayList<String> = ArrayList()
    var workOn: ArrayList<String> = ArrayList()
}

//    weeks = RealmList(jsonObject.getJSONArray("weeks").toList().map { it as Int })
