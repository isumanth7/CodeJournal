# Java Stream API — Stream Sources (Finite & Infinite)

Think of a **stream** as a conveyor belt in a factory — items come in one end, get processed, and come out the other end. The first question is: **where do the items on the belt come from?** That's what "stream sources" means.

---

## Part 1: What IS a Stream?

A stream is NOT a data structure. It doesn't store anything. It's a **pipeline** that:
1. Takes data from a **source**
2. Processes it through **operations** (filter, map, etc.)
3. Produces a **result**

```java
// source → operation → result
List.of(1, 2, 3).stream().filter(n -> n > 1).toList();
// source: the list
// operation: keep numbers > 1
// result: [2, 3]
```

---

## Part 2: Finite Stream Sources

These produce a **known, limited** number of elements. The conveyor belt eventually stops.

### 2a. From Collections

The most common source. Any `Collection` has a `.stream()` method.

```java
// From a List
List.of("a", "b", "c").stream()

// From a Set
Set.of(1, 2, 3).stream()

// From a Queue
new LinkedList<>(List.of(1, 2)).stream()
```

Maps are slightly different — you stream their entries, keys, or values:

```java
Map.of("x", 1, "y", 2).entrySet().stream()
Map.of("x", 1, "y", 2).keySet().stream()
Map.of("x", 1, "y", 2).values().stream()
```

### 2b. From Arrays

```java
// Using Arrays.stream()
int[] nums = {10, 20, 30};
Arrays.stream(nums)  // returns IntStream

String[] words = {"hi", "bye"};
Arrays.stream(words) // returns Stream<String>

// Partial array — from index 1 to 3 (exclusive)
Arrays.stream(nums, 1, 3) // 20, 30
```

### 2c. Using Stream.of()

Create a stream directly from values — like putting items on the belt by hand.

```java
Stream.of("apple", "banana", "cherry")
Stream.of(1, 2, 3, 4, 5)

// Single element
Stream.of("solo")

// ⚠️ Gotcha with int arrays:
Stream.of(new int[]{1,2,3}) // Stream<int[]> — ONE element (the array itself!)
// Use Arrays.stream() or IntStream.of() for primitives instead
```

### 2d. Stream.empty()

A belt with nothing on it. Useful as a default/fallback.

```java
Stream.empty() // zero elements
```

### 2e. From Strings

```java
// Stream of characters (as IntStream of code points)
"hello".chars() // IntStream: 104, 101, 108, 108, 111

// Stream of lines
"line1\nline2\nline3".lines() // Stream<String>
```

### 2f. From Files (I/O)

```java
// Each line of a file becomes an element
Files.lines(Path.of("data.txt")) // Stream<String>

// Directory listing
Files.list(Path.of("/tmp"))      // Stream<Path> — immediate children
Files.walk(Path.of("/tmp"))      // Stream<Path> — recursive
Files.walk(Path.of("/tmp"), 2)   // Stream<Path> — max 2 levels deep
```

⚠️ File streams should be closed — use try-with-resources:

```java
try (var lines = Files.lines(Path.of("data.txt"))) {
    lines.filter(l -> !l.isBlank()).forEach(System.out::println);
}
```

### 2g. Primitive Streams

Java has specialized streams for `int`, `long`, `double` to avoid boxing overhead:

```java
IntStream.of(1, 2, 3)
IntStream.range(0, 10)      // 0 to 9
IntStream.rangeClosed(1, 10) // 1 to 10

LongStream.range(0, 1_000_000L)
DoubleStream.of(1.5, 2.5, 3.5)
```

### 2h. Other Finite Sources

```java
// Regex splitting
Pattern.compile(",").splitAsStream("a,b,c") // "a", "b", "c"

// Random numbers (finite)
new Random().ints(5)          // 5 random ints
new Random().ints(5, 1, 100)  // 5 random ints between 1-99

// Stream.builder — add items one by one
Stream.<String>builder().add("x").add("y").build()

// Concatenate two streams
Stream.concat(Stream.of(1, 2), Stream.of(3, 4)) // 1, 2, 3, 4
```

---

## Part 3: Infinite Stream Sources

