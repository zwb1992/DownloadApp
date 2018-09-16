package com.zwb.rxjava2demo.http

class Progress() {
    var readLength = 0
    var totalLength = 0
    var done = false

    constructor(readLength: Int, totalLength: Int, done: Boolean):this() {
        this.readLength = readLength
        this.totalLength = totalLength
        this.done = done
    }
}