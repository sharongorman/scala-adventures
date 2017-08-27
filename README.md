# scala-adventures
In general you should complete the exercises in the master branch.
There is a solution branch with  ... guess what...

Choose your adventure below


## Adventure 1 : Handling Errors with Monads
The aim is:

1. Gain practice/confidence in handling errors.
2. Gain practice weilding monads (all the exercises can be solved using generic monad or applicative functor methods!).

Complete the exercises in: com.rea.error.ErrorExercises

There are a series of tests that you need to make pass in ErrorExercisesSpec.
Run them with `./sbt test`


### Using scala either to handle errors

We can represent the result of a function that may give us an error as "either an error or a result".
We choose to use the scala "Either" class to hold this result type.  Together with the Cats functional type-classes to provide us with the Monad and Applicative instances for Either, along with other useful type-classes.

Either forms a right-biased monad (since scala 2-12), which
gives us all the richness of monad transformers and combinators to help us work with our errors.  It is also an applicative functor, which gives us additional methods (sequence / transform).  

In these exercises we first define `AppError` : a case class to hold our errors.
Then we define a type alias `ErrorOr[A]`, that represents our return type of "Either an error or a result".

We then practice creating, combining and transforming our results.

## Adventure 2 : Encoding Json with Circe

The aim is:

1. Gain practice/confidence in encoding using the circe library
2. Gain an understanding of how circe is doing this.

Complete the exercises in: com.rea.json.CirceEncodingExercises

There are a series of tests that you need to make pass in CirceEncodingExercisesSpec

Run them with `./sbt test`

## Adventure 3 : Decoding Json with Circe

The aim is:

1. Gain practice/confidence in decoding using the circe library
2. Gain an understanding of how circe is doing this.

Complete the exercises in: com.rea.json.CirceDecodeExercises

There are a series of tests that you need to make pass in CirceDecodeExercisesSpec

Run them with `./sbt test`