These produce elements **forever** — the conveyor belt never stops on its own. You MUST use a limiting operation like `.limit()`, `.takeWhile()`, or `.findFirst()` to stop it.

### 3a. Stream.generate()

Takes a `Supplier` — a function that produces a value each time it's called. No relationship between elements.

```java
// Infinite stream of "hello"
Stream.generate(() -> "hello")        // hello, hello, hello, ...

// Infinite random numbers
Stream.generate(Math::random)         // 0.72, 0.13, 0.91, ...

// Infinite UUIDs
Stream.generate(UUID::randomUUID)

// ⚠️ MUST limit it!
Stream.generate(Math::random).limit(5).toList() // 5 random doubles
```

Think of `generate` as: "keep calling this function, forever."

### 3b. Stream.iterate()

Takes a **seed** (starting value) and a **function** to produce the next value from the previous one. Each element depends on the one before it.

```java
// 0, 1, 2, 3, 4, ...
Stream.iterate(0, n -> n + 1)

// 1, 2, 4, 8, 16, ... (powers of 2)
Stream.iterate(1, n -> n * 2)

// Must limit!
Stream.iterate(0, n -> n + 1).limit(10).toList() // [0..9]
```

Think of `iterate` as: "start here, and keep applying this rule to get the next one."

### 3c. Bounded iterate (Java 9+)

A version of `iterate` with a built-in stop condition — making it **finite**:

```java
// iterate(seed, hasNext, next)
Stream.iterate(0, n -> n < 10, n -> n + 1)
// Equivalent to: for (int n = 0; n < 10; n++)
// Produces: 0, 1, 2, 3, ..., 9

Stream.iterate(1, n -> n <= 1000, n -> n * 2)
// 1, 2, 4, 8, 16, ..., 512
```

### 3d. Infinite Primitive Streams

```java
IntStream.iterate(0, n -> n + 2)       // 0, 2, 4, 6, ... (even numbers)
new Random().ints()                      // infinite random ints
new Random().ints(1, 100)               // infinite random ints in [1, 100)
new Random().doubles()                   // infinite random doubles
```

---

## Part 4: Quick Reference Cheat Sheet

| Source | Finite? | Example |
|--------|---------|---------|
| `collection.stream()` | ✅ Finite | `list.stream()` |
| `Arrays.stream(arr)` | ✅ Finite | `Arrays.stream(nums)` |
| `Stream.of(...)` | ✅ Finite | `Stream.of(1, 2, 3)` |
| `Stream.empty()` | ✅ Finite | `Stream.empty()` |
| `Files.lines(path)` | ✅ Finite | `Files.lines(Path.of("f.txt"))` |
| `IntStream.range()` | ✅ Finite | `IntStream.range(0, 10)` |
| `"str".chars()` | ✅ Finite | `"hello".chars()` |
| `Pattern.splitAsStream()` | ✅ Finite | `pattern.splitAsStream(str)` |
| `Stream.concat()` | ✅ Finite | `Stream.concat(s1, s2)` |
| `Stream.generate()` | ♾️ Infinite | `Stream.generate(Math::random)` |
| `Stream.iterate(seed, fn)` | ♾️ Infinite | `Stream.iterate(0, n -> n+1)` |
| `Stream.iterate(seed, pred, fn)` | ✅ Finite | `Stream.iterate(0, n -> n<10, n -> n+1)` |
| `Random.ints()` | ♾️ Infinite | `new Random().ints()` |
| `Random.ints(count)` | ✅ Finite | `new Random().ints(5)` |

---

## Part 5: Key Gotchas to Remember

1. **Streams are single-use** — once consumed, you can't reuse them
2. **Infinite streams without limit = your program hangs** — always cap them
3. **`Stream.of(intArray)`** gives you `Stream<int[]>`, not `Stream<Integer>` — use `Arrays.stream()` or `IntStream.of()` for primitives
4. **File streams must be closed** — use try-with-resources
5. **Streams are lazy** — nothing happens until a terminal operation (`toList()`, `forEach()`, `count()`, etc.) is called
6. **`parallelStream()`** exists but use it carefully — not always faster

---

## Part 6: Quiz — Multiple Choice (10 Questions)

### Q1. What does this code produce?

```java
long count = Stream.generate(() -> "hello").count();
System.out.println(count);
```

