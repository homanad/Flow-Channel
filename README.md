# Kotlin flows & channels

- [Streams](#streams-hot---cold-streams)
    - [Cold streams](#cold-streams)
    - [Hot streams](#hot-streams)
- [Flow](#flow)
- [StateFlow](#stateflow)
- [SharedFlow](#sharedflow)
- [Channel](#channel)
    - [Channel types](#channel-types)
    - [Difference between SendChannel.close() and ReceiveChannel.cancel()](#difference-between-sendchannelclose-and-receivechannelcancel)
    - [Consume values](#consume-values)
- [Operators](#operators)
    - [Immediate operators](#immediate-operators)
    - [Terminal operators](#terminal-operators)
- [Conclusion](#conclusion)

## Streams/ Hot - Cold streams

Before we start with the flow and new things offered by Kotlin, let's recap a little bit about
Streams concept, types of streams and its properties.

### Streams

* There are two types of streams: **_Hot_** and **_Cold_** streams.
* To distinguish these two types of streams, we can rely on 3 main characteristics:
    * Where the data is produced: the data ca be produced inside or outside the stream. In other
      words, the data exists (or doesn't) no matter if you use the stream or not.
    * How many receivers at the same time can get the data: unicast or multicast mechanism.
    * Laziness: when the stream starts to emit values. Can it starts eagerly or it will start when
      we request for it.

#### Cold streams

* Data is produced inside the stream.
* Cold stream has only one subscriber and is initialized only when a subscriber starts listening to
  it (each subscriber listening generates a different cold stream) -> _**Unicast mechanism**_
* Each time a subscriber starts listening, the cold stream is reinitialized and the code inside it
  will be executed again, and has nothing to do with the previous execution -> Multiple instances of
  the same stream and are completely independent of each other.
* A lazy stream, only initialize and emit data when there is subscriber
* Sometimes, cold stream is not the same as defined, can emit different data depending on the
  listening time, like a hot stream inside a cold stream.

#### Hot Streams

* Data is produced outside the stream.
* Data can exist without a subscriber
* Can have no or more subscribers, emit data simultaneously to all subscriber -> _**Multicast
  mechanism.**_
* Subscriber doesn't initiate the stream, it's only called to start listening to the stream.
* Depending on when to start listening, subscribers may receive different data.
* A eagerly stream, always starts even without subscribers.

## Flow

* **_Flow is a cold stream_**, which means it only emits data when there is a subscriber.
* As the name, flow is like a flow, it only flows, not stores.
* Always execute the same code block when a subscriber starts listening (cold stream concept)
* Example:

    * This is my ViewModel with a flow:

      <img src="/attachments/flow_viewmodel.png" />
    * And this my activity code:

      <img src="/attachments/flow_activity1.png" />
        <br/>
        <img src="/attachments/flow_activity2.png" />

        * I will start Observer 1 right after my activity is created;
        * And after delaying 1s, I will start Observer 2;
        * And then, when I click **_Start observer 3_** button, it will start Observer 3.
    * Ok, let's see what happens!

      <img src="/attachments/flow.gif"/>
    * As you can see, it doesn't matter when we start collecting data from the flow, the flow is
      always is recreated and executes the code block inside it, and emitting all the data.

## StateFlow

* **_StateFlow is a hot stream_**, holds and emits data to subscribers, and keeps data even without
  subscriber.
* When a subscriber starts collecting StateFlow, it receives the last data and subsequent data.
* StateFlow is quite similar to LiveData, the differences:

    * Always have initial data (non-null)
    * LiveData automatically stops emitting data when subscriber is no longer active (`onPause` and
      after that), StateFlow can't be automatic (refer to `Lifecycle.repeatOnLifeCycle`)
* Turn a cold stream (Flow) into a hot stream (StateFlow) using `stateIn` operator.
* Example:

    * This is my ViewModel with a flow:

      <img src="/attachments/state_flow_viewmodel.png" />
    * And this my activity code:

      <img src="/attachments/state_flow_activity1.png" />
      <br/>
      <img src="/attachments/state_flow_activity2.png" />

        * I will start Observer 1 right after my activity is created;
        * And after delaying 1,5s, I will start Observer 2;
        * And then, when I click **_Start observer 3_** button, it will start Observer 3.
    * Ok, let's see what happens!

      <img src="/attachments/state_flow.gif"/>
    * As you can see, depending on when to start observing, the data received by each observer will
      be different. However, a special thing that we need to note, **_StateFlow will not emit 2
      duplicate data consecutively_**, we have lost 1 `l` in `Hello` and two `!` final.

        * Observer 1: it gets all the letters because it starts collecting from the beginning.
        * Observer 2: StateFlow emits each character in turn every 0.2s, ie after 1.4s there will be
          8 letters emitted (at "W" since we have an empty item at first position of `chars`),
          Observer 2 starts listening from 1.5s, so then we will get the last data before, which is
          the letter "W" and the letters after that.
        * Observer 3: of course, depending on the time we press the button, it will start
          collecting.

## SharedFlow

* **_SharedFlow is also a hot stream_**, a more customizable version than StateFlow.

    * `reply`: the number of values that will be sent to a new subscriber (cannot be negative,
      default is 0).
    * `extraBufferCapacity`: The number of values that will be buffered in the replay, emit will not
      suspend if the buffer is still empty.
    * `onBufferOverflow`: config an emit action if the buffer is full, by default emit will be
      suspended until the buffer is available (SUSPEND), there are also DROP_OLDEST and DROP_LATEST.
* Turn a cold stream (Flow) into a hot stream (SharedFlow) using `shareIn` operator.
* Example:

    * This is my view model:

      <img src="/attachments/shared_flow_viewmodel.png" />
    * And this is my activity code:

      <img src="/attachments/shared_flow_activity1.png" />
      <br/>
      <img src="/attachments/shared_flow_activity2.png" />
    * SharedFlow will emit one letter every 0.2s, Observer 1 will be started after 1.3s, Observer 2
      will be started after 3s, and Observer 3 will be started when I click `buttonStartObserver3`
      . So, let's see what happens!

      <img src="/attachments/shared_flow.gif"/>

        * As you can see,
            * Observer 1: after 1.3s, there are 7 emitted letters, it means when we start
              collecting, it has to start printing from "W", but it can also print 3 letters
              before "W" as "lo ", that's because I defined `replay = 3`
            * Observer 2: Same as Observer 1, it can also replay 3 letters before I start collecting
              it.
            * Observer 3: of course, depending on the time we press the button, it will start
              collecting, and also replay 3 letters before that.

## Channel

* **_Channel is a hot stream_**, it implements SendChannel and ReceiveChannel
* `SendChannel` provides two methods to emit data:

    * `trySend()`: add an element to the buffer immediately, and return ChannelResult:

        * `isSuccess`: it's `true` if the element is added successfully. n this case `isFailure` and
          `isClosed` return `false`.
        * `isFailed`: it's `true` if the buffer is full. In this case, `isSuccess` return `false`.
        * `isClosed`: it's `true` if the channel is **_closed or failed_** (we will talk about these
          two types later). In this case, `isSuccess` return `false`, `isFailed` return `true`
    * `send()`: If the buffer is not full, the element is added immediately, otherwise `send()` **_
      will be suspended and wait_** until the buffer is available.
    * Summary:

        * If we want to send data immediately without waiting, that is acceptable to ignore data if
          buffer is full, use `trySend()`
        * If we want to be sure that data has tobe sent, and can wait until the buffer is available
          to process it, should use `send()`
* In additional:

    * `close()`: close the channel immediately, then `isClosedForSend` will return `true`, call
      `close()` with a cause will make this channel a `Failed Channel`.
    * `isClosedForSend` (ExperimentalCoroutinesApi): returns `true` if the channel is closed by an
      invocation of `close()`
    * `invokeOnClose()` (ExperimentalCoroutinesApi): which is synchronously invoked once the channel
      is closed (`SendChannel.close()`) or the receiving side of this channel
      is `ReceiveChannel.cancel`.
* `ReceiveChannel` provides two methods to receive data:

    * `tryReceive()`: receive data synchronously, it return `ChannelResult` (same as `trySend()`)
    * `receive()`: The data will be received asynchronously, if the buffer has data, the data will
      be emitted, otherwise, the receive will be suspended and wait until the data is put into the
      buffer.
    * Summary:
        * If we want to get the data immediately at call time, accepting null value, we can
          use `tryReceive()`.
        * If we want to receive data asynchronously, and wait until there is data, we should use
          `receive()`
* In additional:

    * `isClosedForReceive`: determines whether the receiver is still available to receive data or
      not. For this value to be `true`, we need 2 conditions:

        * When somewhere calls `SendChannel.close()`
        * All items inside buffer have been received by receiver -> buffer is empty, if the buffer
          is not empty, the channel still cannot close for receive.
        * So, even when calling `SendChannel.close()`, but the buffer still has data, then the
          channel will not be closed until all the data in the buffer is emitted -> buffer is empty.
    * `ReceiveChannel.cancel()`: Used to stop emitting all data inside the buffer, and clear
      undelivered data (clean buffer).

### Channel types

#### Rendezvous Channel

* The default capacity value when initializing a `Channel` is `RENDEZVOUS`, the value
  of `RENDEZVOUS`
  is 0, i.e capacity will be 0. We can say this channel doesn't exist buffer, which means items are
  only sent and received when the sender and receiver are met.
* Technically:

    * `send()` functions will be suspended until receiver appears.
    * `receive()` functions will be suspended until sender calls `send()`.
* Example:

    * This is my view model:

      <img src="/attachments/channel_rendezvous_viewmodel.png" />
    * And this is my activity code:

      <img src="/attachments/channel_rendezvous_activity.png" />
    * `rendezvousChannel` will emit a letter every 0.2s (I used `trySend()`), and we will start
      collecting that data when `button1` (**_Rendezvous channel_**) is clicked.
    * Ok, let's see what happens!

      <img src="/attachments/channel_rendezvous.gif"/>

        * As you can see, since I use `trySend()`, and RENDEZVOUS has no buffer, so the previously
          emitted data will definitely be lost.
        * What if I use `send()` instead of `trySend()`? If you thought it would start emitting all
          the data, congratulations, you got it! Since the RENDEZVOUS channel has no buffer,
          the `send()` function will be suspended and wait for an Observer to start collecting data.
          Try changing the code yourself and see the difference between `send()` and `trySend()`!

#### Buffered Channel

* Instance of this channel is ArrayChannel. The value of `BUFFERED` is 64, that means buffer
  capacity will be 64, this will be the default value when initializing the channel as `BUFFERED`.
* Technically:
    * `send()` functions will be suspended when buffer is full (64 items inside).
    * `receive()` functions will be suspended when buffer is empty.
* Example:
    * This is my view model:

      <img src="/attachments/channel_rendezvous_viewmodel.png" />
    * And this is activity code:

      <img src="/attachments/channel_rendezvous_activity.png" />
    * `bufferedChannel` will emit a letter every 0.2s (I used `trySend()`), and we will start
      collecting that data when `button2` (**_Buffered channel_**) is clicked.
    * Ok, let's see what happens!

      <img src="/attachments/channel_buffered.gif"/>

        * As you can see, even if I start collecting it after a few seconds, you still see a bunch
          of previous letters being emitted at the same time, then continue to collect each
          subsequent character.
        * That's the nature of the buffer, in this case it can store up to 64 letters, and almost
          infinite for **_UNLIMITED channel_**, so I'll skip the example for UNLIMITED channel.

#### Unlimited Channel

* Instance of this channel is LinkedListChannel. The value of `LIMITED` is `Int.MAX_VALUE`,
  basically we can assume it has unlimited buffer.
* Technically:
    * `send()` functions will never be suspended.
    * `receive()` will be suspended when buffer is empty.

#### Conflated Channel

* Instance of this channel is ConflatedChannel.
* As for how it works, it feels like it has an unlimited buffer (`send()` will never be suspended).
* However, its special thing is that it keeps only the last item, and skips the previous item if it
  hasn't yet received by any receiver (`DROP_OLDEST`). If it has only one item, and if that item
  received, buffer will be empty.
* Technically:

    * `send()` will never be suspended.
    * `receive()` will be suspended if buffer is empty.
* Conflated Channel is somewhat similar to **_Rendezvous channel and trySend()**_ (data not received
  will be lost), but also like Unlimited channel where send() never suspends, but it only holds one
  last item, try to pull my code and change it to Conflated channel for better understanding.

#### Buffered Channel with custom capacity

* It is similar to BUFFERED channel, the only difference is that we pass an optional capacity value
  into the constructor.
* In this section, I will try it in two case: with send() and trySend()

    * This is my view model with two custom capacity channels:

      <img src="/attachments/channel_custom_capacity_viewmodel.png" />
    * And this is my activity code:

      <img src="/attachments/channel_custom_capacity_activity.png" />
    * `customizedCapacityChannel1` will `trySend()` and `customizedCapacityChannel2` will `send()`
      each character every 0.2s, and we start collecting them for `button3` and `button4`
      respectively, let's see what happens for each channel:

      <img src="/attachments/channel_custom_capacity.gif" />

        * To explain about `chars`, it is a string and I use `split` function, so there will always
          be an empty word at the beginning and end of this list.
          `private val chars = "Hello World! These are Channels example!!!".split("")`
        * As you can see:

            * `customizedCapacityChannel1`: I use `trySend()`, so after buffer is full (we have 5
              letters -> "Hell" and the first one is empty), then I press `button3` (
              **_Custom capacity 1_**), we will immediately get "Hell" from Channel and items are
              being emitted -> non-consecutive string.
            * `customizedCapacityChannel2`: I use `send()`, so when buffer is full, `send` function
              will suspend and wait until I press `button4` (**_Custom capacity 2_**), after data
              inside Channel has been received, `send()` will continue to run,
              then `customizedCapacityChannel2` will continue to emit data -> consecutive series.

### Difference between SendChannel.close() and ReceiveChannel.cancel():

| SendChannel.close()                                                                          | ReceiveChannel.cancel()                                    |
| ---------------------------------------------------------------------------------------------- | ------------------------------------------------------------ |
| Pending items inside the buffer will not be deleted                                          | Pending items inside the buffer will be deleted            |
| ReceiveChannel will still receive data if there are still items in the buffer                | Receive will no longer receive any items                   |
| ReceiveChannel.isClosedForReceive will not be true if there are still items inside in buffer | ReceiveChannel.isClosedForReceive will immediately be true |

* **_Closed Channel_** và **_Failed Channel_**:

    * The `close()` method inside SendChannel can throw an exception, if we pass an exception, it
      will be treated as a `FailedChannel`
    * Closed channel:

        * Call `SendChannel.close()` without passing exception.
        * When trying to call `send()` -> ClosedSendChannelException will be thrown.
        * When calling `tryReceive()` from `ReceiveChannel`, `isClosed` return true
        * When calling `receive()`, it throws `ClosedReceiveChannelException`
    * Failed Channel:

        * Call `SendChannel.close()` with passing exception.
        * When calling `send()`, it throws that exception.
* There are some examples for closed and failed channels in my source code, so please try it out to
  see what happens!

### Consume values

* As mentioned earlier, we can receive data from a channel using `receive()` or `tryReceive()`, but
  these functions return only one value, so to process more than 1 value we can use `flow`, in here
  we can get all values from `Channel` with just one `terminal operator` (`consumeAsFlow()`
  , `consumeEach()`,...)

## Operators

### Immediate operators

* Are operators that will not execute against the flow, instead, operators only execute something
  with the data returned from the flow, and return something else.
* -> Kind of understandable as in a flowing water, the flow will pass through a plant, that plant
  treats the water and continues transmit treated water.
* However, some operators will not return data, it only adds methods or functions to flow, which can
  be mentioned as `takeWhile`, `dropWhile`,...
* Immediate operators are not suspendable functions, but can work with suspend functions inside,
  which means we can create a sequence of operations.

### Terminal operators

* These are suspendable functions and the purpose is to collect values from the upstream flow.
* These are also the operators that will initialize the flow, if no operator is called, the flow
  will not start data transmitter (same as Rx subscription)
* As the name suggests, after calling the terminal, you won't be able to apply any other operators
* Several terminals: `collect`, `single`, `toList`,...

## Conclusion

Those are the basics when we start moving from Rx and LiveData to Kotlin coroutines and flows, in
the next article, I will go through and explain common operators or will be used in some specific
cases. It will be soon!
