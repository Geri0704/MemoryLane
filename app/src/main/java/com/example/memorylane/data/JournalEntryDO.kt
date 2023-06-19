package com.example.memorylane.data

import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import java.util.UUID

class JournalEntryDO : RealmObject {
    @PrimaryKey
    var id: String = UUID.randomUUID().toString()
    var date: String = ""
    var prompt: String = ""
    var entry: String = ""
    var happiness: Float = 0f
    var themes: RealmList<String> = realmListOf()
}