- A) 0
- B) 1
- C) Compilation error
- D) Runs forever / hangs

<details><summary>Answer</summary>D — `Stream.generate()` is infinite. `.count()` tries to count all elements, so it never finishes.</details>

---

### Q2. What is the type of the stream?

```java
var stream = Stream.of(new int[]{1, 2, 3});
```

- A) `Stream<Integer>`
- B) `IntStream`
- C) `Stream<int[]>`
- D) `Stream<int>`

<details><summary>Answer</summary>C — `Stream.of()` uses generics. `int[]` is an object, so it becomes `Stream<int[]>` with ONE element (the array). Use `Arrays.stream()` or `IntStream.of()` for primitives.</details>

---

### Q3. What does this print?

```java
Stream.of("a", "b", "c").forEach(System.out::print);
Stream.of("a", "b", "c").forEach(System.out::print);
```

- A) `abcabc`
- B) `abc` then throws `IllegalStateException`
- C) Throws `IllegalStateException` immediately
- D) `abc`

<details><summary>Answer</summary>A — These are two separate streams. Each `Stream.of()` creates a new stream. Both are consumed independently.</details>

---

### Q4. What happens here?

```java
Stream<String> s = Stream.of("a", "b", "c");
s.forEach(System.out::println);
s.forEach(System.out::println);
```

- A) Prints a b c twice
- B) Prints a b c once, then nothing
- C) Throws `IllegalStateException` on the second `forEach`
- D) Compilation error

<details><summary>Answer</summary>C — Streams are single-use. The second `forEach` on the same consumed stream throws `IllegalStateException`.</details>

---

### Q5. What does this print?

```java
System.out.println(Arrays.stream(new int[]{1, 2, 3}).count()
    + " " + Stream.of(new int[]{1, 2, 3}).count());
```

- A) `3 3`
- B) `3 1`
- C) `1 1`
- D) Compilation error

<details><summary>Answer</summary>B — `Arrays.stream(int[])` unpacks into `IntStream` (3 elements). `Stream.of(int[])` wraps as `Stream<int[]>` (1 element).</details>

---

### Q6. What does this print?

```java
IntStream.range(1, 5).forEach(n -> System.out.print(n + " "));
```

- A) `1 2 3 4 5`
- B) `1 2 3 4`
- C) `0 1 2 3 4`
- D) `0 1 2 3 4 5`

<details><summary>Answer</summary>B — `range(1, 5)` is start-inclusive, end-exclusive. Use `rangeClosed(1, 5)` for inclusive end.</details>

---

### Q7. What does this print?

```java
Stream.iterate(10, n -> n - 1)
      .takeWhile(n -> n > 0)
      .forEach(n -> System.out.print(n + " "));
```

- A) `10 9 8 7 6 5 4 3 2 1`
- B) `10 9 8 7 6 5 4 3 2 1 0`
- C) Runs forever
- D) Compilation error

<details><summary>Answer</summary>A — `takeWhile` stops when the condition becomes false. `0 > 0` is false, so 0 is not included.</details>

---

### Q8. Which line will NOT compile?

```java
IntStream a = IntStream.of(1, 2, 3);       // Line 1
IntStream b = Arrays.stream(new int[]{1});  // Line 2
IntStream c = Stream.of(1, 2, 3);           // Line 3
IntStream d = "hello".chars();              // Line 4
```

- A) Line 1
- B) Line 2
- C) Line 3
- D) Line 4

<details><summary>Answer</summary>C — `Stream.of(1, 2, 3)` returns `Stream<Integer>` (autoboxed), not `IntStream`. They are different types.</details>

---

### Q9. Which creates a finite stream?

- A) `new Random().ints()`
- B) `Stream.iterate(0, n -> n + 1)`
- C) `Stream.generate(() -> 42)`
- D) `new Random().doubles(10)`

<details><summary>Answer</summary>D — `doubles(10)` produces exactly 10 elements. The others are all infinite.</details>

---

### Q10. What does this print?

```java
Stream.iterate(1, n -> n <= 1000, n -> n * 2)
      .forEach(n -> System.out.print(n + " "));
```

