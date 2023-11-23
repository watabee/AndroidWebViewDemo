export function setImageSrc(src: string) {
    const img = document.querySelector('#image_preview')
    if (!(img instanceof HTMLImageElement)) {
        return
    }
    img.src = src
}

export function postMessage(eventType: string) {
    // @ts-ignore
    jsObject.postMessage(JSON.stringify({ eventType: eventType }))
}
