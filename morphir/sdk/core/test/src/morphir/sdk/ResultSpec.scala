package morphir.sdk

import zio.test.Assertion._
import zio.test._

object ResultSpec extends DefaultRunnableSpec {
  def spec = suite("ResultSpec")(
    suite("Mapping")(
      suite("Calling map")(
        testM("Given an Ok value should invoke the mapping") {
          check(Gen.alphaNumericString) { input =>
            assert(
              Result.map((text: String) => text.toUpperCase())(Result.Ok(input))
            )(
              equalTo(Result.Ok(input.toUpperCase()))
            )
          }
        },
        test("Given an Err value should return that value") {
          val original: Result[String, Int] = Result.Err("No Bueno!")
          assert(Result.map((x: Int) => x * 2)(original))(
            equalTo(Result.Err("No Bueno!"))
          )
        }
      )
    ),
    suite("Map2")(
      suite("Calling map2")(
        testM("Given an Ok value should invoke the map2") {
          check(Gen.int(1, 100), Gen.int(1, 100)) { (inputA, inputB) =>
            val inputa = Result.Ok(inputA).withErr[String]
            val inputb = Result.Ok(inputB).withErr[String]
            assert(
              Result.map2((a: Int, b: Int) => a + b)(inputa)(inputb)
            )(
              equalTo(Result.Ok(inputA + inputB))
            )
          }
        },
        test("Given an Err in value A should return that value") {
          val bad: Result[String, Int] = Result.Err("No Bueno!")
          val inputb                   = Result.Ok(1).withErr[String]
          assert(
            Result.map2((a: Int, b: Int) => a * b * 2)(bad)(inputb)
          )(
            equalTo(Result.Err("No Bueno!"))
          )
        },
        test("Given an Err in value B should return that value") {
          val bad: Result[String, Int] = Result.Err("No Bueno!")
          val inputa                   = Result.Ok(1).withErr[String]
          assert(
            Result.map2((a: Int, b: Int) => a * b * 2)(inputa)(bad)
          )(
            equalTo(Result.Err("No Bueno!"))
          )
        }
      )
    ),
    suite("Calling flatMap")(
      testM("Given an Ok value, then it should invoke the mapping function") {
        check(Gen.alphaNumericString, Gen.int(1, 200)) { (product, quantity) =>
          val orderItem = OrderItem(product, quantity)
          val input     = Result.Ok(orderItem).withErr[String]
          assert(
            input.flatMap((oi: OrderItem) => Result.Ok(Product(oi.product)).withErr[String])
          )(
            equalTo(
              Result.Ok(Product(product)).withErr[String]
            )
          )
        }
      },
      test("Given an Err value, then it should return the original error") {
        val result: Result[String, Unit] = Result.Err("Whamo!")
        assert(result.flatMap(_ => Result.Ok(42)))(
          equalTo(Result.Err("Whamo!").withOk[Int])
        )
      }
    ),
    suite("Calling andThen")(
      testM("Given an Ok value, then it should invoke the mapping function") {
        check(Gen.alphaNumericString, Gen.int(1, 200)) { (product, quantity) =>
          val orderItem = OrderItem(product, quantity)
          val input     = Result.Ok(orderItem).withErr[String]
          assert(
            Result.andThen((oi: OrderItem) => Result.Ok(Product(oi.product)).withErr[String])(input)
          )(
            equalTo(
              Result.Ok(Product(product)).withErr[String]
            )
          )
        }
      },
      test("Given an Err value, then it should return the original error") {
        val result: Result[String, Unit] = Result.Err("Whamo!")
        assert(Result.andThen((_: Unit) => Result.Ok(42))(result))(
          equalTo(Result.Err("Whamo!").withOk[Int])
        )
      }
    ),
    suite("Calling mapError as a method")(
      test("Given an Ok value, then it should return that success value") {
        val original: Result[String, Int] = Result.Ok(42)
        assert(original.mapError(e => "[" + e.toUpperCase() + "]"))(
          equalTo(Result.Ok(42))
        )
      },
      test("Given an Err value, then it should return a mapped value") {
        val original: Result[String, Int] = Result.Err("Boom!!!")
        assert(original.mapError(e => "[" + e.toUpperCase() + "]"))(
          equalTo(Result.Err("[BOOM!!!]"))
        )
      }
    ),
    suite("Calling mapError as a function")(
      test("Given an Ok value, then it should return that success value") {
        val original: Result[String, Int] = Result.Ok(42)
        assert(
          Result.mapError((e: String) => "[" + e.toUpperCase() + "]")(original)
        )(
          equalTo(Result.Ok(42))
        )
      },
      test("Given an Err value, then it should return a mapped value") {
        val original: Result[String, Int] = Result.Err("Boom!!!")
        assert(original.mapError(e => "[" + e.toUpperCase() + "]"))(
          equalTo(Result.Err("[BOOM!!!]"))
        )
      }
    )
  )

  case class OrderItem(product: String, quantity: Int)
  case class Product(value: String) extends AnyVal
}