- A) `1 2 4 8 16 32 64 128 256 512`
- B) `1 2 4 8 16 32 64 128 256 512 1024`
- C) Runs forever
- D) Compilation error

<details><summary>Answer</summary>A — Bounded iterate (Java 9+). The predicate `n <= 1000` is checked before including. 512 * 2 = 1024 fails the check, so it stops at 512.</details>

---

## Part 7: Coding Exercises (10 Questions)

### C1. First 10 even numbers

Create an infinite stream of even numbers starting from 0 and collect the first 10 into a list.

```java
List<Integer> evens = // your code
```

<details><summary>Answer</summary>

```java
List<Integer> evens = Stream.iterate(0, n -> n + 2).limit(10).toList();
```

</details>

---

### C2. Count non-empty lines in a file

Using `Files.lines()`, count how many non-empty lines a file has.

```java
Path path = Path.of("data.txt");
long count = // your code
```

<details><summary>Answer</summary>

```java
try (var lines = Files.lines(path)) {
    long count = lines.filter(line -> !line.isBlank()).count();
}
```

</details>

---

### C3. First 8 powers of 2

Generate `[1, 2, 4, 8, 16, 32, 64, 128]` using `Stream.iterate()`.

```java
List<Integer> powers = // your code
```

<details><summary>Answer</summary>

```java
List<Integer> powers = Stream.iterate(1, n -> n * 2).limit(8).toList();
```

</details>

---

### C4. Split and filter CSV

Split `"apple,banana,,cherry,"` using `Pattern.splitAsStream()` and collect only non-empty values.

```java
List<String> fruits = // your code
```

<details><summary>Answer</summary>

```java
List<String> fruits = Pattern.compile(",")
        .splitAsStream("apple,banana,,cherry,")
        .filter(s -> !s.isEmpty())
        .toList();
```

</details>

---

### C5. Random integers

Generate a list of 5 random integers between 1 and 100 (inclusive).

```java
List<Integer> randoms = // your code
```

<details><summary>Answer</summary>

```java
List<Integer> randoms = new Random().ints(5, 1, 101).boxed().toList();
// Note: ints(count, origin, bound) — bound is exclusive, so 101 for inclusive 100
```

</details>

---

### C6. Fibonacci sequence

Generate the first 10 Fibonacci numbers using `Stream.iterate()` with a pair/array as seed.

```java
List<Integer> fib = // your code
```

<details><summary>Answer</summary>

```java
List<Integer> fib = Stream.iterate(new int[]{0, 1}, a -> new int[]{a[1], a[0] + a[1]})
        .limit(10)
        .map(a -> a[0])
        .toList();
// [0, 1, 1, 2, 3, 5, 8, 13, 21, 34]
```

</details>

---

### C7. Merge two arrays into one stream

Given two arrays, create a single stream containing all elements from both.

```java
int[] a = {1, 2, 3};
int[] b = {4, 5, 6};
List<Integer> merged = // your code
```

<details><summary>Answer</summary>

```java
List<Integer> merged = IntStream.concat(Arrays.stream(a), Arrays.stream(b))
        .boxed()
        .toList();
```

</details>

---

### C8. Stream of characters

Convert the string `"hello"` into a `List<Character>`.

```java
List<Character> chars = // your code
```

<details><summary>Answer</summary>

```java
List<Character> chars = "hello".chars()
        .mapToObj(c -> (char) c)
        .toList();
```

</details>

---

### C9. Bounded countdown

Using the 3-argument `Stream.iterate()`, produce a countdown: `[10, 9, 8, 7, 6, 5, 4, 3, 2, 1]`.

```java
List<Integer> countdown = // your code
```

<details><summary>Answer</summary>

```java
List<Integer> countdown = Stream.iterate(10, n -> n >= 1, n -> n - 1).toList();
```

</details>

---

### C10. Directory listing

List all `.java` files in a directory (non-recursive) as a list of filenames.

```java
Path dir = Path.of("src");
List<String> javaFiles = // your code
```

<details><summary>Answer</summary>

```java
try (var files = Files.list(dir)) {
    List<String> javaFiles = files
            .filter(p -> p.toString().endsWith(".java"))
            .map(p -> p.getFileName().toString())
            .toList();
}
```

</details>
