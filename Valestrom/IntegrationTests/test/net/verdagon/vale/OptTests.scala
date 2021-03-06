package net.verdagon.vale

import net.verdagon.vale.templar._
import net.verdagon.vale.templar.env.ReferenceLocalVariable2
import net.verdagon.vale.templar.types._
import net.verdagon.von.VonInt
import org.scalatest.{FunSuite, Matchers}
import net.verdagon.vale.driver.Compilation

class OptTests extends FunSuite with Matchers {
  test("Test empty and get for Some") {
    val compile = new Compilation(
      Samples.get("genericvirtuals/opt.vale") +
        """
          |fn main() {
          |  opt Opt<int> = Some(9);
          |  = if (opt.empty?()) { 0 }
          |    else { opt.get() }
          |}
        """.stripMargin)

    compile.evalForReferend(Vector()) shouldEqual VonInt(9)
  }

  test("Test empty and get for None") {
    val compile = new Compilation(
      Samples.get("genericvirtuals/opt.vale") +
        """
          |fn main() {
          |  opt Opt<int> = None<int>();
          |  = if (opt.empty?()) { 0 }
          |    else { opt.get() }
          |}
        """.stripMargin)

    compile.evalForReferend(Vector()) shouldEqual VonInt(0)
  }

}
