package com.tomwyr.features.search

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.tomwyr.StreamerInfo
import com.tomwyr.features.common.Padding
import com.tomwyr.features.common.loadingView
import io.kvision.core.*
import io.kvision.html.*
import io.kvision.panel.SimplePanel
import io.kvision.state.bind
import io.kvision.utils.perc
import io.kvision.utils.px
import io.kvision.utils.rem

fun Container.searchView() {
    add(SearchView())
}

class SearchView : SimplePanel() {
    init {
        addAfterInsertHook { SearchModel.initialize() }

        width = 100.perc
        height = 100.perc
        marginTop = 4.rem
        display = Display.FLEX
        flexDirection = FlexDirection.COLUMN
        alignItems = AlignItems.CENTER

        div {
            width = 100.perc
            maxWidth = 384.px
            background = Background(Color.name(Col.WHITE))
            padding = 1.rem
            borderRadius = 0.75.rem
            boxShadow = BoxShadow(1.px, 1.px, 2.px, 2.px, Color("#00000033"))

            onClick { it.stopPropagation() }

            queryInput()
            validationMessage()
            searchResults()
        }
    }
}

private fun Div.queryInput() {
    searchInput {
        placeholder = "Streamer name..."

        SearchOverlay.overlayVisible.subscribe { visible ->
            if (visible) focus()
        }

        onInput {
            SearchModel.searchQueryInput.value = value ?: ""
        }
    }
}

private fun Div.validationMessage() {
    span().bind(SearchModel.searchQuery) {
        content = when (it) {
            is Ok -> null
            is Err -> when (it.error) {
                SearchQueryFailure.Empty -> null
                SearchQueryFailure.InvalidFormat -> "Only letters and digits are allowed."
                SearchQueryFailure.TooShort -> "At least 3 characters are required"
            }
        }
    }
}

private fun Div.searchResults() {
    div().bind(SearchModel.streamers) { state ->
        when (state) {
            StreamersState.Initial -> Unit

            StreamersState.Loading -> loadingView(
                    padding = Padding(top = 3.25.rem, bottom = 1.75.rem, left = 1.rem, right = 1.rem)
            )

            is StreamersState.Result -> when (state.result) {
                is Ok -> ul {
                    marginBottom = 0.px

                    state.result.value.forEach {
                        streamerTile(it)
                    }
                }

                is Err -> span("Something went wrong. Please try again.")
            }
        }
    }
}

private fun Container.streamerTile(streamerInfo: StreamerInfo) {
    li {
        display = Display.FLEX
        flexDirection = FlexDirection.ROW
        alignItems = AlignItems.CENTER
        cursor = Cursor.POINTER

        image(streamerInfo.imageUrl, className = "search-result-logo") {
            width = 24.px
            marginRight = 8.px
            borderRadius = 50.perc
        }

        span(streamerInfo.name)

        onClick {
            SearchOverlay.overlayVisible.value = false
            SearchModel.onStreamerClick(streamerInfo)
        }
    }
}
