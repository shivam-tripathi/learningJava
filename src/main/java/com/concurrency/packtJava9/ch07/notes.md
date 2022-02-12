# Chapter 07: Processing Massive Datasets with Parallel Streams – The Map and Reduce Model
- A stream is a sequence of elements (not a data structure) that can be processed in a sequential or parallel way. 
- We can transform the stream applying the intermediate operations and then perform a final computation to get the 
desired result (a list, an array, a number, and so on). We can filter, convert, sort, reduce, or organize those elements 
to obtain a final object. A stream API resembles LINQ (short for Language-Integrated Query) queries.
- Characteristics of streams:
    - Does not store it's elements. Takes elements from source and sends it across the operations in the pipeline.
    - Streams can work in parallel without any extra work. Base stream defines `sequential` and `parallel` methods. These
    can be interconverted as well. When the terminal stream operation is to be performed, all the stream operations will 
    be performed according to the last setting. We cannot instruct a stream to perform some operations sequentially and 
    some concurrently. Internally, parallel streams use fork/join framework.
    - Streams are greatly influenced by functional programming language, in particular Scala. We can use lambda functions
    to define algorithms with streams.
    - Streams are not reusable.    
    - Streams make for lazy processing of data. Stream has an origin, some intermediate operation and a terminal 
    operation. Data isn't processed till terminal operation needs it, so stream processing doesn't begin till the 
    terminal operation is executed.
    - Stream operations process data uniformly, so we only have the stream itself - we cannot access data in stream 
    differently. In parallel streams, elements get processed in any order.
    - Stream operations do not allow us to modify the source. It helps in writing pure functions.
- Sections of a stream
    - Source
    - 0 or more immediate operations, each of which generate a new output
    - One terminal operation which may or may not produce some output object like list, map, array, hashtable etc.
    - 
