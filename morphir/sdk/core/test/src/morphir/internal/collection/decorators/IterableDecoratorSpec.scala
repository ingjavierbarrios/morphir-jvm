package morphir.internal.collection.decorators

import zio.test.Assertion._
import zio.test._

object IterableDecoratorSpec extends DefaultRunnableSpec {
  def spec = suite("IterableDecoratorSpec")(
    suite("foldSomeLeft Specs")(
      test("Test Case 1:") {
        assert(List.range(0, 100).foldSomeLeft(0)((_, _) => None))(equalTo(0))
      },
      test("Test Case 2:") {
        assert(
          List
            .range(0, 100)
            .foldSomeLeft(0)((_, y) => if (y > 10) None else Some(y))
        )(equalTo(10))
      },
      test("Test Case 3:") {
        assert(
          List
            .range(0, 100)
            .foldSomeLeft(0)((x, y) => if (y > 10) None else Some(x + y))
        )(equalTo(55))
      },
      test("Test Case 4:") {
        assert(
          List
            .range(0, 100)
            .foldSomeLeft(0)((x, y) => Some(x + y))
        )(equalTo(4950))
      },
      test("Test Case 4:") {
        assert(
          List[Int]().foldSomeLeft(10)((x, y) => Some(x + y))
        )(equalTo(10))
      }
    )
  )
}
