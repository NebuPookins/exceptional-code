# exceptional-code

Makes it easier to work with checked exceptions in Java in a type safe manner.

## Motivation

### I.

Most Java developers agree that static typed checking is better than dynamic type checking. They expect the compiler to
notify them if forget to handle some of the possible outputs of a function.

For example, this code...

```java
List<Foo> myList = someMethod(); 
```

... will produce a compile error if `someMethod` declares that it returns a `Collection<Foo>`, signaling that the result
coudl be a `List<Foo>`, yes, but it could also be a `Set<Foo>` or some other subtype of `Collection`, and that the
developer should either handle this, or explicitly signal that they know the type is definitely a `List`; For example,
by performing a cast:

```java
//I spoke to Dave, and he assured me their backend server will always return a list.
List<Foo> myList = (List<Foo>) someMethod();  
```

The same reasoning applies to exceptions. Checked exceptions are better than unchecked exceptions, because a function
declaring a checked exception is communicating to its callers the possible behaviours it can engage in, aside from
returning a value, and the compiler can confirm that the developer either handles this exception, or explicitly signals
that they know that behavior definitely won't happen; For example, by wrapping the checked exception in an unchecked
exception, analogous to casting):

```java
try {
    someMethod();
} catch (CapacitorsAreFullException e) {
  //I spoke to Dave, and he assured me the capacitors won't be full.
  throw new RuntimeException(e);
}
```

In both cases, if you get your explicit signal wrong (a value being a type that your cast asserted it wouldn't be, or an
exceptional occurring that your wrapping asserted wouldn't happen), your program will probably end up crashing. So
these assertions should be used sparingly, and as a last resort. In the general case, you should design your method
signatures so that they communicate their possible behaviors to the compiler, and you should assume that when a compiler
tells you a behavior is possible, then it really is possible.

And so you should embrace checked exceptions over unchecked ones.

### II.

Many Java developers have the mistaken belief that Java's lambdas are incompatible with checked exceptions. Java lambdas
actually work perfectly fine with checked exceptions, as this library demonstrates. However, the APIs that accept
lambdas have traditionally not been written to properly handle checked exceptions. For example, the JDK's
`java.util.Function` class cannot handle checked exceptions.

That's where this library comes in.

This library provides alternatives to the JDK's library that correctly handle checked exception, and are intended to be
as close to drop-in replacements as possible. For example, where previously you might have had to write code like
this...

```java
public List<Item> getItems(List<String> itemIds) throws DBConnectionException {
  final String uniqueExceptionIdentifier = UUID.randomUUID().toString();
  try {
    return itemIds
      .stream()
      .map(id -> {
        try {
          return this.datastore.fetchItem(id);
        } catch (DBConnectionException e) {
          throw new RuntimeException(uniqueExceptionIdentifier, e);
        }
      })
      .collect(Collectors.toList());
  } catch(RuntimeException e) {
    if (uniqueExceptionIdentifier.equals(e.getMessage())) {
      //this is actually the exception we caught and wrapped, so unwrap it
      throw (DBConnectionException)e.getCause();
    } else {
      //this is some other exception, throw it as is
      throw e;
    }
  }
}
```

... using this library, you can write code like this ...

```java
public List<Item> getItems(List<String> itemIds) throws DBConnectionException {
    return EStream.<String, DBConnectionException>from(itemIds)
        .map(id -> dataStore.fetchItem(id))
        .collect(Collectors.toList());
}
```

... while maintaining the exact same level of type safety that the JDK provides with the APIs it provided before the
introduction of lambdas.

### III.

Checked exceptions are better than unchecked exceptions, but the `Either` monad (for example, as implemented by
http://www.functionaljava.org/) is better still.
 
However, sometimes you are working on a shared project with a development team who is unwilling to commit to the monadic
style of programming. In that case, this library tends to be a good compromise: It offers a familiar imperative-style
API that Java developers are used to, while still allowing for the compiler to robustly confirm coverage for all
behaviors of a method.

## Roadmap

### I.

This project involves going through various APIs defined in the JDK (in particular in
the `java.util.function` and `java.util.stream` packages), looking for opportunities where
the API could be defined in a more type-safe manner, and then providing those safer APIs.

The amount of APIs available is actually quite extensive (`Consumer`, `BiConsumer`,
`Function`, `BiFunction`, `Predicate`, `BiPredicate`, `BinaryOperator`,
`DoubleBinaryOperator`, `BooleanSupplier`, `IntToDoubleFunction`, `IntToLongFunction`, etc.)
Rather than wait until we have type-safe alternatives for all of these, I'm releasing
this project early with a 0.x version number to get early feedback. I plan on gradually
adding in the missing APIs, but because this is a personal project, I am going to prioritize
the APIs I personally find myself needing. Feel free to submit a bug report if there's an API
you need that is missing to help influence where I focus my efforts next.

### II.

Often, when figuring out what the type-safe equivalent would be to an existing API, you
can derive the type-safe version almost mechanically: Just take the original API, and allow
it to throw a checked exception. However, there are some places where you need to make a
design decision, and it's possible I made the "wrong" decision.

I want to balance maintaining backwards compatibility with avoiding shackling ourselves to
bad design decisions. While the project has a 0.x version number (i.e. a version number
less than 1.0), this means I believe I have not yet reached the "critical mass" of adoption
to freeze the API yet, and I may make backwards incompatible changes if I realize I have
made the wrong design decision. I use this library in my "real world" projects and
production code, so I won't be changing the API willy nilly. To give an idea of the level
of breakage I'm expecting, I suspect I will introduce backwards incompatible changes *less
frequently* than Google Guava or Lombok. So if you're comfortable using Google Guava or
Lombok in your production code, then you should be fine with using this project as well.

Please let me know if you're using this library in your project, especially if your
project is open source! This will mutually benefit both of us. It will benefit you because
I can then add your project to my test suite which will allow me to make sure any changes
I do will not break your project. And it will benefit me because I can have a better idea
of when the adoption has indeed reached a critical mass and the project is ready to move
to a 1.x version number. 

## Contributing

Pull requests are welcome. For major changes, please open an issue first to
discuss what you would like to change.

## License
[MIT](https://choosealicense.com/licenses/mit/)