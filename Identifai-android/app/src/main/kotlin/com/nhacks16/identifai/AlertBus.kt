package com.nhacks16.identifai

import rx.Observable
import rx.subjects.PublishSubject

object AlertBus {
    val subject = PublishSubject.create<Alert>()

    fun post(alert: Alert) {
        subject.onNext(alert)
    }

    fun observable(): Observable<Alert> {
        return subject
    }
}
