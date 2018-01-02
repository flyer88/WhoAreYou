package io.dove.whoareyou;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/**
 * Created by flyer on 29/12/2017.
 */

public class RxBus {
    private static RxBus sRxBus =new RxBus();
    public static RxBus getRxBusInstance(){
        return sRxBus;
    }
    private final Subject bus;
    // PublishSubject只会把在订阅发生的时间点之后来自原始Observable的数据发射给观察者
    public RxBus() {
        bus = PublishSubject.create();
    }

    // 提供了一个新的事件
    public void post (Object o) {
        bus.onNext(o);
    }
    // 根据传递的 eventType 类型返回特定类型(eventType)的 被观察者
    public <T> Observable<T> observable (Class<T> eventType) {
        return bus.ofType(eventType);
    }
}
