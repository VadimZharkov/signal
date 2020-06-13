## Signal

This is a small library for reactive programming.

**Usage example:**

```java
// Create a one-way communication channel
Channel<Integer> channel = Signal.newChannel();

// Subscribe to listen to the values output by the channel
Disposable lifecicle = channel.output.subscribeValues(System.out::println);

// Send value
channel.input.sendValue(10);
```   
**Lazy generation:**  

In some cases, the signal emitter should not be created until after a subscriber connects.  

```java
Signal<Integer> signal = Signal.generate(emitter -> {
     emitter.sendValue(10);
});

// Returns false
boolean isSignalStarted = signal.isStarted();

// Start sending
Disposable lifecicle = signal.subscribeValues(v -> System.out.println(v));
```  
**Execution context:**  

It is posible to specify invocation of subscription callback with help of Context.  
```java
Context context = new Context() {
    ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    public void invoke(Action action) {
        executor.submit(action::invoke);
    }
};

Channel<Integer> channel = Signal.newChannel();
channel.output.subscribeValues(context, v -> {
    System.out.println(Thread.currentThread().getName());
    System.out.println(v);
});
```  

**Reactive extentions operators:**  

Currently `map` is supported.

**Thread safety:**  

Signal is threadsafe.
