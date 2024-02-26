package com.example.qbf

import com.google.firebase.firestore.DocumentId

data class Bank(
    var Name: String? = null,
    var Address: String? = null,
    var Email: String? = null,
    var availabilityStatus: Boolean,
    var newStatus: Boolean,
    @DocumentId var Docid: String? = null){
    // No-argument constructor required for Firestore deserialization
    constructor() : this(
        Name = "",
        Address = "",
        availabilityStatus = false,
        newStatus = false

    )
}
