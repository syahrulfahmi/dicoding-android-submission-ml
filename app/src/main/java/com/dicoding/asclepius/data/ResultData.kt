package com.dicoding.asclepius.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ResultData(
    var confidenceScore: String,
    var labelResult: String
): Parcelable