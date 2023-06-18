# Copenhagen

— a story that changes depending on how you look at it

## Setup

1. `clj -P` and `npm install`

## Structure

Copenhagen is an experimental way of telling a certain kind of story. A story where what you know determines not only the future, but also the past.

To facilitate this, the story is represented by a `story graph`, which is an ordered tree with three layers:

1. The first layer contains only one node, the `root` and is where the story's metadata lives.

2. The second layer represent `paragraph`s, which are in turn made up of `sentence`s in the third layer.

Each layer has its own `activation mechanism`, which when triggered dispatches an event carrying data that describes a transformation of the `story graph`, e.g., a new paragraph is inserted, a sentence has its words changed.

The UI is then a simple view of the `story graph`.

> ℹ️ Word-level indexing may be supported in the future.

### Low-level structure

Each story `element` is a map with the following shape:

```clojure
#:element{:id         <uuid>
          :type       <element.type>
          :content    <string | hiccup>
          :children   <vector>
          :dispatch   <dispatch-vector>
          :triggered? <boolean>       
          :active?    <boolean>}
```

where `element.type` can be any of `:element.type/{word | sentence | paragraph | story}`. The `:children` vector contains IDs of other story elements of a lower-level of organisation (e.g. an `:element.type/sentence` cannot contain `:element.type/paragraph` children). 

An element of type `:element.type/word` should always have `:element/children` of `nil`. However, since word-level indexing isn't available yet, `:element.type/sentence` must also obey the same rule.

A `story graph` is then recursively built from these `element`s. 

When an element is triggered, a `re-frame` event with the dispatch vector `:dispatch` is fired, which usually applies a transformation to the story graph. Valid transformations are as follows:

- Inserting a new `element`
- Changing an existing `element`
- Deleting an `element`
- Trigerring another `element`

Thus, the evolution of the `story graph` should be fully determined by an initial state plus the history of all events dispatched.

> :warning: The use of `re-frame` implies the ability to subscribe to elements in the story graph. However, that feature has yet to be implemented.
