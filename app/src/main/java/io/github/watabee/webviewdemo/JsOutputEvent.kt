package io.github.watabee.webviewdemo

import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory

fun createMoshi(): Moshi {
    return Moshi.Builder()
        .add(
            PolymorphicJsonAdapterFactory.of(JsOutputEvent::class.java, "eventType")
                .withSubtype(JsOutputEvent.OnLoadScript::class.java, "onLoadScript")
                .withSubtype(JsOutputEvent.OnCameraButtonClicked::class.java, "onCameraButtonClicked")
        )
        .build()
}

/**
 * JavaScript から通知されるイベント.
 */
sealed interface JsOutputEvent {

    /**
     * スクリプトがロードされた際のイベント.
     */
    @JsonClass(generateAdapter = true)
    class OnLoadScript : JsOutputEvent

    /**
     * カメラボタンがクリックされた際のイベント.
     */
    @JsonClass(generateAdapter = true)
    class OnCameraButtonClicked : JsOutputEvent
}
