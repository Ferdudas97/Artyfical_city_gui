package sample.service

import com.launchdarkly.eventsource.EventHandler
import com.launchdarkly.eventsource.MessageEvent

class TestHandler: EventHandler {
    override fun onOpen() {
        System.out.println("open")
    }

    override fun onComment(p0: String?) {
        System.out.println("commed")
    }

    override fun onClosed() {
        System.out.println("closed")
    }

    override fun onError(p0: Throwable?) {
        System.out.println("error")
    }

    override fun onMessage(p0: String?, p1: MessageEvent?) {
            System.out.println("mess")
    }
}