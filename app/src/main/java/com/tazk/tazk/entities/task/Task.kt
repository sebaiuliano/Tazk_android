package com.tazk.tazk.entities.task

import java.util.*

class Task (
    var id: Long,
    var title: String,
    var createdAt: GregorianCalendar,
    var modifiedAt: GregorianCalendar,
    var deleted: Boolean,
)