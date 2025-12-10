package com.cases.carefull.features.carefullcontents.util

import com.cases.carefull.domain.model.feed.FeedException
import com.cases.carefull.features.carefullcommon.R
import com.cases.carefull.features.carefullcontents.util.UiText.StringResource

fun Throwable.asUiText(): UiText {
    return when (this) {
        is FeedException.NotFoundPost -> StringResource(R.string.error_post_load_failed)
        is FeedException.NotFoundRank -> StringResource(R.string.error_no_ranking_data)
        is FeedException.Unauthorized -> StringResource(R.string.error_no_permission)
        is FeedException.NetworkError -> StringResource(R.string.error_fetch_data_failed)

        else -> StringResource(R.string.error_unknown)
    }
}
