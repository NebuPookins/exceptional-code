There will probably never be an `EComparable` type.

The JavaDocs in `EComparator` describes a convention whereby depending on whether the exception thrown in the `compare`
method is checked or unchecked, you have a partial or total ordering.

In a hypothetical `EComparable` design, we would want to apply the same convention here for consistency. However,
`Comparable` (and by extension `EComparable`) is intended to represent the natural ordering of a given type. A natural
ordering that is also partial is of limited use. If all instances of our hypothetical `EComparable` were total and
thus declare that they only throw unchecked exceptions, then this is essentially indistinguishable from the original
`Comparable` type.

Furthermore, most usages of `Comparable` are in APIs for which it is not easy to provide alternative implementations
for, such as `TreeSet` or `PriorityQueue`. 