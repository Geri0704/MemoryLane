package com.example.memorylane.data

import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import java.util.UUID

class WeeklyStorageDO : RealmObject {
    @PrimaryKey
    var id: String = UUID.randomUUID().toString()
    var weekStartDate: String = ""
    var weekEndDate: String = ""
    var continuingPositives: RealmList<String> = realmListOf()
    var problems: RealmList<String> = realmListOf()
    var thingsToWorkOn: RealmList<String> = realmListOf()
}